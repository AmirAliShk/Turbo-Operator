package ir.taxi1880.operatormanagement.fragment

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.Keys
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.dataBase.DataBase
import ir.taxi1880.operatormanagement.databinding.FragmentPendingMistakesBinding
import ir.taxi1880.operatormanagement.dialog.GeneralDialog
import ir.taxi1880.operatormanagement.dialog.PendingMistakesOptionsDialog
import ir.taxi1880.operatormanagement.dialog.SaveMistakeResultDialog
import ir.taxi1880.operatormanagement.dialog.SaveMistakeResultDialog.MistakesResult
import ir.taxi1880.operatormanagement.helper.DateHelper
import ir.taxi1880.operatormanagement.helper.FileHelper
import ir.taxi1880.operatormanagement.helper.StringHelper
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.AllMistakesModel
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor
import ir.taxi1880.operatormanagement.okHttp.RequestHelper
import ir.taxi1880.operatormanagement.push.AvaCrashReporter
import org.json.JSONObject
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class PendingMistakesFragmentK : Fragment() {

    private lateinit var dataBase: DataBase
    private lateinit var broadcaster: LocalBroadcastManager
    private lateinit var model: AllMistakesModel
    private var TOTAL_VOICE_DURATION: Int = 0

    companion object {
        val TAG: String = PendingMistakesFragmentK::class.java.simpleName
        private var mediaPlayer: MediaPlayer? = null
        private lateinit var binding: FragmentPendingMistakesBinding
        private var timer: Timer? = null

        class RefreshTokenAsyncTask :
            AsyncTask<Void?, Void?, Boolean?>() {
            override fun doInBackground(vararg p0: Void?): Boolean? {
                AuthenticationInterceptor().refreshToken()
                return null
            }
        }

        fun pauseVoice() {
            try {
                if (mediaPlayer != null) mediaPlayer?.pause()

                binding.skbTimer.setProgress(0f)
                binding.vfPlayPause.displayedChild = 0
            } catch (e: Exception) {
                e.printStackTrace()
                AvaCrashReporter.send(e, "$TAG class, pauseVoice method")
            }
            cancelTimer()
        }

        private fun cancelTimer() {
            try {
                if (timer == null) return
                timer?.cancel()
                timer = null
            } catch (e: Exception) {
                e.printStackTrace();
                AvaCrashReporter.send(e, "$TAG class, cancelTimer method");
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPendingMistakesBinding.inflate(inflater, container, false)
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        TypefaceUtil.overrideFonts(binding.txtEmpty)

        dataBase = DataBase(MyApplication.context)
        binding.llMissedCall.setOnClickListener { missCall() }
        binding.imgPause.setOnClickListener { pauseVoice() }
        binding.imgPlay.setOnClickListener {
            binding.vfPlayPause.displayedChild = 1
            Log.i("URL", "show: ${EndPoints.CALL_VOICE}${dataBase.mistakesRow.voipId}")
            val voiceName = "${dataBase.mistakesRow.id}.mp3"
            val file =
                File("${MyApplication.DIR_MAIN_FOLDER}${MyApplication.VOICE_FOLDER_NAME}$voiceName")

            val voipId = dataBase.mistakesRow.voipId
            when {
                file.exists() -> {
                    initVoice(Uri.fromFile(file))
                    playVoice()
                }
                voipId == "0" -> {
                    binding.vfVoiceStatus.displayedChild = 1
                    binding.vfPlayPause.displayedChild = 0
                }
                else -> {
                    startDownload(
                        "${EndPoints.CALL_VOICE}${dataBase.mistakesRow.voipId}",
                        voiceName
                    )
                }
            }
        }
        binding.btnOptions.setOnClickListener {
            pauseVoice()
            val tell = dataBase.mistakesRow.tell
            val mobile = dataBase.mistakesRow.mobile
            PendingMistakesOptionsDialog()
                .show(tell, mobile)
        }

        binding.btnSaveResult.setOnClickListener {
            binding.vfSaveResult.displayedChild = 1
            pauseVoice()
            SaveMistakeResultDialog()
                .show(model.id, object : MistakesResult {
                    override fun onSuccess(success: Boolean) {
                        MyApplication.handler.postDelayed({
                            getMistakesFromDB()
                             binding.vfVoiceStatus.displayedChild = 0
                        }, 200)
                    }

                    override fun dismiss() {
                        binding.vfSaveResult.displayedChild = 0
                    }
                })
        }
        return binding.root
    }

    private fun startDownload(urlString: String, fileName: String) {
        try {
            val url = URL(urlString)
            val dirPath = "${MyApplication.DIR_MAIN_FOLDER}${MyApplication.VOICE_FOLDER_NAME}"
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
                .setOnPauseListener {}
                .setOnCancelListener {}
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
//                    FinishedDownload.execute(urlString);
                        val file = File(dirPath + fileName)
                        MyApplication.handler.postDelayed({
                            initVoice(Uri.fromFile(file))
                            playVoice()
                        }, 500)
                    }

                    override fun onError(error: Error) {
                        Log.e("pendingMistakeFragment", "onError: " + error.responseCode + "")
                        Log.e("pendingMistakeFragment", "onError: " + error.serverErrorMessage + "")
                        FileHelper.deleteFile(dirPath, fileName)
                        if (error.responseCode == 401) RefreshTokenAsyncTask().execute()
                        if (error.responseCode == 404) binding.vfVoiceStatus.displayedChild = 1
                    }
                })

//        StartDownload.execute(downloadId, url.toString(), dirPathTemp + fileName);
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, startDownload method")
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, startDownload method1")
        }

    }

    private fun initVoice(uri: Uri) {
        try {
            mediaPlayer = MediaPlayer.create(MyApplication.context, uri)
            mediaPlayer?.setOnCompletionListener {
                binding.vfPlayPause.displayedChild = 0
            }
            TOTAL_VOICE_DURATION = mediaPlayer?.duration!!
            binding.skbTimer.max = TOTAL_VOICE_DURATION.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e,"$TAG class, initVoice method")
        }
    }

    private fun playVoice() {
        try {
            if (mediaPlayer != null)
                mediaPlayer?.start()
            binding.vfPlayPause.displayedChild = 2
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, playVoice method")
        }
        startTimer()

    }

    private fun startTimer() {
        Log.i("pendingMistakeFragment", "startTimer: ")
        if (timer != null) return
        timer = Timer()
        val task = UpdateSeekBar()
        timer?.scheduleAtFixedRate(task, 500, 1000)
    }

    inner class UpdateSeekBar : TimerTask() {
        override fun run() {
            if (mediaPlayer != null) {
                try {
                    MyApplication.handler.post {
                        Log.i(
                            "pendingMistakeFragment",
                            "onStopTrackingTouch run: " + mediaPlayer?.currentPosition
                        )
                        binding.skbTimer.setProgress(mediaPlayer?.currentPosition!!.toFloat())
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(
                        e, "$TAG class, UpdateSeekBar method"
                    )
                }
            }
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            getAccepted()
        }
    }

    private fun getAccepted() {
        RequestHelper.builder(EndPoints.ACCEPT_LISTEN + "ed")
            .listener(getAcceptedListener)
            .get()
    }

    private val getAcceptedListener = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    dataBase.clearMistakeTable()
                    val listenObj = JSONObject(args[0].toString())
                    val success = listenObj.getBoolean("success")
                    val message = listenObj.getString("message")
                    if (success) {
                        val dataArr = listenObj.getJSONArray("data")
                        for (i in 0 until dataArr.length()) {
                            val dataObj = dataArr.getJSONObject(i)
                            model = AllMistakesModel()
                            model.id = dataObj.getInt("id")
                            model.serviceCode = dataObj.getInt("serviceCode")
                            model.userCode = dataObj.getInt("userCode")
                            model.date = dataObj.getString("saveDate")
                            model.time = dataObj.getString("saveTime")
                            model.description = dataObj.getString("Description")
                            model.tell = dataObj.getString("tell")
                            model.mobile = dataObj.getString("mobile")
                            model.userCodeContact = dataObj.getInt("userCodeContact")
                            model.stationRegisterUser = dataObj.getInt("stationRegisterUser")
                            model.destStationRegisterUser =
                                dataObj.getInt("destStationRegisterUser")
                            model.address = dataObj.getString("address")
                            model.customerName = dataObj.getString("customerName")
                            model.conDate = dataObj.getString("conDate")
                            model.conTime = dataObj.getString("conTime")
                            model.sendTime = dataObj.getString("sendTime")
                            model.voipId = dataObj.getString("VoipId")
                            model.stationCode = dataObj.getInt("stationCode")
                            model.city = dataObj.getInt("cityId")
                            model.mistakeReason = dataObj.getString("reasonMistake")
                            model.destStation = dataObj.getString("destinationStation")
                            model.destination = dataObj.getString("destinationAddress")
                            model.price = dataObj.getString("servicePrice")
                            dataBase.insertMistakes(model)
                        }
                        if (dataBase.mistakesCount == 0) {
                            binding.vfPending.displayedChild = 2
                        } else {
                            getMistakesFromDB()
                        }
                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context)
                        val broadcastIntent =
                            Intent(Keys.KEY_PENDING_MISTAKE_COUNT)
                        broadcastIntent.putExtra(
                            Keys.PENDING_MISTAKE_COUNT,
                            dataBase.mistakesCount
                        )
                        broadcaster.sendBroadcast(broadcastIntent)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(
                        e, "$TAG class, getAccepted method"
                    )
                }
            }
        }
    }

    fun getMistakesFromDB() {
        if (dataBase.mistakesCount > 0) {
            model = dataBase.mistakesRow
            binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.address)
            binding.txtPassengerName.text = StringHelper.toPersianDigits(model.customerName)
            binding.txtPassengerPhone.text = StringHelper.toPersianDigits(model.tell)
            binding.txtOriginStation.text =
                StringHelper.toPersianDigits(model.stationCode.toString() + "")

            binding.txtCity.text = StringHelper.toPersianDigits(dataBase.getCityName(model.city))
            binding.txtDescription.text = StringHelper.toPersianDigits(model.description)
            binding.txtTripDate.text = StringHelper.toPersianDigits(
                DateHelper.strPersianTen(DateHelper.parseDate(model.date)) + " " + model.time.substring(
                    0,
                    5
                )
            )
            binding.txtDestAddress.text = StringHelper.toPersianDigits(model.destination)
            binding.txtDestStation.text = StringHelper.toPersianDigits(model.destStation)

            if (model.mistakeReason == null || model.mistakeReason.isEmpty()) {
                binding.llMistakeReason.visibility = View.GONE
            } else {
                binding.vfMissedCall.visibility = View.GONE
                binding.txtMistakeReason.text = StringHelper.toPersianDigits(model.mistakeReason)
            }
            binding.txtPrice.text = StringHelper.toPersianDigits(StringHelper.setComma(model.price))
            binding.txtUserCode.text = StringHelper.toPersianDigits(model.userCode.toString() + "")
            binding.txtUserCodeOrigin.text =
                StringHelper.toPersianDigits(model.stationRegisterUser.toString() + "")
            binding.txtUserCodeDestination.text =
                StringHelper.toPersianDigits(model.destStationRegisterUser.toString() + "")
            binding.skbTimer.setProgress(0f)
            binding.skbTimer.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    val timeRemaining = seekParams.progress / 1000
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                    if (mediaPlayer != null) {
                        if (seekBar != null) {
                            mediaPlayer!!.seekTo(seekBar.progress)
                        }
                    }
                }
            }
            binding.vfPending.displayedChild = 1
        } else
        {
            binding.vfPending.displayedChild = 2
        }
    }

    private fun missCall() {
        binding.vfMissedCall.displayedChild = 1
        RequestHelper.builder(EndPoints.MISSED_CALL)
            .addParam("listenId", model.id)
            .listener(sendMissCallListener)
            .post()
    }

    private val sendMissCallListener = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable, vararg args: Any) {
            MyApplication.handler.post {
                try { // {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"status":true}}
                    val `object` = JSONObject(args[0].toString())
                    val success = `object`.getBoolean("success")
                    val message = `object`.getString("message")
                    if (success) {
                        val data = `object`.getJSONObject("data")
                        val status = data.getBoolean("status")
                        if (!status) {
                            GeneralDialog()
                                .message(message)
                                .cancelable(false)
                                .secondButton("برگشت", null)
                                .show()
                        } else {
                            GeneralDialog()
                                .message("پیامک تماس از دست رفته ارسال شد.")
                                .cancelable(true)
                                .firstButton("تایید", null)
                                .show()
                        }
                        binding.vfMissedCall.displayedChild = 0

                    }
                } catch (e: java.lang.Exception) {
                    binding.vfMissedCall.displayedChild = 0
                    e.printStackTrace()
                    AvaCrashReporter.send(
                        e, "$TAG class, sendMissCall method"
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pauseVoice()
    }

    override fun onStop() {
        super.onStop()
        pauseVoice()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            pauseVoice()
            cancelTimer()
        }catch (e:Exception)
        {e.printStackTrace()
            AvaCrashReporter.send(e,"$TAG class, onDestroy method")

        }
    }

}
