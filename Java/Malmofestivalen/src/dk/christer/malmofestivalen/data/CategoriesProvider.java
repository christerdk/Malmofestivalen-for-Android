package dk.christer.malmofestivalen.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class CategoriesProvider extends FestivalProvider {
	
    public static final String AUTHORITY = "dk.christer.malmofestivalen.categories";

    public static final String DATABASE_TABLE_CATEGORIES = "categories";

    private static final String ITEMLIST_TYPE = "vnd.android.cursor.dir/vnd.dk.christer.malmofestivalen.category";

    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://" + AUTHORITY);

    public static final Uri CONTENT_URI_CATEGORIES_FOR_ACT_ID = Uri.withAppendedPath(CONTENT_URI_CATEGORIES, "forAct");
    public static final Uri CONTENT_URI_CATEGORY_FOR_CATEGORY_ID = Uri.withAppendedPath(CONTENT_URI_CATEGORIES, "byId");

    public static final int URI_MATCH_CATEGORIES = 0;
    public static final int URI_MATCH_CATEGORIES_BY_ACT_ID = 1;
    public static final int URI_MATCH_CATEGORIES_BY_CATEGORY_ID = 2;
    
    //public static final String CATEGORY_KEY_ACTID = "ActId";
    public static final String CATEGORY_KEY_CATEGORYID = "CategoryId";
    public static final String CATEGORY_KEY_TITLE = "Title";
    
    public CategoriesProvider() 
    {
    	super(AUTHORITY, DATABASE_TABLE_CATEGORIES);
    	
    	//content://dk.christer.malmofestivalen.categories/
        addUri(null, URI_MATCH_CATEGORIES);
        
        // content://dk.christer.malmofestivalen.categories/forAct/<Act id (text)>
        addUri(CONTENT_URI_CATEGORIES_FOR_ACT_ID.getPath() + "/*", URI_MATCH_CATEGORIES_BY_ACT_ID);

        // content://dk.christer.malmofestivalen.categories/byId/<Category id (text)>
        addUri(CONTENT_URI_CATEGORY_FOR_CATEGORY_ID.getPath() + "/*", URI_MATCH_CATEGORIES_BY_CATEGORY_ID);
}
    
    @Override
    protected Cursor query(int uriMatch, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatch) {
        	case URI_MATCH_CATEGORIES:
            return getCategories();
            case URI_MATCH_CATEGORIES_BY_ACT_ID:
                return getCategoriesForActId(uri.getLastPathSegment());
            case URI_MATCH_CATEGORIES_BY_CATEGORY_ID:
            	return getCategoryForCategoryId(uri.getLastPathSegment());
            default:
                return null;
        }
    }

    
    @Override
    protected String getType(final int match) {
        switch (match) {
            case URI_MATCH_CATEGORIES_BY_ACT_ID:
                return ITEMLIST_TYPE;
            default:
                return null;
        }
    }
    
    private Cursor getCategoriesForActId(final String actId) {
        return dbQuery(
        		"SELECT categories.CategoryId, categories.Title " +
        		"FROM categories " +
        		"LEFT JOIN actstocategories ON actstocategories.CategoryId = categories.CategoryId " +
        		"WHERE actstocategories.ActId = ?",
                new String[]{
                		actId
                });
    }
	
    
    private Cursor getCategories() {
        return  dbQuery(
                null,
                null,
                null,
                CATEGORY_KEY_TITLE + " asc",
                null);
    }
    
    private Cursor getCategoryForCategoryId(String categoryId) {
    	return dbQuery(
                null,
                CATEGORY_KEY_CATEGORYID + "=?",
                new String[]{
                		categoryId
                },
                null,
                null);
    }
    
}
