package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.CardServicesBinding

class DashboardHolder(private val binding: CardServicesBinding): RecyclerView.ViewHolder(binding.root) {
    val service_imageView = binding.serviceImageView
    val heading_textView = binding.headingTextView
    val desc_textView = binding.descTextView
    val free_imageView = binding.freeImageView
}