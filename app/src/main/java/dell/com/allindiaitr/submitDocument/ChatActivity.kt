package dell.com.allindiaitr.submitDocument
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.ChatArrayAdapter
import dell.com.allindiaitr.databinding.ActivityChatBinding
import dell.com.allindiaitr.interfaces.API_Interface
import dell.com.allindiaitr.models.Chat
import dell.com.allindiaitr.models.NewItrBase
import dell.com.allindiaitr.utils.*
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

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var apI_Interface : API_Interface
    lateinit var mContext: Context
    var newItrBase = NewItrBase.instance
    var CommentTypeArrayList = arrayListOf<String>()
    var CommentArrayList = arrayListOf<String>()
    var CreatedByArrayList = arrayListOf<String>()
    var CreatedDateArrayList = arrayListOf<String>()
    var chatArrayAdapter: ChatArrayAdapter? = null
    var PERMISSIONS_REQUEST_CODE = 1024
    var appPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    var GALLERY = 1
    var CAMERA = 2
    var postPath: String? = null
    var password = ""
    var chat = Chat.instance
    lateinit var brodcostIntent:Intent
    private var appPreferences: AppPreferences? = null
    private lateinit var binding:ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this
        apI_Interface = APIClient().getClient().create(API_Interface::class.java)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = "Chat with Expert"

        checkAndRequestPermissions()

        AlertDialogueManager().targetView(this, binding.cameraImage, "Upload any other document from here.", "")

        if (ConnectionDetector().isConnectingToInternet(mContext)){
            getChat()
            newItrBase.setIsChatOpen(true)
        }
        else{
            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        }


        binding.sendImageView.setOnClickListener(this)
        binding.cameraImage.setOnClickListener(this)
    }


    override fun onPause() {
        super.onPause()
        newItrBase.setIsChatOpen(false)
    }

    override fun onRestart() {
        super.onRestart()
        newItrBase.setIsChatOpen(true)
    }

    override fun onStop() {
        super.onStop()
        newItrBase.setIsChatOpen(false)
    }


    private fun checkAndRequestPermissions(): Boolean{
        val listPermissionsNeeded = arrayListOf<String>()
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

    override fun attachBaseContext(newBase:Context?){
        mContext= newBase!!
        appPreferences= AppPreferences(newBase!!)
        val lang:String=appPreferences?.selectedLanguage!!
        //  setAppLocal(lang)
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))

    }


    private fun getChat(){
        var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        val call = apI_Interface.getChat(newItrBase.selectedUser_userAssessmentYearUserID.toString())
        call.enqueue(object : Callback<List<Chat>> {

            override fun onResponse(call: Call<List<Chat>>?, response: Response<List<Chat>>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()

                        if (response.body() != null) {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.noOrder.visibility = View.GONE
                            for (i in 0 until response.body().size) {

                                CommentTypeArrayList.add(response.body()[i].commentType.toString().takeUnless {it.isEmpty()} ?: "")
                                CommentArrayList.add(response.body()[i].comment.toString().takeUnless {it.isEmpty()} ?: "")
                                CreatedByArrayList.add(response.body()[i].createdBy.toString().takeUnless {it.isEmpty()} ?: "")
                                CreatedDateArrayList.add(response.body()[i].createdDate.toString().takeUnless {it.isEmpty()} ?: "")
                            }

                            binding.recyclerView.setHasFixedSize(true)
                            val manager = LinearLayoutManager(mContext)
                            binding.recyclerView.layoutManager = manager
                            chatArrayAdapter = ChatArrayAdapter(
                                mContext,
                                CommentTypeArrayList,
                                CommentArrayList,
                                CreatedByArrayList,
                                CreatedDateArrayList
                            )
                            binding.recyclerView.adapter = chatArrayAdapter

                            val newMsgPosition = CommentTypeArrayList.size - 1
                            chatArrayAdapter!!.notifyItemInserted(newMsgPosition)
                            binding.recyclerView.scrollToPosition(newMsgPosition)
                        } else {
                            dialog.hideDialog()
                            binding.recyclerView.visibility = View.GONE
                            binding.noOrder.visibility = View.VISIBLE
                            Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        dialog.hideDialog()
                    }
                }
                catch(e: Exception) {
                    dialog.hideDialog()
                    binding.recyclerView.visibility = View.GONE
                    binding.noOrder.visibility = View.VISIBLE
                    Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Chat>>?, t: Throwable?) {
                dialog.hideDialog()
                binding.recyclerView.visibility = View.GONE
                binding.noOrder.visibility = View.VISIBLE
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            getChat()
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.sendImageView->{
                if (binding.commentEditext.text.isNotEmpty()) {
                    if (ConnectionDetector().isConnectingToInternet(mContext))
                        postChat()
                    else
                        Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.cameraImage->{
                val items = arrayOf("Select photo from gallery", "Capture photo from camera")
                AlertDialog.Builder(this)
                    .setTitle("Select Action")
                    .setItems(items, DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            0 -> {
                                val galleryIntent = Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                startActivityForResult(galleryIntent, GALLERY)
                            }
                            1 -> {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }

                    }).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY)
            {
                if (data != null)
                {
                    val contentURI = data.data
                    try
                    {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        postPath = saveImage(bitmap)
                        uploadFile()
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            else if (requestCode == CAMERA)
            {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                postPath = saveImage(thumbnail)
                uploadFile()
            }
        }
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

    private fun uploadFile() {
        if (postPath == null || postPath == "") {
            Toast.makeText(this, "please select an image ", Toast.LENGTH_LONG).show()
            return
        } else {
            var dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

            val file = File(postPath!!)

            val mFile = RequestBody.create(MediaType.parse("*/*"), file)
            val mUserAssessmentYearId = RequestBody.create(MediaType.parse("text/plain"), newItrBase.selectedUser_userAssessmentYearUserID.toString())
            val mFileType= RequestBody.create(MediaType.parse("text/plain"), "AnyOtherDocument")
            val mFilePassword= RequestBody.create(MediaType.parse("text/plain"), password)
            val body = MultipartBody.Part.createFormData("file", file.name, mFile)

            val call = apI_Interface.uploadFile(body, mUserAssessmentYearId, mFileType, mFilePassword)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                dialog.hideDialog()
                                binding.commentEditext.setText(postPath)
                                binding.commentEditext.isEnabled = false
                                if (ConnectionDetector().isConnectingToInternet(mContext))
                                    postChat()
                                else
                                    Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                dialog.hideDialog()
                            }
                        } else {
                            dialog.hideDialog()
                            Toast.makeText(applicationContext, "problem uploading image", Toast.LENGTH_SHORT).show()
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

    private fun postChat(){
        val dialog = AlertDialogueManager(mContext,getString(R.string.dilog_pleasewait))

        chat.userAssessmentYearId = newItrBase.selectedUser_userAssessmentYearUserID
        chat.comment = binding.commentEditext.text.toString()
        chat.commentType = "1"

        val call = apI_Interface.postChat(chat)
        call.enqueue(object : Callback<Void>{

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                try {
                    if (response!!.isSuccessful) {
                        dialog.hideDialog()
                        binding.commentEditext.text.clear()
                        CommentTypeArrayList.clear()
                        CommentArrayList.clear()
                        CreatedByArrayList.clear()
                        CreatedDateArrayList.clear()
                        if (ConnectionDetector().isConnectingToInternet(mContext))
                            getChat()
                        else
                            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
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

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                dialog.hideDialog()
                Toast.makeText(mContext, resources.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        binding.commentEditext.clearFocus()
        //        bottom_navigation.setSelectedItemId(R.id.navigation_chat);
        mContext.registerReceiver(mMessageReceiver, IntentFilter("unique_name"))
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
