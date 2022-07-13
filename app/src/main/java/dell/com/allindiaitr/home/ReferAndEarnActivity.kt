package dell.com.allindiaitr.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityReferAndEarnBinding
import dell.com.allindiaitr.interfaces.FragmentCommunicator
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.ConnectionDetector
import dell.com.allindiaitr.utils.MyContextWrapper


class ReferAndEarnActivity : AppCompatActivity(), FragmentCommunicator {


    lateinit var mContext: Context
    var newItrBase = NewItrBase.instance
     var pos:Int=-1
    lateinit var viewPager: ViewPager
    var isWithdrawalFragmentClicked:Boolean=false
    private var appPreferences: AppPreferences? = null
    lateinit var binding:ActivityReferAndEarnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityReferAndEarnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarTitle.text =getString(R.string.title_refer)
        mContext = this
        binding.bottomNavigationView.selectedItemId = R.id.navigation_refer

        var menuView: BottomNavigationMenuView = binding.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount){
            var iconView = menuView.getChildAt(i).findViewById<View>(R.id.icon)
            if (iconView!=null){
                var layoutParams = iconView.layoutParams
                var displayMetrics = resources.displayMetrics
                layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18F, displayMetrics).toInt()
                layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18F, displayMetrics).toInt()
                iconView.layoutParams = layoutParams
            }

        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            if (!ConnectionDetector().isConnectingToInternet(mContext)){
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                return@setOnNavigationItemSelectedListener false
            }
            when (it.itemId) {
                R.id.navigation_home -> {
                    newItrBase.orderMode = "Orders"
                    val intent = Intent(mContext, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_orders -> {
                    newItrBase.orderMode = "AllOrders"
                    val intent = Intent(mContext, AllOrdersActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_refer -> return@setOnNavigationItemSelectedListener false
                R.id.navigation_more-> {
                    newItrBase.orderMode = "Orders"
                    val intent = Intent(mContext, MoreActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        setupViewPager(binding.viewpager!!)
        binding.tabs!!.setupWithViewPager(binding.viewpager)

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
               // Toast.makeText(mContext,"Hello From onPageScrollStateChanged",Toast.LENGTH_LONG).show()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
              //  Toast.makeText(mContext,"Hello From onPageScrolled",Toast.LENGTH_LONG).show()
                pos=position
            }
            override fun onPageSelected(position: Int) {
               // Toast.makeText(mContext,"Hello From onPageSelected",Toast.LENGTH_LONG).show()
                pos=position
                if(pos==2)
                {
                    isWithdrawalFragmentClicked=true
                }else if(pos==0)
                {
                    isWithdrawalFragmentClicked=false
                }

            }

        })
    }


    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }

    override fun onBackPressed() {
        val intent = Intent(mContext, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivityForResult(intent, 0)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.navigation_refer
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ReferFragment(), getString(R.string.title_referral))
        adapter.addFragment(EarningsFragment(), getString(R.string.title_earning))
        adapter.addFragment(WithdrawalFragment(), getString(R.string.title_withdrawal))
        viewPager.adapter = adapter
        this.viewPager=viewPager
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    override fun respond(data: String, balanceString: String,fragment_name: String) {
        if(isWithdrawalFragmentClicked){
            val withdrawalFragment = supportFragmentManager?.fragments?.get(1)
            withdrawalFragment as WithdrawalFragment
            withdrawalFragment.displayReceivedData(data, balanceString)
        }else{
            val withdrawalFragment = supportFragmentManager?.fragments?.get(2) as WithdrawalFragment
            withdrawalFragment.displayReceivedData(data, balanceString)
        }
    }

    override fun requestPageLoad(page: Int) {

    }
}
