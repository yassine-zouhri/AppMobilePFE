package com.example.tracking_app.servise.FirebaseService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.tracking_app.R
import com.example.tracking_app.models.Event
import com.example.tracking_app.repository.EventRepository
import com.example.tracking_app.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import kotlin.random.Random


private const val CHANNEL_ID = "my_channel"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("datadatdatdadadaada    ="+message)
        println(message.data ["imageBytes"])
        if(!message.data.isEmpty()){
            val NewEvent : Event = Event(message.data.getValue("id"),message.data.getValue("titre")
                    ,message.data.getValue("description"),message.data.getValue("degre_danger").toInt(),
                    message.data.getValue("longitude").toDouble(),message.data.getValue("latitude").toDouble(),
                    message.data.getValue("imageBytes"),message.data.getValue("dejavue").toBoolean(),
                    message.data.getValue("date").toLong(),message.data.getValue("zone"),
                    message.data.getValue("categorie"))

            GlobalScope.launch (Dispatchers.IO) {
                EventRepository.getInstance(applicationContext).AddNewEvent(NewEvent)
            }
        }


        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }


        val notificationLayout = RemoteViews(packageName, R.layout.custom_notif)
        notificationLayout.setTextViewText(R.id.titleEventNotif,message.notification?.title)
        notificationLayout.setTextViewText(R.id.descriptionEventNotif,message.notification?.body)
        try {
            val bitmap: Bitmap = Glide.with(applicationContext)
                .asBitmap()
                .load(message.data ["imageBytes"])
                .submit(120, 120)
                .get()

            notificationLayout.setImageViewBitmap(R.id.imageViewNotif,bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }



        /*val that = this
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        if(!message.data.isEmpty()){
            var urlImage= URL(message.data ["imageBytes"])
            var result: Deferred<Bitmap?> = GlobalScope.async {
                urlImage.toBitmap()
            }
            GlobalScope.launch(Dispatchers.IO) {
                val notification = NotificationCompat.Builder(that, CHANNEL_ID)
                    .setContentTitle(message.notification?.title)
                    .setContentText(message.notification?.body)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setLargeIcon(result.await())
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(result.await()))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                    .build()
                notificationManager.notify(notificationID, notification)
            }
        }else{
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.notification?.title)
                .setContentText(message.notification?.body)
                .setSmallIcon(R.drawable.ic_notif)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .build()
            notificationManager.notify(notificationID, notification)
        }*/



        val bitmap   = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_user)
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_user)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setSmallIcon(R.drawable.ic_notif)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .build()
            notificationManager.notify(notificationID, notification)






    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun URL.toBitmap(): Bitmap?{
        return try {
            BitmapFactory.decodeStream(openStream())
        }catch (e:IOException){
            null
        }
    }


}



/*class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FireBaseMessagingService"
    var NOTIFICATION_CHANNEL_ID = "net.larntech.notification"
    val NOTIFICATION_ID = 100

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        println("onMessageReceivedonMessageReceivedonMessageReceivedonMessageReceived")
        if (remoteMessage.data.size > 0) {
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            println("datadatdatdadadaada    ="+remoteMessage.data)
            println("datadatdatdadadaada    ="+remoteMessage.notification.toString())
            if(!GeoApplication.isActivityVisible()){
                showNotification(applicationContext, title, body)
            }
        } else {
            println("datadatdatdadadaada  else  ="+remoteMessage.data)
            println("datadatdatdadadaada    else="+remoteMessage.notification)
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            if(!GeoApplication.isActivityVisible()){
                showNotification(applicationContext, title, body)
            }
        }
    }

    override fun onNewToken(p0: String) {
        println("onNewToken(onNewToken(onNewToken(onNewToken(onNewToken(onNewToken(" )
        println("p00000000000    ="+p0)
        super.onNewToken(p0)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast

            Toast.makeText(baseContext,token, Toast.LENGTH_SHORT).show()
        })
    }



    fun showNotification(
        context: Context,
        title: String?,
        message: String?
    ) {
        val ii: Intent
        ii = Intent(context, MainActivity::class.java)
        ii.data = Uri.parse("custom://" + System.currentTimeMillis())
        ii.action = "actionstring" + System.currentTimeMillis()
        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi =
            PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.e("Notification", "Created in up to orio OS device");
            notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(getNotificationIcon())
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setWhen(System.currentTimeMillis())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                title,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            notification = NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setAutoCancel(true)
                .setContentText(message)
                .setContentIntent(pi)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
    }

}*/