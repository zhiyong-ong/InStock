package stock.awesome.instock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.Firebase;

import java.util.GregorianCalendar;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.ProductInKit;
import stock.awesome.instock.misc_classes.TestKit;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        Firebase.setAndroidContext(this);
        DatabaseLauncher.launch();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TESTING
        Product testProd = new Product("AAAAAAAA", "name", "desc", "location", 5, new GregorianCalendar(2018, 11, 18));

        // product write testing
//        DatabaseWriteProduct.write(testProd);
//        testProd.setQuantity(80);
//        DatabaseWriteProduct.updateProduct(testProd);
//
//        DatabaseWriteProduct.updateQuantityExpiry(new Product("282in", -1020, testProd.getExpiry()));
//        DatabaseWriteProduct.deleteProduct("refactor");


        // product read testing

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
//        Kit testKit = new Kit("yolo");
//        testKit.addProduct(testProd);
//
//        ProductInKit pink = new ProductInKit("71ue", 6);
//        testKit.addProduct(pink);
//
//        testKit.addProduct("ab", 44);
//
//        Log.e("testKit", testKit.getKit().toString());
//
//        DatabaseWriteKit kitWriter = new DatabaseWriteKit();
//        kitWriter.write(testKit);
//
//        // kit read testing
//        DatabaseReadKit.read("yolo", DatabaseReadKit.KitUseCase.DEBUG);

    }

    public void sendNewItemIntent(View view) {
        Intent intent = new Intent(this, InputItemActivity.class);
        startActivity(intent);
    }

    public void sendNewKitIntent(View view) {
        Intent intent = new Intent(this, BuildKitActivity.class);
        startActivity(intent);
    }

    public void sendExistingKitIntent(View view) {
        Intent intent = new Intent(this, ExistingKitActivity.class);
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

}
