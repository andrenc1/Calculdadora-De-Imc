package com.andre.calculadoradeimc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andre.calculadoradeimc.model.IMCRecord
import com.andre.calculadoradeimc.model.IMCRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IMCViewModel(private val repository: IMCRepository) : ViewModel() {

    val allRecords: StateFlow<List<IMCRecord>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveIMC(
        peso: String,
        altura: String,
        idade: String,
        isHomem: Boolean,
        activityFactor: Double
    ) {
        val pesoVal = peso.replace(",", ".").toDoubleOrNull()
        val alturaVal = altura.toDoubleOrNull()
        val idadeVal = idade.toIntOrNull()

        if (pesoVal != null && alturaVal != null && idadeVal != null) {
            val imc = pesoVal / ((alturaVal / 100) * (alturaVal / 100))
            
            val classificacao = when {
                imc < 18.5 -> "Abaixo do Peso"
                imc in 18.5..24.9 -> "Peso Normal"
                imc in 25.0..29.9 -> "Sobrepeso"
                imc in 30.0..34.9 -> "Obesidade Grau I"
                imc in 35.0..39.9 -> "Obesidade Severa (Grau II)"
                else -> "Obesidade Mórbida (Grau III)"
            }

            // TMB
            val base = (10 * pesoVal) + (6.25 * alturaVal) - (5 * idadeVal)
            val tmb = if (isHomem) base + 5 else base - 161

            // Peso Ideal (Devine)
            val alturaPolegadas = alturaVal / 2.54
            val pesoIdeal = if (isHomem) {
                50.0 + 2.3 * (alturaPolegadas - 60)
            } else {
                45.5 + 2.3 * (alturaPolegadas - 60)
            }

            val necessidadeCalorica = tmb * activityFactor
            
            val atividadeStr = when(activityFactor) {
                1.2 -> "Sedentário"
                1.375 -> "Levemente Ativo"
                1.55 -> "Moderadamente Ativo"
                1.725 -> "Muito Ativo"
                1.9 -> "Extremamente Ativo"
                else -> "Desconhecido"
            }

            val record = IMCRecord(
                timestamp = System.currentTimeMillis(),
                peso = pesoVal,
                altura = alturaVal,
                idade = idadeVal,
                isHomem = isHomem,
                imc = imc,
                classificacaoIMC = classificacao,
                tmb = tmb,
                pesoIdeal = pesoIdeal,
                necessidadeCalorica = necessidadeCalorica,
                atividadeFisica = atividadeStr
            )

            viewModelScope.launch {
                repository.insert(record)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}

class IMCViewModelFactory(private val repository: IMCRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IMCViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IMCViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}