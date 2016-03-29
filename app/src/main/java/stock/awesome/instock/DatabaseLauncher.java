package stock.awesome.instock;

import android.app.Application;

import com.firebase.client.Firebase;

// when referencing to the database, always call DatabaseLauncher.database
// instead of re-establishing a connection
public class DatabaseLauncher extends Application {

    public static Firebase database;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");
    }
}
