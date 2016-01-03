package com.silicongo.george.autotextmessage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.FormatFlagsConversionMismatchException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingItemActivity extends AppCompatActivity {

    @Bind(R.id.btSettingPhoneNumber)
    Button btSettingPhoneNumber;
    @OnClick(R.id.btSettingPhoneNumber)
    void settingPhoneNumber() {
        DialogFragment newFragment = new SettingPhoneNumberDialogFragment();
        newFragment.show(getFragmentManager(), "phonePicker");
    }
    @Bind(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;
    @Bind(R.id.btDayOfWeek)
    Button btDayOfWeek;
    @OnClick(R.id.btDayOfWeek)
    void settingDayOfWeek() {
        DialogFragment newFragment = new DayOfWeekPickerDialogFragment();
        newFragment.show(getFragmentManager(), "dayOfWeekPicker");
    }
    @Bind(R.id.tvDayofWeek)
    TextView tvDayofWeek;
    @Bind(R.id.btTime)
    Button btTime;
    @OnClick(R.id.btTime)
    void settingTime() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    @Bind(R.id.tvTime)
    TextView tvTime;
    @Bind(R.id.btMessageContent)
    Button btMessageContent;
    @OnClick(R.id.btMessageContent)
    void settingMessageContent() {
        textMsgInfo.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE+"0").set("Text Only");
        updateDisplayUI();
    }
    @Bind(R.id.tvMessageContent)
    TextView tvMessageContent;
    @Bind(R.id.btSimCard)
    Button btSimCard;
    @OnClick(R.id.btSimCard)
    void settingSimCard() {
        updateDisplayUI();
    }
    @Bind(R.id.tvSimCard)
    TextView tvSimCard;
    @Bind(R.id.btTag)
    Button btTag;
    @OnClick(R.id.btTag)
    void settingTag() {
        updateDisplayUI();
    }
    @Bind(R.id.tvTag)
    TextView tvTag;
    @Bind(R.id.cbRepeatable)
    CheckBox cbRepeatable;
    @OnClick(R.id.cbRepeatable)
    void settingRepeatable() {
        textMsgInfo.get(TextMsgInfo.ROW_ID_REPEATABLE).set(cbRepeatable.isChecked());
        updateDisplayUI();
    }
    @Bind(R.id.btConfirm)
    Button btConfirm;
    @OnClick(R.id.btConfirm)
    void settingConfirm() {
        /* Check if the data is valid or not */
        String str = TextMsgInfo.checkTextMsgInfoValid(textMsgInfo);
        if(str != null){
            Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        }else{
            TextDbAdapter db = new TextDbAdapter(this);
            db.open();

            db.saveTextMsgInfo(textMsgInfo);

            db.close();
            finish();
        }
    }
    @Bind(R.id.btCancel)
    Button btCancel;
    @OnClick(R.id.btCancel)
    void settingCancel() {
    }

    /* Data */
    private static TextMsgInfo textMsgInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        textMsgInfo = new TextMsgInfo();

        ButterKnife.bind(this);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateDisplayUI();
    }

    public void updateDisplayUI(){
        tvPhoneNumber.setText(textMsgInfo.get(TextMsgInfo.ROW_PHONE_NUMBER).getString());

        int hour = textMsgInfo.get(TextMsgInfo.ROW_TIME_HOUR).getInt();
        int minute = textMsgInfo.get(TextMsgInfo.ROW_TIME_MINUTE).getInt();
        String str = String.format("%d:%d", hour, minute);
        tvTime.setText(str);

        str = textMsgInfo.get(TextMsgInfo.ROW_WEEK_SUNDAY).getBool()?"Sunday ":"";
        str += textMsgInfo.get(TextMsgInfo.ROW_WEEK_MONDAY).getBool()?"Monday ":"";
        str += textMsgInfo.get(TextMsgInfo.ROW_WEEK_TUESDAY).getBool()?"Tuesday ":"";
        str += textMsgInfo.get(TextMsgInfo.ROW_WEEK_WEDNESDAY).getBool()?"Wednesday ":"";
        str += textMsgInfo.get(TextMsgInfo.ROW_WEEK_THURSDAY).getBool()?"Thursday ":"";
        str += textMsgInfo.get(TextMsgInfo.ROW_WEEK_FRIDAY).getBool()?"Friday ":"";
        str += textMsgInfo.get(TextMsgInfo.ROW_WEEK_SATURDAY).getBool()?"Saturday ":"";

        if(str.compareTo("") != 0) {
            tvDayofWeek.setText(str);
        }else{
            tvDayofWeek.setText(R.string.week_data_empty);
        }

        tvMessageContent.setText(textMsgInfo.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE+"0").getString());

        tvSimCard.setText(Integer.toString(textMsgInfo.get(TextMsgInfo.ROW_SIM_CARD).getInt()));
        tvTag.setText(textMsgInfo.get(TextMsgInfo.ROW_TEXT_TAG).getString());
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = textMsgInfo.get(TextMsgInfo.ROW_TIME_HOUR).getInt();
            int minute = textMsgInfo.get(TextMsgInfo.ROW_TIME_MINUTE).getInt();

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            textMsgInfo.get(TextMsgInfo.ROW_TIME_HOUR).set(hourOfDay);
            textMsgInfo.get(TextMsgInfo.ROW_TIME_MINUTE).set(minute);
        }
    }

    public static class SettingPhoneNumberDialogFragment extends DialogFragment {
        private EditText editText;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View v = inflater.inflate(R.layout.setting_phone_number_dialog, null);
            editText = (EditText)v.findViewById(R.id.etPhoneNumber);
            editText.setText(textMsgInfo.get(TextMsgInfo.ROW_PHONE_NUMBER).getString());

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(v)
                    // Add action buttons
                    .setPositiveButton(R.string.setting_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            textMsgInfo.get(TextMsgInfo.ROW_PHONE_NUMBER).set(editText.getText().toString());
                        }
                    })
                    .setNegativeButton(R.string.setting_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SettingPhoneNumberDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }

    public static class DayOfWeekPickerDialogFragment extends DialogFragment {
        boolean[] initStatus = new boolean[7];

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            initStatus[0] = textMsgInfo.get(TextMsgInfo.ROW_WEEK_SUNDAY).getBool();
            initStatus[1] = textMsgInfo.get(TextMsgInfo.ROW_WEEK_MONDAY).getBool();
            initStatus[2] = textMsgInfo.get(TextMsgInfo.ROW_WEEK_TUESDAY).getBool();
            initStatus[3] = textMsgInfo.get(TextMsgInfo.ROW_WEEK_WEDNESDAY).getBool();
            initStatus[4] = textMsgInfo.get(TextMsgInfo.ROW_WEEK_THURSDAY).getBool();
            initStatus[5] = textMsgInfo.get(TextMsgInfo.ROW_WEEK_FRIDAY).getBool();
            initStatus[6] = textMsgInfo.get(TextMsgInfo.ROW_WEEK_SATURDAY).getBool();

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
                            textMsgInfo.get(TextMsgInfo.ROW_WEEK_SUNDAY).set(initStatus[0]);
                            textMsgInfo.get(TextMsgInfo.ROW_WEEK_MONDAY).set(initStatus[1]);
                            textMsgInfo.get(TextMsgInfo.ROW_WEEK_TUESDAY).set(initStatus[2]);
                            textMsgInfo.get(TextMsgInfo.ROW_WEEK_WEDNESDAY).set(initStatus[3]);
                            textMsgInfo.get(TextMsgInfo.ROW_WEEK_THURSDAY).set(initStatus[4]);
                            textMsgInfo.get(TextMsgInfo.ROW_WEEK_FRIDAY).set(initStatus[5]);
                            textMsgInfo.get(TextMsgInfo.ROW_WEEK_SATURDAY).set(initStatus[6]);
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
}
