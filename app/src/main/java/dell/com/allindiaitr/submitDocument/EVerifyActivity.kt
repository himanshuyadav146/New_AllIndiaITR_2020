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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityEverifyBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.MyContextWrapper

class EVerifyActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    var newItrBase = NewItrBase.instance
    lateinit var appPreferences: AppPreferences
    private lateinit var binding: ActivityEverifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEverifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(mContext)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = getString(R.string.title_e_verify)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            binding.btnProceed.backgroundTintList = mContext.getColorStateList(R.color.light_blue)
        else
            ViewCompat.setBackgroundTintList(binding.btnProceed, ColorStateList.valueOf(Color.rgb(0,238,231)))

        binding.btnProceed.setOnClickListener {
            val intent = Intent(mContext, PaymentActivity::class.java)
            newItrBase.processMode = CommonVal.EVerify.name
            newItrBase.selectedProcessStatus = CommonVal.InProgress.name
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            finish()
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

    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}
