package com.example.rd26.my_android_ojt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ShowPicItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_download_item);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        ImageView imageView = (ImageView)findViewById(R.id.pic_download_item_imageview);
//        try {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 1;
//            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, options);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
//            DisplayMetrics test = new DisplayMetrics();
//            this.getWindowManager().getDefaultDisplay().getMetrics(test);
        imageView.setImageBitmap(bitmap);
        // 因為pic_download_item.xml裡imageView的layout_width和layout_height設為200dp
        // 為了符合原圖，重新設定為wrap_content
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
