package stock.awesome.instock;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import stock.awesome.instock.misc_classes.GMailSender;
import stock.awesome.instock.misc_classes.KitAdapter;
import stock.awesome.instock.misc_classes.KitStorer;
import stock.awesome.instock.misc_classes.Product;


public class ViewKitDetailsActivity extends AppCompatActivity {

    private static SendEmailTask emailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_kit_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.list_view_kit_details);

        KitAdapter mAdapter = new KitAdapter(this, KitStorer.kit);
        listView.setAdapter(mAdapter);

        emailer = new SendEmailTask();

        // ON BUTTON PRESS
//        // copy hash map from adapter
//        HashMap<String, ProductInKit> checkedItems = mAdapter.mKitMap;
//
//        // if item is unchecked, remove from map
//        for (int i=0; i<mAdapter.status.size(); i++) {
//            if (!mAdapter.status.get(i)) {
//                checkedItems.remove(mAdapter.mKeys[i]);
//            }
//        }
//
//        // create array of products to update from checkedItems map
//        ArrayList<Product> toUpdate = new ArrayList<>();
//
//        // write to array of products
//        for (HashMap.Entry<String, ProductInKit> entry : checkedItems.entrySet()) {
//
//            String key = entry.getKey();
//            ProductInKit value = entry.getValue();
//            Product correspondingProd = KitAdapter.mProductMap.get(key);
//
//            Product product = new Product(value.getId(), value.getQuantity());
//            product.setExpiry(correspondingProd.getExpiry());
//
//            toUpdate.add(product);
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    public static void sendEmail(ArrayList<Product> lowQtyProds) {
        emailer.execute(lowQtyProds);
    }


    public class SendEmailTask extends AsyncTask<ArrayList<Product>, Void, Void>{

        @Override
        protected Void doInBackground(ArrayList<Product>... params) {
            String subject = "Low stock alert at warehouse";
            String prefix = "Inventory levels for the following items have fallen below the threshold of 100: \n";

            StringBuilder body = new StringBuilder();

            for (Product product : params[0]) {
                body.append(product.getId()).append(" has quantity ").append(product.getQuantity()).append("\n");
            }

            try {
                GMailSender sender = new GMailSender("tembusu.college.events@gmail.com", "teas_checker1");
                sender.sendMail(subject,
                        prefix + body.toString(),
                        "tembusu.college.events@gmail.com",
                        "kabirk@live.com");

                Log.e("sendMail", "happened");

            } catch (Exception e) {
                Log.e("Mail send failed", e.getMessage(), e);
            }
            return null;
        }
    }

}
