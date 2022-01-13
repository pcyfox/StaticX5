package com.tk.x5test

import android.app.Application
import android.util.Log
import com.elvishew.xlog.XLog
import com.silang.superfileview.QbSdkManager
import com.tencent.smtt.sdk.QbSdk

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        XLog.init()
        initX5()
    }

    private fun initX5() {
        QbSdkManager.init(this, object : QbSdkManager.QbCallback {
            override fun onViewInitFinished(arg0: Boolean) {
                Log.d(TAG, "onViewInitFinished() called with: arg0 = [$arg0]")
            }

            override fun onCoreInitFinished() {
                Log.d(TAG, "onCoreInitFinished() called")
            }
        })
    }

    companion object {
        private const val TAG = "App"
    }
}