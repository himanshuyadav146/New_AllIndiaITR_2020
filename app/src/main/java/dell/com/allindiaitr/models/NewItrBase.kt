package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NewItrBase : Serializable {

    //**********Version Update Model Start**********//
    @SerializedName("MobileAppVersion") @Expose var mobileAppVersion: String? = null
    @SerializedName("AlertMessage") @Expose var alertMessage: String? = null
    @SerializedName("ITRComputationURL") @Expose var iTRComputationURL: String? = null
    @SerializedName("ChildUserCount") @Expose var childUserCount: Boolean? = null
    @SerializedName("PriFileClosed") @Expose var priFileClosed: Boolean? = true   //new added
    @SerializedName("ManualFileClosed") @Expose var manualFileClosed: Boolean? = true  //new added
    //    @SerializedName("AssestmentYear") @Expose var assestmentYear: String? = null  //new added
//    @SerializedName("AssestmentYearId") @Expose var assestmentYearId: String? = null  //new added
    @SerializedName("AppUtilityList") @Expose var appUtilityList: List<NewItrBase>? = null
    //**********Version Update Model End**********//

    //**********App Utility List Model Start**********//
    @SerializedName("Type") @Expose var type: String? = null
    @SerializedName("Description") @Expose var description: String? = null         //*****Used in E-Filing Status, E-Filing Status Field*****//
    @SerializedName("IsActive") @Expose var isActive: Boolean? = null
    @SerializedName("CreditScoreAmount") @Expose var CreditScoreAmount: String? = null
    //**********App Utility List Model End**********//

    //**********Personal Information Model Start**********//
    @SerializedName("Id")
    @Expose
    var id: String? = null         //*****Used in New User For ITR, E-Filing Status, E-Filing Status Field, Financial Year, Bank Details List*****//
    @SerializedName("FirstName") @Expose var firstName: String? = null         //*****Used in Submit Document Status*****//
    @SerializedName("MiddleName") @Expose var middleName: String? = null         //*****Used in Submit Document Status*****//
    @SerializedName("LastName") @Expose var lastName: String? = null         //*****Used in Submit Document Status*****//
    @SerializedName("PhoneNumber") @Expose var phoneNumber: String? = null         //*****Used in User List, Submit Document Status, E-Filing Status*****//
    @SerializedName("Email") @Expose var email: String? = null         //*****Used in Submit Document Status*****//
    @SerializedName("Password") @Expose var password: Any? = null
    @SerializedName("PanNumber") @Expose var panNumber: String? = null           //*****Used in User List, Submit Document Status, Aadhaar Details*****//
    @SerializedName("FinancialYear") @Expose var financialYear: Any? = null          //*****Used in Child User List, Financial Year*****//
    @SerializedName("AadharCardNumber") @Expose var aadharCardNumber: String? = null         //*****Used in Aadhaar Details*****//
    @SerializedName("Form16TempDataID") @Expose var form16TempDataID: Any? = null
    @SerializedName("DeviceType") @Expose var deviceType: Any? = null
    @SerializedName("IncomeSource") @Expose var incomeSource: Any? = null
    @SerializedName("DateOfBirth") @Expose var dateOfBirth: String? = null         //*****Used in User List, Submit Document Status*****//
    @SerializedName("UserId") @Expose var userId: Any? = null         //*****Used in User List, New User For ITR, Aadhaar Details*****//
    @SerializedName("UserAssessmentYearId") @Expose var userAssessmentYearId: String? = null            //*****Used in Child User List, E-Filing Status, Form Document*****//
    @SerializedName("IsSalary") @Expose var isSalary: String? = null
    @SerializedName("IsBusiness") @Expose var isBusiness: String? = null
    @SerializedName("IsHouseProperty") @Expose var isHouseProperty: String? = null
    @SerializedName("IsCapitalGain") @Expose var isCapitalGain: String? = null
    @SerializedName("IsOtherSource") @Expose var isOtherSource: String? = null
    @SerializedName("IsForignIncome") @Expose var isForeignIncome: String? = null
    @SerializedName("UserAssestmentYearId") @Expose var UserAssestmentYearId: String? = null         //*****Used in New User For ITR, Aadhaar Details, Bank Details List, Other Income*****//
    @SerializedName("ProcessMode") @Expose var processMode: String? = null            //*****Used in Child User List, New User For ITR*****//
    @SerializedName("EmailId") @Expose var emailId: String? = null         //*****Used in User List, E-Filing Status*****//
    @SerializedName("MobileNumber") @Expose var mobileNumber: String? = null
    @SerializedName("AssestmentYearId") @Expose var assestmentYearId: String? = null
    @SerializedName("DeviceId") @Expose var deviceId: String? = null
    @SerializedName("IfUserExist") @Expose var ifUserExist: Any? = null         //*****Used in New User For ITR*****//
    @SerializedName("ReturnMsg") @Expose var returnMsg: String? = null         //*****Used in New User For ITR*****//
    @SerializedName("Gender") @Expose var gender: String? = null

    @SerializedName("AreYouFilingSec139_1") @Expose var areYouFilingSec139_1: Boolean? = null //optional values
    @SerializedName("currentAccountDeposit") @Expose var currentAccountDeposit: Int? = null
    @SerializedName("ForeignTravel") @Expose var foreignTravel: Int? = null
    @SerializedName("ElectricityConsumption") @Expose var electricityConsumption: Int? = null


    @SerializedName("FatherName") @Expose var fatherName: String? = null         //*****Used in Aadhaar Details*****//
    @SerializedName("FlatNumber") @Expose var flatNumber: String? = null
    @SerializedName("UserAssesstmentYearId") @Expose var userAssesstmentYearId: String? = null
    @SerializedName("Street") @Expose var street: String? = null
    @SerializedName("StateId") @Expose var stateId: String? = null         //*****Used in Aadhaar Details*****//
    @SerializedName("Pincode") @Expose var pincode: String? = null
    @SerializedName("City") @Expose var city: String? = null         //*****Used in Aadhaar Details*****//
    @SerializedName("CreditScoreXml") @Expose var creditScoreXml: Any? = null
    @SerializedName("CreditScoreConfidLevel") @Expose var creditScoreConfidLevel: String? = null
    @SerializedName("CreditScore") @Expose var creditScore: String? = null
    var isPost: Boolean? = null
    //**********Personal Information Model End**********//

    //**********User List Model Start**********//
    @SerializedName("Name")
    @Expose
    var name: String? = null
    @SerializedName("childUserStatus")
    @Expose
    var childUserStatus: List<NewItrBase>? = null
    //**********User List Model End**********//

    //**********Child User List Model Start**********//
    @SerializedName("AssestmentYear") @Expose var assestmentYear: String? = null
    @SerializedName("PaymentStatus") @Expose var paymentStatus: Boolean? = null
    @SerializedName("IsAckGenerated") @Expose var isAckGenerated: Boolean? = null
    @SerializedName("AcknowledgementNo") @Expose var acknowledgementNo: Any? = null         //*****Used in Submit Document Status*****//
    @SerializedName("EVerifyPaymentDone") @Expose var eVerifyPaymentDone: Boolean? = null
    @SerializedName("EVerifyDone") @Expose var eVerifyDone: Boolean? = null
    @SerializedName("RevisedReturnDone") @Expose var revisedReturnDone: Boolean? = null
    @SerializedName("CreatedDate") @Expose var createdDate: String? = null         //*****Used in E-Filing Status*****//
    //**********Child User List Model End**********//

    //**********New User For ITR Model Start**********//
    //**********New User For ITR Model End**********//

    //**********Submit Document Status Model Start**********//
    @SerializedName("EfillingStatus")
    @Expose
    var efilingStatuses: List<NewItrBase>? = null
    @SerializedName("EfillingStatusField")
    @Expose
    var efilingStatusField: List<NewItrBase>? = null
    //**********Submit Document Status Model End**********//

    //**********E-Filing Status Model Start**********//
    @SerializedName("StatusId")
    @Expose
    var statusId: String? = null
    @SerializedName("Status")
    @Expose
    var status: String? = null         //*****Used in E-Filing Status Field*****//
    @SerializedName("ClientNote")
    @Expose
    var clientNote: Any? = null
    @SerializedName("ParentId")
    @Expose
    var parentId: Any? = null
    @SerializedName("IsActivate")
    @Expose
    var isActivate: Boolean? = null
    //**********E-Filing Status Model End**********//

    //**********E-Filing Status Field Model Start**********//
    @SerializedName("Label")
    @Expose
    var label: String? = null
    @SerializedName("FieldType")
    @Expose
    var fieldType: String? = null
    //**********E-Filing Status Field Model End**********//

    //**********Financial Year Model Start**********//
    @SerializedName("FinancialyearAndAssestmentYear")
    @Expose
    var financialYearAndAssestmentYear: String? = null
    //**********Financial Year Model End**********//

    //**********Documents List Model Start**********//
    @SerializedName("Getform16andotherdocList")
    @Expose
    var getFormDocList: List<NewItrBase>? = null
    @SerializedName("Comment")
    @Expose
    var comment: Comment? = null
    //**********Documents List Model End**********//

    //**********Form Document Model Start**********//
    @SerializedName("UserUploadedDocumentId")
    @Expose
    var userUploadedDocumentId: String? = null
    @SerializedName("DocumentName")
    @Expose
    var documentName: String? = null
    @SerializedName("UploadDocumentDownloadName")
    @Expose
    var uploadDocumentDownloadName: Any? = null
    @SerializedName("IsDoucmentUploaded")
    @Expose
    var isDocumentUploaded: Boolean? = null
    @SerializedName("DocumentDescription")
    @Expose
    var documentDescription: Any? = null
    @SerializedName("UploadedDocumentName")
    @Expose
    var uploadedDocumentName: Any? = null
    @SerializedName("UploadedDocumentPath")
    @Expose
    var uploadedDocumentPath: String? = null
    @SerializedName("UploadedDate")
    @Expose
    var uploadedDate: String? = null
    @SerializedName("DocumentVirtualURL")
    @Expose
    var documentVirtualURL: String? = null
    @SerializedName("FilePassword")
    @Expose
    var filePassword: Any? = null
    @SerializedName("FileType")
    @Expose
    var fileType: String? = null
    //**********Form Document Model End**********//

    //**********Aadhaar Details Model Start**********//
    @SerializedName("Address1")
    @Expose
    var address1: String? = null
    @SerializedName("Address2")
    @Expose
    var address2: String? = null
    @SerializedName("PinCode")
    @Expose
    var pinCode: String? = null
    @SerializedName("Message")
    @Expose
    var message: String? = null
    //**********Aadhaar Details Model End**********//

    //**********Bank Details List Model Start**********//
    @SerializedName("BankAccountNumber") @Expose var bankAccountNumber: String? = null
    @SerializedName("IFSCCode") @Expose var iFSCCode: String? = null
    @SerializedName("TypeOfBankAccount") @Expose var typeOfBankAccount: String? = null
    @SerializedName("NameOfYourBank") @Expose var nameOfYourBank: String? = null
    @SerializedName("CashDiposites") @Expose var cashDiposites: String? = null
    @SerializedName("BankAccountTypeFlag") @Expose var bankAccountTypeFlag: String? = null
    //**********Bank Details List Model End**********//

    //**********Other Income Model Start**********//
    @SerializedName("InterestIncomeFromSaving") @Expose var interestIncomeFromSaving: Double? = null
    @SerializedName("OtherInterestIncome") @Expose var otherInterestIncome: Double? = null
    @SerializedName("OtherThanSalaryIncome") @Expose var otherThanSalaryIncome: Double? = null
    @SerializedName("DividendEarn") @Expose var dividendEarn: Double? = null
    @SerializedName("ExemptInterestIncome") @Expose var exemptInterestIncome: Double? = null
    @SerializedName("OtherExemptIncome") @Expose var otherExemptIncome: Double? = null
    @SerializedName("GrossAgricultureIncome") @Expose var grossAgricultureIncome: Double? = null
    @SerializedName("ExpenditureOnAgriculture") @Expose var expenditureOnAgriculture: Double? = null
    @SerializedName("UnabsorbedAgricultureLoss") @Expose var unabsorbedAgricultureLoss: Double? = null
    @SerializedName("ExemptIncomeDescription") @Expose var exemptIncomeDescription: Any? = null
    @SerializedName("AnyOtherIncomeDescription") @Expose var anyOtherIncomeDescription: Any? = null
    //**********Other Income Model End**********//

    //**********Miscellaneous Model Start**********//
    @SerializedName("BusinessRangeId")
    @Expose
    var businessRangeId: Any? = null
    //**********Miscellaneous Model End**********//

    var selectedUser_userAssessmentYearUserID: String?=null
    var isNewUser: Boolean = true
    var isNewItr: Boolean = false
    var orderMode: String? = "Orders"
    var referNowVisible: Boolean = true
    var isReferVisible: Boolean = false
    var referNCount: Int = 0
    var selectedAge: Int = 0                      //new added
    var selectedForgetPrefilledId: String = ""    //new added
    // var paymentType: String? = null
    var selectedProcessStatus:String?=null
    var selectedUserEmail: String? = null
    var selectedUserMobile: String? = null
    var selectedUserName: String? = null
    var itrStatus:String?=null
    var selectedUserId: String? = null
    var baseUserList: List<NewItrBase>? = null
    var assessmentYearId: String? = null
    var userName: String? = null
    var flagInfo: Boolean = false
    var bankSize: Int = 0
    var isAddBank: Boolean = false
    var bankInfoId: String? = null
    var rateUsVisible: Boolean = true
    //created for manual tax selected id
    var manualSelfTaxSelectedId: String? = null
    var isManualSelfTax: Boolean = false

    //created for manual donation deduction selected id
    var manualDonationSelectedId: String? = null
    var manualDonationSelectedOrgId: String? = null
    var isManualDonation: Boolean = false




    //***************************************** Payment Model **************************************

    @SerializedName("payDetails")
    @Expose
    private var payDetails: PayDetails? = null

    fun getPayDetails(): PayDetails? {
        return payDetails
    }

    fun setPayDetails(payDetails: PayDetails) {
        this.payDetails = payDetails
    }

    var ChatMode: String? = null

    //***************************************** Payment Model **************************************

    //***************************************** Chat Val **************************************
    private var isChatOpen:Boolean=false
    private var isNotification:Boolean=false

    fun setIsChatOpen(msg:Boolean)
    {
        isChatOpen=msg
    }

    fun  getIsChatOpen():Boolean
    {
        return isChatOpen
    }

    fun setisNotification(msg:Boolean)
    {
        isNotification=msg
    }

    fun getisNotification():Boolean
    {
        return isNotification
    }
    //***************************************** Chat Val **************************************



    //***************************************** User List **************************************




    //***************************************** User List **************************************



    constructor(
        firstName: String?,
        middleName: String?,
        lastName: String?,
        panNumber: String?,
        deviceType: Any?,
        dateOfBirth: String?,
        userId: Any?,
        isSalary: String?,
        isBusiness: String?,
        isHouseProperty: String?,
        isCapitalGain: String?,
        isOtherSource: String?,
        isForeignIncome: String?,
        UserAssestmentYearId: String?,
        processMode: String?,
        emailId: String?,
        mobileNumber: String?,
        assestmentYearId: String?,
        deviceId: String?,
        areYouFilingSec139_1: Boolean?,
        currentAccountDeposit: Int?,
        foreignTravel: Int?,
        electricityConsumption: Int?
    ) {
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.panNumber = panNumber
        this.deviceType = deviceType
        this.dateOfBirth = dateOfBirth
        this.userId = userId
        this.isSalary = isSalary
        this.isBusiness = isBusiness
        this.isHouseProperty = isHouseProperty
        this.isCapitalGain = isCapitalGain
        this.isOtherSource = isOtherSource
        this.isForeignIncome = isForeignIncome
        this.UserAssestmentYearId = UserAssestmentYearId
        this.processMode = processMode
        this.emailId = emailId
        this.mobileNumber = mobileNumber
        this.assestmentYearId = assestmentYearId
        this.deviceId = deviceId
        this.areYouFilingSec139_1 = areYouFilingSec139_1
        this.currentAccountDeposit = currentAccountDeposit
        this.foreignTravel = foreignTravel
        this.electricityConsumption = electricityConsumption
    }

    fun setInstanceEmpty(){
        firstName = ""
        middleName = ""
        lastName = ""
        panNumber = ""
        dateOfBirth = ""
        isSalary = "false"
        isBusiness = "false"
        isCapitalGain = "false"
        isForeignIncome = "false"
        isHouseProperty = "false"
        isOtherSource = "false"
    }

    private constructor (){

    }
    companion object {
        val instance: NewItrBase by lazy { NewItrBase() }
    }


}