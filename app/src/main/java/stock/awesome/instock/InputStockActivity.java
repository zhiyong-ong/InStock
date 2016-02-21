package stock.awesome.instock;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InputStockActivity extends AppCompatActivity {

    Calendar myCalendar;
    EditText expiryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                new DatePickerDialog(InputStockActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Product inputProd = new Product();
                onSubmitPress(inputProd);
                Log.w("Submit successful: ", inputProd.getName() + " " + inputProd.getQuantity());
            }
        });
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expiryDate.setText(sdf.format(myCalendar.getTime()));
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


    // Call on press of the submit button for this activity
    public void onSubmitPress(Product inputProd) {

        EditText inputId = (EditText) findViewById(R.id.productEdit);
        inputProd.setId(inputId.getText().toString());

        EditText inputName = (EditText) findViewById(R.id.nameEdit);
        inputProd.setName(inputName.getText().toString());

        EditText inputDesc = (EditText) findViewById(R.id.descriptionEdit);
        inputProd.setDesc(inputDesc.getText().toString());

        EditText inputQty = (EditText) findViewById(R.id.quantityEdit);
        inputProd.setQuantity(Integer.valueOf(inputQty.getText().toString()));

        EditText inputLocation = (EditText) findViewById(R.id.locationEdit);
        inputProd.setLocation(inputLocation.getText().toString());

        // expiry not implemented. easier to parse date input with a picker
        // see http://developer.android.com/guide/topics/ui/controls/pickers.html

    }

}
