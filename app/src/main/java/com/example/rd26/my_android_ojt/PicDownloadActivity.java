package com.example.rd26.my_android_ojt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PicDownloadActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private Button startDownloadBtn, clearUrlBtn;
    private EditText urlEditText;
    private GridView gridView;
    private LinearLayout topLinearLayout;
    private ProgressBar progressBar;
    private final int PROGRESS_MAX = 101, UPDATE = 102;
    private File imageFiles;
    private ArrayList<HashMap<String, Object>> imagePaths;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_download);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_CODE);
        }else {
            startWork();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startWork();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("必須允許讀寫權限才能顯示資料")
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

    private void startWork() {
        processView();
        processController();
        processAdapter();

        // TODO: 2017/3/3
        // 1.取得自定義資料夾位置
        // 2.adapter載入自訂義資料夾裡的圖片
        // 3.gridview設定adapter
        // 4.按下下載按鈕
        // 5.開始下載，網址UI轉為progressBar
        // 6.載入完畢，存至自訂義資料夾
        // 7.更新adapter和畫面
        // 8.點選後開新頁顯示圖片
        // 9.長按時可刪除圖片，並呼叫adapter更新畫面
    }

    // findViewById
    private void processView() {
        gridView = (GridView) findViewById(R.id.pic_download_gridview);
        startDownloadBtn = (Button) findViewById(R.id.pic_download_start);
        clearUrlBtn = (Button) findViewById(R.id.pic_download_clear);
        urlEditText = (EditText) findViewById(R.id.pic_download_url);
        topLinearLayout = (LinearLayout)findViewById(R.id.pic_download_inputLayout);
        progressBar = (ProgressBar) findViewById(R.id.pic_download_progressbar);
    }

    private void processController() {
        imagePaths = new ArrayList<>();
        imageFiles = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.pic_download_clear:
                        urlEditText.setText("");
                        break;
                    case R.id.pic_download_start:
                        downloadPic();
                        break;
                }
            }
        };
        startDownloadBtn.setOnClickListener(btnListener);
        clearUrlBtn.setOnClickListener(btnListener);
    }

    private void processAdapter() {
        loadImagePaths();
        adapter = new SimpleAdapter(this, imagePaths, R.layout.pic_download_item, new String[]{"imagePath"}, new int[]{R.id.pic_download_item_imageview});
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(PicDownloadActivity.this)
                        .setView(R.layout.pic_download_item)
                        .show();
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int itemId = i;
                new AlertDialog.Builder(PicDownloadActivity.this)
                        .setTitle("刪除")
                        .setMessage("確定刪除此圖片?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File temFile = new File(imagePaths.get(itemId).get("imagePath").toString());
                                temFile.delete();
                                imagePaths.remove(itemId);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

                return true;
            }
        });
    }

    private void loadImagePaths() {
        if (imagePaths.size() > 0) imagePaths.clear();
        for (File imageFile : imageFiles.listFiles()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("imagePath", imageFile.getPath());
            imagePaths.add(item);
        }
    }

    // 下載圖片
    private void downloadPic() {
        String url = urlEditText.getText().toString();
        topLinearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new DownLoadImageAsyncTask().execute(url);
    }

    class DownLoadImageAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Log.d("rd26", "before:" + imagePaths.size());
                URL url = new URL(strings[0]);
                URLConnection urlConnection = url.openConnection();
//                Log.d("rd26", "" + urlConnection.getContentLength());
                publishProgress(PROGRESS_MAX, urlConnection.getContentLength());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
                Calendar calendar = Calendar.getInstance();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(imageFiles.getPath() + "/" + calendar.getTimeInMillis() + transferImageType(urlConnection.getContentType()))));
                int len =-1;
                byte[] bytes = new byte[1024];
                while((len=bufferedInputStream.read(bytes))!=-1) {
                    bufferedOutputStream.write(bytes, 0, len);
                    bufferedOutputStream.flush(); //實時更新下載進度（使用標記區別最大值）
                    publishProgress(UPDATE, len);
                }
                bufferedInputStream.close();
                bufferedOutputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case PROGRESS_MAX:
                    progressBar.setMax(values[1]);
                    break;
                case UPDATE:
                    progressBar.incrementProgressBy(values[1]);
//                    Log.d("rd26", "increase:" + values[1]);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            topLinearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            loadImagePaths();
            Log.d("rd26", "after:" + imagePaths.size());
            adapter.notifyDataSetChanged();
        }
    }

    public String transferImageType(String type) {
        switch (type) {
            case "image/jpeg":
                return "jpg";
            default:
                return "";
        }
    }

//    public boolean isExternalStorageWritable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            return true;
//        }
//        return false;
//    }
}
