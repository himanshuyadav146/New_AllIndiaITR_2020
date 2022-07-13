package dell.com.allindiaitr.utils

import android.content.Context
import android.net.ConnectivityManager

class ConnectionDetector {

    fun isConnectingToInternet(context: Context): Boolean {
        val connectivity = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        return info != null && info.isConnected
    }

}