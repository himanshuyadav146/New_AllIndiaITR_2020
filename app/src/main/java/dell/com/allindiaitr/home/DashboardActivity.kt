package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.gson.GsonBuilder
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.DashboardAdapter
import dell.com.allindiaitr.databinding.ActivityDashboardBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.revisedReturn.ReviseListActivity
import dell.com.allindiaitr.revisedReturn.RevisePersonalInfoActivity
import dell.com.allindiaitr.sliderscreen.RunOnce
import dell.com.allindiaitr.submitDocument.SourceOfIncomeActivity
import dell.com.allindiaitr.submitDocument.UserListActivity
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityDashboardBinding
    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    private var appPreferences: AppPreferences? = null
    var Refer2EarnShowIsActive: Boolean = false
    lateinit var Refer2EarnShowDescription: String
    lateinit var Refer2EarnShowType: String
    var undermaintenanceIsActive: Boolean = false
    lateinit var undermaintenanceDescription: String
    lateinit var undermaintenanceType: String
    var kycIsActive: Boolean = false
    lateinit var kycIsDescription: String
    lateinit var kycIsType: String
    var ITRIsActive: Boolean = false
    lateinit var ITRDescription: String
    lateinit var ITRType: String
    lateinit var versionName: String
    var newItrBase = NewItrBase.instance
    private var runOnce: RunOnce? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(mContext)
        binding.toolbar.toolbarTitle.text =getString(R.string.title_choose_services)
        runOnce = RunOnce(this)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        versionName = mContext.packageManager.getPackageInfo(packageName, 0).versionName.toString()

        newItrBase.selectedUser_userAssessmentYearUserID = null

        visibilityLayout(undermaintenanceIsActive)
        setRecyclerView()
        binding.bottomNavigationView.selectedItemId = R.id.navigation_home
        var menuView: BottomNavigationMenuView = binding.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount){
            var iconView = menuView.getChildAt(i).findViewById<View>(R.id.icon)
//            var iconView = menuView.getChildAt(i).findViewById<View>(R.id.email_id)
            if(iconView!=null){
                var layoutParams = iconView.layoutParams
                var displayMetrics = resources.displayMetrics
                layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20F, displayMetrics).toInt()
                layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20F, displayMetrics).toInt()
                iconView.layoutParams = layoutParams
            }

        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            if (!ConnectionDetector().isConnectingToInternet(mContext)){
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                return@setOnNavigationItemSelectedListener false
            }
            when (it.itemId) {
                R.id.navigation_home -> return@setOnNavigationItemSelectedListener false
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
                R.id.navigation_more-> {
                    newItrBase.orderMode = "Orders"
                    val intent = Intent(mContext, MoreActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }


        binding.cardStartFiling.setOnClickListener {
            newItrBase.processMode = CommonVal.SubmitDocument.name
            if (ConnectionDetector().isConnectingToInternet(mContext)){
                getUserList()
            }
            else {
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardResolveTax.setOnClickListener {
            newItrBase.processMode = CommonVal.RevisedReturn.name
            if (ConnectionDetector().isConnectingToInternet(mContext)){
                getUserList()
            }
            else {
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardReferUrn.setOnClickListener {
            newItrBase.orderMode = "Orders"
            val intent = Intent(mContext, ReferAndEarnActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        binding.cardNeedHelp.setOnClickListener {
            val intent = Intent(mContext, ContactUsActivity::class.java)
            (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
        }

    }




    override fun attachBaseContext(newBase:Context?){
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }

    override fun onResume() {
        super.onResume()
        if (ConnectionDetector().isConnectingToInternet(mContext)){
            getVersionUpdate()
        }
        else{
//            dashboard_layout.visibility = View.GONE
            binding.underMaintanenceLayout.visibility = View.GONE
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getVersionUpdate(){

//        var dialog = AlertDialogueManager(mContext,"Please Wait")
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
        val call = apI_Interface.versionUpdate("Android", appPreferences!!.parentId.toString())
        call.enqueue(object : Callback<NewItrBase> {
            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        newItrBase.iTRComputationURL = response.body().iTRComputationURL?.takeUnless { it.isEmpty() } ?: ""
                        var mobileAppVersion = response.body().mobileAppVersion?.takeUnless { it.isEmpty() } ?: ""
                        var alertMessage = response.body().alertMessage?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.isNewUser = if (response.body().childUserCount != null) response.body()!!.childUserCount!! else true


                        //new added
                        newItrBase.assestmentYear = response.body().assestmentYear?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.assessmentYearId = response.body().assestmentYearId?.takeUnless { it.isEmpty() } ?: ""
                        var appUtilityTemp : List<NewItrBase>? = response.body().appUtilityList
                        if(appUtilityTemp!=null){
                            for (i in 0 until appUtilityTemp.size) {
                                when (appUtilityTemp[i].type) {

                                    "PriFileClosed" -> {
                                        newItrBase.priFileClosed = if (appUtilityTemp[i].isActive != null) appUtilityTemp[i].isActive!! else false
                                    }

                                    "ManualFileClosed" -> {
                                        newItrBase.manualFileClosed = if (appUtilityTemp[i].isActive != null) appUtilityTemp[i].isActive!! else false
                                    }
                                }
                            };
                        }


                        var appUtilityList = response.body().appUtilityList as MutableList<NewItrBase>
                        for (i in 0 until appUtilityList.size){
                            when (appUtilityList[i].type) {
                                "Refer2EarnShow" -> {
                                    Refer2EarnShowIsActive = if (appUtilityList[i].isActive != null) appUtilityList[i].isActive!! else false
                                    Refer2EarnShowDescription = appUtilityList[i].description?.takeUnless { it.isEmpty() } ?: ""
                                    Refer2EarnShowType = appUtilityList[i].type?.takeUnless { it.isEmpty() } ?: ""
                                }
                                "undermaintenance" -> {
                                    undermaintenanceIsActive = if (appUtilityList[i].isActive != null) appUtilityList[i].isActive!! else false
                                    undermaintenanceDescription = appUtilityList[i].description?.takeUnless { it.isEmpty() } ?: ""
                                    undermaintenanceType = appUtilityList[i].type?.takeUnless { it.isEmpty() } ?: ""
                                }
                                "KYCClosed" -> {
                                    kycIsActive = if (appUtilityList[i].isActive != null) appUtilityList[i].isActive!! else false
                                    kycIsDescription = appUtilityList[i].description?.takeUnless { it.isEmpty() } ?: ""
                                    kycIsType = appUtilityList[i].type?.takeUnless { it.isEmpty() } ?: ""
                                }
                                "ITRClosed" -> {
                                    ITRIsActive = if (appUtilityList[i].isActive != null) appUtilityList[i].isActive!! else false
                                    ITRDescription = appUtilityList[i].description?.takeUnless { it.isEmpty() } ?: ""
                                    ITRType = appUtilityList[i].type?.takeUnless { it.isEmpty() } ?: ""
                                }
                                /*"PriFileClosed" -> {
                                    newItrBase.priFileClosed = if (appUtilityList[i].isActive != null) appUtilityList[i].isActive!! else false
                                }
                                "ManualFileClosed" -> {
                                    newItrBase.manualFileClosed = if (appUtilityList[i].isActive != null) appUtilityList[i].isActive!! else false
                                }*/
                            }
                        }
                        visibilityLayout(undermaintenanceIsActive)

                        if (versionName.toFloat() < mobileAppVersion.toFloat()) {
                            AlertDialogueManager().updateVersionDialogue(mContext, alertMessage.toString())
                        }

                        if (appPreferences!!.appLaunch){
//                        if(runOnce!!.isFirstTimeLaunch){
                            if (newItrBase.referNowVisible){
                                if (newItrBase.isReferVisible && Refer2EarnShowIsActive){
                                    if ((Refer2EarnShowDescription.toInt() > newItrBase.referNCount)){
                                        AlertDialogueManager().referAndEarnDialogue(mContext)
                                        newItrBase.referNCount = newItrBase.referNCount + 1
                                    }
                                }
                            }
                        }
                    }
                    else{
                        dialog.hideDialog()
                        binding.dashboardLayout.visibility = View.GONE
                        binding.underMaintanenceLayout.visibility = View.GONE
                        binding.oopsImageView.visibility = View.VISIBLE
                    }
                }
                catch (e: Exception){
                    dialog.hideDialog()
                    binding.dashboardLayout.visibility = View.GONE
                    binding.underMaintanenceLayout.visibility = View.GONE
                    binding.oopsImageView.visibility = View.VISIBLE
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                binding.dashboardLayout.visibility = View.GONE
                binding.underMaintanenceLayout.visibility = View.GONE
                binding.oopsImageView.visibility = View.VISIBLE
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getUserList(){
        //  var dialog = AlertDialogueManager(mContext,"Please Wait")
        var dialog = AlertDialogueManager(mContext,mContext.getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getITRuserList(appPreferences!!.parentId.toString())
        call.enqueue(object : Callback<List<NewItrBase>> {
            override fun onResponse(call: Call<List<NewItrBase>>?, response: Response<List<NewItrBase>>?) {
                // Log.e("response ","get_users_list " +response?.body());
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        if (response.body() != null) {
                            newItrBase.baseUserList = response.body()
                            newItrBase.isNewUser = response.body()!!.isEmpty()
                            navigate()
                        }
                    }
                    else {
                        dialog.hideDialog()
                        navigate()
                    }
                }
                catch (e: java.lang.Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NewItrBase>>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun navigate(){
        if (newItrBase.processMode == CommonVal.SubmitDocument.name){
            if (newItrBase.isNewUser){
                val intent = Intent(mContext, SourceOfIncomeActivity::class.java)
                mContext.startActivity(intent)
            }
            else {
                val intent = Intent(mContext, UserListActivity::class.java)
                mContext.startActivity(intent)
            }
        }
        else if (newItrBase.processMode == CommonVal.RevisedReturn.name) {
            if (newItrBase.isNewUser){
                val intent = Intent(mContext, RevisePersonalInfoActivity::class.java)
                mContext.startActivity(intent)
            }
            else {
                val intent = Intent(mContext, ReviseListActivity::class.java)
                mContext.startActivity(intent)
            }
        }
        else {

        }
    }

    fun visibilityLayout(undermaintenanceIsActive: Boolean){
        if (undermaintenanceIsActive){
            binding.dashboardLayout.visibility = View.GONE
            binding.underMaintanenceLayout.visibility = View.VISIBLE
            binding.oopsImageView.visibility = View.GONE
        }
        else {
            binding.dashboardLayout.visibility = View.VISIBLE
            binding.underMaintanenceLayout.visibility = View.GONE
            binding.oopsImageView.visibility = View.GONE
        }
    }

    private fun setRecyclerView(){
        var heading: List<String> = listOf<String>(getString(R.string.dashboard_starte_filing),
            getString(R.string.dashboard_replytonotice),
            getString(R.string.dashboard_incometax),
            getString(R.string.dashboard_hra),
            getString(R.string.dashboard_rent_recipt))

        var description: List<String> = listOf<String>(getString(R.string.dashboard_sub_starte_filing),
            getString(R.string.dashboard_replytonotice),
            getString(R.string.dashboard_sub_incometax),
            getString(R.string.dashboard_sub_hra),
            getString(R.string.dashboard_sub_rent_recipt))

        var images: List<Int> = listOf<Int>(R.drawable.ic_start_e_filing,
            R.drawable.ic_reply_to_notice,
            R.drawable.ic_income_tax_calculator,
            R.drawable.ic_hra_calculator,
            R.drawable.ic_rent_reciept_generator)

        binding.dashboardRecyclerView.layoutManager = LinearLayoutManager(mContext)
        binding.dashboardRecyclerView.adapter = DashboardAdapter(mContext, heading, description, images)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
//      finish()

    }

}
