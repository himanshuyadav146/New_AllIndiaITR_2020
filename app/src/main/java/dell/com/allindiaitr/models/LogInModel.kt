package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogInModel {
    @SerializedName("PhoneNumber")
    @Expose
    var phoneNumber: String? = null
    @SerializedName("Email")
    @Expose
    var email: String? = null
    @SerializedName("Password")
    @Expose
    var password: Any? = null
    @SerializedName("DeviceType")
    @Expose
    var deviceType: String? = null
    @SerializedName("DeviceId")
    @Expose
    var deviceId: String? = null
    @SerializedName("FlagValue")
    @Expose
    var flagValue: Int = 0
    @SerializedName("Message")
    @Expose
    var message: String? = null
    private constructor (){
    }
    companion object {
        val instance: LogInModel by lazy { LogInModel() }
    }
}