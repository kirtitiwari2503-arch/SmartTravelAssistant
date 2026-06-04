package com.example.smarttravelassistant.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RouteRetrofitInstance {

    private const val BASE_URL =
        "https://api.geoapify.com/"

    val apiService: RouteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RouteApiService::class.java)
    }
}