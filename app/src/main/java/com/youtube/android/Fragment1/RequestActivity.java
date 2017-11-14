package com.youtube.android.Fragment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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


public class RequestActivity extends AppCompatActivity {

    RecyclerView  recyclerView_req;
    DatabaseReference mref,tempRef;
    String currentUserUid;
    Toolbar toolbar;
    long totalRequest;
    ProgressBar progressBar;
    TextView noRequestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        toolbar = (Toolbar) findViewById(R.id.request_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request");
        recyclerView_req = (RecyclerView) findViewById(R.id.request_recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.requestProgresBbar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView_req.setHasFixedSize(true);
        noRequestText= (TextView) findViewById(R.id.norequesttext);
        recyclerView_req.setLayoutManager(new LinearLayoutManager(this));
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mref = FirebaseDatabase.getInstance().getReference().child("UsersFriends").child(currentUserUid).child("requestReceived");

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                totalRequest =  dataSnapshot.getChildrenCount();
                if (totalRequest == 0)
                {
                    noRequestText.setText("No Request");
                    noRequestText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }
                else
                {
                    noRequestText.setText("");
                    noRequestText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            final FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String,MyViewHolder>(String.class
                    ,R.layout.users_row_layout
                    ,MyViewHolder.class
                    ,mref) {
                @Override
                protected void populateViewHolder(final MyViewHolder viewHolder, final String model, final int position) {

                    tempRef = FirebaseDatabase.getInstance().getReference().child("Users").child(model);
                    tempRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            viewHolder.allusername.setText(dataSnapshot.child("name").getValue().toString());
                            viewHolder.alluserstatus.setText(dataSnapshot.child("status").getValue().toString());
                            setPicture(viewHolder,model);
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent oneProfileIntent = new Intent(getBaseContext(), One_Profile.class);
                                    oneProfileIntent.putExtra("UID", model);
                                    startActivity(oneProfileIntent);
                                }
                            });
                            if (position == (totalRequest - 1))
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

        recyclerView_req.setAdapter(firebaseRecyclerAdapter);
    }


    private void setPicture(final MyViewHolder viewHolder, final String inf)
    {


        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child("thumbnail").getValue().toString().equalsIgnoreCase("default"))
                {
                    StorageReference mRef = FirebaseStorage.getInstance().getReference().child("thumbnails").child(inf+".jpg");
                    Glide.with(getBaseContext())
                            .using(new FirebaseImageLoader())
                            .load(mRef)
                            .placeholder(R.drawable.profiledefaulmale)
                            .into(viewHolder.alluserimage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
