package dell.com.allindiaitr.firebase

import android.content.Context
//import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsClass {
    lateinit var mContext: Context
//    var mFirebaseAnalytics: FirebaseAnalytics? = null

  //  mFirebaseAnalytics=FirebaseAnalytics.getInstance(this)



   private constructor() {

   }
    companion object {
        val instance: FirebaseAnalyticsClass by lazy { FirebaseAnalyticsClass() }
    }
}