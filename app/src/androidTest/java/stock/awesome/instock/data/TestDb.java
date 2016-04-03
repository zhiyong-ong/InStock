package stock.awesome.instock.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;


public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(InventoryDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(InventoryContract.ItemEntry.TABLE_NAME);
        tableNameHashSet.add(InventoryContract.CollectionEntry.TABLE_NAME);
        tableNameHashSet.add(InventoryContract.CollectionItemJunction.TABLE_NAME);

        mContext.deleteDatabase(InventoryDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new InventoryDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + InventoryContract.ItemEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_ITEM_ID);
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_LOCATION);
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_NAME);
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_QUANTITY);
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_DESC);
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_EXPIRY);
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_ITEM_CUSTOM_1);
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_ITEM_CUSTOM_2);
        locationColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_ITEM_CUSTOM_3);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }
}
