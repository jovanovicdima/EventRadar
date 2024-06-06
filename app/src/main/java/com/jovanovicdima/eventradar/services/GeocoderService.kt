package com.jovanovicdima.eventradar.services

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class LocationViewModel {
    suspend fun getLocationFromAddress(address: String): LatLng? {
        return withContext(Dispatchers.IO) {
            var location: LatLng? = null

            val url = URL("https://nominatim.openstreetmap.org/search?q=$address&format=json")
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            if (jsonArray.length() > 0) {
                val jsonObject = jsonArray.getJSONObject(0)
                val latitude = jsonObject.getDouble("lat")
                val longitude = jsonObject.getDouble("lon")
                location = LatLng(latitude, longitude)
            }
            location
        }
    }

    suspend fun getAddressSuggestions(address: String): List<String> {
        return withContext(Dispatchers.IO) {
            val url = URL("https://nominatim.openstreetmap.org/search?q=$address&format=json&accept-language=en")
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            val suggestions = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val displayName = jsonObject.getString("display_name")
                suggestions.add(displayName)
            }
            suggestions
        }
    }

}