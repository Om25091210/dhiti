package com.aryomtech.dhitifoundation.fcm


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.aryomtech.dhitifoundation.Home
import com.aryomtech.dhitifoundation.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

private const val CHANNEL_ID="my_channel"

class FirebaseService :FirebaseMessagingService(){


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("newToken", p0)
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent=Intent(this,Home::class.java)
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotifionChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this,0,intent,FLAG_ONE_SHOT)

        val dp_string= getSharedPreferences("imageBASE64_data", MODE_PRIVATE)
            .getString("base64_user_image", "").toString()
        if(message.data["title"].toString()=="New Event"){

            val contentView = RemoteViews(this.getPackageName(), R.layout.event_notification_layout)
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
            if(!dp_string.equals(""))
                contentView.setImageViewBitmap(R.id.flashButton,
                    getBitmapFromURL(dp_string)?.let { getCroppedBitmap(it) })
            else
                contentView.setViewVisibility(R.id.flashButton, View.GONE)
            contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent)
            contentView.setTextViewText(R.id.message, message.data["title"])
            contentView.setTextViewText(R.id.date, message.data["message"])

            val notification=NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
                .setSmallIcon(R.drawable.ic_noti_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.dhiti_splash))
                .setAutoCancel(true)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID,notification)

        }
        else if(message.data["title"].toString() == "New group task"){

            val contentView = RemoteViews(this.getPackageName(), R.layout.assign_group_noti_layout)
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
            if(!dp_string.equals(""))
                contentView.setImageViewBitmap(R.id.flashButton,
                    getBitmapFromURL(dp_string)?.let { getCroppedBitmap(it) })
            else
                contentView.setViewVisibility(R.id.flashButton, View.GONE)
            contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent)
            contentView.setTextViewText(R.id.message, message.data["title"])
            contentView.setTextViewText(R.id.date, message.data["message"])

            val notification=NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
                .setSmallIcon(R.drawable.ic_noti_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.dhiti_splash))
                .setAutoCancel(true)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID,notification)
        }
        else if(message.data["title"].toString() == "New task for you"){

            val contentView = RemoteViews(this.getPackageName(), R.layout.private_task_noti_layout)
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
            if(!dp_string.equals(""))
                contentView.setImageViewBitmap(R.id.flashButton,
                    getBitmapFromURL(dp_string)?.let { getCroppedBitmap(it) })
            else
                contentView.setViewVisibility(R.id.flashButton, View.GONE)
            contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent)
            contentView.setTextViewText(R.id.message, message.data["title"])
            contentView.setTextViewText(R.id.date, message.data["message"])

            val notification=NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
                .setSmallIcon(R.drawable.ic_noti_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.dhiti_splash))
                .setAutoCancel(true)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID,notification)
        }
        else if(message.data["title"].toString() == "Group task approved" || message.data["title"].toString() == "Task approved"){

            val contentView = RemoteViews(this.getPackageName(), R.layout.approval_noti_layout)
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
            if(!dp_string.equals(""))
                contentView.setImageViewBitmap(R.id.flashButton,
                    getBitmapFromURL(dp_string)?.let { getCroppedBitmap(it) })
            else
                contentView.setViewVisibility(R.id.flashButton, View.GONE)
            contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent)
            contentView.setTextViewText(R.id.message, message.data["title"])
            contentView.setTextViewText(R.id.date, message.data["message"])

            val notification=NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
                .setSmallIcon(R.drawable.ic_noti_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.dhiti_splash))
                .setAutoCancel(true)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID,notification)
        }
        else if (message.data["title"].toString() == "New announcement"){

            val contentView = RemoteViews(this.getPackageName(), R.layout.announcement_noti_layout)
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
            if(!dp_string.equals(""))
                contentView.setImageViewBitmap(R.id.flashButton,
                    getBitmapFromURL(dp_string)?.let { getCroppedBitmap(it) })
            else
                contentView.setViewVisibility(R.id.flashButton, View.GONE)
            contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent)
            contentView.setTextViewText(R.id.message, message.data["title"])
            contentView.setTextViewText(R.id.date, message.data["message"])

            val notification=NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
                .setSmallIcon(R.drawable.ic_noti_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.dhiti_splash))
                .setAutoCancel(true)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID,notification)
        }
        else if(message.data["title"].toString()+"" == "null"){
            val contentView = RemoteViews(this.getPackageName(), R.layout.notification_layout)
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
            if(!dp_string.equals(""))
                contentView.setImageViewBitmap(R.id.flashButton,
                    getBitmapFromURL(dp_string)?.let { getCroppedBitmap(it) })
            else
                contentView.setViewVisibility(R.id.flashButton, View.GONE)
            contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent)
            contentView.setTextViewText(R.id.message, "Referral Successfull!!")
            contentView.setTextViewText(R.id.date, "We got a confirmed donation through your referral link. We are adding some points to your profile.")

            val notification=NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Referral Successfull!!")
                .setContentText("We got a confirmed donation through your referral link. We are adding some points to your profile.")
                .setStyle(NotificationCompat.BigTextStyle().bigText("We got a confirmed donation through your referral link. We are adding some points to your profile."))
                .setSmallIcon(R.drawable.ic_noti_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.dhiti_splash))
                .setAutoCancel(true)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID,notification)
        }
        else {
            val contentView = RemoteViews(this.getPackageName(), R.layout.notification_layout)
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
            if (!dp_string.equals(""))
                contentView.setImageViewBitmap(R.id.flashButton,
                    getBitmapFromURL(dp_string)?.let { getCroppedBitmap(it) })
            else
                contentView.setViewVisibility(R.id.flashButton, View.GONE)
            contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent)
            contentView.setTextViewText(R.id.message, message.data["title"])
            contentView.setTextViewText(R.id.date, message.data["message"])

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
                .setSmallIcon(R.drawable.ic_noti_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.dhiti_splash))
                .setAutoCancel(true)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifionChannel(notificationManager: NotificationManager){
        val channelName="ChannelName"
        val channel=NotificationChannel(CHANNEL_ID,channelName,IMPORTANCE_HIGH).apply {

            description="My channel description"
            enableLights(true)
            lightColor=Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }

    @Throws(Exception::class)
    fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        canvas.drawCircle((bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat(), (bitmap.width / 2).toFloat(), paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}