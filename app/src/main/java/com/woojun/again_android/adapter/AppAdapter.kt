package com.woojun.again_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.again_android.data.AppInfo
import com.woojun.again_android.databinding.FilterItemBinding

class AppAdapter(private val appList: MutableList<AppInfo>): RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = FilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding).also { handler->
            binding.appSwitch.setOnCheckedChangeListener { _, isChecked ->
                appList[handler.position].isChecked = isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    class AppViewHolder(private val binding: FilterItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appInfo: AppInfo) {
            if (binding.root.context != null) {
                Glide.with(binding.root.context)
                    .load(appInfo.icon)
                    .into(binding.appIcon)

                binding.appTitle.text = appInfo.name
            }
        }

    }


    fun getCheckedAppList(): List<AppInfo> {
        return appList.filter { it.isChecked }
    }


}