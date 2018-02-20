package com.speakameqb.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.speakameqb.Beans.User;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.R;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.UnsupportedCharsetException;

import dmax.dialog.SpotsDialog;

public class Verify_numberActivity extends AnimRootActivity {
    private static final String TAG = "Verify_numberActivity";
    TextView headtext, mtxt1, mtxt2, mtxt3, resendtext;
    EditText medit_password;
    Button mbtn_submit;
    Typeface typeface, typeface1;
    String OTPNUMBER;
    BroadcastReceiver mMessageReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            medit_password.setText(intent.getStringExtra("msg"));
            OTPNUMBER = medit_password.getText().toString();
            if (OTPNUMBER.length() == 6) {
                mbtn_submit.performClick();
//               new MobileOtpTask(otpnumber).execute();
            } else {

            }
        }
    };
    ProgressDialog dialog;
    String email, mobile, mobileWithoutCountryCode, full_name;
    QBChatService chatService;
    private AlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getAction().equalsIgnoreCase("SignUp")) {
            full_name = getIntent().getExtras().getString("full_name");
            email = getIntent().getExtras().getString("email");
            mobile = getIntent().getExtras().getString("mobile");
            mobileWithoutCountryCode = getIntent().getExtras().getString("mobilewithoutcountryCode");
        }

        Log.v(TAG, "Mobile with cCode :- " + mobile + " " + email + " " + full_name);
        Log.v(TAG, "Mobile mobileWithoutCountryCode cCode :- " + mobileWithoutCountryCode);

        initViews();
        setListener();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReciver, new IntentFilter("MSGINTENT"));

    }

    private void initViews() {

        headtext = (TextView) findViewById(R.id.headtext);
        mtxt1 = (TextView) findViewById(R.id.textView1);
        mtxt2 = (TextView) findViewById(R.id.textView2);
        mtxt3 = (TextView) findViewById(R.id.textView3);
        resendtext = (TextView) findViewById(R.id.txtresend);
        medit_password = (EditText) findViewById(R.id.forgotpassword);
        mbtn_submit = (Button) findViewById(R.id.btn_submit);
        headtext.setText("Verify");
        typeface = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        headtext.setTypeface(typeface);
        typeface1 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mtxt1.setTypeface(typeface1);
        mtxt2.setTypeface(typeface1);
        mtxt3.setTypeface(typeface1);
        resendtext.setTypeface(typeface1);
        medit_password.setTypeface(typeface1);
        mbtn_submit.setTypeface(typeface1);


    }

    private void setListener() {


        mbtn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v(TAG, "Social id :- " + AppPreferences.getSocialId(Verify_numberActivity.this));

                dismissKeyboard(Verify_numberActivity.this);
                OTPNUMBER = medit_password.getText().toString();
                if (OTPNUMBER.length() == 0) {
                    medit_password.setError(getResources().getString(R.string.error_field_required));

                } else if (AppPreferences.getSocialId(Verify_numberActivity.this).equalsIgnoreCase("")) {

                    new ConfirmOTpTask().execute();

                } else {

                    new SignInfilefbAsynch().execute();

                }

//                Intent intent = new Intent(Verify_numberActivity.this, MainScreenActivity.class);
//                startActivity(intent);

            }
        });

        resendtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResendOtpTask().execute();

            }
        });

    }

    private void registerUserToQuickBlox(String name, String mobile_no, String pwd, String email) {
        chatService = QBChatService.getInstance();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final QBUser user = new QBUser();

        user.setFullName(name);
        user.setPhone(mobile_no);
        user.setPassword("12345678");
        user.setEmail(email);
        user.setLogin(mobile_no);

//        registerSync(user); // Synchronous way:
        registerAsync(user); // Asynchronous way:
//        registerBefore_sdk(user);
    }

    private void registerAsync(QBUser user) {

//        dialog = new ProgressDialog(Verify_numberActivity.this);
//        dialog.setMessage("Please wait...");
//        dialog.show();

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Asynchronus ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

        QBUsers.signUp(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
//                dialog.dismiss();
                String login = qbUser.getLogin();
                String pwd = qbUser.getPassword();

                Log.v(TAG, "Login QuickBlox:-  " + login);
                Log.v(TAG, "Pwd QuickBlox:-  " + pwd);
                Log.v(TAG, "User id after signup QuickBlox:-  " + qbUser.getId());
                Log.v(TAG, "User created to QuickBlox");

                new ConfirmOTpTask().execute();

                Snackbar.make(findViewById(android.R.id.content), "User sign up done QuickBlox", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                textViewlogin.setVisibility(View.VISIBLE);
//                loginLinearLayout.setVisibility(View.VISIBLE);
//                registerLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Sign up failed QuickBlox .." + e.getMessage());
//                dialog.dismiss();
                String message = e.getMessage();
                Snackbar.make(findViewById(android.R.id.content), "User sign up failed QuickBlox" + message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Asynchronus ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeAllUserData(AppPreferences.getQBUserId(Verify_numberActivity.this));

    }


    private void removeAllUserData(int QBUserId) {

        Log.v(TAG, "QuickBlox id :- " + AppPreferences.getQBUserId(Verify_numberActivity.this));
      /*  requestExecutor.deleteCurrentUser(QBUserId, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                Log.v(TAG, "User deleted succesfully");

            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error deleted succesfully");
                Log.v(TAG, "Error message :- " + e.getMessage());

            }
        });
*/

        QBUsers.deleteUser(QBUserId).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.v(TAG, "User deleted succesfully");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.v(TAG, "Error deleted succesfully");
                Log.v(TAG, "Error message :- " + e.getMessage());
            }
        });
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    private void loginUserToQuickBlox(String mobile_no, String pwd) {

//        QBSettings.getInstance().fastConfigInit(MyApplication.APP_ID, MyApplication.AUTH_KEY, MyApplication.AUTH_SECRET);

        Log.v(TAG, " ~~~~~~~~~~~~ Inside Login Button ~~~~~~~~~~~~ ");
        Log.v(TAG, "Login :-  " + mobile_no);
        Log.v(TAG, "Pwd :-  " + pwd);

        final QBUser user = new QBUser();
        user.setLogin(mobile_no);
        user.setPassword(pwd);

//        dialog = new ProgressDialog(Verify_numberActivity.this);
//        dialog.setMessage("Please wait...");
//        dialog.show();

        loginAsync(user, dialog); // Asynchronous way:
//        loginSync(user, dialog);  // Synchronous way

    }

    private void loginAsync(QBUser user, final ProgressDialog dialog) {

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
//                dialog.dismiss();
                Log.v(TAG, "Login Sucessfully");
                Log.v(TAG, "Bundle data :- " + bundle.toString());

                Snackbar.make(findViewById(android.R.id.content), "User Login Sucessfully.", Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Login failed .." + e.getMessage());
//                dialog.dismiss();
                String message = e.getMessage();
                Snackbar.make(findViewById(android.R.id.content), "Login failed due to " + message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private class ResendOtpTask extends AsyncTask<Void, Void, String> {
        private AlertDialog mProgressDialog;
        private JSONObject jsonObj;
        private int status = 0;

        public ResendOtpTask() {
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(Verify_numberActivity.this);
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
                System.out.println("resendvalue value : ------------ "
                        + AppConstants.COMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.COMMONURL);

                jsonObj = new JSONObject();
                jsonObj.put("method", "requestOtp");
                jsonObj.put("mobile", mobileWithoutCountryCode);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());

                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }

                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Sent JSON is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;

                response = httpclient.execute(httppost);

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
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
                Log.d(TAG, "JSONString response is  :- " + jsonString);
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        Log.v(TAG, "JSONString response is  :- " + jsonObj);

                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out.println("--------- message 200 got ----------");
                            JSONArray jsonArray1 = jsonObj.getJSONArray("result");


                            status = 200;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("400")) {
                            status = 400;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("800")) {
                            status = 800;
                            return jsonString;
                        }

                    }

                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
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
            if (status == 200) {

                Toast.makeText(Verify_numberActivity.this, "Resend OTP", Toast.LENGTH_LONG).show();//fa

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Check network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();


            }
        }

    }

    private class ConfirmOTpTask extends AsyncTask<Void, Void, String> {
        private JSONObject jsonObj;
        private int status = 0;

        public ConfirmOTpTask() {
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(Verify_numberActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(true);
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
                System.out.println("resendvalue value : ------------ "
                        + AppConstants.COMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.REGISTER_LOG);
                Log.v(TAG, "URl :-" + AppConstants.REGISTER_LOG);

                jsonObj = new JSONObject();
                jsonObj.put("method", AppConstants.OTP);
                jsonObj.put("mobile_type", "AN");
                jsonObj.put("userOtp", OTPNUMBER);
                jsonObj.put("mobile", AppPreferences.getMobileuser(Verify_numberActivity.this));

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d(TAG, "json Request:---------" + jsonArray.toString());

                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }

                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Sent JSON is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;

                response = httpclient.execute(httppost);

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
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
                System.out.println("Response:-" + jsonString);
                Log.d(TAG, "json Response:---------" + jsonString);
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);

                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out.println("--------- message 200 got ----------");
                            JSONArray jsonArray1 = jsonObj.getJSONArray("result");
                            for (int i = 0; jsonArray1.length() > i; i++) {

                                final JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                String user_id = jsonObject2.getString("userId");
//                        System.out.println("user_id" + user_id);

                                AppPreferences.setLoginId(Verify_numberActivity.this, Integer.parseInt(jsonObject2.getString("userId")));
                                AppPreferences.setMobileuser(Verify_numberActivity.this, jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                                AppPreferences.setPassword(Verify_numberActivity.this, jsonObject2.getString("password"));
                                AppPreferences.setFirstUsername(Verify_numberActivity.this, jsonObject2.getString("username"));
                                AppPreferences.setUserprofile(Verify_numberActivity.this, jsonObject2.getString("userImage"));
                                AppPreferences.setEmail(Verify_numberActivity.this, jsonObject2.getString("email"));
                                AppPreferences.setUsercity(Verify_numberActivity.this, jsonObject2.getString("country"));
                                AppPreferences.setCountrycode(Verify_numberActivity.this, jsonObject2.getString("countrycode"));
                                AppPreferences.setUSERLANGUAGE(Verify_numberActivity.this, jsonObject2.getString("language"));
                                AppPreferences.setUsergender(Verify_numberActivity.this, jsonObject2.getString("gender"));
                                AppPreferences.setRemainingDays(Verify_numberActivity.this, jsonObject2.getString("reamning_days"));
                                AppPreferences.setRegisterDate(Verify_numberActivity.this, jsonObject2.getString("start_date"));
                                AppPreferences.setRegisterEndDate(Verify_numberActivity.this, jsonObject2.getString("end_date"));
                                AppPreferences.setLoginStatus(Verify_numberActivity.this, jsonObject2.getString("user_status"));

                                User user = new User();
                                user.setName(jsonObject2.getString("username"));
                                user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                                user.setPassword(jsonObject2.getString("password"));

                                DatabaseHelper.getInstance(Verify_numberActivity.this).insertUser(user);


                            }

                            status = 200;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("400")) {
                            status = 400;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("500")) {

                            System.out.println("loginid1" + AppPreferences.getLoginId(Verify_numberActivity.this));


                           /* Intent mintent_home = new Intent(Verify_numberActivity.this, AlertDaysSpeakameActivity.class);
                            mintent_home.setAction("");
                            startActivity(mintent_home);
                            finish();
*/

                            JSONArray jsonArray1 = jsonObj.getJSONArray("result");
                            for (int i = 0; jsonArray1.length() > i; i++) {

                                final JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                String user_id = jsonObject2.getString("userId");
//                        System.out.println("user_id" + user_id);

                                AppPreferences.setLoginId(Verify_numberActivity.this, Integer.parseInt(jsonObject2.getString("userId")));
                                AppPreferences.setMobileuser(Verify_numberActivity.this, jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                                AppPreferences.setPassword(Verify_numberActivity.this, jsonObject2.getString("password"));
                                AppPreferences.setFirstUsername(Verify_numberActivity.this, jsonObject2.getString("username"));
                                AppPreferences.setUserprofile(Verify_numberActivity.this, jsonObject2.getString("userImage"));
                                AppPreferences.setEmail(Verify_numberActivity.this, jsonObject2.getString("email"));
                                AppPreferences.setUsercity(Verify_numberActivity.this, jsonObject2.getString("country"));
                                AppPreferences.setCountrycode(Verify_numberActivity.this, jsonObject2.getString("countrycode"));
                                AppPreferences.setUSERLANGUAGE(Verify_numberActivity.this, jsonObject2.getString("language"));
                                AppPreferences.setUsergender(Verify_numberActivity.this, jsonObject2.getString("gender"));
                                AppPreferences.setRemainingDays(Verify_numberActivity.this, jsonObject2.getString("reamning_days"));
                                AppPreferences.setRegisterDate(Verify_numberActivity.this, jsonObject2.getString("start_date"));
                                AppPreferences.setRegisterEndDate(Verify_numberActivity.this, jsonObject2.getString("end_date"));
                                AppPreferences.setLoginStatus(Verify_numberActivity.this, jsonObject2.getString("user_status"));

                                Log.v(TAG, "mobile no" + jsonObject2.getString("mobile"));
                                Log.v(TAG, "mobile no 1" + jsonObject2.getString("country_with_mobile"));
                                Log.v(TAG, "mobile no from sgn up activity" + mobile);

                                AppPreferences.setQB_LoginId(Verify_numberActivity.this, mobile);


                                User user = new User();
                                user.setName(jsonObject2.getString("username"));
                                user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                                user.setPassword(jsonObject2.getString("password"));

                                DatabaseHelper.getInstance(Verify_numberActivity.this).insertUser(user);

                            }
                            status = 500;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("800")) {
                            status = 800;
                            return jsonString;
                        }
                    }
                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
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
            if (status == 200) {

                loginUserToQuickBlox(AppPreferences.getQB_LoginId(Verify_numberActivity.this), "12345678");

                Intent intent = new Intent(Verify_numberActivity.this, AlertDaysSpeakameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();

                Toast.makeText(Verify_numberActivity.this, "Verify successfully", Toast.LENGTH_LONG).show();//fa

            } else if (status == 400) {
                Toast.makeText(Verify_numberActivity.this, "Otp doesnot match", Toast.LENGTH_LONG).show();//fa
            } else if (status == 500) {
//                Toast.makeText(Verify_numberActivity.this, "Otp doesnot match", Toast.LENGTH_LONG).show();//fa
                loginUserToQuickBlox(AppPreferences.getQB_LoginId(Verify_numberActivity.this), "12345678");

                Intent intent = new Intent(Verify_numberActivity.this, AlertDaysSpeakameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    private class SignInfilefbAsynch extends AsyncTask<Void, Void, String> {
        String status = "", result = "", loginId = "";

        private JSONObject jsonObj;

        public SignInfilefbAsynch() {

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(Verify_numberActivity.this);
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
                System.out.println("Edit profile successfully : ------------ "
                        + AppConstants.COMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.COMMONURL);
                jsonObj = new JSONObject();

                jsonObj.put("method", AppConstants.FB_OTP);
                jsonObj.put("userOtp", OTPNUMBER);
                jsonObj.put("mobile", AppPreferences.getMobileuser(Verify_numberActivity.this));


                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());
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
                            final JSONObject jsonObject2 = resultArray.getJSONObject(i);
                            loginId = jsonObject2.getString("userId");

                            AppPreferences.setLoginId(Verify_numberActivity.this, Integer.parseInt(jsonObject2.getString("userId")));
                            AppPreferences.setVerifytype(Verify_numberActivity.this, Integer.parseInt(jsonObject2.getString("veryfymobile")));
                            AppPreferences.setSocialId(Verify_numberActivity.this, jsonObject2.getString("social_id"));
                            AppPreferences.setMobileuser(Verify_numberActivity.this, jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                            AppPreferences.setPassword(Verify_numberActivity.this, jsonObject2.getString("password"));
                            AppPreferences.setFirstUsername(Verify_numberActivity.this, jsonObject2.getString("username"));
                            AppPreferences.setUserprofile(Verify_numberActivity.this, jsonObject2.getString("userImage"));
                            AppPreferences.setEmail(Verify_numberActivity.this, jsonObject2.getString("email"));
                            AppPreferences.setUsercity(Verify_numberActivity.this, jsonObject2.getString("country"));
                            AppPreferences.setCountrycode(Verify_numberActivity.this, jsonObject2.getString("countrycode"));
                            AppPreferences.setUSERLANGUAGE(Verify_numberActivity.this, jsonObject2.getString("language"));
                            AppPreferences.setUsergender(Verify_numberActivity.this, jsonObject2.getString("gender"));
                            AppPreferences.setUserstatus(Verify_numberActivity.this, jsonObject2.getString("userProfileStatus"));

                            User user = new User();
                            user.setName(jsonObject2.getString("username"));
                            user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                            user.setPassword(jsonObject2.getString("password"));

                            DatabaseHelper.getInstance(Verify_numberActivity.this).insertUser(user);

//                            registerUserToQuickBlox(full_name, mobile, "12345678", email);
/*

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            }).start();

                            //new AddmemberAsynch().execute(jsonObject2.getString("mobile"), jsonObject2.getString("password"), jsonObject2.getString("username"), jsonObject2.getString("email"));

*/
                        }
                    } else if (jsonObj.getString("status").equalsIgnoreCase("400")) {
                        status = "400";
                        Snackbar.make(findViewById(android.R.id.content), "Number is already exist", Snackbar.LENGTH_LONG)
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
                Log.d(TAG, "Fb otp status :-- " + status);

                Toast.makeText(Verify_numberActivity.this, "Inside fb otp Verify successfully", Toast.LENGTH_LONG).show();//fa

              /*  Intent intent = new Intent(Verify_numberActivity.this, AlertDaysSpeakameActivity.class);  // older
//                Intent intent = new Intent(Verify_numberActivity.this, AlertDaysSpeakameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();*/

            } else if (status.equals("400")) {
                status = "400";
                Snackbar.make(findViewById(android.R.id.content), "Wrong otp", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

}
