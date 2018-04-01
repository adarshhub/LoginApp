package com.youtube.android.Fragment1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
    private int PERMISSION_CODE= 2;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();

        verifyPermissions();
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

    private void verifyPermissions(){
        // verify camera and storage permissions
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[2]) == PackageManager.PERMISSION_GRANTED ){
            respond(intent.getIntExtra("binary",0));
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,permissions,PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}
