package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Coupon {

    @SerializedName("Id")
    @Expose
     val id: Int? = null
    @SerializedName("CouponType")
    @Expose
     val couponType: Any? = null
    @SerializedName("ClientName")
    @Expose
     val clientName: String? = null
    @SerializedName("CouponCode")
    @Expose
     val couponCode: String? = null
    @SerializedName("DiscountType")
    @Expose
     val discountType: String? = null
    @SerializedName("Discount")
    @Expose
     val discount: Int? = null
    @SerializedName("ParentId")
    @Expose
     val parentId: Any? = null
    @SerializedName("IsCouponApply")
    @Expose
     val isCouponApply: Boolean? = null
    @SerializedName("IsItr50Applied")
    @Expose
     val isItr50Applied: Boolean? = null



}