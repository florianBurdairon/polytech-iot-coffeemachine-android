package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GuestList(guests: List<String>, header: (@Composable () -> Unit), onRemoveGuest: (String) -> Unit) {
    LazyColumn {
        item {
            header()
        }
        items(guests) { guest ->
            GuestListItem(guest = guest, onRemoveGuest = onRemoveGuest)
        }
    }
}

@Composable
fun GuestListItem(guest: String, onRemoveGuest: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Display the guest name
        Text(
            text = guest,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        )
        // Display the remove button
        Icon(
            imageVector = Icons.Rounded.Delete,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = "Remove guest",
            modifier = Modifier
                .padding(16.dp)
                .clickable { onRemoveGuest(guest) }
        )
    }
}
