package com.eyalengel.photosapp.Model;

import android.content.Context;
import android.support.v4.util.LruCache;
import java.io.File;

/**
 * Created by EyalEngel on 20/01/16.
 */
public class PhotosCache {
    private static PhotosCache photosCache;
    private LruCache<Object, Object> lruCache;

    private PhotosCache() {
        lruCache = new LruCache<Object, Object>(1024);
    }

    public static PhotosCache getInstance() {
        if (photosCache == null) {
            photosCache = new PhotosCache();
        }

        return photosCache;
    }

    public LruCache<Object, Object> getLruCache() {
        return lruCache;
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir(); // get path to cache directory used by the app
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // delete the cache files and directory
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list(); // get files list in directory
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));  // delete i-th item
                if (!success) {
                    return false;
                }
            }
        }

        // delete empty directory
        return dir.delete();
    }
}
