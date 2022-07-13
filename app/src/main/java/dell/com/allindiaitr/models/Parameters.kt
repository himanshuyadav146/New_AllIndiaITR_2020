package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Parameters {

    @SerializedName("MID")
    @Expose
    private var mID: String? = null
    @SerializedName("ORDER_ID")
    @Expose
    private var oRDERID: String? = null
    @SerializedName("CUST_ID")
    @Expose
    private var cUSTID: String? = null
    @SerializedName("CHANNEL_ID")
    @Expose
    private var cHANNELID: String? = null
    @SerializedName("INDUSTRY_TYPE_ID")
    @Expose
    private var iNDUSTRYTYPEID: String? = null
    @SerializedName("WEBSITE")
    @Expose
    private var wEBSITE: String? = null
    @SerializedName("TXN_AMOUNT")
    @Expose
    private var tXNAMOUNT: String? = null
    @SerializedName("CALLBACK_URL")
    @Expose
    private var cALLBACKURL: String? = null
    @SerializedName("EMAIL")
    @Expose
    private var eMAIL: String? = null
    @SerializedName("MOBILE_NO")
    @Expose
    private var mOBILENO: String? = null

    fun getMID(): String? {
        return mID
    }

    fun setMID(mID: String) {
        this.mID = mID
    }

    fun getORDERID(): String? {
        return oRDERID
    }

    fun setORDERID(oRDERID: String) {
        this.oRDERID = oRDERID
    }

    fun getCUSTID(): String? {
        return cUSTID
    }

    fun setCUSTID(cUSTID: String) {
        this.cUSTID = cUSTID
    }

    fun getCHANNELID(): String? {
        return cHANNELID
    }

    fun setCHANNELID(cHANNELID: String) {
        this.cHANNELID = cHANNELID
    }

    fun getINDUSTRYTYPEID(): String? {
        return iNDUSTRYTYPEID
    }

    fun setINDUSTRYTYPEID(iNDUSTRYTYPEID: String) {
        this.iNDUSTRYTYPEID = iNDUSTRYTYPEID
    }

    fun getWEBSITE(): String? {
        return wEBSITE
    }

    fun setWEBSITE(wEBSITE: String) {
        this.wEBSITE = wEBSITE
    }

    fun getTXNAMOUNT(): String? {
        return tXNAMOUNT
    }

    fun setTXNAMOUNT(tXNAMOUNT: String) {
        this.tXNAMOUNT = tXNAMOUNT
    }

    fun getCALLBACKURL(): String? {
        return cALLBACKURL
    }

    fun setCALLBACKURL(cALLBACKURL: String) {
        this.cALLBACKURL = cALLBACKURL
    }

    fun getEMAIL(): String? {
        return eMAIL
    }

    fun setEMAIL(eMAIL: String) {
        this.eMAIL = eMAIL
    }

    fun getMOBILENO(): String? {
        return mOBILENO
    }

    fun setMOBILENO(mOBILENO: String) {
        this.mOBILENO = mOBILENO
    }
}