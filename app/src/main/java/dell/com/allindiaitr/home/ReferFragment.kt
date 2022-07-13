package dell.com.allindiaitr.home

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.FragmentReferBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.login.LogInActivity
import dell.com.allindiaitr.models.ReferEarnModel
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.ConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReferFragment : Fragment(), View.OnClickListener {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var appPreferences: AppPreferences
    private lateinit var clipboard: ClipboardManager
    lateinit var clip: ClipData
    lateinit var msg: String
    lateinit var couponCodeString : String
    lateinit var binding: FragmentReferBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_refer, container, false)
        binding = FragmentReferBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext = requireContext()
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(mContext)

      //  val text_invite = "<font color='#00B894'>Refer All India<br> ITR</font> to your Friends"
        val text_invite =  getString(R.string.ref_earn_refer_friend)
//        val text_invite =  getString(R.string.ref_earn_refer_friend)
//        var ss:SpannableString=SpannableString(text_invite)
//        ss.setSpan(ForegroundColorSpan(Color.GREEN),0, 5, 0)
        binding.tvInvite.text = HtmlCompat.fromHtml(text_invite, HtmlCompat.FROM_HTML_MODE_LEGACY)

       // val text_notify = "Get <font color='#00B894'>Notified</font> once<br> your friends file ITR"
        val text_notify =getString(R.string.ref_earn_get_notify)
        binding.tvNotify.text = HtmlCompat.fromHtml(text_notify, HtmlCompat.FROM_HTML_MODE_LEGACY)

//        val text_reward = "You and your<br> Friends Get <font color='#00B894'>Cashback</font>"
        val text_reward = getString(R.string.ref_earn_get_cashback)
        binding.tvReward.text = HtmlCompat.fromHtml(text_reward, HtmlCompat.FROM_HTML_MODE_LEGACY)

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getReferCode()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()

        binding.coupontext.setOnLongClickListener {
            clip = ClipData.newPlainText("text", binding.coupontext.text.toString().trim { it <= ' ' })
//            clipboard.primaryClip = clip
            Toast.makeText(mContext, "copy successfully", Toast.LENGTH_SHORT).show()
            true
        }

        binding.facebookLayout.setOnClickListener(this)
        binding.whatsappLinearLayout.setOnClickListener(this)
        binding.msgLinearLayout.setOnClickListener(this)
        binding.emailLinearLayout.setOnClickListener(this)
        binding.shareMoreLayout.setOnClickListener(this)
    }

    private fun getReferCode(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getReferCode("Bearer " + appPreferences.accessTokenId, appPreferences.parentId.toString())
        call.enqueue(object : Callback<ReferEarnModel> {

            override fun onResponse(call: Call<ReferEarnModel>?, response: Response<ReferEarnModel>?) {
                try {
                    when {
                        response!!.isSuccessful -> {
                            dialog.hideDialog()
                            couponCodeString = response.body().couponCode?.takeUnless {it.isEmpty()} ?: ""
                            binding.coupontext.setText(couponCodeString)
                            msg =
                                "Make Tax Filing hassle-free with All India ITR. Use my unique code " + couponCodeString + " to file your ITR and earn Rs. 100! For better experience Download our App. " + "\n\n" +
                                        "Android Link - https://play.google.com/store/apps/details?id=dell.com.allindiaitr&hl=en" + "\n\n" +
                                        "Iphone Link - https://itunes.apple.com/us/app/all-india-itr/id1195821566"
                        }
                        response.code() == 401 -> {
                            dialog.hideDialog()
                            appPreferences.ClearPreferences()
                            val intent = Intent(mContext, LogInActivity::class.java)
                            (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                            (mContext as Activity).finish()
                            startActivity(intent)
                        }
                        else -> dialog.hideDialog()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReferEarnModel>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.facebookLayout -> {
                    val facebookIntent = Intent(Intent.ACTION_SEND)
                    facebookIntent.type = "text/plain"
                    facebookIntent.setPackage("com.facebook.orca")
                    facebookIntent.putExtra(Intent.EXTRA_TEXT, msg)
                    try {
                        startActivity(facebookIntent)
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(mContext, "Please Install Facebook Messenger", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.whatsappLinearLayout -> {
                    val whatsappIntent = Intent(Intent.ACTION_SEND)
                    whatsappIntent.type = "text/plain"
                    whatsappIntent.setPackage("com.whatsapp")
                    whatsappIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Checkout This India’s number 1 Tax App for Filing Income Tax Return By All India ITR.I have used it and its awesome.Use my unique code  " + couponCodeString + " to get Rs 100 Instant Discount" +
                                "Downloaded Link - https://www.allindiaitr.com"
                    )
                    try {
                        startActivity(whatsappIntent)
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(mContext, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.msgLinearLayout -> {
                    val m =
                        "Checkout This India’s number 1 Tax App for Filing Income Tax Return By All India ITR.I have used it and its awesome.Use my unique code  " + couponCodeString + "  to get Rs 100 Instant Discount" + "\n\n" +
                                "App Link - https://www.allindiaitr.com"
                    val sendIntent = Intent(Intent.ACTION_VIEW)
                    sendIntent.data = Uri.parse("sms:")
                    sendIntent.putExtra("sms_body", m)
                    startActivity(sendIntent)
                }
                R.id.emailLinearLayout -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + " "))
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Refer And Earn")
                        intent.putExtra(Intent.EXTRA_TEXT, msg)
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
                R.id.shareMoreLayout -> {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "All India ITR - e-filing was never so easy")
                    var sAux = "I found it useful, hope you’ll do too.\n\n"
                    sAux = sAux + "https://play.google.com/store/apps/details?id=dell.com.allindiaitr"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, sAux)
                    mContext.startActivity(Intent.createChooser(shareIntent, "Share Via"))
                }
                R.id.copyButton -> {
                    clip = ClipData.newPlainText("text", binding.coupontext.text.toString().trim { it <= ' ' })
//                    clipboard.primaryClip = clip
                    Toast.makeText(mContext, "copy successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}