package com.speakame.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppPreferences;

public class Account_Activity extends AnimRootActivity {
    public static ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star,
            star_blue, user, user_blue, user_profile;
    TextView toolbartext;
    TextView txt_privacy, txt_payment, txt_changenumber, txt_deleteac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Account");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        toolbartext.setTypeface(tf1);
        txt_privacy = (TextView) findViewById(R.id.privacy);
        txt_payment = (TextView) findViewById(R.id.payment);
        txt_changenumber = (TextView) findViewById(R.id.changenumber);
        txt_deleteac = (TextView) findViewById(R.id.deleteaccount);


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

        setting_blue.setVisibility(View.VISIBLE);
        setting.setVisibility(View.GONE);


        if (AppPreferences.getTotf(Account_Activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(Account_Activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);

        }


        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Account_Activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Account_Activity.this, "0");
        }

        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Account_Activity.this, "0");
                user.setVisibility(View.GONE);
                return true;
            }
        });
        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Account_Activity.this, "1");
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
                Intent intent = new Intent(Account_Activity.this, Languagelearn_activity.class);
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
                finish();
                /*Intent intent = new Intent(Setting_Activity.this, TwoTab_Activity.class);
                intent.setAction("");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
            }
        });


        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star.setVisibility(View.GONE);
                star_blue.setVisibility(View.VISIBLE);
                setting_blue.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Account_Activity.this, Favoirite_Activity.class);
                startActivity(intent);
            }
        });
//        user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                user.setVisibility(View.GONE);
//                user_blue.setVisibility(View.VISIBLE);
//                setting_blue.setVisibility(View.GONE);
//                setting.setVisibility(View.VISIBLE);
//                Intent intent = new Intent(Account_Activity.this, EditProfile_Activity.class);
//                startActivity(intent);
//            }
//        });


        txt_privacy.setTypeface(tf2);
        txt_payment.setTypeface(tf2);
        txt_changenumber.setTypeface(tf2);
        txt_deleteac.setTypeface(tf2);

        txt_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account_Activity.this, Privacy_Activity.class);
                startActivity(intent);

            }
        });
        txt_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account_Activity.this, PaymentInfo_Activity.class);
                startActivity(intent);

            }
        });
        txt_changenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account_Activity.this, InstructionChangeNumberActivity.class);
                startActivity(intent);

            }
        });
        txt_deleteac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account_Activity.this, DeleteAccount_Activity.class);
                startActivity(intent);

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

    @Override
    protected void onResume() {
        super.onResume();


        setting.setVisibility(View.GONE);
        setting_blue.setVisibility(View.VISIBLE);

        language_blue.setVisibility(View.GONE);
        language.setVisibility(View.VISIBLE);

        chat.setVisibility(View.VISIBLE);
        chat_blue.setVisibility(View.GONE);

//        user.setVisibility(View.VISIBLE);
//        user_blue.setVisibility(View.GONE);

        star.setVisibility(View.VISIBLE);
        star_blue.setVisibility(View.GONE);
    }
}
