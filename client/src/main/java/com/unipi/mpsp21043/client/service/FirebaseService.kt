package com.unipi.mpsp21043.client.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.unipi.mpsp21043.client.R
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.notifications.PushNotification
import com.unipi.mpsp21043.client.notifications.RetrofitInstance
import com.unipi.mpsp21043.client.ui.activities.MainActivity
import com.unipi.mpsp21043.client.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class FirebaseService : FirebaseMessagingService() {

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()

                FirestoreHelper().setUserToken(token.toString())
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        createNotificationChannel(notificationManager)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            FLAG_ONE_SHOT or FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(getNotificationIcon())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(javaClass.simpleName, "Response: ${Gson().toJson(response)}")
            }
            else {
                Log.e(javaClass.simpleName, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(javaClass.simpleName, e.toString())
        }
    }

    /*private fun sendNotification(data: Map<String, String>) {
        val title = String.format(getString(R.string.notification_payload_fav_title),
            data[Constants.PAYLOAD_PRODUCT_NAME])
        val body = String.format(getString(R.string.notification_payload_fav_body),
            data[Constants.PAYLOAD_PRODUCT_NAME])

        val resultIntent = Intent(this@FirebaseService, ProductDetailsActivity::class.java).run {
            putExtra(Constants.EXTRA_PRODUCT_ID, data[Constants.PAYLOAD_PRODUCT_ID])
            putExtra(Constants.EXTRA_IS_IN_FAVORITES, true)
        }

        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // Action broadcasters
        val cancelIntent = Intent(this, NotificationReceiver::class.java).run {
            putExtra(Constants.EXTRA_NOTIFICATION_ID, Constants.NOTIFICATION_ID)
        }
        val cancelPendingIntent =
            PendingIntent.getBroadcast(this, Constants.NOTIFICATION_ID, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val productImg = Glide.with(this@FirebaseService)
            .asBitmap()
            .load(data[Constants.PAYLOAD_PRODUCT_IMG_URL])
            .fitCenter()
            .submit()

        val imgBitmap = productImg.get()

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification: Notification = NotificationCompat.Builder(this@FirebaseService, Constants.NOTIFICATION_CHANNEL_ID)
            .setOngoing(false)
            .setSmallIcon(getNotificationIcon())
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .addAction(R.drawable.ic_heart, getString(R.string.notification_mark_as_read), cancelPendingIntent) // todo
            .addAction(R.drawable.ic_heart, getString(R.string.notification_add_to_cart), cancelPendingIntent) // todo
            .setLargeIcon(imgBitmap)
            .setGroup(Constants.GROUP_KEY_FAVORITES)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(imgBitmap)
            .setContentTitle(title) // Title
            .setContentText(body) // Body
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // this parameter is used to configure lock screen visibility
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_PROMO)
            .setWhen(System.currentTimeMillis())
            .setColor(getColor(R.color.colorPrimary))
            .setSound(defaultSoundUri).build()

        val notificationManager = this@FirebaseService.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        Glide.with(this).clear(productImg)

        val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_favorites),
            NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = getString(R.string.notification_channel_desc)
                lockscreenVisibility = 1
                setShowBadge(true)
        }

        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }*/

    private fun getNotificationIcon(): Int {
        return R.drawable.ic_stat_name
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, channelName, IMPORTANCE_DEFAULT).apply {
            description = "Used to send notifications!"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}
