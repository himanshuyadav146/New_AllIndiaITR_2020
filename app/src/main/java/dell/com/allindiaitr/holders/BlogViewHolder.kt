package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.BlogAdapterBinding

class BlogViewHolder(val binding: BlogAdapterBinding): RecyclerView.ViewHolder(binding.root) {

    val yearNameText = binding.yearNameText
    val card_view = binding.cardView
}