package com.babynutricare.core.data.model

import kotlinx.serialization.Serializable

/**
 * 餐次枚举
 */
@Serializable
enum class MealSlot(val id: Int, val value: String, val displayName: String, val sortOrder: Int) {
    BREAKFAST(0, "breakfast", "早餐", 0),
    LUNCH(1, "lunch", "午餐", 1),
    DINNER(2, "dinner", "晚餐", 2),
    SNACK(3, "snack", "加餐", 3);

    companion object {
        fun fromId(id: Int): MealSlot? {
            return values().find { it.id == id }
        }
    }
}