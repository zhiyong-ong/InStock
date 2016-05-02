package stock.awesome.instock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import stock.awesome.instock.adapters.PagerAdapter;
import stock.awesome.instock.fragments.KitFragment;
import stock.awesome.instock.fragments.ProductFragment;
import stock.awesome.instock.fragments.SearchFragment;
import stock.awesome.instock.misc_classes.Globals;
import stock.awesome.instock.misc_classes.Product;

public class MainPage extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static Firebase ref;
    private static ArrayList<String> idNameList;
    private static HashMap<String, String> idNameMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpagerMain);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
        ref = DatabaseLauncher.database.child("products");

        getFirebaseDataArray();
        testSuite();
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment(), "Search");
        adapter.addFragment(new KitFragment(), "Kits");
        adapter.addFragment(new ProductFragment(), "Items");
        viewPager.setAdapter(adapter);
    }

    public void sendNewKitIntent(View view) {
        Intent intent = new Intent(this, BuildKitActivity.class);
        startActivity(intent);
    }

    public void sendExistingKitIntent(View view) {
        Intent intent = new Intent(this, ViewAllKitsActivity.class);
        startActivity(intent);
    }

    public void sendEditKitIntent(View view) {
        Intent intent = new Intent(this, ViewEditKitActivity.class);
        startActivity(intent);
    }

    public void sendNewItemIntent(View view) {
        Intent intent = new Intent(this, InsertItemActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.info) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialogView = inflater.inflate(R.layout.info, null);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do nothing, go back.
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }
        return super.onOptionsItemSelected(item);
    }


    protected void onResume() {
        super.onResume();
        getFirebaseDataArray();
    }
    private void getFirebaseDataArray() {
        idNameList = new ArrayList<>();
        idNameMap = new HashMap<>();

        Query queryRef = ref.orderByKey(); //.startAt(startingChar).endAt(startingChar + "\uf8ff");

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String prodId;
                String prodName;

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Product prod = child.getValue(Product.class);
                    prodId = prod.getId();
                    prodName = prod.getName();

                    // Prevents duplicates that might arise through read errors
                    if (!idNameMap.containsKey(prodId)) {
                        idNameList.add(prodId);
                        // map id to id
                        idNameMap.put(prodId, prodId);

                        // only add name to map if there was a corresponding id read
                        if (!idNameMap.containsKey(prodName)) {
                            idNameList.add(prodName);
                            // map name to id
                            idNameMap.put(prodName, prodId);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Product read failed", firebaseError.getMessage());
            }
        });

        // Shallow copy of list
        Globals.idNameList = idNameList;
        Globals.idNameMap = idNameMap;
    }
    private void testSuite() {
        // TESTING
//        Product testProd = new Product("zzz", "name", "desc", "location", 5, new GregorianCalendar(2018, 11, 18));
//        // product write testing
//        DatabaseWriteProduct.write(testProd);
//        testProd.setQuantity(80);
//        DatabaseWriteProduct.updateProduct(testProd);
//
//        DatabaseWriteProduct.updateQuantityExpiry(new Product("282in", -1020, testProd.getExpiry()));
//        DatabaseWriteProduct.deleteProduct("refactor");
//
//        Product[] toUpdate = {new Product("282in", 6000), new Product("71ue", -6000), new Product("8272br", -8)};
//        DatabaseWriteProduct.updateQuantities(toUpdate);
//
//        // product read testing
//        try {
//            DatabaseReadProduct.read("282in", DatabaseReadProduct.ProdUseCase.DEBUG);
//        }
//        catch (ProductNotFoundException e){
//            Log.e("", e.getMessage());
//        }
//
//        // product update testing
//        testProd.setLocation("changed location");
//        DatabaseUpdateProduct updater = new DatabaseUpdateProduct(database);
//        updater.updateProduct(testProd);
//
//        // kit write testing
//        Kit testKit = new Kit("test_kit_1");
//        testKit.addProduct(testProd, 6);
//
//        ProductInKit pink = new ProductInKit("71ue", 6);
//        testKit.addProduct(pink);
//
//        testKit.addProduct("282in", 44);
//
//        Log.e("testKit", testKit.getKitMap().toString());
//
//        DatabaseWriteKit kitWriter = new DatabaseWriteKit();
//        kitWriter.write(testKit);
//
//        // Kit update testing
//        DatabaseWriteKit.addProductsToKit("test_kit_1", "AAAAAAAA", 200);
//
//        DatabaseReadKit.read("test_kit_1", DatabaseReadKit.KitUseCase.GET_PRODUCT_DETAILS);
//
//        // Kit delete test
//        DatabaseWriteKit.deleteKit("test_kit_4");
//
//        // get array of kits test
//        DatabaseReadKit.getArrayOfKits();
    }
}
