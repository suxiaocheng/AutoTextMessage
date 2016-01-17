package com.silicongo.george.autotextmessage.setting;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TimePicker;

/**
 * Created by suxch on 2016/1/16.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "TimePickerFragment";
    public static final String HOUR = "TimePickerFragment.HOUR";
    public static final String MINUTE = "TimePickerFragment.MINUTE";

    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Intent intent);
        public void onDialogNegativeClick(DialogFragment dialog, Intent intent);
    }
    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour, minute;
        // Use the current time as the default values for the picker
        if (getArguments() != null) {
            hour = getArguments().getInt(HOUR);
            minute = getArguments().getInt(MINUTE);
        } else {
            hour = 12;
            minute = 0;
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }

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


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Intent intent = new Intent();
        intent.putExtra(HOUR, hourOfDay);
        intent.putExtra(MINUTE, minute);
        mListener.onDialogPositiveClick(this, intent);
    }
}
