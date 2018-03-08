package com.speakameqb.Activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jackandphantom.circularprogressbar.CircleProgressbar;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.model.QBPresence;
import com.speakameqb.Beans.Image;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;
import com.speakameqb.Xmpp.ChatMessage;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Function;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jivesoftware.smack.SmackException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfile_Activity extends AnimRootActivity {
    private static final String TAG = "EditProfile";
    //keep track of camera capture intent
    public static ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star,
            on_image, off_image, star_blue, user, user_blue, user_profile;
    final int CAMERA_CAPTURE = 1, PIC_CROP = 2;
    protected BottomSheetLayout bottomSheetLayout;
    //keep track of cropping intent
    Bitmap thePic;
    TextView title_name, txt2, txt3, editstatus;
    EditText editText_name;
    String Status, Encoded_userimage = "", Username, currentDateTimeString;
    CircleImageView prof_pic;
    //captured picture uri
    Typeface tf3, tf2;
    RecyclerView recyclerView;
    Gson gson = new Gson();
    CircleProgressbar imageProgressBar;
    ProgressBar circular_progress_bar;
    int currentPosition = 0, imageListSize = 0;
    File mImageFile;
    private Uri picUri;
    private HorizontalAdapter horizontalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_);

        //

        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottom_sheet_layout);
        bottomSheetLayout.setPeekOnDismiss(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        initViews();

        setListener();

        permission();


    }

    private void permission() {

        Function.cameraPermisstion(EditProfile_Activity.this, 1);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        currentDateTimeString = dateFormatter.format(today);
        Log.d("currentdatetime", currentDateTimeString);


    }

    private void setListener() {

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_blue.setVisibility(View.VISIBLE);
                language.setVisibility(View.GONE);
                setting_blue.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(EditProfile_Activity.this, Languagelearn_activity.class);
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

                Intent intent = new Intent(EditProfile_Activity.this, TwoTab_Activity.class);
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
                Intent intent = new Intent(EditProfile_Activity.this, Favoirite_Activity.class);
                startActivity(intent);
            }
        });


        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(EditProfile_Activity.this, "0");
                user.setVisibility(View.GONE);
                return true;
            }
        });
        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(EditProfile_Activity.this, "1");
                user_blue.setVisibility(View.GONE);
                return true;
            }
        });


        editstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatestatusDialog();
            }
        });

        prof_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showMenuSheet(MenuSheetView.MenuType.LIST);
                showMenuSheet(MenuSheetView.MenuType.GRID);
            }
        });

        String getprofileImageArray = AppPreferences.getprofileImageArray(EditProfile_Activity.this);
        Log.v(TAG, "Getting data from Prefrences :- " + getprofileImageArray);
        if (!getprofileImageArray.equalsIgnoreCase(" ")) {
            if (!AppPreferences.getprofileImageArray(EditProfile_Activity.this).equalsIgnoreCase("")) {
                ArrayList<Image> imageArrayList = gson.fromJson(getprofileImageArray, new TypeToken<ArrayList<Image>>() {
                }.getType());
                Log.v(TAG, "if no null Arraylist after getting prefrences :- " + imageArrayList);
                setAdapter(imageArrayList);
            }
        } else {
            recyclerView.setVisibility(View.GONE);
        }

    }

    private void showMenuSheet(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(EditProfile_Activity.this, menuType, "Choose Action", new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(EditProfile_Activity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }

                        if (item.getItemId() == R.id.item_gallery_pp) {

                            Intent intent = new Intent(getApplicationContext(), AlbumSelectActivity.class);
                            startActivityForResult(intent, AppConstants.ALBUMSELECT_REQUEST_CODE);

                        } else if (item.getItemId() == R.id.item_delete_pp) {

                            Username = editText_name.getText().toString();
                            Status = editstatus.getText().toString();

                            //Toast.makeText(EditProfile_Activity.this, "Remove", Toast.LENGTH_SHORT).show();
                            circular_progress_bar.setVisibility(View.GONE);
                            String name = AppPreferences.getFirstUsername(EditProfile_Activity.this);
                            if (recyclerView.getVisibility() == View.VISIBLE) {
                                AppPreferences.clearprofileImageArray(EditProfile_Activity.this);
                                AppPreferences.setFirstUsername(EditProfile_Activity.this, name);
                                editText_name.setText(name);
                                int size = horizontalAdapter.getItemCount();
                                horizontalAdapter.notifyItemRangeRemoved(0, size);
                                recyclerView.setAdapter(horizontalAdapter);
                                recyclerView.setVisibility(View.GONE);
                                Log.v(TAG, "NotifyitemRangeRemoved :--- " + horizontalAdapter.getItemCount());
                            }

                            prof_pic.setImageResource(R.drawable.profile_default);

                            Bitmap myBitmap = ((BitmapDrawable) prof_pic.getDrawable()).getBitmap();
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                            if (Function.isConnectingToInternet(EditProfile_Activity.this)) {
                                new UpdateprofileAsynch().execute();
                            } else {
                                Toast.makeText(getApplicationContext(), "Internet not Connected !", Toast.LENGTH_SHORT).show();
                            }
                            AppPreferences.setFirstUsername(EditProfile_Activity.this, editText_name.getText().toString());
                            Log.v(TAG, "btn_remove username edt value : --- " + editText_name.getText().toString());
                        }

                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.image_function);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void initViews() {

        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        tf3 = Typeface.createFromAsset(getAssets(), "Raleway-SemiBold.ttf");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        title_name = (TextView) findViewById(R.id.title_name);
        txt2 = (TextView) findViewById(R.id.nametext);
        txt3 = (TextView) findViewById(R.id.statustext);
        title_name.setText("Edit Profile");
        title_name.setTypeface(tf1);
        editstatus = (TextView) findViewById(R.id.edit_status);
        editText_name = (EditText) findViewById(R.id.edit_name);
        prof_pic = (CircleImageView) findViewById(R.id.profile_picture);

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


        editText_name.setTypeface(tf2);
        editstatus.setTypeface(tf2);

        txt2.setTypeface(tf3);
        txt3.setTypeface(tf3);


        if (AppPreferences.getTotf(EditProfile_Activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(EditProfile_Activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);
        }

        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(EditProfile_Activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(EditProfile_Activity.this, "0");
        }

        editText_name.setText(AppPreferences.getFirstUsername(EditProfile_Activity.this));
        Log.v(TAG, "Init View EDIT_TEXT_NAME :--- " + AppPreferences.getFirstUsername(EditProfile_Activity.this));

        if (AppPreferences.getUserstatus(EditProfile_Activity.this).equalsIgnoreCase("")) {
            editstatus.setText("Can't talk SpeakAme Only");
        } else {
            editstatus.setText(AppPreferences.getUserstatus(EditProfile_Activity.this));
        }

        if (AppPreferences.getUserprofile(EditProfile_Activity.this).equalsIgnoreCase("")) {

        } else {
            Picasso.with(getApplicationContext())
                    .load(AppPreferences.getUserprofile(EditProfile_Activity.this))
                    .placeholder(R.drawable.profile_default)
                    .centerCrop()
                    .error(R.drawable.profile_default)
                    .resize(200, 200)
                    .into(prof_pic);
        }

        circular_progress_bar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        System.out.println("profpic :- " + AppPreferences.getUserprofile(EditProfile_Activity.this));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_display_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

               /* Intent intent = new Intent(EditProfile_Activity.this, Setting_Activity.class);
                startActivity(intent);*/
                finish();
                break;
            case R.id.done:
                Username = editText_name.getText().toString();
                Status = editstatus.getText().toString();

                Log.v(TAG, "Curent Profile Pic Position :- " + currentPosition);
                Log.v(TAG, "Curent Profile Pic Position  Sizes :- " + imageListSize);

                AppPreferences.setprofileImagePos(EditProfile_Activity.this, currentPosition, imageListSize);
                Log.v(TAG, "AppPreference EDIT_TEXT_NAME :--- " + AppPreferences.getFirstUsername(EditProfile_Activity.this));
                Log.v(TAG, "editText_name.getText() :--- " + Username);
                if (mImageFile != null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(mImageFile.getAbsolutePath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
                if (Function.isConnectingToInternet(EditProfile_Activity.this)) {
                    new UpdateprofileAsynch().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Internet not Connected !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void UpdatestatusDialog() {

        final Dialog markerDialog = new Dialog(this);
        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
//        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        window.setGravity(Gravity.CENTER);


        markerDialog.setContentView(R.layout.status_edit_dialog);
        Button update = (Button) markerDialog.findViewById(R.id.updatestatus);
        TextView updatehead = (TextView) markerDialog.findViewById(R.id.txtupdate);
        final EditText stat_edit = (EditText) markerDialog.findViewById(R.id.status_edit);
        stat_edit.setTypeface(tf2);
        update.setTypeface(tf3);
        updatehead.setTypeface(tf3);

        if (AppPreferences.getUserstatus(EditProfile_Activity.this).equalsIgnoreCase("")) {
            stat_edit.setText("Can't talk SpeakAme Only");
        } else {
            stat_edit.setText(AppPreferences.getUserstatus(EditProfile_Activity.this));
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Status = stat_edit.getText().toString();
                Log.w("EditProfile_Activity", "Update status:-" + Status);
                AppPreferences.setUserstatus(EditProfile_Activity.this, Status);
                markerDialog.dismiss();
                editstatus.setText(Status);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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

    private void senduseriD() {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "speaka_updateProfile");
            jsonObject.put("user_id", AppPreferences.getLoginId(EditProfile_Activity.this));
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("send>json--" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(EditProfile_Activity.this);
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

    private void sendImageinPresence() {

        QBRosterListener rosterListener = new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {
            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {

            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {
            }

            @Override
            public void presenceChanged(QBPresence presence) {
                Log.v("EditProfile", "userID...........=" + presence);
            }
        };
        QBSubscriptionListener subscriptionListener = new QBSubscriptionListener() {
            @Override
            public void subscriptionRequested(int userId) {
            }
        };


        QBRoster chatRoster = QBChatService.getInstance().getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);
        chatRoster.addRosterListener(rosterListener);

        JSONObject presenceJson = new JSONObject();
        try {
            presenceJson.put("sender_id", AppPreferences.getLoginId(EditProfile_Activity.this));
            presenceJson.put("profile_image", AppPreferences.getUserprofile(EditProfile_Activity.this));
            presenceJson.put("profile_name", editText_name.getText().toString());
            presenceJson.put("chat_Type", "singleChat");
            presenceJson.put("profile_language", "");
            presenceJson.put("status", editstatus.getText().toString());

            Log.v(TAG, "Json object sending as a status while sending presence :- " + presenceJson);

        } catch (JSONException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

//................................................................................................//
        QBPresence presence = new QBPresence(QBPresence.Type.online, presenceJson.toString(), 1, QBPresence.Mode.available);

        Log.v(TAG, "User Presnece in Edit user image 19 dec :- " + presence);
        try {
            chatRoster.sendPresence(presence);
        } catch (SmackException.NotConnectedException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Image> imageArrayList = new ArrayList<>();
        Log.v(TAG, " On Activity Result ");

        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_CAPTURE) {
                picUri = data.getData();
                performCrop();
            }//user is returning from cropping the files
            else if (requestCode == PIC_CROP) {
//get the returned data
                Bundle extras = data.getExtras();
//get the cropped bitmap
                thePic = extras.getParcelable("data");
                Log.d("pictureimage", String.valueOf(thePic));

                prof_pic.setImageBitmap(thePic);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.v(TAG, "" + Encoded_userimage);
                Log.d("pictureimage", Encoded_userimage);


            }

        }

        if (requestCode == AppConstants.ALBUMSELECT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Log.v(TAG, "On Activity Result : -- " + data);
            imageArrayList.clear();
            Log.v(TAG, "Image Array List Clear " + imageArrayList);
            imageArrayList = data.getParcelableArrayListExtra(AppConstants.INTENT_EXTRA_IMAGES);//intent.getParcelableArrayListExtra(AppConstants.INTENT_EXTRA_IMAGES);
            Log.v(TAG, "Image Array List Received :- " + imageArrayList);
            for (int i = 0; i < imageArrayList.size(); i++) {
                String path = imageArrayList.get(i).path;
                Log.v(TAG, "Recevied Path of image from arraylist index : " + i + " " + path);
            }
            /*if (linearLayout != null){
                linearLayout.removeAllViews();
            }
            scrollViewPagerAdapter = new ScrollViewPagerAdapter(this, imageArrayList);
            viewPager.setAdapter(scrollViewPagerAdapter);
            viewPager.setOffscreenPageLimit(10); // how many images to load into memory*/

//            dsvvvvvvvvvvvvvvvvvvvvvvvvvv
//                    sdvvvvvvvvvvvvvvvvvvvvv

//            AppPreferences.setImageList();
            String profilePicJson = gson.toJson(imageArrayList);

            AppPreferences.setprofileImageArray(EditProfile_Activity.this, profilePicJson);

            Log.v(TAG, "Arraylist before from prefrences :- " + imageArrayList);
            Log.v(TAG, "Arraylist getting :- " + AppPreferences.getprofileImageArray(EditProfile_Activity.this));
            setAdapter(imageArrayList);

            try {
                recyclerView.setAdapter(horizontalAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }


            //---------------- ----------  SOMIL 27 FEB   -------------------------------
            String getprofileImageArray = AppPreferences.getprofileImageArray(EditProfile_Activity.this);
            Log.v(TAG, "Getting data from Prefrences :- " + getprofileImageArray);
            if (!getprofileImageArray.equalsIgnoreCase(" ")) {
                if (!AppPreferences.getprofileImageArray(EditProfile_Activity.this).equalsIgnoreCase("")) {

                    ArrayList<Image> imageArrayLists = gson.fromJson(getprofileImageArray, new TypeToken<ArrayList<Image>>() {
                    }.getType());
                    Log.v(TAG, "if no null Arraylist after getting prefrences :- " + imageArrayLists);
                    Log.v(TAG, "PAth : -- " + imageArrayLists.get(0).path);

                    Username = editText_name.getText().toString();
                    Status = editstatus.getText().toString();

                    AppPreferences.setFirstUsername(EditProfile_Activity.this, Username);
                    Bitmap myBitmap = BitmapFactory.decodeFile(String.valueOf(imageArrayLists.get(0).path));
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    if (Function.isConnectingToInternet(EditProfile_Activity.this)) {
                        new UpdateprofileAsynch().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Internet not Connected !", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }

    private void setAdapter(ArrayList<Image> imageArrayList) {

        recyclerView.setVisibility(View.VISIBLE);
        horizontalAdapter = new HorizontalAdapter(this, imageArrayList);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(horizontalAdapter);
    }


    // ------------------------------   ADAPTER FOR IMAGE AND SCROLLVIEW  --------------------------------------

    private class UpdateprofileAsynch extends AsyncTask<Void, Void, String> {
        private AlertDialog mProgressDialog;
        private JSONObject jsonObj;
        private String status;

        public UpdateprofileAsynch() {

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(EditProfile_Activity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("Edit profile successfully : ------------ "
                        + AppConstants.COMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.COMMONURL);
                jsonObj = new JSONObject();

                jsonObj.put("method", "editProfile");
                jsonObj.put("userImage", Encoded_userimage);
                jsonObj.put("userProfileStatus", Status);
                jsonObj.put("username", Username);
                Log.v(TAG, "JSoN.put username :--- " + Username);
                jsonObj.put("userId", AppPreferences.getLoginId(EditProfile_Activity.this));
                jsonObj.put("dateTime", currentDateTimeString);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());
                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString());


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Request JSON is : " + jsonArray.toString());
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
                if (jsonString != null) {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    status = jsonObj.getString("status");
                    System.out.println("jsonstring------" + jsonString);
                    if (jsonObj.getString("status").equalsIgnoreCase("200")) {

                        JSONArray resultArray = jsonObj.getJSONArray("result");
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject2 = resultArray.getJSONObject(i);
                            //AppPreferences.setLoginId(EditProfile_Activity.this, Integer.parseInt(jsonObject2.getString("userId")));
                            //AppPreferences.setMobileuser(EditProfile_Activity.this, jsonObject2.getString("mobile"));
                            AppPreferences.setFirstUsername(EditProfile_Activity.this, jsonObject2.getString("username"));
                            Log.v(TAG, "Json se username :-- " + jsonObject2.getString("username"));
                            AppPreferences.setUserprofile(EditProfile_Activity.this, jsonObject2.getString("userImage"));
                            //AppPreferences.setEmail(EditProfile_Activity.this, jsonObject2.getString("email"));
                            //AppPreferences.setCountrycode(EditProfile_Activity.this, jsonObject2.getString("countrycode"));
                            //AppPreferences.setUsercity(EditProfile_Activity.this, jsonObject2.getString("country"));
                            //AppPreferences.setUSERLANGUAGE(EditProfile_Activity.this, jsonObject2.getString("language"));
                            //AppPreferences.setUsergender(EditProfile_Activity.this, jsonObject2.getString("gender"));
                            AppPreferences.setUserstatus(EditProfile_Activity.this, jsonObject2.getString("userProfileStatus"));
                        }

                        sendImageinPresence();
//                        XmppConneceted activity = new XmppConneceted();
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.Groupimage = "updateProPic";
                        chatMessage.receiver = AppPreferences.getMobileuser(EditProfile_Activity.this);
                        chatMessage.ReciverFriendImage = resultArray.getJSONObject(0).getString("userImage");
                        chatMessage.userStatus = resultArray.getJSONObject(0).getString("userProfileStatus");

//                        activity.getmService().xmpp.updateProfile(chatMessage);
                    } else if (jsonObj.getString("status").equalsIgnoreCase("400")) {
                        status = "400";
                    } else if (jsonObj.getString("status").equalsIgnoreCase("500")) {
                        status = "500";
                    } else if (jsonObj.getString("status").equalsIgnoreCase("600")) {
                        status = "600";
                    }
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
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
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            Log.d(TAG, "status :- " + status + "");

            if (!status.equalsIgnoreCase(null) || !status.equalsIgnoreCase("null") || !status.equalsIgnoreCase("")) {

                if (status.equalsIgnoreCase("200")) {
                    Snackbar.make(findViewById(android.R.id.content), "Profile updated successfully.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    senduseriD();
                    //Intent intent = new Intent(EditProfile_Activity.this, EditProfile_Activity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(intent);
                    Picasso.with(getApplicationContext())
                            .load(AppPreferences.getUserprofile(EditProfile_Activity.this))
                            .placeholder(R.drawable.profile_default)
                            .centerCrop()
                            .error(R.drawable.profile_default)
                            .resize(200, 200)
                            .into(prof_pic);
                } else {

                    Snackbar.make(findViewById(android.R.id.content), "Check network connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            } else {

                Snackbar.make(findViewById(android.R.id.content), "Server not responding.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        }
    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        ArrayList<Image> horizontalList;
        Context context;

        public HorizontalAdapter(Context context, ArrayList<Image> horizontalList) {
            this.context = context;
            this.horizontalList = horizontalList;
        }

        @Override
        public HorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_gallery_item, parent, false);

            return new HorizontalAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final HorizontalAdapter.MyViewHolder holder, final int position) {

            File imgFile = new File(horizontalList.get(position).path);

            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Bitmap mBitmap = ThumbnailUtils.extractThumbnail(myBitmap, 64, 64);
                ;
                holder.imageView.setImageBitmap(mBitmap);
            } else {
                Log.v(TAG, "selected File not exists");
                Toast.makeText(context, "File Not Exists", Toast.LENGTH_SHORT).show();
            }
            //holder.imageView.setImageResource(horizontalList.get(position).path);
            int pos = AppPreferences.getprofileImagePos(EditProfile_Activity.this);

            /*horizontalList.get(pos).isSelected = true;

            if (horizontalList.get(position).isSelected) {
                holder.view.setAlpha(0.5f);
                ((FrameLayout) holder.view).setForeground(context.getResources().getDrawable(R.drawable.ic_done_green));

            } else {
                holder.view.setAlpha(0.0f);
                ((FrameLayout) holder.view).setForeground(null);
            }*/


            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    circular_progress_bar.setVisibility(View.VISIBLE);
                    final ObjectAnimator objanim = ObjectAnimator.ofInt(circular_progress_bar, "progress", 0, 100);
                    objanim.setDuration(15000);
                    objanim.setInterpolator(new DecelerateInterpolator());
                    objanim.start();

                    currentPosition = position;
                    imageListSize = horizontalList.size();
                    String list = horizontalList.get(position).path;


                    Log.v(TAG, "Getting profile position :- " + AppPreferences.getprofileImagePos(EditProfile_Activity.this));
                    Log.v(TAG, "Getting profile size :- " + AppPreferences.getprofileImageSize(EditProfile_Activity.this));

                    mImageFile = new File(horizontalList.get(position).path);

                    Picasso.with(context)
                            .load(mImageFile)
                            .into(prof_pic, new Callback() {
                                @Override
                                public void onSuccess() {
//                                    prof_pic.setImageBitmap(myBitmap);
                                    objanim.cancel();

                                }

                                @Override
                                public void onError() {
                                    objanim.end();
                                    circular_progress_bar.setVisibility(View.GONE);
                                }
                            });


                   /* Glide.with(context)
                            .load(list)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                                    objanim.cancel();
                                    objanim.end();

                                    //imageViewCircle.setImage(ImageSource.bitmap(bitmap));
                                    //RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),
                                    //      Bitmap.createScaledBitmap(bitmap, 50, 50, false));
                                    prof_pic.setImageBitmap(bitmap);
                                }
                            });*/
                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList != null ? horizontalList.size() : 0;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            View view;
            ImageView imageView;

            public MyViewHolder(View view) {
                super(view);
                this.view = view;
                imageView = (ImageView) view.findViewById(R.id.image_view);
            }
        }
    }
}
