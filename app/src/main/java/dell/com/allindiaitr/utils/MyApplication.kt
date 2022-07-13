package dell.com.allindiaitr.utils

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

import dell.com.allindiaitr.helper.AppSignatureHelper
import net.gotev.uploadservice.BuildConfig
import net.gotev.uploadservice.UploadService


class MyApplication : Application() {
//class MyApplication : MultiDexApplication() {
private var appPreferences: AppPreferences? = null
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
//        val appSignatureHelper = AppSignatureHelper(this)
//        appSignatureHelper.appSignatures
       // mFirebaseAnalytics=FirebaseAnalytics.getInstance(this)
       // mFirebaseAnalytics.setCurrentScreen(this,"My Application",null)

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID
        // Or, you can define it manually.
        UploadService.NAMESPACE = "dell.com.allindiaitr"

//        appPreferences= AppPreferences(base!!)
//        val lang:String=appPreferences?.selectedLanguage!!
//        super.attachBaseContext(MyContextWrapper.wrap(base,lang))

    }


}
