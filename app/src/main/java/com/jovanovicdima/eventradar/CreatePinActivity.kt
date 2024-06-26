package com.jovanovicdima.eventradar

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.jovanovicdima.eventradar.network.Firebase
import com.jovanovicdima.eventradar.services.CameraService
import com.jovanovicdima.eventradar.services.LocationViewModel
import com.jovanovicdima.eventradar.ui.theme.EventRadarTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CreatePinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventRadarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreatePinScreen(
                        onPinUpload = {
                            finish()
                        }
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePinScreen(onPinUpload: () -> Unit) {
    val context = LocalContext.current
    var job by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    val locationViewModel = LocationViewModel()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val startTimePickerState = rememberTimePickerState()
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val endDatePickerState = rememberDatePickerState()
    val endTimePickerState = rememberTimePickerState()

    // Validation
    var invalidLocation by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var location by remember { mutableStateOf<LatLng?>(null) } // For advanced option
    var previewImageUri by remember { mutableStateOf<Uri?>(null) }
    var startDatetime by remember { mutableStateOf(getDatetime(startDatePickerState.selectedDateMillis ?: System.currentTimeMillis(), startTimePickerState.hour, startTimePickerState.minute)) }
    var endDatetime by remember { mutableStateOf(getDatetime(endDatePickerState.selectedDateMillis ?: System.currentTimeMillis(), endTimePickerState.hour, endTimePickerState.minute)) }

    val cameraService = CameraService(context)
    cameraService.Setup {
        previewImageUri = it
        Log.e("CAMERA", "RegisterScreen: $it", )
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 50.dp)
    ) {
        // Title
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            value = title,
            onValueChange = {
                title = it
            },
            label = { Text("Title") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
        )

        // Address
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 4.dp),
            singleLine = true,
            value = address,
            onValueChange = { newAddress ->
                address = newAddress
                job?.cancel() // Cancel previous job if user is still typing
                job = coroutineScope.launch {
                    delay(300) // Debounce user input
                    suggestions = locationViewModel.getAddressSuggestions(newAddress)
                    Log.e("TEST", "CreatePinScreen: run", )
                }
            },
            label = { Text("Enter Address") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            trailingIcon = {
                IconButton(onClick = {
                    address = ""
                    suggestions = emptyList()
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear Field")
                }
            },
            isError = invalidLocation
        )

        // Address suggestions
        LazyColumn(
            modifier = Modifier
                .animateContentSize()
                .heightIn(0.dp, 256.dp)
        ) {
            itemsIndexed(
                items = suggestions,
                key = { index: Int, item: String -> "$item-$index" }
            ) { _, suggestion ->
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
                        suggestions = emptyList()
                        coroutineScope.launch {
                            address = suggestion
                            location = locationViewModel.getLocationFromAddress(suggestion)
                            invalidLocation = location == null
                        }
                    }
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        text = suggestion,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 8.dp),
            onClick = {
                /*TODO*/
                // turn off address suggestions
                // put lat long string in address field
            }
        ) {
            Text("Get Current Location")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Button(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                onClick = {
                    showStartDatePicker = true
                }
            ) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = "Start: $startDatetime",
                    textAlign = TextAlign.Center
                )
            }

            Button(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                onClick = {
                    showEndDatePicker = true
                }
            ) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = "End: $endDatetime",
                    textAlign = TextAlign.Center
                )
            }
        }

        // start date picker component
        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showStartDatePicker = false
                            showStartTimePicker = true
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showStartDatePicker = false
                            showStartTimePicker = false
                        }
                    ) { Text("Cancel") }
                }
            )
            {
                DatePicker(state = startDatePickerState)
            }
        }

        // start time picker component
        if (showStartTimePicker) {
            TimePickerDialog(
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showStartTimePicker = false
                            startDatetime = getDatetime(startDatePickerState.selectedDateMillis!!, startTimePickerState.hour, startTimePickerState.minute)
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showStartTimePicker = false
                        }
                    ) { Text("Cancel") }
                }
            )
            {
                TimePicker(state = startTimePickerState)
            }
        }

        // end date picker component
        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showEndDatePicker = false
                            showEndTimePicker = true
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showEndDatePicker = false
                            showEndTimePicker = false
                        }
                    ) { Text("Cancel") }
                }
            )
            {
                DatePicker(state = endDatePickerState)
            }
        }

        // end time picker component
        if (showEndTimePicker) {
            TimePickerDialog(
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showEndTimePicker = false
                            endDatetime = getDatetime(endDatePickerState.selectedDateMillis!!, endTimePickerState.hour, endTimePickerState.minute)
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showEndTimePicker = false
                        }
                    ) { Text("Cancel") }
                }
            )
            {
                TimePicker(state = endTimePickerState)
            }
        }

        // Description
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .heightIn(0.dp, 256.dp),
            singleLine = false,
            value = description,
            onValueChange = {
                description = it
            },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Default,
                keyboardType = KeyboardType.Text
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Button(
                onClick = { cameraService.uploadPicture() },
                Modifier.weight(1f)
            ) {
                Text(
                    "Upload Picture",
                    color = MaterialTheme.colorScheme.background
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { cameraService.takePicture() },
                Modifier.weight(1f)
            ) {
                Text(
                    "Take Picture",
                    color = MaterialTheme.colorScheme.background
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(256.dp)
                .padding(16.dp)
                .clip(RectangleShape)
        ) {
            AsyncImage(
                model = previewImageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Create pin
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {
                    if(location == null) {
                        Toast.makeText(context, "Location cannot be unknown.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if(previewImageUri == null) {
                        Toast.makeText(context, "Preview image must be set.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if(getMillisFromDatetime(startDatetime) >= getMillisFromDatetime(endDatetime)) {
                        Toast.makeText(context, "Start time must be earlier then end time.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    Firebase.uploadEvent(
                        title = title,
                        location = location!!,
                        description = description,
                        image = cameraService.loadImageBitmapFromUri(previewImageUri!!),
                        startDatetime = startDatetime,
                        endDatetime = endDatetime,
                        successCallback = {
                            onPinUpload()
                        },
                        failureCallback = {
                            Toast.makeText(context, "Couldn't create pin", Toast.LENGTH_SHORT).show()
                        }
                    )

                }
            ) {
                Text("Create Pin")
            }
        }
    }
}

private val dateFormat = "MMMM dd, yyyy"
private val datetimeFormat = "MMMM dd, yyyy HH:mm"
private fun getDatetime(dateMillis: Long, hour: Int, minute: Int): String {
    val date = Date(dateMillis)
    val datesdf = SimpleDateFormat(dateFormat, Locale.getDefault())
    return "${datesdf.format(date)} ${String.format("%02d", hour)}:${String.format("%02d", minute)}"
}

private fun getMillisFromDatetime(datetime: String): Long {
    val datetimesdf = SimpleDateFormat(datetimeFormat, Locale.getDefault())
    return datetimesdf.parse(datetime)!!.time
}

@Composable
private fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePinPreview() {
    EventRadarTheme {
        CreatePinScreen({})
    }
}