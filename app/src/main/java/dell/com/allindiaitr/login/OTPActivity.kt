package dell.com.allindiaitr.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import dell.com.allindiaitr.BroadcastReceiver.SmsBroadcastReceiver
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityOtpBinding
import dell.com.allindiaitr.home.DashboardActivity
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.interfaces.OtpReceivedInterface
import dell.com.allindiaitr.models.LogInwithOTPModel
import dell.com.allindiaitr.models.Token
import dell.com.allindiaitr.utils.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

class OTPActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
     GoogleApiClient.OnConnectionFailedListener  {

//    override fun onSuccess(intent: Intent?) {
//
//    }
//
    override fun onConnected(p0: Bundle?) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(p0: Int) {
      //To change body of created functions use File | Settings | File Templates.
    }
//
//    override fun onOtpReceived(otp: String) {
//         //To change body of created functions use File | Settings | File Templates.
////        Toast.makeText(this, "Otp Received $otp", Toast.LENGTH_LONG).show()
//        binding.otpEditText.setText(otp)
//
//        otpLogin()
////        if (smsReceiver != null) {
////            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver)
////        }
//
//    }

//    override fun onOtpTimeout() {
//       //To change body of created functions use File | Settings | File Templates.
////        Toast.makeText(this, "Time out, please resend", Toast.LENGTH_LONG).show()
//    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    lateinit var mContext : Context
    lateinit var apI_Interface : API_Interface
    var objLoginWithOTPModel = LogInwithOTPModel.instance
    var tokenModel = Token.instance
    private var appPreferences: AppPreferences? = null
    lateinit var mGoogleApiClient: GoogleApiClient
     lateinit var mSmsBroadcastReceiver: SmsBroadcastReceiver
     lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(this)
        binding.toolbar.toolbarTitle.text = "Enter OTP"

        // init broadcast receiver
        mSmsBroadcastReceiver = SmsBroadcastReceiver()
//        mGoogleApiClient = GoogleApiClient.Builder(this)
//            .addConnectionCallbacks(this)
//            .enableAutoManage(this, this)
//            .addApi(Auth.CREDENTIALS_API)
//            .build()

       // mSmsBroadcastReceiver.setOnOtpListeners(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
      //  applicationContext.registerReceiver(mSmsBroadcastReceiver, intentFilter)

      //  scroll_view.fullScroll(View.FOCUS_DOWN)
        //scroll_view.scrollTo(0, scroll_view.getBottom())
//        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener OnCompleteListener@{ task ->
//
//            if(!task.isSuccessful) {
//                // Log.w(TAG, "getInstanceId failed", task.exception)
//                return@OnCompleteListener
//            }
//            // Get new Instance ID token
//            val token = task.result?.token
//
//            // Log and toast
//            val msg =  token
//            //Log.d(TAG, msg)
//            appPreferences!!.deviceTokenId=token
//
//        }


        binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        binding.resendOtpTextView.setOnClickListener {
            if (ConnectionDetector().isConnectingToInternet(mContext)){
                binding.otpEditText.text.clear()
                resendOTP()
                countDownTimer(40000, 1000).start()
            }
            else {
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.doneButton.setOnClickListener {
            if (Validation().isOTPValid(binding.otpEditText.text.toString().trim(), binding.otpEditText)){
                if (ConnectionDetector().isConnectingToInternet(mContext)){
                    objLoginWithOTPModel.Code = binding.otpEditText.text.toString().trim()
                    verifyPhoneNo()
                }
                else {
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.mobileNumberTextView.text = "+91 " + (objLoginWithOTPModel.phoneNumber?.toString() ?: objLoginWithOTPModel.phoneNumber)

        countDownTimer(40000, 1000).start()
    }


    private fun registerToSmsBroadcastReceiver() {

        mSmsBroadcastReceiver=SmsBroadcastReceiver().also {
            it.otpReceiveInterface=object :OtpReceivedInterface{
                override fun onOtpReceived(otp: String) {

                }

                override fun onOtpTimeout() {

                }

                override fun onSuccess(intent: Intent?) {

                    intent?.let { context -> startActivityForResult(context, REQ_USER_CONSENT) }
                }

            }
        }

//
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(mSmsBroadcastReceiver, intentFilter)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_USER_CONSENT -> {
                try {
                    if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                        //That gives all message to us. We need to get the code from inside with regex
                        val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
//                    val otpMessage = message.replace("<#> Your One Time Password for All India ITR verification is: ", "")
                       // val otpMessage = message?.replace("is your one time password for All India ITR verification.", "")
                        val otpMessage = message?.replace("Hello, OTP for phone verification is", "")
                        val otp =
                            otpMessage?.split("\\.".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                ?.get(0)

                        val newOTP = extractDigits(message)
                        binding.otpEditText.setText(newOTP?.trim())
                        objLoginWithOTPModel.Code = binding.otpEditText.text.toString().trim()
                        verifyPhoneNo()

                    }
                }catch (e:Exception){

                }

            }
        }
    }

    fun extractDigits(`in`: String?): String? {
        val p: Pattern = Pattern.compile("(\\d{6})")
        val m: Matcher = p.matcher(`in`)
        return if (m.find()) {
            m.group(0)
        } else ""
    }


    private fun otpLogin()
    {

        if (Validation().isOTPValid(binding.otpEditText.text.toString().trim(), binding.otpEditText)){
            if (ConnectionDetector().isConnectingToInternet(mContext)){
                objLoginWithOTPModel.Code = binding.otpEditText.text.toString().trim()
                verifyPhoneNo()
            }
            else {
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun countDownTimer(millisInFuture : Long, countDownInterval : Long) : CountDownTimer {
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toString()
                if (seconds.length < 2){
                    binding.resendTextView.text = "Resend in 00:0${seconds.toString()}"
                }
                else {
                    binding.resendTextView.text = "Resend in 00:${seconds.toString()}"
                }
                binding.resendOtpTextView.visibility = View.GONE
            }

            override fun onFinish() {
                binding.resendTextView.text = ""
                binding.resendOtpTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun verifyPhoneNo(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
        objLoginWithOTPModel.deviceId=appPreferences?.deviceTokenId
        val call = apI_Interface.postVerifyOTP(objLoginWithOTPModel)
        call.enqueue(object : Callback<LogInwithOTPModel>{
            override fun onResponse(call: Call<LogInwithOTPModel>?, response: Response<LogInwithOTPModel>?) {
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        tokenModel = response.body().token!!
                        appPreferences!!.parentId = tokenModel.id?.takeUnless {it.isEmpty()} ?: ""
                        appPreferences!!.childId = tokenModel.id?.takeUnless {it.isEmpty()} ?: ""
                        appPreferences!!.userName = tokenModel.userName?.takeUnless {it.isEmpty()} ?: ""
                        appPreferences!!.emailAddress = tokenModel.email?.takeUnless {it.isEmpty()} ?: ""
                        appPreferences!!.accessTokenId = tokenModel.accessToken?.takeUnless {it.isEmpty()} ?: ""
                        appPreferences!!.mobileNumber = tokenModel.phoneNumber?.takeUnless {it.isEmpty()} ?: ""
                        appPreferences!!.userFirstName = tokenModel.firstName?.takeUnless {it.isEmpty()} ?: ""
                        appPreferences!!.userLastName = tokenModel.lastName?.takeUnless {it.isEmpty()} ?: ""
                        val intent = Intent(mContext, DashboardActivity::class.java)
                        finish()
                        Toast.makeText(mContext, "Login Successfully", Toast.LENGTH_SHORT).show()
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    else {
                        dialog.hideDialog()
                        var jsonObject = JSONObject(response.errorBody().string())
                        var jsonArray = JSONArray(jsonObject.getString("Message"))
                        AlertDialogueManager().errorMessageDialogue(mContext, jsonArray[0].toString(), "Message")
                    }
                }
                catch (e: Exception){
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LogInwithOTPModel>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun resendOTP(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.resendOTP(objLoginWithOTPModel)
        call.enqueue(object : Callback<LogInwithOTPModel> {
            override fun onResponse(call: Call<LogInwithOTPModel>?, response: Response<LogInwithOTPModel>?) {
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        Toast.makeText(mContext, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                        appPreferences!!.parentId = response.body().Id!!
                        appPreferences!!.childId = response.body().Id!!
                        appPreferences!!.createdTime = response.body().CreatedTime!!
                        objLoginWithOTPModel.CreatedTime = response.body().CreatedTime?.takeUnless {it.isEmpty()} ?: ""
                    }
                    else {
                        dialog.hideDialog()
                        var jsonObject = JSONObject(response.errorBody().string())
                        var jsonArray = JSONArray(jsonObject.getString("Message"))
                        AlertDialogueManager().errorMessageDialogue(mContext, jsonArray[0].toString(), "Message")
                    }
                }
                catch (e: Exception){
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LogInwithOTPModel>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }




    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        registerToSmsBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
//        LocalBroadcastManager.getInstance(this)
//            .unregisterReceiver(broadCastReceiver)

        if(mSmsBroadcastReceiver!=null)
        {
            unregisterReceiver(mSmsBroadcastReceiver)

        }

    }

    companion object {
        const val TAG = "SMS_USER_CONSENT"

        const val REQ_USER_CONSENT = 100
    }

}
