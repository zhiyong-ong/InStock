package stock.awesome.instock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import stock.awesome.instock.misc_classes.GMailSender;
import stock.awesome.instock.misc_classes.KitAdapter;
import stock.awesome.instock.misc_classes.KitStorer;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.ProductInKit;


public class ViewKitDetailsActivity extends AppCompatActivity {

    private static SendEmailTask emailer;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_kit_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.list_view_kit_details);

        final KitAdapter mAdapter = new KitAdapter(this, KitStorer.kit);
        listView.setAdapter(mAdapter);

        emailer = new SendEmailTask();

        Button doneButton = (Button) findViewById(R.id.kit_details_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.popup_confirm_kit_done, null);

                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // copy hash map from adapter
                        HashMap<String, ProductInKit> checkedItems = mAdapter.mKitMap;

                        // if item is unchecked, remove from map
                        for (int i = 0; i < mAdapter.status.size(); i++) {
                            if (!mAdapter.status.get(i)) {
                                checkedItems.remove(mAdapter.mKeys[i]);
                            }
                        }

                        // create array of products to update from checkedItems map
                        ArrayList<Product> toUpdate = new ArrayList<>();

                        // write to array of products
                        for (HashMap.Entry<String, ProductInKit> entry : checkedItems.entrySet()) {

                            String key = entry.getKey();
                            ProductInKit value = entry.getValue();
                            Product correspondingProd = KitAdapter.mProductMap.get(key);

                            Product product = new Product(value.getId(), -1*value.getQuantity());
                            product.setExpiry(correspondingProd.getExpiry());

                            toUpdate.add(product);
                        }

                        DatabaseWriteProduct.updateQuantities(toUpdate);

                        //Toast.makeText(context, "Kit " +  + " saved", Toast.LENGTH_SHORT).show();
                        //go back to the main activity
                        Intent intent = new Intent(ViewKitDetailsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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
            String suffix = "\n InStock";

            StringBuilder body = new StringBuilder();

            for (Product product : params[0]) {
                body.append(product.getId()).append(" has quantity ").append(product.getQuantity()).append("\n");
            }

            try {
                GMailSender sender = new GMailSender("tembusu.college.events@gmail.com", "teas_checker1");
                //check sender name here.
                sender.sendMail(subject, prefix + body.toString() + suffix, "InStock",
                        "tembusu.college.events@gmail.com",
                        "kabirk@live.com");
            } catch (Exception e) {
                Log.e("Mail send failed", e.getMessage(), e);
            }
            return null;
        }
    }

}
