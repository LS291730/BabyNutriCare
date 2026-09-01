package com.babynutricare.app.data.repository

import com.babynutricare.app.data.local.dao.IngredientDao
import com.babynutricare.app.data.local.entity.IngredientEntity
import com.babynutricare.core.data.model.IngredientCategory
import com.babynutricare.core.domain.model.Ingredient
import com.babynutricare.core.domain.model.NutritionInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 食材仓库
 */
class IngredientRepository(
    private val dao: IngredientDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 观察所有食材
     */
    fun observeAll(): Flow<List<Ingredient>> {
        return dao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    /**
     * 获取所有食材
     */
    suspend fun getAll(): List<Ingredient> {
        return dao.getAll().map { it.toDomain() }
    }

    /**
     * 按ID获取食材
     */
    suspend fun getByIds(ids: List<Long>): List<Ingredient> {
        return dao.getByIds(ids).map { it.toDomain() }
    }

    /**
     * 保存食材（批量导入）
     */
    suspend fun saveAll(ingredients: List<Ingredient>) {
        dao.insertAll(ingredients.map { it.toEntity() })
    }

    /**
     * 更新收藏状态
     */
    suspend fun updateFavorite(ingredientId: Long, isFavorite: Boolean) {
        val entity = dao.getAll().find { it.id == ingredientId } ?: return
        dao.update(entity.copy(isFavorite = isFavorite))
    }

    private fun IngredientEntity.toDomain(): Ingredient {
        return Ingredient(
            id = id,
            name = name,
            category = IngredientCategory.fromId(category) ?: IngredientCategory.OTHER,
            nutritionInfo = json.decodeFromString<NutritionInfo>(nutritionInfo),
            isAllergen = isAllergen,
            allergenTypes = json.decodeFromString<List<String>>(allergenTypes),
            defaultUnit = defaultUnit,
            isFavorite = isFavorite,
            digestibility = digestibility,
            minAgeMonth = minAgeMonth,
            maxAgeMonth = maxAgeMonth,
            notes = notes
        )
    }

    private fun Ingredient.toEntity(): IngredientEntity {
        return IngredientEntity(
            id = id,
            name = name,
            category = category.id,
            nutritionInfo = json.encodeToString(nutritionInfo),
            isAllergen = isAllergen,
            allergenTypes = json.encodeToString(allergenTypes),
            defaultUnit = defaultUnit,
            isFavorite = isFavorite,
            digestibility = digestibility,
            minAgeMonth = minAgeMonth,
            maxAgeMonth = maxAgeMonth,
            notes = notes
        )
    }
}