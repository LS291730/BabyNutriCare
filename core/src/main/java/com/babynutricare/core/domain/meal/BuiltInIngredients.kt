package com.babynutricare.core.domain.meal

import com.babynutricare.core.data.model.IngredientCategory
import com.babynutricare.core.domain.model.Ingredient
import com.babynutricare.core.domain.model.NutritionInfo

/**
 * 内置食材库 - 预置常见婴幼儿辅食食材
 * 营养数据参考《中国食物成分表》及婴幼儿营养学标准
 * 营养含量均为每100g可食部
 */
object BuiltInIngredients {

    val ingredients: List<Ingredient> = listOf(
        // ===== 谷物类 =====
        Ingredient(
            name = "强化铁米粉",
            category = IngredientCategory.GRAIN,
            nutritionInfo = NutritionInfo(
                protein = 8.0f, carbohydrate = 80.0f, fat = 1.5f,
                iron = 10.0f, zinc = 4.0f, calcium = 150.0f,
                vitaminA = 200.0f, vitaminB1 = 0.5f, vitaminB2 = 0.5f,
                folicAcid = 100.0f, calorie = 360.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 10,
            minAgeMonth = 6,
            notes = "宝宝第一口辅食首选"
        ),
        Ingredient(
            name = "大米",
            category = IngredientCategory.GRAIN,
            nutritionInfo = NutritionInfo(
                protein = 7.4f, carbohydrate = 77.9f, fat = 0.8f,
                iron = 2.3f, zinc = 1.7f, calcium = 13.0f,
                vitaminB1 = 0.33f, calorie = 346.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 10,
            minAgeMonth = 7,
            notes = "可制成米粥、米糊"
        ),
        Ingredient(
            name = "小米",
            category = IngredientCategory.GRAIN,
            nutritionInfo = NutritionInfo(
                protein = 9.0f, carbohydrate = 75.1f, fat = 3.1f,
                iron = 5.1f, zinc = 1.87f, calcium = 41.0f,
                vitaminB1 = 0.33f, vitaminB2 = 0.1f, calorie = 358.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 7,
            notes = "易消化，适合做小米粥"
        ),
        Ingredient(
            name = "燕麦",
            category = IngredientCategory.GRAIN,
            nutritionInfo = NutritionInfo(
                protein = 15.0f, carbohydrate = 61.6f, fat = 6.7f,
                iron = 7.0f, zinc = 2.59f, calcium = 186.0f,
                vitaminB1 = 0.3f, vitaminB2 = 0.13f, calorie = 367.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 8,
            notes = "含丰富膳食纤维，需煮烂"
        ),
        Ingredient(
            name = "面条",
            category = IngredientCategory.GRAIN,
            nutritionInfo = NutritionInfo(
                protein = 8.3f, carbohydrate = 61.9f, fat = 1.1f,
                iron = 2.9f, zinc = 1.5f, calcium = 30.0f,
                vitaminB1 = 0.22f, vitaminB2 = 0.08f, calorie = 301.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 8,
            notes = "需煮至软烂"
        ),
        Ingredient(
            name = "全麦面包",
            category = IngredientCategory.GRAIN,
            nutritionInfo = NutritionInfo(
                protein = 12.7f, carbohydrate = 48.6f, fat = 3.5f,
                iron = 2.5f, zinc = 1.4f, calcium = 49.0f,
                vitaminB1 = 0.17f, vitaminB2 = 0.09f, calorie = 256.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("小麦"),
            defaultUnit = "g",
            digestibility = 6,
            minAgeMonth = 12,
            notes = "需确认无过敏后再添加"
        ),

        // ===== 蔬菜类 =====
        Ingredient(
            name = "胡萝卜",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 1.0f, carbohydrate = 8.8f, fat = 0.2f,
                vitaminA = 835.0f, vitaminC = 13.0f, calcium = 32.0f,
                iron = 1.0f, zinc = 0.23f, calorie = 39.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 6,
            notes = "富含维生素A，保护视力"
        ),
        Ingredient(
            name = "土豆",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 2.0f, carbohydrate = 17.2f, fat = 0.2f,
                vitaminC = 27.0f, calcium = 8.0f, iron = 0.8f,
                zinc = 0.37f, calorie = 77.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 6,
            notes = "易消化，可做成土豆泥"
        ),
        Ingredient(
            name = "南瓜",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 0.7f, carbohydrate = 5.3f, fat = 0.1f,
                vitaminA = 148.0f, vitaminC = 8.0f, calcium = 16.0f,
                iron = 0.4f, zinc = 0.14f, calorie = 23.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 6,
            notes = "口感香甜，宝宝易接受"
        ),
        Ingredient(
            name = "西兰花",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 4.1f, carbohydrate = 4.3f, fat = 0.6f,
                vitaminC = 51.0f, vitaminA = 120.0f, calcium = 67.0f,
                iron = 1.0f, zinc = 0.78f, folicAcid = 66.0f, calorie = 36.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 8,
            notes = "营养丰富，需煮熟打碎"
        ),
        Ingredient(
            name = "菠菜",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 2.6f, carbohydrate = 4.5f, fat = 0.3f,
                vitaminA = 487.0f, vitaminC = 32.0f, iron = 2.9f,
                calcium = 66.0f, zinc = 0.85f, folicAcid = 194.0f, calorie = 28.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 7,
            minAgeMonth = 8,
            notes = "富含铁和叶酸，焯水后食用"
        ),
        Ingredient(
            name = "番茄",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 0.9f, carbohydrate = 4.0f, fat = 0.2f,
                vitaminC = 19.0f, vitaminA = 92.0f, calcium = 10.0f,
                iron = 0.4f, zinc = 0.13f, calorie = 20.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 8,
            notes = "去皮去籽后食用"
        ),
        Ingredient(
            name = "山药",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 1.9f, carbohydrate = 12.4f, fat = 0.2f,
                vitaminC = 5.0f, calcium = 16.0f, iron = 0.3f,
                zinc = 0.27f, calorie = 56.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 7,
            notes = "健脾养胃，易消化"
        ),
        Ingredient(
            name = "白菜",
            category = IngredientCategory.VEGETABLE,
            nutritionInfo = NutritionInfo(
                protein = 1.5f, carbohydrate = 3.2f, fat = 0.1f,
                vitaminC = 31.0f, calcium = 50.0f, iron = 0.7f,
                zinc = 0.38f, calorie = 17.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 7,
            notes = "叶菜类，煮烂后食用"
        ),

        // ===== 水果类 =====
        Ingredient(
            name = "苹果",
            category = IngredientCategory.FRUIT,
            nutritionInfo = NutritionInfo(
                protein = 0.2f, carbohydrate = 13.5f, fat = 0.2f,
                vitaminC = 4.0f, calcium = 4.0f, iron = 0.6f,
                zinc = 0.19f, calorie = 54.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 6,
            notes = "蒸熟后制成苹果泥"
        ),
        Ingredient(
            name = "香蕉",
            category = IngredientCategory.FRUIT,
            nutritionInfo = NutritionInfo(
                protein = 1.4f, carbohydrate = 22.0f, fat = 0.2f,
                vitaminC = 8.0f, vitaminA = 10.0f, calcium = 7.0f,
                iron = 0.4f, zinc = 0.18f, calorie = 93.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 6,
            notes = "富含钾，直接压成泥即可"
        ),
        Ingredient(
            name = "梨",
            category = IngredientCategory.FRUIT,
            nutritionInfo = NutritionInfo(
                protein = 0.4f, carbohydrate = 13.3f, fat = 0.1f,
                vitaminC = 6.0f, calcium = 9.0f, iron = 0.5f,
                zinc = 0.46f, calorie = 51.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 7,
            notes = "润肺止咳，蒸熟后食用"
        ),
        Ingredient(
            name = "蓝莓",
            category = IngredientCategory.FRUIT,
            nutritionInfo = NutritionInfo(
                protein = 0.7f, carbohydrate = 14.5f, fat = 0.3f,
                vitaminC = 9.7f, vitaminA = 54.0f, calcium = 6.0f,
                iron = 0.3f, zinc = 0.16f, calorie = 57.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 9,
            notes = "富含花青素，打成泥食用"
        ),
        Ingredient(
            name = "橙子",
            category = IngredientCategory.FRUIT,
            nutritionInfo = NutritionInfo(
                protein = 0.8f, carbohydrate = 11.1f, fat = 0.2f,
                vitaminC = 33.0f, vitaminA = 27.0f, calcium = 20.0f,
                iron = 0.4f, zinc = 0.25f, calorie = 48.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 7,
            minAgeMonth = 9,
            notes = "富含维生素C，榨汁或直接食用"
        ),
        Ingredient(
            name = "猕猴桃",
            category = IngredientCategory.FRUIT,
            nutritionInfo = NutritionInfo(
                protein = 0.8f, carbohydrate = 14.5f, fat = 0.6f,
                vitaminC = 62.0f, vitaminA = 22.0f, calcium = 27.0f,
                iron = 1.2f, zinc = 0.57f, calorie = 61.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("猕猴桃"),
            defaultUnit = "g",
            digestibility = 6,
            minAgeMonth = 12,
            notes = "高致敏水果，1岁后少量添加"
        ),
        Ingredient(
            name = "草莓",
            category = IngredientCategory.FRUIT,
            nutritionInfo = NutritionInfo(
                protein = 1.0f, carbohydrate = 7.1f, fat = 0.2f,
                vitaminC = 47.0f, vitaminA = 5.0f, calcium = 18.0f,
                iron = 1.8f, zinc = 0.14f, calorie = 32.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("草莓"),
            defaultUnit = "g",
            digestibility = 6,
            minAgeMonth = 12,
            notes = "部分宝宝可能过敏，注意观察"
        ),

        // ===== 肉类 =====
        Ingredient(
            name = "猪里脊肉",
            category = IngredientCategory.MEAT,
            nutritionInfo = NutritionInfo(
                protein = 20.2f, carbohydrate = 0.7f, fat = 7.9f,
                iron = 1.6f, zinc = 2.3f, calcium = 6.0f,
                vitaminB1 = 0.54f, vitaminB2 = 0.1f, calorie = 155.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 7,
            minAgeMonth = 8,
            notes = "优质蛋白质来源，打成肉泥"
        ),
        Ingredient(
            name = "鸡胸肉",
            category = IngredientCategory.MEAT,
            nutritionInfo = NutritionInfo(
                protein = 24.6f, carbohydrate = 0.6f, fat = 1.9f,
                iron = 0.8f, zinc = 0.9f, calcium = 6.0f,
                vitaminB1 = 0.02f, vitaminB2 = 0.09f, calorie = 118.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 8,
            notes = "低脂高蛋白，适合宝宝"
        ),
        Ingredient(
            name = "牛里脊肉",
            category = IngredientCategory.MEAT,
            nutritionInfo = NutritionInfo(
                protein = 22.2f, carbohydrate = 0.0f, fat = 5.3f,
                iron = 2.6f, zinc = 3.4f, calcium = 7.0f,
                vitaminB2 = 0.12f, calorie = 138.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 6,
            minAgeMonth = 9,
            notes = "富含血红素铁，补铁佳品"
        ),
        Ingredient(
            name = "猪肝",
            category = IngredientCategory.MEAT,
            nutritionInfo = NutritionInfo(
                protein = 19.3f, carbohydrate = 5.0f, fat = 3.5f,
                iron = 22.6f, zinc = 5.8f, vitaminA = 4972.0f,
                vitaminB1 = 0.21f, vitaminB2 = 2.08f, calcium = 6.0f,
                folicAcid = 425.0f, calorie = 129.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 6,
            minAgeMonth = 9,
            notes = "补铁补维生素A效果极佳，每周1-2次"
        ),
        Ingredient(
            name = "三文鱼",
            category = IngredientCategory.MEAT,
            nutritionInfo = NutritionInfo(
                protein = 22.0f, carbohydrate = 0.0f, fat = 13.4f,
                vitaminD = 441.0f, vitaminA = 23.0f, calcium = 13.0f,
                iron = 0.6f, zinc = 0.8f, calorie = 208.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("鱼"),
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 8,
            notes = "富含DHA和维生素D"
        ),
        Ingredient(
            name = "鳕鱼",
            category = IngredientCategory.MEAT,
            nutritionInfo = NutritionInfo(
                protein = 20.4f, carbohydrate = 0.5f, fat = 0.5f,
                vitaminD = 20.0f, calcium = 42.0f, iron = 0.5f,
                zinc = 1.0f, calorie = 88.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("鱼"),
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 8,
            notes = "刺少肉嫩，适合宝宝"
        ),

        // ===== 蛋类 =====
        Ingredient(
            name = "鸡蛋黄",
            category = IngredientCategory.EGG,
            nutritionInfo = NutritionInfo(
                protein = 15.2f, carbohydrate = 3.4f, fat = 28.2f,
                iron = 6.5f, zinc = 3.79f, calcium = 112.0f,
                vitaminA = 438.0f, vitaminD = 158.0f, vitaminB1 = 0.33f,
                vitaminB2 = 0.29f, calorie = 328.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("鸡蛋"),
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 8,
            notes = "从1/4个蛋黄开始添加"
        ),
        Ingredient(
            name = "鸡蛋",
            category = IngredientCategory.EGG,
            nutritionInfo = NutritionInfo(
                protein = 13.3f, carbohydrate = 2.8f, fat = 8.8f,
                iron = 2.0f, zinc = 1.1f, calcium = 56.0f,
                vitaminA = 234.0f, vitaminD = 60.0f, vitaminB1 = 0.11f,
                vitaminB2 = 0.27f, folicAcid = 70.0f, calorie = 144.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("鸡蛋"),
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 9,
            notes = "全蛋需9个月后添加"
        ),

        // ===== 豆制品 =====
        Ingredient(
            name = "嫩豆腐",
            category = IngredientCategory.BEAN,
            nutritionInfo = NutritionInfo(
                protein = 6.6f, carbohydrate = 3.0f, fat = 3.0f,
                iron = 1.5f, zinc = 0.59f, calcium = 145.0f,
                vitaminB1 = 0.04f, vitaminB2 = 0.03f, calorie = 60.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("大豆"),
            defaultUnit = "g",
            digestibility = 9,
            minAgeMonth = 8,
            notes = "口感软嫩，富含植物蛋白和钙"
        ),
        Ingredient(
            name = "无糖豆浆",
            category = IngredientCategory.BEAN,
            nutritionInfo = NutritionInfo(
                protein = 3.0f, carbohydrate = 1.2f, fat = 1.6f,
                iron = 0.7f, zinc = 0.35f, calcium = 10.0f,
                vitaminB1 = 0.03f, calorie = 31.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("大豆"),
            defaultUnit = "ml",
            digestibility = 8,
            minAgeMonth = 10,
            notes = "需确认无大豆过敏"
        ),

        // ===== 乳制品 =====
        Ingredient(
            name = "配方奶",
            category = IngredientCategory.DAIRY,
            nutritionInfo = NutritionInfo(
                protein = 12.0f, carbohydrate = 55.0f, fat = 24.0f,
                iron = 6.0f, zinc = 4.0f, calcium = 450.0f,
                vitaminA = 400.0f, vitaminD = 260.0f, vitaminE = 5.0f,
                vitaminB1 = 0.4f, vitaminB2 = 0.8f, folicAcid = 60.0f,
                calorie = 480.0f
            ),
            isAllergen = false,
            defaultUnit = "g",
            digestibility = 10,
            minAgeMonth = 0,
            notes = "按配方说明冲泡"
        ),
        Ingredient(
            name = "酸奶",
            category = IngredientCategory.DAIRY,
            nutritionInfo = NutritionInfo(
                protein = 3.2f, carbohydrate = 7.3f, fat = 3.0f,
                calcium = 120.0f, vitaminB2 = 0.14f, calorie = 72.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("牛奶"),
            defaultUnit = "g",
            digestibility = 8,
            minAgeMonth = 12,
            notes = "选择无糖原味酸奶"
        ),
        Ingredient(
            name = "奶酪",
            category = IngredientCategory.DAIRY,
            nutritionInfo = NutritionInfo(
                protein = 25.7f, carbohydrate = 3.5f, fat = 23.5f,
                calcium = 799.0f, vitaminA = 152.0f, calorie = 328.0f
            ),
            isAllergen = true,
            allergenTypes = listOf("牛奶"),
            defaultUnit = "g",
            digestibility = 7,
            minAgeMonth = 12,
            notes = "高钙，选择低钠奶酪"
        )
    )

    fun findByName(name: String): Ingredient? {
        return ingredients.find { it.name == name }
    }
}