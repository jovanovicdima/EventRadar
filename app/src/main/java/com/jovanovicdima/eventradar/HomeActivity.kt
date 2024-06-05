package com.jovanovicdima.eventradar

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Home() {
    GoogleMap {
        ShowMarker(LatLng(37.7749, -122.4194), "Evrovizijska Zurka", "https://firebasestorage.googleapis.com/v0/b/event-radar-b7978.appspot.com/o/images%2FoevX8zMKcxSftvwseoT4qJ31e282.jpg?alt=media&token=122f1305-0ebf-4f37-8e45-616b0d261adb")
    }
}

@Composable
fun ShowMarker(location: LatLng, title: String, previewImageLink: String) {
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