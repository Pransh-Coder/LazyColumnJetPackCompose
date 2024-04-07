package com.chalomobility.lazycolumnjetpackcompose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<NewsUiState> = MutableStateFlow(NewsUiState(isLoading = true))
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    var uiState2 by mutableStateOf(NewsUiState(isLoading = true))
        private set

    fun getNewsData(){
        viewModelScope.launch {
            try {
                val newsListArticles = newsRepository.getNewsData()
                _uiState.update {
                    it.copy(isLoading = false, newsItems = newsListArticles.body()?.articles!!)
                }
                uiState2 = uiState2.copy(isLoading = false, newsItems = newsListArticles.body()?.articles!!)
            }catch (e: Exception){
                // Handle the error and notify the UI when appropriate.
                _uiState.update {
                    //val messages = getMessagesFromThrowable(ioe)
                    it.copy(isLoading = false, errorMessage = e.message)
                }
                uiState2 = uiState2.copy(isLoading = false, errorMessage = e.message)
            }

        }
    }
}