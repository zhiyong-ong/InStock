package stock.awesome.instock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

public class ViewAllStocksActivity extends AppCompatActivity {

    Firebase database;
    FirebaseListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_stocks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView itemView = (ListView) findViewById(R.id.viewAllListView);
        //set up connection with the firebase database.
        Firebase.setAndroidContext(this);
        database = new Firebase("https://scorching-inferno-2190.firebaseio.com/products");
        mAdapter = new FirebaseListAdapter<Product>(this, Product.class, android.R.layout.two_line_list_item, database) {
            @Override
            protected void populateView(View view, Product pik, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(pik.getId());
                ((TextView)view.findViewById(android.R.id.text2)).setText(Integer.toString(pik.getQuantity()));
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
