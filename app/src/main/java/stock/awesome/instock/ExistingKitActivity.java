package stock.awesome.instock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import stock.awesome.instock.misc_classes.Kit;


public class ExistingKitActivity extends AppCompatActivity {
    //private static RecyclerView mRecyclerView;
    //private static RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_kit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        Firebase database = DatabaseLauncher.database.child("kits");

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view_kits);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<Kit, KitViewHolder> mAdapter = new FirebaseRecyclerAdapter<Kit, KitViewHolder>
                (Kit.class, android.R.layout.simple_list_item_1, KitViewHolder.class, database) {
            @Override
            public void populateViewHolder(KitViewHolder kitViewHolder, Kit kit, int position) {
                kitViewHolder.nameText.setText(kit.getKitName());
            }
        };
        recycler.setAdapter(mAdapter);

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


    public static class KitViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;

        public KitViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView)itemView.findViewById(android.R.id.text1);
        }
    }
}
