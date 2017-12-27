package com.speakame.Xmpp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
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
import android.webkit.MimeTypeMap;
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

import static com.speakame.Activity.TwoTab_Activity.adapter;

/**
 * Created by Max on 22-Dec-16.
 */
public class ChatAdapterOld extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CONTACT = 1;
    private static final int IMAGE = 2;
    private static final int VIDEO = 3;
    private static final int DOC = 4;
    private static final int MESSAGE = 5;
    ArrayList<ChatMessage> chatMessageList;
    Context context;
    private SparseBooleanArray mSelectedItemsIds;

    public ChatAdapterOld(Context context, ArrayList<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void add(ChatMessage object, int pos) {
        chatMessageList.add(object);
        notifyItemInserted(pos);

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
        //String fileName = chatMessageList.get(position).fileName;
        String fileName = MimeTypeMap.getFileExtensionFromUrl(chatMessageList.get(position).fileName);

        String msg = chatMessageList.get(position).body;
        Log.d("Filename", ">>" + fileName);
        if ((fileName.equalsIgnoreCase("png") || fileName.equalsIgnoreCase("jpg") || fileName.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {
            Log.d("Filename contact", fileName);
            return CONTACT;
        } else if (fileName.equalsIgnoreCase("png") || fileName.equalsIgnoreCase("jpg") || fileName.equalsIgnoreCase("jpeg")) {
            Log.d("Filename image", fileName);
            return IMAGE;
        } else if (fileName.equalsIgnoreCase("mp4") || fileName.equalsIgnoreCase("3gp")) {
            Log.d("Filename video", fileName);
            return VIDEO;
        } else if (fileName.equalsIgnoreCase("pdf")) {
            Log.d("Filename doc", fileName);
            return DOC;
        } else {
            Log.d("Filename msg", fileName);
            return MESSAGE;
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
            case IMAGE:
                View v2 = inflater.inflate(R.layout.chatimage, viewGroup, false);
                viewHolder = new ImageViewHolder(v2);
                break;
            case VIDEO:
                View v3 = inflater.inflate(R.layout.chatvideo, viewGroup, false);
                viewHolder = new VideoViewHolder(v3);
                break;
            case DOC:
                View v4 = inflater.inflate(R.layout.chatdocument, viewGroup, false);
                viewHolder = new DocViewHolder(v4);
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
            case IMAGE:
                ImageViewHolder vh2 = (ImageViewHolder) viewHolder;
                configureImageViewHolder(vh2, position);
                break;
            case VIDEO:
                VideoViewHolder vh3 = (VideoViewHolder) viewHolder;
                configureVideoViewHolder(vh3, position);
                break;
            case DOC:
                DocViewHolder vh4 = (DocViewHolder) viewHolder;
                configureDocViewHolder(vh4, position);
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

            } else {
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);
                vh1.addcontact.setVisibility(View.VISIBLE);
            }
            String[] contactName = message.body.split(AppConstants.KEY_CONTACT);
            File file = new File(message.fileName);
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

            vh1.msg.setText(contactName[0]);
            vh1.imageView.setImageURI(uri);

            //K:mma

            vh1.time.setText(message.Time);

            final byte[] finalInputData = inputData;
            vh1.addcontact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] contactName = message.body.split(AppConstants.KEY_CONTACT);
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                    intent.putExtra(ContactsContract.Intents.Insert.NAME, contactName[0]);
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, contactName[1]);
                    intent.putExtra(ContactsContract.CommonDataKinds.Photo.PHOTO, finalInputData);

                    context.startActivity(intent);
                }
            });
        }
    }


    private void configureImageViewHolder(final ImageViewHolder vh1, final int position) {
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

                if(message.msgStatus.equalsIgnoreCase("0")) {
                    vh1.progressBar.setVisibility(View.VISIBLE);
                    new UploadImage(context, new VolleyCallback() {
                        @Override
                        public void backResponse(String response) {
                            vh1.progressBar.setVisibility(View.GONE);

                            if (response.equalsIgnoreCase("")) {
                                chatMessageList.get(position).msgStatus = "5";
                                vh1.msgStatus.setImageResource(R.drawable.stopwatch);
                                Toast.makeText(context, "File Uploaded Fail!", Toast.LENGTH_LONG).show();
                            } else {
                                chatMessageList.get(position).msgStatus = "1";
                                //vh1.msgStatus.setImageResource(R.drawable.tick);

                                DatabaseHelper.getInstance(context).insertChat(message);
                                message.files = response;
                               /* XmppConneceted activity = new XmppConneceted();
                                activity.getmService().xmpp.sendMessage(message, new CallBackUi() {
                                    @Override
                                    public void update(String s) {

                                    }
                                });*/

                            }
                        }
                    }).execute(message.files, message.msgid);
                }else{

                }

            } else {
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);

                //if() {
                    Transformation blurTransformation = new Transformation() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public Bitmap transform(Bitmap source) {
                            Bitmap blurredBitmap = Bitmap.createBitmap(source);
                            RenderScript rs = RenderScript.create(context);
                            // Allocate memory for Renderscript to work with
                            Allocation input = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
                            Allocation output = Allocation.createTyped(rs, input.getType());

                            // Load up an instance of the specific script that we want to use.
                            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                            script.setInput(input);

                            // Set the blur radius
                            script.setRadius(10);

                            // Start the ScriptIntrinisicBlur
                            script.forEach(output);

                            // Copy the output to the blurred bitmap
                            output.copyTo(blurredBitmap);
                            source.recycle();
                            return blurredBitmap;
                        }

                        @Override
                        public String key() {
                            return "blur()";
                        }
                    };
Log.d("FILESSS",message.files);
                    Picasso.with(context)
                            .load(message.files) // thumbnail url goes here
                            .placeholder(R.drawable.add_pic)
                            //.resize(imageViewWidth, imageViewHeight)
                            .transform(blurTransformation)
                            .into(vh1.imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Picasso.with(context)
                                            .load(message.files) // image url goes here
                                            //.resize(imageViewWidth, imageViewHeight)
                                            .placeholder(vh1.imageView.getDrawable())
                                            .into(vh1.imageView);
                                }

                                @Override
                                public void onError() {
                                }
                            });
              //  }
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

            File file = new File(message.fileName);
            final Uri uri = Uri.fromFile(file);

            vh1.msg.setText(message.body);
            //vh1.imageView.setImageURI(uri);


            vh1.time.setText(message.Time);

            vh1.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageDialog(uri);
                }
            });


        }
    }

    private void configureVideoViewHolder(VideoViewHolder vh1, int position) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        if (message != null) {
            vh1.view.setBackgroundColor(message.isSelected() ? Color.GRAY : 0);
            if (message.isMine) {

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

            } else {
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);

            }

            File file = new File(message.fileName);
            final Uri uri = Uri.fromFile(file);

            vh1.msg.setText(message.body);
            vh1.time.setText(message.Time);

            vh1.videoView.setVideoURI(uri);
            vh1.msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            vh1.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {

                    mp.seekTo(1);

                }
            });
            vh1.videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    videoDialog(uri);
                    return false;
                }
            });
        }
    }

    private void configureDocViewHolder(DocViewHolder vh1, int position) {
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

            } else {
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);

            }

            File file = new File(message.fileName);
            final Uri uri = Uri.fromFile(file);

            vh1.msg.setText(message.body);
            vh1.time.setText(message.Time);
            vh1.msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (message.fileName.endsWith(".pdf")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                    } else {

                    }
                }
            });
        }
    }

    private void configureMessageViewHolder(MessageViewHolder vh1, int position) {
        final ChatMessage message = (ChatMessage) chatMessageList.get(position);
        if (message != null) {
            vh1.view.setBackgroundColor(message.isSelected() ? Color.GRAY : 0);
            if (message.isMine) {
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

            } else {
                vh1.layout.setBackgroundResource(R.drawable.bubble1);
                vh1.parent_layout.setGravity(Gravity.LEFT);
                vh1.msg.setTextColor(Color.BLACK);
                vh1.originalmsg.setTextColor(Color.BLACK);
                vh1.time.setTextColor(Color.BLACK);
               // vh1.msgStatus.setVisibility(View.GONE);
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

            if(message.msgStatus.equalsIgnoreCase("0")){
                vh1.msgStatus.setImageResource(0);
            }else if(message.msgStatus.equalsIgnoreCase("1")){
                vh1.msgStatus.setImageResource(R.drawable.tick);
            }else if(message.msgStatus.equalsIgnoreCase("2")) {
                vh1.msgStatus.setImageResource(R.drawable.reach);
            }else if(message.msgStatus.equalsIgnoreCase("3")) {
                vh1.msgStatus.setImageResource(R.drawable.read);
            }

            vh1.time.setText(message.Time);


        }
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

    public void updateStatus(String status, String receiptId) {
        Log.d("onReceiptReceived upd", status);
        for (int i = 0; i < chatMessageList.size(); i++) {

            if (chatMessageList.get(i).receiptId == null) {

            } else if (chatMessageList.get(i).receiptId.equalsIgnoreCase(receiptId)) {
                chatMessageList.get(i).msgStatus = status;

                MediaPlayer mp = MediaPlayer.create(context, R.raw.tick);
                mp.start();
                notifyItemChanged(i);
            }
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public static ImageView msgStatus;
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
        public ImageView imageView;
        public LinearLayout layout;
        public RelativeLayout parent_layout;
        public View view;

        public ContactViewHolder(View view) {
            super(view);
            this.view = view;
            parent_layout = (RelativeLayout) view.findViewById(R.id.bubble_layout_parent);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            imageView = (ImageView) view.findViewById(R.id.image);
            msg = (TextView) view.findViewById(R.id.message_text);
            addcontact = (TextView) view.findViewById(R.id.addcontact);
            time = (TextView) view.findViewById(R.id.time_text);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView msg, time;
        public ImageView imageView,msgStatus;
        public LinearLayout layout;
        public RelativeLayout parent_layout;
        public View view;
        public ProgressBar progressBar;

        public ImageViewHolder(View view) {
            super(view);
            this.view = view;
            parent_layout = (RelativeLayout) view.findViewById(R.id.bubble_layout_parent);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            imageView = (ImageView) view.findViewById(R.id.image);
            msgStatus = (ImageView) view.findViewById(R.id.msgStatus);
            msg = (TextView) view.findViewById(R.id.message_text);
            time = (TextView) view.findViewById(R.id.time_text);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public TextView msg, time;
        public VideoView videoView;
        public LinearLayout layout;
        public RelativeLayout parent_layout;
        public View view;

        public VideoViewHolder(View view) {
            super(view);
            this.view = view;
            parent_layout = (RelativeLayout) view.findViewById(R.id.bubble_layout_parent);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            videoView = (VideoView) view.findViewById(R.id.videoView);
            msg = (TextView) view.findViewById(R.id.message_text);
            time = (TextView) view.findViewById(R.id.time_text);
        }
    }

    public class DocViewHolder extends RecyclerView.ViewHolder {
        public TextView msg, time;
        public LinearLayout layout;
        public RelativeLayout parent_layout;
        public View view;

        public DocViewHolder(View view) {
            super(view);
            this.view = view;
            parent_layout = (RelativeLayout) view.findViewById(R.id.bubble_layout_parent);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            msg = (TextView) view.findViewById(R.id.message_text);
            time = (TextView) view.findViewById(R.id.time_text);
        }
    }


}
