package ir.taxi1880.operatormanagement.adapter

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import ir.taxi1880.operatormanagement.R
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.ItemOperatorMistakeListBinding
import ir.taxi1880.operatormanagement.dialog.GeneralDialog
import ir.taxi1880.operatormanagement.helper.DateHelper
import ir.taxi1880.operatormanagement.helper.FileHelper
import ir.taxi1880.operatormanagement.helper.StringHelper
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.OperatorMistakeModel
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor
import ir.taxi1880.operatormanagement.okHttp.RequestHelper
import ir.taxi1880.operatormanagement.push.AvaCrashReporter
import org.json.JSONObject
import java.io.File
import java.net.MalformedURLException
import java.net.URL

class OperatorMistakesAdapter() : RecyclerView.Adapter<OperatorMistakesAdapter.OpMistakeHolder>() {

    val TAG = OperatorMistakesAdapter::class.java.simpleName
    private var opMistakesA: ArrayList<OperatorMistakeModel> = ArrayList()
    lateinit var aHolder: OpMistakeHolder

    var mediaPlayer: MediaPlayer? = null

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

        var TOTAL_VOICE_DURATION: Float? = null
    }

    class OpMistakeHolder(val binding: ItemOperatorMistakeListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpMistakeHolder {
        val binding = ItemOperatorMistakeListBinding.inflate(
            LayoutInflater.from(MyApplication.currentActivity),
            parent,
            false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return OpMistakeHolder(binding)
    }

    override fun getItemCount(): Int {
        return opMistakesA.size
    }

    override fun onBindViewHolder(holder: OpMistakeHolder, position: Int) {
        val opMistake = opMistakesA[position]


        holder.binding.timeAndDate.text =
            if (opMistake.serviceTime.trim().isEmpty() || opMistake.serviceDate.trim().isEmpty()) {
                "زمان درج نشده است"
            } else {
                StringHelper.toPersianDigits(
                    DateHelper.strPersianTree(
                        DateHelper.parseDate(opMistake.serviceDate)
                    ) + " " + opMistake.serviceTime.substring(0, 5)
                )
            }
        holder.binding.description.text = StringHelper.toPersianDigits(opMistake.description)
        holder.binding.originAddress.text = StringHelper.toPersianDigits(opMistake.sourceAddress)
        holder.binding.originStation.text =
            StringHelper.toPersianDigits(opMistake.sourceStation.toString())
        holder.binding.destinationAddress.text =
            StringHelper.toPersianDigits(opMistake.destinationAddress)
        holder.binding.destinationStation.text =
            StringHelper.toPersianDigits(opMistake.destinationStation.toString())

        if (opMistake.reasonMis.isEmpty()) holder.binding.llResonMis.visibility = View.GONE
        else {
            holder.binding.llResonMis.visibility = View.VISIBLE
            holder.binding.reasonMis.text = opMistake.reasonMis
        }

        when (opMistake.misStatus) {
            1 -> {
                holder.binding.recheck.visibility = View.GONE
                holder.binding.situationTxt.text = "بررسی نشده"
                holder.binding.situationTxt.setTextColor(Color.parseColor("#f44336"))
                holder.binding.situationImg.setImageResource(R.drawable.ic_false)

            }
            2 -> {
                holder.binding.recheck.visibility = View.GONE
                holder.binding.llSituation.visibility = View.VISIBLE
                holder.binding.situationTxt.text = "در حال بررسی"
                holder.binding.situationTxt.setTextColor(Color.parseColor("#1976d2"))
                holder.binding.situationImg.setImageResource(R.drawable.ic_pennding)
            }
            3 -> {
                holder.binding.recheck.visibility = View.VISIBLE
                holder.binding.llSituation.visibility = View.VISIBLE
                holder.binding.situationTxt.text = "بررسی شده"
                holder.binding.situationTxt.setTextColor(Color.parseColor("#388e3c"))
                holder.binding.situationImg.setImageResource(R.drawable.ic_reviewed)

                holder.binding.recheck.setOnClickListener {
                    aHolder = OpMistakeHolder(holder.binding)
                    aHolder.binding.vfRecheck.displayedChild = 1

                    RequestHelper.builder(EndPoints.RECHECK_OPERATOR_MISTAKE)
                        .addParam("mistakeId", opMistake.id)
                        .listener(recheckListener)
                        .put()
                }
            }
        }

        holder.binding.imgplay.setOnClickListener {
            aHolder = OpMistakeHolder(holder.binding)
            if (mediaPlayer?.isPlaying == true) {
                pauseVoice()
            }
            holder.binding.vfPlayPause.displayedChild = 1

            Log.i("URL", "show: ${EndPoints.CALL_VOICE}${opMistake.voipId}")
            val voiceName = "${opMistake.voipId}.mp3"
            val file =
                File(MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME + voiceName)

            val voipId = opMistake.voipId
            when {
                file.exists() -> {
                    initVoice(Uri.fromFile(file))
                    playVoice()
                }
                voipId == "0" -> {
                    holder.binding.vfVoiceStatus.displayedChild = 1
                    holder.binding.vfPlayPause.displayedChild = 0
                }
                else -> {
                    startDownload(EndPoints.CALL_VOICE + opMistake.voipId, voiceName)
                }
            }

        }

//        holder.binding.skbTimer.onSeekChangeListener = object : OnSeekChangeListener {
//            override fun onSeeking(seekParams: SeekParams?) {
//            }
//
//            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
//                seekBar?.let { mediaPlayer?.seekTo(it.progress) }
//            }
//
//        }

        holder.binding.imgPause.setOnClickListener {
            pauseVoice()
        }
    }

    private var recheckListener = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                aHolder.binding.vfRecheck.displayedChild = 0

                try {
                    val rawContent = JSONObject(args[0].toString())

                    val success = rawContent.getBoolean("success")
                    val reviewRequest = rawContent.getJSONObject("data").getInt("reviewRequest")
                    val reviewMessage = rawContent.getJSONObject("data").getString("msg")

                    if (success) {
                        if (reviewRequest == 1) {
//                        OperatorMistakesFragment.getOperatorMistakes()
                            aHolder.binding.recheck.visibility = View.GONE
                            aHolder.binding.situationTxt.text = "در حال بررسی"
                            aHolder.binding.situationTxt.setTextColor(Color.parseColor("#1976d2"))
                            aHolder.binding.situationImg.setImageResource(R.drawable.ic_pennding)
                        } else {
                            GeneralDialog()
                                .message(reviewMessage)
                                .secondButton("فهمیدم") {}
                                .show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "$TAG class, recheckListener method")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                aHolder.binding.vfRecheck.displayedChild = 0
            }
        }
    }

    private fun startDownload(urlString: String, fileName: String) {
        try {
            val url = URL(urlString)
            val dirPath = MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME
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
            AvaCrashReporter.send(e, "$TAG class, startDownload method")
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, startDownload method2")
        }
    }

    //    private var timer: Timer? = null
    private fun initVoice(uri: Uri?) {
        try {
            mediaPlayer = MediaPlayer.create(MyApplication.currentActivity, uri)
            mediaPlayer?.setOnCompletionListener {
                aHolder.binding.vfPlayPause.displayedChild = 0
            }
            Companion.TOTAL_VOICE_DURATION = mediaPlayer?.duration?.toFloat()
            aHolder.binding.skbTimer.max = Companion.TOTAL_VOICE_DURATION as Float
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, initVoice method")
        }
    }

    private fun playVoice() {
        try {
            mediaPlayer?.start()
            aHolder.binding.vfPlayPause.displayedChild = 2
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, playVoice method")
        }

//        startTimer()
    }

//    private fun startTimer() {
//        Log.i("operatorMistakeAdapter", "startTimer: ")
//        timer = Timer()
//        val task = UpdateSeekBar()
//        timer?.scheduleAtFixedRate(task, 500, 1000)
//
//    }

    private fun pauseVoice() {
        try {
            mediaPlayer?.pause()
            aHolder.binding.skbTimer.setProgress(0f)
            aHolder.binding.vfPlayPause.displayedChild = 0
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, pauseVoice method")
        }
//        cancelTimer()
    }

//    private fun cancelTimer() {
//        try {
//            timer?.cancel()
//            timer = null
//        } catch (e: Exception) {
//
//        }
//    }

//    private inner class UpdateSeekBar : TimerTask() {
//        override fun run() {
//            try {
//                MyApplication.handler.post {
//                    Log.i(
//                        "pendingMistakeFragment",
//                        "onStopTrackingTouch run: " + mediaPlayer?.currentPosition
//                    )
//                    mediaPlayer?.currentPosition?.toFloat()?.let {
//                        aHolder.binding.skbTimer.setProgress(
//                            it
//                        )
//                    }
//
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//    }


}