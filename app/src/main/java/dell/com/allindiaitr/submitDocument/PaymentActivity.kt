package dell.com.allindiaitr.submitDocument
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.adapter.OrderPaymentAdapter
import dell.com.allindiaitr.interfaces.API_Interface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.paytm.pgsdk.PaytmMerchant
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.CustomPaymentAdapter
import dell.com.allindiaitr.databinding.ActivityPaymentBinding
import dell.com.allindiaitr.home.CustomPaymentActivity
import dell.com.allindiaitr.models.*
import dell.com.allindiaitr.utils.*
import dell.com.allindiaitr.volley.VolleySingleton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.reflect.Parameter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.HashMap
import kotlin.math.roundToInt


class PaymentActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface: API_Interface
    lateinit var appPreferences: AppPreferences
    lateinit var objItrBase: NewItrBase
    var netAmount: Double = 0.0
    var mIsCouponApply: Boolean = false;
    lateinit var mCouponType: String
    lateinit var mClientName: String
    lateinit var mCouponCode: String
    lateinit var mDiscountType: String
    lateinit var mDiscount: String
    lateinit var mMessage: String
    private var modelSelectedPackagesArrayList: List<ModelSelectedPackages> = ArrayList()
    lateinit var responseBody: NewItrBase
    lateinit var objCouponModel: CouponModel
    lateinit var PaymentType: String
    var mGstInString: String = "false"
    lateinit var mGstInNoString: String
    lateinit var mGstInCompanyString: String
    lateinit var mGstInCompanyAddressString: String
     var mGstInStateString: String="0"
    lateinit var objPaymentModel: PaymentModel
    var statePositionInt: Int = 0
    var mCouponID: Int = 0
    internal var stateIdArrayList = ArrayList<String>()
    internal var stateNameArrayList = ArrayList<String>()
    lateinit var end_point: String

    private var merchantKey: String? = null
    private var userCredentials: String? = null
    lateinit var resultData: String

    val newItrBase = NewItrBase.instance
    private lateinit var binding:ActivityPaymentBinding


    // PaytmPGService Service = PaytmPGService.getProductionService()

    lateinit var Service: PaytmPGService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        appPreferences = AppPreferences(mContext)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        objItrBase = NewItrBase.instance
        objCouponModel = CouponModel()
        objPaymentModel = PaymentModel()
        Service = PaytmPGService.getProductionService()
//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text =getString(R.string.title_otherdetails)
        binding.recyclerViewOrder.setHasFixedSize(true)
        binding.recyclerViewOrder.layoutManager = LinearLayoutManager(mContext)

        binding.userName.text = objItrBase.selectedUserName
        binding.emailId.text = objItrBase.selectedUserEmail
        binding.phoneNo.text = objItrBase.selectedUserMobile
        
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            binding.payNowButton.backgroundTintList = mContext.getColorStateList(R.color.white)
//            binding.paytmButton.backgroundTintList = mContext.getColorStateList(R.color.white)
//        } else {
//            ViewCompat.setBackgroundTintList(
//                binding.payNowButton,
//                ColorStateList.valueOf(Color.rgb(255, 255, 255))
//            )
//            ViewCompat.setBackgroundTintList(
//                binding.paytmButton,
//                ColorStateList.valueOf(Color.rgb(255, 255, 255))
//            )
//        }


        loadJSONFromAsset()
        setCouponVisibilityGone()


        if (ConnectionDetector().isConnectingToInternet(mContext)) {
            GetPackageId();
        } else {
            Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("custom-message")
        )

        binding.applyButton.setOnClickListener {
            if (Validation().isEmpaty(binding.couponEdittext.text.toString(), binding.couponEdittext,"Please enter coupon code")) {
                objCouponModel.setPackageId(responseBody.getPayDetails()?.packageId.toString())
                objCouponModel.setCouponCode(binding.couponEdittext.text.toString())
                objCouponModel.setParentId(appPreferences.parentId.toString())
                applyCouponCode()

            }

        }

//        switch_gstin.setOnCheckedChangeListener { compoundButton, b ->
//            if (b) {
//                ll_gstin.visibility = LinearLayout.VISIBLE
//                mGstInString = "true"
//            } else {
//                ll_gstin.visibility = LinearLayout.GONE
//                mGstInString = "false"
//            }
//        }

        binding.paytmButton.setOnClickListener {
            objPaymentModel.setIsGSTIN(mGstInString)
            statePositionInt = binding.spinnerState.selectedItemPosition
            mGstInStateString = stateIdArrayList[statePositionInt]
            newItrBase.orderMode="AllOrders"
            if (mGstInString == "true") {
                if (Validation().isGstInNumber(
                        binding.edittxtGstinNo.text.toString(),
                        binding.edittxtGstinNo
                    ) &&
                    Validation().isGstInCompanyNameAddress(
                        binding.edittxtCompName.text.toString(),
                        binding.edittxtCompName
                    ) &&
                    Validation().isStateValid(mGstInStateString, mContext) &&
                    Validation().isGstInCompanyNameAddress(
                        binding.edittxtCompAdd.text.toString(),
                        binding.edittxtCompAdd
                    )
                ) {
                    objPaymentModel.setPaymentType("PayTM")
                    objPaymentModel.setGSTIN(binding.edittxtGstinNo.text.toString())
                    objPaymentModel.setCompanyName(binding.edittxtCompName.text.toString())
                    objPaymentModel.setCompanyAddress(binding.edittxtCompAdd.text.toString())
                    objPaymentModel.setStateId(mGstInStateString)
                    // setPackageAddOnsListIntoModel()
                    setDataintoModel()
                    if (ConnectionDetector().isConnectingToInternet(mContext)) {
                        end_point = "api/PayTMGateway/Payment"
//                        postPayTMGateway(objPaymentModel)
//                        postPayment()
                        AsyncTaskExample(
                            this,
                            end_point,
                            objItrBase,
                            objPaymentModel
                        ).execute(end_point)
                    }
                }

            } else {
                objPaymentModel.setPaymentType("PayTM")
                objPaymentModel.setGSTIN("")
                objPaymentModel.setCompanyName("")
                objPaymentModel.setCompanyAddress("")
                objPaymentModel.setStateId("")
                //  setPackageAddOnsListIntoModel()
                setDataintoModel()
                if (ConnectionDetector().isConnectingToInternet(mContext)) {
                    end_point = "api/PayTMGateway/Payment"
//                    postPayTMGateway(objPaymentModel)
//                    postPayment()
                    AsyncTaskExample(
                        this,
                        end_point,
                        objItrBase,
                        objPaymentModel
                    ).execute(end_point)

                }

            }
        }

      //  ButtonVisibility().hideButton(mContext, binding.activityOrderPaymentDetails, binding.paytmButton)

        binding.paytmButton.setOnClickListener {
            if (mGstInString.equals("true")) {

            } else {
                objPaymentModel.setPaymentType("PayTM")
                setDataintoModel()
                if (ConnectionDetector().isConnectingToInternet(mContext)) {
                    end_point = "api/PayTMGateway/Payment"
                    postPayTMGateway(objPaymentModel)
                }

            }
        }

        binding.payNowButton.setOnClickListener {
            if (mGstInString.equals("true")) {

            } else {
                objPaymentModel.setPaymentType("PayU")
                setDataintoModel()
                if (ConnectionDetector().isConnectingToInternet(mContext)) {
                    end_point = "api/PayUGateway/Payment"
                    postPayTMGateway(objPaymentModel)
                }
            }
        }


        binding.switchGstin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.llGstin.visibility = View.VISIBLE
                mGstInString = "true"
            } else {
                binding.llGstin.visibility = View.GONE
                mGstInString = "false"
            }
        }
    }


    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }


    private fun setPackageAddOnsListIntoModel() {
        if (modelSelectedPackagesArrayList!!.isNotEmpty()) {
            objPaymentModel.setUserPackageAddOnsList(modelSelectedPackagesArrayList)
        }

    }

    private fun setDataintoModel() {
        mGstInNoString = binding.edittxtGstinNo.text.toString()
        mGstInCompanyString = binding.edittxtCompName.text.toString()
        mGstInCompanyAddressString = binding.edittxtCompAdd.text.toString()
        statePositionInt = binding.spinnerState.selectedItemPosition
        mGstInStateString = stateIdArrayList[statePositionInt]
        //  appPreferences.setOrderMode("AllOrders")
        if (mGstInString == "true") {
            objPaymentModel.setGSTIN(mGstInNoString)
            objPaymentModel.setCompanyName(mGstInCompanyString)
            objPaymentModel.setCompanyAddress(mGstInCompanyAddressString)
            objPaymentModel.setStateId(mGstInStateString)
        } else {
            objPaymentModel.setGSTIN("")
            objPaymentModel.setCompanyName("")
            objPaymentModel.setCompanyAddress("")
            objPaymentModel.setStateId("")
        }

        objPaymentModel.setUserId(appPreferences.parentId.toString())
        objPaymentModel.setUserAssestmentYearId(objItrBase.selectedUser_userAssessmentYearUserID.toString())
       // objPaymentModel.setPackageId(responseBody.getPayDetails()!!.packageId.toString())
        objPaymentModel.setPackageId(responseBody.getPayDetails()?.packageId!!)
        objItrBase.processMode?.let { objPaymentModel.setProcessMode(it) }
        objItrBase.processMode?.let { objPaymentModel.setPackageType(it) }

//        objPaymentModel.setPackageId(objItrBase.getPayDetails()!!.packageId.toString())
//        objPaymentModel.setProcessMode(objItrBase.processMode.toString())
//        objPaymentModel.setPackageType(objItrBase.processMode.toString())
        // Dublicate is avalable

        objPaymentModel.setIsMobile(true)
        // objPaymentModel.setPaymentType("PayTM")
        objPaymentModel.setActualAmount(getSplittedAmount(binding.originalPriceTextView.getText().toString()))
        objPaymentModel.setServiceTax(responseBody.getPayDetails()?.serviceTaxRate!!.roundToInt())
        objPaymentModel.setTotalAmount(getSplittedAmount(binding.totalAmountTextView.getText().toString()))
        objPaymentModel.setIsCouponApply(mIsCouponApply.toString())
        objPaymentModel.setCouponId(mCouponID)
        objPaymentModel.setIsGSTIN(mGstInString)


        if (modelSelectedPackagesArrayList!!.isNotEmpty()) {
            val jsonArray = JSONArray()
            var userPackageAddOnsList: List<UserPackageAddOnsList>

            for (n in modelSelectedPackagesArrayList.indices) {

                val contact = JSONObject()
                contact.put("PackageId", modelSelectedPackagesArrayList.get(n).getPackageId())

                jsonArray.put(n, contact)
//                var userPackageAddOnsList1:UserPackageAddOnsList= UserPackageAddOnsList()
//                userPackageAddOnsList1.setPackageId(modelSelectedPackagesArrayList.get(n).getPackageId())
                //userPackageAddOnsList?.get(n)?.setPackageId(modelSelectedPackagesArrayList.get(n).getPackageId())

            }

//            objPaymentModel.setUserPackageAddOnsList(jsonArray)
            //   objPaymentModel.setUserPackageAddOnsListStr(jsonArray.toString())
            setPackageAddOnsListIntoModel()
        }
    }

    private fun postPayTMGateway(objPaymentModel1: PaymentModel) {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))
        val call: Call<PaymentModel> = apI_Interface.postPayTMGateway(
            end_point,
            "Bearer " + appPreferences.accessTokenId,
            objPaymentModel1
        )
        call.enqueue(object : Callback<PaymentModel> {
            override fun onFailure(call: Call<PaymentModel>?, t: Throwable?) {
                //To change body of created functions use File | Settings | File Templates.
                dialog.hideDialog()
            }

            override fun onResponse(call: Call<PaymentModel>?, response: Response<PaymentModel>?) {
                //To change body of created functions use File | Settings | File Templates.
                dialog.hideDialog()
                if (response?.isSuccessful!!) {

                    if (objPaymentModel.getPaymentType() == "PayTM") {
                        if (!TextUtils.isEmpty(response.body().getChecksum())) {
                            objPaymentModel.setChecksum(response.body().getChecksum().toString())
                            objPaymentModel.setTransactionId(response.body().getTransactionId().toString())
                            response.body().getParameters()
                                ?.let { objPaymentModel.setParameters(it) }
                            paytmNavigation()
                        } else {

                        }
                    } else {
                        objPaymentModel.setTransactionId(response.body().getTransactionId().toString())
                        //navigateToBaseActivity()
                    }
                }
            }

        })
    }


    //*************************************Async Task For payTM


    @SuppressLint("StaticFieldLeak")
    inner class AsyncTaskExample(
        private var activity: PaymentActivity?,
        endpoint: String,
        objItrBase: NewItrBase,
        objPaymentModel: PaymentModel
    ) : AsyncTask<String, Void, String>() {
//        val serverURL: String = "http://125.63.99.18:2020/" + end_point
        val serverURL: String = "https://www.allindiaitr.com/" + end_point  // Live
      //  val serverURL: String = APIClient.BaseUrl + end_point
        val url = URL(serverURL)
        var token: String = appPreferences.accessTokenId.toString()
        val connection = url.openConnection() as HttpURLConnection
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

        override fun onPreExecute() {
            super.onPreExecute()
//            var dialog = AlertDialogueManager(mContext, "Please Wait")
        }

        override fun doInBackground(vararg p0: String?): String {
            var result = ""
            try {
                var appPreferences1: AppPreferences = AppPreferences(activity!!.mContext)


                connection.requestMethod = "POST"
                connection.connectTimeout = 300000
                connection.connectTimeout = 300000
                connection.doOutput = true
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Authorization", "Bearer " + token)

                var responseDetailsJson = JSONObject()
                responseDetailsJson.put("UserId", appPreferences.parentId)
                responseDetailsJson.put("UserAssestmentYearId", objPaymentModel.getUserAssestmentYearId())
                responseDetailsJson.put("PackageId", objPaymentModel.getPackageId())
                responseDetailsJson.put("ProcessMode", objPaymentModel.getProcessMode())
                responseDetailsJson.put("PackageType", objPaymentModel.getPackageType())
                responseDetailsJson.put("IsMobile", true)
                responseDetailsJson.put("PaymentType", objPaymentModel.getPaymentType())

                responseDetailsJson.put(
                    "ActualAmount", getSplittedAmount(binding.originalPriceTextView.text.toString())
                )
                responseDetailsJson.put("ServiceTax", objPaymentModel.getServiceTax())

                responseDetailsJson.put(
                    "TotalAmount",
                    getSplittedAmount(binding.totalAmountTextView.text.toString())
                )
                responseDetailsJson.put("IsCouponApply", objPaymentModel.getIsCouponApply())
                responseDetailsJson.put("CouponId", objPaymentModel.getCouponId())
                responseDetailsJson.put("IsGSTIN", objPaymentModel.getIsGSTIN())



                if (modelSelectedPackagesArrayList!!.isNotEmpty()) {
                    val jsonArray = JSONArray()
                    var userPackageAddOnsList: List<UserPackageAddOnsList>

                    for (n in modelSelectedPackagesArrayList.indices) {
                        val contact = JSONObject()
                        contact.put(
                            "PackageId",
                            modelSelectedPackagesArrayList.get(n).getPackageId()
                        )
                        jsonArray.put(n, contact)
                    }
                    responseDetailsJson.put("UserPackageAddOnsList", jsonArray)
                }

                if (mGstInString == "true") {
                    responseDetailsJson.put("GSTIN", objPaymentModel.getGSTIN())
                    responseDetailsJson.put("CompanyName", objPaymentModel.getCompanyName())
                    responseDetailsJson.put("CompanyAddress", objPaymentModel.getCompanyAddress())
                    responseDetailsJson.put("StateId", objPaymentModel.getStateId())
                } else {
                    responseDetailsJson.put("GSTIN", "")
                    responseDetailsJson.put("CompanyName", "")
                    responseDetailsJson.put("CompanyAddress", "")
                    responseDetailsJson.put("StateId", "")
                }

                val jsonString = responseDetailsJson.toString()

                val postData: ByteArray = jsonString.toByteArray(StandardCharsets.UTF_8)

                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Content-lenght", postData.size.toString())
                connection.setRequestProperty("Content-Type", "application/json")


                try {
                    val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
                    outputStream.write(postData)
                    outputStream.flush()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                if (connection.responseCode == 200) {
                    try {
                        resultData = connection.inputStream.bufferedReader().readText()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return resultData
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                dialog.hideDialog()
                val jsonObject = JSONObject(result)
                //TransactionId = jsonObject.optString("TransactionId")

                if (objPaymentModel.getPaymentType() == "PayTM") {
                        objPaymentModel.setChecksum(jsonObject.optString("checksum"))
                        objPaymentModel.setTransactionId(jsonObject.optString("TransactionId"))
                          val jsonObjecctForParameters=JSONObject(jsonObject.optString("parameters"))
                        var obj:Parameters= Parameters()
                        obj.setMID(jsonObjecctForParameters.optString("MID"))
                        obj.setORDERID(jsonObjecctForParameters.optString("ORDER_ID"))
                        obj.setCUSTID(jsonObjecctForParameters.optString("CUST_ID"))
                        obj.setCHANNELID(jsonObjecctForParameters.optString("CHANNEL_ID"))
                        obj.setINDUSTRYTYPEID(jsonObjecctForParameters.optString("INDUSTRY_TYPE_ID"))
                        obj.setWEBSITE(jsonObjecctForParameters.optString("WEBSITE"))
                        obj.setTXNAMOUNT(jsonObjecctForParameters.optString("TXN_AMOUNT"))
                        obj.setCALLBACKURL(jsonObjecctForParameters.optString("CALLBACK_URL"))
                        obj.setEMAIL(jsonObjecctForParameters.optString("EMAIL"))
                        obj.setMOBILENO(jsonObjecctForParameters.optString("MOBILE_NO"))
                        objPaymentModel.setParameters(obj)
                        paytmNavigation()

                } else {
                    objPaymentModel.setTransactionId(jsonObject.optString("TransactionId"))
                    //navigateToBaseActivity()
                }

            } catch (e: Exception) {
               Toast.makeText(mContext,"Error",Toast.LENGTH_LONG).show()
            }


        }

    }


    private fun postPayment() {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

        val path = APIClient().BaseUrl + end_point
        val params = HashMap<String,String>()
        val params1 = HashMap<String, JSONArray>()
        params["UserId"] = appPreferences.parentId.toString()
        params["UserAssestmentYearId"] = objItrBase.selectedUser_userAssessmentYearUserID.toString()
        params["PackageId"] = objPaymentModel.getPackageId().toString()
        params["ProcessMode"] = newItrBase.processMode.toString()
        params["PackageType"] = objPaymentModel.getPackageType().toString()
        params["IsMobile"] = objPaymentModel.getIsMobile().toString()
        params["PaymentType"] = objPaymentModel.getPaymentType().toString()
        params["ActualAmount"] = objPaymentModel.getActualAmount().toString()
        params["ServiceTax"] = objPaymentModel.getServiceTax().toString()
        params["TotalAmount"] = objPaymentModel.getTotalAmount().toString()
        params["IsCouponApply"] = objPaymentModel.getIsCouponApply().toString()
        params["CouponId"] = objPaymentModel.getCouponId().toString()
        params["IsGSTIN"] = objPaymentModel.getIsGSTIN().toString()

        if (modelSelectedPackagesArrayList!!.isNotEmpty()) {
            val jsonArray = JSONArray()
            var userPackageAddOnsList: List<UserPackageAddOnsList>

            for (n in modelSelectedPackagesArrayList.indices) {
                val contact = JSONObject()
                contact.put("PackageId", modelSelectedPackagesArrayList.get(n).getPackageId())
                jsonArray.put(n, contact)
            }
            params1["UserPackageAddOnsList"] = jsonArray

            // params["UserPackageAddOnsList"] = jsonArray
        }


        // val jsArray = Gson().toJson(modelSelectedPackagesArrayList)


        // jsonText:String =  Gson().toJson(objPaymentModel.getUserPackageAddOnsList());


        val jsonObject = JSONObject(params as Map<*, *>?)

        val request =
            JsonObjectRequest(path, jsonObject, com.android.volley.Response.Listener { response ->
                dialog.hideDialog()

            }, com.android.volley.Response.ErrorListener {
                Log.e("Error", it.toString())
                dialog.hideDialog()
            })
        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the volley post request to the request queue
        VolleySingleton.getInstance(mContext).addToRequestQueue(request)

    }



    private fun paytmNavigation() {
        run {
//                                    testPaytm();
//            livePaytm()

            newLivePaytm()
            Service.startPaymentTransaction(this, true, true,
                object : PaytmPaymentTransactionCallback {
                    override fun someUIErrorOccurred(inErrorMessage: String) {
                        overridePendingTransition(0, 0)
                        Log.d("LOG", "Payment Transaction is successful $inErrorMessage")
                    }

                    override fun onTransactionResponse(inResponse: Bundle) {
                        val json = JSONObject()
                        val keys = inResponse.keySet()
                        for (key in keys) {
                            try {
                                json.put(key, inResponse.get(key))
                                json.put(key, JSONObject.wrap(inResponse.get(key)))
                                Log.v("response......", json.toString() + "")
                                val innjsonJsonObject = JSONObject(json.toString())
                                //   objPaymentModel.setTransactionId(innjsonJsonObject.getString("TXNID"))
                                objPaymentModel.setORDERID(innjsonJsonObject.getString("ORDERID"))
                                objPaymentModel.setMID(innjsonJsonObject.getString("MID"))
                                // objPaymentModel.setTXNID(innjsonJsonObject.getString("TXNID"))
                                objPaymentModel.setCHECKSUMHASH(innjsonJsonObject.getString("CHECKSUMHASH"))

                                Log.v("key", key)
                            } catch (e: JSONException) {
                                //Handle exception here
                                Log.d("LOG", "Payment Transaction is successful $e")
                            }

                        }
                        //checkStatus(MID, ORDERID, CHECKSUMHASH)
                        checkStatus()

                    }

                    override fun networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                    }

                    override fun clientAuthenticationFailed(inErrorMessage: String) {
                        overridePendingTransition(0, 0)
                        Log.d("LOG", "Payment Transaction is successful $inErrorMessage")
                    }

                    override fun onErrorLoadingWebPage(
                        iniErrorCode: Int,
                        inErrorMessage: String, inFailingUrl: String
                    ) {
                        overridePendingTransition(0, 0)
                        Log.d("LOG", "Payment Transaction is successful $inErrorMessage")
                    }

                    // had to be added: NOTE
                    override fun onBackPressedCancelTransaction() {
                        overridePendingTransition(0, 0)
                        Toast.makeText(
                            mContext,
                            "Back pressed. Transaction cancelled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onTransactionCancel(inErrorMessage: String, inResponse: Bundle) {
                        Log.d("LOG", "Payment Transaction Failed $inErrorMessage")
                        overridePendingTransition(0, 0)
                        Toast.makeText(
                            baseContext,
                            "Payment Transaction Failed ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }
    }


    private fun checkStatus() {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))
        val call: Call<PaymentModel> = apI_Interface.checkStatus(
            objPaymentModel.getMID().toString(),
            objPaymentModel.getORDERID().toString(),
            objPaymentModel.getCHECKSUMHASH().toString()
        )
        call.enqueue(object : Callback<PaymentModel> {
            override fun onFailure(call: Call<PaymentModel>?, t: Throwable?) {
                //To change body of created functions use File | Settings | File Templates.
                dialog.hideDialog()
                Toast.makeText(mContext, "Check Status Failure", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<PaymentModel>?, response: Response<PaymentModel>?) {
                //To change body of created functions use File | Settings | File Templates.
                try {
                    dialog.hideDialog()
                    if (response?.isSuccessful!!) {

                        if (response.body().getStatus() == "TXN_SUCCESS" && objItrBase.processMode == CommonVal.EVerify.name) {
                            objItrBase.itrStatus = "payment success"
                            newItrBase.selectedProcessStatus = CommonVal.InProgress.name
                            val i = Intent(mContext, ERStatusActivity::class.java)
                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(i)
                        } else if (response.body().getStatus() == "TXN_SUCCESS" && objItrBase.processMode == CommonVal.RevisedReturn.name) {
                            objItrBase.itrStatus = "payment success"
                            newItrBase.selectedProcessStatus = CommonVal.InProgress.name
                            val i = Intent(mContext, ERStatusActivity::class.java)
                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(i)
                        }else if (response.body().getStatus() == "TXN_SUCCESS" && objItrBase.processMode == CommonVal.paymentlink.name) {
                            objItrBase.itrStatus = "payment success"
                            val i = Intent(mContext, CustomPaymentActivity::class.java)
                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(i)
                        }
                        else if (response.body().getStatus() == "TXN_SUCCESS") {
                            objItrBase.itrStatus = "payment success"
                            val i = Intent(mContext, ITROrderStatusActivity::class.java)
                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(i)

                        } else {
                            val i = Intent(mContext, FailPaymentActivity::class.java)
                            i.putExtra("transactionId", objPaymentModel.getTransactionId())
                            i.putExtra("orderId", objPaymentModel.getTransactionId())
                            i.putExtra(
                                "amount",
                                getSplittedAmount(binding.totalAmountTextView.text.toString())
                            )
                            i.putExtra("screenName", "OrderPayment")
                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(i)

                        }
                    }
                } catch (e: Exception) {
                    dialog.hideDialog()
                }
            }
        }
        )

    }

    private fun livePaytm() {

        Service = PaytmPGService.getProductionService()
        val paramMap = HashMap<String, String>()
        // these are mandatory parameters
        // paramMap.put("MID", "Corwhi41955223185539");
        paramMap["MID"] = "Corwhi93423578092957"
        paramMap["ORDER_ID"] = objPaymentModel.getTransactionId().toString()
        //paramMap["CUST_ID"] = appPreferences.getUserAssestmentYearId().toLowerCase()
        paramMap["CUST_ID"] = objItrBase.selectedUser_userAssessmentYearUserID.toString()
        paramMap["CHANNEL_ID"] = "WAP"
        // paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap["INDUSTRY_TYPE_ID"] = "Retail109"
        //   paramMap.put("WEBSITE", "APPSTAGING");
        paramMap["WEBSITE"] = "CorwhiWAP"

        //        paramMap.put("TXN_AMOUNT", totalAmountString);
        paramMap["TXN_AMOUNT"] = getSplittedAmount(binding.totalAmountTextView.text.toString())

        paramMap["CALLBACK_URL"] =
            "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=$objItrBase.UserAssestmentYearId"
        paramMap["EMAIL"] = binding.emailId.text.toString()
        paramMap["MOBILE_NO"] = binding.phoneNo.text.toString()
        paramMap["CHECKSUMHASH"] = objPaymentModel.getChecksum().toString()

//        paramMap["EMAIL"] = emailString
//        paramMap["MOBILE_NO"] = mobileString
//        paramMap["CHECKSUMHASH"] = checkSomeString
        val Order = PaytmOrder(paramMap)

        val Merchant = PaytmMerchant(
            "https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
            "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp"
        )

        Service.initialize(Order, null)
    }

    private fun newLivePaytm() {

        Service = PaytmPGService.getProductionService()  // For Live
//        Service = PaytmPGService.getStagingService()    // For testing
        val paramMap = HashMap<String, String>()
        // these are mandatory parameters
        // paramMap.put("MID", "Corwhi41955223185539");
        paramMap["MID"] = objPaymentModel.getParameters()?.getMID().toString()
        paramMap["ORDER_ID"] = objPaymentModel.getTransactionId().toString()
        //paramMap["CUST_ID"] = appPreferences.getUserAssestmentYearId().toLowerCase()
        paramMap["CUST_ID"] = objPaymentModel.getParameters()?.getCUSTID().toString()
        paramMap["CHANNEL_ID"] = objPaymentModel.getParameters()?.getCHANNELID().toString()
        // paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap["INDUSTRY_TYPE_ID"] =
            objPaymentModel.getParameters()?.getINDUSTRYTYPEID().toString()
        //   paramMap.put("WEBSITE", "APPSTAGING");
        paramMap["WEBSITE"] = objPaymentModel.getParameters()?.getWEBSITE().toString()

        //        paramMap.put("TXN_AMOUNT", totalAmountString);
        paramMap["TXN_AMOUNT"] = objPaymentModel.getParameters()?.getTXNAMOUNT().toString()

        paramMap["CALLBACK_URL"] = objPaymentModel.getParameters()?.getCALLBACKURL().toString()
        paramMap["EMAIL"] = objPaymentModel.getParameters()?.getEMAIL().toString()
        paramMap["MOBILE_NO"] = objPaymentModel.getParameters()?.getMOBILENO().toString()
        paramMap["CHECKSUMHASH"] = objPaymentModel.getChecksum().toString()

//        paramMap["EMAIL"] = emailString
//        paramMap["MOBILE_NO"] = mobileString
//        paramMap["CHECKSUMHASH"] = checkSomeString
        val Order = PaytmOrder(paramMap)

        val Merchant = PaytmMerchant(
            "https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
            "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp"
        )

        Service.initialize(Order, null)
    }


    private fun testPaytm()
    {
        Service = PaytmPGService.getStagingService("")
        val paramMap = HashMap<String, String>()

        paramMap["MID"]="Corwhi41955223185539"
        paramMap["ORDER_ID"]= objPaymentModel.getTransactionId().toString()
        paramMap["CUST_ID"]= objPaymentModel.getTransactionId().toString()

// these are mandatory parameters
        paramMap.put("MID", "Corwhi41955223185539");
        paramMap.put("ORDER_ID", objPaymentModel.getTransactionId().toString())
// paramMap.put("CUST_ID", appPreferences.getParentId());
        paramMap.put("CUST_ID", objPaymentModel.getParameters()?.getCUSTID().toString())
        paramMap.put("CHANNEL_ID", "WAP")
        paramMap.put("INDUSTRY_TYPE_ID", "Retail")
        paramMap.put("WEBSITE", "APPSTAGING")
//paramMap.put("TXN_AMOUNT", totalAmountString);
        paramMap.put("TXN_AMOUNT", objPaymentModel.getParameters()?.getTXNAMOUNT().toString())
        paramMap["CALLBACK_URL"] = objPaymentModel.getParameters()?.getCALLBACKURL().toString()
        paramMap.put("EMAIL", objPaymentModel.getParameters()?.getEMAIL().toString())
        paramMap.put("MOBILE_NO", objPaymentModel.getParameters()?.getMOBILENO().toString())
        paramMap.put("CHECKSUMHASH", objPaymentModel.getChecksum().toString())

        Log.e(" order 222 ","- "+paramMap)

        val Order = PaytmOrder(paramMap)

        val Merchant = PaytmMerchant(
            "https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
            "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp"
        );

        Service.initialize(Order, null);
    }


    private fun applyCouponCode() {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))
        val call: Call<CouponModel> = apI_Interface.postCouponCode(objCouponModel)
        call.enqueue(object : Callback<CouponModel> {
            override fun onFailure(call: Call<CouponModel>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(
                    mContext,
                    "Please Check Your Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<CouponModel>?, response: Response<CouponModel>?) {
                try {
                    dialog.hideDialog()
                    if (response?.isSuccessful!!) {
                        binding.couponEdittext.setText("")
                        if(response.body().getCoupon()!=null){
                            mIsCouponApply =
                                if (response.body().getCoupon()!!.isCouponApply != null) response.body().getCoupon()!!.isCouponApply!! else false
                            responseBody.getPayDetails()?.isCouponApply = mIsCouponApply
                            mCouponType =
                                if (response.body().getCoupon()?.couponType != null) response.body().getCoupon()?.couponType!! as String else ""
                            //       mClientName=(response.body().getCoupon()?.clientName)!!
                            mCouponCode =
                                response.body().getCoupon()?.couponCode?.takeUnless { it.isEmpty() } ?: ""
                            mDiscountType =
                                response.body().getCoupon()?.discountType?.takeUnless { it.isEmpty() } ?: ""
                            mDiscount = (response.body().getCoupon()?.discount.toString())!!
                            mCouponID = (response.body().getCoupon()?.id as Int)!!
                            getCalculation1()
                        }else{

                            Toast.makeText(mContext,response.body().getMessage()?.get(0), Toast.LENGTH_SHORT).show()
                        }

                    }
                }catch (e:Exception)
                {

                }


                //  Toast.makeText(mContext,"Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home) {
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        newItrBase.selectedProcessStatus = "No"
        val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            modelSelectedPackagesArrayList =
                intent.extras!!.getSerializable("packages") as List<ModelSelectedPackages>
            updateUI()
        }
    }


    private fun updateUI() {
        val tbl_layout_amountsummary = findViewById<TableLayout>(R.id.tbl_layout_amountsummary)
        tbl_layout_amountsummary.isStretchAllColumns = true
        tbl_layout_amountsummary.weightSum = 3f
        var packageName: TextView
        var packageAmount: TextView
        var rupees: TextView
        tbl_layout_amountsummary.removeAllViews()

        for (i in modelSelectedPackagesArrayList.indices) {
            val addons_row = TableRow(this)
            packageName = TextView(this)
            packageAmount = TextView(this)
            rupees = TextView(this)
            packageName.text = modelSelectedPackagesArrayList[i].getPackageName()
            packageAmount.text = "\u20B9 " + modelSelectedPackagesArrayList[i].getPackageAmount()
            packageAmount.gravity = Gravity.RIGHT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                packageName.setTextColor(mContext.getColor(R.color.lighter_black))
                packageAmount.setTextColor(mContext.getColor(R.color.lighter_black))
            } else {
                packageName.setTextColor(Color.parseColor("#505050"))
                packageAmount.setTextColor(Color.parseColor("#505050"))
            }

            addons_row.addView(packageName)
            addons_row.addView(packageAmount)
            tbl_layout_amountsummary.addView(addons_row)
        }
        getCalculation1()
    }


    private fun GetPackageId() {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))
//        val call: Call<NewItrBase> = apI_Interface.GetPackageId(objItrBase.selectedUser_userAssessmentYearUserID.toString(), CommonVal.SubmitDocument.name)
        val call: Call<NewItrBase> = apI_Interface.GetPackageId(
            objItrBase.selectedUser_userAssessmentYearUserID.toString(),
            objItrBase.processMode.toString()
        )
        call.enqueue(object : Callback<NewItrBase> {
            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                //To change body of created functions use File | Settings | File Templates.
                dialog.hideDialog()
                Toast.makeText(
                    mContext,
                    resources.getString(R.string.error_message),
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                //To change body of created functions use File | Settings | File Templates.
                try {
                    dialog.hideDialog()
                    if (response?.isSuccessful!!) {
                        binding.recyclerViewOrder.adapter = OrderPaymentAdapter(
                            mContext,
                            response.body().getPayDetails()?.packageAddOnsList
                        )

                        if(response.body().getPayDetails()?.appUtility!=null)
                        {
                            if(response.body().getPayDetails()!!.appUtility?.isActive!!)
                            {
                                var desc=response.body().getPayDetails()!!.appUtility?.description;
                                binding.warningLayout.visibility = View.VISIBLE
                                binding.warningDescription.text =
                                    Html.fromHtml("<b>Alert:</b> $desc")
                            }else{
                                binding.warningLayout.visibility = View.GONE
                                binding.warningDescription.text = ""
                            }
                        }

                        binding.originalPriceTextView.text =
                            "₹ " + response.body().getPayDetails()?.packageAmount.toString()
                        binding.taxesTextView.text =
                            "₹ " + response.body().getPayDetails()?.serviceTaxRate.toString()
                        // getCalculation1(response.body().getPayDetails()?.packageAddOnsList,response.body())
                        responseBody = response.body();
                        //getCalculation1(response.body())
                        getCalculation1()
                    }
                } catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.error_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        })
//

    }

    private fun getCalculation1() {
        var originalPrice = 0
        val discountPrice = 0
        val addOnPrice = 0
        var taxPrice = 0
        netAmount =
            Integer.parseInt(getSplittedAmount(binding.originalPriceTextView.text.toString())).toDouble()

        originalPrice = Integer.parseInt(getSplittedAmount(binding.originalPriceTextView.text.toString()))
        netAmount = originalPrice.toDouble()

        if (modelSelectedPackagesArrayList!!.isNotEmpty()) {
            for (i in modelSelectedPackagesArrayList.indices) {
                originalPrice += Integer.parseInt(modelSelectedPackagesArrayList[i].getPackageAmount())
                netAmount = originalPrice.toDouble()
            }
        }

        //**********Discount Calculations

        if (responseBody.getPayDetails()?.isCouponApply!! && mDiscountType == CommonVal.Amount.name) {
            taxPrice = Integer.parseInt(mDiscount)
            netAmount =
                (if (originalPrice - taxPrice > originalPrice) originalPrice else originalPrice - taxPrice).toDouble()
        } else if (responseBody.getPayDetails()?.isCouponApply!! && mDiscountType == CommonVal.Percent.name) {
            taxPrice = originalPrice * Integer.parseInt(mDiscount) / 100
            netAmount =
                (if (originalPrice - taxPrice > originalPrice) originalPrice else originalPrice - taxPrice).toDouble()
        }


        if (mIsCouponApply && originalPrice >= Math.max(originalPrice, taxPrice)) {
            setCouponSummaryVisibility()
        } else {
            setCouponVisibilityGone()
        }

        val gstAmount: Float =
            (netAmount * responseBody.getPayDetails()?.serviceTaxRate!! / 100).toFloat()
        netAmount += Math.round(gstAmount)
        setAmountSummaryInUI(netAmount.toInt(), taxPrice, taxPrice, gstAmount.toDouble())
    }


    private fun setAmountSummaryInUI(
        mTotalAmount: Int,
        mTax: Int,
        mDiscount: Int,
        mGstAmount: Double
    ) {
        binding.originalPriceTextView.text = "₹ " + responseBody.getPayDetails()?.packageAmount.toString()
        binding.taxesTextView.text = "₹ " + mGstAmount.toInt()
        binding.couponAppliedTextView.text = "₹ $mDiscount"
        binding.totalAmountTextView.text = "₹ $mTotalAmount"


    }

    private fun setCouponSummaryVisibility() {
        //   tbl_row_discountp.setVisibility(View.GONE)
        binding.tblRowCouponapply.visibility = View.VISIBLE

        //        Toast.makeText(mContext, mMessage, Toast.LENGTH_SHORT).show();//*******************************************
    }

    private fun setCouponVisibilityGone() {
        // tbl_row_discountp.setVisibility(View.GONE)
        binding.tblRowCouponapply.visibility = View.GONE

        mCouponType = ""
        mClientName = ""
        mCouponCode = ""
        mDiscountType = ""
        mDiscount = ""
        mIsCouponApply = false

    }

    private fun loadJSONFromAsset(): String {
        var json: String? = null
        try {
            val `is` = resources.openRawResource(R.raw.state)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer)
            val jsonArray = JSONArray(json)
            stateNameArrayList.add(0, "Select State")
            stateIdArrayList.add(0, "0")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                stateNameArrayList.add(jsonObject.getString("State"))
                stateIdArrayList.add(jsonObject.getString("Id"))
                val adapter = ArrayAdapter<String>(
                    applicationContext,
                    R.layout.spinner_item,
                    stateNameArrayList
                )
                binding.spinnerState.adapter = adapter
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }

        return json
    }

    private fun getSplittedAmount(splitAmount: String): String {
        val strings =
            splitAmount.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()//no i18n
        val splittedAmount: String
        splittedAmount = strings[1]
        return splittedAmount
    }


}
