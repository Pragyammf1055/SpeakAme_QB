package com.speakame.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class DeleteAccount_Activity extends AnimRootActivity {
    TextView toolbartext, txt1, txt2, txt3;
    EditText edit1, edit2;
    Button btn_delete;
    String VerifyNumber;
    ProgressDialog mProgressDialog;
    LinearLayout mliLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Delete my account");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

        txt1 = (TextView) findViewById(R.id.texthead);
        txt2 = (TextView) findViewById(R.id.text2);
        txt3 = (TextView) findViewById(R.id.textcountry);
        edit1 = (EditText) findViewById(R.id.editstd);
        edit2 = (EditText) findViewById(R.id.editnumber);
        btn_delete = (Button) findViewById(R.id.btn_deleteacoount);
        mliLinearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        txt1.setTypeface(tf2);
        txt2.setTypeface(tf2);
        txt3.setTypeface(tf2);
        edit1.setTypeface(tf2);
        edit2.setTypeface(tf2);
        btn_delete.setTypeface(tf2);

        txt3.setText(AppPreferences.getUsercity(DeleteAccount_Activity.this));
        edit1.setText(AppPreferences.getCountrycode(DeleteAccount_Activity.this));

        mliLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeleteAccount_Activity.this, InstructionChangeNumberActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyNumber = edit2.getText().toString();
                if (VerifyNumber.length() == 0) {
                    edit2.setError(getResources().getString(R.string.error_field_required));
                } else if (VerifyNumber.length() < 8 || VerifyNumber.length() > 12) {
                    edit2.setError(getResources().getString(R.string.error_field_required_length));
                } else {
                    numberverifystatus();

                }


            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void numberverifystatus() {
        mProgressDialog = new ProgressDialog(DeleteAccount_Activity.this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "deleteNumber");
            jsonObject.put("mobile_number", VerifyNumber);
            jsonObject.put("user_id", AppPreferences.getLoginId(DeleteAccount_Activity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(DeleteAccount_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {


                Log.d("response>>", response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {

                            Intent intent = new Intent(DeleteAccount_Activity.this, ConfirmDelet_Account.class);
                            startActivity(intent);


                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                //  AppPreferences.setPicprivacy(DeleteAccount_Activity.this, topObject.getString("profie_pic_setting"));


                            }


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                            showAppQuitAlertDialog(getResources().getString(R.string.alert_number), DeleteAccount_Activity.this);

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

    public void showAppQuitAlertDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.alert));
        builder.setMessage(message);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Runtime.getRuntime().gc();
            }
        });


        AlertDialog msg = builder.create();
        msg.show();
    }

}
