package stock.awesome.instock.misc_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedHashMap;

/**
 * Created by Kabir on 07/03/2016.
 */
// populates kit items in a listview. The layout for each row is described in the getView method.
public class KitAdapter extends BaseAdapter {

    private String mKitName;
    private LinkedHashMap<String, ProductInKit> mKitMap = new LinkedHashMap<String, ProductInKit>();
    private String[] mKeys;
    private View mView;
    private Context mContext;

    public KitAdapter(Context context, Kit data, View convertView){
        mContext = context;
        mKitName = data.getKitName();
        mKitMap  = data.getKitMap();
        mKeys = mKitMap.keySet().toArray(new String[mKitMap.size()]);
        mView = convertView;
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

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        String key = mKeys[pos];
        ProductInKit value = (ProductInKit) getItem(pos);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
        }

        TextView pinkId = (TextView) convertView.findViewById(android.R.id.text1);
        TextView pinkQty = (TextView) convertView.findViewById(android.R.id.text2);

        pinkId.setText(value.getId());
        pinkQty.setText(Integer.toString(value.getQuantity()));

        return convertView;
    }
}
