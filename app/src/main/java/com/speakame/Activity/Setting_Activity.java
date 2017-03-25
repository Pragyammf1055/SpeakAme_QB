package com.speakame.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.ConnectionDetector;
import com.speakame.utils.JSONParser;
import com.speakame.utils.ListCountry;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class Setting_Activity extends AnimRootActivity {
    public static ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star,
            on_image, off_image, star_blue, user, user_blue, user_profile;
    TextView toolbartext, username;
    TextView txt1, txt2, txt3, txt4, txt5, txt6, txt7, txt8, txtlanguage;
    LinearLayout l1, l2, l3, l4, l6;
    ImageView info_img, disconect_img, conect_image;
    String currentDateTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Setting");
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
        username = (TextView) findViewById(R.id.username);
        user_profile = (ImageView) findViewById(R.id.user_profile);

        on_image = (ImageView) findViewById(R.id.on_image);
        off_image = (ImageView) findViewById(R.id.off_image);
        info_img = (ImageView) findViewById(R.id.info_image);
        disconect_img = (ImageView) findViewById(R.id.disconnect_image);
        conect_image = (ImageView) findViewById(R.id.conect_img);

        txt1 = (TextView) findViewById(R.id.username);
        txt2 = (TextView) findViewById(R.id.txtaccount);
        txt3 = (TextView) findViewById(R.id.txtchatsetting);
        txt4 = (TextView) findViewById(R.id.txtlanguage);
        txt5 = (TextView) findViewById(R.id.txtnotify);
        txt6 = (TextView) findViewById(R.id.txtnetstatus);
        txt7 = (TextView) findViewById(R.id.txtlogout);
        txt8 = (TextView) findViewById(R.id.totfsetting);
        txtlanguage = (TextView) findViewById(R.id.languagetext);
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        txt1.setTypeface(tf2);
        txt2.setTypeface(tf2);
        txt3.setTypeface(tf2);
        txt4.setTypeface(tf2);
        txt5.setTypeface(tf2);
        txt6.setTypeface(tf2);
        txt7.setTypeface(tf2);
        txt8.setTypeface(tf2);
        txtlanguage.setTypeface(tf2);
        toolbartext.setTypeface(tf1);


        username.setText(AppPreferences.getFirstUsername(Setting_Activity.this));

        final Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(this.openFileInput("InterimPoster"));
            final Drawable lowResPoster = new BitmapDrawable(getResources(), bitmap);
            user_profile.setImageDrawable(lowResPoster);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

//                    if (AppPreferences.getUserprofile(Setting_Activity.this).equalsIgnoreCase("")) {
//                        user_profile.setBackgroundResource(R.drawable.user_icon);
//
//                    } else {
                        Picasso.with(getApplicationContext()).load(AppPreferences.getUserprofile(Setting_Activity.this))
                                .error(R.drawable.user_icon)
                                .placeholder(R.drawable.user_icon)
                                .fit()
                                .centerCrop()
                                .noFade()
                                .resize(200, 200)
                                .into(user_profile);
                 //   }


//                    Picasso.with(c).load("http://image.tmdb.org/t/p/w780" + mMovieInfo[2])
//                            // still need placeholder here otherwise will flash of white image
//                            .placeholder(lowResPoster)
//                            .error(lowResPoster)
//                            .fit()
//                            .centerCrop()
//                            .noFade() // without this image replacement will not be smooth
//                            .into(iv);
                }
            };
            handler.postDelayed(runnable, 800);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        setting_blue.setVisibility(View.VISIBLE);
        setting.setVisibility(View.GONE);

        if ((ConnectionDetector
                .isConnectingToInternet(Setting_Activity.this))) {
            conect_image.setVisibility(View.VISIBLE);
            disconect_img.setVisibility(View.GONE);

        } else {
            disconect_img.setVisibility(View.VISIBLE);
            conect_image.setVisibility(View.GONE);
        }

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        currentDateTimeString = dateFormatter.format(today);
        Log.d("currentdatetime", currentDateTimeString);

        txtlanguage.setText(AppPreferences.getUSERLANGUAGE(Setting_Activity.this));

        if (AppPreferences.getTotf(Setting_Activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(Setting_Activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);

        }


        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Setting_Activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Setting_Activity.this, "0");
        }

        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Setting_Activity.this, "0");
                user.setVisibility(View.GONE);

                return true;
            }
        });
        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Setting_Activity.this, "1");
                user_blue.setVisibility(View.GONE);

                return true;
            }
        });


//        info_img.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                infodialog();
//
//                return false;
//            }
//        });


        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_blue.setVisibility(View.VISIBLE);
                language.setVisibility(View.GONE);
                setting_blue.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Setting_Activity.this, Languagelearn_activity.class);
                startActivity(intent);
                finish();
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
                Intent intent = new Intent(Setting_Activity.this, Favoirite_Activity.class);
                startActivity(intent);
                finish();
            }
        });
//        user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                user.setVisibility(View.GONE);
//                user_blue.setVisibility(View.VISIBLE);
//                setting_blue.setVisibility(View.GONE);
//                setting.setVisibility(View.VISIBLE);
//                Intent intent = new Intent(Setting_Activity.this, EditProfile_Activity.class);
//                startActivity(intent);
//            }
//        });

        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        l3 = (LinearLayout) findViewById(R.id.l3);
        l4 = (LinearLayout) findViewById(R.id.changelanguage);
        l6 = (LinearLayout) findViewById(R.id.logoutlayout);
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting_Activity.this, Account_Activity.class);
                startActivity(intent);
                finish();

            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting_Activity.this, ChatSetting.class);
                startActivity(intent);
                finish();

            }
        });
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting_Activity.this, Notification_Activity.class);
                startActivity(intent);
                finish();

            }
        });

        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changelanguageDialog();

            }
        });


        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting_Activity.this, EditProfile_Activity.class);
                startActivity(intent);
                finish();
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

    public void changelanguageDialog() {

        System.out.println("language" + AppPreferences.getUSERLANGUAGE(Setting_Activity.this));

        final Dialog markerDialog = new Dialog(this);
        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
//        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        window.setGravity(Gravity.CENTER);

        //  window.setBackgroundDrawableResource(R.drawable.rounded_corner_dialog);
        //window.s
        markerDialog.setContentView(R.layout.languagechange_popup);
        final Button btn_done = (Button) markerDialog.findViewById(R.id.done_btn);
        final Button btn_cancel = (Button) markerDialog.findViewById(R.id.cancel_btn);

        final TextView text1 = (TextView) markerDialog.findViewById(R.id.texthead);
        final TextView text2 = (TextView) markerDialog.findViewById(R.id.texttwo);
        Typeface tf3 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        text1.setTypeface(tf3);
        text2.setTypeface(tf3);
        btn_done.setTypeface(tf3);
        btn_cancel.setTypeface(tf3);
        //String[] ITEMS = getResources().getStringArray(R.array.country);
        ListCountry country = new ListCountry();
        List<String> ITEMS = country.getAllLanguages();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final SearchableSpinner spinner = (SearchableSpinner) markerDialog.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        String text = "Select Language";
        spinner.setTitle(text);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                btn_cancel.setBackgroundResource(R.drawable.buttonshape);
//                btn_done.setBackgroundResource(R.drawable.buttonshape_two);
                JSONObject object = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                try {
                    object.put("method", AppConstants.CHANGELANGUAGE);
                    object.put("user_id", AppPreferences.getLoginId(Setting_Activity.this));
                    object.put("change_language", spinner.getSelectedItem().toString());
                    jsonArray.put(object);

                    System.out.println("SentJson" + jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONParser jsonParser = new JSONParser(Setting_Activity.this);
                jsonParser.parseVollyJsonArray(AppConstants.COMMONURL, 1, jsonArray, new VolleyCallback() {
                    @Override
                    public void backResponse(String response) {
                        System.out.println("languageresponse" + response);

                        senduseriD();
                        AppPreferences.setUSERLANGUAGE(Setting_Activity.this, spinner.getSelectedItem().toString());
                        txtlanguage.setText(AppPreferences.getUSERLANGUAGE(Setting_Activity.this));
                        markerDialog.dismiss();
                    }
                });

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                btn_cancel.setBackgroundResource(R.drawable.buttonshape_two);
//                btn_done.setBackgroundResource(R.drawable.buttonshape);
                markerDialog.dismiss();
                txtlanguage.setText(AppPreferences.getUSERLANGUAGE(Setting_Activity.this));
            }
        });

        markerDialog.setCanceledOnTouchOutside(false);
        markerDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("settingresume");
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

        if (AppPreferences.getUserprofile(Setting_Activity.this).equalsIgnoreCase("")) {
            user_profile.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(getApplicationContext()).load(AppPreferences.getUserprofile(Setting_Activity.this))
                    .error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(user_profile);
        }

    }


    private void senduseriD() {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "speka_updateLanguage");
            jsonObject.put("user_id", AppPreferences.getLoginId(Setting_Activity.this));
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(Setting_Activity.this);
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


                            }

                            Toast.makeText(getApplicationContext(), "successfully", Toast.LENGTH_LONG).show();


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.COMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }


}
