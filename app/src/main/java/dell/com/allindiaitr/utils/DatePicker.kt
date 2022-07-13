package dell.com.allindiaitr.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ValidFragment")
class DatePicker(mContext: Context, textView: TextView, age: Int): DialogFragment() {

    var cal = Calendar.getInstance()
    var mContext = mContext
    var age:Int=age


    private val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView(textView)
    }

    private fun updateDateInView(textView: TextView) {
        val myFormat = "dd-MM-yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textView.text = sdf.format(cal.time)
    }

    fun datePickerDialog() {
//        var dialog = DatePickerDialog(mContext,
//            dateSetListener,
//            // set DatePickerDialog to point to today's date when it loads up
//            cal.get(Calendar.YEAR),
//            cal.get(Calendar.MONTH),
//            cal.get(Calendar.DAY_OF_MONTH))
//        dialog.datePicker.maxDate = cal.timeInMillis
//        dialog.show()
        var dialog1 =DatePickerDialog(mContext,
//            android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR)-age,
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        dialog1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog1.show()

    }
}