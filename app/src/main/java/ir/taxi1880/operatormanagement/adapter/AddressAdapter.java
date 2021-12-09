package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.model.AddressArr;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class AddressAdapter extends ArrayAdapter<AddressArr> {
    Context context;
    int resource;
    List<AddressArr> addressModels, addressFilterModels;

    public AddressAdapter(Context context, int resource, int textViewResourceId, List<AddressArr> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.addressModels = items;
        addressFilterModels = items;
        getFilter();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        try {
            AddressArr addressArr = addressModels.get(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_custom_auto_complete, parent, false);
            }
            if (addressArr != null) {
                TextView lblName = (TextView) view.findViewById(R.id.lbl_address);
                if (lblName != null)
                    lblName.setText(addressArr.address);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "AddressAdapter class, getView method");
        }
        return view;
    }

    @Override
    public int getCount() {
        return addressModels.size();
    }

    @Nullable
    @Override
    public AddressArr getItem(int position) {
        return addressModels.get(position);
    }

    private AddressFilter addressFilter;


    @Override
    public Filter getFilter() {
        if (addressFilter == null) {
            addressFilter = new AddressFilter();
        }
        return addressFilter;

    }

    public AddressArr getAddress(int position){
        return addressModels.get(position);
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    private class AddressFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<AddressArr> filterList = new ArrayList<>();
                String[] split = constraint.toString().split(" ");
                for (int i = 0; i < addressFilterModels.size(); i++) {
                    for (int j = 0; j < split.length; j++){
                        if (addressFilterModels.get(i).address.contains(split[j])) {
                            if (filterList.size() == 0)
                            {
                                AddressArr addressArr = new AddressArr();
                                addressArr.address = addressFilterModels.get(i).address;
                                filterList.add(addressArr);
                                Log.i("TAF_AddressAdapter 0",addressArr.address);
                            }
                            else
                            {
                                for (int h = 0 ; h < filterList.size() ; h++) {
                                    if (!filterList.get(h).address.contains(addressFilterModels.get(i).address)) {
                                        AddressArr addressArr = new AddressArr();
                                        addressArr.address = addressFilterModels.get(i).address;
                                        Log.i("TAF_AddressAdapter " + j, addressArr.address);
                                        filterList.add(addressArr);
                                    }
                                }
                            }

                        }

                    }
                }

                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = addressFilterModels.size();
                results.values = addressFilterModels;
            }
            return results;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            Set<AddressArr> setAddressArr = ((ArrayList<AddressArr>) results.values)
                    .stream()
                    .collect(Collectors.toCollection(() ->
                            new TreeSet<>(Comparator.comparing(AddressArr::getAddress))));

            List<AddressArr> sortedList = setAddressArr
                    .stream() // get stream for unique SET
                    .sorted(Comparator.comparing(AddressArr::getAddress)) // rank comparing
                    .collect(Collectors.toList());

            addressModels = sortedList;
            notifyDataSetChanged();

        }
    }
}
