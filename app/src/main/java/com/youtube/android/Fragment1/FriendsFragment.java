package com.youtube.android.Fragment1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class FriendsFragment extends Fragment {

    RecyclerView recyclerView;
    private DatabaseReference mref,tempRef;
    private String currentUserUid;
    long totalFriends;
    private ProgressBar progressBar;
    TextView noFriendText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = view.findViewById(R.id.friends_recyclerView);
        noFriendText = view.findViewById(R.id.nofriendtext);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        currentUserUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mref = FirebaseDatabase.getInstance().getReference().child("UsersFriends").child(currentUserUid).child("friends");
        progressBar = view.findViewById(R.id.friends_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                totalFriends = dataSnapshot.getChildrenCount();
                if (totalFriends == 0) {
                    noFriendText.setText("No Friends");
                    noFriendText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                } else {
                    noFriendText.setText("");
                    noFriendText.setVisibility(View.INVISIBLE);
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
        mref.keepSynced(true);
        final FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String,FriendsViewHolder>(String.class
                ,R.layout.friends_row_layout
                ,FriendsViewHolder.class
                ,mref) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, final String model, final int position)
            {
                tempRef = FirebaseDatabase.getInstance().getReference().child("Users").child(model);
                tempRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        viewHolder.friendusername.setText(dataSnapshot.child("name").getValue().toString());
                        viewHolder.frienduserstatus.setText(dataSnapshot.child("status").getValue().toString());
                        if (dataSnapshot.hasChild("online"))
                        {
                            if (dataSnapshot.child("online").getValue().hashCode() == 1)
                                viewHolder.online.setVisibility(View.VISIBLE);
                            else
                                viewHolder.online.setVisibility(View.INVISIBLE);
                        }
                        else
                            viewHolder.online.setVisibility(View.INVISIBLE);
                        setPicture(viewHolder,model);
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent oneProfileIntent = new Intent(getActivity(), One_Profile.class);
                                oneProfileIntent.putExtra("UID", model);
                                startActivity(oneProfileIntent);
                            }
                        });
                        if (position == (totalFriends - 1))
                        {
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                
            }

        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    private void setPicture(final FriendsViewHolder viewHolder, final String inf)
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
                            .into(viewHolder.frienduserimage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
