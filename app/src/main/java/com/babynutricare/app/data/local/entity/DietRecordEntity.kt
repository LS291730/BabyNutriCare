package com.babynutricare.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 饮食记录实体
 */
@Entity(
    tableName = "diet_record",
    indices = [
        Index(value = ["date"]),
        Index(value = ["mealSlot"]),
        Index(value = ["date", "mealSlot"])
    ]
)
data class DietRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordId: String,               // UUID
    val date: String,                   // yyyy-MM-dd
    val mealSlot: Int,                  // MealSlot.id
    val ingredients: String,            // JSON: List<IngredientPortion>
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)