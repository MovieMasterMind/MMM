package com.example.mmm

data class EpisodeMember(
    val id: String, // Unique identifier for each EpisodeMember
    val name: String,
    val overview: String,
    val episode_number: String,
    val episode_type: String,
    val runtime: String,
    val still_path: String,
    val vote_average: Double
)