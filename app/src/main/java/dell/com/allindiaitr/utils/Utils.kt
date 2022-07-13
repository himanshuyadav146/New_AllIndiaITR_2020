package dell.com.allindiaitr.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min


fun getFile(actualImage:File,mContext:Context):File{

    var compressedImage:File?=null

    if(actualImage==null){
        Toast.makeText(mContext, "Please Select an Image", Toast.LENGTH_SHORT).show();
    }else{

         compressedImage = Compressor(mContext).compressToFile(actualImage)
        var compressedImage =Compressor(mContext)
            .setMaxWidth(1000)
            .setMaxHeight(1000)
            .setQuality(100)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
            .compressToFile(actualImage)

    }
    return compressedImage!!;
}



 fun getRealPathFromURI(contentURI: Uri,mContext: Context): String {
    var result: String? = ""
    try {
        val cursor = mContext.contentResolver.query(contentURI, null, null, null, null)
//            val cursor =
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


 fun getRealPathFromURINew(uri: Uri,mContext: Context): String? {
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


 fun checkAndRequestPermissions(mContext: Context): Boolean {
     var PERMISSIONS_REQUEST_CODE = 1024
     var appPermissions = arrayOf(
         Manifest.permission.WRITE_EXTERNAL_STORAGE,
         Manifest.permission.READ_EXTERNAL_STORAGE,
         Manifest.permission.CAMERA
     )
    var listPermissionsNeeded = arrayListOf<String>()
    for (i in 0 until appPermissions.size) {
        if (ContextCompat.checkSelfPermission(
                mContext,
                appPermissions[i].toString()
            ) != PackageManager.PERMISSION_GRANTED
        )
            listPermissionsNeeded.add(appPermissions[i].toString())
    }

    if (listPermissionsNeeded.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            mContext as Activity,
            listPermissionsNeeded.toArray(arrayOfNulls(listPermissionsNeeded.size)),
            PERMISSIONS_REQUEST_CODE
        )
        return false
    }

    return true
}


 fun getImageUri(inImage: Bitmap,mContext: Context): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        mContext.contentResolver,
        inImage,
        "Title" + System.currentTimeMillis(),
        null
    )
    return if (!TextUtils.isEmpty(path)) {
        try {
            Uri.parse(path)
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}

 fun makeFileCopyInCacheDir(contentUri: Uri,mContext: Context): String? {
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
            return file.absolutePath
        }
    } catch (ex: Exception) {
//        Logger.e("Exception", ex.message!!)
    }
    return contentUri.let { getRealPathFromURINew(it,mContext).toString() }
}