package com.saadfauzi.uasmad.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.databinding.ItemPresenceBinding
import com.saadfauzi.uasmad.helper.Helpers
import com.saadfauzi.uasmad.models.ListAttendance

class ListPresenceAdapter(private val listItem: ArrayList<ListAttendance>, val listener: OnAdapterListener) :
    RecyclerView.Adapter<ListPresenceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPresenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listItem[position]
        holder.bind(data, listener)
    }

    class ViewHolder(var binding: ItemPresenceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListAttendance, listener: OnAdapterListener) {
            binding.datePresence.text = data.date
            binding.timePresence.text = data.attend
            Glide.with(itemView.context)
                .load("${Helpers.ENDPOINT_SHOW_IMAGE}${data.photo}")
                .centerCrop()
                .placeholder(R.drawable.ic_place_holder)
                .into(binding.icPresence)
            binding.btnDeletePresence.setOnClickListener {
                listener.onDelete(data)
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
    fun setData(data: ArrayList<ListAttendance>) {
        listItem.clear()
        listItem.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onUpdate(data: ListAttendance)
        fun onDelete(data: ListAttendance)
    }
}