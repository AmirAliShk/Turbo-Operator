package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import ir.taxi1880.operatormanagement.model.AllMistakesModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class AllMistakesAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AllMistakesModel> allMistakesModels;
    DataBase dataBase;
    AllMistakesModel currentAllMistakesModel;
    ViewFlipper viewFlipper;
    int position;

    public AllMistakesAdapter(Context mContext, ArrayList<AllMistakesModel> allMistakesModels) {
        this.mContext = mContext;
        this.allMistakesModels = allMistakesModels;
    }

    @Override
    public int getCount() {
        return allMistakesModels.size();
    }

    @Override
    public Object getItem(int i) {
        return allMistakesModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_all_mistakes, viewGroup, false);
            TypefaceUtil.overrideFonts(view);
        }

        currentAllMistakesModel = (AllMistakesModel) getItem(i);
        dataBase = new DataBase(context);
        TextView txtComplaintDate = view.findViewById(R.id.txtMistakesDate);
        TextView txtComplaintTime = view.findViewById(R.id.txtMistakesTime);
        TextView txtComplaintId = view.findViewById(R.id.txtMistakesId);
        TextView txtComplaintVoipId = view.findViewById(R.id.txtMistakesVoipId);
        viewFlipper = view.findViewById(R.id.vfAccept);

        txtComplaintDate.setText(StringHelper.toPersianDigits(currentAllMistakesModel.getDate()));
        txtComplaintTime.setText(StringHelper.toPersianDigits(currentAllMistakesModel.getTime().substring(0, 5)));
        txtComplaintId.setText("  id:  "+ currentAllMistakesModel.getId()+"");
        txtComplaintVoipId.setText("  voipId:  "+ currentAllMistakesModel.getVoipId()+"");

        view.findViewById(R.id.btnAccept).setOnClickListener(view1 -> {
            position = i;
            getAccept(allMistakesModels.get(i).getId());
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
                                        dataBase.insertMistakes(allMistakesModels.get(position));
                                        allMistakesModels.remove(position);
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
