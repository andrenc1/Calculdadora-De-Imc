package com.andre.calculadoradeimc.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imc_records")
data class IMCRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val peso: Double,
    val altura: Double,
    val idade: Int,
    val isHomem: Boolean,
    val imc: Double,
    val classificacaoIMC: String,
    val tmb: Double,
    val pesoIdeal: Double,
    val necessidadeCalorica: Double,
    val atividadeFisica: String
)