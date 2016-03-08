package stock.awesome.instock.misc_classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import stock.awesome.instock.DatabaseReadKit;
import stock.awesome.instock.R;


// populates kit items in a listview.
// the layout for each row that is inflated is android.R.layout.two_line_list_item
// (described in the getView method)
public class KitAdapter extends BaseAdapter {

    private String mKitName;
    private LinkedHashMap<String, ProductInKit> mKitMap = new LinkedHashMap<String, ProductInKit>();
    private String[] mKeys;
    private Context mContext;
    private static HashMap<String, Product> mProductMap;

    public KitAdapter(Context context, Kit data){
        mContext = context;
        mKitName = data.getKitName();
        mKitMap  = data.getKitMap();
        mKeys = mKitMap.keySet().toArray(new String[mKitMap.size()]);
    }


    public static void getProductDetails(HashMap<String, Product> productMap) {
        mProductMap = productMap;
        Log.e("productMap received", productMap.toString());
    }


    @Override
    public int getCount() {
        return mKitMap.size();
    }

    @Override
    public Object getItem(int position) {
        return mKitMap.get(mKeys[position]);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    // LAGS UI THREAD BECAUSE IT IS ONLY CALLED AFTER THE PRODUCT DETAILS ARE RETURNED FROM DATABASE
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ProductInKit value = (ProductInKit) getItem(pos);
        String id = value.getId();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.v2_layout_view_kit_details, parent, false);
        }

        TextView pinkId = (TextView) convertView.findViewById(R.id.product_in_kit_id);
        TextView pinkQty = (TextView) convertView.findViewById(R.id.product_in_kit_qty);
        TextView prodName = (TextView) convertView.findViewById(R.id.product_in_kit_name);
        TextView prodLocation = (TextView) convertView.findViewById(R.id.product_in_kit_location);

        pinkId.setText(id);
        pinkQty.setText(Integer.toString(value.getQuantity()));
        prodName.setText(mProductMap.get(id).getName());
        prodLocation.setText(mProductMap.get(id).getLocation());

        return convertView;
    }
}
