package ir.taxi1880.operatormanagement.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ir.taxi1880.operatormanagement.adapter.SameNameStreetsViewPagerAdapter
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.FragmentSameNameStreetBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil

class SameNameStreetsFragment:Fragment() {

    lateinit var binding:FragmentSameNameStreetBinding
    lateinit var adapter: SameNameStreetsViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSameNameStreetBinding.inflate(inflater, container , false)
        TypefaceUtil.overrideFonts(binding.root)

        adapter = SameNameStreetsViewPagerAdapter(this)

        binding.VPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout , binding.VPager) { tab: TabLayout.Tab, position: Int ->
            tab.customView = adapter.getTabView(position)
        }.attach()


        binding.imgClose.setOnClickListener {

            MyApplication.currentActivity.onBackPressed()
        }

        return binding.root
    }
}