package com.speakame.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.speakame.Beans.User;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Services.HomeService;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;
import com.speakame.utils.ServiceHandler;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class SignIn_Activity extends AnimRootActivity {
    private static final String TAG = "SignIn_Activity";
    public static String loginType = "";
    final ArrayList<String> CountryName = new ArrayList<String>();
    final ArrayList<String> CountryCode = new ArrayList<String>();
    public ProgressDialog pDialog;
    public Dialog dialog;
    TextView headtext, memberlogintxt, textregister, mortext, mforgottext, mCountryCode;
    Typeface typeface, typeface1, typeface2;
    ImageView museriamge, mfbimage, mtwiterimage;
    EditText mUserName, mpaasword;
    Button mbtnsignin, mbtnsignup;
    String MobileNumber, Password, currentDateTimeString, Language, Country, CounCode, countryCode;
    LoginButton loginbutton;
    CallbackManager callbackManager;
    JSONObject obj = null;
    EditText autoCompleteTextView, meditcountcode, selectLanguage;
    ProgressDialog mDialog;
    private AlertDialog mProgressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                mCountryCode.setText(data.getStringExtra("countrycode"));

            } else if (requestCode == 1) {

                autoCompleteTextView.setText(data.getStringExtra("countryname"));
                meditcountcode.setText(data.getStringExtra("countrycode"));

            } else if (requestCode == 2) {
                selectLanguage.setText(data.getStringExtra("language"));
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in_);
//        callbackManager = CallbackManager.Factory.create();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        /*
        loginbutton = (LoginButton) findViewById(R.id.login_button);
        // loginbutton.setBackgroundResource(R.drawable.facebook);
        // loginbutton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.trans);

        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        loginbutton.setReadPermissions(permissionNeeds);

        loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("onSuccess");
                GraphRequest request = GraphRequest.newMeRequest
                        (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                Log.v("LoginActivity", response.toString());
                                System.out.println("Check: " + response.toString());
                                try {
                                    String socialid = object.getString("id");
                                    String name = object.getString("name");
                                    Log.d("facebook_user", name);
                                    String email = object.getString("email");
                                    String imgUrl = "https://graph.facebook.com/" + socialid + "/picture?type=large";
                                    //  String gender = object.getString("gender");
                                    //  String birthday = object.getString("birthday");
                                    System.out.println("facebookauthentication" + socialid + ", " + name + ", " + email + "," + imgUrl);

                                    new LoginTask(SignIn_Activity.this).execute("facebook", socialid,
                                            name, email, imgUrl);
                                    //response-513937435454439, Vikas Barve, mmfinfotech346@gmail.com, male, 03/10/2000
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("onError");
                Snackbar.make(findViewById(android.R.id.content), "Please try again later", Snackbar.LENGTH_LONG).setAction("Alert!", null).show();
            }

        });
*/

        setListener();
        getCountryCodes();

    }

    private void loginUserToQuickBlox(String mobile_no, String pwd, String status) {

//        QBSettings.getInstance().fastConfigInit(MyApplication.APP_ID, MyApplication.AUTH_KEY, MyApplication.AUTH_SECRET);

        Log.v(TAG, " ~~~~~~~~~~~~ Inside Login Button QuickBlox ~~~~~~~~~~~~ ");
        Log.v(TAG, "Login with QuickBlox:-  " + mobile_no);
        Log.v(TAG, "Pwd :-  " + pwd);

        final QBUser user = new QBUser();
        user.setLogin(mobile_no);
        user.setPassword("12345678");

        pDialog = new ProgressDialog(SignIn_Activity.this);
        pDialog.setTitle("Please wait...");
        pDialog.show();

        loginAsync(user, pDialog, status); // Asynchronous way:
//        loginSync(user, dialog);  // Synchronous way

    }

    private void loginAsync(QBUser user, final ProgressDialog dialog, final String status) {

        final String countryCode = mCountryCode.getText().toString().replace("+", "").trim();
        final String mobileNo = mUserName.getText().toString().trim();


        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                dialog.dismiss();
                Log.v(TAG, "Login Sucessfully QuickBlox");
                Log.v(TAG, "Bundle data :- " + bundle.toString());

                Snackbar.make(findViewById(android.R.id.content), "User Login Sucessfully to QuickBlox", Snackbar.LENGTH_SHORT).show();

                AppPreferences.setQB_LoginId(SignIn_Activity.this, countryCode + mobileNo);

                if (status.equalsIgnoreCase("200")) {

                    Intent mintent_home = new Intent(SignIn_Activity.this, TwoTab_Activity.class);
                    mintent_home.setAction("");
                    startActivity(mintent_home);
                    finish();

                } else if (status.equalsIgnoreCase("600")) {
                    Intent mintent_home = new Intent(SignIn_Activity.this, AlertDaysSpeakameActivity.class);
                    mintent_home.setAction("");
                    startActivity(mintent_home);
                    finish();
                } else if (status.equalsIgnoreCase("700")) {
                    Intent mintent_home = new Intent(SignIn_Activity.this, AlertDaysSpeakameActivity.class);
                    mintent_home.setAction("");
                    startActivity(mintent_home);
                    finish();
                }

            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Login failed QuickBlox .." + e.getMessage());
                dialog.dismiss();
                String message = e.getMessage();
                Snackbar.make(findViewById(android.R.id.content), "QuickBlox Login failed due to " + message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void getCountryCodes() {

        //////////////country_gettingcode/////////

        try {
            obj = new JSONObject(Function.loadJSONFromAsset(SignIn_Activity.this, "countries.json"));

            JSONArray m_jArry = obj.getJSONArray("countries");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jsonObject = m_jArry.getJSONObject(i);
                CountryName.add(jsonObject.getString("name"));
                CountryCode.add(jsonObject.getString("code"));

            }

            System.out.println("namecountry" + CountryName);
            System.out.println("codecountry" + CountryCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////////////country_gettingcode/////////

    }

    private void initViews() {

        headtext = (TextView) findViewById(R.id.headtext);
        memberlogintxt = (TextView) findViewById(R.id.memberlogin);
        textregister = (TextView) findViewById(R.id.textregister);
        mforgottext = (TextView) findViewById(R.id.forgottext);
        mCountryCode = (TextView) findViewById(R.id.countryCode);
        museriamge = (ImageView) findViewById(R.id.userimageView);
        // mfbimage = (ImageView) findViewById(R.id.fb);
//        loginbutton = (LoginButton) findViewById(R.id.login_button);
        mtwiterimage = (ImageView) findViewById(R.id.twit);
        mUserName = (EditText) findViewById(R.id.username);
        mUserName.requestFocus();
        mpaasword = (EditText) findViewById(R.id.password);
        mbtnsignin = (Button) findViewById(R.id.btn_sign);
        mbtnsignup = (Button) findViewById(R.id.signupbutton);

        headtext.setText("Login");

        typeface = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        headtext.setTypeface(typeface);
        typeface1 = Typeface.createFromAsset(getAssets(), "Raleway-Light.ttf");
        memberlogintxt.setTypeface(typeface1);
        textregister.setTypeface(typeface1);
        typeface2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mUserName.setTypeface(typeface2);
        mpaasword.setTypeface(typeface2);
        mbtnsignin.setTypeface(typeface2);
        mbtnsignup.setTypeface(typeface2);
        mforgottext.setTypeface(typeface2);
        mCountryCode.setTypeface(typeface2);

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        currentDateTimeString = dateFormatter.format(today);
        Log.d("currentdatetime", currentDateTimeString);

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        String diallingCode = Function.getCountryCode(tm);
        Log.v(TAG, "CountryCode :- " + diallingCode);

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        mCountryCode.setText("+" + diallingCode);

    }

    private void loginAsync(QBUser user, final ProgressDialog dialog) {

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                dialog.dismiss();
                Log.v(TAG, "Login Sucessfully");
                Log.v(TAG, "Bundle data :- " + bundle.toString());

                Snackbar.make(findViewById(android.R.id.content), "User Login Sucessfully.", Snackbar.LENGTH_SHORT).show();

                new LoginTask(SignIn_Activity.this).execute(loginType, mUserName.getText().toString(), mpaasword.getText().toString(), mCountryCode.getText().toString());
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Login failed .." + e.getMessage());
                dialog.dismiss();
                String message = e.getMessage();
                Snackbar.make(findViewById(android.R.id.content), "Login failed due to " + message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void loginUserToQuickBlox(String mobile_no, String pwd) {

//        QBSettings.getInstance().fastConfigInit(MyApplication.APP_ID, MyApplication.AUTH_KEY, MyApplication.AUTH_SECRET);

        Log.v(TAG, " ~~~~~~~~~~~~ Inside Login Button ~~~~~~~~~~~~ ");
        Log.v(TAG, "Login :-  " + mobile_no);
        Log.v(TAG, "Pwd :-  " + pwd);

        final QBUser user = new QBUser();
        user.setLogin(mobile_no);
        user.setPassword(pwd);

        mDialog = new ProgressDialog(SignIn_Activity.this);
        mDialog.setMessage("Please wait...");
        mDialog.show();

        loginAsync(user, mDialog); // Asynchronous way:
//        loginSync(user, dialog);  // Synchronous way

    }

    private void setListener() {

        mCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn_Activity.this, CountryListActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mbtnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissKeyboard(SignIn_Activity.this);
                if (mUserName.length() > 0) {
                    MobileNumber = mUserName.getText().toString();
                    if (mpaasword.length() > 0) {

                        loginType = "web";
                        Password = mpaasword.getText().toString();
                        countryCode = mCountryCode.getText().toString().replace("+", "").trim();
                        loginUserToQuickBlox(countryCode + MobileNumber, "12345678");

                    } else {

                        mpaasword.setError("Enter Password");

                    }
                } else {

                    mUserName.setError("Enter Mobile Number");

                }
                //  new  AddeventAsynch().execute();
            }
        });

        mbtnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn_Activity.this, SignUp_Activity.class);
                startActivity(intent);
            }
        });

        mforgottext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn_Activity.this, Forgot_paasword.class);
                startActivity(intent);

            }
        });

    }

    private void imageDialog() {
        dialog = new Dialog(SignIn_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Image");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.mobilenumber_popup);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        final EditText mediEditText = (EditText) dialog.findViewById(R.id.mobileno);
        meditcountcode = (EditText) dialog.findViewById(R.id.cntrycode);
        autoCompleteTextView = (EditText) dialog.findViewById(R.id.country);
        selectLanguage = (EditText) dialog.findViewById(R.id.selectLanguage);

         /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        String diallingCode = Function.getCountryCode(tm);
        Log.v(TAG, "CountryCode :- " + diallingCode);
        meditcountcode.setText("+" + diallingCode);

        String countryName = Function.getCountryFullName(tm);
        Log.v(TAG, "Country Name full :- " + countryName);
        autoCompleteTextView.setText(countryName);
        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        Button mbutton = (Button) dialog.findViewById(R.id.btn_submit);

        selectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn_Activity.this, SelectLanguageActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn_Activity.this, CountryListActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissKeyboard(SignIn_Activity.this);
                Country = autoCompleteTextView.getText().toString();
                CounCode = meditcountcode.getText().toString();
                MobileNumber = mediEditText.getText().toString();
                Language = selectLanguage.getText().toString();

                if (Country.length() == 0) {
                    autoCompleteTextView.setError(getResources().getString(R.string.error_field_required));
                } else if (CounCode.length() == 0) {
                    meditcountcode.setError(getResources().getString(R.string.error_field_required));
                } else if (MobileNumber.length() < 8 && MobileNumber.length() > 12) {
                    mediEditText.setError("Please insert valid mobile number");
                } else {
                    new SignInfileAsynch().execute();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void popup() {

        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Number already exixts. Please use another number !!!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(SignIn_Activity.this, HomeService.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(SignIn_Activity.this, HomeService.class));
    }

    public void confirmpopup() {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You are already login in another device! Please verify otp")
                .setCancelText("No")
                .setConfirmText("Yes")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        new VryfyLogin().execute();
                        sDialog.cancel();


                    }
                })
                .show();
    }

    class LoginTask extends AsyncTask<String, Void, String> {
        String status = "", result = "", loginId = "";
        String pass = "";
        Context context;
        JSONObject jsonObject;


        public LoginTask(Context context) {
            this.context = context;
            mProgressDialog = new SpotsDialog(SignIn_Activity.this);
            mProgressDialog.setTitle("Login");
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("loginType", params[0]));

            if (params[0].equals("web")) {
                values.add(new BasicNameValuePair("method", AppConstants.LOGIN));
                values.add(new BasicNameValuePair("mobile", params[1]));
                values.add(new BasicNameValuePair("password", params[2]));
//                values.add(new BasicNameValuePair("countryCode", params[3]));
                values.add(new BasicNameValuePair("countryCode", countryCode));
                values.add(new BasicNameValuePair("dateTime", currentDateTimeString));
                values.add(new BasicNameValuePair("mobile_type", "AN"));
                values.add(new BasicNameValuePair("mobile_uniquekey", Function.getAndroidID(SignIn_Activity.this)));
                values.add(new BasicNameValuePair("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken()));
                pass = params[2];
            } else if (params[0].equals("facebook")) {
                values.add(new BasicNameValuePair("method", AppConstants.LOGIN));
                values.add(new BasicNameValuePair("user_social_id", params[1]));
                values.add(new BasicNameValuePair("user_name", params[2]));
                values.add(new BasicNameValuePair("password", ""));
                values.add(new BasicNameValuePair("email", params[3]));
                values.add(new BasicNameValuePair("profpic", params[4]));
                values.add(new BasicNameValuePair("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken()));
                values.add(new BasicNameValuePair("mobile_uniquekey", Function.getAndroidID(SignIn_Activity.this)));
//                values.add(new BasicNameValuePair("device_id", getDeviceId()));
//                values.add(new BasicNameValuePair("android_id", Uniqueid));

            }

            Log.d("login_value", values.toString());
            String login_json = serviceHandler.makeServiceCall(AppConstants.REGISTER_LOG, ServiceHandler.POST, values);
            System.out.println("Logine value : ------------ " + AppConstants.REGISTER_LOG);

            if (login_json != null) {
                Log.d("login_json", login_json.toString());
                try {
                    jsonObject = new JSONObject(login_json);
                    status = jsonObject.getString("status");
                    Log.v(TAG, "All Status :-- " + status);
                    result = jsonObject.getString("result");

                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        loginId = jsonObject2.getString("userId");

                        Log.v(TAG, "Free status when status 800 :- " + jsonObject2.getString("free_status"));

                        AppPreferences.setLoginId(SignIn_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));
                        AppPreferences.setSocialId(SignIn_Activity.this, jsonObject2.getString("social_id"));
                        // AppPreferences.setMobileuser(SignIn_Activity.this, jsonObject2.getString("country_with_mobile").replace(" ","-"));
//                        if (params[0].equals("web"))
                        AppPreferences.setPassword(SignIn_Activity.this, jsonObject2.getString("password"));
                        AppPreferences.setFirstUsername(SignIn_Activity.this, jsonObject2.getString("username"));
                        AppPreferences.setUserprofile(SignIn_Activity.this, jsonObject2.getString("userImage"));

                        System.out.println("userpic" + jsonObject2.getString("userImage"));
                        System.out.println("usermobile" + jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        System.out.println("userPassword" + jsonObject2.getString("password"));
                        AppPreferences.setEmail(SignIn_Activity.this, jsonObject2.getString("email"));

                        AppPreferences.setMobileuserWithoutCountry(SignIn_Activity.this, jsonObject2.getString("mobile"));
                        AppPreferences.setMobileuser(SignIn_Activity.this, jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        AppPreferences.setUsercity(SignIn_Activity.this, jsonObject2.getString("country"));
                        AppPreferences.setCountrycode(SignIn_Activity.this, jsonObject2.getString("countrycode"));
                        AppPreferences.setUSERLANGUAGE(SignIn_Activity.this, jsonObject2.getString("language"));
                        AppPreferences.setUsergender(SignIn_Activity.this, jsonObject2.getString("gender"));
                        AppPreferences.setUserstatus(SignIn_Activity.this, jsonObject2.getString("userProfileStatus"));
                        AppPreferences.setBlockList(SignIn_Activity.this, jsonObject2.getString("block_users"));
                        AppPreferences.setPicprivacy(SignIn_Activity.this, jsonObject2.getString("profie_pic_privacy"));
                        AppPreferences.setStatusprivacy(SignIn_Activity.this, jsonObject2.getString("profie_status_privacy"));
                        //AppPreferences.setLoginStatus(SignIn_Activity.this, jsonObject2.getString("user_status"));
                        AppPreferences.setLoginStatus(SignIn_Activity.this, "1");
                        AppPreferences.setRegisterDate(SignIn_Activity.this, jsonObject2.getString("start_date"));
                        AppPreferences.setRegisterEndDate(SignIn_Activity.this, jsonObject2.getString("end_date"));
                        AppPreferences.setRemainingDays(SignIn_Activity.this, jsonObject2.getString("reamning_days"));

                        AppPreferences.setFreeStatus(SignIn_Activity.this, jsonObject2.getString("free_status"));
                        //   System.out.println("userenddate" + jsonObject2.getString("end_date"));
                        User user = new User();
                        user.setName(jsonObject2.getString("username"));
                        user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        user.setPassword(jsonObject2.getString("password"));
                        DatabaseHelper.getInstance(SignIn_Activity.this).insertUser(user);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result1) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            //    finish();

            Log.d("data from server", status + "   " + loginId);
            Log.v(TAG, "Free status + " + status);
            Log.v(TAG, "Result :- " + result);

            String countryCode = mCountryCode.getText().toString().replace("+", "").trim();
            String mobileNo = mUserName.getText().toString().trim();


            if (status.equals("200") && !loginId.equals("")) {

                System.out.println("loginid1" + AppPreferences.getLoginId(context));
//                startService(new Intent(getBaseContext(), MyService.class));

                loginUserToQuickBlox(countryCode + mobileNo, "12345678", "200");


            } else if (status.equals("300")) {

                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        String freeStatus = jsonObject2.getString("free_status");
                        Log.v(TAG, "Free Status :- " + freeStatus);
                        AppPreferences.setFreeStatus(SignIn_Activity.this, jsonObject2.getString("free_status"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                imageDialog();
                Log.d("data>>>", status + "   " + loginId);

            } else if (status.equals("400")) {

                Snackbar.make(findViewById(android.R.id.content), "Wrong Password !!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            } else if (status.equals("600")) {

                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        String freeStatus = jsonObject2.getString("free_status");
                        Log.v(TAG, "Free Status :- " + freeStatus);
                        AppPreferences.setFreeStatus(SignIn_Activity.this, jsonObject2.getString("free_status"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("loginid1" + AppPreferences.getLoginId(context));
//                startService(new Intent(getBaseContext(), MyService.class));
//                Intent mintent_home = new Intent(SignIn_Activity.this, AlertSpeakameActivity.class);

                loginUserToQuickBlox(countryCode + mobileNo, "12345678", "600");


            } else if (status.equals("700")) {

                JSONArray jsonArray = null;

                try {
                    jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        String freeStatus = jsonObject2.getString("free_status");
                        Log.v(TAG, "Free Status :- " + freeStatus);
                        AppPreferences.setFreeStatus(SignIn_Activity.this, jsonObject2.getString("free_status"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("loginid1" + AppPreferences.getLoginId(context));
//                startService(new Intent(getBaseContext(), MyService.class));

                loginUserToQuickBlox(countryCode + mobileNo, "12345678", "700");


            } else if (status.equals("800")) {

                /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~  if user is already logged in toanothr device ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        String freeStatus = jsonObject2.getString("free_status");
                        Log.v(TAG, "Free Status :- " + freeStatus);
                        AppPreferences.setFreeStatus(SignIn_Activity.this, jsonObject2.getString("free_status"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                confirmpopup();

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Some Error occured !!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }


    private class SignInfileAsynch extends AsyncTask<Void, Void, String> {
        String status = "", result = "", loginId = "";

        private JSONObject jsonObj;

        public SignInfileAsynch() {

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(SignIn_Activity.this);
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
                System.out.println("verify_successfully : ------------ "
                        + AppConstants.REGISTER_LOG);
                HttpPost httppost = new HttpPost(AppConstants.REGISTER_LOG);
                jsonObj = new JSONObject();

                jsonObj.put("method", AppConstants.CHECK_NUMBER);
                jsonObj.put("mobile_number", MobileNumber);
                jsonObj.put("user_fb_id", AppPreferences.getSocialId(SignIn_Activity.this));
                jsonObj.put("language", Language);
                jsonObj.put("country", Country);
                jsonObj.put("countrycode", CounCode);
                jsonObj.put("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken());
                jsonObj.put("mobile_uniquekey", Function.getAndroidID(SignIn_Activity.this));

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("jsonValue", jsonArray.toString());
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

                String jsonString = " ";

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

                            AppPreferences.setLoginId(SignIn_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));
                            AppPreferences.setVerifytype(SignIn_Activity.this, Integer.parseInt(jsonObject2.getString("veryfymobile")));
                            AppPreferences.setSocialId(SignIn_Activity.this, jsonObject2.getString("social_id"));
                            AppPreferences.setMobileuser(SignIn_Activity.this, jsonObject2.getString("mobile"));
                            AppPreferences.setPassword(SignIn_Activity.this, jsonObject2.getString("password"));
                            AppPreferences.setFirstUsername(SignIn_Activity.this, jsonObject2.getString("username"));
                            AppPreferences.setUserprofile(SignIn_Activity.this, jsonObject2.getString("userImage"));
                            AppPreferences.setEmail(SignIn_Activity.this, jsonObject2.getString("email"));
                            AppPreferences.setUsercity(SignIn_Activity.this, jsonObject2.getString("country"));
                            AppPreferences.setCountrycode(SignIn_Activity.this, jsonObject2.getString("countrycode"));
                            AppPreferences.setUSERLANGUAGE(SignIn_Activity.this, jsonObject2.getString("language"));
                            AppPreferences.setUsergender(SignIn_Activity.this, jsonObject2.getString("gender"));
                            AppPreferences.setUserstatus(SignIn_Activity.this, jsonObject2.getString("userProfileStatus"));

                            User user = new User();
                            user.setName(jsonObject2.getString("username"));
                            user.setMobile(jsonObject2.getString("mobile"));
                            user.setPassword(jsonObject2.getString("mobile"));
                            System.out.println("checkmobilepaas2" + jsonObject2.getString("mobile"));
                            System.out.println("checksocialid" + jsonObject2.getString("social_id"));

                            DatabaseHelper.getInstance(SignIn_Activity.this).insertUser(user);
                        }
                    } else if (jsonObj.getString("status").equalsIgnoreCase("400")) {
                        status = "400";
                        Snackbar.make(findViewById(android.R.id.content), "Number already exist !!!", Snackbar.LENGTH_LONG)
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
                dialog.dismiss();

                /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code commented by Pragya for removing XMPP register ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

                /*
                new AddmemberAsynch().execute();
                startService(new Intent(getBaseContext(), MyService.class));
                */

                /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Code commented by Pragya for removing XMPP register ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

                System.out.println("loginid1" + AppPreferences.getLoginId(SignIn_Activity.this));
//                startService(new Intent(getBaseContext(), MyService.class));
                Intent mintent_home = new Intent(SignIn_Activity.this, Verify_numberActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("400")) {
                status = "400";
                popup();
//                Snackbar.make(findViewById(android.R.id.content), "Number is already exist", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        }

    }

    private class AddmemberAsynch extends AsyncTask<Void, Void, String> {
        //  private ProgressDialog mProgressDialog;
        private JSONObject jsonObj;
        private int status = 0;

        public AddmemberAsynch() {
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            mProgressDialog = new ProgressDialog(SignIn_Activity.this);
//            mProgressDialog.setMessage("Please wait...");
//            mProgressDialog.show();

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
                System.out.println("registartion value : ------------ "
                        + AppConstants.XMPPURL);
                HttpPost httppost = new HttpPost(AppConstants.XMPPURL);
                jsonObj = new JSONObject();

                jsonObj.put("username", MobileNumber);
                jsonObj.put("password", MobileNumber);
                jsonObj.put("name", AppPreferences.getFirstUsername(SignIn_Activity.this));
                jsonObj.put("email", AppPreferences.getEmail(SignIn_Activity.this));


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
            // mProgressDialog.dismiss();
//            startService(new Intent(getBaseContext(), MyService.class));


        }

    }

    private class VryfyLogin extends AsyncTask<Void, Void, String> {
        String jsonString = "";
        private AlertDialog mProgressDialog;
        private JSONObject jsonObj;
        private String status;

        public VryfyLogin() {

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(SignIn_Activity.this);
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
                System.out.println("successfully : ------------ "
                        + AppConstants.DEMOCOMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.DEMOCOMMONURL);
                jsonObj = new JSONObject();

                jsonObj.put("method", "checkLoginNewDevice");
                jsonObj.put("user_mobile", MobileNumber);
                jsonObj.put("countrycode", countryCode);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("jsonvalue", jsonArray.toString());
                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v(TAG, "json : " + jsonArray.toString(2));
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
                        status = "200";
                        JSONArray resultArray = jsonObj.getJSONArray("result");
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject2 = resultArray.getJSONObject(i);
                            AppPreferences.setMobileuser(SignIn_Activity.this, jsonObject2.getString("mobile"));
                        }
                    } else if (jsonObj.getString("status").equalsIgnoreCase("400")) {
                        status = "400";
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
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.d("SignIn_Activity", "inside 200 status :- " + status + "\n Result is :- " + result);

            if (result.contains("200")) {
//                String countryCode = mCountryCode.getText().toString().replace("+", "").trim();
                final String countryCode = mCountryCode.getText().toString().trim();
                final String mobileNo = mUserName.getText().toString().trim();

                Intent intent = new Intent(SignIn_Activity.this, ConfirmLoginOtp.class);
                intent.setAction("SignIn_Activity");
                intent.putExtra("mobileNo", countryCode + mobileNo);
                startActivity(intent);
                finish();

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Check network connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

    }
}
