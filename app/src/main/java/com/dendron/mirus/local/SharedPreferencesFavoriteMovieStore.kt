package com.dendron.mirus.local

import android.content.SharedPreferences
import com.dendron.mirus.common.Constants
import com.dendron.mirus.domain.repository.FavoriteMovieStore

class SharedPreferencesFavoriteMovieStore(
    private val sharedPreferences: SharedPreferences
) : FavoriteMovieStore {

    override fun saveMovie(data: String) {
        with(sharedPreferences.edit()) {
            putString(Constants.FAVORITES_KEY, data)
            commit()
        }

    }

    override fun getMovies(): String {
        return sharedPreferences.getString(Constants.FAVORITES_KEY, "").toString()
    }
}