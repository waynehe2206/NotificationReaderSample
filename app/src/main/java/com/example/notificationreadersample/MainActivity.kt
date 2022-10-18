package com.example.notificationreadersample

import android.app.NotificationManager
import android.content.*
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : AppCompatActivity() {
    lateinit var titleText: TextView
    lateinit var subTitleText: TextView
    lateinit var contentText: TextView
    lateinit var iconView: ImageView
    lateinit var permissionText: TextView
    lateinit var broadcastReceiver: BroadcastReceiver

    companion object {
        const val ACTION_NOTIFICATION_IN = "action_notification_in"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        initReceiver()
    }

    private fun initUI(){
        titleText = findViewById(R.id.tv_title)
        subTitleText = findViewById(R.id.tv_sub_title)
        contentText = findViewById(R.id.tv_content)
        iconView = findViewById(R.id.iv_icon)
        permissionText = findViewById(R.id.tv_permission)
    }

    private fun initReceiver(){
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ACTION_NOTIFICATION_IN){
                    val title = intent.extras?.get("title") as CharSequence
                    val subText = intent.extras?.get("subText") as CharSequence
                    val content = intent.extras?.get("content") as CharSequence
                    val icon = intent.extras?.getParcelable<Bitmap>("icon")
                    titleText.text = title
                    subTitleText.text = subText
                    contentText.text = content
                    icon?.let {
                        iconView.setImageBitmap(it)
                    }
                }
            }
        }

        val filter = IntentFilter(ACTION_NOTIFICATION_IN);
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun checkPermission() {
        val cn = ComponentName(this, NLService::class.java)
        val enabled: Boolean

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
            val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            enabled = notificationManager.isNotificationListenerAccessGranted(cn)
        } else {
            val flat: String? = Settings.Secure.getString(
                contentResolver,
                "enabled_notification_listeners"
            )
            enabled = flat != null && flat.contains(cn.flattenToString())
        }

        if (enabled) {
            permissionText.text = "DONE"
        } else {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
    }
}