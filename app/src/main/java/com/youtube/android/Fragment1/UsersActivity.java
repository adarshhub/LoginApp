package com.youtube.android.Fragment1;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class UsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference mref;
    StorageReference mStorageRef;
    Toolbar toolbar;
    long totalUser;
    ProgressBar mprogressbar;
    int refresh=1,limit=10;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        toolbar = (Toolbar) findViewById(R.id.users_toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mprogressbar = (ProgressBar) findViewById(R.id.users_progressbar);
        mprogressbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("USERS");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mref = FirebaseDatabase.getInstance().getReference().child("Users");

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                totalUser = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query limitreference = mref.limitToFirst(limit);
        final FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserInformation,MyViewHolder>(UserInformation.class
                ,R.layout.users_row_layout
                ,MyViewHolder.class
                ,limitreference) {



            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final UserInformation model, int position) {



                if(model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    viewHolder.allusername.setText("YOU");

                }
                else {
                    viewHolder.allusername.setText(model.getName());
                    viewHolder.alluserstatus.setText(model.getStatus());
                    setpicture(viewHolder, model);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent oneProfileIntent = new Intent(getBaseContext(), One_Profile.class);
                            oneProfileIntent.putExtra("UID", model.getUid());
                            startActivity(oneProfileIntent);

                        }
                    });
                }


                if (position== totalUser-1)
                {
                    mprogressbar.setVisibility(View.GONE);
                }
            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh++;
                if(limit-9 < totalUser) {
                    limit = refresh * 10;

                }
                refrehLayout();
            }
        });

    }


    public void setpicture(MyViewHolder VH,UserInformation information)
    {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference mRef = mStorageRef.child("thumbnails").child(information.getThumbnail());
        if (!information.getThumbnail().equals("default"))
        {
            Glide.with(getBaseContext())
                    .using(new FirebaseImageLoader())
                    .load(mRef)
                    .placeholder(R.drawable.profiledefaulmale)
                    .into(VH.alluserimage);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refrehLayout()
    {
        if (refresh == 1)
            recyclerView.scrollToPosition(0);
        else
            recyclerView.scrollToPosition(limit);
        swipeRefreshLayout.setRefreshing(false);
    }

}
