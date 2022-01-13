package com.silang.superfileview;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.silang.superfileview.view.TbsReaderProxyView;

import java.io.File;

public class FileDisplayActivity extends FragmentActivity implements View.OnClickListener {
    private String TAG = "FileDisplayActivity";
    private TbsReaderProxyView readerViewProxy;
    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x5_activity_file_display);
        init();
    }

    public void init() {
        //防止7.0及以上版本出现FileUriExposedException异常
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            //builder.detectFileUriExposure();
        }
        readerViewProxy =  findViewById(R.id.mSuperFileView);
        findViewById(R.id.tv_title_left).setOnClickListener(this);
        findViewById(R.id.iv_title_right).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.tv_title);
        Intent intent = this.getIntent();

        String path = intent.getStringExtra("path");
        String fileName = intent.getStringExtra("name");
        title.setText(fileName);
        if (!TextUtils.isEmpty(path)) {
            setFilePath(path);
        }
        readerViewProxy.setOnGetFilePathListener(mSuperFileView2 -> mSuperFileView2.displayDocFile(new File(getFilePath())));
        readerViewProxy.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (readerViewProxy != null) {
            readerViewProxy.stopDisplay();
        }
    }

    public static void show(Context context, String url, String name) {
        Intent intent = new Intent(context, FileDisplayActivity.class);
        intent.putExtra("path", url);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private String getFilePath() {
        return filePath;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_title_left) {
            finish();
        } else if (id == R.id.iv_title_right) {

        }
    }
}
