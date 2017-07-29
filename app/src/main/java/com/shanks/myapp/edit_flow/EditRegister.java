package com.shanks.myapp.edit_flow;import android.annotation.SuppressLint;import android.app.DatePickerDialog;import android.app.Dialog;import android.content.Context;import android.content.CursorLoader;import android.content.Intent;import android.database.Cursor;import android.graphics.Bitmap;import android.media.ExifInterface;import android.net.Uri;import android.os.AsyncTask;import android.os.Build;import android.os.Bundle;import android.os.Environment;import android.provider.DocumentsContract;import android.provider.MediaStore;import android.support.v7.app.AppCompatActivity;import android.util.Log;import android.view.View;import android.view.Window;import android.widget.AdapterView;import android.widget.ArrayAdapter;import android.widget.AutoCompleteTextView;import android.widget.Button;import android.widget.CheckBox;import android.widget.DatePicker;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.RadioButton;import android.widget.Spinner;import android.widget.TextView;import android.widget.Toast;import com.shanks.myapp.R;import com.shanks.myapp.activities.Edit;import com.shanks.myapp.activities.MainActivity;import com.shanks.myapp.models.User_Get_Set_class;import com.squareup.picasso.Picasso;import org.json.JSONArray;import org.json.JSONObject;import java.io.ByteArrayOutputStream;import java.io.File;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.IOException;import java.text.ParseException;import java.text.SimpleDateFormat;import java.util.ArrayList;import java.util.Calendar;import java.util.Date;import java.util.GregorianCalendar;import java.util.Locale;import de.hdodenhof.circleimageview.CircleImageView;import com.shanks.myapp.adapter.CityAdapter;import com.shanks.myapp.adapter.QualificationAdapter;import com.shanks.myapp.adapter.StateAdapter;import com.shanks.myapp.models.CityModel;import com.shanks.myapp.models.QualificationModel;import com.shanks.myapp.models.StateModel;import com.shanks.myapp.utils.CallService;import com.shanks.myapp.utils.Session;import com.shanks.myapp.utils.Utils;/** * Created by katrina on 25/01/17. */public class EditRegister extends AppCompatActivity{    Session session;    Button register;    private static final int CAMERA_REQUEST = 1;    private static final int SELECT_PICTURE = 2;    private String selectedImagePath;    CircleImageView profile_image;    TextView header;    Spinner spinner1;    String category = "",compareValue;    ArrayAdapter<String> adapter;    // registration feilds    AutoCompleteTextView firstname,lastname,dob,email,phonenumber,username,password,pan,aadhar,et_address            ,et_pincode;    Spinner education_qualifition,state_id,city_id;    String stateID = "";    String cityID = "";    ArrayList<StateModel> stateModel = new ArrayList<>();    RadioButton male,female;    CheckBox accept_checkbox;    String serverFilePath="none";    String qualificationID = "0",CATID = "";    ArrayList<QualificationModel> mainQualificationModel = new ArrayList<>();    ArrayList<CityModel> cityModel = new ArrayList<>();    Calendar myCalendar = Calendar.getInstance();    LinearLayout checkbox_lin;    String addressId;    ImageView arrow_left;    String[] CategoryValues = {"Select...", "Student", "Working", "Housewife", "Freelancer", "Others"};    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.editregistration);        synchronized (this){            init();        }        getUserDetails();    }    private void init(){        session = Session.getSession(EditRegister.this);        arrow_left = (ImageView)findViewById(R.id.arrow_left);        arrow_left.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                onBackPressed();            }        });        checkbox_lin = (LinearLayout)findViewById(R.id.checkbox_lin);        header = (TextView)findViewById(R.id.header);        header.setText("Edit Profile");        profile_image = (CircleImageView)findViewById(R.id.profile_image);        profile_image.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                makeDialog();            }        });        // registration init        firstname = (AutoCompleteTextView)findViewById(R.id.firstname);        lastname = (AutoCompleteTextView)findViewById(R.id.lastname);        dob = (AutoCompleteTextView)findViewById(R.id.dob);        email = (AutoCompleteTextView)findViewById(R.id.email);        phonenumber = (AutoCompleteTextView)findViewById(R.id.phonenumber);        username = (AutoCompleteTextView)findViewById(R.id.username);        password = (AutoCompleteTextView)findViewById(R.id.password);        pan = (AutoCompleteTextView)findViewById(R.id.pan);        aadhar = (AutoCompleteTextView)findViewById(R.id.aadhar);        et_address = (AutoCompleteTextView)findViewById(R.id.et_address);        et_pincode= (AutoCompleteTextView)findViewById(R.id.et_pincode);        Log.d("NAME" , session.getFirstname());        firstname.setCursorVisible(false);        lastname.setCursorVisible(false);        email.setCursorVisible(false);        phonenumber.setCursorVisible(false);//        firstname.setText(session.getFirstname());//        lastname.setText(session.getLastname());//        email.setText(session.getEmail());//        phonenumber.setText(session.getMobilenumber());//        Picasso.with(EditRegister.this).load(Utils.BASE_IMAGE_URL + session.getUserimage()).error(R.drawable.profile).into(profile_image);        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {            @Override            public void onDateSet(DatePicker view, int year, int monthOfYear,                                  int dayOfMonth) {                // TODO Auto-generated method stub                final Calendar c = Calendar.getInstance();                int mYear = c.get(Calendar.YEAR);                Calendar userAge = new GregorianCalendar(year,monthOfYear,dayOfMonth);                Calendar minAdultAge = new GregorianCalendar();                minAdultAge.add(Calendar.YEAR, -18);                if (minAdultAge.before(userAge)) {                    Toast.makeText(EditRegister.this,"Age should be more than 18 years",Toast.LENGTH_LONG).show();                } else {                    myCalendar.set(Calendar.YEAR, year);                    myCalendar.set(Calendar.MONTH, monthOfYear);                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);//                    String myFormat = "yyyy/MM/dd"; //In which you need put here                    String myFormat = "dd/MM/yyyy"; //In which you need put here                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);                    dob.setText(sdf.format(myCalendar.getTime()));                }            }        };        dob.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                new DatePickerDialog(EditRegister.this, date, myCalendar                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();            }        });        // populate data from server        education_qualifition = (Spinner)findViewById(R.id.education_qualifition);        initEducationQualification();        education_qualifition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {            @Override            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {                qualificationID = mainQualificationModel.get(i).getQualificationID();            }            @Override            public void onNothingSelected(AdapterView<?> adapterView) {                qualificationID = "";            }        });//        spinner1.setTag(session.getCategory());        final TextView sp_Category_txt = (TextView) findViewById(R.id.sp_Category_txt);        String CATVALUE = session.getCategory();        if (CATVALUE.equalsIgnoreCase("")) {            compareValue = "Select...";            category = category;        }else {            compareValue = CATVALUE;            sp_Category_txt.setVisibility(View.VISIBLE);            sp_Category_txt.setText(compareValue);            category =compareValue;        }            sp_Category_txt.setVisibility(View.GONE);           // String[] values = {compareValue, "Retail", "Service", "Wholesale", "Manufacturing", "Exporter"};            spinner1 = (Spinner) findViewById(R.id.sp_Category);            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CategoryValues);            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);            spinner1.setAdapter(adapter);            for(int i=0;i<CategoryValues.length;i++){                if(session.getCategory().equalsIgnoreCase(CategoryValues[i])){                    spinner1.setSelection(i);                }            }            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {                @Override                public void onItemSelected(AdapterView<?> arg0, View arg1,                                           int arg2, long arg3) {                    Log.d("cat","SESSION CAT"+session.getCategory());//                if (!compareValue.equals("")) {//                    arg2 = adapter.getPosition(compareValue);//                    spinner1.setSelection(arg2);//                    arg2 = 0;//                spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(compareValue));//                if (!compareValue.equals(null)) {//                    int spinnerPosition = adapter.getPosition(compareValue);//                    spinner1.setSelection(spinnerPosition);//                }else//                if (arg2 == 0) {//                    category = compareValue;//                }else                    if (arg2 == 1) {                        category = "Retail";                    } else if (arg2 == 2)                        category = "Service";                    else if (arg2 == 3)                        category = "Wholesale";                    else if (arg2 == 4)                        category = "Manufacturing";                    else if (arg2 == 5)                        category = "Exporter";                }                @Override                public void onNothingSelected(AdapterView<?> arg0) {                    sp_Category_txt.setVisibility(View.VISIBLE);                    sp_Category_txt.setText(session.getCategory());                }            });        state_id = (Spinner)findViewById(R.id.state_id);        initState();//        city_id.setTag(session.getCityname());        final TextView city_id_txt = (TextView)  findViewById(R.id.city_id_txt);        if (!session.getCityname().equalsIgnoreCase("")) {            city_id_txt.setVisibility(View.VISIBLE);            city_id_txt.setText(session.getCityname());            cityID = city_id_txt.getText().toString();        }//            city_id_txt.setVisibility(View.GONE);            city_id = (Spinner) findViewById(R.id.city_id);            city_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {                @Override                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {                    cityID = cityModel.get(i).getId();                }                @Override                public void onNothingSelected(AdapterView<?> adapterView) {                }            });        state_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {            @Override            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {                stateID = stateModel.get(i).getId();                if(!stateID.equalsIgnoreCase("0"))                    city_id_txt.setVisibility(View.GONE);                    initCity(stateID);            }            @Override            public void onNothingSelected(AdapterView<?> adapterView) {            }        });        male = (RadioButton)findViewById(R.id.male);        male.setChecked(true);        female = (RadioButton)findViewById(R.id.female);//        if(gender.equalsIgnoreCase("male")){//            male.setChecked(true);//        } else {//            female.setChecked(true);//        }        accept_checkbox = (CheckBox)findViewById(R.id.checkbox);        register = (Button)findViewById(R.id.register);        register.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {//                startActivity(new Intent(EditRegister.this,EditRegisterUpload.class));//                finish();                String firstnameString = firstname.getText().toString().trim();                String lastnameString = lastname.getText().toString().trim();                String dobString = dob.getText().toString().trim();                String emailString = email.getText().toString().trim();                String phonenumberString = phonenumber.getText().toString().trim();                String usernameString = username.getText().toString().trim();                String passwordString = password.getText().toString().trim();                String panString = pan.getText().toString().trim();                String aadharString = aadhar.getText().toString().trim();                String et_addressString = et_address.getText().toString().trim();                String et_pincodeString = et_pincode.getText().toString().trim();                String genderString = "";                if(male.isChecked()){                    genderString = "male";                } else if(female.isChecked()){                    genderString = "female";                }                if(serverFilePath.equalsIgnoreCase("")){                    //Utils.makeDialog(EditRegister.this,"Please enter image");                } //else                if(firstnameString.equalsIgnoreCase("")){                    //Utils.makeDialog(EditRegister.this,"Please enter first name");                    firstnameString="";                } //else                if(lastnameString.equalsIgnoreCase("")){                    //Utils.makeDialog(EditRegister.this,"Please enter last name");                    lastnameString="";                } //else                if(dobString.equalsIgnoreCase("")){                    //Utils.makeDialog(EditRegister.this,"Please enter date of birth");                    dobString="";                } //else                if(qualificationID.equalsIgnoreCase("")){                    //Utils.makeDialog(EditRegister.this,"Please select qualification");                }                //else                if(emailString.equalsIgnoreCase("")){                    //Utils.makeDialog(EditRegister.this,"Please enter email id");                    emailString="";                } //else                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){                    //Utils.makeDialog(EditRegister.this,"Please enter valid email id");                }                //else                if(phonenumberString.equalsIgnoreCase("")){                    //Utils.makeDialog(EditRegister.this,"Please enter phone number");                    phonenumberString="";                } //else                 if(phonenumberString.length()<10){                    //Utils.makeDialog(EditRegister.this,"Phone number should of 10 digits");                }               /* else if(usernameString.equalsIgnoreCase("")){                    Utils.makeDialog(EditRegister.this,"Please enter user name");                }                else if(panString.equalsIgnoreCase("")){                Utils.makeDialog(EditRegister.this,"Please enter pan card number");                } else if(panString.length()<10){                    Utils.makeDialog(EditRegister.this,"Pan card number should be of 10 digits");                }*/                else if(aadharString.equalsIgnoreCase("")){                    Utils.makeDialog(EditRegister.this,"Please enter adhar card number");                } else if(aadharString.length()<12){                    Utils.makeDialog(EditRegister.this,"Adhar card number should be of 12 digits");                } else if(et_addressString.equalsIgnoreCase("")){                    Utils.makeDialog(EditRegister.this,"Please enter address");                } else if(et_pincodeString.equalsIgnoreCase("")){                    Utils.makeDialog(EditRegister.this,"Please enter pincode");                } else if(aadharString.length()<6){                     Utils.makeDialog(EditRegister.this,"Pincode should be of 6 digits");                }                /* else if(stateID.equalsIgnoreCase("0")){                    Utils.makeDialog(EditRegister.this,"Please select state");                } else if(cityID.equalsIgnoreCase("0")){                    Utils.makeDialog(EditRegister.this,"Please select city");                } */                else {                    String registerUrl = Utils.BASE_URL+Utils.EDIT_REGISTER+                            "?userid="+session.getUserId()+                            "&username=" + session.getFullname()//                            + "&password=" + passwordString                            + "&email=" + emailString                            + "&firstname=" + firstnameString                            + "&lastname=" + lastnameString                            + "&mobilenumber=" + phonenumberString                            + "&gender=" + genderString                            + "&dateofbirth=" + dobString//                            + "&imagestring=" + serverFilePath                            + "&pannumber=" + panString                            + "&category=" + category                            + "&aadharnumber=" + aadharString                            + "&termaccept=1"                            + "&address=" + et_addressString                            + "&cityid=" + cityID                            + "&pincode=" + et_pincodeString                            + "&qualificationid=" + qualificationID                            + "&addressid=" + session.getAddressid();                    Log.d("URL","Tanya"+ registerUrl);                    CallService service = new CallService(EditRegister.this, registerUrl, Utils.GET, true, new CallService.OnServicecall() {                        @Override                        public void onServicecall(String response) {                            try{                                Log.d("ankit",response);                                JSONObject jobj = new JSONObject(response);                                String responseCode = jobj.getString("responseCode");                                if(responseCode.equalsIgnoreCase("2001")){                                    String userid = jobj.getString("userid");                                    session.setUserId(userid);                                    startActivity(new Intent(EditRegister.this,EditRegisterUpload.class));                                    finish();                                    Log.d("ankit","user id :"+userid);                                } else {                                    String result = jobj.getString("result");                                    Toast.makeText(EditRegister.this,"SERVER RESPONSE - DATA NOT SAVED",Toast.LENGTH_LONG).show();                                }                            } catch (Exception ex){                                ex.printStackTrace();                            }                        }                    });                    service.execute();                }            }        });    }    private void makeDialog(){        // custom dialog        final Dialog dialog = new Dialog(EditRegister.this);        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);        dialog.setContentView(R.layout.image_dialog);        TextView dialog_camera = (TextView)dialog.findViewById(R.id.dialog_camera);        dialog_camera.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                dialog.dismiss();                imageFromCamera();            }        });        TextView dialog_card = (TextView)dialog.findViewById(R.id.dialog_card);        dialog_card.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                dialog.dismiss();                imageFromCard();            }        });        dialog.show();    }    private void imageFromCamera(){        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);        startActivityForResult(cameraIntent, CAMERA_REQUEST);    }    private void imageFromCard(){        Intent intent = new Intent();        intent.setType("image/*");        intent.setAction(Intent.ACTION_GET_CONTENT);        startActivityForResult(                Intent.createChooser(intent, "Select Picture"),                SELECT_PICTURE);    }    @SuppressLint("NewApi")    public  String getRealPathFromURI_API19(Context context, Uri uri){        String filePath = "";        String wholeID = DocumentsContract.getDocumentId(uri);        // Split at colon, use second item in the array        String id = wholeID.split(":")[1];        String[] column = { MediaStore.Images.Media.DATA };        // where id is equal to        String sel = MediaStore.Images.Media._ID + "=?";        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,                column, sel, new String[]{ id }, null);        int columnIndex = cursor.getColumnIndex(column[0]);        if (cursor.moveToFirst()) {            filePath = cursor.getString(columnIndex);        }        cursor.close();        return filePath;    }    @SuppressLint("NewApi")    public  String getRealPathFromURI_API11to18(Context context, Uri contentUri) {        String[] proj = { MediaStore.Images.Media.DATA };        String result = null;        CursorLoader cursorLoader = new CursorLoader(                context,                contentUri, proj, null, null, null);        Cursor cursor = cursorLoader.loadInBackground();        if(cursor != null){            int column_index =                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);            cursor.moveToFirst();            result = cursor.getString(column_index);        }        return result;    }    public  String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){        String[] proj = { MediaStore.Images.Media.DATA };        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);        int column_index                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);        cursor.moveToFirst();        return cursor.getString(column_index);    }    @Override    public void onActivityResult(int requestCode, int resultCode, Intent data) {        super.onActivityResult(requestCode, resultCode, data);        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {            Bitmap photo = (Bitmap) data.getExtras().get("data");            profile_image.setImageBitmap(photo);            ByteArrayOutputStream bytes = new ByteArrayOutputStream();            photo.compress(Bitmap.CompressFormat.JPEG, 90, bytes);            final String filePath = Environment.getExternalStorageDirectory()+"/"+System.currentTimeMillis() + ".jpg";            File destination = new File(filePath);            FileOutputStream fo;            try {                destination.createNewFile();                fo = new FileOutputStream(destination);                fo.write(bytes.toByteArray());                fo.close();            } catch (FileNotFoundException e) {                e.printStackTrace();            } catch (IOException e) {                e.printStackTrace();            }            new AsyncTask<Void,Void,Void>(){                @Override                protected Void doInBackground(Void... voids) {                    try{                        serverFilePath = Utils.uploadFile(filePath);                    } catch (Exception ex){                        ex.printStackTrace();                    }                    Log.d("ankit",serverFilePath);                    return null;                }            }.execute();        } else if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK){            Uri selectedImageUri = data.getData();            sendToServer(selectedImageUri);            profile_image.setImageURI(selectedImageUri);            profile_image.setRotation(getCameraPhotoOrientation(EditRegister.this,selectedImageUri,selectedImagePath));        }    }    private void sendToServer(Uri selectedImageUri){        synchronized (this){            if (Build.VERSION.SDK_INT < 11)                selectedImagePath = getRealPathFromURI_BelowAPI11(EditRegister.this, selectedImageUri);                // SDK >= 11 && SDK < 19            else if (Build.VERSION.SDK_INT < 19)                selectedImagePath = getRealPathFromURI_API11to18(EditRegister.this, selectedImageUri);                // SDK > 19 (Android 4.4)            else                selectedImagePath = getRealPathFromURI_API19(EditRegister.this, selectedImageUri);        }        new AsyncTask<Void,Void,Void>(){            @Override            protected Void doInBackground(Void... voids) {                Log.d("ankit","local path :::"+selectedImagePath);                try{                    serverFilePath = Utils.uploadFile(selectedImagePath);                } catch (Exception ex){                    ex.printStackTrace();                }                return null;            }            @Override            protected void onPostExecute(Void aVoid) {                super.onPostExecute(aVoid);                Log.d("ankit",serverFilePath);            }        }.execute();    }    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){        int rotate = 0;        try {            context.getContentResolver().notifyChange(imageUri, null);            File imageFile = new File(imagePath);            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);            switch (orientation) {                case ExifInterface.ORIENTATION_ROTATE_270:                    rotate = 270;                    break;                case ExifInterface.ORIENTATION_ROTATE_180:                    rotate = 180;                    break;                case ExifInterface.ORIENTATION_ROTATE_90:                    rotate = 90;                    break;            }        } catch (Exception e) {            e.printStackTrace();        }        return rotate;    }//    private void initCategory(){//        CallService service = new CallService(EditRegister.this, Utils.BASE_URL//                + Utils.CATEGORIES, Utils.GET, false, new CallService.OnServicecall() {//            @Override//            public void onServicecall(String response) {//                try{//////                    JSONObject jobj = new JSONObject(response);//                    JSONArray jarr = jobj.getJSONArray("categoryList");////                    QualificationModel zero_model = new QualificationModel();//                    zero_model.setQualificationID("");//                    zero_model.setQualificationDescription("");//                    zero_model.setQualificationName("Select ... ");////                    mainQualificationModel.add(zero_model);////                    for (int i=0;i<jarr.length();i++){//                        JSONObject innerJob = jarr.getJSONObject(i);//                        String qualificationname = innerJob.getString("qualificationname");//                        String description = innerJob.getString("description");//                        String id = innerJob.getString("id");////                        QualificationModel model = new QualificationModel();//                        model.setQualificationID(id);//                        model.setQualificationDescription(description);//                        model.setQualificationName(qualificationname);////                        mainQualificationModel.add(model);//                    }////                    QualificationAdapter adapter = new QualificationAdapter(EditRegister.this,0,mainQualificationModel,getResources());//                    education_qualifition.setAdapter(adapter);//                } catch (Exception ex){//                    ex.printStackTrace();//                }//            }//        });////        service.execute();//    }    private void initEducationQualification(){        CallService service = new CallService(EditRegister.this, Utils.BASE_URL                + Utils.GET_QUALIFICATION, Utils.GET, false, new CallService.OnServicecall() {            @Override            public void onServicecall(String response) {                try{                    JSONObject jobj = new JSONObject(response);                    JSONArray jarr = jobj.getJSONArray("qualificationList");                    QualificationModel zero_model = new QualificationModel();                    zero_model.setQualificationID("");                    zero_model.setQualificationDescription("");                    zero_model.setQualificationName("Select ... ");                    mainQualificationModel.add(zero_model);                    for (int i=0;i<jarr.length();i++){                        JSONObject innerJob = jarr.getJSONObject(i);                        String qualificationname = innerJob.getString("qualificationname");                        String description = innerJob.getString("description");                        String id = innerJob.getString("id");                        QualificationModel model = new QualificationModel();                        model.setQualificationID(id);                        model.setQualificationDescription(description);                        model.setQualificationName(qualificationname);                        mainQualificationModel.add(model);                    }                    QualificationAdapter adapter = new QualificationAdapter(EditRegister.this,0,mainQualificationModel,getResources());                    education_qualifition.setAdapter(adapter);                } catch (Exception ex){                    ex.printStackTrace();                }            }        });        service.execute();    }    private void initState(){        CallService service = new CallService(EditRegister.this, Utils.BASE_URL + Utils.COUNTRY_CITY_STATE + "?countryid=1", Utils.GET, false, new CallService.OnServicecall() {            @Override            public void onServicecall(String response) {                try{                    JSONObject jobj = new JSONObject(response);                    JSONArray jarr = jobj.getJSONArray("stateList");                    StateModel zero_model = new StateModel();                    zero_model.setId("0");                    zero_model.setName("Select ...");                    stateModel.add(zero_model);                    for (int i=0;i<jarr.length();i++){                        JSONObject innerJobj = jarr.getJSONObject(i);                        String name = innerJobj.getString("name");                        String id = innerJobj.getString("id");                        StateModel model = new StateModel();                        model.setId(id);                        model.setName(name);                        stateModel.add(model);                        Log.d("CJ PRINT","STATE MODEL -"+stateModel.get(i).getName());                    }                    StateAdapter adapter = new StateAdapter(EditRegister.this,0,stateModel,getResources());                    state_id.setAdapter(adapter);                } catch (Exception ex){                    ex.printStackTrace();                }            }        });        service.execute();    }    private void initCity(String stateId){        Log.d("ankit","state id "+stateId);        try{            cityModel.clear();        } catch (Exception ex){            ex.printStackTrace();        }        CallService service = new CallService(EditRegister.this, Utils.BASE_URL + Utils.COUNTRY_CITY_STATE                + "?stateid=" + stateId, Utils.GET, false, new CallService.OnServicecall() {            @Override            public void onServicecall(String response) {                try{                    Log.d("ankit",response);                    JSONObject jobj = new JSONObject(response);                    JSONArray jarr = jobj.getJSONArray("cityList");                    CityModel zero_model = new CityModel();                    zero_model.setId("0");                    zero_model.setName("Select ...");                    cityModel.add(zero_model);                    for (int i=0;i<jarr.length();i++){                        JSONObject inner_job = jarr.getJSONObject(i);                        String name = inner_job.getString("name");                        String id = inner_job.getString("id");                        CityModel model = new CityModel();                        model.setName(name);                        model.setId(id);                        cityModel.add(model);                    }                    CityAdapter adapter = new CityAdapter(EditRegister.this,0,cityModel,getResources());                    city_id.setAdapter(adapter);                    for(int j=0;j<cityModel.size();j++){                        if(cityID.equalsIgnoreCase(cityModel.get(j).getId())){                            city_id.setSelection(j);                        }                    }                } catch (Exception ex){                    ex.printStackTrace();                }            }        });        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {            service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);        } else {            service.execute();        }//        service.execute();    }    private void getUserDetails(){        String url = Utils.BASE_URL + Utils.USER_DETAILS + "?userid="+session.getUserId();        Log.d("ankit","get user details::"+url);        CallService service = new CallService(EditRegister.this, url, Utils.GET, true, new CallService.OnServicecall() {            @Override            public void onServicecall(String response) {                Log.d("ankit","get user details response::"+response);                try{                    PARSE_RESPONSE(response);                }catch (Exception ex){                    ex.printStackTrace();                }            }        });        service.execute();    }    private void PARSE_RESPONSE(String response) throws Exception{        password.setVisibility(View.GONE);        checkbox_lin.setVisibility(View.GONE);        JSONObject jobj = new JSONObject(response);        JSONArray personaldetailList = jobj.getJSONArray("personaldetailList");        JSONObject innerJob = personaldetailList.getJSONObject(0);        String Ppincode = innerJob.getString("pincode");        et_pincode.setText(Ppincode);        String Ffirstname = innerJob.getString("firstname");        firstname.setText(Ffirstname);        firstname.setEnabled(false);        String ADaddress = innerJob.getString("address");        et_address.setText(ADaddress);        String Ggender = innerJob.getString("gender");        if(Ggender.equalsIgnoreCase("male")){            male.setChecked(true);        } else {            female.setChecked(true);        }        String Qqualificationid = innerJob.getString("qualificationId");        for(int i=0;i<mainQualificationModel.size();i++){            if(Qqualificationid.equalsIgnoreCase(mainQualificationModel.get(i).getQualificationID())){                education_qualifition.setSelection(i);            }        }        String CTcatid = innerJob.getString("category");        for(int i=0;i<mainQualificationModel.size();i++){            if(CTcatid.equalsIgnoreCase(mainQualificationModel.get(i).getQualificationID())){                spinner1.setSelection(i);            }        }        String MmobileNumber = innerJob.getString("mobileNumber");        phonenumber.setText(MmobileNumber);        String Sstateid = innerJob.getString("stateid");        TextView state_id_txt = (TextView) findViewById(R.id.state_id_txt);        if (!Sstateid.equalsIgnoreCase("")) {            state_id_txt.setVisibility(View.VISIBLE);            state_id_txt.setText(Sstateid);            //state_id.setVisibility(View.GONE);        }        String Ccityid = innerJob.getString("cityid");        cityID = Ccityid;        synchronized (this){            for(int i=0;i<stateModel.size();i++) {                if (Sstateid.equalsIgnoreCase(stateModel.get(i).getId())) {                    state_id_txt.setVisibility(View.GONE);                    state_id.setSelection(i);                }            }        }        String EemailId = innerJob.getString("emailId");        email.setText(EemailId);        String Sstatename = innerJob.getString("statename");        String Ppannumber = innerJob.getString("pannumber");        pan.setText(Ppannumber);        String Llastname = innerJob.getString("lastname");        lastname.setText(Llastname);        String Aaddressid = innerJob.getString("addressid");        addressId = Aaddressid;        String Ddob = innerJob.getString("dob");        dob.setText(parseDateToddMMyyyy(Ddob));        String Yuserimage = innerJob.getString("userimage");        serverFilePath = Yuserimage;        Picasso.with(EditRegister.this).load(Utils.BASE_IMAGE_URL+Yuserimage).into(profile_image);        String Aaadharnumber = innerJob.getString("aadharnumber");        aadhar.setText(Aaadharnumber);//        aadhar.setEnabled(false);        String Uusername = innerJob.getString("username");        username.setText(Uusername);    }    public String parseDateToddMMyyyy(String time) {        String inputPattern = "yyyy-MM-dd HH:mm:ss";        String outputPattern = "yyyy/MM/dd";        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);        Date date = null;        String str = null;        try {            date = inputFormat.parse(time);            str = outputFormat.format(date);        } catch (ParseException e) {            e.printStackTrace();        }        return str;    }}