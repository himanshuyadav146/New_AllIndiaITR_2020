package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityOtherIncomeBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherIncomeActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var appPreferences: AppPreferences
    var newItrBase = NewItrBase.instance
    var responseCode : Int = 0
    lateinit var binding:ActivityOtherIncomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOtherIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        appPreferences = AppPreferences(mContext)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = getString(R.string.title_otherincome)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgUser.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgForms.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgDetails.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgBank.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgDoc.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgUser, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgForms, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgDetails, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgBank, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgDoc, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
        }

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getOtherIncome()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()

        binding.contButton.setOnClickListener {
            var interestIncomeFromSaving : String = if (binding.interestIncomeField.text.toString().isEmpty())
                "0.0"
            else
                binding.interestIncomeField.text.toString()

            var otherInterestIncome : String = if (binding.interestOnRdFdField.text.toString().isEmpty())
                "0.0"
            else
                binding.interestOnRdFdField.text.toString()

            var otherThanSalaryIncome : String = if (binding.anyOtherIncomeField.text.toString().isEmpty())
                "0.0"
            else
                binding.anyOtherIncomeField.text.toString()

            newItrBase.interestIncomeFromSaving = interestIncomeFromSaving.toDouble()
            newItrBase.otherInterestIncome = otherInterestIncome.toDouble()
            newItrBase.otherThanSalaryIncome = otherThanSalaryIncome.toDouble()

            if (ConnectionDetector().isConnectingToInternet(mContext)){
                if (responseCode == 200)
                    putOtherIncome()
                else if (responseCode == 404)
                    postOtherIncome()
            }
            else
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }



    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }


    private fun getOtherIncome() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getOtherIncome(newItrBase.selectedUser_userAssessmentYearUserID.toString())
        call.enqueue(object : Callback<NewItrBase> {

            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        responseCode = 200
                        val interestIncomeFromSaving = response.body().interestIncomeFromSaving?.toInt()
                        val otherInterestIncome = response.body().otherInterestIncome?.toInt()
                        val otherThanSalaryIncome = response.body().otherThanSalaryIncome?.toInt()

                        if (interestIncomeFromSaving == 0)
                            binding.interestIncomeField.setText("")
                        else
                            binding.interestIncomeField.setText(interestIncomeFromSaving.toString())

                        if (otherInterestIncome == 0)
                            binding.interestOnRdFdField.setText("")
                        else
                            binding.interestOnRdFdField.setText(otherInterestIncome.toString())

                        if (otherThanSalaryIncome == 0)
                            binding.anyOtherIncomeField.setText("")
                        else
                            binding.anyOtherIncomeField.setText(otherThanSalaryIncome.toString())
                    }
                    else {
                        dialog.hideDialog()
                        responseCode = 404
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

    private fun postOtherIncome() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.postOtherIncome(newItrBase)
        call.enqueue(object : Callback<Void>{

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        navigation()
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

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun putOtherIncome() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.putOtherIncome(newItrBase.selectedUser_userAssessmentYearUserID.toString(), newItrBase)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        navigation()
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

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigation(){
        if (newItrBase.isHouseProperty == "true" || newItrBase.isForeignIncome == "true"
            || newItrBase.isCapitalGain == "true" || newItrBase.isBusiness == "true") {
            val intent = Intent(mContext, MiscellaneousIncomeActivity::class.java)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
        }
        else {
            val intent = Intent(mContext, AnyOtherDocumentActivity::class.java)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
        }
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
