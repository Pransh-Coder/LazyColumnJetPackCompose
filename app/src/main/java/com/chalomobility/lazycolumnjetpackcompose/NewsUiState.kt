package com.chalomobility.lazycolumnjetpackcompose

data class NewsUiState(
    val isLoading:Boolean = false,
    val newsItems: List<NewsData.Articles> = listOf(),
    val errorMessage: String ?= null
)
