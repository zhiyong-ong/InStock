package stock.awesome.instock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);
        Firebase database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseOps readWrite = new DatabaseOps(database);

        Product test1 = new Product("1245", "Zhi yong's Marvellous Test Tube, 5ml");
        test1.setLocation("B4");
        readWrite.writeToFirebase(test1);

        Product test2 = new Product("7676", "Inflatable lifeboat, blue");
        readWrite.writeToFirebase(test2);

        Product test = readWrite.readFromFirebase("1245");

        if (test.getName() != null) {
            Log.d("main: name is ", test.getName());
            Log.d("main: qty is ", "" + test.getQuantity());
        }


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
