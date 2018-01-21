package com.example.m16142.pulseoximeter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by m16142 on 7/4/2015.
 */
public class ClearDialogFragment extends DialogFragment {
    /**
     * The EditText field that will be cleared by the dialog.
     */
    private TextView mFieldToClear;
    /**
     * The Text that will be displayed in the dialog.
     */
    private String mDialogText;



    /**
     * Creates a new DialogFragment that will prompt the user to clear the desire EditText.
     * @param txtField  (EditText)  - reference to the EditText field that should be clear.
     * @param txtDialog (String)    - text that will appear in the dialog frame.
     */
    public ClearDialogFragment(final TextView txtField, final String txtDialog) {
        this.mFieldToClear = txtField;
        this.mDialogText = txtDialog;
    }

    public ClearDialogFragment(){

    }

    @Override
    public final Dialog onCreateDialog(final Bundle savedInstanceState) {
	        /* Use the Builder class for convenient dialog construction. */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mDialogText)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
						/* clear the output window */
                        mFieldToClear.setText("");


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
	                       /* User cancelled the dialog. */
                    }
                });
	        /* Create the AlertDialog object and return it. */
        return builder.create();
    }
}