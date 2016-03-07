package stock.awesome.instock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.firebase.client.Firebase;

import stock.awesome.instock.misc_classes.KitAdapter;
import stock.awesome.instock.misc_classes.KitStorer;


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

        // inflate listview list_view_kit_details (in activity_view_kit_details.xml)
        ListView itemView = (ListView) findViewById(R.id.list_view_kit_details);

        // null param means use default view in KitAdapter code
        KitAdapter mAdapter = new KitAdapter(this, KitStorer.kit, null);
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
