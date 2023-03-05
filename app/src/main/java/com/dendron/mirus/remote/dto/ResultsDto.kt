package com.dendron.mirus.remote.dto

import com.google.gson.annotations.SerializedName

data class ResultsDto(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val resultDto: List<ResultDto>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

