package com.dendron.mirus.domain.use_case

import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ToggleMovieFavoriteUseCase @Inject constructor(private val favoriteMovieRepository: FavoriteMovieRepository) {
    operator fun invoke(movie: Movie, isFavorite: Boolean): Flow<Boolean> = flow {
        try {
            if (isFavorite) {
                favoriteMovieRepository.removeFavoriteMovie(movie)
            } else {
                favoriteMovieRepository.saveFavoriteMovie(movie)
            }
            emit(true)
        } catch (e: HttpException) {
            emit(false)
        } catch (e: IOException) {
            emit(false)
        } catch (e: Exception) {
            emit(false)
        }
    }
}