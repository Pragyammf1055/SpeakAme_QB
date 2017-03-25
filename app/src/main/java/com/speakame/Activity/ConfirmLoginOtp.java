package com.speakame.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.speakame.utils.Function;

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

import dmax.dialog.SpotsDialog;

public class ConfirmLoginOtp extends AnimRootActivity {
    TextView headtext, mtxt1, mtxt2, mtxt3, resendtext;
    EditText medit_password;
    Button mbtn_submit;
    Typeface typeface, typeface1;
    String OTPNUMBER;
    private AlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_login_otp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        headtext = (TextView) findViewById(R.id.headtext);
        headtext = (TextView) findViewById(R.id.headtext);
        mtxt1 = (TextView) findViewById(R.id.textView1);
        mtxt2 = (TextView) findViewById(R.id.textView2);
        mtxt3 = (TextView) findViewById(R.id.textView3);
        resendtext = (TextView) findViewById(R.id.txtresend);
        medit_password = (EditText) findViewById(R.id.veryfyotpedittext);
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

                dismissKeyboard(ConfirmLoginOtp.this);
                OTPNUMBER = medit_password.getText().toString();
                if (OTPNUMBER.length() == 0) {
                    medit_password.setError(getResources().getString(R.string.error_field_required));

                } else {
                    new ConfirmOTpTask().execute();
                }


            }
        });

        resendtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResendOtpTask().execute();

            }
        });


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
            mProgressDialog = new SpotsDialog(ConfirmLoginOtp.this);
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
                        + AppConstants.DEMOCOMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.DEMOCOMMONURL);


                jsonObj = new JSONObject();
                jsonObj.put("method", "requestOtp");
                jsonObj.put("mobile", AppPreferences.getMobileuser(ConfirmLoginOtp.this));

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
                System.out.println("JSONStringresponse is : " + jsonString);
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

                Toast.makeText(ConfirmLoginOtp.this, "Resend OTP", Toast.LENGTH_LONG).show();//fa

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Cheack network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();


            }
        }

    }

    private class ConfirmOTpTask extends AsyncTask<Void, Void, String> {
        String status = "", result = "", loginId = "";

        private JSONObject jsonObj;

        public ConfirmOTpTask() {

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(ConfirmLoginOtp.this);
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
                System.out.println("confirmsuccessfully : ------------"
                        + AppConstants.DEMOCOMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.DEMOCOMMONURL);
                jsonObj = new JSONObject();

                jsonObj.put("method", "checkNewDeviceOtp");
                jsonObj.put("otp", OTPNUMBER);
                jsonObj.put("user_mobile", AppPreferences.getMobileuser(ConfirmLoginOtp.this));
                jsonObj.put("mobile_uniquekey", Function.getAndroidID(ConfirmLoginOtp.this));


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
                System.out.println("JSONStringresponseis" + jsonString);
                if (jsonString != null) {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    status = jsonObj.getString("status");
                    System.out.println("jsonstring------" + jsonString);
                    if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                        JSONArray resultArray = jsonObj.getJSONArray("result");
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject2 = resultArray.getJSONObject(i);
                            loginId = jsonObject2.getString("userId");
                            AppPreferences.setLoginId(ConfirmLoginOtp.this, Integer.parseInt(jsonObject2.getString("userId")));
                            AppPreferences.setSocialId(ConfirmLoginOtp.this, jsonObject2.getString("social_id"));
                            AppPreferences.setMobileuser(ConfirmLoginOtp.this, jsonObject2.getString("mobile"));
                            AppPreferences.setPassword(ConfirmLoginOtp.this, jsonObject2.getString("password"));
                            AppPreferences.setFirstUsername(ConfirmLoginOtp.this, jsonObject2.getString("username"));
                            AppPreferences.setUserprofile(ConfirmLoginOtp.this, jsonObject2.getString("userImage"));

                            System.out.println("userpic" + jsonObject2.getString("userImage"));
                            System.out.println("usermobile" + jsonObject2.getString("mobile"));
                            System.out.println("userPassword" + jsonObject2.getString("password"));
                            AppPreferences.setEmail(ConfirmLoginOtp.this, jsonObject2.getString("email"));
                            AppPreferences.setUsercity(ConfirmLoginOtp.this, jsonObject2.getString("country"));
                            AppPreferences.setCountrycode(ConfirmLoginOtp.this, jsonObject2.getString("countrycode"));
                            AppPreferences.setUSERLANGUAGE(ConfirmLoginOtp.this, jsonObject2.getString("language"));
                            AppPreferences.setUsergender(ConfirmLoginOtp.this, jsonObject2.getString("gender"));
                            AppPreferences.setUserstatus(ConfirmLoginOtp.this, jsonObject2.getString("userProfileStatus"));
                            AppPreferences.setBlockList(ConfirmLoginOtp.this, jsonObject2.getString("block_users"));
                            AppPreferences.setPicprivacy(ConfirmLoginOtp.this, jsonObject2.getString("profie_pic_privacy"));
                            AppPreferences.setStatusprivacy(ConfirmLoginOtp.this, jsonObject2.getString("profie_status_privacy"));
                            AppPreferences.setRemainingDays(ConfirmLoginOtp.this, jsonObject2.getString("reamning_days"));
                            AppPreferences.setRegisterDate(ConfirmLoginOtp.this, jsonObject2.getString("start_date"));
                            AppPreferences.setRegisterEndDate(ConfirmLoginOtp.this, jsonObject2.getString("end_date"));
                            AppPreferences.setLoginStatus(ConfirmLoginOtp.this, jsonObject2.getString("user_status"));
                            User user = new User();
                            user.setName(jsonObject2.getString("username"));
                            user.setMobile(jsonObject2.getString("mobile"));
                            user.setPassword(jsonObject2.getString("password"));

                            DatabaseHelper.getInstance(ConfirmLoginOtp.this).insertUser(user);
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

                System.out.println("loginid1" + AppPreferences.getLoginId(ConfirmLoginOtp.this));
                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(ConfirmLoginOtp.this, AlertDaysSpeakameActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("400")) {
                status = "400";
                Snackbar.make(findViewById(android.R.id.content), "Wrong otp", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

    }
}
