package com.example.calcimcandre.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IMCDao {
    @Insert
    suspend fun insert(record: IMCRecord)

    @Query("SELECT * FROM imc_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<IMCRecord>>
    
    @Query("SELECT * FROM imc_records WHERE id = :id")
    suspend fun getRecordById(id: Int): IMCRecord?

    @Query("DELETE FROM imc_records")
    suspend fun clearHistory()
}