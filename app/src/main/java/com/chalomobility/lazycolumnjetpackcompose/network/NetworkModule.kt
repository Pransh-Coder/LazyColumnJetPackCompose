package com.chalomobility.lazycolumnjetpackcompose.network

import android.content.Context
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class NetworkModule {

    private val gson = Gson()

    private fun retrofit(): Retrofit {

        val okkHttpclient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .build()
        return Retrofit.Builder()
            .client(okkHttpclient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val retrofitService: ApiInterface by lazy {
        retrofit().create(ApiInterface::class.java)
    }

    companion object{
        const val BASE_URL = "https://api.thecatapi.com/v1/"
    }
}