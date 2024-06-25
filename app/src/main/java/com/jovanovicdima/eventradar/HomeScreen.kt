package com.jovanovicdima.eventradar

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.jovanovicdima.eventradar.data.Event
import com.jovanovicdima.eventradar.data.LocationInfo
import com.jovanovicdima.eventradar.network.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Home() {
    var events: List<Event> = emptyList()
    Firebase.getAllPins {
        events = it
    }
    var userLocation by remember { mutableStateOf<Location?>(LocationInfo.location) }

    fun subscriber(location: Location?) {
        userLocation = location
        Log.e("HOME", "Home: $location")
    }

    LocationInfo.Subscribe(::subscriber)

    GoogleMap {
        if(userLocation != null) {
            MarkerComposable(
                state = MarkerState(position = LatLng(userLocation!!.latitude, userLocation!!.longitude)),
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(128.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(128.dp)
                            )
                    )
                }
            }
        }
        events.let {
            for (pin in it) {
                ShowMarker(pin.id, LatLng(pin.latitude!!, pin.longitude!!), pin.title, pin.preview, pin.startDatetime, pin.endDatetime)
            }
        }
    }
}

@Composable
fun ShowMarker(id: String, location: LatLng, title: String, previewImageLink: String, startDatetime: String, endDatetime: String) {
    val context = LocalContext.current
    var bitmap: ImageBitmap? = null
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(previewImageLink) {
        coroutineScope.launch {
            bitmap = loadImageBitmap(context, previewImageLink)?.asImageBitmap()
        }
    }

    MarkerInfoWindow(
        state = MarkerState(location),
        onInfoWindowClick = {
            Log.e("MARKER", "Home: marker clicked", )
        }
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(4.dp))
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier
                        .width(256.dp)
                        .padding(8.dp),
                    text = title,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .width(256.dp)
                        .padding(8.dp),
                    text = "$startDatetime - $endDatetime",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                bitmap.let {
                    if (it != null) {
                        Image(
                            modifier = Modifier
                                .size(256.dp, 144.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            bitmap = it,
                            contentDescription = "image",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}


suspend fun loadImageBitmap(context: Context, url: String): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false) // Disable hardware bitmaps to prevent issues with conversion
        .build()

    val result = withContext(Dispatchers.IO) {
        loader.execute(request)
    }

    return if (result is SuccessResult) {
        result.drawable.toBitmap()
    } else {
        null
    }
}