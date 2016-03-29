package stock.awesome.instock;

import android.app.Application;

import com.firebase.client.Firebase;

// Global initialisations that required context are done here.
// When referencing to the Firebase database, always call DatabaseLauncher.database instead of
// re-establishing a connection.
public class DatabaseLauncher extends Application {

    public static Firebase database;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        // Firebase writes persist across app restarts and reads use cached data
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");
    }
}
