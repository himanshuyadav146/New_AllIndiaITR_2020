package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityBlogBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.utils.APIClient
import dell.com.allindiaitr.utils.AlertDialogueManager
import dell.com.allindiaitr.utils.AppPreferences
import dell.com.allindiaitr.utils.MyContextWrapper

class BlogsViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityBlogBinding
    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    private var appPreferences: AppPreferences? = null
    var position: Int = 0
    var urlList = arrayListOf<String>()
    var isWebView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        appPreferences = AppPreferences(this)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = "Blogs"

        position = intent.getIntExtra("position", 0)
        urlList = intent.getStringArrayListExtra("key") as ArrayList<String>
        isWebView = intent.getBooleanExtra("isWebView",false)

        if (isWebView){
            binding.webview.visibility = View.VISIBLE
            binding.myRecyclerView.visibility = View.GONE
        }else{
            binding.webview.visibility = View.GONE
            binding.myRecyclerView.visibility = View.VISIBLE
        }

        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        binding.webview.settings.loadWithOverviewMode = true
        binding.webview.settings.useWideViewPort = true
        binding.webview.settings.domStorageEnabled = true
        binding.webview.settings.loadsImagesAutomatically = true
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.webview.settings.builtInZoomControls = true

        binding.webview.webViewClient = object: WebViewClient(){
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            }

            override fun onPageFinished(view: WebView, url: String) {
                dialog.hideDialog()
            }
        }

        binding.webview.loadUrl("https://blog.allindiaitr.com/" + urlList[position])
        binding.webview.settings.loadWithOverviewMode = true
        binding.webview.settings.useWideViewPort = true
    }


    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            if (binding.webview.canGoBack()) {
                binding.webview.goBack()
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            } else {
                super.onBackPressed()
                finish()
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.webview.canGoBack()) {
            binding.webview.goBack()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        } else {
            super.onBackPressed()
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }
    }
}
