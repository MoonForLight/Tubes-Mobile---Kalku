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

    @Query("""
        SELECT * FROM calculations
        WHERE userId = :userId
        AND productName LIKE '%' || :keyword || '%'
        ORDER BY createdAt DESC
    """)
    suspend fun searchCalculationsByUser(userId: Int, keyword: String): List<CalculationEntity>

    @Query("""
        SELECT * FROM calculations
        WHERE userId = :userId
        AND createdAt >= :startDate
        ORDER BY createdAt DESC
    """)
    suspend fun getCalculationsByDate(userId: Int, startDate: Long): List<CalculationEntity>

    @Query("""
        SELECT * FROM calculations
        WHERE userId = :userId
        AND productName LIKE '%' || :keyword || '%'
        AND createdAt >= :startDate
        ORDER BY createdAt DESC
    """)
    suspend fun searchCalculationsByDate(
        userId: Int,
        keyword: String,
        startDate: Long
    ): List<CalculationEntity>

    @Query("SELECT * FROM calculations WHERE id = :id LIMIT 1")
    suspend fun getCalculationById(id: Int): CalculationEntity?

    @Delete
    suspend fun delete(calculation: CalculationEntity)

    @Query("DELETE FROM calculations WHERE id = :id")
    suspend fun deleteById(id: Int)
}
