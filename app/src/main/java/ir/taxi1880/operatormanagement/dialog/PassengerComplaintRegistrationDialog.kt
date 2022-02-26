package ir.taxi1880.operatormanagement.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import ir.taxi1880.operatormanagement.R
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter
import ir.taxi1880.operatormanagement.app.DataHolder
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.DialogPassengerComplaintRegistrationBinding
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.TypeServiceModel
import ir.taxi1880.operatormanagement.okHttp.RequestHelper
import ir.taxi1880.operatormanagement.push.AvaCrashReporter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PassengerComplaintRegistrationDialog {

    var dialog: Dialog? = Dialog(MyApplication.currentActivity)
    lateinit var binding: DialogPassengerComplaintRegistrationBinding
    private val TAG = DriverComplaintRegistrationDialog::class.java.simpleName
    private var complaintType = 0

    fun show(serviceId: String) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing) return
        dialog = Dialog(MyApplication.currentActivity)
        binding = DialogPassengerComplaintRegistrationBinding.inflate(
            LayoutInflater.from(dialog!!.context)
        )
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.attributes.windowAnimations =
            R.style.ExpandAnimation
        dialog!!.setContentView(binding.root)
        TypefaceUtil.overrideFonts(dialog!!.window!!.decorView)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog!!.window!!.attributes
        wlp.gravity = Gravity.CENTER
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.windowAnimations = R.style.ExpandAnimation
        dialog!!.window!!.attributes = wlp
        dialog!!.setCancelable(false)
        initSpinner()
        binding.imgClose.setOnClickListener {
            KeyBoardHelper.hideKeyboard()
            dismiss()
        }
        binding.btnSubmit.setOnClickListener {
            KeyBoardHelper.hideKeyboard()
            setComplaint(serviceId)
            dismiss()
        }
        dialog!!.show()
    }

    private fun initSpinner() {
        val typeServiceModels = ArrayList<TypeServiceModel>()
        val serviceList = ArrayList<String>()
        try {
            val serviceArr = JSONArray(MyApplication.prefManager.complaint)
            for (i in 0 until serviceArr.length()) {
                val serviceObj = serviceArr.getJSONObject(i)
                val typeServiceModel = TypeServiceModel()
                typeServiceModel.name = serviceObj.getString("ShektypeSharh")
                typeServiceModel.id = serviceObj.getInt("sheKtypeId")
                typeServiceModels.add(typeServiceModel)
                serviceList.add(serviceObj.getString("ShektypeSharh"))
            }
            binding.spComplaintType.isEnabled = true
            binding.spComplaintType.adapter =
                SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, serviceList)
            binding.spComplaintType.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    complaintType = typeServiceModels[position].id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            AvaCrashReporter.send(
                e,
                "$TAG class, initSpinner method"
            )
        }
    }

    private fun setComplaint(serviceId: String) {
        binding.vfLoader.displayedChild = 1
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.INSERT_COMPLAINT)
            .addParam("serviceId", serviceId)
            .addParam("complaintType", complaintType)
            .addParam("voipId", DataHolder.getInstance().voipId)
            .addParam("description", " ")
            .listener(onSetComplaint)
            .post()
    }

    private var onSetComplaint: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable, vararg args: Any) {
            MyApplication.handler.post {
                try {
                    val `object` = JSONObject(args[0].toString())
                    val success = `object`.getBoolean("success")
                    val message = `object`.getString("message")
                    if (success) {
                        val dataObj = `object`.getJSONObject("data")
                        val status = dataObj.getBoolean("result")
                        if (status) {
                            GeneralDialog()
                                .title("تایید شد")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show()
                            DataHolder.getInstance().voipId = "0"
                        } else {
                            GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show()
                        }
                    } else {
                        GeneralDialog()
                            .title("خطا")
                            .message(message)
                            .cancelable(false)
                            .firstButton("باشه", null)
                            .show()
                    }

                    binding.vfLoader.displayedChild = 0
                    LoadingDialog.dismissCancelableDialog()
                } catch (e: java.lang.Exception) {
                    LoadingDialog.dismissCancelableDialog()
                    e.printStackTrace()
                    AvaCrashReporter.send(
                        e,
                        "$TAG class, onSetComplaint method"
                    )
                }
            }
        }

        override fun onFailure(reCall: Runnable, e: java.lang.Exception) {
            MyApplication.handler.post {
                LoadingDialog.dismissCancelableDialog()
                binding.vfLoader.displayedChild = 0
            }
        }
    }

    private fun dismiss() {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
                KeyBoardHelper.hideKeyboard()
            }
        } catch (e: Exception) {
            Log.e("TAG", "dismiss: " + e.message)
            AvaCrashReporter.send(
                e,
                "$TAG class, dismiss method"
            )
        }
        dialog = null
    }
}