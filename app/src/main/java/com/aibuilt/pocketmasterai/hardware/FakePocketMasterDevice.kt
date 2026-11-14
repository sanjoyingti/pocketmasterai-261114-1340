package com.aibuilt.pocketmasterai.hardware

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Offline hardware simulator mirroring tests/fake_device.py: echoes dump/names/params. */
class FakePocketMasterDevice {

    private var _connected = false
    val connected: StateFlow<Boolean> = MutableStateFlow(false).apply { value = false }

    /** Current raw edit-buffer dump bytes (if any), parsed elsewhere into EditBuffer. */
    private var _editBufferDump: ByteArray? = null
    val editBufferDump: StateFlow<ByteArray?> = MutableStateFlow(_editBufferDump)

    private var _status: String? = null
    val status: StateFlow<String?> = MutableStateFlow(_status)

    suspend fun connect() {
        _connected = true
        connected.value = true
        _status = "Simulated device connected"
        status.value = _status
    }

    suspend fun disconnect() {
        _connected = false
        connected.value = false
        _status = "Simulated device disconnected"
        status.value = _status
    }

    suspend fun sysex(bytes: ByteArray): Boolean {
        if (!_connected) return false
        kotlinx.coroutines.delay(20) // mimic packet delay
        _status = "Echo: ${bytes.joinToString(" ")}"
        status.value = _status
        return true
    }

    fun sendFakeDump() {
        val dump = byteArrayOf(
            0x7E.toByte(),
            0x03.toByte(),
            PocketMasterProtocol.T_STATES,
            0x00.toByte(),
            0x00.toByte()
        )
        _editBufferDump = dump
        editBufferDump.value = dump
        _status = "Fake dump received"
        status.value = _status
    }

    companion object {
        fun slot(slot: Byte): Byte = slot
    }
}
