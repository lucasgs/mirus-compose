package com.dendron.mirus.data.remote.dto


import com.google.gson.annotations.SerializedName

data class GenreResultDto(
    @SerializedName("genres")
    val genreDtos: List<GenreDto>
)