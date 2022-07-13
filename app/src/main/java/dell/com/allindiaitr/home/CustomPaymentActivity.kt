package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.CustomPaymentAdapter
import dell.com.allindiaitr.databinding.ActivityCustomPaymentBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.MoreModel
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.submitDocument.ITROrderStatusActivity
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.ConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class CustomPaymentActivity : AppCompatActivity() {

    lateinit var binding: ActivityCustomPaymentBinding
    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    private lateinit var objItrBase: NewItrBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCustomPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(this)
        objItrBase = NewItrBase.instance

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = "Custom Payment"

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getCustomPaymentList()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
    }

    private fun getCustomPaymentList(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getCustomPaymentList(appPreferences?.parentId.toString())
        call.enqueue(object : Callback<List<MoreModel>> {

            override fun onResponse(call: Call<List<MoreModel>>?, response: Response<List<MoreModel>>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        binding.relativelayoutFrame.visibility = View.VISIBLE
                        binding.noPayment.visibility = View.GONE
                        binding.recyclerView.adapter = CustomPaymentAdapter(mContext, response.body())
                    }
                    else {
                        dialog.hideDialog()
                        binding.relativelayoutFrame.visibility = View.GONE
                        binding.noPayment.visibility = View.VISIBLE
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    binding.relativelayoutFrame.visibility = View.GONE
                    binding.noPayment.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<MoreModel>>?, t: Throwable?) {
                dialog.hideDialog()
                binding.relativelayoutFrame.visibility = View.GONE
                binding.noPayment.visibility = View.VISIBLE
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
        if(objItrBase.itrStatus == "payment success" && objItrBase.processMode == CommonVal.paymentlink.name)
        {
            val i = Intent(mContext, DashboardActivity::class.java)
            startActivity(i)
            finish()
        }else{
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

    }
}
