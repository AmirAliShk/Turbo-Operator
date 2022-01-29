package ir.taxi1880.operatormanagement.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.taxi1880.operatormanagement.adapter.MessageAdapter;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentMessageBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.MessageModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class MessageFragment extends Fragment {
    public static final String TAG = MessageFragment.class.getSimpleName();
    FragmentMessageBinding binding;
    private List<MessageModel> messageModels;
    MessageAdapter messageAdapter;
    String message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        TypefaceUtil.overrideFonts(binding.getRoot());

        messageModels = new ArrayList<>();

        NotificationManager notificationManager = (NotificationManager) MyApplication.currentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constant.PUSH_NOTIFICATION_ID);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyApplication.context);
        binding.listMessage.setLayoutManager(layoutManager);
        binding.listMessage.setItemAnimator(new DefaultItemAnimator());

        getMessages();

        binding.llSend.setOnClickListener(view -> {
            message = binding.edtMessage.getText().toString();
            if (message.isEmpty()) {
                return;
            }
            sendMessage();
            messageAdapter.notifyDataSetChanged();
        });

        binding.imgBack.setOnClickListener(view -> {
            KeyBoardHelper.hideKeyboard();
            MyApplication.currentActivity.onBackPressed();
        });

        return binding.getRoot();
    }

    private void getMessages() {
        if (binding.vfMessage != null)
            binding.vfMessage.setDisplayedChild(0);

        RequestHelper.builder(EndPoints.GET_MESSAGES)
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

                    messageAdapter = new MessageAdapter(messageModels);
                    if (binding.listMessage != null) {
                        binding.listMessage.setAdapter(messageAdapter);
                        MyApplication.handler.postDelayed(() -> {
                            if (binding.vfMessage != null) {
                                binding.vfMessage.setDisplayedChild(1);
                                binding.listMessage.scrollToPosition(binding.listMessage.getAdapter().getItemCount() - 1);
                                messageAdapter.notifyDataSetChanged();
                            }
                        }, 200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onGetMessages onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfMessage != null) {
                    binding.vfMessage.setDisplayedChild(2);
                }
            });
        }
    };

    private void sendMessage() {
        RequestHelper.builder(EndPoints.SEND_MESSAGES)
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
                        if (binding.edtMessage != null)
                            binding.edtMessage.setText("");
                        messageAdapter.notifyDataSetChanged();
                        getMessages();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onSendMessage onResponse method");
                }
            });
        }
    };
}