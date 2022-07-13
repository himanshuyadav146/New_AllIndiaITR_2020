//package dell.com.allindiaitr.usefultools
//
//import android.app.Activity
//import android.content.Context
//import android.os.Bundle
//import android.view.MenuItem
//import android.view.inputmethod.InputMethodManager
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.models.UsefulToolsModel
//import dell.com.allindiaitr.utils.AppPreferences
//import dell.com.allindiaitr.utils.MyContextWrapper
//import kotlinx.android.synthetic.main.activity_hra_calculator_result.*
//import kotlinx.android.synthetic.main.app_toolbar.*
//
//class HraCalculatorResultActivity : AppCompatActivity() {
//
//    lateinit var mContext: Context
//    var usefulToolsModel = UsefulToolsModel.instance
//    private var appPreferences: AppPreferences? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_hra_calculator_result)
//
//        mContext = this
//
//        setSupportActionBar(toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar_title.text =getString(R.string.title_calculated_hra)
//
//        hraChargeable.text = usefulToolsModel.hraChargeableTax.toString()
//        percentageOfBasicSalary.text = usefulToolsModel.percentage.toString() + " of Basic Salary"
//        basicSalary.text = "\u20B9 " + usefulToolsModel.basicSalary.toString()
//        hraReceived.text = "\u20B9 " + usefulToolsModel.hra.toString()
//        rentPaid.text = "\u20B9 " + usefulToolsModel.rentPaid.toString()
//        exemptedHra.text = "\u20B9 " + usefulToolsModel.hraExemption.toString()
//        hraChargeableTax.text = "\u20B9 " + usefulToolsModel.hraChargeableTax.toString()
//
//        calculate_again_button.setOnClickListener {
//            finish()
//            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//        }
//    }
//
//    override fun attachBaseContext(newBase:Context?){
//        mContext= newBase!!
//        appPreferences= AppPreferences(newBase!!)
//        val lang:String=appPreferences?.selectedLanguage!!
//        //  setAppLocal(lang)
//        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))
//
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        var id = item!!.itemId
//        if (id == android.R.id.home){
//            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
//            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//            finish()
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//    }
//}
