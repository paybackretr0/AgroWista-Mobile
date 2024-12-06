package com.example.agrowista.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agrowista.data.response.UlasanItem
import com.example.agrowista.databinding.ItemUlasanBinding

class UlasanAdapter(private val list: List<UlasanItem?>?) : RecyclerView.Adapter<UlasanAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemUlasanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UlasanItem?) {
            binding.tvUlasan.text = item?.ulasan
            binding.tvUserName.text = item?.dataDiri?.nama ?: "John"
            binding.tvJob.text = item?.dataDiri?.pekerjaan ?: "Programmer"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUlasanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

    override fun getItemCount(): Int = list?.size ?: 0
}