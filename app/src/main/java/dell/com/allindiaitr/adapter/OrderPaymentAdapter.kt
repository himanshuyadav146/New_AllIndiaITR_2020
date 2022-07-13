package dell.com.allindiaitr.adapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.CardOrderPaymentBinding
import dell.com.allindiaitr.holders.CardOrderPaymentViewHolder
import dell.com.allindiaitr.models.ModelSelectedPackages
import dell.com.allindiaitr.models.PackageAddOnsList
import dell.com.allindiaitr.utils.AppPreferences
import java.util.ArrayList

class OrderPaymentAdapter(private val mContext:Context, private val addOnsList: List<PackageAddOnsList>?): RecyclerView.Adapter<CardOrderPaymentViewHolder>(){
    private var appPreferences: AppPreferences? = AppPreferences(mContext)
    private var modelAddonpackage: ModelSelectedPackages? = null
    private val modelSelectedPackagesArrayList = ArrayList<ModelSelectedPackages>()
    private var selectedAddOnPackageID: String? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CardOrderPaymentViewHolder {
        //return CardOrderPaymentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_order_payment, p0, false))
        val binding = CardOrderPaymentBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return CardOrderPaymentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (addOnsList != null) {
            addOnsList.size
        }else{
            0
        }
    }

    override fun onBindViewHolder(holder: CardOrderPaymentViewHolder, position: Int) {
        holder.mTv_package_title.text= addOnsList?.get(position)!!.packageTitle
        holder.mTv_package_cost.text= "\u20B9 "+addOnsList.get(position).packageAmount.toString()
//        holder.mSwitch_package.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener())
        holder.mSwitch_package.setOnCheckedChangeListener { _, isChecked ->
            selectedAddOnPackageID = addOnsList[position].id
            if (isChecked) {
                modelAddonpackage = ModelSelectedPackages()
                val packageName = addOnsList.get(position).packageName
                val packageId = addOnsList.get(position).id
                val packageAmount = addOnsList.get(position).packageAmount
                modelAddonpackage!!.setPackageName(packageName.toString())
                modelAddonpackage!!.setPackageId(packageId.toString())
                modelAddonpackage!!.setPackageAmount(packageAmount.toString())
                modelSelectedPackagesArrayList.add(modelAddonpackage!!)

                val bundle = Bundle()
                bundle.putSerializable("packages", modelSelectedPackagesArrayList)
                val intent = Intent("custom-message")
                intent.putExtras(bundle)
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)

            } else {

                    if(modelSelectedPackagesArrayList.size>0)
                    {
                        modelSelectedPackagesArrayList.removeAt(
                            getPosition(
                                selectedAddOnPackageID.toString(), modelSelectedPackagesArrayList
                            )
                        )
                        val bundle = Bundle()
                        bundle.putSerializable("packages", modelSelectedPackagesArrayList)
                        val intent1 = Intent("custom-message")
                        intent1.putExtras(bundle)
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent1)
                    }
            }

            modelAddonpackage!!.setListdata(
                listOf<List<ModelSelectedPackages>>(
                    modelSelectedPackagesArrayList
                )
            )
        }

    }


    private fun getPosition(
        search: String,
        modelSelectedPackagesArrayList: List<ModelSelectedPackages>
    ): Int {
        var pos = 0
        for (i in modelSelectedPackagesArrayList.indices) {
            if (modelSelectedPackagesArrayList[i].getPackageId().contains(search)) {
                pos = i
            }
        }
        return pos
    }


}