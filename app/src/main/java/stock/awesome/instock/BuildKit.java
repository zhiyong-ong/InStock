package stock.awesome.instock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class BuildKit extends AppCompatActivity {
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> newProduct = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_kit);
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

    public void sendNewKitItem(View view) {
        showChangeLangDialog();
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_add_item, null);
        dialogBuilder.setView(dialogView);

        final EditText productID = (EditText) dialogView.findViewById(R.id.productID);
        final EditText quantity = (EditText) dialogView.findViewById(R.id.addQuantity);
        //dialogBuilder.setTitle("Input New Item");
        //dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                newProduct.add(productID.getText().toString() + " " + quantity.getText().toString());
                populateListView();
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

    public void populateListView() {
        ListView mainListView = (ListView) findViewById( R.id.listView );

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newProduct);

        mainListView.setAdapter(listAdapter);

    }
}
