package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityAddBankDetailsBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AddBankDetailsActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var appPreferences: AppPreferences
    var newItrBase = NewItrBase.instance
    var selection = newItrBase.isAddBank
    var typeOfBankAccount = arrayOf("Saving Account", "Current Account")
    var accountTypeFlag = "2"
    lateinit var accType : String
    lateinit var binding:ActivityAddBankDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        appPreferences = AppPreferences(mContext)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text =getString(R.string.title_bankdetials)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgUser.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgForms.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgDetails.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgBank.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgUser, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgForms, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgDetails, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgBank, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
        }

        binding.primaryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            accountTypeFlag = if (isChecked) {
                "1"
            } else {
                "2"
            }
        }
       //

        binding.accTypeSpinner.adapter = ArrayAdapter<String>(mContext, R.layout.spinner_item, typeOfBankAccount)
        binding.accTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(mContext, "Select your Bank Type", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                accType = typeOfBankAccount[position].toString()
            }
        }

        if (selection) {
            binding.primaryCheckBox.isChecked = true
        }
        else {
            binding.primaryCheckBox.isChecked = false
            if (ConnectionDetector().isConnectingToInternet(mContext))
                getBankDetails()
            else
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        }

        ButtonVisibility().hideButton(mContext, binding.activityAddbankDetails, binding.calculateButton)

        binding.calculateButton.setOnClickListener {
            if (Validation().isBankNameValid(binding.bankNameFeild.text.toString(), binding.bankNameFeild) &&
                Validation().isIfscValid(binding.ifscCodeField.text.toString(), binding.ifscCodeField) &&
                Validation().isBankAccountNumberValid(binding.accNumberField.text.toString(), binding.accNumberField)) {
                binding.calculateButton.setEnabled(false)
                newItrBase.bankAccountNumber = binding.accNumberField.text.toString()
                newItrBase.nameOfYourBank = binding.bankNameFeild.text.toString()
                newItrBase.iFSCCode = binding.ifscCodeField.text.toString()
                newItrBase.typeOfBankAccount = accType.toString()

                if (selection){
                    if (ConnectionDetector().isConnectingToInternet(mContext))
                        postBankDetails()
                    else
                        Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (ConnectionDetector().isConnectingToInternet(mContext))
                        putBankDetails()
                    else
                        Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
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


    private fun getBankDetails(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
        val call = apI_Interface.getBankDetail(newItrBase.bankInfoId.toString())
        call.enqueue(object : Callback<NewItrBase>{

            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        binding.bankNameFeild.setText(response.body().nameOfYourBank?.takeUnless {it.isEmpty()} ?: "")
                        binding.ifscCodeField.setText(response.body().iFSCCode?.takeUnless {it.isEmpty()} ?: "")
                        binding.accNumberField.setText(response.body().bankAccountNumber?.takeUnless {it.isEmpty()} ?: "")
                        if (response.body().bankAccountTypeFlag == "1")
                            binding.primaryCheckBox.isChecked = true
                        for (i in 0 until typeOfBankAccount.size){
                            if (binding.accTypeSpinner.getItemAtPosition(i).toString() == response.body().typeOfBankAccount?.takeUnless {it.isEmpty()} ?: "") {
                                binding.accTypeSpinner.setSelection(i)
                                break
                            }
                        }
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

    private fun postBankDetails() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        if (!newItrBase.isAddBank && (newItrBase.bankSize == 0 || newItrBase.bankSize == 1))
            accountTypeFlag = "1"

        newItrBase.bankAccountTypeFlag = accountTypeFlag.toString()
        newItrBase.UserAssestmentYearId = newItrBase.selectedUser_userAssessmentYearUserID
        val call = apI_Interface.postBankDetails(newItrBase)
        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                try {
                    binding.calculateButton.setEnabled(true)
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        val intent = Intent(mContext, BankListActivity::class.java)
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        dialog.hideDialog()
                        var jsonObject = JSONObject(response.errorBody().string())
                        var jsonArray = JSONArray(jsonObject.getString("Message"))
                        AlertDialogueManager().errorMessageDialogue(mContext, jsonArray[0].toString(), "Message")
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                dialog.hideDialog()
                binding.calculateButton.setEnabled(true)
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun putBankDetails() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        if (!newItrBase.isAddBank && (newItrBase.bankSize == 0 || newItrBase.bankSize == 1))
            accountTypeFlag = "1"

        newItrBase.bankAccountTypeFlag = accountTypeFlag.toString()

        val call = apI_Interface.putBankDetails(newItrBase.bankInfoId.toString(), newItrBase)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        val intent = Intent(mContext, BankListActivity::class.java)
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        dialog.hideDialog()
                        val intent = Intent(mContext, BankListActivity::class.java)
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                        startActivity(intent)
                        finish()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
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
            if (selection) {
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
            }
            else {
                val intent = Intent(mContext, BankListActivity::class.java)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (selection) {
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            super.onBackPressed()
        }
        else {
            val intent = Intent(mContext, BankListActivity::class.java)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            startActivity(intent)
            finish()
        }
    }
}
