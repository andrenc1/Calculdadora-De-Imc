package com.example.calcimcandre.datasource

object Calculation {

    fun calculateIMC(peso: String, altura: String, response: (String, Boolean) -> Unit) {
        if (peso.isBlank() || altura.isBlank()) {
            response("Preencha todos os campos!", true)
            return
        }

        val pesoConvertido = peso.replace(",", ".").toDoubleOrNull()
        val alturaConvertida = altura.toDoubleOrNull()

        if (pesoConvertido == null || alturaConvertida == null) {
            response("Valores inválidos", true)
            return
        }
        
        if (pesoConvertido <= 0 || pesoConvertido > 300) {
            response("Peso inválido (Use 1-300 kg)", true)
            return
        }

        if (alturaConvertida <= 0 || alturaConvertida > 300) {
            response("Altura inválida (Use 1-300 cm)", true)
            return
        }

        val imc = pesoConvertido / ( (alturaConvertida / 100) * (alturaConvertida / 100) )
        val imcFormatado = String.format("%.2f", imc)

        val classificacao = when {
            imc < 18.5 -> "Abaixo do Peso"
            imc in 18.5..24.9 -> "Peso Normal"
            imc in 25.0..29.9 -> "Sobrepeso"
            imc in 30.0..34.9 -> "Obesidade Grau I"
            imc in 35.0..39.9 -> "Obesidade Severa (Grau II)"
            else -> "Obesidade Mórbida (Grau III)"
        }

        val resultadoFinal = "IMC: $imcFormatado\n$classificacao"
        response(resultadoFinal, false)
    }

    data class TMBResult(
        val imc: Double,
        val classificacao: String,
        val risco: String,
        val tmb: Double,
        val caloriasDiarias: Double,
        val pesoIdeal: Double,
        val resultadoTexto: String
    )

    fun calculateTMB(
        peso: String,
        altura: String,
        idade: String,
        isHomem: Boolean,
        activityFactor: Double = 1.2,
        response: (TMBResult?, String?) -> Unit
    ) {
        if (peso.isBlank() || altura.isBlank() || idade.isBlank()) {
            response(null, "Preencha todos os campos")
            return
        }

        val pesoKg = peso.replace(",", ".").toDoubleOrNull()
        val alturaCm = altura.toDoubleOrNull()
        val idadeAnos = idade.toIntOrNull()

        if (pesoKg == null || alturaCm == null || idadeAnos == null) {
            response(null, "Valores numéricos inválidos")
            return
        }

        if (pesoKg <= 0 || pesoKg > 300) {
            response(null, "Peso deve estar entre 1 e 300 kg")
            return
        }
        if (alturaCm <= 0 || alturaCm > 300) {
            response(null, "Altura deve estar entre 1 e 300 cm")
            return
        }
        if (idadeAnos <= 0 || idadeAnos > 100) {
            response(null, "Idade deve estar entre 1 e 100 anos")
            return
        }

        // Fórmula Mifflin-St Jeor (TMB)
        val base = (10 * pesoKg) + (6.25 * alturaCm) - (5 * idadeAnos)
        val tmb = if (isHomem) base + 5 else base - 161

        // Peso Ideal (Fórmula de Devine)
        val alturaPolegadas = alturaCm / 2.54
        val pesoIdeal = if (isHomem) {
            50.0 + 2.3 * (alturaPolegadas - 60)
        } else {
            45.5 + 2.3 * (alturaPolegadas - 60)
        }
        // Peso ideal não deve ser negativo (para alturas muito baixas)
        val pesoIdealValidado = if (pesoIdeal < 0) 0.0 else pesoIdeal

        // Necessidade Calórica Diária
        val caloriasDiarias = tmb * activityFactor
        
        // IMC Atual para referência
        val imc = pesoKg / ((alturaCm / 100) * (alturaCm / 100))
        
        val (interpretacao, zonaDeRisco) = when {
            imc < 18.5 -> "Abaixo do Peso" to "!Atenção!: Baixo peso pode indicar desnutrição."
            imc in 18.5..24.9 -> "Peso Normal" to "✅ Parabéns! Você está em uma faixa saudável."
            imc in 25.0..29.9 -> "Sobrepeso" to "!Atenção!: Excesso de peso moderado."
            imc in 30.0..34.9 -> "Obesidade Grau I" to "🚨 Cuidado: Obesidade leve, risco aumentado."
            imc in 35.0..39.9 -> "Obesidade Severa (Grau II)" to "🚨 Alerta: Obesidade severa, procure um médico."
            else -> "Obesidade Mórbida (Grau III)" to "🚑 Perigo: Obesidade mórbida, risco alto à saúde."
        }
        
        val imcFormatado = String.format("%.2f", imc)

        val resultadoFinal = """
            📊 IMC Atual: $imcFormatado ($interpretacao)
            $zonaDeRisco
            
            🔥 Taxa Metabólica Basal (TMB): 
            %.0f kcal/dia
            (Energia gasta em repouso absoluto)
            
            ⚡ Necessidade Calórica Diária:
            %.0f kcal/dia
            (Para manter o peso atual com seu nível de atividade)
            
            ⚖️ Peso Ideal Estimado: 
            %.1f kg
        """.trimIndent().format(tmb, caloriasDiarias, pesoIdealValidado)

        response(
            TMBResult(
                imc = imc,
                classificacao = interpretacao,
                risco = zonaDeRisco,
                tmb = tmb,
                caloriasDiarias = caloriasDiarias,
                pesoIdeal = pesoIdealValidado,
                resultadoTexto = resultadoFinal
            ),
            null
        )
    }
}