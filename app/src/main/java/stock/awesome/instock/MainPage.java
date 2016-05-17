package stock.awesome.instock;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import stock.awesome.instock.adapters.PagerAdapter;
import stock.awesome.instock.fragments.KitFragment;
import stock.awesome.instock.fragments.ProductFragment;
import stock.awesome.instock.fragments.SearchFragment;
import stock.awesome.instock.misc_classes.Globals;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;

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

    Context context = this;
    static Context activity;
    private MaterialSearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpagerMain);
        setupViewPager(mViewPager);
        activity = this;
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
        ref = DatabaseLauncher.database.child("products");

        getFirebaseDataArray();
        testSuite();

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        //Log.e("SUGGESTIONS: ", "Array: " + Arrays.toString(idNameArr));
        Log.e("SUGGESTIONS: ", "Array list: " + idNameList.toString());


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                DatabaseReadProduct.read(query, DatabaseReadProduct.ProdUseCase.DISPLAY_SEARCH_RESULT);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                String[] idNameArr = new String[idNameList.size()];
                idNameArr = idNameList.toArray(idNameArr);
                Log.e("SUGGESTIONS: ", "Array: " + Arrays.toString(idNameArr));
                searchView.setSuggestions(idNameArr);
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

    }

    public static void getSearchItem(final Product product) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View dialogView = inflater.inflate(R.layout.popup_item_edit, null);

        final TextView productIDText = (TextView) dialogView.findViewById(R.id.productView);
        final EditText quantityText = (EditText) dialogView.findViewById(R.id.quantityEdit);
        final EditText nameText = (EditText) dialogView.findViewById(R.id.nameEdit);
        final EditText locationText = (EditText) dialogView.findViewById(R.id.locationEdit);
        final EditText descText = (EditText) dialogView.findViewById(R.id.descriptionEdit);
        final EditText expiryText = (EditText) dialogView.findViewById(R.id.expiryEdit);
        final Calendar myCalendar = Calendar.getInstance();

        //TODO: center the new qty
        Log.e("PRODUCT", "------------------" + product.getId() + "\t" + product.getQuantity());

        //set all the text to the current product
        productIDText.setText(product.getId());
        quantityText.setText(Integer.toString(product.getQuantity()));
        nameText.setText(product.getName());
        locationText.setText(product.getLocation());
        expiryText.setText(StringCalendar.toProperDateString(product.getExpiry()));
        Log.e("EXPIRY", "Expiry 1st: " + expiryText.getEditableText().toString());
        descText.setText(product.getDesc());

        //pop up for datepicker dialog box
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            myCalendar.setTime(df.parse(StringCalendar.toProperDateString(product.getExpiry())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                expiryText.setText(sdf.format(myCalendar.getTime()));
            }

        };
        expiryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(activity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String productID = productIDText.getText().toString().trim();
                String quantityTxt = quantityText.getEditableText().toString().trim();
                String name = nameText.getEditableText().toString().trim();
                String location = locationText.getEditableText().toString().trim();
                String expiry = expiryText.getEditableText().toString();
                String desc = descText.getEditableText().toString().trim();
                Log.e("EXPIRY", "dialog " + expiry);

                //ERROR checks
                if (productID.length() == 0) {
                    Toast.makeText(activity, "No ID entered", Toast.LENGTH_SHORT).show();
                } else if (name.length() == 0) {
                    Toast.makeText(activity, "No product name entered", Toast.LENGTH_SHORT).show();
                } else if (quantityTxt.length() == 0) {
                    Toast.makeText(activity, "No quantity entered", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(quantityTxt) < 0) {
                    Toast.makeText(activity, "Invalid quantity value. Must be larger than or equals to 0.", Toast.LENGTH_SHORT).show();
                } else if (location.length() == 0) {
                    Toast.makeText(activity, "No location entered", Toast.LENGTH_SHORT).show();
                } else if (expiry.length() == 0) {
                    Toast.makeText(activity, "No expiry date entered", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("EXPIRY", "REACHED HERE!!");
                    int quantity = Integer.parseInt(quantityText.getEditableText().toString().trim());
                    Product updateProd = new Product(productID, name, desc, location, quantity,
                            StringCalendar.toCalendarProper(expiry));

                    Log.e("Some thing", updateProd.getId() + "\t" + Integer.toString(updateProd.getQuantity()) +
                            "\t" + StringCalendar.toProperDateString(updateProd.getExpiry()));

                    DatabaseWriteProduct.updateProduct(updateProd);
                    Toast.makeText(activity, "ID: " + productID + " updated", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //show the dialog to confirm to delete item
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = LayoutInflater.from(activity);
                final View dialogView = inflater.inflate(R.layout.popup_delete_item, null);

                final TextView deleteProductID = (TextView) dialogView.findViewById(R.id.deleteIDView);
                deleteProductID.setText(product.getId());

                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseWriteProduct.deleteProduct(product.getId());
                        Toast.makeText(activity, "Item " + product.getId() + " deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                AlertDialog b = dialogBuilder.create();

                b.show();
            }
        });

        dialogBuilder.setView(dialogView);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public static void noSuchProduct() {
        Toast.makeText(activity, "No such product exists.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.info:
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
                return true;
            case R.id.search_main:
                searchView.showSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    public void sendUpdateItemIntent(View view) {
        Intent intent = new Intent(this, UpdateItemActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.search_main);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
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

    //method for datepicker
    /*
    private static void updateLabel() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expiryText.setText(sdf.format(myCalendar.getTime()));
    }
    */
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
