package com.youtube.android.Fragment1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Registration extends android.support.v4.app.Fragment {

    private TextView tologin;
    Communicator comm;
    private EditText name;
    private EditText email,password2,password1;
    private Button signup;
    private String NAME,EMAIL,PASSWORD;
    private UserInformation userInformation;
    private FirebaseAuth mAuth;
    RotDialog rotDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registration_layout,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        tologin = getActivity().findViewById(R.id.tologin);
        name = getActivity().findViewById(R.id.nameText);
        email = getActivity().findViewById(R.id.emailText);
        password1 = getActivity().findViewById(R.id.password1);
        rotDialog =  new RotDialog();
        password2 = getActivity().findViewById(R.id.password2);
        signup = getActivity().findViewById(R.id.signup);
        comm= (Communicator) getActivity();
        mAuth =FirebaseAuth.getInstance();
        super.onActivityCreated(savedInstanceState);
        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                comm.respond(0);
            }
        });

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotDialog.show(getFragmentManager(),"progress");
                    storedata();
                }

            });



    }

    public boolean checkname(String name){

        int c=0;
        for(int i=0;i<name.length();i++){
            char ch=name.charAt(i);
            if(!((ch>='A' && ch<='Z') || (ch>='a' && ch<='z') || ch==' '))
            {
                c=1;
                break;
            }
        }
        return c == 0;

    }

    public void registeruser(){

        mAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(mCurrentUser != null) {
                        String uid = mCurrentUser.getUid();
                        userInformation.setUid(uid);
                        mref.child("Users").child(uid).setValue(userInformation);
                        Intent regsucessintent = new Intent(getContext(), ChatActivity.class);
                        regsucessintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        rotDialog.dismiss();
                        startActivity(regsucessintent);
                    }

                } else {
                    rotDialog.dismiss();
                    Toast.makeText(getContext(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    public void storedata()
    {
        NAME = name.getText().toString();
        EMAIL = email.getText().toString();
        PASSWORD = password1.getText().toString();


        if (PASSWORD.equals(password2.getText().toString()) && !TextUtils.isEmpty(NAME) && !TextUtils.isEmpty(EMAIL)) {

            if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {

                if (checkname(NAME)) {

                    userInformation=new UserInformation(NAME,EMAIL,PASSWORD);
                    registeruser();

                } else {
                    name.setError("Enter appropiate name");
                    rotDialog.dismiss();
                }
            } else {
                email.setError("Please enter valid email");
                rotDialog.dismiss();
            }
        } else {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            rotDialog.dismiss();
        }
    }


}
