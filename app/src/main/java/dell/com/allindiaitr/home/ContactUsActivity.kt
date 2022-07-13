package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityContactUsBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.MoreModel
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : AppCompatActivity() {

    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    var moreModel = MoreModel.instance
    lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(this)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = "Request a Callback"

        binding.requestCallbackButton.setOnClickListener {
            if (Validation().isNameValid(binding.nameEditText.text.toString(), binding.nameEditText) &&
                Validation().isEmailValid(binding.emailEditText.text.toString(), binding.emailEditText) &&
                Validation().isMobileValid(binding.mobileNumberEditText.text.toString(), binding.mobileNumberEditText) &&
                Validation().isFieldValid(binding.commentEditText.text.toString(), binding.commentEditText)) {
                moreModel.name = binding.nameEditText.text.toString()
                moreModel.emailId = binding.emailEditText.text.toString()
                moreModel.phoneNo = binding.mobileNumberEditText.text.toString()
                moreModel.comments = binding.commentEditText.text.toString()

                if (ConnectionDetector().isConnectingToInternet(mContext))
                    postContactUs()
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        ButtonVisibility().hideButton(mContext, binding.activityContactUs, binding.requestCallbackButton)
    }



    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }
    private fun postContactUs(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.postContactUs(moreModel)
        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        AlertDialogueManager().errorMessageDialogue(mContext, "Data Saved Successfully", "Message")
                    }
                    else {
                        dialog.hideDialog()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
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
