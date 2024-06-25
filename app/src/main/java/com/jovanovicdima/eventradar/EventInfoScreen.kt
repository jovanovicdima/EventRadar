package com.jovanovicdima.eventradar

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jovanovicdima.eventradar.data.Event
import com.jovanovicdima.eventradar.network.Firebase

@Composable
fun EventInfo(eventID: String) {
    Log.e("EVENT", "EventInfo: $eventID", )
    var eventInfo by remember { mutableStateOf<Event?>(null) }
    Firebase.getEventInfo(eventID) {
        eventInfo = it
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = eventInfo?.title ?: "",
            textAlign = TextAlign.Center
        )
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = eventInfo?.description ?: "",
            textAlign = TextAlign.Center
        )
    }
}