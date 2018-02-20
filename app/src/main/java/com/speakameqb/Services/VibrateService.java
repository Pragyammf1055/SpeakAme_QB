package com.speakameqb.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Peter on 13-Feb-17.
 */
public class VibrateService extends Service {

    String modeVibration;

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

        modeVibration = (String) intent.getExtras().get("vibrateType");
        Log.v("VibrateServiceClass", "Selected Option in Button OnClick Listener :-" + modeVibration);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (modeVibration.equalsIgnoreCase("Off")) {

            vibrator.cancel();
            Toast.makeText(getApplicationContext(), "Phone is Vibrating Off", Toast.LENGTH_LONG).show();

        } else if (modeVibration.equalsIgnoreCase("Default")) {
//            vibrator.vibrate(DEFAULT_VIBRATE);
            Toast.makeText(getApplicationContext(), "Phone is Vibrating Default", Toast.LENGTH_LONG).show();

        } else if (modeVibration.equalsIgnoreCase("Short")) {

            vibrator.vibrate(1000);
            Toast.makeText(getApplicationContext(), "Phone is Vibrating Short", Toast.LENGTH_LONG).show();

        } else if (modeVibration.equalsIgnoreCase("Long")) {

            vibrator.vibrate(2000);
    /*    // If you want to vibrate  in a pattern
           long pattern[] = {0, 800, 200, 1200, 300, 2000, 400, 4000};
        // 2nd argument is for repetition pass -1 if you do not want to repeat the Vibrate
           v.vibrate(pattern, -1); */

            Toast.makeText(getApplicationContext(), "Phone is Vibrating Long", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
