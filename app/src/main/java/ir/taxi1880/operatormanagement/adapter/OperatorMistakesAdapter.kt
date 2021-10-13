package ir.taxi1880.operatormanagement.adapter

import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.ItemOperatorMistakeListBinding
import ir.taxi1880.operatormanagement.fragment.OperatorMistakesFragment
import ir.taxi1880.operatormanagement.helper.DateHelper
import ir.taxi1880.operatormanagement.helper.FileHelper
import ir.taxi1880.operatormanagement.helper.StringHelper
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.OperatorMistakeModel
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor
import ir.taxi1880.operatormanagement.okHttp.RequestHelper
import org.json.JSONObject
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class OperatorMistakesAdapter() : RecyclerView.Adapter<OperatorMistakesAdapter.OpMistakeHolder>() {

    private var opMistakesA: ArrayList<OperatorMistakeModel> = ArrayList()
    lateinit var aHolder: OpMistakeHolder

    var TOTAL_VOICE_DURATION: Float? = null

    lateinit var mediaPlayer: MediaPlayer


    constructor(opMistakes: ArrayList<OperatorMistakeModel>) : this() {
        opMistakesA = opMistakes
    }

    companion object {
        class RefreshTokenAsyncTask : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean? {
                AuthenticationInterceptor().refreshToken()
                return null
            }
        }
    }

    class OpMistakeHolder(val binding: ItemOperatorMistakeListBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpMistakeHolder {
        val binding = ItemOperatorMistakeListBinding.inflate(
            LayoutInflater.from(MyApplication.currentActivity),
            parent,
            false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return OpMistakeHolder(binding)
    }

    override fun onBindViewHolder(holder: OpMistakeHolder, position: Int) {
        val opMistake = opMistakesA[position]
        aHolder = OpMistakeHolder(holder.binding)


        holder.binding.timeAndDate.text = StringHelper.toPersianDigits(
            DateHelper.strPersianTen(DateHelper.parseDate(opMistake.serviceDate)) + " " + opMistake.serviceTime.substring(
                0,
                5
            )
        )
        holder.binding.description.text = StringHelper.toPersianDigits(opMistake.description)
        holder.binding.originAddress.text = StringHelper.toPersianDigits(opMistake.sourceAddress)
        holder.binding.originStation.text =
            StringHelper.toPersianDigits(opMistake.sourceStation.toString())
        holder.binding.destinationAddress.text =
            StringHelper.toPersianDigits(opMistake.destinationAddress)
        holder.binding.destinationStation.text =
            StringHelper.toPersianDigits(opMistake.destinationStation.toString())
        when (opMistake.misStatus) {
            0 -> {
                holder.binding.recheck.setOnClickListener {

                    aHolder = holder
                    aHolder.binding.vfRecheck.displayedChild = 1
                    RequestHelper.builder(EndPoints.RECHECK_OPERATOR_MISTAKE)
                        .addParam("mistakeId", opMistake.id)
                        .listener(recheckListener)
                        .put()
                }
            }
            1 -> {
                aHolder.binding.recheck.isEnabled = false
                aHolder.binding.btnTxt.text = "در حال بررسی"
            }
            2 -> {
                aHolder.binding.recheck.isEnabled = false
                aHolder.binding.btnTxt.text = "بررسی شده"
            }
        }


        holder.binding.imgplay.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                pauseVoice()
            }
            holder.binding.vfPlayPause.displayedChild = 1

            Log.i("URL", "show: ${EndPoints.CALL_VOICE}${opMistake.voipId}")
            val voiceName = "${opMistake.voipId}.mp3"

            val file: File = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}${File.separator}operatorParsian/${voiceName}")
            } else {
                File("${MyApplication.DIR_ROOT}${MyApplication.VOICE_FOLDER_NAME}/$voiceName")
            }

            val voipId = opMistake.voipId
            when {
                file.exists() -> {
                    initVoice(Uri.fromFile(file))
                    playVoice()
                }
                voipId == "0" -> {
                    MyApplication.Toast("صوتی برای این تماس وجود ندارد", Toast.LENGTH_SHORT)
                    holder.binding.vfVoiceStatus.displayedChild = 1
                    holder.binding.vfPlayPause.displayedChild = 0
                }
                else -> {
                    startDownload(EndPoints.CALL_VOICE + opMistake.voipId, voiceName)
                }
            }

        }
        holder.binding.skbTimer.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                val timeRemaining = seekParams?.progress!! / 1000f
                val strTimeRemaining = String.format(
                    Locale("en_US"),
                    "%02d:%02d",
                    timeRemaining / 60,
                    timeRemaining % 60
                )
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                seekBar?.let { mediaPlayer.seekTo(it.progress) }
            }

        }
        holder.binding.imgPause.setOnClickListener {
            pauseVoice()
        }
    }

    private fun startDownload(urlString: String, fileName: String) {
        try {
            val url = URL(urlString)
            val dirPath: String = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}${File.separator}operatorParsian/"
            } else {
                "${MyApplication.DIR_ROOT}voice/"
            }
            File(dirPath).mkdirs()
            val file = File(dirPath)
            if (file.isDirectory) {
                val children = file.list();
                for (i in children.indices) {
                    File(file, children[i]).delete()
                }
            }
            PRDownloader.download(url.toString(), dirPath, fileName)
                .setHeader("Authorization", MyApplication.prefManager.authorization)
                .setHeader("id_token", MyApplication.prefManager.idToken)
                .build()
                .setOnStartOrResumeListener { }.setOnPauseListener { }.setOnCancelListener { }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        val file = File(dirPath + fileName)

                        MyApplication.handler.postDelayed({
                            initVoice(Uri.fromFile(file))
                            playVoice()
                        }, 500)
                    }

                    override fun onError(error: Error?) {
                        Log.e("pendingMistakeFragment", "onError: ${error?.responseCode}")
                        Log.e("pendingMistakeFragment", "onError: ${error?.serverErrorMessage}")
                        FileHelper.deleteFile(dirPath, fileName)
                        if (error?.responseCode == 401) {
                            RefreshTokenAsyncTask().execute()
                        }
                        if (error?.responseCode == 404) {
                            aHolder.binding.vfVoiceStatus.displayedChild = 1
                        }
                    }

                })
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var recheckListener = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                aHolder.binding.vfRecheck.displayedChild = 0

                try {
                    val rawContent = JSONObject(args[0].toString())
                    Log.i("TAF", rawContent.toString())
                    val status = rawContent.getJSONObject("data").getBoolean("status")
                    if (status) {
                        OperatorMistakesFragment.getOperatorMistakes()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            super.onFailure(reCall, e)
        }

    }

    override fun getItemCount(): Int {
        return opMistakesA.size
    }

    var timer: Timer? = null
    private fun startTimer() {
        Log.i("operatorMistakeAdapter", "startTimer: ")
        timer = Timer()
        val task = UpdateSeekBar()
        timer?.scheduleAtFixedRate(task, 500, 1000)

    }

    private fun initVoice(uri: Uri?) {
        try {
            mediaPlayer = MediaPlayer.create(MyApplication.currentActivity, uri)
            mediaPlayer.setOnCompletionListener {
//                aHolder.binding.vfPlayPause.let { it.displayedChild = 0 }
                aHolder.binding.vfPlayPause.displayedChild = 0
            }
            TOTAL_VOICE_DURATION = mediaPlayer.duration.toFloat()
            aHolder.binding.skbTimer.max = TOTAL_VOICE_DURATION as Float
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playVoice() {
        try {
            mediaPlayer.start()
            aHolder.binding.vfPlayPause.displayedChild = 2
        } catch (e: Exception) {
        }

        startTimer()
    }

    private fun pauseVoice() {
        try {
            mediaPlayer.pause()
            aHolder.binding.skbTimer.setProgress(0f)
            aHolder.binding.vfPlayPause.displayedChild = 0
        } catch (e: Exception) {

        }
        cancelTimer()
    }

    private fun cancelTimer() {
        try {
            timer?.cancel()
            timer = null
        } catch (e: Exception) {

        }
    }

    private inner class UpdateSeekBar : TimerTask() {
        override fun run() {
            try {
                MyApplication.handler.post {
                    Log.i(
                        "pendingMistakeFragment",
                        "onStopTrackingTouch run: " + mediaPlayer.currentPosition
                    )
                    aHolder.binding.skbTimer.setProgress(mediaPlayer.currentPosition.toFloat())

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


}