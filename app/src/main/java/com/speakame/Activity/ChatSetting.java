package com.speakame.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppPreferences;
import java.util.Locale;

public class ChatSetting extends AnimRootActivity {
    public static ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star,
            star_blue, user, user_blue, user_profile;
    TextView toolbartext, txt1, txt2, txt3, txt4;
    CheckBox chkenter;
    String Language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        txt1 = (TextView) findViewById(R.id.textapp);
        txt2 = (TextView) findViewById(R.id.applanguagetext);
        txt3 = (TextView) findViewById(R.id.text2);
        txt4 = (TextView) findViewById(R.id.enterkeytextview);
        chkenter = (CheckBox) findViewById(R.id.chk1);
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        toolbartext.setTypeface(tf1);
        toolbartext.setText("Chat Setting");
        txt1.setTypeface(tf2);
        txt2.setTypeface(tf2);
        txt3.setTypeface(tf2);
        txt4.setTypeface(tf2);

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

        Language = Locale.getDefault().getDisplayLanguage();
        txt2.setText(Language);


        System.out.println("chkkkkvalue" + AppPreferences.getEnetrSend(ChatSetting.this));

        chkenter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // change your tag here

                if (isChecked) {
                    chkenter.setTag("Y");
                    AppPreferences.setEnetrSend(ChatSetting.this, "1");
                    System.out.println("chkkkkvalue" + AppPreferences.getEnetrSend(ChatSetting.this));
                    txt4.setText("Enter key will send your message");

                }
                if (chkenter.isChecked() == false) {
                    chkenter.setTag("N");
                    AppPreferences.setEnetrSend(ChatSetting.this, "");
                    txt4.setText("Enter key will add a new line");
                    System.out.println("chkkkkvalue" + AppPreferences.getEnetrSend(ChatSetting.this));
                }
            }
        });

        if (AppPreferences.getEnetrSend(ChatSetting.this).equalsIgnoreCase("")) {
            chkenter.setChecked(false);
            txt4.setText("Enter key will add a new line");
        } else {
            chkenter.setChecked(true);
            txt4.setText("Enter key will send your message");
        }


        setting_blue.setVisibility(View.VISIBLE);
        setting.setVisibility(View.GONE);


        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_blue.setVisibility(View.VISIBLE);
                language.setVisibility(View.GONE);
                setting_blue.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(ChatSetting.this, Languagelearn_activity.class);
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

                Intent intent = new Intent(ChatSetting.this, TwoTab_Activity.class);
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
                Intent intent = new Intent(ChatSetting.this, Favoirite_Activity.class);
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
//                Intent intent = new Intent(ChatSetting.this, EditProfile_Activity.class);
//                startActivity(intent);
//            }
//        });


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
