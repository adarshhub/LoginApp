package com.youtube.android.Fragment1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Win8.1 on 10/27/2017.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder
{

    ImageView proImage;
    TextView comment;

    public CommentViewHolder(View itemView)
    {

        super(itemView);
        proImage=itemView.findViewById(R.id.commenters_userimage);
        comment = itemView.findViewById(R.id.commenters_text);
    }
}
