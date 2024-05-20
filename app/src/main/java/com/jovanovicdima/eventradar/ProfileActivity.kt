package com.jovanovicdima.eventradar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Profile() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            Modifier.size(256.dp)
        )
        Spacer(modifier = Modifier.width(64.dp))
        Text(text = "jovanovic.dima")

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
                Text(text = "Dimitrije Jovanovic")
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
                Text(text = "jovanovic.dima@proton.me")
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
                Text(text = "0611414114")
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            thickness = 1.dp
        )
    }
}

@Preview
@Composable
fun PreviewProfile() {
    Profile()
}