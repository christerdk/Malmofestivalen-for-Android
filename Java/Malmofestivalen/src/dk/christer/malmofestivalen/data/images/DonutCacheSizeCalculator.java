package dk.christer.malmofestivalen.data.images;

import android.app.ActivityManager;
import android.content.Context;

public class DonutCacheSizeCalculator extends CacheSizeCalculator {
    @Override
    protected int getCacheSizeInternal(final Context context) {
        final int memClass = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        return 1024 * 1024 * memClass / 8;
    }
}
