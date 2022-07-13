package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.CardItrSelectBinding

class SelectITRHolder(val binding:CardItrSelectBinding): RecyclerView.ViewHolder(binding.root){
    val username_textView = binding.usernameTextView
    val pan_no_textView = binding.panNoTextView
    val mob_no_textView = binding.mobNoTextView
}