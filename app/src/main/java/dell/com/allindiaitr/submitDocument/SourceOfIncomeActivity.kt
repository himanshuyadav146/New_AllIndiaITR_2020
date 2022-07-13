package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.SourceOfIncomeAdapter
import dell.com.allindiaitr.databinding.ActivityPaymentBinding
import dell.com.allindiaitr.databinding.ActivitySourceOfIncomeBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.ITROption_Model
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SourceOfIncomeActivity : AppCompatActivity() {

    var mModelList = mutableListOf<ITROption_Model>()
    lateinit var itroptions:List<String>

    var card_img = listOf<Int>(R.drawable.ic_salaries, R.drawable.ic_house_property, R.drawable.ic_business,
        R.drawable.ic_capital_gains, R.drawable.ic_other_sources, R.drawable.ic_foreign_income)
    lateinit var userAssessmentYearId: String
    lateinit var userId: String
    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    var newItrBase = NewItrBase.instance
    private lateinit var binding: ActivitySourceOfIncomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourceOfIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(this)

       // setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text =getString(R.string.title_select_incomesource)

        itroptions = listOf<String>(getString(R.string.sourceof_income_salary), getString(R.string.sourceof_income_house)
            , getString(R.string.sourceof_income_bussiness),
        getString(R.string.sourceof_income_capital), getString(R.string.sourceof_income_other_source), getString(R.string.sourceof_income_foreign))

        userAssessmentYearId = newItrBase.selectedUser_userAssessmentYearUserID.toString()
        userId = appPreferences!!.parentId.toString()

        binding.recyclerViewList.setHasFixedSize(true)
        binding.recyclerViewList.layoutManager = GridLayoutManager(mContext, 2)
        binding.recyclerViewList.adapter = SourceOfIncomeAdapter(this, getListData())

        var sessionId = newItrBase.selectedUserId
        var isNewUser = newItrBase.isNewUser

        if (sessionId != null) {
            userId = sessionId
        }
        if (isNewUser) {
            binding.recyclerViewList.adapter = SourceOfIncomeAdapter(this, getListData())
            newItrBase.setInstanceEmpty()
        }
        else {
            if (userAssessmentYearId == "") {
                binding.recyclerViewList.adapter = SourceOfIncomeAdapter(this, getListData())
                if (ConnectionDetector().isConnectingToInternet(mContext)){
                    getInformation(userAssessmentYearId, userId)
                }
                else {
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                if (ConnectionDetector().isConnectingToInternet(mContext)){
                    getInformation(userAssessmentYearId, userId)
                }
                else {
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.contButton.setOnClickListener {
            if (!mModelList[0].isSelected && !mModelList[1].isSelected && !mModelList[2].isSelected &&
                !mModelList[3].isSelected && !mModelList[4].isSelected && !mModelList[5].isSelected)
                Toast.makeText(mContext, "Please select any one option", Toast.LENGTH_SHORT).show()
            if (mModelList[0].isSelected)
                newItrBase.isSalary = "true"
            else
                newItrBase.isSalary = "false"
            if (mModelList[1].isSelected)
                newItrBase.isHouseProperty = "true"
            else
                newItrBase.isHouseProperty = "false"
            if (mModelList[2].isSelected)
                newItrBase.isBusiness = "true"
            else
                newItrBase.isBusiness = "false"
            if (mModelList[3].isSelected)
                newItrBase.isCapitalGain = "true"
            else
                newItrBase.isCapitalGain = "false"
            if (mModelList[4].isSelected)
                newItrBase.isOtherSource = "true"
            else
                newItrBase.isOtherSource = "false"
            if (mModelList[5].isSelected)
                newItrBase.isForeignIncome = "true"
            else
                newItrBase.isForeignIncome = "false"
            if (mModelList[0].isSelected || mModelList[1].isSelected || mModelList[2].isSelected ||
                mModelList[3].isSelected || mModelList[4].isSelected || mModelList[5].isSelected) {
                val intent = Intent(mContext, EFilingPersonalInfoActivity::class.java)
               // overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent)
            }
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

    private fun getListData(): List<ITROption_Model> {
        mModelList.clear()
        for (i in 0 until itroptions.size){
            mModelList.add(ITROption_Model(itroptions[i], card_img[i]))
        }
        return mModelList
    }

    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }

    private fun getInformation(userAssessmentYearId: String, userId: String) {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getInformation(userId, userAssessmentYearId)
        call.enqueue(object : Callback<NewItrBase> {

            override fun onResponse(
                call: Call<NewItrBase>?,
                response: Response<NewItrBase>?
            ) {
                try {
                    val gson = Gson()
                    val jsonTutsList: String = gson.toJson(response)
                    Log.e("111 get itr data ", "prefiled -- " + jsonTutsList)

                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        newItrBase.firstName =
                            response.body().firstName?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.phoneNumber =
                            response.body().phoneNumber?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.email = response.body().email?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.dateOfBirth =
                            response.body().dateOfBirth?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.middleName =
                            response.body().middleName?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.lastName =
                            response.body().lastName?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.userAssessmentYearId =
                            response.body().userAssessmentYearId?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.isSalary =
                            response.body().isSalary?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.isHouseProperty =
                            response.body().isHouseProperty?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.isBusiness =
                            response.body().isBusiness?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.isCapitalGain =
                            response.body().isCapitalGain?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.isOtherSource =
                            response.body().isOtherSource?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.isForeignIncome =
                            response.body().isForeignIncome?.takeUnless { it.isEmpty() } ?: ""
                        newItrBase.panNumber =
                            response.body().panNumber?.takeUnless { it.isEmpty() } ?: ""


                        newItrBase.assessmentYearId =
                            response.body().assestmentYearId?.takeUnless { it.isEmpty() } ?: ""                //new added
                        newItrBase.areYouFilingSec139_1 =
                            response.body().areYouFilingSec139_1?.takeUnless { it.equals(null)} ?: false
                        newItrBase.currentAccountDeposit =
                            response.body().currentAccountDeposit?.takeUnless { it.equals(null) } ?: 0
                        newItrBase.foreignTravel =
                            response.body().foreignTravel?.takeUnless { it.equals(null) } ?: 0
                        newItrBase.electricityConsumption =
                            response.body().electricityConsumption?.takeUnless { it.equals(null) } ?: 0

                        binding.recyclerViewList.adapter =
                            SourceOfIncomeAdapter(mContext, mModelList, newItrBase)
                    } else {
                        dialog.hideDialog()
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

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(
                    mContext,
                    resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}