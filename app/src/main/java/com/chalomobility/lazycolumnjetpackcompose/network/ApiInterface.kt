package com.chalomobility.lazycolumnjetpackcompose.network

import com.chalomobility.lazycolumnjetpackcompose.NewsData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("news-api-feed/staticResponse.json")
    suspend fun getNewsData(): Response<NewsData>
}