package fr.polytech.coffeemachineapp.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.model.Request
import fr.polytech.coffeemachineapp.model.RequestLog
import fr.polytech.coffeemachineapp.utils.DateUtils
import fr.polytech.coffeemachineapp.utils.LogStatus
import fr.polytech.coffeemachineapp.utils.RequestStatus

@Composable
fun RequestList(
    currentRequest: Request?,
    nextRequest: Request?,
    requests: List<Request>,
    requestLogs: List<RequestLog>,
    onCancel: (Request) -> Unit = {}
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
                RequestItem(nextRequest) {
                    onCancel(nextRequest)
                }
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
                RequestItem(request) {
                    onCancel(request)
                }
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
fun RequestItem(request: Request, isCurrent: Boolean = false, onCancel: () -> Unit = {}) {
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
        Row(
            modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RequestStatusIcon(
                request.status,
                modifier = Modifier.size(30.dp),
                color =
                if (isCurrent)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSecondaryContainer
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                ActionDisplay(
                    request.action,
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
        }
        if (!isCurrent) {
            Row(
                modifier = Modifier.padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel_24dp),
                    contentDescription = "cancel request",
                    modifier = Modifier.size(30.dp).clickable { onCancel() },
                    tint = colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun RequestLogItem(requestLog: RequestLog) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .background(
                color = if (LogStatus.isError(requestLog.status))
                    colorScheme.tertiaryContainer
                else
                    colorScheme.secondaryContainer,
                MaterialTheme.shapes.medium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Affichez ici les informations du log de la requête
        Column(modifier = Modifier.padding(start = 16.dp)) {
            ActionDisplay(
                requestLog.action,
                color = if (LogStatus.isError(requestLog.status))
                    colorScheme.onTertiaryContainer
                else
                    colorScheme.onSecondaryContainer
            )
            Text(
                text = DateUtils.formatDate(requestLog.timestamp),
                style = MaterialTheme.typography.labelLarge,
                color = if (LogStatus.isError(requestLog.status))
                    colorScheme.onTertiaryContainer
                else
                    colorScheme.onSecondaryContainer
            )
        }
        Row(
            modifier = Modifier.padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogStatusIcon(
                requestLog.status,
                modifier = Modifier.size(30.dp),
                color = if (LogStatus.isError(requestLog.status))
                    colorScheme.onTertiaryContainer
                else
                    colorScheme.onSecondaryContainer
            )
            if (LogStatus.isError(requestLog.status)) {
                Text(
                    text = requestLog.status.toString(),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (LogStatus.isError(requestLog.status))
                        colorScheme.onTertiaryContainer
                    else
                        colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun RequestStatusIcon(status: RequestStatus, modifier: Modifier = Modifier, color: Color) {
    when (status) {
        RequestStatus.WAITING -> Icon(
            painter = painterResource(id = R.drawable.hourglass_top_24dp),
            contentDescription = "request status : $status",
            modifier = modifier,
            tint = color
        )
        RequestStatus.INITIALIZING -> CircularProgressIndicator(
            color = color,
            modifier = modifier.size(20.dp)
        )
        RequestStatus.WARMING -> Icon(
            painter = painterResource(id = R.drawable.heat_24dp),
            contentDescription = "request status : $status",
            modifier = modifier,
            tint = color
        )
        RequestStatus.FILLING -> Icon(
            painter = painterResource(id = R.drawable.water_drop_24dp),
            contentDescription = "request status : $status",
            modifier = modifier,
            tint = color
        )
        RequestStatus.COLLECTING -> Icon(
            painter = painterResource(id = R.drawable.local_cafe),
            contentDescription = "request status : $status",
            modifier = modifier,
            tint = color
        )
        else -> {
            Log.d("DeviceStatusIcon", "Unknown status: $status")
            CircularProgressIndicator(
                color = color,
                modifier = modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LogStatusIcon(status: LogStatus, modifier: Modifier = Modifier, color: Color) {
    when {
        status == LogStatus.SUCCESS -> Icon(
            painter = painterResource(id = R.drawable.check_circle_24dp),
            contentDescription = "log status : $status",
            modifier = modifier,
            tint = color
        )
        LogStatus.isError(status) -> Icon(
            painter = painterResource(id = R.drawable.error_24dp),
            contentDescription = "log status : $status",
            modifier = modifier,
            tint = color
        )
        else -> {
            Log.d("LogStatusIcon", "Unknown status: $status")
            CircularProgressIndicator(
                color = color,
                modifier = modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ActionDisplay(action: String, color: Color) {
    val text = when(action) {
        "1CUP" -> "1 Coffee"
        "2CUP" -> "2 Coffees"
        else -> "Unknown action"
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            modifier = Modifier.padding(end = 8.dp)
        )
        val modifier = Modifier.size(16.dp)
        when (action) {
            "1CUP" -> Icon(
                painter = painterResource(id = R.drawable.local_cafe),
                contentDescription = "1 Coffee",
                tint = color,
                modifier = modifier
            )
            "2CUP" -> Row {
                Icon(
                    painter = painterResource(id = R.drawable.local_cafe),
                    contentDescription = "2 Coffees",
                    tint = color,
                    modifier = modifier
                )
                Icon(
                    painter = painterResource(id = R.drawable.local_cafe),
                    contentDescription = "2 Coffees",
                    tint = color,
                    modifier = modifier
                )
            }
        }
    }
}