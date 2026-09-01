package com.babynutricare.core.domain.nutrition

import com.babynutricare.core.data.model.NutritionStandard
import com.babynutricare.core.data.model.NutritionStandardRepository
import com.babynutricare.core.domain.model.NutritionGap
import com.babynutricare.core.domain.model.NutritionSummary

/**
 * 营养缺口分析器
 * 对比已摄入营养与标准营养，计算营养缺口
 */
class NutritionGapAnalyzer {

    /**
     * 计算营养缺口
     * @param actual 已摄入营养
     * @param standard 月龄营养标准
     * @return 营养缺口（正值表示缺失，负值表示过量）
     */
    fun analyzeGap(
        actual: NutritionSummary,
        standard: NutritionStandard
    ): NutritionGap {
        return NutritionGap(
            protein = calculateGap(actual.protein, standard.protein),
            fat = calculateGap(actual.fat, standard.fat),
            carbohydrate = calculateGap(actual.carbohydrate, standard.carbohydrate),
            calcium = calculateGap(actual.calcium, standard.calcium),
            iron = calculateGap(actual.iron, standard.iron),
            zinc = calculateGap(actual.zinc, standard.zinc),
            vitaminA = calculateGap(actual.vitaminA, standard.vitaminA),
            vitaminC = calculateGap(actual.vitaminC, standard.vitaminC),
            vitaminD = calculateGap(actual.vitaminD, standard.vitaminD),
            vitaminE = calculateGap(actual.vitaminE, standard.vitaminE),
            vitaminB1 = calculateGap(actual.vitaminB1, standard.vitaminB1),
            vitaminB2 = calculateGap(actual.vitaminB2, standard.vitaminB2),
            folicAcid = calculateGap(actual.folicAcid, standard.folicAcid),
            calorie = calculateGap(actual.calorie, standard.calorie)
        )
    }

    /**
     * 计算按剩余天数比例折算后的营养缺口
     * 用于周维度配餐（如本周剩余3天，则缺口按3/7折算）
     *
     * @param actual 本周已摄入营养
     * @param standard 每日标准营养
     * @param totalDays 周期总天数
     * @param remainingDays 剩余天数
     */
    fun analyzeWeeklyGap(
        actual: NutritionSummary,
        standard: NutritionStandard,
        totalDays: Int = 7,
        remainingDays: Int
    ): NutritionGap {
        require(totalDays > 0 && remainingDays in 0..totalDays) {
            "非法天数参数: totalDays=$totalDays, remainingDays=$remainingDays"
        }
        val expectedTotal = standardToSummary(standard)
        val weekTarget = scaleSummary(expectedTotal, totalDays.toFloat())
        val remainingTarget = scaleSummary(weekTarget, remainingDays.toFloat() / totalDays)
        // 已摄入的占剩余目标的比例，用于折算缺口
        val actualForRemaining = scaleSummary(actual, remainingDays.toFloat() / totalDays)

        return analyzeGap(actualForRemaining, remainingTarget)
    }

    /**
     * 计算营养达标率（百分比）
     * @return 每个营养素达标率，取值 0-100
     */
    fun calculateAchievementRate(
        actual: NutritionSummary,
        standard: NutritionStandard
    ): Map<String, Float> {
        val standardSummary = standardToSummary(standard)
        return mapOf(
            "protein" to rate(actual.protein, standardSummary.protein),
            "fat" to rate(actual.fat, standardSummary.fat),
            "carbohydrate" to rate(actual.carbohydrate, standardSummary.carbohydrate),
            "calcium" to rate(actual.calcium, standardSummary.calcium),
            "iron" to rate(actual.iron, standardSummary.iron),
            "zinc" to rate(actual.zinc, standardSummary.zinc),
            "vitaminA" to rate(actual.vitaminA, standardSummary.vitaminA),
            "vitaminC" to rate(actual.vitaminC, standardSummary.vitaminC),
            "vitaminD" to rate(actual.vitaminD, standardSummary.vitaminD),
            "vitaminE" to rate(actual.vitaminE, standardSummary.vitaminE),
            "vitaminB1" to rate(actual.vitaminB1, standardSummary.vitaminB1),
            "vitaminB2" to rate(actual.vitaminB2, standardSummary.vitaminB2),
            "folicAcid" to rate(actual.folicAcid, standardSummary.folicAcid),
            "calorie" to rate(actual.calorie, standardSummary.calorie)
        )
    }

    /**
     * 获取当前月龄的每日营养标准
     */
    fun getStandardByMonth(month: Int): NutritionStandard {
        return NutritionStandardRepository.getStandardByMonth(month)
    }

    private fun standardToSummary(standard: NutritionStandard): NutritionSummary {
        return NutritionSummary(
            protein = standard.protein,
            fat = standard.fat,
            carbohydrate = standard.carbohydrate,
            calcium = standard.calcium,
            iron = standard.iron,
            zinc = standard.zinc,
            vitaminA = standard.vitaminA,
            vitaminC = standard.vitaminC,
            vitaminD = standard.vitaminD,
            vitaminE = standard.vitaminE,
            vitaminB1 = standard.vitaminB1,
            vitaminB2 = standard.vitaminB2,
            folicAcid = standard.folicAcid,
            calorie = standard.calorie
        )
    }

    private fun scaleSummary(summary: NutritionSummary, factor: Float): NutritionSummary {
        return NutritionSummary(
            protein = summary.protein * factor,
            fat = summary.fat * factor,
            carbohydrate = summary.carbohydrate * factor,
            calcium = summary.calcium * factor,
            iron = summary.iron * factor,
            zinc = summary.zinc * factor,
            vitaminA = summary.vitaminA * factor,
            vitaminC = summary.vitaminC * factor,
            vitaminD = summary.vitaminD * factor,
            vitaminE = summary.vitaminE * factor,
            vitaminB1 = summary.vitaminB1 * factor,
            vitaminB2 = summary.vitaminB2 * factor,
            folicAcid = summary.folicAcid * factor,
            calorie = summary.calorie * factor
        )
    }

    private fun calculateGap(actual: Float, standard: Float): Float {
        val gap = standard - actual
        return Math.round(gap * 100f) / 100f
    }

    private fun rate(actual: Float, standard: Float): Float {
        if (standard <= 0f) return 0f
        val rate = (actual / standard) * 100f
        return rate.coerceIn(0f, 100f)
    }
}