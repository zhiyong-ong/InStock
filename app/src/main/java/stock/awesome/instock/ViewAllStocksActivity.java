package stock.awesome.instock;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import stock.awesome.instock.exceptions.ProductNotFoundException;

public class ViewAllStocksActivity extends AppCompatActivity {

    Firebase database;
    FirebaseListAdapter<Product> mAdapter;
    Context context = this;
    Calendar myCalendar;
    static EditText expiryText;
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

                final TextView productIDText = (TextView) dialogView.findViewById(R.id.productView);
                final EditText quantityText = (EditText) dialogView.findViewById(R.id.quantityEdit);
                final EditText nameText = (EditText) dialogView.findViewById(R.id.nameEdit);
                final EditText locationText = (EditText) dialogView.findViewById(R.id.locationEdit);
                final EditText descText = (EditText) dialogView.findViewById(R.id.descriptionEdit);
                expiryText = (EditText) dialogView.findViewById(R.id.expiryEdit);


                //set all the text to the current product
                Product selectedProduct = mAdapter.getItem(position);
                productIDText.setText(selectedProduct.getId());
                quantityText.setText(Integer.toString(selectedProduct.getQuantity()));
                nameText.setText(selectedProduct.getName());
                locationText.setText(selectedProduct.getLocation());
                expiryText.setText(StringCalendar.toProperDateString(selectedProduct.getExpiry()));
                descText.setText(selectedProduct.getDesc());

                //pop up for datepicker dialog box
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                myCalendar = Calendar.getInstance();
                try {
                    myCalendar.setTime(df.parse(StringCalendar.toProperDateString(selectedProduct.getExpiry())));
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
                        updateLabel();
                    }

                };
                expiryText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(ViewAllStocksActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String productID = productIDText.getText().toString();
                        int quantity = Integer.parseInt(quantityText.getEditableText().toString());
                        String name = nameText.getEditableText().toString();
                        String location = locationText.getEditableText().toString();
                        String expiry = expiryText.getEditableText().toString();
                        String desc = descText.getEditableText().toString();

                        Product updateProd = new Product(productID, name, desc, location, quantity,
                                StringCalendar.toCalendarProper(expiry));

                        Log.e("TESTING", "\t" + expiry + "\t" + updateProd.getExpiry());

                        //TODO: check for change in listView after editing. Not sure why it doesn't change as of now
                        Log.e("Some thing", updateProd.getId() + "\t" + Integer.toString(updateProd.getQuantity()) +
                                "\t" + StringCalendar.toProperDateString(updateProd.getExpiry()));
                        try {
                            DatabaseWriteProduct.updateProduct(updateProd);
                        }
                        catch (ProductNotFoundException e) {
                            // TODO display error msg
                        }
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

    //method for datepicker
    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expiryText.setText(sdf.format(myCalendar.getTime()));
    }


}
