package com.youtube.android.Fragment1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements Communicator {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef= null;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        respond(intent.getIntExtra("binary",0));
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("online");
                    if (mRef != null)
                        mRef.setValue(1);
                    Intent intent = new Intent(getBaseContext(),ChatActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // User is signed out
                    respond(0);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void respond(int data) {
        Login frag1 = new Login();
        Registration frag2 = new Registration();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (data==0)
            transaction.replace(R.id.rel,frag1,"login");
        else
            transaction.replace(R.id.rel,frag2,"registration");
        transaction.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

}
