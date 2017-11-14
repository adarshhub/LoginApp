package com.youtube.android.Fragment1;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class StatusFragment extends Fragment implements Total{

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView noStatusText;
    DatabaseReference mRef,tempRef;
    String currentUserUid;
    long newFriendsStatus=0;
    FirebaseRecyclerAdapter statusAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_status, container, false);
        recyclerView = view.findViewById(R.id.status_recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noStatusText = view.findViewById(R.id.nostatustext);
        noStatusText.setVisibility(View.INVISIBLE);
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar=view.findViewById(R.id.status_progressbar);
        mRef = FirebaseDatabase.getInstance().getReference().child("UsersStatus").child(currentUserUid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                newFriendsStatus =dataSnapshot.child("friends").getChildrenCount();
                initTotal(newFriendsStatus);
                if (newFriendsStatus == 0)
                {
                    noStatusText.setText("Nothing New");
                    noStatusText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    noStatusText.setText("");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        statusAdapter = new FirebaseRecyclerAdapter<String,StatusViewHolder> (String.class,
                R.layout.single_status_row,
                StatusViewHolder.class,
                mRef.child("friends")) {
            @Override
            protected void populateViewHolder(final StatusViewHolder viewHolder, String model, int position) {

                final String otherUserUid= getRef(position).getKey();
                Log.d("ADA",otherUserUid);
                tempRef = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserUid);
                tempRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        viewHolder.status.setText(dataSnapshot.child("status").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                setPicture(viewHolder,otherUserUid);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent commentIntent = new Intent(getActivity(),CommentsActivity.class);
                        commentIntent.putExtra("UID",otherUserUid);
                        startActivity(commentIntent);

                    }
                });
                if (newFriendsStatus == position + 1)
                {
                    progressBar.setVisibility(View.GONE);
                }

            }

        };
        recyclerView.setAdapter(statusAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelper = new RecycleItemTouchHelper(0,ItemTouchHelper.RIGHT,statusAdapter);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);

    }
    private void setPicture(final StatusViewHolder viewHolder, final String inf)
    {


        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child("thumbnail").getValue().toString().equalsIgnoreCase("default"))
                {
                    StorageReference mRef = FirebaseStorage.getInstance().getReference().child("thumbnails").child(inf+".jpg");
                    Glide.with(getActivity())
                            .using(new FirebaseImageLoader())
                            .load(mRef)
                            .placeholder(R.drawable.profiledefaulmale)
                            .into(viewHolder.image);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void initTotal(long t) {
        newFriendsStatus=t;
        Log.d("ADA",newFriendsStatus+"");
    }

}
