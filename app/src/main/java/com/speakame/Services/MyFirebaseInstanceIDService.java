package com.speakame.Services;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MMFA-YOGESH on 03-Feb-17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token:" + refreshedToken);

        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "userFCMUpdate");
            jsonObject.put("fcm_mobile_id", token);
            jsonObject.put("user_id", AppPreferences.getLoginId(this));

            jsonArray.put(jsonObject);
            System.out.println("sentfcmjson" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(MyFirebaseInstanceIDService.this);
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
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }


}
