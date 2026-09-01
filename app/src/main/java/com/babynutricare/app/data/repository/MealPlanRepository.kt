package com.babynutricare.app.data.repository

import com.babynutricare.app.data.local.dao.MealPlanDao
import com.babynutricare.app.data.local.entity.MealPlanEntity
import com.babynutricare.app.data.serialization.LocalDateSerializer
import com.babynutricare.core.domain.model.MealPlan
import com.babynutricare.core.domain.model.MealPlanStatus
import com.babynutricare.core.domain.model.MealPlanType
import com.babynutricare.core.domain.model.NutritionSummary
import com.babynutricare.core.domain.model.PlannedMeal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDate

/**
 * 配餐方案仓库
 */
class MealPlanRepository(
    private val dao: MealPlanDao
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            contextual(LocalDate::class, LocalDateSerializer)
        }
    }

    /**
     * 观察所有配餐方案
     */
    fun observeAll(): Flow<List<MealPlan>> {
        return dao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    /**
     * 按类型观察配餐方案
     */
    fun observeByType(type: MealPlanType): Flow<List<MealPlan>> {
        return dao.observeByType(type.id).map { entities -> entities.map { it.toDomain() } }
    }

    /**
     * 保存配餐方案
     */
    suspend fun savePlan(plan: MealPlan): Long {
        return dao.insert(plan.toEntity())
    }

    /**
     * 获取最新配餐方案
     */
    suspend fun getLatestByType(type: MealPlanType): MealPlan? {
        return dao.getLatestByType(type.id)?.toDomain()
    }

    private fun MealPlanEntity.toDomain(): MealPlan {
        return MealPlan(
            id = id,
            planId = planId,
            planName = planName,
            planType = MealPlanType.values().firstOrNull { it.id == planType } ?: MealPlanType.INGREDIENT_BASED,
            startDate = LocalDate.parse(startDate),
            endDate = LocalDate.parse(endDate),
            status = MealPlanStatus.values().firstOrNull { it.id == status } ?: MealPlanStatus.DRAFT,
            meals = json.decodeFromString<List<PlannedMeal>>(mealsJson),
            nutritionAnalysis = json.decodeFromString<NutritionSummary>(nutritionSummaryJson),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun MealPlan.toEntity(): MealPlanEntity {
        return MealPlanEntity(
            id = id,
            planId = planId,
            planName = planName,
            planType = planType.id,
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            status = status.id,
            mealsJson = json.encodeToString(meals),
            nutritionSummaryJson = json.encodeToString(nutritionAnalysis),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}