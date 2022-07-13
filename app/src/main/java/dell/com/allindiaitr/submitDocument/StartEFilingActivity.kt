package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.StartEFilingAdapter
import dell.com.allindiaitr.databinding.ActivityStartEfilingBinding
import dell.com.allindiaitr.home.DashboardActivity
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.MyContextWrapper

class StartEFilingActivity : AppCompatActivity() {

    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    private lateinit var binding:ActivityStartEfilingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartEfilingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = getString(R.string.title_choose_services)

        var heading = listOf<String>(getString(R.string.dashboard_starte_filing),
           getString(R.string.dashboard_replytonotice))
        var description = listOf<String>(getString(R.string.dashboard_sub_starte_filing),
            getString(R.string.dashboard_sub_replytonotice))
        var images = listOf<Int>(R.drawable.ic_start_e_filing, R.drawable.ic_reply_to_notice)

        binding.recyclerView.layoutManager = LinearLayoutManager(mContext)
        binding.recyclerView.adapter = StartEFilingAdapter(mContext, heading, description, images)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home){
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            val intent = Intent(mContext, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivityForResult(intent, 0)
            overridePendingTransition(0, 0)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }


    override fun onBackPressed() {
        val intent = Intent(mContext, DashboardActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        startActivityForResult(intent, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}
