package com.example.mycatapi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mycatapi.network.RetrofitService
import com.example.mycatapi.network.model.CatCatalog
import kotlinx.coroutines.launch

class MainViewModel(baseUrl: String) : ViewModel() {
    //private val apiService = RetrofitService.api
    //private var apiService = RetrofitService.getApiService(baseUrl)

    var isDarkThemeEnabled: Boolean by mutableStateOf(false)
    val cats: MutableLiveData<List<CatCatalog>> = MutableLiveData(emptyList())
    var baseUrl by mutableStateOf(baseUrl)

    init {
        getCats()
    }

    fun getCats() {
        val apiService = RetrofitService.getApiService(baseUrl)
        viewModelScope.launch {
            try {
                val response = apiService.catSearch(10)
                if (response.isNotEmpty()) {
                    cats.value = response
                }
            } catch (e: Exception) {
                // Обработчик исключения
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val baseUrl:String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(baseUrl = baseUrl) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}