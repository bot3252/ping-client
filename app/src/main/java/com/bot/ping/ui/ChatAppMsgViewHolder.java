package com.bot.ping.ui;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;

public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout leftMsgLayout;
    public ConstraintLayout rightMsgLayout;
    public TextView leftMsgTextView;
    public TextView rightMsgTextView;
    public ChatAppMsgViewHolder(View itemView) {
        super(itemView);
        if(itemView!=null) {
            leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_right_msg_layout);
            leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
            rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
        }
    }
}