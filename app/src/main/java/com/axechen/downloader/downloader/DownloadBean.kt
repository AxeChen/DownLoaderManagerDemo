package com.axechen.downloader.downloader

/**
 * 下载文件的Bean，用于记录文件的信息
 *  downloadID 下载的id
 *  title 下载的title
 *  downLoaderType 下载的类型
 *  needInstall 如果是apk，是否需要下载完成之后安装
 */
class DownloadBean {

    companion object {

        // 下载类型：apk
        const val TYPE_APK = "APK"
        // 下载类型：文件
        const val TYPE_FILE = "FILE"
    }

    var downloadId = 0L
    var title: String = ""
    var downloadType: String = ""
    var needInstall: Boolean = false
}