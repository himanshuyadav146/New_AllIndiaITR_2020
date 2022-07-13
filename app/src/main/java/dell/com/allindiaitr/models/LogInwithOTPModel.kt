package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogInwithOTPModel {

    //**********Log In With OTP Model Start**********//
    @SerializedName("PhoneNumber")
    @Expose
    var phoneNumber: String? = null         //*****Used in Log In*****//
    @SerializedName("Email")
    @Expose
    var email: String? = null         //*****Used in Log In*****//
    @SerializedName("Password")
    @Expose
    var password: String? = null         //*****Used in Log In*****//
    @SerializedName("DeviceType")
    @Expose
    var deviceType: String? = null         //*****Used in Log In*****//
    @SerializedName("DeviceId")
    @Expose
    var deviceId: String? = null         //*****Used in Log In*****//
    @SerializedName("FlagValue")
    @Expose
    var flagValue: Int = 0         //*****Used in Log In*****//
    @SerializedName("Message")
    @Expose
    var message: String? = null         //*****Used in Log In*****//
    @SerializedName("LoginModel")
    @Expose
    var loginModel: LogInModel? = null
    @SerializedName("Token")
    @Expose
    var token: Token? = null
    @SerializedName("access_token")
    @Expose
    var access_token: String? = null
    @SerializedName("Id")
    @Expose
    var Id: String? = null
    @SerializedName("Code")
    @Expose
    var Code: String? = null
    @SerializedName("CreatedTime")
    @Expose
    var CreatedTime: String? = null
    //**********Log In With OTP Model End**********//

    //**********Log In Model Start**********//
    //**********Log In Model End**********//

    private constructor (){

    }
    companion object {
        val instance: LogInwithOTPModel by lazy { LogInwithOTPModel() }
    }
}