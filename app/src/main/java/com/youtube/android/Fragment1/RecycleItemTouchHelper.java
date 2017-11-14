package com.youtube.android.Fragment1;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;


public class RecycleItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    FirebaseRecyclerAdapter adapter;


    public RecycleItemTouchHelper(int dragDirs, int swipeDirs, FirebaseRecyclerAdapter statusAdapter) {
        super(dragDirs, swipeDirs);
        this.adapter = statusAdapter;
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        this.adapter.getRef(viewHolder.getAdapterPosition()).removeValue();

    }


}
