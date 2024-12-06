package com.example.agrowista.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agrowista.data.response.AgroItem
import com.example.agrowista.databinding.ItemAgrotourismBinding

class AgroAdapter(private val list: List<AgroItem?>?) : RecyclerView.Adapter<AgroAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemAgrotourismBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AgroItem?) {
            binding.tvNamaWisata.text = item?.namaWisata
            binding.tvDeskripsi.text = item?.deskripsi
            Glide.with(binding.root.context)
                .load(item?.gambar)
                .into(binding.ivWisata)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAgrotourismBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    override fun getItemCount(): Int = list?.size ?: 0
}