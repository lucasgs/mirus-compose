package com.dendron.mirus.di

import android.content.Context
import com.dendron.mirus.common.Constants
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import com.dendron.mirus.domain.repository.FavoriteMovieStore
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.local.LocalFavoriteMovieRepository
import com.dendron.mirus.local.SharedPreferencesFavoriteMovieStore
import com.dendron.mirus.remote.TheMovieDBApi
import com.dendron.mirus.remote.TheMovieDbRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideFavoriteMovieStore(@ApplicationContext appContext: Context): FavoriteMovieStore {
        return SharedPreferencesFavoriteMovieStore(
            appContext.getSharedPreferences(
                Constants.PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
        )
    }

    @Provides
    @Singleton
    fun provideLocalFavoriteMovieRepository(
        localStore: FavoriteMovieStore
    ): FavoriteMovieRepository {
        return LocalFavoriteMovieRepository(localStore)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        api: TheMovieDBApi
    ): MovieRepository {
        return TheMovieDbRepository(api)
    }
}