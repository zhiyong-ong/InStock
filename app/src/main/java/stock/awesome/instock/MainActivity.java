package stock.awesome.instock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);
        Firebase database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // write testing
        DatabaseWriteKit db = new DatabaseWriteKit(database);
        Kit testKit = new Kit("test_kit");
        testKit.addProduct("104", 6);
        testKit.addProduct("105", 44);
        db.writeKit(testKit);
        //testProd = db.readFromFirebase("3047");
        //Log.w("db written", "id: " + testProd.getId() + " location: " + testProd.getLocation());
    }

    public void sendNewItemIntent(View view) {
        Intent intent = new Intent(this, InputStockActivity.class);
        startActivity(intent);
    }

    public void sendNewKitIntent(View view) {
        Intent intent = new Intent(this, BuildKitActivity.class);
        startActivity(intent);
    }
    public void sendExistingKitIntent(View view) {
        Intent intent = new Intent(this, ExistingKitActivity.class);
        startActivity(intent);
    }
    public void viewAllIntent (View view) {
        Intent intent = new Intent(this, ViewAllStocksActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}