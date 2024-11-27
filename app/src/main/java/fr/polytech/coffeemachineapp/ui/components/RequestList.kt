package fr.polytech.coffeemachineapp.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.model.Request
import fr.polytech.coffeemachineapp.model.RequestLog
import fr.polytech.coffeemachineapp.utils.DateUtils
import fr.polytech.coffeemachineapp.utils.RequestStatus

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
                color = colorScheme.onBackground,
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
                    color = colorScheme.onBackground,
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
                color = colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (requestLogs.isEmpty()) {
            item {
                Text(
                    text = "No logs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onBackground,
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
                    colorScheme.primaryContainer
                else
                    colorScheme.secondaryContainer,
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
                        colorScheme.onPrimaryContainer
                    else
                        colorScheme.onSecondaryContainer
            )
            Text(
                text = DateUtils.formatDate(request.timestamp),
                style = MaterialTheme.typography.labelLarge,
                color =
                    if (isCurrent)
                        colorScheme.onPrimaryContainer
                    else
                        colorScheme.onSecondaryContainer
            )
        }
        Text(
            text = request.status.toString(),
            modifier = Modifier.padding(end = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color =
                if (isCurrent)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSecondaryContainer
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
            .background(colorScheme.secondaryContainer, MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Affichez ici les informations du log de la requête
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = requestLog.action,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSecondaryContainer
            )
            Text(
                text = DateUtils.formatDate(requestLog.timestamp),
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSecondaryContainer
            )
        }
        Text(
            text = requestLog.status.toString(),
            modifier = Modifier.padding(end = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun RequestStatusIcon(status: RequestStatus, modifier: Modifier = Modifier) {
    when (status) {
        RequestStatus.WAITING -> Icon(
            painter = painterResource(id = R.drawable.check_circle_24dp),
            contentDescription = "request status : $status",
            modifier = modifier,
            tint = colorScheme.onPrimaryContainer
        )
        RequestStatus.INITIALIZING -> CircularProgressIndicator(
            color = colorScheme.onPrimaryContainer,
            modifier = modifier.size(20.dp)
        )
        RequestStatus.WARMING -> CircularProgressIndicator(
            color = colorScheme.onPrimaryContainer,
            modifier = modifier.size(20.dp)
        )
        RequestStatus.FILLING -> CircularProgressIndicator(
            color = colorScheme.onPrimaryContainer,
            modifier = modifier.size(20.dp)
        )
        RequestStatus.COLLECTING -> Icon(
            painter = painterResource(id = R.drawable.check_circle_24dp),
            contentDescription = "request status : $status",
            modifier = modifier,
            tint = colorScheme.onPrimaryContainer
        )
        else -> {
            Log.d("DeviceStatusIcon", "Unknown status: $status")
            CircularProgressIndicator(
                color = colorScheme.onPrimaryContainer,
                modifier = modifier.size(20.dp)
            )
        }
    }
}