package com.speakame.Xmpp;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.speakame.Activity.ChatActivity;
import com.speakame.Activity.TwoTab_Activity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Services.XmppConneceted;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.CallBackUi;
import com.speakame.utils.DownloadFile;
import com.speakame.utils.FileUpload;
import com.speakame.utils.Function;
import com.speakame.utils.UploadImage;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.speakame.Activity.TwoTab_Activity.adapter;

/**
 * Created by Max on 22-Dec-16.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FILE = 1;
    private static final int CONTACT = 2;
    private static final int MESSAGE = 5;
    final int ADDCONTACT = 24;
    ArrayList<ChatMessage> chatMessageList;
    Context context;
    private SparseBooleanArray mSelectedItemsIds;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        mSelectedItemsIds = new SparseBooleanArray();
    }


    public void add(ChatMessage object, int pos) {
        chatMessageList.add(object);
    }

    public void removeData(int position) {

        String msgId = chatMessageList.get(position).msgid;
        new DatabaseHelper(context).deleteChat(msgId);
        chatMessageList.remove(position);
        if (TwoTab_Activity.instance != null) {
            adapter.notifyDataSetChanged();
        }
        notifyItemRemoved(position);
    }

    public String copyMsg(int position) {
        String selectText = chatMessageList.get(position).body.split("~")[0];
        chatMessageList.get(position).setSelected(false);
        //notifyItemRemoved(position);
        return selectText;
    }

    public JSONObject copyData(int position) {
        String selectText = chatMessageList.get(position).body.split("~")[0];
        String files = chatMessageList.get(position).files;
        String fileName = chatMessageList.get(position).fileName;
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("msg", selectText);
            jsonObject.put("file", files);
            jsonObject.put("fileName", fileName);

        } catch (JSONException e) {
        }
        chatMessageList.get(position).setSelected(false);
        //notifyItemRemoved(position);
        return jsonObject;
    }

    public ChatMessage getItem(int position) {
        return chatMessageList.get(position);
    }

    @Override
    public int getItemCount() {
        return this.chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {

        String msg = chatMessageList.get(position).body;
        Log.d("Filename", chatMessageList.get(position).files+">>" + msg);
        if (!chatMessageList.get(position).files.equalsIgnoreCase("") && msg.contains(AppConstants.KEY_CONTACT)) {

            return CONTACT;
        }else if (chatMessageList.get(position).files.equalsIgnoreCase("")) {

            return MESSAGE;
        } else {

            return FILE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case CONTACT:
                View v1 = inflater.inflate(R.layout.chatcontact, viewGroup, false);
                viewHolder = new ContactViewHolder(v1);
                break;
            case FILE:
                View v3 = inflater.inflate(R.layout.chatbubble, viewGroup, false);
                viewHolder = new FileViewHolder(v3);
                break;
            case MESSAGE:
                View v4 = inflater.inflate(R.layout.chatmessage, viewGroup, false);
                viewHolder = new MessageViewHolder(v4);
                break;
            default:
                View v5 = inflater.inflate(R.layout.chatmessage, viewGroup, false);
                viewHolder = new MessageViewHolder(v5);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case CONTACT:
                ContactViewHolder vh1 = (ContactViewHolder) viewHolder;
                configureContactViewHolder(vh1, position);
                break;
            case FILE:
                FileViewHolder vh3 = (FileViewHolder) viewHolder;
                configureFileViewHolder(vh3, position);
                break;
            case MESSAGE:
                MessageViewHolder vh4 = (MessageViewHolder) viewHolder;
                configureMessageViewHolder(vh4, position);
                break;
            default:

                MessageViewHolder vh5 = (MessageViewHolder) viewHolder;
                configureMessageViewHolder(vh5, position);
                break;
        }
    }

    private void configureContactViewHolder(ContactViewHolder vh1, int position) {
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);
        if (message != null) {
            vh1.view.setBackgroundColor(message.isSelected() ? Color.GRAY : 0);
            if (message.isMine) {

                try {
                    if (message.type.equalsIgnoreCase(Message.Type.chat.name()) && message.receiver.equalsIgnoreCase(message.sender)) {
                        vh1.layout.setBackgroundResource(0);
                        //parent_layout.setGravity(Gravity.CENTER_HORIZONTAL);
                        vh1.msg.setTextColor(Color.GRAY);
                        vh1.time.setTextColor(Color.GRAY);
                        vh1.addcontact.setVisibility(View.GONE);
                    } else {
                        vh1.layout.setBackgroundResource(R.drawable.yyk);
                        vh1.parent_layout.setGravity(Gravity.RIGHT);
                        vh1.msg.setTextColor(Color.WHITE);
                        vh1.time.setTextColor(Color.BLACK);
                        vh1.addcontact.setVisibility(View.GONE);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    vh1.layout.setBackgroundResource(R.drawable.yyk);
                    vh1.parent_layout.setGravity(Gravity.RIGHT);
                    vh1.msg.setTextColor(Color.WHITE);
                    vh1.time.setTextColor(Color.BLACK);
                    vh1.addcontact.setVisibility(View.GONE);
                }

                if(message.msgStatus.equalsIgnoreCase("0")) {
                    uploadContactImage(vh1, message, position);
                }

            } else {
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);
                vh1.addcontact.setVisibility(View.VISIBLE);
            }

            Log.d("IMAGEPATH", message.files);
            File file = new File(message.files);
            final Uri uri = Uri.fromFile(file);

            InputStream iStream = null;
            try {
                iStream = context.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] inputData = null;
            try {
                inputData = getBytes(iStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] contactName = new String[]{"name","00"};
            if(message.body.equalsIgnoreCase("")){
               // vh1.textMsgBg.setVisibility(View.GONE);
            }else{
               // vh1.textMsgBg.setVisibility(View.VISIBLE);
                String[] msg = message.body.split("~");
                contactName = msg[0].split(AppConstants.KEY_CONTACT);
                vh1.msg.setText(contactName[0]);
                // vh1.msg.setText(message.body);
            }

            //vh1.msg.setText(contactName[0]);
            //vh1.imageView.setImageURI(uri);

            //K:mma

            vh1.time.setText(message.Time);

            final byte[] finalInputData = inputData;
            final String[] finalContactName = contactName;
            vh1.addcontact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String[] contactName = message.body.split(AppConstants.KEY_CONTACT);
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                    intent.putExtra(ContactsContract.Intents.Insert.NAME, finalContactName[0]);
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, finalContactName[1]);
                    intent.putExtra(ContactsContract.CommonDataKinds.Photo.PHOTO, finalInputData);

                    context.startActivity(intent);
                }
            });
        }
    }
    private void configureFileViewHolder(final FileViewHolder vh1, final int position) {
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);


        if (message != null) {
            vh1.view.setBackgroundColor(message.isSelected() ? Color.GRAY : 0);

            if(message.fileName == null) {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.GONE);
            }else if(message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.VISIBLE);
                vh1.video.setVisibility(View.GONE);
            }else if(message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                vh1.docImage.setVisibility(View.VISIBLE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.GONE);
            }else if(message.fileName.contains(".mp4") || message.fileName.contains(".3gp")) {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.VISIBLE);
            }else {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.GONE);
            }

            if (message.isMine) {
                vh1.msgStatus.setVisibility(View.VISIBLE);
                vh1.startUpload.setImageDrawable(context.getResources().getDrawable(R.drawable.uploadimage));
                try {
                    if (message.type.equalsIgnoreCase(Message.Type.chat.name()) && message.receiver.equalsIgnoreCase(message.sender)) {
                        vh1.layout.setBackgroundResource(0);
                        //parent_layout.setGravity(Gravity.CENTER_HORIZONTAL);
                        vh1.msg.setTextColor(Color.GRAY);
                        vh1.time.setTextColor(Color.GRAY);
                    } else {
                        vh1.layout.setBackgroundResource(R.drawable.yyk);
                        vh1.parent_layout.setGravity(Gravity.RIGHT);
                        vh1.msg.setTextColor(Color.WHITE);
                        vh1.time.setTextColor(Color.BLACK);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    vh1.layout.setBackgroundResource(R.drawable.yyk);
                    vh1.parent_layout.setGravity(Gravity.RIGHT);
                    vh1.msg.setTextColor(Color.WHITE);
                    vh1.time.setTextColor(Color.BLACK);
                }



                //vh1.imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, vh1.imageView.getWidth(),
                       // vh1.imageView.getHeight(), false));

                if(message.msgStatus.equalsIgnoreCase("5")){
                    vh1.startUpload.setVisibility(View.VISIBLE);
                }else{
                    vh1.startUpload.setVisibility(View.GONE);
                }

                if(message.msgStatus.equalsIgnoreCase("0")) {
                    Log.d("IMAGEPATH upload", message.files+ ">>>>");
                    vh1.progressBar.setVisibility(View.VISIBLE);
                    vh1.cancelUploading.setVisibility(View.VISIBLE);

                    if(message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    }else if(message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if(message.fileName.contains(".pdf")){
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        }else if(message.fileName.contains(".docx")){
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    }else if(message.fileName.contains(".mp4")) {
                        File file = new File(message.files);
                        final Uri uri = Uri.fromFile(file);
                        vh1.video.setVideoURI(uri);
                        vh1.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mp) {

                                mp.seekTo(1);


                            }
                        });
                    }
                    uploadImage(vh1, message, position);
                }else{
                    Log.d("IMAGEPATH send", message.files+">>>>");
                    Log.d("IMAGEPATH send", message.toString()+">>>>");
                    if(message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    }else if(message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if(message.fileName.contains(".pdf")){
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        }else if(message.fileName.contains(".docx")){
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    }else if(message.fileName.contains(".mp4")) {
                        //vh1.video.setVisibility(View.VISIBLE);
                        File file = new File(message.files);
                        final Uri uri = Uri.fromFile(file);
                        vh1.video.setVideoURI(uri);
                        //vh1.video.setMediaController(new MediaController(context));
                        vh1.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mp) {

                                mp.seekTo(1);


                            }
                        });
                    }

                }

                if(message.msgStatus.equalsIgnoreCase("0")){
                    vh1.msgStatus.setImageResource(0);
                }else if(message.msgStatus.equalsIgnoreCase("1")){
                    vh1.msgStatus.setImageResource(R.drawable.tick);
                }else if(message.msgStatus.equalsIgnoreCase("2")) {
                    vh1.msgStatus.setImageResource(R.drawable.reach);
                }else if(message.msgStatus.equalsIgnoreCase("3")) {
                    vh1.msgStatus.setImageResource(R.drawable.read);
                }else if(message.msgStatus.equalsIgnoreCase("5")) {
                    vh1.msgStatus.setImageResource(R.drawable.stopwatch);
                }else{
                    vh1.msgStatus.setImageResource(0);
                }

                if(message.msgStatus.equalsIgnoreCase("2")){
                    vh1.msgStatus.setImageResource(R.drawable.tick);
                }else{

                }

            } else {
                vh1.msgStatus.setVisibility(View.GONE);
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);
                vh1.startDownloading.setImageDrawable(context.getResources().getDrawable(R.drawable.downloadimage));

                if(message.msgStatus.equalsIgnoreCase("10")){
                    if(message.fileName.contains(".jpg")) {
                        vh1.imageView.setImageResource(R.mipmap.ic_launcher);
                    }else if(message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if(message.fileName.contains(".pdf")){
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        }else if(message.fileName.contains(".docx")){
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    }else if(message.fileName.contains(".mp4")) {

                    }
                    //vh1.imageView.setAlpha(1);
                    vh1.startDownloading.setVisibility(View.GONE);
                    downLoadFile(vh1,message);
                }else if(message.msgStatus.equalsIgnoreCase("11")){
                    if(message.fileName.contains(".jpg")) {
                        vh1.imageView.setImageResource(R.mipmap.ic_launcher);
                    }else if(message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if(message.fileName.contains(".pdf")){
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        }else if(message.fileName.contains(".docx")){
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    }else if(message.fileName.contains(".mp4")) {
                        Log.d("IMAGEPATH recive", message.files);
                    }
                    //vh1.imageView.setAlpha(1);
                    vh1.startDownloading.setVisibility(View.VISIBLE);
                }else{
                    Log.d("IMAGEPATH recive", message.files);
                   // vh1.imageView.setImageDrawable(Drawable.createFromPath(message.fileName));
                    //vh1.imageView.setAlpha(0);
                    vh1.startDownloading.setVisibility(View.GONE);

                    if(message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    }else if(message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if(message.fileName.contains(".pdf")){
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        }else if(message.fileName.contains(".docx")){
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    }else if(message.fileName.contains(".mp4")) {
                        //vh1.video.setVisibility(View.VISIBLE);
                        File file = new File(message.files);
                        final Uri uri = Uri.fromFile(file);
                      /*  vh1.video.setVideoURI(uri);
                       // vh1.video.setMediaController(new MediaController(context));
                        vh1.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mp) {

                            }
                        });*/
                    }
                }


            }



            File file = new File(message.files);
            final Uri uri = Uri.fromFile(file);





            if(message.body.equalsIgnoreCase("")){
                vh1.textMsgBg.setVisibility(View.GONE);
            }else{
                vh1.textMsgBg.setVisibility(View.VISIBLE);
                String[] msg = message.body.split("~");

                    vh1.msg.setText(msg[0]);

               // vh1.msg.setText(message.body);
            }

            //vh1.imageView.setImageURI(uri);


            vh1.time.setText(message.Time);

            vh1.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri,"image/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                       // imageDialog(uri);
                }
            });
            vh1.video.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!message.msgStatus.equalsIgnoreCase("10")){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri,"video/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                      //  videoDialog(uri);
                    }

                    return false;
                }
            });
            vh1.startUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("IMAGEPATH upanddownload", message.files);
                    if (message.isMine) {
                        uploadImage(vh1, message, position);
                    }else {
                        vh1.startUpload.setVisibility(View.GONE);
                        downLoadFile(vh1,message);
                    }
                }
            });

            vh1.msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (message.fileName.endsWith(".pdf")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri,"application/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                   // } else {

                   // }
                }
            });
        }

    }



    private void configureMessageViewHolder(final MessageViewHolder vh1, final int position) {
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);
        if (message != null) {
            vh1.view.setBackgroundColor(message.isSelected() ? Color.GRAY : 0);
            if (message.isMine) {
                vh1.msgStatus.setVisibility(View.VISIBLE);
                vh1.reciverName.setVisibility(View.GONE);
                try {
                    if (message.type.equalsIgnoreCase(Message.Type.chat.name()) && message.receiver.equalsIgnoreCase(message.sender)) {
                        vh1.layout.setBackgroundResource(0);
                        //parent_layout.setGravity(Gravity.CENTER_HORIZONTAL);
                        vh1.msg.setTextColor(Color.GRAY);
                        vh1.originalmsg.setTextColor(Color.GRAY);
                        vh1.time.setTextColor(Color.GRAY);
                      //  vh1.msgStatus.setVisibility(View.GONE);

                    } else {
                        vh1.layout.setBackgroundResource(R.drawable.yyk);
                        vh1.parent_layout.setGravity(Gravity.RIGHT);
                        vh1.msg.setTextColor(Color.WHITE);
                        vh1.originalmsg.setTextColor(Color.WHITE);
                        vh1.time.setTextColor(Color.BLACK);
                       // vh1.msgStatus.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    vh1.layout.setBackgroundResource(R.drawable.yyk);
                    vh1.parent_layout.setGravity(Gravity.RIGHT);
                    vh1.msg.setTextColor(Color.WHITE);
                    vh1.originalmsg.setTextColor(Color.WHITE);
                    vh1.time.setTextColor(Color.BLACK);
                  //  vh1.msgStatus.setVisibility(View.VISIBLE);
                }


                if(message.msgStatus.equalsIgnoreCase("0")) {
                    XmppConneceted activity = new XmppConneceted();
                    if(message.groupName.equalsIgnoreCase("")){
                        activity.getmService().xmpp.sendMessage(message, new CallBackUi() {
                            @Override
                            public void update(String s) {
                                // String aa[] = s.split(",");

                                chatMessageList.get(position).msgStatus = s;
                                if(s.equalsIgnoreCase("1")){
                                    vh1.msgStatus.setImageResource(R.drawable.tick);
                                }else if(s.equalsIgnoreCase("2")){
                                    vh1.msgStatus.setImageResource(R.drawable.reach);
                                }else {
                                    vh1.msgStatus.setImageResource(R.drawable.read);
                                }

                                MediaPlayer mp = MediaPlayer.create(context, R.raw.tick);
                                if (AppPreferences.getConvertTone(context).equalsIgnoreCase("false")) {
                                    mp.stop();
                                } else {
                                    mp.start();
                                }

                            }
                        });
                    }else{
                        activity.getmService().xmpp.sendGroupMessage(message, new CallBackUi() {
                            @Override
                            public void update(String s) {
                                // String aa[] = s.split(",");

                                chatMessageList.get(position).msgStatus = s;
                                if(s.equalsIgnoreCase("1")){
                                    vh1.msgStatus.setImageResource(R.drawable.tick);
                                }else if(s.equalsIgnoreCase("2")){
                                    vh1.msgStatus.setImageResource(R.drawable.reach);
                                }else {
                                    vh1.msgStatus.setImageResource(R.drawable.read);
                                }

                                MediaPlayer mp = MediaPlayer.create(context, R.raw.tick);
                                if (AppPreferences.getConvertTone(context).equalsIgnoreCase("false")) {
                                    mp.stop();
                                } else {
                                    mp.start();
                                }

                            }
                        });
                    }

                }

                if(message.msgStatus.equalsIgnoreCase("0")){
                    vh1.msgStatus.setImageResource(0);
                }else if(message.msgStatus.equalsIgnoreCase("1")){
                    vh1.msgStatus.setImageResource(R.drawable.tick);
                }else if(message.msgStatus.equalsIgnoreCase("2")) {
                    vh1.msgStatus.setImageResource(R.drawable.reach);
                }else if(message.msgStatus.equalsIgnoreCase("3")) {
                    vh1.msgStatus.setImageResource(R.drawable.read);
                }
            } else {
                vh1.msgStatus.setVisibility(View.GONE);
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.originalmsg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);
               // vh1.msgStatus.setVisibility(View.GONE);

                //DatabaseHelper.getInstance(context).UpdateMsgStatus("12",message.msgid);

                if(message.groupName == null){
                    vh1.reciverName.setVisibility(View.GONE);
                }else if(message.groupName.equalsIgnoreCase("")){
                    vh1.reciverName.setVisibility(View.GONE);
                }else {
                    vh1.reciverName.setVisibility(View.VISIBLE);
                    if (message.senderName.contains("@ip-")) {
                        vh1.reciverName.setVisibility(View.GONE);
                    } else {
                        vh1.reciverName.setText(message.senderName);
                    }
                }

            }

            String[] msg = message.body.split("~");

            if (msg.length > 1) {
                vh1.originalmsg.setText(msg[0]);
                vh1.msg.setText(msg[2]);
                vh1.originalmsg.setVisibility(View.VISIBLE);
                vh1.img_totf.setVisibility(View.VISIBLE);
            } else {
                vh1.msg.setText(msg[0]);
                vh1.originalmsg.setVisibility(View.GONE);
                vh1.img_totf.setVisibility(View.GONE);
            }



            vh1.time.setText(message.Time);


        }
    }
    private void downLoadFile(final FileViewHolder vh1, final ChatMessage message) {
        /*ObjectAnimator animation = ObjectAnimator.ofInt (vh1.progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration (5000); //in milliseconds
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();*/
        vh1.progressBar.setVisibility(View.VISIBLE);
        vh1.cancelDownloading.setVisibility(View.VISIBLE);
        new DownloadFile(context,vh1.cancelDownloading,vh1.progressBar, new VolleyCallback() {
            @Override
            public void backResponse(String response) {
                if(response.equalsIgnoreCase("11")){
                    vh1.imageView.setImageDrawable(Drawable.createFromPath(response));
                    DatabaseHelper.getInstance(context).UpdateMsgStatus(response,message.msgid);
                }else{

                    DatabaseHelper.getInstance(context).UpdateMsgStatus("12",message.msgid);

                    DatabaseHelper.getInstance(context).UpdateFileName(response,message.msgid);

                    if(message.fileName.contains(".jpg")) {
                        // vh1.video.setVisibility(View.GONE);
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(response));
                    }else if(message.fileName.contains(".mp4")) {
                        //vh1.video.setVisibility(View.VISIBLE);
                        File file = new File(response);
                        final Uri uri = Uri.fromFile(file);
                        vh1.video.setVideoURI(uri);
                        // vh1.video.setMediaController(new MediaController(context));
                        vh1.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mp) {

                                mp.seekTo(1);


                            }
                        });
                    }

                }
                vh1.progressBar.setVisibility(View.GONE);
                vh1.cancelDownloading.setVisibility(View.GONE);
                /*File SpeakaMe = Environment.getExternalStorageDirectory();
                File SpeakaMeDirectory = new File(SpeakaMe + "/SpeakaMe/image/recive");
                String file = SpeakaMeDirectory+"/"+message.fileName;*/

            }
        }).execute(message.files, message.fileName);
    }

    private void uploadImage(final FileViewHolder vh1, final ChatMessage message, final int position) {


        vh1.startUpload.setVisibility(View.GONE);
        vh1.progressBar.setVisibility(View.VISIBLE);
        vh1.cancelUploading.setVisibility(View.VISIBLE);
        new FileUpload(context, new VolleyCallback() {
            @Override
            public void backResponse(final String response) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        vh1.progressBar.setVisibility(View.GONE);
                        vh1.cancelUploading.setVisibility(View.GONE);

                        if (response.equalsIgnoreCase("5")) {
                            chatMessageList.get(position).msgStatus = response;
                            vh1.startUpload.setVisibility(View.VISIBLE);
                            vh1.msgStatus.setImageResource(R.drawable.stopwatch);
                            Toast.makeText(context, "File Uploaded Fail!", Toast.LENGTH_LONG).show();
                            DatabaseHelper.getInstance(context).UpdateMsgStatus(response, message.msgid);
                        } else {
                            chatMessageList.get(position).msgStatus = "1";
                            vh1.startUpload.setVisibility(View.GONE);
                            message.files = response;
                            XmppConneceted activity = new XmppConneceted();
                            message.fileData = new byte[0];
                            activity.getmService().xmpp.sendMessage(message, new CallBackUi() {
                                @Override
                                public void update(String s) {
                                    // String aa[] = s.split(",");
                                    Log.d("onReceiptReceived>>>>", s);
                                    chatMessageList.get(position).msgStatus = s;
                                    if (s.equalsIgnoreCase("1")) {
                                        vh1.msgStatus.setImageResource(R.drawable.tick);
                                    } else if (s.equalsIgnoreCase("2")) {
                                        vh1.msgStatus.setImageResource(R.drawable.reach);
                                    } else {
                                        vh1.msgStatus.setImageResource(R.drawable.read);
                                    }
                                    MediaPlayer mp = MediaPlayer.create(context, R.raw.tick);
                                    if (AppPreferences.getConvertTone(context).equalsIgnoreCase("false")) {
                                        mp.stop();
                                    } else {
                                        mp.start();
                                    }


                                }
                            });

                            /*if(response.contains(".jpg")) {
                                // vh1.video.setVisibility(View.GONE);
                               // vh1.imageView.setImageDrawable(Drawable.createFromPath(response));
                                Bitmap bitmap = Function.getBitmap(response);
                                if(bitmap != null){
                                    vh1.imageView.setImageBitmap(bitmap);
                                }else {

                                }
                            }else if(response.contains(".mp4")) {
                                //vh1.video.setVisibility(View.VISIBLE);
                                File file = new File(response);
                                final Uri uri = Uri.fromFile(file);
                                vh1.video.setVideoURI(uri);
                                // vh1.video.setMediaController(new MediaController(context));
                                vh1.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(final MediaPlayer mp) {

                                        mp.seekTo(1);


                                    }
                                });
                            }*/

                        }
                    }
                    });
                    }


        }, vh1.progressBar, vh1.cancelUploading ).execute(message.files, message.msgid);
    }

    private void uploadContactImage(final ContactViewHolder vh1, final ChatMessage message, final int position) {


        vh1.startUpload.setVisibility(View.GONE);
        vh1.progressBar.setVisibility(View.VISIBLE);
        vh1.cancelUploading.setVisibility(View.VISIBLE);
        new FileUpload(context, new VolleyCallback() {
            @Override
            public void backResponse(final String response) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        vh1.progressBar.setVisibility(View.GONE);
                        vh1.cancelUploading.setVisibility(View.GONE);

                        if (response.equalsIgnoreCase("5")) {
                            chatMessageList.get(position).msgStatus = response;
                            vh1.startUpload.setVisibility(View.VISIBLE);
                            vh1.msgStatus.setImageResource(R.drawable.stopwatch);
                            Toast.makeText(context, "Contact Uploaded Fail!", Toast.LENGTH_LONG).show();
                            DatabaseHelper.getInstance(context).UpdateMsgStatus(response, message.msgid);
                        } else {
                            chatMessageList.get(position).msgStatus = "1";
                            vh1.startUpload.setVisibility(View.GONE);
                            message.files = response;
                            XmppConneceted activity = new XmppConneceted();
                            message.fileData = new byte[0];
                            activity.getmService().xmpp.sendMessage(message, new CallBackUi() {
                                @Override
                                public void update(String s) {
                                    // String aa[] = s.split(",");
                                    Log.d(">>>>", s);
                                    chatMessageList.get(position).msgStatus = s;
                                    if (s.equalsIgnoreCase("1")) {
                                        vh1.msgStatus.setImageResource(R.drawable.tick);
                                    } else if (s.equalsIgnoreCase("2")) {
                                        vh1.msgStatus.setImageResource(R.drawable.reach);
                                    } else {
                                        vh1.msgStatus.setImageResource(R.drawable.read);
                                    }
                                    MediaPlayer mp = MediaPlayer.create(context, R.raw.tick);
                                    if (AppPreferences.getConvertTone(context).equalsIgnoreCase("false")) {
                                        mp.stop();
                                    } else {
                                        mp.start();
                                    }


                                }
                            });


                        }
                    }
                    });
                    }


        }, vh1.progressBar, vh1.cancelUploading ).execute(message.files, message.msgid);
    }

    private void imageDialog(Uri uri) {
        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Image");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_popup);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);
        imageView.setImageURI(uri);
        window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_corner_dialog));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void videoDialog(Uri uri) {
        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Video");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.video_popup);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_corner_dialog));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        VideoView videoView = (VideoView) dialog.findViewById(R.id.videoView);


        videoView.setVideoURI(uri);
        videoView.setMediaController(new MediaController(context));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {

                mp.setScreenOnWhilePlaying(true);
                mp.start();


            }
        });
        dialog.show();
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;


        try {
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (NullPointerException e) {

            e.printStackTrace();
        }

        return byteBuffer.toByteArray();
    }


    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }


    public void toggleSelection(View view, int position) {
        //selectView(position, !mSelectedItemsIds.get(position));
        if (mSelectedItemsIds.get(position, false)) {
            mSelectedItemsIds.delete(position);

        } else {
            mSelectedItemsIds.put(position, true);
        }


        final ChatMessage message = (ChatMessage) chatMessageList.get(position);
        message.setSelected(!message.isSelected());
        view.setBackgroundColor(message.isSelected() ? Color.GRAY : Color.WHITE);

        notifyItemChanged(position);
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(mSelectedItemsIds.size());
        for (int i = 0; i < mSelectedItemsIds.size(); i++) {
            items.add(mSelectedItemsIds.keyAt(i));
        }
        return items;
    }


    public void clearSelections() {
        mSelectedItemsIds.clear();
        notifyDataSetChanged();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView msgStatus;
        public TextView time, reciverName;

        public EmojiconTextView msg, originalmsg;
        public LinearLayout layout;
        public RelativeLayout parent_layout;
        public ImageView img_totf;
        public View view;

        public MessageViewHolder(View view) {
            super(view);
            this.view = view;
            parent_layout = (RelativeLayout) view.findViewById(R.id.bubble_layout_parent);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            msg = (EmojiconTextView) view.findViewById(R.id.message_text);
            time = (TextView) view.findViewById(R.id.time_text);
            reciverName = (TextView) view.findViewById(R.id.reciverName);
            originalmsg = (EmojiconTextView) view.findViewById(R.id.original_text);
            img_totf = (ImageView) view.findViewById(R.id.imgtotf);
            msgStatus = (ImageView) view.findViewById(R.id.msgStatus);
        }
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView msg, addcontact, time;
        public ImageView imageView,msgStatus, startUpload, startDownloading,cancelUploading, cancelDownloading;
        public LinearLayout layout;
        public RelativeLayout parent_layout;
        public View view;
        public ProgressBar progressBar;

        public ContactViewHolder(View view) {
            super(view);
            this.view = view;
            parent_layout = (RelativeLayout) view.findViewById(R.id.bubble_layout_parent);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            imageView = (ImageView) view.findViewById(R.id.image);
            msg = (TextView) view.findViewById(R.id.message_text);
            addcontact = (TextView) view.findViewById(R.id.addcontact);
            time = (TextView) view.findViewById(R.id.time_text);
            msgStatus = (ImageView) view.findViewById(R.id.msgStatus);
            startUpload = (ImageView) view.findViewById(R.id.startUpload);
            startDownloading = (ImageView) view.findViewById(R.id.startUpload);
            cancelUploading = (ImageView) view.findViewById(R.id.cancel);
            cancelDownloading = (ImageView) view.findViewById(R.id.cancel);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        public TextView msg, time;
        public ImageView docImage,imageView,msgStatus, startUpload, startDownloading,cancelUploading, cancelDownloading;
        public VideoView video;
        public LinearLayout layout;
        public RelativeLayout parent_layout,textMsgBg;
        public View view;
        public ProgressBar progressBar;

        public FileViewHolder(View view) {
            super(view);
            this.view = view;
            parent_layout = (RelativeLayout) view.findViewById(R.id.bubble_layout_parent);
            textMsgBg = (RelativeLayout) view.findViewById(R.id.textMsgBg);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            video = (VideoView) view.findViewById(R.id.video);
            docImage = (ImageView) view.findViewById(R.id.docImage);
            imageView = (ImageView) view.findViewById(R.id.image);
            msgStatus = (ImageView) view.findViewById(R.id.msgStatus);
            startUpload = (ImageView) view.findViewById(R.id.startUpload);
            startDownloading = (ImageView) view.findViewById(R.id.startUpload);
            cancelUploading = (ImageView) view.findViewById(R.id.cancel);
            cancelDownloading = (ImageView) view.findViewById(R.id.cancel);
            msg = (TextView) view.findViewById(R.id.message_text);
            time = (TextView) view.findViewById(R.id.time_text);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }





    public void updateStatus(String status, String receiptId){
        Log.d("onReceiptReceived upd", status);
        for(int i=0; i<chatMessageList.size(); i++ ){

            if(chatMessageList.get(i).receiptId == null){

            }else
            if(chatMessageList.get(i).receiptId.equalsIgnoreCase(receiptId)){
                chatMessageList.get(i).msgStatus = status;

                MediaPlayer mp = MediaPlayer.create(context, R.raw.tick);
                if (AppPreferences.getConvertTone(context).equalsIgnoreCase("false")) {
                    mp.stop();
                } else {
                    mp.start();
                }
                //notifyItemChanged(i);
            }
        }
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        if (resultCode == RESULT_OK) {


        }
    }
}