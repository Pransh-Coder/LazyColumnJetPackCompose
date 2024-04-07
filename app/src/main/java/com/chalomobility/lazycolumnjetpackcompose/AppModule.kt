package com.chalomobility.lazycolumnjetpackcompose

import android.content.Context
import com.chalomobility.lazycolumnjetpackcompose.Constants.BASE_URL
import com.chalomobility.lazycolumnjetpackcompose.network.ApiInterface
import com.chalomobility.lazycolumnjetpackcompose.network.NetworkModule
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// A binding contains the information necessary to provide instances of a type as a dependency.
// One way to provide binding information to Hilt is constructor injection.
// Use the @Inject annotation on the constructor of a class to tell Hilt how to provide instances of that class:

// ActivityComponent - inject for Activity, it means retain until activity lifecycle.
// This annotation means that all of the dependencies in Module are available in all of the app's activities.

@Module
// you must annotate Hilt modules with "@InstallIn" to tell Hilt which Android class each module will be used or installed in.
@InstallIn(SingletonComponent::class) // injection for whole application
object AppModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return Constants.BASE_URL
    }

    @Provides
    @Singleton
    fun provideConnectionTimeout(): Long = Constants.NETWORK_TIMEOUT

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context,connectionTimeout:Long): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build(),
            )
            .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, gson: Gson, client: OkHttpClient): ApiInterface {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun providesRepository(apiInterface: ApiInterface): NewsRepository {
        return NewsRepository(apiInterface = apiInterface)
    }
}