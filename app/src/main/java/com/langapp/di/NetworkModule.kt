package com.langapp.di

import com.langapp.BuildConfig
import com.langapp.data.local.TokenDataStore
import com.langapp.data.remote.api.*
import com.langapp.data.remote.interceptor.AuthInterceptor
import com.langapp.data.remote.interceptor.BearerTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideLogging() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides @Singleton
    fun provideOkHttp(
        tokenDataStore: TokenDataStore,
        authInterceptor: AuthInterceptor,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .authenticator(authInterceptor)
        .addInterceptor(BearerTokenInterceptor(tokenDataStore))
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Una funcion por cada API:
    @Provides @Singleton
    fun provideAuthApi(r: Retrofit) = r.create(AuthApi::class.java)
    @Provides @Singleton
    fun provideLanguageApi(r: Retrofit) = r.create(LanguageApi::class.java)
    @Provides @Singleton
    fun provideLevelApi(r: Retrofit) = r.create(LevelApi::class.java)
    @Provides @Singleton
    fun provideLessonApi(r: Retrofit) = r.create(LessonApi::class.java)
    @Provides @Singleton
    fun provideExerciseApi(r: Retrofit) = r.create(ExerciseApi::class.java)
    @Provides @Singleton
    fun provideProfileApi(r: Retrofit) = r.create(ProfileApi::class.java)
    @Provides @Singleton
    fun provideEnrollmentApi(r: Retrofit) = r.create(EnrollmentApi::class.java)
    @Provides @Singleton
    fun provideProgressApi(r: Retrofit) = r.create(ProgressApi::class.java)
}
