package com.woojun.again_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.again_android.R
import com.woojun.again_android.data.AppTimeInfo
import com.woojun.again_android.databinding.TimeItemBinding

class TimeAdapter(private val appList: MutableList<AppTimeInfo>): RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val binding = TimeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeViewHolder(binding).also { handler->

        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    class TimeViewHolder(private val binding: TimeItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appInfo: AppTimeInfo) {
            if (binding.root.context != null) {
                Glide.with(binding.root.context)
                    .load(R.drawable.d_logo)
                    .centerCrop()
                    .into(binding.appIcon)

                binding.appTitle.text = appInfo.name
                binding.timeText.text = appInfo.time
            }
        }

    }



}