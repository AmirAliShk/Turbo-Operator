package ir.taxi1880.operatormanagement.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.romainpiel.shimmer.Shimmer
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.app.MyApplication.context
import ir.taxi1880.operatormanagement.databinding.DialogDownloadUpdateBinding
import ir.taxi1880.operatormanagement.helper.ApkInstallerHelper
import ir.taxi1880.operatormanagement.helper.HashHelper
import ir.taxi1880.operatormanagement.helper.TypefaceUtil

class DownloadUpdateDialog {

    lateinit var dialog: Dialog
    lateinit var binding: DialogDownloadUpdateBinding

    fun show(url: String) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing) return
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogDownloadUpdateBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window?.attributes
        dialog.window?.attributes = wlp
        wlp!!.width = WindowManager.LayoutParams.MATCH_PARENT
        TypefaceUtil.overrideFonts(binding.root)

        val prdownloader = PRDownloaderConfig.newBuilder()
            .build()
        PRDownloader.initialize(context, prdownloader)

        startDownload(url)

        //start floating text color loading
        val shimmer = Shimmer()
        shimmer.setDuration(2000)
            .setStartDelay(100)
            .start(binding.txt)

        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun startDownload(url: String) {
        PRDownloader.download(
            url,
            MyApplication.DIR_MAIN_FOLDER + MyApplication.UPDATE_FOLDER_NAME,
            "${HashHelper.md5Generator(url)}.apk"
        )
            .build()
            .setOnProgressListener {
                binding.updateProgress.progress = ((it.currentBytes * 100) / it.totalBytes).toInt()
                binding.textProgress.text = "${((it.currentBytes * 100) / it.totalBytes).toInt()} %"
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    ApkInstallerHelper.install(
                        MyApplication.currentActivity,
                        "${HashHelper.md5Generator(url)}.apk"
                    )
                }

                override fun onError(error: Error?) {
                    GeneralDialog()
                        .message("مشکلی در به روز رسانی برنامه به وجود امد لطفا بعد از چند لحظه دوباره تلاش نمایید")
                        .title("خطا در بروز رسانی")
                        .firstButton("تلاش مجدد", ({ startDownload(url) }))
                        .secondButton("فعلا نه") { MyApplication.currentActivity.finish() }
                        .cancelable(false)
                        .show()
                }
            })
    }
}