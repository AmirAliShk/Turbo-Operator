package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.OperatorModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class OperatorAdapter extends BaseAdapter implements Filterable {

    private ArrayList<OperatorModel> operatorModels;
    private ArrayList<OperatorModel> operatorFilteredModels;
    private LayoutInflater layoutInflater;
    private Context context;
    private ValueFilter valueFilter;


    public OperatorAdapter(ArrayList<OperatorModel> operatorModels, Context context) {
        this.context = context;
        this.operatorModels = operatorModels;
        this.operatorFilteredModels = operatorModels;
        this.layoutInflater = LayoutInflater.from(context);
        getFilter();
    }

    @Override
    public int getCount() {
        return operatorModels.size();
    }

    @Override
    public OperatorModel getItem(int position) {
        return operatorModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView = convertView;

        try {
            final OperatorModel operatorModel = operatorModels.get(position);
            if (myView == null) {
                myView = layoutInflater.inflate(R.layout.item_operator, null);
                TypefaceUtil.overrideFonts(myView);
            }
            TextView txtOperatorName = myView.findViewById(R.id.txtOperatorName);
            TextView txtOperatorShift = myView.findViewById(R.id.txtOperatorShift);

            txtOperatorName.setText(operatorModel.getOperatorName());
            txtOperatorShift.setText(operatorModel.getOperatorShift());

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e,"OperatorAdapter class, getView method");
        }

        return myView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {

                ArrayList<OperatorModel> filterList = new ArrayList<>();

                for (int i = 0; i < operatorFilteredModels.size(); i++) {
                    if (operatorFilteredModels.get(i).getOperatorName().contains(constraint.toString())) {
                        OperatorModel operator = new OperatorModel();
                        operator.setOperatorName(operatorFilteredModels.get(i).getOperatorName());
                        operator.setOperatorId(operatorFilteredModels.get(i).getOperatorId());
                        filterList.add(operator);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = operatorFilteredModels.size();
                results.values = operatorFilteredModels;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            operatorModels.clear();
            operatorModels = (ArrayList<OperatorModel>) results.values;
            notifyDataSetChanged();
        }
    }

    public OperatorModel getOperator(int position){
        return operatorModels.get(position);
    }

}