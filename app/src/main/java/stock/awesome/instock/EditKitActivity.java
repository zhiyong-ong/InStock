package stock.awesome.instock;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;

import stock.awesome.instock.adapters.Autocompletify;
import stock.awesome.instock.adapters.KitAdapter;
import stock.awesome.instock.misc_classes.Globals;
import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.ProductInKit;


public class EditKitActivity extends AppCompatActivity {
    private static HashMap<String, String> idNameMap;
    final Context context = this;
    String kitNameStr;
    Kit curKit;
    KitAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up the back button here
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        kitNameStr = Globals.kit.getKitName();
        curKit = Globals.kit;

        ListView listView = (ListView) findViewById(R.id.list_view_edit_kit);
        TextView kitName = (TextView) findViewById(R.id.kit_name_view);
        kitName.setText(kitNameStr);
        idNameMap = Globals.idNameMap;

        mAdapter = new KitAdapter(this, curKit, R.layout.item_view_edit_kit);
        listView.setAdapter(mAdapter);

        Button doneButton = (Button) findViewById(R.id.kit_details_done_button);
        if (doneButton != null) {
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Toast.makeText(context, "Kit " + Globals.kit.getKitName() + " Edited", Toast.LENGTH_SHORT).show();
                    //go back to the main activity
                    Intent intent = new Intent(EditKitActivity.this, ViewEditKitActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_kit, menu);

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

        if(id == R.id.addToKit) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.popup_add_item, null);

            dialogBuilder.setView(dialogView);

            final EditText quantityText = (EditText) dialogView.findViewById(R.id.addQuantity);
            final AutoCompleteTextView idNameText = Autocompletify.makeAutocomplete(this, dialogView, R.id.id_name_autocomplete_text_view);
//            idNameText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
//                }
//            });
            idNameText.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                @Override
                public void onDismiss() {
                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(idNameText.getApplicationWindowToken(), 0);
                }
            });
            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Log.v(LOG_TAG, "-------------------TESTING on click: " + productID + "\t" + productID.getText().toString());
                    //Error handling
                    if (idNameText.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "You did not enter an ID", Toast.LENGTH_SHORT).show();
                    } else if (quantityText.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "You did not enter a quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        String productID = idNameText.getText().toString().trim();
                        productID = idNameMap.get(productID);
                        // if the id/name entered does not exist in the list
                        if (productID == null) {
                            noSuchProduct();
                        } else {
                            Log.e("ID selected", productID);
                            int quantity = Integer.parseInt(quantityText.getText().toString());
                            //add to the list.
                            //communicate with the database here
                            //DatabaseReadProduct.read(productID, DatabaseReadProduct.ProdUseCase.DISPLAY_PRODUCT);
                            DatabaseWriteKit.addProductsToKit(kitNameStr, productID, quantity);
                            mAdapter.notifyDataSetChanged();
                        }
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

        if(id == R.id.deleteFromKit) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogView = inflater.inflate(R.layout.popup_confirm_edited_kit, null);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // copy hash map from adapter
                    LinkedHashMap<String, ProductInKit> checkedItems = mAdapter.mKitMap;

                    // if item is unchecked, remove from map
                    for (int i = 0; i < mAdapter.status.size(); i++) {
                        if (!mAdapter.status.get(i)) {
                            //left the items to be deleted from the kit
                            checkedItems.remove(mAdapter.mKeys[i]);
                        }
                    }

                    Kit toDelete = new Kit(Globals.kit.getKitName());
                    toDelete.setKitMap(checkedItems);

                    DatabaseWriteKit.removeProductsFromKit(toDelete);
                    Toast.makeText(context, "Item(s) Deleted", Toast.LENGTH_SHORT).show();

                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            AlertDialog b = dialogBuilder.create();

            b.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void noSuchProduct() {
        Toast.makeText(this, "No such product exists", Toast.LENGTH_SHORT).show();
    }

//    public static void getProduct(Product newProd) {
//        mAdapter.updateKit(curKit, newProd, qty);
//    }

}
