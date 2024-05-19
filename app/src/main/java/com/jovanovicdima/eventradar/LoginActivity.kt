package com.jovanovicdima.eventradar

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jovanovicdima.eventradar.network.Firebase
import com.jovanovicdima.eventradar.ui.theme.EventRadarTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventRadarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    LoginScreen(
                        onLoginClick =  { username, password ->
                            Firebase.login(username, password) {
                                startActivity(Intent(this, MainScreenActivity::class.java))
                                finish()
                            }
                        },
                        onRegisterButton = {
                            startActivity(Intent(this, RegisterActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginClick: (String, String) -> Unit, onRegisterButton: (Unit) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    // TODO: Implement verification for username and password fields

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { username = it },
            singleLine = true,
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            label = { Text("Password") },
            visualTransformation = if(passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if(passwordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }

                // Please provide localized description for accessibility services
                val description = if(passwordVisible) {
                    "Hide password"
                } else {
                    "Show password"
                }

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector  = image, description)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLoginClick(username, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Login",
                color = MaterialTheme.colorScheme.background
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("New Around Here?", style = TextStyle(fontSize = 16.sp))
            Spacer(modifier = Modifier.width(8.dp))
            ClickableText(
                text = AnnotatedString("Create Account"),
                onClick = { onRegisterButton(Unit) },
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}


@Preview
@Composable
fun Preview() {
    EventRadarTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoginScreen({ _, _ -> }, { _ -> })
        }
    }
}