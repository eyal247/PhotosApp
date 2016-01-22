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
import android.widget.Button;

import com.eyalengel.photosapp.Model.AppConstants;
import com.eyalengel.photosapp.Model.PhotosCache;
import com.eyalengel.photosapp.Model.SavedSharedPreferences;
import com.eyalengel.photosapp.R;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener{


    private Button captureButton;
    private Button feedButton;
    private int photosCount; // Number of uploaded photos
    private Bitmap bm; // Picked photo bitmap from camera/gallery

    // Width and Height of Device
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        setActionBarTitle();
        getUIComponents();
        setButtonsListeners();
    }

    private void setButtonsListeners()
    {
        captureButton.setOnClickListener(this);
        feedButton.setOnClickListener(this);
    }

    private void getUIComponents()
    {
        captureButton = (Button) findViewById(R.id.home_capture_button);
        feedButton = (Button) findViewById(R.id.home_feed_button);
    }

    private void setActionBarTitle()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Home");
    }

    @Override
    protected void onDestroy() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.home_capture_button:
                handleCaptureButtonClick();
                break;
            case R.id.home_feed_button:
                startActivity(new Intent(UserHomeActivity.this, FeedActivity.class));
                break;
            default:
                break;
        }
    }

    private void handleCaptureButtonClick()
    {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == AppConstants.CAMERA_REQUEST_CODE) { // from Camera
            bm = (Bitmap) data.getExtras().get("data");
        }
        else if (resultCode == RESULT_OK && requestCode == AppConstants.DEVICE_PHOTO_LIBRARY_REQUEST_CODE) { //from Gallery
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
            photosCount = SavedSharedPreferences.photo_info.getInt("count", 0) + 1; // get photos count and add 1
            width = SavedSharedPreferences.photo_info.getInt("width", 0); // get Device Width
            height = SavedSharedPreferences.photo_info.getInt("height", 0); // get Device Height

            int w = bm.getWidth(); // get Bitmap Width
            int h = bm.getHeight(); // get Bitmap Height

            width  = AppConstants.REQUIRED_PHOTO_WIDTH; // change Bitmap Width
            height = (int) (h * ((float) AppConstants.REQUIRED_PHOTO_WIDTH / w)); // change Bitmap Height

            resizeBitmapTask resizeBitmapTask = new resizeBitmapTask(); //resize bitmap in AsyncTask (different Thread)
            resizeBitmapTask.execute();
        }

    }

    // AsyncTask Resizing Bitmap
    private class resizeBitmapTask extends AsyncTask<Void, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap  = Bitmap.createScaledBitmap(bm, width, height, true); //resizing bitmap to new width and height
            return bitmap;
        }

        @Override protected void onPostExecute(Bitmap result) {
            SavedSharedPreferences.saveBitmapToCahche(result, photosCount); //save bitmap to cache
            startActivity(new Intent(UserHomeActivity.this, FeedActivity.class)); //go to Feed activity
        }
    }
}

