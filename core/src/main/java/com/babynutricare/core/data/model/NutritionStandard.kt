package com.babynutricare.core.data.model

import com.babynutricare.core.data.constants.NutritionConstants

/**
 * 营养标准模型 - 按年龄段定义每日营养需求
 */
data class NutritionStandard(
    val ageGroup: AgeGroup,
    val calorie: Float,            // 每日热量需求 (kcal)
    val protein: Float,            // 蛋白质 (g)
    val fat: Float,                // 脂肪 (g)
    val carbohydrate: Float,       // 碳水化合物 (g)
    val calcium: Float,            // 钙 (mg)
    val iron: Float,               // 铁 (mg)
    val zinc: Float,               // 锌 (mg)
    val vitaminA: Float,           // 维生素A (μg)
    val vitaminC: Float,           // 维生素C (mg)
    val vitaminD: Float,           // 维生素D (IU)
    val vitaminE: Float,           // 维生素E (mg)
    val vitaminB1: Float,          // 维生素B1 (mg)
    val vitaminB2: Float,          // 维生素B2 (mg)
    val folicAcid: Float           // 叶酸 (μg)
)

/**
 * 月龄分组枚举
 */
enum class AgeGroup(val id: Int, val label: String, val minMonth: Int, val maxMonth: Int) {
    ZERO_TO_SIX(0, "0-6月", 0, 6),
    SIX_TO_NINE(1, "6-9月", 6, 9),
    NINE_TO_TWELVE(2, "9-12月", 9, 12),
    TWELVE_TO_EIGHTEEN(3, "12-18月", 12, 18),
    EIGHTEEN_TO_TWENTY_FOUR(4, "18-24月", 18, 24),
    TWENTY_FOUR_TO_THIRTY_SIX(5, "24-36月", 24, 36);

    companion object {
        fun fromMonth(month: Int): AgeGroup {
            return values().firstOrNull { month in it.minMonth until it.maxMonth }
                ?: TWENTY_FOUR_TO_THIRTY_SIX
        }
    }
}

/**
 * 营养标准数据仓库 - 内置0-3岁各月龄营养标准
 * 数据参考中国居民膳食营养素参考摄入量(DRIs)
 */
object NutritionStandardRepository {

    private val standards = mapOf(
        AgeGroup.ZERO_TO_SIX to NutritionStandard(
            ageGroup = AgeGroup.ZERO_TO_SIX,
            calorie = 500f,
            protein = 10f,
            fat = 40f,
            carbohydrate = 60f,
            calcium = 200f,
            iron = 0.3f,
            zinc = 2f,
            vitaminA = 300f,
            vitaminC = 40f,
            vitaminD = 400f,
            vitaminE = 3f,
            vitaminB1 = 0.2f,
            vitaminB2 = 0.2f,
            folicAcid = 60f
        ),
        AgeGroup.SIX_TO_NINE to NutritionStandard(
            ageGroup = AgeGroup.SIX_TO_NINE,
            calorie = 700f,
            protein = 18f,
            fat = 38f,
            carbohydrate = 80f,
            calcium = 350f,
            iron = 5f,
            zinc = 4f,
            vitaminA = 350f,
            vitaminC = 50f,
            vitaminD = 400f,
            vitaminE = 4f,
            vitaminB1 = 0.3f,
            vitaminB2 = 0.4f,
            folicAcid = 80f
        ),
        AgeGroup.NINE_TO_TWELVE to NutritionStandard(
            ageGroup = AgeGroup.NINE_TO_TWELVE,
            calorie = 800f,
            protein = 22f,
            fat = 35f,
            carbohydrate = 100f,
            calcium = 450f,
            iron = 8f,
            zinc = 6f,
            vitaminA = 350f,
            vitaminC = 50f,
            vitaminD = 400f,
            vitaminE = 5f,
            vitaminB1 = 0.4f,
            vitaminB2 = 0.5f,
            folicAcid = 100f
        ),
        AgeGroup.TWELVE_TO_EIGHTEEN to NutritionStandard(
            ageGroup = AgeGroup.TWELVE_TO_EIGHTEEN,
            calorie = 900f,
            protein = 28f,
            fat = 33f,
            carbohydrate = 110f,
            calcium = 550f,
            iron = 8f,
            zinc = 9f,
            vitaminA = 350f,
            vitaminC = 50f,
            vitaminD = 400f,
            vitaminE = 6f,
            vitaminB1 = 0.5f,
            vitaminB2 = 0.6f,
            folicAcid = 120f
        ),
        AgeGroup.EIGHTEEN_TO_TWENTY_FOUR to NutritionStandard(
            ageGroup = AgeGroup.EIGHTEEN_TO_TWENTY_FOUR,
            calorie = 1000f,
            protein = 32f,
            fat = 32f,
            carbohydrate = 120f,
            calcium = 650f,
            iron = 8f,
            zinc = 11f,
            vitaminA = 350f,
            vitaminC = 50f,
            vitaminD = 400f,
            vitaminE = 6f,
            vitaminB1 = 0.6f,
            vitaminB2 = 0.7f,
            folicAcid = 140f
        ),
        AgeGroup.TWENTY_FOUR_TO_THIRTY_SIX to NutritionStandard(
            ageGroup = AgeGroup.TWENTY_FOUR_TO_THIRTY_SIX,
            calorie = 1100f,
            protein = 38f,
            fat = 32f,
            carbohydrate = 130f,
            calcium = 750f,
            iron = 8f,
            zinc = 13f,
            vitaminA = 350f,
            vitaminC = 50f,
            vitaminD = 400f,
            vitaminE = 6f,
            vitaminB1 = 0.6f,
            vitaminB2 = 0.7f,
            folicAcid = 150f
        )
    )

    fun getStandard(ageGroup: AgeGroup): NutritionStandard {
        return standards[ageGroup]
            ?: standards[AgeGroup.ZERO_TO_SIX]!!
    }

    fun getStandardByMonth(month: Int): NutritionStandard {
        return getStandard(AgeGroup.fromMonth(month))
    }
}