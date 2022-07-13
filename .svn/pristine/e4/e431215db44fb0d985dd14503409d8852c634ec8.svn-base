package dell.com.allindiaitr.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dell.com.allindiaitr.interfaces.OtpReceivedInterface

//import com.wave.smsverification.interfaces.OtpReceivedInterface;

/**
 * Created on : May 21, 2019
 * Author     : AndroidWave
 */
class SmsBroadcastReceiver : BroadcastReceiver() {
    internal var otpReceiveInterface: OtpReceivedInterface? = null

    fun setOnOtpListeners(otpReceiveInterface: OtpReceivedInterface) {
        this.otpReceiveInterface = otpReceiveInterface
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {

            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT).also {
//                        otpReceiveInterface.onSuccess(it)
//                        otpReceiveInterface!!.onOtpReceived("1234")
                        otpReceiveInterface!!.onSuccess(it)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
//                    otpReceiveInterface.onFailure()
                    if (otpReceiveInterface != null) {
                        otpReceiveInterface!!.onOtpTimeout()
                    }
                }
            }
        }
    }


    // For Auto read sms

//    override fun onReceive(context: Context, intent: Intent) {
//        Log.d(TAG, "onReceive: ")
//        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
//            val extras = intent.extras
//            val mStatus = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status
//
//            when (mStatus.statusCode) {
//                CommonStatusCodes.SUCCESS -> {
//                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
//                    Log.d(TAG, "onReceive: failure $message")
//                    if (otpReceiveInterface != null) {
////                        val otpMessage = message.replace("<#> DO NOT SHARE: ", "")
////                        val otpMessage = message.replace("<#> Your one time password for All India ITR verification: ", "")
//                        Toast.makeText(context,"Reciving OTP :",Toast.LENGTH_LONG).show()
//                        val otpMessage = message.replace("<#> Your One Time Password for All India ITR verification is: ", "")
//                        val otp = otpMessage.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
//                        otpReceiveInterface!!.onOtpReceived(otp.trim())
//                    }
//                }
//                CommonStatusCodes.TIMEOUT -> {
//                    // Waiting for SMS timed out (5 minutes)
//                    Log.d(TAG, "onReceive: failure")
//                    if (otpReceiveInterface != null) {
//                        otpReceiveInterface!!.onOtpTimeout()
//                    }
//                }
//            }
//        }
//    }

    companion object {
        private val TAG = "SmsBroadcastReceiver"
    }
}