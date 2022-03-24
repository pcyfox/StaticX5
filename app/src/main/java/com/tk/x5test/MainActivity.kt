package com.tk.x5test

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.silang.superfileview.QbSdkManager
import com.silang.superfileview.view.TbsReaderProxyView
import com.tencent.smtt.sdk.QbSdk
import java.io.*

class MainActivity : AppCompatActivity() {
    private val TAG = "TBS_X5"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate() called with: app dir= ${this.filesDir}")

        if (PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                WRITE_EXTERNAL_STORAGE
            )
        ) {
            startTest()
        } else {
            val ps = arrayOf(WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(this, ps, 100)
        }
    }


    private fun startTest() {
        Log.d(TAG, "startTest() called")
        //File(this.filesDir.absolutePath+"/plugins").mkdir()
        Thread {
            /*
            app_dynamic_jar_output
            app_tbs
            app_tbs_common_share
            cache
            code_cache
            databases
            files
            shared_prefs
             */

//            Tools.dirCopy("/sdcard/com.tk.x5test/app_dynamic_jar_output/", this.filesDir.parent + "/")
//            Tools.dirCopy("/sdcard/com.tk.x5test/app_tbs/", this.filesDir.parent + "/")
//            Tools.dirCopy("/sdcard/com.tk.x5test/app_tbs_common_share/", this.filesDir.parent + "/")
//            Tools.dirCopy("/sdcard/com.tk.x5test/databases/", this.filesDir.parent + "/")
//            Tools.dirCopy("/sdcard/com.tk.x5test/files/", this.filesDir.parent + "/")

          //  initX5()
        }.start()
        test()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startTest()
    }

    private fun initX5() {
        val context = this
        if (QbSdk.canLoadX5(context)) {
            Log.i("TBS_X5", "已安装好，直接显示");
            test()
        } else {
            Log.i("TBS_X5", "新安装");
            val ok = QbSdk.preinstallStaticTbs(context)
            if (ok) {
                test()
            }
            Log.d(TAG, "initX5() called ----$ok")
        }

    }


    private fun test() {
        runOnUiThread {
            findViewById<TbsReaderProxyView>(R.id.x5_trv).run {
                //   openUrl("http://debugtbs.qq.com")
                //open("sdcard/test.docx")
                // open("sdcard/test.pdf")
                //open("sdcard/test/2.doc")
                //open(externalMediaDirs[0].absolutePath+"/test.pdf")
               
            }
        }
    }

}
