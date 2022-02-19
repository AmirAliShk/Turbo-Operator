package ir.taxi1880.operatormanagement.adapter

import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Progress
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.ItemRecentCallsBinding
import ir.taxi1880.operatormanagement.helper.DateHelper
import ir.taxi1880.operatormanagement.helper.FileHelper
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.RecentCallsModel
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor
import ir.taxi1880.operatormanagement.push.AvaCrashReporter
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.math.log

class RecentCallsAdapterK() : RecyclerView.Adapter<RecentCallsAdapterK.RecentCallsHolder>() {

    private var recentCallsList: ArrayList<RecentCallsModel> = ArrayList()

    var isDownloading = false
    private var TOTAL_VOICE_DURATION = 0

    constructor(recentCallsList: ArrayList<RecentCallsModel>)
            : this() {
        this.recentCallsList = recentCallsList
    }

    companion object {
        val TAG: String = RecentCallsAdapterK::class.java.simpleName
        lateinit var requireHolder: RecentCallsHolder
        var mediaPlayer: MediaPlayer? = null
        private var timer: Timer? = null

        class RefreshTokenAsyncTask : AsyncTask<Void?, Void?, Boolean?>() {
            override fun doInBackground(vararg p0: Void?): Boolean? {
                AuthenticationInterceptor().refreshToken()
                return null
            }
        }

        fun pauseVoice() {
            MyApplication.handler.post {
                try {
                    mediaPlayer?.pause()
                    if (::requireHolder.isInitialized) {
                        requireHolder.binding.skbTimer.setProgress(0f)
                        requireHolder.binding.vfPlayPause.displayedChild = 0
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "$TAG class, pauseVoice method")
                }
                cancelTimer()
            }
        }

        private fun cancelTimer() {
            try {
                if (timer == null) return
                timer!!.cancel()
                timer = null
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                AvaCrashReporter.send(e, "$TAG class, cancelTimer method")
            }
        }
    }

    class RecentCallsHolder(val binding: ItemRecentCallsBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentCallsHolder {
        val binding = ItemRecentCallsBinding.inflate(
            LayoutInflater.from(MyApplication.currentActivity),
            parent,
            false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return RecentCallsHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentCallsHolder, position: Int) {
        val recentCall = recentCallsList[position]

        holder.binding.txtDate.text = DateHelper.parseFormatToString(recentCall.txtDate)
        holder.binding.txtTime.text = DateHelper.parseFormat(recentCall.txtDate)

        if (recentCall.phone == null) {
            holder.binding.llPhone.visibility = View.GONE
        } else {
            holder.binding.txtPassengerTell.text = recentCall.phone
        }
        if (recentCall.destinationOperator == null) {
            holder.binding.llDestinationOperator.visibility = View.GONE
        } else {
            holder.binding.txtDestinationOperator.text = recentCall.destinationOperator
        }

        holder.binding.imgPlay.setOnClickListener {
            requireHolder = holder
            if (mediaPlayer != null && mediaPlayer?.isPlaying!!) {
                pauseVoice()
            }
            holder.binding.vfPlayPause.displayedChild = 1
            Log.i("URL", "show: ${EndPoints.CALL_VOICE}${recentCall.voipId}")
            val voiceName = "${recentCall.voipId}.mp3"
            val file =
                File("${MyApplication.DIR_MAIN_FOLDER}${MyApplication.VOICE_FOLDER_NAME}$voiceName")
            val voipId = recentCall.voipId
            when {
                file.exists() -> {
                    initVoice(Uri.fromFile(file))
                    playVoice()
                }
                voipId.equals("0") -> {
                    MyApplication.Toast("صوتی برای این تماس وجود ندارد", Toast.LENGTH_SHORT)
                }
                else -> {
                    startDownload("${EndPoints.CALL_VOICE}${recentCall.voipId}", voiceName)
                }
            }

            holder.binding.skbTimer.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                    val timeRemaining = seekParams?.progress!! / 1000
                    val strTImeRemaining = String.format(
                        Locale("en_US"),
                        "%02d:%02d",
                        timeRemaining / 60,
                        timeRemaining % 60
                    )
                    holder.binding.txtTimeRemaining.text = strTImeRemaining
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
//                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    if (mediaPlayer != null) {
                        if (seekBar != null) {
                            mediaPlayer?.seekTo(seekBar.progress)
                        }
                    }
                }
            }
        }
        holder.binding.imgPause.setOnClickListener {
            requireHolder = holder
            pauseVoice()
        }
    }

    override fun getItemCount(): Int {
        return recentCallsList.size
    }

    private fun startDownload(urlString: String, fileName: String) {
        try {
            val url = URL(urlString)
            val dirPath = MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME

            File(dirPath).mkdirs()

            val file = File(dirPath)
            if (file.isDirectory) {
                val children = file.list()
                for (i in children.indices) {
                    File(file, children[i]).delete()
                }
            }

//      File file = new File(dirPathTemp + fileName);
//      int downloadId = FindDownloadId.execte(urlString);
//      if (file.exists() && downloadId != -1) {
//        PRDownloader.resume(downloadId);
//      } else {
//        downloadId =
            PRDownloader.download(url.toString(), dirPath, fileName)
                .setHeader("Authorization", MyApplication.prefManager.authorization)
                .setHeader("id_token", MyApplication.prefManager.idToken)
                .build()
                .setOnStartOrResumeListener {}
                .setOnPauseListener { isDownloading = false }
                .setOnCancelListener { isDownloading = false }
                .setOnProgressListener { progress: Progress ->
                    isDownloading = true
                    Log.i("TAG", "startDownload: $progress")
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        isDownloading = false
//                            FileHelper.moveFile(dirPathTemp, fileName, dirPath);
                        val fileDown = File(dirPath + fileName)
                        MyApplication.handler.postDelayed({
//                            if (view != null) {
                            initVoice(Uri.fromFile(fileDown))
                            playVoice()
//                            }
                        }, 200)
                    }

                    override fun onError(error: Error) {
                        isDownloading = false
                        Log.e("PlayConversationDialog", "onError: " + error.responseCode + "")
                        Log.e("getServerErrorMessage", "onError: " + error.serverErrorMessage + "")
                        Log.e(
                            "getConnectionException",
                            "onError: " + error.connectionException + ""
                        )
                        FileHelper.deleteFile(dirPath, fileName)
                        if (error.responseCode == 401) RefreshTokenAsyncTask().execute()
                        if (error.responseCode == 404) requireHolder.binding.vfVoiceStatus.displayedChild =
                            1
                    }
                })

//        StartDownload.execute(downloadId, url.toString(), dirPathTemp + fileName);
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, startDownload method")
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, startDownload method2")
        }
    }

    private fun initVoice(uri: Uri?) {
        try {
//            if (view != null) {
            mediaPlayer = MediaPlayer.create(MyApplication.context, uri)
            mediaPlayer?.setOnCompletionListener { mp: MediaPlayer? ->
//                if (requireHolder.binding.vfPlayPause != null) {
                requireHolder.binding.vfPlayPause.displayedChild = 0
//                }
            }
            TOTAL_VOICE_DURATION = mediaPlayer?.duration!!
            requireHolder.binding.skbTimer.max = TOTAL_VOICE_DURATION.toFloat()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG, initVoice method")
        }
    }

    private fun playVoice() {
        try {
            mediaPlayer?.start()
            requireHolder.binding.vfPlayPause.displayedChild = 2
            startTimer()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG, playVoice")
        }
    }

    private fun startTimer() {
        if (timer != null) return

        timer = Timer()
        val task = UpdateSeekBar()
        timer!!.scheduleAtFixedRate(task, 500, 1000)
    }

    inner class UpdateSeekBar : TimerTask() {
        override fun run() {
            if (mediaPlayer != null) {
                try {
                    MyApplication.handler.post {
                        requireHolder.binding.skbTimer.setProgress(
                            mediaPlayer?.currentPosition!!.toFloat()
                        )
                        val timeRemaining =
                            mediaPlayer?.currentPosition!! / 1000
                        val strTimeRemaining = String.format(
                            Locale("en_US"),
                            "%02d:%02d",
                            timeRemaining / 60,
                            timeRemaining % 60
                        )
//                        if (txtTimeRemaining != null)
                        requireHolder.binding.txtTimeRemaining.text = strTimeRemaining
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(
                        e,
                        "$TAG class, UpdateSeekBar method "
                    )
                }
            }
        }
    }

}