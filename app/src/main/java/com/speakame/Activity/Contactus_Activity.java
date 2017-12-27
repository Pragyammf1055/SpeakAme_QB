package com.speakame.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class Contactus_Activity extends AnimRootActivity {
    //keep track of camera capture intent
    final int CAMERA_CAPTURE1 = 1;
    final int CAMERA_CAPTURE2 = 2;
    final int CAMERA_CAPTURE3 = 3;
    //keep track of cropping intent
    final int PIC_CROP = 2;
    TextView toolbartext;
    ImageView img_one, img_two, img_three;
    EditText medit_description;
    String Description, First_image, Second_image, Third_image, currentDateTimeString;
    ArrayList<Uri> fileArrayList = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Contact us");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        medit_description = (EditText) findViewById(R.id.describebox);
        img_one = (ImageView) findViewById(R.id.image_one);
        img_two = (ImageView) findViewById(R.id.image_two);
        img_three = (ImageView) findViewById(R.id.image_three);

        Function.cameraPermisstion(Contactus_Activity.this, 1);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        currentDateTimeString = dateFormatter.format(today);
        Log.d("currentdatetime", currentDateTimeString);


        img_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        CAMERA_CAPTURE1);

            }
        });
        img_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        CAMERA_CAPTURE2);

            }
        });
        img_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        CAMERA_CAPTURE3);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contactus_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.done:
                Description = medit_description.getText().toString();

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                emailIntent.setType("application/image");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"help@speakame.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I had Issues");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Description);

                emailIntent.putExtra(Intent.EXTRA_STREAM, fileArrayList);
               /* for(int i=0; i<fileArrayList.size(); i++){
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileArrayList.get(i).toString()));
                }*/

                startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                //new ContactusTask().execute();

//                Uri packageURI = Uri.parse("package:"+"com.whatsapp");
//                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//                startActivity(uninstallIntent);
                finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE1) {
                Uri photoUri = data.getData();

                if (photoUri != null){
                    try {
                        //We get the file path from the media info returned by the content resolver
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        File ImageFile = new File(filePath);
                        fileArrayList.add(photoUri);
                        Bitmap bitmap = BitmapFactory.decodeFile((filePath));
                        img_one.setImageBitmap(bitmap);

                    }catch(Exception e){
                    }
                }
            } else if (requestCode == CAMERA_CAPTURE2) {
                Uri photoUri = data.getData();

                if (photoUri != null){
                    try {
                        //We get the file path from the media info returned by the content resolver
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        File ImageFile = new File(filePath);
                        fileArrayList.add(photoUri);
                        Bitmap bitmap = BitmapFactory.decodeFile((ImageFile.getAbsolutePath()));
                        img_two.setImageBitmap(bitmap);

                    }catch(Exception e){
                    }
                }
            } else if (requestCode == CAMERA_CAPTURE3) {
                Uri photoUri = data.getData();

                if (photoUri != null){
                    try {
                        //We get the file path from the media info returned by the content resolver
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        File ImageFile = new File(filePath);
                        fileArrayList.add(photoUri);
                        Bitmap bitmap = BitmapFactory.decodeFile((filePath));
                        img_three.setImageBitmap(bitmap);

                    }catch(Exception e){
                    }
                }
            }
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

    private class ContactusTask extends AsyncTask<Void, Void, String> {
        private AlertDialog mProgressDialog;
        private JSONObject jsonObj;
        private int status = 0;

        public ContactusTask() {
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new SpotsDialog(Contactus_Activity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(true);
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
                System.out.println("resendvalue value : ------------ "
                        + AppConstants.COMMONURL);
                HttpPost httppost = new HttpPost(AppConstants.COMMONURL);


                jsonObj = new JSONObject();
                jsonObj.put("method", "contactUs");
                jsonObj.put("userId", AppPreferences.getLoginId(Contactus_Activity.this));
                jsonObj.put("screenShotOne", img_one);
                jsonObj.put("dateTime", currentDateTimeString);
                jsonObj.put("screenShotTwo", img_two);
                jsonObj.put("screenShotThree", img_three);
                jsonObj.put("problem", Description);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());

                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Sent JSON is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;

                response = httpclient.execute(httppost);

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
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
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);

                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out.println("--------- message 200 got ----------");
                            JSONArray jsonArray1 = jsonObj.getJSONArray("result");

                            status = 200;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("400")) {
                            status = 400;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("800")) {
                            status = 800;
                            return jsonString;
                        }

                    }

                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.d("status", status + "");
            if (status == 200) {
                Toast.makeText(Contactus_Activity.this, "Your Complain Has Been Registered", Toast.LENGTH_LONG).show();//fa

                Intent intent = new Intent(Contactus_Activity.this, HelpFeedback_Activity.class);
                startActivity(intent);

            } else {

                Snackbar.make(findViewById(android.R.id.content), "Cheack network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        }


    }


}
