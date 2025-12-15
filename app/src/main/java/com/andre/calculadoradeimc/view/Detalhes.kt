package com.andre.calculadoradeimc.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andre.calculadoradeimc.datasource.Calculation
import com.andre.calculadoradeimc.ui.theme.*
import com.andre.calculadoradeimc.viewmodel.IMCViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TMBScreen(
    viewModel: IMCViewModel,
    initialPeso: String = "",
    initialAltura: String = "",
    initialIdade: String = "",
    initialIsHomem: Boolean = true,
    onBack: () -> Unit = {}
) {
    var altura by remember { mutableStateOf(initialAltura) }
    var peso by remember { mutableStateOf(initialPeso) }
    var idade by remember { mutableStateOf(initialIdade) }
    var isHomem by remember { mutableStateOf(initialIsHomem) }
    var tmbResult by remember { mutableStateOf<Calculation.TMBResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var selectedActivityLevel by remember { mutableStateOf("Sedentário") }
    var activityFactor by remember { mutableDoubleStateOf(1.2) }

    val activityOptions = listOf(
        "Sedentário" to 1.2,
        "Levemente Ativo" to 1.375,
        "Moderadamente Ativo" to 1.55,
        "Muito Ativo" to 1.725,
        "Extremamente Ativo" to 1.9
    )

    LaunchedEffect(peso, altura, idade, isHomem, activityFactor) {
        if (peso.isNotEmpty() && altura.isNotEmpty() && idade.isNotEmpty()) {
            Calculation.calculateTMB(peso, altura, idade, isHomem, activityFactor) { res, error ->
                tmbResult = res
                errorMessage = error
            }
        } else {
            tmbResult = null
            errorMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalhes", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenHealth)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Selecione o Nível de Atividade:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp).fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedActivityLevel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenHealth,
                            focusedLabelColor = GreenHealth,
                            cursorColor = GreenHealth
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        activityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.first) },
                                onClick = {
                                    selectedActivityLevel = option.first
                                    activityFactor = option.second
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (tmbResult != null) {
                val result = tmbResult!!
                val cardColor = when {
                    result.imc < 18.5 -> BlueInfo.copy(alpha = 0.2f)
                    result.imc in 18.5..24.9 -> GreenHealth.copy(alpha = 0.2f)
                    result.imc in 25.0..29.9 -> YellowWarning.copy(alpha = 0.2f)
                    result.imc in 30.0..34.9 -> OrangeWarning.copy(alpha = 0.2f)
                    else -> Red.copy(alpha = 0.2f)
                }

                val titleColor = when {
                    result.imc < 18.5 -> BlueInfo
                    result.imc in 18.5..24.9 -> GreenHealth
                    result.imc in 25.0..29.9 -> OrangeWarning
                    result.imc in 30.0..34.9 -> OrangeWarning
                    else -> Red
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resultados Detalhados",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = result.resultadoTexto,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Button(
                    onClick = {
                        if (peso.isNotEmpty() && altura.isNotEmpty() && idade.isNotEmpty()) {
                            viewModel.saveIMC(peso, altura, idade, isHomem, activityFactor)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenHealth),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Salvar Medição no Histórico",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Preencha os dados na tela anterior ou abaixo para ver os resultados.",
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            Text("Editar Dados (Opcional)", fontWeight = FontWeight.Light, fontSize = 14.sp)

            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = isHomem, onClick = { isHomem = true }, colors = RadioButtonDefaults.colors(selectedColor = GreenHealth))
                Text("Homem", Modifier.padding(end = 20.dp))
                RadioButton(selected = !isHomem, onClick = { isHomem = false }, colors = RadioButtonDefaults.colors(selectedColor = GreenHealth))
                Text("Mulher")
            }

            OutlinedTextField(
                value = altura, onValueChange = { if(it.length<=3) altura=it },
                label = { Text("Altura (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = OutlinedTextFieldDefaults.colors(cursorColor = GreenHealth, focusedBorderColor = GreenHealth, focusedLabelColor = GreenHealth)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = peso, onValueChange = { if(it.length<=6) peso=it },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = OutlinedTextFieldDefaults.colors(cursorColor = GreenHealth, focusedBorderColor = GreenHealth, focusedLabelColor = GreenHealth)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = idade,
                onValueChange = {
                    if (it.length <= 3) {
                        val age = it.toIntOrNull()
                        if (it.isEmpty() || (age != null && age <= 100)) {
                            idade = it
                        }
                    }
                },
                label = { Text("Idade (anos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = OutlinedTextFieldDefaults.colors(cursorColor = GreenHealth, focusedBorderColor = GreenHealth, focusedLabelColor = GreenHealth)
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}