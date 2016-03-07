package stock.awesome.instock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import stock.awesome.instock.misc_classes.Kit;
import stock.awesome.instock.misc_classes.KitStorer;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.ProductInKit;
import stock.awesome.instock.misc_classes.StringCalendar;


public class ViewAllKitsActivity extends AppCompatActivity {
    public final static String KIT_NAME = "stock.awesome.instock.KIT_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_kits);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase kitRef = DatabaseLauncher.database.child("kits");

        ListView kitView = (ListView) findViewById(R.id.list_view_all_kits);

        // to change the way each item in the list looks, replace android.R.layout.simple_list_item_1
        // in the following code with a custom linear layout. The xml file should have only one textview
        FirebaseListAdapter<Kit> mAdapter = new FirebaseListAdapter<Kit>(this, Kit.class, android.R.layout.simple_list_item_1, kitRef) {
            @Override
            protected void populateView(View view, Kit kit, int position) {
                ((TextView) view.findViewById(android.R.id.text1)).setText(kit.getKitName());
            }
        };
        kitView.setAdapter(mAdapter);

        // when kit in list is clicked, go to new activity that populates products of that kit
        kitView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewAllKitsActivity.this, ViewKitDetailsActivity.class);

                Kit entry = (Kit) parent.getAdapter().getItem(position);
                LinkedHashMap<String, ProductInKit> kitMap = entry.getKitMap();

                intent.putExtra(KIT_NAME, entry.getKitName());
                startActivity(intent);
            }
        });


//        Firebase.setAndroidContext(this);
//        Firebase database = DatabaseLauncher.database.child("kits");
//
//        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view_kits);
//        recycler.setHasFixedSize(true);
//        recycler.setLayoutManager(new LinearLayoutManager(this));
//
//        FirebaseRecyclerAdapter<Kit, KitViewHolder> mAdapter = new FirebaseRecyclerAdapter<Kit, KitViewHolder>
//                (Kit.class, android.R.layout.simple_list_item_1, KitViewHolder.class, database) {
//            @Override
//            public void populateViewHolder(KitViewHolder kitViewHolder, Kit kit, int position) {
//                kitViewHolder.nameText.setText(kit.getKitName());
//            }
//        };
//        recycler.setAdapter(mAdapter);

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
