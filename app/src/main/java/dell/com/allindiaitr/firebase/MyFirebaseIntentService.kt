package dell.com.allindiaitr.firebase

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dell.com.allindiaitr.R
import dell.com.allindiaitr.home.DashboardActivity

class MyFirebaseIntentService : IntentService("MyService") {

    private val CHANNEL_ID = "personal_notification"
    private val NOTIFICATION_ID = 1

    override fun onHandleIntent(p0: Intent?) {
       //To change body of created functions use File | Settings | File Templates.
        val extras = p0!!.getExtras()

     //   displayNotification("Hello","OPEN_ACTIVITY_1")


//        if (remoteMessage.getData().size > 0) {
////            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            if ( remoteMessage.notification!=null) {
//                objItrBase.selectedUser_userAssessmentYearUserID=remoteMessage.data.get("UserAssessmentYearId")
//                remoteMessage.notification?.clickAction?.let { displayNotification(remoteMessage.notification?.body, it) }
//            } else {
//                // Handle message within 10 seconds
//                //handleNow();
//            }
//
//        }
    }


    fun displayNotification(msg: String?, clickAction: String) {
        createNotificationChennal(msg)

        val intent: Intent
        intent =  Intent(this, DashboardActivity::class.java)
        //  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder1 = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("All India ITR ")
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(largeIcon))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setChannelId(CHANNEL_ID)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(NOTIFICATION_ID, builder1.build())
    }

    private fun createNotificationChennal(messageBody: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Personal Notification"
            val description = "Inclued All Personal Notification"
            //String description = messageBody;
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            @SuppressLint("WrongConstant")
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationChannel.description = description
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }
}