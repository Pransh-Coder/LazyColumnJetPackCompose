package com.chalomobility.lazycolumnjetpackcompose

import com.chalomobility.lazycolumnjetpackcompose.network.ApiInterface
import retrofit2.Response
import javax.inject.Inject

class NewsRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun getNewsData():Response<NewsData>{
        return apiInterface.getNewsData()
    }
}