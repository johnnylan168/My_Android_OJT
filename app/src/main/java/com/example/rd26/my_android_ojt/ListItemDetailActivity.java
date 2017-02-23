package com.example.rd26.my_android_ojt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ListItemDetailActivity extends AppCompatActivity {

    private TextView detailTitle;
    private TextView detailContent;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        processView();
        title = bundle.getString("title");
        detailTitle.setText(title);
        detailContent.setText(bundle.getString("content"));
    }

    private void processView() {
        detailTitle = (TextView)findViewById(R.id.item_detail_title);
        detailContent = (TextView)findViewById(R.id.item_detail_content);
    }

    public void addFavorite(View v) {
        Toast.makeText(this, title + "已加入到我的最愛", Toast.LENGTH_SHORT).show();
    }
}
