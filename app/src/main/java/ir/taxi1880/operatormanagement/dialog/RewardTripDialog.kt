package ir.taxi1880.operatormanagement.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.taxi1880.operatormanagement.R
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.DialogRewardTripBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.okHttp.RequestHelper

class RewardTripDialog {
    lateinit var binding: DialogRewardTripBinding
    lateinit var dialog: Dialog


    fun show() {
        if(MyApplication.currentActivity.isFinishing) return
        binding = DialogRewardTripBinding.inflate(LayoutInflater.from(MyApplication.currentActivity))
        dialog = Dialog(MyApplication.context)

        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        TypefaceUtil.overrideFonts(binding.root)

        val wlp: WindowManager.LayoutParams = dialog.window!!.attributes
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.windowAnimations = R.style.ExpandAnimation
        dialog.window!!.attributes = wlp


        binding.btnSubmit.setOnClickListener {
            val reward = binding.edtReward.text.toString().trim()
            if (reward.isEmpty())
            {
                MyApplication.Toast("مقدار انعام را مشخص کنید",2)
                return@setOnClickListener
            }
            else
            {
                RequestHelper.builder(EndPoints.BESTS)
                    .addParam("ANAM",reward)
                    .listener(rewardListener)
                    .post()
            }
        }

        dialog.show()
    }

    val rewardListener = object :RequestHelper.Callback()
    {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            TODO("Not yet implemented")
        }

    }
}