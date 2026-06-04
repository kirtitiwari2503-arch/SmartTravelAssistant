package com.example.smarttravelassistant.api

// -------------------- GEOCODING (OpenStreetMap) --------------------
data class GeocodeResponse(
    val lat: String,
    val lon: String,
    val display_name: String
)

// -------------------- WEATHER (OpenWeatherMap) --------------------
data class WeatherResponse(
    val main: MainData,
    val weather: List<WeatherSummary>
)

data class MainData(val temp: Double)

data class WeatherSummary(val description: String)

// -------------------- ROUTE (Geoapify) --------------------
data class RouteResponse(
    val features: List<RouteFeature>
)

data class RouteFeature(
    val properties: RouteProperties
)

data class RouteProperties(
    val distance: Double,
    val time: Double
)