package com.speakameqb.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;
import com.speakameqb.Xmpp.ChatMessage;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Random;

public class GroupChat_Activity extends AnimRootActivity implements View.OnClickListener {
    public static ArrayList<ChatMessage> chatlist;
    public static GroupChat_Activity instance = null;
    TextView toolbartitle;
    MultiUserChat muc;
    ImageView img_eye;
    AllBeans allBeans;
    String GroupId, Groupname, GroupDate, Grouptime, GroupImage;
    EditText msg_edittext;
    ImageButton sendMessageButton;
    ListView mlistviView;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        instance = this;

        toolbartitle = (TextView) findViewById(R.id.toolbar_title);
        toolbartitle.setText("Group Title");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartitle.setTypeface(tf1);
        img_eye = (ImageView) findViewById(R.id.iv_chat_eye);
        msg_edittext = (EditText) findViewById(R.id.messageEditText);
        sendMessageButton = (ImageButton) findViewById(R.id.sendMessageButton);
        mlistviView = (ListView) findViewById(R.id.groupListView);
        mlistviView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mlistviView.setStackFromBottom(true);
        sendMessageButton.setOnClickListener(this);
        img_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriviewmsgDialog();
            }
        });
        allBeans = getIntent().getParcelableExtra("value");
        GroupId = allBeans.getGroupid();
        Groupname = allBeans.getGroupName();
        GroupDate = allBeans.getGroupCreateDate();
        GroupImage = allBeans.getGroupImage();
        Grouptime = allBeans.getGroupCreateTime();

        System.out.println("grpid" + GroupId);

        toolbartitle.setText(Groupname);

//        MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(MyXMPP.connection);
       /* List<String> services = null;
        try {
            services = mucm.getServiceNames();

            if (services.isEmpty()) {

                throw new Exception("No MUC services found");

            }
            String service = services.get(0);
            muc = mucm.getMultiUserChat(Groupname + "@" + service);

            muc.join(AppPreferences.getFirstUsername(GroupChat_Activity.this));


        } catch (Exception e) {
            e.printStackTrace();
        }
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groupchat_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.overflow) {
            DrawerDialog();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void DrawerDialog() {
        final Dialog markerDialog = new Dialog(this, R.style.customDialog);
        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
        window.setGravity(Gravity.RIGHT);

        markerDialog.setContentView(R.layout.custom_drawer);
        LinearLayout l1, l2, l3, l4, l5, l6;
        TextView textonelayout;
        l1 = (LinearLayout) markerDialog.findViewById(R.id.l1);
        l2 = (LinearLayout) markerDialog.findViewById(R.id.l2);
        l3 = (LinearLayout) markerDialog.findViewById(R.id.l3);
        l4 = (LinearLayout) markerDialog.findViewById(R.id.l4);
        l5 = (LinearLayout) markerDialog.findViewById(R.id.l5);
        l6 = (LinearLayout) markerDialog.findViewById(R.id.l6);
        textonelayout = (TextView) markerDialog.findViewById(R.id.nametext);
        textonelayout.setText("VIEW GROUP");
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChat_Activity.this, ViewGroupDetail_Activity.class);
                intent.putExtra("GroupId", GroupId);
                intent.putExtra("GroupName", Groupname);
                intent.putExtra("GroupDate", GroupDate);
                intent.putExtra("GroupTime", Grouptime);
                intent.putExtra("GroupImage", GroupImage);
                startActivity(intent);
            }
        });
        l6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();
            }
        });
        l5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttachDialog();
                markerDialog.dismiss();
            }
        });
        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockDialog();
                markerDialog.dismiss();
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChathistoryDialog();
                markerDialog.dismiss();

            }
        });


        markerDialog.setCanceledOnTouchOutside(true);
        markerDialog.show();

    }

    public void AttachDialog() {
        final Dialog markerDialog = new Dialog(this, R.style.PauseDialog);
        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        window.setGravity(Gravity.CENTER);

        markerDialog.setContentView(R.layout.dialog_attach);
        Button btn_cancel = (Button) markerDialog.findViewById(R.id.cancel_btn);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();
                DrawerDialog();
            }
        });
        markerDialog.setCanceledOnTouchOutside(false);
        markerDialog.show();
        //return Strchat;
    }

    public void BlockDialog() {
        final Dialog markerDialog = new Dialog(this, R.style.PauseDialog);
        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        window.setGravity(Gravity.CENTER);

        //  window.setBackgroundDrawableResource(R.drawable.rounded_corner_dialog);
        //window.s
        markerDialog.setContentView(R.layout.block_dialogpopup);
        Button btn_done = (Button) markerDialog.findViewById(R.id.done_btn);
        Button btn_cancel = (Button) markerDialog.findViewById(R.id.cancel_btn);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();
                DrawerDialog();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();
                DrawerDialog();
            }
        });

        markerDialog.setCanceledOnTouchOutside(false);
        markerDialog.show();
    }


    public void ChathistoryDialog() {
        final Dialog markerDialog = new Dialog(this, R.style.PauseDialog);
        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        window.setGravity(Gravity.CENTER);

        //  window.setBackgroundDrawableResource(R.drawable.rounded_corner_dialog);
        //window.s
        markerDialog.setContentView(R.layout.chathistory_dialog);
        Button btn_done = (Button) markerDialog.findViewById(R.id.done_btn);
        Button btn_cancel = (Button) markerDialog.findViewById(R.id.cancel_btn);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();
                DrawerDialog();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();
                DrawerDialog();
            }
        });

        markerDialog.setCanceledOnTouchOutside(false);
        markerDialog.show();
    }

    public void PriviewmsgDialog() {
        final Dialog markerDialog = new Dialog(this, R.style.PauseDialog);
        markerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = markerDialog.getWindow();
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        window.setGravity(Gravity.CENTER);

        markerDialog.setContentView(R.layout.priviewmsg_popup);
        Button btn_send = (Button) markerDialog.findViewById(R.id.send_btn);
        Button btn_cancel = (Button) markerDialog.findViewById(R.id.cancel_btn);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDialog.dismiss();

            }
        });

        markerDialog.setCanceledOnTouchOutside(false);
        markerDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                sendTextMessage(v);

        }
    }

    public void sendTextMessage(View v) {
        try {
            String message = msg_edittext.getEditableText().toString();
            if (!TextUtils.isEmpty(message)) {
                muc.sendMessage(message);
                msg_edittext.setText("");
            }

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
