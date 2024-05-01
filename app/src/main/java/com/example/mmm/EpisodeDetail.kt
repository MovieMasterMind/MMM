package com.example.mmm

data class EpisodeDetail(
    val episodeNumber: String,
    val episodeName: String,
    val episodeOverview: String,
    val imageUrl: String,
    val voteAverage: Double,
    val IdForTVShow: Int,
    val SeasonNum: Int,
    val runTime: String,
    // Add other properties as necessary
)
