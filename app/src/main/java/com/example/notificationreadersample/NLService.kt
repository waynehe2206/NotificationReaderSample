package com.example.notificationreadersample

import android.app.Notification
import android.content.Intent
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.graphics.drawable.toBitmap
import com.example.notificationreadersample.MainActivity.Companion.ACTION_NOTIFICATION_IN

class NLService: NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val packageName = sbn.packageName
            val installedAppList = getInstalledPackageName(applicationContext.packageManager)
            if (!installedAppList.contains(packageName)) return

            sbn.notification.let {
                val title = it.extras.get(Notification.EXTRA_TITLE) ?: ""
                val content = it.extras.get(Notification.EXTRA_TEXT) ?: ""
                val icon = it?.getLargeIcon()?.loadDrawable(applicationContext)?.toBitmap()
                val intent = Intent(ACTION_NOTIFICATION_IN).apply {
                    putExtra("title", title as String)
                    putExtra("content", content as CharSequence)
                    putExtra("icon", icon)
                }
                sendBroadcast(intent)
            }
        }
    }

    private fun getInstalledPackageName(packageManager: PackageManager?): List<String> {
        val result = ArrayList<String>()
        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val appInLauncher = packageManager?.queryIntentActivities(launcherIntent, 0)
        appInLauncher?.forEach {
            result.add(it.activityInfo.packageName)
        }
        return result
    }
}