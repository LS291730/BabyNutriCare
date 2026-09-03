package com.babynutricare.app.ui.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babynutricare.app.data.repository.BabyInfoRepository
import com.babynutricare.app.data.repository.DietRecordRepository
import com.babynutricare.app.data.repository.IngredientRepository
import com.babynutricare.core.data.model.NutritionStandardRepository
import com.babynutricare.core.data.model.MealSlot
import com.babynutricare.core.domain.model.DietRecord
import com.babynutricare.core.domain.model.NutritionSummary
import com.babynutricare.core.domain.nutrition.NutritionCalculator
import com.babynutricare.core.domain.nutrition.NutritionGapAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * 三餐Tab UI状态
 */
data class MealsUiState(
    val isLoading: Boolean = true,
    val hasBaby: Boolean = false,
    val babyMonth: Int = 0,
    val weekDays: List<DayMealStatus> = emptyList(),      // 本周每日餐次状态
    val todayMeals: List<DietRecord> = emptyList(),        // 今日已记录餐次
    val achievement: Map<String, Float> = emptyMap(),      // 关键营养达标率
    val advice: List<String> = emptyList()                 // 营养建议
)

/**
 * 单日餐次状态
 */
data class DayMealStatus(
    val date: LocalDate,
    val mealCount: Int,
    val isToday: Boolean = false
)

/**
 * 三餐Tab ViewModel
 * 提供日/周维度饮食记录概览 + 营养分析建议
 */
@HiltViewModel
class MealsViewModel @Inject constructor(
    private val dietRecordRepository: DietRecordRepository,
    private val ingredientRepository: IngredientRepository,
    private val babyInfoRepository: BabyInfoRepository,
    private val nutritionCalculator: NutritionCalculator = NutritionCalculator()
) : ViewModel() {

    private val gapAnalyzer = NutritionGapAnalyzer()

    private val _uiState = MutableStateFlow(MealsUiState())
    val uiState: StateFlow<MealsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val baby = babyInfoRepository.getLatest()
            val ingredients = ensureIngredientData()
            val today = LocalDate.now()
            val weekStart = today.minusDays(6)

            val weekRecords = dietRecordRepository.getByDateRange(weekStart, today)
            val todayMeals = weekRecords.filter { it.date == today }

            val weekDays = (0..6).map { offset ->
                val date = weekStart.plusDays(offset.toLong())
                DayMealStatus(
                    date = date,
                    mealCount = weekRecords.count { it.date == date },
                    isToday = date == today
                )
            }

            val ingredientMap = ingredients.associateBy { it.id }
            val nutrition = nutritionCalculator.mergeSummaries(
                weekRecords.map { record ->
                    nutritionCalculator.calculateNutrition(record.ingredients, ingredientMap)
                }
            )

            val achievement = if (baby != null) {
                calculateRates(nutrition, weekRecords, baby.getMonthAge())
            } else {
                emptyMap()
            }

            _uiState.value = MealsUiState(
                isLoading = false,
                hasBaby = baby != null,
                babyMonth = baby?.getMonthAge() ?: 0,
                weekDays = weekDays,
                todayMeals = todayMeals,
                achievement = achievement,
                advice = buildAdvice(achievement)
            )
        }
    }

    private suspend fun ensureIngredientData(): List<com.babynutricare.core.domain.model.Ingredient> {
        val existing = ingredientRepository.getAll()
        if (existing.isNotEmpty()) return existing
        ingredientRepository.saveAll(com.babynutricare.core.domain.meal.BuiltInIngredients.ingredients)
        return ingredientRepository.getAll()
    }

    private fun calculateRates(
        weekNutrition: NutritionSummary,
        weekRecords: List<DietRecord>,
        monthAge: Int
    ): Map<String, Float> {
        val recordedDays = weekRecords.map { it.date }.distinct().size
        if (recordedDays == 0) return emptyMap()
        val perDay = NutritionSummary(
            protein = weekNutrition.protein / recordedDays,
            calcium = weekNutrition.calcium / recordedDays,
            iron = weekNutrition.iron / recordedDays,
            zinc = weekNutrition.zinc / recordedDays,
            vitaminA = weekNutrition.vitaminA / recordedDays,
            vitaminC = weekNutrition.vitaminC / recordedDays,
            calorie = weekNutrition.calorie / recordedDays
        )
        val standard = NutritionStandardRepository.getStandardByMonth(monthAge)
        return gapAnalyzer.calculateAchievementRate(perDay, standard)
    }

    private fun buildAdvice(achievement: Map<String, Float>): List<String> {
        if (achievement.isEmpty()) return listOf("本周还没有饮食记录，去记录一下吧")

        val labels = mapOf(
            "protein" to "蛋白质",
            "calcium" to "钙",
            "iron" to "铁",
            "zinc" to "锌",
            "vitaminA" to "维生素A",
            "vitaminC" to "维生素C",
            "calorie" to "热量"
        )
        val low = labels.filter { (key, _) -> (achievement[key] ?: 0f) < 80f }
        return if (low.isEmpty()) {
            listOf("各项营养均衡达标，继续保持哦 🎉")
        } else {
            listOf("本周「${low.values.joinToString("、")}」摄入偏低，建议下周加强补充")
        }
    }
}