package com.youtube.android.Fragment1;


import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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


public class CommentsActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView mainUserImage,currentUserImage;
    TextView mainUserStatus,noCommentsText,mainUserName;
    private DatabaseReference otherRef,commentsRef;
    private String otherUserUid,currentUserUid;
    RecyclerView recyclerView;
    TextInputEditText commentText;
    ImageButton doComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        otherUserUid = getIntent().getStringExtra("UID");
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mainUserImage = (ImageView) findViewById(R.id.comment_main_userimage);
        mainUserStatus = (TextView) findViewById(R.id.comment_main_status);
        noCommentsText = (TextView) findViewById(R.id.nocommentsText);
        mainUserName = (TextView) findViewById(R.id.comment_main_username);
        commentText = (TextInputEditText) findViewById(R.id.commentText);
        doComment = (ImageButton) findViewById(R.id.commentbutton);
        doComment.setOnClickListener(this);
        commentsRef = FirebaseDatabase.getInstance().getReference().child("UsersComments").child(otherUserUid);
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0)
                {
                    noCommentsText.setText("No New Comments");
                    noCommentsText.setVisibility(View.VISIBLE);
                }
                else
                {
                    noCommentsText.setText("");
                    noCommentsText.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        currentUserImage = (ImageView) findViewById(R.id.comment_curruserimage);
        otherRef = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserUid);
        setActivityPicture(otherUserUid,mainUserImage);
        setActivityPicture(currentUserUid,currentUserImage);
        otherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainUserStatus.setText(dataSnapshot.child("status").getValue().toString());
                mainUserName.setText(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().trim().length() ==0)
                    doComment.setEnabled(false);
                else
                    doComment.setEnabled(true);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter commentAdapter = new FirebaseRecyclerAdapter<String,CommentViewHolder>(String.class,
                R.layout.comment_single_row,
                CommentViewHolder.class,
                commentsRef) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, String model, int position) {

                String commenterUserId = getRef(position).getKey();
                viewHolder.comment.setText(model);
                setActivityPicture(commenterUserId,viewHolder.proImage);

            }
        };
        recyclerView.setAdapter(commentAdapter);
    }

    private void setActivityPicture(String id,ImageView into)
    {
        Glide.with(this).using(new FirebaseImageLoader())
                .load(FirebaseStorage.getInstance().getReference().child("thumbnails").child(id+".jpg"))
                .placeholder(ContextCompat.getDrawable(this,R.drawable.profiledefaulmale))
                .into(into);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.commentbutton)
        {
            String text = commentText.getText().toString();
            if (!TextUtils.isEmpty(text))
            {
                commentsRef.child(currentUserUid).setValue(text);
                commentText.setText("");
            }
        }
    }
}
