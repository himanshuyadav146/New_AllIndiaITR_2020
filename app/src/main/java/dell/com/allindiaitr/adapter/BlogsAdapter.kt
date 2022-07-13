package dell.com.allindiaitr.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.BlogAdapterBinding
import dell.com.allindiaitr.holders.BlogViewHolder
import dell.com.allindiaitr.home.BlogsViewActivity
import dell.com.allindiaitr.utils.ConnectionDetector

class BlogsAdapter(private val mContext : Context,
                   private val titleList: List<String>,
                   private val urlList: List<String>): RecyclerView.Adapter<BlogViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BlogViewHolder {
       // return BlogViewHolder(LayoutInflater.from(mContext).inflate(R.layout.blog_adapter, p0, false))
        val binding = BlogAdapterBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return titleList.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.yearNameText.text = titleList.get(position)
        holder.card_view.setOnClickListener(View.OnClickListener {
            if (ConnectionDetector().isConnectingToInternet(mContext)) {
                val intent = Intent(mContext, BlogsViewActivity::class.java)
                intent.putStringArrayListExtra("key", urlList as ArrayList<String>?)
                intent.putExtra("position", position)
                intent.putExtra("isWebView",true)
                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.startActivity(intent)

            } else {
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })
    }
}