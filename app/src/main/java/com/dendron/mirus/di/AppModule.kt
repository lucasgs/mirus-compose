package com.dendron.mirus.di

import android.content.Context
import androidx.room.Room
import com.dendron.mirus.common.Constants
import com.dendron.mirus.data.device.AndroidNetworkChecker
import com.dendron.mirus.data.local.AppDatabase
import com.dendron.mirus.data.remote.TheMovieDBApi
import com.dendron.mirus.data.repository.FavoriteMovieRepositoryImp
import com.dendron.mirus.data.repository.GenreRepositoryImp
import com.dendron.mirus.data.repository.MovieRepositoryImp
import com.dendron.mirus.domain.NetworkChecker
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import com.dendron.mirus.domain.repository.GenreRepository
import com.dendron.mirus.domain.repository.MovieRepository
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
    fun provideLocalFavoriteMovieRepository(
        appDatabase: AppDatabase
    ): FavoriteMovieRepository {
        return FavoriteMovieRepositoryImp(appDatabase.favoriteDao())
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        api: TheMovieDBApi, appDatabase: AppDatabase
    ): MovieRepository {
        return MovieRepositoryImp(api, appDatabase)
    }

    @Provides
    @Singleton
    fun provideGenreRepository(
        api: TheMovieDBApi,
        appDatabase: AppDatabase
    ): GenreRepository {
        return GenreRepositoryImp(api, appDatabase)
    }

    @Provides
    @Singleton
    fun provideFavoriteDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, Constants.DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNetworkChecker(@ApplicationContext appContext: Context): NetworkChecker {
        return AndroidNetworkChecker(appContext)
    }
}