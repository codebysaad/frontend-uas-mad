package com.saadfauzi.uasmad.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.databinding.ItemJenisCutiBinding
import com.saadfauzi.uasmad.helper.Helpers
import com.saadfauzi.uasmad.models.JenisCuti

class ListJenisCutiAdapter(
    private val listItem: ArrayList<JenisCuti>,
    val listener: OnAdapterListener
) :
    RecyclerView.Adapter<ListJenisCutiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemJenisCutiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listItem[position]
        holder.bind(data, listener)
    }

    class ViewHolder(var binding: ItemJenisCutiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: JenisCuti, listener: OnAdapterListener) {
            binding.apply {
                tvTypeCuti.text = data.jenisCuti
                tvDesc.text = data.deskripsi
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
    fun setData(data: ArrayList<JenisCuti>) {
        listItem.clear()
        listItem.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onUpdate(data: JenisCuti)
        fun onDelete(data: JenisCuti)
    }
}