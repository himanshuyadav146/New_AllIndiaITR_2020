package dell.com.allindiaitr.utils

import android.content.Context
import android.content.SharedPreferences
import com.facebook.internal.Mutable
import dell.com.allindiaitr.models.NewItrBase

class AppPreferences(context: Context) {

    private var APP_SHARED_PREFS = "dell.com"
    private var sharedPreferences : SharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE)
    private var editor : SharedPreferences.Editor = sharedPreferences.edit()

//    private var mContext
//    private constructor()
//    {
//
//    }
    fun ClearPreferences(){
        sharedPreferences.edit().clear().commit()
    }

    //used to check authority of user for api
    var accessTokenId: String?
    get() = sharedPreferences.getString("accessTokenId", "")
    set(accessTokenId) {
        editor.putString("accessTokenId", accessTokenId)
        editor.commit()
    }

    //mobile gsm id for notification
    var deviceTokenId: String?
        get() = sharedPreferences.getString("deviceTokenId", "")
        set(deviceTokenId) {
            editor.putString("deviceTokenId", deviceTokenId)
            editor.commit()
        }

    var parentId: String?
    get() = sharedPreferences.getString("parentId", "")
    set(parentId){
        editor.putString("parentId", parentId)
        editor.commit()
    }


    var selectedLanguage:String?
    get() = sharedPreferences.getString("selectedLanguage","en")
//    get() = sharedPreferences.getString("selectedLanguage","hi")
    set(selectedLanguage){
        editor.putString("selectedLanguage",selectedLanguage)
        editor.apply()
    }


    var childId: String?
    get() = sharedPreferences.getString("childId", "")
    set(childId){
        editor.putString("childId", childId)
        editor.commit()
    }

    var createdTime: String?
    get() = sharedPreferences.getString("createdTime", "")
    set(createdTime){
        editor.putString("createdTime", createdTime)
        editor.commit()
    }

    var userName: String?
    get() = sharedPreferences.getString("userName", "")
    set(userName) {
        editor.putString("userName", userName)
        editor.commit()
    }

    var emailAddress: String?
    get() = sharedPreferences.getString("emailAddress", "")
    set(emailAddress) {
        editor.putString("emailAddress", emailAddress)
        editor.commit()
    }

    var mobileNumber: String?
    get() = sharedPreferences.getString("mobileNumber", "")
    set(mobileNumber) {
        editor.putString("mobileNumber", mobileNumber)
        editor.commit()
    }

    var userFirstName: String?
    get() = sharedPreferences.getString("userFirstName", "")
    set(userFirstName) {
        editor.putString("userFirstName", userFirstName)
        editor.commit()
    }

    var userLastName: String?
    get() = sharedPreferences.getString("userLastName", "")
    set(userLastName) {
        editor.putString("userLastName", userLastName)
        editor.commit()
    }

    var appLaunch: Boolean
    get() = sharedPreferences.getBoolean("appLaunch", false)
    set(appLaunch) {
        editor.putBoolean("appLaunch", appLaunch)
        editor.commit()
    }


}