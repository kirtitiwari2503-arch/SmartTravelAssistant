package com.example.smarttravelassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarttravelassistant.api.GeocodeRetrofitInstance
import com.example.smarttravelassistant.api.RouteRetrofitInstance
import com.example.smarttravelassistant.api.WeatherRetrofitInstance
import kotlinx.coroutines.launch

private const val WEATHER_API_KEY = "8fa82f0255dbc96cba1419078db0de62"
private const val GEOAPIFY_API_KEY = "42d57ae00a5246278639c7478bd1da56"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartTravelAssistantLite()
                }
            }
        }
    }
}

@Composable
fun SmartTravelAssistantLite() {

    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var outputDetails by remember { mutableStateOf("") }
    var showResultScreen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    if (!showResultScreen) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "🌍 Smart Travel Assistant",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = fromLocation,
                onValueChange = { fromLocation = it },
                label = { Text("From") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = toLocation,
                onValueChange = { toLocation = it },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    coroutineScope.launch {

                        try {
                            isLoading = true

                            // WEATHER API
                            val weatherResponse =
                                WeatherRetrofitInstance.apiService.getWeather(
                                    cityName = toLocation,
                                    apiKey = WEATHER_API_KEY
                                )

                            val weather =
                                weatherResponse.weather.firstOrNull()?.description
                                    ?: "Unknown"

                            val temp = weatherResponse.main.temp

                            // GEOCODING
                            val fromCoordinates =
                                GeocodeRetrofitInstance.apiService.getCoordinates(fromLocation)

                            val toCoordinates =
                                GeocodeRetrofitInstance.apiService.getCoordinates(toLocation)

                            if (fromCoordinates.isEmpty() || toCoordinates.isEmpty()) {
                                outputDetails = "❌ Location not found"
                                isLoading = false
                                showResultScreen = true
                                return@launch
                            }

                            val fromLat = fromCoordinates[0].lat.toDouble()
                            val fromLon = fromCoordinates[0].lon.toDouble()
                            val toLat = toCoordinates[0].lat.toDouble()
                            val toLon = toCoordinates[0].lon.toDouble()

                            // GEOAPIFY ROUTE
                            val waypoints = "$fromLat,$fromLon|$toLat,$toLon"

                            val routeResponse =
                                RouteRetrofitInstance.apiService.getRoute(
                                    waypoints = waypoints,
                                    mode = "drive",
                                    apiKey = GEOAPIFY_API_KEY
                                )

                            val route = routeResponse.features.first().properties

                            val distanceKm = route.distance / 1000
                            val durationHours = route.time / 3600

                            outputDetails = """
🚗 TRAVEL BLUEPRINT

📍 From: $fromLocation
📍 To: $toLocation

📏 Distance:
${String.format("%.2f", distanceKm)} km

⏱ Travel Time:
${String.format("%.2f", durationHours)} hrs

🌤 Weather:
$weather

🌡 Temperature:
$temp °C

✅ Travel Status:
Safe To Travel
                            """.trimIndent()

                            isLoading = false
                            showResultScreen = true

                        } catch (e: Exception) {
                            isLoading = false
                            outputDetails = "❌ Error:\n${e.localizedMessage}"
                            showResultScreen = true
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(if (isLoading) "Loading..." else "Calculate Travel Blueprint")
            }
        }

    } else {

        ResultScreen(
            outputData = outputDetails,
            onBackClick = {
                showResultScreen = false
            }
        )
    }
}

@Composable
fun ResultScreen(
    outputData: String,
    onBackClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = onBackClick) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "🗺️ Travel Result",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            SelectionContainer {

                Text(
                    text = outputData,
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}