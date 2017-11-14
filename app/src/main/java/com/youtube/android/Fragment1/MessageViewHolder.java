package com.youtube.android.Fragment1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;



public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView message,sender;
    public MessageViewHolder(View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.messageText);
        sender = itemView.findViewById(R.id.message_sender);
    }
}
