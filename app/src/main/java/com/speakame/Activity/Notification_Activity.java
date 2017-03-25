package com.speakame.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.Services.VibrateService;
import com.speakame.utils.AppPreferences;

public class Notification_Activity extends AnimRootActivity {
    public static ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star,
            on_image, off_image, star_blue, user, user_blue, user_profile;
    TextView toolbartext;
    LinearLayout l1;
    CheckBox chkBox;
    TextView txt1, txt2, txt3, txt4, txt5, txt6, txt7, txt8, txt9, txt10, txt11, txt12, txt13;
    LinearLayout vibrateLinearLayout;
    String selectedOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Notifications");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        txt1 = (TextView) findViewById(R.id.textView);
        txt2 = (TextView) findViewById(R.id.text2);
        txt3 = (TextView) findViewById(R.id.text3);
        txt4 = (TextView) findViewById(R.id.text4);
        txt5 = (TextView) findViewById(R.id.text5);
        txt6 = (TextView) findViewById(R.id.text6);
        txt7 = (TextView) findViewById(R.id.text7);
        txt8 = (TextView) findViewById(R.id.text8);
        txt9 = (TextView) findViewById(R.id.text9);
        txt10 = (TextView) findViewById(R.id.text10);
        txt11 = (TextView) findViewById(R.id.text11);
        txt12 = (TextView) findViewById(R.id.text12);
        txt13 = (TextView) findViewById(R.id.text13);
        l1 = (LinearLayout) findViewById(R.id.tone_layout);
        chkBox = (CheckBox) findViewById(R.id.chkconverttone);
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

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
        txt11.setTypeface(tf2);
        txt12.setTypeface(tf2);
        txt13.setTypeface(tf2);

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

        chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // change your tag here

                if (isChecked) {
                    chkBox.setTag("Y");
                    Log.v("Notification_Activity", "Inside CheckBox checked ");
                    AppPreferences.setConvertTone(Notification_Activity.this, "true");
                    System.out.println("chkkkkvalue :- " + AppPreferences.getConvertTone(Notification_Activity.this));
                }

                if (chkBox.isChecked() == false) {
                    chkBox.setTag("N");
                    Log.v("Notification_Activity", "Inside CheckBox Unchecked ");
                    AppPreferences.setConvertTone(Notification_Activity.this, "false");
                    System.out.println("chkkkkvalue :- " + AppPreferences.getConvertTone(Notification_Activity.this));
                }
            }
        });

        if (!AppPreferences.getVibrationType(Notification_Activity.this).equalsIgnoreCase("")) {
            txt5.setText(AppPreferences.getVibrationType(Notification_Activity.this));
        }


        if (!AppPreferences.getNotificationRingtone(Notification_Activity.this).equalsIgnoreCase("")) {
            txt3.setText(AppPreferences.getNotificationRingtone(Notification_Activity.this));
        }


        if (AppPreferences.getConvertTone(Notification_Activity.this).equalsIgnoreCase("") || AppPreferences.getConvertTone(Notification_Activity.this).equalsIgnoreCase("true")) {
            chkBox.setChecked(true);
        } else {
            chkBox.setChecked(false);
        }


        if (AppPreferences.getTotf(Notification_Activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(Notification_Activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);

        }


        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Notification_Activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Notification_Activity.this, "0");
        }

        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Notification_Activity.this, "0");
                user.setVisibility(View.GONE);
                return true;
            }
        });
        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Notification_Activity.this, "1");
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
                Intent intent = new Intent(Notification_Activity.this, Languagelearn_activity.class);
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
                Intent intent = new Intent(Notification_Activity.this, Favoirite_Activity.class);
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
//                Intent intent = new Intent(Notification_Activity.this, EditProfile_Activity.class);
//                startActivity(intent);
//            }
//        });


        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 5);
            }
        });
        vibrateLinearLayout = (LinearLayout) findViewById(R.id.vibrateLinearLayout);
        vibrateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibratedialog();
            }
        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        String ringtonestring = null;
        Log.d("onActivityResult", requestCode + "::" + resultCode);

        if (resultCode == RESULT_OK) {

            Uri uriR = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uriR != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, uriR);
                String rintone_URI = uriR.toString();
                String ringtoneName = ringtone.getTitle(this);
                Log.v("Notification Activity", "Selected Ringtone Name :-" + ringtoneName);
                Log.v("Notification Activity", "Selected Ringtone URi :-" + rintone_URI);
                txt3.setText(ringtoneName);
                AppPreferences.setNotificationRingtone(Notification_Activity.this, ringtoneName, rintone_URI);
            }
        }
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

    private void vibratedialog() {
        int index;
        if (AppPreferences.getVibrationIndex(Notification_Activity.this) != 0) {
            index = AppPreferences.getVibrationIndex(Notification_Activity.this);
            Log.v("Notification_Activity", "Vibration dialog index :- " + index);
        } else {
            index = -1;
            Log.v("Notification_Activity", "Vibration dialog index :- " + index);
        }

        final CharSequence[] photo = {"Off", "Default", "Short", "Long"};
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        TextView title = new TextView(this);
// You Can Customise your Title here
        title.setText("Vibrate");
        title.setBackgroundColor(Color.TRANSPARENT);
        title.setPadding(10, 30, 10, 20);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getResources().getColor(R.color.colorPrimary));
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);

        alert.setCustomTitle(title);
        alert.setCancelable(false);
        alert.setSingleChoiceItems(photo, index, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (photo[which] == "Off") {
                            selectedOption = "Off";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            txt5.setText(selectedOption);
                            AppPreferences.setVibrationType(Notification_Activity.this, selectedOption);
                            AppPreferences.setVibrationIndex(Notification_Activity.this, 0);

                        } else if (photo[which] == "Default") {

                            selectedOption = "Default";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            txt5.setText(selectedOption);
                            AppPreferences.setVibrationType(Notification_Activity.this, selectedOption);
                            AppPreferences.setVibrationIndex(Notification_Activity.this, 1);

                        } else if (photo[which] == "Short") {

                            selectedOption = "Short";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            txt5.setText(selectedOption);
                            AppPreferences.setVibrationType(Notification_Activity.this, selectedOption);
                            AppPreferences.setVibrationIndex(Notification_Activity.this, 2);

                            Intent intentVibrate = new Intent(getApplicationContext(), VibrateService.class);
                            intentVibrate.putExtra("vibrateType", selectedOption);
                            startService(intentVibrate);

                        } else if (photo[which] == "Long") {

                            selectedOption = "Long";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            txt5.setText(selectedOption);
                            AppPreferences.setVibrationType(Notification_Activity.this, selectedOption);
                            AppPreferences.setVibrationIndex(Notification_Activity.this, 3);

                            Intent intentVibrate = new Intent(getApplicationContext(), VibrateService.class);
                            intentVibrate.putExtra("vibrateType", selectedOption);
                            startService(intentVibrate);
                        }
                    }
                });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Log.v("Notification", "Out side vibratedialog () Selected Option :-" + selectedOption);

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();

        Button negButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button posiButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        if (negButton != null) {
            negButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            negButton.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if (posiButton != null) {
            posiButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            posiButton.setTypeface(Typeface.DEFAULT_BOLD);
        }

/* String vibText = txt5.getText().toString().trim();
 Log.v("Notification", "Selected Option in Button OnClick Listener :-" + vibText);
 Intent intentVibrate = new Intent(getApplicationContext(), VibrateService.class);
 intentVibrate.putExtra("vibrateType", vibText);
 startService(intentVibrate);*/

    }


}
