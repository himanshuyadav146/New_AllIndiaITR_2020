package dell.com.allindiaitr.sliderscreen

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import dell.com.allindiaitr.databinding.ActivitySplashBinding
import dell.com.allindiaitr.home.DashboardActivity
import dell.com.allindiaitr.login.LogInActivity
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.MyApplication


class SplashActivity : AppCompatActivity() {
    lateinit var accessTokenString: String
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000
    private var runOnce: RunOnce? = null
    private var appPreferences: AppPreferences? = null
    private lateinit var myApplication: MyApplication
    lateinit var binding:ActivitySplashBinding

//    var itrBase = ItrBase.INSTANCE
    var newItrBase = NewItrBase.instance
    internal val mRunnable: Runnable = Runnable {
        runOnce = RunOnce(this)

        accessTokenString = appPreferences!!.accessTokenId!!
        if (!isFinishing) {
            if (!runOnce!!.isFirstTimeLaunch) {
                runOnce!!.isFirstTimeLaunch = false
                if (accessTokenString.isEmpty()){
                    val intent = Intent(applicationContext, LogInActivity::class.java)
//                    val intent = Intent(applicationContext, OTPActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    newItrBase.isReferVisible = true
                    val intent = Intent(applicationContext, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            else {
                runOnce!!.isFirstTimeLaunch = true
                val intent = Intent(applicationContext, SliderActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDelayHandler = Handler()
        appPreferences = AppPreferences(this)
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        myApplication=MyApplication()
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

//        val bundle = Bundle()
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "splash")
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Splash Screen")
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image1111")
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
               // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            appPreferences!!.deviceTokenId=token
        })


//        Crashlytics.getInstance().crash()
    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }
}
