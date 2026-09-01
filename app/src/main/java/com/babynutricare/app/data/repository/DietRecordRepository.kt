package com.babynutricare.app.data.repository

import com.babynutricare.app.data.local.dao.DietRecordDao
import com.babynutricare.app.data.local.entity.DietRecordEntity
import com.babynutricare.app.data.serialization.LocalDateSerializer
import com.babynutricare.core.data.model.MealSlot
import com.babynutricare.core.domain.model.DietRecord
import com.babynutricare.core.domain.model.IngredientPortion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDate

/**
 * 饮食记录仓库
 */
class DietRecordRepository(
    private val dao: DietRecordDao
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            contextual(LocalDate::class, LocalDateSerializer)
        }
    }

    /**
     * 观察所有饮食记录
     */
    fun observeAll(): Flow<List<DietRecord>> {
        return dao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    /**
     * 观察指定日期范围的记录
     */
    fun observeByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DietRecord>> {
        return dao.observeByDateRange(startDate.toString(), endDate.toString())
            .map { entities -> entities.map { it.toDomain() } }
    }

    /**
     * 获取指定日期的记录
     */
    suspend fun getByDate(date: LocalDate): List<DietRecord> {
        return dao.getByDate(date.toString()).map { it.toDomain() }
    }

    /**
     * 获取指定日期范围的记录
     */
    suspend fun getByDateRange(startDate: LocalDate, endDate: LocalDate): List<DietRecord> {
        return dao.getByDateRange(startDate.toString(), endDate.toString()).map { it.toDomain() }
    }

    /**
     * 添加饮食记录
     */
    suspend fun insert(record: DietRecord): Long {
        return dao.insert(record.toEntity())
    }

    /**
     * 更新饮食记录
     */
    suspend fun update(record: DietRecord) {
        dao.update(record.toEntity())
    }

    /**
     * 删除饮食记录
     */
    suspend fun delete(record: DietRecord) {
        dao.delete(record.toEntity())
    }

    private fun DietRecordEntity.toDomain(): DietRecord {
        return DietRecord(
            id = id,
            recordId = recordId,
            date = LocalDate.parse(date),
            mealSlot = MealSlot.fromId(mealSlot) ?: MealSlot.BREAKFAST,
            ingredients = json.decodeFromString<List<IngredientPortion>>(ingredients),
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DietRecord.toEntity(): DietRecordEntity {
        return DietRecordEntity(
            id = id,
            recordId = recordId,
            date = date.toString(),
            mealSlot = mealSlot.id,
            ingredients = json.encodeToString(ingredients),
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}