package com.speakameqb.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.ListCountry;
import com.speakameqb.utils.TextTranslater;
import com.speakameqb.utils.VolleyCallback;

import java.util.List;

import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;

public class Languagelearn_activity extends AnimRootActivity {
    public static ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star,
            on_image, off_image, star_blue, user, user_blue, user_profile;
    public static AlertDialog mProgressDialog;
    TextView toolbartext;
    MaterialSpinner spinner1, spinner2;
    EditText typeEdit;
    TextView changelanguageedit;
    Button btn1, btn2;
    String SourceLanguage, Destinationlanguage, SourceContent, DestinationContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languagelearn_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Language Learning");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        toolbartext.setTypeface(tf1);

        spinner1 = (MaterialSpinner) findViewById(R.id.spinner);
        spinner2 = (MaterialSpinner) findViewById(R.id.spinner2);
        typeEdit = (EditText) findViewById(R.id.edit1);
        changelanguageedit = (TextView) findViewById(R.id.edit2);
        btn1 = (Button) findViewById(R.id.translate_btn);
        btn2 = (Button) findViewById(R.id.clear_btn);

        typeEdit.setTypeface(tf2);
        changelanguageedit.setTypeface(tf2);
        btn1.setTypeface(tf2);
        btn2.setTypeface(tf2);

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

        language_blue.setVisibility(View.VISIBLE);
        language.setVisibility(View.GONE);


        // String[] ITEMS = getResources().getStringArray(R.array.country);
        ListCountry country = new ListCountry();
        List<String> ITEMS = country.getAllLanguages(Languagelearn_activity.this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceLanguage = spinner1.getSelectedItem().toString();
                Destinationlanguage = spinner2.getSelectedItem().toString();
                SourceContent = typeEdit.getText().toString();
                priviewlanguage(SourceLanguage, Destinationlanguage, SourceContent);

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeEdit.setText("");
                changelanguageedit.setText("");
            }
        });
        typeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    changelanguageedit.setText("");

                }

            }
        });


        if (AppPreferences.getTotf(Languagelearn_activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(Languagelearn_activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);

        }


        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Languagelearn_activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Languagelearn_activity.this, "0");
        }

        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Languagelearn_activity.this, "0");
                user.setVisibility(View.GONE);
                return true;
            }
        });
        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Languagelearn_activity.this, "1");
                user_blue.setVisibility(View.GONE);
                return true;
            }
        });


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_blue.setVisibility(View.VISIBLE);
                setting.setVisibility(View.GONE);
                language_blue.setVisibility(View.GONE);
                language.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Languagelearn_activity.this, Setting_Activity.class);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_blue.setVisibility(View.VISIBLE);
                chat.setVisibility(View.GONE);

                language_blue.setVisibility(View.GONE);
                language.setVisibility(View.VISIBLE);

                Intent intent = new Intent(Languagelearn_activity.this, TwoTab_Activity.class);
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
                language_blue.setVisibility(View.GONE);
                language.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Languagelearn_activity.this, Favoirite_Activity.class);
                startActivity(intent);
            }
        });
//        user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                user.setVisibility(View.GONE);
//                user_blue.setVisibility(View.VISIBLE);
//                language_blue.setVisibility(View.GONE);
//                language.setVisibility(View.VISIBLE);
//                Intent intent = new Intent(Languagelearn_activity.this, EditProfile_Activity.class);
//                startActivity(intent);
//            }
//        });


    }

    public void priviewlanguage(String sorcountry, String descountry, final String message) {

        ListCountry country = new ListCountry();
        String sorcountrycode = country.getCode(Languagelearn_activity.this, sorcountry.trim());
        if (sorcountrycode.equalsIgnoreCase("")) {
            sorcountrycode = "en";
        }
        String descountrycode = country.getCode(Languagelearn_activity.this, descountry.trim());
        if (descountrycode.equalsIgnoreCase("")) {
            descountrycode = "en";
        }

        mProgressDialog = new SpotsDialog(Languagelearn_activity.this);
        mProgressDialog.setTitle("Please wait while translating language...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        TextTranslater.getInstance().translate(Languagelearn_activity.this, sorcountrycode, descountrycode, message, new VolleyCallback() {
            @Override
            public void backResponse(final String response) {
                if (response.equalsIgnoreCase("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changelanguageedit.setText(message);
                        }
                    });

                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            changelanguageedit.setText(response);
                        }
                    });

                }
                mProgressDialog.dismiss();

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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();


        language.setVisibility(View.GONE);
        language_blue.setVisibility(View.VISIBLE);

        setting_blue.setVisibility(View.GONE);
        setting.setVisibility(View.VISIBLE);

        chat.setVisibility(View.VISIBLE);
        chat_blue.setVisibility(View.GONE);

//        user.setVisibility(View.VISIBLE);
//        user_blue.setVisibility(View.GONE);

        star.setVisibility(View.VISIBLE);
        star_blue.setVisibility(View.GONE);
    }


}
