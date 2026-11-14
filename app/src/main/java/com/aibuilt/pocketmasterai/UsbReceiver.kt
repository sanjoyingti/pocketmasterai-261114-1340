package com.aibuilt.pocketmasterai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbConstants.USB_PERMISSION
import android.hardware.usb.UsbManager
import android.util.Log

/** Receives USB attach/detach events and permission-result broadcasts declared in
 *  AndroidManifest.xml via the receiver's intent-filter. */
class UsbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_USB_DEVICE_ATTACHED,
            ACTION_USB_DEVICE_DETACHED,
            ACTION_USB_PERMISSION -> {
                // Permission grants / reconnection handling live in PocketMasterMidiManager / ViewModel
            }
            else -> Log.w("UsbReceiver", "Unknown USB intent: ${intent.action}")
        }
    }

    companion object {
        const val ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        const val ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"
        const val ACTION_USB_PERMISSION = "android.hardware.usb.action.USB_PERMISSION"
    }
}
