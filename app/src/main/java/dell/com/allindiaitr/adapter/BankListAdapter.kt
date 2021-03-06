package dell.com.allindiaitr.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.NewBankaccountAdapterBinding
import dell.com.allindiaitr.holders.BankListHolder
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.submitDocument.AddBankDetailsActivity
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.ConnectionDetector
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class   BankListAdapter(private val mContext: Context,
                      private val bankName: List<String>,
                      private val accountNumber: List<String>,
                      private val bankId: List<String>,
                      private val bankTypeFlag: List<String>): RecyclerView.Adapter<BankListHolder>() {

    val apI_Interface = APIClient().getClient().create(API_Interface::class.java)
    var newItrBase = NewItrBase.instance

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BankListHolder {
//        return BankListHolder(LayoutInflater.from(mContext).inflate(R.layout.new_bankaccount_adapter, p0, false))
        val binding = NewBankaccountAdapterBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return BankListHolder(binding)
    }

    override fun getItemCount(): Int {
        return bankId.size
    }

    override fun onBindViewHolder(p0: BankListHolder, p1: Int) {
        p0.bankName?.text = bankName[p1].toString()
        p0.accountNumber?.text = accountNumber[p1].toString()

        if (bankTypeFlag[p1] == "1"){
            p0.card_info_img?.setImageResource(R.drawable.ic_e_filed)
            p0.notify_msg?.text = mContext.getString(R.string.txt_tax_refund)
        }

        p0.menuIconImage?.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(mContext, p0.menuIconImage)
            popupMenu.menuInflater.inflate(R.menu.edit, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.action_edit -> {
                        newItrBase.bankInfoId = bankId[p1].toString()
                        val intent = Intent(mContext, AddBankDetailsActivity::class.java)
                        newItrBase.isAddBank = false
                        (mContext as Activity).finish()
                        (mContext).startActivityForResult(intent, 0)
                        (mContext).overridePendingTransition(0, 0)
                    }
                    R.id.action_delete -> {
                        if (ConnectionDetector().isConnectingToInternet(mContext)) {
                            delete(bankId[p1].toString())
                        }
                        else {
                            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                true
            })
            popupMenu.show()
        }
    }

    private fun delete(id: String){
        var dialog = AlertDialogueManager(mContext,mContext.getString(R.string.dilog_pleasewait))

        val call = apI_Interface.deleteBank(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    dialog.hideDialog()
                    if (response!!.isSuccessful){
                        (mContext as Activity).finish()
                        mContext.overridePendingTransition(0, 0)
                        mContext.startActivity(mContext.intent)
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                dialog.hideDialog()
            }

        })
    }
}