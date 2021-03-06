package dell.com.allindiaitr.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.CardAllOrdersBinding
import dell.com.allindiaitr.holders.AllOrdersHolder
import dell.com.allindiaitr.models.AllOrderStatusModel
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.submitDocument.ERStatusActivity
import dell.com.allindiaitr.submitDocument.ITROrderStatusActivity
import dell.com.allindiaitr.utils.AppPreferences

class AllOrdersAdapter(private val mContext: Context,
                       private val packageModelList: List<AllOrderStatusModel>) : RecyclerView.Adapter<AllOrdersHolder>() {

    private var appPreferences: AppPreferences? = AppPreferences(mContext)
    var newItrBase = NewItrBase.instance
    
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AllOrdersHolder {
      //  return AllOrdersHolder(LayoutInflater.from(mContext).inflate(R.layout.card_all_orders, p0, false))
        val binding = CardAllOrdersBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return AllOrdersHolder(binding)
    }

    override fun getItemCount(): Int {
        return packageModelList.size
    }

    override fun onBindViewHolder(holder: AllOrdersHolder, p1: Int) {
        holder.tv_name.text = if (packageModelList[p1].name == null) "" else packageModelList[p1].name.toString()
        holder.tv_order_type.text = if (packageModelList[p1].iTRProcess == null) "" else packageModelList[p1].iTRProcess.toString()
        holder.tv_orderAmount.text = if (packageModelList[p1].totalAmount == null) "" else packageModelList[p1].totalAmount.toString()
        holder.tv_orderDate.text = if (packageModelList[p1].paymentDate == null) "" else packageModelList[p1].paymentDate.toString()
        holder.tv_status.text = if (packageModelList[p1].processStatus == null) "" else packageModelList[p1].processStatus.toString()

        if (packageModelList[p1].processStatus != null) {
            if (packageModelList[p1].processStatus.equals("In Progress")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    holder.tv_status.setTextColor(mContext.getColor(R.color.red))
                else
                    holder.tv_status.setTextColor(Color.parseColor("#FF6463"))
                holder.img_status.setImageResource(R.drawable.ic_itr_inprogress)
            } else if (packageModelList[p1].processStatus.equals("E-Filed") || packageModelList[p1].processStatus.equals(CommonVal.Complete.name)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    holder.tv_status.setTextColor(mContext.getColor(R.color.colorPrimary))
                else
                    holder.tv_status.setTextColor(Color.parseColor("#00B894"))
                holder.img_status.setImageResource(R.drawable.ic_e_filed)
            }
        }

        if (packageModelList[p1].iTRProcess == null) {
            holder.tv_order_type.visibility = View.INVISIBLE
        } else {
            holder.tv_order_type.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {

            if(packageModelList[p1].processMode==null || packageModelList[p1].processStatus==null)
            {
                return@setOnClickListener
            }

            if(appPreferences==null)
            {
                return@setOnClickListener
            }

            if (packageModelList[p1].processMode.equals(CommonVal.SubmitDocument.name) && packageModelList[p1].processStatus.equals("In Progress"))
            {
                newItrBase.itrStatus = "ITR Inprogress"
                newItrBase.isNewUser = false
                newItrBase.selectedUser_userAssessmentYearUserID = packageModelList[p1].userAssestmentYearId
                val intent = Intent(mContext, ITROrderStatusActivity::class.java)
                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.finish()
                mContext.startActivity(intent)
            }
            else if (packageModelList[p1].processMode.equals(CommonVal.SubmitDocument.name) && packageModelList[p1].processStatus.equals("E-Filed"))
            {
                newItrBase.itrStatus = "E-Filed"
                newItrBase.isNewUser = false
                newItrBase.selectedUserEmail = packageModelList[p1].emailId
                newItrBase.selectedUserMobile = packageModelList[p1].phoneNumber
                newItrBase.eVerifyPaymentDone = packageModelList[p1].eVerifyPaymentDone
                newItrBase.acknowledgementNo = packageModelList[p1].acknowledgementNo
                newItrBase.selectedUserName = packageModelList[p1].name

                newItrBase.selectedUser_userAssessmentYearUserID = packageModelList[p1].userAssestmentYearId
                val intent = Intent(mContext, ITROrderStatusActivity::class.java)
                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.finish()
                mContext.startActivity(intent)
            }
            else if (packageModelList[p1].processMode.equals(CommonVal.RevisedReturn.name) && packageModelList[p1].processStatus.equals("In Progress"))
            {
                newItrBase.selectedProcessStatus = CommonVal.InProgress.name
                newItrBase.processMode = CommonVal.RevisedReturn.name
                val intent = Intent(mContext, ERStatusActivity::class.java)
                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.finish()
                mContext.startActivity(intent)
            }
            else if (packageModelList[p1].processMode.equals(CommonVal.RevisedReturn.name) && packageModelList[p1].processStatus.equals(CommonVal.Complete.name))
            {
                newItrBase.selectedProcessStatus = CommonVal.Complete.name
                newItrBase.processMode = CommonVal.RevisedReturn.name

                val intent = Intent(mContext, ERStatusActivity::class.java)
                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.finish()
                mContext.startActivity(intent)
            }
            else if (packageModelList[p1].processMode.equals(CommonVal.EVerify.name) && packageModelList[p1].processStatus.equals("In Progress"))
            {
                newItrBase.selectedProcessStatus = CommonVal.InProgress.name
                newItrBase.processMode = CommonVal.EVerify.name
                val intent = Intent(mContext, ERStatusActivity::class.java)
                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.finish()
                mContext.startActivity(intent)
            }
            else if (packageModelList[p1].processMode.equals(CommonVal.EVerify.name) && packageModelList[p1].processStatus.equals(CommonVal.Complete.name))
            {
                newItrBase.selectedProcessStatus = CommonVal.Complete.name
                newItrBase.processMode = CommonVal.EVerify.name
                val intent = Intent(mContext, ERStatusActivity::class.java)
                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                mContext.finish()
                mContext.startActivity(intent)
            }
        }
    }
}