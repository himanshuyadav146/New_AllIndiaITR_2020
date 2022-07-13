package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.WithdrawalAdapter
import dell.com.allindiaitr.databinding.FragmentWithdrawalBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.login.LogInActivity
import dell.com.allindiaitr.models.ReferEarnModel
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithdrawalFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var appPreferences: AppPreferences
    var withdrawalMoney = arrayListOf<String>()
    var withdrawalDate = arrayListOf<String>()
    var withdrawalStatus = arrayListOf<String>()
    var referEarnModel = ReferEarnModel.instance
    lateinit var coupenIFSCString: String
    lateinit var coupenAccountNumberString: String
    lateinit var coupenBankNameString: String
    lateinit var coupenNameString: String
    var check = "false"
    lateinit var balanceString: String
    lateinit var mView: View
    lateinit var binding:FragmentWithdrawalBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        mView = inflater.inflate(R.layout.fragment_withdrawal, container, false)
//        return mView
        binding = FragmentWithdrawalBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext = requireContext()
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(mContext)

        withdrawalMoney.clear()
        withdrawalDate.clear()
        withdrawalStatus.clear()

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getCouponWithdrawal()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
    }




    private fun getCouponWithdrawal() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getCouponWithdrawl("Bearer " + appPreferences.accessTokenId, appPreferences.parentId.toString())
        call.enqueue(object : Callback<List<ReferEarnModel>> {

            override fun onResponse(call: Call<List<ReferEarnModel>>?, response: Response<List<ReferEarnModel>>?) {
                try {
                    when {
                        response!!.isSuccessful -> {
                            dialog.hideDialog()
                            if (response.body() != null) {
                                withdrawalMoney.clear()
                                withdrawalDate.clear()
                                withdrawalStatus.clear()

                                setVisibility_StatusMessage(false)
                                binding.submitButton.visibility = View.GONE

                                for (i in 0 until response.body().size) {
                                    withdrawalMoney.add(if (response.body()[i].amount == null) "" else response.body()[i].amount.toString())
                                    withdrawalDate.add(if (response.body()[i]. createdDate == null) "" else response.body()[i].createdDate.toString())
                                    withdrawalStatus.add(if (response.body()[i].status == null) "" else response.body()[i].status.toString())
                                }
                                binding.recyclerView.adapter = WithdrawalAdapter(mContext, withdrawalMoney, withdrawalDate, withdrawalStatus)
                                binding.recyclerView.setHasFixedSize(true)
                                binding.recyclerView.layoutManager = LinearLayoutManager(mContext)
                            }
                            else {
                                setVisibility_StatusMessage(true)
                            }
                        }
                        response.code() == 401 -> {
                            dialog.hideDialog()
                            appPreferences.ClearPreferences()
                            val intent = Intent(mContext, LogInActivity::class.java)
//                            (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                            (mContext as Activity).finish()
                            startActivity(intent)
                        }
                        else -> {
                            dialog.hideDialog()
                            setVisibility_StatusMessage(true)
                            binding.submitButton.visibility = View.GONE
                        }
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ReferEarnModel>>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setVisibility_StatusMessage(isVisible: Boolean) {
        if (isVisible) {
            binding.recyclerView.visibility = View.GONE
            binding.layoutMessageContainer.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.layoutMessageContainer.visibility = View.GONE
        }
    }

    private fun getWithdrawalBank() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getWithdrawlBank("Bearer " + appPreferences.accessTokenId, appPreferences.parentId.toString())
        call.enqueue(object : Callback<ReferEarnModel> {

            override fun onResponse(call: Call<ReferEarnModel>?, response: Response<ReferEarnModel>?) {
                try {
                    when {
                        response!!.isSuccessful -> {
                            dialog.hideDialog()
                            var nameString = response.body().name?.takeUnless {it.isEmpty()} ?: ""
                            var bankNameString = response.body().bankName?.takeUnless {it.isEmpty()} ?: ""
                            var accountNumber = response.body().accountNumber?.takeUnless {it.isEmpty()} ?: ""
                            var IFSCCodeString = response.body().iFSCCode?.takeUnless {it.isEmpty()} ?: ""

                            if (nameString == "null") {
                                nameString = ""
                            }
                            if (bankNameString == "null") {
                                bankNameString = ""
                            }
                            if (accountNumber == "null") {

                                accountNumber = ""
                            }
                            if (IFSCCodeString == "null") {
                                IFSCCodeString = ""
                            }


                            binding.coupenNameEditText.setText(nameString)
                            binding.coupenBankNameEditText.setText(bankNameString)
                            binding.coupenAccountNumberEditText.setText(accountNumber)
                            binding.coupenIFSCEditText.setText(IFSCCodeString)
                        }
                        response.code() ==401 -> {
                            dialog.hideDialog()
                            appPreferences.ClearPreferences()
                            val intent = Intent(mContext, LogInActivity::class.java)
//                            (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                            (mContext as Activity).finish()
                            startActivity(intent)
                        }
                        else -> dialog.hideDialog()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReferEarnModel>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun displayReceivedData(message: String, balanceString: String) {
        check = message
        this.balanceString = balanceString
        if (check == "true") {
            binding.scrollView.visibility = View.VISIBLE
            binding.submitButton.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.layoutMessageContainer.visibility = View.GONE
//            mView.viewTreeObserver.addOnGlobalLayoutListener {
//                val heightDiff = mView.rootView.height - mView.height
//                if (heightDiff > dpToPx(mContext, 270f)) {
//                    binding.submitButton.visibility = View.GONE
//                } else {
//                    if (check == "true") {
////                        binding.submitButton.visibility = View.VISIBLE
//                    } else {
////                        binding.submitButton.visibility = View.GONE
//                    }
//
//                }
//            }
            withdrawalBank()
        }
        else {
            binding.scrollView.visibility = View.GONE
            binding.submitButton.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.layoutMessageContainer.visibility = View.VISIBLE
            if (ConnectionDetector().isConnectingToInternet(mContext)) {
                getCouponWithdrawal()
            } else {
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    private fun withdrawalBank() {
        if (ConnectionDetector().isConnectingToInternet(mContext))
            getWithdrawalBank()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()

        binding.submitButton.setOnClickListener {
            check = "false"
            coupenNameString = binding.coupenNameEditText.text.toString().trim { it <= ' ' }
            coupenAccountNumberString = binding.coupenAccountNumberEditText.text.toString().trim { it <= ' ' }
            coupenIFSCString = binding.coupenIFSCEditText.text.toString().trim { it <= ' ' }
            coupenBankNameString = binding.coupenBankNameEditText.text.toString().trim { it <= ' ' }
            binding.scrollView.visibility = View.GONE
            binding.submitButton.visibility = View.GONE
            if (Validation().isNameValid(binding.coupenNameEditText.text.toString(), binding.coupenNameEditText) &&
                Validation().isBankNameValid(binding.coupenBankNameEditText.text.toString(), binding.coupenBankNameEditText) &&
                Validation().isBankAccountNumberValid(binding.coupenAccountNumberEditText.text.toString(), binding.coupenAccountNumberEditText) &&
                Validation().isIfscValid(binding.coupenIFSCEditText.text.toString(), binding.coupenIFSCEditText)) {

                if (ConnectionDetector().isConnectingToInternet(mContext))
                    postAddBankAccount()
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun postAddBankAccount(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        referEarnModel.parentId = appPreferences.parentId.toString()
        referEarnModel.name = coupenNameString
        referEarnModel.bankName = coupenBankNameString
        referEarnModel.accountNumber = coupenAccountNumberString
        referEarnModel.amount = balanceString
        referEarnModel.iFSCCode = coupenIFSCString
        referEarnModel.createdDate = appPreferences.parentId.toString()

        val call = apI_Interface.postAddCouponBankDetails("Bearer " + appPreferences.accessTokenId, referEarnModel)
        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        binding.scrollView.visibility = View.GONE
                        binding.submitButton.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.layoutMessageContainer.visibility = View.GONE
                        if (ConnectionDetector().isConnectingToInternet(mContext))
                            getCouponWithdrawal()
                        else
                            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    else if (response.code() == 401) {
                        dialog.hideDialog()
                    }
                    else {
                        dialog.hideDialog()
                        Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }
}


