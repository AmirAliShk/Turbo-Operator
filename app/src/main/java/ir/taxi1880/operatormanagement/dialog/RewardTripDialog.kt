package ir.taxi1880.operatormanagement.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import ir.taxi1880.operatormanagement.R
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.DialogRewardTripBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.okHttp.RequestHelper
import ir.taxi1880.operatormanagement.push.AvaCrashReporter
import org.json.JSONObject


class RewardTripDialog {
    lateinit var binding: DialogRewardTripBinding
    lateinit var dialog: Dialog
    lateinit var rewardPrice: String
    private lateinit var rewardArr: List<String>

    lateinit var rewardBackListener: RewardBackListener


    interface RewardBackListener {
        fun rewardBack(reward: String)
    }

    fun show(serviceId: Int, rewardBackListener: RewardBackListener) {
        if (MyApplication.currentActivity.isFinishing) return
        dialog = Dialog(MyApplication.currentActivity)
        binding = DialogRewardTripBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        TypefaceUtil.overrideFonts(binding.root)

        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        this.rewardBackListener = rewardBackListener
        initSpinner();

        binding.imgClose.setOnClickListener {
            dialog.dismiss()
        }
        binding.blrView.setOnClickListener {
            dialog.dismiss()
        }

        binding.llSpinner.setOnClickListener {
            binding.rewardSpinner.performClick()
        }
        binding.btnSubmit.setOnClickListener {
            if (rewardPrice == rewardArr[0]) {
                MyApplication.Toast("مقدار انعام را مشخص کنید", 2)
                return@setOnClickListener
            } else {
                binding.vfSubmit.displayedChild = 1
                RequestHelper.builder(EndPoints.REWARD_FOR_TRIP)
                    .addParam("amount", rewardPrice)
                    .addParam("tripId", serviceId)
                    .listener(rewardListener)
                    .put()
            }
        }

        dialog.show()
    }

    private val rewardListener = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val rawContent = JSONObject(args[0].toString())
                    val success = rawContent.getBoolean("success")
                    val message = rawContent.getString("message")

                    if (success) {
                        val isAdded = rawContent.getJSONObject("data").getBoolean("status")
                        if (isAdded)
                            MyApplication.Toast("انعام با موفقیت اضافه شد.", 2)
                        rewardBackListener.rewardBack(rewardPrice)
                    } else {
                        MyApplication.Toast("انعام با موفقیت اضافه نشد.", 2)
                    }
                    binding.vfSubmit.displayedChild = 0
                    dialog.dismiss()
                } catch (e: Exception) {

                    AvaCrashReporter.send(e, "RewardTripDialog, rewardListener")
                    dialog.dismiss()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                dialog.dismiss()
            }
        }

    }

    private fun initSpinner() {
        rewardArr = listOfNotNull("مقدار انعام را مشخص کنید", "5000", "10000", "15000")
        try {
            binding.rewardSpinner.adapter =
                SpinnerAdapter(dialog.context, R.layout.item_spinner, rewardArr)
            binding.rewardSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        when (position) {
                            0 -> rewardPrice = rewardArr[0]
                            1 -> rewardPrice = rewardArr[1]
                            2 -> rewardPrice = rewardArr[2]
                            3 -> rewardPrice = rewardArr[3]
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}