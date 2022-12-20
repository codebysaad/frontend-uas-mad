package com.saadfauzi.uasmad.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.databinding.ItemPegawaiBinding
import com.saadfauzi.uasmad.models.DataPegawai

class ListPegawaiAdapter(private val listItem: ArrayList<DataPegawai>, val listener: OnAdapterListener) :
    RecyclerView.Adapter<ListPegawaiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPegawaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listItem[position]
        holder.bind(data, listener)
    }

    class ViewHolder(var binding: ItemPegawaiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataPegawai, listener: OnAdapterListener) {
            binding.tvNamaLengkap.text = data.namaLengkap
            binding.tvAlamat.text = data.alamat
            Glide.with(itemView.context)
//                .load("${Helpers.ENDPOINT_SHOW_IMAGE}${data.photo}")
                .load(R.drawable.ic_place_holder)
                .centerCrop()
                .placeholder(R.drawable.ic_place_holder)
                .into(binding.icPegawai)
            binding.btnDeletePegawai.setOnClickListener {
                listener.onDelete(data)
            }
            binding.btnUpdatePegawai.setOnClickListener {
                listener.onUpdate(data)
            }
        }
    }

    override fun getItemCount(): Int = listItem.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<DataPegawai>) {
        listItem.clear()
        listItem.addAll(data)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onUpdate(data: DataPegawai)
        fun onDelete(data: DataPegawai)
    }
}