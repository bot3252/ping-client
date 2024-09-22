package com.bot.ping.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.model.Message;
import com.bot.ping.ui.holder.ChatAppMsgViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<ChatAppMsgViewHolder> {
    public List<Message> allMessage;
    public ChatAdapter(List<Message> allMessage) {
        this.allMessage = allMessage;
    }

    @Override
    public void onBindViewHolder(ChatAppMsgViewHolder holder, int position) {
        Message currentMessage = allMessage.get(position);
        int wrapContent=ViewGroup.LayoutParams.MATCH_PARENT;

        LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(wrapContent, wrapContent, 1);

        if (position!=0) {
            Message lastMessage = allMessage.get(position - 1);
            if(!Objects.equals(currentMessage.getDate(), lastMessage.getDate())){
                holder.dateTextView.setText(currentMessage.getDate());
                holder.dateTextView.setHeight(60);
            }else {
                holder.dateTextView.setText("");
                holder.dateTextView.setHeight(0);
            }
        }else {
            holder.dateTextView.setText(currentMessage.getDate());
            holder.dateTextView.setHeight(60);
        }

        if(Message.MSG_TYPE_RECEIVED.equals(currentMessage.getMsgType())) {
            holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.leftMsgTextView.setText(currentMessage.getMessage());
            holder.chat_left_time_text_view.setText(currentMessage.getTime());
            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
        }
        else if(Message.MSG_TYPE_SENT.equals(currentMessage.getMsgType())) {
            holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.rightMsgTextView.setText(currentMessage.getMessage());
            holder.chat_right_time_text_view.setText(currentMessage.getTime());
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
        if(allMessage==null)
        {
            allMessage = new ArrayList<Message>();
        }
        return allMessage.size();
    }

}
