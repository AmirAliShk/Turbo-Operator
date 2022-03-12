package ir.taxi1880.operatormanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.taxi1880.operatormanagement.R
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.fragment.SameNameStreetsRecycleFragment
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import org.json.JSONArray
import org.json.JSONObject

class SameNameStreetsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return checkExitSameNameStreetInCity().length()
    }

    override fun createFragment(position: Int): Fragment {
        return SameNameStreetsRecycleFragment(position);
    }

    fun checkExitSameNameStreetInCity(): JSONArray {
        val citySameNameStreetJArr = JSONArray(MyApplication.prefManager.sameNameStreets)
        val selectedCity = JSONArray()
        for (i in 0 until citySameNameStreetJArr.length()) {
            val sameNameStreetsJArr =
                citySameNameStreetJArr.getJSONObject(i).getJSONArray("specificAddressItems")
            if (sameNameStreetsJArr.length() == 0) continue
            val cityJObj = JSONObject()
            cityJObj.put("_id", citySameNameStreetJArr.getJSONObject(i).getString("_id"))
            cityJObj.put("CityId", citySameNameStreetJArr.getJSONObject(i).getString("CityId"))
            cityJObj.put("CityName", citySameNameStreetJArr.getJSONObject(i).getString("CityName"))
            val specificAddressItems = JSONArray()
            for (j in 0 until sameNameStreetsJArr.length()) {
                val localJObj = JSONObject()
                localJObj.put("_id",sameNameStreetsJArr.getJSONObject(j).getString("_id"))
                localJObj.put(
                    "streetName",
                    sameNameStreetsJArr.getJSONObject(j).getString("streetName")
                )
                localJObj.put(
                    "description",
                    sameNameStreetsJArr.getJSONObject(j).getString("description")
                )
                specificAddressItems.put(localJObj)
            }
            cityJObj.put("specificAddressItems", specificAddressItems)
            selectedCity.put(cityJObj)
        }

        return selectedCity
    }

    fun getTabView(position: Int): View {
        val v: View =
            LayoutInflater.from(MyApplication.currentActivity).inflate(R.layout.item_tab, null)
        val textView = v.findViewById<TextView>(R.id.txtTabTitle)
        TypefaceUtil.overrideFonts(v)

        val cityArr = checkExitSameNameStreetInCity()
        for (i in 0 until cityArr.length()) {
            if (position == i)
                textView.text = cityArr.getJSONObject(i).getString("CityName")
        }
        return v
    }
}