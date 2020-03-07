package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.MessageAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.MessageModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    public static final String TAG = MessageFragment.class.getSimpleName();
    Unbinder unbinder;
    private List<MessageModel> messageModels;
    MessageAdapter messageAdapter;

    @BindView(R.id.listMessage)
    RecyclerView listMessage;

    @BindView(R.id.edtMessage)
    EditText edtMessage;

    @OnClick(R.id.imgBack)
    void onBack() {
        KeyBoardHelper.hideKeyboard();
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.vfSend)
    ViewFlipper vfSend;

    @BindView(R.id.vfMessage)
    ViewFlipper vfMessage;

    @OnClick(R.id.llSend)
    void onSend() {
        String message = edtMessage.getText().toString();
        if (message.isEmpty()) {
            return;
        }
//        progressBar.setVisibility(View.VISIBLE);
//        imgSend.setVisibility(View.GONE);
        sendMessage(MyApplication.prefManager.getUserCode(), message);
        messageAdapter.notifyDataSetChanged();
    }

    @BindView(R.id.tvName)
    TextView tvName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        tvName.setText(MyApplication.prefManager.getOperatorName());

        messageModels = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyApplication.context);
        listMessage.setLayoutManager(layoutManager);
        listMessage.setItemAnimator(new DefaultItemAnimator());

        getMessages(MyApplication.prefManager.getUserCode());

//        MyApplication.handler.postDelayed(() -> listMessage.smoothScrollToPosition(messageModels.size()), 100);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getMessages(int operatorId) {
        vfMessage.setDisplayedChild(0);

            RequestHelper.builder(EndPoints.GET_MESSAGES)
                    .addParam("operatorId", operatorId)
                    .listener(onGetMessages)
                    .post();
    }

    private RequestHelper.Callback onGetMessages = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    messageModels.clear();
                    JSONArray arr = new JSONArray(args[0].toString());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object = arr.getJSONObject(i);
                        MessageModel messageModel = new MessageModel();
                        messageModel.setMsgId(object.getInt("id"));
                        messageModel.setUserId(object.getInt("userId"));
                        messageModel.setMsgType(object.getInt("type"));
                        messageModel.setMsgDate(object.getString("saveDate"));
                        messageModel.setMsgTime(object.getString("saveTime"));
                        messageModel.setMsgText(object.getString("messageText"));
                        messageModels.add(messageModel);
                    }
                    vfMessage.setDisplayedChild(1);
                    messageAdapter = new MessageAdapter(MyApplication.context, messageModels);
                    listMessage.setAdapter(messageAdapter);
                    MyApplication.handler.postDelayed(() -> listMessage.smoothScrollToPosition(messageModels.size()), 100);
                    messageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };

    private void sendMessage(int operatorId, String message) {
//        vfSend.setDisplayedChild(1);

            RequestHelper.builder(EndPoints.SEND_MESSAGES)
                    .addParam("operatorId", operatorId)
                    .addParam("message", message)
                    .listener(onSendMessage)
                    .post();

        messageAdapter.notifyDataSetChanged();
    }

    private RequestHelper.Callback onSendMessage = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    if (status == 1) {
                        edtMessage.setText("");
                        messageAdapter.notifyDataSetChanged();
                        getMessages(MyApplication.prefManager.getUserCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };

}
