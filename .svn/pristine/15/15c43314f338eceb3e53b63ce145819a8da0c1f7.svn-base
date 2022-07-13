package dell.com.allindiaitr.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.home.DashboardActivity
import dell.com.allindiaitr.home.ReferAndEarnActivity
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.submitDocument.ChatActivity

@Suppress("NAME_SHADOWING")
class AlertDialogueManager {

    private var appPreferences: AppPreferences? = null
    lateinit var mContext: Context
    lateinit var mMessage: String
    lateinit var inflater: LayoutInflater
    lateinit var builder : AlertDialog.Builder
    lateinit var dialog :Dialog
    var newItrBase = NewItrBase.instance


    constructor(mContext: Context,message: String){
        this.mContext=mContext
        this.mMessage=message
        inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
         builder = AlertDialog.Builder(mContext)
        progressBuilder(message)
    }

    constructor()
    fun progressBuilder(msg: String){
        val dialogView = inflater.inflate(R.layout.activity_custome_progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = msg
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

    }

    fun errorMessageDialogue(mContext: Context, message: String, title: String){
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("OK"){ _, _ ->  }
        val dialog: AlertDialog = alertDialog.create()
        dialog.show()
    }


    fun showAlertMessagePDFPath(mContext: Context, message: String, title: String){
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("OK"){ _, _ ->

            val intent4 = Intent(mContext, DashboardActivity::class.java)
            mContext.startActivity(intent4)
        }
        val dialog: AlertDialog = alertDialog.create()
        dialog.show()
    }


    fun hideDialog()
    {
        if(dialog.isShowing)
        dialog.dismiss()
    }
    fun updateVersionDialogue(mContext: Context, message: String){
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.version_update_pop_up, null)
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setView(view)
        alertDialog.setCancelable(false)
        val dialog: AlertDialog = alertDialog.create()
        dialog.setOnShowListener {
            val pop_up_desc_textView: TextView = view.findViewById(R.id.pop_up_desc_textView)
            pop_up_desc_textView.text = message
            val update_button: Button = view.findViewById(R.id.update_button)
            update_button.setOnClickListener {
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=dell.com.allindiaitr")
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                mContext.startActivity(goToMarket)
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun referAndEarnDialogue(mContext: Context){
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.refer_earn_pop_up, null)
        val alertDialog = AlertDialog.Builder(mContext)
        appPreferences= AppPreferences(mContext)
        alertDialog.setView(view)
        alertDialog.setCancelable(false)

        val dialog: AlertDialog = alertDialog.create()
        dialog.setOnShowListener {
            val cancel_imageView: ImageView = view.findViewById(R.id.cancel_imageView)
            cancel_imageView.setOnClickListener {
                dialog.dismiss()
                appPreferences?.appLaunch = false
            }
            val refer_button: Button = view.findViewById(R.id.refer_button)
            refer_button.setOnClickListener {
                dialog.dismiss()

                appPreferences?.appLaunch = false
                newItrBase.referNowVisible = false
                newItrBase.orderMode = "Orders"
                val intent = Intent(mContext, ReferAndEarnActivity::class.java)
                (mContext as Activity).finish()
                mContext.startActivityForResult(intent, 0)
                mContext.overridePendingTransition(0, 0)
                mContext.startActivity(intent)
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun showViewAlertDialog(context: Context, message: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rate_us)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#cc66b2ff")))
        val ratingBar = dialog.findViewById<View>(R.id.ratingBar) as RatingBar
        val descriptionText = dialog.findViewById<View>(R.id.descriptionText) as TextView
        descriptionText.text = message
        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar: RatingBar, _, _ ->
            val ratedValue = ratingBar.rating.toString()
            if (ratedValue == "1.0" || ratedValue == "2.0" || ratedValue == "3.0") {
                val intent = Intent(Intent.ACTION_VIEW)
                val subject = "Feedback For All India ITR App"
                val body = ""
                val data = Uri.parse("mailto:support@allindiaitr.com?subject=$subject&body=$body")
                intent.data = data
                context.startActivity(intent)
            } else {
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=dell.com.allindiaitr")
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(goToMarket)
            }
        }
        dialog.show()
    }

    fun targetView(chatActivity: ChatActivity, camraImage: ImageView, s: String, s1: String) {
        TapTargetView.showFor(
            chatActivity, TapTarget.forView(camraImage, s, s1)
                .tintTarget(false)
                .outerCircleColor(R.color.target_blue)
                .outerCircleAlpha(0.99f)
                .targetRadius(30)
                .cancelable(true)
                .dimColor(R.color.black)
                .transparentTarget(true)
                .targetCircleColor(R.color.black)
        )
    }

    fun showRateUsView(context: Context, ack: String) {
        val dialogView = View.inflate(context, R.layout.rating_popup, null)
        val ratedValue = arrayOfNulls<String>(1)
        ratedValue[0] = "0.0"
        val dialog = android.app.AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        val textView = dialogView.findViewById<TextView>(R.id.tv_msg)
        textView.text = ack
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, _, _ ->
            ratedValue[0] = ratingBar.rating.toString()
        }
        val button_rate = dialogView.findViewById<Button>(R.id.button_rate)
        button_rate.setOnClickListener(View.OnClickListener {
            if (ratedValue[0] == "0.0") {

            } else if (ratedValue[0] == "1.0" || ratedValue[0] == "2.0" || ratedValue[0] == "3.0") {
                dialog.dismiss()
                newItrBase.rateUsVisible = false
                val intent = Intent(Intent.ACTION_VIEW)
                val subject = "Feedback For All India ITR App"
                val body = ""
                val data = Uri.parse("mailto:support@allindiaitr.com?subject=$subject&body=$body")
                intent.data = data
                context.startActivity(intent)
            } else {
                dialog.dismiss()
                newItrBase.rateUsVisible = false
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=dell.com.allindiaitr")
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(goToMarket)
            }
        })
        val cancel = dialogView.findViewById<TextView>(R.id.no_thanks)
        cancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

}


