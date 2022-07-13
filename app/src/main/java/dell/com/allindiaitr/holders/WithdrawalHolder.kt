package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.WithdrawalListBinding

class WithdrawalHolder(val binding: WithdrawalListBinding): RecyclerView.ViewHolder(binding.root) {

    val withdrawalMonyTextView = binding.withdrawalMonyTextView
    val withdrawalDateTextView = binding.withdrawalDateTextView
    val withdrawalStatusTextView = binding.withdrawalStatusTextView
    val withdrawalMessageTextView = binding.withdrawalMessageTextView
    val image = binding.image
    val card_view = binding.cardView
}