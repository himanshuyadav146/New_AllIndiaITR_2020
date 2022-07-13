package dell.com.allindiaitr.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class SelectNumberDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Context context;
    TextView textView;
    private int age=18;

    @SuppressLint("ValidFragment")
    public SelectNumberDateFragment(Context context, TextView textView, int age) {
        this.textView = textView;
        this.context = context;
        this.age=age;
    }

    public SelectNumberDateFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,this, yy-age, mm, dd);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        return dialog;
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {

        int month = mm + 1;
        String formattedMonth = "" + month;
        String formattedDay = "" + dd;

        if (month < 10){
            formattedMonth = "0" + month;
        }
        if (dd < 10){
            formattedDay = "0" + dd;
        }

        textView.setText(formattedDay + "-" + formattedMonth + "-" + String.valueOf(yy));
    }
}