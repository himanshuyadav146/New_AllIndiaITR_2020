package dell.com.allindiaitr.interfaces

import dell.com.allindiaitr.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface API_Interface {

    @POST("/api/Account/NewLoginSignUp")
    fun postLOGINOTP(@Body logInwithOTPModel: LogInwithOTPModel): Call<LogInwithOTPModel>

    @POST("/api/Account/VerifyOTP")
    fun postVerifyOTP(@Body loginWithOTPModel: LogInwithOTPModel): Call<LogInwithOTPModel>

    @POST("api/Account/ReGenerateOTP")
    fun resendOTP(@Body loginWithOTPModel: LogInwithOTPModel): Call<LogInwithOTPModel>

    @GET("api/Account/ForceUpdateMobileApp")
    fun versionUpdate (@Query("AppType") AppType: String, @Query("id") id: String): Call<NewItrBase>

    @Headers("MobileApp: android")
    @GET("api/Child/GetChildUserList?")
    fun getITRuserList(@Query("id") id: String): Call<List<NewItrBase>>

    @POST("api/SubmitDocument/chooseUserForNewITR?")
    fun getchooseUserForNewITR(@Body newItrBase: NewItrBase): Call<NewItrBase>

    @GET("api/OrderStatus/SubmitDocumentStatus?")
    fun getITRStatus(@Query("id") id: String): Call<NewItrBase>

    @GET("api/Child/GetChildPersonalDetails")
    fun getInformation(@Query("id") id: String , @Query("UserAssessmentYearId") UserAssessmentYearId: String): Call<NewItrBase>

    //for getting manual form 16
    @GET("api/AssestmentYears/GetActiveAssesstmentYears")
    fun getFinancialAndAssesmentYear(): Call<List<FinancialYear>>

    @POST("api/Child")
    fun postInformation(@Body newItrBase: NewItrBase): Call<NewItrBase>

    @GET("api/Child/GetFinancialYear")
    fun getFinancialYear(): Call<List<NewItrBase>>

    @GET("api/Child/GetDocumentsByFileType?")
    fun getDocumentList(@Query("UserAssestmentYearId") UserAssestmentYearId : String, @Query("FileType") FileType : String): Call<NewItrBase>

//    @Multipart
//    @POST("api/Child/UploadMobileUserDocument")
//    fun uploadFile(@Part file: MultipartBody.Part,
//                   @Part("UserAssessmentYearId") userAssessmentYearId : RequestBody,
//                   @Part("FileType") fileType : RequestBody,
//                   @Part("FilePassword") filePassword : RequestBody): Call<ResponseBody>


    @Multipart
    @POST("api/Child/UploadMobileUserDocument")
    fun uploadFile(@Part file: MultipartBody.Part,
                   @Part("UserAssessmentYearId") userAssessmentYearId : RequestBody,
                   @Part("FileType") fileType : RequestBody,
                   @Part("FilePassword") filePassword : RequestBody): Call<ResponseBody>



    @DELETE("api/Child/DeleteMobileDocument?")
    fun deleteDocument (@Query("id") id: String): Call<ResponseBody>

    @GET("api/Child/GetPanAadhaarManualDetails?")
    fun getAadhaarDetails(@Query("id") id: String): Call<NewItrBase>

    @POST("api/Child/SaveAadhaarCardDetails")
    @FormUrlEncoded
    fun postAadhaarDetails(@Field("UserAssestmentYearId") UserAssestmentYearId : String,
                           @Field("UserId") UserId : String,
                           @Field("FatherName") FatherName : String,
                           @Field("AadhaarCardNumber") AadhaarCardNumber : String,
                           @Field("Address1") Address1 : String,
                           @Field("Address2") Address2 : String,
                           @Field("City") City : String,
                           @Field("StateId") StateId : String,
                           @Field("PinCode") PinCode : String): Call<NewItrBase>

    @GET("api/Child/GetDocumentsByFileType?")
    fun getAadhaarDocumentList(@Query("UserAssestmentYearId") UserAssestmentYearId : String,
                               @Query("FileType") FileType : String): Call<NewItrBase>

    @GET("api/Child/GetAllBankAccounts?")
    fun getBankList (@Query("UserAssestmentYearId") UserAssestmentYearId :  String): Call<List<NewItrBase>>


    @GET("api/Child/GetSpecificBankAccountDetails?")
    fun getBankDetail (@Query("id") id : String): Call<NewItrBase>

    @PUT("api/Child/UpdateBankAccountDetails?")
    fun putBankDetails (@Query("id") id : String, @Body newItrBase : NewItrBase): Call<ResponseBody>

    @POST("api/Child/SaveBankAccountDetails")
    fun postBankDetails(@Body newItrBase : NewItrBase): Call<Void>

    @DELETE("api/Child/DeleteBankDetails?")
    fun deleteBank (@Query("id") id : String): Call<ResponseBody>

    @GET("api/Child/GetOtherIncome?")
    fun getOtherIncome (@Query("id") id : String): Call<NewItrBase>

    @PUT("api/Child/UpdateOtherIncome?")
    fun putOtherIncome (@Query("id") id : String, @Body newItrBase : NewItrBase): Call<ResponseBody>

    @POST("api/Child/AddOtherIncome")
    fun postOtherIncome(@Body newItrBase : NewItrBase): Call<Void>

    @POST("api/Child/BusinessRange")
    fun postBusinessRange(@Body newItrBase : NewItrBase): Call<Void>

    @POST("api/Child/AddComment")
    fun postComment(@Body comment: Comment): Call<Comment>

    @POST("api/HraCalculator")
    fun hra_calculation(@Body usefulToolsModel: UsefulToolsModel): Call<Void>

    @POST("api/HraRentReceipt?")
    fun hra_rentReceipt(@Body usefulToolsModel: UsefulToolsModel): Call<Void>

    @Multipart
    @POST("api/supportMessage/SendEmail")
    fun mailRentReceipt(@Query("Email") email : String,
                        @Part file: MultipartBody.Part): Call<ResponseBody>

    @POST("api/IncomeTaxCalculator")
    fun tax_calculation(@Body usefulToolsModel: UsefulToolsModel): Call<Void>

    @GET("api/Order/AllOrders?")
    fun getAllOrder(@Query("id") id: String): Call<List<AllOrderStatusModel>>

    @GET("api/Refer2Earn/GetRefralCode?")
    fun getReferCode(@Header("Authorization") token: String, @Query("Id") Id: String): Call<ReferEarnModel>

    @GET("api/Refer2Earn/GetEarnings?")
    fun getReferEarnings(@Header("Authorization") token: String, @Query("Id") Id: String): Call<ReferEarnModel>

    @GET("api/Refer2Earn/GetCouponWithdrawn?")
    fun getCouponWithdrawl(@Header("Authorization") token: String, @Query("Id") Id: String): Call<List<ReferEarnModel>>

    @GET("api/Refer2Earn/GetBankDetails?")
    fun getWithdrawlBank(@Header("Authorization") token: String, @Query("Id") Id: String): Call<ReferEarnModel>

    @POST("api/Refer2Earn/AddCouponBankDetails")
    fun postAddCouponBankDetails(@Header("Authorization") token: String,@Body referEarnModel: ReferEarnModel): Call<Void>

    @GET("api/BlogInformation/GetTop10Blog")
    fun getBlogs(): Call<List<MoreModel>>

    @GET("/api/UserPaymentDetail/GetPaymentLink")
    fun getCustomPaymentList(@Query("Id") Id: String): Call<List<MoreModel>>

    @POST("api/ContactUs")
    fun postContactUs(@Body moreModel: MoreModel) : Call<Void>

    @GET("api/UserPaymentDetail/GetPackageByUserAssestmentYearId")
    fun GetPackageId(@Query("id") id: String,@Query("ProcessMode") processMode:String): Call<NewItrBase>

    @Multipart
    @POST("/api/OrderStatus/UpdateDocumentStatus")
    fun statusUploadFile(@Part file: MultipartBody.Part,
                         @Part("StatusUpdate") statusUpdate : RequestBody,
                         @Part("UserAssessmentYearId") userAssessmentYearId : RequestBody,
                         @Part("FilePassword") filePassword : RequestBody): Call<ResponseBody>

    @Multipart
    @POST("/api/OrderStatus/UpdateDocumentStatus")
    fun statusUploadFile1(@Part file: MultipartBody.Part,
                         @Part("StatusUpdate") statusUpdate : RequestBody,
                         @Part("FilePassword") filePassword : RequestBody): Call<ResponseBody>


    @Multipart
    @POST("/api/OrderStatus/UpdateDocumentStatus")
    fun statusUpdate(@Part("StatusUpdate") statusUpdate : RequestBody,
                         @Part("UserAssessmentYearId") userAssessmentYearId : RequestBody,
                         @Part("FilePassword") filePassword : RequestBody): Call<ResponseBody>

    @GET("api/Child/GetComments?")
    fun getChat(@Query("id") id: String): Call<List<Chat>>

    @POST("api/Child/AddComment")
    fun postChat(@Body chat: Chat): Call<Void>


    @POST("api/UserPaymentDetail/GetCouponByCouponCode?")
    fun postCouponCode(@Body couponModel:CouponModel):Call<CouponModel>


//    @POST("api/PayTMGateway/Payment")
//    fun postPayTMGateway(@Header("Authorization") token: String,@Body paymentModel: PaymentModel ):Call<PaymentModel>

    @POST
    fun postPayTMGateway(@Url url:String,@Header("Authorization") token: String, @Body paymentModel: PaymentModel ):Call<PaymentModel>

    @POST( "api/PayTMGateway/PaytmResponceCheck")
    fun checkStatus(@Query("mid") mid: String, @Query("ORDER_ID") ORDERID:String,@Query("checksum")CHECKSUMHASH:String):Call<PaymentModel>


//    @POST
//    fun checkStatus(@Url url:String,@Query("mid") mid: String, @Query("ORDER_ID") ORDERID:String, @Query("checksum")CHECKSUMHASH:String):Call<PaymentModel>


    @POST( "api/PayUGateway/Payment")
    fun PayUGateway(@Query("mid") mid: String, @Query("ORDER_ID") ORDERID:String,@Query("checksum")CHECKSUMHASH:String):Call<PaymentModel>


//    @POST( "api/PayTMGateway/PaytmResponceCheck?")
//    fun checkStatus(@Field("mid") mid: String, @Field("&ORDER_ID=") ORDERID:String, @Field("&checksum=")CHECKSUMHASH:String):Call<PaymentModel>





}