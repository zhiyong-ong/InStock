package stock.awesome.instock;

import android.util.Log;

import com.firebase.client.Firebase;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseWriteKit {

    private static final Firebase database = DatabaseLauncher.database;

    public static void write(Kit kit) {
        if (kit.getKitName() == null) {
            Log.e("Kit write failed", "kit has no name");
        }

        Firebase ref = database.child("kits").child(kit.getKitName());

        LinkedHashMap<Product, Integer> kitHashMap = kit.getHashMap();

        for (Map.Entry<Product, Integer> entry : kitHashMap.entrySet())  {

            Map<String, Object> newKit = new LinkedHashMap<String, Object>();

            String id = entry.getKey().getId();
            int qty = entry.getValue();

            newKit.put("/id", id);
            newKit.put("/quantity", qty);

            ref.push().updateChildren(newKit);
        }
    }

}
