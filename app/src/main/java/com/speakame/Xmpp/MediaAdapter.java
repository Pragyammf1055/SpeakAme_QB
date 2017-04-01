package com.speakame.Xmpp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.speakame.R;
import com.speakame.utils.AppConstants;

import org.jivesoftware.smack.packet.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Max on 22-Dec-16.
 */
public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CONTACT = 1;
    private static final int IMAGE = 2;
    private static final int VIDEO = 3;
    private static final int DOC = 4;
    private static final int MESSAGE = 5;
    ArrayList<ChatMessage> chatMessageList;
    Context context;

    public MediaAdapter(Context context, ArrayList<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }

    @Override
    public int getItemCount() {
        return this.chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String fileName = MimeTypeMap.getFileExtensionFromUrl(chatMessageList.get(position).fileName);

        String msg = chatMessageList.get(position).body;
        Log.d("Filename", ">>"+fileName);
        if ((fileName.equalsIgnoreCase("png") || fileName.equalsIgnoreCase("jpg") || fileName.equalsIgnoreCase("jpeg")) && msg.contains(AppConstants.KEY_CONTACT)) {

            return CONTACT;
        } else if (fileName.equalsIgnoreCase("png") || fileName.equalsIgnoreCase("jpg") || fileName.equalsIgnoreCase("jpeg")) {

            return IMAGE;
        } else if (fileName.equalsIgnoreCase("mp4") || fileName.equalsIgnoreCase("3gp")) {

            return VIDEO;
        } else if (fileName.equalsIgnoreCase("pdf")) {

            return DOC;
        } else {

            return MESSAGE;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case IMAGE:
                View v2 = inflater.inflate(R.layout.media, viewGroup, false);
                viewHolder = new MyViewHolder(v2);
                break;
            case VIDEO:
                View v3 = inflater.inflate(R.layout.media, viewGroup, false);
                viewHolder = new MyViewHolder(v3);
                break;
            default:
                View v5 = inflater.inflate(R.layout.media, viewGroup, false);
                viewHolder = new MyViewHolder(v5);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case IMAGE:
                MyViewHolder vh2 = (MyViewHolder) viewHolder;
                configureImageViewHolder(vh2, position);
                break;
            case VIDEO:
                MyViewHolder vh3 = (MyViewHolder) viewHolder;
                configureVideoViewHolder(vh3, position);
                break;
        }
    }

    private void configureImageViewHolder(MyViewHolder vh1, int position) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        if (message != null) {

            vh1.videoView.setVisibility(View.GONE);
            vh1.videoBtn.setVisibility(View.GONE);
            vh1.imageView.setVisibility(View.VISIBLE);

            File file = new File(message.fileName);
            final Uri uri = Uri.fromFile(file);

            vh1.imageView.setImageURI(uri);
            vh1.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            vh1.imageView.setPadding(5,0,5,0);
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
        }
    }

    private void configureVideoViewHolder(MyViewHolder vh1, int position) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        if (message != null) {
            vh1.imageView.setVisibility(View.GONE);
            vh1.videoView.setVisibility(View.VISIBLE);
            vh1.videoBtn.setVisibility(View.VISIBLE);
            File file = new File(message.fileName);
            final Uri uri = Uri.fromFile(file);

            vh1.videoView.setVideoURI(uri);
            vh1.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {

                    mp.seekTo(1);

                }
            });
            vh1.videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri,"video/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                   // videoDialog(uri);
                    return false;
                }
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;
        public ImageView imageView;
        public ImageButton videoBtn;

        public MyViewHolder(View view) {
            super(view);
            videoView = (VideoView) view.findViewById(R.id.video);;
            imageView = (ImageView) view.findViewById(R.id.image);
            videoBtn = (ImageButton) view.findViewById(R.id.video_btn);
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
}
