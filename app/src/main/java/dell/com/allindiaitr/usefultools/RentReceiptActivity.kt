//package dell.com.allindiaitr.usefultools
//
//import android.Manifest
//import android.app.Activity
//import android.content.Context
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.media.MediaScannerConnection
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.util.Log
//import android.view.MenuItem
//import android.view.inputmethod.InputMethodManager
//import android.widget.ArrayAdapter
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import com.itextpdf.text.*
//import com.itextpdf.text.pdf.PdfPTable
//import com.itextpdf.text.pdf.PdfWriter
//import com.itextpdf.text.pdf.PdfPCell
//
//import com.vincent.filepicker.Constant
//import com.vincent.filepicker.activity.BaseActivity
//import com.vincent.filepicker.activity.NormalFilePickActivity
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.home.DashboardActivity
//import dell.com.allindiaitr.interfaces.API_Interface
//import dell.com.allindiaitr.models.UsefulToolsModel
//import dell.com.allindiaitr.utils.*
//import kotlinx.android.synthetic.main.activity_rent_receipt.*
//import kotlinx.android.synthetic.main.app_toolbar.*
//import okhttp3.MediaType
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import okhttp3.ResponseBody
//import org.json.JSONArray
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.*
//import java.net.URI
//import java.text.DateFormat
//import java.text.ParseException
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.TimeUnit
//
//class RentReceiptActivity : AppCompatActivity() {
//
//    private lateinit var mContext: Context
//    private lateinit var apI_Interface : API_Interface
//    lateinit var document: Document
//
////    lateinit var document: com.itextpdf.text.Document
//    lateinit var currentDateTimeString: String
//    var receiptNoArrayList = ArrayList<String>()
//    var ownerNameArrayList = ArrayList<String>()
//    var addressLineArrayList = ArrayList<String>()
//    var rentReceivedArrayList = ArrayList<String>()
//    var fromDateArrayList = ArrayList<String>()
//    var toDateArrayList = ArrayList<String>()
//    var tenantNameArrayList = ArrayList<String>()
//    var landLordPanArrayList = ArrayList<String>()
//    var monthArrayList = ArrayList<String>()
//    var yearArrayList = ArrayList<String>()
//    lateinit var address: String
//    var fromDateString:String? = null
//    var stateString:String? = null
//    var receiptCount = 1
//    var appPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
//    var PERMISSIONS_REQUEST_CODE = 1024
//    lateinit var sdf: SimpleDateFormat
//    internal var lastMonthRent = 0
//    var stateIdArrayList = mutableListOf<String>()
//    var stateNameArrayList = mutableListOf<String>()
//    lateinit var toDate: Date
//    lateinit var fromDate:Date
//    lateinit var calendar: Calendar
//    lateinit var newToDateString: String
//    var usefulToolsModel = UsefulToolsModel.instance
//    private var Permission = ""
//    private var appPreferences: AppPreferences? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_rent_receipt)
//        mContext = this
//        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
//
//        setSupportActionBar(toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        toolbar_title.text = "Rent Receipt"
////        askPermission()
//        currentDateTimeString =
//            DateFormat.getDateTimeInstance().format(Date()).replace(",", "").replace(" ", "").replace(":", "")
//
//        checkAndRequestPermissions()
//        readJSONFromAsset()
//
//        printButton.setOnClickListener {
//            fromDateString = fromDateTextView.text.toString()
//
//            receiptNoArrayList.clear()
//            ownerNameArrayList.clear()
//            addressLineArrayList.clear()
//            rentReceivedArrayList.clear()
//            fromDateArrayList.clear()
//            toDateArrayList.clear()
//            tenantNameArrayList.clear()
//            landLordPanArrayList.clear()
//            receiptCount = 1
//
//            if (Validation().isNameValid(tenantNameEditText.text.toString(), tenantNameEditText) &&
//                Validation().isEmailValid(emailIdEditText.text.toString(), emailIdEditText) &&
//                Validation().isNameValid(landlordNameEditText.text.toString(), landlordNameEditText) &&
//                Validation().isPanValid(landlordPanEditText.text.toString(), landlordPanEditText) &&
//                Validation().isFieldValid(totalRentReceivedEditText.text.toString(), totalRentReceivedEditText) &&
//                Validation().isDobValid(fromDateTextView.text.toString(), fromDateTextView) &&
//                Validation().isDobValid(toDateTextView.text.toString(), toDateTextView) &&
//                Validation().isAddressValid(addressLineEditText.text.toString(), addressLineEditText) &&
//                Validation().isCityValid(townCityEditText.text.toString(), townCityEditText) &&
//                Validation().isStateValid(stateEditText.selectedItem.toString(), mContext) &&
//                Validation().isPinValid(pinCodeEditText.text.toString(), pinCodeEditText)) {
//
//                checkDate()
//                if (toDateArrayList.size > 0) {
//                    toDateArrayList.removeAt(toDateArrayList.size - 1)
//                    toDateArrayList.add(toDateTextView.text.toString())
//                    Log.v("toDate ", (toDateArrayList).toString())
//                    try {
//                        val date1 = sdf.parse(fromDateArrayList[fromDateArrayList.size - 1])
//                        val date2 = sdf.parse(toDateArrayList[toDateArrayList.size - 1])
//                        val diff = date2.time - date1.time
//                        val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
//                        lastMonthRent = Integer.parseInt(totalRentReceivedEditText.text.toString()) / 30 * days
//                        Log.v("Days = ", days.toString() + "")
//                    } catch (e: ParseException) {
//                        e.printStackTrace()
//                    }
//
//                    rentReceivedArrayList.removeAt(rentReceivedArrayList.size - 1)
//                    rentReceivedArrayList.add(lastMonthRent.toString())
//                    Log.v("rentReceived", (rentReceivedArrayList).toString())
//                    stateString = stateNameArrayList[stateEditText.selectedItemPosition]
//
//                    address = addressLineEditText.text.toString() + ", " + townCityEditText.text.toString() + ", " + stateString + ", " + pinCodeEditText.text.toString()
//
//                    usefulToolsModel.name = tenantNameEditText.text.toString()
//                    usefulToolsModel.email = emailIdEditText.text.toString()
//                    usefulToolsModel.landlordName = landlordNameEditText.text.toString()
//                    usefulToolsModel.panNumber = landlordPanEditText.text.toString()
//                    usefulToolsModel.address = addressLineEditText.text.toString()
//                    usefulToolsModel.rentPaid = totalRentReceivedEditText.text.toString().toInt()
//                    usefulToolsModel.sDate = fromDateTextView.text.toString()
//                    usefulToolsModel.eDate = toDateTextView.text.toString()
//                    usefulToolsModel.months = receiptNoArrayList.size.toString()
//
//                    pdf()
//                }
//            }
//        }
//
//        fromDateTextView.setOnClickListener {
//            DatePicker(mContext, fromDateTextView, 0).datePickerDialog()
//        }
//
//        toDateTextView.setOnClickListener {
//            DatePicker(mContext, toDateTextView, 0).datePickerDialog()
//        }
//
//        ButtonVisibility().hideButton(mContext, activity_rent_receipt, printButton)
//    }
//
//
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
//    private fun readJSONFromAsset(): String? {
//        var json: String?
//        try {
//            val  inputStream: InputStream = resources.openRawResource(R.raw.state)
//            json = inputStream.bufferedReader().use{it.readText()}
//            stateNameArrayList.add(0, "Select State")
//            stateIdArrayList.add(0,"0")
//            val jsonArray = JSONArray(json)
//            for (i in 0 until jsonArray.length()) {
//                val jsonObject = jsonArray.getJSONObject(i)
//                stateNameArrayList.add(jsonObject.getString("State"))
//                stateIdArrayList.add(jsonObject.getString("Id"))
//            }
//            stateEditText.adapter = ArrayAdapter<String>(mContext, R.layout.spinner_item, stateNameArrayList)
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }
//
//    private fun checkAndRequestPermissions(): Boolean{
//        var listPermissionsNeeded = arrayListOf<String>()
//        for (i in 0 until appPermissions.size) {
//            if (ContextCompat.checkSelfPermission(mContext, appPermissions[i].toString()) != PackageManager.PERMISSION_GRANTED)
//                listPermissionsNeeded.add(appPermissions[i].toString())
//        }
//        if (listPermissionsNeeded.isNotEmpty()){
//            ActivityCompat.requestPermissions(this,
//                listPermissionsNeeded.toArray(arrayOfNulls(listPermissionsNeeded.size)),
//                PERMISSIONS_REQUEST_CODE)
//            return false
//        }
//        return true
//    }
//
//    private fun checkDate() {
//        try {
//            sdf = SimpleDateFormat("dd-MM-yyyy")
//            toDate = sdf.parse(toDateTextView.text.toString())
//            fromDate = sdf.parse(fromDateString)
//
//
//            if (fromDate.before(toDate)) {
//                receiptNoArrayList.add(receiptCount++.toString())
//                ownerNameArrayList.add(landlordNameEditText.text.toString())
//                addressLineArrayList.add(addressLineEditText.text.toString())
//                tenantNameArrayList.add(tenantNameEditText.text.toString())
//                fromDateArrayList.add(fromDateString.toString())
//                rentReceivedArrayList.add(totalRentReceivedEditText.text.toString())
//
//                if (landlordPanEditText.text.toString().toUpperCase() == "") {
//                    landLordPanArrayList.add("N/A")
//                } else {
//                    landLordPanArrayList.add(landlordPanEditText.text.toString().toUpperCase())
//                }
//                calendar = Calendar.getInstance()
//                calendar.time = sdf.parse(fromDateString)
//                monthArrayList.add(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH))
//                yearArrayList.add(calendar.get(Calendar.YEAR).toString())
//                calendar.add(Calendar.MONTH, 1)
//                calendar.add(Calendar.DATE, -1)
//                newToDateString = sdf.format(calendar.time)
//
//                toDateArrayList.add(newToDateString)
//                calendar.add(Calendar.DATE, 1)
//                fromDateString = sdf.format(calendar.time)
//                sdf = SimpleDateFormat("dd-MM-yyyy")
//                fromDate = sdf.parse(fromDateString)
//                checkDate()
//            }
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//
//    }
//
//    private fun pdf() {
//        document = Document(PageSize.A4)
//
////         document = Document()
//
//        try {
//            val path = Environment.getExternalStorageDirectory().absolutePath + "/Receipt/rentReceipt.pdf"
////            val dir = File(path)
////            if (!dir.exists())
////                dir.mkdirs()
////            val file = File(dir, currentDateTimeString + "rentReceipt.pdf")
//            val fOut = FileOutputStream(path)
//            PdfWriter.getInstance(document, fOut)
//
//            document.open()
//            for (i in receiptNoArrayList.indices) {
//
//                var table: PdfPTable?
//                table = PdfPTable(2)
//                table.widthPercentage = 105f
//                table.setWidths(floatArrayOf(0.4f, 0.6f))
//
//                val d = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    mContext.getDrawable(R.drawable.pdf_logo)
//                } else {
//                    TODO("VERSION.SDK_INT < LOLLIPOP")
//                }
//                val bitDw = d as BitmapDrawable
//                val bmp = bitDw.bitmap
//                val stream = ByteArrayOutputStream()
//                bmp.compress(Bitmap.CompressFormat.PNG, 50, stream)
//                val image1 = Image.getInstance(stream.toByteArray())
//                image1.scaleAbsolute(250f, 80f)
//
//                val title = PdfPCell(Phrase("Rent Receipt", FontFactory.getFont("Roboto", 16f, Font.BOLD, BaseColor(0, 184, 148))))
//                title.paddingLeft = 20f
//                title.paddingTop = 20f
//                title.paddingBottom = 20f
//                title.borderWidthBottom = 1f
//                title.borderColorBottom = BaseColor(239, 239, 239)
//                title.borderWidth = 0f
//                title.horizontalAlignment = 0
//                table.addCell(title)
//
//                val month = PdfPCell(Phrase(monthArrayList.get(i).toString() + " " + yearArrayList[i].toString(), FontFactory.getFont("Roboto", 16f, Font.BOLD, BaseColor(80, 80, 80))))
//                month.paddingRight = 20f
//                month.paddingTop = 20f
//                month.paddingBottom = 20f
//                month.borderWidthBottom = 1f
//                month.borderColorBottom = BaseColor(239, 239, 239)
//                month.borderWidth = 0f
//                month.horizontalAlignment = Element.ALIGN_RIGHT
//                table.addCell(month)
//
//                val receiptNo = PdfPCell(Phrase("Receipt No.: " + receiptNoArrayList[i].toString(), FontFactory.getFont("Roboto", 10f, Font.NORMAL, BaseColor(80, 80, 80))))
//                receiptNo.horizontalAlignment = Element.ALIGN_RIGHT
//                receiptNo.paddingRight = 20f
//                receiptNo.paddingTop = 10f
//                receiptNo.borderWidthLeft = 0f
//                receiptNo.borderWidthTop = 0f
//                receiptNo.borderWidthBottom = 0f
//                receiptNo.borderWidth = 0f
//                receiptNo.colspan = 2
//                table.addCell(receiptNo)
//
//                val date = PdfPCell(Phrase("Date: " + toDateArrayList[i], FontFactory.getFont("Roboto", 10f, Font.NORMAL, BaseColor(80, 80, 80))))
//                date.horizontalAlignment = Element.ALIGN_RIGHT
//                date.paddingRight = 20f
//                date.paddingBottom = 5f
//                date.borderWidthLeft = 0f
//                date.borderWidthTop = 0f
//                date.borderWidthBottom = 0f
//                date.borderWidth = 0f
//                date.colspan = 2
//                table.addCell(date)
//
//                val descriptionParagraph = Paragraph("Received sum of INR ", FontFactory.getFont("Roboto", 12f, Font.NORMAL, BaseColor(80, 80, 80)))
//                descriptionParagraph.add(Chunk("Rs. " + rentReceivedArrayList[i].toString(), FontFactory.getFont("Roboto", 12f, Font.BOLD, BaseColor(80, 80, 80))))
//                descriptionParagraph.add(Chunk(" from ", FontFactory.getFont("Roboto", 12f, Font.NORMAL, BaseColor(80, 80, 80))))
//                descriptionParagraph.add(Chunk(tenantNameArrayList[i].toString(), FontFactory.getFont("Roboto", 12f, Font.BOLD, BaseColor(80, 80, 80))))
//                descriptionParagraph.add(Chunk(" towards the rent of property located at ", FontFactory.getFont("Roboto", 12f, Font.NORMAL, BaseColor(80, 80, 80))))
//                descriptionParagraph.add(Chunk(address, FontFactory.getFont("Roboto", 12f, Font.BOLD, BaseColor(80, 80, 80))))
//                descriptionParagraph.add(Chunk(" for the period from ", FontFactory.getFont("Roboto", 12f, Font.NORMAL, BaseColor(80, 80, 80))))
//                descriptionParagraph.add(Chunk(fromDateArrayList[i].toString(), FontFactory.getFont("Roboto", 12f, Font.BOLD, BaseColor(80, 80, 80))))
//                descriptionParagraph.add(Chunk(" to ", FontFactory.getFont("Roboto", 12f, Font.NORMAL, BaseColor(80, 80, 80))))
//                descriptionParagraph.add(Chunk(toDateArrayList[i].toString(), FontFactory.getFont("Roboto", 12f, Font.BOLD, BaseColor(80, 80, 80))))
//
//                val para = PdfPCell(descriptionParagraph)
//                para.paddingLeft = 20f
//                para.paddingTop = 30f
//                para.paddingRight = 20f
//                para.paddingBottom = 10f
//                para.borderWidthBottom = 0f
//                para.colspan = 2
//                para.borderWidth = 0f
//                para.horizontalAlignment = Element.ALIGN_LEFT
//                para.setLeading(0f, 1.2f)
//                table.addCell(para)
//
//                val landlordNamePara = Paragraph("Landlord Name: ", FontFactory.getFont("Roboto", 12f, Font.NORMAL, BaseColor(80, 80, 80)))
//                landlordNamePara.add(Chunk(ownerNameArrayList[i].toString(), FontFactory.getFont("Roboto", 12f, Font.BOLD, BaseColor(80, 80, 80))))
//
//                val landlordName = PdfPCell(landlordNamePara)
//                landlordName.paddingRight = 20f
//                landlordName.paddingLeft = 20f
//                landlordName.paddingTop = 10f
//                landlordName.colspan = 2
//                landlordName.borderWidthBottom = 0f
//                landlordName.borderWidthTop = 0f
//                landlordName.borderWidth = 0f
//                landlordName.horizontalAlignment = Element.ALIGN_LEFT
//                table.addCell(landlordName)
//
//                val landlordPanPara = Paragraph("Landlord PAN: ", FontFactory.getFont("Roboto", 12f, Font.NORMAL, BaseColor(80, 80, 80)))
//                landlordPanPara.add(Chunk(landLordPanArrayList.get(i).toString(), FontFactory.getFont("Roboto", 12f, Font.BOLD, BaseColor(80, 80, 80))))
//
//                val landlordPan = PdfPCell(landlordPanPara)
//                landlordPan.paddingRight = 20f
//                landlordPan.paddingLeft = 20f
//                landlordPan.paddingBottom = 10f
//                landlordPan.colspan = 2
//                landlordPan.horizontalAlignment = Element.ALIGN_LEFT
//                landlordPan.borderWidth = 0f
//                table.addCell(landlordPan)
//
//                val note = PdfPCell(Phrase("Note: This slip is electronically generated by allindiaitr.com based on the information provided by the user.", FontFactory.getFont("Roboto", 10f, Font.NORMAL, BaseColor(80, 80, 80))))
//                note.paddingTop = 20f
//                note.paddingLeft = 20f
//                note.paddingRight = 20f
//                note.colspan = 2
//                note.borderWidthTop = 1f
//                note.borderWidth = 0f
//                note.borderColorBottom = BaseColor(239, 239, 239)
//                note.horizontalAlignment = Element.ALIGN_CENTER
//                table.addCell(note)
//                document.add(table)
//
//                val rect = Rectangle(25f, 1160f, 815f, 640f)
//                rect.border = com.itextpdf.text.Rectangle.BOX
//                rect.borderWidth = 2f
//                rect.borderColor = BaseColor.BLACK
//                document.newPage()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            document.close()
//        }
//        viewPdf(currentDateTimeString + "rentReceipt.pdf", "Receipt")
////        intentForPDFView(currentDateTimeString + "rentReceipt.pdf", "Receipt")
//    }
//
//
//
//
//
//    private fun intentForPDFView(file: String, directory: String)
//    {
//        val intent:Intent= Intent(this,PdfViwerActivity::class.java)
//        intent.putExtra("time",currentDateTimeString+"rentReceipt.pdf")
//        intent.putExtra("directory","Receipt")
//
//        startActivity(intent)
//    }
//
//
//    private fun viewPdf(file: String, directory: String) {
//
//        val pdfFile = File(Environment.getExternalStorageDirectory().toString() + "/" + directory + "/" + file)
////        val path = Uri.fromFile(pdfFile)
//        var path = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);
////        val path=FileProvider.getUriForFile(mContext,"dell.com.allindiaitr.fileprovider",pdfFile)
//        val pdfIntent = Intent(Intent.ACTION_VIEW)
//        pdfIntent.setDataAndType(path, "application/pdf")
////        pdfIntent.setDataAndType(path, "MIME-TYPE")
//
//        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        pdfIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
////        pdfIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//      //  pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        try {
//
////            Intent(Intent.ACTION_VIEW).apply {
////                setDataAndType(path, "application/pdf")
////                mContext.startActivity(this)
//
//            val sendIntent: Intent = Intent().apply {
//                action = Intent.ACTION_VIEW
//                type = "application/pdf"
//            }
//
//
//            //path= Uri.parse("content://dell.com.allindiaitr.fileprovider/external_files/Receipt/A.pdf")
//            sendIntent.data = path
//            sendIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            sendIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            //   val shareIntent = Intent.createChooser(sendIntent, null)
////            startActivity(sendIntent)
//
//
////            }
//
//            startActivity(pdfIntent)
//
//            if (ConnectionDetector().isConnectingToInternet(mContext)) {
//                postRentReceipt()
//                mailRentReceipt(path.path!!.toString())
//
////                val dialogBuilder = AlertDialog.Builder(this)
////                dialogBuilder.setMessage("Your rent receipts have been generated\nKindly check $pdfFile")
////                    // if the dialog is cancelable
////                    .setCancelable(false)
////                    // positive button text and action
////                    .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
////                        val intent:Intent= Intent(mContext,DashboardActivity::class.java)
////                        mContext.startActivity(intent)
////                        finish()
////                    })
////                    // negative button text and action
////                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
////                        val intent:Intent= Intent(mContext,DashboardActivity::class.java)
////                        mContext.startActivity(intent)
////                        dialog.cancel()
////                    })
////
////                // create dialog box
////                val alert = dialogBuilder.create()
////                // set title for alert dialog box
////                alert.setTitle("AllIndiaITR")
////                // show alert dialog
////                alert.show()
//
//            } else {
//                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
//            }
//
//        } catch (e: Exception) {
//            Toast.makeText(mContext, "Can not read pdf file", Toast.LENGTH_SHORT).show()
//        }
//
//    }
//
//
//    private fun pdfViwerwithFilePicker(file: String, directory: String)
//    {
//
////        val pdfFile = File(Environment.getExternalStorageDirectory().toString() + "/" + directory + "/" + file)
////        val path = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);
////
////
////        val intent4 = Intent(this, NormalFilePickActivity::class.java)
////        intent4.putExtra(Constant.MAX_NUMBER, 9)
////        intent4.putExtra(BaseActivity.IS_NEED_FOLDER_LIST, true)
////        intent4.putExtra(
////            NormalFilePickActivity.SUFFIX,
////            arrayOf("pdf"))
////        startActivityForResult(intent4, PICK_PDF_CODE)
//    }
//
//    private fun postRentReceipt(){
//        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
//
//        val call = apI_Interface.hra_rentReceipt(usefulToolsModel)
//        call.enqueue(object : Callback<Void> {
//
//            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
//                try {
//                    if (response!!.isSuccessful) {
//                        dialog.hideDialog()
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
//    private fun mailRentReceipt(pdfFile: String) {
////        val path = saveImage(MediaStore.Images.Media.getBitmap(this.contentResolver, pdfFile))
//        if (pdfFile == "") {
//            Toast.makeText(this, "Please move your file to internal storage and retry", Toast.LENGTH_LONG).show()
//            return
//        }
//        else {
//            var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
//
//            val file = File(pdfFile)
//
//            val mFile = RequestBody.create(MediaType.parse("application/pdf"), file)
//            val body = MultipartBody.Part.createFormData("file", file.name, mFile)
//
//            val call = apI_Interface.mailRentReceipt(emailIdEditText.text.toString(), body)
//            call.enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    try {
//                        if (response.isSuccessful) {
//                            if (response.body() != null) {
//                                dialog.hideDialog()
//                            }
//                            else {
//                                dialog.hideDialog()
//                            }
//                        } else {
//                            dialog.hideDialog()
////                            Toast.makeText(mContext, "problem uploading image", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    dialog.hideDialog()
////                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }
//
//    private fun saveImage(myBitmap: Bitmap):String {
//        val bytes = ByteArrayOutputStream()
//        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
//        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString())
//        if (!wallpaperDirectory.exists()) {
//            wallpaperDirectory.mkdirs()
//        }
//        try {
//            val f = File(wallpaperDirectory, ((Calendar.getInstance().timeInMillis).toString() + ".jpg"))
//            f.createNewFile()
//            val fo = FileOutputStream(f)
//            fo.write(bytes.toByteArray())
//            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
//            fo.close()
//            return f.absolutePath
//        }
//        catch (e1: IOException) {
//            e1.printStackTrace()
//        }
//        return ""
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
//}
