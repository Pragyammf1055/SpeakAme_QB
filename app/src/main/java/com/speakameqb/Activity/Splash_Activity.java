package com.speakameqb.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.speakameqb.R;
import com.speakameqb.Services.QBService;
import com.speakameqb.utils.AppPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Splash_Activity extends AppCompatActivity {
    private static final String TAG = "Splash_Activity";
    private static final int REQUEST_READ_PHONE_STATE = 100;
    public static QBChatService chatService;
    int Splash_time = 1000;
    private String filePathFromGallery = "";

    public static void initChatService() {

        chatService = QBChatService.getInstance();

        QBChatService.setDebugEnabled(true); // enable chat logging
        QBChatService.setDefaultPacketReplyTimeout(10000);

        QBChatService.getInstance().setUseStreamManagement(true);

        QBChatService.ConfigurationBuilder builder = new QBChatService.ConfigurationBuilder();
        builder.setAutojoinEnabled(true);
        builder.setSocketTimeout(60); //Sets chat socket's read timeout in seconds
        builder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        builder.setUseTls(true);
        QBChatService.setConfigurationBuilder(builder);

//        chatService.setUseStreamManagement(true);
//        chatService.startAutoSendPresence(10);

    }

    public void setBadge(Context context) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", "");
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);

        Intent intent1 = new Intent();

        intent1.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        intent1.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", context.getPackageName() + "." + launcherClassName);
        intent1.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
        intent1.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", "");
        intent1.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
        context.sendBroadcast(intent1);
    }

    public String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        WebView webview1 = (WebView) findViewById(R.id.webview1);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //Function.contactPermisstion(Splash_Activity.this, 1);k
        setBadge(Splash_Activity.this);
        Log.d("getLauncherClassName", getLauncherClassName(Splash_Activity.this));

        sslgenration();

        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        if (height == 800) {
            webview1.loadUrl("file:///android_asset/splash_html_hdpi.html");
        } else {
            webview1.loadUrl("file:///android_asset/splash_html_nodpi.html");
        }
        getIntentfromGallery();
        startAction();
//        checkPermissionForReadPhoneState();
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
// Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Log.d(TAG, " handleSendImage imageUri: -- " + imageUri);
            try {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePathFromGallery = cursor.getString(columnIndex);

                Log.d(TAG, " handleSendImage imagePath : -- " + filePathFromGallery);

/* File file1 = new File(filePathFromGallery);

String fileName = file1.getName();
Log.d(TAG, " handleSendImage fileName : -- " + fileName);
*/
// imageView.setImageDrawable(Drawable.createFromPath(filePath));
                cursor.close();
            } catch (Exception e) {
            }
// Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
// Update UI to reflect multiple images being shared
        }
    }

    private void getIntentfromGallery() {

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Log.d(TAG, " Oncreate Intent from other app : -- " + action + " ;; " + type);
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            } else if (type.startsWith("video/")) {   // CODE ADDED ON 03 FEB 2018 for sharing video from gallery
                handleSendVideo(intent); // CODE ADDED ON 03 FEB 2018 for sharing video from gallery
            }

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
// Handle other intents, such as being started from the home screen
        }
    }

    void handleSendVideo(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Log.d(TAG, " handleSendImage imageUriVideo: -- " + imageUri);
            try {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePathFromGallery = cursor.getString(columnIndex);

                Log.d(TAG, " handleSendImage imagePathVideo : -- " + cursor.getString(columnIndex));
///storage/emulated/0/Pictures/SpeakAme Video/MS_25012018_225048982.mp4
                cursor.close();
            } catch (Exception e) {
            }
// Update UI to reflect image being shared
        }
    }

    public void startAction() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Calendar cur_cal = Calendar.getInstance();
                cur_cal.setTimeInMillis(System.currentTimeMillis());
                cur_cal.add(Calendar.SECOND, 50);
                Log.d("Testing", "Calender Set time:" + cur_cal.getTime());

                Intent service = new Intent(Splash_Activity.this, QBService.class);
                startService(service);
                PendingIntent pintent = PendingIntent.getService(Splash_Activity.this, 0, service, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(), 30 * 1000, pintent);

                if (AppPreferences.getLoginStatus(Splash_Activity.this).equalsIgnoreCase("")) {
                    Intent intent = new Intent(Splash_Activity.this, Main_Activity.class);
                    startActivity(intent);
                    finish();
                } else if (AppPreferences.getVerifytype(Splash_Activity.this) == 1) {
                    Intent intent = new Intent(Splash_Activity.this, Verify_numberActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    initChatService();
                    loginUserToQuickBlox(AppPreferences.getQB_LoginId(Splash_Activity.this), "12345678");
                }

            }
        }, Splash_time);
    }

    private void loginUserToQuickBlox(String qb_loginId, String pwd) {

        Log.v(TAG, " ~~~~~~~~~~~~ Inside Login Button ~~~~~~~~~~~~ ");
        Log.v(TAG, "Login :-  " + qb_loginId);
        Log.v(TAG, "Pwd :-  " + pwd);

        final QBUser user = new QBUser();
        user.setLogin(qb_loginId);
        user.setPassword(pwd);

        Log.v(TAG, "before login :- " + AppPreferences.getQBUserId(Splash_Activity.this));
        loginAsync(user); // Asynchronous way:

    }

    private void loginAsync(QBUser user) {

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                checkPermissionForReadPhoneState();
                Log.v(TAG, "After login:- " + AppPreferences.getQBUserId(Splash_Activity.this));
                Log.v(TAG, "after login 1 :- " + qbUser.getId());
                AppPreferences.setQBUserId(Splash_Activity.this, qbUser.getId());
                Log.v(TAG, "after settting Qb user id to pref :- " + AppPreferences.getQBUserId(Splash_Activity.this));

                Log.v(TAG, "Login Sucessfully");
                Log.v(TAG, "Bundle data :- " + bundle.toString());
                createSession(qbUser.getLogin(), "12345678");

                Intent intent = new Intent(Splash_Activity.this, TwoTab_Activity.class);
                intent.setAction("Splash_Activity");
                intent.putExtra("requestFilePath", filePathFromGallery);
                startActivity(intent);
                finish();
                filePathFromGallery = "";
            }

            @Override
            public void onError(QBResponseException e) {

                Log.v(TAG, "Login failed .." + e.getMessage());
                String message = e.getMessage();

                Snackbar.make(findViewById(android.R.id.content), "Login failed.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

    }

    private void createSession(String login, String pwd) {

        final QBUser user = new QBUser(login, pwd);

        QBAuth.createSession(user).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                Log.v(TAG, "QBSession id :- " + qbSession.getUserId());
                AppPreferences.setQBUserId(Splash_Activity.this, qbSession.getUserId());

                user.setId(qbSession.getUserId());

                Intent tempIntent = new Intent(Splash_Activity.this, QBService.class);
                QBService.start(Splash_Activity.this, tempIntent);

//sdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdvdv
//                chatLogin(user);

                /*chatService.login(user, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        Log.v(TAG, "Login to chat service done ");
                        registerQbChatListeners();
                        incomingMessage();
                        initRoster();
                        messageStatusesManager = chatService.getMessageStatusesManager();
                        subscribeUser(AppPreferences.getQBUserId(TwoTab_Activity.this));
                        initMessageStatusManagerAndListener();
//                        SDCCCCCCCCCCCCCCCCCCCCCC
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.v(TAG, "Error while login to chat service ");
                        Log.v(TAG, "Error chat serviev :-  " + e.getMessage());
                    }
                });*/
            }

            @Override
            public void onError(QBResponseException e) {
                Log.v(TAG, "Error in auth :-  " + e.getMessage());
            }
        });
    }

    private void chatLogin(QBUser user) {

        chatService.login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                Log.v(TAG, "Login to chat service done ");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.v(TAG, "Error while login to chat service ");
                Log.v(TAG, "Error chat serviev :-  " + e.getMessage());
            }
        });
    }

    public void sslgenration() {

        try {
            //paste Your package name at the first parameter
            PackageInfo info = getPackageManager().getPackageInfo("com.speakameqb",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MYKEYHASH:", sign);

                System.out.println("keyhash" + sign);
                // Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void checkPermissionForReadPhoneState() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            Log.d(TAG, "Asking for permission 1");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "Asking for permission 2");
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {

        Log.d(TAG, "on Request permission reult A:- " + grantResults.toString());

        switch (requestCode) {

            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    Log.d(TAG, "Asking for permission 4 ");
                    Log.d(TAG, "permission granted ");
                } else {
                    Log.d(TAG, "permission not granted ");
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                }
                break;
            default:
                break;
        }
    }


}
