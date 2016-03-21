package com.apps.andrew.lifelinker;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ChangeNameDialogFragment extends DialogFragment {

    private EditText editText;
    private String name = "";

    static ChangeNameDialogFragment getNewInstance()
    {
        ChangeNameDialogFragment temp = new ChangeNameDialogFragment();
        return temp;
    }

    private void sendResult(int resultCode){
        if(getTargetFragment()== null)
            return;

        Intent i = new Intent();
        i.putExtra("name", name);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    public static ChangeNameDialogFragment newInstance(){

        ChangeNameDialogFragment fragment = new ChangeNameDialogFragment();

        return  fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.name_dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.change_name_dialog, null);

        editText =(EditText) view.findViewById(R.id.userName);

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = editText.getText().toString();
                        if(!name.isEmpty())
                            sendResult(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}

