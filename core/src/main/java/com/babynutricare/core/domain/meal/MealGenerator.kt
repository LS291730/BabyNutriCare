package com.babynutricare.core.domain.meal

import com.babynutricare.core.data.model.MealSlot
import com.babynutricare.core.data.model.NutritionStandard
import com.babynutricare.core.domain.model.BabyInfo
import com.babynutricare.core.domain.model.Ingredient
import com.babynutricare.core.domain.model.IngredientPortion
import com.babynutricare.core.domain.model.NutritionGap
import com.babynutricare.core.domain.model.NutritionSummary
import com.babynutricare.core.domain.model.PlannedMeal
import com.babynutricare.core.domain.nutrition.NutritionCalculator
import com.babynutricare.core.domain.nutrition.NutritionGapAnalyzer
import com.babynutricare.core.domain.weaning.WeaningRules
import java.time.LocalDate
import kotlin.random.Random

/**
 * 配餐方案结果
 */
data class MealPlanResult(
    val meals: List<PlannedMeal>,
    val nutritionSummary: NutritionSummary,
    val warnings: List<String> = emptyList(),
    val achievementRate: Map<String, Float> = emptyMap()
)

/**
 * 配餐生成器 - 核心配餐算法
 *
 * 配餐策略：
 * 1. 根据宝宝月龄确定营养标准和辅食阶段
 * 2. 分析已摄入营养，计算营养缺口
 * 3. 按营养缺口优先级筛选食材（蛋白质 > 钙 > 铁 > 锌 > 维生素A > 维生素C）
 * 4. 过滤过敏源、饮食禁忌、月龄不适宜食材
 * 5. 组合食材生成配餐方案，确保每餐营养均衡且食物多样化
 */
class MealGenerator(
    private val nutritionCalculator: NutritionCalculator = NutritionCalculator()
) {

    /**
     * 基于现有食材生成配餐方案
     */
    fun generateIngredientBasedPlan(
        baby: BabyInfo,
        availableIngredients: List<Ingredient>,
        standard: NutritionStandard,
        alreadyConsumed: NutritionSummary = NutritionSummary()
    ): MealPlanResult {
        val warnings = mutableListOf<String>()

        // 1. 计算营养缺口
        val gap = calculateGap(alreadyConsumed, standard)

        // 2. 过滤可用食材
        val validIngredients = filterIngredients(availableIngredients, baby)

        if (validIngredients.isEmpty()) {
            return MealPlanResult(
                meals = emptyList(),
                nutritionSummary = NutritionSummary(),
                warnings = listOf("没有可用于配餐的食材，请先添加符合宝宝月龄的食材")
            )
        }

        // 3. 按营养缺口选择食材组合
        val mealSlots = listOf(MealSlot.BREAKFAST, MealSlot.LUNCH, MealSlot.DINNER)
        val meals = mutableListOf<PlannedMeal>()
        var remainingGap = gap
        val usedIngredientIds = mutableSetOf<Long>()

        mealSlots.forEach { slot ->
            val plan = generateSingleMeal(
                baby = baby,
                slot = slot,
                date = LocalDate.now(),
                gap = remainingGap,
                availableIngredients = validIngredients,
                usedIngredientIds = usedIngredientIds,
                warnings = warnings
            )
            meals.add(plan)
            // 更新剩余缺口
            remainingGap = calculateGap(
                mergeConsumed(alreadyConsumed, plan.nutritionSummary),
                standard
            )
            usedIngredientIds.addAll(plan.ingredients.map { it.ingredientId })
        }

        val totalSummary = nutritionCalculator.mergeSummaries(meals.map { it.nutritionSummary })
        val achievement = calculateAchievement(totalSummary, standard)

        return MealPlanResult(
            meals = meals,
            nutritionSummary = totalSummary,
            warnings = warnings,
            achievementRate = achievement
        )
    }

    /**
     * 日维度配餐 - 记录当日早餐后推荐午餐和晚餐
     */
    fun generateDailyPlan(
        baby: BabyInfo,
        breakfastConsumed: NutritionSummary,
        availableIngredients: List<Ingredient>,
        standard: NutritionStandard
    ): MealPlanResult {
        val warnings = mutableListOf<String>()
        val validIngredients = filterIngredients(availableIngredients, baby)

        // 全天营养缺口 = 全天标准 - 早餐已摄入
        val remainingGap = calculateGap(breakfastConsumed, standard)
        val priorityGaps = remainingGap.getPriorityGaps()

        if (priorityGaps.isNotEmpty()) {
            warnings.add("主要营养缺口：${priorityGaps.joinToString("、") { it.name }}")
        }

        if (validIngredients.isEmpty()) {
            return MealPlanResult(
                meals = emptyList(),
                nutritionSummary = NutritionSummary(),
                warnings = listOf("没有可用于配餐的食材，请先添加符合宝宝月龄的食材")
            )
        }

        val meals = mutableListOf<PlannedMeal>()
        val usedIngredientIds = mutableSetOf<Long>()
        var currentGap = remainingGap

        // 午餐：补充约50%的剩余缺口
        val lunch = generateSingleMeal(
            baby = baby,
            slot = MealSlot.LUNCH,
            date = LocalDate.now(),
            gap = currentGap,
            availableIngredients = validIngredients,
            usedIngredientIds = usedIngredientIds,
            targetRatio = 0.5f,
            warnings = warnings
        )
        meals.add(lunch)
        usedIngredientIds.addAll(lunch.ingredients.map { it.ingredientId })

        // 晚餐：补充剩余缺口
        currentGap = calculateGap(
            mergeConsumed(breakfastConsumed, lunch.nutritionSummary),
            standard
        )
        val dinner = generateSingleMeal(
            baby = baby,
            slot = MealSlot.DINNER,
            date = LocalDate.now(),
            gap = currentGap,
            availableIngredients = validIngredients,
            usedIngredientIds = usedIngredientIds,
            targetRatio = 0.5f,
            warnings = warnings
        )
        meals.add(dinner)

        val totalSummary = nutritionCalculator.mergeSummaries(meals.map { it.nutritionSummary })
        val achievement = calculateAchievement(totalSummary, standard)

        return MealPlanResult(
            meals = meals,
            nutritionSummary = totalSummary,
            warnings = warnings,
            achievementRate = achievement
        )
    }


    /**
     * 周维度配餐 - 记录周一至当前日期饮食后推荐剩余天数配餐
     *
     * @param recordsStartDate 周起始日期（周一）
     * @param weekConsumed 本周已摄入营养总量
     * @param consumedDays 已记录的天数（如周一、周二 = 2）
     */
    fun generateWeeklyPlan(
        baby: BabyInfo,
        recordsStartDate: LocalDate,
        weekConsumed: NutritionSummary,
        consumedDays: Int,
        availableIngredients: List<Ingredient>,
        standard: NutritionStandard
    ): MealPlanResult {
        val totalDays = 7
        val remainingDays = (totalDays - consumedDays).coerceAtLeast(0)
        val warnings = mutableListOf<String>()

        if (remainingDays <= 0) {
            return MealPlanResult(
                meals = emptyList(),
                nutritionSummary = NutritionSummary(),
                warnings = listOf("本周饮食记录已完整，无需续配餐")
            )
        }

        val validIngredients = filterIngredients(availableIngredients, baby)
        if (validIngredients.isEmpty()) {
            return MealPlanResult(
                meals = emptyList(),
                nutritionSummary = NutritionSummary(),
                warnings = listOf("没有可用于配餐的食材")
            )
        }

        // 剩余天数的营养缺口 = (每日标准 × 剩余天数) - (本周已摄入 × 剩余天数占比)
        val gapAnalyzer = NutritionGapAnalyzer()
        val remainingGap = gapAnalyzer.analyzeWeeklyGap(
            actual = weekConsumed,
            standard = standard,
            totalDays = totalDays,
            remainingDays = remainingDays
        )

        val priorityGaps = remainingGap.getPriorityGaps()
        if (priorityGaps.isNotEmpty()) {
            warnings.add("剩余${remainingDays}天主要营养缺口：${priorityGaps.joinToString("、") { it.name }}")
        }

        // 为剩余每一天生成三顿饭
        val meals = mutableListOf<PlannedMeal>()
        val usedIngredientIds = mutableSetOf<Long>()

        // 将剩余缺口分配到每一天（日均缺口）
        val dailyGap = scaleGap(remainingGap, 1f / remainingDays)
        val mealSlots = listOf(MealSlot.BREAKFAST, MealSlot.LUNCH, MealSlot.DINNER)

        for (dayOffset in consumedDays until totalDays) {
            val currentDate = recordsStartDate.plusDays(dayOffset.toLong())
            mealSlots.forEachIndexed { index, slot ->
                // 每餐按餐次权重分配营养目标：早30%、午40%、晚30%
                val slotRatio = when (index) {
                    0 -> 0.3f
                    1 -> 0.4f
                    else -> 0.3f
                }
                val slotGap = scaleGap(dailyGap, slotRatio)
                val meal = generateSingleMeal(
                    baby = baby,
                    slot = slot,
                    date = currentDate,
                    gap = slotGap,
                    availableIngredients = validIngredients,
                    usedIngredientIds = usedIngredientIds,
                    targetRatio = 1f,
                    warnings = warnings
                )
                meals.add(meal)
                usedIngredientIds.addAll(meal.ingredients.map { it.ingredientId })
            }
            // 每天结束后重置已用食材，允许第二天复用（但同一餐不重复）
            usedIngredientIds.clear()
        }

        val totalSummary = nutritionCalculator.mergeSummaries(meals.map { it.nutritionSummary })
        val achievement = calculateAchievement(totalSummary, scaleStandard(standard, remainingDays.toFloat()))

        return MealPlanResult(
            meals = meals,
            nutritionSummary = totalSummary,
            warnings = warnings,
            achievementRate = achievement
        )
    }


    /**
     * 生成单餐配餐方案
     *
     * @param targetRatio 目标营养占比（0-1），用于日维度配餐时控制每餐摄入量
     */
    private fun generateSingleMeal(
        baby: BabyInfo,
        slot: MealSlot,
        date: LocalDate,
        gap: NutritionGap,
        availableIngredients: List<Ingredient>,
        usedIngredientIds: Set<Long>,
        targetRatio: Float = 1f,
        warnings: MutableList<String>
    ): PlannedMeal {
        val monthAge = baby.getMonthAge(date)
        val stage = WeaningRules.getStage(monthAge)

        // 1. 根据营养缺口优先级选择食材
        val selected = selectIngredients(
            gap = gap,
            ingredients = availableIngredients,
            usedIngredientIds = usedIngredientIds,
            baby = baby,
            monthAge = monthAge,
            targetRatio = targetRatio
        )

        if (selected.isEmpty()) {
            warnings.add("${slot.displayName}：没有合适的食材")
        }

        // 2. 计算用量（根据缺口和食材营养含量）
        val portions = selected.map { ingredient ->
            val amount = calculateAmount(ingredient, gap, targetRatio, monthAge)
            IngredientPortion(
                ingredientId = ingredient.id,
                ingredientName = ingredient.name,
                amount = amount,
                unit = ingredient.defaultUnit
            )
        }

        // 3. 计算本餐营养
        val mealNutrition = nutritionCalculator.calculateNutrition(
            portions,
            availableIngredients.associateBy { it.id }
        )

        // 4. 生成制作步骤
        val cookingSteps = generateCookingSteps(selected.map { it.name }, stage.texture)

        return PlannedMeal(
            mealSlot = slot,
            date = date,
            ingredients = portions,
            nutritionSummary = mealNutrition,
            cookingSteps = cookingSteps,
            notes = "适合${monthAge}月龄宝宝，质地：${stage.texture}"
        )
    }


    /**
     * 按营养缺口优先级选择食材
     */
    private fun selectIngredients(
        gap: NutritionGap,
        ingredients: List<Ingredient>,
        usedIngredientIds: Set<Long>,
        baby: BabyInfo,
        monthAge: Int,
        targetRatio: Float
    ): List<Ingredient> {
        // 过滤后候选食材
        val candidates = ingredients.filter { it.id !in usedIngredientIds }

        // 按缺口优先级打分
        val scored = candidates.map { ingredient ->
            val score = scoreIngredient(ingredient, gap, monthAge, baby)
            ingredient to score
        }.filter { it.second > 0 }
        .sortedByDescending { it.second }
        .map { it.first }

        // 选择2-3种食材（主食 + 蛋白质 + 蔬菜/水果）
        val result = mutableListOf<Ingredient>()

        // 选1个主食类
        val staple = candidates.firstOrNull {
            it.category.id == 0 && it.minAgeMonth <= monthAge
        }
        staple?.let { result.add(it) }

        // 选1-2个高营养密度食材（蛋白质/补铁等）
        val proteinRich = scored.firstOrNull { it.id != staple?.id }
        proteinRich?.let { result.add(it) }

        // 选1个蔬菜或水果
        val veggieFruit = candidates.firstOrNull {
            (it.category.id == 1 || it.category.id == 2) && it.id != staple?.id && it.id != proteinRich?.id
        }
        veggieFruit?.let { result.add(it) }

        return result
    }

    /**
     * 为食材打分 - 得分越高越优先选择
     */
    private fun scoreIngredient(
        ingredient: Ingredient,
        gap: NutritionGap,
        monthAge: Int,
        baby: BabyInfo
    ): Float {
        var score = 0f
        val n = ingredient.nutritionInfo

        // 蛋白质缺口
        if (gap.protein > 0) score += n.protein * 2f
        // 钙缺口
        if (gap.calcium > 0) score += n.calcium * 0.1f
        // 铁缺口
        if (gap.iron > 0) score += n.iron * 5f
        // 锌缺口
        if (gap.zinc > 0) score += n.zinc * 3f
        // 维生素A缺口
        if (gap.vitaminA > 0) score += n.vitaminA * 0.05f
        // 维生素C缺口
        if (gap.vitaminC > 0) score += n.vitaminC * 0.2f

        // 易消化性加分
        score += ingredient.digestibility * 0.5f

        // 月龄适配加分
        if (monthAge in ingredient.minAgeMonth until ingredient.maxAgeMonth) {
            score += 2f
        } else {
            score -= 5f
        }

        // 主食加分（保证每餐有主食）
        if (ingredient.category.id == 0) score += 3f

        // 随机扰动，避免每次配餐完全相同
        score += Random.nextFloat() * 0.5f

        return score
    }


    /**
     * 计算食材用量
     */
    private fun calculateAmount(
        ingredient: Ingredient,
        gap: NutritionGap,
        targetRatio: Float,
        monthAge: Int
    ): Float {
        val n = ingredient.nutritionInfo

        // 基础量（根据月龄阶段）
        val baseAmount = when (monthAge) {
            in 6 until 9 -> 10f
            in 9 until 12 -> 20f
            in 12 until 18 -> 30f
            in 18 until 24 -> 40f
            else -> 50f
        }

        // 根据营养缺口调整用量（最多调整到基础量的2倍）
        val gapFactor = when {
            gap.protein > 0 && n.protein > 0 ->
                minOf(2f, 1f + gap.protein / (n.protein * baseAmount / 100f))
            gap.iron > 0 && n.iron > 0 ->
                minOf(2f, 1f + gap.iron / (n.iron * baseAmount / 100f))
            gap.calcium > 0 && n.calcium > 0 ->
                minOf(2f, 1f + gap.calcium / (n.calcium * baseAmount / 100f))
            else -> 1f
        }

        val amount = baseAmount * gapFactor * targetRatio
        return Math.round(amount * 10f) / 10f
    }

    /**
     * 生成制作步骤
     */
    private fun generateCookingSteps(ingredientNames: List<String>, texture: String): List<String> {
        val steps = mutableListOf<String>()
        if (ingredientNames.isEmpty()) return steps

        steps.add("准备食材：${ingredientNames.joinToString("、")}")
        steps.add("将食材清洗干净，去除不可食用部分")
        when (texture) {
            "糊状", "泥糊状" -> steps.add("将食材蒸熟/煮熟后打成细腻的$texture")
            "碎末状" -> steps.add("将食材切成碎末状，确保宝宝可以吞咽")
            else -> steps.add("将食材切成适合宝宝咀嚼的小块")
        }
        steps.add("将制作好的辅食放至温热后即可喂食")
        steps.add("注意：首次添加新食材时，先少量尝试并观察宝宝是否有过敏反应")

        return steps
    }


    /**
     * 过滤不适合宝宝食用的食材
     */
    private fun filterIngredients(
        ingredients: List<Ingredient>,
        baby: BabyInfo
    ): List<Ingredient> {
        val monthAge = baby.getMonthAge()
        return ingredients.filter { ingredient ->
            // 过滤过敏源
            val isAllergic = baby.allergies.any { allergy ->
                ingredient.name.contains(allergy) || ingredient.allergenTypes.contains(allergy)
            }
            if (isAllergic) return@filter false

            // 过滤饮食禁忌
            val isRestricted = baby.dietaryRestrictions.any { restriction ->
                ingredient.name.contains(restriction)
            }
            if (isRestricted) return@filter false

            // 过滤月龄不适宜食材
            if (monthAge < ingredient.minAgeMonth || monthAge >= ingredient.maxAgeMonth) {
                return@filter false
            }

            true
        }
    }

    /**
     * 计算营养缺口（仅保留正值）
     */
    private fun calculateGap(
        actual: NutritionSummary,
        standard: NutritionStandard
    ): NutritionGap {
        return NutritionGap(
            protein = positive(standard.protein - actual.protein),
            fat = positive(standard.fat - actual.fat),
            carbohydrate = positive(standard.carbohydrate - actual.carbohydrate),
            calcium = positive(standard.calcium - actual.calcium),
            iron = positive(standard.iron - actual.iron),
            zinc = positive(standard.zinc - actual.zinc),
            vitaminA = positive(standard.vitaminA - actual.vitaminA),
            vitaminC = positive(standard.vitaminC - actual.vitaminC),
            vitaminD = positive(standard.vitaminD - actual.vitaminD),
            vitaminE = positive(standard.vitaminE - actual.vitaminE),
            vitaminB1 = positive(standard.vitaminB1 - actual.vitaminB1),
            vitaminB2 = positive(standard.vitaminB2 - actual.vitaminB2),
            folicAcid = positive(standard.folicAcid - actual.folicAcid),
            calorie = positive(standard.calorie - actual.calorie)
        )
    }

    private fun mergeConsumed(
        consumed: NutritionSummary,
        additional: NutritionSummary
    ): NutritionSummary {
        return NutritionSummary(
            protein = consumed.protein + additional.protein,
            fat = consumed.fat + additional.fat,
            carbohydrate = consumed.carbohydrate + additional.carbohydrate,
            calcium = consumed.calcium + additional.calcium,
            iron = consumed.iron + additional.iron,
            zinc = consumed.zinc + additional.zinc,
            vitaminA = consumed.vitaminA + additional.vitaminA,
            vitaminC = consumed.vitaminC + additional.vitaminC,
            vitaminD = consumed.vitaminD + additional.vitaminD,
            vitaminE = consumed.vitaminE + additional.vitaminE,
            vitaminB1 = consumed.vitaminB1 + additional.vitaminB1,
            vitaminB2 = consumed.vitaminB2 + additional.vitaminB2,
            folicAcid = consumed.folicAcid + additional.folicAcid,
            calorie = consumed.calorie + additional.calorie
        )
    }

    private fun scaleGap(gap: NutritionGap, factor: Float): NutritionGap {
        return NutritionGap(
            protein = gap.protein * factor,
            fat = gap.fat * factor,
            carbohydrate = gap.carbohydrate * factor,
            calcium = gap.calcium * factor,
            iron = gap.iron * factor,
            zinc = gap.zinc * factor,
            vitaminA = gap.vitaminA * factor,
            vitaminC = gap.vitaminC * factor,
            vitaminD = gap.vitaminD * factor,
            vitaminE = gap.vitaminE * factor,
            vitaminB1 = gap.vitaminB1 * factor,
            vitaminB2 = gap.vitaminB2 * factor,
            folicAcid = gap.folicAcid * factor,
            calorie = gap.calorie * factor
        )
    }

    private fun scaleStandard(standard: NutritionStandard, factor: Float): NutritionStandard {
        return NutritionStandard(
            ageGroup = standard.ageGroup,
            calorie = standard.calorie * factor,
            protein = standard.protein * factor,
            fat = standard.fat * factor,
            carbohydrate = standard.carbohydrate * factor,
            calcium = standard.calcium * factor,
            iron = standard.iron * factor,
            zinc = standard.zinc * factor,
            vitaminA = standard.vitaminA * factor,
            vitaminC = standard.vitaminC * factor,
            vitaminD = standard.vitaminD * factor,
            vitaminE = standard.vitaminE * factor,
            vitaminB1 = standard.vitaminB1 * factor,
            vitaminB2 = standard.vitaminB2 * factor,
            folicAcid = standard.folicAcid * factor
        )
    }

    private fun calculateAchievement(
        actual: NutritionSummary,
        standard: NutritionStandard
    ): Map<String, Float> {
        val s = standard
        return mapOf(
            "protein" to rate(actual.protein, s.protein),
            "calcium" to rate(actual.calcium, s.calcium),
            "iron" to rate(actual.iron, s.iron),
            "zinc" to rate(actual.zinc, s.zinc),
            "vitaminA" to rate(actual.vitaminA, s.vitaminA),
            "vitaminC" to rate(actual.vitaminC, s.vitaminC),
            "calorie" to rate(actual.calorie, s.calorie)
        )
    }

    private fun rate(actual: Float, standard: Float): Float {
        if (standard <= 0f) return 100f
        return (actual / standard * 100f).coerceIn(0f, 100f)
    }

    private fun positive(value: Float): Float {
        return if (value > 0f) Math.round(value * 100f) / 100f else 0f
    }
}

