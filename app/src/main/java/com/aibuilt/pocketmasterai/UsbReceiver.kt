package com.aibuilt.pocketmasterai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager

/** Receives USB attach/detach events declared in AndroidManifest.xml via the receiver's intent-filter. */
class UsbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED,
            UsbManager.ACTION_USB_DEVICE_DETACHED,
            UsbManager.ACTION_USB_PERMISSION -> {
                // Reconnect / permission handling lives in PocketMasterMidiManager / ViewModel
            }
        }
    }
}
