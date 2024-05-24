package com.jovanovicdima.eventradar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jovanovicdima.eventradar.network.Firebase
import com.jovanovicdima.eventradar.ui.theme.EventRadarTheme


/*  TODO:   Validate fileds
 *          password minimum 6 characters
 *          validate email
 *
 *
 */
class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventRadarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterScreen(
                        /* TODO:    Set firestore writing rule to true only to logged users and
                         *          fill username - email collection
                         */
                        onRegisterClick = { email, password, username, fullName, phoneNumber, image ->
                            Firebase.createAccount(email, password, username, fullName, phoneNumber, image, {
                                startActivity(Intent(this, MainScreenActivity::class.java))
                                finish()
                            }) {
                                Toast.makeText(this, "Couldn't create account", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        onLoginButton = {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(onRegisterClick: (String, String, String, String, String, ImageBitmap) -> Unit, onLoginButton: () -> Unit) {
    val context = LocalContext.current.applicationContext

    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.e("URI", result.data?.data.toString())
        if (result.resultCode == Activity.RESULT_OK) {

            Log.e("URI", result.data.toString())
            imageUri = result.data?.data
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Profile Picture
        imageUri.let { uri ->
            Surface(
                modifier = Modifier
                    .size(256.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Username
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp),
            value = username,
            onValueChange = { username = it },
            singleLine = true,
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
        )

        // Full Name
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp),
            value = fullName,
            onValueChange = { fullName = it },
            singleLine = true,
            label = { Text("Full Name") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
        )

        // Email address
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp),
            value = email,
            // TODO: check if email is valid
            onValueChange = { email = it },
            singleLine = true,
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
        )

        // Password
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp),
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            label = { Text("Password") },
            visualTransformation = if(passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            trailingIcon = {
                val icon = if(passwordVisible) {
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
                    Icon(imageVector  = icon, description)
                }
            }
        )

        // Phone Number
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp),
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            singleLine = true,
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Phone
            ),
        )

        Row() {
            Button(
                onClick = { launchImagePicker(imagePickerLauncher) },
                Modifier.weight(1f)
            ) {
                Text(
                    "Upload Picture",
                    color = MaterialTheme.colorScheme.background
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
//                    takePictureLauncher.launch(null)
                },
                Modifier.weight(1f)
            ) {
                Text(
                    "Take Picture",
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
        Button(
            onClick = { onRegisterClick(email, password, username, fullName, phoneNumber, loadImageFromUri(imageUri!!, context)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Register",
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
            Text("Already Been Here?", style = TextStyle(fontSize = 16.sp))
            Spacer(modifier = Modifier.width(8.dp))
            ClickableText(
                text = AnnotatedString("Log in"),
                onClick = { onLoginButton() },
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

fun launchImagePicker(imagePickerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
    }
    imagePickerLauncher.launch(intent)
}

fun loadImageFromUri(uri: Uri, context: Context): ImageBitmap {
    // Example with ImageDecoder (requires API level 28)
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    val bitmap = ImageDecoder.decodeBitmap(source)
    return bitmap.asImageBitmap()  // Assuming `image` is a variable holding your ImageBitmap
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EventRadarTheme {
        RegisterScreen(
            onLoginButton = {},
            onRegisterClick = { _, _, _, _, _, _ ->}
        )
    }
}