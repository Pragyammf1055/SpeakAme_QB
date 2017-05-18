package com.speakame.Activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.speakame.Beans.AllBeans;
import com.speakame.Beans.User;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Services.HomeService;
import com.speakame.Xmpp.MyService;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;
import com.speakame.utils.JSONParser;
import com.speakame.utils.ServiceHandler;
import com.speakame.utils.VolleyCallback;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;

public class SignUp_Activity extends AnimRootActivity implements VolleyCallback, View.OnClickListener {

    public static Location loc;
    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 2;
    //keep track of cropping intent
    final int PIC_CROP = 3;
    Bitmap thePic;
    TextView headtext;
    AutoCompleteTextView editlanguage;
    EditText editname, editemail, editmobile, editpassword, editcity, mcontry_code, selectLanguage;
    ImageView fbimage, twitterimage, user_image, malecheck, maleuncheck, femalecheck, femaleuncheck;
    Button btn_signup;
    Typeface typeface, tf1;
    String Name, Email, City, CountCode, Mobile, Password, currentDateTimeString, Language, Gender, Encoded_userimage;

    ArrayList<String> languageListString = new ArrayList<String>();
    Spinner spn;
    Double lat, lon;
    //captured picture uri
    private Uri picUri;
    private AlertDialog mProgressDialog;
    LoginButton loginbutton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_up_);
        callbackManager = CallbackManager.Factory.create();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user_image = (ImageView) findViewById(R.id.userimage);
        editname = (EditText) findViewById(R.id.username);
        editemail = (EditText) findViewById(R.id.emailid);
        editcity = (EditText) findViewById(R.id.city);
        editmobile = (EditText) findViewById(R.id.mobileno);
        mcontry_code = (EditText) findViewById(R.id.cntrycode);
        editpassword = (EditText) findViewById(R.id.paasword);

        loginbutton = (LoginButton) findViewById(R.id.login_button);
        selectLanguage = (EditText) findViewById(R.id.selectLanguage);
        // editlanguage = (AutoCompleteTextView) findViewById(R.id.selectlanguage);
        fbimage = (ImageView) findViewById(R.id.facebookbtn);
        twitterimage = (ImageView) findViewById(R.id.twitterbrn);
        btn_signup = (Button) findViewById(R.id.btnsignup);
        headtext = (TextView) findViewById(R.id.headtext);
        malecheck = (ImageView) findViewById(R.id.malec);
        maleuncheck = (ImageView) findViewById(R.id.male);
        femalecheck = (ImageView) findViewById(R.id.femalec);
        femaleuncheck = (ImageView) findViewById(R.id.female);
        headtext.setText("Register");

        editcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp_Activity.this, CountryListActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        selectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp_Activity.this, SelectLanguageActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editname, InputMethodManager.SHOW_IMPLICIT);


//        JSONObject obj = null;
//        final ArrayList<String> CountryName = new ArrayList<String>();
//        final ArrayList<String> CountryCode = new ArrayList<String>();
//        try {
//            obj = new JSONObject(Function.loadJSONFromAsset(SignUp_Activity.this, "countries.json"));
//
//            JSONArray m_jArry = obj.getJSONArray("countries");
//
//            for (int i = 0; i < m_jArry.length(); i++) {
//                JSONObject jsonObject = m_jArry.getJSONObject(i);
//                CountryName.add(jsonObject.getString("name"));
//                CountryCode.add(jsonObject.getString("code"));
//
//            }
//
//            System.out.println("namecountry" + CountryName);
//            System.out.println("codecountry" + CountryCode);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        ArrayAdapter<String> adapterr = new ArrayAdapter<String>
//                (this, android.R.layout.select_dialog_item, CountryName);
//        //Getting the instance of AutoCompleteTextView
//        editcity.setThreshold(1);
//        editcity.setAdapter(adapterr);
//
//        editcity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for (int i = 0; i < CountryCode.size(); i++) {
//                        if(editcity.getText().toString().equalsIgnoreCase(CountryName.get(i))){
//                            mcontry_code.setText(CountryCode.get(i));
//                        }
//                }
//
//            }
//        });


//        Language = Locale.getDefault().getDisplayLanguage();
//        editlanguage.setText(Language);

        //  new GetLanguage().execute();

        tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        headtext.setTypeface(tf1);
        typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        editname.setTypeface(typeface);
        editemail.setTypeface(typeface);
        editcity.setTypeface(typeface);
        selectLanguage.setTypeface(typeface);
        mcontry_code.setTypeface(typeface);
        editmobile.setTypeface(typeface);
        editpassword.setTypeface(typeface);
        // editlanguage.setTypeface(typeface);
        btn_signup.setTypeface(typeface);
        stopService(new Intent(SignUp_Activity.this, HomeService.class));

        Function.cameraPermisstion(SignUp_Activity.this, 1);

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        currentDateTimeString = dateFormatter.format(today);
        Log.d("currentdatetime", currentDateTimeString);


        Pattern gmailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                Email = account.name;
            }
        }
        editemail.setText(Email);

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //use standard intent to capture an files

                    Intent galleryIntent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //we will handle the returned data in onActivityResult
                    startActivityForResult(galleryIntent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(SignUp_Activity.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissKeyboard(SignUp_Activity.this);
                Name = editname.getText().toString();
                Email = editemail.getText().toString();
                City = editcity.getText().toString();
                Language = selectLanguage.getText().toString();
                    CountCode = mcontry_code.getText().toString();
                Mobile = editmobile.getText().toString();
                Password = editpassword.getText().toString();

                if (malecheck.getVisibility() == View.VISIBLE) {
                    Gender = "Male";

                }
                if (femalecheck.getVisibility() == View.VISIBLE) {
                    Gender = "Female";

                }
                if (Name.length() == 0) {
                    editname.setError(getResources().getString(R.string.error_field_required));
                } else if (Email.length() == 0) {
                    editemail.setError(getResources().getString(R.string.error_field_required));
                } else if (!Email.contains("@") && !Email.contains(".")) {
                    editemail.setError(getResources().getString(R.string.error_invalid_email));
                } else if (City.length() == 0) {
                    editcity.setError(getResources().getString(R.string.error_field_required));
                } else if (Language.length() == 0) {
                    selectLanguage.setError(getResources().getString(R.string.error_field_required));
                } else if (Mobile.length() < 8 || Mobile.length() > 12) {
                    editmobile.setError(getResources().getString(R.string.error_field_required_length));
                } else if (Password.length() == 0) {
                    editpassword.setError(getResources().getString(R.string.error_field_required));
                } else if (Password.length() < 6) {
                    editpassword.setError(getResources().getString(R.string.error_field_password_lenght));
                } else {

                    mProgressDialog = new SpotsDialog(SignUp_Activity.this);
                    mProgressDialog.setMessage("Please wait...");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                    JSONObject jsonObj = new JSONObject();
                    JSONArray jsonArray = new JSONArray();

                    try {
                        jsonObj.put("method", AppConstants.USER_SIGNUP);
                        jsonObj.put("userImage", Encoded_userimage);
                        jsonObj.put("username", Name);
                        jsonObj.put("email", Email);
                        jsonObj.put("country", City);
                        jsonObj.put("countrycode", CountCode);
                        jsonObj.put("mobile", Mobile);
                        jsonObj.put("password", Password);
                        jsonObj.put("language", Language);
                        jsonObj.put("gender", Gender);
                        jsonObj.put("dateTime", currentDateTimeString);
                        jsonObj.put("mobile_uniquekey", Function.getAndroidID(SignUp_Activity.this));
                        jsonObj.put("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken());
                        jsonArray.put(jsonObj);

                        System.out.println("SEndSignup>" + jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONParser jsonParser = new JSONParser(getApplicationContext());
                    jsonParser.parseVollyJsonArray(AppConstants.REGISTER_LOG, 1, jsonArray, SignUp_Activity.this);
                    System.out.println("AppConstants.URL.REGISTER_LOG" + AppConstants.REGISTER_LOG);
                    System.out.println("jsonObject" + jsonObj);


                    // new RegisterAsynch().execute();
                }


            }
        });

        malecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                malecheck.setVisibility(View.GONE);
                maleuncheck.setVisibility(View.VISIBLE);
                femalecheck.setVisibility(View.VISIBLE);
                femaleuncheck.setVisibility(View.GONE);

            }
        });
        maleuncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleuncheck.setVisibility(View.GONE);
                malecheck.setVisibility(View.VISIBLE);
                femalecheck.setVisibility(View.GONE);
                femaleuncheck.setVisibility(View.VISIBLE);

            }
        });

        femalecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femalecheck.setVisibility(View.GONE);
                femaleuncheck.setVisibility(View.VISIBLE);
                maleuncheck.setVisibility(View.GONE);
                malecheck.setVisibility(View.VISIBLE);


            }
        });
        femaleuncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femalecheck.setVisibility(View.VISIBLE);
                femaleuncheck.setVisibility(View.GONE);
                maleuncheck.setVisibility(View.VISIBLE);
                malecheck.setVisibility(View.GONE);
            }
        });


        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        loginbutton.setReadPermissions(permissionNeeds);

        loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("onSuccess");
                GraphRequest request = GraphRequest.newMeRequest
                        (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                Log.v("LoginActivity", response.toString());
                                System.out.println("Check: " + response.toString());
                                try {
                                    String socialid = object.getString("id");
                                    String name = object.getString("name");
                                    Log.d("facebook_user", name);
                                    String email = object.getString("email");
                                    String imgUrl = "https://graph.facebook.com/" + socialid + "/picture?type=large";
                                    //  String gender = object.getString("gender");
                                    //  String birthday = object.getString("birthday");
                                    System.out.println("facebookauthentication" + socialid + ", " + name + ", " + email + "," + imgUrl);


                                    new LoginTask(SignUp_Activity.this).execute("facebook", socialid,
                                            name, email, imgUrl);
                                    //response-513937435454439, Vikas Barve, mmfinfotech346@gmail.com, male, 03/10/2000
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }


            @Override
            public void onError(FacebookException error) {
                System.out.println("onError");
                Snackbar.make(findViewById(android.R.id.content), "Please try again later", Snackbar.LENGTH_LONG).setAction("Alert!", null).show();
            }


        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                editcity.setText(data.getStringExtra("countryname"));
                mcontry_code.setText(data.getStringExtra("countrycode"));

            } else if (requestCode == 1) {
                selectLanguage.setText(data.getStringExtra("language"));
            } else if (requestCode == CAMERA_CAPTURE) {
                picUri = data.getData();
                performCrop();
            }//user is returning from cropping the files
            else if (requestCode == PIC_CROP) {
//get the returned data
                Bundle extras = data.getExtras();
//get the cropped bitmap
                thePic = extras.getParcelable("data");
                Log.d("pictureimage", String.valueOf(thePic));

                user_image.setImageBitmap(thePic);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.d("pictureimage", Encoded_userimage);
            }

        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginManager.getInstance().logOut();
    }
    private void performCrop() {

        try {

            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate files type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }

            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }

        }
    }

    @Override
    public void backResponse(String response) {
        Log.d("response", response);
        String user_id = "";
        if (response != null) {
            try {
                JSONObject mainObject = new JSONObject(response);

                if (mainObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray topArray = mainObject.getJSONArray("result");

                    for (int i = 0; topArray.length() > i; i++) {

                        JSONObject jsonObject2 = topArray.getJSONObject(i);
                        user_id = jsonObject2.getString("userId");
//                        System.out.println("user_id" + user_id);

                        //String mobileNo = jsonObject2.getString("mobile").replace("");

                        AppPreferences.setLoginId(SignUp_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));
                        AppPreferences.setMobileuser(SignUp_Activity.this, jsonObject2.getString("mobile"));
                       /* AppPreferences.setPassword(SignUp_Activity.this, jsonObject2.getString("password"));
                        AppPreferences.setFirstUsername(SignUp_Activity.this, jsonObject2.getString("username"));
                        AppPreferences.setUserprofile(SignUp_Activity.this, jsonObject2.getString("userImage"));
                        AppPreferences.setEmail(SignUp_Activity.this, jsonObject2.getString("email"));
                        AppPreferences.setUsercity(SignUp_Activity.this, jsonObject2.getString("country"));
                        AppPreferences.setCountrycode(SignUp_Activity.this, jsonObject2.getString("countrycode"));
                        AppPreferences.setUSERLANGUAGE(SignUp_Activity.this, jsonObject2.getString("language"));
                        AppPreferences.setUsergender(SignUp_Activity.this, jsonObject2.getString("gender"));*/

                        /*User user = new User();
                        user.setName(jsonObject2.getString("username"));
                        user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ","").replace("+",""));
                        user.setPassword(jsonObject2.getString("password"));

                        DatabaseHelper.getInstance(SignUp_Activity.this).insertUser(user);*/

                        Intent intent = new Intent(getApplicationContext(), Verify_numberActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                       /* new AddmemberAsynch().execute(jsonObject2.getString("country_with_mobile"), jsonObject2.getString("password"),
                                jsonObject2.getString("username"), jsonObject2.getString("email"));*/

                    }

                } else if (mainObject.getString("status").equalsIgnoreCase("300")) {
                    Snackbar.make(findViewById(android.R.id.content), "Mobile Number Already Exists. Please use another Mobile Number.", Snackbar.LENGTH_LONG).show();
                } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                    Snackbar.make(findViewById(android.R.id.content), "Email Id Already Exists. Please use another Email Id.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Some Error Occured.", Snackbar.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgressDialog.dismiss();


        }
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {

    }

//////////getlanguage//////////////



    ////////////////////////////////

    private class GetLanguage extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObj;
        private int status = 0;

        public GetLanguage() {
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("************SpeakameLanguage*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("language_value :------------ "
                        + AppConstants.COMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.COMMONURL);
                jsonObj = new JSONObject();
                jsonObj.put("method", "allLanguage");

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json Data", jsonArray.toString());

                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Sent JSON is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;
                response = httpclient.execute(httppost);
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonString = "";
                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("JSONString response is : " + jsonString);
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        jsonString = jsonObj.getString("result");

                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out
                                    .println("--------- message 200 got ----------");
                            status = 200;

                            JSONArray resultArray = jsonObj.getJSONArray("result");
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject jsonObject = resultArray.getJSONObject(i);
                                Language = jsonObject.getString("language");

                                AllBeans allBeans = new AllBeans();
                                allBeans.setLanguages(Language);

                                languageListString.add(Language);

                            }

                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("400")) {
                            status = 400;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("500")) {
                            status = 500;
                            return jsonString;
                        }
                    }
                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("status", status + "");
            if (status == 200) {

                ArrayAdapter city_adapter = new ArrayAdapter<String>(SignUp_Activity.this, android.R.layout.simple_dropdown_item_1line, languageListString);

                editlanguage.setAdapter(city_adapter);

            } else {
                Toast.makeText(SignUp_Activity.this,
                        "Please Try Again Later", Toast.LENGTH_LONG).show();//fa
            }
        }
    }



    class LoginTask extends AsyncTask<String, Void, String> {
        String status = "", result = "", loginId = "";
        String pass = "";
        Context context;


        public LoginTask(Context context) {
            this.context = context;
            mProgressDialog = new SpotsDialog(SignUp_Activity.this);
            mProgressDialog.setTitle("Login");
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("loginType", params[0]));

            if (params[0].equals("web")) {
              /*  values.add(new BasicNameValuePair("mobile", params[1]));
                values.add(new BasicNameValuePair("password", params[2]));
//                values.add(new BasicNameValuePair("countryCode", params[3]));
                values.add(new BasicNameValuePair("countryCode", countryCode));
                values.add(new BasicNameValuePair("dateTime", currentDateTimeString));
                values.add(new BasicNameValuePair("method", AppConstants.LOGIN));
                values.add(new BasicNameValuePair("mobile_uniquekey", Function.getAndroidID(SignUp_Activity.this)));
                values.add(new BasicNameValuePair("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken()));*/
                pass = params[2];
            } else if (params[0].equals("facebook")) {
                values.add(new BasicNameValuePair("user_social_id", params[1]));
                values.add(new BasicNameValuePair("user_name", params[2]));
                values.add(new BasicNameValuePair("password", ""));
                values.add(new BasicNameValuePair("email", params[3]));
                values.add(new BasicNameValuePair("profpic", params[4]));
                values.add(new BasicNameValuePair("method", AppConstants.LOGIN));
                values.add(new BasicNameValuePair("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken()));
                values.add(new BasicNameValuePair("mobile_uniquekey", Function.getAndroidID(SignUp_Activity.this)));
//                values.add(new BasicNameValuePair("device_id", getDeviceId()));
//                values.add(new BasicNameValuePair("android_id", Uniqueid));

            }

            Log.d("login_value", values.toString());
            String login_json = serviceHandler.makeServiceCall(AppConstants.REGISTER_LOG, ServiceHandler.POST, values);
            System.out.println("Logine value : ------------ " + AppConstants.REGISTER_LOG);


            if (login_json != null) {
                Log.d("login_json", login_json.toString());
                try {
                    JSONObject jsonObject = new JSONObject(login_json);
                    status = jsonObject.getString("status");
                    result = jsonObject.getString("result");

                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        loginId = jsonObject2.getString("userId");
                        AppPreferences.setLoginId(SignUp_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));
                        AppPreferences.setSocialId(SignUp_Activity.this, jsonObject2.getString("social_id"));
                        // AppPreferences.setMobileuser(SignIn_Activity.this, jsonObject2.getString("country_with_mobile").replace(" ","-"));
                        AppPreferences.setPassword(SignUp_Activity.this, jsonObject2.getString("password"));
                        AppPreferences.setFirstUsername(SignUp_Activity.this, jsonObject2.getString("username"));
                        AppPreferences.setUserprofile(SignUp_Activity.this, jsonObject2.getString("userImage"));

                        System.out.println("userpic" + jsonObject2.getString("userImage"));
                        System.out.println("usermobile" + jsonObject2.getString("country_with_mobile").replace(" ","").replace("+",""));
                        System.out.println("userPassword" + jsonObject2.getString("password"));
                        AppPreferences.setEmail(SignUp_Activity.this, jsonObject2.getString("email"));
                        AppPreferences.setMobileuser(SignUp_Activity.this, jsonObject2.getString("country_with_mobile").replace(" ","").replace("+",""));
                        AppPreferences.setUsercity(SignUp_Activity.this, jsonObject2.getString("country"));
                        AppPreferences.setCountrycode(SignUp_Activity.this, jsonObject2.getString("countrycode"));
                        AppPreferences.setUSERLANGUAGE(SignUp_Activity.this, jsonObject2.getString("language"));
                        AppPreferences.setUsergender(SignUp_Activity.this, jsonObject2.getString("gender"));
                        AppPreferences.setUserstatus(SignUp_Activity.this, jsonObject2.getString("userProfileStatus"));
                        AppPreferences.setBlockList(SignUp_Activity.this, jsonObject2.getString("block_users"));
                        AppPreferences.setPicprivacy(SignUp_Activity.this, jsonObject2.getString("profie_pic_privacy"));
                        AppPreferences.setStatusprivacy(SignUp_Activity.this, jsonObject2.getString("profie_status_privacy"));
                        //AppPreferences.setLoginStatus(SignIn_Activity.this, jsonObject2.getString("user_status"));
                        AppPreferences.setLoginStatus(SignUp_Activity.this, "1");
                        AppPreferences.setRegisterDate(SignUp_Activity.this, jsonObject2.getString("start_date"));
                        AppPreferences.setRegisterEndDate(SignUp_Activity.this, jsonObject2.getString("end_date"));
//                        AppPreferences.setRemainingDays(SignIn_Activity.this, jsonObject2.getString("reamning_days"));
                        //   System.out.println("userenddate" + jsonObject2.getString("end_date"));
                        User user = new User();
                        user.setName(jsonObject2.getString("username"));
                        user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ","").replace("+",""));
                        user.setPassword(jsonObject2.getString("password"));
                        DatabaseHelper.getInstance(SignUp_Activity.this).insertUser(user);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            //    finish();
            Log.d("data from server", status + "   " + loginId);
            if (status.equals("200") && !loginId.equals("")) {

                System.out.println("loginid1" + AppPreferences.getLoginId(context));
                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(SignUp_Activity.this, TwoTab_Activity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("300")) {

                //imageDialog();
                Log.d("data>>>", status + "   " + loginId);


            } else if (status.equals("400")) {
                Snackbar.make(findViewById(android.R.id.content), "Not valid user !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else if (status.equals("600")) {
                System.out.println("loginid1" + AppPreferences.getLoginId(context));
                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(SignUp_Activity.this, AlertSpeakameActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("700")) {
                System.out.println("loginid1" + AppPreferences.getLoginId(context));
                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(SignUp_Activity.this, AlertDaysSpeakameActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("800")) {
                //confirmpopup();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Check your network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }
    }

}
