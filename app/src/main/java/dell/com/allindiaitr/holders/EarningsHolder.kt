package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.EarnningListadapterBinding

class EarningsHolder(val binding:EarnningListadapterBinding): RecyclerView.ViewHolder(binding.root) {

    val earninglistAmountText = binding.earninglistAmountText
    val earnningDateText = binding.earnningDateText
    val earnningUserNameText = binding.earnningUserNameText
    val card_view = binding.cardView
}