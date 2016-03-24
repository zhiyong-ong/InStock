package stock.awesome.instock;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;

public class SearchProductsActivity extends AppCompatActivity {

    private static Firebase database;
    private static ArrayList<String> idNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_dropdown_item_1line, COUNTRIES);

        database = DatabaseLauncher.database.child("products");
        getFirebaseDataArray();
    }

//    private static final String[] COUNTRIES = new String[] {
//            "Belgium", "France", "Italy", "Germany", "Spain"
//    };

    private void getFirebaseDataArray() { // String startingChar
        idNameList = new ArrayList<>();
        Query queryRef = database.orderByKey(); //.startAt(startingChar).endAt(startingChar + "\uf8ff");

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Product prod = child.getValue(Product.class);
                    Log.e("prod details", prod.getId() + " " + prod.getName());
                    idNameList.add(prod.getId());
                    idNameList.add(prod.getName());

                    viewAdapter(idNameList.toArray(new String[idNameList.size()]));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Product read failed", firebaseError.getMessage());
            }
        });
    }


    private void viewAdapter(String[] idNameList) {
        Log.e("idNameList", idNameList.toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, idNameList);

        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView1);
        textView.setAdapter(adapter);
    }

}
