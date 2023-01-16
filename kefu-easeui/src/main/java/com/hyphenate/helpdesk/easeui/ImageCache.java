package com.hyphenate.helpdesk.easeui;


import android.graphics.Bitmap;

import androidx.collection.LruCache;

public class ImageCache {

    private ImageCache() {
        // use 1/8 of available heap size
        cache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 8)) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    private static ImageCache imageCache = null;

    public static synchronized ImageCache getInstance() {
        if (imageCache == null) {
            imageCache = new ImageCache();
        }
        return imageCache;

    }
    private LruCache<String, Bitmap> cache = null;

    /**
     * put bitmap to image cache
     * @param key
     * @param value
     * @return  the puts bitmap
     */
    public Bitmap put(String key, Bitmap value){
        return cache.put(key, value);
    }

    /**
     * return the bitmap
     * @param key
     * @return
     */
    public Bitmap get(String key){
        return cache.get(key);
    }
}