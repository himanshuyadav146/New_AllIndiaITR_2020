package dell.com.allindiaitr.submitDocument

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.FragmentImpDetailsEnterBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import dell.com.allindiaitr.volley.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class ImpDetailsEnterFragment : Fragment() {

    var stateIdArrayList = mutableListOf<String>()
    var stateNameArrayList = mutableListOf<String>()
    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    var newItrBase = NewItrBase.instance
    lateinit var appPreferences: AppPreferences
    lateinit var mView: View
    lateinit var binding: FragmentImpDetailsEnterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImpDetailsEnterBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext = requireContext()
        appPreferences = AppPreferences(mContext)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

        if (ConnectionDetector().isConnectingToInternet(mContext)) {
            readJSONFromAsset()
            getAadhaarDetails()
        }
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()

        binding.contButton.setOnClickListener {
            if (Validation().isFatherNameValid(binding.fatherNameField.text.toString(), binding.fatherNameField) &&
                Validation().isAadhaarValid(binding.aadhaarField.text.toString(), binding.aadhaarField) &&
                Validation().isAddressValid(binding.addOneField.text.toString(), binding.addOneField) &&
                Validation().isAddressValid(binding.addTwoField.text.toString(), binding.addTwoField) &&
                Validation().isStateValid(binding.stateSpinner.selectedItem.toString(), mContext) &&
                Validation().isCityValid(binding.cityField.text.toString(), binding.cityField) &&
                Validation().isPinValid(binding.pinCodeField.text.toString(), binding.pinCodeField)){

                if (ConnectionDetector().isConnectingToInternet(mContext)) {
                    newItrBase.UserAssestmentYearId = newItrBase.selectedUser_userAssessmentYearUserID.toString()
                    newItrBase.userId = appPreferences.parentId
                    newItrBase.fatherName = binding.fatherNameField.text.toString()
                    newItrBase.aadharCardNumber = binding.aadhaarField.text.toString()
                    newItrBase.address1 = binding.addOneField.text.toString()
                    newItrBase.address2 = binding.addTwoField.text.toString()
                    newItrBase.stateId = binding.stateSpinner.selectedItemPosition.toString()
                    newItrBase.city = binding.cityField.text.toString()
                    newItrBase.pincode = binding.pinCodeField.text.toString()
                    postAadhaarDetails()
                }
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

//        mView.viewTreeObserver.addOnGlobalLayoutListener {
//            val heightDiff = mView.rootView.height - mView.height
//            if (heightDiff > dpToPx(mContext, 270f)) { // if more than 200 dp, it's probably a keyboard...
//                binding.contButton.visibility = View.GONE
//            } else {
//                binding.contButton.visibility = View.VISIBLE
//            }
//        }
    }




    private fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    private fun readJSONFromAsset(): String? {
        var json: String?
        try {
            val  inputStream: InputStream = resources.openRawResource(R.raw.state)
            json = inputStream.bufferedReader().use{it.readText()}
            stateNameArrayList.add(0, "Select State")
            stateIdArrayList.add(0,"0")
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                stateNameArrayList.add(jsonObject.getString("State"))
                stateIdArrayList.add(jsonObject.getString("Id"))
            }
            binding.stateSpinner.adapter = ArrayAdapter<String>(mContext, R.layout.spinner_item, stateNameArrayList)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun getAadhaarDetails(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getAadhaarDetails(newItrBase.selectedUser_userAssessmentYearUserID.toString())
        call.enqueue(object : Callback<NewItrBase> {
            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()

                        var fatherName = response.body().fatherName?.takeUnless {it.isEmpty()} ?: ""
                        var aadhaarCardNumber = response.body().aadharCardNumber?.takeUnless {it.isEmpty()} ?: ""
                        var add1 = response.body().address1?.takeUnless {it.isEmpty()} ?: ""
                        var add2 = response.body().address2?.takeUnless {it.isEmpty()} ?: ""
                        var stateId = response.body().stateId?.takeUnless {it.isEmpty()} ?: ""
                        var city = response.body().city?.takeUnless {it.isEmpty()} ?: ""
                        var pinCode = response.body().pinCode?.takeUnless {it.isEmpty()} ?: ""

                        binding.fatherNameField.setText(fatherName)
                        binding.aadhaarField.setText(aadhaarCardNumber)
                        binding.addOneField.setText(add1)
                        binding.addTwoField.setText(add2)
                        binding.cityField.setText(city)
                        binding.pinCodeField.setText(pinCode)

                        for (i in 0 until stateIdArrayList.size){
                            var id = stateIdArrayList[i]
                            if (id == stateId) {
                                binding.stateSpinner.setSelection(i)
                                break
                            }
                        }
                    }
                    else{
                        dialog.hideDialog()
                        Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

//    private fun postAadhaarDetails() {
//        var dialog = AlertDialogueManager(mContext,"Please Wait")
//
//        val call = apI_Interface.postAadhaarDetails(newItrBase)
//        call.enqueue(object : Callback<NewItrBase> {
//            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
//                try {
//                    if (response!!.isSuccessful) {
//                        dialog.hideDialog()
//
//                        val intent = Intent(mContext, BankListActivity::class.java)
//                        (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
//                        startActivity(intent)
//                    }
//                    else {
//                        dialog.hideDialog()
//                        Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//                    }
//                }
//                catch (e: Exception) {
//                    dialog.hideDialog()
//                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
//                dialog.hideDialog()
//                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }


    private fun postAadhaarDetails() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
        val path = APIClient().BaseUrl + "api/Child/SaveAadhaarCardDetails"
        val params = HashMap<String,String>()
        params["UserAssestmentYearId"] = newItrBase.UserAssestmentYearId.toString()
        params["UserId"] = newItrBase.userId.toString()
        params["FatherName"] = newItrBase.fatherName.toString()
        params["AadhaarCardNumber"] = newItrBase.aadharCardNumber.toString()
        params["Address1"] = newItrBase.address1.toString()
        params["Address2"] = newItrBase.address2.toString()
        params["City"] = newItrBase.city.toString()
        params["StateId"] = newItrBase.stateId.toString()
        params["PinCode"] = newItrBase.pincode.toString()

        val jsonObject = JSONObject(params as Map<*, *>?)

        val request = JsonObjectRequest(path, jsonObject, com.android.volley.Response.Listener { dialog.hideDialog()
                val intent = Intent(mContext, BankListActivity::class.java)
//                (mContext as Activity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent)

            }, com.android.volley.Response.ErrorListener {
                Log.e("Error", it.toString())
                dialog.hideDialog()
            })

        // Volley request policy, only one time request to avoid duplicate transaction
        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the volley post request to the request queue
        VolleySingleton.getInstance(mContext).addToRequestQueue(request)

    }
}