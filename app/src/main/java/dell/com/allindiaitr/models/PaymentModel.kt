package dell.com.allindiaitr.models

import android.util.Log
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONArray


class PaymentModel {

    @SerializedName("checksum")
    @Expose
    private var checksum: String? = null
    @SerializedName("TransactionId")
    @Expose
    private var transactionId: String? = null

    @SerializedName("ORDERID")
    @Expose
    private var ORDERID: String? = null


    @SerializedName("parameters")
    @Expose
    private var parameters: Parameters? = null



    @SerializedName("GSTIN")
    @Expose
    private var GSTIN:String?=null

    @SerializedName("CompanyName")
    @Expose
    private var CompanyName:String?=null


    @SerializedName("CompanyAddress")
    @Expose
    private var CompanyAddress:String?=null

    @SerializedName("StateId")
    @Expose
    private var StateId:String?=null

    //******************************************Check Status
    @SerializedName("status")
    @Expose
    private var status: String? = null
    @SerializedName("RESPMSG")
    @Expose
    private var rESPMSG: String? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getRESPMSG(): String? {
        return rESPMSG
    }

    fun setRESPMSG(rESPMSG: String) {
        this.rESPMSG = rESPMSG
    }

    //******************************************Check Status

    //***********************************userPackageAddOnsList

    @SerializedName("UserPackageAddOnsList")
    @Expose
    private var UserPackageAddOnsList: List<ModelSelectedPackages>? = null


    fun getUserPackageAddOnsList(): List<ModelSelectedPackages>? {
        return UserPackageAddOnsList
    }

    fun setUserPackageAddOnsList(userPackageAddOnsList: List<ModelSelectedPackages>) {
        this.UserPackageAddOnsList = userPackageAddOnsList
    }



//    @SerializedName("UserPackageAddOnsList")
//    @Expose
//    private var UserPackageAddOnsList: JSONArray? = null
//
//    fun getUserPackageAddOnsList(): JSONArray? {
//        return UserPackageAddOnsList
//    }
//
//    fun setUserPackageAddOnsList(userPackageAddOnsList: JSONArray) {
//        this.UserPackageAddOnsList = userPackageAddOnsList
//    }

//    @SerializedName("UserPackageAddOnsListStr")
//    @Expose
//    private var UserPackageAddOnsListStr: String? = null
//
//    fun getUserPackageAddOnsListStr(): String? {
//        return UserPackageAddOnsListStr
//    }
//
//    fun setUserPackageAddOnsListStr(UserPackageAddOnsListStr: String) {
//        this.UserPackageAddOnsListStr = UserPackageAddOnsListStr
//    }






//    @SerializedName("UserPackageAddOnsList")
//    @Expose
//    private var userPackageAddOnsList: ArrayList<String>? = null
//
//    fun getUserPackageAddOnsList(): ArrayList<String>? {
//        return userPackageAddOnsList
//    }
//
//    fun setUserPackageAddOnsList(userPackageAddOnsList: ArrayList<String>) {
//        this.userPackageAddOnsList = userPackageAddOnsList
//    }

//    @SerializedName("UserPackageAddOnsList")
//    @Expose
//    private var userPackageAddOnsList: List<UserPackageAddOnsList>? = null
//
//    fun getUserPackageAddOnsList(): List<UserPackageAddOnsList>? {
//        return userPackageAddOnsList
//    }
//
//    fun setUserPackageAddOnsList(userPackageAddOnsList: List<UserPackageAddOnsList>?) {
//        this.userPackageAddOnsList = userPackageAddOnsList
//    }



    //***********************************userPackageAddOnsList



//******************************************************************************************************************
    @SerializedName("UserId")
    @Expose
    private var UserId:String?=null

    @SerializedName("UserAssestmentYearId")
    @Expose
    private var UserAssestmentYearId:String?=null

    @SerializedName("PackageId")
    @Expose
    private var PackageId:String?=null

    @SerializedName("ProcessMode")
    @Expose
    private var ProcessMode:String?=null

    @SerializedName("PackageType")
    @Expose
    private var PackageType:String?=null

//    @SerializedName("IsMobile")
//    @Expose
//    private var IsMobile:String?=null

    @SerializedName("IsMobile")
    @Expose
    private var IsMobile:Boolean?=null

    @SerializedName("PaymentType")
    @Expose
    private var PaymentType:String?=null

    @SerializedName("ActualAmount")
    @Expose
    private var ActualAmount:String?=null

//    @SerializedName("ServiceTax")
//    @Expose
//    private var ServiceTax:String?=null

    @SerializedName("ServiceTax")
    @Expose
    private var ServiceTax:Int?=null

    @SerializedName("TotalAmount")
    @Expose
    private var TotalAmount:String?=null

    @SerializedName("IsCouponApply")
    @Expose
    private var IsCouponApply:String?=null

    @SerializedName("CouponId")
    @Expose
    private var CouponId:Int?=null

    @SerializedName("IsGSTIN")
    @Expose
    private var IsGSTIN:String?=null

    @SerializedName("MID")
    @Expose
    private var mID: String? = null

    @SerializedName("TXNID")
    @Expose
    private var TXNID: String? = null

    @SerializedName("CHECKSUMHASH")
    @Expose
    private var CHECKSUMHASH: String? = null


    fun getCHECKSUMHASH(): String? {
        return CHECKSUMHASH
    }

    fun setCHECKSUMHASH(CHECKSUMHASH: String) {
        this.CHECKSUMHASH = CHECKSUMHASH
    }

    fun getTXNID(): String? {
        return TXNID
    }

    fun setTXNID(TXNID: String) {
        this.TXNID = TXNID
    }


    fun getMID(): String? {
        return mID
    }

    fun setMID(mID: String) {
        this.mID = mID
    }





    fun getIsGSTIN():String?{
        return IsGSTIN;
    }

    fun setIsGSTIN(IsGSTIN:String){
        this.IsGSTIN=IsGSTIN
    }


//    fun getCouponId():String?{
//        return CouponId;
//    }
//
//    fun setCouponId(CouponId:String){
//        this.CouponId=CouponId
//    }

    fun getCouponId():Int?{
        return CouponId;
    }

    fun setORDERID(ORDERID:String){
        this.ORDERID=ORDERID
    }

    fun getORDERID():String?{
        return ORDERID;
    }


    fun setCouponId(CouponId:Int){
        this.CouponId=CouponId
    }

    fun getIsCouponApply():String?{
        return IsCouponApply;
    }

    fun setIsCouponApply(IsCouponApply:String){
        this.IsCouponApply=IsCouponApply
    }

    fun getTotalAmount():String?{
        return TotalAmount;
    }

    fun setTotalAmount(TotalAmount:String){
        this.TotalAmount=TotalAmount
    }




    fun getServiceTax():Int?{
        return ServiceTax;
    }

    fun setServiceTax(ServiceTax:Int){
        this.ServiceTax=ServiceTax
    }

//    fun getServiceTax():String?{
//        return ServiceTax;
//    }
//
//    fun setServiceTax(ServiceTax:String){
//        this.ServiceTax=ServiceTax
//    }



    fun getActualAmount():String?{
        return ActualAmount;
    }

    fun setActualAmount(ActualAmount:String){
        this.ActualAmount=ActualAmount
    }



    fun getPaymentType():String?{
        return PaymentType;
    }

    fun setPaymentType(PaymentType:String){
        this.PaymentType=PaymentType
    }




    fun getIsMobile():Boolean?{
        return IsMobile;
    }

    fun setIsMobile(IsMobile:Boolean){
        this.IsMobile=IsMobile
    }



//    fun getIsMobile():String?{
//        return IsMobile;
//    }
//
//    fun setIsMobile(IsMobile:String){
//        this.IsMobile=IsMobile
//    }



    fun getPackageType():String?{
        return PackageType;
    }

    fun setPackageType(PackageType:String){
        this.PackageType=PackageType
    }


    fun getProcessMode():String?{
        return ProcessMode;
    }

    fun setProcessMode(ProcessMode:String){
        this.ProcessMode=ProcessMode
    }



    fun getPackageId():String?{
        return PackageId;
    }

    fun setPackageId(PackageId:String){
        this.PackageId=PackageId
    }



    fun getUserAssestmentYearId():String?{
        return UserAssestmentYearId;
    }

    fun setUserAssestmentYearId(UserAssestmentYearId:String){
        this.UserAssestmentYearId=UserAssestmentYearId
    }


    fun getUserId():String?{
        return UserId;
    }

    fun setUserId(UserId:String){
        this.UserId=UserId
    }




//*************************************************************************************************************************

    fun getGSTIN():String?{
        return GSTIN;
    }

    fun setGSTIN(GSTIN:String){
        this.GSTIN=GSTIN
    }

    fun getCompanyName():String?{
        return CompanyName
    }

    fun setCompanyName(CompanyName:String)
    {
        this.CompanyName=CompanyName
    }

    fun setCompanyAddress(CompanyAddress:String)
    {
        this.CompanyAddress=CompanyAddress
    }
    fun getCompanyAddress():String?{
        return CompanyAddress
    }


    fun setStateId(StateId:String)
    {
        this.StateId=StateId
    }

    fun getStateId():String?
    {
        return StateId
    }



    fun getChecksum(): String? {
        return checksum
    }

    fun setChecksum(checksum: String) {
        this.checksum = checksum
    }

    fun getTransactionId(): String? {
        return transactionId
    }

    fun setTransactionId(transactionId: String) {
        this.transactionId = transactionId
    }

    fun getParameters(): Parameters? {
        return parameters
    }

    fun setParameters(parameters: Parameters) {
        this.parameters = parameters
    }
}