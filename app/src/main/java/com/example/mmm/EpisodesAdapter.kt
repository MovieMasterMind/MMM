package com.example.mmm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EpisodesAdapter(private var episodes: List<EpisodeDetail>) :
    RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val episodeNumberView: TextView = view.findViewById(R.id.episodeNumber)
//        val episodeNameView: TextView = view.findViewById(R.id.episodeName)
        val episodeOverviewView: TextView = view.findViewById(R.id.episodeOverview)
        val episodeImageView: ImageView = view.findViewById(R.id.episodeImage)
        val episodeVoteAverage: TextView = view.findViewById(R.id.episodeVoteAverage)
        // More bindings if necessary
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.episode_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = episodes[position]
        holder.episodeNumberView.text = episode.episodeNumber
//        holder.episodeNameView.text = episode.episodeName
        holder.episodeOverviewView.text = episode.episodeOverview
        Glide.with(holder.itemView.context)
            .load(episode.imageUrl)
            .into(holder.episodeImageView)
        holder.episodeVoteAverage.text = episode.voteAverage.toString()
    }

    override fun getItemCount() = episodes.size

    fun updateEpisodes(newEpisodes: List<EpisodeDetail>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }
}