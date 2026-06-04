package com.example.smarttravelassistant.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search")
    suspend fun getCoordinates(
        @Query("q") cityName: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): List<GeocodeResponse>

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}