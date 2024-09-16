package com.example.mycatapi.network

import com.example.mycatapi.network.model.CatCatalog
import retrofit2.http.GET
import retrofit2.http.Query

interface TheCatApiService {
    @GET("images/search")
    suspend fun catSearch(
        @Query("limit") limit: Int
    ): List<CatCatalog>

}