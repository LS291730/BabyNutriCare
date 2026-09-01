package com.babynutricare.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.babynutricare.app.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

/**
 * 食材DAO
 */
@Dao
interface IngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: IngredientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientEntity>)

    @Update
    suspend fun update(ingredient: IngredientEntity)

    @Delete
    suspend fun delete(ingredient: IngredientEntity)

    @Query("SELECT * FROM ingredient ORDER BY category, name")
    fun observeAll(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredient ORDER BY category, name")
    suspend fun getAll(): List<IngredientEntity>

    @Query("SELECT * FROM ingredient WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<IngredientEntity>

    @Query("SELECT * FROM ingredient WHERE category = :category ORDER BY name")
    fun observeByCategory(category: Int): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredient WHERE name LIKE '%' || :keyword || '%'")
    suspend fun search(keyword: String): List<IngredientEntity>

    @Query("SELECT * FROM ingredient WHERE isFavorite = 1 ORDER BY name")
    suspend fun getFavorites(): List<IngredientEntity>

    @Query("SELECT COUNT(*) FROM ingredient")
    suspend fun count(): Int
}