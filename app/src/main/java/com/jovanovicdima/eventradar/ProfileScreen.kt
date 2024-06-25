package com.jovanovicdima.eventradar

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jovanovicdima.eventradar.network.getUser

@Composable
fun Profile(id: String) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }

    Log.e("USER1", id)
    getUser(id) {
        if (it != null) {
            username = it.username
            fullName = it.fullName
            email = it.email
            phone = it.phoneNumber
            image = it.image
        }
    }

    Log.e("USER", username)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(256.dp)
                .clip(CircleShape)
        ) {
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = username)

        Spacer(modifier = Modifier.height(128.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp, 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp, 0.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .size(26.dp),
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                )
            }
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = fullName)
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp, 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp, 0.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .size(26.dp),
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                )
            }
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = email)
            }
        }


        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp, 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp, 0.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .size(26.dp),
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                )
            }
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = phone)
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            thickness = 1.dp
        )
    }

}