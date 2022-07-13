package dell.com.allindiaitr.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.CardUploadListBinding
import dell.com.allindiaitr.holders.UploadDocumentHolder
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.ConnectionDetector
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class UploadDocumentAdapter(private var mContext: Context,
                            private var documentNameList: ArrayList<String>,
                            private var documentIdList: ArrayList<String>): RecyclerView.Adapter<UploadDocumentHolder>() {

    val apI_Interface = APIClient().getClient().create(API_Interface::class.java)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): UploadDocumentHolder {
       // return UploadDocumentHolder(LayoutInflater.from(mContext).inflate(R.layout.card_upload_list, p0, false))

        val binding = CardUploadListBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return UploadDocumentHolder(binding)
    }

    override fun getItemCount(): Int {
        return documentIdList.size
    }

    override fun onBindViewHolder(p0: UploadDocumentHolder, p1: Int) {
        p0.documnet_name_textView.text = documentNameList[p1].toString()
        p0.delete.setOnClickListener {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.dialog_confirm, null)
            val alertDialog = AlertDialog.Builder(mContext)
            alertDialog.setView(view)
            alertDialog.setCancelable(false)
            alertDialog.setIcon(R.drawable.logo)
            alertDialog.setTitle("Confirm Delete...")
            var descTextView = view.findViewById<TextView>(R.id.descTextView)
            descTextView.text = "Are you sure you want to delete this file?"
            alertDialog.setPositiveButton("YES", DialogInterface.OnClickListener { _, _ ->
                if (ConnectionDetector().isConnectingToInternet(mContext))
                    delete(documentIdList[p1].toString())
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            })
            alertDialog.setNegativeButton("NO", DialogInterface.OnClickListener { _, _ ->
            })
            alertDialog.show()
        }
    }

    private fun delete(id: String){
        var dialog = AlertDialogueManager(mContext,mContext.getString(R.string.dilog_pleasewait))

        val call = apI_Interface.deleteDocument(id)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    dialog.hideDialog()
                    if (response!!.isSuccessful){
                        (mContext as Activity).finish()
                        (mContext as Activity).overridePendingTransition(0, 0)
                        mContext.startActivity((mContext as Activity).intent)
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