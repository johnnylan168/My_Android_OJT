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
        switch (id) {
            case R.id.main_calculator:
                break;
            case R.id.main_list_varible:
                break;
            case R.id.main_list_database:
                break;
            case R.id.main_contact_list:
                break;
            case R.id.main_calender:
                break;
            case R.id.main_pic_downloader:
                break;
        }
    }
}
