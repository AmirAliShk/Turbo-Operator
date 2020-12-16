package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    private Context context;
    private List<MessageModel> messageModelsList;
    private ArrayList<MessageModel> messageModelsArrList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMsgIn, llMsgOut;
        public TextView txtMsgIn, txtTimeMsgIn, txtDateMsgIn, txtMsgOut, txtTimeMsgOut, txtDateMsgOut;

        public MyViewHolder(View view) {
            super(view);

            llMsgIn = (LinearLayout) view.findViewById(R.id.llMsgIn);
            llMsgOut = (LinearLayout) view.findViewById(R.id.llMsgOut);
            txtMsgIn = (TextView) view.findViewById(R.id.txtMsgIn);
            txtTimeMsgIn = (TextView) view.findViewById(R.id.txtTimeMsgIn);
            txtDateMsgIn = (TextView) view.findViewById(R.id.txtDateMsgIn);
            txtMsgOut = (TextView) view.findViewById(R.id.txtMsgOut);
            txtTimeMsgOut = (TextView) view.findViewById(R.id.txtTimeMsgOut);
            txtDateMsgOut = (TextView) view.findViewById(R.id.txtDateMsgOut);

        }
    }

    public MessageAdapter(Context context, List<MessageModel> member1) {
        this.context = context;
        messageModelsList = member1;
        messageModelsArrList = new ArrayList<>();
        messageModelsArrList.addAll(member1);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        TypefaceUtil.overrideFonts(itemView);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final MessageModel messageModel = messageModelsList.get(position);

        holder.llMsgOut.setVisibility(View.GONE);
        holder.llMsgIn.setVisibility(View.GONE);

        if (messageModel.getMsgType()==1) {
            holder.llMsgOut.setVisibility(View.VISIBLE);
            holder.txtMsgOut.setText(StringHelper.toPersianDigits(messageModel.getMsgText()));
            holder.txtTimeMsgOut.setText(StringHelper.toPersianDigits(messageModel.getMsgTime()));
            holder.txtDateMsgOut.setText(StringHelper.toPersianDigits(messageModel.getMsgDate()));
        } else {
            holder.llMsgIn.setVisibility(View.VISIBLE);
            holder.txtMsgIn.setText(StringHelper.toPersianDigits(messageModel.getMsgText()));
            holder.txtTimeMsgIn.setText(StringHelper.toPersianDigits(messageModel.getMsgTime()));
            holder.txtDateMsgIn.setText(StringHelper.toPersianDigits(messageModel.getMsgDate()));
        }
    }

    @Override
    public int getItemCount() {
        return messageModelsList.size();
    }

}
