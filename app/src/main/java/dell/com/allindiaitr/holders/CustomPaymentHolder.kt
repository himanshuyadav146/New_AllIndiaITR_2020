package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.CustomPaymentCardBinding

class CustomPaymentHolder(val binding: CustomPaymentCardBinding): RecyclerView.ViewHolder(binding.root) {
    val tv_email_id = binding.tvEmailId
    val tv_phone_no = binding.tvPhoneNo
    val tv_description = binding.tvDescription
    val tv_total_amount = binding.tvTotalAmount
    val btn_paynow = binding.btnPaynow
    val payment_complete = binding.paymentComplete
}