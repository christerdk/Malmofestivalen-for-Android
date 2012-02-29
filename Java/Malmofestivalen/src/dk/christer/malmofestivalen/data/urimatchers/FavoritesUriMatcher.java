package dk.christer.malmofestivalen.data.urimatchers;

import android.content.UriMatcher;
import dk.christer.malmofestivalen.data.FavoritesProvider;

/**
 * Matches favorites uris
 */
public class FavoritesUriMatcher extends UriMatcher {
    public static final int FAVORITES = 0;
    public static final int FAVORITES_ID = 1;

    public FavoritesUriMatcher() {
        super(UriMatcher.NO_MATCH);

        // content://dk.christer.malmofestivalen.favorites
        addURI(FavoritesProvider.AUTHORITY, null, FAVORITES);

        // content://dk.christer.malmofestivalen.favorites/<id>
        addURI(FavoritesProvider.AUTHORITY, "*", FAVORITES_ID);
    }

}
