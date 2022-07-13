package dell.com.allindiaitr.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.EarnningListadapterBinding
import dell.com.allindiaitr.holders.EarningsHolder

class EarningsAdapter(private val mContext: Context,
                      private val nameList: List<String>,
                      private val amountList: List<String>,
                      private val dateList: List<String>) : RecyclerView.Adapter<EarningsHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EarningsHolder {
//        return EarningsHolder(LayoutInflater.from(mContext).inflate(R.layout.earnning_listadapter, p0, false))
    val binding = EarnningListadapterBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return EarningsHolder(binding)
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    override fun onBindViewHolder(p0: EarningsHolder, p1: Int) {
        p0.earnningUserNameText.text = nameList[p1]
        p0.earnningDateText.text = "Date " + dateList[p1]
        p0.earninglistAmountText.text = "\u20B9 " + amountList[p1]
    }
}