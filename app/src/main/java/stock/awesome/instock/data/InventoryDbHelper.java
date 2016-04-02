package stock.awesome.instock.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import stock.awesome.instock.data.InventoryContract.ItemEntry;
import stock.awesome.instock.data.InventoryContract.CollectionEntry;
import stock.awesome.instock.data.InventoryContract.CollectionItemJunction;


/**
 * Manages a local database for inventory data.
 */
public class InventoryDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "weather.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold items.  An item consists of the string supplied in the
        // id, the name, the quantity, location, expiry, and optional arguments.
        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME +
                " (" +

                ItemEntry.COLUMN_ITEM_ID + " TEXT PRIMARY KEY, " +

                ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_EXPIRY + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_DESC + "TEXT, " +
                ItemEntry.COLUMN_ITEM_CUSTOM_1 + "TEXT, " +
                ItemEntry.COLUMN_ITEM_CUSTOM_2 + "TEXT, " +
                ItemEntry.COLUMN_ITEM_CUSTOM_3 + "TEXT " +
                " );";

        final String SQL_CREATE_COLLECTION_TABLE = "CREATE TABLE " + CollectionEntry.TABLE_NAME +
                " (" +

                CollectionEntry.COLUMN_COLLEC_ID + " TEXT PRIMARY KEY, " +

                CollectionEntry.COLUMN_COLLEC_CUSTOM_1 + " TEXT, " +
                CollectionEntry.COLUMN_COLLEC_CUSTOM_2 + " TEXT, " +
                CollectionEntry.COLUMN_COLLEC_CUSTOM_3 + " TEXT " +
                " );";

        final String SQL_CREATE_ITEM_COLLECTION_JUNCTION_TABLE = "CREATE TABLE " +
                CollectionItemJunction.TABLE_NAME + " (" +

                " FOREIGN KEY (" + CollectionItemJunction.COLUMN_COLLEC_ID + ") REFERENCES " +
                CollectionEntry.TABLE_NAME + " (" + CollectionEntry.COLUMN_COLLEC_ID + "), " +

                " FOREIGN KEY (" + CollectionItemJunction.COLUMN_ITEM_ID + ") REFERENCES " +
                ItemEntry.TABLE_NAME + " (" + ItemEntry.COLUMN_ITEM_ID + "), " +

                CollectionItemJunction.COLUMN_ITEM_QUANTITY_IN_COLLEC + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_COLLECTION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_COLLECTION_JUNCTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over.
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CollectionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
