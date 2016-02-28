package com.silicongo.george.autotextmessage;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.silicongo.george.autotextmessage.DataSet.TextMsgInfo;
import com.silicongo.george.autotextmessage.DataSet.TextMsgInfoParcelable;
import com.silicongo.george.autotextmessage.Database.TextDbAdapter;
import com.silicongo.george.autotextmessage.Misc.InfoService;
import com.silicongo.george.autotextmessage.setting.SettingItemActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements AutoTestMsgAdapter.AutoTextClickListener {
    public static final String TAG = "MainActivity";
    public static final String PREFS_NAME = "MyPrefsFile";

    public static final int INTENT_RESULT_ADD_ITEM = 0x0;
    public static final int INTENT_RESULT_EDIT_ITEM = 0x1;

    @Bind(R.id.rvListOfAutoMessage)
    public RecyclerView rvListOfAutoMessage;
    @Bind(R.id.btAddItem)
    public Button btAddItem;
    @Bind(R.id.btLogFile)
    public Button btLogFile;

    private AutoTestMsgAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<TextMsgInfo> mTextMsgInfoList = new ArrayList<>();

    @OnClick(R.id.btAddItem)
    void addAutoTextMsgItem() {
        Intent intent = new Intent(this, SettingItemActivity.class);
        startActivityForResult(intent, INTENT_RESULT_ADD_ITEM);
    }

    @OnClick(R.id.btLogFile)
    void openLogFile() {
    }

    private TextDbAdapter adapter;
    private int editPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        adapter = new TextDbAdapter(this);
        adapter.open();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvListOfAutoMessage.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rvListOfAutoMessage.setLayoutManager(mLayoutManager);

        updateDataSet();

        // specify an adapter (see also next example)
        mAdapter = new AutoTestMsgAdapter(mTextMsgInfoList, this);
        rvListOfAutoMessage.setAdapter(mAdapter);

        startAutoTextMsgService();
    }

    @Override
    public void onResume(){
        super.onResume();
        registerForContextMenu(rvListOfAutoMessage);
    }

    @Override
    public void onPause(){
        unregisterForContextMenu(rvListOfAutoMessage);
        super.onPause();
    }

    @Override
    public  void onDestroy(){
        adapter.close();
        super.onDestroy();
    }

    public void updateDataSet(){
        mTextMsgInfoList.clear();
        Cursor cursor = adapter.fetchAllTextMessages();
        if(cursor.moveToFirst()){
            mTextMsgInfoList.add(new TextMsgInfo(cursor));
            while(cursor.moveToNext()){
                mTextMsgInfoList.add(new TextMsgInfo(cursor));
            }
        }
        cursor.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case INTENT_RESULT_ADD_ITEM:
                if(resultCode != 0x0){
                    if(data != null){
                        TextMsgInfoParcelable textMsgInfoParcelable = data.getParcelableExtra(SettingItemActivity.SETTING_RESULT);
                        if(textMsgInfoParcelable != null){
                            mAdapter.add(mAdapter.getItemCount(), textMsgInfoParcelable.mData);
                            adapter.saveTextMsgInfo(textMsgInfoParcelable.mData);
                        }
                        adapter.fetchAllTextMessages();

                        startAutoTextMsgService();
                    }
                }
                break;
            case INTENT_RESULT_EDIT_ITEM:
                if(resultCode != 0x0){
                    if((editPosition != -1) && (data != null)){
                        TextMsgInfo textMsgInfo = mAdapter.remove(editPosition);
                        adapter.deleteTextMsgInfo(textMsgInfo.get(TextMsgInfo.ROW_ID).getInt());
                        TextMsgInfoParcelable textMsgInfoParcelable = data.getParcelableExtra(SettingItemActivity.SETTING_RESULT);
                        if(textMsgInfoParcelable != null){
                            textMsgInfoParcelable.mData.get(TextMsgInfo.ROW_ID).set(0x0);
                            mAdapter.add(mAdapter.getItemCount(), textMsgInfoParcelable.mData);
                            adapter.saveTextMsgInfo(textMsgInfoParcelable.mData);
                        }
                        adapter.fetchAllTextMessages();

                        startAutoTextMsgService();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public void onItemClick(View view,int postion){
        //Log.d(TAG, "click on : " + postion);
        if((postion >= 0) && (postion < mAdapter.getItemCount())){
            Intent intent = new Intent(this, SettingItemActivity.class);
            TextMsgInfoParcelable textMsgInfoParcelable = new TextMsgInfoParcelable(mAdapter.get(postion));
            intent.putExtra(SettingItemActivity.SETTING_RESULT, textMsgInfoParcelable);
            editPosition = postion;
            startActivityForResult(intent, INTENT_RESULT_EDIT_ITEM);
        }
    }
    public void onItemLongClick(View view,int postion){
        //Log.d(TAG, "long click on : " + postion);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.settingItemEdit:
                if((mAdapter.position >= 0) && (mAdapter.position < mAdapter.getItemCount())){
                    Intent intent = new Intent(this, SettingItemActivity.class);
                    TextMsgInfoParcelable textMsgInfoParcelable = new TextMsgInfoParcelable(mAdapter.get(mAdapter.position));
                    intent.putExtra(SettingItemActivity.SETTING_RESULT, textMsgInfoParcelable);
                    editPosition = mAdapter.position;
                    startActivityForResult(intent, INTENT_RESULT_EDIT_ITEM);
                }
                return true;
            case R.id.settingItemDelete:
                Log.d(TAG, "delete position: " + mAdapter.position);
                if((mAdapter.position >= 0) && (mAdapter.position < mAdapter.getItemCount())){
                    TextMsgInfo textMsgInfo = mAdapter.remove(mAdapter.position);
                    adapter.deleteTextMsgInfo(textMsgInfo.get(TextMsgInfo.ROW_ID).getInt());

                    startAutoTextMsgService();
                }
                return true;
            case R.id.settingItemDeleteAll:
                mAdapter.removeAll();
                adapter.deleteAllTextMsgInfo();
                startAutoTextMsgService();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void startAutoTextMsgService(){
        Intent intent = new Intent(this, AutoTextMsgService.class);
        intent.setAction(AutoTextMsgService.SERVICE_QUERY_TEXT_MESSAGE);
        startService(intent);
    }
}
