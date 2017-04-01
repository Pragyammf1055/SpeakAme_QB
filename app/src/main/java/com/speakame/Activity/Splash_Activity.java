package com.speakame.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Splash_Activity extends AnimRootActivity {
    private static final String TAG = "Splash_Activity";

    int Splash_time = 5000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        WebView webview1 = (WebView) findViewById(R.id.webview1);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //Function.contactPermisstion(Splash_Activity.this, 1);


        sslgenration();

        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        if (height == 800) {
            webview1.loadUrl("file:///android_asset/splash_html_hdpi.html");

        } else {
            webview1.loadUrl("file:///android_asset/splash_html_nodpi.html");
        }


        startAction();


    }


    public void startAction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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


}
