package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityMoreBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.login.LogInActivity
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*

class MoreActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    private var appPreferences: AppPreferences? = null
    var newItrBase = NewItrBase.instance
    lateinit var binding:ActivityMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(mContext)
        binding.toolbar.toolbarTitle.text = getString(R.string.more_title)

        if (appPreferences!!.userFirstName != null && appPreferences!!.userLastName != null)
            binding.tvUsername.text = appPreferences!!.userFirstName + " " + appPreferences!!.userLastName
        else
            binding.tvUsername.visibility = View.GONE

        if (appPreferences!!.emailAddress == "" || appPreferences!!.emailAddress == null)
            binding.tvEmailId.visibility = View.GONE
        else
            binding.tvEmailId.text = appPreferences!!.emailAddress!!.toString()

        binding.tvPhoneNo.text = "+91 " + appPreferences!!.mobileNumber!!.toString()

        binding.blogs.setOnClickListener(this)
        binding.faqs.setOnClickListener(this)
        binding.share.setOnClickListener(this)
        binding.rateUs.setOnClickListener(this)
        binding.aboutUs.setOnClickListener(this)
        binding.contactUs.setOnClickListener(this)
        binding.logOut.setOnClickListener(this)
        binding.lang.setOnClickListener(this)
        binding.custom.setOnClickListener(this)

        binding.bottomNavigationView.selectedItemId = R.id.navigation_more

        var menuView: BottomNavigationMenuView = binding.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount){
            var iconView = menuView.getChildAt(i).findViewById<View>(R.id.icon)
            if(iconView!=null){
                var layoutParams = iconView.layoutParams
                var displayMetrics = resources.displayMetrics
                layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18F, displayMetrics).toInt()
                layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18F, displayMetrics).toInt()
                iconView.layoutParams = layoutParams
            }

        }



        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            if (!ConnectionDetector().isConnectingToInternet(mContext)){
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                return@setOnNavigationItemSelectedListener false
            }
            when (it.itemId) {
                R.id.navigation_home -> {
                    newItrBase.orderMode = "Orders"
                    val intent = Intent(mContext, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_orders -> {
                    newItrBase.orderMode = "AllOrders"
                    val intent = Intent(mContext, AllOrdersActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_refer -> {
                    newItrBase.orderMode = "Orders"
                    val intent = Intent(mContext, ReferAndEarnActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_more-> return@setOnNavigationItemSelectedListener false
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }





    override fun onBackPressed() {
        val intent = Intent(mContext, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivityForResult(intent, 0)
        overridePendingTransition(0, 0)
        finish()
    }


    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.blogs -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.blogs.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
                    else
                        binding.blogs.setBackgroundColor(Color.parseColor("#EFEFEF"))
                    val intent = Intent(mContext, BlogActivity::class.java)
                    (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    startActivity(intent)
                }
                R.id.faqs -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.faqs.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
                    else
                        binding.faqs.setBackgroundColor(Color.parseColor("#EFEFEF"))
                    val intent = Intent(mContext, FAQActivity::class.java)
                    (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    startActivity(intent)
                }
                R.id.share -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.share.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
                    else
                        binding.share.setBackgroundColor(Color.parseColor("#EFEFEF"))
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "All India ITR - e-filing was never so easy")
                    var sAux = "I found it useful, hope you’ll do too.\n\n"
                    sAux += "https://play.google.com/store/apps/details?id=dell.com.allindiaitr"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, sAux)
                    mContext.startActivity(Intent.createChooser(shareIntent, "Share Via"))
                }
                R.id.custom -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.custom.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
                    else
                        binding.custom.setBackgroundColor(Color.parseColor("#EFEFEF"))
                    val intent = Intent(mContext, CustomPaymentActivity::class.java)
                    (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    startActivity(intent)
                }
                R.id.rate_us -> {
                    binding.rateUs.setBackgroundColor(resources.getColor(R.color.lighter_grey))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.faqs.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
                    else
                        binding.faqs.setBackgroundColor(Color.parseColor("#EFEFEF"))
                    AlertDialogueManager().showViewAlertDialog(
                        mContext,
                        "Would you mind stopping for a moment to rate us?"
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.rateUs.setBackgroundColor(mContext.getColor(R.color.white))
                    else
                        binding.rateUs.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }
                R.id.about_us -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.aboutUs.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
                    else
                        binding.aboutUs.setBackgroundColor(Color.parseColor("#EFEFEF"))
                    val intent = Intent(mContext, AboutUsActivity::class.java)
                    (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    startActivity(intent)
                }
                R.id.contact_us -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.contactUs.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
                    else
                        binding.contactUs.setBackgroundColor(Color.parseColor("#EFEFEF"))
                    val intent = Intent(mContext, ContactUsActivity::class.java)
                    (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    startActivity(intent)
                }
                R.id.log_out -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        binding.logOut.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
                    else
                        binding.logOut.setBackgroundColor(Color.parseColor("#EFEFEF"))
                    appPreferences?.ClearPreferences()
                    val intent = Intent(mContext, LogInActivity::class.java)
                    (mContext as Activity).finish()
                    (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    startActivity(intent)
                }
                R.id.lang->{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                        binding.lang.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
//                    else
//                        binding.lang.setBackgroundColor(Color.parseColor("#EFEFEF"))
//                    val intent = Intent(mContext, LanguageSettingActivity::class.java)
//                    (mContext as Activity).finish()
//                    (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
//                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.navigation_more
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.blogs.setBackgroundColor(mContext.getColor(R.color.white))
            binding.faqs.setBackgroundColor(mContext.getColor(R.color.white))
            binding.share.setBackgroundColor(mContext.getColor(R.color.white))
            binding.rateUs.setBackgroundColor(mContext.getColor(R.color.white))
            binding.aboutUs.setBackgroundColor(mContext.getColor(R.color.white))
            binding.contactUs.setBackgroundColor(mContext.getColor(R.color.white))
            binding.logOut.setBackgroundColor(mContext.getColor(R.color.white))
            binding.lang.setBackgroundColor(mContext.getColor(R.color.white))
            binding.custom.setBackgroundColor(mContext.getColor(R.color.white))
        }
        else {
            binding.blogs.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.faqs.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.share.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.rateUs.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.aboutUs.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.contactUs.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.logOut.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.lang.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.custom.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

}
