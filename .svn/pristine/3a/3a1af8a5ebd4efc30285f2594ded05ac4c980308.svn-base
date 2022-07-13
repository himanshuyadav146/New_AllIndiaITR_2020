package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelSelectedPackages  {

    private val instance: ModelSelectedPackages? = null
    @SerializedName("listdata")
    @Expose
    private lateinit var listdata: List<List<ModelSelectedPackages>>

    // private List<List<Group_Model>> mList_parent=new ArrayList<>();
    @SerializedName("PackageName")
    @Expose
    private lateinit var packageName: String
    @SerializedName("PackageId")
    @Expose
    private lateinit var PackageId: String

    @SerializedName("PackageAmount")
    @Expose
    private lateinit var packageAmount: String

    fun getPackageName(): String {
        return packageName
    }

    fun setPackageName(packageName: String) {
        this.packageName = packageName
    }

    fun getPackageId(): String {
        return PackageId
    }

    fun setPackageId(packageId: String) {
        this.PackageId = packageId
    }

    fun getPackageAmount(): String {
        return packageAmount
    }

    fun setPackageAmount(packageAmount: String) {
        this.packageAmount = packageAmount
    }

    fun getListdata(): List<List<ModelSelectedPackages>> {
        return listdata
    }

    fun setListdata(listdata: List<List<ModelSelectedPackages>>) {
        this.listdata = listdata
    }
}