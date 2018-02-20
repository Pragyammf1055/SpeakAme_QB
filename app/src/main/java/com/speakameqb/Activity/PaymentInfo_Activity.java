package com.speakameqb.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaymentInfo_Activity extends AnimRootActivity implements GoogleApiClient.OnConnectionFailedListener {
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    private static final String TAG = "PaymentInfo_Activity";
    public static ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star,
            star_blue, user, user_blue, user_profile;
    private static PayPalConfiguration config;
    TextView toolbartext, txt1, txt2, txt3, txt4, txt5, txt6, txt7, txt8, txt9, txt10, txt11, txt12, rate1, rate2, rate3;
    Button btn1, btn2;
    RadioGroup radioGrp;
    RadioButton radioButton1, radioButton2, radioButton3;
    EditText mobileNumberEditText;
    Typeface tf1;
    Typeface tf2;
    private String paymentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Payment Info");

        //Paypal Configuration Object
        config = new PayPalConfiguration()
                // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                // or live (ENVIRONMENT_PRODUCTION)
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(AppConstants.PAYPAL_CLIENT_ID);

        initViews();


        paymentAmount = rate1.getText().toString();
        paymentAmount = paymentAmount.replace("$", "").trim();

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
    }

    private void initViews() {

        tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        toolbartext.setTypeface(tf1);
        mobileNumberEditText = (EditText) findViewById(R.id.mobileNumber);
        txt1 = (TextView) findViewById(R.id.textphone);
        txt2 = (TextView) findViewById(R.id.expirationtext);
        txt3 = (TextView) findViewById(R.id.date_text);
        txt4 = (TextView) findViewById(R.id.purchaseExpiration);
        txt5 = (TextView) findViewById(R.id.year1);
        txt6 = (TextView) findViewById(R.id.year3);
        txt7 = (TextView) findViewById(R.id.year5);
        txt8 = (TextView) findViewById(R.id.rate1);
        txt9 = (TextView) findViewById(R.id.rate2);
        txt10 = (TextView) findViewById(R.id.rate3);
        txt11 = (TextView) findViewById(R.id.discount1);
        txt12 = (TextView) findViewById(R.id.discount2);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        rate1 = (TextView) findViewById(R.id.rate1);
        rate2 = (TextView) findViewById(R.id.rate2);
        rate3 = (TextView) findViewById(R.id.rate3);

        paymentAmount = rate1.getText().toString();
        paymentAmount = paymentAmount.replace("$", "").trim();
        language = (ImageView) findViewById(R.id.iv_language);
        language_blue = (ImageView) findViewById(R.id.iv_bluelanguage);
        chat = (ImageView) findViewById(R.id.iv_chat_footer);
        chat_blue = (ImageView) findViewById(R.id.iv_chatbluefooter);
        setting = (ImageView) findViewById(R.id.iv_setting);
        setting_blue = (ImageView) findViewById(R.id.iv_bluesetting);
        star = (ImageView) findViewById(R.id.iv_star);
        star_blue = (ImageView) findViewById(R.id.iv_starblue);
        user = (ImageView) findViewById(R.id.iv_user_footer);
        user_blue = (ImageView) findViewById(R.id.iv_userbluefooter);

        txt1.setTypeface(tf2);
        txt2.setTypeface(tf2);
        txt3.setTypeface(tf2);
        txt4.setTypeface(tf2);
        txt5.setTypeface(tf2);
        txt6.setTypeface(tf2);
        txt7.setTypeface(tf2);
        txt8.setTypeface(tf2);
        txt9.setTypeface(tf2);
        txt10.setTypeface(tf2);
        btn1.setTypeface(tf2);
        btn2.setTypeface(tf2);
        mobileNumberEditText.setTypeface(tf2);

        radioButton1 = (RadioButton) findViewById(R.id.radio1);
        radioButton2 = (RadioButton) findViewById(R.id.radio2);
        radioButton3 = (RadioButton) findViewById(R.id.radio3);
        radioGrp = (RadioGroup) findViewById(R.id.radioGrp);

        mobileNumberEditText.setText(AppPreferences.getMobileUserWithoutCountry(PaymentInfo_Activity.this));
        txt3.setText(AppPreferences.getRegisterEndDate(PaymentInfo_Activity.this));
        setting_blue.setVisibility(View.VISIBLE);
        setting.setVisibility(View.GONE);

        if (AppPreferences.getTotf(PaymentInfo_Activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(PaymentInfo_Activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);
        }

        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(PaymentInfo_Activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(PaymentInfo_Activity.this, "0");
        }

        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(PaymentInfo_Activity.this, "0");
                user.setVisibility(View.GONE);
                return true;
            }
        });
        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(PaymentInfo_Activity.this, "1");
                user_blue.setVisibility(View.GONE);
                return true;
            }
        });

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_blue.setVisibility(View.VISIBLE);
                language.setVisibility(View.GONE);
                setting_blue.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(PaymentInfo_Activity.this, Languagelearn_activity.class);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_blue.setVisibility(View.VISIBLE);
                chat.setVisibility(View.GONE);

                setting_blue.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(PaymentInfo_Activity.this, TwoTab_Activity.class);
                intent.setAction("");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star.setVisibility(View.GONE);
                star_blue.setVisibility(View.VISIBLE);
                setting_blue.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(PaymentInfo_Activity.this, Favoirite_Activity.class);
                startActivity(intent);
            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPayment();
            }
        });


    }

    private void checkRadioButton() {

        radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {

                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(id);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked) {
//                    Toast.makeText(getApplicationContext(),
//                            checkedRadioButton.getText(), Toast.LENGTH_LONG).show();
                    String checkedFrogetPasswordType = checkedRadioButton.getText().toString();
                    Log.v(TAG, " Selected  Button  is : " + checkedFrogetPasswordType);
                }

                if (radioButton1.isChecked()) {

                    paymentAmount = rate1.getText().toString().trim();
                    paymentAmount = paymentAmount.replace("$", "");
                    Log.w(TAG, "Payment Amount for 1 year is : " + paymentAmount);

                } else if (radioButton2.isChecked()) {

//                    paymentAmount = "2.40";
                    paymentAmount = rate2.getText().toString().trim();
                    paymentAmount = paymentAmount.replace("$", "");
                    Log.w(TAG, "Payment Amount for 2 year is : " + paymentAmount);
                } else if (radioButton3.isChecked()) {

//                    paymentAmount = "3.34";
                    paymentAmount = rate3.getText().toString().trim();
                    paymentAmount = paymentAmount.replace("$", "");
                    Log.w(TAG, "Payment Amount for 3 year is : " + paymentAmount);
                }
            }
        });
    }

    private void getPayment() {
        //Getting the amount from editText
//        paymentAmount = ;
        checkRadioButton();
        Log.w(TAG, "Payment Amount after calling checkRadioButton : " + paymentAmount);

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount.trim())), "USD", "SpeakAme Subscription Fee\n",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment

            /*
            {
                "client": {
                "environment": "sandbox",
                        "paypal_sdk_version": "2.0.0",
                        "platform": "iOS",
                        "product_name": "PayPal iOS SDK;"
            },
                "response": {
                "create_time": "2014-02-12T22:29:49Z",
                        "id": "PAY-564191241M8701234KL57LXI",
                        "intent": "sale",
                        "state": "approved"
            },
                "response_type": "payment"
            }
        */

            if (resultCode == Activity.RESULT_OK) {


                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        try {
                            JSONObject jsonDetails = new JSONObject(paymentDetails);
                            JSONObject jsonResponse = jsonDetails.getJSONObject("response");
                            String transaction_ID = jsonResponse.getString("id");
                            String createTime = jsonResponse.getString("create_time");
                            String intent = jsonResponse.getString("intent");
                            String paymentState = jsonResponse.getString("state");
                            Log.i("paymentExample", "RESPONSE :- \n" + "Transaction_ID :- " + transaction_ID + "\nCreate Time :- " + createTime +
                                    "\nIntent :- " + intent + "\nPayment State :- " + paymentState);

                        } catch (JSONException e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        /*startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));*/
                        showAlertDialog(paymentAmount);

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    private void showAlertDialog(String paymentAmount) {

        new SweetAlertDialog(this)
                .setTitleText("Payment of " + paymentAmount + " done Successfully !!!")
                .show();

    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
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

    @Override
    protected void onResume() {
        super.onResume();
        setting.setVisibility(View.GONE);
        setting_blue.setVisibility(View.VISIBLE);
        language_blue.setVisibility(View.GONE);
        language.setVisibility(View.VISIBLE);
        chat.setVisibility(View.VISIBLE);
        chat_blue.setVisibility(View.GONE);
        star.setVisibility(View.VISIBLE);
        star_blue.setVisibility(View.GONE);

//        user.setVisibility(View.VISIBLE);
//        user_blue.setVisibility(View.GONE);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
