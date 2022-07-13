package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityImportantDetailsBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.interfaces.FragmentCommunicator
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImportantDetailsActivity : AppCompatActivity(), FragmentCommunicator {
    override fun respond(data: String, balanceString: String, fragments_name: String) {

    }

    override fun requestPageLoad(page: Int) {
        setPager(page)
    }

    lateinit var mContext: Context
    lateinit var apI_Interface: API_Interface
    lateinit var appPreferences: AppPreferences
    var newItrBase = NewItrBase.instance
    private lateinit var binding:ActivityImportantDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = getString(R.string.title_imp_details)

        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

        mContext = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgUser.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgForms.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgDetails.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        }
        else {
            ViewCompat.setBackgroundTintList(binding.imgUser, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgForms, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
            ViewCompat.setBackgroundTintList(binding.imgDetails, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
        }


        setupViewPager(binding.viewpager!!)
        binding.tabLayout!!.setupWithViewPager(binding.viewpager)

        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                binding.viewpager.clearFocus()
            }

            override fun onPageSelected(position: Int) {
                if (position == 0 || position == 1) {
                    if (currentFocus != null) {
                        currentFocus!!.clearFocus()
                    }
                }
            }
            override fun onPageScrollStateChanged(state: Int) {
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


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ImpDetailsEnterFragment(), "Enter Details")
        adapter.addFragment(ImpDetailsUploadFragment(), "Upload")
        viewPager.adapter = adapter

        //viewPager.setCurrentItem(1);
    }
    fun setPager(page:Int)
    {
        binding.viewpager.setCurrentItem(page)
    }



    private fun getDocumentList() {
     //   var dialog = AlertDialogueManager(mContext, "Please Wait")

        val call = apI_Interface.getAadhaarDocumentList(
            newItrBase.selectedUser_userAssessmentYearUserID.toString(),
            "aadharcard"
        )
        call.enqueue(object : Callback<NewItrBase> {
            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                   // dialog.hideDialog()
                    if (response!!.isSuccessful) {
                        if (response.body().getFormDocList != null) {
                            if(response.body().getFormDocList!!.size>0)
                            {
                                setupViewPager(binding.viewpager!!)
                                binding.tabLayout!!.setupWithViewPager(binding.viewpager)
                                binding.viewpager.setCurrentItem(1)
                            }


                        } else {

                        }
                    } else {

                    }
                } catch (e: Exception) {
                  //  dialog.hideDialog()
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                //dialog.hideDialog()

            }

        })
    }






    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
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
