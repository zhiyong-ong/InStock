package stock.awesome.instock;

import com.firebase.client.Firebase;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseWriteKit {

    private static final Firebase database = DatabaseLauncher.database;
    private String id = null;
    private int qty = -1;

    public DatabaseWriteKit(Firebase database) {
    }

    public void writeKit(Kit kit) {
        Firebase ref = database.child("kits").child(kit.getKitName());

        LinkedHashMap<Product, Integer> kitHashMap = kit.getHashMap();

        for (Map.Entry<Product, Integer> entry : kitHashMap.entrySet())  {

            Map<String, Object> newKit = new LinkedHashMap<String, Object>();

            id = entry.getKey().getId();
            qty = entry.getValue();

            newKit.put("/id", id);
            newKit.put("/quantity", qty);

            ref.push().updateChildren(newKit);
        }
    }

}
