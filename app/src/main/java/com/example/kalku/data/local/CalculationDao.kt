package com.example.kalku.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CalculationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculation: CalculationEntity): Long

    @Query("SELECT * FROM calculations WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getCalculationsByUser(userId: Int): List<CalculationEntity>

    @Query("SELECT * FROM calculations WHERE id = :id LIMIT 1")
    suspend fun getCalculationById(id: Int): CalculationEntity?

    @Delete
    suspend fun delete(calculation: CalculationEntity)
}
