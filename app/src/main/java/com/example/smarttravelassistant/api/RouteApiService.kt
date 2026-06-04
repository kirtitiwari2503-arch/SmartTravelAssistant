package com.example.smarttravelassistant.api

import retrofit2.http.GET
import retrofit2.http.Query

interface RouteApiService {

    @GET("v1/routing")
    suspend fun getRoute(
        @Query("waypoints") waypoints: String,
        @Query("mode") mode: String,
        @Query("apiKey") apiKey: String
    ): RouteResponse
}