package ir.taxi1880.operatormanagement.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Progress
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.ItemRecentCallsBinding
import ir.taxi1880.operatormanagement.OnVoiceListener
import ir.taxi1880.operatormanagement.helper.DateHelper
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.helper.VoiceHelper
import ir.taxi1880.operatormanagement.model.RecentCallsModel
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor
import java.util.*

class RecentCallsAdapterK() : RecyclerView.Adapter<RecentCallsAdapterK.RecentCallsHolder>() {

    private var recentCallsList: ArrayList<RecentCallsModel> = ArrayList()

    constructor(recentCallsList: ArrayList<RecentCallsModel>)
            : this() {
        this.recentCallsList = recentCallsList
    }

    companion object {
        val TAG: String = RecentCallsAdapterK::class.java.simpleName
        lateinit var requireHolder: RecentCallsHolder

        class RefreshTokenAsyncTask : AsyncTask<Void?, Void?, Boolean?>() {
            override fun doInBackground(vararg p0: Void?): Boolean? {
                AuthenticationInterceptor().refreshToken()
                return null
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

        holder.binding.txtPassengerTell.setOnLongClickListener {
            copyToClipboard("operatorImportCall", recentCall.phone)
            return@setOnLongClickListener true
        }

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
            holder.binding.vfPlayPause.displayedChild = 1
            Log.i("URL", "show: ${EndPoints.CALL_VOICE}${recentCall.voipId}")
            val voiceName = "${recentCall.voipId}.mp3"

            VoiceHelper.getInstance()
                .autoplay("${EndPoints.CALL_VOICE}${recentCall.voipId}", voiceName,
                    recentCall.voipId,
                    object : OnVoiceListener {
                        override fun onDuringInit() {
                            requireHolder.binding.vfPlayPause.displayedChild = 0
                        }

                        override fun onEndOfInit(maxDuration: Int) {
                            requireHolder.binding.skbTimer.max = maxDuration.toFloat()
                        }

                        override fun onPlayVoice() {
                            requireHolder.binding.vfPlayPause.displayedChild = 2
                        }

                        override fun onTimerTask(currentDuration: Int) {
                            requireHolder.binding.skbTimer.setProgress(
                                currentDuration.toFloat()
                            )
                            val timeRemaining =
                                currentDuration / 1000
                            val strTimeRemaining = String.format(
                                Locale("en_US"),
                                "%02d:%02d",
                                timeRemaining / 60,
                                timeRemaining % 60
                            )
                            requireHolder.binding.txtTimeRemaining.text = strTimeRemaining
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
                            requireHolder.binding.vfVoiceStatus.displayedChild = 1
                        }

                        override fun onPauseVoice() {
                            requireHolder.binding.skbTimer.setProgress(0f)
                            requireHolder.binding.vfPlayPause.displayedChild = 0
                        }

                        override fun onVoipIdEqual0() {
                            MyApplication.Toast("صوتی برای این تماس وجود ندارد", Toast.LENGTH_SHORT)
                        }
                    }
                )

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
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    seekBar?.let { VoiceHelper.getInstance().staticMd()?.seekTo(it.progress) }
                }
            }
        }
        holder.binding.imgPause.setOnClickListener {
            requireHolder = holder
            VoiceHelper.getInstance().pauseVoice()
        }
    }

    override fun getItemCount(): Int {
        return recentCallsList.size
    }

    private fun copyToClipboard(label: String, ClipboardText: String) {
        val clipboard = getSystemService(
            MyApplication.context,
            ClipboardManager::class.java
        ) as ClipboardManager
        val clip = ClipData.newPlainText(label, ClipboardText)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(MyApplication.context, "شماره کپی شد", Toast.LENGTH_LONG).show()

    }

}