package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Token {
    @SerializedName("access_token")
    @Expose
    var accessToken: String? = null
    @SerializedName("token_type")
    @Expose
    var tokenType: String? = null
    @SerializedName("expires_in")
    @Expose
    var expiresIn: String? = null
    @SerializedName("Id")
    @Expose
    var id: String? = null
    @SerializedName("UserName")
    @Expose
    var userName: String? = null
    @SerializedName("Email")
    @Expose
    var email: String? = null
    @SerializedName("IsProfileUpdated")
    @Expose
    var isProfileUpdated: Boolean? = null
    @SerializedName("IsForm16Uploaded")
    @Expose
    var isForm16Uploaded: Boolean? = null
    @SerializedName("IsManualFiling")
    @Expose
    var isManualFiling: Boolean? = null
    @SerializedName("PhoneNumber")
    @Expose
    var phoneNumber: String? = null
    @SerializedName("IsTaxFilingComplete")
    @Expose
    var isTaxFilingComplete: Boolean? = null
    @SerializedName("IsDocumentUploaded")
    @Expose
    var isDocumentUploaded: Boolean? = null
    @SerializedName("IsDocumentFilingComplete")
    @Expose
    var isDocumentFilingComplete: Boolean? = null
    @SerializedName("FirstName")
    @Expose
    var firstName: String? = null
    @SerializedName("LastName")
    @Expose
    var lastName: String? = null
    @SerializedName("PanNumber")
    @Expose
    var panNumber: String? = null
    @SerializedName("AadharCardNumber")
    @Expose
    var aadharCardNumber: Any? = null
    @SerializedName("IsPhoneNumberConfirmed")
    @Expose
    var isPhoneNumberConfirmed: Boolean? = null
    @SerializedName(".issued")
    @Expose
    var issued: String? = null
    @SerializedName(".expires")
    @Expose
    var expires: String? = null
    private constructor (){
    }
    companion object {
        val instance: Token by lazy { Token() }
    }
}