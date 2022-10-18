package com.example.notificationreadersample

import android.app.Notification
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.example.notificationreadersample.MainActivity.Companion.ACTION_NOTIFICATION_IN

class NLService: NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val packageName = sbn.packageName
            val installedAppList = getInstalledPackageName(applicationContext.packageManager)
            if (!installedAppList.contains(packageName)) return

            sbn.notification.let { notification ->
                notification.extras.keySet().forEach {
                    Log.e("NLService", "notification extra key: $it, value: ${notification.extras.get(it)}")
                }

                val title = notification.extras.get(Notification.EXTRA_TITLE) ?: ""
                val subText = notification.extras.get(Notification.EXTRA_SUB_TEXT) ?: ""
                val content = notification.extras.get(Notification.EXTRA_TEXT) ?: ""
                val textLine = notification.extras.get(Notification.EXTRA_TEXT_LINES)
                Notification.EXTRA_COMPACT_ACTIONS
                val icon = notification?.getLargeIcon()?.loadDrawable(applicationContext)?.toBitmap()
                textLine?.let {
                    val charArray = it as Array<*>
                    charArray.forEach { charSeq ->
                        Log.e("NLService", "textLine: ${charSeq as CharSequence}")
                    }
                }

                Log.e("NLService", "notification title: ${title as CharSequence}, content: ${content as CharSequence}")

                val pArray = notification.extras.getParcelableArray(Notification.EXTRA_MESSAGES)
                pArray?.forEach { parcelable ->
                    val bundle = parcelable as Bundle
                    Log.e("NLService","messageObj type: $bundle")
                    bundle.keySet().forEach {
                        Log.e("NLService","messageObj key $it, value: ${bundle.get(it)}")
                    }
                }

                val intent = Intent(ACTION_NOTIFICATION_IN).apply {
                    putExtra("title", title as CharSequence)
                    putExtra("subText", subText as CharSequence)
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