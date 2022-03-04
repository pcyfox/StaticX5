package com.tk.x5test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.silang.superfileview.view.TbsReaderProxyView

class TestVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teat_video)

        findViewById<TbsReaderProxyView>(R.id.player).run {
           openVideoUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
        }
    }
}