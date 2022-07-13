package dell.com.allindiaitr.revisedReturn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityRevisePersonalInfoBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RevisePersonalInfoActivity : AppCompatActivity() {

    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    var newItrBase = NewItrBase.instance
    var isNewUser = newItrBase.isNewUser
    lateinit var itrProcess: String
    var userAssessmentYearId: String? = null
    lateinit var year_list: List<NewItrBase>
    var yearAssisYID = mutableListOf<String>()
    lateinit var assessmentYearId : String
    private lateinit var binding: ActivityRevisePersonalInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRevisePersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(this)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = getString(R.string.personal_info)

        itrProcess = CommonVal.RevisedReturn.name

        setDataIntoFields()

        binding.contButton.setOnClickListener {
            if (Validation().isFirstNameValid(binding.firstNameField.text.toString(), binding.firstNameField) &&
                Validation().isLastNameValid(binding.lastNameField.text.toString(), binding.lastNameField) &&
                Validation().isMobileValid(binding.mobileNumberField.text.toString(), binding.mobileNumberField) &&
                Validation().isEmailValid(binding.emailField.text.toString(), binding.emailField) &&
                Validation().isPanValid(binding.panNumberField.text.toString(), binding.panNumberField) &&
                Validation().isDobValid(binding.dobField.text.toString(), binding.dobField)){

                newItrBase.firstName = binding.firstNameField.text.toString()
                newItrBase.lastName = binding.lastNameField.text.toString()
                newItrBase.phoneNumber = binding.mobileNumberField.text.toString()
                newItrBase.email = binding.mobileNumberField.text.toString()
                newItrBase.panNumber = binding.panNumberField.text.toString()
                newItrBase.dateOfBirth = binding.dobField.text.toString()
                if (isNewUser) {
                    userAssessmentYearId = null
                }
                if (isNewUser)
                    newItrBase.selectedUser_userAssessmentYearUserID = null
                userAssessmentYearId = newItrBase.selectedUser_userAssessmentYearUserID
                if (ConnectionDetector().isConnectingToInternet(mContext))
                    postInformation()
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.dobField.setOnClickListener {
            DatePicker(mContext, binding.dobField, 18).datePickerDialog()
        }

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getFinancialYear()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()

        binding.chooseYearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(mContext, "Select your Financial Year or Assessment Year ", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var year = yearAssisYID[position]
                var pos = getPosition(year, year_list)
                assessmentYearId = year_list[pos].id.toString()
            }
        }

        userAssessmentYearId = newItrBase.selectedUser_userAssessmentYearUserID.toString()
        var userId = appPreferences!!.parentId.toString()
        var sessionId = newItrBase.selectedUserId
        var isNewUser = newItrBase.isNewUser

        if (sessionId != null) {
            userId = sessionId
        }
        if (isNewUser) {
            newItrBase.setInstanceEmpty()
            newItrBase.selectedUser_userAssessmentYearUserID = null
            newItrBase.isSalary = "false"
            newItrBase.isBusiness = "false"
            newItrBase.isCapitalGain = "false"
            newItrBase.isForeignIncome = "false"
            newItrBase.isHouseProperty = "false"
            newItrBase.isOtherSource = "false"
            binding.firstNameField.setText(newItrBase.firstName)
            binding.lastNameField.setText(newItrBase.lastName)
            binding.panNumberField.setText(newItrBase.panNumber)
            binding.dobField.text = newItrBase.dateOfBirth
        }
        else {
            if (userAssessmentYearId == "" || userAssessmentYearId == null) {
                if (ConnectionDetector().isConnectingToInternet(mContext))
                    getInformation(userAssessmentYearId!!, userId)
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
            else {
                if (ConnectionDetector().isConnectingToInternet(mContext))
                    getInformation(userAssessmentYearId!!, userId)
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
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

    private fun getPosition(search : String, year_list : List<NewItrBase>): Int {
        var pos = 0
        for (i in 0 until year_list.size) {
            if (year_list[i].financialYearAndAssestmentYear!!.contains(search)) {
                pos = i
            }
        }
        return pos
    }

    private fun setDataIntoFields(){
        binding.firstNameField.setText(newItrBase.firstName)
        binding.lastNameField.setText(newItrBase.lastName)
        binding.mobileNumberField.setText(newItrBase.phoneNumber)
        binding.emailField.setText(newItrBase.email)
        binding.panNumberField.setText(newItrBase.panNumber)
        binding.dobField.text = newItrBase.dateOfBirth
        if (isNewUser) {
            binding.mobileNumberField.setText(appPreferences!!.mobileNumber)
            binding.emailField.setText(appPreferences!!.emailAddress)
        }
        else {
            binding.mobileNumberField.setText(newItrBase.phoneNumber)
            binding.emailField.setText(newItrBase.email)
        }
    }

    private fun getFinancialYear() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getFinancialYear()
        call.enqueue(object : Callback<List<NewItrBase>> {
            override fun onResponse(
                call: Call<List<NewItrBase>>?,
                response: Response<List<NewItrBase>>?
            ) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        year_list = response.body()
                        for (i in 0 until year_list.size) {
                            yearAssisYID.add(year_list[i].financialYearAndAssestmentYear.toString())
                        }
                        binding.chooseYearSpinner.adapter = ArrayAdapter<String>(mContext, R.layout.spinner_item, yearAssisYID)
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<List<NewItrBase>>?, t: Throwable?) {
                dialog.hideDialog()
            }

        })
    }

    private fun getInformation(userAssessmentYearId: String, userId: String){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getInformation(userId, userAssessmentYearId)
        call.enqueue(object : Callback<NewItrBase> {
            override fun onResponse(
                call: Call<NewItrBase>?,
                response: Response<NewItrBase>?
            ) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        newItrBase.selectedUser_userAssessmentYearUserID = response.body().userAssessmentYearId?.toString() ?: response.body().userAssessmentYearId
                        newItrBase.firstName = response.body().firstName?.toString() ?: response.body().firstName
                        newItrBase.phoneNumber = response.body().phoneNumber?.toString() ?: response.body().phoneNumber
                        newItrBase.email = response.body().email?.toString() ?: response.body().email
                        newItrBase.dateOfBirth = response.body().dateOfBirth?.toString() ?: response.body().dateOfBirth
                        newItrBase.lastName = response.body().lastName?.toString() ?: response.body().lastName
                        newItrBase.userAssessmentYearId = response.body().userAssessmentYearId?.toString() ?: response.body().userAssessmentYearId
                        newItrBase.isSalary = response.body().isSalary?.toString() ?: response.body().isSalary
                        newItrBase.isHouseProperty = response.body().isHouseProperty?.toString() ?: response.body().isHouseProperty
                        newItrBase.isBusiness = response.body().isBusiness?.toString() ?: response.body().isBusiness
                        newItrBase.isCapitalGain = response.body().isCapitalGain?.toString() ?: response.body().isCapitalGain
                        newItrBase.isOtherSource = response.body().isOtherSource?.toString() ?: response.body().isOtherSource
                        newItrBase.isForeignIncome = response.body().isForeignIncome?.toString() ?: response.body().isForeignIncome
                        newItrBase.panNumber = response.body().panNumber?.toString() ?: response.body().panNumber
                        if (newItrBase.firstName == null || newItrBase.firstName == "null"){
                            newItrBase.firstName = ""
                        }
                        if (newItrBase.lastName == null || newItrBase.lastName == "null"){
                            newItrBase.lastName = ""
                        }
                        if (newItrBase.phoneNumber == null || newItrBase.phoneNumber == "null"){
                            newItrBase.phoneNumber = ""
                        }
                        if (newItrBase.email == null || newItrBase.email == "null"){
                            newItrBase.email = ""
                        }
                        if (newItrBase.panNumber == null || newItrBase.panNumber == "null"){
                            newItrBase.panNumber = ""
                        }
                        if (newItrBase.dateOfBirth == null || newItrBase.dateOfBirth == "null"){
                            newItrBase.dateOfBirth = ""
                        }
                        binding.firstNameField.setText(newItrBase.firstName)
                        binding.lastNameField.setText(newItrBase.lastName)
                        binding.mobileNumberField.setText(newItrBase.phoneNumber)
                        binding.emailField.setText(newItrBase.email)
                        binding.panNumberField.setText(newItrBase.panNumber)
                        binding.dobField.text = newItrBase.dateOfBirth
                    }
                    else {
                        dialog.hideDialog()
                    }
                }
                catch (e: Exception) {
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

    private fun postInformation(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        var personalInformationModel = NewItrBase(binding.firstNameField.text.toString(),
            "",
            binding.lastNameField.text.toString(),
            binding.panNumberField.text.toString(),
            "Android",
            binding.dobField.text.toString(),
            appPreferences!!.parentId,
            newItrBase.isSalary,
            newItrBase.isBusiness,
            newItrBase.isHouseProperty,
            newItrBase.isCapitalGain,
            newItrBase.isOtherSource,
            newItrBase.isForeignIncome,
            userAssessmentYearId,
            itrProcess,
            binding.emailField.text.toString(),
            binding.mobileNumberField.text.toString(),
            newItrBase.assessmentYearId,
            appPreferences!!.deviceTokenId,
            false, 0, 0, 0)

        val call = apI_Interface.postInformation(personalInformationModel)
        call.enqueue(object : Callback<NewItrBase> {

            override fun onResponse(
                call: Call<NewItrBase>?,
                response: Response<NewItrBase>?
            ) {
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        newItrBase.selectedUserEmail = binding.emailField.text.toString()
                        newItrBase.selectedUserMobile = binding.mobileNumberField.text.toString()
                        newItrBase.userName = binding.firstNameField.text.toString() + " " + binding.lastNameField.text.toString()
                        newItrBase.selectedUserName= binding.firstNameField.text.toString() + " " + binding.lastNameField.text.toString()

                        if (appPreferences!!.emailAddress == "" || appPreferences!!.emailAddress == null)
                            appPreferences!!.emailAddress = binding.emailField.text.toString()
                        if (appPreferences!!.userFirstName == null)
                            appPreferences!!.userFirstName = binding.firstNameField.text.toString()
                        if (appPreferences!!.userLastName == null)
                            appPreferences!!.userLastName = binding.lastNameField.text.toString()
                        newItrBase.selectedUser_userAssessmentYearUserID = response.body().UserAssestmentYearId?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.isNewUser = false
                        val intent = Intent(mContext, ReviseIntimationNoticeActivity::class.java)
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        startActivity(intent)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home){
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}
