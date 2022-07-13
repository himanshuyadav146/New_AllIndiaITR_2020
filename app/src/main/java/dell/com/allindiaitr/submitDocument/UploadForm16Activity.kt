package dell.com.allindiaitr.submitDocument

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.UploadDocumentAdapter
import dell.com.allindiaitr.databinding.ActivityUploadForm16Binding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.NewItrBase
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
import java.util.*
import java.util.logging.Logger
import kotlin.math.min


class UploadForm16Activity : AppCompatActivity() {

    lateinit var apI_Interface: API_Interface
    lateinit var mContext: Context
    var newItrBase = NewItrBase.instance
    var adapterStatus = "Empty"
    var GALLERY = 1
    var CAMERA = 2
    var PERMISSIONS_REQUEST_CODE = 1024
    var postPath: String? = null
    var password = ""
    var filePath: Uri? = null
    private var mImageFileLocation = ""
    private var fileUri: Uri? = null
    var photoFile: File? = null
    private var mediaPath: String? = null
    private var isPermmited: Boolean = false
    private var appPreferences: AppPreferences? = null
    private lateinit var binding: ActivityUploadForm16Binding


    val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.isNotEmpty() && !it.values.contains(false)) {
                fetchImageFromCamera()
            } else {
                // GeneralUtils.openAppSettings(requireContext())
            }
        }

    private val startForCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //  get result here in result.data
                //val photo: Bitmap? = result.data?.extras?.get("data") as Bitmap?
                val thumbnail = result.data?.extras?.get("data") as Bitmap?
                postPath =
                    thumbnail?.let { getImageUri(it,mContext)?.let { it1 -> getRealPathFromURI(it1,mContext) } }?:mImageFileLocation

                uploadConfirm()
            } else {
                Toast.makeText(
                    mContext,
                    "Sorry, unable to read the image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    val galleryPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.isNotEmpty() && !it.values.contains(false)) {
//                getGalleryImageIntent()
            } else {
//                GeneralUtils.openAppSettings(requireContext())
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadForm16Binding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

        binding.toolbar.toolbarTitle.text = getString(R.string.title_select_uploadform16)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        isPermmited = checkAndRequestPermissions(mContext)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.imgUser.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
            binding.imgForms.backgroundTintList = mContext.getColorStateList(R.color.colorPrimary)
        } else {
            ViewCompat.setBackgroundTintList(
                binding.imgUser,
                ColorStateList.valueOf(Color.rgb(0, 184, 148))
            )
            ViewCompat.setBackgroundTintList(
                binding.imgForms,
                ColorStateList.valueOf(Color.rgb(0, 184, 148))
            )
        }

        if (ConnectionDetector().isConnectingToInternet(mContext))
            getDocumentList()
        else
            Toast.makeText(
                mContext,
                "Please Check Your Internet Connection",
                Toast.LENGTH_SHORT
            ).show()

        binding.contButton.setOnClickListener {
            if (adapterStatus == "Not Empty") {
                val intent = Intent(mContext, ImportantDetailsActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(
                    mContext,
                    "Kindly Upload Form 16 or Pension Slip",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        ButtonVisibility().hideButton(mContext, binding.activityUploadForm16, binding.contButton)

        binding.uploadForm16.setOnClickListener {

            val items = arrayOf(
                getString(R.string.popup_select_gallery),
                getString(R.string.popup_select_camera),
                getString(R.string.popup_select_pdf)
            )
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.popup_title))
                .setItems(items, DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        0 -> {
                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            galleryIntent.type = "image/*"
                            startActivityForResult(
                                Intent.createChooser(galleryIntent, "Select Image"),
                                GALLERY
                            )

                        }
                        1 -> {
                            fetchImageFromCamera()

                        }
                        2 -> {
                            getGalleryFileIntent()
                        }
                    }

                }).show()
        }
    }


    /*
    * New Implementation
    *
    * */

    /**
     * Function to pick only JPG, PNG and PDF file
     */
    private fun getGalleryFileIntent() {
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            galleryPermissionResult.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        } else {
            val mimeTypes = arrayOf( "application/pdf")
            val filePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            filePickerIntent.type = "application/pdf"
            filePickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            filePickerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            filePickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startForFileResult.launch(filePickerIntent)
        }
    }

    private val startForFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == Activity.RESULT_OK) {
                val imageFilePath: String
                if (it.data == null) {
                    Toast.makeText(
                        mContext,
                        "Unable to read file",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (it.data?.data != null) {
                        val imagePath = makeFileCopyInCacheDir(it.data?.data!!,mContext)
                        if (!TextUtils.isEmpty(imagePath)) {
                            imageFilePath = imagePath!!
                            postPath = imagePath!!
                            uploadConfirm()

                        } else {
                            imageFilePath = it.data?.data?.path.toString()
                            if (!TextUtils.isEmpty(imageFilePath)) {

                            }
                        }
                    }
                }
            }

        }

    /**
     * method to open the camera, capture image & import it in the app
     */
    private fun fetchImageFromCamera() {
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionResult.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )

        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", createImageFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startForCameraResult.launch(intent)
        }
    }


    private fun makeFileCopyInCacheDir(contentUri: Uri): String? {
        try {
            val filePathColumn = arrayOf(
                //Base File
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                //Normal File
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DISPLAY_NAME
            )
            //val contentUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", File(mediaUrl))
            val returnCursor = contentUri.let {
                mContext.contentResolver.query(
                    it,
                    filePathColumn,
                    null,
                    null,
                    null
                )
            }
            if (returnCursor != null) {
                returnCursor.moveToFirst()
                val nameIndex = returnCursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                val name = returnCursor.getString(nameIndex)
                val file = File(mContext.cacheDir, name)
                val inputStream = mContext.contentResolver.openInputStream(contentUri)
                val outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable = inputStream!!.available()

                //int bufferSize = 1024;
                val bufferSize = min(bytesAvailable, maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
//                Logger.e("File Path", "Path " + file.path)
//                Logger.e("File Size", "Size " + file.length())
                return file.absolutePath
            }
        } catch (ex: Exception) {
//            Logger.e("Exception", ex.message!!)
        }
        return contentUri.let { getRealPathFromURI(it).toString() }
    }


    override fun attachBaseContext(newBase: Context?) {
        mContext = newBase!!
        appPreferences = AppPreferences(newBase!!)
        val lang: String = appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase, lang))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            var i = 0
            for (i in 0..grantResults.size - 1) {
                if (grantResults.get(i) >= 0) {
                    isPermmited = true
                } else {
                    isPermmited = false
                }
            }
        }
    }


    private fun getDocumentList() {
        val documentNameList = arrayListOf<String>()
        val documentIdList = arrayListOf<String>()
        val dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getDocumentList(
            newItrBase.selectedUser_userAssessmentYearUserID.toString(),
            "Form16"
        )
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
                                documentIdList.add(getFormDocList[i].userUploadedDocumentId?.takeUnless { it.isEmpty() }
                                    ?: "")
                                documentNameList.add(getFormDocList[i].documentName?.takeUnless { it.isEmpty() }
                                    ?: "")
                            }
                            binding.recyclerView.adapter =
                                UploadDocumentAdapter(mContext, documentNameList, documentIdList)
                            adapterStatus = "Not Empty"
                        }
                    } else {
                        dialog.hideDialog()
                        Toast.makeText(
                            mContext,
                            resources.getString(R.string.error_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    dialog.hideDialog()
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<NewItrBase>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(
                    mContext,
                    resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data!!.data
                    try {
                        val uri = FilePath.getPath(mContext, contentURI)
                        postPath = uri
                        uploadConfirm()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        } else if (requestCode == CAMERA) {
            if (Build.VERSION.SDK_INT > 21) {
                customCompressImage(photoFile!!)
            } else {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                postPath = saveImage(thumbnail)
                uploadConfirm()
            }
        }
    }


    //https://andykotlin.blogspot.com/2017/10/compressor-library-kotlin.html


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
                    .setDestinationDirectoryPath(
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        ).absolutePath
                    )
                    .compressToFile(actualImage)


                if (compressedImage.exists()) {
                    var myBitmap = BitmapFactory.decodeFile(compressedImage.absolutePath)
                    filePath = Uri.fromFile(compressedImage)
                    var fileName = getRealPathFromURI(filePath!!)
                    var path = filePath.toString()
                    var fileNameString =
                        fileName?.substring(fileName.lastIndexOf("/") + 1)?.replace("%20", " ")

                    postPath = path.replace("file://", "").trim()
                    uploadConfirm()
                }

            } catch (e: Exception) {
                Toast.makeText(mContext, "Please Select an Image", Toast.LENGTH_SHORT).show();
            }


        }

        return compressedImage!!
    }



//    private fun getImageUri(inImage: Bitmap): Uri? {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(
//            mContext.contentResolver,
//            inImage,
//            "Title" + System.currentTimeMillis(),
//            null
//        )
//        return if (!TextUtils.isEmpty(path)) {
//            try {
//                Uri.parse(path)
//            } catch (e: Exception) {
//                null
//            }
//        } else {
//            null
//        }
//    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor =
            mContext.contentResolver.query(uri, null, null, null, null)
        // assert cursor != null;
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = try {
                cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
            } catch (e: Exception) {
//                Logger.e("FilePath", "error", e)
                try {
                    cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATA)
                } catch (e: Exception) {
//                    Logger.e("FilePath", "error", e)
                    try {
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    } catch (e: Exception) {
//                        Logger.e("FilePath", "error", e)
                        cursor.close()
                        return uri.path
                    }
                }
            }
            return cursor.getString(idx).also { cursor.close() }
        }
        return uri.path
    }


    private fun saveImage(myBitmap: Bitmap): String {
//        var original=BitmapFactory.decodeStream(myBitmap)
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

//        val bytes1 = bytes.toByteArray()
//        Base64.encodeToString(bytes1, Base64.DEFAULT)

        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance().timeInMillis).toString() + ".jpg")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
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
//        val image = File(Environment.getExternalStorageDirectory(), imageFileName + ".jpeg")
        val image = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageFileName + ".jpeg")
//        val image = File(storageDirectory, imageFileName + ".jpeg")
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set")

        mImageFileLocation = image.absolutePath
        // fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image
    }


    private fun uploadConfirm() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_password, null)
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setView(view)
        alertDialog.setCancelable(false)
        alertDialog.setIcon(R.drawable.logo)
        alertDialog.setTitle("Password (Optional)")
        var ediTextpassword = view.findViewById<EditText>(R.id.ediTextpassword)
        alertDialog.setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->
            password = if (ediTextpassword.text.toString().isEmpty())
                ""
            else
                ediTextpassword.text.toString()
            uploadFile()
        })
        alertDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
            password = ""
            uploadFile()
        })
        alertDialog.show()
    }

    private fun uploadFile() {
        if (postPath == null || postPath == "") {
//            Toast.makeText(this, "Kindly select one file ", Toast.LENGTH_LONG).show()
            Toast.makeText(
                this,
                "Please move your file to internal storage and retry ",
                Toast.LENGTH_LONG
            ).show()
            return
        } else {
            var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

            val file = File(postPath!!)

            val mFile = RequestBody.create(MediaType.parse("*/*"), file)
            val mUserAssessmentYearId = RequestBody.create(
                MediaType.parse("text/plain"),
                newItrBase.selectedUser_userAssessmentYearUserID.toString()
            )
            val mFileType = RequestBody.create(MediaType.parse("text/plain"), "Form16")
            val mFilePassword = RequestBody.create(MediaType.parse("text/plain"), password)
            val body = MultipartBody.Part.createFormData("file", file.name, mFile)

            val call =
                apI_Interface.uploadFile(body, mUserAssessmentYearId, mFileType, mFilePassword)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                dialog.hideDialog()
                                if (ConnectionDetector().isConnectingToInternet(mContext)) {
                                    getDocumentList()
                                } else {
                                    Toast.makeText(
                                        mContext,
                                        "Please Check Your Internet Connection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                dialog.hideDialog()
                            }
                        } else {
                            dialog.hideDialog()
                            Toast.makeText(
                                applicationContext,
                                "Please move your file to internal storage and retry",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dialog.hideDialog()
                        Toast.makeText(
                            mContext,
                            resources.getString(R.string.error_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    dialog.hideDialog()
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.error_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home) {
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
//            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}


