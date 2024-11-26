package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.polytech.coffeemachineapp.model.User

@Composable
fun GuestList(guests: List<User>, header: (@Composable () -> Unit), onRemoveGuest: (String) -> Unit) {
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
fun GuestListItem(guest: User, onRemoveGuest: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the guest name
        Text(
            text = guest.name,
            style = MaterialTheme.typography.titleLarge,
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
                .clickable { onRemoveGuest(guest.uid) }
        )
    }
}
