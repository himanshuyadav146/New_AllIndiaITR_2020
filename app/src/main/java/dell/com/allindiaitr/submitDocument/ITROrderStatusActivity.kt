package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityItrorderStatusBinding
import dell.com.allindiaitr.home.AllOrdersActivity
import dell.com.allindiaitr.home.DashboardActivity
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.*
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ITROrderStatusActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    lateinit var apI_Interface : API_Interface
    var prevExpandPosition: Int = -1
    lateinit var efilingStatusesList : List<NewItrBase>
    var efilingStatusFieldList: List<NewItrBase> = emptyList()
    var newItrBase = NewItrBase.instance
    internal var phn_number: String? = null
    internal var email_id:String? = null
    lateinit var parentParams: LinearLayout.LayoutParams
    lateinit var expParentParams:LinearLayout.LayoutParams
    lateinit var childParams: FrameLayout.LayoutParams
    lateinit var binding:ActivityItrorderStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityItrorderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        appPreferences = AppPreferences(this)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

//        setSupportActionBar(binding.toolbar)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbarTitle.text =getString(R.string.title_orderstatus)
        if (ConnectionDetector().isConnectingToInternet(mContext))
            getStatus()
        else
            Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()

        parentParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        parentParams.leftMargin = 7
        childParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        childParams.bottomMargin = 50
        childParams.rightMargin = 7
        expParentParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        if (newItrBase.itrStatus == "payment success") {
            binding.rlInprogress.visibility = RelativeLayout.VISIBLE
            binding.rlCompleted.visibility = RelativeLayout.GONE
            binding.imgItrStatus.setImageResource(R.drawable.ic_itr_inprogress)
            binding.tvStatus.text = "ITR Inprogress"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                binding.tvStatus.setTextColor(mContext.getColor(R.color.red))
            else
                binding.tvStatus.setTextColor(Color.parseColor("#FF6463"))
            binding.rlChat.visibility = View.VISIBLE
            binding.btnEVerify.visibility = View.GONE
            binding.note.visibility = View.GONE

        }
        else if (newItrBase.itrStatus == "ITR Inprogress")
        {
            binding.rlInprogress.visibility = RelativeLayout.VISIBLE
            binding.rlCompleted.visibility = RelativeLayout.GONE
            binding.imgItrStatus.setImageResource(R.drawable.ic_itr_inprogress)
            binding.tvStatus.text = "ITR Inprogress"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                binding.tvStatus.setTextColor(mContext.getColor(R.color.red))
            else
                binding.tvStatus.setTextColor(Color.parseColor("#FF6463"))
            binding.rlChat.visibility = View.VISIBLE
            binding.btnEVerify.visibility = View.GONE
            binding.note.visibility = View.GONE
        }
        else {
            if (newItrBase.eVerifyPaymentDone == false) {
                binding.rlInprogress.visibility = RelativeLayout.GONE
                binding.rlCompleted.visibility = RelativeLayout.VISIBLE
                binding.tvAcknowledgementno.text = newItrBase.acknowledgementNo.toString()
                binding.imgItrStatus.setImageResource(R.drawable.ic_e_filed)
                binding.tvStatus.text = "E-Filed"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.tvStatus.setTextColor(mContext.getColor(R.color.colorPrimary))
                else
                    binding.tvStatus.setTextColor(Color.parseColor("#00B894"))
                binding.rlChat.visibility = View.GONE
                binding.btnEVerify.visibility = View.VISIBLE
                binding.note.visibility = View.VISIBLE
            }
            
        }

        binding.llPaymentSuccess.setOnClickListener(this)
        binding.llExpertAssign.setOnClickListener(this)
        binding.llReviewInfo.setOnClickListener(this)
        binding.llEFiled.setOnClickListener(this)
        binding.llPaymentSuccess.isClickable = false
        binding.llExpertAssign.isClickable = false
        binding.llReviewInfo.isClickable = false
        binding.llEFiled.isClickable = false
        binding.btnChat.setOnClickListener(this)
        binding.btnCall.setOnClickListener(this)
        binding.btnEmail.setOnClickListener(this)
        binding.btnEVerify.setOnClickListener(this)
    }

    // This reciever automatically refresh screen on notification during working
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ConnectionDetector().isConnectingToInternet(mContext))
                getStatus()
            else
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mContext.registerReceiver(mMessageReceiver, IntentFilter("noti_refresh"))
    }

    //    private fun initializer(){
//
//        if (expandable_list_view != null) {
//
//            expandable_list_view!!.setOnGroupExpandListener { groupPosition ->
//                if (prevExpandPosition != -1
//                    && groupPosition != prevExpandPosition) {
//                    expandable_list_view.collapseGroup(prevExpandPosition)
//                }
//                prevExpandPosition = groupPosition
//            }
//
//            expandable_list_view!!.setOnGroupCollapseListener {
//            }
//
//            expandable_list_view!!.setOnChildClickListener { _, _, _, _, _ ->
//                false
//            }
//        }
//    }


    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }


    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.ll_payment_success -> {
                isVisiblePaymentSuccessLayout()
                binding.imgItrStatus.setImageResource(R.drawable.ic_itr_inprogress)
                binding.tvStatus.text = "ITR Inprogress"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.tvStatus.setTextColor(mContext.getColor(R.color.red))
                else
                    binding.tvStatus.setTextColor(Color.parseColor("#FF6463"))
            }
            R.id.ll_expert_assign -> {
                isVisibleExpertAssignLayout()
                binding.imgItrStatus.setImageResource(R.drawable.ic_itr_inprogress)
                binding.tvStatus.text = "ITR Inprogress"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.tvStatus.setTextColor(mContext.getColor(R.color.red))
                else
                    binding.tvStatus.setTextColor(Color.parseColor("#FF6463"))
            }
            R.id.ll_review_info -> {
                isVisibleReviewInfoLayout()
                binding.imgItrStatus.setImageResource(R.drawable.ic_itr_inprogress)
                binding.tvStatus.text = "ITR Inprogress"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.tvStatus.setTextColor(mContext.getColor(R.color.red))
                else
                    binding.tvStatus.setTextColor(Color.parseColor("#FF6463"))

            }
            R.id.ll_e_filed -> {
                isVisibleEFiledLayout()
                binding.imgItrStatus.setImageResource(R.drawable.ic_e_filed)
                binding.tvStatus.text = "E-Filed"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.tvStatus.setTextColor(mContext.getColor(R.color.colorPrimary))
                else
                    binding.tvStatus.setTextColor(Color.parseColor("#00B894"))
            }
            R.id.btn_chat -> {
                val intent = Intent(applicationContext, ChatActivity::class.java)
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent)
            }
            R.id.rl_chat-> {
                val intent = Intent(applicationContext, ChatActivity::class.java)
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent)
            }
            R.id.btn_e_verify -> {
                val intent = Intent(applicationContext, EVerifyActivity::class.java)

                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent)
            }
            R.id.btn_call -> {
                onCall()
            }
            R.id.btn_email -> {
                try {
                    if (email_id == null || email_id!!.isEmpty()) {
                        binding.btnEmail.isEnabled = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            binding.btnEmail.setCardBackgroundColor(mContext.getColorStateList(R.color.lightest_grey_forstatus))
                        else
                            binding.btnEmail.setCardBackgroundColor(Color.parseColor("#F8F8F8"))
                    } else {
                        binding.btnEmail.isEnabled = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            binding.btnEmail.setCardBackgroundColor(mContext.getColorStateList(R.color.colorPrimary))
                        else
                            binding.btnEmail.setCardBackgroundColor(Color.parseColor("#00B894"))
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$email_id"))
                        startActivity(intent)
                    }
                } catch (e: ActivityNotFoundException) {
                    //TODO smth
                }

            }
        }
    }

    private fun onCall(){
        if (phn_number == null || phn_number!!.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                binding.btnCall.setCardBackgroundColor(mContext.getColorStateList(R.color.lightest_grey_forstatus))
            else
                binding.btnCall.setCardBackgroundColor(Color.parseColor("#F8F8F8"))
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                binding.btnCall.setCardBackgroundColor(mContext.getColorStateList(R.color.dark_yellow))
            else
                binding.btnCall.setCardBackgroundColor(Color.parseColor("#FFAB2A"))
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$phn_number")
            startActivity(callIntent)
        }
    }

    private fun getStatus(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getITRStatus(newItrBase.selectedUser_userAssessmentYearUserID.toString())
        call.enqueue(object : Callback<NewItrBase>{
            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        binding.usernameTextView.text = "${response.body().firstName} ${response.body().lastName}"

                        binding.panTextView.text = getString(R.string.user_pan)+": ${response.body().panNumber}"
                        binding.dobTextView.text = getString(R.string.user_dob)+" ${response.body().dateOfBirth}"
                        binding.mobileTextView.text = getString(R.string.orderstatus_mobno)+": +91 ${response.body().phoneNumber}"

                        if (!response.body().efilingStatuses?.isEmpty()!!) {
                            efilingStatusesList = response.body().efilingStatuses!!
                            if (response.body().efilingStatusField != null)
                                efilingStatusFieldList = response.body().efilingStatusField!!

                            for (i in efilingStatusesList.indices) {
                                if (efilingStatusesList[i].status.equals("Payment Success") && efilingStatusesList[i].isActivate == true)
                                {
                                    binding.llPaymentSuccess.isClickable = true
                                    binding.tvPaymentDesc.text = efilingStatusesList[i].description?.takeUnless { it.isEmpty() } ?: ""
                                    binding.expandlayoutPaymentSuccess.isExpanded = true
                                    isVisiblePaymentSuccessLayout()

                                    binding.imgItrStatus.setImageResource(R.drawable.ic_itr_inprogress)
                                    binding.tvStatus.text = "ITR Inprogress"
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        binding.tvStatus.setTextColor(mContext.getColor(R.color.red))
                                    else
                                        binding.tvStatus.setTextColor(Color.parseColor("#FF6463"))
                                    buttonDisable()
                                }
                                if (efilingStatusesList[i].status.equals("Assign Expert") && efilingStatusesList[i].isActivate == true)
                                {
                                    binding.llPaymentSuccess.isClickable = false
                                    binding.llExpertAssign.isClickable = true

                                    binding.tvTaxexpert.text = efilingStatusesList[i].description?.takeUnless { it.isEmpty() } ?: ""
                                    binding.expandlayoutExpertAssign.isExpanded = true
                                    isVisibleExpertAssignLayout()

                                    binding.imgItrStatus.setImageResource(R.drawable.ic_itr_inprogress)
                                    binding.tvStatus.text = "ITR Inprogress"
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        binding.tvStatus.setTextColor(mContext.getColor(R.color.red))
                                    else
                                        binding.tvStatus.setTextColor(Color.parseColor("#FF6463"))

                                    buttonEnable()

                                    phn_number = efilingStatusesList[i].phoneNumber?.takeUnless { it.isEmpty() } ?: ""
                                    email_id = efilingStatusesList[i].emailId?.takeUnless { it.isEmpty() } ?: ""

                                }
                                if (efilingStatusesList[i].status.equals("Review Information") && efilingStatusesList[i].isActivate == true)
                                {
                                    binding.llPaymentSuccess.isClickable = false
                                    binding.llExpertAssign.isClickable = true
                                    binding.llReviewInfo.isClickable = true

                                    binding.tvReviewInfo.text = "Thank you for providing the details to us. Our Tax Experts will review them and will contact you for additional details."
                                    binding.expandlayoutReviewInfo.isExpanded = true
                                    isVisibleReviewInfoLayout()

                                    binding.imgItrStatus.setImageResource(R.drawable.ic_itr_inprogress)
                                    binding.tvStatus.text = "ITR Inprogress"
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        binding.tvStatus.setTextColor(mContext.getColor(R.color.red))
                                    else
                                        binding.tvStatus.setTextColor(Color.parseColor("#FF6463"))

                                    for (j in efilingStatusFieldList.indices) {
                                        if (efilingStatusFieldList.isEmpty()) {
                                            binding.tvReviewInfo.text = efilingStatusesList[i].description?.takeUnless { it.isEmpty() } ?: ""
                                            binding.btnReviewInfo.visibility = View.GONE
                                            binding.llShow.visibility = View.GONE
                                        } else {
                                            newItrBase.efilingStatusField = efilingStatusFieldList
                                            binding.tvReviewInfo.text =
                                                "Some of the information mentioned-below is incorrect. Kindly update."
                                            binding.btnReviewInfo.visibility = View.VISIBLE
                                            binding.llShow.visibility = View.VISIBLE
                                            val tv = TextView(applicationContext)
                                            tv.text = HtmlCompat.fromHtml("&#9670;  " + efilingStatusFieldList[j].label, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                tv.setTextColor(mContext.getColor(R.color.lighter_black))
                                            else
                                                tv.setTextColor(Color.parseColor("#505050"))
                                            tv.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                                            binding.llShow.addView(tv)
                                            binding.btnReviewInfo.setOnClickListener {
                                                val intent = Intent(applicationContext, StatusUpdateDetailsActivity::class.java)
                                                finish()
//                                                intent.putExtra("LIST", efilingStatusFieldList as Serializable)
                                                startActivity(intent)
                                            }
                                        }
                                    }
                                    buttonEnable()
                                }
                                if (efilingStatusesList[i].status.equals("Return Filed") && efilingStatusesList[i].isActivate == true)
                                {
                                    binding.llPaymentSuccess.isClickable = false
                                    binding.llExpertAssign.isClickable = true
                                    binding.llReviewInfo.isClickable = true
                                    binding.llEFiled.isClickable = true

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        binding.imgEFiled.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
                                    else
                                        ViewCompat.setBackgroundTintList(binding.imgEFiled, ColorStateList.valueOf(Color.rgb(0, 184, 148)))

                                    binding.expandlayoutEFiled.isExpanded = true
                                    isVisibleEFiledLayout()

                                    if (newItrBase.eVerifyPaymentDone == false) {
                                        binding.rlInprogress.visibility = RelativeLayout.GONE
                                        binding.rlCompleted.visibility = RelativeLayout.VISIBLE
                                        binding.tvAcknowledgementno.text = newItrBase.acknowledgementNo.toString()
                                        binding.imgItrStatus.setImageResource(R.drawable.ic_e_filed)
                                        binding.tvStatus.text = "E-Filed"
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                            binding.tvStatus.setTextColor(mContext.getColor(R.color.colorPrimary))
                                        else
                                            binding.tvStatus.setTextColor(Color.parseColor("#00B894"))
                                        binding.rlChat.visibility = View.GONE
                                        binding.btnEVerify.visibility = View.VISIBLE
                                        binding.note.visibility = View.VISIBLE
                                    }
                                    else {
                                        binding.rlInprogress.visibility = RelativeLayout.GONE
                                        binding.rlCompleted.visibility = RelativeLayout.VISIBLE
                                        binding.tvAcknowledgementno.text = newItrBase.acknowledgementNo.toString()
                                        binding.imgItrStatus.setImageResource(R.drawable.ic_e_filed)
                                        binding.tvStatus.text = "E-Filed"
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                            binding.tvStatus.setTextColor(mContext.getColor(R.color.colorPrimary))
                                        else
                                            binding.tvStatus.setTextColor(Color.parseColor("#00B894"))
                                        binding.rlChat.visibility = View.VISIBLE
                                        binding.btnEVerify.visibility = View.GONE
                                        binding.note.visibility = View.GONE
                                    }
                                    if (newItrBase.rateUsVisible) {
                                     AlertDialogueManager().showRateUsView(mContext, newItrBase.acknowledgementNo.toString())
                                        }
                                }
                            }
                        }
                    }
                    else {
                        dialog.hideDialog()
                        Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception){
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun buttonEnable() {
        binding.btnCall.isEnabled = true
        binding.btnEmail.isEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.btnCall.setCardBackgroundColor(mContext.getColorStateList(R.color.dark_yellow))
            binding.btnEmail.setCardBackgroundColor(mContext.getColorStateList(R.color.colorPrimary))
        }
        else {
            binding.btnCall.setCardBackgroundColor(Color.parseColor("#FFAB2A"))
            binding.btnEmail.setCardBackgroundColor(Color.parseColor("#00B894"))
        }
    }

    private fun buttonDisable() {
        binding.btnCall.isEnabled = false
        binding.btnEmail.isEnabled = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.btnCall.setCardBackgroundColor(mContext.getColorStateList(R.color.lightest_grey_forstatus))
            binding.btnEmail.setCardBackgroundColor(mContext.getColorStateList(R.color.lightest_grey_forstatus))
        }
        else {
            binding.btnCall.setCardBackgroundColor(Color.parseColor("#F8F8F8"))
            binding.btnEmail.setCardBackgroundColor(Color.parseColor("#F8F8F8"))
        }
    }

    private fun isVisiblePaymentSuccessLayout() {
        binding.expandlayoutPaymentSuccess.expand()
        binding.expandlayoutExpertAssign.collapse()
        binding.expandlayoutReviewInfo.collapse()
        binding.expandlayoutEFiled.collapse()

        binding.imgPaymentSuccess.setImageResource(R.drawable.ic_check_black_24dp)
        binding.imgPaymentSuccess.requestLayout()
        binding.imgPaymentSuccess.layoutParams.height = resources.getDimension(R.dimen.expanded_dimen).toInt()
        binding.imgPaymentSuccess.layoutParams.width = resources.getDimension(R.dimen.expanded_dimen).toInt()

        binding.imgExpertAssign.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgExpertAssign.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()

        binding.imgReviewInfo.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgReviewInfo.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()

        binding.imgEFiled.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgEFiled.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgPaymentSuccess.setColorFilter(mContext.getColor(R.color.white))
            binding.imgExpertAssign.setColorFilter(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.imgReviewInfo.setColorFilter(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.imgEFiled.setColorFilter(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.viewPaymentSuccess.setBackgroundColor(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.viewExpertAssign.setBackgroundColor(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.viewReviewInfo.setBackgroundColor(mContext.getColor(R.color.lightest_grey_forstatus))
        }
        else {
            binding.imgPaymentSuccess.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgExpertAssign.setColorFilter(Color.parseColor("#F8F8F8"))
            binding.imgReviewInfo.setColorFilter(Color.parseColor("#F8F8F8"))
            binding.imgEFiled.setColorFilter(Color.parseColor("#F8F8F8"))
            binding.viewPaymentSuccess.setBackgroundColor(Color.parseColor("#F8F8F8"))
            binding.viewExpertAssign.setBackgroundColor(Color.parseColor("#F8F8F8"))
            binding.viewReviewInfo.setBackgroundColor(Color.parseColor("#F8F8F8"))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgPaymentSuccess.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgExpertAssign.backgroundTintList = mContext.getColorStateList(R.color.lightest_grey_forstatus)
            binding.imgReviewInfo.backgroundTintList = mContext.getColorStateList(R.color.lightest_grey_forstatus)
            binding.imgEFiled.backgroundTintList = mContext.getColorStateList(R.color.lightest_grey_forstatus)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgPaymentSuccess, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgExpertAssign, ColorStateList.valueOf(Color.rgb(248, 248, 248)))
            ViewCompat.setBackgroundTintList(binding.imgReviewInfo, ColorStateList.valueOf(Color.rgb(248, 248, 248)))
            ViewCompat.setBackgroundTintList(binding.imgEFiled, ColorStateList.valueOf(Color.rgb(248, 248, 248)))
        }

        binding.llExpertAssign.layoutParams = parentParams
        binding.llReviewInfo.layoutParams = parentParams
        binding.llEFiled.layoutParams = parentParams

        binding.llPaymentSuccess.layoutParams = expParentParams

//        exp_binding.llExpertAssign.layoutParams = childParams
//        exp_binding.llReviewInfo.layoutParams = childParams
//        exp_binding.llEFiled.layoutParams = childParams

        binding.txtPaymentSuccess.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_sixteen))
        binding.txtPaymentSuccess.typeface = Typeface.defaultFromStyle(if (binding.txtExpertAssign.isClickable) Typeface.NORMAL else Typeface.BOLD)

        binding.txtExpertAssign.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtExpertAssign.typeface = Typeface.defaultFromStyle(if (binding.txtReviewInfo.isClickable) Typeface.BOLD else Typeface.NORMAL)

        binding.txtReviewInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtReviewInfo.typeface = Typeface.defaultFromStyle(if (binding.txtReviewInfo.isClickable) Typeface.BOLD else Typeface.NORMAL)

        binding.txtEFiled.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtEFiled.typeface = Typeface.defaultFromStyle(if (binding.txtEFiled.isClickable) Typeface.BOLD else Typeface.NORMAL)

        if (binding.llPaymentSuccess.isClickable) {
            isActive()
        }
    }

    fun isVisibleExpertAssignLayout() {
        binding.expandlayoutPaymentSuccess.collapse()
        binding.expandlayoutExpertAssign.expand()
        binding.expandlayoutReviewInfo.collapse()
        binding.expandlayoutEFiled.collapse()

        binding.imgExpertAssign.setImageResource(R.drawable.ic_check_black_24dp)
        binding.imgExpertAssign.requestLayout()
        binding.imgExpertAssign.layoutParams.height = resources.getDimension(R.dimen.expanded_dimen).toInt()
        binding.imgExpertAssign.layoutParams.width = resources.getDimension(R.dimen.expanded_dimen).toInt()

        binding.imgPaymentSuccess.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgPaymentSuccess.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgPaymentSuccess.setImageResource(R.drawable.ic_check_black_24dp)

        binding.imgReviewInfo.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgReviewInfo.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()

        binding.imgEFiled.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgEFiled.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgExpertAssign.setColorFilter(mContext.getColor(R.color.white))
            binding.imgPaymentSuccess.setColorFilter(mContext.getColor(R.color.white))
            binding.imgReviewInfo.setColorFilter(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.imgEFiled.setColorFilter(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.viewPaymentSuccess.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.viewExpertAssign.setBackgroundColor(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.viewReviewInfo.setBackgroundColor(mContext.getColor(R.color.lightest_grey_forstatus))
        }
        else {
            binding.imgExpertAssign.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgPaymentSuccess.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgReviewInfo.setColorFilter(Color.parseColor("#F8F8F8"))
            binding.imgEFiled.setColorFilter(Color.parseColor("#F8F8F8"))
            binding.viewPaymentSuccess.setBackgroundColor(Color.parseColor("#00B894"))
            binding.viewExpertAssign.setBackgroundColor(Color.parseColor("#F8F8F8"))
            binding.viewReviewInfo.setBackgroundColor(Color.parseColor("#F8F8F8"))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgExpertAssign.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgPaymentSuccess.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgReviewInfo.backgroundTintList = mContext.getColorStateList(R.color.lightest_grey_forstatus)
            binding.imgEFiled.backgroundTintList = mContext.getColorStateList(R.color.lightest_grey_forstatus)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgExpertAssign, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgPaymentSuccess, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgReviewInfo, ColorStateList.valueOf(Color.rgb(248, 248, 248)))
            ViewCompat.setBackgroundTintList(binding.imgEFiled, ColorStateList.valueOf(Color.rgb(248, 248, 248)))
        }

        binding.llPaymentSuccess.layoutParams = parentParams
        binding.llReviewInfo.layoutParams = parentParams
        binding.llEFiled.layoutParams = parentParams

        binding.llExpertAssign.layoutParams = expParentParams

//        exp_binding.llPaymentSuccess.layoutParams = childParams
//        exp_binding.llReviewInfo.layoutParams = childParams
//        exp_binding.llEFiled.layoutParams = childParams

        binding.txtPaymentSuccess.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtPaymentSuccess.typeface = Typeface.defaultFromStyle(if (binding.txtReviewInfo.isClickable) Typeface.BOLD else Typeface.NORMAL)

        binding.txtExpertAssign.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_sixteen))
        binding.txtExpertAssign.typeface = Typeface.defaultFromStyle(if (binding.txtExpertAssign.isClickable) Typeface.NORMAL else Typeface.BOLD)

        binding.txtReviewInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtReviewInfo.typeface = Typeface.defaultFromStyle(if (binding.txtReviewInfo.isClickable) Typeface.BOLD else Typeface.NORMAL)

        binding.txtEFiled.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtEFiled.typeface = Typeface.defaultFromStyle(if (binding.txtEFiled.isClickable) Typeface.BOLD else Typeface.NORMAL)

        if (binding.llExpertAssign.isClickable) {
            isActive()
        }
    }

    private fun isVisibleReviewInfoLayout() {
        binding.expandlayoutPaymentSuccess.collapse()
        binding.expandlayoutExpertAssign.collapse()
        binding.expandlayoutReviewInfo.expand()
        binding.expandlayoutEFiled.collapse()

        binding.imgReviewInfo.setImageResource(R.drawable.ic_check_black_24dp)
        binding.imgReviewInfo.requestLayout()
        binding.imgReviewInfo.layoutParams.height = resources.getDimension(R.dimen.expanded_dimen).toInt()
        binding.imgReviewInfo.layoutParams.width = resources.getDimension(R.dimen.expanded_dimen).toInt()

        binding.imgPaymentSuccess.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgPaymentSuccess.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgPaymentSuccess.setImageResource(R.drawable.ic_check_black_24dp)

        binding.imgExpertAssign.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgExpertAssign.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgExpertAssign.setImageResource(R.drawable.ic_check_black_24dp)

        binding.imgEFiled.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgEFiled.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgReviewInfo.setColorFilter(mContext.getColor(R.color.white))
            binding.imgPaymentSuccess.setColorFilter(mContext.getColor(R.color.white))
            binding.imgExpertAssign.setColorFilter(mContext.getColor(R.color.white))
            binding.imgEFiled.setColorFilter(mContext.getColor(R.color.lightest_grey_forstatus))
            binding.viewPaymentSuccess.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.viewExpertAssign.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.viewReviewInfo.setBackgroundColor(mContext.getColor(R.color.lightest_grey_forstatus))
        }
        else {
            binding.imgReviewInfo.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgPaymentSuccess.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgExpertAssign.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgEFiled.setColorFilter(Color.parseColor("#F8F8F8"))
            binding.viewPaymentSuccess.setBackgroundColor(Color.parseColor("#00B894"))
            binding.viewExpertAssign.setBackgroundColor(Color.parseColor("#00B894"))
            binding.viewReviewInfo.setBackgroundColor(Color.parseColor("#F8F8F8"))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgReviewInfo.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgPaymentSuccess.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgExpertAssign.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgEFiled.backgroundTintList = mContext.getColorStateList(R.color.lightest_grey_forstatus)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgReviewInfo, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgPaymentSuccess, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgExpertAssign, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgEFiled, ColorStateList.valueOf(Color.rgb(248, 248, 248)))
        }

        binding.llPaymentSuccess.layoutParams = parentParams
        binding.llExpertAssign.layoutParams = parentParams
        binding.llEFiled.layoutParams = parentParams

        binding.llReviewInfo.layoutParams = expParentParams

//        exp_binding.llPaymentSuccess.layoutParams = childParams
//        exp_binding.llExpertAssign.layoutParams = childParams
//        exp_binding.llEFiled.layoutParams = childParams

        binding.txtReviewInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_sixteen))
        binding.txtReviewInfo.typeface = Typeface.defaultFromStyle(if (binding.txtReviewInfo.isClickable) Typeface.NORMAL else Typeface.BOLD)

        binding.txtPaymentSuccess.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtPaymentSuccess.typeface = Typeface.defaultFromStyle(if (binding.txtExpertAssign.isClickable) Typeface.BOLD else Typeface.NORMAL)

        binding.txtExpertAssign.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtExpertAssign.typeface = Typeface.defaultFromStyle(if (binding.txtExpertAssign.isClickable) Typeface.BOLD else Typeface.NORMAL)

        binding.txtEFiled.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtEFiled.typeface = Typeface.defaultFromStyle(if (binding.txtEFiled.isClickable) Typeface.BOLD else Typeface.NORMAL)

        if (binding.llReviewInfo.isClickable) {
            isActive()
        }
    }

    private fun isVisibleEFiledLayout() {
        binding.expandlayoutPaymentSuccess.collapse()
        binding.expandlayoutExpertAssign.collapse()
        binding.expandlayoutReviewInfo.collapse()
        binding.expandlayoutEFiled.expand()

        binding.imgEFiled.setImageResource(R.drawable.ic_check_black_24dp)
        binding.imgEFiled.requestLayout()
        binding.imgEFiled.layoutParams.height = resources.getDimension(R.dimen.expanded_dimen).toInt()
        binding.imgEFiled.layoutParams.width = resources.getDimension(R.dimen.expanded_dimen).toInt()

        binding.imgPaymentSuccess.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgPaymentSuccess.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgPaymentSuccess.setImageResource(R.drawable.ic_check_black_24dp)

        binding.imgExpertAssign.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgExpertAssign.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgExpertAssign.setImageResource(R.drawable.ic_check_black_24dp)

        binding.imgReviewInfo.layoutParams.height = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgReviewInfo.layoutParams.width = resources.getDimension(R.dimen.collapsed_dimen).toInt()
        binding.imgReviewInfo.setImageResource(R.drawable.ic_check_black_24dp)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgReviewInfo.setColorFilter(mContext.getColor(R.color.white))
            binding.imgPaymentSuccess.setColorFilter(mContext.getColor(R.color.white))
            binding.imgExpertAssign.setColorFilter(mContext.getColor(R.color.white))
            binding.imgEFiled.setColorFilter(mContext.getColor(R.color.white))
            binding.viewPaymentSuccess.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.viewExpertAssign.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.viewReviewInfo.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
        }
        else {
            binding.imgReviewInfo.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgPaymentSuccess.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgExpertAssign.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.imgEFiled.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.viewPaymentSuccess.setBackgroundColor(Color.parseColor("#00B894"))
            binding.viewExpertAssign.setBackgroundColor(Color.parseColor("#00B894"))
            binding.viewReviewInfo.setBackgroundColor(Color.parseColor("#00B894"))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgReviewInfo.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgPaymentSuccess.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgExpertAssign.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgEFiled.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgReviewInfo, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgPaymentSuccess, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgExpertAssign, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgEFiled, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
        }

        binding.llPaymentSuccess.layoutParams = parentParams
        binding.llExpertAssign.layoutParams = parentParams
        binding.llReviewInfo.layoutParams = parentParams

        binding.llEFiled.layoutParams = expParentParams

//        exp_binding.llPaymentSuccess.layoutParams = childParams
//        exp_binding.llExpertAssign.layoutParams = childParams
//        exp_binding.llReviewInfo.layoutParams = childParams

        binding.txtEFiled.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_sixteen))
        binding.txtEFiled.typeface = Typeface.defaultFromStyle(if (binding.txtEFiled.isClickable) Typeface.NORMAL else Typeface.BOLD)

        binding.txtPaymentSuccess.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtPaymentSuccess.typeface = Typeface.defaultFromStyle(if (binding.txtExpertAssign.isClickable) Typeface.BOLD else Typeface.NORMAL)

        binding.txtExpertAssign.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtExpertAssign.typeface = Typeface.defaultFromStyle(if (binding.txtExpertAssign.isClickable) Typeface.BOLD else Typeface.NORMAL)

        binding.txtReviewInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fifteen))
        binding.txtReviewInfo.typeface = Typeface.defaultFromStyle(if (binding.txtReviewInfo.isClickable) Typeface.BOLD else Typeface.NORMAL)

        if (binding.llEFiled.isClickable) {
            isActive()
        }
    }

    private fun isActive() {
        if (binding.llPaymentSuccess.isClickable) {
            activePaymentSuccess()
        }
        if (binding.llExpertAssign.isClickable) {
            activeExpertAssign()
        }
        if (binding.llReviewInfo.isClickable) {
            activeReviewInfo()
        }
        if (binding.llEFiled.isClickable) {
            activeEFiled()
        }
    }

    private fun activePaymentSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            binding.imgPaymentSuccess.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        else
            ViewCompat.setBackgroundTintList(binding.imgPaymentSuccess, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
    }

    private fun activeExpertAssign() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgPaymentSuccess.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.viewPaymentSuccess.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.imgExpertAssign.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgPaymentSuccess, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            binding.viewPaymentSuccess.setBackgroundColor(Color.parseColor("#00B894"))
            ViewCompat.setBackgroundTintList(binding.imgExpertAssign, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
        }
    }

    private fun activeReviewInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgPaymentSuccess.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.viewPaymentSuccess.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.imgExpertAssign.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.viewExpertAssign.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.imgReviewInfo.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgPaymentSuccess, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            binding.viewPaymentSuccess.setBackgroundColor(Color.parseColor("#00B894"))
            ViewCompat.setBackgroundTintList(binding.imgExpertAssign, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            binding.viewExpertAssign.setBackgroundColor(Color.parseColor("#00B894"))
            ViewCompat.setBackgroundTintList(binding.imgReviewInfo, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
        }
    }

    private fun activeEFiled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgPaymentSuccess.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.viewPaymentSuccess.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.imgExpertAssign.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.viewExpertAssign.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.imgReviewInfo.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.viewReviewInfo.setBackgroundColor(mContext.getColor(R.color.colorPrimary))
            binding.imgEFiled.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgPaymentSuccess, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            binding.viewPaymentSuccess.setBackgroundColor(Color.parseColor("#00B894"))
            ViewCompat.setBackgroundTintList(binding.imgExpertAssign, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            binding.viewExpertAssign.setBackgroundColor(Color.parseColor("#00B894"))
            ViewCompat.setBackgroundTintList(binding.imgReviewInfo, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            binding.viewReviewInfo.setBackgroundColor(Color.parseColor("#00B894"))
            ViewCompat.setBackgroundTintList(binding.imgEFiled, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home){
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            if(newItrBase.getisNotification())
            {
                val intent = Intent(applicationContext, DashboardActivity::class.java)
                this@ITROrderStatusActivity.finish()
                newItrBase.setisNotification(false)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                startActivity(intent)
                //finish()
            }
            else if (newItrBase.orderMode.equals("AllOrders")) {
                val intent = Intent(applicationContext, AllOrdersActivity::class.java)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                startActivity(intent)
                finish()
            }
            else {
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(newItrBase.getisNotification())
        {
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            this@ITROrderStatusActivity.finish()
            newItrBase.setisNotification(false)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            startActivity(intent)
            //finish()
        }
        else if (newItrBase.orderMode.equals("AllOrders")) {
            val intent = Intent(applicationContext, AllOrdersActivity::class.java)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            startActivity(intent)
            finish()
        }
        else {
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            super.onBackPressed()
        }
    }
}
