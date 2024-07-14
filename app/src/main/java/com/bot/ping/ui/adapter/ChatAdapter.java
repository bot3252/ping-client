package com.bot.ping.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.model.Message;
import com.bot.ping.ui.ChatAppMsgViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAppMsgViewHolder> {
    public List<Message> msgDtoList;
    public ChatAdapter(List<Message> msgDtoList) {
        this.msgDtoList = msgDtoList;
    }

    @Override
    public void onBindViewHolder(ChatAppMsgViewHolder holder, int position) {
        Message msgDto = this.msgDtoList.get(position);

        if(msgDto.MSG_TYPE_RECEIVED.equals(msgDto.getMsgType())) {
            holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.leftMsgTextView.setText(msgDto.getMessage());
            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
        }
        else if(msgDto.MSG_TYPE_SENT.equals(msgDto.getMsgType())) {
            holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.rightMsgTextView.setText(msgDto.getMessage());
            holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
        }
    }
    @Override
    public ChatAppMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_chat_app_item_view, parent, false);
        return new ChatAppMsgViewHolder(view);
    }
    @Override
    public int getItemCount() {
        if(msgDtoList==null)
        {
            msgDtoList = new ArrayList<Message>();
        }
        return msgDtoList.size();
    }
}
