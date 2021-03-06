package dell.com.allindiaitr.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.submitDocument.ERStatusActivity
import dell.com.allindiaitr.submitDocument.ITROrderStatusActivity
import dell.com.allindiaitr.submitDocument.SourceOfIncomeActivity
import dell.com.allindiaitr.models.*
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.ConnectionDetector
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("NAME_SHADOWING")
class UserListExpandableAdapter(
    private val mContext: Context,
    private val expandable_list_view: ExpandableListView,
    private val listData: HashMap<String, List<NewItrBase>>,
    private val userList: List<NewItrBase>
) : BaseExpandableListAdapter() {

    private var appPreferences: AppPreferences? = AppPreferences(mContext)
    val apI_Interface = APIClient().getClient().create(API_Interface::class.java)
    var newItrBase = NewItrBase.instance

    override fun getGroup(groupPosition: Int): Any {
        return this.userList[groupPosition].panNumber.toString()
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView: View? = convertView
        val listTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val layoutInflater =
                this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_parent_view, null)
        }
        val tv_pannumber = convertView!!.findViewById<TextView>(R.id.tv_pannumber)
//        tv_pannumber.text = "PAN No.: ${listTitle.toString()}"
        tv_pannumber.text = mContext.getString(R.string.user_pan) + "${listTitle.toString()}"

        val tv_username = convertView.findViewById<TextView>(R.id.tv_username)
        tv_username.text = userList[groupPosition].name.toString()

        val tv_dob = convertView.findViewById<TextView>(R.id.tv_dob)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val parseDate: Date = simpleDateFormat.parse(userList[groupPosition].dateOfBirth.toString())
        val dob = SimpleDateFormat("dd/MM/yyyy").format(parseDate)
//        tv_dob.text = "DOB: ${dob.toString()}"
        tv_dob.text = mContext.getString(R.string.user_dob) + "${dob.toString()}"

        val start_filing_button = convertView.findViewById<Button>(R.id.start_filing_button)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            start_filing_button.backgroundTintList = mContext.getColorStateList(R.color.blue)
        else
            ViewCompat.setBackgroundTintList(
                start_filing_button,
                ColorStateList.valueOf(Color.rgb(75, 151, 255))
            )

        start_filing_button.setOnClickListener {
            newItrBase.isNewUser = false
//            newItrBase.isNewItr = true
            newItrBase.id =
                listData[userList[groupPosition].panNumber.toString()]!![0].userAssessmentYearId
            newItrBase.userId = appPreferences!!.parentId
//            newItrBase.processMode = CommonVal.SubmitDocument.name
            newItrBase.processMode = listData[userList[groupPosition].panNumber.toString()]!![0].processMode

            Log.e("val last asses id ", " test ... " + listData[userList[groupPosition].panNumber.toString()]!![0].processMode)

            if (ConnectionDetector().isConnectingToInternet(mContext)) {
                getChooseUserForNewITR()
            } else {
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /*convertView.setOnClickListener {
            if (expandable_list_view.isGroupExpanded(groupPosition))
                expandable_list_view.collapseGroup(groupPosition)
            else
                expandable_list_view.expandGroup(groupPosition)
        }*/

        convertView.setOnClickListener {
            if (expandable_list_view.isGroupExpanded(groupPosition)){
                expandable_list_view.collapseGroup(groupPosition)
            }
            else
            {
                expandable_list_view.expandGroup(groupPosition)
                notifyDataSetChanged()
            }

        }

        expandable_list_view.dividerHeight = 20

        return convertView

    }

    override fun getChildrenCount(groupPosition: Int): Int {
        try {
            if (this.userList.size >= groupPosition) {
                return this.listData[this.userList[groupPosition].panNumber]!!.size

            } else {
                return 0
            }
        } catch (e: Exception) {
            return 0
        }

    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.listData[this.userList[groupPosition].panNumber]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView: View? = convertView
        if (convertView == null) {
            val layoutInflater =
                this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_child_view, null)
        }
        val userAssessmentYearTv =
            convertView!!.findViewById<TextView>(R.id.user_assessment_year_tv)
        val tvCreatedTime = convertView.findViewById<TextView>(R.id.tv_created_time)
        val viewButton = convertView.findViewById<Button>(R.id.view_button)
        val statusIconImg = convertView.findViewById<ImageView>(R.id.status_icon_img)
        val statusTv = convertView.findViewById<TextView>(R.id.status_tv)

        val panNumber = userList[groupPosition].panNumber.toString()
        val paymentStatus = listData[panNumber]!![childPosition].paymentStatus
        val isAckGenerated = listData[panNumber]!![childPosition].isAckGenerated
        val processMode = listData[panNumber]!![childPosition].processMode

        userAssessmentYearTv.text = listData[panNumber]!![childPosition].assestmentYear
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val parseDate: Date =
            simpleDateFormat.parse(listData[panNumber]!![childPosition].createdDate)
        tvCreatedTime.text = "${SimpleDateFormat("EEEE").format(parseDate).toString()}, " +
                "${SimpleDateFormat("dd").format(parseDate).toString()} " +
                "${SimpleDateFormat("MMMM", Locale.ENGLISH).format(parseDate).toString()} " +
                "${SimpleDateFormat("yyyy").format(parseDate).toString()} " +
                "${SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(parseDate).toString()}"

        val status: String

        if (processMode.equals(CommonVal.SubmitDocument.name)) {
            if (paymentStatus == false && isAckGenerated == false) {
//                status = "Continue Filing"
                status = mContext.getString(R.string.user_btn_continue_filing)
                convertView.isClickable = false
                statusIconImg.visibility = View.INVISIBLE
                statusTv.visibility = View.INVISIBLE
                viewButton.visibility = View.VISIBLE
                viewButton.text = status
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.dark_yellow)
                else
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(255, 171, 42))
                    )
                viewButton.setOnClickListener {
                    Log.e("test 00","");
                    newItrBase.selectedUser_userAssessmentYearUserID =
                        listData[panNumber]!![childPosition].userAssessmentYearId
                    newItrBase.isNewUser = false
                    //new added
//                    newItrBase.isNewItr = false
//                    newItrBase.processMode = CommonVal.SubmitDocument.name
                    newItrBase.processMode = listData[panNumber]!![childPosition].processMode
                    newItrBase.selectedUserEmail = userList[groupPosition].emailId.toString()
                    newItrBase.selectedUserMobile = userList[groupPosition].phoneNumber.toString()
                    newItrBase.selectedUserName = userList[groupPosition].name.toString()
                    val intent = Intent(mContext, SourceOfIncomeActivity::class.java)
                    mContext.startActivity(intent)
                }
            } else if (paymentStatus == true && isAckGenerated == true) {
//                status = "E-Filed"
                status = mContext.getString(R.string.user_e_field)
                convertView.isClickable = false
                viewButton.visibility = View.VISIBLE
                //  viewButton.text = "View Details"
                viewButton.text = mContext.getString(R.string.user_btn_viewdetails)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
                    statusTv.setTextColor(mContext.getColor(R.color.colorPrimary))
                } else {
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(0, 184, 148))
                    )
                    statusTv.setTextColor(Color.parseColor("#00B894"))
                }
                statusIconImg.visibility = View.VISIBLE
                statusIconImg.setImageResource(R.drawable.ic_e_filed)
                statusTv.visibility = View.VISIBLE
                statusTv.text = status
                viewButton.setOnClickListener {
                    newItrBase.itrStatus = "E-Filed"
                    newItrBase.isNewUser = false
                    newItrBase.selectedUserEmail = userList[groupPosition].emailId.toString()
                    newItrBase.selectedUserMobile = userList[groupPosition].phoneNumber.toString()
                    newItrBase.eVerifyPaymentDone =
                        listData[panNumber]!![childPosition].eVerifyPaymentDone
                    newItrBase.acknowledgementNo =
                        listData[panNumber]!![childPosition].acknowledgementNo
                    newItrBase.selectedUser_userAssessmentYearUserID =
                        listData[panNumber]!![childPosition].userAssessmentYearId
                    newItrBase.selectedUserName = userList[groupPosition].name.toString()
                    val intent = Intent(mContext, ITROrderStatusActivity::class.java)
                    mContext.startActivity(intent)
                }
            } else if (paymentStatus == true && isAckGenerated == false) {
//                status = "ITR Inprogress"
                status = mContext.getString(R.string.user_itr_inprogress)
                convertView.isClickable = false
                viewButton.visibility = View.VISIBLE
//                viewButton.text = "View Details"
                viewButton.text = mContext.getString(R.string.user_btn_viewdetails)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
                    statusTv.setTextColor(mContext.getColor(R.color.red))
                } else {
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(0, 184, 148))
                    )
                    statusTv.setTextColor(Color.parseColor("#FF6463"))
                }
                statusIconImg.visibility = View.VISIBLE
                statusIconImg.setImageResource(R.drawable.ic_itr_inprogress)
                statusTv.visibility = View.VISIBLE
                statusTv.text = status
                viewButton.setOnClickListener {
                    newItrBase.itrStatus = "ITR Inprogress"
                    newItrBase.isNewUser = false
                    newItrBase.selectedUser_userAssessmentYearUserID =
                        listData[panNumber]!![childPosition].userAssessmentYearId
                    val intent = Intent(mContext, ITROrderStatusActivity::class.java)
                    mContext.startActivity(intent)
                }
            }
        }
        else if (processMode.equals(CommonVal.RevisedReturn.name) && paymentStatus == true) {
//            status = "Reply to Notice"
            status = mContext.getString(R.string.user_reply_to_notice)
            convertView.isClickable = true
            viewButton.visibility = View.INVISIBLE
            statusIconImg.visibility = View.VISIBLE
            statusTv.visibility = View.VISIBLE
            statusTv.text = status
            if (listData[panNumber]!![childPosition].revisedReturnDone == true) {
                statusIconImg.setImageResource(R.drawable.ic_e_filed)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    statusTv.setTextColor(mContext.getColor(R.color.colorPrimary))
                } else {
                    statusTv.setTextColor(Color.parseColor("#00B894"))
                }
            } else {
                statusIconImg.setImageResource(R.drawable.ic_itr_inprogress)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    statusTv.setTextColor(mContext.getColor(R.color.red))
                } else {
                    statusTv.setTextColor(Color.parseColor("#FF6463"))
                }
            }
            convertView.setOnClickListener {
                newItrBase.processMode = CommonVal.RevisedReturn.name
                if (listData[panNumber]!![childPosition].revisedReturnDone == true) {
                    newItrBase.selectedProcessStatus = CommonVal.Complete.name
                } else {
                    newItrBase.selectedProcessStatus = CommonVal.InProgress.name
                }
                val intent = Intent(mContext, ERStatusActivity::class.java)
                mContext.startActivity(intent)
            }
        }
        else if (processMode.equals(CommonVal.EVerify.name) && paymentStatus == true) {
//            status = "E-Verify"
            status = mContext.getString(R.string.user_e_verify)
            convertView.isClickable = true
            viewButton.visibility = View.INVISIBLE
            statusIconImg.visibility = View.VISIBLE
            statusTv.visibility = View.VISIBLE
            statusTv.text = status
            if (listData[panNumber]!![childPosition].eVerifyDone == true) {
                statusIconImg.setImageResource(R.drawable.ic_e_filed)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    statusTv.setTextColor(mContext.getColor(R.color.colorPrimary))
                } else {
                    statusTv.setTextColor(Color.parseColor("#00B894"))
                }
            } else {
                statusIconImg.setImageResource(R.drawable.ic_itr_inprogress)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    statusTv.setTextColor(mContext.getColor(R.color.red))
                } else {
                    statusTv.setTextColor(Color.parseColor("#FF6463"))
                }
            }
            convertView.setOnClickListener {
                newItrBase.processMode = CommonVal.EVerify.name
                if (listData[panNumber]!![childPosition].eVerifyDone == true) {
                    newItrBase.selectedProcessStatus = CommonVal.Complete.name
                } else {
                    newItrBase.selectedProcessStatus = CommonVal.InProgress.name
                }
                val intent = Intent(mContext, ERStatusActivity::class.java)
                mContext.startActivity(intent)
            }
        }
        //added for manual filling
        else if (processMode.equals(CommonVal.ManualFiling.name)) {
            if (paymentStatus == false && isAckGenerated == false) {
//                status = "Continue Filing"
                status = mContext.getString(R.string.user_btn_continue_filing)
                convertView.isClickable = false
                statusIconImg.visibility = View.INVISIBLE
                statusTv.visibility = View.INVISIBLE
                viewButton.visibility = View.VISIBLE
                viewButton.text = status
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.dark_yellow)
                else
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(255, 171, 42))
                    )
                viewButton.setOnClickListener {
                    Toast.makeText(mContext, "Manual orders will not work", Toast.LENGTH_SHORT).show()
                }
            }
            //new added
            else if (paymentStatus == true && isAckGenerated == true) {
//                status = "E-Filed"
                status = mContext.getString(R.string.user_e_field)
                convertView.isClickable = false
                viewButton.visibility = View.VISIBLE
                //  viewButton.text = "View Details"
                viewButton.text = mContext.getString(R.string.user_btn_viewdetails)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
                    statusTv.setTextColor(mContext.getColor(R.color.colorPrimary))
                } else {
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(0, 184, 148))
                    )
                    statusTv.setTextColor(Color.parseColor("#00B894"))
                }
                statusIconImg.visibility = View.VISIBLE
                statusIconImg.setImageResource(R.drawable.ic_e_filed)
                statusTv.visibility = View.VISIBLE
                statusTv.text = status
                viewButton.setOnClickListener {
                    Toast.makeText(mContext, "Manual orders will not work", Toast.LENGTH_SHORT).show()
                }
            }
            else if (paymentStatus == true && isAckGenerated == false) {
//                status = "ITR Inprogress"
                status = mContext.getString(R.string.user_itr_inprogress)
                convertView.isClickable = false
                viewButton.visibility = View.VISIBLE
//                viewButton.text = "View Details"
                viewButton.text = mContext.getString(R.string.user_btn_viewdetails)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
                    statusTv.setTextColor(mContext.getColor(R.color.red))
                } else {
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(0, 184, 148))
                    )
                    statusTv.setTextColor(Color.parseColor("#FF6463"))
                }
                statusIconImg.visibility = View.VISIBLE
                statusIconImg.setImageResource(R.drawable.ic_itr_inprogress)
                statusTv.visibility = View.VISIBLE
                statusTv.text = status
                viewButton.setOnClickListener {
                    Toast.makeText(mContext, "Manual orders will not work", Toast.LENGTH_SHORT).show()
                }
            }

        } else if (processMode.equals(CommonVal.prefiled.name)) {
            if (paymentStatus == false && isAckGenerated == false) {
//                status = "Continue Filing"
                status = mContext.getString(R.string.user_btn_continue_filing)
                convertView.isClickable = false
                statusIconImg.visibility = View.INVISIBLE
                statusTv.visibility = View.INVISIBLE
                viewButton.visibility = View.VISIBLE
                viewButton.text = status
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.dark_yellow)
                else
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(255, 171, 42))
                    )
                viewButton.setOnClickListener {
                    Toast.makeText(mContext, "Prefiled orders will not work", Toast.LENGTH_SHORT).show()
                }
            }
            //new added
            else if (paymentStatus == true && isAckGenerated == true) {
//                status = "E-Filed"
                status = mContext.getString(R.string.user_e_field)
                convertView.isClickable = false
                viewButton.visibility = View.VISIBLE
                //  viewButton.text = "View Details"
                viewButton.text = mContext.getString(R.string.user_btn_viewdetails)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
                    statusTv.setTextColor(mContext.getColor(R.color.colorPrimary))
                } else {
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(0, 184, 148))
                    )
                    statusTv.setTextColor(Color.parseColor("#00B894"))
                }
                statusIconImg.visibility = View.VISIBLE
                statusIconImg.setImageResource(R.drawable.ic_e_filed)
                statusTv.visibility = View.VISIBLE
                statusTv.text = status
                viewButton.setOnClickListener {
                    Toast.makeText(mContext, "Prefiled orders will not work", Toast.LENGTH_SHORT).show()
                }
            }
            else if (paymentStatus == true && isAckGenerated == false) {
//                status = "ITR Inprogress"
                status = mContext.getString(R.string.user_itr_inprogress)
                convertView.isClickable = false
                viewButton.visibility = View.VISIBLE
//                viewButton.text = "View Details"
                viewButton.text = mContext.getString(R.string.user_btn_viewdetails)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    viewButton.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
                    statusTv.setTextColor(mContext.getColor(R.color.red))
                } else {
                    ViewCompat.setBackgroundTintList(
                        viewButton,
                        ColorStateList.valueOf(Color.rgb(0, 184, 148))
                    )
                    statusTv.setTextColor(Color.parseColor("#FF6463"))
                }
                statusIconImg.visibility = View.VISIBLE
                statusIconImg.setImageResource(R.drawable.ic_itr_inprogress)
                statusTv.visibility = View.VISIBLE
                statusTv.text = status
                viewButton.setOnClickListener {
                    Toast.makeText(mContext, "Prefiled orders will not work", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (isLastChild)
            expandable_list_view.dividerHeight = 20
        else
            expandable_list_view.dividerHeight = 0

        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return this.userList.size
    }

    private fun getChooseUserForNewITR() {
        var dialog = AlertDialogueManager(mContext, mContext.getString(R.string.dilog_pleasewait))

        val gson = Gson()
        val jsonTutsList: String = gson.toJson(newItrBase)
        Log.e("val gson to json ", " test ... " + jsonTutsList)
        Log.e("val gson to json ", " test ... " + newItrBase.processMode)

        val call = apI_Interface.getchooseUserForNewITR(newItrBase)
        call.enqueue(object : Callback<NewItrBase> {
            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                    when {
                        response!!.isSuccessful -> {
//                            Log.e("User assessment id",""+response.body()?.UserAssestmentYearId?.takeUnless { it.isEmpty() } ?: "");
//                            Log.e("Process mode",""+response.body()?.processMode?.takeUnless { it.isEmpty() } ?: "");

                            dialog.hideDialog()
//                            newItrBase.processMode = CommonVal.SubmitDocument.name
//                            newItrBase.processMode = response.body()?.processMode?.takeUnless { it.isEmpty() } ?: ""
                            newItrBase.selectedUser_userAssessmentYearUserID =
                                response.body()?.UserAssestmentYearId?.takeUnless { it.isEmpty() } ?: ""

                            val intent = Intent(mContext, SourceOfIncomeActivity::class.java)
                            mContext.startActivity(intent)
                        }
                        response.code() == 406 -> {
                            dialog.hideDialog()
                            val jObjError = JSONObject(response.errorBody().string())
                            Log.e("jObjError --- ",""+jObjError);
                            AlertDialogueManager().errorMessageDialogue(mContext, jObjError.getString("ReturnMsg"), "Oops")
//                            Toast.makeText(mContext, jObjError.getString("ReturnMsg"), Toast.LENGTH_LONG).show()

                        }
                        else -> dialog.hideDialog()
                    }
                } catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, mContext.resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

}