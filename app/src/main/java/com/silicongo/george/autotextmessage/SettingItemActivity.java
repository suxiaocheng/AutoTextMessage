package com.silicongo.george.autotextmessage;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.silicongo.george.autotextmessage.DataSet.TextMsgInfoParcelable;
import com.silicongo.george.autotextmessage.Database.TextDbAdapter;
import com.silicongo.george.autotextmessage.setting.DayOfWeekPickerDialogFragment;
import com.silicongo.george.autotextmessage.setting.SettingMessageContent;
import com.silicongo.george.autotextmessage.setting.SettingPhoneNumberDialogFragment;
import com.silicongo.george.autotextmessage.setting.SettingSIMCard;
import com.silicongo.george.autotextmessage.setting.SettingTag;
import com.silicongo.george.autotextmessage.setting.TimePickerFragment;

import com.silicongo.george.autotextmessage.DataSet.TextMsgInfo;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingItemActivity extends AppCompatActivity implements ListView.OnItemClickListener,
        TimePickerFragment.NoticeDialogListener, DayOfWeekPickerDialogFragment.NoticeDialogListener,
        SettingPhoneNumberDialogFragment.NoticeDialogListener, SettingTag.NoticeDialogListener,
        SettingSIMCard.NoticeDialogListener, SettingMessageContent.NoticeDialogListener{

    private final static String TAG = "SettingItemActivity";
    public final static String SETTING_RESULT = "SettingItemActivity.RESULT";

    private String strSettingItem[];

    @Bind(R.id.lvSetting)
    ListView lvSetting;

    private SettingItemAdapter settingItemAdapter;

    @Bind(R.id.btConfirm)
    Button btConfirm;

    @OnClick(R.id.btConfirm)
    void settingConfirm() {
        /* Check if the data is valid or not */
        String str = TextMsgInfo.checkTextMsgInfoValid(textMsgInfo);
        if (str != null) {
            Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        } else {
            textMsgInfo.get(TextMsgInfo.ROW_ENABLE).set(true);
            TextMsgInfoParcelable textMsgInfoParcelable = new TextMsgInfoParcelable(textMsgInfo);
            Intent intent = new Intent();
            intent.putExtra(SETTING_RESULT, textMsgInfoParcelable);

            setResult(0x1, intent);
            finish();
        }
    }

    @Bind(R.id.btCancel)
    Button btCancel;

    @OnClick(R.id.btCancel)
    void settingCancel() {
        setResult(0x0);
        finish();
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

        if(getIntent() == null) {
            textMsgInfo = new TextMsgInfo();
        }else{
            TextMsgInfoParcelable textMsgInfoParcelable = getIntent().getParcelableExtra(SETTING_RESULT);
            if(textMsgInfoParcelable != null) {
                textMsgInfo = textMsgInfoParcelable.mData;
            }else{
                textMsgInfo = new TextMsgInfo();
            }
        }

        ButterKnife.bind(this);

        strSettingItem = new String[]{this.getResources().getString(R.string.setting_phone_number),
                this.getResources().getString(R.string.setting_time),
                this.getResources().getString(R.string.setting_day_of_week),
                this.getResources().getString(R.string.setting_message_content),
                this.getResources().getString(R.string.setting_repeatable),
                this.getResources().getString(R.string.setting_tag),
                this.getResources().getString(R.string.setting_sim_card)};

        settingItemAdapter = new SettingItemAdapter(this);
        lvSetting.setAdapter(settingItemAdapter);

        lvSetting.setOnItemClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public String getDisplayString(int position) {
        String strRet = "";
        if ((position >= 0) && (position < strSettingItem.length)) {
            switch (position) {
                case 0:
                    strRet = textMsgInfo.get(TextMsgInfo.ROW_PHONE_NUMBER).getString();
                    break;
                case 1:
                    int hour = textMsgInfo.get(TextMsgInfo.ROW_TIME_HOUR).getInt();
                    int minute = textMsgInfo.get(TextMsgInfo.ROW_TIME_MINUTE).getInt();
                    strRet = String.format("%d:%d", hour, minute);
                    break;
                case 2:
                    strRet = textMsgInfo.get(TextMsgInfo.ROW_WEEK_SUNDAY).getBool() ? "Sunday " : "";
                    strRet += textMsgInfo.get(TextMsgInfo.ROW_WEEK_MONDAY).getBool() ? "Monday " : "";
                    strRet += textMsgInfo.get(TextMsgInfo.ROW_WEEK_TUESDAY).getBool() ? "Tuesday " : "";
                    strRet += textMsgInfo.get(TextMsgInfo.ROW_WEEK_WEDNESDAY).getBool() ? "Wednesday " : "";
                    strRet += textMsgInfo.get(TextMsgInfo.ROW_WEEK_THURSDAY).getBool() ? "Thursday " : "";
                    strRet += textMsgInfo.get(TextMsgInfo.ROW_WEEK_FRIDAY).getBool() ? "Friday " : "";
                    strRet += textMsgInfo.get(TextMsgInfo.ROW_WEEK_SATURDAY).getBool() ? "Saturday " : "";

                    if (strRet.compareTo("") == 0) {
                        strRet = getResources().getString(R.string.week_data_empty).toString();
                    }
                    break;
                case 3:
                    strRet = textMsgInfo.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString();
                break;
                case 4:
                    strRet = textMsgInfo.get(TextMsgInfo.ROW_ID_REPEATABLE).getBool()?"Enable":"Disable";
                    break;
                case 5:
                    strRet = textMsgInfo.get(TextMsgInfo.ROW_TEXT_TAG).getString();
                    break;
                case 6:
                    strRet = Integer.toString(textMsgInfo.get(TextMsgInfo.ROW_SIM_CARD).getInt());
                    break;
                default:
                    strRet = "";
                    break;
            }
        }
        return strRet;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DialogFragment newFragment;
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                newFragment = new SettingPhoneNumberDialogFragment();
                bundle.putString(SettingPhoneNumberDialogFragment.PHONE_NUMBER, textMsgInfo.get(TextMsgInfo.ROW_PHONE_NUMBER).getString());
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "phonePicker");
                break;
            case 1:
                newFragment = new TimePickerFragment();
                bundle.putInt(TimePickerFragment.HOUR, textMsgInfo.get(TextMsgInfo.ROW_TIME_HOUR).getInt());
                bundle.putInt(TimePickerFragment.MINUTE, textMsgInfo.get(TextMsgInfo.ROW_TIME_MINUTE).getInt());
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "timePicker");
                break;
            case 2:
                newFragment = new DayOfWeekPickerDialogFragment();
                bundle.putBoolean(DayOfWeekPickerDialogFragment.SUNDAY, textMsgInfo.get(TextMsgInfo.ROW_WEEK_SUNDAY).getBool());
                bundle.putBoolean(DayOfWeekPickerDialogFragment.MONDAY, textMsgInfo.get(TextMsgInfo.ROW_WEEK_MONDAY).getBool());
                bundle.putBoolean(DayOfWeekPickerDialogFragment.TUESDAY, textMsgInfo.get(TextMsgInfo.ROW_WEEK_TUESDAY).getBool());
                bundle.putBoolean(DayOfWeekPickerDialogFragment.WEDNESDAY, textMsgInfo.get(TextMsgInfo.ROW_WEEK_WEDNESDAY).getBool());
                bundle.putBoolean(DayOfWeekPickerDialogFragment.THURSDAY, textMsgInfo.get(TextMsgInfo.ROW_WEEK_THURSDAY).getBool());
                bundle.putBoolean(DayOfWeekPickerDialogFragment.FRIDAY, textMsgInfo.get(TextMsgInfo.ROW_WEEK_FRIDAY).getBool());
                bundle.putBoolean(DayOfWeekPickerDialogFragment.SATURDAY, textMsgInfo.get(TextMsgInfo.ROW_WEEK_SATURDAY).getBool());
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "dayOfWeekPicker");
                break;
            case 3:
                newFragment = new SettingMessageContent();
                bundle.putString(SettingMessageContent.MESSAGE_CONTENT, textMsgInfo.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE+"0").getString());
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "settingMessageContent");
                break;
            case 4:
                textMsgInfo.get(TextMsgInfo.ROW_ID_REPEATABLE).set(!textMsgInfo.get(TextMsgInfo.ROW_ID_REPEATABLE).getBool());
                settingItemAdapter.notifyDataSetChanged();
                break;
            case 5:
                newFragment = new SettingTag();
                bundle.putString(SettingTag.SETTING_TAG, textMsgInfo.get(TextMsgInfo.ROW_TEXT_TAG).getString());
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "settingTag");
                break;
            case 6:
                newFragment = new SettingSIMCard();
                bundle.putInt(SettingSIMCard.SIM_CARD, textMsgInfo.get(TextMsgInfo.ROW_SIM_CARD).getInt());
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "settingSimCard");
                break;
            default:
                break;
        }
    }

    private class SettingItemAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

        /**
         * 存放控件
         */
        public final class ViewHolder {
            public TextView title;
            public TextView information;
        }

        /**
         * 构造函数
         */
        public SettingItemAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return strSettingItem.length;//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return strSettingItem[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * 书中详细解释该方法
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.content_setting_item, null);
                holder = new ViewHolder();
                /**得到各个控件的对象*/
                holder.title = (TextView) convertView.findViewById(R.id.tvTitle);
                holder.information = (TextView) convertView.findViewById(R.id.tvInformation);
                convertView.setTag(holder);//绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
            }
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.title.setText(strSettingItem[position]);
            holder.information.setText(getDisplayString(position));

            return convertView;
        }
    }

    public void onDialogPositiveClick(DialogFragment dialog, Intent intent) {
        if (dialog instanceof TimePickerFragment) {
            if (intent != null) {
                textMsgInfo.get(TextMsgInfo.ROW_TIME_HOUR).set(intent.getIntExtra(TimePickerFragment.HOUR, 12));
                textMsgInfo.get(TextMsgInfo.ROW_TIME_MINUTE).set(intent.getIntExtra(TimePickerFragment.MINUTE, 0));
            }
        } else if (dialog instanceof SettingPhoneNumberDialogFragment) {
            if (intent != null) {
                textMsgInfo.get(TextMsgInfo.ROW_PHONE_NUMBER).set(intent.getStringExtra(SettingPhoneNumberDialogFragment.PHONE_NUMBER));
            }
        } else if (dialog instanceof DayOfWeekPickerDialogFragment) {
            if (intent != null) {
                textMsgInfo.get(TextMsgInfo.ROW_WEEK_SUNDAY).set(intent.getBooleanExtra(DayOfWeekPickerDialogFragment.SUNDAY, false));
                textMsgInfo.get(TextMsgInfo.ROW_WEEK_MONDAY).set(intent.getBooleanExtra(DayOfWeekPickerDialogFragment.MONDAY, false));
                textMsgInfo.get(TextMsgInfo.ROW_WEEK_TUESDAY).set(intent.getBooleanExtra(DayOfWeekPickerDialogFragment.TUESDAY, false));
                textMsgInfo.get(TextMsgInfo.ROW_WEEK_WEDNESDAY).set(intent.getBooleanExtra(DayOfWeekPickerDialogFragment.WEDNESDAY, false));
                textMsgInfo.get(TextMsgInfo.ROW_WEEK_THURSDAY).set(intent.getBooleanExtra(DayOfWeekPickerDialogFragment.THURSDAY, false));
                textMsgInfo.get(TextMsgInfo.ROW_WEEK_FRIDAY).set(intent.getBooleanExtra(DayOfWeekPickerDialogFragment.FRIDAY, false));
                textMsgInfo.get(TextMsgInfo.ROW_WEEK_SATURDAY).set(intent.getBooleanExtra(DayOfWeekPickerDialogFragment.SATURDAY, false));
            }
        } else if(dialog instanceof SettingTag){
            if(intent!=null){
                textMsgInfo.get(TextMsgInfo.ROW_TEXT_TAG).set(intent.getStringExtra(SettingTag.SETTING_TAG));
            }
        } else if(dialog instanceof SettingSIMCard){
            if(intent!=null){
                textMsgInfo.get(TextMsgInfo.ROW_SIM_CARD).set(intent.getIntExtra(SettingSIMCard.SIM_CARD, 0));
            }
        }else if(dialog instanceof SettingMessageContent){
            if(intent!=null){
                textMsgInfo.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE+"0").set(intent.getStringExtra(SettingMessageContent.MESSAGE_CONTENT));
            }
        }
        settingItemAdapter.notifyDataSetChanged();
    }

    public void onDialogNegativeClick(DialogFragment dialog, Intent intent) {

    }
}
