package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.polytech.coffeemachineapp.model.Request
import fr.polytech.coffeemachineapp.model.RequestLog
import fr.polytech.coffeemachineapp.utils.DateUtils

@Composable
fun RequestList(
    currentRequest: Request?,
    nextRequest: Request?,
    requests: List<Request>,
    requestLogs: List<RequestLog>
) {
    LazyColumn {
        item {
            // Affichez ici l'en-tête de la liste des requêtes
            Text(
                text = "On-going requests",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (currentRequest != null) {
            item {
                RequestItem(currentRequest, true)
            }
        }
        if (nextRequest != null) {
            item {
                RequestItem(nextRequest)
            }
        }
        if (requests.isEmpty()) {
            item {
                Text(
                    text = "No requests",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        else {
            items(requests) { request ->
                RequestItem(request)
            }
        }
        item {
            // Affichez ici l'en-tête de la liste des logs de requêtes
            Text(
                text = "Finished requests",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (requestLogs.isEmpty()) {
            item {
                Text(
                    text = "No logs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        else {
            items(requestLogs) { requestLog ->
                RequestLogItem(requestLog)
            }
        }
    }
}

@Composable
fun RequestItem(request: Request, isCurrent: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .background(
                if (isCurrent)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.shapes.medium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Affichez ici les informations de la requête
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = request.action,
                style = MaterialTheme.typography.titleLarge,
                color =
                    if (isCurrent)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = DateUtils.formatDate(request.timestamp),
                style = MaterialTheme.typography.labelLarge,
                color =
                    if (isCurrent)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Text(
            text = request.status,
            modifier = Modifier.padding(end = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color =
                if (isCurrent)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun RequestLogItem(requestLog: RequestLog) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Affichez ici les informations du log de la requête
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = requestLog.action,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = DateUtils.formatDate(requestLog.timestamp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Text(
            text = requestLog.status,
            modifier = Modifier.padding(end = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}