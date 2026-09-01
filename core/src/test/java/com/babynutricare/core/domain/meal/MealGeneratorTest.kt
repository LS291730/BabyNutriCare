package com.babynutricare.core.domain.meal

import com.babynutricare.core.data.model.AgeGroup
import com.babynutricare.core.data.model.NutritionStandardRepository
import com.babynutricare.core.domain.model.BabyInfo
import com.babynutricare.core.domain.model.Ingredient
import com.babynutricare.core.domain.model.NutritionInfo
import com.babynutricare.core.data.model.IngredientCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * 配餐生成器测试
 */
class MealGeneratorTest {

    private val generator = MealGenerator()

    // 动态计算出生日期，确保月龄稳定在约10个月（适配6-9月龄食材）
    private val testBaby = BabyInfo(
        name = "测试宝宝",
        birthDate = LocalDate.now().minusMonths(10),
        allergies = emptyList(),
        dietaryRestrictions = emptyList()
    )

    private val testIngredients = listOf(
        Ingredient(
            id = 1,
            name = "大米",
            category = IngredientCategory.GRAIN,
            nutritionInfo = NutritionInfo(
                protein = 7.4f, carbohydrate = 77.9f, fat = 0.8f,
                iron = 2.3f, zinc = 1.7f, calcium = 13f, calorie = 346f
            ),
            digestibility = 10,
            minAgeMonth = 7,
            maxAgeMonth = 36
        ),
        Ingredient(
            id = 2,
            name = "胡萝卜",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 1f, carbohydrate = 8.8f, fat = 0.2f,
                vitaminA = 835f, vitaminC = 13f, calcium = 32f, calorie = 39f
            ),
            digestibility = 9,
            minAgeMonth = 6,
            maxAgeMonth = 36
        ),
        Ingredient(
            id = 3,
            name = "鸡胸肉",
            category = IngredientCategory.MEAT,
            nutritionInfo = NutritionInfo(
                protein = 24.6f, carbohydrate = 0.6f, fat = 1.9f,
                iron = 0.8f, zinc = 0.9f, calcium = 6f, calorie = 118f
            ),
            digestibility = 8,
            minAgeMonth = 8,
            maxAgeMonth = 36
        ),
        Ingredient(
            id = 4,
            name = "苹果",
            category = IngredientCategory.FRUIT,
            nutritionInfo = NutritionInfo(
                protein = 0.2f, carbohydrate = 13.5f, fat = 0.2f,
                vitaminC = 4f, calcium = 4f, iron = 0.6f, calorie = 54f
            ),
            digestibility = 9,
            minAgeMonth = 6,
            maxAgeMonth = 36
        )
    )

    @Test
    fun `基于现有食材配餐 - 生成三餐方案`() {
        val baby = testBaby
        val standard = NutritionStandardRepository.getStandardByMonth(baby.getMonthAge())

        val result = generator.generateIngredientBasedPlan(
            baby = baby,
            availableIngredients = testIngredients,
            standard = standard
        )

        assertEquals(3, result.meals.size)
        assertTrue("应有早餐", result.meals.any { it.mealSlot.id == 0 })
        assertTrue("应有午餐", result.meals.any { it.mealSlot.id == 1 })
        assertTrue("应有晚餐", result.meals.any { it.mealSlot.id == 2 })
    }

    @Test
    fun `过滤过敏食材 - 过敏源不进入配餐`() {
        val baby = testBaby.copy(
            allergies = listOf("鸡蛋")
        )
        val standard = NutritionStandardRepository.getStandardByMonth(baby.getMonthAge())

        // 添加一个鸡蛋食材
        val ingredients = testIngredients + Ingredient(
            id = 5,
            name = "鸡蛋",
            category = IngredientCategory.EGG,
            nutritionInfo = NutritionInfo(
                protein = 13.3f, carbohydrate = 2.8f, fat = 8.8f,
                iron = 2f, zinc = 1.1f, calcium = 56f, calorie = 144f
            ),
            isAllergen = true,
            allergenTypes = listOf("鸡蛋"),
            digestibility = 8,
            minAgeMonth = 9,
            maxAgeMonth = 36
        )

        val result = generator.generateIngredientBasedPlan(
            baby = baby,
            availableIngredients = ingredients,
            standard = standard
        )

        val allPortionNames = result.meals.flatMap { it.ingredients }.map { it.ingredientName }
        assertFalse("过敏食材不应出现在配餐中", allPortionNames.contains("鸡蛋"))
    }

    @Test
    fun `过滤月龄不适宜食材 - 3个月宝宝无可用食材`() {
        val baby = testBaby.copy(
            birthDate = LocalDate.now().minusMonths(3)  // 动态计算：3个月大
        )
        val standard = NutritionStandardRepository.getStandardByMonth(baby.getMonthAge())

        val result = generator.generateIngredientBasedPlan(
            baby = baby,
            availableIngredients = testIngredients,
            standard = standard
        )

        // 3个月宝宝应无可用辅食食材
        assertTrue("3个月宝宝不应有配餐方案", result.meals.isEmpty())
        assertTrue("应返回警告信息", result.warnings.isNotEmpty())
    }

    @Test
    fun `周维度配餐 - 剩余天数生成对应餐次`() {
        val baby = testBaby
        val standard = NutritionStandardRepository.getStandardByMonth(baby.getMonthAge())
        val weekStart = LocalDate.of(2026, 8, 31) // 周一

        val result = generator.generateWeeklyPlan(
            baby = baby,
            recordsStartDate = weekStart,
            weekConsumed = com.babynutricare.core.domain.model.NutritionSummary(),
            consumedDays = 2,  // 已记录周一、周二
            availableIngredients = testIngredients,
            standard = standard
        )

        // 剩余5天 * 每天3餐 = 15餐
        assertEquals(15, result.meals.size)
        assertTrue("所有餐次日期应在剩余天数内", result.meals.all { it.date >= weekStart.plusDays(2) })
    }

    @Test
    fun `周维度配餐 - 记录完整时提示无需续配`() {
        val baby = testBaby
        val standard = NutritionStandardRepository.getStandardByMonth(baby.getMonthAge())
        val weekStart = LocalDate.of(2026, 8, 31)

        val result = generator.generateWeeklyPlan(
            baby = baby,
            recordsStartDate = weekStart,
            weekConsumed = com.babynutricare.core.domain.model.NutritionSummary(),
            consumedDays = 7,
            availableIngredients = testIngredients,
            standard = standard
        )

        assertTrue("本周记录完整，无需配餐", result.meals.isEmpty())
        assertTrue(result.warnings.isNotEmpty())
    }

    @Test
    fun `营养达标率 - 生成的方案包含关键营养指标`() {
        val baby = testBaby
        val standard = NutritionStandardRepository.getStandardByMonth(baby.getMonthAge())

        val result = generator.generateIngredientBasedPlan(
            baby = baby,
            availableIngredients = testIngredients,
            standard = standard
        )

        assertTrue(result.achievementRate.containsKey("protein"))
        assertTrue(result.achievementRate.containsKey("calcium"))
        assertTrue(result.achievementRate.containsKey("iron"))
        assertTrue(result.achievementRate.containsKey("zinc"))
        assertTrue(result.achievementRate.containsKey("vitaminA"))
        assertTrue(result.achievementRate.containsKey("vitaminC"))
        assertTrue(result.achievementRate.containsKey("calorie"))
    }

    @Test
    fun `每个餐次都包含食材用量`() {
        val baby = testBaby
        val standard = NutritionStandardRepository.getStandardByMonth(baby.getMonthAge())

        val result = generator.generateIngredientBasedPlan(
            baby = baby,
            availableIngredients = testIngredients,
            standard = standard
        )

        result.meals.forEach { meal ->
            assertTrue("餐次${meal.mealSlot.displayName}应有食材", meal.ingredients.isNotEmpty())
            meal.ingredients.forEach { portion ->
                assertTrue("食材用量应为正数", portion.amount > 0f)
                assertTrue("食材应有名称", portion.ingredientName.isNotBlank())
            }
        }
    }
}