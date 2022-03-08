package ir.taxi1880.operatormanagement.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.taxi1880.operatormanagement.adapter.SameNameStreetListAdapter
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.FragmentSameNameStreetsPageBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.SameNameStreetsModel
import org.json.JSONArray

class SameNameStreetsPageFragment(position: Int) : Fragment() {

    lateinit var binding: FragmentSameNameStreetsPageBinding
    lateinit var sameNameStreetModels: ArrayList<SameNameStreetsModel>
    val pos = position

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSameNameStreetsPageBinding.inflate(inflater, container, false)
        TypefaceUtil.overrideFonts(binding.root)

        return binding.root
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
//        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            sameNameStreetModels = ArrayList()

            val citySameNameStreetsJArr = JSONArray(MyApplication.prefManager.sameNameStreets)
            for (i in 0 until citySameNameStreetsJArr.length()) {
                if (citySameNameStreetsJArr.getJSONObject(pos).getString("CityId") ==
                    citySameNameStreetsJArr.getJSONObject(i).getString("CityId")
                ) {
                    val sameNameStreetsJArr = citySameNameStreetsJArr.getJSONObject(i).getJSONArray("specificAddressItems");
                    for (j in 0 until sameNameStreetsJArr.length()) {
                        val sameNameStreetsModel = SameNameStreetsModel(
                            citySameNameStreetsJArr.getJSONObject(i).getInt("CityId"),
                            citySameNameStreetsJArr.getJSONObject(i).getString("CityName"),
                            sameNameStreetsJArr.getJSONObject(j).getString("streetName"),
                            sameNameStreetsJArr.getJSONObject(j).getString("description"),
                        )
                        sameNameStreetModels.add(sameNameStreetsModel)
                        val adapter = SameNameStreetListAdapter(sameNameStreetModels)
                        binding.listSameNameStreet.adapter = adapter

                    }
                }
            }

        }

    }


}