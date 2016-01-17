package com.silicongo.george.autotextmessage.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.silicongo.george.autotextmessage.R;

/**
 * Created by suxch on 2016/1/16.
 */
public class DayOfWeekPickerDialogFragment extends DialogFragment {
    private static final String TAG = "DayOfWeekPickerDialogFragment";

    public static final String SUNDAY = "DayOfWeekPickerDialogFragment.SUNDAY";
    public static final String MONDAY = "DayOfWeekPickerDialogFragment.MONDAY";
    public static final String TUESDAY = "DayOfWeekPickerDialogFragment.TUESDAY";
    public static final String WEDNESDAY = "DayOfWeekPickerDialogFragment.WEDNESDAY";
    public static final String THURSDAY = "DayOfWeekPickerDialogFragment.THURSDAY";
    public static final String FRIDAY = "DayOfWeekPickerDialogFragment.FRIDAY";
    public static final String SATURDAY = "DayOfWeekPickerDialogFragment.SATURDAY";

    private boolean[] initStatus = new boolean[7];
    private String[] stringsDayOfWeek = {SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};
    private DayOfWeekPickerDialogFragment mInstance;

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

        int count = 0;
        if(getArguments() != null){
            for(String str:stringsDayOfWeek){
                initStatus[count] = getArguments().getBoolean(str);
                count++;
            }
        }else{
            for(count=0; count<initStatus.length; count++){
                initStatus[count] = false;
            }
        }

        mInstance = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.setting_day_of_week)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.day_of_week, initStatus,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                initStatus[which] = isChecked;
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.setting_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        Intent intent = new Intent();

                        int count = 0;
                        for(String str:stringsDayOfWeek){
                            intent.putExtra(str, initStatus[count]);
                            count++;
                        }
                        mListener.onDialogPositiveClick(mInstance, intent);
                    }
                })
                .setNegativeButton(R.string.setting_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}
