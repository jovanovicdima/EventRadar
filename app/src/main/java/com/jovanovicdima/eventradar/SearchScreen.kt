package com.jovanovicdima.eventradar

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jovanovicdima.eventradar.data.Event
import com.jovanovicdima.eventradar.navigation.Screens
import com.jovanovicdima.eventradar.network.Firebase
import com.jovanovicdima.eventradar.services.LocationViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Search(navigationController: NavHostController) {
    val location = LocationViewModel()
    var events by remember { mutableStateOf(listOf<Event>()) }
    Firebase.getAllEvents {
        events = it
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .animateContentSize()
        ) {
            itemsIndexed(
                items = events,
                key = { index, item -> "$item-$index" }
            ) {_, item ->
                var address by remember { mutableStateOf<String?>("") }
                val coroutineScope = rememberCoroutineScope()
                coroutineScope.launch {
                    address = location.getAddressFromLocation(item.latitude!!, item.longitude!!)
                }

                Button(
                    modifier = Modifier
                        .animateItem()
                        .padding(16.dp)
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background,
                            RectangleShape
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                    onClick = {
                        navigationController.navigate(Screens.EventInfo.screen + "/${item.id}")
                    },
                    shape = RectangleShape
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            text = item.title,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            text = "${item.startDatetime} - ${item.endDatetime}",
                            textAlign = TextAlign.Center
                        )
                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            text = "Location: " + (address ?: "${item.latitude}° ${item.longitude}°"),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
    }
}