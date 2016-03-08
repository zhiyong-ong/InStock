package stock.awesome.instock.misc_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import stock.awesome.instock.R;

/**
 * Created by zhiyong on 8/3/2016.
 */
public class BuildKitAdapter extends ArrayAdapter<Product>{


    /** To cache views of item */
    private static class ViewHolder {
        private TextView productID;
        private TextView qty;
        private TextView name;
        private TextView location;

        /**
         * General constructor
         */
        ViewHolder() {
            // nothing to do here
        }
    }

    /** Inflater for list items */
    private final LayoutInflater inflater;
    public BuildKitAdapter(Context context, int textViewResourceId, List<Product> objects) {
        super(context, textViewResourceId, objects);

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View itemView = convertView;
        ViewHolder holder = null;
        final Product item = getItem(position);

        if(null == itemView) {
            itemView = this.inflater.inflate(R.layout.item_view_build_kit, parent, false);

            holder = new ViewHolder();

            holder.productID = (TextView)itemView.findViewById(R.id.idText);
            holder.qty = (TextView)itemView.findViewById(R.id.qtyText);
            holder.name = (TextView)itemView.findViewById(R.id.nameText);
            holder.location = (TextView)itemView.findViewById(R.id.locText);

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder)itemView.getTag();
        }

        holder.productID.setText(item.getId());
        holder.qty.setText(item.getQuantity());
        holder.name.setText(item.getName());
        holder.location.setText(item.getLocation());

        return itemView;
    }

}
