package dell.com.allindiaitr.revisedReturn

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
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.ReviseListAdapter
import dell.com.allindiaitr.databinding.ActivityLogInBinding
import dell.com.allindiaitr.databinding.ActivityReviseListBinding
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.MyContextWrapper

class ReviseListActivity : AppCompatActivity() {

    lateinit var mContext: Context
    var newItrBase = NewItrBase.instance
    private var appPreferences: AppPreferences? = null
    private lateinit var binding:ActivityReviseListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviseListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this

//        setSupportActionBar(binding.toolbar)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbarTitle.text = getString(R.string.title_reply_to_notice)

        binding.startNewFilingButton.setOnClickListener {
            newItrBase.selectedUser_userAssessmentYearUserID = null
            newItrBase.isNewUser = true
            val intent = Intent(mContext, RevisePersonalInfoActivity::class.java)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.orText.backgroundTintList = mContext.getColorStateList(R.color.white)
            binding.startNewFilingButton.backgroundTintList = mContext.getColorStateList(R.color.white)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.orText, ColorStateList.valueOf(Color.WHITE))
            ViewCompat.setBackgroundTintList(binding.startNewFilingButton, ColorStateList.valueOf(Color.WHITE))
        }

        binding.recyclerViewList.setHasFixedSize(true)
        binding.recyclerViewList.layoutManager = LinearLayoutManager(mContext)
        binding.recyclerViewList.adapter = ReviseListAdapter(mContext)
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
