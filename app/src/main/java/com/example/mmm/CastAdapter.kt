package com.example.mmm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CastAdapter :
    ListAdapter<CastMember, CastAdapter.CastViewHolder>(DIFF_CALLBACK) {

    class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.cast_member_image)
        val nameTextView: TextView = view.findViewById(R.id.cast_member_name)
        val characterTextView: TextView = view.findViewById(R.id.cast_member_character) // Add this line
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cast_member, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val castMember = getItem(position) // Changed to use getItem provided by ListAdapter
        Glide.with(holder.imageView.context)
            .load(castMember.imageUrl)
            .placeholder(R.drawable.placeholder_image) // Add a placeholder
            .into(holder.imageView)
        holder.nameTextView.text = castMember.name
        holder.characterTextView.text = castMember.character
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CastMember>() {
            override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
                // TODO: Replace `id` with the appropriate field in your CastMember class.
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
                return oldItem == newItem
            }
        }
    }
}
