package com.dendron.mirus.data.local.remote.dto

import com.google.gson.annotations.SerializedName

data class ResultsDto<T>(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val resultDto: List<T>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)
