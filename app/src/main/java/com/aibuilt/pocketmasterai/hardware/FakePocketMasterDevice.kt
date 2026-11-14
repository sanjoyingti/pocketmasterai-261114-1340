package com.aibuilt.pocketmasterai.hardware

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Offline hardware simulator mirroring tests/fake_device.py: echoes dump/names/params. */
class FakePocketMasterDevice {

    private var connectedState = false
    var connected: StateFlow<Boolean> = MutableStateFlow(connectedState)

    /** Current raw edit-buffer dump bytes (if any), parsed elsewhere into EditBuffer. */
    var editBufferDump: StateFlow<ByteArray?> = MutableStateFlow(null)

    private var statusText: String? = null
    var status: StateFlow<String?> = MutableStateFlow(statusText)

    suspend fun connect() {
        connectedState = true
        connected.value = connectedState
        statusText = "Simulated device connected"
        status.value = statusText
    }

    suspend fun disconnect() {
        connectedState = false
        connected.value = connectedState
        statusText = "Simulated device disconnected"
        status.value = statusText
    }

    suspend fun sysex(bytes: ByteArray): Boolean {
        if (!connectedState) return false
        kotlinx.coroutines.delay(20) // mimic packet delay
        statusText = "Echo: ${bytes.joinToString(" ")}"
        status.value = statusText
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
        statusText = "Fake dump received"
        status.value = statusText
    }

    companion object {
        fun slot(slot: Byte): Byte = slot
    }
}
