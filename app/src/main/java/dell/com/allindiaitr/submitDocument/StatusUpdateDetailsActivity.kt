package dell.com.allindiaitr.submitDocument

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.BaseActivity
import com.vincent.filepicker.activity.NormalFilePickActivity
import com.vincent.filepicker.filter.entity.NormalFile
import dell.com.allindiaitr.BuildConfig
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityStatusUpdateDetailsBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
import id.zelory.compressor.Compressor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.logging.Logger

class StatusUpdateDetailsActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    var newItrBase = NewItrBase.instance
    var appPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    var PERMISSIONS_REQUEST_CODE = 1024
    var GALLERY = 1
    var CAMERA = 2
    var PICK_PDF_CODE = 3
    lateinit var tv_dob: TextView
    val editTexts = ArrayList<EditText>()
    var fatherNameString = "notExist"
    var dobString = "notExist"
    var panNoString = "notExist"
    var aadharNoString = "notExist"
    var tanNoString = "notExist"
    var ifscCodeString = "notExist"
    var addressString = "notExist"
    var salaryString = "notExist"
    var fileTypeString = "notExist"
    lateinit var panPatternString: String
    lateinit var aadhaarPatternString: String
    lateinit var IFSCRexString: String
    lateinit var salaryRexString: String
//    var postPath: String? = null
    var postPath: String? = ""
    var password = ""
    var filePath: Uri? = null

    private var mImageFileLocation = ""
    private var fileUri: Uri? = null
    var photoFile: File? = null
    private var mediaPath: String? = null
    private var appPreferences: AppPreferences? = null
    private lateinit var binding: ActivityStatusUpdateDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusUpdateDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text =getString(R.string.title_update_details)

        checkAndRequestPermissions()

        newItrBase.efilingStatusField?.let { addView(it) }

        panPatternString = "[A-Za-z]{5}\\d{4}[A-Za-z]{1}"
        aadhaarPatternString = "[0-9]$"
        IFSCRexString = "^[A-Z]{4}0[A-Z0-9]{6}$"
        salaryRexString = "^[0-9]$"

        binding.btnContinue.setOnClickListener(this)
        binding.cardUploadform.setOnClickListener(this)
        
        ButtonVisibility().hideButton(mContext, binding.activityStatusUpdateDetails, binding.btnContinue)
    }



    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_continue -> {
                for (j in newItrBase.efilingStatusField!!.indices) {
                    if (editTexts[j].tag == "Father Name") {
                        fatherNameString = editTexts[j].text.toString()
                    }
                    if (editTexts[j].tag == "Pan" || newItrBase.efilingStatusField!![j].label.equals("PAN Number")) {
                        panNoString = editTexts[j].text.toString()
                    }
                    if (editTexts[j].tag == "Aadhaar Card Number") {
                        aadharNoString = editTexts[j].text.toString()
                    }
                    if (editTexts[j].tag == "TAN") {
                        tanNoString = editTexts[j].text.toString()
                    }
                    if (editTexts[j].tag == "IFSC Code") {
                        ifscCodeString = editTexts[j].text.toString()
                    }
                    if (editTexts[j].tag == "Address") {
                        addressString = editTexts[j].text.toString()
                    }
                    if (editTexts[j].tag == "Salary Incomplete") {
                        salaryString = editTexts[j].text.toString()
                    }
                    if (newItrBase.efilingStatusField!![j].label.equals("DOB")) {
                        dobString = tv_dob.text.toString().trim { it <= ' ' }
                    }
                    if (newItrBase.efilingStatusField!![j].label.equals("Form 16/16A")) {
                        fileTypeString = "Form16"
                    }
                }
                if (checkEmpty()) {
                    uploadForm16()
                }
            }
            R.id.card_uploadform -> {
               // var items = arrayOf("Select photo from gallery", "Capture photo from camera", "Select PDF of the document")
                var items = arrayOf(getString(R.string.popup_select_gallery), getString(R.string.popup_select_camera), getString(R.string.popup_select_pdf))
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.popup_title))
                    .setItems(items, DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            0 -> {

                                val intent = Intent(Intent.ACTION_PICK)
                                intent.type = "image/*"
                                startActivityForResult(intent, GALLERY)
                                
                            }
                            1 -> {
                                captureImage()
                            }
                            2 -> {
                                val intent4 = Intent(this, NormalFilePickActivity::class.java)
                                intent4.putExtra(Constant.MAX_NUMBER, 9)
                                intent4.putExtra(BaseActivity.IS_NEED_FOLDER_LIST, true)
                                intent4.putExtra(
                                    NormalFilePickActivity.SUFFIX,
                                    arrayOf("pdf"))
                                startActivityForResult(intent4, PICK_PDF_CODE)
                            }
                        }

                    }).show()
            }
        }
    }

    private fun checkAndRequestPermissions(): Boolean{
        var listPermissionsNeeded = arrayListOf<String>()
        for (i in 0 until appPermissions.size) {
            if (ContextCompat.checkSelfPermission(mContext, appPermissions[i].toString()) != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(appPermissions[i].toString())
        }

        if (listPermissionsNeeded.isNotEmpty()){
            ActivityCompat.requestPermissions(this,
                listPermissionsNeeded.toArray(arrayOfNulls(listPermissionsNeeded.size)),
                PERMISSIONS_REQUEST_CODE)
            return false
        }

        return true
    }

    private fun addView(efillingStatusField: List<NewItrBase>) {
        val paramsLabel = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paramsLabel.leftMargin = resources.getDimension(R.dimen.fifteen).toInt()
        paramsLabel.rightMargin = resources.getDimension(R.dimen.fifteen).toInt()

        val paramsEditText =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paramsEditText.leftMargin = resources.getDimension(R.dimen.fifteen).toInt()
        paramsEditText.rightMargin = resources.getDimension(R.dimen.fifteen).toInt()
        paramsEditText.bottomMargin = resources.getDimension(R.dimen.fifteen).toInt()
        paramsEditText.height = resources.getDimension(R.dimen.forty).toInt()

        for (j in efillingStatusField.indices) {
            val tv_label = TextView(this)
            tv_label.text = efillingStatusField[j].label.toString()
            tv_label.layoutParams = paramsLabel
            tv_label.textSize = 12f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv_label.setTextColor(getColor(R.color.lighter_black))
            }
            binding.linearlayoutDisplay.addView(tv_label)

            val editText = EditText(this)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                editText.background = getDrawable(R.drawable.text_field)
            }
            editText.layoutParams = paramsEditText
            editText.textSize = 12f
            editText.setPadding(
                resources.getDimension(R.dimen.twelve).toInt(),
                resources.getDimension(R.dimen.twelve).toInt(),
                resources.getDimension(R.dimen.twelve).toInt(),
                resources.getDimension(R.dimen.twelve).toInt()
            )
            editText.includeFontPadding = true
            editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            editText.tag = efillingStatusField[j].label
            if (efillingStatusField[j].label.equals("DOB")) {
                editText.visibility = View.GONE
                tv_dob = TextView(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tv_dob.background = getDrawable(R.drawable.text_field)
                }
                tv_dob.layoutParams = paramsEditText
                tv_dob.textSize = 12f
                tv_dob.setPadding(
                    resources.getDimension(R.dimen.twelve).toInt(),
                    resources.getDimension(R.dimen.twelve).toInt(),
                    resources.getDimension(R.dimen.twelve).toInt(),
                    resources.getDimension(R.dimen.twelve).toInt()
                )
                tv_dob.includeFontPadding = true
                tv_dob.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_calendar_filled, 0)
                tv_dob.setOnClickListener(View.OnClickListener {
                    DatePicker(mContext, tv_dob, 18).datePickerDialog()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tv_dob.setTextColor(getColor(R.color.light_black))
                    }
                })
                binding.linearlayoutDisplay.addView(tv_dob)
            }

            if (efillingStatusField[j].label.equals("Pan") || efillingStatusField[j].label.equals("PAN Number")) {
                val FilterArray = arrayOfNulls<InputFilter>(1)
                FilterArray[0] = InputFilter.LengthFilter(10)
                editText.filters = FilterArray
                editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            }

            if (efillingStatusField[j].label.equals("Form 16/16A") && (efillingStatusField[j].fileType.equals("file") || efillingStatusField[j].fileType == null)) {
                tv_label.visibility = View.GONE
                editText.visibility = View.GONE
                binding.linearUploadForm16.visibility = View.VISIBLE
            }

            editTexts.add(editText)
            binding.linearlayoutDisplay.addView(editText)
        }
    }

    private fun checkEmpty(): Boolean {
        if (fatherNameString == "") {
            Toast.makeText(applicationContext, "Field(s) are mandatory.", Toast.LENGTH_SHORT).show()
        } else if (fatherNameString != "" && fatherNameString != "notExist" && fatherNameString.matches(".*\\d+.*".toRegex())) {
            Toast.makeText(applicationContext, "Enter Correct Father's Name.", Toast.LENGTH_SHORT).show()
        } else if (dobString == "") {
            Toast.makeText(applicationContext, "Field(s) are mandatory.", Toast.LENGTH_SHORT).show()
        } else if (panNoString == "") {
            Toast.makeText(applicationContext, "Field(s) are mandatory.", Toast.LENGTH_SHORT).show()
        } else if (panNoString != "" && panNoString != "notExist" && !panNoString.matches(panPatternString.toRegex())) {
            Toast.makeText(applicationContext, "Enter Correct Pan No.", Toast.LENGTH_SHORT).show()
        } else if (aadharNoString == "") {
            Toast.makeText(applicationContext, "Field(s) are mandatory.", Toast.LENGTH_SHORT).show()
        } else if (aadharNoString != "" && aadharNoString != "notExist" && !aadharNoString.matches(".*\\d+.*".toRegex())
            && (aadharNoString.length < 12 || aadharNoString.length > 28)
        ) {
            Toast.makeText(applicationContext, "Enter correct Aadhaar No.", Toast.LENGTH_SHORT).show()
        } else if (addressString == "") {
            Toast.makeText(applicationContext, "Field(s) are mandatory.", Toast.LENGTH_SHORT).show()
        } else if (addressString.length > 50) {
            Toast.makeText(applicationContext, "Address should not 50 characters", Toast.LENGTH_SHORT).show()
        } else if (addressString.contains("&") || addressString.contains("*")
            || addressString.contains("(") || addressString.contains(")")
        ) {
            Toast.makeText(applicationContext, "Address should not contain &, *, ( and )", Toast.LENGTH_SHORT).show()
        } else if (salaryString == "") {
            Toast.makeText(applicationContext, "Field(s) are mandatory.", Toast.LENGTH_SHORT).show()
        } else if (salaryString != "" && salaryString != "notExist" && !salaryString.matches(".*\\d+.*".toRegex())) {
            Toast.makeText(applicationContext, "Enter correct salary.", Toast.LENGTH_SHORT).show()
        } else if (tanNoString == "") {
            Toast.makeText(applicationContext, "Field(s) are mandatory.", Toast.LENGTH_SHORT).show()
        } else if (ifscCodeString == "") {
            Toast.makeText(applicationContext, "Field(s) are mandatory.", Toast.LENGTH_SHORT).show()
        } else if (ifscCodeString != "" && ifscCodeString != "notExist" && !ifscCodeString.matches(IFSCRexString.toRegex())) {
            Toast.makeText(applicationContext, "Enter correct IFSC code.", Toast.LENGTH_SHORT).show()
        } else if (fileTypeString == "Form16" && postPath!!.isEmpty()){
            Toast.makeText(applicationContext, "Kindly upload your document.", Toast.LENGTH_SHORT).show()
        }
        else {
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY -> {
                    val contentURI = data!!.data

                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
//                        postPath = saveImage(bitmap)
                        val uri=FilePath.getPath(mContext,contentURI)
                        postPath=uri
                        uploadConfirm()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                CAMERA -> {

                    if (Build.VERSION.SDK_INT > 21) {
                        customCompressImage(photoFile!!)
                    } else {
                        // fileUri
//                        var imgFile = getTempImage()
//                        imgFile = customCompressImage(imgFile)

                        val thumbnail = data!!.extras!!.get("data") as Bitmap
                        postPath = saveImage(thumbnail)
                        uploadConfirm()
                    }
                }
                PICK_PDF_CODE -> {
//                    val contentURI = data!!.data
//                    postPath = savePDF(contentURI!!)
//                    uploadConfirm()

                    val list = data?.getParcelableArrayListExtra<NormalFile>(Constant.RESULT_PICK_FILE)
                    if (list != null) {
                        for (file in list) {
                            postPath = file.path
                        }
                    }
                    uploadConfirm()
                }
            }
        }
    }

//**********************************************************************************************************************NEW CODE



    /**
     * Launching camera app to capture image
     */
    private fun captureImage() {
        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            val callCameraApplicationIntent = Intent()
            callCameraApplicationIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

            // We give some instruction to the intent to save the image
            // var photoFile: File? = null

            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = createImageFile()
                // Here we call the function that will try to catch the exception made by the throw function
            } catch (e: IOException) {
                Logger.getAnonymousLogger().info("Exception error in generating the file")
                e.printStackTrace()
            }

            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
            val outputUri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider", photoFile!!)
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            Logger.getAnonymousLogger().info("Calling the camera App by intent")

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA)
        } else {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)

        }


    }



    @Throws(IOException::class)
    internal fun createImageFile(): File {
        Logger.getAnonymousLogger().info("Generating the image - method started")

        // Here we create a "non-collision file name", alternatively said, "an unique filename" using the "timeStamp" functionality
        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmSS").format(Date())
        val imageFileName = "IMAGE_" + timeStamp
        // Here we specify the environment location and the exact path where we want to save the so-created file
//        val storageDirectory = File(Environment.getExternalStorageDirectory(), imageFileName)
//        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app")
//        Logger.getAnonymousLogger().info("Storage directory set")
//
//        // Then we create the storage directory if does not exists
//        if (!storageDirectory.exists()) storageDirectory.mkdir()

        // Here we create the file using a prefix, a suffix and a directory
        val image = File(Environment.getExternalStorageDirectory(), imageFileName + ".jpeg")
//        val image = File(storageDirectory, imageFileName + ".jpeg")
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set")

        mImageFileLocation = image.absolutePath
        // fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image
    }



//**********************************************************************************************************************NEW CODE

    fun getTempImage(): File {
        return File(Environment.getExternalStorageDirectory(), "Image.jpeg")
//        return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Image.jpeg")

    }


    fun customCompressImage(actualImage: File): File {
        var compressedImage: File? = null
        if (actualImage == null) {
            Toast.makeText(mContext, "Please move your file to internal storage and retry", Toast.LENGTH_SHORT).show();
        } else {
            try {

                compressedImage = Compressor(this)
                    .setMaxWidth(1000)
                    .setMaxHeight(1000)
                    .setQuality(100)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                    .setDestinationDirectoryPath(Environment.getExternalStorageDirectory().absolutePath)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath)
                    .compressToFile(actualImage)

//                compressedImage = Compressor(mContext).compressToFile(actualImage)




                if(compressedImage.exists())
                {
                    var myBitmap= BitmapFactory.decodeFile(compressedImage.absolutePath)
                    filePath = Uri.fromFile(compressedImage)
                    var fileName  = getRealPathFromURI(filePath!!)
                    var path=filePath.toString()
                    var fileNameString=fileName.substring(fileName.lastIndexOf("/") + 1).replace("%20", " ")

                    postPath=path.replace("file://","").trim()
                    uploadConfirm()
                }

//                compressedImage = Compressor(mContext).compressToFile(actualImage)
//                compressedImage = Compressor(mContext)
//                    .setMaxWidth(1000)
//                    .setMaxHeight(1000)
//                    .setQuality(100)
//                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                    .setDestinationDirectoryPath(
//                        Environment.getExternalStoragePublicDirectory(
//                            Environment.DIRECTORY_PICTURES
//                        ).absolutePath
//                    )
//                    .compressToFile(actualImage)
            } catch (e: java.lang.Exception) {
                Toast.makeText(mContext, "Please move your file to internal storage and retry", Toast.LENGTH_SHORT).show();
            }


        }

        return compressedImage!!
    }


    private fun getRealPathFromURI(contentURI: Uri): String {
        var result: String? = ""
        try {
            val cursor = contentResolver.query(contentURI, null, null, null, null)
            if (cursor == null) {
                result = contentURI.path
            } else {
                cursor.moveToFirst()
                var idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (idx == -1) {
                    idx = idx + 2
                    result = cursor.getString(idx)
                } else {
                    result = cursor.getString(idx)
                }
                cursor.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return result!!
    }


    private fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }
        try
        {
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .timeInMillis).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null)
            fo.close()
            return f.absolutePath
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    private fun uploadConfirm(){
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_password, null)
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setView(view)
        alertDialog.setCancelable(false)
        alertDialog.setIcon(R.drawable.logo)
        alertDialog.setTitle("Password (Optional)")
        var ediTextpassword = view.findViewById<EditText>(R.id.ediTextpassword)
        alertDialog.setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->
            fileTypeString="Form16"
            password = if (ediTextpassword.text.toString().isEmpty())
                ""
            else
                ediTextpassword.text.toString()
            uploadForm16()
        })
        alertDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
            fileTypeString="Form16"
            password = ""
            uploadForm16()
        })
        alertDialog.show()
    }

    private fun uploadForm16() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))
        try {
            var responseDetailsJson = JSONObject()
            responseDetailsJson.put("UserAssestmentYearId", newItrBase.selectedUser_userAssessmentYearUserID)
            if (panNoString != "notExist") {
                responseDetailsJson.put("Pan", panNoString)
            }
            if (fatherNameString != "notExist") {
                responseDetailsJson.put("FatherName", fatherNameString)
            }
            if (dobString != "notExist") {
                responseDetailsJson.put("DOB", dobString)
            }
            if (aadharNoString != "notExist") {
                responseDetailsJson.put("Aadhar", aadharNoString)
            }
            if (addressString != "notExist") {
                responseDetailsJson.put("Address", addressString)
            }
            if (tanNoString != "notExist") {
                responseDetailsJson.put("Tan", tanNoString)
            }
            if (ifscCodeString != "notExist") {
                responseDetailsJson.put("IFSCCode", ifscCodeString)
            }
            if (salaryString != "notExist") {
                responseDetailsJson.put("Salary", salaryString)
            }
            if (fileTypeString != "notExist") {
                responseDetailsJson.put("FileType", fileTypeString)
            }
            var jsonString = responseDetailsJson.toString()

            if (postPath != "") {
                val file = File(postPath)

                val mFile = RequestBody.create(MediaType.parse("*/*"), file)
                val statusUpdate= RequestBody.create(MediaType.parse("text/plain"), jsonString)
                val mUserAssessmentYearId = RequestBody.create(MediaType.parse("text/plain"), newItrBase.selectedUser_userAssessmentYearUserID.toString())
                val mFilePassword= RequestBody.create(MediaType.parse("text/plain"), password)
                val body = MultipartBody.Part.createFormData("file", file.name, mFile)

//                val call = apI_Interface.statusUploadFile(body, statusUpdate, mUserAssessmentYearId, mFilePassword)
                val call = apI_Interface.statusUploadFile1(body, statusUpdate, mFilePassword)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body() != null) {
                                    dialog.hideDialog()
                                    val intent = Intent(mContext, ITROrderStatusActivity::class.java)
                                    newItrBase.itrStatus = "ITR Inprogress"
//                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                                    finish()
                                    startActivity(intent)
                                }
                                else {
                                    dialog.hideDialog()
                                }
                            } else {
                                dialog.hideDialog()
                                Toast.makeText(applicationContext, "Please move your file to internal storage and retry", Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            dialog.hideDialog()
                            Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        dialog.hideDialog()
                        Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                val statusUpdate= RequestBody.create(MediaType.parse("text/plain"), jsonString)
                val mUserAssessmentYearId = RequestBody.create(MediaType.parse("text/plain"), newItrBase.selectedUser_userAssessmentYearUserID.toString())
                val mFilePassword= RequestBody.create(MediaType.parse("text/plain"), password)

                val call = apI_Interface.statusUpdate(statusUpdate, mUserAssessmentYearId, mFilePassword)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body() != null) {
                                    dialog.hideDialog()
                                    val intent = Intent(mContext, ITROrderStatusActivity::class.java)
                                    newItrBase.itrStatus = "ITR Inprogress"
//                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                                    finish()
                                    startActivity(intent)
                                }
                                else {
                                    dialog.hideDialog()
                                }
                            } else {
                                dialog.hideDialog()
                                Toast.makeText(applicationContext, "Please move your file to internal storage and retry", Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            dialog.hideDialog()
                            Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        dialog.hideDialog()
                        Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            dialog.hideDialog()
            Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home){
           val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            val intent = Intent(mContext, ITROrderStatusActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivityForResult(intent, 0)
            overridePendingTransition(0, 0)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)

        val intent = Intent(mContext, ITROrderStatusActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        startActivityForResult(intent, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

}
