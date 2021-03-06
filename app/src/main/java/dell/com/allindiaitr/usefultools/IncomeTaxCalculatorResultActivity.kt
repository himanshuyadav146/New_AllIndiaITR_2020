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
//import kotlinx.android.synthetic.main.activity_income_tax_calculator_result.*
//import kotlinx.android.synthetic.main.app_toolbar.*
//
//class IncomeTaxCalculatorResultActivity : AppCompatActivity() {
//
//    var usefulToolsModel = UsefulToolsModel.instance
//    var taxpayable: String = 0.toString()
//    var refund: String = 0.toString()
//    lateinit var mContext: Context
//    private var appPreferences: AppPreferences? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_income_tax_calculator_result)
//
//        setSupportActionBar(toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar_title.text = getString(R.string.title_calculated_result)
//
//        mContext = this
//
//        if (usefulToolsModel.totalTax >= 0){
//            taxpayable = if (usefulToolsModel.totalLibility == null)
//                0.toString()
//            else
//                usefulToolsModel.totalLibility.toString()
//            taxStatusTextView.text = taxpayable.toString()
//            tv_refund_payable.text = "You have tax payable of"
//            taxPayableTextView.text = "Tax Payable"
//            finalResultTextView.text = "\u20B9 " + usefulToolsModel.totalLibility.toString()
//        }
//        else {
//            refund = usefulToolsModel.refund.toString()
//            taxStatusTextView.text = refund.toString()
//            tv_refund_payable.text = "You have a refund of"
//            taxPayableTextView.text = "Refund"
//            finalResultTextView.text = "\u20B9 " + usefulToolsModel.refund.toString()
//        }
//
//        totalIncomeTextView.text = "\u20B9 " + usefulToolsModel.totaltaxablesalary.toString()
//        deductionTextView.text = "\u20B9 " + usefulToolsModel.totalDeduction.toString()
//        netTaxableIncomeTextView.text = "\u20B9 " + usefulToolsModel.totalBasicSalary.toString()
//        totalTaxTextView.text = "\u20B9 " + usefulToolsModel.taxPayableAmount.toString()
//        taxPaidTextView.text = "\u20B9 " + usefulToolsModel.netTaxableIncome.toString()
//
//        calculate_again_button.setOnClickListener {
//            finish()
//            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//        }
//    }
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
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//    }
//}
