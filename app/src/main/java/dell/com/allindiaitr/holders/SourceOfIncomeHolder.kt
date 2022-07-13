package dell.com.allindiaitr.holders

import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.CardOptionsBinding

class SourceOfIncomeHolder(val binding:CardOptionsBinding): RecyclerView.ViewHolder(binding.root) {
    val income_cource_imageView = binding.incomeCourceImageView
    val income_source_textView = binding.incomeSourceTextView
    val card_options = binding.cardOptions
}