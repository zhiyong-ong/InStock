package stock.awesome.instock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import stock.awesome.instock.misc_classes.Globals;
import stock.awesome.instock.misc_classes.Kit;


public class ViewAllKitsActivity extends AppCompatActivity {
    public final static String KIT_NAME = "stock.awesome.instock.KIT_NAME";
    FirebaseListAdapter<Kit> mAdapter;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_kits);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up the back button here
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase kitRef = DatabaseLauncher.database.child("kits");

        ListView kitView = (ListView) findViewById(R.id.list_view_all_kits);

        // to change the way each item in the list looks, replace android.R.layout.simple_list_item_1
        // in the following code with a custom linear layout. The xml file should have only one textview
        mAdapter = new FirebaseListAdapter<Kit>(this, Kit.class, R.layout.item_view_existing_kits, kitRef) {
            @Override
            protected void populateView(View view, Kit kit, int position) {
                ((TextView) view.findViewById(R.id.nameText)).setText(kit.getKitName());
            }
        };
        kitView.setAdapter(mAdapter);
        // when kit in list is clicked, go to new activity that populates products of that kit
        kitView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Kit entry = (Kit) parent.getAdapter().getItem(position);
                Globals.kit = entry;

                // this method starts an intent that starts ViewKitDetailsActivity
                DatabaseReadKit.read(entry.getKitName(), DatabaseReadKit.KitUseCase.GET_PRODUCT_DETAILS,
                        ViewAllKitsActivity.this, ViewKitDetailsActivity.class);
            }
        });

        //popup edit / delete / view kit
        registerForContextMenu(kitView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.list_view_all_kits) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mAdapter.getItem(info.position).getKitName());
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);
        final String menuItemName = menuItems[menuItemIndex];
        final String listItemName = mAdapter.getItem(info.position).getKitName();

        if(menuItemName.equals("Delete")) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialogView = inflater.inflate(R.layout.popup_delete_kit, null);
            final TextView kitName = (TextView) dialogView.findViewById(R.id.deleteIDView);
            kitName.setText(listItemName);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    DatabaseWriteKit.deleteKit(listItemName);
                    Toast.makeText(context, "Deleted Kit: " + listItemName, Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do nothing, go back.
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }
        else if(menuItemName.equals("Edit")) {
            Kit entry = mAdapter.getItem(info.position);
            Globals.kit = (entry);
            // this method starts an intent that starts ViewKitDetailsActivity
            DatabaseReadKit.read(entry.getKitName(), DatabaseReadKit.KitUseCase.GET_PRODUCT_DETAILS,
                    ViewAllKitsActivity.this, EditKitActivity.class);
        }
        else if(menuItemName.equals("View")) {
            Kit entry = mAdapter.getItem(info.position);
            Globals.kit = (entry);

            // this method starts an intent that starts ViewKitDetailsActivity
            DatabaseReadKit.read(entry.getKitName(), DatabaseReadKit.KitUseCase.GET_PRODUCT_DETAILS,
                    ViewAllKitsActivity.this, ViewKitDetailsActivity.class);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        if(id == R.id.refresh_main) {
            MainPage.getFirebaseDataArray();
            Toast.makeText(this, "Database refreshed", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
