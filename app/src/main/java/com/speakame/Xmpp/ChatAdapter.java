package com.speakame.Xmpp;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.rockerhieu.emojicon.EmojiconTextView;
import com.speakame.Activity.TwoTab_Activity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.DownloadFile;
import com.speakame.utils.FileUpload;
import com.speakame.utils.Function;
import com.speakame.utils.VolleyCallback;

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
import static com.speakame.utils.Function.mediaScanner;

/**
 * Created by Max on 22-Dec-16.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int OTHER = 0;
    private static final int FILE = 1;
    private static final int CONTACT = 2;
    private static final int MESSAGE = 5;
    private static final String TAG = "ChatAdapter";
    final int ADDCONTACT = 24;
    ArrayList<ChatMessage> chatMessageList;
    Context context;
    OnLongClickPressListener longClickPressListener;
    String status;
    private SparseBooleanArray mSelectedItemsIds;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessageList, OnLongClickPressListener longClickPressListener) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        mSelectedItemsIds = new SparseBooleanArray();
        this.longClickPressListener = longClickPressListener;
    }

    public void add(ChatMessage object, int pos) {
        chatMessageList.add(object);
        //notifyItemInserted(getItemCount()+1);
        // mRecyclerView.scrollToPosition(getItemCount()-1);
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
        String fileName = chatMessageList.get(position).body;

        String dialogId = chatMessageList.get(position).dialog_id;
        String messageId = chatMessageList.get(position).qbMessageId;

        Log.d(TAG, "Filename :- " + chatMessageList.get(position).files + ">>" + msg);
        Log.e(TAG, "TOTF get View Type :- " + chatMessageList.get(position).files + "  :-  " + msg);

        if (chatMessageList.get(position).isOtherMsg == 1) {

            Log.v(TAG, "returning other " + ">>" + msg);
            return OTHER;
        } else if (!chatMessageList.get(position).files.equalsIgnoreCase("") && msg.contains(AppConstants.KEY_CONTACT)) {

            Log.v(TAG, "returning contact " + ">>" + msg);
            return CONTACT;
        } else if (chatMessageList.get(position).files.equalsIgnoreCase("")) {

            Log.v(TAG, "returning message " + ">>" + msg);
            return MESSAGE;
        } else {
            Log.v(TAG, "returning file " + ">>" + msg);
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
//                dvsssssssssssssssssssssssssssssss
                View v3 = inflater.inflate(R.layout.chatbubble, viewGroup, false);
                viewHolder = new FileViewHolder(v3);
                break;
            case MESSAGE:
                View v4 = inflater.inflate(R.layout.chatmessage, viewGroup, false);
                viewHolder = new MessageViewHolder(v4);
                break;
            case OTHER:
                View v5 = inflater.inflate(R.layout.other_text_for_gp, viewGroup, false);
                viewHolder = new OrherViewHolder(v5);
                break;
            default:
                View v6 = inflater.inflate(R.layout.chatmessage, viewGroup, false);
                viewHolder = new MessageViewHolder(v6);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case CONTACT:
                Log.v(TAG, "INSIDE CONTACT this for position:- " + position);
                ContactViewHolder vh1 = (ContactViewHolder) viewHolder;
                configureContactViewHolder(vh1, position);
                break;
            case FILE:
                Log.v(TAG, "INSIDE FILE this for position:- " + position);
                FileViewHolder vh3 = (FileViewHolder) viewHolder;
//                vdsssssssssssssssssssssssssss
                configureFileViewHolder(vh3, position);
                break;
            case MESSAGE:
                Log.v(TAG, "INSIDE MESSAGE this for position:- " + position);
                MessageViewHolder vh4 = (MessageViewHolder) viewHolder;
                configureMessageViewHolder(vh4, position);
                break;
            case OTHER:
                Log.v(TAG, "inside this for position:- " + position);
                OrherViewHolder vh5 = (OrherViewHolder) viewHolder;
                configureOtherViewHolder(vh5, position);
                break;
            default:
                MessageViewHolder vh6 = (MessageViewHolder) viewHolder;
                configureMessageViewHolder(vh6, position);
                break;
        }
    }

    private void configureOtherViewHolder(OrherViewHolder vh5, int position) {
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);
        if (message != null) {
            String[] msg = message.body.split("~");

            if (msg.length > 1) {
                vh5.otherMsg.setText(msg[0]);
            } else {
                vh5.otherMsg.setText(msg[0]);
            }

            vh5.otherMsg.setGravity(Gravity.CENTER_HORIZONTAL);
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

                if (message.msgStatus.equalsIgnoreCase("0")) {
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
            String[] contactName = new String[]{"name", "00"};
            if (message.body.equalsIgnoreCase("")) {
                // vh1.textMsgBg.setVisibility(View.GONE);
            } else {
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

    private void configureFileViewHolder_old(final FileViewHolder vh1, final int position) {
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);


        if (message != null) {
            vh1.view.setBackgroundColor(message.isSelected() ? Color.GRAY : 0);

            if (message.fileName == null) {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.GONE);
            } else if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.VISIBLE);
                vh1.video.setVisibility(View.GONE);
            } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                vh1.docImage.setVisibility(View.VISIBLE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.GONE);
            } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.VISIBLE);
            } else {
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

                if (message.msgStatus.equalsIgnoreCase("5")) {
                    vh1.startUpload.setVisibility(View.VISIBLE);
                } else {
                    vh1.startUpload.setVisibility(View.GONE);
                }

                if (message.msgStatus.equalsIgnoreCase("0")) {
                    Log.d("IMAGEPATH upload", message.files + ">>>>");
                    vh1.progressBar.setVisibility(View.VISIBLE);
                    vh1.cancelUploading.setVisibility(View.VISIBLE);

                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
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
//                    uploadImage(vh1, message, position);
                } else {
                    Log.d("IMAGEPATH send", message.files + ">>>>");
                    Log.d("IMAGEPATH send", message.toString() + ">>>>");
                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
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

                if (message.msgStatus.equalsIgnoreCase("0")) {
                    vh1.msgStatus.setImageResource(0);
                } else if (message.msgStatus.equalsIgnoreCase("1")) {
                    vh1.msgStatus.setImageResource(R.drawable.tick);
                } else if (message.msgStatus.equalsIgnoreCase("2")) {
                    vh1.msgStatus.setImageResource(R.drawable.reach);
                } else if (message.msgStatus.equalsIgnoreCase("3")) {
                    vh1.msgStatus.setImageResource(R.drawable.read);
                } else if (message.msgStatus.equalsIgnoreCase("5")) {
                    vh1.msgStatus.setImageResource(R.drawable.stopwatch);
                } else {
                    vh1.msgStatus.setImageResource(0);
                }

                if (message.msgStatus.equalsIgnoreCase("2")) {
                    vh1.msgStatus.setImageResource(R.drawable.tick);
                } else {

                }

            } else {
                vh1.msgStatus.setVisibility(View.GONE);
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);
                vh1.startDownloading.setImageDrawable(context.getResources().getDrawable(R.drawable.downloadimage));

                if (message.msgStatus.equalsIgnoreCase("10")) {
                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageResource(R.mipmap.ic_launcher);
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {

                    }
                    //vh1.imageView.setAlpha(1);
                    vh1.startDownloading.setVisibility(View.GONE);
//                    downLoadFile(vh1, message, position);

//                    vdsssssssssssssss
                } else if (message.msgStatus.equalsIgnoreCase("11")) {
                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageResource(R.mipmap.ic_launcher);
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
                        Log.d("IMAGEPATH recive", message.files);
                    }
                    //vh1.imageView.setAlpha(1);
                    vh1.startDownloading.setVisibility(View.VISIBLE);

                } else {
                    // vh1.imageView.setImageDrawable(Drawable.createFromPath(message.fileName));
                    //vh1.imageView.setAlpha(0);
                    vh1.startDownloading.setVisibility(View.GONE);

                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
                        //vh1.video.setVisibility(View.VISIBLE);
                        File file = new File(message.files);
                        final Uri uri = Uri.fromFile(file);
                        vh1.video.setVideoURI(uri);
                        // vh1.video.setMediaController(new MediaController(context));
                        vh1.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mp) {
                                mp.seekTo(1);
                                //vh1.video.start();
                            }
                        });
                    }
                }
            }

            Log.v(TAG, "TOTF Message files :-  " + message.files);
            final File file = new File(message.files);
            final Uri uri = Uri.fromFile(file);

            Log.v(TAG, "document Uri :- " + uri);


            if (message.body.equalsIgnoreCase("")) {
                vh1.textMsgBg.setVisibility(View.GONE);
            } else {
                vh1.textMsgBg.setVisibility(View.VISIBLE);
                String[] msg = message.body.split("~");

                vh1.msg.setText(msg[0]);

                // vh1.msg.setText(message.body);
            }
            //vh1.imageView.setImageURI(uri);

            vh1.time.setText(message.Time);
/*
            vh1.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("IMAGEPATH reciveclick", message.files + "\n" + file.getAbsolutePath());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.setDataAndType(uri, "image*/
/*");
                    } else {
                        File file = new File(message.files);
                        Uri photoUri = FileProvider.getUriForFile(context, "com.speakame", file);
                        intent.setDataAndType(photoUri, "image*/
/*");
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                    // imageDialog(uri);
                }
            });
*/

            vh1.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "  IMAGEPATH reciveclick   " + message.files + "\n" + file.getAbsolutePath());
                    final File file1 = new File(message.files);
                    final Uri myUri = Uri.fromFile(file1);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.setDataAndType(myUri, "image/*");
//                      intent.setDataAndType(uri, "image/*");
                    } else {
                        File file = new File(message.files);
                        Uri photoUri = FileProvider.getUriForFile(context, "com.speakame", file);
                        intent.setDataAndType(photoUri, "image/*");
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                    // imageDialog(uri);
                }
            });

            vh1.video.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final File file = new File(message.files);
                    final Uri uri = Uri.fromFile(file);
                    Log.d("IMAGEPATH reciveclick v", message.files + "\n" + file.getAbsolutePath());
                    if (!message.msgStatus.equalsIgnoreCase("10")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //intent.setDataAndType(uri,"video/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        //context.startActivity(intent);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            intent.setDataAndType(uri, "video/*");
                        } else {
                            File file1 = new File(message.files);
                            Uri photoUri = FileProvider.getUriForFile(context, "com.speakame", file1);
                            intent.setDataAndType(photoUri, "video/*");
                        }
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        }
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
                    } else {
                        vh1.startUpload.setVisibility(View.GONE);
                        downLoadFile(vh1, message, position);
//                        xzxzvzvzvzvzvzvzvzvzvzv
                    }
                }
            });

            vh1.msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (message.fileName.endsWith(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Log.v(TAG, " pdf File path :- " + message.files);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.setDataAndType(uri, "application/*");
                    } else {
                        File file = new File(message.files);
                        Uri photoUri = FileProvider.getUriForFile(context, "com.speakame", file);
                        intent.setDataAndType(photoUri, "application/*");
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                    // } else {

                    // }
                }
            });
        }
    }

    private void configureFileViewHolder(final FileViewHolder vh1, final int position) {
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);

        Log.d(TAG, " NewMessageStatus :- " + message.msgStatus);
        Log.d(TAG, " NewMessageStatus message.fileName :- " + message.fileName);
        if (message != null) {
            Log.d(TAG, " NewMessageStatus  1 :- " + message.msgStatus);
            Log.d(TAG, " NewMessageStatus message.fileName 1 :- " + message.fileName);
            vh1.view.setBackgroundColor(message.isSelected() ? Color.GRAY : 0);
            if (message.fileName == null) {
                Log.d(TAG, " NewMessageStatus 2 :- " + message.msgStatus);
                Log.d(TAG, " NewMessageStatus message.fileName 2 :- " + message.fileName);
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.GONE);
            } else if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                Log.d(TAG, " NewMessageStatus 3 :- " + message.msgStatus);
                Log.d(TAG, " NewMessageStatus message.fileName 3 :- " + message.fileName);
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.VISIBLE);
                vh1.video.setVisibility(View.GONE);
            } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                vh1.docImage.setVisibility(View.VISIBLE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.GONE);
            } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.VISIBLE);
            } else {
                vh1.docImage.setVisibility(View.GONE);
                vh1.imageView.setVisibility(View.GONE);
                vh1.video.setVisibility(View.GONE);
            }

            if (message.isMine) {
                Log.d(TAG, " NewMessageStatus message.isMine 1 " + message.msgStatus);

                vh1.msgStatus.setVisibility(View.VISIBLE);
//                vh1.startUpload.setImageDrawable(context.getResources().getDrawable(R.drawable.uploadimage));
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
/*
                if (message.msgStatus.equalsIgnoreCase("5")) {
                    vh1.startUpload.setVisibility(View.VISIBLE);
                } else {
                    vh1.startUpload.setVisibility(View.GONE);
                }*/

                if (message.msgStatus.equalsIgnoreCase("0")) {

                    Log.d("IMAGEPATH upload", message.files + ">>>>");
                    vh1.progressBar.setVisibility(View.GONE);
                    vh1.cancelUploading.setVisibility(View.GONE);
                    vh1.startUpload.setVisibility(View.GONE);                 //     12 dec changes

                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
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

//                     cxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//                    uploadImage(vh1, message, position);
                } else {
                    Log.d("IMAGEPATH send", message.files + ">>>>");
                    Log.d("IMAGEPATH send", message.toString() + ">>>>");
                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
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

                if (message.readStatus.equalsIgnoreCase("0")) {
                    vh1.msgStatus.setImageResource(0);
//                    fbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
                } else if (message.readStatus.equalsIgnoreCase("1")) {
                    vh1.msgStatus.setImageResource(R.drawable.tick);
                } else if (message.readStatus.equalsIgnoreCase("2")) {
                    vh1.msgStatus.setImageResource(R.drawable.reach);
                } else if (message.readStatus.equalsIgnoreCase("3")) {
                    vh1.msgStatus.setImageResource(R.drawable.read);
                } else if (message.readStatus.equalsIgnoreCase("5")) {
                    vh1.msgStatus.setImageResource(R.drawable.stopwatch);
                } else {
                    vh1.msgStatus.setImageResource(0);
                }
/*
                if (message.readStatus.equalsIgnoreCase("2")) {
                    vh1.msgStatus.setImageResource(R.drawable.tick);
                } else {

                }*/

            } else {
//vsddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd
                Log.d(TAG, " NewMessageStatus message.Receiver... " + message.msgStatus);

                vh1.msgStatus.setVisibility(View.GONE);
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);
                vh1.startDownloading.setImageDrawable(context.getResources().getDrawable(R.drawable.downloadimage));

                Log.d(TAG, " NewMessageStatus message.Receiver10 12 dec :- " + message.msgStatus);
                Log.v(TAG, " message.fileName 12 dec :- . " + message.fileName);
                Log.v(TAG, " message.files  12 dec :- " + message.files);

                if (message.msgStatus.equalsIgnoreCase("10")) {

                    Log.d(TAG, " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ If status is 10 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + message.msgStatus);

                    Log.d(TAG, " NewMessageStatus message.Receiver10 12 dec :- " + message.msgStatus);

                    Log.v(TAG, " message.fileName 12 dec :- . " + message.fileName);
                    Log.v(TAG, " message.files  12 dec :- " + message.files);

                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        Log.d(TAG, " NewMessageStatus message.message.fileName... " + message.msgStatus + " : " + message.fileName);

                        Log.d(TAG, " NewMessageStatus message.Receiver10 12 dec :- " + message.msgStatus);

                        Log.v(TAG, " message.fileName 12 dec :- . " + message.fileName);
                        Log.v(TAG, " message.files  12 dec :- " + message.files);

                        final File file = new File(message.files);
                        final Uri uri = Uri.fromFile(file);

                        Log.v(TAG, " files from Path  12 dec :- " + file);
                        Log.v(TAG, " URI from File  12 dec :- " + uri);

                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                        //  vh1.imageView.setImageResource(R.mipmap.ic_launcher);
//                        vh1.imageView.setImageURI(uri);

                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {

                    }
                    //vh1.imageView.setAlpha(1);
                    Log.d(TAG, " NewMessageStatus message.message.fileName...Bahar " + message.msgStatus + " : " + message.fileName + "\n " +
                            " message.files " + message.files);

                    vh1.startDownloading.setVisibility(View.GONE);
                    downLoadFile(vh1, message, position);

//dffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffb

                } else if (message.msgStatus.equalsIgnoreCase("11")) {

                    Log.d(TAG, " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ If status is 11 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + message.msgStatus);

                    Log.d(TAG, " NewMessageStatus message.message.fileName11 ... " + message.msgStatus + " : " + message.fileName);

                    Log.v(TAG, " message.fileName 12 dec :- . " + message.fileName);
                    Log.v(TAG, " message.files  12 dec :- " + message.files);

                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        Log.d(TAG, " NewMessageStatus message.message.fileName1122 ... " + message.msgStatus + " : " + message.fileName);

                        vh1.imageView.setImageResource(R.mipmap.ic_launcher);
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
                        Log.d("IMAGEPATH recive", message.files);
                    }
                    //vh1.imageView.setAlpha(1);
                    vh1.startDownloading.setVisibility(View.VISIBLE);
                    Log.d(TAG, " NewMessageStatus message.message.fileName1144 ... " + message.msgStatus + " : " + message.fileName);

                } else {
                    // vh1.imageView.setImageDrawable(Drawable.createFromPath(message.fileName));
                    //vh1.imageView.setAlpha(0);

                    Log.d(TAG, " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ If status is other than 10/11 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + message.msgStatus);


                    Log.v(TAG, " message.fileName 12 dec :- . " + message.fileName);
                    Log.v(TAG, " message.files  12 dec :- " + message.files);

                    vh1.startDownloading.setVisibility(View.GONE);
                    Log.d(TAG, " NewMessageStatus message.message.fileName1155 ... " + message.msgStatus + " : " + message.fileName);

                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        Log.d(TAG, " NewMessageStatus message.message.fileName1166 ... " + message.msgStatus + " : " + message.fileName);
                        Log.d(TAG, " NewMessageStatus message.message.fileName11667788 ... " + message.files);

                        vh1.imageView.setImageDrawable(Drawable.createFromPath(message.files));
                    } else if (message.fileName.contains(".pdf") || message.fileName.contains(".docx")) {
                        if (message.fileName.contains(".pdf")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_pdf);
                        } else if (message.fileName.contains(".docx")) {
                            vh1.docImage.setImageResource(R.mipmap.ic_doc);
                        }
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
                        //vh1.video.setVisibility(View.VISIBLE);
                        File file = new File(message.files);
                        final Uri uri = Uri.fromFile(file);
                        vh1.video.setVideoURI(uri);
                        // vh1.video.setMediaController(new MediaController(context));
                        vh1.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mp) {
                                mp.seekTo(1);
                                //vh1.video.start();
                            }
                        });
                    }
                    Log.d(TAG, " NewMessageStatus message.message.fileName11 6666... " + message.msgStatus + " : " + message.fileName);
                }
            }
//vdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
            final File file = new File(message.files);
            final Uri uri = Uri.fromFile(file);

            Log.v(TAG, "document Uri :- " + uri);
//            fddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd

            if (message.body.equalsIgnoreCase("")) {
                vh1.textMsgBg.setVisibility(View.GONE);
            } else {
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
                    Log.d(TAG, " IMAGEPATH reciveclick   " + message.files + "\n" + file.getAbsolutePath());

//                    final File file_path = new File(message.files);

                    File file_path = null;
                    Uri myUri = null;

                    if (message.isMine) {
                        file_path = new File(file.getAbsolutePath());
                        myUri = Uri.fromFile(file_path);

                    } else {

                        file_path = new File(message.files);
//                        file_path = new File(file.getAbsolutePath());
                        myUri = Uri.fromFile(file_path);
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.setDataAndType(myUri, "image/*");
//                      intent.setDataAndType(uri, "image/*");
                    } else {
                        File file = new File(message.files);
                        Uri photoUri = FileProvider.getUriForFile(context, "com.speakame", file);
                        intent.setDataAndType(photoUri, "image/*");
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                    // imageDialog(uri);
                }
            });
            vh1.video.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final File file = new File(message.files);
                    final Uri uri = Uri.fromFile(file);
                    Log.d("IMAGEPATH reciveclick v", message.files + "\n" + file.getAbsolutePath());
                    if (!message.msgStatus.equalsIgnoreCase("10")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //intent.setDataAndType(uri,"video/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        //context.startActivity(intent);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            intent.setDataAndType(uri, "video/*");
                        } else {
                            File file1 = new File(message.files);
                            Uri photoUri = FileProvider.getUriForFile(context, "com.speakame", file1);
                            intent.setDataAndType(photoUri, "video/*");
                        }
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        }
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


//                        fbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb

                    } else {
                        vh1.startUpload.setVisibility(View.GONE);
                        downLoadFile(vh1, message, position);
//                        xzxzvzvzvzvzvzvzvzvzvzv
                    }
                }
            });
            vh1.msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (message.fileName.endsWith(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Log.v(TAG, " pdf File path :- " + message.files);
                    final File file1 = new File(message.files);
                    final Uri uriMy = Uri.fromFile(file1);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                        intent.setDataAndType(uri, "application/*");
                        intent.setDataAndType(uriMy, "application/*");
                    } else {
//                        dsvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                        File file = new File(message.files);
                        Uri photoUri = FileProvider.getUriForFile(context, "com.speakame", file);
                        intent.setDataAndType(photoUri, "application/*");
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                    // } else {

                    // }
                }
            });
        }
    }


    private void configureMessageViewHolder_old(final MessageViewHolder vh1, final int position) {

        final ChatMessage message = (ChatMessage) chatMessageList.get(position);
        Log.v(TAG, "Read Status pragya : -" + message.readStatus + "---------" + message.body);
//        Log.v(TAG, "Read Status pragya : -" + message.body);
        Log.v(TAG, "TOTF MESSAGE Testing" + message.body);
        Log.v(TAG, "TOTF MESSAGE Testing messade files" + message.files);

        if (message != null) {
            vh1.storeData = message;
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
                        vh1.layout.setBackgroundResource(R.drawable.yyk);  // not in use
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

                Log.d("groupName", message.groupName + ":::" + message.type);

                if (message.msgStatus.equalsIgnoreCase("0")) {

//                    XmppConneceted activity = new XmppConneceted();
                    if (message.groupName.equalsIgnoreCase("")) {

                        /*~~~~~~~~~~~~~~~~~~~~~~~~~ sendGroupMessage ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

                        /*activity.getmService().xmpp.sendMessage(message, new CallBackUi() {
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
                        });*/
                    } else {

                        /*~~~~~~~~~~~~~~~~~~~~~~~~~ sendGroupMessage ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

                       /* activity.getmService().xmpp.sendGroupMessage(message, new CallBackUi() {
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
                        });*/
                    }
                }

                Log.v(TAG, "Message status after messsage send 1:- " + message.msgStatus);
                Log.v(TAG, "Message status after messsage send 2:- " + status);

                try {
                    if (message.readStatus != null) {
                        if (message.readStatus.equalsIgnoreCase("0")) {
                            vh1.msgStatus.setImageResource(0);
                        } else if (message.readStatus.equalsIgnoreCase("1")) {
                            vh1.msgStatus.setImageResource(R.drawable.tick);
                        } else if (message.readStatus.equalsIgnoreCase("2")) {
                            vh1.msgStatus.setImageResource(R.drawable.reach);
                        } else if (message.readStatus.equalsIgnoreCase("3")) {
                            vh1.msgStatus.setImageResource(R.drawable.read);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                if (message.groupName == null) {
                    vh1.reciverName.setVisibility(View.GONE);
                } else if (message.groupName.equalsIgnoreCase("")) {
                    vh1.reciverName.setVisibility(View.GONE);
                } else {
                    vh1.reciverName.setVisibility(View.VISIBLE);
                    if (message.senderName.contains("@ip-")) {
                        vh1.reciverName.setVisibility(View.GONE);
                    } else {
                        if (Function.isStringInt(message.senderName)) {
                            vh1.reciverName.setText("+" + message.senderName);
                        } else {
                            vh1.reciverName.setText(message.senderName);
                        }
                        //vh1.reciverName.setText(message.senderName);
                        //vh1.reciverName.setText(getContactName(message.sender));
                    }
                }

            }

            String[] msg = message.body.split("~");
            Log.v(TAG, "");

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

    private void configureMessageViewHolder(final MessageViewHolder vh1, final int position) {
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);
        Log.v(TAG, "Read Status pragya 11: -" + message.readStatus + "---------" + message.body + " Position : " + position + "  Type : " + message.type + " msg status " + message.msgStatus);
        Log.v(TAG, "Read Status pragya 22: -" + message.receiver + " :  " + message.sender + " :  " + Message.Type.chat.name());

        if (message != null) {
            vh1.storeData = message;
            vh1.view.setBackgroundColor(message.isSelected() ? Color.GRAY : 0);
            Log.d(TAG, "not null message Dhakad " + message);
            if (message.isMine) {
                Log.d(TAG, "isMine message Dhakad " + message);

                vh1.msgStatus.setVisibility(View.VISIBLE);
                vh1.reciverName.setVisibility(View.GONE);
                try {
                    if (message.type.equalsIgnoreCase(Message.Type.chat.name()) && message.receiver.equalsIgnoreCase(message.sender)) {
                        vh1.layout.setBackgroundResource(0);
                        Log.d(TAG, "Color.GRAY message Dhakad 11" + message.type + " :  " + Message.Type.chat.name());
                        Log.d(TAG, "Color.GRAY message Dhakad 22" + message.receiver + " :  " + message.sender);

                        //parent_layout.setGravity(Gravity.CENTER_HORIZONTAL);
                        vh1.msg.setTextColor(Color.GRAY);
                        vh1.originalmsg.setTextColor(Color.GRAY);
                        vh1.time.setTextColor(Color.GRAY);
                        //  vh1.msgStatus.setVisibility(View.GONE);

                    } else {

                        Log.d(TAG, "Color.GRAY message Dhakad 33 null" + message.type + " :  " + Message.Type.chat.name());

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

                Log.d("groupName", message.groupName + ":::" + message.type);

                if (message.msgStatus.equalsIgnoreCase("0")) {

//                    XmppConneceted activity = new XmppConneceted();
                    if (message.groupName.equalsIgnoreCase("")) {

                        /*~~~~~~~~~~~~~~~~~~~~~~~~~ sendGroupMessage ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

                        /*activity.getmService().xmpp.sendMessage(message, new CallBackUi() {
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
                        });*/
                    } else {

                        /*~~~~~~~~~~~~~~~~~~~~~~~~~ sendGroupMessage ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

                       /* activity.getmService().xmpp.sendGroupMessage(message, new CallBackUi() {
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
                        });*/
                    }
                }

                Log.v(TAG, "Message status after messsage send 1:- " + message.msgStatus);
                Log.v(TAG, "Message status after messsage send 2:- " + status);

                try {
                    if (!message.readStatus.equalsIgnoreCase(null)) {
                        if (message.readStatus.equalsIgnoreCase("0")) {
                            vh1.msgStatus.setImageResource(0);
                        } else if (message.readStatus.equalsIgnoreCase("1")) {
                            vh1.msgStatus.setImageResource(R.drawable.tick);
                        } else if (message.readStatus.equalsIgnoreCase("2")) {
                            vh1.msgStatus.setImageResource(R.drawable.reach);
                        } else if (message.readStatus.equalsIgnoreCase("3")) {
                            vh1.msgStatus.setImageResource(R.drawable.read);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, " ELSE contidoonn dhakad  " + message.isMine);
                vh1.msgStatus.setVisibility(View.GONE);
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.originalmsg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);
                // vh1.msgStatus.setVisibility(View.GONE);

                //DatabaseHelper.getInstance(context).UpdateMsgStatus("12",message.msgid);
/* else if (message.groupName.equalsIgnoreCase("")) {
                    Log.d(TAG," ID message.groupName dhakad null 120 "+message.groupName);

                    vh1.reciverName.setVisibility(View.GONE);
                }*/
                if (message.groupName.equalsIgnoreCase("") || message.groupName == null) {
                    Log.d(TAG, " ID message.groupName dhakad null  " + message.groupName);

                    vh1.reciverName.setVisibility(View.GONE);
                } else {
                    vh1.reciverName.setVisibility(View.VISIBLE);
                    Log.d(TAG, " ID message.groupName dhakad reciverName 121 " + message.groupName);

                    if (message.senderName.contains("@ip-")) {
                        Log.d(TAG, " ID message.groupName dhakad senderName @ip- " + message.senderName);

                        vh1.reciverName.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, " ID message.groupName dhakad Function 1- " + message.senderName);

                        if (Function.isStringInt(message.senderName)) {
                            Log.d(TAG, " ID message.groupName dhakad Function  isStringInt 2- " + message.senderName);

                            vh1.reciverName.setText("+" + message.senderName);
                        } else {
                            Log.d(TAG, " ID message.groupName dhakad Function  isStringInt 3- " + message.senderName);

                            vh1.reciverName.setText(message.senderName);
                        }
                        //vh1.reciverName.setText(message.senderName);
                        //vh1.reciverName.setText(getContactName(message.sender));
                    }
                }

            }
            String[] msg = message.body.split("~");

            if (msg.length > 1) {
                vh1.originalmsg.setText(msg[0]);
                vh1.msg.setText(msg[2]);
                vh1.originalmsg.setVisibility(View.VISIBLE);
                vh1.img_totf.setVisibility(View.VISIBLE);
                Log.d(TAG, " ID message.groupName dhakad Function msg.length > 1- " + msg.length + " : " + msg[0] + " : " + msg[2]);

            } else {
                Log.d(TAG, " ID message.groupName dhakad Function msg.length < 1- " + msg.length + " : " + msg[0]);

                vh1.msg.setText(msg[0]);
                vh1.originalmsg.setVisibility(View.GONE);
                vh1.img_totf.setVisibility(View.GONE);
            }

            vh1.time.setText(message.Time);


        }
    }

    private void downLoadFile(final FileViewHolder vh1, final ChatMessage message, final int pos) {
        /*ObjectAnimator animation = ObjectAnimator.ofInt (vh1.progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration (5000); //in milliseconds
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();*/
        vh1.progressBar.setVisibility(View.VISIBLE);
        vh1.cancelDownloading.setVisibility(View.VISIBLE);

        new DownloadFile(context, vh1.cancelDownloading, vh1.progressBar, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                Log.v(TAG, "chatMessageList.get(pos).files of image if chat activity is open :- " + chatMessageList.get(pos).files);
                Log.v(TAG, "download response of image if chat activity is open :- " + response);

                if (response.equalsIgnoreCase("11")) {
                    vh1.imageView.setImageDrawable(Drawable.createFromPath(response));
                    DatabaseHelper.getInstance(context).UpdateMsgStatus(response, message.msgid);
                } else {

                    DatabaseHelper.getInstance(context).UpdateMsgStatus("12", message.msgid);

                    DatabaseHelper.getInstance(context).UpdateFileName(response, message.msgid);

                    if (message.fileName.contains(".jpg") || message.fileName.contains(".png")) {
                        // vh1.video.setVisibility(View.GONE);
                        vh1.imageView.setImageDrawable(Drawable.createFromPath(response));
                        vh1.progressBar.setVisibility(View.GONE);
                        chatMessageList.get(pos).files = response;
//                        chatMessageList.get(pos).files = response;
                        //notifyItemChanged(pos);
                    } else if (message.fileName.contains(".mp4") || message.fileName.contains(".3gp") || message.fileName.contains(".MOV")) {
                        //vh1.video.setVisibility(View.VISIBLE);
                        chatMessageList.get(pos).files = response;
                        chatMessageList.get(pos).msgStatus = "12";
                        File file = new File(response);
                        final Uri uri = Uri.fromFile(file);
                        vh1.video.setVideoURI(uri);
                        // vh1.video.setMediaController(new MediaController(context));
                        vh1.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mp) {
                                mp.seekTo(1);
                                mp.start();
                            }
                        });
                        vh1.progressBar.setVisibility(View.GONE);
                    } else {//this line is added for the pdf file shown
                        chatMessageList.get(pos).files = response;
                    }

                }

                vh1.cancelDownloading.setVisibility(View.GONE);
                /*File SpeakaMe = Environment.getExternalStorageDirectory();
                File SpeakaMeDirectory = new File(SpeakaMe + "/SpeakaMe/image/recive");
                String file = SpeakaMeDirectory+"/"+message.fileName;*/
                mediaScanner(response);
                Bitmap bitmap = BitmapFactory.decodeFile((message.files));
                MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, message.fileName, null);
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
                            Toast.makeText(context, "File Uploaded Fail !", Toast.LENGTH_LONG).show();
                            DatabaseHelper.getInstance(context).UpdateMsgStatus(response, message.msgid);
                        } else {
                            chatMessageList.get(position).msgStatus = "1";
                            vh1.startUpload.setVisibility(View.GONE);
                            message.files = response;
                            String fileExte = Function.getFileExtention(message.fileName);
                            String msg = message.body;
                            if ((fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {

                                message.Contact = msg;
                            } else if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {

                                message.Image = response;
                            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {

                                message.Video = response;
                            } else if (fileExte.equalsIgnoreCase("pdf")) {

                                message.Document = response;
                            }
//                            XmppConneceted activity = new XmppConneceted();
                            message.fileData = new byte[0];
                            // Toast.makeText(context, message.groupid+"File Uploaded !", Toast.LENGTH_LONG).show();
                            Log.d("groupid>>>>", message.groupid + ">");
                            if (message.groupid == null ||
                                    message.groupid.equalsIgnoreCase("")) {

                                /*activity.getmService().xmpp.sendMessage(message, new CallBackUi() {
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
                                */
                            } else {

                               /* activity.getmService().xmpp.sendGroupMessage(message, new CallBackUi() {
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
                                });*/
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeFile((message.files));
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, message.fileName, null);
                        mediaScanner(response);
                    }
                });
            }
        }, vh1.progressBar, vh1.cancelUploading).execute(message.files, message.msgid);
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

                            String fileExte = Function.getFileExtention(message.fileName);
                            String msg[] = message.body.split(AppConstants.KEY_CONTACT);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("name", msg[0]);
                                jsonObject.put("number", msg[1]);
                                jsonObject.put("image", response);
                                message.Contact = jsonObject.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


//                            XmppConneceted activity = new XmppConneceted();
                            message.fileData = new byte[0];
                            if (message.groupid == null ||
                                    message.groupid.equalsIgnoreCase("")) {
                           /*
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
                                });*/

                            } else {

                                /*activity.getmService().xmpp.sendGroupMessage(message, new CallBackUi() {
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
                                });*/

                            }

                        }
                    }
                });
            }


        }, vh1.progressBar, vh1.cancelUploading).execute(message.files, message.msgid);
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

    public void toggleSelection(int position) {
        //selectView(position, !mSelectedItemsIds.get(position));
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);
        if (mSelectedItemsIds.get(position, false)) {
            mSelectedItemsIds.delete(position);
            message.setSelected(false);
        } else {
            mSelectedItemsIds.put(position, true);
            message.setSelected(true);
        }
        //view.setBackgroundColor(message.isSelected() ? Color.GRAY : Color.WHITE);

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

    public void updateStatus(String status, String receiptId) {
        Log.d("onReceiptReceived upd", status);
        for (int i = 0; i < chatMessageList.size(); i++) {

            if (chatMessageList.get(i).receiptId == null) {

            } else if (chatMessageList.get(i).receiptId.equalsIgnoreCase(receiptId)) {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        if (resultCode == RESULT_OK) {


        }
    }

    public String getContactName(String number) {
        String name = number;

        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        // jsonContacts = new JSONArray();
        if (cursor.moveToFirst()) {

            //jsonContacts = new JSONObject();
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactname = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        if (contactNumber.equalsIgnoreCase(number)) {
                            name = (contactname);
                        }

                        break;

                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        return name;
    }

    public interface OnLongClickPressListener {
        void onItemClicked(int position);

        boolean onItemLongClicked(int position);
    }

    public class OrherViewHolder extends RecyclerView.ViewHolder {

        public TextView otherMsg;

        public OrherViewHolder(View view) {
            super(view);
            otherMsg = (TextView) view.findViewById(R.id.otherMsg);
        }


    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView msgStatus;
        public TextView time, reciverName;
        public EmojiconTextView msg, originalmsg;
        public LinearLayout layout;
        public RelativeLayout parent_layout;
        public ImageView img_totf;
        public View view;
        ChatMessage storeData;

        public MessageViewHolder(View view) {
            super(view);
            this.view = view;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            parent_layout = (RelativeLayout) view.findViewById(R.id.bubble_layout_parent);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            msg = (EmojiconTextView) view.findViewById(R.id.message_text);
            time = (TextView) view.findViewById(R.id.time_text);
            reciverName = (TextView) view.findViewById(R.id.reciverName);
            originalmsg = (EmojiconTextView) view.findViewById(R.id.original_text);
            img_totf = (ImageView) view.findViewById(R.id.imgtotf);
            msgStatus = (ImageView) view.findViewById(R.id.msgStatus);
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickPressListener != null) {
                longClickPressListener.onItemLongClicked(getLayoutPosition());
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (longClickPressListener != null) {
                longClickPressListener.onItemClicked(getLayoutPosition());
            }
        }
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView msg, addcontact, time;
        public ImageView imageView, msgStatus, startUpload, startDownloading, cancelUploading, cancelDownloading;
        public LinearLayout layout;
        public RelativeLayout parent_layout;
        public View view;
        public ProgressBar progressBar;

        public ContactViewHolder(View view) {
            super(view);
            this.view = view;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
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

        @Override
        public boolean onLongClick(View v) {
            if (longClickPressListener != null) {
                longClickPressListener.onItemLongClicked(getLayoutPosition());
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (longClickPressListener != null) {
                longClickPressListener.onItemClicked(getLayoutPosition());
            }
        }
    }

    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView msg, time;
        public ImageView docImage, imageView, msgStatus, startUpload, startDownloading, cancelUploading, cancelDownloading;
        public VideoView video;
        public LinearLayout layout;
        public RelativeLayout parent_layout, textMsgBg;
        public View view;
        public ProgressBar progressBar;

        public FileViewHolder(View view) {

            super(view);
            this.view = view;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
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

        @Override
        public boolean onLongClick(View v) {
            if (longClickPressListener != null) {
                longClickPressListener.onItemLongClicked(getLayoutPosition());
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (longClickPressListener != null) {
                longClickPressListener.onItemClicked(getLayoutPosition());
            }
        }
    }
}
