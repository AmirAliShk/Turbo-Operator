package ir.taxi1880.operatormanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.ItemSameNameStreetsBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.SameNameStreetsModel

class SameNameStreetsAdapter(sameNameStreetsList: ArrayList<SameNameStreetsModel>) :
    RecyclerView.Adapter<SameNameStreetsAdapter.SameNameStreetsHolder>() {
    private var sameNameStreetsArr: ArrayList<SameNameStreetsModel> = ArrayList()

    init {
        this.sameNameStreetsArr = sameNameStreetsList
    }

    class SameNameStreetsHolder(val binding: ItemSameNameStreetsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SameNameStreetsHolder {
        val binding = ItemSameNameStreetsBinding.inflate(
            LayoutInflater.from(MyApplication.currentActivity),
            parent,
            false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return SameNameStreetsHolder(binding)
    }

    override fun onBindViewHolder(holder: SameNameStreetsHolder, position: Int) {
        val sameNameStreetItem = sameNameStreetsArr[position]
        holder.binding.txtSameNameStreetItem.text = sameNameStreetItem.sameNameStreet
        holder.binding.txtAroundStreet.text = sameNameStreetItem.aroundStreet
    }

    override fun getItemCount(): Int {
        return sameNameStreetsArr.size
    }
}