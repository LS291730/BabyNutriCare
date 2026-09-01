package com.babynutricare.core.domain.nutrition

import com.babynutricare.core.domain.model.Ingredient
import com.babynutricare.core.domain.model.IngredientPortion
import com.babynutricare.core.domain.model.NutritionInfo
import com.babynutricare.core.domain.model.NutritionSummary

/**
 * 营养计算器
 * 负责根据食材用量计算各类营养素摄入量
 */
class NutritionCalculator {

    /**
     * 计算一组食材的总营养摄入量
     * @param portions 食材用量列表
     * @param ingredients 食材库（用于获取营养数据）
     */
    fun calculateNutrition(
        portions: List<IngredientPortion>,
        ingredients: Map<Long, Ingredient>
    ): NutritionSummary {
        if (portions.isEmpty()) return NutritionSummary()

        var totalProtein = 0f
        var totalFat = 0f
        var totalCarbohydrate = 0f
        var totalCalcium = 0f
        var totalIron = 0f
        var totalZinc = 0f
        var totalVitaminA = 0f
        var totalVitaminC = 0f
        var totalVitaminD = 0f
        var totalVitaminE = 0f
        var totalVitaminB1 = 0f
        var totalVitaminB2 = 0f
        var totalFolicAcid = 0f
        var totalCalorie = 0f

        portions.forEach { portion ->
            val ingredient = ingredients[portion.ingredientId] ?: return@forEach
            val factor = portion.amount / 100f // 营养数据以每100g为单位

            totalProtein += ingredient.nutritionInfo.protein * factor
            totalFat += ingredient.nutritionInfo.fat * factor
            totalCarbohydrate += ingredient.nutritionInfo.carbohydrate * factor
            totalCalcium += ingredient.nutritionInfo.calcium * factor
            totalIron += ingredient.nutritionInfo.iron * factor
            totalZinc += ingredient.nutritionInfo.zinc * factor
            totalVitaminA += ingredient.nutritionInfo.vitaminA * factor
            totalVitaminC += ingredient.nutritionInfo.vitaminC * factor
            totalVitaminD += ingredient.nutritionInfo.vitaminD * factor
            totalVitaminE += ingredient.nutritionInfo.vitaminE * factor
            totalVitaminB1 += ingredient.nutritionInfo.vitaminB1 * factor
            totalVitaminB2 += ingredient.nutritionInfo.vitaminB2 * factor
            totalFolicAcid += ingredient.nutritionInfo.folicAcid * factor
            totalCalorie += ingredient.nutritionInfo.calorie * factor
        }

        return NutritionSummary(
            protein = round(totalProtein),
            fat = round(totalFat),
            carbohydrate = round(totalCarbohydrate),
            calcium = round(totalCalcium),
            iron = round(totalIron),
            zinc = round(totalZinc),
            vitaminA = round(totalVitaminA),
            vitaminC = round(totalVitaminC),
            vitaminD = round(totalVitaminD),
            vitaminE = round(totalVitaminE),
            vitaminB1 = round(totalVitaminB1),
            vitaminB2 = round(totalVitaminB2),
            folicAcid = round(totalFolicAcid),
            calorie = round(totalCalorie)
        )
    }

    /**
     * 合并多个营养摘要
     */
    fun mergeSummaries(summaries: List<NutritionSummary>): NutritionSummary {
        return NutritionSummary(
            protein = round(summaries.sumOf { it.protein.toDouble() }.toFloat()),
            fat = round(summaries.sumOf { it.fat.toDouble() }.toFloat()),
            carbohydrate = round(summaries.sumOf { it.carbohydrate.toDouble() }.toFloat()),
            calcium = round(summaries.sumOf { it.calcium.toDouble() }.toFloat()),
            iron = round(summaries.sumOf { it.iron.toDouble() }.toFloat()),
            zinc = round(summaries.sumOf { it.zinc.toDouble() }.toFloat()),
            vitaminA = round(summaries.sumOf { it.vitaminA.toDouble() }.toFloat()),
            vitaminC = round(summaries.sumOf { it.vitaminC.toDouble() }.toFloat()),
            vitaminD = round(summaries.sumOf { it.vitaminD.toDouble() }.toFloat()),
            vitaminE = round(summaries.sumOf { it.vitaminE.toDouble() }.toFloat()),
            vitaminB1 = round(summaries.sumOf { it.vitaminB1.toDouble() }.toFloat()),
            vitaminB2 = round(summaries.sumOf { it.vitaminB2.toDouble() }.toFloat()),
            folicAcid = round(summaries.sumOf { it.folicAcid.toDouble() }.toFloat()),
            calorie = round(summaries.sumOf { it.calorie.toDouble() }.toFloat())
        )
    }

    private fun round(value: Float): Float {
        return Math.round(value * 100f) / 100f
    }
}