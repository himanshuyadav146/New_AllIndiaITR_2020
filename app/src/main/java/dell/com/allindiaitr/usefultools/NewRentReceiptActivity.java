package dell.com.allindiaitr.usefultools;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import dell.com.allindiaitr.R;
import dell.com.allindiaitr.interfaces.NewAPIClient;
import dell.com.allindiaitr.interfaces.NewAPIInterface;
import dell.com.allindiaitr.models.HraRentReceiptParams;
import dell.com.allindiaitr.utils.AppPreferences;
import dell.com.allindiaitr.utils.ButtonVisibilityClass;
import dell.com.allindiaitr.utils.ConnectionDetector;
import dell.com.allindiaitr.utils.FilePath;
import dell.com.allindiaitr.utils.MyContextWrapper;
import dell.com.allindiaitr.utils.NewConnectionDetector;
import dell.com.allindiaitr.utils.SelectNumberDateFragment;
import retrofit2.Call;
import retrofit2.Callback;

public class NewRentReceiptActivity extends AppCompatActivity implements View.OnClickListener {

    Context mContext;
    private Toolbar toolbar;
    private EditText landlordNameEditText, emailIdEditText, totalRentReceivedEditText, tenantNameEditText, landlordPanEditText, addressLineEditText,
            townCityEditText, stateEditText, pinCodeEditText;
    private TextView fromDateTextView, toDateTextView;
    private Button printButton;
    private String landlordNameString, emailIdString, totalRentReceivedString, fromDateString, toDateString, tenantNameString, landlordPanString,
            addressLineString, townCityString, stateString, pinCodeString;
    DialogFragment dialogFragment;
    Date toDate = null, fromDate = null;
    SimpleDateFormat sdf;
    Calendar calendar;
    Document document;
    ArrayList<String> receiptNoArrayList = new ArrayList<String>();
    ArrayList<String> ownerNameArrayList = new ArrayList<String>();
    ArrayList<String> addressLineArrayList = new ArrayList<String>();
    ArrayList<String> rentReceivedArrayList = new ArrayList<String>();
    ArrayList<String> fromDateArrayList = new ArrayList<String>();
    ArrayList<String> toDateArrayList = new ArrayList<String>();
    ArrayList<String> tenantNameArrayList = new ArrayList<String>();
    ArrayList<String> landLordPanArrayList = new ArrayList<String>();
    ArrayList<String> monthArrayList = new ArrayList<String>();
    ArrayList<String> yearArrayList = new ArrayList<String>();
    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final int STORAGE_PERMISSION_WRITECODE = 124;

    int receiptCount = 1;
    String newToDateString;
    String patternString = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String panNumberRegex = "^[A-Z]{5}[0-9]{4}[A-Z]$";
    String currentDateTimeString;
    //http://180.151.86.92:83/
    //public static final String UPLOAD_URL = "http://192.168.1.234/api/supportMessage/SendEmail?Email=";
    public static final String UPLOAD_URL = "https://www.allindiaitr.com/"+"api/supportMessage/SendEmail?Email=";

    private int  statePositionInt;
    public Spinner stateSpinner;
    ArrayList<String> stateIdArrayList = new ArrayList<String>();
    ArrayList<String> stateNameArrayList = new ArrayList<String>();

    ButtonVisibilityClass buttonVisibilityClass = new ButtonVisibilityClass();
    View mainRootView;

    ProgressDialog progressDialog;
    String address;
    int lastMonthRent = 0;
    private String TAG="Permission";
    private String Permission = "";

    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rent_receipt);
        mContext = this;
        appPreferences=new AppPreferences(mContext);
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()).replace(",","").replace(" ","").replace(":","");
//        AnalyticsApplication application = (AnalyticsApplication) getApplicationContext();
//        mTracker = application.getDefaultTracker();
//        mTracker.setScreenName("Rent Receipt Screen");
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        progressDialog = new ProgressDialog(mContext);
//        requestStoragePermission();
//        isReadStoragePermissionGranted();
//        isWriteStoragePermissionGranted();
        askPermission();


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        appPreferences=new AppPreferences(mContext);
        setLanguage(appPreferences.getSelectedLanguage());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("Rent Receipt Form");

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            Log.i("RentReceipt", "Deep link clicked " + uri);
        }

        landlordNameEditText = (EditText) findViewById(R.id.landlordNameEditText);
        emailIdEditText = (EditText) findViewById(R.id.emailIdEditText);
        totalRentReceivedEditText = (EditText) findViewById(R.id.totalRentReceivedEditText);
        tenantNameEditText = (EditText) findViewById(R.id.tenantNameEditText);
        landlordPanEditText = (EditText) findViewById(R.id.landlordPanEditText);
        addressLineEditText = (EditText) findViewById(R.id.addressLineEditText);
        townCityEditText = (EditText) findViewById(R.id.townCityEditText);
        stateSpinner = (Spinner) findViewById(R.id.stateEditText);
        pinCodeEditText = (EditText) findViewById(R.id.pinCodeEditText);

        fromDateTextView = (TextView) findViewById(R.id.fromDateTextView);
        toDateTextView = (TextView) findViewById(R.id.toDateTextView);
        printButton = (Button) findViewById(R.id.printButton);

        printButton.setOnClickListener(this);
        fromDateTextView.setOnClickListener(this);
        toDateTextView.setOnClickListener(this);

        getStateService();
        mainRootView = findViewById(R.id.activity_rent_receipt);
        buttonVisibilityClass.hideButton(mContext, mainRootView, printButton);
    }





//    override fun attachBaseContext(newBase:Context?){
//        mContext= newBase!!
//                appPreferences= AppPreferences(newBase!!)
//        val lang:String=appPreferences?.selectedLanguage!!
//                //  setAppLocal(lang)
//                super.attachBaseContext(MyContextWrapper.wrap(newBase,lang))
//
//    }

    @Override
    protected void attachBaseContext(Context base) {
        mContext=base;
        appPreferences=new AppPreferences(mContext);
        String lang=appPreferences.getSelectedLanguage();
        super.attachBaseContext(MyContextWrapper.Companion.wrap(base,lang));
//        sContext = base;
    }





    private void setLanguage(String language){

        //setting new configuration
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getApplicationContext().getResources().updateConfiguration(config, null);

        //store current language in prefrence
//        prefData.setCurrentLanguage(language);
        appPreferences.setSelectedLanguage(language);

        //With new configuration start activity again
//        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//        startActivity(intent);
//        finish();

    }

    public String getStateService() {
        String json = null;
        try {
//            InputStream is = UserListActivity.this.getAssets().open("my_json.json");
            InputStream is = getResources().openRawResource(R.raw.state);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            stateNameArrayList.add(0, "Select State");
            stateIdArrayList.add(0,"0");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                stateNameArrayList.add(jsonObject.getString("State"));
                stateIdArrayList.add(jsonObject.getString("Id"));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, stateNameArrayList);
                stateSpinner.setAdapter(adapter);
                // stateSpinner.setPrompt("Select Country");


            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        }
        return json;
    }

    private boolean askPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Permission = "Granted";
                        } else {
                            Permission = "Denyde";
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
        return true;
    }




    public  void isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_WRITECODE);
                return;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return;
        }
    }



    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
//                Toast.makeText(this, "Permission granted, now you can read the storage", Toast.LENGTH_SHORT).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        lastMonthRent = 0;
        switch (view.getId()) {
            case R.id.printButton:
                landlordNameString = landlordNameEditText.getText().toString().trim();
                emailIdString = emailIdEditText.getText().toString().trim();
                totalRentReceivedString = totalRentReceivedEditText.getText().toString().trim();
                fromDateString = fromDateTextView.getText().toString().trim();
                toDateString = toDateTextView.getText().toString().trim();
                tenantNameString = tenantNameEditText.getText().toString().trim();
                landlordPanString = landlordPanEditText.getText().toString().trim().toUpperCase();
                addressLineString = addressLineEditText.getText().toString().trim();
                townCityString = townCityEditText.getText().toString().trim();
//                stateString = stateEditText.getText().toString().trim();
                pinCodeString = pinCodeEditText.getText().toString().trim();

                receiptNoArrayList.clear();
                ownerNameArrayList.clear();
                addressLineArrayList.clear();
                rentReceivedArrayList.clear();
                fromDateArrayList.clear();
                toDateArrayList.clear();
                tenantNameArrayList.clear();
                landLordPanArrayList.clear();
                receiptCount = 1;

                if (checkEmpty()) {
                    checkDate();
                    if (toDateArrayList.size() > 0) {
                        toDateArrayList.remove(toDateArrayList.size() - 1);
                        toDateArrayList.add(toDateString);
                        Log.v("toDate ", toDateArrayList + "");
                        try {
                            Date date1 = sdf.parse(fromDateArrayList.get(fromDateArrayList.size() - 1));
                            Date date2 = sdf.parse(toDateArrayList.get(toDateArrayList.size() - 1));
                            long diff = date2.getTime() - date1.getTime();
                            int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            lastMonthRent = (Integer.parseInt(totalRentReceivedString) / 30) * days;
                            Log.v("Days = ", days + "");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        rentReceivedArrayList.remove(rentReceivedArrayList.size() - 1);
                        rentReceivedArrayList.add(String.valueOf(lastMonthRent));
                        Log.v("rentReceived", rentReceivedArrayList + "");
                        //newPdf();
                        statePositionInt = stateSpinner.getSelectedItemPosition();
                        stateString = stateNameArrayList.get(statePositionInt);

                        address = addressLineString + ", " + townCityString + ", " + stateString + ", " + pinCodeString;
                        pdf();
                    }
                }
                break;

            case R.id.fromDateTextView:
                dialogFragment = new SelectNumberDateFragment(mContext, fromDateTextView,0);
                dialogFragment.show(getFragmentManager(), "DatePicker");

//                DatePicker(mContext, fromDateTextView, 0).datePickerDialog();
                break;

            case R.id.toDateTextView:
                dialogFragment = new SelectNumberDateFragment(mContext, toDateTextView,0);
                dialogFragment.show(getFragmentManager(), "DatePicker");
                break;

            default:
                break;
        }
    }

    private boolean checkEmpty() {
        try
        {
            sdf = new SimpleDateFormat("dd-MM-yyyy");
            toDate = sdf.parse(toDateString);
            fromDate = sdf.parse(fromDateString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(tenantNameString)) {
            tenantNameEditText.setError("Enter Your Name");
            tenantNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(emailIdString)) {
            emailIdEditText.requestFocus();
            emailIdEditText.setError("Enter Your Email Id");
        } else if (!emailIdString.matches(patternString)) {
            emailIdEditText.requestFocus();
            emailIdEditText.setError("Enter Your Valid Email Id");
        } else if (TextUtils.isEmpty(landlordNameString)) {
            landlordNameEditText.setError("Enter Landlord Name");
            landlordNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(totalRentReceivedString)) {
            totalRentReceivedEditText.setError("Enter Rent Paid Per Month");
            totalRentReceivedEditText.requestFocus();
        } else if (TextUtils.isEmpty(fromDateString)) {
            Toast.makeText(mContext, "Enter Rent Duration", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(toDateString)) {
            Toast.makeText(mContext, "Enter Rent Duration", Toast.LENGTH_SHORT).show();
        } else if (toDate.equals(fromDate)) {
            Toast.makeText(mContext, "Enter Correct Rent Duration", Toast.LENGTH_SHORT).show();
        } else if (toDate.before(fromDate)) {
            Toast.makeText(mContext, "Enter Correct Rent Duration", Toast.LENGTH_SHORT).show();
        } else if ((TextUtils.isEmpty(landlordPanString)) && Integer.valueOf(totalRentReceivedString) > 8333) {
            landlordPanEditText.setError("Enter Your Landlord PAN");
            landlordPanEditText.requestFocus();
        } else if ((!landlordPanString.matches(panNumberRegex)) && Integer.valueOf(totalRentReceivedString) > 8333) {
            landlordPanEditText.setError("Enter Valid PAN Number");
            landlordPanEditText.requestFocus();
        } else if (TextUtils.isEmpty(addressLineString)) {
            addressLineEditText.setError("Enter Your Address Line");
            addressLineEditText.requestFocus();
        } else if (TextUtils.isEmpty(townCityString)) {
            townCityEditText.setError("Enter Your Town/City");
            townCityEditText.requestFocus();
        }
        else if (townCityString.contains("\\d+")){
            townCityEditText.requestFocus();
            townCityEditText.setError("Enter correct Town/City");
        }
        else if (stateSpinner.getSelectedItem().toString().trim().equals("Select State")){
            Toast.makeText(mContext, "Please Select a State", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pinCodeString) || pinCodeString.length() != 6) {
            pinCodeEditText.setError("Enter Your Valid Pincode");
            pinCodeEditText.requestFocus();
        } else {
            tenantNameEditText.clearFocus();
            emailIdEditText.clearFocus();
            landlordNameEditText.clearFocus();
            totalRentReceivedEditText.clearFocus();
            landlordPanEditText.clearFocus();
            addressLineEditText.clearFocus();
            townCityEditText.clearFocus();
            pinCodeEditText.clearFocus();
            return true;
        }
        return false;
    }

    private void checkDate() {
        try {
            sdf = new SimpleDateFormat("dd-MM-yyyy");
            toDate = sdf.parse(toDateString);
            fromDate = sdf.parse(fromDateString);
            if (fromDate.before(toDate)) {

                receiptNoArrayList.add(String.valueOf(receiptCount++));
                ownerNameArrayList.add(landlordNameString);
                addressLineArrayList.add(addressLineString);
                tenantNameArrayList.add(tenantNameString);
                fromDateArrayList.add(fromDateString);
                rentReceivedArrayList.add(totalRentReceivedString);

                if (landlordPanString.equals("")) {
                    landLordPanArrayList.add("N/A");
                } else {
                    landLordPanArrayList.add(landlordPanString);
                }
                Log.v("receiptNo", receiptNoArrayList + "");
                Log.v("owner name", ownerNameArrayList + "");
                Log.v("address", addressLineArrayList + "");
                Log.v("tenant name", tenantNameArrayList + "");
                calendar = Calendar.getInstance();
                calendar.setTime(sdf.parse(fromDateString));
                monthArrayList.add(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
                yearArrayList.add(String.valueOf(calendar.get(Calendar.YEAR)));
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                newToDateString = sdf.format(calendar.getTime());

                toDateArrayList.add(newToDateString);
                calendar.add(Calendar.DATE, 1);
                fromDateString = sdf.format(calendar.getTime());
                sdf = new SimpleDateFormat("dd-MM-yyyy");
                fromDate = sdf.parse(fromDateString);
                Log.v("fromDate ", fromDateArrayList + "");
                Log.v("toDate ", toDateArrayList + "");
                checkDate();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void viewPdf(String file, String directory) {
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(pdfIntent);
            if (NewConnectionDetector.isConnectingToInternet(mContext)) {
                postHraRentReceipt();
                mailRentRecipt(path);
//                Toast.makeText(mContext,"Here are your rent receipts\n(We have also emailed them to you )",Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext,"Your rent receipts have been generated\nKindly check " +pdfFile.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(mContext, "Can not read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    private void mailRentRecipt(Uri pdfFile) {
        String path = FilePath.getPath(this, pdfFile);
        if (path == null) {
            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_SHORT).show();
        } else {
            try {
                String uploadId = UUID.randomUUID().toString();
                new MultipartUploadRequest(this, uploadId, UPLOAD_URL + emailIdString)
                        .addFileToUpload(path, "file") //Adding file
                        // .setNotificationConfig(new UploadNotificationConfig())
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(Context context, UploadInfo uploadInfo) {

                            }

                            @Override
                            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                                Log.v("error", exception + "");
                            }
                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {

                            }
                        })
                        .setMaxRetries(2)
                        .startUpload();
            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pdf() {
        document = new Document(PageSize.A4);
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Receipt";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir,currentDateTimeString+ "rentRecipt.pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(document, fOut);

            document.open();
            for (int i = 0; i < receiptNoArrayList.size(); i++) {

                PdfPTable table = null;
                table = new PdfPTable(2);
                table.setWidthPercentage(105);
                table.setWidths(new float[] { 0.4f, 0.6f });

                Drawable d = getResources().getDrawable(R.drawable.pdf_logo);
                BitmapDrawable bitDw = ((BitmapDrawable) d);
                Bitmap bmp = bitDw.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
                Image image1 = Image.getInstance(stream.toByteArray());
                image1.scaleAbsolute(250,80);

                PdfPCell title = new PdfPCell(new Phrase("Rent Receipt", FontFactory.getFont("Roboto",16, Font.BOLD, WebColors.getRGBColor("#00B894"))));
                title.setPaddingLeft(20);
                title.setPaddingTop(20);
                title.setPaddingBottom(20);
                title.setBorderWidthBottom(1);
                title.setBorderColorBottom(WebColors.getRGBColor("#EFEFEF"));
                title.setBorderWidth(0);
                title.setHorizontalAlignment(0);
                table.addCell(title);

                PdfPCell month = new PdfPCell(new Phrase(monthArrayList.get(i).toString()+" "+yearArrayList.get(i).toString(), FontFactory.getFont("Roboto",16, Font.BOLD, WebColors.getRGBColor("#505050"))));
                month.setPaddingRight(20);
                month.setPaddingTop(20);
                month.setPaddingBottom(20);
                month.setBorderWidthBottom(1);
                month.setBorderColorBottom(WebColors.getRGBColor("#EFEFEF"));
                month.setBorderWidth(0);
                month.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(month);

                PdfPCell receiptNo = new PdfPCell(new Phrase("Receipt No.: " + receiptNoArrayList.get(i).toString(), FontFactory.getFont("Roboto",10, Font.NORMAL, WebColors.getRGBColor("#505050"))));
                receiptNo.setHorizontalAlignment(Element.ALIGN_RIGHT);
                receiptNo.setPaddingRight(20);
                receiptNo.setPaddingTop(10);
                receiptNo.setBorderWidthLeft(0);
                receiptNo.setBorderWidthTop(0);
                receiptNo.setBorderWidthBottom(0);
                receiptNo.setBorderWidth(0);
                receiptNo.setColspan(2);
                table.addCell(receiptNo);

                PdfPCell date = new PdfPCell(new Phrase("Date: " + toDateArrayList.get(i), FontFactory.getFont("Roboto",10, Font.NORMAL, WebColors.getRGBColor("#505050"))));
                date.setHorizontalAlignment(Element.ALIGN_RIGHT);
                date.setPaddingRight(20);
                date.setPaddingBottom(5);
                date.setBorderWidthLeft(0);
                date.setBorderWidthTop(0);
                date.setBorderWidthBottom(0);
                date.setBorderWidth(0);
                date.setColspan(2);
                table.addCell(date);

                Paragraph descriptionParagraph = new Paragraph("Received sum of INR ", FontFactory.getFont("Roboto",12, Font.NORMAL, WebColors.getRGBColor("#505050")));
                descriptionParagraph.add(new Chunk("Rs. " + rentReceivedArrayList.get(i).toString(), FontFactory.getFont("Roboto",12, Font.BOLD, WebColors.getRGBColor("#505050"))));
                descriptionParagraph.add(new Chunk(" from ", FontFactory.getFont("Roboto",12, Font.NORMAL, WebColors.getRGBColor("#505050"))));
                descriptionParagraph.add(new Chunk(tenantNameArrayList.get(i).toString(), FontFactory.getFont("Roboto",12, Font.BOLD, WebColors.getRGBColor("#505050"))));
                descriptionParagraph.add(new Chunk(" towards the rent of property located at ", FontFactory.getFont("Roboto",12, Font.NORMAL, WebColors.getRGBColor("#505050"))));
                descriptionParagraph.add(new Chunk(address , FontFactory.getFont("Roboto",12, Font.BOLD, WebColors.getRGBColor("#505050"))));
                descriptionParagraph.add(new Chunk(" for the period from ", FontFactory.getFont("Roboto",12, Font.NORMAL, WebColors.getRGBColor("#505050"))));
                descriptionParagraph.add(new Chunk(fromDateArrayList.get(i).toString(), FontFactory.getFont("Roboto",12, Font.BOLD, WebColors.getRGBColor("#505050"))));
                descriptionParagraph.add(new Chunk(" to ", FontFactory.getFont("Roboto",12, Font.NORMAL, WebColors.getRGBColor("#505050"))));
                descriptionParagraph.add(new Chunk(toDateArrayList.get(i).toString(), FontFactory.getFont("Roboto",12, Font.BOLD, WebColors.getRGBColor("#505050"))));

                PdfPCell para = new PdfPCell(descriptionParagraph);
                para.setPaddingLeft(20);
                para.setPaddingTop(30);
                para.setPaddingRight(20);
                para.setPaddingBottom(10);
                para.setBorderWidthBottom(0);
                para.setColspan(2);
                para.setBorderWidth(0);
                para.setHorizontalAlignment(Element.ALIGN_LEFT);
                para.setLeading(0,1.2f);
                table.addCell(para);

                Paragraph landlordNamePara = new Paragraph("Landlord Name: ", FontFactory.getFont("Roboto",12, Font.NORMAL, WebColors.getRGBColor("#505050")));
                landlordNamePara.add(new Chunk(ownerNameArrayList.get(i).toString(), FontFactory.getFont("Roboto",12, Font.BOLD, WebColors.getRGBColor("#505050"))));

                PdfPCell landlordName = new PdfPCell(landlordNamePara);
                landlordName.setPaddingRight(20);
                landlordName.setPaddingLeft(20);
                landlordName.setPaddingTop(10);
                landlordName.setColspan(2);
                landlordName.setBorderWidthBottom(0);
                landlordName.setBorderWidthTop(0);
                landlordName.setBorderWidth(0);
                landlordName.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(landlordName);

                Paragraph landlordPanPara = new Paragraph("Landlord PAN: ", FontFactory.getFont("Roboto",12, Font.NORMAL, WebColors.getRGBColor("#505050")));
                landlordPanPara.add(new Chunk(landLordPanArrayList.get(i).toString(), FontFactory.getFont("Roboto",12, Font.BOLD, WebColors.getRGBColor("#505050"))));

                PdfPCell landlordPan = new PdfPCell(landlordPanPara);
                landlordPan.setPaddingRight(20);
                landlordPan.setPaddingLeft(20);
                landlordPan.setPaddingBottom(10);
                landlordPan.setColspan(2);
                landlordPan.setHorizontalAlignment(Element.ALIGN_LEFT);
                landlordPan.setBorderWidth(0);
                table.addCell(landlordPan);

                PdfPCell note = new PdfPCell(new Phrase("Note: This slip is electronically generated by allindiaitr.com based on the information provided by the user.", FontFactory.getFont("Roboto",10, Font.NORMAL, WebColors.getRGBColor("#505050"))));
                note.setPaddingTop(20);
                note.setPaddingLeft(20);
                note.setPaddingRight(20);
                note.setColspan(2);
                note.setBorderWidthTop(1);
                note.setBorderWidth(0);
                note.setBorderColorBottom(WebColors.getRGBColor("#EFEFEF"));
                note.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(note);
                document.add(table);

                Rectangle rect = new Rectangle(25, 1160, 815, 640);
                rect.setBorder(Rectangle.BOX);
                rect.setBorderWidth(2);
                rect.setBorderColor(BaseColor.BLACK);
                document.newPage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        viewPdf(currentDateTimeString+"rentRecipt.pdf", "Receipt");
    }

    private void postHraRentReceipt() {

        progressDialog.setMessage(getString(R.string.dilog_pleasewait));
        progressDialog.show();
        progressDialog.setCancelable(false);

        String months = String.valueOf(receiptNoArrayList.size());

        NewAPIInterface apiServiceInterface= NewAPIClient.getClient().create(NewAPIInterface.class);
        HraRentReceiptParams hraRentReceiptParams=new HraRentReceiptParams(tenantNameString,
                emailIdString,
                landlordNameString,
                landlordPanString,
                address,
                lastMonthRent,
                fromDateString,
                toDateString,
                months);


        Call<Void> responseCall=apiServiceInterface.hra_rentReceipt(hraRentReceiptParams);
        responseCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }
}