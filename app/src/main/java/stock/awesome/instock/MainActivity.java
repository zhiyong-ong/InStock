package stock.awesome.instock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import stock.awesome.instock.misc_classes.KitStorer;
import stock.awesome.instock.misc_classes.Product;

public class MainActivity extends AppCompatActivity {

    private static Firebase ref;
    private static ArrayList<String> idNameList;

    protected void onCreate(Bundle savedInstanceState) {

        Firebase.setAndroidContext(this);
        DatabaseLauncher.launch();
        ref = DatabaseLauncher.database.child("products");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable dr = getResources().getDrawable(R.drawable.ic_info_black_24dp);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));

        getFirebaseDataArray();
        testSuite();
    }

    private void getFirebaseDataArray() {
        idNameList = new ArrayList<>();
        Query queryRef = ref.orderByKey(); //.startAt(startingChar).endAt(startingChar + "\uf8ff");

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Product prod = child.getValue(Product.class);
                    Log.e("prod details", prod.getId() + " " + prod.getName());
                    idNameList.add(prod.getId());
                    idNameList.add(prod.getName());

                    KitStorer.storeProductIdNameList(idNameList);
                    // TODO go from loading screen to main page
                    // doneLoading();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Product read failed", firebaseError.getMessage());
            }
        });
    }

    public void sendNewItemIntent(View view) {
        Intent intent = new Intent(this, InputItemActivity.class);
        startActivity(intent);
    }

    public void searchtest(View view) {
        Intent intent = new Intent(this, SearchProductsActivity.class);
        startActivity(intent);
    }

    public void sendNewKitIntent(View view) {
        Intent intent = new Intent(this, BuildKitActivity.class);
        startActivity(intent);
    }
    public void sendExistingKitIntent(View view) {
        Intent intent = new Intent(this, ViewAllKitsActivity.class);
        startActivity(intent);
    }

    public void viewAllIntent(View view) {
        Intent intent = new Intent(this, ViewAllStocksActivity.class);
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
