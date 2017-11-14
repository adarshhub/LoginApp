package com.youtube.android.Fragment1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StatusActivity extends AppCompatActivity implements View.OnClickListener {

    Button updatestatus;
    EditText status;
    private DatabaseReference mFrndRef,mref;
    private String currentUserUid,tempUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        updatestatus = (Button) findViewById(R.id.change_status_confirm);
        status = (EditText) findViewById(R.id.status_updatetext);
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFrndRef = FirebaseDatabase.getInstance().getReference().child("UsersFriends").child(currentUserUid).child("friends");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        mref = database.getReference().child("Users").child(currentuser.getUid());
        updatestatus.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        mref.child("status").setValue(status.getText().toString());
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("UsersComments");
        tempRef.child(currentUserUid).removeValue();
        mFrndRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    tempUid = data.getValue().toString();
                    FirebaseDatabase.getInstance().getReference().child("UsersStatus")
                            .child(tempUid)
                            .child("friends")
                            .child(currentUserUid).setValue("unseen");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Toast.makeText(getApplicationContext(),"status updated",Toast.LENGTH_SHORT).show();
        finish();

    }
}
