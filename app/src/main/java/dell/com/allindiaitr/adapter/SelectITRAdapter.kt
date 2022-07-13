package dell.com.allindiaitr.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.CardItrSelectBinding
import dell.com.allindiaitr.holders.SelectITRHolder
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.submitDocument.SourceOfIncomeActivity
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.ConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SelectITRAdapter(private val mContext: Context,
                       private val userList: List<NewItrBase>): RecyclerView.Adapter<SelectITRHolder>() {

    private var appPreferences: AppPreferences? = AppPreferences(mContext)
    val apI_Interface = APIClient().getClient().create(API_Interface::class.java)
    var newItrBase = NewItrBase.instance

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SelectITRHolder {
       // return SelectITRHolder(LayoutInflater.from(mContext).inflate(R.layout.card_itr_select, p0, false))
        val binding = CardItrSelectBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return SelectITRHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(p0: SelectITRHolder, p1: Int) {
        p0.username_textView?.text = userList[p1].name.toString()
        p0.pan_no_textView?.text = "PAN No.: ${userList[p1].panNumber.toString()}"
        p0.mob_no_textView?.text = "Mob No.: ${userList[p1].phoneNumber.toString()}"
        p0.itemView.setOnClickListener {
            newItrBase.id = userList[p1].childUserStatus!![0].userAssessmentYearId
            newItrBase.userId = appPreferences!!.parentId
            newItrBase.processMode = CommonVal.SubmitDocument.name
            newItrBase.isNewUser = false
            newItrBase.selectedUserId = userList[p1].userId.toString()

            if (ConnectionDetector().isConnectingToInternet(mContext)){
                getChooseUserForNewITR()
            }
            else {
                Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun getChooseUserForNewITR(){
        var dialog = AlertDialogueManager(mContext,mContext.getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getchooseUserForNewITR(newItrBase)
        call.enqueue(object : Callback<NewItrBase> {
            override fun onResponse(
                call: Call<NewItrBase>?,
                response: Response<NewItrBase>?
            ) {
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        newItrBase.processMode = CommonVal.SubmitDocument.name
                        newItrBase.selectedUser_userAssessmentYearUserID = response.body()?.UserAssestmentYearId?.takeUnless { it.isEmpty() } ?: ""

                        val intent = Intent(mContext, SourceOfIncomeActivity::class.java)
                        (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        mContext.startActivity(intent)
                    }
                    else if (response.code() == 406){
                        dialog.hideDialog()
                        AlertDialogueManager().errorMessageDialogue(mContext, response.body()?.returnMsg.toString(), "Oops")
                    }
                    else {
                        dialog.hideDialog()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }



}