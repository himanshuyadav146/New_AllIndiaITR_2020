package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.CardAllOrdersBinding

class AllOrdersHolder(private val binding:CardAllOrdersBinding): RecyclerView.ViewHolder(binding.root) {

    val tv_name = binding.textName
    val tv_orderDate = binding.orderDate
    val tv_order_type = binding.orderType
    val tv_orderAmount = binding.orderAmount
    val tv_status = binding.tvStatus
    val img_status = binding.imgStatus
    val card = binding.card
}