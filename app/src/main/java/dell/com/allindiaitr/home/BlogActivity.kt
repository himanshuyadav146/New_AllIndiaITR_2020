package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.BlogsAdapter
import dell.com.allindiaitr.databinding.ActivityBlogBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.MoreModel
import dell.com.allindiaitr.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlogActivity : AppCompatActivity() {

    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    var titleList = arrayListOf<String>()
    var urlList = arrayListOf<String>()
    lateinit var binding: ActivityBlogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(this)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = "Blogs"

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getBlogs()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
    }

    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }


    private fun getBlogs(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getBlogs()
        call.enqueue(object : Callback<List<MoreModel>> {

            override fun onResponse(call: Call<List<MoreModel>>?, response: Response<List<MoreModel>>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()

                        for (i in 0 until response.body().size) {
                            titleList.add(if (response.body()[i].metaTitle == null) "" else response.body()[i].metaTitle.toString())
                            urlList.add(if (response.body()[i].blogKey == null) "" else response.body()[i].blogKey.toString())
                        }
                        binding.myRecyclerView.adapter = BlogsAdapter(mContext, titleList, urlList)
                        binding.myRecyclerView.setHasFixedSize(true)
                        binding.myRecyclerView.layoutManager = LinearLayoutManager(mContext)
                    }
                    else {
                        dialog.hideDialog()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MoreModel>>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home){
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}
