package com.speakameqb.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.R;
import com.speakameqb.Services.HomeService;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fr.ganfra.materialspinner.MaterialSpinner;

public class ConfirmDelet_Account extends AppCompatActivity {
    final static String TAG = "ConfirmDelete_Activity";
    TextView toolbartext, txt1, txt2, txt3;
    EditText motp_edit;
    Button mbtn_delete;
    String OtpNumber, Reason;
    ProgressDialog mProgressDialog;
    MaterialSpinner spinner;
    String mobile_number, otp, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_delet__account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        motp_edit = (EditText) findViewById(R.id.confirm_otp);
        mbtn_delete = (Button) findViewById(R.id.btn_deleteacoount);

        toolbartext.setText("Delete my account");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

        String[] ITEMS = getResources().getStringArray(R.array.reason);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        Intent intent = getIntent();
        mobile_number = intent.getStringExtra("mobile_number");
        otp = intent.getStringExtra("otp");
        user_id = intent.getStringExtra("user_id");

        mbtn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtpNumber = motp_edit.getText().toString();
                Reason = spinner.getSelectedItem().toString();
                if (OtpNumber.length() == 0) {
                    motp_edit.setError(getResources().getString(R.string.error_field_required));
                } else {

                    finaldeleteAccount();


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


    private void finaldeleteAccount() {
        mProgressDialog = new ProgressDialog(ConfirmDelet_Account.this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "deleteAccount");
            jsonObject.put("delete_otp", OtpNumber);
            jsonObject.put("question", Reason);
            jsonObject.put("message", Reason);
            jsonObject.put("mobile_number", AppPreferences.getMobileuser(ConfirmDelet_Account.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ConfirmDelet_Account.this);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                System.out.println("rcv>json--" + response);

                Log.d("rcv>json--", response);

                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {

                            JSONArray orderArray = mainObject.getJSONArray("result");
                            int QbuserID = AppPreferences.getQBUserId(ConfirmDelet_Account.this);
                            QBUsers.deleteUser(QbuserID).performAsync(new QBEntityCallback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid, Bundle bundle) {
                                    Log.v(TAG, "QBUser Delete OnSucess :-  ");


                                    AppPreferences.setEmail(ConfirmDelet_Account.this, "");
                                    AppPreferences.setMobileuser(ConfirmDelet_Account.this, "");
                                    AppPreferences.setFirstUsername(ConfirmDelet_Account.this, "");
                                    AppPreferences.setLoginId(ConfirmDelet_Account.this, 0);
                                    AppPreferences.setUserprofile(ConfirmDelet_Account.this, "");
                                    AppPreferences.setTotf(ConfirmDelet_Account.this, "");
                                    AppPreferences.setEnetrSend(ConfirmDelet_Account.this, "");
                                    AppPreferences.setConvertTone(ConfirmDelet_Account.this, "");
                                    AppPreferences.setLoginStatus(ConfirmDelet_Account.this, "");
                                    AppPreferences.setRemainingDays(ConfirmDelet_Account.this, "");
                                    AppPreferences.setAckwnoledge(ConfirmDelet_Account.this, "");

                                    AppPreferences.setBadgeCount(ConfirmDelet_Account.this, 0);
                                    AppPreferences.setprofileImageArray(ConfirmDelet_Account.this, "");
                                    DatabaseHelper.getInstance(ConfirmDelet_Account.this).deleteDB();


                                    stopService(new Intent(ConfirmDelet_Account.this, HomeService.class));
                                    Intent intent = new Intent(ConfirmDelet_Account.this, Main_Activity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });

//                            AppPreferences.setEmail(ConfirmDelet_Account.this, "");
//                            AppPreferences.setMobileuser(ConfirmDelet_Account.this, "");
//                            AppPreferences.setFirstUsername(ConfirmDelet_Account.this, "");
//                            AppPreferences.setLoginId(ConfirmDelet_Account.this, 0);
//                            AppPreferences.setUserprofile(ConfirmDelet_Account.this, "");
//                            AppPreferences.setTotf(ConfirmDelet_Account.this, "");
//                            AppPreferences.setEnetrSend(ConfirmDelet_Account.this, "");
//                            AppPreferences.setConvertTone(ConfirmDelet_Account.this, "");
//                            AppPreferences.setLoginStatus(ConfirmDelet_Account.this, "");
//                            AppPreferences.setRemainingDays(ConfirmDelet_Account.this, "");
//                            AppPreferences.setAckwnoledge(ConfirmDelet_Account.this, "");
//                            DatabaseHelper.getInstance(ConfirmDelet_Account.this).deleteDB();
//
//
//                            stopService(new Intent(ConfirmDelet_Account.this, HomeService.class));
//                            Intent intent = new Intent(ConfirmDelet_Account.this, Main_Activity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                            popup();

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


    public void popup() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Otp number is incorrect")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void deleteSubscription(final int subscriptionId) {

        QBPushNotifications.getSubscriptions().performAsync(new QBEntityCallback<ArrayList<QBSubscription>>() {
            @Override
            public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {

                Log.v(TAG, "QBPushNotificatio arraylist :== " + qbSubscriptions);

                Log.v(TAG, "My Subscription id :--  " + subscriptionId);

                for (QBSubscription qbSubscription : qbSubscriptions) {

                    Log.v(TAG, "All Subscription id :--  " + qbSubscription.getId());

                    if (qbSubscription.getId() != subscriptionId) {

                        Log.v(TAG, "Subscription id :== " + qbSubscription.getId());

                        QBPushNotifications.deleteSubscription(qbSubscription.getId()).performAsync(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                Log.v(TAG, "push on sucess deleted");
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.e(TAG, "push on on ERROR");
                                Log.e(TAG, "Error :- " + e.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(QBResponseException e) {

                Log.e(TAG, "Error :- " + e.getMessage());
            }
        });
    }

}
