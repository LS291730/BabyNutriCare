package com.babynutricare.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 宝宝信息实体
 */
@Entity(
    tableName = "baby_info",
    indices = [Index(value = ["birthDate"])]
)
data class BabyInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthDate: String,          // yyyy-MM-dd
    val gender: Int = 1,            // 0-女, 1-男
    val weight: Float = 0f,         // kg
    val height: Float = 0f,         // cm
    val allergies: String = "[]",   // JSON数组
    val dietaryRestrictions: String = "[]", // JSON数组
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)