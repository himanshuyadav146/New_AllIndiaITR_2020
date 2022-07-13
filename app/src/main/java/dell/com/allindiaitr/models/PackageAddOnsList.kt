package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PackageAddOnsList {

    @SerializedName("Id")
    @Expose
    var id: String? = null
    @SerializedName("PackageName")
    @Expose
    var packageName: String? = null
    @SerializedName("PackageTitle")
    @Expose
    var packageTitle: String? = null
    @SerializedName("PackageDescription")
    @Expose
    var packageDescription: String? = null
    @SerializedName("PackageAmount")
    @Expose
    var packageAmount: Int? = null
    @SerializedName("DiscountedAmount")
    @Expose
    var discountedAmount: Int? = null
    @SerializedName("PackageType")
    @Expose
    var packageType: String? = null
    @SerializedName("NewService")
    @Expose
    var newService: Boolean? = null
    @SerializedName("PackageId")
    @Expose
    var packageId: String? = null
    @SerializedName("PackageCategoryId")
    @Expose
    var packageCategoryId: Any? = null

}
