package com.silang.superfileview;

/**
 * Created by LP on 2017/8/2.
 */

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;
import com.tencent.smtt.sdk.QbSdk;

/**
 *
 */
public final class QbSdkManager {
    @Keep
    private static final String TAG = "QbSdkManager";

    private QbSdkManager() {
    }

    private static boolean isInitOk = false;

    public static boolean isIsInitOk() {
        return isInitOk;
    }

    public static void setIsInitOk(boolean isInitOk) {
        QbSdkManager.isInitOk = isInitOk;
    }

    private static int initFailCount = 0;

    public static int getInitFailCount() {
        return initFailCount;
    }

    public static void init(@NonNull Context context, @Nullable final QbCallback cb) {
        //BS内核首次使用和加载时，ART虚拟机会将Dex文件转为Oat，该过程由系统底层触发且耗时较长，很容易引起anr问题，解决方法是使用TBS的 ”dex2oat优化方案“
        // 在调用TBS初始化、创建WebView之前进行如下配置
//        HashMap<String, Object> map = new HashMap<>();
//        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
//        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
//        QbSdk.initTbsSettings(map);

//        QbSdk.initX5Environment(context.getApplicationContext(), new QbSdk.PreInitCallback() {
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                isInitOk = arg0;
//                if (arg0) {
//                    initFailCount = 0;
//                    XLog.i(TAG + " TBS  QbSdk.initX5Environment() onViewInitFinished  success");
//                } else {
//                    initFailCount++;
//                    XLog.e(TAG + "TBS  QbSdk.initX5Environment onViewInitFinished  fail");
//                }
//                if (cb != null) {
//                    cb.onViewInitFinished(arg0);
//                }
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//                if (cb != null) {
//                    cb.onCoreInitFinished();
//                }
//            }
//        });

        if (QbSdk.canLoadX5(context)) {
            Log.i("TBS_X5", "已安装好");
            isInitOk = true;
        } else {
            Log.i("TBS_X5", "新安装");
            isInitOk = QbSdk.preinstallStaticTbs(context);
            Log.i(TAG, "initX5() init result---->" + isInitOk);
        }

        if (cb != null) {
            cb.onCoreInitFinished();
            cb.onViewInitFinished(isInitOk);
        }
    }

    public static void clear(Context context) {
        QbSdk.clear(context.getApplicationContext());
    }

    public interface QbCallback {
        void onViewInitFinished(boolean arg0);

        void onCoreInitFinished();
    }
}