package com.unipi.p17172.emarket.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {

        val notificationID = intent.getIntExtra("extraNotificationId", -1)
        if (notificationID > 0) {
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationID)
        }

        // todo
        val message = intent.getStringExtra("read")
    }
}