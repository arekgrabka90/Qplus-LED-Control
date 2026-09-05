package com.arekgrabka90.qplusledcontrol.system

import android.app.ActivityManager
import android.content.Context
import android.util.Log

/**
 * System utilities for getting foreground app and other system info.
 */
class SystemUtils(private val context: Context) {
    companion object {
        private const val TAG = "SystemUtils"
    }

    /**
     * Get the currently focused/foreground application package name.
     */
    fun getForegroundAppPackage(): String? {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = activityManager.runningAppProcesses ?: return null

            val foregroundPid = android.os.Process.myPid()
            for (app in runningApps) {
                if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.d(TAG, "Foreground app: ${app.processName}")
                    return app.processName
                }
            }
            null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get foreground app: ${e.message}")
            null
        }
    }

    /**
     * Get app name from package name.
     */
    fun getAppName(packageName: String): String {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get app name for $packageName: ${e.message}")
            packageName
        }
    }

    /**
     * Check if app is installed.
     */
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get list of installed apps.
     */
    fun getInstalledApps(): List<String> {
        return try {
            context.packageManager.getInstalledApplications(0).map { it.packageName }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get installed apps: ${e.message}")
            emptyList()
        }
    }
}
