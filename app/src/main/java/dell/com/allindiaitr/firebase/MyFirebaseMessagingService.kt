package dell.com.allindiaitr.firebase

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.submitDocument.ChatActivity
import dell.com.allindiaitr.utils.AppPreferences
import android.app.PendingIntent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dell.com.allindiaitr.R
import dell.com.allindiaitr.home.DashboardActivity
import dell.com.allindiaitr.submitDocument.ERStatusActivity
import dell.com.allindiaitr.submitDocument.FailPaymentActivity
import dell.com.allindiaitr.submitDocument.ITROrderStatusActivity

class MyFirebaseMessagingService: FirebaseMessagingService() {
    private lateinit var appPreferences : AppPreferences
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "personal_notification"
    internal var delivered_priority: String? = null
    internal var sent_time:String? = null
    internal var ttl:String? = null
    internal var original_priority:String? = null
    internal var notification_e:String? = null
    internal var badge:String? = null
    internal var title:String? = null
    internal var from: String? = null
    internal var UserAssessmentYearId:String? = null
    internal var message_id:String? = null
    internal var body:String? = null
    internal var google_e:String? = null
    internal var click_action:String? = null
    internal var wakelockid:String? = null
    internal var collapse_key:String? = null
    lateinit var objItrBase: NewItrBase
    lateinit var mAcknowledgement:String

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        if(!token.isEmpty())
        {
            appPreferences = AppPreferences(this)
            appPreferences.deviceTokenId=token
            appPreferences.appLaunch=true
        }

    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        try {
            Log.e("map value 1 --- ",""+remoteMessage.toString());
            Log.e("map value 2 --- ",""+remoteMessage.data.toString());
            objItrBase= NewItrBase.instance
            val action = remoteMessage?.data?.get("click_action").toString()
            val UserAssessmentYearId=remoteMessage?.data?.get("UserAssessmentYearId").toString()
            mAcknowledgement=remoteMessage?.data?.get("AcknowledgeNo").toString()
            objItrBase.acknowledgementNo=mAcknowledgement
            if (remoteMessage?.getData() != null) {
                if(objItrBase.getIsChatOpen()){
                    if(action!=null && action!="")
                    {
                        if(UserAssessmentYearId==objItrBase.selectedUser_userAssessmentYearUserID)
                        {
                            updateMyActivity(this, "Update Chat")
                        }

                    }else{
                        // if diffrent user got message
//                        sendNotificationAPI26(remoteMessage)
                    }
                }else{
                    sendNotificationAPI26(remoteMessage)
                }


            }
        }catch (e:Exception)
        {
            Log.e("Error ", e.toString())
        }


    }



    private fun sendNotificationAPI26(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        var title = remoteMessage.data.get("title").toString()
        var body = remoteMessage.data.get("body").toString()
        val sound = remoteMessage.data.get("sound").toString()
        val badge = remoteMessage.data.get("badge")
        val action = remoteMessage.data.get("click_action").toString()
        val UserAssessmentYearId=remoteMessage.data.get("UserAssessmentYearId").toString()
        objItrBase.selectedUser_userAssessmentYearUserID=UserAssessmentYearId
        if(body.contains("Your Acknowledgement No"))
        {
            objItrBase.acknowledgementNo=body.split("Your Acknowledgement No.").get(1).trim()
        }
        if(!mAcknowledgement.isEmpty())
        {
            objItrBase.acknowledgementNo=mAcknowledgement
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelID = "notification"
        var intent:Intent=getIntent(action, title)
        if (title.isEmpty()||title.equals("null")){
            title = remoteMessage.notification?.title.toString()
            body = remoteMessage.notification?.body.toString()
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        //Log.d("Pending Intent123",pendingIntent.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            val notificationChannel = NotificationChannel(
                notificationChannelID,
                "My notification",
                NotificationManager.IMPORTANCE_MAX
            )
            notificationChannel.description = "Hello from notification"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

        }

        val builder = NotificationCompat.Builder(this, notificationChannelID)
        builder.setAutoCancel(false)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setTicker("All India ITR")
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setContentInfo("info")
            .setAutoCancel(true)
        Log.d("Pending Intent", pendingIntent.toString())
        notificationManager.notify(1, builder.build())


    }


    internal fun updateMyActivity(context: Context, message: String) {
        val intent = Intent("unique_name")
        intent.putExtra("message", message)
        context.sendBroadcast(intent)
    }

    // Sending broadcast for refreshing notification screen.
    // it will recieve broadcast in ITRStatus screen.
    internal fun updateNotificationScreen(context: Context, message: String) {
        val intent = Intent("noti_refresh")
        intent.putExtra("message", message)
        context.sendBroadcast(intent)
    }


    fun displayNotification(msg: String?)
    {
        var intent:Intent=Intent(this,DashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "App Name"
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(msg)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(NOTIFICATION_ID, notificationBuilder.build())
    }


    fun displayNotification(msg: String?, clickAction: String) {
        createNotificationChennal(msg)

        val intent: Intent
        intent = getIntent(clickAction, "")
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
            //.setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(largeIcon))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setChannelId(CHANNEL_ID)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(NOTIFICATION_ID, builder1.build())
    }


    private fun openActivity(){
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, DashboardActivity::class.java)
// Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notifyIntent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )



        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentIntent(resultPendingIntent)

        }
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }


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


    private fun getIntent(intentData: String, title: String): Intent {
        var intentRedirect: Intent? = null

        /*var localIntentData = intentData
        //getting fail situation for ITR
        if(title.equals("Payment Fail") && intentData.equals("OPEN_ACTIVITY_1")){
            localIntentData = "Payment Fail"
        }*/

        when (intentData) {
            "InternalChat" -> {
                objItrBase.ChatMode="IndividualChat"
                intentRedirect = Intent(this, ChatActivity::class.java)
            }
            "ITR" -> {
                objItrBase.ChatMode="ITR"
               // objItrBase.selectedUser_userAssessmentYearUserID=UserAssessmentYearId
                intentRedirect = Intent(this, ITROrderStatusActivity::class.java)

                //new added
                updateNotificationScreen(this, "refresh notification")
            }
            "OPEN_ACTIVITY_1"->{
                intentRedirect = Intent(this, DashboardActivity::class.java)
            }
            /*"Payment Fail"->{
                intentRedirect = Intent(this, FailPaymentActivity::class.java)
            }*/
            "RevisedReturn"->{
                intentRedirect = Intent(this, ERStatusActivity::class.java)
            }
            "EVerify"->{
                intentRedirect = Intent(this, ERStatusActivity::class.java)
            }
            else->intentRedirect = Intent(this, DashboardActivity::class.java)


        }
        objItrBase.setisNotification(true)
        return intentRedirect
    }

}