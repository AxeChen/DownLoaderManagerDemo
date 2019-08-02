package com.axechen.downloader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.axechen.downloader.downloader.DownLoaderManager
import com.axechen.downloader.downloader.DownLoaderUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DownLoaderManager.init(this)
    }

    /**
     * 下载apk
     */
    public fun downLoadApk(view: View) {
        var util = DownLoaderUtil(this)
    }

    /**
     * 下载文件
     */
    public fun downLoadFile(view: View) {

    }
}
