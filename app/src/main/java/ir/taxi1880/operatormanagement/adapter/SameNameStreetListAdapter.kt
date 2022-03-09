package ir.taxi1880.operatormanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.taxi1880.operatormanagement.app.MyApplication
import ir.taxi1880.operatormanagement.databinding.ItemSameNameStreetInFragmentBinding
import ir.taxi1880.operatormanagement.helper.TypefaceUtil
import ir.taxi1880.operatormanagement.model.SameNameStreetsModel

class SameNameStreetListAdapter(list: ArrayList<SameNameStreetsModel>) :
    RecyclerView.Adapter<SameNameStreetListAdapter.SameNameStreetHolder>() {
    private val models = list

    class SameNameStreetHolder(val binding: ItemSameNameStreetInFragmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SameNameStreetHolder {
        val binding = ItemSameNameStreetInFragmentBinding.inflate(
            LayoutInflater.from(MyApplication.currentActivity),
            parent,
            false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return SameNameStreetHolder(binding)
    }

    override fun onBindViewHolder(holder: SameNameStreetHolder, position: Int) {
        val model = models[position]

        holder.binding.txtSameNameStreet.text = model.sameNameStreet
        holder.binding.txtAroundStreet.text = model.aroundStreet
    }

    override fun getItemCount(): Int {
        return models.size
    }

}