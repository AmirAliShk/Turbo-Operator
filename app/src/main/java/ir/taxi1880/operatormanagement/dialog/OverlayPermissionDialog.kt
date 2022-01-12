package ir.taxi1880.operatormanagement.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.DialogOverlayPermissionBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil

class OverlayPermissionDialog {

    var dialog: Dialog = Dialog(MyApplication.currentActivity)
    lateinit var binding: DialogOverlayPermissionBinding

    interface result {
        fun result(s: Boolean)
    }

    fun show(result: result) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing) return
//        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogOverlayPermissionBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window?.attributes
        dialog.window?.attributes = wlp
        wlp!!.width = WindowManager.LayoutParams.MATCH_PARENT
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)

        binding.btnGoToSetting.setOnClickListener {
            val REQUEST_CODE = 101
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            myIntent.data = Uri.parse("package:" + MyApplication.currentActivity.packageName)
            MyApplication.currentActivity.startActivityForResult(myIntent, REQUEST_CODE)
            result.result(s = true)
            dialog.dismiss()
        }

        binding.btnDismiss.setOnClickListener {
            result.result(s = false)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}