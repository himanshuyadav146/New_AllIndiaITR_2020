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
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityMiscellaneousIncomeBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MiscellaneousIncomeActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var appPreferences: AppPreferences
    var newItrBase = NewItrBase.instance
    var businessRangeIdString = "E2C3F117-9AC1-4A93-915D-338B873AD56D"
    private lateinit var binding:ActivityMiscellaneousIncomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiscellaneousIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        appPreferences = AppPreferences(mContext)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = getString(R.string.title_miscellaneous)

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

        setSelectedSourceOfIncome()

        binding.contButton.setOnClickListener {
            if (ConnectionDetector().isConnectingToInternet(mContext)) {
                if (newItrBase.isBusiness == "true") {
                    postBusinessRange()
                }
                else {
                    val intent = Intent(mContext, AnyOtherDocumentActivity::class.java)
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    startActivity(intent)
                }
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

    private fun setSelectedSourceOfIncome() {
        if (newItrBase.isSalary == "true")
            binding.salaryCheckBox.visibility = View.VISIBLE
        else
            binding.salaryCheckBox.visibility = View.GONE

        if (newItrBase.isHouseProperty == "true")
            binding.housePropertyCheckBox.visibility = View.VISIBLE
        else
            binding.housePropertyCheckBox.visibility = View.GONE

        if (newItrBase.isBusiness == "true") {
            binding.businessCheckBox.visibility = View.VISIBLE
            binding.textViewHeading.visibility = View.VISIBLE
            binding.radioGroup.visibility = View.VISIBLE
        }
        else {
            binding.businessCheckBox.visibility = View.GONE
            binding.textViewHeading.visibility = View.GONE
            binding.radioGroup.visibility = View.GONE
        }

        if (newItrBase.isCapitalGain == "true")
            binding.capitalGainsCheckBox.visibility = View.VISIBLE
        else
            binding.capitalGainsCheckBox.visibility = View.GONE

        if (newItrBase.isOtherSource == "true")
            binding.otherCheckBox.visibility = View.VISIBLE
        else
            binding.otherCheckBox.visibility = View.GONE

        if (newItrBase.isForeignIncome == "true")
            binding.foreignCheckBox.visibility = View.VISIBLE
        else
            binding.foreignCheckBox.visibility = View.GONE
    }

    fun radioSelections(view: View) {
        var checkedId = view as RadioButton
            when(checkedId) {
                binding.rbtn10lakhs -> {
                    binding.rbtn10to25lakhs.isChecked = false
                    binding.rbtn25to50lakhs.isChecked = false
                    binding.rbtn50pluslakhs.isChecked = false
                    businessRangeIdString = "E2C3F117-9AC1-4A93-915D-338B873AD56D"
                }
                binding.rbtn10to25lakhs -> {
                    binding.rbtn10lakhs.isChecked = false
                    binding.rbtn25to50lakhs.isChecked = false
                    binding.rbtn50pluslakhs.isChecked = false
                    businessRangeIdString = "7E5004A1-A345-4779-A16B-82BAC0A165C1"
                }
                binding.rbtn25to50lakhs -> {
                    binding.rbtn10lakhs.isChecked = false
                    binding.rbtn10to25lakhs.isChecked = false
                    binding.rbtn50pluslakhs.isChecked = false
                    businessRangeIdString = "7C0C95A1-DAAE-4468-98C0-A45AFA44EDA6"
                }
                binding.rbtn50pluslakhs -> {
                    binding.rbtn10lakhs.isChecked = false
                    binding.rbtn10to25lakhs.isChecked = false
                    binding.rbtn25to50lakhs.isChecked = false
                    businessRangeIdString = "ACABB236-5BDB-4524-905C-A6C953F6F7F0"
                }
            }
    }

    private fun postBusinessRange() {
        val dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        newItrBase.businessRangeId = businessRangeIdString

        val call = apI_Interface.postBusinessRange(newItrBase)
        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        val intent = Intent(mContext, AnyOtherDocumentActivity::class.java)
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        startActivity(intent)
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
