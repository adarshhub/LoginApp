package com.youtube.android.Fragment1;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class FriendsViewHolder extends RecyclerView.ViewHolder{

    ImageView frienduserimage,online;
    TextView friendusername,frienduserstatus;

    public FriendsViewHolder(final View itemView) {
        super(itemView);
        frienduserimage=itemView.findViewById(R.id.friends_userimage);
        friendusername= itemView.findViewById(R.id.friends_username);
        frienduserstatus = itemView.findViewById(R.id.friends_userstatus);
        online= itemView.findViewById(R.id.online_status);
    }
}
