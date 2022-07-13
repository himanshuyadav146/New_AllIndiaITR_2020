package dell.com.allindiaitr.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.databinding.CardOrderPaymentBinding

class CardOrderPaymentViewHolder (val binding:CardOrderPaymentBinding): RecyclerView.ViewHolder(binding.root){

    val mImg_package=binding.imgPackage
    val mTv_package_title=binding.tvPackageTitle
    val mTv_package_desc=binding.tvPackageDesc
    val mSwitch_package=binding.switchPackage
    val mTv_package_cost=binding.tvPackageCost
}