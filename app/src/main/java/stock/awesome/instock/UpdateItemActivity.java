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
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import stock.awesome.instock.adapters.Autocompletify;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;

public class UpdateItemActivity extends AppCompatActivity {

    static Context activity;
    Firebase database;
    static Calendar myCalendar;
    static EditText expiryText = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activity = this;
        //get root view here
        final View aView = findViewById(android.R.id.content);
        final AutoCompleteTextView productIDText = Autocompletify.makeAutocomplete(this,
                aView, R.id.productSearchEdit);
        productIDText.requestFocus();
        Button searchButton = (Button)aView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String productID = productIDText.getText().toString().trim();
                Log.e("prod id", productID);

                if (productID.trim().length() == 0) {
                    Toast.makeText(activity, "No Product ID entered", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e("PRODUCT", "------------------- product id: " + productID);
                    DatabaseReadProduct.read(productID, DatabaseReadProduct.ProdUseCase.DISPLAY_SEARCH);
                }
            }
        });
    }

    public static void noSuchProduct() {
        Toast.makeText(activity, "No such product exists", Toast.LENGTH_SHORT).show();
    }
    public static void SearchItem(final Product product) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View dialogView = inflater.inflate(R.layout.search_box, null);

        final TextView productIDText = (TextView) dialogView.findViewById(R.id.productView);
        final TextView quantityText = (TextView) dialogView.findViewById(R.id.qtyView);
        expiryText = (EditText) dialogView.findViewById(R.id.expiryView);
        final TextView nameText = (TextView) dialogView.findViewById(R.id.nameView);
        final EditText newQty = (EditText) dialogView.findViewById(R.id.newUpdateQty);
        //TODO: center the new qty
        Log.e("PRODUCT", "------------------" + product.getId() + "\t" + product.getQuantity());

        nameText.setText(product.getName());
        productIDText.setText(product.getId());
        quantityText.setText(Integer.toString(product.getQuantity()));
        expiryText.setText(StringCalendar.toProperDateString(product.getExpiry()));

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        myCalendar = Calendar.getInstance();
        try {
            myCalendar.setTime(df.parse(StringCalendar.toProperDateString(product.getExpiry())));
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
                new DatePickerDialog(activity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String productID = product.getId();
                int quantity = Integer.parseInt(newQty.getEditableText().toString());
                String expiry = expiryText.getEditableText().toString();
                Product qtyExpProduct = new Product(productID, quantity, StringCalendar.toCalendarProper(expiry));
                Log.e("PRODUCT", "--------------------- qty: " + newQty.getText().toString());
                DatabaseWriteProduct.updateQuantityExpiry(qtyExpProduct);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        dialogBuilder.setView(dialogView);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private static void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expiryText.setText(sdf.format(myCalendar.getTime()));
    }
}
