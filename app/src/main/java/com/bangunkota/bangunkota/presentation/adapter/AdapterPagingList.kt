package com.bangunkota.bangunkota.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.databinding.ItemEventBinding
import com.bangunkota.bangunkota.domain.entity.Event
import com.bumptech.glide.Glide

class AdapterPagingList<T: Any , V: ViewBinding>(
    private val context: Context,
    private val bindCallback: (V, T) -> Unit,
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> V
    ) : PagingDataAdapter<T, AdapterPagingList<T, V>.ViewHolder>(object : DiffUtil.ItemCallback<T>(){
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}) {

    inner class ViewHolder(val binding: V): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) bindCallback(holder.binding, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


//    inner class ViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)
//
//    @SuppressLint("SetTextI18n")
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        with(holder) {
//            with(getItem(position) ?: return) {
//                binding.itemTitle.text = this.title
//                binding.itemAddress.text = this.address
//                binding.itemDate.text = this.date
//                binding.itemTime.text = "${this.time} WIB"
//                Glide.with(context)
//                    .load(this.image)
//                    .error(R.drawable.img_placeholder)
//                    .into(binding.itemImage)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
}