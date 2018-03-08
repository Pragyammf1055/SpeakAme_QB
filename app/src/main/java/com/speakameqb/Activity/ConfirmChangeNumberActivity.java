package com.speakameqb.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.speakameqb.utils.Function;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ConfirmChangeNumberActivity extends AnimRootActivity {
    final static String TAG = "ConfirmChangeNumberAct";
    TextView toolbartext, textviewhead, textsmrytextone, textsmrytexttwo;
    EditText medit_otp;
    Button mbtn_submit;

    String NewNumber, OtpNumber, countrycode;
    JSONArray orderArray;
    int i = 0;
    int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_change_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Change Number");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

        textviewhead = (TextView) findViewById(R.id.textView1);
        textsmrytextone = (TextView) findViewById(R.id.textView2);
        textsmrytexttwo = (TextView) findViewById(R.id.textView3);
        medit_otp = (EditText) findViewById(R.id.otpnumber);
        mbtn_submit = (Button) findViewById(R.id.btn_submit);
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        textviewhead.setTypeface(tf2);
        textsmrytextone.setTypeface(tf2);
        textsmrytexttwo.setTypeface(tf2);
        medit_otp.setTypeface(tf2);
        mbtn_submit.setTypeface(tf2);

        Intent intent = getIntent();
        NewNumber = intent.getStringExtra("newnumber");
        countrycode = intent.getStringExtra("countrycode");
        textviewhead.setText("Verify" + " " + countrycode + NewNumber);

        mbtn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtpNumber = medit_otp.getText().toString();
                dismissKeyboard(ConfirmChangeNumberActivity.this);
                if (OtpNumber.length() == 0) {
                    mbtn_submit.setError(getResources().getString(R.string.error_field_required));

                } else {
//                    sendOtpChangenumber(OtpNumber);
                    try {
                        i = ((Number) NumberFormat.getInstance().parse(NewNumber)).intValue();
                        //j = Integer.parseInt(NewNumber);
                        // Log.v(TAG, "i............ " + i);
                        // Log.v(TAG, "j............ " + j);
                        sendOtpChangenumber(OtpNumber);
                        Log.v(TAG, "getQB_loginID :--- " + AppPreferences.getQB_LoginId(ConfirmChangeNumberActivity.this));
                        Log.v(TAG, "getQBuserID :--- " + String.valueOf(AppPreferences.getQBUserId(ConfirmChangeNumberActivity.this)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }


            }
        });


    }

    private void sendOtpChangenumber(String Otpnumber) {

        final AlertDialog mProgressDialog = new SpotsDialog(ConfirmChangeNumberActivity.this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put("method", "numberOtpCheck");
            jsonObject.put("change_no_otp", Otpnumber);
            jsonObject.put("new_number", NewNumber);
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(ConfirmChangeNumberActivity.this));
            jsonObject.put("user_id", AppPreferences.getLoginId(ConfirmChangeNumberActivity.this));
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            Log.v(TAG, "send Otp Change number JSON :-- " + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ConfirmChangeNumberActivity.this);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                Log.v(TAG, "response php Otp Change Number() :--- " + response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            orderArray = mainObject.getJSONArray("result");

                            QBUser user = new QBUser();
                            user.setId(AppPreferences.getQBUserId(ConfirmChangeNumberActivity.this));
                            user.setLogin(countrycode.concat(NewNumber));
                            Log.v(TAG, "QuickBlox update with code number " + countrycode.concat(NewNumber));

                            QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                                @Override
                                public void onSuccess(QBUser qbUser, Bundle bundle) {
                                    Log.v(TAG, "QB UpdateUser() onSuccess :--- " + qbUser);
                                    AppPreferences.setQB_LoginId(ConfirmChangeNumberActivity.this, qbUser.getLogin());
                                    AppPreferences.setMobileuser(ConfirmChangeNumberActivity.this, qbUser.getLogin());
                                    AppPreferences.setMobileuserWithoutCountry(ConfirmChangeNumberActivity.this, NewNumber);
                                    try {
                                        for (int i = 0; orderArray.length() > i; i++) {
                                            JSONObject jsonObject2 = orderArray.getJSONObject(i);
                                            String loginId = jsonObject2.getString("userId");

                                            User user = new User();
                                            user.setName(jsonObject2.getString("username"));
                                            user.setMobile(qbUser.getLogin());//(jsonObject2.getString("mobile"));
                                            user.setPassword(jsonObject2.getString("password"));

                                            DatabaseHelper.getInstance(ConfirmChangeNumberActivity.this).insertUser(user);

                                            Intent intent = new Intent();
                                            setResult(741, intent);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                @Override
                                public void onError(QBResponseException e) {
                                    Log.v(TAG, "QBUpdateUser onError() :-- " + e.getMessage());
                                }
                            });

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }

            }
        });
        Log.v(TAG, "AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        Log.v(TAG, "jsonObject" + jsonObject);
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
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
            mProgressDialog = new ProgressDialog(ConfirmChangeNumberActivity.this);
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
            Intent intent = new Intent(getApplicationContext(), TwoTab_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }

    }

}
