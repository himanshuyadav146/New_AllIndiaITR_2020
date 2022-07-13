//package dell.com.allindiaitr.usefultools
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.view.MenuItem
//import android.view.View
//import android.view.inputmethod.InputMethodManager
//import android.widget.RadioButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.interfaces.API_Interface
//import dell.com.allindiaitr.models.UsefulToolsModel
//import dell.com.allindiaitr.utils.*
//import kotlinx.android.synthetic.main.activity_house_rent_calculator.*
//import kotlinx.android.synthetic.main.app_toolbar.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.lang.Exception
//import kotlin.math.min
//
//class HouseRentCalculatorActivity : AppCompatActivity() {
//
//    lateinit var apI_Interface : API_Interface
//    lateinit var mContext: Context
//    var isValid: Boolean = true
//    var percentageBasicSalary = "50%"
//    var basicSalaryInt = 0
//    var daReceivedInt = 0
//    var hraReceivedInt = 0
//    var totalRentPaidInt = 0
//    var exemptHraInt = 0
//    var hraChargeableTaxInt = 0
//    var usefulToolsModel = UsefulToolsModel.instance
//    private var appPreferences: AppPreferences? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_house_rent_calculator)
//
//        mContext = this
//        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
//
//        setSupportActionBar(toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar_title.text =getString(R.string.title_hra_cal)
//
//        calculate_button.setOnClickListener {
//            basicSalaryInt = 0
//            daReceivedInt = 0
//            hraReceivedInt = 0
//            totalRentPaidInt = 0
//            exemptHraInt = 0
//
//            if (Validation().isFieldValid(basicSalaryReceivedField.text.toString(), basicSalaryReceivedField) &&
//                Validation().isFieldValid(hraReceivedField.text.toString(), hraReceivedField) &&
//                Validation().isFieldValid(totalRentPaidField.text.toString(), totalRentPaidField) &&
//                    Validation().isEmailValid(emailIdField.text.toString(), emailIdField)) {
//
//                daReceivedInt = if (daReceivedField.text.toString().isEmpty())
//                    0
//                else
//                    daReceivedField.text.toString().toInt()
//
//                basicSalaryInt = basicSalaryReceivedField.text.toString().toInt()
//                hraReceivedInt = hraReceivedField.text.toString().toInt()
//                totalRentPaidInt = totalRentPaidField.text.toString().toInt()
//
//                basicSalaryInt = if (percentageBasicSalary == "50%")
//                    (0.5 * (basicSalaryInt + daReceivedInt)).toInt()
//                else
//                    (0.4 * (basicSalaryInt + daReceivedInt)).toInt()
//
//                totalRentPaidInt = (totalRentPaidInt - (0.1 * (basicSalaryInt + daReceivedInt))).toInt()
//                exemptHraInt = min(min(basicSalaryInt, totalRentPaidInt), hraReceivedInt)
//
//                if (exemptHraInt < 0)
//                    exemptHraInt = 0
//
//                hraChargeableTaxInt = hraReceivedInt - exemptHraInt
//
//                if (ConnectionDetector().isConnectingToInternet(mContext)) {
//                    usefulToolsModel.basicSalary = basicSalaryInt
//                    usefulToolsModel.dearnessAllowance = daReceivedInt
//                    usefulToolsModel.hra = hraReceivedInt
//                    usefulToolsModel.rentPaid = totalRentPaidInt
//                    usefulToolsModel.isMetroCity = isValid
//                    usefulToolsModel.email = emailIdField.text.toString()
//                    usefulToolsModel.hraExemption = exemptHraInt.toString()
//                    usefulToolsModel.hraChargeableTax = hraChargeableTaxInt
//                    usefulToolsModel.percentage = percentageBasicSalary
//                    postHraCalculation()
//                }
//                else
//                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        warning_image.setOnClickListener {
//            AlertDialogueManager().errorMessageDialogue(mContext, "DA - This allowance is given to government and public sector employees and pensioners", "Message")
//        }
//    }
//
//
//    override fun attachBaseContext(newBase:Context?){
//        mContext= newBase!!
//        appPreferences= AppPreferences(newBase!!)
//        val lang:String=appPreferences?.selectedLanguage!!
//        //  setAppLocal(lang)
//        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))
//
//    }
//
//
//    fun radioSelections(view: View) {
//        var checkedId = view as RadioButton
//        when(checkedId) {
//            yesRadioButton -> {
//                noRadioButton.isChecked = false
//                isValid = true
//                percentageBasicSalary = "50%"
//            }
//            noRadioButton -> {
//                yesRadioButton.isChecked = false
//                isValid = false
//                percentageBasicSalary = "40%"
//            }
//
//        }
//    }
//
//    private fun postHraCalculation() {
//        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
//
//        val call = apI_Interface.hra_calculation(usefulToolsModel)
//        call.enqueue(object : Callback<Void> {
//
//            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
//                try {
//                    if (response!!.isSuccessful) {
//                        dialog.hideDialog()
//                        val intent = Intent(mContext, HraCalculatorResultActivity::class.java)
//                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
//                        startActivity(intent)
//                    }
//                    else {
//                        dialog.hideDialog()
//                    }
//                }
//                catch (e: Exception) {
//                    dialog.hideDialog()
//                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<Void>?, t: Throwable?) {
//                dialog.hideDialog()
//                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        var id = item!!.itemId
//        if (id == android.R.id.home){
//            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
//            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//            finish()
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//    }
//
//}
