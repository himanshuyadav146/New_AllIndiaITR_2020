package dell.com.allindiaitr.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.CardServicesBinding
import dell.com.allindiaitr.holders.DashboardHolder
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.submitDocument.ITRProcessTutorialActivity
import dell.com.allindiaitr.submitDocument.UserListActivity
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.revisedReturn.ReviseListActivity
import dell.com.allindiaitr.revisedReturn.RevisePersonalInfoActivity
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.ConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class StartEFilingAdapter(private val mContext: Context,
                          private val heading: List<String>,
                          private val description: List<String>,
                          private val images: List<Int>): RecyclerView.Adapter<DashboardHolder>() {

    private var appPreferences: AppPreferences? = AppPreferences(mContext)
    val apI_Interface = APIClient().getClient().create(API_Interface::class.java)
    var newItrBase = NewItrBase.instance

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DashboardHolder {
        val binding = CardServicesBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return DashboardHolder(binding)
    }

    override fun getItemCount(): Int {
        return heading.size
    }

    override fun onBindViewHolder(p0: DashboardHolder, p1: Int) {
        p0.heading_textView?.text = heading[p1].toString()
        p0.desc_textView?.text = description[p1].toString()
        p0.service_imageView.setImageResource(images[p1])
        p0.itemView.setOnClickListener {
            when(p1){
                0 -> {
                    newItrBase.processMode = CommonVal.SubmitDocument.name
                    if (ConnectionDetector().isConnectingToInternet(mContext)){
                        getUserList()
                    }
                    else {
                        Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                }
                1 -> {
                    newItrBase.processMode = CommonVal.RevisedReturn.name
                    if (ConnectionDetector().isConnectingToInternet(mContext)){
                        getUserList()
                    }
                    else {
                        Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getUserList(){
      //  var dialog = AlertDialogueManager(mContext,"Please Wait")
        var dialog = AlertDialogueManager(mContext,mContext.getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getITRuserList(appPreferences!!.parentId.toString())
        call.enqueue(object : Callback<List<NewItrBase>>{
            override fun onResponse(call: Call<List<NewItrBase>>?, response: Response<List<NewItrBase>>?) {
               // Log.e("response ","get_users_list " +response?.body());
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        if (response.body() != null) {
                            newItrBase.baseUserList = response.body()
                            newItrBase.isNewUser = response.body()!!.isEmpty()
                            navigate()
                        }
                    }
                    else {
                        dialog.hideDialog()
                        navigate()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NewItrBase>>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun navigate(){
        if (newItrBase.processMode == CommonVal.SubmitDocument.name){
            if (newItrBase.isNewUser){
                val intent = Intent(mContext, ITRProcessTutorialActivity::class.java)
//                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.startActivity(intent)
            }
            else {
                val intent = Intent(mContext, UserListActivity::class.java)
//                (mContext as Activity).finish()
//                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.startActivity(intent)
            }
        }
        else if (newItrBase.processMode == CommonVal.RevisedReturn.name) {
            if (newItrBase.isNewUser){
                val intent = Intent(mContext, RevisePersonalInfoActivity::class.java)
//                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.startActivity(intent)
            }
            else {
                val intent = Intent(mContext, ReviseListActivity::class.java)
//                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.startActivity(intent)
            }
        }
        else {

        }
    }
}