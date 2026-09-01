package com.babynutricare.core.data.model

/**
 * 食材分类枚举
 */
enum class IngredientCategory(val id: Int, val name: String, val icon: String, val sortOrder: Int) {
    GRAIN(0, "谷物类", "ic_grain", 0),
    VEGETABLE(1, "蔬菜类", "ic_vegetable", 1),
    FRUIT(2, "水果类", "ic_fruit", 2),
    MEAT(3, "肉类", "ic_meat", 3),
    EGG(4, "蛋类", "ic_egg", 4),
    BEAN(5, "豆制品", "ic_bean", 5),
    DAIRY(6, "乳制品", "ic_dairy", 6),
    SPICE(7, "调味品", "ic_spice", 7),
    OTHER(8, "其他", "ic_other", 8);

    companion object {
        fun fromId(id: Int): IngredientCategory? {
            return values().find { it.id == id }
        }
    }
}