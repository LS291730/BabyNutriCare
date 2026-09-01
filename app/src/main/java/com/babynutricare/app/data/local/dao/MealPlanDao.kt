package com.babynutricare.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.babynutricare.app.data.local.entity.MealPlanEntity
import kotlinx.coroutines.flow.Flow

/**
 * 配餐方案DAO
 */
@Dao
interface MealPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: MealPlanEntity): Long

    @Update
    suspend fun update(plan: MealPlanEntity)

    @Delete
    suspend fun delete(plan: MealPlanEntity)

    @Query("SELECT * FROM meal_plan ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MealPlanEntity>>

    @Query("SELECT * FROM meal_plan WHERE planType = :planType ORDER BY createdAt DESC")
    fun observeByType(planType: Int): Flow<List<MealPlanEntity>>

    @Query("SELECT * FROM meal_plan WHERE planId = :planId LIMIT 1")
    suspend fun getByPlanId(planId: String): MealPlanEntity?

    @Query("SELECT * FROM meal_plan WHERE planType = :planType ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestByType(planType: Int): MealPlanEntity?

    @Query("SELECT COUNT(*) FROM meal_plan")
    suspend fun count(): Int
}