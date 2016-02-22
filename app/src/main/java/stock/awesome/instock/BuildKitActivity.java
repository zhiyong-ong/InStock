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

import java.util.ArrayList;

public class BuildKitActivity extends AppCompatActivity {
    private ArrayAdapter<Product> listAdapter;
    private ArrayList<Product> newProduct = new ArrayList<Product>();
    private ListView mainListView;
    final Context context = this;


    String LOG_TAG = Product.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_kit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainListView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_list_item_1, newProduct);
        mainListView.setAdapter(listAdapter);

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

    public void sendNewKitItem(View view) {
        showChangeLangDialog();
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_add_item, null);

        dialogBuilder.setView(dialogView);

        final EditText productID = (EditText) dialogView.findViewById(R.id.productID);
        final EditText quantity = (EditText) dialogView.findViewById(R.id.addQuantity);
        //dialogBuilder.setTitle("Input New Item");
        //dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Log.v(LOG_TAG, "-------------------TESTING on click: " + productID + "\t" + productID.getText().toString());

                Product productTuple = new Product(productID.getText().toString(), Integer.parseInt(quantity.getText().toString()));
                newProduct.add(productTuple);
                listAdapter.notifyDataSetChanged();
                //do something with edt.getText().toString();
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

    public void sendSaveKit(View view) {
        if (newProduct.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle("Error in Saving")
                    .setMessage("The kit is empty.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogView = inflater.inflate(R.layout.save_kit_name, null);

            final EditText kitNameText = (EditText) dialogView.findViewById(R.id.kitName);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String kitName = kitNameText.getEditableText().toString();
                    Toast.makeText(context, "Kit Name: " + kitName, Toast.LENGTH_LONG).show();

                    //show the dialog to confirm to save kit
                    new AlertDialog.Builder(context)
                            .setTitle("Save New Kit")
                            .setMessage("Are you sure you want to save this kit?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //continue with saving the kit
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
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




