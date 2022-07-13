package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserListHolder {


    @SerializedName("userList")
    @Expose
    var userList = mutableListOf<NewItrBase>()

    @SerializedName("listData")
    @Expose
    var listData = HashMap<String, List<NewItrBase>>()

    fun getuserList():MutableList<NewItrBase>
    {
        return userList
    }

    fun setuserList(userList: MutableList<NewItrBase>)
    {
        this.userList = userList
    }


    fun getlistData():HashMap<String, List<NewItrBase>>
    {
        return listData
    }

    fun setlistData(listData: HashMap<String, List<NewItrBase>>)
    {
        this.listData = listData
    }

//    @SerializedName("payDetails")
//    @Expose
//    private var payDetails: PayDetails? = null
//
//    fun getPayDetails(): PayDetails? {
//        return payDetails
//    }
//
//    fun setPayDetails(payDetails: PayDetails) {
//        this.payDetails = payDetails
//    }


    private constructor (){
    }
    companion object {
        val instance: UserListHolder by lazy { UserListHolder() }
    }


}


