package com.babynutricare.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.babynutricare.app.data.local.dao.BabyInfoDao
import com.babynutricare.app.data.local.dao.DietRecordDao
import com.babynutricare.app.data.local.dao.IngredientDao
import com.babynutricare.app.data.local.dao.MealPlanDao
import com.babynutricare.app.data.local.entity.BabyInfoEntity
import com.babynutricare.app.data.local.entity.DietRecordEntity
import com.babynutricare.app.data.local.entity.IngredientEntity
import com.babynutricare.app.data.local.entity.MealPlanEntity

/**
 * 应用数据库实例
 */
@Database(
    entities = [
        BabyInfoEntity::class,
        IngredientEntity::class,
        DietRecordEntity::class,
        MealPlanEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun babyInfoDao(): BabyInfoDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun dietRecordDao(): DietRecordDao
    abstract fun mealPlanDao(): MealPlanDao
}