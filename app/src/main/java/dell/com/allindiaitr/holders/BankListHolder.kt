package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.NewBankaccountAdapterBinding

class BankListHolder(private val binding:NewBankaccountAdapterBinding): RecyclerView.ViewHolder(binding.root) {
    val card_info_img = binding.cardInfoImg
    val bankName = binding.bankName
    val accountNumber = binding.accountNumber
    val menuIconImage = binding.menuIconImage
    val notify_msg = binding.notifyMsg
}