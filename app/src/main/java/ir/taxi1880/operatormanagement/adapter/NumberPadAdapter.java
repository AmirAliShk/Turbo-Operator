package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class NumberPadAdapter extends BaseAdapter {

    public interface NumberListener {
        void onResult(String character);
    }

    String[] padNumbers = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    private LayoutInflater layoutInflater;
    private NumberListener listener;

    public NumberPadAdapter(NumberListener listener) {
        layoutInflater = LayoutInflater.from(MyApplication.currentActivity);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return padNumbers.length;
    }

    @Override
    public Object getItem(int i) {
        return padNumbers[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        String padNumber = padNumbers[i];
        if (v == null) {
            v = layoutInflater.inflate(R.layout.item_number_pad, null, false);
        }
        TypefaceUtil.overrideFonts(v);
        TextView txtNumber = v.findViewById(R.id.txtNumber);
        RelativeLayout rlRoot = v.findViewById(R.id.rlRoot);

        rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (i) {
                    case 9:
                        listener.onResult("0");
                        break;
                    default:
                        listener.onResult(i + 1 + "");
                }

            }
        });

        txtNumber.setText(padNumber);

        return v;
    }
}
