package dell.com.allindiaitr.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dell.com.allindiaitr.firebase.MyFirebaseIntentService
import dell.com.allindiaitr.firebase.MyFirebaseMessagingService

//class NotificationsBroadcastReceiver : BroadcastReceiver() {
class NotificationsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val service = Intent(context, MyFirebaseMessagingService::class.java)
//        val service = Intent(context, MyFirebaseIntentService::class.java)
//        service.putExtra("reason", intent?.getStringExtra("reason"))
//        service.putExtra("timestamp", intent?.getLongExtra("timestamp", 0))

        context?.startService(service)
    }

}