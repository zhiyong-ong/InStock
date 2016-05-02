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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;

public class InsertItemActivity extends AppCompatActivity {

    DatabaseWriteProduct writer = new DatabaseWriteProduct();
    Firebase database;

    Calendar myCalendar;
    static EditText expiryDate;
    EditText inputId;
    EditText inputName;
    EditText inputDesc;
    EditText inputQty;
    EditText inputLocation;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up the back button here
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myCalendar = Calendar.getInstance();
        expiryDate = (EditText) findViewById(R.id.expiryEdit);

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
        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(InsertItemActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button submitButton = (Button) findViewById(R.id.submitButton);
        if(submitButton != null) {
            submitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    inputId = (EditText) findViewById(R.id.productEdit);
                    inputName = (EditText) findViewById(R.id.nameEdit);
                    inputDesc = (EditText) findViewById(R.id.descriptionEdit);
                    inputQty = (EditText) findViewById(R.id.quantityEdit);
                    inputLocation = (EditText) findViewById(R.id.locationEdit);

                    if (inputId.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "No ID entered", Toast.LENGTH_SHORT).show();
                    } else if (inputName.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "No product name entered", Toast.LENGTH_SHORT).show();
                    } else if (inputQty.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "No quantity entered", Toast.LENGTH_SHORT).show();
                    } else if (inputLocation.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "No location entered", Toast.LENGTH_SHORT).show();
                    } else if (expiryDate.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "No expiry date entered", Toast.LENGTH_SHORT).show();
                    } else {

                        //update the product with all the relevant details
                        Product inputProd = new Product();
                        String expiry = expiryDate.getEditableText().toString();
                        inputProd.setId(inputId.getText().toString());
                        inputProd.setName(inputName.getText().toString());
                        inputProd.setDesc(inputDesc.getText().toString());
                        inputProd.setQuantity(Integer.valueOf(inputQty.getText().toString()));
                        inputProd.setLocation(inputLocation.getText().toString());
                        inputProd.setExpiry(StringCalendar.toCalendarProper(expiry));

                        DatabaseWriteProduct.write(inputProd);

                        //clear the edit text
                        inputId.getText().clear();
                        inputName.getText().clear();
                        inputDesc.getText().clear();
                        inputQty.getText().clear();
                        inputLocation.getText().clear();
                        expiryDate.getText().clear();

                        Log.d("Submit successful", inputProd.getName() + " " + inputProd.getQuantity() + " "
                                + StringCalendar.toString(inputProd.getExpiry()));
                        Toast.makeText(context, "   New Item Added   ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
        return super.onOptionsItemSelected(item);
    }

    //method for datepicker
    private void updateLabel() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expiryDate.setText(sdf.format(myCalendar.getTime()));
    }
}
