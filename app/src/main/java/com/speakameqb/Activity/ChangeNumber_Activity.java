package com.speakameqb.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Function;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class ChangeNumber_Activity extends AnimRootActivity {
    private static final String TAG = "ChangeNumberAcivity";
    TextView toolbartext, txt1, txt2;
    EditText stdedit, numberedit, newstdedit, newnumedit;
    String OldNumber, NewNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Change Number");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        txt1 = (TextView) findViewById(R.id.texthead);
        txt2 = (TextView) findViewById(R.id.textchangenum);

        stdedit = (EditText) findViewById(R.id.editcode);
        stdedit.setActivated(false);
        stdedit.setFocusableInTouchMode(false);
        stdedit.setFocusable(false);
        numberedit = (EditText) findViewById(R.id.oldnumberedit);
        newstdedit = (EditText) findViewById(R.id.changestd);
        newnumedit = (EditText) findViewById(R.id.newnumberedit);
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        txt1.setTypeface(tf2);
        txt2.setTypeface(tf2);
        stdedit.setTypeface(tf2);
        numberedit.setTypeface(tf2);
        newstdedit.setTypeface(tf2);
        newnumedit.setTypeface(tf2);

//        stdedit.setText(AppPreferences.getCountrycode(ChangeNumber_Activity.this));
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String diallingCode = Function.getCountryCode(tm);
        Log.v("ChangeNumberActivity", "CountryCode :- " + diallingCode);
        stdedit.setText("+" + diallingCode);

        stdedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " Inside click !!! ");
                stdedit.setEnabled(true);
                stdedit.setActivated(true);
                stdedit.setCursorVisible(true);
                stdedit.setFocusable(true);
                stdedit.setFocusableInTouchMode(true);
                stdedit.setClickable(true);
                stdedit.requestFocus();
                stdedit.requestFocusFromTouch();
            }
        });

        numberedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Inside clivk numberedit");
                numberedit.setCursorVisible(true);
                numberedit.setFocusable(true);
                numberedit.setFocusableInTouchMode(true);
                numberedit.setEnabled(true);
                numberedit.setClickable(true);
                numberedit.requestFocus();
                numberedit.requestFocusFromTouch();
            }
        });
        numberedit.setText(AppPreferences.getMobileuser(ChangeNumber_Activity.this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.changenumber_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.done) {
            OldNumber = numberedit.getText().toString();
            NewNumber = newnumedit.getText().toString();
            dismissKeyboard(ChangeNumber_Activity.this);

            if (OldNumber.length() == 0) {
                numberedit.setError(getResources().getString(R.string.error_field_required));

            } else if (NewNumber.length() == 0) {
                newnumedit.setError(getResources().getString(R.string.error_field_required));
            } else if (OldNumber.length() < 8 || OldNumber.length() > 12) {
                numberedit.setError(getResources().getString(R.string.error_field_required_length));
            } else if (NewNumber.length() < 8 || NewNumber.length() > 12) {
                newnumedit.setError(getResources().getString(R.string.error_field_required_length));
            } else {
                changenumberRequest();
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void changenumberRequest() {

        final AlertDialog mProgressDialog = new SpotsDialog(ChangeNumber_Activity.this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "changeNumber");
            jsonObject.put("current_number", OldNumber);
            jsonObject.put("new_number", NewNumber);
            jsonObject.put("user_id", AppPreferences.getLoginId(ChangeNumber_Activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(ChangeNumber_Activity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ChangeNumber_Activity.this);
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
                                JSONObject topObject = orderArray.getJSONObject(i);

                            }
                            Toast.makeText(getApplicationContext(), "Otp send", Toast.LENGTH_LONG).show();
                            senduserId();

                            Intent intent = new Intent(ChangeNumber_Activity.this, ConfirmChangeNumberActivity.class);
                            intent.putExtra("newnumber", NewNumber);
                            startActivity(intent);
                            finish();


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
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }


    private void senduserId() {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "changeNumberOtp");
            jsonObject.put("user_id", AppPreferences.getLoginId(ChangeNumber_Activity.this));
            jsonObject.put("new_number", NewNumber);

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ChangeNumber_Activity.this);
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
                                JSONObject topObject = orderArray.getJSONObject(i);


                            }


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

}
