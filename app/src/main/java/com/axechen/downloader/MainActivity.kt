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
        util.downloadApk("百度","http://gdown.baidu.com/data/wisegame/089002f01757d5e4/baidu_49284352.apk",true)
    }

    /**
     * 下载文件
     */
    public fun downLoadFile(view: View) {
        var util = DownLoaderUtil(this)
        util.downloadFile("应用宝","http://gdown.baidu.com/data/wisegame/0995fa2e64afa17e/yingyongbao_7382130.apk","ddd")
    }
}
