package com.youtube.android.Fragment1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
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


public class Login extends android.support.v4.app.Fragment {

    private TextView toregistration;
    Communicator comm;
    private EditText EMAIL,PASSWORD;
    private FirebaseAuth mAuth;
    Button submitbtn;
    RotDialog rotateRotDialog;
    FragmentManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        toregistration = getActivity().findViewById(R.id.toregistration);
        EMAIL=getActivity().findViewById(R.id.editText2);
        PASSWORD=getActivity().findViewById(R.id.editText3);
        comm = (Communicator) getActivity();
        submitbtn = getActivity().findViewById(R.id.submitbtn);
        rotateRotDialog = new RotDialog();
        manager = getFragmentManager();
        super.onActivityCreated(savedInstanceState);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showDialog();
                if(TextUtils.isEmpty(EMAIL.getText()) || TextUtils.isEmpty(PASSWORD.getText()))
                    Toast.makeText(getContext(),"empty fields",Toast.LENGTH_SHORT).show();
                else {


                    mAuth.signInWithEmailAndPassword(EMAIL.getText().toString(),PASSWORD.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            rotateRotDialog.dismiss();

                            if(!task.isSuccessful())
                                Toast.makeText(getActivity(), "login failed", Toast.LENGTH_SHORT).show();
                            else{

                                startActivity(new Intent(getActivity(),ChatActivity.class));
                                getActivity().finish();
                            }
                        }
                    });
                }

            }
        });
        toregistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comm.respond(1);
            }
        });
    }

    public void showDialog()
    {

        rotateRotDialog.show(manager,"progress");
    }





}
