package com.youtube.android.Fragment1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Win8.1 on 10/25/2017.
 */

public class StatusViewHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView status;
    public StatusViewHolder(View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.status_userimage);
        status = itemView.findViewById(R.id.status_text);
    }
}
