package com.example.rd26.my_android_ojt;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {

    private final int REQUEST_CONTACT = 1;
    private ArrayList<HashMap<String, Object>> contactList;
    private ListView contactListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.CALL_PHONE},
                    REQUEST_CONTACT);
        } else {
            readContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("必須允許聯絡人權限才能顯示資料")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    // 開始讀取聯絡人
    private void readContacts() {
        processView();
        processController();
        processListView();
    }

    // findVIewById
    private void processView() {
        contactListView = (ListView) findViewById(R.id.contact_liseview);
        contactList = new ArrayList<>();
    }

    // 處理資料
    private void processController() {
        ContentResolver contentResolver = getContentResolver();
        Cursor nameCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        while (nameCursor.moveToNext()) {
            int id = nameCursor.getInt(nameCursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor detailCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    },
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?" +
                            "AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
                    new String[]{String.valueOf(id), String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)},
                    null
            );
            String phoneNumber = "";
            while (detailCursor.moveToNext()) {
                phoneNumber = detailCursor.getString(detailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            detailCursor.close();

            HashMap<String, Object> items = new HashMap<>();
            items.put("name", nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            items.put("phone", phoneNumber);
            contactList.add(items);
        }
        nameCursor.close();
    }

    // 顯示在ListView上
    private void processListView() {
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                contactList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "phone"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        contactListView.setAdapter(adapter);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactList.get(i).get("phone")));
                startActivity(intent);
            }
        });
    }
}
