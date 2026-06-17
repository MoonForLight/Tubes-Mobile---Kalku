package com.example.kalku.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("SELECT * FROM products WHERE userId = :userId ORDER BY updatedAt DESC")
    suspend fun getProductsByUser(userId: Int): List<ProductEntity>

    @Query(
        """
        SELECT * FROM products
        WHERE userId = :userId
        AND (productName LIKE '%' || :keyword || '%' OR category LIKE '%' || :keyword || '%')
        ORDER BY updatedAt DESC
        """
    )
    suspend fun searchProducts(userId: Int, keyword: String): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?

    @Query("SELECT COUNT(*) FROM products WHERE userId = :userId")
    suspend fun countProducts(userId: Int): Int

    @Query("SELECT COALESCE(SUM(sellingPrice * quantity), 0) FROM products WHERE userId = :userId")
    suspend fun getInventoryValue(userId: Int): Long

    @Query("SELECT COALESCE(SUM(totalProfit), 0) FROM products WHERE userId = :userId")
    suspend fun getEstimatedProfit(userId: Int): Long
}
