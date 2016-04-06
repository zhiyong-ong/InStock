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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;

import stock.awesome.instock.misc_classes.Globals;
import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.adapters.KitAdapter;
import stock.awesome.instock.misc_classes.ProductInKit;


public class EditKitActivity extends AppCompatActivity {
    private static HashMap<String, String> idNameMap;
    final Context context = this;
    String kitNameStr;
    KitAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        kitNameStr = Globals.kit.getKitName();
        ListView listView = (ListView) findViewById(R.id.list_view_edit_kit);
        TextView kitName = (TextView) findViewById(R.id.kit_name_view);
        kitName.setText(kitNameStr);
        idNameMap = Globals.idNameMap;

        mAdapter = new KitAdapter(this, Globals.kit, R.layout.item_view_edit_kit);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view
                        .findViewById(R.id.product_in_kit_checkbox);
//                Category category = (Category) imageView.getTag();
//
//                if (category.getChecked() == false) {
//                    imageView.setImageResource(R.drawable.set_check);
//                    listOfItemsToDelete.add(category.getId());
//                    category.setChecked(true);
//                } else {
//                    imageView.setImageResource(R.drawable.set_basecircle);
//                    listOfItemsToDelete.remove((Integer) category.getId());
//                    category.setChecked(false);
//                }
            }
        });
        Button doneButton = (Button) findViewById(R.id.kit_details_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                checkedItems.remove(mAdapter.mKeys[i]);
                            }
                        }

                        Kit toDelete = new Kit(Globals.kit.getKitName());
                        toDelete.setKitMap(checkedItems);

                        DatabaseWriteKit.removeProductsFromKit(toDelete);
                        Toast.makeText(context, "Kit " + Globals.kit.getKitName() + " Edited", Toast.LENGTH_SHORT).show();
                        //go back to the main activity
                        Intent intent = new Intent(EditKitActivity.this, ViewAllKitsActivity.class);
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

            final EditText productIDText = (EditText) dialogView.findViewById(R.id.id_name_autocomplete_text_view);
            final EditText quantityText = (EditText) dialogView.findViewById(R.id.addQuantity);
            String[] idNameArr = Globals.idNameList.toArray(new String[Globals.idNameList.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_dropdown_item_1line, idNameArr);

            final AutoCompleteTextView idNameText = (AutoCompleteTextView)
                    dialogView.findViewById(R.id.id_name_autocomplete_text_view);
            // start auto-completing from 1st char
            idNameText.setThreshold(1);
            idNameText.setAdapter(adapter);
            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Log.v(LOG_TAG, "-------------------TESTING on click: " + productID + "\t" + productID.getText().toString());
                    //Error handling
                    if (idNameText.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "You did not enter an ID", Toast.LENGTH_SHORT).show();
                    } else if (quantityText.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "You did not enter a quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        String productID = idNameText.getText().toString();
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
                            DatabaseReadKit.read(kitNameStr, DatabaseReadKit.KitUseCase.GET_PRODUCT_DETAILS,
                                    EditKitActivity.this, EditKitActivity.class);
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
        return super.onOptionsItemSelected(item);
    }

    public void noSuchProduct() {
        Toast.makeText(this, "No such product exists", Toast.LENGTH_SHORT).show();
    }

}
