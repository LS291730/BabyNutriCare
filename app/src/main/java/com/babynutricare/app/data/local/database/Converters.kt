package com.babynutricare.app.data.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room类型转换器
 * 用于将领域模型序列化为JSON存储
 */
class Converters {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromListToString(value: List<String>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toListFromString(value: String): List<String> {
        return try {
            json.decodeFromString<List<String>>(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromLongListToString(value: List<Long>?): String {
        return json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toLongListFromString(value: String): List<Long> {
        return try {
            json.decodeFromString<List<Long>>(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}