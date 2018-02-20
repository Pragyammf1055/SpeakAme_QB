package com.speakameqb.Activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.speakameqb.R;
import com.speakameqb.utils.ScalingUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SendImageActivity extends AppCompatActivity {
    private static final String TAG = "SendImageActivity";
    //final int CAMERA_CAPTURE = 0;
    final int GALLARY_CAPTURE = 1;
    final int CROP_FROM_CAMERA = 2;
    SendImageActivity instance = null;
    String imagePath = "";
    ImageView imageView;
    EditText text;
    int requestCode;
    Uri picUri;
    ImageView click_image_view, iv_smily;
    FrameLayout fm;
    private String user1, user2;
    private String FriendName;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        instance = this;
        random = new Random();

        imageView = (ImageView) findViewById(R.id.image);
        text = (EditText) findViewById(R.id.text);
        click_image_view = (ImageView) findViewById(R.id.click_image_view);
        iv_smily = (ImageView) findViewById(R.id.iv_smily);
        fm = (FrameLayout) findViewById(R.id.emojicons);


        user1 = getIntent().getStringExtra("user1");
        user2 = getIntent().getStringExtra("user2");
        FriendName = getIntent().getStringExtra("FriendName");
        requestCode = getIntent().getIntExtra("requestCode", 0);
        fm.setVisibility(View.GONE);

        if (getIntent().getAction() == null) {

        } else {
            if (getIntent().getAction().equalsIgnoreCase("gallary")) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLARY_CAPTURE);

              /*  Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType("image*//*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLARY_CAPTURE);
                */

            } else {

            }
        }

        click_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent(SendImageActivity.this, SendImageActivity.class);
                intent.putExtra("uri",picUri);
                startActivityForResult(intent, CROP_FROM_CAMERA);*/
                //   doCrop(picUri);
                performCrop(picUri);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "  SendImageA" + "" + requestCode + " :: " + imagePath + " :: " + "");
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

        iv_smily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.setVisibility(View.VISIBLE);
                View view = SendImageActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, " onActivityResult new : --- " + requestCode + " :: " + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLARY_CAPTURE:
                    Log.d(TAG, " Gallary capture : --- " + " gallary 11 ");
                    picUri = data.getData();
                    //   doCrop(picUri);
                    try {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(picUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        imagePath = filePath;
                        imageView.setImageDrawable(Drawable.createFromPath(filePath));
                        cursor.close();
                    } catch (Exception e) {
                    }
                    break;
                case CROP_FROM_CAMERA:
                    Log.d(TAG, " Crop from camera : --- " + " crop 22 ---- :  " + data.getData() + " Bundal " + data.getExtras());

                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        imageView.setImageBitmap(photo);

//----------------------------------------------------------------------------------//
                        imagePath = String.valueOf(System.currentTimeMillis())
                                + ".jpg";

                        Log.i("TAG", "new selectedImagePath before file "
                                + imagePath);

                        File file = new File(Environment.getExternalStorageDirectory(),
                                imagePath);

                        try {
                            file.createNewFile();
                            FileOutputStream fos = new FileOutputStream(file);
                            photo.compress(Bitmap.CompressFormat.PNG, 95, fos);
                        } catch (IOException e) {
// TODO Auto-generated catch block
                            Toast.makeText(this,
                                    "Sorry, Camera Crashed-Please Report as Crash A.",
                                    Toast.LENGTH_LONG).show();
                        }

                        imagePath = Environment.getExternalStorageDirectory()
                                + "/" + imagePath;
                        Log.i("TAG", "After File Created " + imagePath);

//----------------------------------------------------------------------------------//
                    }

                    break;
            }
        }
    }

    private void doCrop(final Uri uri) {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {

            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {

            intent.setData(uri);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (uri != null) {
                            getContentResolver().delete(uri, null, null);
                            picUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

//----------------------------------------------------------------------------------------//

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

    private void performCrop(Uri picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
          /*  File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);
*/
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
//            cropIntent.putExtra("aspectX", 1);
//            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
//            cropIntent.putExtra("outputX", 200);
//            cropIntent.putExtra("outputY", 200);
//dsffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_FROM_CAMERA);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

    //-------------------------------------------------------------------------------------------------//

    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_selector, options);

            mOptions = options;

            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);

            CropOption item = mOptions.get(position);

            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.iv_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name))
                        .setText(item.title);

                return convertView;
            }

            return null;
        }
    }

}
