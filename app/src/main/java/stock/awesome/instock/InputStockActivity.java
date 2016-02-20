package stock.awesome.instock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class InputStockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
    public void onSubmitPress() {
        Product inputProd = new Product();

        EditText inputId = (EditText) findViewById(R.id.productEdit);
        inputProd.setId(inputId.getText().toString());

        EditText inputName = (EditText) findViewById(R.id.nameEdit);
        inputProd.setName(inputName.getText().toString());

        EditText inputDesc = (EditText) findViewById(R.id.descriptionEdit);
        inputProd.setDesc(inputDesc.getText().toString());

        // test
        EditText inputQty = (EditText) findViewById(R.id.quantityEdit);
        inputProd.setQuantity(Integer.valueOf(inputQty.getText().toString()));

        EditText inputLocation = (EditText) findViewById(R.id.locationEdit);
        inputProd.setLocation(inputLocation.getText().toString());

        // expiry not implemented. easier to parse date input with a picker
        // see http://developer.android.com/guide/topics/ui/controls/pickers.html

    }
}
