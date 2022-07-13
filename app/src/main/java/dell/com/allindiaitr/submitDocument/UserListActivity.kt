package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import dell.com.allindiaitr.Enum.CommonVal

import dell.com.allindiaitr.adapter.UserListExpandableAdapter
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.APIClient
import java.lang.Exception
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityUserListBinding
import dell.com.allindiaitr.home.DashboardActivity
import dell.com.allindiaitr.models.UserListHolder
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.MyContextWrapper


class UserListActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    var prevExpandPosition: Int = -1
    var newItrBase = NewItrBase.instance
    var userListModel = NewItrBase.instance
    var childUserListModel = NewItrBase.instance
    var userList = mutableListOf<NewItrBase>()
    var listData = HashMap<String, List<NewItrBase>>()
    private var appPreferences: AppPreferences? = null
    var userListHolder:UserListHolder= UserListHolder.instance
    lateinit var binding: ActivityUserListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)


        //        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbarTitle1.text=getString(R.string.title_select_User)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            binding.startNewFilingButton.backgroundTintList = mContext.getColorStateList(R.color.white)
        else
            ViewCompat.setBackgroundTintList(binding.startNewFilingButton, ColorStateList.valueOf(Color.rgb(255, 255, 255)))

        binding.startNewFilingButton.setOnClickListener {
            try {
                newItrBase.selectedUser_userAssessmentYearUserID = null
                val intent1 = Intent(mContext, SourceOfIncomeActivity::class.java)
                newItrBase.selectedUser_userAssessmentYearUserID = null
                newItrBase.isNewUser = true

                userListHolder.userList=userList
                userListHolder.listData=listData

                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent1)
            }catch (e:Exception){
                Toast.makeText(mContext,"Error",Toast.LENGTH_LONG).show()
            }


        }

        initializer()
        newItrBase.flagInfo = false
        getUserList()

    }


    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

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

    override fun onBackPressed() {
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivityForResult(intent, 0)
        overridePendingTransition(0, 0)
        finish()
    }

    private fun initializer(){
        binding.expandableListView.setOnGroupExpandListener { groupPosition ->
            if (prevExpandPosition != -1
                && groupPosition != prevExpandPosition) {
                binding.expandableListView.collapseGroup(prevExpandPosition)
            }
            prevExpandPosition = groupPosition
        }

        binding.expandableListView.setOnGroupCollapseListener {
        }

        binding.expandableListView.setOnChildClickListener { _, _, _, _, _ ->
            false
        }
    }

    private fun visibility(visible: Boolean){
        if (visible){
            binding.oopsImage.visibility = View.GONE
            binding.userListLayout.visibility = View.VISIBLE
        }
        else {
            binding.oopsImage.visibility = View.VISIBLE
            binding.userListLayout.visibility = View.GONE
        }
    }

    private fun getUserList(){
        if (newItrBase.baseUserList != null && newItrBase.baseUserList!!.isNotEmpty()){
            visibility(true)
            for (i in 0 until newItrBase.baseUserList!!.size){
                userListModel = newItrBase.baseUserList!![i]
                var childList = mutableListOf<NewItrBase>()
                for (j in 0 until userListModel.childUserStatus!!.size){
                    childUserListModel = userListModel.childUserStatus!![j]
                    if (childUserListModel.processMode.equals(CommonVal.EVerify.name) && childUserListModel.paymentStatus == false)
                    else if (childUserListModel.processMode.equals(CommonVal.RevisedReturn.name) && childUserListModel.paymentStatus == false)
                    else
                        childList.add(childUserListModel)
                }

                if (childList.size!=0){
                    userList.add(userListModel)
                    listData[newItrBase.baseUserList!![i].panNumber.toString()] = childList
                }
            }
            binding.expandableListView.setAdapter(UserListExpandableAdapter(mContext, binding.expandableListView, listData, userList))
            binding.expandableListView.expandGroup(0)
        }
        else
            visibility(false)
    }
}
