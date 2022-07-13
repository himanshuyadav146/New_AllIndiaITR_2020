package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.ChatTextAdapterBinding

class ChatArrayHolder(private val binding:ChatTextAdapterBinding): RecyclerView.ViewHolder(binding.root) {
    val userchatText = binding.userchatText
    val adminchatText = binding.adminchatText
    val userCreateDateText = binding.userCreateDateText
    val adminDateText = binding.adminDateText
    val userLayout = binding.userLayout
    val adminLayout = binding.adminLayout
}