package com.speakame.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Privacy_Activity extends AnimRootActivity {
    private static final String TAG = "Profile_Activity";
    public static ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star,
            star_blue, user, user_blue, user_profile;
    final CharSequence[] photo = {"Everyone", "MyContacts", "Nobody"};
    TextView toolbartext, text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13;
    String MyprofilePhoto, MyStatus;
    ProgressDialog mProgressDialog;
    String selectedOption;
    LinearLayout statusLayout, profilePhotoLayout, lastSeenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Privacy");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

        lastSeenLayout = (LinearLayout) findViewById(R.id.lastSeenLayout);
        profilePhotoLayout = (LinearLayout) findViewById(R.id.profilePhotoLayout);
        statusLayout = (LinearLayout) findViewById(R.id.statusLayout);

        text1 = (TextView) findViewById(R.id.textViewhead);
        text2 = (TextView) findViewById(R.id.textlastseen);
        text3 = (TextView) findViewById(R.id.textseen);
        text4 = (TextView) findViewById(R.id.textprofile);
        text5 = (TextView) findViewById(R.id.textpicstatus);
        text6 = (TextView) findViewById(R.id.textstatus);
        text7 = (TextView) findViewById(R.id.textstatusseen);
        text8 = (TextView) findViewById(R.id.textdemocontent);
        text9 = (TextView) findViewById(R.id.textmessaging);
        text10 = (TextView) findViewById(R.id.textblockcontact);
        text11 = (TextView) findViewById(R.id.textcontactseen);
        text12 = (TextView) findViewById(R.id.textrecipt);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        text1.setTypeface(tf2);
        text2.setTypeface(tf2);
        text3.setTypeface(tf2);
        text4.setTypeface(tf2);
        text5.setTypeface(tf2);
        text6.setTypeface(tf2);
        text7.setTypeface(tf2);
        text8.setTypeface(tf2);
        text9.setTypeface(tf2);
        text10.setTypeface(tf2);
        text11.setTypeface(tf2);
        text12.setTypeface(tf2);

        text10.setText("Blocked Contact:" + " " + AppPreferences.getBlockList(Privacy_Activity.this));

        if (AppPreferences.getPicprivacy(Privacy_Activity.this).equalsIgnoreCase("")) {
            text5.setText("Everyone");
        } else {
            text5.setText(AppPreferences.getPicprivacy(Privacy_Activity.this));
        }

        if (AppPreferences.getStatusprivacy(Privacy_Activity.this).equalsIgnoreCase("")) {
            text7.setText("Everyone");
        } else {
            text7.setText(AppPreferences.getStatusprivacy(Privacy_Activity.this));
        }

        profilePhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacyPicDialog();
            }
        });

        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacyStatusDialog();
            }
        });

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
        if (AppPreferences.getTotf(Privacy_Activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(Privacy_Activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);
        }

        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Privacy_Activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Privacy_Activity.this, "0");
        }

        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Privacy_Activity.this, "0");
                user.setVisibility(View.GONE);
                return true;
            }
        });

        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Privacy_Activity.this, "1");
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
                Intent intent = new Intent(Privacy_Activity.this, Languagelearn_activity.class);
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
                Intent intent = new Intent(Privacy_Activity.this, TwoTab_Activity.class);
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
                Intent intent = new Intent(Privacy_Activity.this, Favoirite_Activity.class);
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
//                Intent intent = new Intent(Privacy_Activity.this, EditProfile_Activity.class);
//                startActivity(intent);
//            }
//        });

        if(! AppPreferences.getLastSeenType(Privacy_Activity.this).equalsIgnoreCase("")){
            text3.setText(AppPreferences.getLastSeenType(Privacy_Activity.this));
        } else {
            text3.setText("Everyone");
        }

        lastSeenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSeenDialog();
            }
        });
    }

    private void lastSeenDialog() {

        int index;
        if( AppPreferences.getLastSeenIndex(Privacy_Activity.this) != 0){
            index = AppPreferences.getLastSeenIndex(Privacy_Activity.this);
            Log.v("Privacy_Activity", "Vibration dialog index :- " + index);
        } else {
            index = 0;
            Log.v("Privacy_Activity", "Vibration dialog index :- " + index);
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.RadioDialogTheme);

        TextView title = new TextView(this);
// You Can Customise your Title here
        title.setText("Last Seen");
        title.setBackgroundColor(Color.TRANSPARENT);
        title.setPadding(10, 20, 10, 20);
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

                        if (photo[which] == "Everyone") {
                            selectedOption = "Everyone";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text3.setText(selectedOption);
                            AppPreferences.setLastSeenType(Privacy_Activity.this, selectedOption);
                            AppPreferences.setLastSeenIndex(Privacy_Activity.this, 0);

                        } else if (photo[which] == "MyContacts") {

                            selectedOption = "MyContacts";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text3.setText(selectedOption);
                            AppPreferences.setLastSeenType(Privacy_Activity.this, selectedOption);
                            AppPreferences.setLastSeenIndex(Privacy_Activity.this, 1);

                        } else if (photo[which] == "Nobody") {

                            selectedOption = "Nobody";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text3.setText(selectedOption);
                            AppPreferences.setLastSeenType(Privacy_Activity.this, selectedOption);
                            AppPreferences.setLastSeenIndex(Privacy_Activity.this, 2);
                        }
                    }
                });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v("Notification", "Out Side Last Seen Selected Option :-" + selectedOption);
                dialogInterface.dismiss();

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
        Button negButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button posiButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        if(negButton != null) {
            negButton .setTextColor(getResources().getColor(R.color.colorPrimary));
            negButton.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if (posiButton != null) {
            posiButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            posiButton.setTypeface(Typeface.DEFAULT_BOLD);
        }

//        alert.show();
    }

    public void privacyPicDialog() {

        int index;
        if( AppPreferences.getProfilePicIndex(Privacy_Activity.this) != 0){
            index = AppPreferences.getProfilePicIndex(Privacy_Activity.this);
            Log.v("Privacy_Activity", "Vibration dialog index :- " + index);
        } else {
            index = 0;
            Log.v("Privacy_Activity", "Vibration dialog index :- " + index);
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.RadioDialogTheme);

        TextView title = new TextView(this);
// You Can Customise your Title here
        title.setText("Profile photo");
        title.setBackgroundColor(Color.TRANSPARENT);
        title.setPadding(10, 20, 10, 20);
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

                        if (photo[which] == "Everyone") {
                            selectedOption = "Everyone";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text5.setText(selectedOption);
                            AppPreferences.setProfilePicIndex(Privacy_Activity.this, 0);

                        } else if (photo[which] == "MyContacts") {

                            selectedOption = "MyContacts";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text5.setText(selectedOption);
                            AppPreferences.setProfilePicIndex(Privacy_Activity.this, 1);

                        } else if (photo[which] == "Nobody") {

                            selectedOption = "Nobody";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text5.setText(selectedOption);
                            AppPreferences.setProfilePicIndex(Privacy_Activity.this, 2);

                        }
                    }
                });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v("Notification", "Out Side Profile Pic Option :-" + selectedOption);
                dialogInterface.dismiss();
                sendPicstatus(selectedOption);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
        Button negButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button posiButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        if(negButton != null) {
            negButton .setTextColor(getResources().getColor(R.color.colorPrimary));
            negButton.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if (posiButton != null) {
            posiButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            posiButton.setTypeface(Typeface.DEFAULT_BOLD);
        }

    }

    public void privacyStatusDialog() {

        int index;
        if( AppPreferences.getStatusIndex(Privacy_Activity.this) != 0){
            index = AppPreferences.getStatusIndex(Privacy_Activity.this);
            Log.v("Privacy_Activity", "Status dialog index :- " + index);
        } else {
            index = 0;
            Log.v("Privacy_Activity", "Status dialog index :- " + index);
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.RadioDialogTheme );

        TextView title = new TextView(this);
// You Can Customise your Title here
        title.setText("Status");
        title.setBackgroundColor(Color.TRANSPARENT);
        title.setPadding(10, 20, 10, 20);
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

                        if (photo[which] == "Everyone") {
                            selectedOption = "Everyone";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text7.setText(selectedOption);
                            AppPreferences.setStatusIndex(Privacy_Activity.this, 0);

                        } else if (photo[which] == "MyContacts") {

                            selectedOption = "MyContacts";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text7.setText(selectedOption);
                            AppPreferences.setStatusIndex(Privacy_Activity.this, 1);

                        } else if (photo[which] == "Nobody") {

                            selectedOption = "Nobody";
                            Log.v("Notification", "Selected Option :-" + selectedOption);
                            text7.setText(selectedOption);
                            AppPreferences.setStatusIndex(Privacy_Activity.this, 2);
                        }
                    }
                });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v("Notification", "Out Side Status Option :-" + selectedOption);
                dialogInterface.dismiss();
                sendPrivacystatus(selectedOption);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
        Button negButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button posiButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        if(negButton != null) {
            negButton .setTextColor(getResources().getColor(R.color.colorPrimary));
            negButton.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if (posiButton != null) {
            posiButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            posiButton.setTypeface(Typeface.DEFAULT_BOLD);
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


    private void sendPicstatus(String PicPrivacy) {
        mProgressDialog = new ProgressDialog(Privacy_Activity.this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "profie_pic_privacy");
            jsonObject.put("profie_pic_setting", PicPrivacy);
            jsonObject.put("user_id", AppPreferences.getLoginId(Privacy_Activity.this));
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(Privacy_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                Log.d("response>>>>>", response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AppPreferences.setPicprivacy(Privacy_Activity.this, topObject.getString("profie_pic_setting"));
                            }
                            String profilePicText = AppPreferences.getPicprivacy(Privacy_Activity.this);
                            Log.v(TAG, "Profile  pic status :- "+ profilePicText);
                            text5.setText(profilePicText);

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }


    private void sendPrivacystatus(String StatusPrivacy) {
        mProgressDialog = new ProgressDialog(Privacy_Activity.this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "status_privacy");
            jsonObject.put("status_setting", StatusPrivacy);
            jsonObject.put("user_id", AppPreferences.getLoginId(Privacy_Activity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(Privacy_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {


                Log.d("response>>>>>", response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AppPreferences.setStatusprivacy(Privacy_Activity.this, topObject.getString("status_setting"));
                            }

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }
            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
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
