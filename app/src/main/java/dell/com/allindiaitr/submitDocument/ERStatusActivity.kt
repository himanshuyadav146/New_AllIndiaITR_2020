package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityErstatusBinding
import dell.com.allindiaitr.home.AllOrdersActivity
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.MyContextWrapper

class ERStatusActivity : AppCompatActivity() {

    var newItrBase = NewItrBase.instance
    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    private lateinit var binding:ActivityErstatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityErstatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mContext = this

        if (newItrBase.processMode.equals(CommonVal.RevisedReturn.name)){
            binding.toolbar.toolbarTitle.text = ""
            if (newItrBase.selectedProcessStatus.equals(CommonVal.InProgress.name)){
                binding.statementTextView.text = getString(R.string.title_payment_rec_successfull)
                binding.descriptionTextView.text =getString(R.string.title_payment_rec_successfull2)
            }
            else if (newItrBase.selectedProcessStatus.equals(CommonVal.Complete.name)){
                binding.statementTextView.text =getString(R.string.title_order_complete)
                binding.descriptionTextView.text = getString(R.string.title_r_notice_success)
            }
        }
        else if (newItrBase.processMode.equals(CommonVal.EVerify.name)){
            binding.toolbar.toolbarTitle.text = getString(R.string.title_e_verify_status)
            if (newItrBase.selectedProcessStatus.equals(CommonVal.InProgress.name)){
                binding.statementTextView.text =getString(R.string.title_p_received_success)
                binding.descriptionTextView.text =getString(R.string.title_r_received_success)
            }
            else if (newItrBase.selectedProcessStatus.equals(CommonVal.Complete.name)){
                binding.statementTextView.text = getString(R.string.title_order_complete)
                binding.descriptionTextView.text =getString(R.string.title_itr_success)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home){
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            if (newItrBase.orderMode.equals("AllOrders")) {
                val intent =Intent(applicationContext, AllOrdersActivity::class.java)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                startActivity(intent)
                finish()
            }
            else {
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (newItrBase.orderMode.equals("AllOrders")) {
            val intent =Intent(applicationContext, AllOrdersActivity::class.java)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            startActivity(intent)
            finish()
        }
        else {
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            super.onBackPressed()
        }
    }
}
