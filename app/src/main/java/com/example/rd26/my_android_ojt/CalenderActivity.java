package com.example.rd26.my_android_ojt;

import android.content.Intent;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalenderActivity extends AppCompatActivity {

    private ListView listView;
    private List<Map<String, Object>> items;
    private int rows = 10, page = 1;
    private List<Map<String, Object>> showItems;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);

        processView();
        processController();
        addAdapter();
    }

    // findViewById
    private void processView() {
        listView = (ListView) findViewById(R.id.data_listview);
    }

    // 處理監聽事件和資料
    private void processController() {
        AdapterView.OnItemClickListener adapterItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CalenderActivity.this, ListItemDetailActivity.class);
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
                            if (items.size() > (page*rows)) {
                                page++;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showItems.addAll(loadData(page, rows));
                                        adapter.notifyDataSetChanged();
                                    }
                                }, 500);
                            }
                            Toast.makeText(CalenderActivity.this, "觸底了", Toast.LENGTH_SHORT).show();
                        }else if (absListView.getFirstVisiblePosition() == 0){
                            Toast.makeText(CalenderActivity.this, "碰頭了", Toast.LENGTH_SHORT).show();
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
    }

    // 設定Adapter
    private void addAdapter() {
        //改寫SimpleAdapter的getView，因為要取得哪一項按鈕被按下
        adapter = new SimpleAdapter(this, showItems, R.layout.list_item, new String[]{"title", "saleDate", "content"}, new int[]{R.id.item_title, R.id.item_sale_time, R.id.item_content}){
            //改寫SimpleAdapter的getView，因為要取得哪一項按鈕被按下
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                convertView = super.getView(position, convertView, parent);
                final Button itemButton = (Button) convertView.findViewById(R.id.item_notice);
                final TextView itemTitle = (TextView) convertView.findViewById(R.id.item_title);
                TextView itemTimeTitle = (TextView) convertView.findViewById(R.id.item_sale_time_title);
                final TextView itemTime = (TextView) convertView.findViewById(R.id.item_sale_time);
                itemTimeTitle.setVisibility(View.VISIBLE);
                itemTime.setVisibility(View.VISIBLE);
                itemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        insertCalendar(itemTitle.getText().toString(), itemTime.getText().toString());
                    }
                });
                return convertView;
            }
        };

        listView.setAdapter(adapter);
    }

    // 新增行事曆
    private void insertCalendar(String title, String saltime) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date = dateFormat.parse(saltime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -1);
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, "商品名稱:" + title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, title + "產品將於明日開賣,不要忘囉!");
            startActivity(intent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    // 準備資料
    private void prepareData() {
        items = new ArrayList<>();
        for (int i = 0 ; i<50 ; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("title", "第" + (i+1) + "項");
            item.put("saleDate", generateDate(i));
            item.put("content", "第" + (i+1) + "項產品一向都是國人最愛，如果你也愛，請購買本產品，" +
                    "它將帶給你無比的快樂及滿足。功能多樣化，讓你愛不釋手，還有99段變速和超乎人腦智慧的功能");
            items.add(item);
        }
        showItems = new ArrayList<>();
        showItems.addAll(loadData(page, rows));
    }

    private List<Map<String, Object>> loadData(int dataPage, int dataRows) {
        int toIndex = dataPage*dataRows;
        if (toIndex > items.size()) {
            toIndex = items.size();
        }
        return items.subList((dataPage-1)*dataRows, toIndex);
    }

    /**
     * 產生日期，可依照增加天數來產生特定日期
     * @param addDate 欲增加天數
     * @return String
     */
    private String generateDate(int addDate)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, addDate);
        return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.DATE);
    }
}
