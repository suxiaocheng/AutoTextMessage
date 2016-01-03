package com.silicongo.george.autotextmessage;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.rvListOfAutoMessage)
    public RecyclerView rvListOfAutoMessage;
    @Bind(R.id.btAddItem)
    public Button btAddItem;
    @Bind(R.id.btLogFile)
    public Button btLogFile;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public TextMsgInfo[] mTextMsgInfoList;

    @OnClick(R.id.btAddItem)
    void addAutoTextMsgItem() {
        Intent intent = new Intent(this, SettingItemActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btLogFile)
    void openLogFile() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvListOfAutoMessage.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rvListOfAutoMessage.setLayoutManager(mLayoutManager);

        updateDataSet();

        // specify an adapter (see also next example)
        mAdapter = new AutoTestMsgAdapter(mTextMsgInfoList);
        rvListOfAutoMessage.setAdapter(mAdapter);
    }

    public void updateDataSet(){
        TextDbAdapter adapter = new TextDbAdapter(this);
        adapter.open();

        Cursor cursor = adapter.fetchAllTextMessages();
        if(cursor.moveToFirst()){
            int count=0;
            mTextMsgInfoList = new TextMsgInfo[cursor.getCount()];
            mTextMsgInfoList[count++] = new TextMsgInfo(cursor);
            while(cursor.moveToNext()){
                mTextMsgInfoList[count++] = new TextMsgInfo(cursor);
            }
        }else{
            mTextMsgInfoList = new TextMsgInfo[0];
        }
        cursor.close();
        adapter.close();
    }
}
