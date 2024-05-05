package com.example.mmm

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.EpisodeDetailActivity

class EpisodesAdapter(
    private var episodes: List<EpisodeDetail>) :
    RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val episodeNumberView: TextView = view.findViewById(R.id.episodeNumber)
        val episodeOverviewView: TextView = view.findViewById(R.id.episodeOverview)
        val episodeImageView: ImageView = view.findViewById(R.id.episodeImage)
        val episodeVoteAverage: TextView = view.findViewById(R.id.episodeVoteAverage)

        init {
            val drawableStar = ContextCompat.getDrawable(view.context, R.drawable.ic_star_vector)
            drawableStar?.setBounds(0, 0, drawableStar.intrinsicWidth, drawableStar.intrinsicHeight)
            episodeVoteAverage.setCompoundDrawablesWithIntrinsicBounds(
                drawableStar, null, null, null
            )
            episodeVoteAverage.compoundDrawablePadding = view.resources.getDimensionPixelSize(R.dimen.default_padding)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episode_item_layout, parent, false)
        return ViewHolder(view).apply {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val episode = episodes[position]
                    val context = itemView.context
                    val intent = Intent(context, EpisodeDetailActivity::class.java).apply {
                        putExtra("TV_SHOW_ID", episode.IdForTVShow)
                        putExtra("SEASON_ID", episode.SeasonNum)
                        putExtra("EPISODE_NUMBER", episode.episodeNumber)

                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = episodes[position]
        holder.episodeNumberView.text = episode.episodeNumber
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
