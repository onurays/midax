package com.onuray.midax.di

import com.onuray.midax.BuildConfig
import com.onuray.midax.data.remote.api.StocksApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val originalUrl = original.url
        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("token", BuildConfig.MIDAX_PRO_API_KEY)
            .build()
        val newReq = original.newBuilder().url(newUrl).build()
        chain.proceed(newReq)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.BASIC
        }

    @Provides @Singleton
    fun provideOkHttp(
        auth: Interceptor,
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(auth)
        .addInterceptor(logging)
        .build()

    @Provides @Singleton
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideStocksApi(retrofit: Retrofit): StocksApi =
        retrofit.create(StocksApi::class.java)
}
