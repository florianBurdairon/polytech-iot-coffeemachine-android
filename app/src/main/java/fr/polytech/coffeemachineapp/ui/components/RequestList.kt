package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fr.polytech.coffeemachineapp.model.Request
import fr.polytech.coffeemachineapp.utils.DateUtils

@Composable
fun RequestList(requests: List<Request>) {
    LazyColumn {
        items(requests) { request ->
            RequestItem(request)
        }
    }
}

@Composable
fun RequestItem(request: Request) {
    // Affichez ici les informations de la requête
    Text(text = "Action: ${request.action}, Date: ${DateUtils.formatDate(request.timestamp)}")
}