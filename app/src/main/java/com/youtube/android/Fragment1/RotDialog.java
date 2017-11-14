package com.youtube.android.Fragment1;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;


import com.victor.loading.rotate.RotateLoading;

public class RotDialog extends DialogFragment {


    RotateLoading rotateLoading;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view =getActivity().getLayoutInflater().inflate(R.layout.dialog_loading,null);
        rotateLoading = view.findViewById(R.id.rotateloading);
        builder.setView(view);
        setCancelable(false);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        rotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.start();
    }

    @Override
    public void onStop() {
        rotateLoading.stop();
        rotateLoading.setVisibility(View.INVISIBLE);
        super.onStop();
    }

}
