package com.youtube.android.Fragment1;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class One_Profile extends AppCompatActivity implements View.OnClickListener {

    private String uid,currentUserUid;
    private DatabaseReference mRef,mRequestSent,mFriends,mRequestReceived,mLikeSent,mUserFriends,mLikeReceived;
    private ImageView profile_picture;
    private TextView name,status,totalfriends,totallikes;
    Button addfriend,like;
    private FirebaseUser mcurrentUser;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one__profile);
        uid=getIntent().getStringExtra("UID");
        mRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        profile_picture = (ImageView) findViewById(R.id.profile_picture);
        progressBar = (ProgressBar) findViewById(R.id.one_progressbar);
        name = (TextView) findViewById(R.id.one_profile_name);
        status = (TextView) findViewById(R.id.one_profile_status);
        totalfriends = (TextView) findViewById(R.id.totalfriends);
        totallikes = (TextView) findViewById(R.id.totallikes);
        like = (Button) findViewById(R.id.like);
        addfriend = (Button) findViewById(R.id.addfriend);
        mcurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        currentUserUid = mcurrentUser.getUid();
        mUserFriends =FirebaseDatabase.getInstance().getReference().child("UsersFriends");
        mLikeSent= mUserFriends.child(currentUserUid).child("likesSent");
        mLikeReceived=mUserFriends.child(currentUserUid).child("likeReceived");
        mRequestReceived=mUserFriends.child(currentUserUid).child("requestReceived");
        mFriends =  mUserFriends.child(currentUserUid).child("friends");
        mRequestSent = mUserFriends.child(currentUserUid).child("requestSent");

        progressBar.setVisibility(View.VISIBLE);

        addfriend.setOnClickListener(this);
        like.setOnClickListener(this);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name.setText(dataSnapshot.child("name").getValue().toString());
                status.setText(dataSnapshot.child("status").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("image").getValue().toString().equalsIgnoreCase("default"))
                {
                    Glide.with(getBaseContext())
                            .using(new FirebaseImageLoader())
                            .load(FirebaseStorage.getInstance().getReference().child("images").child(uid+".jpg"))
                            .listener(new RequestListener<StorageReference, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.drawable.profiledefaulmale)
                            .into(profile_picture);

                }
                else
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mUserFriends.child(uid).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                totalfriends.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserFriends.child(uid).child("likesReceived").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                totallikes.setText( dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mLikeSent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean flag=false;

                if (dataSnapshot != null)
                {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (uid.equals(data.getKey())) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    like.setText(getResources().getText(R.string.unlike));
                }
                else
                    like.setText(getResources().getText(R.string.like));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean flag=false;

                if (dataSnapshot != null)
                {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (uid.equals(data.getKey())) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    addfriend.setText(getResources().getText(R.string.message));
                }
                else
                {
                    mRequestSent.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Boolean flag=false;

                            if (dataSnapshot != null)
                            {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    if (uid.equals(data.getKey())) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            if (flag) {
                                addfriend.setText(getResources().getText(R.string.request_sent));
                            }
                            else
                            {
                                mRequestReceived.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Boolean flag=false;

                                        if (dataSnapshot != null)
                                        {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                if (uid.equals(data.getKey())) {
                                                    flag = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            addfriend.setText(getResources().getText(R.string.respond));
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    public void showPopupMenu()
    {
        PopupMenu popupMenu = new PopupMenu(this,addfriend);
        popupMenu.getMenuInflater().inflate(R.menu.request_respond_popup,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.acceptRequest)
                {
                    mFriends.child(uid).setValue(uid);
                    mUserFriends.child(uid).child("friends").child(currentUserUid).setValue(currentUserUid);
                    mRequestReceived.child(uid).removeValue();
                    mUserFriends.child(uid).child("requestSent").child(currentUserUid).removeValue();
                }
                else
                {
                    mRequestReceived.child(uid).removeValue();
                    mUserFriends.child(uid).child("requestSent").child(currentUserUid).removeValue();
                }
                Snackbar.make(findViewById(R.id.one_profile_parent_relative)," Successfull ",Snackbar.LENGTH_SHORT)
                        .setAction("Go Back", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onBackPressed();
                            }
                        }).show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.addfriend:
                if (addfriend.getText().toString().equalsIgnoreCase("add friend"))
                {
                    mRequestSent.child(uid).setValue(uid);
                    mUserFriends.child(uid)
                            .child("requestReceived")
                            .child(currentUserUid).setValue(currentUserUid);

                }
                else if (addfriend.getText().toString().equalsIgnoreCase("cancel request"))
                {
                    mRequestSent.child(uid).removeValue();
                    mUserFriends.child(uid)
                            .child("requestReceived")
                            .child(currentUserUid).removeValue();
                }
                else if(addfriend.getText().toString().equalsIgnoreCase("Respond"))
                {
                    showPopupMenu();
                }
                else if (addfriend.getText().toString().equalsIgnoreCase("Message"))
                {
                    Intent chatroomIntent = new Intent(this,ChatRoomActivity.class);
                    chatroomIntent.putExtra("UID",uid);
                    startActivity(chatroomIntent);
                    finish();
                }
                break;
            case R.id.like:
                if (like.getText().toString().equalsIgnoreCase("like"))
                {
                    mLikeSent.child(uid).setValue(uid);
                    mUserFriends.child(uid).child("likesReceived").child(currentUserUid).setValue(currentUserUid);
                }
                else
                {
                    mLikeSent.child(uid).removeValue();
                    mUserFriends.child(uid).child("likesReceived").child(currentUserUid).removeValue();
                }
                break;

        }

    }
}
