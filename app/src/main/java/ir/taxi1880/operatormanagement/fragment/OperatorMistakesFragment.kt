package ir.taxi1880.operatormanagement.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ir.taxi1880.operatormanagement.adapter.OperatorMistakesAdapter
import ir.taxi1880.operatormanagement.app.EndPoints
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.FragmentOperatorMistakeListBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.OperatorMistakeModel
import ir.taxi1880.operatormanagement.okHttp.RequestHelper
import org.json.JSONObject

class OperatorMistakesFragment : Fragment() {


    @SuppressLint("StaticFieldLeak")
    lateinit var binding: FragmentOperatorMistakeListBinding
    lateinit var adapter: OperatorMistakesAdapter
    lateinit var mistakeArr: ArrayList<OperatorMistakeModel>

    fun getOperatorMistakes() {
        binding.vfOpMistake.displayedChild = 0
        RequestHelper.builder(EndPoints.GET_OPERATOR_MISTAKE)
            .listener(operatorMistakesListener)
            .get()
    }

    private var operatorMistakesListener = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    mistakeArr = ArrayList()
                    val rawContent = JSONObject(args[0].toString())
                    val status = rawContent.getBoolean("success")
                    val message = rawContent.getString("message")


                    if (status) {
                        val mistakes = rawContent.getJSONArray("data")
                        for (i in 0 until mistakes.length()) {
                            val jsonItem = mistakes.getJSONObject(i)
                            val opMisModel = OperatorMistakeModel(
                                jsonItem.getInt("id"),
                                jsonItem.getString("voipId"),
//                                    "1634133871.1961665",
                                jsonItem.getString("serviceDate"),
                                jsonItem.getString("serviceTime"),
                                jsonItem.getString("description"),
                                jsonItem.getString("destinationAddress"),
                                jsonItem.getInt("destinationStation"),
                                jsonItem.getString("sourceAddress"),
                                jsonItem.getInt("sourceStation"),
                                jsonItem.getString("reason"),
                                jsonItem.getInt("status")
                            )
                            mistakeArr.add(opMisModel)

                        }
                    } else {
                        binding.vfOpMistake.displayedChild = 2
                    }

                    if (mistakeArr.size == 0) {
                        binding.vfOpMistake.displayedChild = 3
                    } else {
                        binding.vfOpMistake.displayedChild = 1
                        adapter = OperatorMistakesAdapter(mistakeArr)
                        binding.opMistakesList.layoutManager =
                            LinearLayoutManager(MyApplication.context)
                        binding.opMistakesList.adapter = adapter
                        binding.opMistakesList.setHasFixedSize(true)


                    }
                } catch (e: Exception) {
                    binding.vfOpMistake.displayedChild = 2
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfOpMistake.displayedChild = 2
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperatorMistakeListBinding.inflate(inflater, container, false)
        TypefaceUtil.overrideFonts(binding.root)

        getOperatorMistakes()
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        binding.imgRefresh.setOnClickListener { getOperatorMistakes() }
        binding.imgRefreshFail.setOnClickListener { getOperatorMistakes() }
        return binding.root
    }
}