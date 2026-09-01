package com.babynutricare.core.domain.model

import com.babynutricare.core.data.model.IngredientCategory
import kotlinx.serialization.Serializable

/**
 * 食材领域模型
 */
@Serializable
data class Ingredient(
    val id: Long = 0,
    val name: String,
    val category: IngredientCategory = IngredientCategory.OTHER,
    val nutritionInfo: NutritionInfo,
    val isAllergen: Boolean = false,
    val allergenTypes: List<String> = emptyList(),
    val defaultUnit: String = "g",
    val isFavorite: Boolean = false,
    val digestibility: Int = 5,        // 易消化性评分 1-10
    val minAgeMonth: Int = 0,          // 最早可食用月龄
    val maxAgeMonth: Int = 36,         // 最晚可食用月龄
    val notes: String = ""
)

/**
 * 食材营养信息
 */
@Serializable
data class NutritionInfo(
    val protein: Float = 0f,           // 每100g蛋白质含量
    val fat: Float = 0f,               // 每100g脂肪含量
    val carbohydrate: Float = 0f,      // 每100g碳水化合物含量
    val calcium: Float = 0f,           // 每100g钙含量 (mg)
    val iron: Float = 0f,              // 每100g铁含量 (mg)
    val zinc: Float = 0f,              // 每100g锌含量 (mg)
    val vitaminA: Float = 0f,          // 每100g维生素A含量 (μg)
    val vitaminC: Float = 0f,          // 每100g维生素C含量 (mg)
    val vitaminD: Float = 0f,          // 每100g维生素D含量 (IU)
    val vitaminE: Float = 0f,          // 每100g维生素E含量 (mg)
    val vitaminB1: Float = 0f,         // 每100g维生素B1含量 (mg)
    val vitaminB2: Float = 0f,         // 每100g维生素B2含量 (mg)
    val folicAcid: Float = 0f,         // 每100g叶酸含量 (μg)
    val calorie: Float = 0f            // 每100g热量 (kcal)
)

/**
 * 食材用量记录（用于饮食记录和配餐方案）
 */
@Serializable
data class IngredientPortion(
    val ingredientId: Long,
    val ingredientName: String,
    val amount: Float,
    val unit: String = "g"
)