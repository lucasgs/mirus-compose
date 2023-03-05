package com.dendron.mirus.di

import com.dendron.mirus.common.Constants
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.remote.TheMovieDBApi
import com.dendron.mirus.remote.TheMovieDbRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoviesApi(): TheMovieDBApi {
        val moviesClient = OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .client(moviesClient)
            .baseUrl(Constants.TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TheMovieDBApi::class.java)
    }

    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url
            .newBuilder()
            .addQueryParameter("api_key", Constants.tmdbApiKey)
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(api: TheMovieDBApi): MovieRepository {
        return TheMovieDbRemoteRepository(api)
    }
}