package ir.taxi1880.operatormanagement.adapter;

import android.annotation.SuppressLint;
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
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ReplacementModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SendReplacementAdapter extends BaseAdapter {

    public static final String TAG = SendReplacementAdapter.class.getSimpleName();
    private ArrayList<ReplacementModel> replacementModels = new ArrayList<>();
    private LayoutInflater layoutInflater;
    int replaceId = 0;

    public interface Listener {
        void onRemoveItem(int position);
    }

    Listener listener;

    public SendReplacementAdapter(ArrayList<ReplacementModel> replacementModels, Listener listener) {
        this.replacementModels = replacementModels;
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(MyApplication.currentActivity);
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
            String date = DateHelper.strPersianTwo(DateHelper.parseDate(replacementModels.get(position).getReplaceDate()));
            viewHolder.txtOperatorName.setText("درخواست ارسالی توسط خانم " + replacementModel.getReplaceOperatorNameChange());
            viewHolder.txtStatus.setText(replacementModel.getStatusStr());
            viewHolder.txtShiftDate.setText(date);
            viewHolder.txtShiftName.setText(replacementModel.getReplaceShiftName());

            int statusImage = R.drawable.ic_question;
            switch (replacementModel.getReplaceStatus()) {
                case 0:
                    statusImage = R.drawable.ic_question;
                    viewHolder.btnCancel.setVisibility(View.VISIBLE);
                    viewHolder.txtStatus.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorYellow));
                    break;
                case 1:
                    statusImage = R.drawable.ic_tick;
                    viewHolder.btnCancel.setVisibility(View.GONE);
                    viewHolder.txtStatus.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorGreen));
                    break;
                case 2:
                    statusImage = R.drawable.ic_cancel;
                    viewHolder.btnCancel.setVisibility(View.GONE);
                    viewHolder.txtStatus.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorRed));
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
            AvaCrashReporter.send(e, TAG + " class, getView method");
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView imgStatus;
        Button btnCancel;
        TextView txtStatus;
        TextView txtOperatorName;
        TextView txtShiftDate;
        TextView txtShiftName;

        public ViewHolder() {
        }

        ViewHolder(View convertView) {
            txtStatus = convertView.findViewById(R.id.txtStatus);
            txtShiftDate = convertView.findViewById(R.id.txtShiftDate);
            txtShiftName = convertView.findViewById(R.id.txtShiftName);
            txtOperatorName = convertView.findViewById(R.id.txtOperatorName);
            imgStatus = convertView.findViewById(R.id.imgStatus);
            btnCancel = convertView.findViewById(R.id.btnCancel);
        }
    }

    private void cancelRequest() {
        RequestHelper.builder(EndPoints.CANCEL_REPLACEMENT_REQUEST)
                .addParam("requestId", replaceId)
                .listener(onCancel)
                .put();
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
                    AvaCrashReporter.send(e, TAG + " class, onCancel onResponse method");
                }
            });
        }
    };
}