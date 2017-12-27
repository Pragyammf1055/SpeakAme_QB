package com.speakame.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.speakame.Beans.User;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Services.HomeService;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;
import com.speakame.utils.ServiceHandler;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Main_Activity extends AnimRootActivity {

    private static final String TAG = "Main_Activity";
    private static final int REQUEST_READ_PHONE_STATE = 100;
    public Dialog dialog;
    Button mbtn_sign, mbtn_signup;
    TextView mhelp_textview;
    Typeface typeface;
    CallbackManager callbackManager;
    LoginButton loginbutton;
    EditText autoCompleteTextView, meditcountcode, selectLanguage;
    String MobileNumber, Password, currentDateTimeString, Language, Country, CounCode, countryCode;
    private AlertDialog mProgressDialog;

    @Override
    protected void onStop() {
        super.onStop();
        LoginManager.getInstance().logOut();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());             // FaceBook
        setContentView(R.layout.activity_mainscreen);
        callbackManager = CallbackManager.Factory.create();
        getSupportActionBar().hide();

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ FaceBook initialization code Start~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  */

        loginbutton = (LoginButton) findViewById(R.id.login_button);
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

                                    new LoginTask(Main_Activity.this).execute("facebook", socialid,
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

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ FaceBook initialization code END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  */

        initViews();
        setListener();
        checkPermission();
//        checkPermissionForReadPhoneState();
    }


    private void checkPermissionForReadPhoneState() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            QBSubscriptiondevice();
        }

    }

    private void QBSubscriptiondevice() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG, "Firebase refreshedToken :- " + refreshedToken);
        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
        subscription.setEnvironment(QBEnvironment.DEVELOPMENT);
//        checkPermission();

        String deviceId;
        final TelephonyManager mTelephony = (TelephonyManager) this.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
            Log.v(TAG, "Device id :- .............." + deviceId);
        } else {
            deviceId = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }
        subscription.setDeviceUdid(deviceId);
//
        subscription.setRegistrationID(refreshedToken);

        Log.v(TAG, "Firebase refreshedToken :-" + refreshedToken);
        Log.v(TAG, "Device id :- .............." + deviceId);

        QBPushNotifications.createSubscription(subscription).performAsync(
                new QBEntityCallback<ArrayList<QBSubscription>>() {

                    @Override
                    public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                        Log.i(TAG, ">>> subscription created :- " + subscriptions.toString());
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e(TAG, "onSubscriptionError :- " + errors.getMessage());
                    }
                });

    }


    private void checkPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            QBSubscriptiondevice();
        }


        if (ContextCompat.checkSelfPermission(Main_Activity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Main_Activity.this,
                    Manifest.permission.READ_CONTACTS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]
                                    {Manifest.permission.READ_CONTACTS}
                            , 1);
                }

            } else {

                ActivityCompat.requestPermissions(Main_Activity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);

            }
        }

        if (ContextCompat.checkSelfPermission(Main_Activity.this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Main_Activity.this,
                    Manifest.permission.WRITE_CONTACTS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]
                                    {Manifest.permission.WRITE_CONTACTS}
                            , 1);
                }

            } else {

                ActivityCompat.requestPermissions(Main_Activity.this,
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        1);

            }
        }
/*
        if (ContextCompat.checkSelfPermission(Main_Activity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Main_Activity.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]
                                    {Manifest.permission.READ_PHONE_STATE}
                            , REQUEST_READ_PHONE_STATE);
                }
            } else {

                ActivityCompat.requestPermissions(Main_Activity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_READ_PHONE_STATE);
            }
        }*/
    }

    private void initViews() {

        mbtn_sign = (Button) findViewById(R.id.btn_signin);
        mbtn_signup = (Button) findViewById(R.id.btn_signup);
        mhelp_textview = (TextView) findViewById(R.id.helpful);
        typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Regular.ttf");
        mbtn_sign.setTypeface(typeface);
        mbtn_signup.setTypeface(typeface);
        mhelp_textview.setTypeface(typeface);

    }

    private void setListener() {

        mbtn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity.this, SignIn_Activity.class);
                startActivity(intent);

            }
        });

        mbtn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity.this, SignUp_Activity.class);
                startActivity(intent);

            }
        });

        mhelp_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Main_Activity.this, HelpFullActivity.class));

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {

        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // startAction();
                } else {
                    finish();
                }
                return;
            }

            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }

            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    QBSubscriptiondevice();
                }
                break;


            default:
                break;

        }
    }

    private void imageDialog() {
        dialog = new Dialog(Main_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Image");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.mobilenumber_popup);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        final EditText mediEditText = (EditText) dialog.findViewById(R.id.mobileno);
        meditcountcode = (EditText) dialog.findViewById(R.id.cntrycode);
        autoCompleteTextView = (EditText) dialog.findViewById(R.id.country);
        selectLanguage = (EditText) dialog.findViewById(R.id.selectLanguage);

         /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        String diallingCode = Function.getCountryCode(tm);
        Log.v(TAG, "CountryCode :- " + diallingCode);
        meditcountcode.setText("+" + diallingCode);

        String countryName = Function.getCountryFullName(tm);
        Log.v(TAG, "Country Name full :- " + countryName);
        autoCompleteTextView.setText(countryName);

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        Button mbutton = (Button) dialog.findViewById(R.id.btn_submit);

        selectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity.this, SelectLanguageActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity.this, CountryListActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissKeyboard(Main_Activity.this);
                Country = autoCompleteTextView.getText().toString();
                CounCode = meditcountcode.getText().toString();
                MobileNumber = mediEditText.getText().toString();
                Language = selectLanguage.getText().toString();

                if (Country.length() == 0) {
                    autoCompleteTextView.setError(getResources().getString(R.string.error_field_required));
                } else if (CounCode.length() == 0) {
                    meditcountcode.setError(getResources().getString(R.string.error_field_required));
                } else if (MobileNumber.length() < 8 && MobileNumber.length() > 12) {
                    mediEditText.setError("Please insert valid mobile number");
                } else {
                    new SignInfileAsynch().execute();
                }
            }
        });
        dialog.show();
    }

    public void popup() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Number already exixts. Please use another number !!!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    public void confirmpopup() {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You are already login in another device! Please verify otp")
                .setCancelText("No")
                .setConfirmText("Yes")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        new VryfyLogin().execute();
                        sDialog.cancel();
                    }
                })
                .show();

    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                autoCompleteTextView.setText(data.getStringExtra("countryname"));
                meditcountcode.setText(data.getStringExtra("countrycode"));

            } else if (requestCode == 2) {
                selectLanguage.setText(data.getStringExtra("language"));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(Main_Activity.this, HomeService.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(Main_Activity.this, HomeService.class));
    }

    class LoginTask extends AsyncTask<String, Void, String> {
        String status = "", result = "", loginId = "";
        String pass = "";
        Context context;


        public LoginTask(Context context) {
            this.context = context;
            mProgressDialog = new SpotsDialog(Main_Activity.this);
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

            if (params[0].equals("facebook")) {
                values.add(new BasicNameValuePair("user_social_id", params[1]));
                values.add(new BasicNameValuePair("user_name", params[2]));
                values.add(new BasicNameValuePair("password", ""));
                values.add(new BasicNameValuePair("email", params[3]));
                values.add(new BasicNameValuePair("profpic", params[4]));
                values.add(new BasicNameValuePair("method", AppConstants.LOGIN));
                values.add(new BasicNameValuePair("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken()));
                values.add(new BasicNameValuePair("mobile_uniquekey", Function.getAndroidID(Main_Activity.this)));
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
                        AppPreferences.setLoginId(Main_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));
                        AppPreferences.setSocialId(Main_Activity.this, jsonObject2.getString("social_id"));
                        // AppPreferences.setMobileuser(SignIn_Activity.this, jsonObject2.getString("country_with_mobile").replace(" ","-"));
                        AppPreferences.setPassword(Main_Activity.this, jsonObject2.getString("password"));
                        AppPreferences.setFirstUsername(Main_Activity.this, jsonObject2.getString("username"));
                        AppPreferences.setUserprofile(Main_Activity.this, jsonObject2.getString("userImage"));

                        System.out.println("userpic" + jsonObject2.getString("userImage"));
                        System.out.println("usermobile" + jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        System.out.println("userPassword" + jsonObject2.getString("password"));
                        AppPreferences.setEmail(Main_Activity.this, jsonObject2.getString("email"));

                        AppPreferences.setMobileuserWithoutCountry(Main_Activity.this, jsonObject2.getString("mobile").replace(" ", "").replace("+", ""));
                        AppPreferences.setMobileuser(Main_Activity.this, jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        AppPreferences.setUsercity(Main_Activity.this, jsonObject2.getString("country"));
                        AppPreferences.setCountrycode(Main_Activity.this, jsonObject2.getString("countrycode"));
                        AppPreferences.setUSERLANGUAGE(Main_Activity.this, jsonObject2.getString("language"));
                        AppPreferences.setUsergender(Main_Activity.this, jsonObject2.getString("gender"));
                        AppPreferences.setUserstatus(Main_Activity.this, jsonObject2.getString("userProfileStatus"));
                        AppPreferences.setBlockList(Main_Activity.this, jsonObject2.getString("block_users"));
                        AppPreferences.setPicprivacy(Main_Activity.this, jsonObject2.getString("profie_pic_privacy"));
                        AppPreferences.setStatusprivacy(Main_Activity.this, jsonObject2.getString("profie_status_privacy"));
                        //AppPreferences.setLoginStatus(SignIn_Activity.this, jsonObject2.getString("user_status"));
                        AppPreferences.setLoginStatus(Main_Activity.this, "1");
                        AppPreferences.setRegisterDate(Main_Activity.this, jsonObject2.getString("start_date"));
                        AppPreferences.setRegisterEndDate(Main_Activity.this, jsonObject2.getString("end_date"));
                        AppPreferences.setRemainingDays(Main_Activity.this, jsonObject2.getString("reamning_days"));
                        //   System.out.println("userenddate" + jsonObject2.getString("end_date"));
                        User user = new User();
                        user.setName(jsonObject2.getString("username"));
                        user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        user.setPassword(jsonObject2.getString("password"));
                        DatabaseHelper.getInstance(Main_Activity.this).insertUser(user);
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
//                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(Main_Activity.this, TwoTab_Activity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("300")) {

                imageDialog();
                Log.d("data>>>", status + "   " + loginId);

            } else if (status.equals("400")) {
                Snackbar.make(findViewById(android.R.id.content), "Wrong Password !!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else if (status.equals("600")) {
                System.out.println("loginid1" + AppPreferences.getLoginId(context));
//                startService(new Intent(getBaseContext(), MyService.class));

//                Intent mintent_home = new Intent(SignIn_Activity.this, AlertSpeakameActivity.class);
                Intent mintent_home = new Intent(Main_Activity.this, AlertDaysSpeakameActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("700")) {
                System.out.println("loginid1" + AppPreferences.getLoginId(context));
//                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(Main_Activity.this, AlertDaysSpeakameActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("800")) {

                confirmpopup();

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Some Error occured !!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    private class SignInfileAsynch extends AsyncTask<Void, Void, String> {
        String status = "", result = "", loginId = "";

        private JSONObject jsonObj;

        public SignInfileAsynch() {

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(Main_Activity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("verify_successfully : ------------ "
                        + AppConstants.REGISTER_LOG);
                HttpPost httppost = new HttpPost(AppConstants.REGISTER_LOG);
                jsonObj = new JSONObject();

                jsonObj.put("method", "checkNumber");
                jsonObj.put("mobile_number", MobileNumber);
                jsonObj.put("user_fb_id", AppPreferences.getSocialId(Main_Activity.this));
                jsonObj.put("language", Language);
                jsonObj.put("country", Country);
                jsonObj.put("countrycode", CounCode);
                jsonObj.put("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken());
                jsonObj.put("mobile_uniquekey", Function.getAndroidID(Main_Activity.this));


                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("jsonValue", jsonArray.toString());
                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString());


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Request JSON is : " + jsonArray.toString());
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
                    JSONObject jsonObj = new JSONObject(jsonString);
                    status = jsonObj.getString("status");
                    System.out.println("jsonstring------" + jsonString);
                    if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                        JSONArray resultArray = jsonObj.getJSONArray("result");
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject2 = resultArray.getJSONObject(i);
                            loginId = jsonObject2.getString("userId");

                            AppPreferences.setLoginId(Main_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));
                            AppPreferences.setVerifytype(Main_Activity.this, Integer.parseInt(jsonObject2.getString("veryfymobile")));
                            AppPreferences.setSocialId(Main_Activity.this, jsonObject2.getString("social_id"));
                            AppPreferences.setMobileuser(Main_Activity.this, jsonObject2.getString("mobile"));
                            AppPreferences.setPassword(Main_Activity.this, jsonObject2.getString("password"));
                            AppPreferences.setFirstUsername(Main_Activity.this, jsonObject2.getString("username"));
                            AppPreferences.setUserprofile(Main_Activity.this, jsonObject2.getString("userImage"));
                            AppPreferences.setEmail(Main_Activity.this, jsonObject2.getString("email"));
                            AppPreferences.setUsercity(Main_Activity.this, jsonObject2.getString("country"));
                            AppPreferences.setCountrycode(Main_Activity.this, jsonObject2.getString("countrycode"));
                            AppPreferences.setUSERLANGUAGE(Main_Activity.this, jsonObject2.getString("language"));
                            AppPreferences.setUsergender(Main_Activity.this, jsonObject2.getString("gender"));
                            AppPreferences.setUserstatus(Main_Activity.this, jsonObject2.getString("userProfileStatus"));

                            User user = new User();
                            user.setName(jsonObject2.getString("username"));
                            user.setMobile(jsonObject2.getString("mobile"));
                            user.setPassword(jsonObject2.getString("mobile"));
                            System.out.println("checkmobilepaas2" + jsonObject2.getString("mobile"));
                            System.out.println("checksocialid" + jsonObject2.getString("social_id"));

                            DatabaseHelper.getInstance(Main_Activity.this).insertUser(user);
                        }
                    } else if (jsonObj.getString("status").equalsIgnoreCase("400")) {
                        status = "400";
                        Snackbar.make(findViewById(android.R.id.content), "Number already exist !!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else if (jsonObj.getString("status").equalsIgnoreCase("500")) {
                        status = "500";
                    } else if (jsonObj.getString("status").equalsIgnoreCase("600")) {

                        status = "600";
                    }
                }

            } catch (ConnectTimeoutException e) {

            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.d("status", status + "");
            if (status.equals("200") && !loginId.equals("")) {
                dialog.dismiss();

                /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code commented by Pragya for removing XMPP register ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

                /*

                new AddmemberAsynch().execute();
                startService(new Intent(getBaseContext(), MyService.class));

                */

                /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code commented by Pragya for removing XMPP register ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */


                System.out.println("loginid1" + AppPreferences.getLoginId(Main_Activity.this));
//                startService(new Intent(getBaseContext(), MyService.class));
                Intent mintent_home = new Intent(Main_Activity.this, Verify_numberActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("400")) {
                status = "400";
                popup();
//                Snackbar.make(findViewById(android.R.id.content), "Number is already exist", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        }

    }

    private class VryfyLogin extends AsyncTask<Void, Void, String> {
        String jsonString = "";
        private AlertDialog mProgressDialog;
        private JSONObject jsonObj;
        private String status;

        public VryfyLogin() {

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(Main_Activity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("successfully : ------------ "
                        + AppConstants.DEMOCOMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.DEMOCOMMONURL);
                jsonObj = new JSONObject();

                jsonObj.put("method", "checkLoginNewDevice");
                jsonObj.put("user_mobile", MobileNumber);
                jsonObj.put("countrycode", countryCode);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("jsonvalue", jsonArray.toString());
                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Request JSON is : " + jsonArray.toString());
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

                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("JSONString response is : " + jsonString);
                if (jsonString != null) {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    status = jsonObj.getString("status");
                    System.out.println("jsonstring------" + jsonString);
                    if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                        status = "200";
                        JSONArray resultArray = jsonObj.getJSONArray("result");
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject2 = resultArray.getJSONObject(i);
                            AppPreferences.setMobileuser(Main_Activity.this, jsonObject2.getString("mobile"));
                        }
                    } else if (jsonObj.getString("status").equalsIgnoreCase("400")) {
                        status = "400";
                    } else if (jsonObj.getString("status").equalsIgnoreCase("500")) {
                        status = "500";
                    } else if (jsonObj.getString("status").equalsIgnoreCase("600")) {
                        status = "600";
                    }
                }

            } catch (ConnectTimeoutException e) {

            } catch (SocketTimeoutException e) {

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.d("SignIn_Activity", "inside 200 status :- " + status + "\n Result is :- " + result);

            if (result.contains("200")) {

                Intent intent = new Intent(Main_Activity.this, ConfirmLoginOtp.class);
                intent.setAction("");
                startActivity(intent);
                finish();

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Check network connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

    }
}
