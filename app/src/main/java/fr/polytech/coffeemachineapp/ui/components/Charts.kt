package fr.polytech.coffeemachineapp.ui.components

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.polytech.coffeemachineapp.model.Device
import fr.polytech.coffeemachineapp.model.RequestLog
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

@Composable
fun PieChartDeviceUsage(devices: List<Device>, logs: List<RequestLog>) {

    var data by remember { mutableStateOf<List<Pie>>(mutableListOf()) }
    var selectedPie by remember { mutableStateOf<Pie?>(null) }
    val pieColor = MaterialTheme.colorScheme.secondary
    val pieSelectedColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(devices, logs) {
        val lastWeekLogs = logs.filter { log ->
            val logDate = Date(log.timestamp * 1000).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toInstant(
                ZoneOffset.UTC).epochSecond

            // Get date at start of the current day
            val currentDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay().toInstant(
                ZoneOffset.UTC).epochSecond

            // Check if the log is in the last 7 days
            logDate >= currentDate - 7 * 24 * 3600 && logDate <= currentDate + 24 * 3600
        }
        // Calculate the total number of requests for each device
        val deviceRequestLogs = mutableMapOf<String, Double>()
        for (log in lastWeekLogs) {
            val device = devices.find { it.mac == log.mac }
            val deviceMac = device?.mac ?: "Other"
            if (deviceRequestLogs.containsKey(deviceMac)) {
                deviceRequestLogs.compute(deviceMac) { _, value ->
                    value?.plus(1.0)
                }
            }
            else {
                deviceRequestLogs[deviceMac] = 1.0
            }
        }
        deviceRequestLogs.toSortedMap(comparator = compareByDescending { deviceRequestLogs[it] })
        val mutableData = mutableListOf<Pie>()
        deviceRequestLogs.forEach { (key, value) ->
            mutableData.add(
                Pie(
                    label = key,
                    data = value / lastWeekLogs.size * 100,
                    color = pieColor,
                    selectedColor = pieSelectedColor
                )
            )
        }
        if(mutableData.size == 1) {
            // Update the only to be selected
            mutableData[0] = mutableData[0].copy(selected = true)
            selectedPie = mutableData[0]
        }
        data = mutableData
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PieChart(
            modifier = Modifier
                .size(250.dp)
                .padding(start = 20.dp, end = 20.dp),
            data = data,
            onPieClick = { clickedPie ->
                selectedPie = clickedPie
                println("${clickedPie.label} Clicked")
                data = data.map { pie -> pie.copy(selected = pie == clickedPie) }.toMutableList()
            },
            selectedScale = 1.1f,
            selectedPaddingDegree = 5f,
            spaceDegree = 5f,
            scaleAnimEnterSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            colorAnimEnterSpec = tween(300),
            colorAnimExitSpec = tween(300),
            scaleAnimExitSpec = tween(300),
            spaceDegreeAnimExitSpec = tween(300),
            style = Pie.Style.Stroke()
        )
        selectedPie?.let { pie ->
            val selectedDevice = devices.find { it.mac == pie.label }
            val text = (selectedDevice?.name ?: "Other") + " (" + DecimalFormat("#.##").format(pie.data) + "%)"
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * Display a bar graph with the coffee consumption for the last 7 days
 */
@Composable
fun ColumnChartCoffeeConsumption(logs: List<RequestLog>) {
    var data by remember { mutableStateOf<List<Bars>>(mutableListOf()) }
    var maxValue by remember { mutableIntStateOf(0) }

    val barColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(logs) {
        // Get the last 7 days
        val lastWeekLogs = logs.filter { log ->
            val logDate = Date(log.timestamp * 1000).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toInstant(
                ZoneOffset.UTC).epochSecond

            // Get date at start of the current day
            val currentDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay().toInstant(
                ZoneOffset.UTC).epochSecond

            // Check if the log is in the last 7 days
            Log.d("ColumnChartCoffeeConsumption", "Log date: $logDate, current date: $currentDate, less 7 days ago: ${logDate >= currentDate - 7 * 24 * 60 * 60}")
            logDate >= currentDate - 7 * 24 * 3600 && logDate <= currentDate + 24 * 3600
        }.sortedBy { it.timestamp }
        Log.d("ColumnChartCoffeeConsumption", "Logs: ${lastWeekLogs.size}")
        Log.d("ColumnChartCoffeeConsumption", "Last week logs: $lastWeekLogs")

        // Get the consumption for each day
        val consumption = mutableMapOf<LocalDate, Int>()
        lastWeekLogs.forEach { log ->
            val date = Date(log.timestamp*1000).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            consumption.compute(date) { _, count ->
                (count ?: 0) + if(log.action == "1CUP") 1 else 2
            }
        }

        // Create the data for the chart
        val mutableData = mutableListOf<Bars>()
        consumption.forEach { (key, value) ->
            mutableData.add(
                Bars(
                    label = key.toString(),
                    values = listOf(
                        Bars.Data(
                            value = value.toDouble(),
                            color = SolidColor(barColor)
                        )
                    )
                )
            )
            if(value > maxValue) {
                maxValue = value
            }
        }
        data = mutableData
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                val barNumber = data.size
                val chartWidth = if(barNumber < 8) 350.dp else (100 + barNumber * 50).dp
                Log.d("ColumnChartCoffeeConsumption", "Chart width: $chartWidth")
                ColumnChart(
                    modifier = Modifier
                        .height(300.dp)
                        .width(chartWidth)
                        .padding(start = 20.dp, end = 20.dp, bottom = 50.dp),
                    data = data,
                    labelProperties = LabelProperties(
                        enabled = true,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        ),
                        padding = 8.dp
                    ),
                    indicatorProperties = HorizontalIndicatorProperties(
                        enabled = false,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        contentBuilder = { it.toInt().toString() }
                    ),
                    labelHelperProperties = LabelHelperProperties(
                        enabled = false
                    ),
                    gridProperties = GridProperties(

                    ),
                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                        spacing = 25.dp,
                        thickness = 30.dp
                    ),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    maxValue = maxValue.toDouble(),
                )
            }
        }
    }
}