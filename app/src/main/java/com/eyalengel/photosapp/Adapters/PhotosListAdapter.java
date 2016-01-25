package com.eyalengel.photosapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.eyalengel.photosapp.Model.SavedSharedPreferences;
import com.eyalengel.photosapp.R;

import java.util.List;

/**
 * Created by EyalEngel on 20/01/16.
 */
public class PhotosListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> photosKeys;
    private int layoutResourceID;

    public PhotosListAdapter(Context context, int layoutResourceID, List<String> photosKeys) {
        super(context, layoutResourceID, photosKeys);

        this.context = context;
        this.photosKeys = photosKeys;
        this.layoutResourceID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layoutResourceID, null);
        }

        String key = getItem(position); //get current photo key in cache
        Bitmap bm = SavedSharedPreferences.retrieveBitmapFromCache(key); //retrieve current photo from cache
        if (bm != null) {
            setBitmapInsideImageView(bm, convertView);
        }

        return convertView;
    }

    private void setBitmapInsideImageView(Bitmap bm, View convertView)
    {
        ImageView photoIV = (ImageView)convertView.findViewById(R.id.photo_image_view);

        photoIV.getLayoutParams().width = bm.getWidth();
        photoIV.getLayoutParams().height = bm.getHeight();
        photoIV.requestLayout(); // change ImageView size according to above width and height of current bitmap
        photoIV.setImageBitmap(bm); //attach photo to ImageView
    }
}