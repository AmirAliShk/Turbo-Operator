package ir.taxi1880.operatormanagement.dialog

import android.app.Dialog
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.DialogRewardTripBinding

class RewardTripDialog {
    lateinit var binding: DialogRewardTripBinding
    lateinit var dialog: Dialog


    fun show() {
        if(MyApplication.currentActivity.isFinishing) return


    }
}