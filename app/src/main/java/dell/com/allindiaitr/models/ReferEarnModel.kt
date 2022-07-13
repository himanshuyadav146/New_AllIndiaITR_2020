package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReferEarnModel {

    @SerializedName("BalanceAmount")
    @Expose
    var balanceAmount: Double? = null
    @SerializedName("EarningAmount")
    @Expose
    var earningAmount: Double? = null
    @SerializedName("WithdrawnAmount")
    @Expose
    var withdrawnAmount: Double? = null
    @SerializedName("EarningsUserList")
    @Expose
    var earningsUserListModel: List<ReferEarnModel>? = null
    @SerializedName("CouponCode")
    @Expose
    var couponCode: String? = null
    @SerializedName("Amount")
    @Expose
    var amount: String? = null
    @SerializedName("CreatedDate")
    @Expose
    var createdDate: String? = null
    @SerializedName("Status")
    @Expose
    var status: String? = null
    @SerializedName("Name")
    @Expose
    var name: String? = null
    @SerializedName("BankName")
    @Expose
    var bankName: String? = null
    @SerializedName("AccountNumber")
    @Expose
    var accountNumber: String? = null
    @SerializedName("IFSCCode")
    @Expose
    var iFSCCode: String? = null
    @SerializedName("Date")
    @Expose
    var date: String? = null
    @SerializedName("PhoneNumber")
    @Expose
    var phoneNumber: String? = null
    @SerializedName("ParentId")
    @Expose
    var parentId: String? = null

    constructor() {

    }
    companion object {
        val instance: ReferEarnModel by lazy { ReferEarnModel() }
    }
}