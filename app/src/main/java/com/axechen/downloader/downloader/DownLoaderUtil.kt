package com.axechen.downloader.downloader

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import com.axechen.downloader.BuildConfig
import java.io.File
import java.lang.ref.WeakReference


/**
 * created by AxeChen on 2019/02/16.
 * 使用系统提供的下载工具下载文件（图片或者apk）
 */
class DownLoaderUtil(context: Context) {

    private var weakReference: WeakReference<Context>? = null
    private var downloadId = 0L

    init {
        weakReference = WeakReference(context)
    }


    /**
     * 直接下载apk，下载完成之后无需安装apk
     */
    public fun downloadApk(title: String, url: String) {
        downloadApk(title, url, false)
    }

    /**
     * 下载apk
     * title：应用名称，状态栏会提示：正在下载。
     * url：应用下载地址。
     */
    public fun downloadApk(title: String, url: String, needInstall: Boolean) {
        weakReference?.get()?.run {
            // 这边是初始化download错误的时候

            var fileName = if (url.contains(".apk")) {
                url.substring(url.lastIndexOf("/"), url.length)
            } else {
                "axe_" + System.currentTimeMillis() + ".apk"
            }


            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            // 假如这个apk已经下载了，然后需要直接安装，那么就直接安装
            if (file.exists() && needInstall) {
                install(this, file.absolutePath)
                return
            }
            Toast.makeText(this,"正在下载$title",Toast.LENGTH_SHORT).show()
            // 真正的下载逻辑
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle(title)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setMimeType("application/vnd.android.package-archive")
            if (TextUtils.isEmpty(title)) {
                request.setDescription(fileName)
            } else {
                request.setDescription(title)
            }

            try {
                if (DownLoaderManager.downloadManager == null) {
                    // 可能部分机型不支持这个DownloaderManger ，这边可以使用自定义的下载框架
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            request.setDestinationUri(Uri.fromFile(file))
            if (DownLoaderManager.downloadManager != null) {
                downloadId = DownLoaderManager.downloadManager!!.enqueue(request)
                val downloadBean = DownloadBean()
                downloadBean.downloadId = downloadId
                downloadBean.title = title
                downloadBean.downloadType = DownloadBean.TYPE_APK
                downloadBean.needInstall = needInstall
                DownLoaderManager.downloadList.add(downloadBean)
            }
        }
    }

    private fun install(context: Context, filePath: String) {
        val apkFile = File(filePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            var contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }

    /**
     * 下载文件
     */
    public fun downloadFile(title: String, url: String, fileName: String) {
        weakReference?.get()?.run {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle(title)
            request.setDescription(title)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            var saveFile = ""
            saveFile = if (TextUtils.isEmpty(fileName)) {
                url.substring(url.lastIndexOf("/"), url.length)
            } else {
                fileName
            }

            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), saveFile)
            request.setDestinationUri(Uri.fromFile(file))
            if (DownLoaderManager.downloadManager != null) {
                downloadId = DownLoaderManager.downloadManager!!.enqueue(request)
                val downloadBean = DownloadBean()
                downloadBean.downloadId = downloadId
                downloadBean.title = title
                downloadBean.downloadType = DownloadBean.TYPE_FILE
                DownLoaderManager.downloadList.add(downloadBean)
            }
        }
    }


}