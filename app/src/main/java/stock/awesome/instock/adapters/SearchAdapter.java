package stock.awesome.instock.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import stock.awesome.instock.R;

// From https://gist.github.com/fjfish/3024308
public class SearchAdapter extends BaseAdapter implements Filterable {

    private List<String> originalData = null;
    private List<String> filteredData = null;
    private int mLayout = 0;
    private Context mContext = null;
    private ItemFilter mFilter = new ItemFilter();

    public SearchAdapter(Context context, List<String> data, int layout) {
        mContext = context;
        filteredData = data;
        originalData = data;
        mLayout = layout;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need to reinflate
        // it. We only inflate a new View when the convertView supplied by ListView is null.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mLayout, parent, false);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.idTextView = (TextView) convertView.findViewById(R.id.search_id_text_view);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.search_name_text_view);

            // Bind the data efficiently with the holder.
            convertView.setTag(holder);

        } else {
            // Get the ViewHolder back to get fast access to the TextView and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.idTextView.setText(filteredData.get(position));

        return convertView;
    }


    static class ViewHolder {
        TextView idTextView;
        TextView nameTextView;
    }


    @Override
    public Filter getFilter() {
        return mFilter;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            final List<String> list = originalData;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<>(count);

            String filterableString ;

            for (int i=0; i<count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }
}
