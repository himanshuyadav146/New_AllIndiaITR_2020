package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.EarningsAdapter
import dell.com.allindiaitr.databinding.FragmentEarningsBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.interfaces.FragmentCommunicator
import dell.com.allindiaitr.login.LogInActivity
import dell.com.allindiaitr.models.ReferEarnModel
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.ConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EarningsFragment : Fragment() {

    lateinit var binding:FragmentEarningsBinding
    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var appPreferences: AppPreferences
    lateinit var balanceString: String
    lateinit var earningString: String
    lateinit var withdrawlString: String
    var nameList = arrayListOf<String>()
    var amountList = arrayListOf<String>()
    var dateList = arrayListOf<String>()
    lateinit var fragmentCommunicator: FragmentCommunicator
    lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_earnings, container, false)
        binding = FragmentEarningsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext = requireContext()
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(mContext)

        nameList.clear()
        amountList.clear()
        dateList.clear()

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getReferEarnings()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()

        fragmentCommunicator = (activity as FragmentCommunicator?)!!

        binding.withdrawalbutton.setOnClickListener {
            fragmentCommunicator.respond("true", balanceString,"WithdrawalFragment")
            viewPager.currentItem = 2
        }
    }

    override fun onAttach(context: Context) {

        viewPager = requireActivity().findViewById(R.id.viewpager)
        super.onAttach(context)
    }
    private fun getReferEarnings() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getReferEarnings("Bearer " + appPreferences.accessTokenId, appPreferences.parentId.toString())
        call.enqueue(object : Callback<ReferEarnModel> {

            override fun onResponse(call: Call<ReferEarnModel>?, response: Response<ReferEarnModel>?) {
                try {
                    when {
                        response!!.isSuccessful -> {
                            dialog.hideDialog()
                            balanceString = response.body().balanceAmount.toString()
                            earningString = response.body().earningAmount.toString()
                            withdrawlString = response.body().withdrawnAmount.toString()
                            binding.balanceTextView.text = balanceString

                            if (balanceString == "0.0") {
                                binding.withdrawalbutton.isEnabled = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    binding.withdrawalbutton.backgroundTintList = ColorStateList.valueOf(mContext.getColor(R.color.grey))
                                else
                                    ViewCompat.setBackgroundTintList(binding.withdrawalbutton, ColorStateList.valueOf(Color.rgb(170, 170, 170)))
                            } else {
                                binding.withdrawalbutton.isEnabled = true
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    binding.withdrawalbutton.backgroundTintList = ColorStateList.valueOf(mContext.getColor(R.color.colorPrimary))
                                else
                                    ViewCompat.setBackgroundTintList(binding.withdrawalbutton, ColorStateList.valueOf(Color.rgb(0, 184, 148)))
                            }

                            if (response.body().earningsUserListModel != null) {
                                for (i in 0 until response.body().earningsUserListModel!!.size) {
                                    nameList.add(response.body().earningsUserListModel!![i].name?.takeUnless {it.isEmpty()} ?: "")
                                    amountList.add(response.body().earningsUserListModel!![i].amount?.takeUnless {it.isEmpty()} ?: "")
                                    dateList.add(response.body().earningsUserListModel!![i].date?.takeUnless {it.isEmpty()} ?: "")
                                }
                            }
                            if (nameList.size >= 1) {
                                binding.textiew.visibility = View.VISIBLE
                            } else {
                                binding.textiew.visibility = View.GONE
                            }
                            binding.myRecyclerView.adapter = EarningsAdapter(mContext, nameList, amountList, dateList)
                            binding.myRecyclerView.setHasFixedSize(true)
                            binding.myRecyclerView.layoutManager = LinearLayoutManager(mContext)
                        }
                        response.code() == 401 -> {
                            dialog.hideDialog()
                            appPreferences.ClearPreferences()
                            val intent = Intent(mContext, LogInActivity::class.java)
                            (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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
}