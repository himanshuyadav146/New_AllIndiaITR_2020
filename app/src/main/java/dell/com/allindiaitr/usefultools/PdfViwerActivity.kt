package dell.com.allindiaitr.usefultools

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityPdfViwerBinding
import java.io.File

class PdfViwerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViwerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViwerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var time=intent.getStringExtra("time")
        var directory=intent.getStringExtra("directory")

        val pdfFile = File(Environment.getExternalStorageDirectory().toString() + "/" + directory + "/" + time)

            binding.webview.loadUrl("http://www.africau.edu/images/default/sample.pdf")
////        val path = Uri.fromFile(pdfFile)
//       // var path = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);
////
////        pdfView.fromFile(pdfFile).enableSwipe(true).swipeHorizontal(true)
////            .enableDoubletap(true)
////            .defaultPage(0)
////            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
////            .password(null)
////            .scrollHandle(null)
////            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
////            // spacing between pages in dp. To define spacing color, set view background
////            .spacing(0)
////            .pageFitPolicy(FitPolicy.WIDTH)
////            .load()
//
//
//        pdfView.fromUri(Uri.fromFile(pdfFile))
//            .enableSwipe(true).swipeHorizontal(true)
//            .enableDoubletap(true)
//            .defaultPage(0)
//            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//            .password(null)
//            .scrollHandle(null)
//            .enableDoubletap(true)
//            .onDraw{canvas, pageWidth, pageHeight, displayedPage ->  }
//            .onDrawAll{canvas, pageWidth, pageHeight, displayedPage ->  }
//            .onPageChange{page, pageCount ->  }
//            .onPageError{page, t ->
//
//                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
//            }
//            .load()


//            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//            // spacing between pages in dp. To define spacing color, set view background
//            .spacing(0)
//            .pageFitPolicy(FitPolicy.WIDTH)
//            .load()
    }
}
