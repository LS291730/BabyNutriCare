package com.babynutricare.app.di

import com.babynutricare.app.data.local.database.AppDatabase
import com.babynutricare.app.data.local.dao.BabyInfoDao
import com.babynutricare.app.data.local.dao.DietRecordDao
import com.babynutricare.app.data.local.dao.IngredientDao
import com.babynutricare.app.data.local.dao.MealPlanDao
import com.babynutricare.app.data.repository.BabyInfoRepository
import com.babynutricare.app.data.repository.DietRecordRepository
import com.babynutricare.app.data.repository.IngredientRepository
import com.babynutricare.app.data.repository.MealPlanRepository
import com.babynutricare.core.domain.meal.MealGenerator
import com.babynutricare.core.domain.nutrition.NutritionCalculator
import com.babynutricare.core.domain.nutrition.NutritionGapAnalyzer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 领域与仓库依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    // ===== DAO =====
    @Provides
    @Singleton
    fun provideBabyInfoDao(db: AppDatabase): BabyInfoDao = db.babyInfoDao()

    @Provides
    @Singleton
    fun provideIngredientDao(db: AppDatabase): IngredientDao = db.ingredientDao()

    @Provides
    @Singleton
    fun provideDietRecordDao(db: AppDatabase): DietRecordDao = db.dietRecordDao()

    @Provides
    @Singleton
    fun provideMealPlanDao(db: AppDatabase): MealPlanDao = db.mealPlanDao()

    // ===== 核心算法 =====
    @Provides
    @Singleton
    fun provideNutritionCalculator(): NutritionCalculator = NutritionCalculator()

    @Provides
    @Singleton
    fun provideNutritionGapAnalyzer(): NutritionGapAnalyzer = NutritionGapAnalyzer()

    @Provides
    @Singleton
    fun provideMealGenerator(
        nutritionCalculator: NutritionCalculator
    ): MealGenerator = MealGenerator(nutritionCalculator)

    // ===== 仓库 =====
    @Provides
    @Singleton
    fun provideBabyInfoRepository(
        dao: BabyInfoDao
    ): BabyInfoRepository = BabyInfoRepository(dao)

    @Provides
    @Singleton
    fun provideIngredientRepository(
        dao: IngredientDao
    ): IngredientRepository = IngredientRepository(dao)

    @Provides
    @Singleton
    fun provideDietRecordRepository(
        dao: DietRecordDao
    ): DietRecordRepository = DietRecordRepository(dao)

    @Provides
    @Singleton
    fun provideMealPlanRepository(
        dao: MealPlanDao
    ): MealPlanRepository = MealPlanRepository(dao)
}