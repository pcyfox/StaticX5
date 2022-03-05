package com.tk.x5test

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        test()
    }

    fun test() {
        findViewById<TbsReaderProxyView>(R.id.player).run {
            // openVideoUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
            displayDocFile(Environment.getExternalStorageDirectory().absolutePath+"/test.docx")
           // openImageUrl("http://192.168.30.253:8080/public/1d8e7f60d2354b26b5c3146aa96127f1/2172_content.png")
        }
    }

    fun testUrl(view: View) {


        findViewById<TbsReaderProxyView>(R.id.player).run {
            // openVideoUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
            displayDocFile(Environment.getExternalStorageDirectory().absolutePath+"/test.docx")
            // openImageUrl("http://192.168.30.253:8080/public/1d8e7f60d2354b26b5c3146aa96127f1/2172_content.png")
        }

    }
    fun testVideo(view: View) {
        findViewById<TbsReaderProxyView>(R.id.player).run {
            openVideoUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
        }

    }
    fun testDoc(view: View) {
        findViewById<TbsReaderProxyView>(R.id.player).run {
            displayDocFile(Environment.getExternalStorageDirectory().absolutePath+"/test.docx")
        }
    }
    fun testImage(view: View) {
        findViewById<TbsReaderProxyView>(R.id.player).run {
            openImageUrl("http://192.168.30.253:8080/public/1d8e7f60d2354b26b5c3146aa96127f1/2172_content.png")
        }
    }
}