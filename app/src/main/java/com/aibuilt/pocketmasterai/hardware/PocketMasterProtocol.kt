package com.aibuilt.pocketmasterai.hardware

import kotlin.experimental.and

/** SysEx framing and command encoding/decoding for Sonicake Pocket Master (QME-10).
 *  Byte-exact port of src/pocket_master_mcp/protocol.py. */
object PocketMasterProtocol {

    // SysEx frame builder: F0 <len> <bytes...> F7
    fun buildSysex(vararg bytes: Byte): ByteArray {
        require(bytes.isNotEmpty())
        val totalLen = bytes.size + 2 // F0 + F7
        val out = ByteArray(totalLen)
        out[0] = 0xF0.toByte()
        out[totalLen - 2] = totalLen.toByte()
        System.arraycopy(bytes, 0, out, 2, bytes.size)
        out[totalLen - 1] = 0xF7.toByte()
        return out
    }

    // Nibble expansion: 4-bit value -> 2 MIDI bytes (0x0F mask)
    fun nibbleExpand(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0x0F and 0x7F).toByte(),
            ((value ushr 4) and 0x0F and 0x7F).toByte()
        )
    }

    // CRC-8/SMBus over total..payload (exclusive of F0/F7, inclusive of length byte)
    fun computeCrc(data: ByteArray): Byte {
        var crc = 0
        for (b in data) {
            crc = crc.xor(b.toInt())
            for (i in 0..7) {
                val hi = crc and 0x80
                crc = (crc shl 1) and 0xFF
                if (hi != 0) crc = crc xor 0x07
            }
        }
        return crc.toByte()
    }

    // Command IDs
    const val CMD_GLOBAL: Byte = 0x11.toByte()
    const val CMD_PRESET_VOLUME: Byte = 0x42.toByte()
    const val CMD_SELECT: Byte = 0x43.toByte()
    const val CMD_CHAIN: Byte = 0x44.toByte()
    const val CMD_EFFECT: Byte = 0x47.toByte()
    const val CMD_PARAM: Byte = 0x48.toByte()
    const val CMD_STATE: Byte = 0x49.toByte()
    const val CMD_SAVE: Byte = 0x4A.toByte()
    // Queries
    const val Q_GLOBALS: Byte = 0x10.toByte()
    const val Q_IR_NAMES: Byte = 0x20.toByte()
    const val Q_CLONE_NAMES: Byte = 0x24.toByte()
    const val Q_PRESET_NAMES: Byte = 0x40.toByte()
    const val Q_CURRENT_DUMP: Byte = 0x41.toByte()
    const val Q_CURRENT_INDEX: Byte = 0x43.toByte()
    const val Q_MODIFIED: Byte = 0x45.toByte()

    // Edit-buffer dump groups (T_*)
    const val T_STATES: Byte = 0x01.toByte()
    const val T_CHAIN: Byte = 0x02.toByte()
    const val T_EFFECTS: Byte = 0x03.toByte()
    const val T_PARAMS: Byte = 0x04.toByte()
    const val T_VOLUME: Byte = 0x05.toByte()
    const val T_TEMPO: Byte = 0x06.toByte()

    // Build a query frame: F0 len Q_... F7
    fun buildQuery(query: Byte): ByteArray {
        return buildSysex(0x7E.toByte(), 0x02.toByte(), query)
    }

    // Build a global set command: F0 len 11 <value> F7
    fun buildGlobalSet(value: Byte): ByteArray {
        return buildSysex(0x7E.toByte(), 0x03.toByte(), CMD_GLOBAL, value)
    }

    // Build preset volume: F0 len 42 <lo> <hi> F7 (2-byte value little-endian)
    fun buildPresetVolume(value: Short): ByteArray {
        return buildSysex(
            0x7E.toByte(),
            0x04.toByte(),
            CMD_PRESET_VOLUME,
            (value and 0xFF).toByte(),
            ((value and 0xFF00) shr 8).toByte()
        )
    }

    // Build effect command for block slot: F0 len 47 <slot> <effect> F7
    fun buildEffect(slot: Byte, effect: Byte): ByteArray {
        return buildSysex(0x7E.toByte(), 0x04.toByte(), CMD_EFFECT, slot, effect)
    }

    // Build param command: F0 len 48 <slot> <param> <lo> <hi> F7 (float32 as 2 bytes via nibble pack)
    fun buildParam(slot: Byte, param: Byte, value: Short): ByteArray {
        return buildSysex(
            0x7E.toByte(),
            0x05.toByte(),
            CMD_PARAM,
            slot,
            param,
            (value and 0xFF).toByte(),
            ((value and 0xFF00) shr 8).toByte()
        )
    }

    // Build state command: F0 len 49 <slot> <state> F7 (0=on, 1=off)
    fun buildState(slot: Byte, on: Boolean): ByteArray {
        return buildSysex(0x7E.toByte(), 0x04.toByte(), CMD_STATE, slot, (if (on) 0 else 1).toByte())
    }

    // Build chain command: F0 len 44 <0xF0..0xF9 9 bytes> F7
    fun buildChain(vararg slots: Byte): ByteArray {
        val out = ByteArray(2 + 1 + slots.size + 1)
        out[0] = 0xF0.toByte()
        out[1] = out.size.toByte()
        out[2] = CMD_CHAIN
        System.arraycopy(slots, 0, out, 3, slots.size)
        out[out.size - 1] = 0xF7.toByte()
        return out
    }

    // Save: F0 len 4A <slot> F7
    fun buildSave(slot: Byte): ByteArray {
        return buildSysex(0x7E.toByte(), 0x04.toByte(), CMD_SAVE, slot)
    }

    // Decode preset name record from dump: name field is 8 nibbles -> ASCII
    fun decodePresetName(nibbles: Int): String {
        val chars = IntArray(8) { (nibbles ushr (6 - 2 * it)) and 0x3F }
        val byteChars = chars.map { c ->
            if (c in 0x40..0x5A) (c - 0x20).toChar() else c.toChar()
        }.map { it.code.toByte() }
        return byteChars.toByteArray().decodeToString()
    }

    // Chain validation: slots 2,3,4,5 are fixed DRV-AMP-IR-EQ
    fun validateChain(chain: List<Byte>): Boolean {
        if (chain.size != 9) return false
        return chain[2] == 0x02.toByte() && chain[3] == 0x03.toByte() &&
                chain[4] == 0x04.toByte() && chain[5] == 0x05.toByte()
    }
}
