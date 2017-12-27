package com.speakame.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.Function;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class Forgot_paasword extends AnimRootActivity {
    private static final String TAG = "Forgot_Password";
    TextView toolbartext;
    EditText mMobile_edittext, mnewpasword_edittext, mchangepass_edit, mconfirmedit_paas;
    TextView mcontry_code;
    Button mbtn_submit, mbtn_confirm;
    Typeface typeface;
    String Mobilenumber, Newpassword, Changepaasword, ConfirmPassword;

    AlertDialog mProgressDialog;
    private String CountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_paasword);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        mMobile_edittext = (EditText) findViewById(R.id.mobilenum);
        mnewpasword_edittext = (EditText) findViewById(R.id.newpassword);
        mchangepass_edit = (EditText) findViewById(R.id.changpassword);
        mconfirmedit_paas = (EditText) findViewById(R.id.confirmpassword);

        mcontry_code = (TextView) findViewById(R.id.cntrycode);

        mbtn_submit = (Button) findViewById(R.id.btn_submit);
        mbtn_confirm = (Button) findViewById(R.id.btn_done);

        toolbartext.setText("Forgot Password");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mMobile_edittext.setTypeface(typeface);
        mnewpasword_edittext.setTypeface(typeface);
        mchangepass_edit.setTypeface(typeface);
        mconfirmedit_paas.setTypeface(typeface);
        mbtn_submit.setTypeface(typeface);
        mbtn_confirm.setTypeface(typeface);


        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        String diallingCode = Function.getCountryCode(tm);
        Log.v(TAG, "CountryCode :- " + diallingCode);

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        mcontry_code.setText("+" + diallingCode);

        mcontry_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forgot_paasword.this, CountryListActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mbtn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissKeyboard(Forgot_paasword.this);
                Mobilenumber = mMobile_edittext.getText().toString();
                CountryCode = mcontry_code.getText().toString();
                if (Mobilenumber.length() == 0) {
                    mMobile_edittext.setError(getResources().getString(R.string.error_field_required));
                } else if (Mobilenumber.length() < 8 || Mobilenumber.length() > 12) {
                    mMobile_edittext.setError(getResources().getString(R.string.error_field_required_length));
                } else if (CountryCode.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please Select Country Code !!!", Toast.LENGTH_SHORT).show();
//                    mcontry_code.setError(getResources().getString(R.string.error_field_required));
                } else {
                    senOtppaasword();
                }

            }
        });


        mbtn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissKeyboard(Forgot_paasword.this);

                Newpassword = mnewpasword_edittext.getText().toString();
                Changepaasword = mchangepass_edit.getText().toString();
                ConfirmPassword = mconfirmedit_paas.getText().toString();

                if (Newpassword.length() == 0) {
                    mnewpasword_edittext.setError(getResources().getString(R.string.error_field_required));
                } else if (Changepaasword.length() == 0) {
                    mchangepass_edit.setError(getResources().getString(R.string.error_field_required));
                } else if (ConfirmPassword.length() == 0) {
                    mconfirmedit_paas.setError(getResources().getString(R.string.error_field_required));

                } else if (!Changepaasword.equals(ConfirmPassword)) {
                    mconfirmedit_paas.setError("password not matched");

                } else {

                    confirmpassword();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                mcontry_code.setText(data.getStringExtra("countrycode"));

            }
        }
    }

    private void senOtppaasword() {

        CountryCode = mcontry_code.getText().toString();

        mProgressDialog = new SpotsDialog(Forgot_paasword.this);
        mProgressDialog.setTitle("Your contact is retrieving...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "fotgotPassword");
            jsonObject.put("mobile_number", Mobilenumber);
            jsonObject.put("CountryCode", CountryCode);

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONParser jsonParser = new JSONParser(Forgot_paasword.this);
        jsonParser.parseVollyJsonArray(AppConstants.COMMONURL, 1, jsonArray, new VolleyCallback() {
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
                                JSONObject topObject = orderArray.getJSONObject(i);
                            }
                            Toast.makeText(getApplicationContext(), "Password sent successfully ", Toast.LENGTH_LONG).show();
                            mMobile_edittext.setVisibility(View.GONE);
                            mnewpasword_edittext.setVisibility(View.VISIBLE);
                            mchangepass_edit.setVisibility(View.VISIBLE);
                            mconfirmedit_paas.setVisibility(View.VISIBLE);
                            mbtn_submit.setVisibility(View.GONE);
                            mbtn_confirm.setVisibility(View.VISIBLE);


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                            Snackbar.make(findViewById(android.R.id.content), "Number not exist", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

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
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.COMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

    private void confirmpassword() {
        mProgressDialog = new SpotsDialog(Forgot_paasword.this);
        mProgressDialog.setTitle("Your contact is retrieving...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "changePassword");
            jsonObject.put("otp_number", Newpassword);
            jsonObject.put("new_password", ConfirmPassword);

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(Forgot_paasword.this);
        jsonParser.parseVollyJsonArray(AppConstants.COMMONURL, 1, jsonArray, new VolleyCallback() {
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
                                JSONObject topObject = orderArray.getJSONObject(i);
                            }

                            Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Forgot_paasword.this, SignIn_Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                            Snackbar.make(findViewById(android.R.id.content), "Wrong input", Snackbar.LENGTH_LONG).setAction("Action", null).show();

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
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.COMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }
}
