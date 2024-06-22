package com.woojun.again_android.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.woojun.again_android.data.Suggest
import com.woojun.again_android.databinding.SuggestItemBinding

class SuggestAdapter(private val appList: MutableList<Suggest>): RecyclerView.Adapter<SuggestAdapter.SuggestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestViewHolder {
        val binding = SuggestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestViewHolder(binding).also { handler->
            binding.root.setOnClickListener {
                binding.root.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appList[handler.position].link)))
            }
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: SuggestViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    class SuggestViewHolder(private val binding: SuggestItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appInfo: Suggest) {
            if (binding.root.context != null) {
                Glide.with(binding.root.context)
                    .load(appInfo.image)
                    .centerCrop()
                    .into(binding.image)

                binding.titleText.text = appInfo.title
            }
        }

    }



}