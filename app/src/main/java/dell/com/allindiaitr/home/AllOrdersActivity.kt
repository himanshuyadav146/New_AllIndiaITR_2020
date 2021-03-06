package dell.com.allindiaitr.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.AllOrdersAdapter
import dell.com.allindiaitr.databinding.ActivityAllOrdersBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.AllOrderStatusModel
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllOrdersActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    private var appPreferences: AppPreferences? = null
    lateinit var no_order: LinearLayout
    lateinit var recycler_view: RecyclerView
    var newItrBase = NewItrBase.instance

    lateinit var binding: ActivityAllOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAllOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(mContext)
        binding.toolbar.toolbarTitle.text = getString(R.string.title_allorder)

        no_order = findViewById(R.id.no_order)
        recycler_view = findViewById(R.id.recycler_view)

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(mContext)

        binding.bottomNavigationView.selectedItemId = R.id.navigation_orders
        var menuView: BottomNavigationMenuView = binding.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount){
            var iconView = menuView.getChildAt(i).findViewById<View>(R.id.icon)
            if (iconView!=null){
                var layoutParams = iconView.layoutParams
                var displayMetrics = resources.displayMetrics
                layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18F, displayMetrics).toInt()
                layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18F, displayMetrics).toInt()
                iconView.layoutParams = layoutParams
            }

        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            if (!ConnectionDetector().isConnectingToInternet(mContext)){
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                return@setOnNavigationItemSelectedListener false
            }
            when (it.itemId) {
                R.id.navigation_home -> {
                    newItrBase.orderMode = "Orders"
                    val intent = Intent(mContext, DashboardActivity::class.java)
                    this@AllOrdersActivity.finish()
                    finish()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_orders -> return@setOnNavigationItemSelectedListener false
                R.id.navigation_refer -> {
                    newItrBase.orderMode = "Orders"
                    val intent = Intent(mContext, ReferAndEarnActivity::class.java)
                    this@AllOrdersActivity.finish()
                    finish()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                   // finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_more-> {
                    newItrBase.orderMode = "Orders"
                    val intent = Intent(mContext, MoreActivity::class.java)
                    this@AllOrdersActivity.finish()
                    finish()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivityForResult(intent, 0)
                    overridePendingTransition(0, 0)
                   // finish()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getOrder()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.navigation_orders
    }


    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }


    override fun onBackPressed() {
        val intent = Intent(mContext, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivityForResult(intent, 0)
        overridePendingTransition(0, 0)
        finish()
    }

    private fun getOrder() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getAllOrder(appPreferences?.parentId.toString())
        call.enqueue(object : Callback<List<AllOrderStatusModel>> {
            override fun onResponse(
                call: Call<List<AllOrderStatusModel>>?,
                response: Response<List<AllOrderStatusModel>>?
            ) {
                try {
                    if (response!!.isSuccessful){
                        dialog.hideDialog()
                        if (response.code() == 200){
                            if (response.body() != null) {
                                val packageModelList = response.body()
                                no_order.visibility = View.GONE
                                recycler_view.visibility = View.VISIBLE
                                recycler_view.adapter = AllOrdersAdapter(mContext, packageModelList)
                            }
                            else {
                                no_order.visibility = View.VISIBLE
                                recycler_view.visibility = View.GONE
                            }
                        }
                        else if (response.code() == 204){
                            no_order.visibility = View.VISIBLE
                            recycler_view.visibility = View.GONE
                        }
                    }
                    else {
                        dialog.hideDialog()
                        no_order.visibility = View.VISIBLE
                        recycler_view.visibility = View.GONE
                    }
                }
                catch (e: Exception){
                    dialog.hideDialog()
                    no_order.visibility = View.VISIBLE
                    recycler_view.visibility = View.GONE
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AllOrderStatusModel>>?, t: Throwable?) {
                dialog.hideDialog()
                no_order.visibility = View.VISIBLE
                recycler_view.visibility = View.GONE
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }
}