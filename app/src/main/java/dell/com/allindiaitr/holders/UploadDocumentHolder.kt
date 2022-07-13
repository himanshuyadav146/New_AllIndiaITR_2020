package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.CardUploadListBinding

class UploadDocumentHolder(val binding: CardUploadListBinding) : RecyclerView.ViewHolder(binding.root) {

    var documnet_name_textView = binding.documnetNameTextView
    var delete = binding.delete
}