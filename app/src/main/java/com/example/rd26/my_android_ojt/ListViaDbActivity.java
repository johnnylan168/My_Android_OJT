package com.example.rd26.my_android_ojt;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViaDbActivity extends AppCompatActivity {

    private SQLiteDatabase sqLiteDatabase;
    private ListView listView;
    private Cursor cursor;
    private SimpleCursorAdapter simpleCursorAdapter;
    private int page = 1, rows =10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);

        sqLiteDatabase = MyDbHelper.getDatabase(this);
        processView();
        processController();
    }

    // findViewById
    private void processView() {
        listView = (ListView) findViewById(R.id.data_listview);
    }

    // 資料處理及設定
    private void processController() {
        // 載入資料
        loadData(page, rows);
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, new String[] {"title", "content"}, new int[] {R.id.item_title, R.id.item_content}, 0){
            @Override
            public void bindView(View view, Context context, final Cursor cursor) {
                super.bindView(view, context, cursor);
                Button itemButton = (Button) view.findViewById(R.id.item_notice);
                final TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
                itemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ListViaDbActivity.this, itemTitle.getText().toString()+"已加入到行事曆", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        listView.setAdapter(simpleCursorAdapter);
        processListener();
    }

    // 處理監聽事件
    private void processListener() {
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (cursor.moveToPosition(i)) {
                    Intent intent = new Intent(ListViaDbActivity.this, ListItemDetailActivity.class);
                    intent.putExtra("title", cursor.getString(1));
                    intent.putExtra("content", cursor.getString(2));
                    startActivity(intent);
                }
            }
        };
        listView.setOnItemClickListener(itemClickListener);

        AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if (absListView.getLastVisiblePosition() == absListView.getCount()-1) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (cursor.getCount() >= (page*rows)) {
                                        page++;
                                        loadData(page, rows);
                                        simpleCursorAdapter.changeCursor(cursor);
                                        simpleCursorAdapter.notifyDataSetChanged();
                                    }
                                }
                            }, 500);
                            Toast.makeText(ListViaDbActivity.this, "觸底了", Toast.LENGTH_SHORT).show();
                        }else if (absListView.getFirstVisiblePosition() == 0){
                            Toast.makeText(ListViaDbActivity.this, "碰頭了", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        };
        listView.setOnScrollListener(scrollListener);
    }

    // 讀取資料
    private void loadData(int dataPage, int dataRows)
    {
        if (! sqLiteDatabase.isOpen()) {
            sqLiteDatabase = MyDbHelper.getDatabase(ListViaDbActivity.this);
        }
        cursor = sqLiteDatabase.query("b_data", null, null, null, null, null, null, String.valueOf(dataPage*dataRows));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cursor.isClosed()) {
            loadData(page, rows);
            simpleCursorAdapter.changeCursor(cursor);
            simpleCursorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cursor.close();
        sqLiteDatabase.close();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
//    private void insertData() {
//        ContentValues contentValues = new ContentValues();
//        for(int i = 0; i<55 ;i++) {
//            contentValues.put("title", "第" + (i+1) + "項");
//            contentValues.put("content", "第" + (i+1) + "項產品一向都是國人最愛，如果你也愛，請購買本產品，" +
//                    "它將帶給你無比的快樂及滿足。功能多樣化，讓你愛不釋手，還有99段變速和超乎人腦智慧的功能");
//            sqLiteDatabase.insert("b_data", null, contentValues);
//        }
//        sqLiteDatabase.close();
//    }
}
