package com.bot.ping.ui.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;

public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout leftMsgLayout;
    public ConstraintLayout rightMsgLayout;
    public TextView leftMsgTextView, chat_left_time_text_view;
    public TextView rightMsgTextView, chat_right_time_text_view;
    public TextView dateTextView;
    public ChatAppMsgViewHolder(View itemView) {
        super(itemView);
        if(itemView!=null) {
            leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_left_msg_layout);
            leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
            chat_left_time_text_view = (TextView) itemView.findViewById(R.id.chat_left_time_text_view);

            rightMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_right_msg_layout);
            rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
            chat_right_time_text_view = (TextView) itemView.findViewById(R.id.chat_right_time_text_view);

            dateTextView = itemView.findViewById(R.id.textViewData);

        }
    }
}