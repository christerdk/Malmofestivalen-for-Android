package dk.christer.malmofestivalen.data.images;

import android.content.Context;

public class AncientCacheSizeCalculator extends CacheSizeCalculator {
    @Override
    protected int getCacheSizeInternal(Context context) {
        // We stick to 4 MB of cache for these old devices to reduce OOM risk
        return 1024 * 1024 * 4;
    }
}
