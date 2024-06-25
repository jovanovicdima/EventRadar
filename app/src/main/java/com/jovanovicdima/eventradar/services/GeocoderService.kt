package com.jovanovicdima.eventradar.services

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
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
            try {


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
            catch (e: Exception) {
                Log.e("GeocoderService", "getAddressSuggestions: $e", )
                emptyList()
            }

        }
    }

    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            val url = URL("https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$latitude&lon=$longitude")
            val connection = url.openConnection()
            try {
                val inputStream = connection.getInputStream()
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonString)
                val address = jsonObject.getJSONObject("address")
                val houseNumber = address.getString("house_number")
                val road = address.getString("road")
                val city = address.getString("city")
                val country = address.getString("country")
                "$road $houseNumber, $city, $country"
            }
            catch (e: Exception) {
                Log.e("GeocoderService", "getAddressSuggestions: $e", )
                null
            }

        }

    }

}