package com.dendron.mirus.domain.use_case

import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetFavoritesMovieUseCase @Inject constructor(private val favoriteMovieRepository: FavoriteMovieRepository) {
    operator fun invoke(): Flow<Resource<List<Int>>> = flow {
        try {
            emit(Resource.Loading())
            val favorites = favoriteMovieRepository.getFavoritesMovie()
            emit(Resource.Success(data = favorites))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage))
        }
    }
}