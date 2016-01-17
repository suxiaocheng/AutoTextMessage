package com.silicongo.george.autotextmessage.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.silicongo.george.autotextmessage.R;

/**
 * Created by suxch on 2016/1/16.
 */
public class SettingTag  extends DialogFragment {
    private EditText editText;

    private static final String TAG = "SettingTag";
    public static final String SETTING_TAG = "SettingTag.SETTING_TAG";

    private SettingTag mInstance;

    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Intent intent);
        public void onDialogNegativeClick(DialogFragment dialog, Intent intent);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.setting_tag, null);
        editText = (EditText) v.findViewById(R.id.etSettingTag);

        if (getArguments() != null) {
            editText.setText(getArguments().getString(SETTING_TAG));
        } else {
            editText.setText("");
        }

        mInstance = this;

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.setting_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra(SETTING_TAG, editText.getText().toString());
                        mListener.onDialogPositiveClick(mInstance, intent);
                    }
                })
                .setNegativeButton(R.string.setting_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SettingTag.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
