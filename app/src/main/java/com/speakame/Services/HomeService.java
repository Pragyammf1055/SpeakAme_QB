package com.speakame.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.speakame.Activity.Main_Activity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Function;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MMFA-YOGESH on 5/18/2016.
 */
public class HomeService extends IntentService {


    public static volatile boolean shouldContinue = true;
    private Timer timer;
    private TimerTask task;


    public HomeService() {
        super(HomeService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Function.importcontact(HomeService.this);
        if(!AppPreferences.getMobileuser(HomeService.this).equalsIgnoreCase(""))
         checklogin();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("***********service command******************");
        // new TimelineAsynch().execute();
        if(!AppPreferences.getMobileuser(HomeService.this).equalsIgnoreCase(""))
         checklogin();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        //  tripIdList = db.getTripId();
        System.out.println("***********service******************");
        if(!AppPreferences.getMobileuser(HomeService.this).equalsIgnoreCase(""))
         checklogin();

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int delay = 50000;
        int period = 50000;


        timer = new Timer();

        timer.scheduleAtFixedRate(task = new TimerTask() {
            public void run() {
                if(!AppPreferences.getMobileuser(HomeService.this).equalsIgnoreCase(""))
                 checklogin();
                if (shouldContinue == false) {
                    timer.cancel();
                    task.cancel();

                    return;
                }
//                else {
//                    if (Function.isAppIsInBackground(HomeService.this)) {
//                        System.out.println("***********service pause******************");
//                        //id = new ArrayList<String>();
//                        // al = new ArrayList<AvailableRequestTimelineBean>();
//                    } else {
//                        acceptOrder();
//                        System.out.println("***********service restart******************");
//                    }
//                }
            }
        }, delay, period);
        stopSelf();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ServiceTask", "Stop");
        //timer.cancel();
        // task.cancel();
        //stopSelf();
    }


    private void checklogin() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", "checkLogin");
            jsonObject.put("user_mobile", AppPreferences.getMobileuser(HomeService.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(HomeService.this));

            jsonArray.put(jsonObject);

            System.out.println("continuerunning" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(HomeService.this);
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

                            System.out.println("noanotherlogin>>>" + orderArray);

                            //     Toast.makeText(getApplicationContext(), "Order successfully accepted", Toast.LENGTH_LONG).show();


                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                            // Toast.makeText(getApplicationContext(), "Alerady accepted", Toast.LENGTH_LONG).show();
                           /* AppPreferences.setEmail(HomeService.this, "");
                            AppPreferences.setMobileuser(HomeService.this, "");
                            AppPreferences.setFirstUsername(HomeService.this, "");
                            AppPreferences.setLoginId(HomeService.this, 0);
                            AppPreferences.setUserprofile(HomeService.this, "");
                            AppPreferences.setTotf(HomeService.this, "");
                            AppPreferences.setEnetrSend(HomeService.this, "");
                            DatabaseHelper.getInstance(HomeService.this).deleteDB();

                            //   MyXMPP.deleteUserr();

                            Intent intent = new Intent(HomeService.this, Main_Activity.class);
                            stopSelf();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);*/


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        // System.out.println("AppConstants.COMMONURL---------" + AppConstants.COMMONURL);
        //System.out.println("jsonObject" + jsonObject);
    }
}
