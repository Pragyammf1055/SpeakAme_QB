package com.speakameqb.Recivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.speakameqb.utils.Function;


/**
 * Created by MAX on 15-Feb-17.
 */

public class NetworkStateChecker extends BroadcastReceiver {


    private static final String TAG = "NetworkStateChecker";
    public static ConnectionReceiverListener connectionReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        String status = Function.getConnectivityStatusString(context);

        Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        if (activeNetwork != null) { // connected to the internet

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.v(TAG, "connected to wifi ");
                // connected to wifi
                connectionReceiverListener.onNetworkConnectionChanged(true);
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                Log.v(TAG, "connected to the mobile provider's data plan");
                connectionReceiverListener.onNetworkConnectionChanged(true);
                // connected to the mobile provider's data plan
            }
        } else {
            Log.v(TAG, "Not connected to internet");
            // not connected to the internet
            connectionReceiverListener.onNetworkConnectionChanged(false);
        }

//        boolean isConnected = Function.isConnected(context);
//        Toast.makeText(context, "Internet is connected :- " + isConnected, Toast.LENGTH_LONG).show();

        if (connectionReceiverListener != null) {
//            connectionReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }


    public interface ConnectionReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
