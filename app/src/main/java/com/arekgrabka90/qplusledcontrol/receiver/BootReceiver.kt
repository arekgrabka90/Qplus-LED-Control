package com.arekgrabka90.qplusledcontrol.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.arekgrabka90.qplusledcontrol.service.LEDControlService

/**
 * Receiver for boot completion to start LED control service.
 */
class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed, starting LED Control Service")
            context?.let {
                val serviceIntent = Intent(it, LEDControlService::class.java)
                try {
                    it.startService(serviceIntent)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start service: ${e.message}")
                }
            }
        }
    }
}
