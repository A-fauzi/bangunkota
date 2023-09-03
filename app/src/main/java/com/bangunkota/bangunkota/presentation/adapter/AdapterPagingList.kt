package com.bangunkota.bangunkota.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class AdapterPagingList<T: Any , V: ViewBinding>(
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
}