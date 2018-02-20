package com.speakameqb.Recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.speakameqb.Activity.Main_Activity;


/**
 * Created by yogesh on 18/06/16.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = SmsReceiver.class.getSimpleName();
    public static String verificationCode;

    @Override
    public void onReceive(Context context, Intent intent) {

        context.startActivity(new Intent(context, Main_Activity.class));

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "ReceivedSMS" + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.contains("-SpeakA")) {
                        Log.e(TAG, "SMS is not for our app!");
                        return;
                    }

                    // verification code from sms
                    verificationCode = getVerificationCode(message);

                    Intent intent1 = new Intent("MSGINTENT");
                    intent1.putExtra("msg", verificationCode);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);

                    Log.e(TAG, "OTP received: " + verificationCode);

//                    Intent hhtpIntent = new Intent(context, HttpService.class);
//                    hhtpIntent.putExtra("otp", verificationCode);
//                    context.startService(hhtpIntent);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(":");

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
