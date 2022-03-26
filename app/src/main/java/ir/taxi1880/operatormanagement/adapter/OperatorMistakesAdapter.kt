package ir.taxi1880.operatormanagement.adapter

import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Progress
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import ir.taxi1880.operatormanagement.R
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.ItemOperatorMistakeListBinding
import ir.taxi1880.operatormanagement.dialog.GeneralDialog
import ir.taxi1880.operatormanagement.OnVoiceListener
import ir.taxi1880.operatormanagement.fragment.mistake.PendingMistakesFragmentK
import ir.taxi1880.operatormanagement.helper.*
import ir.taxi1880.operatormanagement.model.OperatorMistakeModel
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor
import ir.taxi1880.operatormanagement.okHttp.RequestHelper
import ir.taxi1880.operatormanagement.push.AvaCrashReporter
import org.json.JSONObject

class OperatorMistakesAdapter() : RecyclerView.Adapter<OperatorMistakesAdapter.OpMistakeHolder>() {

    val TAG = OperatorMistakesAdapter::class.java.simpleName
    private var opMistakesA: ArrayList<OperatorMistakeModel> = ArrayList()
    lateinit var aHolder: OpMistakeHolder

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
            VoiceHelper.getInstance().pauseVoice()
            holder.binding.vfPlayPause.displayedChild = 1

            Log.i("URL", "show: ${EndPoints.CALL_VOICE}${opMistake.voipId}")
            val voiceName = "${opMistake.voipId}.mp3"

            VoiceHelper.getInstance().autoplay(
                EndPoints.CALL_VOICE + opMistake.voipId,
                voiceName,
                opMistake.voipId,
                object : OnVoiceListener {
                    override fun onDuringInit() {
                        aHolder.binding.vfPlayPause.displayedChild = 0
                    }

                    override fun onEndOfInit(maxDuration: Int) {
                        aHolder.binding.skbTimer.max = maxDuration.toFloat()
                    }

                    override fun onPlayVoice() {
                        aHolder.binding.vfPlayPause.displayedChild = 2
                    }

                    override fun onTimerTask(currentDuration: Int) {
                    }

                    override fun onFileExist() {

                    }

                    override fun onStartDownload() {
                    }

                    override fun onProgressDownload(progress: Progress?) {
                    }

                    override fun onDownloadCompleted() {
                    }

                    override fun onDownloadError() {
                    }

                    override fun onDownload401Error() {
                        RefreshTokenAsyncTask().execute()
                    }

                    override fun onDownload404Error() {
                        aHolder.binding.vfVoiceStatus.displayedChild = 1
                    }

                    override fun onPauseVoice() {
                        aHolder.binding.skbTimer.setProgress(0f)
                        aHolder.binding.vfPlayPause.displayedChild = 0
                    }

                    override fun onVoipIdEqual0() {
                        holder.binding.vfVoiceStatus.displayedChild = 1
                        holder.binding.vfPlayPause.displayedChild = 0
                    }
                }
            )

            holder.binding.skbTimer.onSeekChangeListener = object : OnSeekChangeListener {

                override fun onSeeking(seekParams: SeekParams?) {
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    seekBar?.let { VoiceHelper.getInstance().staticMd()?.seekTo(it.progress) }
                }
            }
        }
        holder.binding.imgPause.setOnClickListener {
            VoiceHelper.getInstance().pauseVoice()
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

}