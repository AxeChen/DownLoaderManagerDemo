package com.axechen.downloader.downloader

import android.app.*
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.support.annotation.StringRes
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.axechen.downloader.R
import com.maning.updatelibrary.InstallUtils
import com.maning.updatelibrary.MNUpdateApkFileProvider
import com.maning.updatelibrary.utils.ActForResultCallback
import com.maning.updatelibrary.utils.ActResultRequest
import com.maning.updatelibrary.utils.MNUtils
import com.ysyc.core.extend.toast
import com.ysyc.core.widget.dialog.DialogFragmentHelper
import com.ysyc.core.widget.dialog.IDialogResultListener
import com.ysyc.nhydapp.R
import com.ysyc.nhydapp.app.BaseApplication
import com.ysyc.nhydapp.downloader.interfaces.DownloadProgressCallBack
import com.ysyc.nhydapp.preference.PreferenceHelper

import java.io.File
import java.lang.Exception
import java.util.*

/**
 * App 下载升级管理器.
 *
 */
class DownloadInstaller
/**
 * 下载安装App
 *
 * @param context                  上下文
 * @param downloadApkUrl           下载URL
 * @param callBack                 回调
 */
@JvmOverloads constructor(private val mContext: Context, //新包的下载地址
                          private val downloadApkUrl: String, //事件监听器
                          private val downloadProgressCallBack: DownloadProgressCallBack? = null) {

    private var notificationManager: NotificationManager? = null
    private var notification: Notification? = null
    private var builder: NotificationCompat.Builder? = null
    private var downloadApkUrlMd5: String = UUID.randomUUID().toString()
    private var downloadApkNotifyId: Int = 0

    //local saveFilePath
    private var storageApkPath: String? = null

    /**
     * app下载升级管理
     */
    fun start() {
        initNotification()
        download()
    }


    private fun download() {
        InstallUtils.cancleDownload()
        //下载APK
//        InstallUtils.with(mContext)
//                .setApkUrl(downloadApkUrl)
//                //非必须-下载回调
//                .setCallBack(object : InstallUtils.DownloadCallBack {
//                    override fun onComplete(path: String?) {
//                        //下载完成
//                        storageApkPath = path
//                        Log.e("TAG", "下载的保存的地址:$storageApkPath")
//                        completeNotify()
//                        downloadProgressCallBack?.onInstallStart(storageApkPath)
//                    }
//
//                    override fun onFail(p0: Exception?) {
//                        //下载失败
//                        if (PreferenceHelper.hasNetwork) {
//                            downloadProgressCallBack?.downloadException("下载出现错误!")
//                            notifyError("下载出现错误!")
//                            toastError(R.string.download_failure_storage_permission_deny)
//                        } else {
//                            downloadProgressCallBack?.downloadException("当前无网络，下载已取消!")
//                            notifyError("当前无网络，下载已取消!")
//                            toastError(R.string.download_failure_storage_permission_deny)
//                        }
//                        InstallUtils.cancleDownload()
//                    }
//
//                    override fun onLoading(total: Long, current: Long) {
//                        //下载中
//                        val progress = (current.toFloat() / total * 100).toInt()
//                        downloadProgressCallBack?.downloadProgress(progress)
//                        updateNotify(progress)
//                    }
//
//                    override fun onStart() {
//                        //下载开始
//                    }
//
//                    override fun cancle() {
//                        //下载取消
//                        InstallUtils.cancleDownload()
//                        Log.e("TAG", "下载取消了")
//                    }
//
//                })
//                //开始下载
//                .startDownload()
    }


    /**
     * get String from id
     *
     * @param id res id
     * @return string
     */
    private fun getStringFrom(@StringRes id: Int): String {
        return mContext.resources.getString(id)
    }

    /**
     * Toast error message
     *
     * @param id res id
     */
    private fun toastError(@StringRes id: Int) {
        Looper.prepare()
        Toast.makeText(mContext, getStringFrom(id), Toast.LENGTH_LONG).show()
        Looper.loop()
    }


    /**
     * 初始化通知 initNotification
     */
    private fun initNotification() {
        notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(downloadApkUrlMd5, downloadApkUrlMd5, NotificationManager.IMPORTANCE_LOW)
            notificationManager?.createNotificationChannel(mChannel)
        }

        builder = NotificationCompat.Builder(mContext, downloadApkUrl)
        builder!!.setContentTitle(mContext.resources.getString(R.string.apk_update_tips_title)) //设置通知标题
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(true)  //
                .setOngoing(true)     // 不可以删除
                .setContentText(getStringFrom(R.string.apk_update_downloading_progress))
                .setChannelId(downloadApkUrlMd5!!)
                .setProgress(100, 0, false)
        notification = builder!!.build()//构建通知对象
    }


    /**
     * 通知下载更新过程中的错误信息
     *
     * @param errorMsg 错误信息
     */
    private fun notifyError(errorMsg: String) {
        builder?.setContentTitle(mContext.resources.getString(R.string.apk_update_tips_error_title))
        builder?.setContentText(errorMsg)
        notification = builder?.build()
        notificationManager?.notify(downloadApkNotifyId, notification)
    }


    /**
     * 更新下载的进度
     *
     * @param progress
     */
    private fun updateNotify(progress: Int) {
        if (progress < 100) {
            builder?.setProgress(100, progress, false)
            builder!!.setContentText(mContext.resources.getString(R.string.apk_update_downloading_progress) + " 「" + progress + "%」")
            notification = builder?.build()
            notificationManager!!.notify(downloadApkNotifyId, notification)
        }

    }

    /**
     * 下载完成的通知
     */
    private fun completeNotify() {
        builder?.setContentText("下载完成，点击安装")
        notification = builder?.build()

        //点击通知栏到安装界面，可能下载好了，用户没有安装
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val apkFile = File(storageApkPath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = mContext.packageName + ".updateFileProvider"
            val contentUri = MNUpdateApkFileProvider.getUriForFile(mContext, authority, apkFile)
            intent.setDataAndType(contentUri, intentType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), intentType)
        }
        notification!!.contentIntent = PendingIntent.getActivity(mContext, 0, intent, FLAG_UPDATE_CURRENT)
        notificationManager!!.notify(downloadApkNotifyId, notification)
    }

    companion object {
        const val intentType = "application/vnd.android.package-archive"
    }


}
