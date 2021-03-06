package dell.com.allindiaitr.submitDocument

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.ContentValues.TAG
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
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dell.com.allindiaitr.BuildConfig
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityImportantDetailsBinding
import dell.com.allindiaitr.databinding.FragmentImpDetailsUploadBinding
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.interfaces.FragmentCommunicator
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

class ImpDetailsUploadFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var apI_Interface: API_Interface
    lateinit var appPreferences: AppPreferences
    var newItrBase = NewItrBase.instance
    lateinit var docVirtualUrlString: String
    lateinit var docIdFrontString: String
    lateinit var docIdBackString: String
    var GALLERY = 1
    var CAMERA = 2
    var PERMISSIONS_REQUEST_CODE = 1024
    var appPermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    lateinit var setImageString: String
    var postPath: String? = null

    var password = ""
    var filePath: Uri? = null
    private var mImageFileLocation = ""
    private var fileUri: Uri? = null
    var photoFile: File? = null
    private var mediaPath: String? = null
    private var isPermmited:Boolean=false
    lateinit var binding:FragmentImpDetailsUploadBinding

    private val startForCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //  get result here in result.data
                //val photo: Bitmap? = result.data?.extras?.get("data") as Bitmap?
                val thumbnail = result.data?.extras?.get("data") as Bitmap?
                postPath =
                    thumbnail?.let { getImageUri(it,mContext)?.let { it1 -> getRealPathFromURI(it1,mContext) } }?:mImageFileLocation
                uploadFile()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Sorry, unable to read the image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.isNotEmpty() && !it.values.contains(false)) {
                fetchImageFromCamera()
            } else {
                // GeneralUtils.openAppSettings(requireContext())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_imp_details_upload, container, false)
        binding = FragmentImpDetailsUploadBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext = requireContext()
        appPreferences = AppPreferences(mContext)
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)
        checkAndRequestPermissions(mContext)
        if (ConnectionDetector().isConnectingToInternet(mContext))
            getDocumentList()
        else
            Toast.makeText(
                mContext,
                "Please Check Your Internet Connection",
                Toast.LENGTH_SHORT
            ).show()



        binding.frontUploadCard.setOnClickListener {
            setImageString = "aadharcard1"
            showUploadDialogue()
        }

        binding.backUploadCard.setOnClickListener {
            setImageString = "aadharcard2"
            showUploadDialogue()
        }

        binding.retry1.setOnClickListener {
            setImageString = "aadharcard1"
            showUploadDialogue()
        }

        binding.retry2.setOnClickListener {
            setImageString = "aadharcard2"
            showUploadDialogue()
        }

        binding.deleteFront.setOnClickListener {
            deleteDialogue(docIdFrontString, "front")
        }

        binding.deleteBack.setOnClickListener {
            deleteDialogue(docIdBackString, "back")
        }

        binding.contButton.setOnClickListener {
            if (binding.frontUploadCard.visibility == View.VISIBLE && binding.backUploadCard.visibility == View.VISIBLE) {
                Toast.makeText(mContext, "Upload Both Sides of Aadhaar Card", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.frontUploadCard.visibility == View.VISIBLE && binding.backUploadCard.visibility == View.GONE) {
                Toast.makeText(mContext, "Upload Front Side of Aadhaar Card", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.frontUploadCard.visibility == View.GONE && binding.backUploadCard.visibility == View.VISIBLE) {
                Toast.makeText(mContext, "Upload Back Side of Aadhaar Card", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val intent = Intent(mContext, BankListActivity::class.java)
                (mContext as Activity).overridePendingTransition(
                    R.anim.slide_from_right,
                    R.anim.slide_to_left
                )
                startActivity(intent)
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            try {
                val mImm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mImm.hideSoftInputFromWindow(requireView().windowToken, 0)
                //                mImm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            } catch (e: Exception) {
                Log.e(TAG, "setUserVisibleHint: ", e)
            }

        }
    }

//    private fun checkAndRequestPermissions(): Boolean {
//        var listPermissionsNeeded = arrayListOf<String>()
//        for (i in 0 until appPermissions.size) {
//            if (ContextCompat.checkSelfPermission(
//                    mContext,
//                    appPermissions[i].toString()
//                ) != PackageManager.PERMISSION_GRANTED
//            )
//                listPermissionsNeeded.add(appPermissions[i].toString())
//        }
//
//        if (listPermissionsNeeded.isNotEmpty()) {
//            ActivityCompat.requestPermissions(
//                mContext as Activity,
//                listPermissionsNeeded.toArray(arrayOfNulls(listPermissionsNeeded.size)),
//                PERMISSIONS_REQUEST_CODE
//            )
//            return false
//        }
//
//        return true
//    }

    private fun showUploadDialogue() {
        var items = arrayOf("Select photo from gallery", "Capture photo from camera")
        AlertDialog.Builder(mContext)
            .setTitle("Select Action")
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

//                        captureImage()
                        fetchImageFromCamera()
                    }
                }

            }).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data!!.data

                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(mContext.contentResolver, contentURI)
//                        postPath = saveImage(bitmap)
                        val uri=FilePath.getPath(mContext,contentURI)
                        postPath=uri
                        uploadFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            } else if (requestCode == CAMERA) {
                if (Build.VERSION.SDK_INT > 21) {
                    customCompressImage(photoFile!!)
                } else {
                    val thumbnail = data!!.extras!!.get("data") as Bitmap
                    postPath = saveImage(thumbnail)
                    uploadFile()
                }
            }
        }
    }

    private fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                mContext,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
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
//            captureImage()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", createImageFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startForCameraResult.launch(intent)
        }
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
                mContext,
                BuildConfig.APPLICATION_ID + ".provider", photoFile!!
            )
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            Logger.getAnonymousLogger().info("Calling the camera App by intent")

            // The following strings calls the camera app and wait for his file in return.
//            startActivityForResult(callCameraApplicationIntent, CAMERA)
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startForCameraResult.launch(callCameraApplicationIntent)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", createImageFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startForCameraResult.launch(intent)

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

    /**
     * Creating file uri to store image/video
     */
    fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }


    private fun getOutputMediaFile(type: Int): File? {

        // External sdcard location
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Image.jpeg"
        )

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                Log.d(TAG, "Oops! Failed create "
//                        + IMAGE_DIRECTORY_NAME + " directory")
                return null
            }
        }
        // Create a media file name
        val timeStamp = java.text.SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val mediaFile: File
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            mediaFile = File(
                mediaStorageDir.path + File.separator
                        + "IMG_" + ".jpg"
            )
        } else {
            return null
        }

        return mediaFile
    }



    fun customCompressImage(actualImage: File): File {
        var compressedImage: File? = null
        if (actualImage == null) {
            Toast.makeText(mContext, "Please Select an Image", Toast.LENGTH_SHORT).show();
        } else {
            try {


                compressedImage = Compressor(mContext)
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

//                    var fileName = getRealPathFromURI(filePath!!)
                    var fileName = getRealPathFromURI(filePath!!,mContext)
                    var path = filePath.toString()
                    var fileNameString =
                        fileName.substring(fileName.lastIndexOf("/") + 1).replace("%20", " ")

                    postPath = path.replace("file://", "").trim()
                    uploadFile()
                }

            } catch (e: java.lang.Exception) {
                Toast.makeText(mContext, "Please Select an Image", Toast.LENGTH_SHORT).show();
            }


        }

        return compressedImage!!
    }


//
//    private fun getRealPathFromURI(contentURI: Uri): String {
//        var result: String? = ""
//        try {
//            val cursor = contentResolver.query(contentURI, null, null, null, null)
////            val cursor =
//            if (cursor == null) {
//                result = contentURI.path
//            } else {
//                cursor.moveToFirst()
//                var idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//                if (idx == -1) {
//                    idx = idx + 2
//                    result = cursor.getString(idx)
//                } else {
//                    result = cursor.getString(idx)
//                }
//                cursor.close()
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//
//        return result!!
//    }










    private fun uploadFile() {
        if (postPath == null || postPath == "") {
            Toast.makeText(mContext, "please select an image ", Toast.LENGTH_LONG).show()
            return
        } else {
            var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

            val file = File(postPath!!)

            val mFile = RequestBody.create(MediaType.parse("*/*"), file)
            val mUserAssessmentYearId = RequestBody.create(
                MediaType.parse("text/plain"),
                newItrBase.selectedUser_userAssessmentYearUserID.toString()
            )
            val mFileType = RequestBody.create(MediaType.parse("text/plain"), setImageString)
            val mFilePassword = RequestBody.create(MediaType.parse("text/plain"), "")
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
                                successDialogue()
                            } else {
                                dialog.hideDialog()
                            }
                        } else {
                            dialog.hideDialog()
                            Toast.makeText(mContext, "problem uploading image", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    dialog.hideDialog()
//                    Log.v("Response gotten is", t.message)
                }
            })
        }
    }

    private fun successDialogue() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_confirm, null)
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setView(view)
        alertDialog.setCancelable(false)
        alertDialog.setIcon(R.drawable.logo)
        alertDialog.setTitle("Upload Successful.")
        var descTextView = view.findViewById<TextView>(R.id.descTextView)
        descTextView.text = "Your file uploaded successfully."
        alertDialog.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            if (ConnectionDetector().isConnectingToInternet(mContext))
                getDocumentList()
            else
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        })
        alertDialog.show()
    }

//    private fun setDataOnUI(){
//        if (response.body().getFormDocList != null) {
//
//            val getFormList = response.body().getFormDocList
//            for (i in 0 until getFormList!!.size) {
//                if (getFormList[i].fileType == "aadharcard1") {
//                    docVirtualUrlString = getFormList[i].documentVirtualURL?.takeUnless { it.isEmpty() } ?: ""
//                    docIdFrontString = getFormList[i].userUploadedDocumentId?.takeUnless { it.isEmpty() } ?: ""
//                    binding.frontUploadCard.visibility = View.GONE
//                    binding.frontImageLayout.visibility = View.VISIBLE
//                    var requestOptions:RequestOptions= RequestOptions().centerCrop()
//                    requestOptions.placeholder(R.drawable.progress_animation)
//                    Glide.with(mContext)
//                        .load(APIClient().BaseUrl + docVirtualUrlString)
////                                        .load("")
////                                        .load("")
//                        .apply(requestOptions)
//                        .into(binding.imgFrontAadhar)
//
//
//                }
//                else if (getFormList[i].fileType == "aadharcard2") {
//                    docVirtualUrlString = getFormList[i].documentVirtualURL?.takeUnless { it.isEmpty() } ?: ""
//                    docIdBackString = getFormList[i].userUploadedDocumentId?.takeUnless { it.isEmpty() } ?: ""
//                    binding.backUploadCard.visibility = View.GONE
//                    binding.backImageLayout.visibility = View.VISIBLE
//
////                                    Glide.with(mContext)
////                                        .load(APIClient().BaseUrl + docVirtualUrlString)
////                                        .into(binding.imgBackAadhar)
//
//                    var requestOptions:RequestOptions= RequestOptions().centerCrop()
//                    requestOptions.placeholder(R.drawable.progress_animation)
//                    Glide.with(mContext)
//                        .load(APIClient().BaseUrl + docVirtualUrlString)
////                                        .load("")
//                        .apply(requestOptions)
//                        .into(binding.imgBackAadhar)
//                }
//            }
//        } else {
//            binding.frontUploadCard.visibility = View.VISIBLE
//            binding.backUploadCard.visibility = View.VISIBLE
//            binding.frontImageLayout.visibility = View.GONE
//            binding.backImageLayout.visibility = View.GONE
//        }
//    }


    private fun getDocumentList() {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getAadhaarDocumentList(
            newItrBase.selectedUser_userAssessmentYearUserID.toString(),
            "aadharcard"
        )
        call.enqueue(object : Callback<NewItrBase> {
            override fun onResponse(call: Call<NewItrBase>?, response: Response<NewItrBase>?) {
                try {
                    dialog.hideDialog()
                    if (response!!.isSuccessful) {
                        if (response.body().getFormDocList != null) {
                            if(response.body().getFormDocList!!.size>0)
                            {
                                var fragmentCommunicator:FragmentCommunicator= (activity as FragmentCommunicator?)!!
                                fragmentCommunicator.requestPageLoad(1)

                            }

                            val getFormList = response.body().getFormDocList
                            for (i in 0 until getFormList!!.size) {
                                if (getFormList[i].fileType == "aadharcard1") {
                                    docVirtualUrlString = getFormList[i].documentVirtualURL?.takeUnless { it.isEmpty() } ?: ""
                                    docIdFrontString = getFormList[i].userUploadedDocumentId?.takeUnless { it.isEmpty() } ?: ""
                                    binding.frontUploadCard.visibility = View.GONE
                                    binding.frontImageLayout.visibility = View.VISIBLE
                                    var requestOptions:RequestOptions= RequestOptions().centerCrop()
                                    requestOptions.placeholder(R.drawable.progress_animation)
                                    Glide.with(mContext)
                                        .load(APIClient().BaseUrl + docVirtualUrlString)
//                                        .load("")
//                                        .load("")
                                        .apply(requestOptions)
                                        .into(binding.imgFrontAadhar)


//                                    GlideApp
//                                        .with(context)
//                                        .load(UsageExampleListViewAdapter.eatFoodyImages[0])
//                                        .placeholder(R.mipmap.ic_launcher) // can also be a drawable
//                                        .into(imageViewPlaceholder);

                                }
                                else if (getFormList[i].fileType == "aadharcard2") {
                                    docVirtualUrlString = getFormList[i].documentVirtualURL?.takeUnless { it.isEmpty() } ?: ""
                                    docIdBackString = getFormList[i].userUploadedDocumentId?.takeUnless { it.isEmpty() } ?: ""
                                    binding.backUploadCard.visibility = View.GONE
                                    binding.backImageLayout.visibility = View.VISIBLE

//                                    Glide.with(mContext)
//                                        .load(APIClient().BaseUrl + docVirtualUrlString)
//                                        .into(binding.imgBackAadhar)

                                    var requestOptions:RequestOptions= RequestOptions().centerCrop()
                                    requestOptions.placeholder(R.drawable.progress_animation)
                                    Glide.with(mContext)
                                        .load(APIClient().BaseUrl + docVirtualUrlString)
//                                        .load("")
                                        .apply(requestOptions)
                                        .into(binding.imgBackAadhar)
                                }
                            }
                        } else {
                            binding.frontUploadCard.visibility = View.VISIBLE
                            binding.backUploadCard.visibility = View.VISIBLE
                            binding.frontImageLayout.visibility = View.GONE
                            binding.backImageLayout.visibility = View.GONE
                        }
                    } else {
                        binding.frontUploadCard.visibility = View.VISIBLE
                        binding.backUploadCard.visibility = View.VISIBLE
                        binding.frontImageLayout.visibility = View.GONE
                        binding.backImageLayout.visibility = View.GONE
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
                binding.frontUploadCard.visibility = View.VISIBLE
                binding.backUploadCard.visibility = View.VISIBLE
                binding.frontImageLayout.visibility = View.GONE
                binding.backImageLayout.visibility = View.GONE
            }

        })
    }

    private fun deleteDialogue(id: String, selection: String) {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_confirm, null)
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setView(view)
        alertDialog.setCancelable(false)
        alertDialog.setIcon(R.drawable.logo)
        alertDialog.setTitle("Confirm Delete...")
        var descTextView = view.findViewById<TextView>(R.id.descTextView)
        descTextView.text = "Are you sure you want to delete this file?"
        alertDialog.setPositiveButton("YES", DialogInterface.OnClickListener { _, _ ->
            if (ConnectionDetector().isConnectingToInternet(mContext))
                delete(id, selection)
            else
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        })
        alertDialog.setNegativeButton("NO", DialogInterface.OnClickListener { _, _ ->
        })
        alertDialog.show()
    }

    private fun delete(id: String, selection: String) {
        var dialog = AlertDialogueManager(mContext, getString(R.string.dilog_pleasewait))

        val call = apI_Interface.deleteDocument(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    dialog.hideDialog()
                    if (response!!.isSuccessful) {
                        if (selection == "front") {
                            binding.frontUploadCard.visibility = View.VISIBLE
                            binding.frontImageLayout.visibility = View.GONE
                        } else {
                            binding.backUploadCard.visibility = View.VISIBLE
                            binding.backImageLayout.visibility = View.GONE
                        }
                    }
                } catch (e: java.lang.Exception) {
                    dialog.hideDialog()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                dialog.hideDialog()
            }

        })
    }
}