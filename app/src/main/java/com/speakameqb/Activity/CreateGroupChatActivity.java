package com.speakameqb.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;
import com.speakameqb.Services.HomeService;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Function;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class CreateGroupChatActivity extends AnimRootActivity implements VolleyCallback, EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;
    //keep track of cropping intent
    final int PIC_CROP = 2;
    ImageView mimageview;
    //EditText meditetext;
    String GroupName, GroupProfileImage, currentDateTimeString, group_id;
    Bitmap thePic;
    MultiUserChat muc;
    String nickname = "yogi";
    String jid = "9993243209@speakameqb/Smack";
    FrameLayout fm;
    ImageView emojiImageView;
    EmojiconEditText emojiconEditText;
    private Uri picUri;
    private AlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        TextView title_name = (TextView) findViewById(R.id.title_name);
        mimageview = (ImageView) findViewById(R.id.imagegroup);
        //meditetext = (EditText) findViewById(R.id.edit_title);

        fm = (FrameLayout) findViewById(R.id.emojicons);
        emojiImageView = (ImageView) findViewById(R.id.emojiImageView);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.edit_title);

        title_name.setText("New Group");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        title_name.setTypeface(tf1);

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        currentDateTimeString = dateFormatter.format(today);
        Log.d("currentdatetime", currentDateTimeString);
        stopService(new Intent(CreateGroupChatActivity.this, HomeService.class));
        Function.cameraPermisstion(CreateGroupChatActivity.this, 1);
        mimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //use standard intent to capture an files

                    Intent galleryIntent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //we will handle the returned data in onActivityResult
                    startActivityForResult(galleryIntent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(CreateGroupChatActivity.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });


        emojiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.setVisibility(View.VISIBLE);
                View view = CreateGroupChatActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });


        emojiconEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fm.setVisibility(View.GONE);
                return false;
            }
        });
        setEmojiconFragment(false);

    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(emojiconEditText);

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(emojiconEditText, emojicon);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ok:
                dismissKeyboard(CreateGroupChatActivity.this);
                GroupName = emojiconEditText.getText().toString();

                if (GroupName.length() == 0) {
                    emojiconEditText.setError("Please enter group name");

                } else {


                    mProgressDialog = new SpotsDialog(CreateGroupChatActivity.this);
                    mProgressDialog.setMessage("Please wait...");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                    JSONObject jsonObj = new JSONObject();
                    JSONArray jsonArray = new JSONArray();

                    try {
                        jsonObj.put("method", AppConstants.CREATEGROUP);
                        jsonObj.put("userId", AppPreferences.getLoginId(getApplicationContext()));
                        jsonObj.put("group_image", GroupProfileImage);
                        jsonObj.put("group_title", GroupName);
                        jsonObj.put("dateTime", currentDateTimeString);

                        jsonArray.put(jsonObj);

                        System.out.println("creategroup" + jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONParser jsonParser = new JSONParser(getApplicationContext());
                    jsonParser.parseVollyJsonArray(AppConstants.COMMONURL, 1, jsonArray, CreateGroupChatActivity.this);
                    System.out.println("AppConstants.CREATEGROUP" + AppConstants.COMMONURL);
                    System.out.println("jsonObject" + jsonObj);

//                Intent intent = new Intent(CreateGroupChatActivity.this, GroupMemberList_Activity.class);
//                startActivity(intent);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                mimageview.setImageBitmap(thePic);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                GroupProfileImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.d("pictureimage", GroupProfileImage);
            }

        }
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
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

    @Override
    public void backResponse(String response) {
        Log.d("response", response);


        if (response != null) {
            try {
                JSONObject mainObject = new JSONObject(response);

                if (mainObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray topArray = mainObject.getJSONArray("result");

                    for (int i = 0; topArray.length() > i; i++) {

                        JSONObject jsonObject2 = topArray.getJSONObject(i);
                        group_id = jsonObject2.getString("group_id");
                        GroupName = jsonObject2.getString("group_name");
                        GroupProfileImage = jsonObject2.getString("group_image");

                        System.out.println("grpimg" + GroupProfileImage);
                    }

                    Intent intent = new Intent(CreateGroupChatActivity.this, GroupMemberList_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction("CreateGroupChatActivity");
                    intent.putExtra("groupName", GroupName);
                    intent.putExtra("groupId", group_id);
                    intent.putExtra("groupImage", GroupProfileImage);
                    startActivity(intent);
                    finish();

                } else if (mainObject.getString("status").equalsIgnoreCase("300")) {
                    Snackbar.make(findViewById(android.R.id.content), "300", Snackbar.LENGTH_LONG).show();
                } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                    Snackbar.make(findViewById(android.R.id.content), "400", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Some Error Occured.", Snackbar.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgressDialog.dismiss();


        }
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }
}


