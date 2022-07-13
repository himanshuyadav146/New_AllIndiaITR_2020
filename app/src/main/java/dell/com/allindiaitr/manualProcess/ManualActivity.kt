//package dell.com.allindiaitr.manualProcess
//
//import android.app.Activity
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.MenuItem
//import android.view.View
//import android.view.inputmethod.InputMethodManager
//import android.widget.ArrayAdapter
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.widget.Toolbar
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.resource.bitmap.Rotate
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.home.DashboardActivity
//import dell.com.allindiaitr.interfaces.API_Interface
//import dell.com.allindiaitr.models.NewItrBase
//import dell.com.allindiaitr.utils.*
//import kotlinx.android.synthetic.main.activity_contact_us.toolbar
//import kotlinx.android.synthetic.main.activity_manual.*
//import kotlinx.android.synthetic.main.activity_manual.personalInfoLayOut
//import kotlinx.android.synthetic.main.app_toolbar.*
//import kotlinx.android.synthetic.main.list_child_view.*
//import org.json.JSONArray
//import java.io.InputStream
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.lang.Exception
//
//class ManualActivity : AppCompatActivity() {
//
//    lateinit var apI_Interface: API_Interface
//    lateinit var mContext: Context
//    private var appPreferences: AppPreferences? = null
//    private var totalSal: Int = 0
//    private var salaryAmount: Int = 0
//    private var perquisites: Int = 0
//    private var profit_of_sall: Int = 0
//    private var lessAllowances: Int = 0
//    private var professinalTax: Int = 0
//    private var incomeFromSalary: Int = 0
//    private var deduction_80c: Int = 0
//    private var deduction_80ccc: Int = 0
//    private var deduction_80ccd1: Int = 0
//    private var deduction_80ccd2: Int = 0
//    private var deduction_80d: Int = 0
//    private var total_deduction: Int = 0
//    private var house_property: Int = 0
//    private var tds: Int = 0
//    private var companyNameTypeList = mutableListOf<String>()
//    private var companyIdTypeList = mutableListOf<Int>()
//    private var last_image: ImageView? = null
//    var newItrBase = NewItrBase.instance
//    var isNewUser = newItrBase.isNewUser
//    var userAssessmentYearId:String? = null
//    var assessmentYearId = "3DF22446-B42E-464B-8BB0-E810DE1E835E"
//    lateinit var itrProcess: String
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_manual)
//        mContext = this
//        appPreferences = AppPreferences(this)
//        setSupportActionBar(toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar_title.text = getString(R.string.mainual_title)
//
//        newItrBase.assessmentYearId = assessmentYearId
//        userAssessmentYearId = newItrBase.selectedUser_userAssessmentYearUserID.toString()
//
//
//        txt_standarddeducrion.text = "40000"
//        readJSONFromAsset()
//        txt_sal_amount.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
////                Toast.makeText(mContext,totalSal.toString(),Toast.LENGTH_LONG).show()
////                getTotalSal()
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    salaryAmount = p0.toString().toInt()
//                } else {
//                    salaryAmount = 0
//                }
//
//
//            }
//
//        })
//
//
//        txt_perquisites.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
////                getTotalSal()
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    perquisites = p0.toString().toInt()
//                } else {
//                    perquisites = 0
//                }
//
//            }
//
//        })
//
//
//
//        txt_sub_profileinlieu.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
////                getTotalSal()
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    profit_of_sall = p0.toString().toInt()
//                } else {
//                    profit_of_sall = 0
//                }
//
//            }
//
//        })
//
//
//
//        txt_tv_less.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                lessAllowances = p0.toString().toInt()
//            }
//
//        })
//
//
//        txt_professionaltax.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                professinalTax = p0.toString().toInt()
//            }
//
//        })
//
//
//
//        txt_80C.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    deduction_80c = p0.toString().toInt()
//                } else {
//                    deduction_80c = 0
//
//                }
//            }
//
//        })
//
//
//        txt_80CCCC.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    deduction_80ccc = p0.toString().toInt()
//                } else {
//                    deduction_80ccc = 0
//                }
//            }
//
//        })
//
//        txt_80CCD_1.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                deduction_80ccd1 = p0.toString().toInt()
//            }
//
//        })
//
//
//        txt_80CCD_1.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    deduction_80ccd1 = p0.toString().toInt()
//                }
//            }
//
//        })
//
//        txt_80CCD2.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    deduction_80ccd2 = p0.toString().toInt()
//                } else {
//                    deduction_80ccd2 = 0
//                }
//            }
//
//        })
//
//        txt_80D.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                getIncomeFromSalary()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    deduction_80d = p0.toString().toInt()
//                } else {
//                    deduction_80d = 0
//                }
//            }
//
//        })
//
//
//
//        txt_tds.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    tds = p0.toString().toInt()
//                    if (p0.toString().toInt() == 0)
//                        tan_container.visibility = View.GONE
//                    else
//                        tan_container.visibility = View.VISIBLE
//                } else {
//                    tan_container.visibility = View.GONE
//                }
//            }
//
//        })
//
//
//
//        txt_income_from_house.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (!p0!!.isEmpty()) {
//                    house_property = p0.toString().toInt()
//                } else {
//                    house_property = 0
//                }
//
//            }
//
//        })
//
//        btn_submit.setOnClickListener {
//            if (Validation().isEmpaty(
//                    txt_nameofemp.text.toString(),
//                    txt_nameofemp,
//                    "Please Enter Employer"
//                ) &&
//                Validation().isEmpaty(
//                    txt_sal_amount.text.toString(),
//                    txt_sal_amount,
//                    "Please Enter Salary Amount"
//                ) &&
//                Validation().isEmpaty(
//                    txt_perquisites.text.toString(),
//                    txt_perquisites,
//                    "Please Enter Perquisites"
//                ) &&
//                Validation().isEmpaty(
//                    txt_sub_profileinlieu.text.toString(),
//                    txt_sub_profileinlieu,
//                    "Profits inlieu of Salary"
//                ) &&
//                Validation().isEmpaty(
//                    txt_tv_less.text.toString(),
//                    txt_tv_less,
//                    "Please Enter Less Allowances"
//                ) &&
//                Validation().isEmpaty(
//                    txt_professionaltax.text.toString(),
//                    txt_professionaltax,
//                    "Please Enter Professional Tax"
//                ) &&
//                Validation().isEmpaty(
//                    txt_income_from_house.text.toString(),
//                    txt_income_from_house,
//                    "Please Enter Income from Salary"
//                ) &&
//                Validation().isEmpaty(
//                    txt_80CCCC.text.toString(),
//                    txt_80CCCC,
//                    "Please Enter Income from House Property"
//                ) &&
//                Validation().isEmpaty(
//                    txt_80CCD_1.text.toString(),
//                    txt_80CCD_1,
//                    "Please Enter 80C"
//                ) &&
//                Validation().isEmpaty(
//                    txt_80CCD2.text.toString(),
//                    txt_80CCD2,
//                    "Please Enter 80CCC"
//                ) &&
//                Validation().isEmpaty(txt_80D.text.toString(), txt_80D, "Please Enter 80D") &&
//                Validation().isEmpaty(txt_tds.text.toString(), txt_tds, "Please Enter TDS")
//
//            ) {
//                val intent = Intent(mContext, Manual_DetaisActivity::class.java)
//                intent.putExtra("income from sal", incomeFromSalary)
//                intent.putExtra("house property", house_property)
//                intent.putExtra("Deduction", total_deduction)
////                intent.putExtra("total tax income",)
//                intent.putExtra("total TDS", tds)
//                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//                startActivity(intent)
//                //finish()
//            }
//        }
//
//        spn_typeoforgnization
//
////        ButtonVisibility().hideButton(mContext, activity_manual, btn_submit)
//
//        personalInfoLayOut.setOnClickListener {
//            showHide(cl_personal_info, personalInfoDownArowImage)
//        }
//        important_detail.setOnClickListener {
//            showHide(cl_important_info, personalInfoDownArowImage2)
//        }
//        bank_detail.setOnClickListener {
//            showHide(cl_bank_info, personalInfoDownArowImage3)
//        }
//        manual_detail.setOnClickListener {
//            showHide(cl_manual, personalInfoDownArowImage4)
//        }
//
//        btn_save_personal.setOnClickListener {
//            if (Validation().isFirstNameValid(firstNameField.text.toString(), firstNameField) &&
//                Validation().isLastNameValid(lastNameField.text.toString(), lastNameField) &&
//                Validation().isMobileValid(mobileNumberField.text.toString(), mobileNumberField) &&
//                Validation().isEmailValid(emailField.text.toString(), emailField) &&
//                Validation().isPanValid(panNumberField.text.toString(), panNumberField) &&
//                Validation().isDobValid(dobField.text.toString(), dobField)){
//
//                newItrBase.firstName = firstNameField.text.toString()
//                newItrBase.lastName = lastNameField.text.toString()
//                newItrBase.phoneNumber = mobileNumberField.text.toString()
//                newItrBase.email = emailField.text.toString()
//                newItrBase.panNumber = panNumberField.text.toString()
//                newItrBase.dateOfBirth = dobField.text.toString()
//                if (isNewUser){
//                    userAssessmentYearId = null
//                }
//                /*if(ConnectionDetector().isConnectingToInternet(mContext))
////                    postInformation()
//                else{
//                    Toast.makeText(mContext, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show()
//                }*/
//            }
//        }
//
//
//    }
//
//    /*private fun postInformation(){
//        var dialog = AlertDialogueManager(mContext, "hello")
//        var personalInformationModel = NewItrBase(firstNameField.text.toString(), "",
//            lastNameField.text.toString(),
//            panNumberField.text.toString(),
//            "Android",
//            dobField.text.toString(),
//            appPreferences!!.parentId,
//            newItrBase.isSalary,
//            newItrBase.isBusiness,
//            newItrBase.isHouseProperty,
//            newItrBase.isCapitalGain,
//            newItrBase.isOtherSource,
//            newItrBase.isForeignIncome,
//            userAssessmentYearId,
//            itrProcess,
//            emailField.text.toString(),
//            mobileNumberField.text.toString(),
//            newItrBase.assessmentYearId,
//            appPreferences!!.deviceTokenId)
//
//        val call = apI_Interface.postInformation(personalInformationModel)
//        call.enqueue(object : Callback<NewItrBase>{
//
//            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
//                try{
//                    if (response!!.isSuccessful){
//                        dialog.hideDialog()
//                        newItrBase.selectedUserEmail = emailField.text.toString()
//                        newItrBase.selectedUserMobile = mobileNumberField.text.toString()
//                        newItrBase.userName = firstNameField.text.toString() + " "+ lastNameField.text.toString()
//                        newItrBase.selectedUserName = firstNameField.text.toString()+ " "+ lastNameField.text.toString()
////                        if (appPreferences!!.emailAddress == "" || appPreferences!!.emailAddress == null)
//
//
//                    }else{
//                        dialog.hideDialog()
//                        Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//                    }
//
//
//                }catch (e: Exception){}
//
//            }
//
//            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
//
//            }
//
//        })
//
//
//    }*/
//
//
//    private fun readJSONFromAsset(): String? {
//        var json: String?
//        try {
//            val inputStream: InputStream = resources.openRawResource(R.raw.organisation)
//            json = inputStream.bufferedReader().use { it.readText() }
//            companyNameTypeList.add(0, "Select")
//            companyIdTypeList.add(0, 0)
//            val jsonArray = JSONArray(json)
//            for (i in 0 until jsonArray.length()) {
//                val jsonObject = jsonArray.getJSONObject(i)
//                companyNameTypeList.add(jsonObject.getString("companytype"))
//                companyIdTypeList.add(jsonObject.getInt("id"))
//            }
//            spn_typeoforgnization.adapter =
//                ArrayAdapter<String>(mContext, R.layout.spinner_item, companyNameTypeList)
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }
//
//
//    private fun getTotalSal(): Int {
//        totalSal = salaryAmount + perquisites + profit_of_sall
//
//        txt_totalsal1.text = totalSal.toString()
////        Toast.makeText(mContext,totalSal.toString(),Toast.LENGTH_LONG).show()
//        return 0
//    }
//
//    private fun getIncomeFromSalary() {
//
//        totalSal = salaryAmount + perquisites + profit_of_sall
//
//        txt_totalsal1.text = totalSal.toString()
//
//        var total: Int =
//            (lessAllowances + txt_standarddeducrion.text.toString().toInt() + professinalTax)
//        incomeFromSalary = totalSal - total
//        if (incomeFromSalary <= 0)
//            incomeFromSalary = 0
//        txt_incomefromsal.text = incomeFromSalary.toString()
//
//
//        total_deduction =
//            deduction_80c + deduction_80ccc + deduction_80ccd1 + deduction_80ccd2 + deduction_80d
//        if (total_deduction <= 0)
//            total_deduction = 0
//
//        txt_total_deductions.text = total_deduction.toString()
//    }
//
//    override fun attachBaseContext(newBase: Context?) {
//        mContext = newBase!!
//        appPreferences = AppPreferences(newBase!!)
//        val lang: String = appPreferences?.selectedLanguage!!
//        //  setAppLocal(lang)
//        super.attachBaseContext(MyContextWrapper.wrap(newBase, lang))
//
//    }
//
////    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        var id = item!!.itemId
////        if (id == android.R.id.home){
////            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
////            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
////            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
////            finish()
////        }
////        return super.onOptionsItemSelected(item)
////    }
////
////    override fun onBackPressed() {
////        super.onBackPressed()
////        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
////    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        var id = item!!.itemId
//        if (id == android.R.id.home) {
//            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
//            val intent = Intent(mContext, DashboardActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//            startActivityForResult(intent, 0)
//            overridePendingTransition(0, 0)
//            finish()
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onBackPressed() {
//        val intent = Intent(applicationContext, DashboardActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        startActivityForResult(intent, 0)
//        overridePendingTransition(0, 0)
//        finish()
//    }
//
//    private fun showHide(view: View, imageView: ImageView) {
//
//        var boo = false
//        if (view.visibility == View.VISIBLE)
//            boo = true
//
//        cl_personal_info.visibility = View.GONE
//        cl_important_info.visibility = View.GONE
//        cl_bank_info.visibility = View.GONE
//        cl_manual.visibility = View.GONE
//
//        if (boo) {
//            view.visibility = View.GONE
//            Glide
//                .with(mContext)
//                .load(R.drawable.ic_dropdown)
//                .into(imageView)
//
//
//        } else {
//            view.visibility = View.VISIBLE
//            Glide
//                .with(mContext)
//                .load(R.drawable.ic_dropdown)
//                .transform(Rotate(180))
//                .into(imageView)
//
//
//        }
//
//
//    }
//
//
//}
