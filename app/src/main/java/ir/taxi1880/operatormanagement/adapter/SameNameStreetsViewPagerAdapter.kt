package ir.taxi1880.operatormanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.taxi1880.operatormanagement.R
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.fragment.SameNameStreetsPageFragment
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import org.json.JSONArray

class SameNameStreetsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return JSONArray(MyApplication.prefManager.sameNameStreets).length()
    }

    override fun createFragment(position: Int): Fragment {
        return SameNameStreetsPageFragment(position);
    }

    fun getTabView(position: Int): View {
        val v: View =
            LayoutInflater.from(MyApplication.currentActivity).inflate(R.layout.item_tab, null)
        val textView = v.findViewById<TextView>(R.id.txtTabTitle)
        TypefaceUtil.overrideFonts(v)

        val cityArr= JSONArray(MyApplication.prefManager.sameNameStreets)
        for (i in 0 until cityArr.length()){
            if (position == i)
                textView.text = cityArr.getJSONObject(i).getString("CityName")
        }

        return v
    }
}