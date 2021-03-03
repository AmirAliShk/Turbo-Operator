package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllComplaintModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class AllComplaintAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AllComplaintModel> allComplaintModels;
    DataBase dataBase;
    AllComplaintModel currentAllComplaintModel;
    ViewFlipper viewFlipper;
    int position;

    public AllComplaintAdapter(Context mContext, ArrayList<AllComplaintModel> allComplaintModels) {
        this.mContext = mContext;
        this.allComplaintModels = allComplaintModels;
    }

    @Override
    public int getCount() {
        return allComplaintModels.size();
    }

    @Override
    public Object getItem(int i) {
        return allComplaintModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_all_complaint, viewGroup, false);
            TypefaceUtil.overrideFonts(view);
        }

        currentAllComplaintModel = (AllComplaintModel) getItem(i);
        dataBase = new DataBase(context);
        TextView txtComplaintDate = view.findViewById(R.id.txtComplaintDate);
        TextView txtComplaintTime = view.findViewById(R.id.txtComplaintTime);
        viewFlipper = view.findViewById(R.id.vfAccept);

        txtComplaintDate.setText(StringHelper.toPersianDigits(currentAllComplaintModel.getDate()));
        txtComplaintTime.setText(StringHelper.toPersianDigits(currentAllComplaintModel.getTime().substring(0, 5)));

        view.findViewById(R.id.btnAccept).setOnClickListener(view1 -> {
            position = i;
            getAccept(currentAllComplaintModel.getId());
        });

        return view;
    }

    private void getAccept(int id) {
        if (viewFlipper != null) {
            viewFlipper.setDisplayedChild(1);
        }
        RequestHelper.builder(EndPoints.ACCEPT_LISTEN)
                .addParam("listenId", id)
                .listener(getAccept)
                .put();
    }

    RequestHelper.Callback getAccept = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");
                    if (success) {
                        JSONObject data = obj.getJSONObject("data");
                        boolean status = data.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .message("با موفقیت به لیست در حال بررسی اضافه شد")
                                    .cancelable(false)
                                    .firstButton("باشه", () -> {
                                        dataBase.insertComplaint(currentAllComplaintModel);
                                        allComplaintModels.remove(position);
                                        notifyDataSetChanged();
                                    })
                                    .show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (viewFlipper != null) {
                    viewFlipper.setDisplayedChild(0);
                }

            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (viewFlipper != null) {
                    viewFlipper.setDisplayedChild(0);
                }
            });
        }
    };
}
