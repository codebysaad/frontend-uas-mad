package com.saadfauzi.uasmad.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saadfauzi.uasmad.databinding.ItemCutiBinding
import com.saadfauzi.uasmad.models.Cuti

class ListCutiAdapter (
    private val listItem: ArrayList<Cuti>,
    val listener: OnAdapterListener
) :
    RecyclerView.Adapter<ListCutiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCutiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listItem[position]
        holder.bind(data, listener)
    }

    class ViewHolder(var binding: ItemCutiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Cuti, listener: OnAdapterListener) {
            binding.apply {
                tvAlasan.text = data.alasan
                tvTypeCuti.text = data.jenisCuti
                tvDateCuti.text = data.tglAwal
                btnDelete.setOnClickListener {
                    listener.onDelete(data)
                }
                btnUpdate.setOnClickListener {
                    listener.onUpdate(data)
                }
            }

//            itemView.setOnClickListener {
//                val intent = Intent(itemView.context, DetailActivity::class.java)
//                intent.putExtra(DetailActivity.EXTRA_DATA, data)
//                val optionsCompat: ActivityOptionsCompat =
//                    ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        itemView.context as Activity,
//                        Pair(binding.photoStory, "photo"),
//                        Pair(binding.tvName, "name"),
//                        Pair(binding.tvDescStory, "description"),
//                        Pair(binding.tvCreatedAt, "date")
//                    )
//                itemView.context.startActivity(intent, optionsCompat.toBundle())
//            }
        }
    }

    override fun getItemCount(): Int = listItem.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<Cuti>) {
        listItem.clear()
        listItem.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onUpdate(data: Cuti)
        fun onDelete(data: Cuti)
    }
}