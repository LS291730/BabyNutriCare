package com.babynutricare.app.data.repository

import com.babynutricare.app.data.local.dao.BabyInfoDao
import com.babynutricare.app.data.local.entity.BabyInfoEntity
import com.babynutricare.core.domain.model.BabyInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

/**
 * 宝宝信息仓库
 */
class BabyInfoRepository(
    private val dao: BabyInfoDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 观察最新宝宝信息
     */
    fun observeLatest(): Flow<BabyInfo?> {
        return dao.observeLatest().map { entity -> entity?.toDomain() }
    }

    /**
     * 获取最新宝宝信息
     */
    suspend fun getLatest(): BabyInfo? {
        return dao.getLatest()?.toDomain()
    }

    /**
     * 保存宝宝信息（新增或更新）
     */
    suspend fun saveBaby(baby: BabyInfo): Long {
        val existing = dao.getById(baby.id)
        val entity = if (existing != null) {
            existing.toEntity(baby)
        } else {
            baby.toEntity()
        }
        return dao.insert(entity)
    }

    private fun BabyInfoEntity.toDomain(): BabyInfo {
        return BabyInfo(
            id = id,
            name = name,
            birthDate = LocalDate.parse(birthDate),
            gender = gender,
            weight = weight,
            height = height,
            allergies = json.decodeFromString<List<String>>(allergies),
            dietaryRestrictions = json.decodeFromString<List<String>>(dietaryRestrictions),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun BabyInfo.toEntity(): BabyInfoEntity {
        return BabyInfoEntity(
            name = name,
            birthDate = birthDate.toString(),
            gender = gender,
            weight = weight,
            height = height,
            allergies = json.encodeToString(allergies),
            dietaryRestrictions = json.encodeToString(dietaryRestrictions),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun BabyInfoEntity.toEntity(baby: BabyInfo): BabyInfoEntity {
        return copy(
            name = baby.name,
            birthDate = baby.birthDate.toString(),
            gender = baby.gender,
            weight = baby.weight,
            height = baby.height,
            allergies = json.encodeToString(baby.allergies),
            dietaryRestrictions = json.encodeToString(baby.dietaryRestrictions),
            updatedAt = System.currentTimeMillis()
        )
    }
}