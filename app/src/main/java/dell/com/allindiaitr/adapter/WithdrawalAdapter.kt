package dell.com.allindiaitr.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.WithdrawalListBinding
import dell.com.allindiaitr.holders.WithdrawalHolder

class WithdrawalAdapter(private val mContext: Context,
                        private val withdrawalMoney: List<String>,
                        private val withdrawalDate: List<String>,
                        private val withdrawalStatus: List<String>) : RecyclerView.Adapter<WithdrawalHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WithdrawalHolder {
//        return WithdrawalHolder(LayoutInflater.from(mContext).inflate(R.layout.withdrawal_list, p0, false))

        val binding = WithdrawalListBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return WithdrawalHolder(binding)
    }

    override fun getItemCount(): Int {
        return withdrawalMoney.size
    }

    override fun onBindViewHolder(holder: WithdrawalHolder, position: Int) {
        holder.withdrawalMonyTextView.text = "\u20B9 " + withdrawalMoney.get(position)
        holder.withdrawalDateTextView.text = withdrawalDate.get(position)

        if (withdrawalStatus.get(position) == "Pending") {
            holder.withdrawalStatusTextView.text = CommonVal.InProgress.name
            holder.image.setImageResource(R.drawable.ic_itr_inprogress)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.withdrawalStatusTextView.setTextColor(mContext.getColor(R.color.red))
            }
        } else {
            holder.withdrawalStatusTextView.text = "Processed Successfully"
            holder.image.setImageResource(R.drawable.ic_e_filed)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.withdrawalStatusTextView.setTextColor(mContext.getColor(R.color.colorPrimary))
            }
        }
    }
}