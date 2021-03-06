package dell.com.allindiaitr.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import dell.com.allindiaitr.BroadcastReceiver.SmsBroadcastReceiver
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.LogInwithOTPModel
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityLogInBinding
import dell.com.allindiaitr.interfaces.OtpReceivedInterface
import dell.com.allindiaitr.models.LogInModel
import dell.com.allindiaitr.utils.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogInActivity : AppCompatActivity() , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    override fun onConnected(p0: Bundle?) {
            //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(p0: Int) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        //To change body of created functions use File | Settings | File Templates.
    }


    var objLoginWithOTPModel = LogInwithOTPModel.instance
    var objLoginModel = LogInModel.instance
    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var mobile_number : String

     lateinit var mGoogleApiClient: GoogleApiClient
    private val RESOLVE_HINT = 2
     lateinit var mSmsBroadcastReceiver: SmsBroadcastReceiver
    lateinit var appPreferences:AppPreferences
    lateinit var binding:ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences=AppPreferences(mContext)
        binding.toolbar.toolbarTitle.text = "Log In"

        // init broadcast receiver
//        mSmsBroadcastReceiver = SmsBroadcastReceiver()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .enableAutoManage(this, this)
            .addApi(Auth.CREDENTIALS_API)
            .build()

        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
//        applicationContext.registerReceiver(mSmsBroadcastReceiver, intentFilter)

        getHintPhoneNumber()
        binding.sendOtpButton.setOnClickListener {
            mobile_number = binding.mobileEditText.text.toString().trim()
            if (Validation().isMobileValid(mobile_number, binding.mobileEditText)){
                objLoginWithOTPModel.phoneNumber = mobile_number
               // objLoginWithOTPModel.deviceId = "dshfdhbfdhbfdbfdjdfkjfmkdfngdk1655484815bhjfbhjfdbhjdfhf454857576576576776557655767"
                objLoginWithOTPModel.deviceId = appPreferences.deviceTokenId
                objLoginWithOTPModel.deviceType = "Android"
                if (ConnectionDetector().isConnectingToInternet(mContext)){
                    startSMSListener()
//                    sendOTP()
                }
                else {
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun getHintPhoneNumber() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest)
        try {
            startIntentSenderForResult(mIntent.intentSender, RESOLVE_HINT, null, 0, 0, 0)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }

    }


    // For Auto read functionality sms listner
//    fun startSMSListener() {
//        val mClient = SmsRetriever.getClient(this)
//        val mTask = mClient.startSmsRetriever()
//        mTask.addOnSuccessListener {
//            try {
//                if (ConnectionDetector().isConnectingToInternet(mContext)){
//                    sendOTP()
//                }
//                else {
//                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//
//            }
//        }
//        mTask.addOnFailureListener {
//            //                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
//        }
//    }


//   For one tap listener

    fun startSMSListener() {
        SmsRetriever.getClient(this).also {
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                    Log.d("SMS", "LISTENING_SUCCESS")
                    try {
                        if(ConnectionDetector().isConnectingToInternet(mContext))
                        {
                            sendOTP()
                        }else{
                            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                        }

                    }catch (e:java.lang.Exception){

                    }
                }
                .addOnFailureListener {
                    Log.d("SMS", "LISTENING_FAILURE")
                }
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RESOLVE_HINT)
        {
            if (resultCode==Activity.RESULT_OK)
            {
                val credential=data!!.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                binding.mobileEditText?.setText(credential?.id?.replace("+91", ""))
                binding.mobileEditText?.setSelection(binding.mobileEditText.text.toString().length)
            }
        }


    }

    private fun sendOTP(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.postLOGINOTP(objLoginWithOTPModel)
        call.enqueue(object : Callback<LogInwithOTPModel> {
            override fun onResponse(call: Call<LogInwithOTPModel>?, response: Response<LogInwithOTPModel>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        objLoginModel = response.body().loginModel!!
                        objLoginWithOTPModel.deviceType = objLoginModel.deviceType?.takeUnless {it.isEmpty()} ?: ""
                        objLoginWithOTPModel.deviceId = objLoginModel.deviceId?.takeUnless {it.isEmpty()} ?: ""
                        objLoginWithOTPModel.email = objLoginModel.email?.takeUnless {it.isEmpty()} ?: ""
                        objLoginWithOTPModel.flagValue = objLoginModel.flagValue
                        objLoginWithOTPModel.message = objLoginModel.message?.takeUnless {it.isEmpty()} ?: ""
                        objLoginWithOTPModel.password = objLoginModel.password?.toString()?.takeUnless {it.isEmpty()} ?: ""
                        objLoginWithOTPModel.phoneNumber = objLoginModel.phoneNumber?.takeUnless {it.isEmpty()} ?: ""
                        objLoginWithOTPModel.CreatedTime = response.body().CreatedTime?.takeUnless {it.isEmpty()} ?: ""
                        objLoginWithOTPModel.Id = response.body().Id?.takeUnless {it.isEmpty()} ?: ""
                        val intent = Intent(mContext, OTPActivity::class.java)
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        finish()
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onPause() {
        super.onPause()
       // Toast.makeText(mContext,"From onPause()",Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        //Toast.makeText(mContext,"From onStop()",Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

//        LocalBroadcastManager.getInstance(this)
//            .unregisterReceiver(broadCastReceiver)

//        if(mSmsBroadcastReceiver!=null)
//        {
////            applicationContext.registerReceiver(mSmsBroadcastReceiver)
//            applicationContext.unregisterReceiver(mSmsBroadcastReceiver)
////            Toast.makeText(mContext,"Un-Register Success",Toast.LENGTH_LONG).show()
//        }

    }
}
