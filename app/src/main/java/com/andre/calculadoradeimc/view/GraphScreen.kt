package com.andre.calculadoradeimc.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import com.andre.calculadoradeimc.model.IMCRecord
import com.andre.calculadoradeimc.ui.theme.*
import com.andre.calculadoradeimc.viewmodel.IMCViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(
    viewModel: IMCViewModel,
    onBack: () -> Unit
) {
    val historyList by viewModel.allRecords.collectAsState()

    // Inverter para ordem cronológica (mais antigo primeiro)
    val sortedList = historyList.reversed()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gráficos de Evolução", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenHealth)
            )
        }
    ) { padding ->
        if (sortedList.size < 2) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Registre pelo menos 2 medições para ver os gráficos!",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(White)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Evolução das Medições",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenHealth,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Gráfico de IMC
                GraphCard(
                    title = "📊 Evolução do IMC",
                    data = sortedList,
                    getValue = { it.imc },
                    color = GreenHealth,
                    yAxisLabel = "IMC"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Gráfico de Peso
                GraphCard(
                    title = "⚖️ Evolução do Peso",
                    data = sortedList,
                    getValue = { it.peso },
                    color = BlueInfo,
                    yAxisLabel = "Peso (kg)"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Gráfico de TMB
                GraphCard(
                    title = "🔥 Evolução da TMB",
                    data = sortedList,
                    getValue = { it.tmb },
                    color = OrangeWarning,
                    yAxisLabel = "TMB (kcal)"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Estatísticas resumidas
                StatisticsCard(sortedList)
            }
        }
    }
}

@Composable
fun GraphCard(
    title: String,
    data: List<IMCRecord>,
    getValue: (IMCRecord) -> Double,
    color: Color,
    yAxisLabel: String
) {
    val points = data.mapIndexed { index, record ->
        Point(index.toFloat(), getValue(record).toFloat())
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .backgroundColor(Color.Transparent)
        .steps(points.size - 1)
        .labelData { i ->
            if (i < data.size) {
                val date = Date(data[i].timestamp)
                SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
            } else ""
        }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Black)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val minValue = points.minOf { it.y }
            val maxValue = points.maxOf { it.y }
            val step = (maxValue - minValue) / 5
            String.format("%.1f", minValue + (step * i))
        }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Black)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = points,
                    lineStyle = LineStyle(
                        color = color,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    intersectionPoint = IntersectionPoint(
                        color = color
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = color
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.3f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = Color.LightGray.copy(alpha = 0.3f)),
        backgroundColor = Color.White
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(10.dp))
            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                lineChartData = lineChartData
            )
        }
    }
}

@Composable
fun StatisticsCard(data: List<IMCRecord>) {
    val firstRecord = data.first()
    val lastRecord = data.last()

    val imcChange = lastRecord.imc - firstRecord.imc
    val pesoChange = lastRecord.peso - firstRecord.peso
    val tmbChange = lastRecord.tmb - firstRecord.tmb

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = GreenHealth.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📈 Resumo da Evolução",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GreenHealth
            )
            Spacer(modifier = Modifier.height(10.dp))

            StatRow("Total de medições:", "${data.size}")
            StatRow(
                "Variação de IMC:",
                String.format("%.2f", imcChange),
                if (imcChange < 0) GreenHealth else if (imcChange > 0) Red else Color.Gray
            )
            StatRow(
                "Variação de Peso:",
                String.format("%.2f kg", pesoChange),
                if (pesoChange < 0) GreenHealth else if (pesoChange > 0) Red else Color.Gray
            )
            StatRow(
                "Variação de TMB:",
                String.format("%.0f kcal", tmbChange),
                Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Período: ${formatDate(firstRecord.timestamp)} até ${formatDate(lastRecord.timestamp)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun StatRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
}