package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityItrprocessTutorialBinding
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.MyContextWrapper

class ITRProcessTutorialActivity : AppCompatActivity() {

    lateinit var mContext: Context

    private var appPreferences: AppPreferences? = null
    private lateinit var binding:ActivityItrprocessTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItrprocessTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = getString(R.string.title_tutorials)

        mContext = this

        binding.skipButton.setOnClickListener {
            val intent = Intent(applicationContext, SourceOfIncomeActivity::class.java)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
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
