package com.speakame.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class AlertSpeakameActivity extends AnimRootActivity {
    Button mbtn_continue;
    TextView toolbartext;
    AlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_speakame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Welcome to Speakame");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        mbtn_continue = (Button) findViewById(R.id.btn_continue);
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mbtn_continue.setTypeface(typeface1);
        mbtn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                senduserId();

            }
        });
    }

    private void senduserId() {
        mProgressDialog = new SpotsDialog(AlertSpeakameActivity.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "userContinue");
            jsonObject.put("user_id", AppPreferences.getLoginId(AlertSpeakameActivity.this));
            jsonObject.put("mobilenumber",/* AppPreferences.getCountrycode(AlertSpeakameActivity.this) + " " +*/ AppPreferences.getMobileuser(AlertSpeakameActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(AlertSpeakameActivity.this);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                Log.d("response>>>>>", response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {

                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject jsonObject2 = orderArray.getJSONObject(i);

                                /*String loginId = jsonObject2.getString("userId");
                                AppPreferences.setLoginId(AlertSpeakameActivity.this, Integer.parseInt(jsonObject2.getString("userId")));
                                AppPreferences.setSocialId(AlertSpeakameActivity.this, jsonObject2.getString("social_id"));
                                AppPreferences.setMobileuser(AlertSpeakameActivity.this, jsonObject2.getString("mobile"));
                                AppPreferences.setPassword(AlertSpeakameActivity.this, jsonObject2.getString("password"));
                                AppPreferences.setFirstUsername(AlertSpeakameActivity.this, jsonObject2.getString("username"));
                                AppPreferences.setUserprofile(AlertSpeakameActivity.this, jsonObject2.getString("userImage"));

                                System.out.println("userpic" + jsonObject2.getString("userImage"));
                                System.out.println("usermobile" + jsonObject2.getString("mobile"));
                                System.out.println("userPassword" + jsonObject2.getString("password"));
                                AppPreferences.setEmail(AlertSpeakameActivity.this, jsonObject2.getString("email"));
                                AppPreferences.setUsercity(AlertSpeakameActivity.this, jsonObject2.getString("country"));
                                AppPreferences.setCountrycode(AlertSpeakameActivity.this, jsonObject2.getString("countrycode"));
                                AppPreferences.setUSERLANGUAGE(AlertSpeakameActivity.this, jsonObject2.getString("language"));
                                AppPreferences.setUsergender(AlertSpeakameActivity.this, jsonObject2.getString("gender"));
                                AppPreferences.setUserstatus(AlertSpeakameActivity.this, jsonObject2.getString("userProfileStatus"));
                                AppPreferences.setBlockList(AlertSpeakameActivity.this, jsonObject2.getString("block_users"));
                                AppPreferences.setPicprivacy(AlertSpeakameActivity.this, jsonObject2.getString("profie_pic_privacy"));
                                AppPreferences.setStatusprivacy(AlertSpeakameActivity.this, jsonObject2.getString("profie_status_privacy"));
                                AppPreferences.setLoginStatus(AlertSpeakameActivity.this, jsonObject2.getString("user_status"));

                                User user = new User();
                                user.setName(jsonObject2.getString("username"));
                                user.setMobile(jsonObject2.getString("mobile"));
                                user.setPassword(jsonObject2.getString("password"));

                                DatabaseHelper.getInstance(AlertSpeakameActivity.this).insertUser(user);*/
                                AppPreferences.setRegisterDate(AlertSpeakameActivity.this, jsonObject2.getString("start_date"));
                                AppPreferences.setRegisterEndDate(AlertSpeakameActivity.this, jsonObject2.getString("end_date"));
                            }

                            Intent intent = new Intent(AlertSpeakameActivity.this, MainScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish();


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                            Intent intent = new Intent(AlertSpeakameActivity.this, MainScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish();
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
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }
}
