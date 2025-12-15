package com.example.calcimcandre.model

import kotlinx.coroutines.flow.Flow

class IMCRepository(private val dao: IMCDao) {
    val allRecords: Flow<List<IMCRecord>> = dao.getAllRecords()

    suspend fun insert(record: IMCRecord) {
        dao.insert(record)
    }

    suspend fun getRecordById(id: Int): IMCRecord? {
        return dao.getRecordById(id)
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }
}