package dk.christer.malmofestivalen.data.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
* A simple image cache based on LruCache
*/
public class ImageCache extends LruCache<String, Bitmap> {
    public static ImageCache newInstance(final Context context) {
        return new ImageCache(CacheSizeCalculator.getCacheSize(context));
    }

    public ImageCache(final int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
