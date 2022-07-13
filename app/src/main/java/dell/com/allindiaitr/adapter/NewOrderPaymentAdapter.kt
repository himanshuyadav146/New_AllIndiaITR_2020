//package dell.com.allindiaitr.adapter
//
//import android.content.Context
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseAdapter
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.models.ModelSelectedPackages
//import dell.com.allindiaitr.models.PackageAddOnsList
//import dell.com.allindiaitr.utils.AppPreferences
//import android.view.LayoutInflater
//import androidx.recyclerview.widget.RecyclerView.ViewHolder
//
//class NewOrderPaymentAdapter(private val mContext: Context, private val addOnsList: List<PackageAddOnsList>?) : BaseAdapter() {
//
//    private var appPreferences: AppPreferences? = AppPreferences(mContext)
//    private var modelAddonpackage: ModelSelectedPackages? = null
//    private val modelSelectedPackagesArrayList = ArrayList<ModelSelectedPackages>()
//    private var selectedAddOnPackageID: String? = null
//    private val inflater: LayoutInflater? = null
//
//    override fun getView(p0: Int, convertView: View?, parent: ViewGroup?): View? {
//        //convertView = inflater?.inflate(dell.com.allindiaitr.R.layout.card_order_payment, null, false)
//         View.inflate(mContext, dell.com.allindiaitr.R.layout.card_order_payment,parent)
//
//        val viewHolder: ViewHolder
//
//
//return null
//    }
//
//    override fun getItem(p0: Int): Any {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getItemId(p0: Int): Long {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getCount(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}