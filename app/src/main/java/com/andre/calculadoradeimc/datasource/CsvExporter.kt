package com.andre.calculadoradeimc.datasource

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.andre.calculadoradeimc.model.IMCRecord
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun exportToCSV(context: Context, records: List<IMCRecord>) {
        // 1. Definir nome do arquivo e diretório
        val fileName = "historico_imc.csv"
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        val file = File(exportDir, fileName)

        // 2. Escrever os dados
        try {
            val writer = FileWriter(file)

            // Cabeçalho do CSV
            writer.append("Data,Hora,Peso(kg),Altura(cm),IMC,Classificação,TMB(kcal),Atividade\n")

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            // Linhas de dados
            for (record in records) {
                val date = Date(record.timestamp)
                writer.append(dateFormat.format(date)).append(",")
                writer.append(timeFormat.format(date)).append(",")
                writer.append("${record.peso},")
                writer.append("${record.altura},")
                writer.append(String.format("%.2f", record.imc).replace(",", ".")).append(",") // Garante ponto para Excel universal ou mantenha virgula se for BR
                writer.append("\"${record.classificacaoIMC}\",") // Aspas para evitar quebra com espaços
                writer.append(String.format("%.0f", record.tmb)).append(",")
                writer.append("\"${record.atividadeFisica}\"\n")
            }

            writer.flush()
            writer.close()

            // 3. Compartilhar o arquivo gerado via Intent
            shareFile(context, file)

        } catch (e: Exception) {
            e.printStackTrace() // Em app real, retornaria um erro para a UI
        }
    }

    private fun shareFile(context: Context, file: File) {
        // Gera a URI segura via FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Histórico de IMC")
            // Permissão temporária para o app que receber (Gmail, Drive) ler o arquivo
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Exportar Histórico CSV")
        // Como estamos chamando de um Composable/Activity, precisamos da flag se for context de Application (mas aqui usaremos context de Activity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}