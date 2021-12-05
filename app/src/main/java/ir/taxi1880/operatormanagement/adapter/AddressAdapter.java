package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.model.AddressArr;

public class AddressAdapter extends ArrayAdapter<AddressArr> {
    Context context;
    int resource, textViewResourceId;
    List<AddressArr> items, tempItems, suggestions;

    public AddressAdapter(Context context, int resource, int textViewResourceId, List<AddressArr> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<AddressArr>(items); // this makes the difference.
        suggestions = new ArrayList<AddressArr>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_custom_auto_complete, parent, false);
        }
        AddressArr addressArr = items.get(position);
        if (addressArr != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_address);
            if (lblName != null)
                lblName.setText(addressArr.address);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((AddressArr) resultValue).address;
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (AddressArr addressArr : tempItems) {
                    if (addressArr.address.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(addressArr);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<AddressArr> filterList = (ArrayList<AddressArr>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (AddressArr addressArr : filterList) {
                    add(addressArr);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
