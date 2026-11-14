package com.aibuilt.pocketmasterai.hardware

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Offline hardware simulator mirroring tests/fake_device.py: echoes dump/names/params. */
class FakePocketMasterDevice {

    val connected = MutableStateFlow(false)

    /** Current raw edit-buffer dump bytes (if any), parsed elsewhere into EditBuffer. */
    val editBufferDump: StateFlow<ByteArray?> = MutableStateFlow(null)

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    suspend fun connect() {
        connected.value = true
        _status.value = "Simulated device connected"
    }

    suspend fun disconnect() {
        connected.value = false
        _status.value = "Simulated device disconnected"
    }

    suspend fun sysex(bytes: ByteArray): Boolean {
        if (!connected.value) return false
        kotlinx.coroutines.delay(20) // mimic packet delay
        _status.value = "Echo: ${bytes.joinToString(" ")}"
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
        editBufferDump.value = dump
        _status.value = "Fake dump received"
    }

    companion object {
        fun slot(slot: Byte): Byte = slot
    }
}
