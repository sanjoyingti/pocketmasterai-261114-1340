package com.aibuilt.pocketmasterai

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager

/** Receives USB attach/detach events declared in AndroidManifest.xml via usb_device_filter.xml. */
class UsbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED,
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                // Reconnect logic lives in PocketMasterMidiManager / ViewModel
            }
            UsbManager.ACTION_USB_PERMISSION -> {
                // Permission result handling lives in PocketMasterMidiManager / ViewModel
            }
        }
    }

    companion object {
        fun createPermissionPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                0,
                Intent(UsbManager.ACTION_USB_PERMISSION),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }
}
