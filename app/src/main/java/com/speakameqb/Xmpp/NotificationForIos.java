package com.speakameqb.Xmpp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MAX on 05-09-2017.
 */

class NotificationForIos extends AsyncTask<Void, Void, Void> {
    Context context;
    String FriendMobileTWO;

    public NotificationForIos(Context context, String FriendMobileTWO) {
        this.context = context;
        this.FriendMobileTWO = FriendMobileTWO;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", AppConstants.NOTIFICATION_IOS);
            jsonObject.put("mobile", FriendMobileTWO);
            jsonObject.put("sound", "default");
            jsonObject.put("test_message", "Text Message");
            jsonObject.put("badge", "1");

            jsonArray.put(jsonObject);
            Log.v("", "Json Object OfflineNotification :- " + jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONParser jsonParser = new JSONParser(context);
        jsonParser.parseVollyJsonArray(AppConstants.IOS_APIS, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {
                Log.d("", "Response OfflineNotification:- " + response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            //    Toast.makeText(context, "Check network connection", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
        return null;
    }
}
