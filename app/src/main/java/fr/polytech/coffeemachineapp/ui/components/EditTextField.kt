package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditTextField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String, Boolean) -> Unit
) {
    var isNameEdited by remember { mutableStateOf(false) }

    if (!isNameEdited) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onPrimaryContainer
            )
            IconButton(onClick = { isNameEdited = true }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit name",
                    tint = colorScheme.onPrimaryContainer
                )
            }
        }
    }
    else {
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.onPrimaryContainer,
            unfocusedBorderColor = colorScheme.onPrimaryContainer,
            focusedLabelColor = colorScheme.onPrimaryContainer,
            unfocusedLabelColor = colorScheme.onPrimaryContainer,
            focusedTextColor = colorScheme.onPrimaryContainer,
            unfocusedTextColor = colorScheme.onPrimaryContainer,
            focusedPlaceholderColor = colorScheme.onPrimaryContainer,
            unfocusedPlaceholderColor = colorScheme.onPrimaryContainer,
            cursorColor = colorScheme.onPrimaryContainer
        )

        OutlinedTextField(
            value = value,
            onValueChange = { newValue -> onValueChange(newValue, false) },
            colors = textFieldColors,
            shape = RoundedCornerShape(15.dp),
            modifier = modifier,
            trailingIcon = {
                IconButton(
                    onClick = {
                        isNameEdited = false
                        onValueChange(value, true)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Validate new name",
                        tint = colorScheme.onPrimaryContainer
                    )
                }
            }
        )
    }
}