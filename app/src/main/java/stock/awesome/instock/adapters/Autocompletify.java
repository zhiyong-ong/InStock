package stock.awesome.instock.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import stock.awesome.instock.DatabaseReadProduct;
import stock.awesome.instock.misc_classes.Globals;

import stock.awesome.instock.R;


// Not really an adapter. It makes a textview autocomplete items from the Globals idNameList.
public class Autocompletify {

    public static AutoCompleteTextView makeAutocomplete(Context context, View view, int textView) {
        String[] idNameArr = Globals.idNameList.toArray(new String[Globals.idNameList.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_dropdown_item_1line, idNameArr);

        final AutoCompleteTextView idNameText = (AutoCompleteTextView) view.findViewById(textView);
        // start auto-completing from 1st char
        idNameText.setThreshold(1);
        idNameText.setAdapter(adapter);

        return idNameText;
    }

    public static String getStringFromView(AutoCompleteTextView textView) {
        String productID = textView.getText().toString();
        productID = Globals.idNameMap.get(productID);

        return productID;
    }
}
