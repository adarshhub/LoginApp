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


public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView noMessageText;
    DatabaseReference mRef,tempRef;
    String currentUserUid;
    long totalMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        progressBar = view.findViewById(R.id.chat_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        noMessageText = view.findViewById(R.id.nomessagetext);
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(currentUserUid).child("from");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                totalMessages = dataSnapshot.getChildrenCount();
                if (totalMessages == 0)
                {
                    noMessageText.setText("No Message");
                    progressBar.setVisibility(View.GONE);
                    noMessageText.setVisibility(View.VISIBLE);
                }
                else {
                    noMessageText.setText("");
                    noMessageText.setVisibility(View.INVISIBLE);
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
        final FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String,FriendsViewHolder>(String.class
                ,R.layout.friends_row_layout
                ,FriendsViewHolder.class
                ,mRef) {
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

                                Intent chatroomIntent = new Intent(getActivity(),ChatRoomActivity.class);
                                chatroomIntent.putExtra("UID",model);
                                startActivity(chatroomIntent);

                            }
                        });
                        if (position == (totalMessages - 1))
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
