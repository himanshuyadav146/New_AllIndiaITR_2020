package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName




class CouponModel {
    @SerializedName("Coupon")
    @Expose
    private var coupon: Coupon? = null
    @SerializedName("Message")
    @Expose
    private var message: List<String>? = null

    @SerializedName("CouponCode")
    @Expose
    private var CouponCode: String? = null

    @SerializedName("ParentId")
    @Expose
    private var ParentId: String? = null

    @SerializedName("PackageId")
    @Expose
    private var PackageId: String? = null

    fun getCoupon(): Coupon? {
        return coupon
    }

    fun setCoupon(coupon: Coupon) {
        this.coupon = coupon
    }

    fun getMessage(): List<String>? {
        return message
    }

    fun setMessage(message: List<String>) {
        this.message = message
    }

        fun getPackageId():String?{
        return PackageId;
    }

    fun setPackageId(packageID:String){
        this.PackageId=packageID
    }

    fun getCouponCode():String?{
        return CouponCode;
    }

    fun setCouponCode(CouponCode:String){
        this.CouponCode=CouponCode
    }

    fun getParentId():String{
        return ParentId.toString();
    }
    fun setParentId(ParentId:String)
    {
        this.ParentId=ParentId
    }


}