package ir.taxi1880.operatormanagement.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.ItemOperatorMistakeListBinding
import ir.taxi1880.operatormanagement.helper.DateHelper
import ir.taxi1880.operatormanagement.helper.StringHelper
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.OperatorMistakeModel
import ir.taxi1880.operatormanagement.okHttp.RequestHelper
import org.json.JSONObject

class OperatorMistakesAdapter() :
    RecyclerView.Adapter<OperatorMistakesAdapter.OpMistakeHolder>() {

    private var opMistakesA: ArrayList<OperatorMistakeModel> = ArrayList()
    lateinit var aHolder:OpMistakeHolder

    constructor(opMistakes: ArrayList<OperatorMistakeModel>) : this() {
        opMistakesA = opMistakes

    }

    class OpMistakeHolder(val binding: ItemOperatorMistakeListBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpMistakeHolder {
        val binding = ItemOperatorMistakeListBinding.inflate(
            LayoutInflater.from(MyApplication.currentActivity),
            parent,
            false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return OpMistakeHolder(binding)
    }

    override fun onBindViewHolder(holder: OpMistakeHolder, position: Int) {
        val opMistake = opMistakesA[position]
        aHolder = OpMistakeHolder(holder.binding)

        holder.binding.timeAndDate.text = StringHelper.toPersianDigits(
            DateHelper.strPersianTen(DateHelper.parseDate(opMistake.serviceDate)) + " " + opMistake.serviceTime.substring(
                0,
                5
            )
        )
        holder.binding.description.text = StringHelper.toPersianDigits(opMistake.description)
        holder.binding.originAddress.text = StringHelper.toPersianDigits(opMistake.sourceAddress)
        holder.binding.originStation.text =
            StringHelper.toPersianDigits(opMistake.sourceStation.toString())
        holder.binding.destinationAddress.text =
            StringHelper.toPersianDigits(opMistake.destinationAddress)
        holder.binding.destinationStation.text =
            StringHelper.toPersianDigits(opMistake.destinationStation.toString())

        holder.binding.recheck.setOnClickListener {
            aHolder = holder
            aHolder.binding.vfRecheck.displayedChild = 1
            RequestHelper.builder(EndPoints.RECHECK_OPERATOR_MISTAKE)
                .addParam("mistakeId", opMistake.id)
                .listener(recheckListener)
                .put()
        }


    }

    var recheckListener = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                aHolder.binding.vfRecheck.displayedChild = 0

                try {
                    val rawContent = JSONObject(args[0].toString())
                    Log.i("TAF", rawContent.toString())

                }catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            super.onFailure(reCall, e)
        }

    }

    override fun getItemCount(): Int {
        return opMistakesA.size
    }
}