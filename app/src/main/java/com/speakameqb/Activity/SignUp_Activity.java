package com.speakameqb.Activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.speakameqb.AppController;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Beans.User;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.QuickBlox.QBResRequestExecutor;
import com.speakameqb.R;
import com.speakameqb.Services.HomeService;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Function;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.ServiceHandler;
import com.speakameqb.utils.VolleyCallback;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class SignUp_Activity extends AnimRootActivity implements VolleyCallback, View.OnClickListener {

    private static final String TAG = "SignUp_Activity";
    public static Location loc;
    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 2;
    //keep track of cropping intent
    final int PIC_CROP = 3;
    protected QBResRequestExecutor requestExecutor;
    Bitmap thePic;
    TextView headtext;
    AutoCompleteTextView editlanguage;
    EditText editname, editemail, editmobile, editpassword, editcity, mcontry_code, selectLanguage;
    ImageView fbimage, twitterimage, user_image, malecheck, maleuncheck, femalecheck, femaleuncheck;
    Button btn_signup;
    Typeface typeface, tf1;
    String Name, Email, City, CountCode, Mobile, Password, currentDateTimeString, Language, Gender, Encoded_userimage = "";
    ArrayList<String> languageListString = new ArrayList<String>();
    Spinner spn;
    Double lat, lon;
    LoginButton loginbutton;
    CallbackManager callbackManager;
    ProgressDialog dialog;
    QBChatService chatService;
    QBUser qbUserForDelete;
    ProgressDialog pDialog;
    //captured picture uri
    private Uri picUri;
    private AlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_up_);
        callbackManager = CallbackManager.Factory.create();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        editcity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignUp_Activity.this, CountryListActivity.class);
                startActivityForResult(intent, 0);

            }
        });

        selectLanguage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignUp_Activity.this, SelectLanguageActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editname, InputMethodManager.SHOW_IMPLICIT);

//        JSONObject obj = null;
//        final ArrayList<String> CountryName = new ArrayList<String>();
//        final ArrayList<String> CountryCode = new ArrayList<String>();
//        try {
//            obj = new JSONObject(Function.loadJSONFromAsset(SignUp_Activity.this, "countries.json"));
//
//            JSONArray m_jArry = obj.getJSONArray("countries");
//
//            for (int i = 0; i < m_jArry.length(); i++) {
//                JSONObject jsonObject = m_jArry.getJSONObject(i);
//                CountryName.add(jsonObject.getString("name"));
//                CountryCode.add(jsonObject.getString("code"));
//
//            }
//
//            System.out.println("namecountry" + CountryName);
//            System.out.println("codecountry" + CountryCode);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        ArrayAdapter<String> adapterr = new ArrayAdapter<String>
//                (this, android.R.layout.select_dialog_item, CountryName);
//        //Getting the instance of AutoCompleteTextView
//        editcity.setThreshold(1);
//        editcity.setAdapter(adapterr);
//
//        editcity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for (int i = 0; i < CountryCode.size(); i++) {
//                        if(editcity.getText().toString().equalsIgnoreCase(CountryName.get(i))){
//                            mcontry_code.setText(CountryCode.get(i));
//                        }
//                }
//
//            }
//        });


//        Language = Locale.getDefault().getDisplayLanguage();
//        editlanguage.setText(Language);

        //  new GetLanguage().execute();

        tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        headtext.setTypeface(tf1);
        typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        editname.setTypeface(typeface);
        editemail.setTypeface(typeface);
        editcity.setTypeface(typeface);
        selectLanguage.setTypeface(typeface);
        mcontry_code.setTypeface(typeface);
        editmobile.setTypeface(typeface);
        editpassword.setTypeface(typeface);
        // editlanguage.setTypeface(typeface);
        btn_signup.setTypeface(typeface);
        stopService(new Intent(SignUp_Activity.this, HomeService.class));

        Function.cameraPermisstion(SignUp_Activity.this, 1);

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        currentDateTimeString = dateFormatter.format(today);
        Log.d("currentdatetime", currentDateTimeString);


        Pattern gmailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                Email = account.name;
            }
        }
        editemail.setText(Email);

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //use standard intent to capture an files
                    Intent galleryIntent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //we will handle the returned data in onActivityResult
                    startActivityForResult(galleryIntent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(SignUp_Activity.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissKeyboard(SignUp_Activity.this);
                Name = editname.getText().toString();
                Email = editemail.getText().toString();
                City = editcity.getText().toString();
                Language = selectLanguage.getText().toString();
                CountCode = mcontry_code.getText().toString();
                Mobile = editmobile.getText().toString();
                Password = editpassword.getText().toString();

                if (malecheck.getVisibility() == View.VISIBLE) {
                    Gender = "Male";
                }
                if (femalecheck.getVisibility() == View.VISIBLE) {
                    Gender = "Female";
                }
                if (Name.length() == 0) {
                    editname.setError(getResources().getString(R.string.error_field_required));
                } else if (Email.length() == 0) {
                    editemail.setError(getResources().getString(R.string.error_field_required));
                } else if (!Email.contains("@") && !Email.contains(".")) {
                    editemail.setError(getResources().getString(R.string.error_invalid_email));
                } else if (City.length() == 0) {
                    editcity.setError(getResources().getString(R.string.error_field_required));
                } else if (Language.length() == 0) {
                    selectLanguage.setError(getResources().getString(R.string.error_field_required));
                } else if (Mobile.length() < 8 || Mobile.length() > 12) {
                    editmobile.setError(getResources().getString(R.string.error_field_required_length));
                } else if (Password.length() == 0) {
                    editpassword.setError(getResources().getString(R.string.error_field_required));
                } else if (Password.length() < 6) {
                    editpassword.setError(getResources().getString(R.string.error_field_password_lenght));
                } else {
                    registerUserToQuickBlox(Name, CountCode.replace("+", "") + Mobile, "12345678", Email);
                }
            }
        });

        malecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                malecheck.setVisibility(View.GONE);
                maleuncheck.setVisibility(View.VISIBLE);
                femalecheck.setVisibility(View.VISIBLE);
                femaleuncheck.setVisibility(View.GONE);
            }
        });

        maleuncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleuncheck.setVisibility(View.GONE);
                malecheck.setVisibility(View.VISIBLE);
                femalecheck.setVisibility(View.GONE);
                femaleuncheck.setVisibility(View.VISIBLE);
            }
        });

        femalecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femalecheck.setVisibility(View.GONE);
                femaleuncheck.setVisibility(View.VISIBLE);
                maleuncheck.setVisibility(View.GONE);
                malecheck.setVisibility(View.VISIBLE);


            }
        });
        femaleuncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femalecheck.setVisibility(View.VISIBLE);
                femaleuncheck.setVisibility(View.GONE);
                maleuncheck.setVisibility(View.VISIBLE);
                malecheck.setVisibility(View.GONE);
            }
        });


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
                                Log.v(TAG, "Json response USER_SIGNUP :- " + response.toString());
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


                                    new LoginTask(SignUp_Activity.this).execute("facebook", socialid,
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
                Log.v(TAG, "On error : " + AppConstants.REGISTER_LOG);
                Snackbar.make(findViewById(android.R.id.content), "Please try again later", Snackbar.LENGTH_LONG).setAction("Alert!", null).show();
            }

        });

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        String diallingCode = Function.getCountryCode(tm);
        Log.v(TAG, "CountryCode :- " + diallingCode);
        mcontry_code.setText("+" + diallingCode);

        String countryName = Function.getCountryFullName(tm);
        Log.v(TAG, "Country Name full :- " + countryName);
        editcity.setText(countryName);
        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get Country Name and its dialling Counry code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    }

    private void registerUserToQuickBlox(String name, String mobile_no, String pwd, String email) {
        Log.v(TAG, "Inside  registerUserToQuickBlox :- ");
        chatService = QBChatService.getInstance();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final QBUser user = new QBUser();

        user.setFullName(name);
        user.setPhone(mobile_no);
        user.setPassword(pwd);
        user.setEmail(email);
        user.setLogin(mobile_no);

//        registerSync(user); // Synchronous way:
        registerAsync(user); // Asynchronous way:
//        registerBefore_sdk(user);
    }

    private void registerAsync(final QBUser user) {

        pDialog = new ProgressDialog(SignUp_Activity.this);
        pDialog.setTitle("Please wait...");
        pDialog.show();


        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Asynchronus ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        Log.v(TAG, "Inside  registerUserToQuickBlox :- ");

        QBUsers.signUp(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                pDialog.dismiss();
                String login = qbUser.getLogin();
                String pwd = qbUser.getPassword();

                Log.v(TAG, "Login QuickBlox:-  " + login);
                Log.v(TAG, "Pwd QuickBlox:-  " + pwd);
                Log.v(TAG, "User id after signup QuickBlox:-  " + qbUser.getId());
                Log.v(TAG, "User created to QuickBlox");

                AppPreferences.setQBUserId(SignUp_Activity.this, qbUser.getId());
                AppPreferences.setQB_LoginId(SignUp_Activity.this, login);
                Snackbar.make(findViewById(android.R.id.content), "User sign up done QuickBlox", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                sendRequestToServer();
                qbUserForDelete = qbUser;
//                textViewlogin.setVisibility(View.VISIBLE);
//                loginLinearLayout.setVisibility(View.VISIBLE);
//                registerLinearLayout.setVisibility(View.GONE);
                loginUserToQuickBlox(login, "12345678", pDialog, "");
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Sign up failed QuickBlox :- " + e.getMessage());
                pDialog.dismiss();
                String message = e.getMessage();
                Snackbar.make(findViewById(android.R.id.content), "User sign up failed QuickBlox" + message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                loginUserToQuickBlox(user.getLogin(), "12345678", pDialog, message);

            }
        });

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Asynchronus ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    }

    private void loginUserToQuickBlox(String mobile_no, String pwd, ProgressDialog pDialog, String message) {

//        QBSettings.getInstance().fastConfigInit(MyApplication.APP_ID, MyApplication.AUTH_KEY, MyApplication.AUTH_SECRET);

        Log.v(TAG, " ~~~~~~~~~~~~ Inside Login Button QuickBlox ~~~~~~~~~~~~ ");
        Log.v(TAG, "Login with QuickBlox:-  " + mobile_no);
        Log.v(TAG, "Pwd :-  " + pwd);

        final QBUser user = new QBUser();
        user.setLogin(mobile_no);
        user.setPassword("12345678");

        loginAsync(user, pDialog, message); // Asynchronous way:
//        loginSync(user, dialog);  // Synchronous way

    }

    private void loginAsync(QBUser user, final ProgressDialog dialog, final String message) {

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                dialog.dismiss();
                Log.v(TAG, "Login Sucessfully QuickBlox");
                Log.v(TAG, "Bundle data :- " + bundle.toString());
                QBSubscriptiondevice();
//                Snackbar.make(findViewById(android.R.id.content), "User Login Sucessfully to QuickBlox", Snackbar.LENGTH_SHORT).show();

                if (message.equalsIgnoreCase("email has already been taken.")) {

                    AppPreferences.getQBUserId(SignUp_Activity.this);
                    Log.v(TAG, "QuickBlox id :- " + AppPreferences.getQBUserId(SignUp_Activity.this));
                    removeAllUserData(AppPreferences.getQBUserId(SignUp_Activity.this));

                } else if (message.equalsIgnoreCase("login has already been taken.")) {

                    removeAllUserData(AppPreferences.getQBUserId(SignUp_Activity.this));

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

    private void QBSubscriptiondevice() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG, "Firebase refreshedToken :- " + refreshedToken);
        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
        subscription.setEnvironment(QBEnvironment.DEVELOPMENT);
//        checkPermission();

        String deviceId;
        final TelephonyManager mTelephony = (TelephonyManager) this.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
            Log.v(TAG, "Device id :- .............." + deviceId);
        } else {
            deviceId = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }
        subscription.setDeviceUdid(deviceId);
//
        subscription.setRegistrationID(refreshedToken);

        Log.v(TAG, "Firebase refreshedToken :-" + refreshedToken);
        Log.v(TAG, "Device id :- .............." + deviceId);

        QBPushNotifications.createSubscription(subscription).performAsync(
                new QBEntityCallback<ArrayList<QBSubscription>>() {

                    @Override
                    public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                        Log.i(TAG, ">>> subscription created :- " + subscriptions.toString());

                        QBSubscription qbSubscription = subscriptions.get(0);
                        Log.i(TAG, ">>> subscription created 1:- " + qbSubscription);

                        int subscriptionId = qbSubscription.getId();
                        Log.i(TAG, ">>> subscription id :- " + subscriptionId);


                        AppPreferences.setQBFcmSubscribeId(SignUp_Activity.this, subscriptionId);
                        Log.i(TAG, "SIGN Up Activity subscription id123 pref :- " + AppPreferences.getQBFcmSubscribeId(SignUp_Activity.this));
                        deleteSubscription(AppPreferences.getQBFcmSubscribeId(SignUp_Activity.this));

                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e(TAG, "onSubscriptionError :- " + errors.getMessage());
                    }
                });
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

    private void sendRequestToServer() {

        mProgressDialog = new SpotsDialog(SignUp_Activity.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            jsonObj.put("method", AppConstants.USER_SIGNUP);
            jsonObj.put("mobile_type", "AN");
            jsonObj.put("username", Name);
            jsonObj.put("email", Email);
            jsonObj.put("country", City);
            jsonObj.put("countrycode", CountCode);
            jsonObj.put("mobile", Mobile);
            jsonObj.put("password", Password);
            jsonObj.put("language", Language);
            jsonObj.put("gender", "");
            jsonObj.put("dateTime", currentDateTimeString);
            jsonObj.put("mobile_uniquekey", Function.getAndroidID(SignUp_Activity.this));
            jsonObj.put("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken());
            jsonObj.put("userImage", Encoded_userimage);
            jsonObj.put("qb_id", AppPreferences.getQBUserId(SignUp_Activity.this));
            jsonArray.put(jsonObj);

            System.out.println("SEndSignup>" + jsonArray);
            Log.v(TAG, "Json request USER_SIGNUP :- " + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(getApplicationContext());
        jsonParser.parseVollyJsonArray(AppConstants.REGISTER_LOG, 1, jsonArray, SignUp_Activity.this);
        Log.v(TAG, "USER_SIGNUP URL :- " + AppConstants.REGISTER_LOG);
        System.out.println("AppConstants.URL.REGISTER_LOG" + AppConstants.REGISTER_LOG);
        System.out.println("jsonObject" + jsonObj);
    }

    private void initViews() {

        requestExecutor = AppController.getAppInstance().getQbResRequestExecutor();

        user_image = (ImageView) findViewById(R.id.userimage);
        editname = (EditText) findViewById(R.id.username);
        editname.requestFocus();
        editemail = (EditText) findViewById(R.id.emailid);
        editcity = (EditText) findViewById(R.id.city);
        editmobile = (EditText) findViewById(R.id.mobileno);
        mcontry_code = (EditText) findViewById(R.id.cntrycode);
        editpassword = (EditText) findViewById(R.id.paasword);
        loginbutton = (LoginButton) findViewById(R.id.login_button);
        selectLanguage = (EditText) findViewById(R.id.selectLanguage);
//        editlanguage = (AutoCompleteTextView) findViewById(R.id.selectlanguage);
        fbimage = (ImageView) findViewById(R.id.facebookbtn);
        twitterimage = (ImageView) findViewById(R.id.twitterbrn);
        btn_signup = (Button) findViewById(R.id.btnsignup);
        headtext = (TextView) findViewById(R.id.headtext);
        malecheck = (ImageView) findViewById(R.id.malec);
        maleuncheck = (ImageView) findViewById(R.id.male);
        femalecheck = (ImageView) findViewById(R.id.femalec);
        femaleuncheck = (ImageView) findViewById(R.id.female);
        headtext.setText("Register");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                editcity.setText(data.getStringExtra("countryname"));
                mcontry_code.setText(data.getStringExtra("countrycode"));

            } else if (requestCode == 1) {
                selectLanguage.setText(data.getStringExtra("language"));
            } else if (requestCode == CAMERA_CAPTURE) {
                picUri = data.getData();
                performCrop();
            }//user is returning from cropping the files
            else if (requestCode == PIC_CROP) {
//get the returned data
                Bundle extras = data.getExtras();
//get the cropped bitmap
                thePic = extras.getParcelable("data");
                Log.d("pictureimage", String.valueOf(thePic));

                user_image.setImageBitmap(thePic);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.d("pictureimage", Encoded_userimage);
            }

        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginManager.getInstance().logOut();
    }

    private void performCrop() {

        try {

            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate files type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }

            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }

        }
    }

    @Override
    public void backResponse(String response) {
        Log.d("Signup response", response);
        Log.v(TAG, "Json response USER_SIGNUP :- " + response.toString());

        String user_id = "";

        if (response != null) {
            try {
                JSONObject mainObject = new JSONObject(response);

                if (mainObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray topArray = mainObject.getJSONArray("result");

                    for (int i = 0; topArray.length() > i; i++) {

                        JSONObject jsonObject2 = topArray.getJSONObject(i);
                        user_id = jsonObject2.getString("userId");
//                        System.out.println("user_id" + user_id);
                        //String mobileNo = jsonObject2.getString("mobile").replace("");

                        AppPreferences.setLoginId(SignUp_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));

                        AppPreferences.setMobileuser(SignUp_Activity.this, jsonObject2.getString("country_with_mobile"));
                        AppPreferences.setFreeStatus(SignUp_Activity.this, jsonObject2.getString("free_status"));

                        /* AppPreferences.setPassword(SignUp_Activity.this, jsonObject2.getString("password"));
                        AppPreferences.setFirstUsername(SignUp_Activity.this, jsonObject2.getString("username"));
                        AppPreferences.setUserprofile(SignUp_Activity.this, jsonObject2.getString("userImage"));
                        AppPreferences.setEmail(SignUp_Activity.this, jsonObject2.getString("email"));
                        AppPreferences.setUsercity(SignUp_Activity.this, jsonObject2.getString("country"));
                        AppPreferences.setCountrycode(SignUp_Activity.this, jsonObject2.getString("countrycode"));
                        AppPreferences.setUSERLANGUAGE(SignUp_Activity.this, jsonObject2.getString("language"));
                        AppPreferences.setUsergender(SignUp_Activity.this, jsonObject2.getString("gender"));*/

                        /*User user = new User();
                        user.setName(jsonObject2.getString("username"));
                        user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ","").replace("+",""));
                        user.setPassword(jsonObject2.getString("password"));

                        DatabaseHelper.getInstance(SignUp_Activity.this).insertUser(user);*/
//dsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                        Intent intent = new Intent(getApplicationContext(), Verify_numberActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("SignUp");
                        intent.putExtra("full_name", Name);
                        intent.putExtra("email", Email);
                        intent.putExtra("mobile", CountCode.replace("+", "") + Mobile);
                        intent.putExtra("mobilewithoutcountryCode", editmobile.getText().toString());
                        startActivity(intent);
                    }

                } else if (mainObject.getString("status").equalsIgnoreCase("300")) {

                    removeAllUserData(AppPreferences.getQBUserId(SignUp_Activity.this));
                    Snackbar.make(findViewById(android.R.id.content), "Mobile Number Already Exists. Please use another Mobile Number !!!", Snackbar.LENGTH_LONG).show();

                } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                    removeAllUserData(AppPreferences.getQBUserId(SignUp_Activity.this));
                    Snackbar.make(findViewById(android.R.id.content), "Some Error Occured !!!", Snackbar.LENGTH_LONG).show();

                } else if (mainObject.getString("status").equalsIgnoreCase("500")) {

                    removeAllUserData(AppPreferences.getQBUserId(SignUp_Activity.this));
                    Snackbar.make(findViewById(android.R.id.content), "Email Id Already Exists. Please use another Email Id !!!", Snackbar.LENGTH_LONG).show();

                } else if (mainObject.getString("status").equalsIgnoreCase("100")) {

                    removeAllUserData(AppPreferences.getQBUserId(SignUp_Activity.this));
                    Snackbar.make(findViewById(android.R.id.content), "Internet not Connected.", Snackbar.LENGTH_LONG).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();
        }
    }

    private void removeAllUserData(int QBUserId) {

        Log.v(TAG, "QuickBlox id :- " + AppPreferences.getQBUserId(SignUp_Activity.this));
      /*  requestExecutor.deleteCurrentUser(QBUserId, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                Log.v(TAG, "User deleted succesfully");

            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Error deleted succesfully");
                Log.v(TAG, "Error message :- " + e.getMessage());

            }
        });
*/

        QBUsers.deleteUser(QBUserId).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.v(TAG, "User deleted succesfully");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.v(TAG, "Error deleted succesfully");
                Log.v(TAG, "Error message :- " + e.getMessage());
            }
        });
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {

    }


    //////////getlanguage//////////////

    ////////////////////////////////

    private class GetLanguage extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObj;
        private int status = 0;

        public GetLanguage() {
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("************SpeakameLanguage*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("language_value :------------ "
                        + AppConstants.COMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.COMMONURL);
                jsonObj = new JSONObject();
                jsonObj.put("method", "allLanguage");

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json Data", jsonArray.toString());

                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
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
                Log.v(TAG, "Json response :- " + jsonString);
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        jsonString = jsonObj.getString("result");

                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out
                                    .println("--------- message 200 got ----------");
                            status = 200;

                            JSONArray resultArray = jsonObj.getJSONArray("result");
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject jsonObject = resultArray.getJSONObject(i);
                                Language = jsonObject.getString("language");

                                AllBeans allBeans = new AllBeans();
                                allBeans.setLanguages(Language);

                                languageListString.add(Language);

                            }

                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("400")) {
                            status = 400;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("500")) {
                            status = 500;
                            return jsonString;
                        }
                    }
                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("status", status + "");
            if (status == 200) {

                ArrayAdapter city_adapter = new ArrayAdapter<String>(SignUp_Activity.this, android.R.layout.simple_dropdown_item_1line, languageListString);

                editlanguage.setAdapter(city_adapter);

            } else {
                Toast.makeText(SignUp_Activity.this,
                        "Please Try Again Later", Toast.LENGTH_LONG).show();//fa
            }
        }
    }

    class LoginTask extends AsyncTask<String, Void, String> {
        String status = "", result = "", loginId = "";
        String pass = "";
        Context context;

        public LoginTask(Context context) {
            this.context = context;
            mProgressDialog = new SpotsDialog(SignUp_Activity.this);
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
              /*  values.add(new BasicNameValuePair("mobile", params[1]));
                values.add(new BasicNameValuePair("password", params[2]));
//                values.add(new BasicNameValuePair("countryCode", params[3]));
                values.add(new BasicNameValuePair("countryCode", countryCode));
                values.add(new BasicNameValuePair("dateTime", currentDateTimeString));
                values.add(new BasicNameValuePair("method", AppConstants.LOGIN));
                values.add(new BasicNameValuePair("mobile_uniquekey", Function.getAndroidID(SignUp_Activity.this)));
                values.add(new BasicNameValuePair("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken()));*/
                pass = params[2];
            } else if (params[0].equals("facebook")) {
                values.add(new BasicNameValuePair("method", AppConstants.LOGIN));
                values.add(new BasicNameValuePair("user_social_id", params[1]));
                values.add(new BasicNameValuePair("user_name", params[2]));
                values.add(new BasicNameValuePair("password", ""));
                values.add(new BasicNameValuePair("email", params[3]));
                values.add(new BasicNameValuePair("profpic", params[4]));
                values.add(new BasicNameValuePair("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken()));
                values.add(new BasicNameValuePair("mobile_uniquekey", Function.getAndroidID(SignUp_Activity.this)));
//                values.add(new BasicNameValuePair("device_id", getDeviceId()));
//                values.add(new BasicNameValuePair("android_id", Uniqueid));

            }

            Log.d("login_value", values.toString());
            String login_json = serviceHandler.makeServiceCall(AppConstants.REGISTER_LOG, ServiceHandler.POST, values);
            System.out.println("Logine value : ------------ " + AppConstants.REGISTER_LOG);


            if (login_json != null) {
                Log.d("login_json", login_json.toString());
                try {
                    JSONObject jsonObject = new JSONObject(login_json);
                    status = jsonObject.getString("status");
                    result = jsonObject.getString("result");

                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        loginId = jsonObject2.getString("userId");
                        String Qb_id = jsonObject2.getString("receiver_QB_Id");
                        Log.v(TAG, "QB User Id :- " + Qb_id);

                        AppPreferences.setLoginId(SignUp_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));
                        AppPreferences.setSocialId(SignUp_Activity.this, jsonObject2.getString("social_id"));
                        // AppPreferences.setMobileuser(SignIn_Activity.this, jsonObject2.getString("country_with_mobile").replace(" ","-"));
                        AppPreferences.setPassword(SignUp_Activity.this, jsonObject2.getString("password"));
                        AppPreferences.setFirstUsername(SignUp_Activity.this, jsonObject2.getString("username"));
                        AppPreferences.setUserprofile(SignUp_Activity.this, jsonObject2.getString("userImage"));

                        System.out.println("userpic" + jsonObject2.getString("userImage"));
                        System.out.println("usermobile" + jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        System.out.println("userPassword" + jsonObject2.getString("password"));
                        AppPreferences.setEmail(SignUp_Activity.this, jsonObject2.getString("email"));
                        AppPreferences.setMobileuser(SignUp_Activity.this, jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        AppPreferences.setUsercity(SignUp_Activity.this, jsonObject2.getString("country"));
                        AppPreferences.setCountrycode(SignUp_Activity.this, jsonObject2.getString("countrycode"));
                        AppPreferences.setUSERLANGUAGE(SignUp_Activity.this, jsonObject2.getString("language"));
                        AppPreferences.setUsergender(SignUp_Activity.this, jsonObject2.getString("gender"));
                        AppPreferences.setUserstatus(SignUp_Activity.this, jsonObject2.getString("userProfileStatus"));
                        AppPreferences.setBlockList(SignUp_Activity.this, jsonObject2.getString("block_users"));
                        AppPreferences.setPicprivacy(SignUp_Activity.this, jsonObject2.getString("profie_pic_privacy"));
                        AppPreferences.setStatusprivacy(SignUp_Activity.this, jsonObject2.getString("profie_status_privacy"));
                        //AppPreferences.setLoginStatus(SignIn_Activity.this, jsonObject2.getString("user_status"));
                        AppPreferences.setLoginStatus(SignUp_Activity.this, "1");
                        AppPreferences.setRegisterDate(SignUp_Activity.this, jsonObject2.getString("start_date"));
                        AppPreferences.setRegisterEndDate(SignUp_Activity.this, jsonObject2.getString("end_date"));
//                        AppPreferences.setRemainingDays(SignIn_Activity.this, jsonObject2.getString("reamning_days"));
                        //   System.out.println("userenddate" + jsonObject2.getString("end_date"));
                        User user = new User();
                        user.setName(jsonObject2.getString("username"));
                        user.setMobile(jsonObject2.getString("country_with_mobile").replace(" ", "").replace("+", ""));
                        user.setPassword(jsonObject2.getString("password"));
                        DatabaseHelper.getInstance(SignUp_Activity.this).insertUser(user);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            //    finish();
            Log.d("data from server", status + "   " + loginId);
            if (status.equals("200") && !loginId.equals("")) {

                System.out.println("loginid1" + AppPreferences.getLoginId(context));
//                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(SignUp_Activity.this, TwoTab_Activity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("300")) {

                //imageDialog();
                Log.d("data>>>", status + "   " + loginId);


            } else if (status.equals("400")) {
                Snackbar.make(findViewById(android.R.id.content), "Not valid user !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else if (status.equals("600")) {
                System.out.println("loginid1" + AppPreferences.getLoginId(context));
//                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(SignUp_Activity.this, AlertDaysSpeakameActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("700")) {
                System.out.println("loginid1" + AppPreferences.getLoginId(context));
//                startService(new Intent(getBaseContext(), MyService.class));

                Intent mintent_home = new Intent(SignUp_Activity.this, AlertDaysSpeakameActivity.class);
                mintent_home.setAction("");
                startActivity(mintent_home);
                finish();
            } else if (status.equals("800")) {
                //confirmpopup();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Check your network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }
}
