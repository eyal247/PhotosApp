package com.eyalengel.photosapp.Model;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

/**
 * Created by EyalEngel on 20/01/16.
 */
public class SavedSharedPreferences
{

    public static SharedPreferences photo_info = null;
    public static SharedPreferences alreadyLoggedIn = null;


    public static void setSharedPreferenceCount (SharedPreferences setting) {
        photo_info = setting;
    }

    public static void setSharedPreferenceLogin(SharedPreferences setting) {
        alreadyLoggedIn = setting;
    }

    public static void saveBitmapToCahche(Bitmap bm, int count) {
        String path = "photo_"+ count;
        SavedSharedPreferences.photo_info.edit().putInt("count", count).commit();

        PhotosCache.getInstance().getLruCache().put(path, bm);
    }

    public static Bitmap retrieveBitmapFromCache(String key) {
        Bitmap bitmap = (Bitmap)PhotosCache.getInstance().getLruCache().get(key);
        return bitmap;
    }
}
