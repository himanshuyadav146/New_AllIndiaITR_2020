package dell.com.allindiaitr.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.CustomPaymentCardBinding
import dell.com.allindiaitr.holders.CustomPaymentHolder
import dell.com.allindiaitr.models.MoreModel
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.submitDocument.PaymentActivity
import dell.com.allindiaitr.utils.AppPreferences

class CustomPaymentAdapter(
    private val mContext: Context,
    private val paymentModelList: List<MoreModel>
) : RecyclerView.Adapter<CustomPaymentHolder>() {

    var newItrBase = NewItrBase.instance

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomPaymentHolder {
//        return CustomPaymentHolder(LayoutInflater.from(mContext).inflate(R.layout.custom_payment_card, p0, false))
        val binding = CustomPaymentCardBinding.inflate(LayoutInflater.from(mContext), p0, false)
        return CustomPaymentHolder(binding)
    }

    override fun getItemCount(): Int {
        return paymentModelList.size
    }

    override fun onBindViewHolder(holder: CustomPaymentHolder, position: Int) {
        holder.tv_email_id.text =
            if (paymentModelList[position].email == null) "" else "Email ID: " + paymentModelList[position].email
        holder.tv_phone_no.text =
            if (paymentModelList[position].phoneNumber == null) "" else "Mob No.: " + paymentModelList[position].phoneNumber
        holder.tv_description.text = HtmlCompat.fromHtml(
            if (paymentModelList[position].description == null) "" else "<i>Description:</i> " + paymentModelList[position].description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        holder.tv_total_amount.text =
            if (paymentModelList[position].amount == null) "" else "Total Amount: \u20B9 " + paymentModelList[position].amount

        if (paymentModelList[position].paymentStatus!!) {
            holder.btn_paynow.visibility = View.GONE
            holder.payment_complete.visibility = View.VISIBLE
        } else {
            holder.btn_paynow.visibility = View.VISIBLE
            holder.payment_complete.visibility = View.GONE
        }
        holder.btn_paynow.setOnClickListener(View.OnClickListener {
            newItrBase.selectedUser_userAssessmentYearUserID =
                paymentModelList[position].userAssessmentYearId.toString()
            newItrBase.processMode = "paymentlink"
            newItrBase.selectedUserEmail = paymentModelList[position].email
            newItrBase.selectedUserMobile = paymentModelList[position].phoneNumber
            newItrBase.selectedUserName =
                paymentModelList[position].firstName + " " + paymentModelList[position].lastName
//            newItrBase.selectedUserName=appPreferences.userFirstName+" "+paymentModelList[position].lastName
            val intent = Intent(mContext, PaymentActivity::class.java)
            (mContext as Activity).overridePendingTransition(
                R.anim.slide_from_right,
                R.anim.slide_to_left
            )
            (mContext as Activity).finish()
            mContext.startActivity(intent)
        })
    }
}