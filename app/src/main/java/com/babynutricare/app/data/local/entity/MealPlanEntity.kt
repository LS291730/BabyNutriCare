package com.babynutricare.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 配餐方案实体
 */
@Entity(
    tableName = "meal_plan",
    indices = [Index(value = ["startDate"]), Index(value = ["planType"])]
)
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planId: String,                 // UUID
    val planName: String,
    val planType: Int,                  // MealPlanType.id
    val startDate: String,              // yyyy-MM-dd
    val endDate: String,                // yyyy-MM-dd
    val status: Int = 0,                // MealPlanStatus.id
    val mealsJson: String = "[]",       // JSON: List<PlannedMeal>
    val nutritionSummaryJson: String = "{}", // JSON: NutritionSummary
    val warnings: String = "[]",        // JSON数组
    val achievementRate: String = "{}", // JSON: Map<String, Float>
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)