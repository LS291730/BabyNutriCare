package com.babynutricare.core.domain.nutrition

import com.babynutricare.core.data.model.AgeGroup
import com.babynutricare.core.data.model.NutritionStandard
import com.babynutricare.core.data.model.NutritionStandardRepository
import com.babynutricare.core.domain.model.NutritionSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 营养缺口分析器测试
 */
class NutritionGapAnalyzerTest {

    private val analyzer = NutritionGapAnalyzer()

    @Test
    fun `分析营养缺口 - 摄入不足时返回正缺口`() {
        val standard = NutritionStandardRepository.getStandard(AgeGroup.SIX_TO_NINE)
        val actual = NutritionSummary(
            protein = 10f,
            calcium = 200f,
            iron = 2f
        )

        val gap = analyzer.analyzeGap(actual, standard)

        // 蛋白质缺口 = 18 - 10 = 8
        assertEquals(8f, gap.protein, 0.01f)
        // 钙缺口 = 350 - 200 = 150
        assertEquals(150f, gap.calcium, 0.01f)
        // 铁缺口 = 5 - 2 = 3
        assertEquals(3f, gap.iron, 0.01f)
        // 未摄入的脂肪为完整标准值
        assertEquals(standard.fat, gap.fat, 0.01f)
    }

    @Test
    fun `分析营养缺口 - 摄入过量时缺口为0`() {
        val standard = NutritionStandardRepository.getStandard(AgeGroup.SIX_TO_NINE)
        val actual = NutritionSummary(
            protein = 30f,  // 超过标准18g
            calorie = 900f  // 超过标准700kcal
        )

        val gap = analyzer.analyzeGap(actual, standard)

        assertEquals(0f, gap.protein, 0.01f)
        assertEquals(0f, gap.calorie, 0.01f)
    }

    @Test
    fun `周维度缺口分析 - 剩余天数比例正确折算`() {
        val standard = NutritionStandardRepository.getStandard(AgeGroup.SIX_TO_NINE)
        // 周一、周二已记录，剩余5天
        val weekConsumed = NutritionSummary(
            protein = 36f,  // 2天已摄入
            calcium = 700f  // 2天已摄入
        )

        val gap = analyzer.analyzeWeeklyGap(
            actual = weekConsumed,
            standard = standard,
            totalDays = 7,
            remainingDays = 5
        )

        // 周目标蛋白质 = 18*7 = 126
        // 剩余5天目标 = 126 * 5/7 = 90
        // 已摄入折算到剩余 = 36 * 5/7 ≈ 25.71
        // 缺口 ≈ 90 - 25.71 = 64.29
        assertTrue("蛋白质缺口应为正", gap.protein > 0f)
        assertEquals(64.28f, gap.protein, 0.5f)
    }

    @Test
    fun `达标率计算 - 摄入一半为50个百分点`() {
        val standard = NutritionStandardRepository.getStandard(AgeGroup.SIX_TO_NINE)
        val actual = NutritionSummary(
            protein = 9f  // 标准18g的一半
        )

        val rates = analyzer.calculateAchievementRate(actual, standard)

        assertEquals(50f, rates["protein"] ?: 0f, 0.1f)
    }

    @Test
    fun `按月龄获取营养标准 - 各月龄组有对应标准`() {
        assertEquals(AgeGroup.SIX_TO_NINE, NutritionStandardRepository.getStandardByMonth(7).ageGroup)
        assertEquals(AgeGroup.NINE_TO_TWELVE, NutritionStandardRepository.getStandardByMonth(10).ageGroup)
        assertEquals(AgeGroup.TWELVE_TO_EIGHTEEN, NutritionStandardRepository.getStandardByMonth(15).ageGroup)
        assertEquals(AgeGroup.TWENTY_FOUR_TO_THIRTY_SIX, NutritionStandardRepository.getStandardByMonth(30).ageGroup)
        // 超范围默认取最大月龄组
        assertEquals(AgeGroup.TWENTY_FOUR_TO_THIRTY_SIX, NutritionStandardRepository.getStandardByMonth(48).ageGroup)
    }
}