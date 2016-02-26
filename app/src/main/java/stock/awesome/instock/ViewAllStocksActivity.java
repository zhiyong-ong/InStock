package stock.awesome.instock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import java.util.Calendar;

public class ViewAllStocksActivity extends AppCompatActivity {

    Firebase database;
    FirebaseListAdapter<Product> mAdapter;
    Context context = this;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_stocks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView itemView = (ListView) findViewById(R.id.viewAllListView);
        //set up connection with the firebase database.
        Firebase.setAndroidContext(this);

        database = DatabaseLauncher.database.child("products");
        //database.child("products");
        //database = new Firebase("https://scorching-inferno-2190.firebaseio.com/products");
        mAdapter = new FirebaseListAdapter<Product>(this, Product.class, R.layout.item_view, database) {
            @Override
            protected void populateView(View view, Product product, int position) {
                ((TextView)view.findViewById(R.id.idText)).setText(product.getId());
                ((TextView)view.findViewById(R.id.qtyText)).setText(Integer.toString(product.getQuantity()));
                ((TextView)view.findViewById(R.id.nameText)).setText(product.getName());
                ((TextView)view.findViewById(R.id.locText)).setText(product.getLocation());
                ((TextView)view.findViewById(R.id.expdateText)).setText(StringCalendar.toProperDateString(product.getExpiry()));
            }
        };
        itemView.setAdapter(mAdapter);

        //edit items in database
        itemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //check when items in the listview are clicked
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.item_edit, null);

                final EditText productIDText = (EditText) dialogView.findViewById(R.id.productEdit);
                final EditText quantityText = (EditText) dialogView.findViewById(R.id.quantityEdit);
                final EditText nameText = (EditText) dialogView.findViewById(R.id.nameEdit);
                final EditText locationText = (EditText) dialogView.findViewById(R.id.locationEdit);
                final EditText expiryText = (EditText) dialogView.findViewById(R.id.expiryEdit);
                final EditText descText = (EditText) dialogView.findViewById(R.id.descriptionEdit);

                //set all the text to the current product
                Product selectedProduct = mAdapter.getItem(position);
                productIDText.setText(selectedProduct.getId());
                quantityText.setText(Integer.toString(selectedProduct.getQuantity()));
                nameText.setText(selectedProduct.getName());
                locationText.setText(selectedProduct.getLocation());
                expiryText.setText(StringCalendar.toProperDateString(selectedProduct.getExpiry()));
                descText.setText(selectedProduct.getDesc());

                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String productID = productIDText.getEditableText().toString();
                        int quantity = Integer.parseInt(quantityText.getEditableText().toString());
                        String name = nameText.getEditableText().toString();
                        String location = locationText.getEditableText().toString();
                        String expiry = expiryText.getEditableText().toString();
                        String desc = descText.getEditableText().toString();

                        Product updateProd = new Product(productID, name, desc, location, quantity, StringCalendar.toCalendar(expiry));

                        DatabaseUpdateProduct update = new DatabaseUpdateProduct(database);
                        update.updateProduct(updateProd);
                        //newProduct.set(position, new Product(productID, quantity));
                        Toast.makeText(context, "ID: " + productID + ", QTY: " + quantity, Toast.LENGTH_LONG).show();
                        mAdapter.notifyDataSetChanged();

                    }
                });

                dialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //newProduct.remove(position);
                        //listAdapter.notifyDataSetChanged();
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



}
