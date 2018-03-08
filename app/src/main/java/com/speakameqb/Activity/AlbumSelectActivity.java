package com.speakameqb.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.speakameqb.Adapter.CustomAlbumSelectAdapter;
import com.speakameqb.Beans.Album;
import com.speakameqb.Beans.Image;
import com.speakameqb.R;
import com.speakameqb.utils.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;


public class AlbumSelectActivity extends AlbumActivity {
    private final static String TAG = "AlbumSelectedActivity";
    private final String[] projection = new String[]{
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    private ArrayList<Album> albums;
    private TextView errorDisplay;
    private ProgressBar progressBar;
    private GridView gridView;
    private CustomAlbumSelectAdapter adapter;
    private ActionBar actionBar;
    private ContentObserver observer;
    private Handler handler;
    private Thread thread;
    //private LinearLayout linearLayout;
    //private ViewPager viewPager;
    //private ScrollViewPagerAdapter scrollViewPagerAdapter;
    private ImageView imageViewCircle;
    private RecyclerView recyclerView;
    private HorizontalAdapter horizontalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_select);
        setView(findViewById(R.id.layout_album_select));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Select photo album");
        }

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        AppConstants.limit = intent.getIntExtra(AppConstants.INTENT_EXTRA_LIMIT, AppConstants.DEFAULT_LIMIT);

        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);

        //linearLayout = findViewById(R.id.horizontalScrollView_thumbnails);
        //viewPager = findViewById(R.id.pager);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        progressBar = (ProgressBar) findViewById(R.id.progress_bar_album_select);
        gridView = (GridView) findViewById(R.id.grid_view_album_select);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ImageSelectActivity.class);
                intent.putExtra(AppConstants.INTENT_EXTRA_ALBUM, albums.get(position).name);
                startActivityForResult(intent, AppConstants.REQUEST_CODE);
                //startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AppConstants.PERMISSION_GRANTED: {
                        Log.v(TAG, "  case permission granted");
                        loadAlbums();
                        break;
                    }

                    case AppConstants.FETCH_STARTED: {
                        progressBar.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.INVISIBLE);
                        Log.v(TAG, " case Fetch started ");
                        break;
                    }

                    case AppConstants.FETCH_COMPLETED: {
                        Log.v(TAG, " case Fetch Completed ");
                        if (adapter == null) {
                            Log.v(TAG, "case Fetch Completed Adapter is null");
                            adapter = new CustomAlbumSelectAdapter(getApplicationContext(), albums);
                            gridView.setAdapter(adapter);

                            progressBar.setVisibility(View.INVISIBLE);
                            gridView.setVisibility(View.VISIBLE);
                            orientationBasedUI(getResources().getConfiguration().orientation);

                        } else {
                            Log.v(TAG, "case fetch completed notifying adpter");
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    }

                    case AppConstants.ERROR: {
                        Log.v(TAG, "case error ");
                        progressBar.setVisibility(View.INVISIBLE);
                        errorDisplay.setVisibility(View.VISIBLE);
                        break;
                    }

                    default: {
                        Log.v(TAG, " case default ");
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                Log.v(TAG, " ContentObserver ");
                Log.v(TAG, "On Change");
                loadAlbums();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);

        checkPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.v(TAG, "On stop () ");
        stopThread();
        Log.v(TAG, "Thread Shopping ");

        getContentResolver().unregisterContentObserver(observer);
        observer = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.v(TAG, " On Destroy() ");
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(null);
        }
        albums = null;
        if (adapter != null) {
            adapter.releaseResources();
        }
        gridView.setOnItemClickListener(null);
        Log.v(TAG, " actionbar null, adapter releaseResources, clickLisner null ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, " onconfigureChanged ");
        orientationBasedUI(newConfig.orientation);
    }

    private void orientationBasedUI(int orientation) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        if (adapter != null) {
            int size = orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels / 2 : metrics.widthPixels / 4;
            adapter.setLayoutParams(size);
        }
        gridView.setNumColumns(orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Image> imageArrayList = new ArrayList<>();
        Log.v(TAG, " On Activity Result ");

        if (requestCode == AppConstants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Log.v(TAG, "On Activity Result : -- " + data);
            imageArrayList.clear();
            Log.v(TAG, "Image Array List Clear " + imageArrayList);
            imageArrayList = data.getParcelableArrayListExtra(AppConstants.INTENT_EXTRA_IMAGES); //intent.getParcelableArrayListExtra(AppConstants.INTENT_EXTRA_IMAGES);
            Log.v(TAG, "Image Array List Received " + imageArrayList);

            for (int i = 0; i < imageArrayList.size(); i++) {
                String path = imageArrayList.get(i).path;
                Log.v(TAG, "Recevied Path of image from arraylist index : " + i + " " + path);
            }

            sendIntent(imageArrayList);

//            if (linearLayout != null){
//                linearLayout.removeAllViews();
//            }
//            scrollViewPagerAdapter = new ScrollViewPagerAdapter(this, imageArrayList);
//            viewPager.setAdapter(scrollViewPagerAdapter);
//            viewPager.setOffscreenPageLimit(10); // how many images to load into memory

            // horizontalAdapter = new HorizontalAdapter(this, imageArrayList);

            //LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            //recyclerView.setLayoutManager(horizontalLayoutManager);
            //recyclerView.setAdapter(horizontalAdapter);
            // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            //horizontalAdapter = new HorizontalAdapter(this,imageArrayList);
            // recyclerView.setAdapter(horizontalAdapter);
        }
    }

    private void sendIntent(ArrayList<Image> arrayList) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AppConstants.INTENT_EXTRA_IMAGES, arrayList);
        intent.putExtras(bundle);
        Log.v(TAG, "getSelected............." + arrayList);
        Log.v(TAG, " send Intent");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            default: {
                return false;
            }
        }
    }

    private void loadAlbums() {
        startThread(new AlbumLoaderRunnable());
    }

    private void startThread(Runnable runnable) {
        stopThread();
        thread = new Thread(runnable);
        thread.start();
    }

    private void stopThread() {
        if (thread == null || !thread.isAlive()) {
            return;
        }

        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(int what) {
        if (handler == null) {
            return;
        }

        Message message = handler.obtainMessage();
        message.what = what;
        message.sendToTarget();
    }

    @Override
    protected void permissionGranted() {
        Message message = handler.obtainMessage();
        message.what = AppConstants.PERMISSION_GRANTED;
        message.sendToTarget();
    }

    @Override
    protected void hideViews() {
        progressBar.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.INVISIBLE);
    }

    private class AlbumLoaderRunnable implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            if (adapter == null) {
                sendMessage(AppConstants.FETCH_STARTED);
            }

            Cursor cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                            null, null, MediaStore.Images.Media.DATE_ADDED);
            if (cursor == null) {
                sendMessage(AppConstants.ERROR);
                return;
            }

            ArrayList<Album> temp = new ArrayList<>(cursor.getCount());
            HashSet<Long> albumSet = new HashSet<>();
            File file;
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long albumId = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String album = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String image = cursor.getString(cursor.getColumnIndex(projection[2]));

                    if (!albumSet.contains(albumId)) {
                        /*
                        It may happen that some image file paths are still present in cache,
                        though image file does not exist. These last as long as media
                        scanner is not run again. To avoid get such image file paths, check
                        if image file exists.
                         */
                        file = new File(image);
                        if (file.exists()) {
                            temp.add(new Album(album, image));
                            albumSet.add(albumId);
                        }
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            if (albums == null) {
                albums = new ArrayList<>();
            }
            albums.clear();
            albums.addAll(temp);

            sendMessage(AppConstants.FETCH_COMPLETED);
        }
    }


    // ------------------------------   ADAPTER FOR IMAGE AND SCROLLVIEW  --------------------------------------

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        ArrayList<Image> horizontalList;
        Context context;


        public HorizontalAdapter(Context context, ArrayList<Image> horizontalList) {
            this.context = context;
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_gallery_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            File imgFile = new File(horizontalList.get(position).path);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imageView.setImageBitmap(myBitmap);
            } else {
                Log.v(TAG, "selected File not exists");
                Toast.makeText(context, "File Not Exists", Toast.LENGTH_SHORT).show();
            }
            //holder.imageView.setImageResource(horizontalList.get(position).path);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                    String list = horizontalList.get(position).path;
                    Toast.makeText(AlbumSelectActivity.this, list, Toast.LENGTH_SHORT).show();
                    Glide.with(context)
                            .load(list)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                    //imageViewCircle.setImage(ImageSource.bitmap(bitmap));
                                    //RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),
                                    //      Bitmap.createScaledBitmap(bitmap, 50, 50, false));
                                    imageViewCircle.setImageBitmap(bitmap);
                                }
                            });
                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;

            public MyViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.image_view);
            }
        }
    }
}


//    class ScrollViewPagerAdapter extends PagerAdapter {
//
//        Context _context;
//        LayoutInflater _inflater;
//        private ArrayList<Image> receive_images;
//
//        public ScrollViewPagerAdapter(Context context, ArrayList<Image> images) {
//            this._context = context;
//            this._inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            this.receive_images = images;
//        }
//
//        @Override
//        public int getCount() {
//            return receive_images.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == ((LinearLayout) object);
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, final int position) {
//            View itemView = _inflater.inflate(R.layout.pager_gallery_item, container, false);
//            container.addView(itemView);
//
//            // Get the border size to show around each image
//            int borderSize = linearLayout.getPaddingTop();
//
//            // Get the size of the actual thumbnail image
//            int thumbnailSize = ((FrameLayout.LayoutParams) viewPager.getLayoutParams()).bottomMargin - (borderSize*2);
//
//            // Set the thumbnail layout parameters. Adjust as required
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
//            params.setMargins(0, 0, borderSize, 0);
//
//            final ImageView thumbView = new ImageView(_context);
//            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            thumbView.setLayoutParams(params);
//           // linearLayout.addView(thumbView);
//            Log.d(TAG, "instantiateItem: " + linearLayout);
//            Log.v(TAG, "Adapter Array Size " + receive_images.size());
//
//            thumbView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Thumbnail clicked");
//                    viewPager.setCurrentItem(position);
//                }
//            });
//            linearLayout.addView(thumbView);
//
//            final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) itemView.findViewById(R.id.image);
//
//            // Asynchronously load the image and set the thumbnail and pager view
//            Glide.with(_context)
//                    .load(receive_images.get(position).path)
//                    .asBitmap()
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                            imageView.setImage(ImageSource.bitmap(bitmap));
//                            thumbView.setImageBitmap(bitmap);
//                            thumbView.offsetLeftAndRight(1);
//                        }
//                    });
//
//            //container.addView(itemView);
//            return itemView;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((LinearLayout) object);
//        }
//    }
//}
