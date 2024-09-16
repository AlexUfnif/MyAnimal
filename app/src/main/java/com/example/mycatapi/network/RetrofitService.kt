package com.example.mycatapi.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    //private val BASE_URL = "https://api.thecatapi.com/v1/"

    //    val api: TheCatApiService by lazy {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BaseURL.BASE_URL_CAT)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        retrofit.create(TheCatApiService::class.java)
//    }
    private var retrofit: Retrofit? = null

    fun getApiService(baseUrl: String): TheCatApiService {
        if (retrofit == null || retrofit?.baseUrl().toString() != baseUrl) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(TheCatApiService::class.java)
    }

}

object BaseURL {
    val BASE_URL_CAT = "https://api.thecatapi.com/v1/"
    val BASE_URL_DOG = "https://api.thedogapi.com/v1/"
}