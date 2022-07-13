package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.google.gson.Gson
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityEfilingPersonalInfoBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.FinancialYear
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class EFilingPersonalInfoActivity : AppCompatActivity() {

    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    var newItrBase = NewItrBase.instance
    var isNewUser = newItrBase.isNewUser
    lateinit var itrProcess: String
    var userAssessmentYearId: String? = null
    private var companyNameTypeList = mutableListOf<String>()
    private var companyIdTypeList = mutableListOf<String>()
    private lateinit var binding:ActivityEfilingPersonalInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEfilingPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(this)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text =getString(R.string.personal_info)
        Log.e("parent id --- ","" + appPreferences!!.parentId.toString())

        userAssessmentYearId = newItrBase.selectedUser_userAssessmentYearUserID.toString()
        itrProcess = CommonVal.SubmitDocument.name

        //sent after api call
//        setDataIntoFields()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            binding.imgUser.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        else
            ViewCompat.setBackgroundTintList(binding.imgUser, ColorStateList.valueOf(Color.rgb(0, 184, 148)))

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
                newItrBase.email = binding.emailField.text.toString()
                newItrBase.panNumber = binding.panNumberField.text.toString()
                newItrBase.dateOfBirth = binding.dobField.text.toString()

                newItrBase.areYouFilingSec139_1 = binding.switchAreYouFilingSec139.isChecked
                newItrBase.currentAccountDeposit = binding.txtCurrentAccountDeposit.text.toString().toIntOrNull()
                newItrBase.foreignTravel = binding.txtForeignTravel.text.toString().toIntOrNull()
                newItrBase.electricityConsumption = binding.txtElectricityConsumption.text.toString().toIntOrNull()
                if (isNewUser) {
                    userAssessmentYearId = null
                }
                if (ConnectionDetector().isConnectingToInternet(mContext))
                    postInformation()
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.dobField.setOnClickListener {
            DatePicker(mContext, binding.dobField,18).datePickerDialog()
        }

        binding.switchAreYouFilingSec139.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.hideOption.visibility = View.VISIBLE
            } else {
                binding.hideOption.visibility = View.GONE
            }
        }

        getFinancialYear()

        ButtonVisibility().hideButton(mContext, binding.activityManualPersonalInfo, binding.contButton)

    }

    private fun getFinancialYear() {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getFinancialAndAssesmentYear()
        call.enqueue(object : Callback<List<FinancialYear>> {

            override fun onResponse(call: Call<List<FinancialYear>>?, response: Response<List<FinancialYear>>?) {
                try {
                    if (response!!.isSuccessful) {
                        if (response.code() == 404 || response.code() == 400) {
                            dialog.hideDialog()
                            //if dont get data in api
                        } else {
                            dialog.hideDialog()
//                            responseList = response.body()

                            for(items in response.body()){
                                val value : String = items.financialYear + " | " + items.assestmentYear
                                companyNameTypeList.add(value)
//                                items.employerType?.let { companyNameTypeList.add(it) }
                                items.id?.let { companyIdTypeList.add(it) }
                            }

                            binding.chooseYearSpinner.adapter =
                                ArrayAdapter<String>(mContext, R.layout.spinner_item, companyNameTypeList)

                            val gson = Gson()
                            val jsonTutsList: String = gson.toJson(companyNameTypeList)
                            Log.e("111 asses year", "--" + jsonTutsList)

                            val jsonTutsList2: String = gson.toJson(companyIdTypeList)
                            Log.e("111 asses id year", "--" + jsonTutsList2)

                            setDataIntoFields()

                        }
                        dialog.hideDialog()

                    } else {
                        dialog.hideDialog()
                        if (response.code()==404 || response.code() == 400){
                            //if dont get data in api

                        }else{
                            Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<FinancialYear>>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(
                    mContext,
                    resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }



    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }


    private fun setDataIntoFields(){
        binding.textView13.setText(newItrBase.assestmentYear)   //new added
        binding.firstNameField.setText(newItrBase.firstName)
        binding.middleName.setText(newItrBase.middleName)
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

            //new added
            binding.switchAreYouFilingSec139.isChecked = newItrBase.areYouFilingSec139_1!!
            binding.switchAreYouFilingSec139.setText(newItrBase.currentAccountDeposit.toString())
            binding.switchAreYouFilingSec139.setText(newItrBase.foreignTravel.toString())
            binding.txtElectricityConsumption.setText(newItrBase.electricityConsumption.toString())

            if(newItrBase.areYouFilingSec139_1 == true)
                binding.hideOption.visibility = View.VISIBLE

            Log.e("111 selec asess year", "--" + newItrBase.assessmentYearId)
            try {
                //new added
                for (i in 0 until companyIdTypeList.size) {
                    var id = companyIdTypeList[i]
                    if (id.equals(newItrBase.assessmentYearId)) {
                        binding.chooseYearSpinner.setSelection(i)
                        break
                    }
                }
            } catch (e: Exception) {
                // Must be safe
            }


        }

    }

    private fun postInformation(){              //changes in model due to addition
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
        //new added
        var personalInformationModel = NewItrBase(binding.firstNameField.text.toString(),
            binding.middleName.text.toString(),
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
            companyIdTypeList[binding.chooseYearSpinner.selectedItemPosition],
            appPreferences!!.deviceTokenId,
            newItrBase.areYouFilingSec139_1,
            newItrBase.currentAccountDeposit,
            newItrBase.foreignTravel,
            newItrBase.electricityConsumption)

        val gson = Gson()
        val jsonTutsList: String = gson.toJson(personalInformationModel)
        Log.e("111 request itr create ", "prefiled -- " + jsonTutsList)

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
                        newItrBase.selectedUser_userAssessmentYearUserID = response.body().UserAssestmentYearId?.takeUnless {it.isEmpty()} ?: ""
                        newItrBase.isNewUser = false
                        newItrBase.isNewItr = false  // new added
                        userAssessmentYearId = newItrBase.selectedUser_userAssessmentYearUserID  // new added
                        if (newItrBase.isSalary == "true") {
                            val intent = Intent(mContext, UploadForm16Activity::class.java)
//                            val intent = Intent(mContext, NewUploadfom16Activity::class.java)
                            newItrBase.flagInfo = true
                            startActivity(intent)
                        }
                        else {
                            val intent = Intent(mContext, ImportantDetailsActivity::class.java)
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                            newItrBase.flagInfo = true
                            startActivity(intent)
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
