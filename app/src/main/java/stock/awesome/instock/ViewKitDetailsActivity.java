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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import stock.awesome.instock.adapters.KitAdapter;
import stock.awesome.instock.misc_classes.GMailSender;
import stock.awesome.instock.misc_classes.Globals;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.ProductInKit;


public class ViewKitDetailsActivity extends AppCompatActivity {

    private static SendEmailTask emailer;
    private final Context context = this;
    private EditText barcodeInput;
    private KitAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_kit_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up the back button here
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barcodeInput = (EditText) findViewById(R.id.barcode_input_picking_kits);
        barcodeInput.setFocusableInTouchMode(true);

        ListView listView = (ListView) findViewById(R.id.list_view_kit_details);
        TextView kitName = (TextView) findViewById(R.id.kit_name_view);
        kitName.setText(Globals.kit.getKitName());

        mAdapter = new KitAdapter(this, Globals.kit, R.layout.item_view_kit_details);
        listView.setAdapter(mAdapter);

        emailer = new SendEmailTask();

        Button doneButton = (Button) findViewById(R.id.kit_details_done_button);
        if(doneButton != null) {
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.popup_confirm_kit_done, null);

                // When the submit button on the dialog is pressed, subtract the quantities of the
                // checked items in the list.
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

                            Product product = new Product(value.getId(), -1 * value.getQuantity());
                            product.setExpiry(correspondingProd.getExpiry());

                            toUpdate.add(product);
                        }

                        DatabaseWriteProduct.updateQuantities(toUpdate);

                        Toast.makeText(context, "Kit " + Globals.kit.getKitName() + " picked", Toast.LENGTH_SHORT).show();
                        //go back to the main activity
                        Intent intent = new Intent(ViewKitDetailsActivity.this, MainPage.class);
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

        final HashMap<String, ProductInKit> kitHashMap = Globals.kit.getKitMap();

        barcodeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                barcodeInput.setFocusableInTouchMode(true);
                barcodeInput.requestFocus();
                Log.e("barcode input", "focused");
            }
        });

//        barcodeInput.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(keyCode == KeyEvent.KEYCODE_ENTER) {
//                    Log.e("barcode input", "text is: " + barcodeInput.getText());
//                    String barcodeID = barcodeInput.getText().toString();
//                    if (kitHashMap.containsKey(barcodeID)) {
//                        int pos = mAdapter.productPositions.get(barcodeID);
//                        mAdapter.setChecked(true, pos);
//                        Log.e("barcode input", "position is " + Integer.toString(pos));
//                    }
//                    barcodeInput.getText().clear();
//                }
//                Log.e("barcode input", "General text is: " + barcodeInput.getText());
//                return true;
//            }
//        });
        barcodeInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((actionId == EditorInfo.IME_ACTION_SEARCH) || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.KEYCODE_ENTER || actionId == 0) {
                    Log.e("barcode input", "text is: " + v.getText());
                    String barcodeID = v.getText().toString();
                    if (kitHashMap.containsKey(barcodeID)) {
                        int pos = mAdapter.productPositions.get(barcodeID);
                        mAdapter.setChecked(pos);
                        Log.e("barcode input", "position is " + Integer.toString(pos));
                    }
                    barcodeInput.getText().clear();
                }
                Log.e("barcode input", "General text is: " + v.getText());

                return true;
            }
        });

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.e("barcode input", "onResume");
//        final HashMap<String, ProductInKit> kitHashMap = Globals.kit.getKitMap();
//
//        barcodeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                barcodeInput.setFocusableInTouchMode(true);
//                barcodeInput.requestFocus();
//                Log.e("barcode input", "focused");
//            }
//        });
//
//        final StringBuilder scannedBuilder = new StringBuilder();
//        barcodeInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                Log.e("barcode input", "onTextChanged");
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//                scannedBuilder.append(s.toString());
//                String barcodeId = scannedBuilder.toString();
//                Log.e("barcode input", "id is " + barcodeId);
//
//                // if ID scanned is in the kit, check the item in the listview
//                if (kitHashMap.containsKey(barcodeId)) {
//                    int pos = mAdapter.productPositions.get(barcodeId);
//                    mAdapter.status.set(pos, true);
//                    mAdapter.checkBox.setChecked(mAdapter.status.get(pos));
//                    Log.e("barcode input", "position is " + Integer.toString(pos));
//
//                    // clear text and string builder
//                    if (!barcodeInput.getText().toString().equals("")) {
//                        barcodeInput.setText("");
//                        scannedBuilder.delete(0, scannedBuilder.length() - 1);
//                    }
//                }
//            }
//        });
//    }

    ///////////////
    // alt code for addTextChangedListener
    //////////////
//    final StringBuilder scannedBuilder = new StringBuilder();
//        final Handler handler = new Handler();
//        barcodeInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                Log.e("barcode input", "onTextChanged");
//            }
//            @Override
//            public void afterTextChanged(final Editable s) {
//                // Wait 1.5 seconds before running code in run()
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        String barcodeId = s.toString();
//                        Log.e("barcode input", "id is " + barcodeId);
//
//                        // if ID scanned is in the kit, check the item in the listview
//                        if (kitHashMap.containsKey(barcodeId)) {
//                            int pos = mAdapter.productPositions.get(barcodeId);
//                            Log.e("barcode input", "position is " + Integer.toString(pos));
//                            mAdapter.status.set(pos, true);
//                            mAdapter.checkBox.setChecked(mAdapter.status.get(pos));
//                        }
//                    }
//                }, 1500);
//
//                // clear text
//                if (!barcodeInput.getText().toString().equals("")) {
//                    barcodeInput.setText("");
//                }
//            }
//        });



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kit_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
//        if (id == R.id.deleteKit) {
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//            LayoutInflater inflater = LayoutInflater.from(this);
//            final View dialogView = inflater.inflate(R.layout.popup_delete_kit, null);
//            final TextView kitName = (TextView) dialogView.findViewById(R.id.deleteIDView);
//            kitName.setText(Globals.kit.getKitName());
//
//            dialogBuilder.setView(dialogView);
//            dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    DatabaseWriteKit.deleteKit(kitName.getText().toString());
//                    Toast.makeText(context, "Deleted Kit: " + kitName.getText().toString(), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(ViewKitDetailsActivity.this, ViewAllKitsActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }
//            });
//            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    //do nothing, go back.
//                }
//            });
//            AlertDialog b = dialogBuilder.create();
//            b.show();
//        }
        return super.onOptionsItemSelected(item);
    }


    public static void sendEmail(ArrayList<Product> lowQtyProds) {
        emailer.execute(lowQtyProds);
    }


    private class SendEmailTask extends AsyncTask<ArrayList<Product>, Void, Void>{

        @Override
        protected Void doInBackground(ArrayList<Product>... params) {
            String subject = "Low stock alert at warehouse";

            String prefix = "Inventory levels for the following items have fallen below the " +
                    "threshold of " + DatabaseReadProduct.THRESHOLD + ": \n";
            String suffix = "\n InStock";

            StringBuilder body = new StringBuilder();

            for (Product product : params[0]) {
                body.append(product.getId()).append(" (").append(product.getName()).append(") ").append(": ").append(product.getQuantity()).append(" units left.").append("\n");
            }

            try {
                GMailSender sender = new GMailSender("noreply.instock@gmail.com", "instockinno");
                sender.sendMail(subject,
                        prefix + body.toString() + suffix,
                        "Instock App",
                        "noreply.instock@gmail.com",
                        "kabirk@live.com");
                sender.sendMail(subject,
                        prefix + body.toString() + suffix,
                        "Instock App",
                        "noreply.instock@gmail.com",
                        "zhiyong.ong@hotmail.com");
                sender.sendMail(subject,
                        prefix + body.toString() + suffix,
                        "Instock App",
                        "noreply.instock@gmail.com",
                        "windrichie@gmail.com");
                sender.sendMail(subject,
                        prefix + body.toString() + suffix,
                        "Instock App",
                        "noreply.instock@gmail.com",
                        "tankehan@live.com");
            } catch (Exception e) {
                Log.e("Mail send failed", e.getMessage(), e);
            }
            return null;
        }
    }

}
