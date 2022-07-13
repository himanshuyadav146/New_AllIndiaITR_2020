//package dell.com.allindiaitr.usefultools
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.text.TextUtils
//import android.view.MenuItem
//import android.view.View
//import android.view.inputmethod.InputMethodManager
//import android.widget.ArrayAdapter
//import android.widget.SpinnerAdapter
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.interfaces.API_Interface
//import dell.com.allindiaitr.models.UsefulToolsModel
//import dell.com.allindiaitr.utils.*
//import kotlinx.android.synthetic.main.activity_income_tax_calculator.*
//import kotlinx.android.synthetic.main.app_toolbar.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.text.ParseException
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.TimeUnit
//
//class IncomeTaxCalculatorActivity : AppCompatActivity(), View.OnClickListener {
//
//    lateinit var apI_Interface : API_Interface
//    lateinit var mContext: Context
//    private val assessmentYear = arrayOf("2020-2021" ,"2019-2020", "2018-2019", "2017-2018")
//    private val residentialStatus = arrayOf("Resident", "Non-Resident")
//    lateinit var assessmentYearString: String
//    lateinit var ageString: String
//    lateinit var date1: Date
//    lateinit var date2: Date
//    var diff: Long = 0
//    lateinit var residentialStatusString: String
//    var days = 0
//    var years = 0
//    lateinit var section80TTAString: String
//    lateinit var section80TTBString: String
//    var totaltaxablesalary: Float = 0.toFloat()
//    var totalDeduction: Float = 0.toFloat()
//    var totalBasicSalary: Float = 0.toFloat()
//    var taxPayableAmount: Float = 0.toFloat()
//    var netTaxableIncome: Float = 0.toFloat()
//    var totalPayableTax: Float = 0.toFloat()
//    var interestFromSavings: Float = 0.toFloat()
//    var totalTax: Float = 0.toFloat()
//    var refund: Float = 0.toFloat()
//    lateinit var taxableSalaryString: String
//    lateinit var incomeFromInterestString:String
//    lateinit var interestPaidHomeLoneString: String
//    lateinit var rentalIncomeString: String
//    lateinit var interestPaidLoneString: String
//    lateinit var section80CString: String
//    lateinit var section80GString: String
//    lateinit var section80DString: String
//    lateinit var section80EString: String
//    lateinit var tdsDeductedSalaryString: String
//    lateinit var tdsDeductedOtherSalaryString: String
//    lateinit var totalAdvanceString: String
//    var taxableSalaryFloat: Float = 0.toFloat()
//    var incomeFromInterestFloat: Float = 0.toFloat()
//    var interestPaidHomeLoneFloat: Float = 0.toFloat()
//    var rentalIncomeFloat: Float = 0.toFloat()
//    var interestPaidLoneFloat: Float = 0.toFloat()
//    var section80CFloat: Float = 0.toFloat()
//    var section80TTAFloat: Float = 0.toFloat()
//    var section80TTBFloat: Float = 0.toFloat()
//    var section80GFloat:Float = 0.toFloat()
//    var section80DFloat:Float = 0.toFloat()
//    var section80EFloat:Float = 0.toFloat()
//    var tdsDeductedSalaryFloat:Float = 0.toFloat()
//    var tdsDeductedOtherSalaryFloat:Float = 0.toFloat()
//    var totalAdvanceFloat: Float = 0.toFloat()
//    var totalRentalIncome:Float = 0.toFloat()
//    var totalRebateTax: Float = 0.toFloat()
//    var usefulToolsModel = UsefulToolsModel.instance
//    lateinit var appPreferences: AppPreferences
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_income_tax_calculator)
//
//        mContext = this
//        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
//        appPreferences = AppPreferences(mContext)
//
//        setSupportActionBar(toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar_title.text = getString(R.string.title_income_calculator)
//
//        val assessmentYearAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, assessmentYear)
//        assessmentYearSpinner.adapter = assessmentYearAdapter as SpinnerAdapter?
//
//        val residentialStatusAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, residentialStatus)
//        residentialStatusSpinner.adapter = residentialStatusAdapter
//
//        deducationLayout.setOnClickListener(this)
//        personalInfoLayOut.setOnClickListener(this)
//        tdsDetailsLayout.setOnClickListener(this)
//        incomeDetailLayout.setOnClickListener(this)
//
//        ageTextView.setOnClickListener(this)
//        calculateButton.setOnClickListener(this)
//
//        ButtonVisibility().hideButton(mContext, activity_income_tax_calculator, calculateButton)
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
//    override fun onClick(view: View) {
//        when (view.id) {
//            R.id.personalInfoLayOut -> {
//                deducationLinearLayout.visibility = View.GONE
//                personalInfoLinearLayout.visibility = View.VISIBLE
//                tdsDetailsLinearLayout.visibility = View.GONE
//                incomeDetailLinearLayout.visibility = View.GONE
//
//                personalInfoDownArowImage.visibility = View.INVISIBLE
//                personalInfoUpArrowImage.visibility = View.VISIBLE
//
//                incomeDetailDownArrowImage.visibility = View.VISIBLE
//                incomeDetailUPArrowImage.visibility = View.INVISIBLE
//
//                deductionDownArrowImage.visibility = View.VISIBLE
//                deductionUpArrowImage.visibility = View.INVISIBLE
//
//                tdsDownArrowImage.visibility = View.VISIBLE
//                tdsUpArrowImage.visibility = View.INVISIBLE
//            }
//
//            R.id.incomeDetailLayout -> {
//
//                assessmentYearString = assessmentYearSpinner.selectedItem.toString().trim()
//                if (assessmentYearString == "2019-2020") {
//                    tv_desc_income.visibility = View.VISIBLE
//                } else {
//                    tv_desc_income.visibility = View.GONE
//                }
//
//                deducationLinearLayout.visibility = View.GONE
//                personalInfoLinearLayout.visibility = View.GONE
//                tdsDetailsLinearLayout.visibility = View.GONE
//                incomeDetailLinearLayout.visibility = View.VISIBLE
//                //incomeDetailLinearLayoutString= mContext.incomeDetailLinearLayout.getText().toString();
//
//                personalInfoDownArowImage.visibility = View.VISIBLE
//                personalInfoUpArrowImage.visibility = View.INVISIBLE
//
//                incomeDetailDownArrowImage.visibility = View.INVISIBLE
//                incomeDetailUPArrowImage.visibility = View.VISIBLE
//
//                deductionDownArrowImage.visibility = View.VISIBLE
//                deductionUpArrowImage.visibility = View.INVISIBLE
//
//                tdsDownArrowImage.visibility = View.VISIBLE
//                tdsUpArrowImage.visibility = View.INVISIBLE
//            }
//
//            R.id.deducationLayout -> {
//                ageString = ageTextView.text.toString().trim { it <= ' ' }
//                if (ageString != "") {
//                    val c = Calendar.getInstance()
//                    val df = SimpleDateFormat("dd-MM-yyyy")
//                    val formattedDate = df.format(c.time)
//
//                    try {
//
//                        date1 = df.parse(ageString)
//                        date2 = df.parse(formattedDate)
//
//
//                    } catch (e: ParseException) {
//                        e.printStackTrace()
//                    }
//
//
//                    diff = date2.time - date1.time
//
//                    assessmentYearString = assessmentYearSpinner.selectedItem.toString().trim { it <= ' ' }
//                    residentialStatusString = residentialStatusSpinner.selectedItem.toString().trim { it <= ' ' }
//                    days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
//                    years = days / 365
//                }
//
//                section80TTAString = section80TTAEditText.text.toString().trim { it <= ' ' }
//                section80TTBString = section80TTBEditText.text.toString().trim { it <= ' ' }
//
//                if (section80TTBString.isEmpty()) {
//                    section80TTBEditText.setText(section80TTAString)
//                    section80TTBFloat = section80TTAFloat
//                }
//
//                deducationLinearLayout.visibility = View.VISIBLE
//                personalInfoLinearLayout.visibility = View.GONE
//                incomeDetailLinearLayout.visibility = View.GONE
//                tdsDetailsLinearLayout.visibility = View.GONE
//
//                if (years > 60 && assessmentYearString == "2019-2020") {
//                    deducationLinearLayout_tta.visibility = View.GONE
//                    deducationLinearLayout_ttb.visibility = View.VISIBLE
//                } else {
//                    deducationLinearLayout_tta.visibility = View.VISIBLE
//                    deducationLinearLayout_ttb.visibility = View.GONE
//                }
//
//                personalInfoDownArowImage.visibility = View.VISIBLE
//                personalInfoUpArrowImage.visibility = View.INVISIBLE
//
//                incomeDetailDownArrowImage.visibility = View.VISIBLE
//                incomeDetailUPArrowImage.visibility = View.INVISIBLE
//
//                deductionDownArrowImage.visibility = View.INVISIBLE
//                deductionUpArrowImage.visibility = View.VISIBLE
//
//                tdsDownArrowImage.visibility = View.VISIBLE
//                tdsUpArrowImage.visibility = View.INVISIBLE
//            }
//
//            R.id.tdsDetailsLayout -> {
//                deducationLinearLayout.visibility = View.GONE
//                personalInfoLinearLayout.visibility = View.GONE
//                tdsDetailsLinearLayout.visibility = View.VISIBLE
//                incomeDetailLinearLayout.visibility = View.GONE
//
//                personalInfoDownArowImage.visibility = View.VISIBLE
//                personalInfoUpArrowImage.visibility = View.INVISIBLE
//
//                incomeDetailDownArrowImage.visibility = View.VISIBLE
//                incomeDetailUPArrowImage.visibility = View.INVISIBLE
//
//                deductionDownArrowImage.visibility = View.VISIBLE
//                deductionUpArrowImage.visibility = View.INVISIBLE
//
//                tdsDownArrowImage.visibility = View.INVISIBLE
//                tdsUpArrowImage.visibility = View.VISIBLE
//            }
//
//            R.id.ageTextView -> {
//                DatePicker(mContext, ageTextView, 0).datePickerDialog()
//            }
//
//            R.id.calculateButton -> calculate()
//
//            else -> {
//            }
//        }
//    }
//
//    private fun calculate() {
//        totaltaxablesalary = 0f
//        totalDeduction = 0f
//        totalBasicSalary = 0f
//        taxPayableAmount = 0f
//        netTaxableIncome = 0f
//        totalPayableTax = 0f
//        interestFromSavings = 0f
//        totalTax = 0f
//        refund = 0f
//
//        ageString = ageTextView.text.toString().trim { it <= ' ' }
//        if (ageString != "") {
//            val c = Calendar.getInstance()
//            val df = SimpleDateFormat("dd-MM-yyyy")
//            val formattedDate = df.format(c.time)
//
//            try {
//
//                date1 = df.parse(ageString)
//                date2 = df.parse(formattedDate)
//
//
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
//
//
//            diff = date2.time - date1.time
//
//            assessmentYearString = assessmentYearSpinner.selectedItem.toString().trim { it <= ' ' }
//            residentialStatusString = residentialStatusSpinner.selectedItem.toString().trim { it <= ' ' }
//            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
//            years = days / 365
//        }
//
//        //-------Personal Details Section Start-------//
//        assessmentYearString = assessmentYearSpinner.selectedItem.toString().trim { it <= ' ' }
//        ageString = ageTextView.text.toString().trim { it <= ' ' }
//        residentialStatusString = residentialStatusSpinner.selectedItem.toString().trim { it <= ' ' }
//        //-------Personal Details Section End-------//
//
//        //-------Income Details Section Start-------//
//        taxableSalaryString = taxableSalaryEditText.text.toString().trim { it <= ' ' }
//        incomeFromInterestString = incomeFromInterestEditText.text.toString().trim { it <= ' ' }
//        interestPaidHomeLoneString = interestPaidHomeLoneEditText.text.toString().trim { it <= ' ' }
//        rentalIncomeString = rentalIncomeEditText.text.toString().trim { it <= ' ' }
//        interestPaidLoneString = interestPaidLoneEditText.text.toString().trim { it <= ' ' }
//        //-------Income Details Section End-------//
//
//        //-------Deduction Section Start-------//
//        section80CString = section80CEditText.text.toString().trim { it <= ' ' }
//        section80TTAString = section80TTAEditText.text.toString().trim { it <= ' ' }
//        section80TTBString = section80TTBEditText.text.toString().trim { it <= ' ' }
//        section80GString = section80GEditText.text.toString().trim { it <= ' ' }
//        section80DString = section80DEditText.text.toString().trim { it <= ' ' }
//        section80EString = section80EEditText.text.toString().trim { it <= ' ' }
//        //-------Deduction Section End-------//
//
//        //-------TDS Details Section Start-------//
//        tdsDeductedSalaryString = tdsDeductedSalaryEditText.text.toString().trim { it <= ' ' }
//        tdsDeductedOtherSalaryString = tdsDeductedOtherSalaryEditText.text.toString().trim { it <= ' ' }
//        totalAdvanceString = totalAdvanceEditText.text.toString().trim { it <= ' ' }
//        //-------TDS Details Section End-------//
//
//        if (TextUtils.isEmpty(ageString)) {
//            Toast.makeText(mContext, "Please Choose Your Date of Birth", Toast.LENGTH_SHORT).show()
//        } else if (!TextUtils.isEmpty(ageString)) {
//            if (TextUtils.isEmpty(taxableSalaryString)) {
//                taxableSalaryString = "0"
//            }
//            if (TextUtils.isEmpty(incomeFromInterestString)) {
//                incomeFromInterestString = "0"
//            }
//            if (TextUtils.isEmpty(interestPaidHomeLoneString)) {
//                interestPaidHomeLoneString = "0"
//            }
//            if (TextUtils.isEmpty(rentalIncomeString)) {
//                rentalIncomeString = "0"
//            }
//            if (TextUtils.isEmpty(interestPaidLoneString)) {
//                interestPaidLoneString = "0"
//            }
//            if (TextUtils.isEmpty(section80CString)) {
//                section80CString = "0"
//            }
//            if (TextUtils.isEmpty(section80TTAString)) {
//                section80TTAString = "0"
//            }
//            if (TextUtils.isEmpty(section80TTBString)) {
//                section80TTBString = "0"
//            }
//            if (TextUtils.isEmpty(section80GString)) {
//                section80GString = "0"
//            }
//            if (TextUtils.isEmpty(section80DString)) {
//                section80DString = "0"
//            }
//            if (TextUtils.isEmpty(section80EString)) {
//                section80EString = "0"
//            }
//            if (TextUtils.isEmpty(tdsDeductedSalaryString)) {
//                tdsDeductedSalaryString = "0"
//            }
//            if (TextUtils.isEmpty(tdsDeductedOtherSalaryString)) {
//                tdsDeductedOtherSalaryString = "0"
//            }
//            if (TextUtils.isEmpty(totalAdvanceString)) {
//                totalAdvanceString = "0"
//            }
//            /*------------------------- start income detail---------------------------*/
//            taxableSalaryFloat = java.lang.Float.parseFloat(taxableSalaryString)
//            incomeFromInterestFloat = java.lang.Float.parseFloat(incomeFromInterestString)
//            interestPaidHomeLoneFloat = java.lang.Float.parseFloat(interestPaidHomeLoneString)
//            rentalIncomeFloat = java.lang.Float.parseFloat(rentalIncomeString)
//            interestPaidLoneFloat = java.lang.Float.parseFloat(interestPaidLoneString)
//            /*------------------------- start income detail---------------------------*/
//
//            /*------------------------- start deduction ---------------------------*/
//            section80CFloat = java.lang.Float.parseFloat(section80CString)
//            section80TTAFloat = java.lang.Float.parseFloat(section80TTAString)
//            section80TTBFloat = java.lang.Float.parseFloat(section80TTBString)
//            if (section80TTAFloat.toDouble() == 0.0 || section80TTBFloat.toDouble() != 0.0) {
//                interestFromSavings = section80TTBFloat
//            } else if (section80TTAFloat.toDouble() != 0.0 || section80TTBFloat.toDouble() == 0.0) {
//                interestFromSavings = section80TTAFloat
//            }
//            section80GFloat = java.lang.Float.parseFloat(section80GString)
//            section80DFloat = java.lang.Float.parseFloat(section80DString)
//            section80EFloat = java.lang.Float.parseFloat(section80EString)
//            /*------------------------- end deduction ---------------------------*/
//
//            /*------------------------- start tds ---------------------------*/
//            tdsDeductedSalaryFloat = java.lang.Float.parseFloat(tdsDeductedSalaryString)
//            tdsDeductedOtherSalaryFloat = java.lang.Float.parseFloat(tdsDeductedOtherSalaryString)
//            totalAdvanceFloat = java.lang.Float.parseFloat(totalAdvanceString)
//            /*------------------------- end tds ---------------------------*/
//            try {
//
//                days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
//                years = days / 365
//
//                /****************************start caluclation  of  income details */
//                totaltaxablesalary = taxableSalaryFloat + incomeFromInterestFloat
//
//                /*-----------------------start self occupied---------------------------*/
//                /************************ totalsalary - interrestpa */
//                if (interestPaidLoneFloat < 200000) {
//                    /************************ totalsalary - interrestpa */
//                    totaltaxablesalary -= interestPaidLoneFloat
//                } else if (interestPaidLoneFloat >= 200000) {
//                    totaltaxablesalary -= 200000
//                }
//                /*--------------------------end self occupied------------------------*/
//
//                /*-----------------------start rental---------------------------*/
//                totalRentalIncome = (rentalIncomeFloat * 0.7 - interestPaidHomeLoneFloat).toFloat()
//                /***************************start inter-paid on loan 2019-2020(ssjg) */
//                if (assessmentYearString == "2019-2020" || assessmentYearString == "2020-2021") {
//                    if (totalRentalIncome > 0) {
//                        totaltaxablesalary += totalRentalIncome
//                    } else {
//                        if (totalRentalIncome > -200000) {
//                            totaltaxablesalary += totalRentalIncome
//                        } else if (totalRentalIncome <= -200000) {
//                            totaltaxablesalary -= 200000
//                        }
//                    }
//                } else {
//                    totaltaxablesalary += totalRentalIncome
//                }
//                /*-----------------------end rental---------------------------*/
//                /****************************end calculation  of  income details */
//
//                /****************************Start calculation  of  deductions */
//                /*-----------------------start Section 80C---------------------------*/
//                if (section80CFloat < 150000) {
//                    totalDeduction += section80CFloat
//                } else if (section80CFloat >= 150000) {
//                    totalDeduction += 150000
//                }
//                /*-----------------------end Section 80C---------------------------*/
//
//                /*-----------------------start Section 80TTA-80ttb--------------------------*/
//                if (years >= 60 && residentialStatusString == "Resident" && assessmentYearString == "2019-2020") {
//                    if (interestFromSavings < 50000) {
//                        totalDeduction += interestFromSavings
//                    } else if (interestFromSavings >= 50000) {
//                        totalDeduction += 50000
//                    }
//                } else {
//                    if (interestFromSavings < 10000) {
//                        totalDeduction += interestFromSavings
//                    } else if (interestFromSavings >= 10000) {
//                        totalDeduction += 10000
//                    }
//                }
//                /*-----------------------end Section 80TTA---------------------------*/
//
//                /*-----------------------start Section Section 80G---------------------------*/
//                totalDeduction += section80GFloat
//                /*-----------------------end Section Section 80G---------------------------*/
//
//                /*-----------------------start Section Section 80D---------------------------*/
//                if (section80DFloat > 0) {
//                    if (assessmentYearString == "2019-2020" || assessmentYearString == "2020-2021") {
//                        /*------------start senior citizen-------------------*/
//                        if (years in 60..79) {
//                            if (section80DFloat >= 75000) {
//                                totalDeduction += 75000
//                            } else if (section80DFloat < 75000) {
//                                totalDeduction += section80DFloat
//                            }
//                        } else if (years < 60) {
//                            /*------------start less than 60-------------------*/
//                            if (section80DFloat >= 50000) {
//                                totalDeduction += 50000
//                            } else if (section80DFloat < 50000) {
//                                totalDeduction += section80DFloat
//                            }
//                        } else {
//                            if (section80DFloat >= 100000) {
//                                totalDeduction += 100000
//                            } else if (section80DFloat < 100000) {
//                                totalDeduction += section80DFloat
//                            }
//                        }/*------------end senior citizen-------------------*/
//                        /*------------end senior citizen-------------------*/
//                    } else {
//                        if (years in 60..79) {
//                            /*------------start senior citizen-------------------*/
//                            if (section80DFloat >= 55000) {
//                                totalDeduction += 55000
//                            } else if (section80DFloat < 55000) {
//                                totalDeduction += section80DFloat
//                            }
//
//                            /*------------end senior citizen-------------------*/
//                        } else if (years < 60) {
//                            /*------------start less than 60-------------------*/
//                            if (section80DFloat >= 50000) {
//                                totalDeduction += 50000
//                            } else if (section80DFloat < 50000) {
//                                totalDeduction += section80DFloat
//                            }
//                        } else {
//                            if (section80DFloat >= 60000) {
//                                totalDeduction += 60000
//                            } else if (section80DFloat < 60000) {
//                                totalDeduction += section80DFloat
//                            }
//                        }
//                        /*------------end less than 60-------------------*/
//                    }
//                }
//                /*-----------------------End Section Section 80D---------------------------*/
//
//                /*-----------------------start Section Section 80E---------------------------*/
//                totalDeduction += section80EFloat
//                /*-----------------------end Section Section 80E---------------------------*/
//
//                if (totaltaxablesalary <= totalDeduction) {
//                    totalDeduction = totaltaxablesalary
//                }
//                /****************************end caluclation  of  deductions */
//
//                /********************************************start tax caluclation  */
//                if (assessmentYearString == "2017-2018") {
//                    if (residentialStatusString == "Resident") {
//                        if (totaltaxablesalary > 0) {
//                            totalBasicSalary = totaltaxablesalary - totalDeduction
//                        } else {
//                            totalBasicSalary = 0f
//                            totaltaxablesalary = 0f
//                            totalDeduction = 0f
//                        }
//                        /*-----------------------start age group less than 60---------------------------*/
//                        taxPayableAmount = 0f
//                        if (years in 0..59) {
//                            /*----------------------0 to 2.5 lac---------------*/
//                            taxPayableAmount = 0f
//                            if (totalBasicSalary <= 250000) {
//                                taxPayableAmount = 0f
//                            } else if (totalBasicSalary > 250000 && totalBasicSalary <= 500000) {
//                                taxPayableAmount += ((totalBasicSalary - 250000) * 0.1).toFloat()
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount += (250000 * 0.1).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount += (250000 * 0.1).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (250000 * 0.1).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------2.5+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 2.5 lack---------------*//*----------------------2.5+ to5 lack---------------*/
//                        } else if (years in 60..79) {
//                            /*----------------------0 to 2.5 lac---------------*/
//                            if (totalBasicSalary <= 300000) {
//                                taxPayableAmount = 0f
//                            } else if (totalBasicSalary > 300000 && totalBasicSalary <= 500000) {
//                                taxPayableAmount = ((totalBasicSalary - 300000) * 0.1).toFloat()
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount = (200000 * 0.1).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount = (200000 * 0.1).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (200000 * 0.1).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------3.0+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 3.0 lack---------------*//*----------------------3.0+ to5 lack---------------*/
//                        } else if (years >= 80) {
//                            /*----------------------0 to 5 lac---------------*/
//                            if (totalBasicSalary <= 500000) {
//                                taxPayableAmount += 0f
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*---------------------- 0 to 5 lack---------------*//*----------------------5+ to 10 lack---------------*/
//                            /*----------------------10+ lack---------------*/
//                        }/*-----------------------end age group  60 to 80---------------------------*//*-----------------------start age group  80 and more---------------------------*//*-----------------------end age group  less than 60---------------------------*//*-----------------------start age group 60 to 80---------------------------*/
//                    } else {
//                        if (totaltaxablesalary > 0) {
//                            totalBasicSalary = totaltaxablesalary - totalDeduction
//                        } else {
//                            totalBasicSalary = 0f
//                            totaltaxablesalary = 0f
//                            totalDeduction = 0f
//                        }
//                        /*-----------------------start age group less than 60---------------------------*/
//                        taxPayableAmount = 0f
//
//                        /*----------------------0 to 2.5 lac---------------*/
//                        taxPayableAmount = 0f
//                        if (totalBasicSalary <= 250000) {
//                            taxPayableAmount = 0f
//                        } else if (totalBasicSalary > 250000 && totalBasicSalary <= 500000) {
//                            taxPayableAmount += ((totalBasicSalary - 250000) * 0.1).toFloat()
//                        } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                            taxPayableAmount += (250000 * 0.1).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                        } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 10000000) {
//                            taxPayableAmount += (250000 * 0.1).toFloat()
//                            taxPayableAmount += (500000 * 0.2).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                        } else if (totalBasicSalary > 10000000) {
//                            taxPayableAmount += (250000 * 0.1).toFloat()
//                            taxPayableAmount += (500000 * 0.2).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                        }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------2.5+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 2.5 lack---------------*//*----------------------2.5+ to5 lack---------------*/
//                    }
//                    if (totalBasicSalary > 10000000) {
//                        taxPayableAmount += (taxPayableAmount * 0.15).toFloat()
//                    }
//                }
//                else if (assessmentYearString == "2018-2019") {
//                    if (residentialStatusString == "Resident") {
//                        if (totaltaxablesalary > 0) {
//                            totalBasicSalary = totaltaxablesalary - totalDeduction
//                        } else {
//                            totalBasicSalary = 0f
//                            totaltaxablesalary = 0f
//                            totalDeduction = 0f
//                        }
//                        /*-----------------------start age group less than 60---------------------------*/
//                        taxPayableAmount = 0f
//
//                        if (years in 0..59) {
//                            /*----------------------0 to 2.5 lac---------------*/
//                            taxPayableAmount = 0f
//                            if (totalBasicSalary <= 250000) {
//                                taxPayableAmount = 0f
//                            } else if (totalBasicSalary > 250000 && totalBasicSalary <= 500000) {
//                                taxPayableAmount += ((totalBasicSalary - 250000) * 0.05).toFloat()
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount += (250000 * 0.05).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 5000000) {
//                                taxPayableAmount += (250000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount += (250000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (250000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------2.5+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 2.5 lack---------------*//*----------------------2.5+ to5 lack---------------*/
//                        } else if (years in 60..79) {
//                            /*----------------------0 to 2.5 lac---------------*/
//                            if (totalBasicSalary <= 300000) {
//                                taxPayableAmount = 0f
//
//                            } else if (totalBasicSalary > 300000 && totalBasicSalary <= 500000) {
//                                taxPayableAmount = ((totalBasicSalary - 300000) * 0.05).toFloat()
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount = (200000 * 0.05).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 5000000) {
//                                taxPayableAmount = (200000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount += (200000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += (1000000 * 0.3).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (200000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------3.0+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 3.0 lack---------------*//*----------------------3.0+ to5 lack---------------*/
//                        } else if (years >= 80) {
//                            /*----------------------0 to 5 lac---------------*/
//                            if (totalBasicSalary <= 500000) {
//                                taxPayableAmount += 0f
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 5000000) {
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*---------------------- 0 to 5 lack---------------*//*----------------------5+ to 10 lack---------------*/
//                        }/*-----------------------end age group  60 to 80---------------------------*//*-----------------------start age group  80 and more---------------------------*//*-----------------------end age group  less than 60---------------------------*//*-----------------------start age group 60 to 80---------------------------*/
//                    } else {
//                        if (totaltaxablesalary > 0) {
//                            totalBasicSalary = totaltaxablesalary - totalDeduction
//                        } else {
//                            totalBasicSalary = 0f
//                            totaltaxablesalary = 0f
//                            totalDeduction = 0f
//                        }
//                        /*-----------------------start age group less than 60---------------------------*/
//                        taxPayableAmount = 0f
//
//                        /*----------------------0 to 2.5 lac---------------*/
//                        taxPayableAmount = 0f
//                        if (totalBasicSalary <= 250000) {
//                            taxPayableAmount = 0f
//                        } else if (totalBasicSalary > 250000 && totalBasicSalary <= 500000) {
//                            taxPayableAmount += ((totalBasicSalary - 250000) * 0.05).toFloat()
//                        } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                            taxPayableAmount += (250000 * 0.05).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                        } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 5000000) {
//                            taxPayableAmount += (250000 * 0.05).toFloat()
//                            taxPayableAmount += (500000 * 0.2).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                        } else if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                            taxPayableAmount += (250000 * 0.05).toFloat()
//                            taxPayableAmount += (500000 * 0.2).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                        } else if (totalBasicSalary > 10000000) {
//                            taxPayableAmount += (250000 * 0.05).toFloat()
//                            taxPayableAmount += (500000 * 0.2).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                        }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------2.5+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 2.5 lack---------------*//*----------------------2.5+ to5 lack---------------*/
//                    }
//                    if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                        taxPayableAmount += (taxPayableAmount * 0.10).toFloat()
//                    } else if (totalBasicSalary > 10000000) {
//                        taxPayableAmount += (taxPayableAmount * 0.15).toFloat()
//                    }
//                }
//                else if (assessmentYearString == "2019-2020" || assessmentYearString == "2020-2021") {
//                    if (residentialStatusString == "Resident") {
//                        if (totaltaxablesalary > 0) {
//                            totalBasicSalary = totaltaxablesalary - totalDeduction
//                        } else {
//                            totalBasicSalary = 0f
//                            totaltaxablesalary = 0f
//                            totalDeduction = 0f
//                        }
//                        /*-----------------------start age group less than 60---------------------------*/
//                        taxPayableAmount = 0f
//
//                        if (years in 0..59) {
//                            /*----------------------0 to 2.5 lac---------------*/
//                            taxPayableAmount = 0f
//                            if (totalBasicSalary <= 250000) {
//                                taxPayableAmount = 0f
//                            } else if (totalBasicSalary > 250000 && totalBasicSalary <= 500000) {
//                                taxPayableAmount += ((totalBasicSalary - 250000) * 0.05).toFloat()
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount += (250000 * 0.05).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 5000000) {
//                                taxPayableAmount += (250000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount += (250000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (250000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------2.5+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 2.5 lack---------------*//*----------------------2.5+ to5 lack---------------*/
//                        } else if (years in 60..79) {
//                            /*----------------------0 to 2.5 lac---------------*/
//                            if (totalBasicSalary <= 300000) {
//                                taxPayableAmount = 0f
//
//                            } else if (totalBasicSalary > 300000 && totalBasicSalary <= 500000) {
//                                taxPayableAmount = ((totalBasicSalary - 300000) * 0.05).toFloat()
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount = (200000 * 0.05).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 5000000) {
//                                taxPayableAmount = (200000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount += (200000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (200000 * 0.05).toFloat()
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------3.0+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 3.0 lack---------------*//*----------------------3.0+ to5 lack---------------*/
//                        } else if (years >= 80) {
//                            /*----------------------0 to 5 lac---------------*/
//                            if (totalBasicSalary <= 500000) {
//                                taxPayableAmount += 0f
//                            } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                                taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                            } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 5000000) {
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                                taxPayableAmount += (500000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            } else if (totalBasicSalary > 10000000) {
//                                taxPayableAmount += (5000000 * 0.2).toFloat()
//                                taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                            }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*---------------------- 0 to 5 lack---------------*//*----------------------5+ to 10 lack---------------*/
//                        }/*-----------------------end age group  60 to 80---------------------------*//*-----------------------start age group  80 and more---------------------------*//*-----------------------end age group  less than 60---------------------------*//*-----------------------start age group 60 to 80---------------------------*/
//                    } else {
//                        if (totaltaxablesalary > 0) {
//                            totalBasicSalary = totaltaxablesalary - totalDeduction
//                        } else {
//                            totalBasicSalary = 0f
//                            totaltaxablesalary = 0f
//                            totalDeduction = 0f
//                        }
//                        /*-----------------------start age group less than 60---------------------------*/
//                        taxPayableAmount = 0f
//
//                        /*----------------------0 to 2.5 lac---------------*/
//                        taxPayableAmount = 0f
//                        if (totalBasicSalary <= 250000) {
//                            taxPayableAmount = 0f
//                        } else if (totalBasicSalary > 250000 && totalBasicSalary <= 500000) {
//                            taxPayableAmount += ((totalBasicSalary - 250000) * 0.05).toFloat()
//                        } else if (totalBasicSalary > 500000 && totalBasicSalary <= 1000000) {
//                            taxPayableAmount += (250000 * 0.05).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 500000) * 0.2).toFloat()
//                        } else if (totalBasicSalary > 1000000 && totalBasicSalary <= 5000000) {
//                            taxPayableAmount += (250000 * 0.05).toFloat()
//                            taxPayableAmount += (500000 * 0.2).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                        } else if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                            taxPayableAmount += (250000 * 0.05).toFloat()
//                            taxPayableAmount += (500000 * 0.2).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                        } else if (totalBasicSalary > 10000000) {
//                            taxPayableAmount += (250000 * 0.05).toFloat()
//                            taxPayableAmount += (5000000 * 0.2).toFloat()
//                            taxPayableAmount += ((totalBasicSalary - 1000000) * 0.3).toFloat()
//                        }/*----------------------10+ lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------10+ lack---------------*//*----------------------2.5+ to5 lack---------------*//*----------------------5+ to 10 lack---------------*//*----------------------0 to 2.5 lack---------------*//*----------------------2.5+ to5 lack---------------*/
//                    }/*-----------------------end age group  80 and more---------------------------*/
//                    if (totalBasicSalary > 5000000 && totalBasicSalary <= 10000000) {
//                        taxPayableAmount += (taxPayableAmount * 0.10).toFloat()
//                    } else if (totalBasicSalary > 10000000) {
//                        taxPayableAmount += (taxPayableAmount * 0.15).toFloat()
//                    }
//                }
//                /********************************************start tax caluclation 2018-19  */
//                /********************************************End tax caluclation  */
//
//                /************************************ start AY 2019-20 */
//
//                if (assessmentYearString == "2020-2021") {
//
//                    /********************************************Start tax 87A or resident  */
//                    if (residentialStatusString == "Resident" && totalBasicSalary <= 500000) {
//                        if (taxPayableAmount >= 12500) {
//                            taxPayableAmount -= 12500
//                            totalRebateTax = 2500f
//                        } else {
//                            taxPayableAmount -= taxPayableAmount
//                            totalRebateTax = 0f
//                        }
//                        /********************************************End tax 87A or resident  */
//                    }
//                }
//                if (assessmentYearString == "2019-2020") {
//
//                    /********************************************Start tax 87A or resident  */
//                    if (residentialStatusString == "Resident" && totalBasicSalary <= 350000) {
//                        if (taxPayableAmount >= 2500) {
//                            taxPayableAmount -= 2500
//                            totalRebateTax = 2500f
//                        } else {
//                            taxPayableAmount -= taxPayableAmount
//                            totalRebateTax = 0f
//                        }
//                        /********************************************End tax 87A or resident  */
//                    }
//                }
//                /************************************ END AY 2019-20 */
//                /************************************ start AY 2018-19 */
//                if (assessmentYearString == "2018-2019") {
//
//                    /********************************************Start tax 87A or resident  */
//                    if (residentialStatusString == "Resident" && totalBasicSalary <= 350000) {
//                        if (taxPayableAmount >= 2500) {
//                            taxPayableAmount -= 2500
//                            totalRebateTax = 2500f
//                        } else {
//                            taxPayableAmount -= taxPayableAmount
//                            totalRebateTax = 0f
//                        }
//                        /********************************************End tax 87A or resident  */
//                    }
//                }
//                /************************************ END AY 2018-19 */
//                /************************************ start AY 2017-18 */
//                if (assessmentYearString == "2017-2018") {
//                    /********************************************Start tax 87A or resident  */
//
//
//                    if (residentialStatusString == "Resident" && totalBasicSalary <= 500000) {
//                        if (taxPayableAmount >= 5000) {
//                            taxPayableAmount -= 5000
//                            totalRebateTax = 5000f
//                        } else {
//                            taxPayableAmount -= taxPayableAmount
//                            totalRebateTax = 0f
//                        }
//                        /********************************************End tax 87A or resident  */
//                    }
//                }
//                /************************************ END AY 2015-16 */
//
//                /************************************ Start EC 2% SH 1% */
//                if (assessmentYearString == "2019-2020" || assessmentYearString == "2020-2021") {
//                    if (taxPayableAmount > 0) {
//                        taxPayableAmount = (taxPayableAmount + taxPayableAmount * 0.04).toFloat()
//                    }
//                } else if (assessmentYearString == "2018-2019") {
//                    if (taxPayableAmount > 0) {
//                        taxPayableAmount = (taxPayableAmount + taxPayableAmount * 0.03).toFloat()
//                    }
//                } else if (assessmentYearString == "2017-2018") {
//                    if (taxPayableAmount > 0) {
//                        taxPayableAmount = (taxPayableAmount + taxPayableAmount * 0.03).toFloat()
//                    }
//                }
//
//
//                /************************************ END EC 2% SH 1% */
//
//
//                /************************************Start Tds details */
//                netTaxableIncome = tdsDeductedSalaryFloat + tdsDeductedOtherSalaryFloat + totalAdvanceFloat
//                totalTax = taxPayableAmount - netTaxableIncome
//                /************************************end Tds details */
//
//                if (totalTax < 0) {
//                    refund = totalTax * -1
//                    totalPayableTax = 0f
//
//                } else {
//                    refund = 0f
//                    totalPayableTax = totalTax
//                }
//
//                if (ConnectionDetector().isConnectingToInternet(mContext)) {
//                    postCalculationAPI()
//                } else {
//                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//    }
//
//    private fun postCalculationAPI(){
//        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
//
//        usefulToolsModel.emailId = appPreferences.emailAddress.toString()
//        usefulToolsModel.assessmentYear = assessmentYearString
//        usefulToolsModel.age = ageString
//        usefulToolsModel.residentialStatus = residentialStatusString
//        usefulToolsModel.taxableSalary = taxableSalaryFloat.toInt()
//        usefulToolsModel.interestIncome = incomeFromInterestFloat.toInt()
//        usefulToolsModel.interestpaidOnHomeLoan = interestPaidHomeLoneFloat.toInt()
//        usefulToolsModel.basicDeduction80c = section80CFloat.toInt()
//        usefulToolsModel.eightyTTAeightyTTB = interestFromSavings.toInt()
//        usefulToolsModel.eightyD = section80DFloat.toInt()
//        usefulToolsModel.eightyG = section80GFloat.toInt()
//        usefulToolsModel.eightyE = section80EFloat.toInt()
//        usefulToolsModel.tdsDeductedFromSalary = tdsDeductedSalaryFloat.toInt()
//        usefulToolsModel.tdsDeductedOtherThanSalary = tdsDeductedOtherSalaryFloat.toInt()
//        usefulToolsModel.totalAdvanceTaxAlreadyPaid = totalAdvanceFloat.toInt()
//        usefulToolsModel.rentalIncomeReceived = rentalIncomeFloat.toInt()
//        usefulToolsModel.interestPaidOnLoan = interestPaidLoneFloat.toInt()
//        usefulToolsModel.totalLibility = totalPayableTax.toInt().toString()
//        usefulToolsModel.refund = refund.toInt().toString()
//
//        val call = apI_Interface.tax_calculation(usefulToolsModel)
//        call.enqueue(object : Callback<Void> {
//
//            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
//                try {
//                    if (response!!.isSuccessful) {
//                        dialog.hideDialog()
//                        val intent = Intent(mContext, IncomeTaxCalculatorResultActivity::class.java)
//                        usefulToolsModel.totaltaxablesalary = totaltaxablesalary.toInt()
//                        usefulToolsModel.totalDeduction = totalDeduction.toInt()
//                        usefulToolsModel.totalBasicSalary = totalBasicSalary.toInt()
//                        usefulToolsModel.taxPayableAmount = taxPayableAmount.toInt()
//                        usefulToolsModel.netTaxableIncome = netTaxableIncome.toInt()
//                        usefulToolsModel.totalTax = totalTax.toInt()
//                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
//                        startActivity(intent)
//                    }
//                    else {
//                        dialog.hideDialog()
//                    }
//                }
//                catch (e: Exception) {
//                    dialog.hideDialog()
//                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<Void>?, t: Throwable?) {
//                dialog.hideDialog()
//                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//            }
//
//        })
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
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//    }
//
//}
