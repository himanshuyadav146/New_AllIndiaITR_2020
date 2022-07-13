package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoreModel {

    @SerializedName("MetaTitle") @Expose var metaTitle: String? = null
    @SerializedName("MetaKeywords") @Expose var metaKeywords: String? = null
    @SerializedName("MetaDescription") @Expose var metaDescription: String? = null
    @SerializedName("BlogTitle") @Expose var blogTitle: String? = null
    @SerializedName("BlogContent") @Expose var blogContent: Any? = null
    @SerializedName("ImagePath") @Expose var imagePath: String? = null
    @SerializedName("CreatedDate") @Expose var createdDate: String? = null
    @SerializedName("RecId") @Expose var recId: Int? = null
    @SerializedName("BlogCategoryId") @Expose var blogCategoryId: Any? = null
    @SerializedName("CategoryName") @Expose var categoryName: Any? = null
    @SerializedName("BlogView") @Expose var blogView: Int? = null
    @SerializedName("OgTitle") @Expose var ogTitle: Any? = null
    @SerializedName("OgDescription") @Expose var ogDescription: Any? = null
    @SerializedName("TwitterTitle") @Expose var twitterTitle: Any? = null
    @SerializedName("TwitterDescription") @Expose var twitterDescription: Any? = null
    @SerializedName("BlogKey") @Expose var blogKey: String? = null
    @SerializedName("Id") @Expose var id: String? = null
    @SerializedName("UserAssessmentYearId") @Expose var userAssessmentYearId: String? = null
    @SerializedName("Email") @Expose var email: String? = null
    @SerializedName("PhoneNumber") @Expose var phoneNumber: String? = null
    @SerializedName("ProcessMode") @Expose var processMode: String? = null
    @SerializedName("PackageId") @Expose var packageId: String? = null
    @SerializedName("Amount") @Expose var amount: String? = null
    @SerializedName("FirstName") @Expose var firstName: String? = null
    @SerializedName("LastName") @Expose var lastName: String? = null
    @SerializedName("Description") @Expose var description: Any? = null
    @SerializedName("PaymentStatus") @Expose var paymentStatus: Boolean? = null
    @SerializedName("Name") @Expose var name: String? = null
    @SerializedName("EmailId") @Expose var emailId: String? = null
    @SerializedName("PhoneNo") @Expose var phoneNo: String? = null
    @SerializedName("Comments") @Expose var comments: String? = null

    constructor (){
    }
    companion object {
        val instance: MoreModel by lazy { MoreModel() }
    }
}