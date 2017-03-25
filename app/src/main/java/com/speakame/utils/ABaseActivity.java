package com.speakame.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.speakame.Services.XmppConneceted;
import com.speakame.Xmpp.MyXMPP;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.List;

/**
 * Created by MAX on 21-Jan-17.
 */
abstract public class ABaseActivity extends AppCompatActivity {

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("isAppForground","onResume");
        XmppConneceted activity = new XmppConneceted();
        try {
            activity.getmService().xmpp.setStatus(true, "online");


        } catch (XMPPException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
      // setStatus();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("isAppForground","onPause");
        XmppConneceted activity = new XmppConneceted();
        try {
            activity.getmService().xmpp.setStatus(true, "offline");
            new Thread(new Runnable() {
                @Override
                public void run() {
                  //  MyXMPP.connection.disconnect();
                }
            }).start();

            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            try {
//                                MyXMPP.connection.connect();
//                            } catch (SmackException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } catch (XMPPException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }).start();
                }
            }.start();
        } catch (XMPPException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
       // setStatus();
    }



}
