package ir.taxi1880.operatormanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ReplacementModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SendReplacementAdapter extends BaseAdapter {

    private ArrayList<ReplacementModel> replacementModels = new ArrayList<>();
    private LayoutInflater layoutInflater;
    int replaceId = 0;

    public interface Listener {
        void onRemoveItem(int position);
    }

    Listener listener;

    public SendReplacementAdapter(ArrayList<ReplacementModel> replacementModels, Context context, Listener listener) {
        this.replacementModels = replacementModels;
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return replacementModels.size();
    }

    @Override
    public Object getItem(int position) {
        return replacementModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        try {
            final ReplacementModel replacementModel = replacementModels.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_send_replacement, parent, false);
                TypefaceUtil.overrideFonts(convertView);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.txtStatus.setText("درخواست جابه جایی شما به تاریخ: " + replacementModel.getReplaceDate() + "، شیفت: " + replacementModel.getReplaceShiftName() + "، توسط اپراتور: " + replacementModel.getReplaceOperatorNameChange() + "، در وضعیت: " + replacementModel.getStatusStr() + " میباشد.");

            int statusImage = R.drawable.ic_waiting_req;
            switch (replacementModel.getReplaceStatus()) {
                case 0:
                    statusImage = R.drawable.ic_waiting_req;
                    viewHolder.btnCancel.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    statusImage = R.drawable.ic_done;
                    viewHolder.btnCancel.setVisibility(View.GONE);
                    break;
                case 2:
                    statusImage = R.drawable.ic_cancel;
                    viewHolder.btnCancel.setVisibility(View.GONE);
                    break;
            }
            viewHolder.imgStatus.setImageResource(statusImage);

            viewHolder.btnCancel.setOnClickListener(v -> {
                new GeneralDialog()
                        .title("هشدار")
                        .message("آیا از لغو درخواست خود اطمینان دارید؟")
                        .firstButton("بله", () -> {
                            replaceId = replacementModel.getReplaceId();
                            cancelRequest();
                        })
                        .secondButton("خیر", null)
                        .show();
            });

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "SendReplacementAdapter class, getView method");
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView imgStatus;
        Button btnCancel;
        TextView txtStatus;

        public ViewHolder() {
        }

        ViewHolder(View convertView) {
            txtStatus = convertView.findViewById(R.id.txtStatus);
            imgStatus = convertView.findViewById(R.id.imgStatus);
            btnCancel = convertView.findViewById(R.id.btnCancel);
        }
    }

    private void cancelRequest() {

        RequestHelper.builder(EndPoints.CANCEL_REPLACEMENT_REQUEST)
                .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
                .addHeader("id_token", MyApplication.prefManager.getIdToken())
                .addParam("requestId", replaceId)
                .listener(onCancel)
                .post();

    }

    RequestHelper.Callback onCancel = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    if (status == 1) {
                        MyApplication.Toast("لغو درخواست با موفقیت انجام شد", Toast.LENGTH_SHORT);
                        if (new ViewHolder().btnCancel != null)
                            new ViewHolder().btnCancel.setVisibility(View.GONE);
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "SendReplacementAdapter class, onCancel onResponse method");
                }
            });
        }


        @Override
        public void onFailure(Runnable reCall, Exception e) {
        }

    };
}
