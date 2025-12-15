package com.andre.calculadoradeimc.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andre.calculadoradeimc.datasource.Calculation
import com.andre.calculadoradeimc.ui.theme.BlueLink
import com.andre.calculadoradeimc.ui.theme.GreenHealth
import com.andre.calculadoradeimc.ui.theme.White
import com.andre.calculadoradeimc.ui.theme.Red
import com.andre.calculadoradeimc.viewmodel.IMCViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    viewModel: IMCViewModel,
    onNavigateToTmb: (String, String, String, Boolean) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGraphs: () -> Unit = {}
) {

    var altura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var idade by remember { mutableStateOf("") }
    var isHomem by remember { mutableStateOf(true) }
    var resultado by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Calculadora de IMC", color = White, fontWeight = FontWeight.Bold)
                },
                actions = {
                    TextButton(onClick = onNavigateToGraphs) {
                        Text("Gráficos", color = White)
                    }
                    TextButton(onClick = onNavigateToHistory) {
                        Text("Histórico", color = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenHealth
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState())
        ) {

            // --- Dados Pessoais (Sexo) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isHomem,
                    onClick = { isHomem = true },
                    colors = RadioButtonDefaults.colors(selectedColor = GreenHealth)
                )
                Text("Homem", Modifier.padding(end = 20.dp))
                RadioButton(
                    selected = !isHomem,
                    onClick = { isHomem = false },
                    colors = RadioButtonDefaults.colors(selectedColor = GreenHealth)
                )
                Text("Mulher")
            }

            // --- Campos Idade, Altura, Peso ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Idade",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Text(
                    text = "Altura (cm)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 60.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Campo Idade
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
                    label = { Text("Ex: 25") },
                    isError = isError && idade.isEmpty(), // Marca como erro se vazio ao calcular
                    modifier = Modifier
                        .padding(start = 20.dp, top = 10.dp)
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = GreenHealth,
                        focusedBorderColor = GreenHealth,
                        focusedLabelColor = GreenHealth,
                        errorBorderColor = Red,
                        errorLabelColor = Red
                    )
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Campo Altura
                OutlinedTextField(
                    value = altura,
                    onValueChange = {
                        if (it.length <= 3) {
                            altura = it
                            isError = false
                        }
                    },
                    label = { Text("Ex: 170") },
                    isError = isError && altura.isEmpty(),
                    modifier = Modifier
                        .padding(end = 20.dp, top = 10.dp)
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = GreenHealth,
                        focusedBorderColor = GreenHealth,
                        focusedLabelColor = GreenHealth,
                        errorBorderColor = Red,
                        errorLabelColor = Red
                    )
                )
            }

            Text(
                text = "Peso (kg)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp)
            )

            OutlinedTextField(
                value = peso,
                onValueChange = {
                    if (it.length <= 7) {
                        peso = it
                        isError = false
                    }
                },
                label = { Text("Ex: 70.5") },
                isError = isError && peso.isEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = GreenHealth,
                    focusedBorderColor = GreenHealth,
                    focusedLabelColor = GreenHealth,
                    errorBorderColor = Red,
                    errorLabelColor = Red
                )
            )

            // --- Botão Calcular ---
            Button(
                onClick = {
                    if (peso.isEmpty() || altura.isEmpty() || idade.isEmpty()) {
                        isError = true
                        resultado = "Preencha todos os campos!"
                    } else {
                        Calculation.calculateIMC(peso, altura) { message, errorState ->
                            resultado = message
                            isError = errorState
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenHealth),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Calcular",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            // --- Resultado ---
            Text(
                text = resultado,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if(isError) Color.Red else GreenHealth,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // --- Botão + Detalhes ---
            if (resultado.isNotEmpty() && !isError) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = { onNavigateToTmb(peso, altura, idade, isHomem) }) {
                        Text(
                            text = "+ Detalhes",
                            color = BlueLink,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline)
                        )
                    }
                }
            }
        }
    }
}