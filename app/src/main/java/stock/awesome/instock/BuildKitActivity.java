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

import java.util.ArrayList;

import stock.awesome.instock.misc_classes.BuildKitAdapter;
import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;

public class BuildKitActivity extends AppCompatActivity {
    private static BuildKitAdapter listAdapter;
    private static ArrayList<Product> newProduct = new ArrayList<Product>();
    private static ListView mainListView;

    final Context context = this;
    Firebase database;
    String LOG_TAG = Product.class.getSimpleName();
    static int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_kit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainListView = (ListView) findViewById(R.id.listView);
        listAdapter = new BuildKitAdapter(this, R.layout.item_view_build_kit, newProduct);
        mainListView.setAdapter(listAdapter);

        //edit items that are already inside the kit
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //check when items in the listview are clicked
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.item_edit_build_kit, null);

                final TextView productIDText = (TextView) dialogView.findViewById(R.id.productView);
                final EditText quantityText = (EditText) dialogView.findViewById(R.id.quantityEdit);
                final TextView nameText = (TextView) dialogView.findViewById(R.id.nameEdit);
                final TextView locationText = (TextView) dialogView.findViewById(R.id.locationEdit);
                final TextView descText = (TextView) dialogView.findViewById(R.id.descriptionEdit);
                final TextView expiryText = (TextView) dialogView.findViewById(R.id.expiryEdit);

                //set all the text to the current product
                final Product selectedProduct = listAdapter.getItem(position);
                productIDText.setText(selectedProduct.getId());
                quantityText.setText(Integer.toString(selectedProduct.getQuantity()));
                nameText.setText(selectedProduct.getName());
                locationText.setText(selectedProduct.getLocation());
                expiryText.setText(StringCalendar.toProperDateString(selectedProduct.getExpiry()));
                descText.setText(selectedProduct.getDesc());
                //set the cursor to the end of quantity edit text
                quantityText.setSelection(quantityText.getText().length());

                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //update quantity here and notify the listadapter
                        int quantity = Integer.parseInt(quantityText.getEditableText().toString());
                        selectedProduct.setQuantity(quantity);
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
    public static void displayItem(final Product product) {
        product.setQuantity(quantity);
        newProduct.add(product);
        //don't need this.
        //mainListView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

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
                    String productID = productIDText.getText().toString();
                    quantity = Integer.parseInt(quantityText.getText().toString());
                    //add to the list.
                    //communicate with the database here
                    //DatabaseReadProduct reader = new DatabaseReadProduct(database, DatabaseReadProduct.ProdUseCase.BUILD_KIT);
                    //reader.execute(productIDText.getText().toString());
                    DatabaseReadProduct.read(productID, DatabaseReadProduct.ProdUseCase.DISPLAY_PRODUCT);

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
    // Handle exceptions thrown by DatabaseReadProduct here
    public static Product displayProduct(Product result, Exception e) {
        Log.w("result info:", result.getId() + " " + result.getName() + " " +
                result.getQuantity() + " " + StringCalendar.toString(result.getExpiry()));
        return result;
    }*/

    //intent when clicking on the button for save kit
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

                    final String kitName = kitNameText.getEditableText().toString();
                    Toast.makeText(context, "Kit Name: " + kitName, Toast.LENGTH_LONG).show();

                    //show the dialog to confirm to save kit
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View dialogView = inflater.inflate(R.layout.save_kit, null);

                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //save kit to database here!
                            Kit newKit = new Kit(kitName);
                            for(Product cur : newProduct) {
                                newKit.addProduct(cur, cur.getQuantity());
                            }
                            DatabaseWriteKit.write(newKit);

                            newProduct.clear();
                            listAdapter.notifyDataSetChanged();
                            Toast.makeText(context, "Kit " + kitName + " saved", Toast.LENGTH_LONG).show();
                            //go back to the main activity
//                            Intent intent = new Intent(BuildKitActivity.this, MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
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




