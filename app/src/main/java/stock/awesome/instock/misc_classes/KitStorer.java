package stock.awesome.instock.misc_classes;

// Stores the kit that needs to be passed in between ViewAllKitsActivity and ViewKitDetailsActivity
public class KitStorer {
    public static Kit kit = null;

    public static void storeKit(Kit inKit) {
        kit = inKit;
    }
}
