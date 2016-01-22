package com.eyalengel.photosapp.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.eyalengel.photosapp.Model.AppConstants;
import com.eyalengel.photosapp.Model.PhotosCache;
import com.eyalengel.photosapp.Model.SavedSharedPreferences;
import com.eyalengel.photosapp.Adapters.PhotosListAdapter;
import com.eyalengel.photosapp.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity
{
    private ListView photosListView;
    private Button captureButton;
    private ArrayAdapter<String> myPhotosAdapter;
    private List<String> keys;
    private int photosCount;
    private Bitmap bm;
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        setActionBarTitle();
        getCaptureButtonComponent(); // (the feed button has no use in this activity...)
        setCaptureButtonListener();
        showPhotos(); // Add photos to ListView
    }

    private void setCaptureButtonListener()
    {
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCaptureButtonClick();
            }
        });
    }

    private void handleCaptureButtonClick() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Photo");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, AppConstants.DEVICE_PHOTO_LIBRARY_REQUEST_CODE);
            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, AppConstants.CAMERA_REQUEST_CODE);
            }
        });
        myAlertDialog.show();
    }

    private void getCaptureButtonComponent()
    {
        captureButton = (Button) findViewById(R.id.feed_capture_button);
    }

    private void setActionBarTitle()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Feed");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() { //delete cache files and directory
        super.onDestroy();
        try {
            PhotosCache.trimCache(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //enable capture and feed options also in device top right menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_capture:
                handleCaptureButtonClick();
                return true;
            case R.id.action_feed:
                startActivity(new Intent(this, FeedActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == AppConstants.CAMERA_REQUEST_CODE) {
            bm = (Bitmap) data.getExtras().get("data");
        }
        else if (resultCode == RESULT_OK && requestCode == AppConstants.DEVICE_PHOTO_LIBRARY_REQUEST_CODE) {
            if (data != null) {
                Uri imageUri = data.getData();
                try {
                    bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (bm != null) {
            //getting photos count
            photosCount = SavedSharedPreferences.photo_info.getInt("count", 0) + 1;
            width = SavedSharedPreferences.photo_info.getInt("width", 0);
            height = SavedSharedPreferences.photo_info.getInt("height", 0);

            //getting bitmap original width and height
            int w = bm.getWidth();
            int h = bm.getHeight();

            height = (int) (h * ((float) AppConstants.REQUIRED_PHOTO_WIDTH / w)); //changing photo width to 1080
            width  = AppConstants.REQUIRED_PHOTO_WIDTH;

            resizeBitmapTask resizeBitmapTask = new resizeBitmapTask();
            resizeBitmapTask.execute();
        }
    }

    // Add photos to ListView
    private void showPhotos() {
        photosListView = (ListView) findViewById(R.id.lv_feed_photo);

        keys = new ArrayList<String>();
        for (int i = SavedSharedPreferences.photo_info.getInt("count", 0); i > 0; i--) {
            keys.add("photo_"+i); //create key that will be used to save photo in cache
        }

        // create and set adapter for list view
        myPhotosAdapter = new PhotosListAdapter(this, R.layout.photo_list_item, keys);
        photosListView.setAdapter(myPhotosAdapter);
    }

    // AsyncTask Resizing Bitmap
    private class resizeBitmapTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap  = Bitmap.createScaledBitmap(bm, width, height, true);
            return bitmap;
        }

        @Override protected void onPostExecute(Bitmap result) {
            SavedSharedPreferences.saveBitmapToCahche(result, photosCount);  //save bitmap to cache
            showPhotos();
        }
    }
}

