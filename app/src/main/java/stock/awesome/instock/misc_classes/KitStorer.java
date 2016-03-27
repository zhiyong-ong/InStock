package stock.awesome.instock.misc_classes;

import java.util.ArrayList;

import stock.awesome.instock.misc_classes.Kit;

// Stores the kit that needs to be passed in between ViewAllKitsActivity and ViewKitDetailsActivity
// Bad software engineering practice, but implementing parcelables is time-consuming
// Also stores list of product ids and names for autocompletetextview in SearchProductsActivity
public class KitStorer {
    public static Kit kit = null;
    public static ArrayList<String> idNameList = null;

    public static void storeKit(Kit inKit) {
        kit = inKit;
    }

    public static void storeProductIdNameList(ArrayList<String> list) {
        idNameList = list;
    }

}
