package com.youtube.android.Fragment1;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView alluserimage;
    TextView allusername,alluserstatus;

    public MyViewHolder(final View itemView) {
        super(itemView);
        alluserimage=itemView.findViewById(R.id.alluserimage);
        allusername= itemView.findViewById(R.id.allusername);
        alluserstatus = itemView.findViewById(R.id.alluserstatus);

    }
}
