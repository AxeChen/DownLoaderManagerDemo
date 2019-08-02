package com.axechen.downloader.downloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

/**
 * created by AxeChen 2019/02/18.
 * 用于download下载的广播监听
 */
class DownLoaderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (DownLoaderManager.downloadList.size <= 0) {
                return
            }
            val query = DownloadManager.Query()
            for (it in DownLoaderManager.downloadList) {
                query.setFilterById(it.downloadId)
                val cursor = DownLoaderManager.downloadManager?.query(query)
                if (cursor != null && cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_PAUSED -> Toast.makeText(context, it.title + "下载暂停", Toast.LENGTH_SHORT).show()
                        DownloadManager.STATUS_PENDING -> {
                        }
                        DownloadManager.STATUS_RUNNING -> Toast.makeText(context, it.title + "正在下载", Toast.LENGTH_SHORT).show()
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Toast.makeText(context, it.title + "下载成功", Toast.LENGTH_SHORT).show()
                            cursor.close()
                            DownLoaderManager.downloadList.remove(it)
                            if (context != null && it.downloadType === DownloadBean.TYPE_APK && it.needInstall) {
                                installApk(context, it.downloadId, it)
                            }
                        }
                        //下载失败
                        DownloadManager.STATUS_FAILED -> {
                            Toast.makeText(context, it.title + "下载失败", Toast.LENGTH_SHORT).show()
                            DownLoaderManager.downloadList.remove(it)
                            cursor.close()
                        }
                    }
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 安装apk
     */
    private fun installApk(context: Context, downloadApkId: Long, downloadBean: DownloadBean) {
        val dManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val install = Intent(Intent.ACTION_VIEW)
        val downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId)
        if (downloadFileUri != null) {
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive")
            if ((Build.VERSION.SDK_INT >= 24)) {//判读版本是否在7.0以上
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (install.resolveActivity(context.packageManager) != null) {
                context.startActivity(install)
            } else {
                Toast.makeText(context, downloadBean.title + "下载完成，请手动安装", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, downloadBean.title + "下载错误", Toast.LENGTH_SHORT).show()
        }
    }
}