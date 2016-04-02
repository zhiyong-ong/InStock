package stock.awesome.instock.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.GregorianCalendar;


/**
 * Defines table and column names for the inventory database.
 */
public class InventoryContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "stock.awesome.instock";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://stock.awesome.instock/items/ is a valid path for
    // looking at weather data. content://stock.awesome.instock/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_ITEMS = "items";
    public static final String PATH_COLLECTIONS = "collections";
    public static final String PATH_COLLECTION_ITEMS = "collection_item";

    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // Table name
        public static final String TABLE_NAME = "items";

        // Primary key
        public static final String COLUMN_ITEM_ID = "id";

        // The following strings store product details (as returned by Firebase)
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_DESC = "description";
        // Stored as a string of the format yyyy-MM-dd
        public static final String COLUMN_EXPIRY = "expiry";
        // Allow the user to declare custom item properties
        public static final String COLUMN_ITEM_CUSTOM_1 = "item_custom1";
        public static final String COLUMN_ITEM_CUSTOM_2 = "item_custom2";
        public static final String COLUMN_ITEM_CUSTOM_3 = "item_custom3";

        public static Uri buildItemsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    /* Inner class that defines the table contents of the collections table */
    public static final class CollectionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLECTIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_COLLECTIONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_COLLECTIONS;

        public static final String TABLE_NAME = "collections";

        // Primary key
        public static final String COLUMN_COLLEC_ID = "collec_id";

        // Quantity of the item in this collection
        public static final String COLUMN_COLLEC_QUANTITY = "collec_quantity";
        // Allow the user to declare custom collection properties
        public static final String COLUMN_COLLEC_CUSTOM_1 = "collec_custom1";
        public static final String COLUMN_COLLEC_CUSTOM_2 = "collec_custom2";
        public static final String COLUMN_COLLEC_CUSTOM_3 = "collec_custom3";

        public static Uri buildCollectionsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        /*
            Student: This is the buildWeatherLocation function you filled in.
         */
        public static Uri buildCollection(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
    }


    // Junction table to define many-to-many r/s between items and collections.
    // Each collection contains many items, and each item can be in many collections.
    public static final class CollectionItem implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLECTION_ITEMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_COLLECTION_ITEMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_COLLECTION_ITEMS;

        public static final String TABLE_NAME = "collection_items";

        // Primary key is composite of two below
        public static final String COLUMN_COLLEC_ID = CollectionEntry.COLUMN_COLLEC_ID;
        public static final String COLUMN_ITEM_ID = ItemEntry.COLUMN_ITEM_ID;

        // Quanity of items in collection
        public static final String COLUMN_ITEM_QUANTITY = "item_quantity_in_collec";
    }
}
