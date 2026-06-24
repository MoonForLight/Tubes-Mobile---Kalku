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

    @Query("SELECT * FROM calculations WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentCalculations(userId: Int, limit: Int): List<CalculationEntity>

    @Query(
        """
        SELECT * FROM calculations
        WHERE userId = :userId AND productName LIKE '%' || :keyword || '%'
        ORDER BY createdAt DESC
        """
    )
    suspend fun searchCalculations(userId: Int, keyword: String): List<CalculationEntity>

    @Query(
        """
        SELECT * FROM calculations
        WHERE userId = :userId
          AND createdAt BETWEEN :startTime AND :endTime
        ORDER BY createdAt DESC
        """
    )
    suspend fun getCalculationsByDateRange(
        userId: Int,
        startTime: Long,
        endTime: Long
    ): List<CalculationEntity>

    @Query("SELECT * FROM calculations WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getCalculationById(id: Int, userId: Int): CalculationEntity?

    @Query("SELECT COALESCE(SUM(totalProfit), 0) FROM calculations WHERE userId = :userId")
    suspend fun getTotalProfitByUser(userId: Int): Long

    @Query("DELETE FROM calculations WHERE userId = :userId")
    suspend fun deleteByUser(userId: Int)

    @Delete
    suspend fun delete(calculation: CalculationEntity)
}
