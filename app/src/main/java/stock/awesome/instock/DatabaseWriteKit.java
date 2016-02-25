package stock.awesome.instock;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseWriteKit {

    Firebase database = null;
    String id = null;
    int qty = -1;

    public DatabaseWriteKit(Firebase database) {
        this.database = database;
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
