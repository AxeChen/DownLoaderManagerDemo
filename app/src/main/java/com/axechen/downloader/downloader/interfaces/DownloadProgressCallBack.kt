package com.axechen.downloader.downloader.interfaces

/**
 * 下载进度回调
 *
 */
interface DownloadProgressCallBack {
    fun downloadProgress(progress: Int)
    fun downloadException(e: String)
    fun onInstallStart(path:String?)
}