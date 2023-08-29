package com.bangunkota.bangunkota.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.databinding.ItemEventBinding
import com.bangunkota.bangunkota.domain.entity.Event
import com.bumptech.glide.Glide

class EventPagingAdapter(private val context: Context) : PagingDataAdapter<Event, EventPagingAdapter.ViewHolder>(EventDiffComp) {
    object EventDiffComp : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }

    }

    inner class ViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(getItem(position)) {
                binding.itemTitle.text = this?.title
                binding.itemAddress.text = this?.address
                binding.itemDate.text = this?.date
                Glide.with(context)
                    .load(this?.image)
                    .error(R.drawable.img_placeholder)
                    .into(binding.itemImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
}