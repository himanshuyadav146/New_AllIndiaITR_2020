//package dell.com.allindiaitr.manualProcess
//
//import android.app.Activity
//import android.content.Context
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.MenuItem
//import android.view.inputmethod.InputMethodManager
//import androidx.appcompat.widget.Toolbar
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.interfaces.API_Interface
//import dell.com.allindiaitr.utils.AppPreferences
//import kotlinx.android.synthetic.main.activity_contact_us.*
//import kotlinx.android.synthetic.main.activity_contact_us.toolbar
//import kotlinx.android.synthetic.main.activity_manual__detais.*
//import kotlinx.android.synthetic.main.app_toolbar.*
//
//class Manual_DetaisActivity : AppCompatActivity() {
//
//    lateinit var apI_Interface : API_Interface
//    lateinit var mContext: Context
//    private var appPreferences: AppPreferences? = null
//    private var incomeFromSalary:Int=0
//    private var house_property:Int=0
//    private var total_deduction:Int=0
//    private var tds:Int=0
//    private var totalTaxableIncome:Int=0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_manual__detais)
//
//        mContext=this
//        appPreferences = AppPreferences(this)
//        setSupportActionBar(toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar_title.text =getString(R.string.mainual_title_fromdetails)
//
//
//        var intent=getIntent()
//        incomeFromSalary=intent.getIntExtra("income from sal",0)
//        house_property=intent.getIntExtra("house property",0)
//        total_deduction=intent.getIntExtra("Deduction",0)
//        tds=intent.getIntExtra("total TDS",0)
//
//        tv_incom_sa2.text=incomeFromSalary.toString()
//        tv_house2.text=house_property.toString()
//        tv_deduction2.text=total_deduction.toString()
//        tv_taxable_tds2.text=tds.toString()
//
//        getCalculation()
//
//    }
//
//    private  fun getCalculation(){
//
//        var totalAddition:Int=incomeFromSalary+house_property
//        var totalDeduction:Int=tv_deduction2.text.toString().toInt()
//       // totalTaxableIncome=incomeFromSalary-(house_property+total_deduction)
//        totalTaxableIncome=totalAddition-totalDeduction
//        tv_taxable_income2.text=totalTaxableIncome.toString()
//    }
//
//
//
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
//
//
//}
