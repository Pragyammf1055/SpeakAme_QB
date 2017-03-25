package com.speakame.Recivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.speakame.Xmpp.MyService;

/**
 * Created by MAX on 15-Feb-17.
 */

public class NetworkStateChecker extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                context.startService(new Intent(context, MyService.class));
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                context.startService(new Intent(context, MyService.class));
            }
        } else {
            // not connected to the internet
        }
    }


}
