package com.tk.x5test

import android.app.Application
import com.elvishew.xlog.XLog

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        XLog.init()
//        QbSdk.preinstallStaticTbs(this)
//        QbSdkManager.init(this, object : QbCallback {
//            override fun onViewInitFinished(arg0: Boolean) {
//                Log.d(TAG, "onViewInitFinished() called with: arg0 = [$arg0]")
//            }
//
//            override fun onCoreInitFinished() {
//                Log.d(TAG, "onCoreInitFinished() called")
//            }
//        })
    }

    companion object {
        private const val TAG = "App"
    }
}