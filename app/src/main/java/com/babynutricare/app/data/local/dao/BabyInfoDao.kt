package com.babynutricare.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.babynutricare.app.data.local.entity.BabyInfoEntity
import kotlinx.coroutines.flow.Flow

/**
 * 宝宝信息DAO
 */
@Dao
interface BabyInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(baby: BabyInfoEntity): Long

    @Update
    suspend fun update(baby: BabyInfoEntity)

    @Delete
    suspend fun delete(baby: BabyInfoEntity)

    @Query("SELECT * FROM baby_info ORDER BY id DESC LIMIT 1")
    fun observeLatest(): Flow<BabyInfoEntity?>

    @Query("SELECT * FROM baby_info ORDER BY id DESC LIMIT 1")
    suspend fun getLatest(): BabyInfoEntity?

    @Query("SELECT * FROM baby_info WHERE id = :id")
    suspend fun getById(id: Long): BabyInfoEntity?

    @Query("SELECT COUNT(*) FROM baby_info")
    suspend fun count(): Int
}