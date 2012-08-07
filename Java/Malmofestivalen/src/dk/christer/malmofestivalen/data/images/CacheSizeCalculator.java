package dk.christer.malmofestivalen.data.images;

import android.content.Context;
import android.os.Build;

public abstract class CacheSizeCalculator {
    public static int getCacheSize(final Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.DONUT) {
            return new DonutCacheSizeCalculator().getCacheSizeInternal(context);
        } else {
            return new AncientCacheSizeCalculator().getCacheSizeInternal(context);
        }
    }

    protected abstract int getCacheSizeInternal(Context context);
}
