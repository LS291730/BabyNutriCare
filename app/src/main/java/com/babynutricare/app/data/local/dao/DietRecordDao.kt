package com.babynutricare.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.babynutricare.app.data.local.entity.DietRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * 饮食记录DAO
 */
@Dao
interface DietRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DietRecordEntity): Long

    @Update
    suspend fun update(record: DietRecordEntity)

    @Delete
    suspend fun delete(record: DietRecordEntity)

    @Query("SELECT * FROM diet_record ORDER BY date DESC, mealSlot ASC")
    fun observeAll(): Flow<List<DietRecordEntity>>

    @Query("SELECT * FROM diet_record WHERE date = :date ORDER BY mealSlot ASC")
    suspend fun getByDate(date: String): List<DietRecordEntity>

    @Query("SELECT * FROM diet_record WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, mealSlot ASC")
    suspend fun getByDateRange(startDate: String, endDate: String): List<DietRecordEntity>

    @Query("SELECT * FROM diet_record WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, mealSlot ASC")
    fun observeByDateRange(startDate: String, endDate: String): Flow<List<DietRecordEntity>>

    @Query("SELECT * FROM diet_record WHERE recordId = :recordId LIMIT 1")
    suspend fun getByRecordId(recordId: String): DietRecordEntity?

    @Query("SELECT DISTINCT date FROM diet_record ORDER BY date DESC")
    suspend fun getDistinctDates(): List<String>
}