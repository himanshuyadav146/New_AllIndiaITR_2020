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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.BankListAdapter
import dell.com.allindiaitr.databinding.ActivityBankListBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankListActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var appPreferences: AppPreferences
    var newItrBase = NewItrBase.instance
    private lateinit var binding:ActivityBankListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        appPreferences = AppPreferences(mContext)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getBankList()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()

        binding.addBankButton.setOnClickListener {
            val intent = Intent(mContext, AddBankDetailsActivity::class.java)
            newItrBase.isAddBank = true
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
            finish()
        }

        binding.contButton.setOnClickListener {
            if (binding.recyclerView.childCount == 0){
                Toast.makeText(mContext, "Add your Bank Details", Toast.LENGTH_SHORT).show()
            }else{
                if (ConnectionDetector().isConnectingToInternet(mContext))
                navigation()
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


    private fun getBankList() {
        var bankName = arrayListOf<String>()
        var accountNumber = arrayListOf<String>()
        var bankId = arrayListOf<String>()
        var bankTypeFlag = arrayListOf<String>()

        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getBankList(newItrBase.selectedUser_userAssessmentYearUserID.toString())
        call.enqueue(object : Callback<List<NewItrBase>> {

            override fun onResponse(call: Call<List<NewItrBase>>?, response: Response<List<NewItrBase>>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        newItrBase.bankSize = response.body().size
                        if (response.body().isEmpty()) {
                            val intent = Intent(mContext, AddBankDetailsActivity::class.java)
                            newItrBase.isAddBank = true
                            finish()
                            startActivityForResult(intent, 0)
                            overridePendingTransition(0, 0)
                        }
                        else {
                            binding.addBankButton.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.contButton.visibility = View.VISIBLE
                            binding.toolbar.toolbarTitle.text  =getString(R.string.title_bankdetialstitle)
                            if (response.body() != null) {
                                for (i in 0 until response.body().size) {
                                    bankName.add(response.body()[i].nameOfYourBank?.takeUnless {it.isEmpty()} ?: "")
                                    accountNumber.add(response.body()[i].bankAccountNumber?.takeUnless {it.isEmpty()} ?: "")
                                    bankId.add(response.body()[i].id?.takeUnless {it.isEmpty()} ?: "")
                                    bankTypeFlag.add(response.body()[i].bankAccountTypeFlag?.takeUnless {it.isEmpty()} ?: "")
                                }
                                binding.recyclerView.adapter = BankListAdapter(mContext, bankName, accountNumber, bankId, bankTypeFlag)
                                binding.recyclerView.setHasFixedSize(true)
                                binding.recyclerView.layoutManager = LinearLayoutManager(mContext)
                            }
                        }
                    }
                    else {
                        dialog.hideDialog()
                        val intent = Intent(mContext, AddBankDetailsActivity::class.java)
                        newItrBase.isAddBank = true
                        finish()
                        startActivityForResult(intent, 0)
                        overridePendingTransition(0, 0)
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NewItrBase>>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun navigation(){
        if (newItrBase.isOtherSource == "true") {
            val intent = Intent(mContext, OtherIncomeActivity::class.java)
//            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
        }
        else if (newItrBase.isHouseProperty == "true" || newItrBase.isForeignIncome == "true"
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
