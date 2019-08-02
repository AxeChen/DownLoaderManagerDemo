package com.axechen.downloader.downloader

import android.app.DownloadManager
import android.content.Context

/**
 * created by AxeChen 2019/02/18.
 *
 * 下载管理类
 */
class DownLoaderManager {
    companion object {

        var downloadList: MutableList<DownloadBean> = mutableListOf()
        var downloadManager: DownloadManager? = null
        /**
         * 初始化数据
         */
        public fun init(context: Context) {
            downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        }
    }

}