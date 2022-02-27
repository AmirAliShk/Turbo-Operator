package ir.taxi1880.operatormanagement.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.taxi1880.operatormanagement.R
import ir.taxi1880.operatormanagement.adapter.SameNameStreetsAdapter
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.DialogSameNameStreetsBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.SameNameStreetsModel

class SameNameStreetsDialog {
    private lateinit var dialog: Dialog
    private lateinit var binding: DialogSameNameStreetsBinding
    private lateinit var sameNameStreetAdapter: SameNameStreetsAdapter
    private var list: ArrayList<SameNameStreetsModel> = ArrayList()

    fun show(list: ArrayList<SameNameStreetsModel>) {
        dialog = Dialog(MyApplication.currentActivity)
        binding = DialogSameNameStreetsBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        TypefaceUtil.overrideFonts(binding.root)

        val wlp = dialog.window?.attributes
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp


        this.list = list
        sameNameStreetAdapter = SameNameStreetsAdapter(list)
        binding.SameNameStreetList.adapter = sameNameStreetAdapter

        binding.imgClose.setOnClickListener {  dialog.dismiss() }


        dialog.show()
    }
}