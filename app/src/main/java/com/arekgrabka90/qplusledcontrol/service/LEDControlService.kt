package com.arekgrabka90.qplusledcontrol.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.arekgrabka90.qplusledcontrol.led.LEDController
import kotlinx.coroutines.*

/**
 * Background service for LED control.
 * Handles foreground app detection and LED state management.
 */
class LEDControlService : Service() {
    companion object {
        private const val TAG = "LEDControlService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "led_control_channel"
    }

    private lateinit var ledController: LEDController
    private var serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "LEDControlService created")
        ledController = LEDController(this)
        startForegroundService()
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Q+ LED Control")
            .setContentText("LED control is running in background")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "LEDControlService started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "LEDControlService destroyed")
        serviceScope.cancel()
    }
}
