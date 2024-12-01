package fr.polytech.coffeemachineapp.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.model.Device
import fr.polytech.coffeemachineapp.model.Request
import fr.polytech.coffeemachineapp.utils.RequestStatus
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRequestDialog(showDialog: Boolean, device: Device?, uid : String?, onScheduleRequest: (Request) -> Unit, onDismiss: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    var useTimePicker by rememberSaveable { mutableStateOf(false) }

    var selectedAction by rememberSaveable { mutableStateOf("1CUP") }

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Input,
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayedMonthMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = utcTimeMillis
                val selectedYear = calendar.get(Calendar.YEAR)
                val selectedDay = calendar.get(Calendar.DAY_OF_YEAR)

                val currentCalendar = Calendar.getInstance()
                val currentYear = currentCalendar.get(Calendar.YEAR)
                val currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR)

                return if (selectedYear == currentYear) {
                    selectedDay >= currentDay
                } else {
                    selectedYear > currentYear
                }
            }
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + (Calendar.getInstance().get(Calendar.MINUTE) + 5) / 60,
        initialMinute = (Calendar.getInstance().get(Calendar.MINUTE) + 5) % 60,
        is24Hour = true
    )

    if (showDialog) {
        // Implement the UI for the dialog
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Schedule Request") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ActionSelector(selectedAction) { action ->
                        selectedAction = action
                    }
                    DatePicker(datePickerState)
                    if (useTimePicker && datePickerState.displayMode == DisplayMode.Input) {
                        TimePicker(timePickerState)
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            contentDescription = "Use time input",
                            modifier = Modifier.clickable {
                                useTimePicker = false
                            }
                        )
                    }
                    else {
                        TimeInput(timePickerState)
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Use time picker",
                            modifier = Modifier.clickable {
                                datePickerState.displayMode = DisplayMode.Input
                                useTimePicker = true
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Get the timestamp from the date and time pickers
                    val timestamp = datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selectedDateMillis
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        calendar.set(Calendar.SECOND, 0) // Optional: Set seconds to 0
                        calendar.set(Calendar.MILLISECOND, 0) // Optional: Set milliseconds to 0
                        calendar.timeInMillis / 1000L // Convert to seconds
                    }
                    // Create the request
                    if (timestamp != null && device != null && uid != null) {
                        val request = Request(
                            mac = device.mac,
                            uid = uid,
                            timestamp = timestamp / 60 * 60,
                            status = RequestStatus.WAITING,
                            action = selectedAction
                        )
                        Log.d("ScheduleRequestDialog", "Scheduling request: $request")
                        onScheduleRequest(request)
                    }
                    else {
                        Log.d("ScheduleRequestDialog", "timestamp: $timestamp, device: $device, uid: $uid")
                        Log.e("ScheduleRequestDialog", "Error scheduling request")
                    }
                }) {
                    Text("Ok")
                }
            },
            dismissButton = {
                Button(onClick = {
                    onDismiss()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ActionSelector(defaultAction: String = "1CUP", onActionSelected: (String) -> Unit) {
    var selectedAction by rememberSaveable { mutableStateOf(defaultAction) }

    val activeButtonColor = ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )

    val inactiveButtonColor = ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.medium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .weight(0.5f)
                .height(50.dp)
                .border(
                    width = if (selectedAction == "1CUP") 1.dp else (-1).dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
                ),
            shape = MaterialTheme.shapes.medium,
            colors = if (selectedAction == "1CUP") activeButtonColor else inactiveButtonColor,
            onClick = {
                selectedAction = "1CUP"
                onActionSelected("1CUP")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.local_cafe),
                contentDescription = "1 Coffee",
                tint = if (selectedAction == "1CUP")
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Button(
            modifier = Modifier
                .weight(0.5f)
                .height(50.dp)
                .border(
                    width = if (selectedAction == "2CUP") 1.dp else (-1).dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
                ),
            shape = MaterialTheme.shapes.medium,
            colors = if (selectedAction == "2CUP") activeButtonColor else inactiveButtonColor,
            onClick = {
                selectedAction = "2CUP"
                onActionSelected("2CUP")
            }
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.local_cafe),
                    contentDescription = "2 Coffees",
                    tint = if (selectedAction == "2CUP")
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
                Icon(
                    painter = painterResource(id = R.drawable.local_cafe),
                    contentDescription = "2 Coffees",
                    tint = if (selectedAction == "2CUP")
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}