package com.babynutricare.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 食材实体
 */
@Entity(
    tableName = "ingredient",
    indices = [Index(value = ["name"], unique = true), Index(value = ["category"])]
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: Int,              // IngredientCategory.id
    val nutritionInfo: String,      // JSON: NutritionInfo
    val isAllergen: Boolean = false,
    val allergenTypes: String = "[]", // JSON数组
    val defaultUnit: String = "g",
    val isFavorite: Boolean = false,
    val digestibility: Int = 5,
    val minAgeMonth: Int = 0,
    val maxAgeMonth: Int = 36,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)