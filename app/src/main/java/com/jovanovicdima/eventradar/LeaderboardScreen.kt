package com.jovanovicdima.eventradar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jovanovicdima.eventradar.data.Event
import com.jovanovicdima.eventradar.data.User
import com.jovanovicdima.eventradar.network.Firebase

@Composable
fun Leaderboard() {
    var leaderboard by remember { mutableStateOf(listOf<Pair<Int, String>>()) }

    Firebase.getLeaderboardScores { list ->
        leaderboard = list
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
                items = leaderboard,
                key = { index, item -> "$item-$index" }
            ) { _, item ->
                var user by remember { mutableStateOf(User()) }
                Firebase.getUser(item.second) {
                    user = it!!
                }
                Button(
                    onClick = {
                        // show user profile
                    }
                ) {
                    Text(
                        text = "User: ${user.username} - Score: ${item.first}"
                    )
                }
            }

        }
    }
}