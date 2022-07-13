package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UsefulToolsModel {

    @SerializedName("BasicSalary")
    @Expose
    var basicSalary: Int = 0
    @SerializedName("DearnessAllowance")
    @Expose
    var dearnessAllowance: Int = 0
    @SerializedName("HRA")
    @Expose
    var hra: Int = 0
    @SerializedName("RentPaid")
    @Expose
    var rentPaid: Int = 0
    @SerializedName("IsMetroCity")
    @Expose
    var isMetroCity: Boolean = false
    @SerializedName("Email")
    @Expose
    var email: String? = null
    @SerializedName("HraExemption")
    @Expose
    var hraExemption: String? = null

    var hraChargeableTax : Int = 0
    var percentage : String? = null

    @SerializedName("Name")
    @Expose
    var name: String? = null
    @SerializedName("LandlordName")
    @Expose
    var landlordName: String? = null
    @SerializedName("PanNumber")
    @Expose
    var panNumber: String? = null
    @SerializedName("Address")
    var address: String? = null
    @SerializedName("SDate")
    @Expose
    var sDate: String? = null
    @SerializedName("EDate")
    @Expose
    var eDate: String? = null
    @SerializedName("Months")
    @Expose
    var months: String? = null

    @SerializedName("EmailId")
    @Expose
    var emailId: String? = null
    @SerializedName("AssessmentYear")
    @Expose
    var assessmentYear: String? = null
    @SerializedName("Age")
    @Expose
    var age: String? = null
    @SerializedName("ResidentialStatus")
    @Expose
    var residentialStatus: String? = null
    @SerializedName("TaxableSalary")
    @Expose
    var taxableSalary: Int? = null
    @SerializedName("InterestIncome")
    @Expose
    var interestIncome: Int? = null
    @SerializedName("InterestpaidOnHomeLoan")
    @Expose
    var interestpaidOnHomeLoan: Int? = null
    @SerializedName("BasicDeduction80c")
    @Expose
    var basicDeduction80c: Int? = null
    @SerializedName("EightyTTAeightyTTB")
    @Expose
    var eightyTTAeightyTTB: Int? = null
    @SerializedName("EightyD")
    @Expose
    var eightyD: Int? = null
    @SerializedName("EightyG")
    @Expose
    var eightyG: Int? = null
    @SerializedName("EightyE")
    @Expose
    var eightyE: Int? = null
    @SerializedName("TdsDeductedFromSalary")
    @Expose
    var tdsDeductedFromSalary: Int? = null
    @SerializedName("TdsDeductedOtherThanSalary")
    @Expose
    var tdsDeductedOtherThanSalary: Int? = null
    @SerializedName("TotalAdvanceTaxAlreadyPaid")
    @Expose
    var totalAdvanceTaxAlreadyPaid: Int? = null
    @SerializedName("RentalIncomeReceived")
    @Expose
    var rentalIncomeReceived: Int? = null
    @SerializedName("InterestPaidOnLoan")
    @Expose
    var interestPaidOnLoan: Int? = null
    @SerializedName("TotalLibility")
    @Expose
    var totalLibility: String? = null
    @SerializedName("Refund")
    @Expose
    lateinit var refund: String

    var totaltaxablesalary: Int = 0
    var totalDeduction: Int = 0
    var totalBasicSalary: Int = 0
    var taxPayableAmount: Int = 0
    var netTaxableIncome: Int = 0
    var totalTax: Int = 0

     constructor (){
    }
    companion object {
        val instance: UsefulToolsModel by lazy { UsefulToolsModel() }
    }
}