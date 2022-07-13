//package dell.com.allindiaitr.Multilanguage
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.content.res.Configuration
//import android.os.Bundle
//import android.view.MenuItem
//import android.view.inputmethod.InputMethodManager
//import android.widget.ArrayAdapter
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.gson.GsonBuilder
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.adapter.LanguageAdapter
//import dell.com.allindiaitr.home.DashboardActivity
//import dell.com.allindiaitr.models.LanguageModel
//import dell.com.allindiaitr.utils.AppPreferences
//import dell.com.allindiaitr.utils.MyContextWrapper
//import kotlinx.android.synthetic.main.activity_efiling_personal_info.*
//import kotlinx.android.synthetic.main.activity_language_setting.*
//import kotlinx.android.synthetic.main.activity_user_list.*
//import kotlinx.android.synthetic.main.app_toolbar.*
//import org.json.JSONArray
//import java.io.InputStream
//import java.util.*
//
//
//class LanguageSettingActivity : AppCompatActivity() {
//
//
//    var lang_key_ArrayList = mutableListOf<String>()
//    var lang_ArrayList = mutableListOf<String>()
//    lateinit var mContext: Context
//    private var appPreferences: AppPreferences? = null
//    var languageList:Array<String> = arrayOf("en","hi")
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_language_setting)
//      //  appPreferences = AppPreferences(this)
//        val gson = GsonBuilder().create()
//        val Model= gson.fromJson(readJSONFromAsset(),Array<LanguageModel>::class.java).toList()
//
////        setSupportActionBar(toolbar as Toolbar?)
////        supportActionBar!!.setDisplayShowTitleEnabled(false)
////        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
////        toolbar_title1.text=getString(R.string.title_itr_language)
//       // recycler_view.adapter=LanguageSettingAdapter(mContext,Model)
//
//        setSupportActionBar(toolbar_lang as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar_title.text =getString(R.string.title_itr_language)
//
//        val adaperList=LanguageAdapter(mContext,Model)
//        recycler_view_language.layoutManager= LinearLayoutManager(mContext)
//        recycler_view_language.adapter= adaperList
//
//        spinner.adapter=
//            ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,languageList)
//
//        val lang:String=appPreferences?.selectedLanguage!!
//        val index=languageList.indexOf(lang)
//        if(index>0){
//            spinner.setSelection(index)
//        }
//        button.setOnClickListener {
//
//            appPreferences?.selectedLanguage=languageList[spinner.selectedItemPosition]
//            //startActivity(Intent(this, MoreActivity::class.java))
////            startActivity(Intent(this, LanguageSettingActivity::class.java))
//            startActivity(Intent(this, DashboardActivity::class.java))
//            finish()
//        }
//
//    }
//
//    override fun attachBaseContext(newBase:Context?){
//        mContext= newBase!!
//        appPreferences= AppPreferences(newBase)
//        val lang:String=appPreferences?.selectedLanguage!!
//      //  setAppLocal(lang)
//        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))
//
//    }
//
//
//    private fun readJSONFromAsset(): String? {
//        var json: String?
//        try {
//            val  inputStream: InputStream = resources.openRawResource(R.raw.language)
//            json = inputStream.bufferedReader().use{it.readText()}
//            val jsonArray = JSONArray(json)
//            for (i in 0 until jsonArray.length()) {
//                val jsonObject = jsonArray.getJSONObject(i)
//                lang_key_ArrayList.add(jsonObject.getString("key"))
//                lang_ArrayList.add(jsonObject.getString("language"))
//            }
//
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }
//
//    override fun onBackPressed() {
//        val intent = Intent(mContext, DashboardActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        startActivityForResult(intent, 0)
//        overridePendingTransition(0, 0)
//        finish()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        var id = item!!.itemId
////        if (id == android.R.id.home){
////            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
////            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
////            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
////            finish()
////        }
//
//        val intent = Intent(mContext, DashboardActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        startActivityForResult(intent, 0)
//        overridePendingTransition(0, 0)
//        finish()
//        return super.onOptionsItemSelected(item)
//
//    }
//
//
//}
