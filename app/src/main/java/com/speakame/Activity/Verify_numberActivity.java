package com.speakame.Activity;

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

import com.speakame.Beans.User;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Xmpp.MyService;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class Verify_numberActivity extends AnimRootActivity {
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
    private AlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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


        mbtn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReciver, new IntentFilter("MSGINTENT"));
    }

    @Override
    public void onBackPressed() {

    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
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
                jsonObj.put("mobile", AppPreferences.getMobileuser(Verify_numberActivity.this));

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());

                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");

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
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);

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
                Snackbar.make(findViewById(android.R.id.content), "Cheack network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();


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
                HttpPost httppost = new HttpPost(AppConstants.COMMONURL);


                jsonObj = new JSONObject();
                jsonObj.put("method", "otp");
                jsonObj.put("userOtp", OTPNUMBER);
                jsonObj.put("mobile", AppPreferences.getMobileuser(Verify_numberActivity.this));

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());

                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");

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
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);

                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out.println("--------- message 200 got ----------");
                            JSONArray jsonArray1 = jsonObj.getJSONArray("result");
                            for (int i = 0; jsonArray1.length() > i; i++) {

                                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                String user_id = jsonObject2.getString("userId");
//                        System.out.println("user_id" + user_id);

                                AppPreferences.setLoginId(Verify_numberActivity.this, Integer.parseInt(jsonObject2.getString("userId")));
                                AppPreferences.setMobileuser(Verify_numberActivity.this, jsonObject2.getString("mobile"));
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
                            startService(new Intent(getBaseContext(), MyService.class));

                            Intent mintent_home = new Intent(Verify_numberActivity.this, AlertDaysSpeakameActivity.class);
                            mintent_home.setAction("");
                            startActivity(mintent_home);
                            finish();

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
                Toast.makeText(Verify_numberActivity.this, "Verify successfully", Toast.LENGTH_LONG).show();//fa

                Intent intent = new Intent(Verify_numberActivity.this, AlertSpeakameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();

            } else {

                Snackbar.make(findViewById(android.R.id.content), "Cheack network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();

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

                jsonObj.put("method", "fb_otp");
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
                            JSONObject jsonObject2 = resultArray.getJSONObject(i);
                            loginId = jsonObject2.getString("userId");
                            AppPreferences.setLoginId(Verify_numberActivity.this, Integer.parseInt(jsonObject2.getString("userId")));
                            AppPreferences.setVerifytype(Verify_numberActivity.this, Integer.parseInt(jsonObject2.getString("veryfymobile")));
                            AppPreferences.setSocialId(Verify_numberActivity.this, jsonObject2.getString("social_id"));
                            AppPreferences.setMobileuser(Verify_numberActivity.this, jsonObject2.getString("mobile"));
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
                            user.setMobile(jsonObject2.getString("mobile"));
                            user.setPassword(jsonObject2.getString("password"));

                            DatabaseHelper.getInstance(Verify_numberActivity.this).insertUser(user);

                            new AddmemberAsynch().execute(jsonObject2.getString("mobile"), jsonObject2.getString("password"), jsonObject2.getString("username"), jsonObject2.getString("email"));


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

                Toast.makeText(Verify_numberActivity.this, "Verify successfully", Toast.LENGTH_LONG).show();//fa

                Intent intent = new Intent(Verify_numberActivity.this, AlertSpeakameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();

            } else if (status.equals("400")) {
                status = "400";
                Snackbar.make(findViewById(android.R.id.content), "Wrong otp", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

    }


    private class AddmemberAsynch extends AsyncTask<String, Void, String> {
        ArrayList<Integer> catogariesid;
        private ProgressDialog mProgressDialog;
        private JSONObject jsonObj;
        private int status = 0;

        public AddmemberAsynch() {

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(Verify_numberActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("registartion value : ------------ "
                        + AppConstants.XMPPURL);
                HttpPost httppost = new HttpPost(AppConstants.XMPPURL);
                jsonObj = new JSONObject();

                jsonObj.put("username", params[0]);
                jsonObj.put("password", params[0]);
                jsonObj.put("name", params[2]);
                jsonObj.put("email", params[3]);


//                JSONArray jsonArray = new JSONArray();
//                jsonArray.put(jsonObj);

                Log.d("json Data", jsonObj.toString());

                httppost.setHeader("Authorization", "speakme");
                httppost.setHeader("Content-type", "application/json");
                StringEntity se = null;
                try {
                    se = new StringEntity(jsonObj.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonObj.toString(2));
                System.out.println("Sent JSON is : " + jsonObj.toString());
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
            Toast.makeText(Verify_numberActivity.this, "Verify successfully", Toast.LENGTH_LONG).show();//fa

            Intent intent = new Intent(Verify_numberActivity.this, AlertSpeakameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();

        }

    }
}
