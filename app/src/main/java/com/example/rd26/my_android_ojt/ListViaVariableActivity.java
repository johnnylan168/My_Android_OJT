package com.example.rd26.my_android_ojt;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViaVariableActivity extends AppCompatActivity {

    private ListView listView;
    private List<Map<String, Object>> items;
    private int rows = 10, page = 1;
    private List<Map<String, Object>> showItems;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_via_variable);

        processView();
        processController();
        addAdapter();
    }

    // findViewById
    private void processView() {
        listView = (ListView) findViewById(R.id.listview_via_varable);
    }

    // 處理監聽事件和資料
    private void processController() {
        AdapterView.OnItemClickListener adapterItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListViaVariableActivity.this, ListItemDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", items.get(i).get("title").toString());
                bundle.putString("content", items.get(i).get("content").toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };

        listView.setOnItemClickListener(adapterItemClickListener);

        AbsListView.OnScrollListener listScrollListener = new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                // i所對應的值
                // AbsListView.OnScrollListener.SCROLL_STATE_IDLE 停止滑動
                // AbsListView.OnScrollListener.SCROLL_STATE_FLING 手指接觸，並快速放開滑動
                // AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手指接觸滑動
                switch (i) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if (absListView.getLastVisiblePosition() == absListView.getCount()-1) {
                            page++;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showItems.addAll(loadData(page, rows));
                                    adapter.notifyDataSetChanged();
                                }
                            },1000);
                            Toast.makeText(ListViaVariableActivity.this, "觸底了", Toast.LENGTH_SHORT).show();
                        }else if (absListView.getFirstVisiblePosition() == 0){
                            Toast.makeText(ListViaVariableActivity.this, "碰頭了", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        };

        listView.setOnScrollListener(listScrollListener);
        // 準備數值
        prepareData();
        showItems = loadData(page, rows);
    }

    // 設定Adapter
    private void addAdapter() {
        Log.d("rd26", "addAdapter:"+showItems.size());
        //改寫SimpleAdapter的getView，因為要取得哪一項按鈕被按下
        adapter = new SimpleAdapter(this, showItems, R.layout.list_item, new String[]{"title", "content"}, new int[]{R.id.item_title, R.id.item_content}){
            //改寫SimpleAdapter的getView，因為要取得哪一項按鈕被按下
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                convertView = super.getView(position, convertView, parent);
                final Button itemButton = (Button) convertView.findViewById(R.id.item_notice);
                final TextView itemTitle = (TextView) convertView.findViewById(R.id.item_title);
                itemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ListViaVariableActivity.this, itemTitle.getText().toString()+"已加入到行事曆", Toast.LENGTH_SHORT).show();
                    }
                });
                return convertView;
            }
        };

        listView.setAdapter(adapter);
    }

    // 準備資料
    private void prepareData() {
        items = new ArrayList<>();
        for (int i = 0 ; i<50 ; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("title", "第" + (i+1) + "項");
            item.put("content", "第" + (i+1) + "項產品一向都是國人最愛，如果你也愛，請購買本產品，" +
                    "它將帶給你無比的快樂及滿足。功能多樣化，讓你愛不釋手，還有99段變速和超乎人腦智慧的功能");
            items.add(item);
        }
    }

    private List<Map<String, Object>> loadData(int dataPage, int dataRows) {
        Log.d("rd26", "dataPage:" + dataPage);
        Log.d("rd26", "dataRows:" + dataRows);
        return items.subList((dataPage-1)*dataRows, dataPage*dataRows);
//        return items.subList(0, dataPage*dataRows);
//        return items.subList(10, 20);
    }
}
