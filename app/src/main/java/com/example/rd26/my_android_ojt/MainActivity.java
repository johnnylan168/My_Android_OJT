package com.example.rd26.my_android_ojt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 項目選擇
    public void itemClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.main_calculator:
                intent = new Intent(MainActivity.this, CalculatorActivity.class);
                startActivity(intent);
                break;
            case R.id.main_list_varible:
                intent = new Intent(MainActivity.this, ListViaVariableActivity.class);
                startActivity(intent);
                break;
            case R.id.main_list_database:
                intent = new Intent(MainActivity.this, ListViaDbActivity.class);
                startActivity(intent);
                break;
            case R.id.main_contact_list:
                intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
                break;
            case R.id.main_calender:
                intent = new Intent(MainActivity.this, CalenderActivity.class);
                startActivity(intent);
                break;
            case R.id.main_pic_downloader:
                intent = new Intent(MainActivity.this, PicDownloadActivity.class);
                startActivity(intent);
                break;
        }
    }
}
