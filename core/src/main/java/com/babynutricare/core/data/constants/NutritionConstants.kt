package com.babynutricare.core.data.constants

/**
 * 营养常量定义
 */
object NutritionConstants {
    // 营养素单位
    const val UNIT_GRAM = "g"
    const val UNIT_MILLILITER = "ml"
    const val UNIT_MICROGRAM = "μg"
    const val UNIT_MILLIGRAM = "mg"
    const val UNIT_IU = "IU"

    // 营养素名称
    const val NUTRITION_PROTEIN = "protein"
    const val NUTRITION_FAT = "fat"
    const val NUTRITION_CARBOHYDRATE = "carbohydrate"
    const val NUTRITION_CALCIUM = "calcium"
    const val NUTRITION_IRON = "iron"
    const val NUTRITION_ZINC = "zinc"
    const val NUTRITION_VITAMIN_A = "vitamin_a"
    const val NUTRITION_VITAMIN_C = "vitamin_c"
    const val NUTRITION_VITAMIN_D = "vitamin_d"
    const val NUTRITION_VITAMIN_E = "vitamin_e"
    const val NUTRITION_VITAMIN_B1 = "vitamin_b1"
    const val NUTRITION_VITAMIN_B2 = "vitamin_b2"
    const val NUTRITION_FOLIC_ACID = "folic_acid"

    // 营养素中文名称
    const val NUTRITION_PROTEIN_CN = "蛋白质"
    const val NUTRITION_FAT_CN = "脂肪"
    const val NUTRITION_CARBOHYDRATE_CN = "碳水化合物"
    const val NUTRITION_CALCIUM_CN = "钙"
    const val NUTRITION_IRON_CN = "铁"
    const val NUTRITION_ZINC_CN = "锌"
    const val NUTRITION_VITAMIN_A_CN = "维生素A"
    const val NUTRITION_VITAMIN_C_CN = "维生素C"
    const val NUTRITION_VITAMIN_D_CN = "维生素D"
    const val NUTRITION_VITAMIN_E_CN = "维生素E"
    const val NUTRITION_VITAMIN_B1_CN = "维生素B1"
    const val NUTRITION_VITAMIN_B2_CN = "维生素B2"
    const val NUTRITION_FOLIC_ACID_CN = "叶酸"

    // 营养素颜色
    const val COLOR_PROTEIN = "#FF6B6B"
    const val COLOR_FAT = "#4ECDC4"
    const val COLOR_CARBOHYDRATE = "#45B7D1"
    const val COLOR_CALCIUM = "#96CEB4"
    const val COLOR_IRON = "#FFEAA7"
    const val COLOR_ZINC = "#DDA0DD"
    const val COLOR_VITAMIN_A = "#98D8C8"
    const val COLOR_VITAMIN_C = "#F7DC6F"
    const val COLOR_VITAMIN_D = "#BB8FCE"
    const val COLOR_VITAMIN_E = "#85C1E9"
    const val COLOR_VITAMIN_B1 = "#F8B500"
    const val COLOR_VITAMIN_B2 = "#FF6F61"
    const val COLOR_FOLIC_ACID = "#00CED1"

    // 营养达标阈值
    const val CALORIE_TARGET_MIN = 0.85f // 85%达标
    const val CALORIE_TARGET_MAX = 1.15f // 115%达标

    // 营养素达标阈值
    const val NUTRITION_TARGET_MIN = 0.8f // 80%达标
    const val NUTRITION_TARGET_MAX = 1.2f // 120%达标

    // 蛋白质/脂肪/碳水化合物的合理比例
    const val PROTEIN_RATIO_MIN = 0.15f
    const val PROTEIN_RATIO_MAX = 0.25f
    const val FAT_RATIO_MIN = 0.30f
    const val FAT_RATIO_MAX = 0.50f
    const val CARBOHYDRATE_RATIO_MIN = 0.30f
    const val CARBOHYDRATE_RATIO_MAX = 0.55f

    // 每日热量参考值（kcal）
    const val CALORIE_0_6_MONTH = 500f
    const val CALORIE_6_9_MONTH = 700f
    const val CALORIE_9_12_MONTH = 800f
    const val CALORIE_12_18_MONTH = 900f
    const val CALORIE_18_24_MONTH = 1000f
    const val CALORIE_24_36_MONTH = 1100f
}