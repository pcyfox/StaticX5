package com.tk.x5test

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.silang.superfileview.view.TbsReaderProxyView

class TestVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_teat_video)
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {

            test()

        } else {
            val ps = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(this, ps, 100)
        }
    }
    private val TAG = "TestVideoActivity"

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        test()
    }

    fun test() {
        findViewById<TbsReaderProxyView>(R.id.player).setOnLoadListener(object:TbsReaderProxyView.OnLoadListener{
            override fun onLoadFinish(currentFileOrUrl: String?) {
                Log.d(TAG, "onLoadFinish() called with: currentFileOrUrl = $currentFileOrUrl")
            }

            override fun onLoadStart(currentFileOrUrl: String?) {
                Log.d(TAG, "onLoadStart() called with: currentFileOrUrl = $currentFileOrUrl")
            }

            override fun onLoadStop(currentFileOrUrl: String?) {
                Log.d(TAG, "onLoadStop() called with: currentFileOrUrl = $currentFileOrUrl")
            }

            override fun onLoadError(currentFileOrUrl: String?,msg:String?) {
                Log.d(TAG, "onLoadError() called with: currentFileOrUrl = $currentFileOrUrl")
            }
        }) ;
    }

    fun testUrl(view: View) {
        findViewById<TbsReaderProxyView>(R.id.player).run {
            open("https://www.baidu.com/")
        }
    }

    fun testVideo(view: View) {
        findViewById<TbsReaderProxyView>(R.id.player).run {
            openVideoUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
        }

    }
    fun testDoc(view: View) {
        findViewById<TbsReaderProxyView>(R.id.player).run {
            open(Environment.getExternalStorageDirectory().absolutePath+"/test.docx")
        }
    }
    fun testImage(view: View) {
        findViewById<TbsReaderProxyView>(R.id.player).run {
            open("http://192.168.30.253:8080/public/1d8e7f60d2354b26b5c3146aa96127f1/2172_content.png")
        }
    }
}