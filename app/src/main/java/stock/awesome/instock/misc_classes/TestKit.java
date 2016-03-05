package stock.awesome.instock.misc_classes;

import java.util.LinkedHashMap;

/**
 * Created by Kabir on 06/03/2016.
 */
public class TestKit {

    private LinkedHashMap<String, Integer> kit = new LinkedHashMap<>();


    public TestKit() {
    }


    public LinkedHashMap<String, Integer> getKit() {
        return kit;
    }

    public void setKit(LinkedHashMap<String, Integer> kit) {
        this.kit = kit;
    }

    public void addProduct(String id, int qty) {
        kit.put(id, qty);
    }

}
