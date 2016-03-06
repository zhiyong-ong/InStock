package stock.awesome.instock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.ProductInKit;


public class ViewKitDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_kit_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // receive intent with kitName clicked in prev activity
        Intent intent = getIntent();
        String kitName = intent.getStringExtra(ViewAllKitsActivity.KIT_NAME);

        // look in kit specified
        Firebase ref = DatabaseLauncher.database.child(kitName).child("kitMap");

        // inflate listview list_view_kit_details (in activity_view_kit_details.xml)
        ListView itemView = (ListView) findViewById(R.id.list_view_kit_details);

        // to change the way each item in the list looks, replace android.R.layout.simple_list_item_1
        // in the following code with a custom linear layout. The xml file should have only one textview
        FirebaseListAdapter<ProductInKit> mAdapter = new FirebaseListAdapter<ProductInKit>
                (this, ProductInKit.class, android.R.layout.two_line_list_item, ref) {
            @Override
            protected void populateView(View view, ProductInKit pink, int position) {

                Log.e("pink id", pink.getId());

                ((TextView) view.findViewById(android.R.id.text1)).setText(pink.getId());
                ((TextView) view.findViewById(android.R.id.text2)).setText(pink.getQuantity());
            }
        };
        itemView.setAdapter(mAdapter);
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
}
