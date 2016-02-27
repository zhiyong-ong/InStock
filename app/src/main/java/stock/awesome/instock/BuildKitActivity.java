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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import stock.awesome.instock.Misc_classes.Product;

public class BuildKitActivity extends AppCompatActivity {
    private ArrayAdapter<Product> listAdapter;
    private ArrayList<Product> newProduct = new ArrayList<Product>();
    private ListView mainListView;

    final Context context = this;
    Firebase database;
    String LOG_TAG = Product.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_kit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up connection with the firebase database.
        Firebase.setAndroidContext(this);
        database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");

        mainListView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_list_item_1, newProduct);
        mainListView.setAdapter(listAdapter);

        //edit items that are already inside the kit
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //check when items in the listview are clicked
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.popup_add_item, null);

                final EditText productIDText = (EditText) dialogView.findViewById(R.id.productID);
                final EditText quantityText = (EditText) dialogView.findViewById(R.id.addQuantity);

                productIDText.setText(newProduct.get(position).getId());
                quantityText.setText(newProduct.get(position).getQuantity());

                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String productID = productIDText.getEditableText().toString();
                        int quantity = Integer.parseInt(quantityText.getEditableText().toString());
                        Toast.makeText(context, "ID: " + productID + ", QTY: " + quantity, Toast.LENGTH_LONG).show();

                        newProduct.set(position, new Product(productID, quantity));
                        listAdapter.notifyDataSetChanged();
                    }
                });

                dialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        newProduct.remove(position);
                        listAdapter.notifyDataSetChanged();
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
    //add new item to kit
    public void sendNewKitItem(View view) {
        showChangeLangDialog();
    }

    public void showChangeLangDialog() {
        //adding a new item to the kit
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_add_item, null);

        dialogBuilder.setView(dialogView);

        final EditText productIDText = (EditText) dialogView.findViewById(R.id.productID);
        final EditText quantityText = (EditText) dialogView.findViewById(R.id.addQuantity);

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Log.v(LOG_TAG, "-------------------TESTING on click: " + productID + "\t" + productID.getText().toString());
                //Error handling
                if(productIDText.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "You did not enter an ID", Toast.LENGTH_SHORT).show();
                }
                else if(quantityText.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "You did not enter a quantity", Toast.LENGTH_SHORT).show();
                }
                else {
                    //add to the list.
                    //communicate with the database here
                    //DatabaseReadProduct reader = new DatabaseReadProduct(database, DatabaseReadProduct.ProdUseCase.BUILD_KIT);
                    //reader.execute(productIDText.getText().toString());

                    Product productTuple = new Product(productIDText.getText().toString(), Integer.parseInt(quantityText.getText().toString()));
                    newProduct.add(productTuple);
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    /*
    public static Product displayProduct(Product result) {
        Log.w("result info:", result.getId() + " " + result.getName() + " " +
                result.getQuantity() + " " + StringCalendar.toString(result.getExpiry()));
        return result;
    }*/
    public void sendSaveKit(View view) {

        //display error message for empty kit
        if (newProduct.isEmpty()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogView = inflater.inflate(R.layout.error_no_item, null);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do nothing, go back.
                }
            });

            AlertDialog b = dialogBuilder.create();
            b.show();
            }

        else {
            //key in the name of the kit here
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogView = inflater.inflate(R.layout.save_kit_name, null);

            //name of the kit!
            final EditText kitNameText = (EditText) dialogView.findViewById(R.id.kitName);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String kitName = kitNameText.getEditableText().toString();
                    Toast.makeText(context, "Kit Name: " + kitName, Toast.LENGTH_LONG).show();

                    //show the dialog to confirm to save kit
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View dialogView = inflater.inflate(R.layout.save_kit, null);

                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //do nothing, go back.
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //do nothing here.
                        }
                    });
                    AlertDialog b = dialogBuilder.create();

                    b.show();
                }
            });

            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do nothing here.
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }
    }
}




