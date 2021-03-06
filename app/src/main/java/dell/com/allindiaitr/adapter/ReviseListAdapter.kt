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
import dell.com.allindiaitr.databinding.CardItrSelectBinding
import dell.com.allindiaitr.holders.SelectITRHolder
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.revisedReturn.RevisePersonalInfoActivity
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.ConnectionDetector
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ReviseListAdapter(private val mContext: Context): RecyclerView.Adapter<SelectITRHolder>() {

    private var appPreferences: AppPreferences? = AppPreferences(mContext)
    val apI_Interface = APIClient().getClient().create(API_Interface::class.java)
    var newItrBase = NewItrBase.instance
//    var dialog=AlertDialogueManager()


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SelectITRHolder {
//        return SelectITRHolder(LayoutInflater.from(mContext).inflate(R.layout.card_itr_select, p0, false))
    val binding = CardItrSelectBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return SelectITRHolder(binding)
    }

    override fun getItemCount(): Int {
        return newItrBase.baseUserList!!.size
    }

    override fun onBindViewHolder(holder: SelectITRHolder, p1: Int) {
        holder.username_textView?.text = newItrBase.baseUserList!![p1].name.toString()
        holder.pan_no_textView?.text = "PAN No.: ${newItrBase.baseUserList!![p1].panNumber.toString()}"
        holder.mob_no_textView?.text = "Mob No.: ${newItrBase.baseUserList!![p1].phoneNumber.toString()}"
        holder.itemView.setOnClickListener {
                newItrBase.id = newItrBase.baseUserList!![p1].childUserStatus!![0].userAssessmentYearId
                newItrBase.userId = appPreferences!!.parentId
                newItrBase.processMode = CommonVal.RevisedReturn.name
                newItrBase.isNewUser = false
                newItrBase.selectedUserId = newItrBase.baseUserList!![p1].userId.toString()
                newItrBase.selectedUserName=newItrBase.baseUserList!![p1].name.toString()
                newItrBase.selectedUserEmail=newItrBase.baseUserList!![p1].emailId.toString()
            newItrBase.selectedUserMobile=newItrBase.baseUserList!![p1].phoneNumber.toString()
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
                        newItrBase.processMode = CommonVal.RevisedReturn.name
                        newItrBase.selectedUser_userAssessmentYearUserID = response.body()?.UserAssestmentYearId?.takeUnless { it.isEmpty() } ?: ""

                        val intent = Intent(mContext, RevisePersonalInfoActivity::class.java)
                        (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        mContext.startActivity(intent)
                    }
                    else if (response.code() == 406){
                        dialog.hideDialog()
                        AlertDialogueManager().errorMessageDialogue(mContext, response.body().returnMsg.toString(), "Oops")
                    }
                    else {
                        dialog.hideDialog()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    if (response?.code() == 406){
                        val jObjError = JSONObject(response.errorBody().string())
                        Log.e("jObjError --- ",""+jObjError);
                        AlertDialogueManager().errorMessageDialogue(mContext, jObjError.getString("ReturnMsg"), "Oops")
                    }else{
                        Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }
}