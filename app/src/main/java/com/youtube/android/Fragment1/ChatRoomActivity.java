package com.youtube.android.Fragment1;


import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    private String otherUserUid,currentUsetUid;
    private int online=0;
    DatabaseReference currUserchatRef,otherUserchatRef;
    FloatingActionButton send;
    TextInputEditText inputEditText;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter firebaseadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        toolbar =  findViewById(R.id.chatroom_toolbar);
        recyclerView =  findViewById(R.id.recylerview_chatroom);
        send =  findViewById(R.id.sendfloatbutton);
        inputEditText = findViewById(R.id.sendMessage);
        setSupportActionBar(toolbar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        otherUserUid = getIntent().getStringExtra("UID");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentUsetUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        currUserchatRef =FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(currentUsetUid);
        otherUserchatRef =FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(otherUserUid);
        send.setOnClickListener(this);
        FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setTitle((String) dataSnapshot.child("name").getValue());
                if(dataSnapshot.hasChild("online"))
                    online =  dataSnapshot.child("online").getValue().hashCode();
                if (online == 1)
                    getSupportActionBar().setSubtitle("online");
                else
                    getSupportActionBar().setSubtitle("offline");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initRecyclerView();
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().trim().length() == 0)
                {
                    send.setImageResource(android.R.drawable.ic_menu_camera);
                }
                else
                    send.setImageResource(android.R.drawable.ic_menu_send);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void initRecyclerView(){

        currUserchatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(otherUserUid))
                {
                    firebaseadapter =new FirebaseRecyclerAdapter <MessageModel,MessageViewHolder>(MessageModel.class,
                        R.layout.message_row_layout,
                        MessageViewHolder.class,
                        currUserchatRef.child(otherUserUid))
                    {

                        @Override
                        protected void populateViewHolder(final MessageViewHolder viewHolder, MessageModel model, int position) {

                            viewHolder.message.setText(model.getMessage());
                            if(model.getGravity().equalsIgnoreCase("to"))
                            {
                                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUsetUid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        viewHolder.sender.setText(dataSnapshot.child("name").getValue().toString());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else
                            {

                                FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserUid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        viewHolder.sender.setText(dataSnapshot.child("name").getValue().toString());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }


                        }


                        @Override
                        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            return super.onCreateViewHolder(parent, viewType);
                        }
                    };
                    recyclerView.setAdapter(firebaseadapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        currUserchatRef.keepSynced(true);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendfloatbutton)
        {
            String message= inputEditText.getText().toString();
            if (TextUtils.isEmpty(message)){

            }
            else
            {
                DatabaseReference temp1,temp2;
                temp1=currUserchatRef.child(otherUserUid).push();
                temp1.child("message").setValue(message);
                temp1.child("gravity").setValue("to");
                temp2=otherUserchatRef.child(currentUsetUid).push();
                temp2.child("message").setValue(message);
                temp2.child("gravity").setValue("from");
                otherUserchatRef.child("from").child(currentUsetUid).setValue(currentUsetUid);
                inputEditText.setText("");
                firebaseadapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        else if (item.getItemId() == R.id.clearchat)
        {
            currUserchatRef.child(otherUserUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(getBaseContext(), "Chat Cleared", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatroom_menu,menu);
        return true;
    }

}
