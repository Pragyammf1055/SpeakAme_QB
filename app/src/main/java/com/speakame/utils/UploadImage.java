package com.speakame.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.speakame.Activity.ChatActivity;
import com.speakame.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by MAX on 10-Mar-17.
 */

public class UploadImage extends AsyncTask<String,Void,String> {

    VolleyCallback volleyCallback;
   // private final WeakReference<ImageView> imageViewReference;
    Context context;

    public UploadImage(Context context, VolleyCallback volleyCallback){
        this.context = context;
        this.volleyCallback = volleyCallback;
    }
    @Override
    protected String doInBackground(String... params) {

        return uploadImage(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("onresponse>>", s);

    }

    private String uploadImage(final String image, final String msgId){
        //Showing the progress dialog
        final String[] response = {""};
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://truckslogistics.com/Projects-Work/SpeakAme/user/upload.php",//"http://192.168.1.118/openfire/UploadImage/upload.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("onresponse>>", s==null?"null":s);
                        String ss = (s==null)?"":s;
                        response[0] = ss.replace("\"","");
                        if (s.equalsIgnoreCase("")) {
                            volleyCallback.backResponse("5");
                        }else{
                            volleyCallback.backResponse(ss);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("onresponse>>>", volleyError.getMessage() == null?"volleyError":volleyError.getMessage().toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("msgId", msgId);
                params.put("image", image);

                //returning parameters
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        Log.d("onresponse>>", response[0]);
       return response[0];

    }
}
