package com.speakame.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by MAX on 18-Mar-17.
 */

public class FileUpload extends AsyncTask<String, Integer, String> {
    String urlString = "http://truckslogistics.com/Projects-Work/SpeakAme/user/fileupload.php";
    //String urlString = "http://192.168.1.139/openfire/fileupload.php";
    VolleyCallback volleyCallback;
    Context context;
    ProgressBar mProgressDialog;
    ImageView cancelUpload;
    HttpClient httpclient;
    HttpPost httppost;
    AndroidMultiPartEntity entity;
    long totalSize = 0;
    View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            uploadCancel();
            volleyCallback.backResponse("5");
        }
    };

    public FileUpload() {

    }

    public FileUpload(Context context, VolleyCallback volleyCallback, ProgressBar mProgressDialog, ImageView cancelUpload) {
        this.context = context;
        this.volleyCallback = volleyCallback;
        this.mProgressDialog = mProgressDialog;
        this.cancelUpload = cancelUpload;
        this.cancelUpload.setOnClickListener(cancel);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setProgress(0);
    }

    @Override
    protected String doInBackground(String... params) {
        //doFileUpload(params[0]);
        return uploadFile(params[0]);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mProgressDialog.setProgress(progress[0]);
    }



    @SuppressWarnings("deprecation")
    private String uploadFile(String filePath) {
        String responseString = null;

        httpclient = new DefaultHttpClient();
        httppost = new HttpPost(urlString);

        try {
            entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            File sourceFile = new File(filePath);

            // Adding file data to http body
            entity.addPart("uploadedfile", new FileBody(sourceFile));

            // Extra parameters if you want to pass to server
           /* entity.addPart("website",
                    new StringBody("www.androidhive.info"));
            entity.addPart("email", new StringBody("abc@gmail.com"));*/

            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response

                try {
                    responseString = EntityUtils.toString(r_entity);
                    JSONObject jsonObject = new JSONObject(responseString);
                    String resp = null;
                    resp = jsonObject.getString("status");
                    Log.d("fileupload data", resp);
                    if (resp.equalsIgnoreCase("200")) {
                        volleyCallback.backResponse(jsonObject.getString("file_path"));
                    } else {
                        volleyCallback.backResponse("5");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                responseString = "error";
                volleyCallback.backResponse("5");
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;

    }

    public void uploadCancel() {
        this.cancel(true);
        if(entity != null){
            entity.cancelUploading();
        }

        if (httppost != null) {
            httppost.abort();
            httppost = null;
        }
        if (httpclient != null) {
            httpclient.getConnectionManager().shutdown();
        }
        httpclient = null;
    }


}
