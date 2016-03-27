package stock.awesome.instock;

import com.firebase.client.Firebase;

// when referencing to the database, always call DatabaseLauncher.database
// instead of re-establishing a connection
public class DatabaseLauncher {

    public static Firebase database;

    public static void launch() {
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        database = new Firebase("https://scorching-inferno-2190.firebaseio.com/");
    }

}
