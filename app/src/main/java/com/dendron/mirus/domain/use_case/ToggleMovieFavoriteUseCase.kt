package com.dendron.mirus.domain.use_case

import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ToggleMovieFavoriteUseCase @Inject constructor(private val favoriteMovieRepository: FavoriteMovieRepository) {
    operator fun invoke(movie: Movie, isFavorite: Boolean) {
        try {
            if (isFavorite) {
                favoriteMovieRepository.removeFavoriteMovie(movie)
            } else {
                favoriteMovieRepository.saveFavoriteMovie(movie)
            }
        } catch (e: HttpException) {
        } catch (e: IOException) {
        }
    }
}