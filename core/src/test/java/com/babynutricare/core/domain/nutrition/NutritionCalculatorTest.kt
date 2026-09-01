package com.babynutricare.core.domain.nutrition

import com.babynutricare.core.domain.model.Ingredient
import com.babynutricare.core.domain.model.IngredientPortion
import com.babynutricare.core.domain.model.NutritionInfo
import com.babynutricare.core.data.model.IngredientCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 营养计算器测试
 */
class NutritionCalculatorTest {

    private val calculator = NutritionCalculator()

    // 大米：每100g含蛋白质7.4g
    private val rice = Ingredient(
        id = 1,
        name = "大米",
        category = IngredientCategory.GRAIN,
        nutritionInfo = NutritionInfo(
            protein = 7.4f, carbohydrate = 77.9f, fat = 0.8f,
            iron = 2.3f, zinc = 1.7f, calcium = 13f, calorie = 346f
        )
    )

    @Test
    fun `计算单食材营养 - 100g大米`() {
        val result = calculator.calculateNutrition(
            portions = listOf(
                IngredientPortion(ingredientId = 1, ingredientName = "大米", amount = 100f)
            ),
            ingredients = mapOf(1L to rice)
        )

        assertEquals(7.4f, result.protein, 0.01f)
        assertEquals(77.9f, result.carbohydrate, 0.01f)
        assertEquals(0.8f, result.fat, 0.01f)
        assertEquals(346f, result.calorie, 0.01f)
    }

    @Test
    fun `计算多食材营养 - 50g大米加50g其他食材`() {
        val chicken = Ingredient(
            id = 2,
            name = "鸡胸肉",
            category = IngredientCategory.MEAT,
            nutritionInfo = NutritionInfo(
                protein = 24.6f, carbohydrate = 0.6f, fat = 1.9f,
                iron = 0.8f, zinc = 0.9f, calcium = 6f, calorie = 118f
            )
        )

        val result = calculator.calculateNutrition(
            portions = listOf(
                IngredientPortion(ingredientId = 1, ingredientName = "大米", amount = 50f),
                IngredientPortion(ingredientId = 2, ingredientName = "鸡胸肉", amount = 50f)
            ),
            ingredients = mapOf(1L to rice, 2L to chicken)
        )

        // 大米蛋白质 7.4 * 0.5 = 3.7；鸡肉蛋白质 24.6 * 0.5 = 12.3；合计16.0
        assertEquals(16.0f, result.protein, 0.1f)
        assertTrue(result.calorie > 200f)
    }

    @Test
    fun `空食材列表返回零营养`() {
        val result = calculator.calculateNutrition(
            portions = emptyList(),
            ingredients = emptyMap()
        )

        assertEquals(0f, result.protein, 0.01f)
        assertEquals(0f, result.calorie, 0.01f)
    }

    @Test
    fun `合并多个营养摘要`() {
        val summary1 = calculator.calculateNutrition(
            portions = listOf(IngredientPortion(1, "大米", 50f)),
            ingredients = mapOf(1L to rice)
        )
        val summary2 = calculator.calculateNutrition(
            portions = listOf(IngredientPortion(1, "大米", 50f)),
            ingredients = mapOf(1L to rice)
        )

        val merged = calculator.mergeSummaries(listOf(summary1, summary2))

        // 两次50g大米 = 100g = 蛋白质7.4g
        assertEquals(7.4f, merged.protein, 0.01f)
        assertEquals(346f, merged.calorie, 0.01f)
    }
}