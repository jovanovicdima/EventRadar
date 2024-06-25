package com.jovanovicdima.eventradar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jovanovicdima.eventradar.data.Event
import com.jovanovicdima.eventradar.network.Firebase

@Composable
fun Search() {
    var events by remember { mutableStateOf(listOf<Event>()) }
    Firebase.getAllPins {
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
                .heightIn(0.dp, 256.dp)
        ) {
            itemsIndexed(
                items = events,
                key = { index, item -> "$item-$index" }
            ) {_, item ->
                Button(
                    modifier = Modifier
                        .animateItem()
                        .padding(2.dp)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.background,
                            RectangleShape
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                    onClick = {
                    }
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        text = item.title,
                        textAlign = TextAlign.Center
                    )

                }
            }
        }
    }
}