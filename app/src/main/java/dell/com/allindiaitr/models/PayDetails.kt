package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PayDetails {

    @SerializedName("PackageId")
    @Expose
    var packageId: String? = ""
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
    @SerializedName("PackageCategoryId")
    @Expose
    var packageCategoryId: Any? = null
    @SerializedName("PackageAddOnsList")
    @Expose
    var packageAddOnsList: List<PackageAddOnsList>? = null
    @SerializedName("UserAssestmentYearId")
    @Expose
    var userAssestmentYearId: Any? = null
    @SerializedName("ActualAmount")
    @Expose
    var actualAmount: Double? = null
    @SerializedName("ServiceTax")
    @Expose
    var serviceTax: Double? = null
    @SerializedName("ServiceTaxRate")
    @Expose
    var serviceTaxRate: Double? = null
    @SerializedName("totalPayment")
    @Expose
    var totalPayment: Double? = null
    @SerializedName("TotalAmount")
    @Expose
    var totalAmount: Double? = null
    @SerializedName("CouponDiscountAmount")
    @Expose
    var couponDiscountAmount: Double? = null
    @SerializedName("CouponCodeId")
    @Expose
    var couponCodeId: Int? = null
    @SerializedName("IsCouponApply")
    @Expose
    var isCouponApply: Boolean? = null
    @SerializedName("Id")
    @Expose
    var id: String? = null
    @SerializedName("PersonalDetail")
    @Expose
    var personalDetail: PersonalDetail? = null
    @SerializedName("appUtility")
    @Expose
    var appUtility: AppUtility? = null

}
