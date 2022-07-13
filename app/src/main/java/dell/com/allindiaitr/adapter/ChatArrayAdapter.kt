package dell.com.allindiaitr.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ChatTextAdapterBinding
import dell.com.allindiaitr.holders.ChatArrayHolder

class ChatArrayAdapter(private val mContext: Context,
                       private var CommentTypeArrayList: ArrayList<String>,
                       private var CommentArrayList: ArrayList<String>,
                       private var CreatedByArrayList: ArrayList<String>,
                       private var CreatedDateArrayList: ArrayList<String>): RecyclerView.Adapter<ChatArrayHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChatArrayHolder {
//        return ChatArrayHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_text_adapter, p0, false))
        val biding = ChatTextAdapterBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return ChatArrayHolder(biding)
    }

    override fun getItemCount(): Int {
        return CommentArrayList.size
    }

    override fun onBindViewHolder(holder: ChatArrayHolder, position: Int) {
        if (CommentTypeArrayList.get(position) == "AdminComment") {
            holder.adminLayout.visibility = View.VISIBLE
            holder.userLayout.visibility = View.GONE
            holder.adminchatText.text = CommentArrayList.get(position)
            val a = StringBuffer(CreatedDateArrayList.get(position))
            holder.adminDateText.text = a
        } else {
            holder.userLayout.visibility = View.VISIBLE
            holder.adminLayout.visibility = View.GONE
            holder.userchatText.text = CommentArrayList.get(position)
            holder.userCreateDateText.text = CreatedDateArrayList.get(position)
        }
    }
}