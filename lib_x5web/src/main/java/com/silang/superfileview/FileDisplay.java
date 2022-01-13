package com.silang.superfileview;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import android.util.Log;
import androidx.annotation.NonNull;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import java.util.HashMap;

public class FileDisplay {
    private static final String TAG = "FileDisplay";
    private static FileDisplay instance;

    private FileDisplay(@NonNull Application context) {
        //初始化TBS的X5内核
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d(TAG, " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                Log.e(TAG, " onCoreInitFinished");
            }
        });

    }

    public static FileDisplay getInstance(Application context) {
        if (instance == null) {
            instance = new FileDisplay(context);
        }
        return instance;
    }

    public void openFile(@NonNull Activity context, @NonNull String url, String name) {
        FileDisplayActivity.show(context, url, name);
    }

    //通过QQ浏览器打开
    public void openFileByQbSdk(Activity activity, String path) {

    }

    /**
     * 浏览文件是在另一个进程中实现的，当不再使用后需要手动干掉进程(包括所有后台进程)
     */
    public void close(Context context) {
        ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mAm != null)
            mAm.killBackgroundProcesses(context.getPackageName());
        Log.d(TAG, "close() called with: context = [" + context + "]");
    }
}
