package com.speakame.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.speakame.R;
import com.speakame.Services.QBService;
import com.speakame.utils.AppPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Splash_Activity extends AppCompatActivity {
    private static final String TAG = "Splash_Activity";
    private static final int REQUEST_READ_PHONE_STATE = 100;
    int Splash_time = 1000;

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
        //Function.contactPermisstion(Splash_Activity.this, 1);
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


        startAction();
        checkPermissionForReadPhoneState();
    }

    public void startAction() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent service = new Intent(Splash_Activity.this, QBService.class);
                startService(service);

                if (AppPreferences.getLoginStatus(Splash_Activity.this).equalsIgnoreCase("")) {
                    Intent intent = new Intent(Splash_Activity.this, Main_Activity.class);
                    startActivity(intent);
                    finish();
                } else if (AppPreferences.getVerifytype(Splash_Activity.this) == 1) {
                    Intent intent = new Intent(Splash_Activity.this, Verify_numberActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Splash_Activity.this, TwoTab_Activity.class);
                    intent.setAction("");
                    startActivity(intent);
                    finish();
                }


            }
        }, Splash_time);
    }

    public void sslgenration() {

        try {
            //paste Your package name at the first parameter
            PackageInfo info = getPackageManager().getPackageInfo("com.speakame",
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

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            QBSubscriptiondevice();
        }

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
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e(TAG, "onSubscriptionError :- " + errors.getMessage());
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {

        switch (requestCode) {

            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    QBSubscriptiondevice();
                }
                break;


            default:
                break;

        }
    }


}
