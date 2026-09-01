package com.babynutricare.core.domain.model

import com.babynutricare.core.data.model.MealSlot
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * 饮食记录领域模型
 */
@Serializable
data class DietRecord(
    val id: Long = 0,
    val recordId: String,
    @Contextual val date: LocalDate,
    val mealSlot: MealSlot,
    val ingredients: List<IngredientPortion>,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalAmount: Float
        get() = ingredients.sumOf { it.amount.toDouble() }.toFloat()
}

/**
 * 配餐方案类型
 */
@Serializable
enum class MealPlanType(val id: Int, val label: String) {
    WEEKLY(0, "周维度配餐"),
    DAILY(1, "日维度配餐"),
    INGREDIENT_BASED(2, "现有食材配餐")
}

/**
 * 配餐方案状态
 */
@Serializable
enum class MealPlanStatus(val id: Int, val label: String) {
    DRAFT(0, "草稿"),
    USED(1, "已使用"),
    EXPIRED(2, "已过期")
}

/**
 * 配餐方案领域模型
 */
@Serializable
data class MealPlan(
    val id: Long = 0,
    val planId: String,
    val planName: String,
    val planType: MealPlanType,
    @Contextual val startDate: LocalDate,
    @Contextual val endDate: LocalDate,
    val status: MealPlanStatus = MealPlanStatus.DRAFT,
    val meals: List<PlannedMeal> = emptyList(),
    val nutritionAnalysis: NutritionSummary = NutritionSummary(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 配餐方案中的一餐
 */
@Serializable
data class PlannedMeal(
    val id: Long = 0,
    val mealPlanId: Long = 0,
    val mealSlot: MealSlot,
    @Contextual val date: LocalDate,
    val ingredients: List<IngredientPortion>,
    val nutritionSummary: NutritionSummary = NutritionSummary(),
    val cookingSteps: List<String> = emptyList(),
    val notes: String = ""
)

/**
 * 营养摘要
 */
@Serializable
data class NutritionSummary(
    val protein: Float = 0f,
    val fat: Float = 0f,
    val carbohydrate: Float = 0f,
    val calcium: Float = 0f,
    val iron: Float = 0f,
    val zinc: Float = 0f,
    val vitaminA: Float = 0f,
    val vitaminC: Float = 0f,
    val vitaminD: Float = 0f,
    val vitaminE: Float = 0f,
    val vitaminB1: Float = 0f,
    val vitaminB2: Float = 0f,
    val folicAcid: Float = 0f,
    val calorie: Float = 0f
)

/**
 * 营养缺口分析结果
 */
data class NutritionGap(
    val protein: Float = 0f,
    val fat: Float = 0f,
    val carbohydrate: Float = 0f,
    val calcium: Float = 0f,
    val iron: Float = 0f,
    val zinc: Float = 0f,
    val vitaminA: Float = 0f,
    val vitaminC: Float = 0f,
    val vitaminD: Float = 0f,
    val vitaminE: Float = 0f,
    val vitaminB1: Float = 0f,
    val vitaminB2: Float = 0f,
    val folicAcid: Float = 0f,
    val calorie: Float = 0f
) {
    /**
     * 获取主要缺失营养素列表（按缺口大小排序）
     */
    fun getPriorityGaps(limit: Int = 5): List<NutritionGapItem> {
        val gaps = listOf(
            NutritionGapItem("蛋白质", protein, NutritionType.PROTEIN),
            NutritionGapItem("钙", calcium, NutritionType.CALCIUM),
            NutritionGapItem("铁", iron, NutritionType.IRON),
            NutritionGapItem("锌", zinc, NutritionType.ZINC),
            NutritionGapItem("维生素A", vitaminA, NutritionType.VITAMIN_A),
            NutritionGapItem("维生素C", vitaminC, NutritionType.VITAMIN_C)
        ).filter { it.gapAmount > 0 }
        return gaps.sortedByDescending { it.gapAmount }.take(limit)
    }
}

/**
 * 单个营养素缺口
 */
data class NutritionGapItem(
    val name: String,
    val gapAmount: Float,
    val nutritionType: NutritionType
)

/**
 * 营养素类型枚举
 */
enum class NutritionType {
    PROTEIN, FAT, CARBOHYDRATE, CALCIUM, IRON, ZINC,
    VITAMIN_A, VITAMIN_C, VITAMIN_D, VITAMIN_E, VITAMIN_B1, VITAMIN_B2, FOLIC_ACID, CALORIE
}

/**
 * 宝宝信息领域模型
 */
data class BabyInfo(
    val id: Long = 0,
    val name: String,
    val birthDate: LocalDate,
    val gender: Int = 1,           // 0-女, 1-男
    val weight: Float = 0f,        // kg
    val height: Float = 0f,        // cm
    val allergies: List<String> = emptyList(),
    val dietaryRestrictions: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * 计算宝宝当前月龄
     */
    fun getMonthAge(referenceDate: LocalDate = LocalDate.now()): Int {
        val years = referenceDate.year - birthDate.year
        val months = referenceDate.monthValue - birthDate.monthValue
        var total = years * 12 + months
        if (referenceDate.dayOfMonth < birthDate.dayOfMonth) {
            total -= 1
        }
        return maxOf(0, total)
    }
}