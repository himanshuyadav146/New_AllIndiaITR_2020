package dell.com.allindiaitr.revisedReturn

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.BaseActivity
import com.vincent.filepicker.activity.NormalFilePickActivity
import com.vincent.filepicker.filter.entity.NormalFile
import dell.com.allindiaitr.BuildConfig
import dell.com.allindiaitr.Enum.CommonVal
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.UploadDocumentAdapter
import dell.com.allindiaitr.databinding.ActivityReviseIntimationNoticeBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.Comment
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.submitDocument.PaymentActivity
import dell.com.allindiaitr.utils.*
import id.zelory.compressor.Compressor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.logging.Logger

class ReviseIntimationNoticeActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var apI_Interface : API_Interface
    lateinit var appPreferences: AppPreferences
    var appPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    var PERMISSIONS_REQUEST_CODE = 1024
    var newItrBase = NewItrBase.instance
    var GALLERY = 1
    var CAMERA = 2
    var PICK_PDF_CODE = 3
    var postPath: String? = null
    var password = ""
    var comment = Comment.instance
    var filePath: Uri? = null

    private var mImageFileLocation = ""
    private var fileUri: Uri? = null
    var photoFile: File? = null
    private var mediaPath: String? = null
    private lateinit var binding:ActivityReviseIntimationNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviseIntimationNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        appPreferences = AppPreferences(mContext)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = getString(R.string.personal_info)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        checkAndRequestPermissions()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.commentField.setBackgroundColor(getColor(R.color.lightest_grey))
        }
        else {
            binding.commentField.setBackgroundColor(Color.parseColor("#F8F8F8"))
        }

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getDocumentList()
        else
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()

        binding.contButton.setOnClickListener {
            if (binding.commentField.text.toString().isNotEmpty()){
                comment.userAssessmentYearId = newItrBase.selectedUser_userAssessmentYearUserID
                comment.comment = binding.commentField.text.toString()
                comment.commentType = "7"
                comment.createdBy = appPreferences.parentId
                if (ConnectionDetector().isConnectingToInternet(mContext))
                    postCommentData()
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(mContext, PaymentActivity::class.java)
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent)
            }
        }

        binding.uploadIntimationNotice.setOnClickListener {
            val items = arrayOf( getString(R.string.popup_select_gallery),
                getString(R.string.popup_select_camera),
                getString(R.string.popup_select_pdf)
            )

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.popup_title))
                .setItems(items, DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        0 -> {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, GALLERY)

//                            val galleryIntent = Intent(Intent.ACTION_PICK,
//                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                            startActivityForResult(galleryIntent, GALLERY)
                        }
                        1 -> {
//                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                            startActivityForResult(intent, CAMERA)
                            captureImage()
                        }
                        2 -> {
//                            val intent = Intent(Intent.ACTION_GET_CONTENT)
//                            intent.type = "application/pdf"
//                            startActivityForResult(intent, PICK_PDF_CODE)

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



    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

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

    private fun getDocumentList() {
        var documentNameList = arrayListOf<String>()
        var documentIdList = arrayListOf<String>()
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getDocumentList(newItrBase.selectedUser_userAssessmentYearUserID.toString(), CommonVal.RevisedReturn.name)
        call.enqueue(object : Callback<NewItrBase> {
            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        documentIdList.clear()
                        documentNameList.clear()
                        if (response.body().getFormDocList != null) {
                            var getFormDocList = response.body().getFormDocList!!
                            for (i in 0 until getFormDocList.size) {
                                documentIdList.add(getFormDocList[i].userUploadedDocumentId?.takeUnless { it.isEmpty() } ?: "")
                                documentNameList.add(getFormDocList[i].documentName?.takeUnless { it.isEmpty() } ?: "")
                            }
                            binding.recyclerView.adapter = UploadDocumentAdapter(mContext, documentNameList, documentIdList)
                            if (response.body().comment != null) {
                                val commentModel = response.body().comment
                                if (commentModel?.comment == null) {
                                    binding.commentField.hint = mContext.resources.getString(R.string.upload_other_write_comment)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        binding.commentField.setHintTextColor(mContext.getColor(R.color.grey))
                                    else
                                        binding.commentField.setHintTextColor(Color.parseColor("#AAAAAA"))
                                }
                                else
                                    binding.commentField.setText(commentModel.comment.toString())
                            }
                        }
                        else if (response.body().comment != null) {
                            val commentModel = response.body().comment
                            if (commentModel?.comment == null) {
                                binding.commentField.hint = mContext.resources.getString(R.string.upload_other_write_comment)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    binding.commentField.setHintTextColor(mContext.getColor(R.color.grey))
                                else
                                    binding.commentField.setHintTextColor(Color.parseColor("#AAAAAA"))
                            }
                            else
                                binding.commentField.setText(commentModel.comment.toString())
                        }
                        else {

                        }
                    }
                    else {
                        dialog.hideDialog()
                        Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }
        })
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
                    }else{
                        // fileUri
//                        var imgFile = getTempImage()
//                        imgFile = customCompressImage(imgFile)

                        val thumbnail = data!!.extras!!.get("data") as Bitmap
                        postPath = saveImage(thumbnail)
                        uploadConfirm()
                    }


//                    val thumbnail = data!!.extras!!.get("data") as Bitmap
//                    postPath = saveImage(thumbnail)
//                    uploadConfirm()
                }
                PICK_PDF_CODE -> {
//                    val contentURI = data!!.data
//                    postPath = savePDF(contentURI!!)
//                    uploadConfirm()

                    val list = data?.getParcelableArrayListExtra<NormalFile>(Constant.RESULT_PICK_FILE)
                    list.let {
                        if (it != null) {
                            for (file in it) {
                                postPath = file.path
                            }
                        }
                    }

                    uploadConfirm()
                }
            }
        }
    }


    fun getTempImage(): File {
        return File(Environment.getExternalStorageDirectory(), "Image.jpeg")
//        return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Image.jpeg")

    }
    //*********************************************************************************************NEW IMAGE UPLOAD

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









    //*********************************************************************************************NEW IMAGE UPLOAD


    fun customCompressImage(actualImage: File): File {
        var compressedImage: File? = null
        if (actualImage == null) {
            Toast.makeText(mContext, "Please Select an Image", Toast.LENGTH_SHORT).show();
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
            } catch (e: Exception) {
                Toast.makeText(mContext, "Please Select an Image", Toast.LENGTH_SHORT).show();
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result!!
    }


    private fun savePDF(contentURI: Uri): String {
        var result: String? = ""
        try {
            val cursor = contentResolver.query(contentURI, null, null, null, null)
            if (cursor == null) {
                result = contentURI.path
            } else {
                cursor.moveToFirst()
                var idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (idx == -1) {
                    idx += 2
                    result = cursor.getString(idx)
                } else {
                    result = cursor.getString(idx)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result.toString()
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
        AlertDialog.Builder(mContext)
            .setTitle("Confirm Upload...")
            .setMessage("Are you sure you want to upload this file?")
            .setIcon(R.drawable.logo)
            .setPositiveButton("YES", DialogInterface.OnClickListener { _, _ ->
                if (ConnectionDetector().isConnectingToInternet(mContext))
                    uploadFile()
                else
                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener { _, _ ->

            }).show()
    }

    private fun uploadFile() {
        if (postPath == null || postPath == "") {
            Toast.makeText(this, "Please move your file to internal storage and retry", Toast.LENGTH_LONG).show()
            return
        } else {
            var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

            val file = File(postPath!!)

            val mFile = RequestBody.create(MediaType.parse("*/*"), file)
            val mUserAssessmentYearId = RequestBody.create(MediaType.parse("text/plain"), newItrBase.selectedUser_userAssessmentYearUserID.toString())
            val mFileType= RequestBody.create(MediaType.parse("text/plain"), CommonVal.RevisedReturn.name)
            val mFilePassword= RequestBody.create(MediaType.parse("text/plain"), password)
            val body = MultipartBody.Part.createFormData("file", file.name, mFile)

            val call = apI_Interface.uploadFile(body, mUserAssessmentYearId, mFileType, mFilePassword)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                dialog.hideDialog()
                                if (ConnectionDetector().isConnectingToInternet(mContext)){
                                    getDocumentList()
                                }
                                else {
                                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                dialog.hideDialog()
                            }
                        } else {
                            dialog.hideDialog()
                            Toast.makeText(applicationContext, "Please move your file to internal storage and retry", Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun postCommentData() {
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.postComment(comment)
        call.enqueue(object : Callback<Comment> {

            override fun onResponse(call: Call<Comment>?, response: Response<Comment>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        val intent = Intent(mContext, PaymentActivity::class.java)
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        startActivity(intent)
                    }
                    else {
                        dialog.hideDialog()
                    }
                }
                catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Comment>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home){
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}
