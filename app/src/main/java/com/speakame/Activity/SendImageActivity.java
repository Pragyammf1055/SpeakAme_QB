package com.speakame.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.speakame.R;
import com.speakame.utils.ScalingUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class SendImageActivity extends AppCompatActivity {
    //final int CAMERA_CAPTURE = 0;
    final int GALLARY_CAPTURE = 1;
    SendImageActivity instance = null;
    String imagePath = "";
    ImageView imageView;
    EditText text;
    int requestCode;
    private String user1, user2;
    private String FriendName;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = this;
        random = new Random();

        imageView = (ImageView) findViewById(R.id.image);
        text = (EditText) findViewById(R.id.text);

        user1 = getIntent().getStringExtra("user1");
        user2 = getIntent().getStringExtra("user2");
        FriendName = getIntent().getStringExtra("FriendName");
        requestCode = getIntent().getIntExtra("requestCode", 0);

        if (getIntent().getAction().equalsIgnoreCase("gallary")) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType("image/*");
            //intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLARY_CAPTURE);
        } else {

        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imagePath.equals("")) {
                    Toast.makeText(SendImageActivity.this, "Please select image", Toast.LENGTH_LONG).show();

                } else {
                    String msg = text.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("requestCode", requestCode);
                    intent.putExtra("file", imagePath);
                    intent.putExtra("msg", msg);
                    setResult(-1, intent);


                    finish();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri picUri;
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLARY_CAPTURE) {
                picUri = data.getData();

                try {
                    //We get the file path from the media info returned by the content resolver
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(picUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    String filePath = cursor.getString(columnIndex);

                    //imagePath = decodeFile(filePath, 200, 200);
                    imagePath = filePath;

                    imageView.setImageDrawable(Drawable.createFromPath(filePath));
                    cursor.close();
                } catch (Exception e) {
                }


                //performCrop(picUri);
            }

        }


    }


    private String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode files
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale files
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/SpeakaMe/image/send");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());

            //String s = "MS_" + timeStamp + ".jpg";
            String s = "MS_temp.jpg";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            // scaledBitmap.recycle();
            imageView.setImageBitmap(scaledBitmap);
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

}
