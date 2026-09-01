package com.babynutricare.app.ui.mealplan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babynutricare.app.data.repository.BabyInfoRepository
import com.babynutricare.app.data.repository.IngredientRepository
import com.babynutricare.app.data.repository.MealPlanRepository
import com.babynutricare.core.data.model.NutritionStandard
import com.babynutricare.core.data.model.NutritionStandardRepository
import com.babynutricare.core.domain.meal.MealGenerator
import com.babynutricare.core.domain.meal.MealPlanResult
import com.babynutricare.core.domain.model.BabyInfo
import com.babynutricare.core.domain.model.Ingredient
import com.babynutricare.core.domain.model.MealPlan
import com.babynutricare.core.domain.model.MealPlanStatus
import com.babynutricare.core.domain.model.MealPlanType
import com.babynutricare.core.domain.model.NutritionSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/**
 * 配餐结果UI状态
 */
data class MealPlanResultUiState(
    val isLoading: Boolean = false,
    val result: MealPlanResult? = null,
    val error: String? = null,
    val saved: Boolean = false
)

/**
 * 配餐结果ViewModel
 */
@HiltViewModel
class MealPlanResultViewModel @Inject constructor(
    private val mealGenerator: MealGenerator,
    private val babyInfoRepository: BabyInfoRepository,
    private val ingredientRepository: IngredientRepository,
    private val mealPlanRepository: MealPlanRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val planTypeName: String = savedStateHandle.get<String>("planType") ?: "INGREDIENT_BASED"
    private val selectedIds: Set<Long> = savedStateHandle
        .get<String>("selectedIds")
        ?.split(",")
        ?.mapNotNull { it.trim().toLongOrNull() }
        ?.toSet()
        ?: emptySet()

    private val _uiState = MutableStateFlow(MealPlanResultUiState())
    val uiState = _uiState.asStateFlow()

    init {
        generatePlan()
    }

    /**
     * 根据配餐类型生成方案
     */
    private fun generatePlan() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val baby = babyInfoRepository.getLatest()
                if (baby == null) {
                    _uiState.value = MealPlanResultUiState(
                        isLoading = false,
                        error = "请先设置宝宝信息"
                    )
                    return@launch
                }

                val standard = NutritionStandardRepository.getStandardByMonth(baby.getMonthAge())
                val allIngredients = ingredientRepository.getAll()

                // 现有食材配餐：只使用用户选中的食材；周/日维度配餐：使用全部可用食材
                val availableIngredients = if (planTypeName != "INGREDIENT_BASED" || selectedIds.isEmpty()) {
                    allIngredients
                } else {
                    allIngredients.filter { it.id in selectedIds }
                }

                if (planTypeName == "INGREDIENT_BASED" && availableIngredients.isEmpty()) {
                    _uiState.value = MealPlanResultUiState(
                        isLoading = false,
                        error = "没有可用的食材，请返回选择食材"
                    )
                    return@launch
                }

                val result = when (planTypeName) {
                    "WEEKLY" -> generateWeekly(baby, standard, availableIngredients)
                    "DAILY" -> generateDaily(baby, standard, availableIngredients)
                    else -> generateIngredientBased(baby, standard, availableIngredients)
                }

                _uiState.value = MealPlanResultUiState(
                    isLoading = false,
                    result = result,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = MealPlanResultUiState(
                    isLoading = false,
                    error = e.message ?: "配餐失败，请重试"
                )
            }
        }
    }

    private suspend fun generateIngredientBased(
        baby: BabyInfo,
        standard: NutritionStandard,
        ingredients: List<Ingredient>
    ): MealPlanResult {
        return mealGenerator.generateIngredientBasedPlan(
            baby = baby,
            availableIngredients = ingredients,
            standard = standard
        )
    }

    private suspend fun generateDaily(
        baby: BabyInfo,
        standard: NutritionStandard,
        ingredients: List<Ingredient>
    ): MealPlanResult {
        return mealGenerator.generateDailyPlan(
            baby = baby,
            breakfastConsumed = NutritionSummary(),
            availableIngredients = ingredients,
            standard = standard
        )
    }

    private suspend fun generateWeekly(
        baby: BabyInfo,
        standard: NutritionStandard,
        ingredients: List<Ingredient>
    ): MealPlanResult {
        val today = LocalDate.now()
        val weekStart = today.minusDays(today.dayOfWeek.value - 1L)
        return mealGenerator.generateWeeklyPlan(
            baby = baby,
            recordsStartDate = weekStart,
            weekConsumed = NutritionSummary(),
            consumedDays = today.dayOfWeek.value - 1,
            availableIngredients = ingredients,
            standard = standard
        )
    }

    /**
     * 保存配餐方案
     */
    fun savePlan() {
        viewModelScope.launch {
            val state = _uiState.value
            val result = state.result ?: return@launch
            val baby = babyInfoRepository.getLatest() ?: return@launch

            val planType = when (planTypeName) {
                "WEEKLY" -> MealPlanType.WEEKLY
                "DAILY" -> MealPlanType.DAILY
                else -> MealPlanType.INGREDIENT_BASED
            }

            val today = LocalDate.now()
            val endDate = when (planType) {
                MealPlanType.WEEKLY -> today.plusDays(6)
                else -> today
            }

            val plan = MealPlan(
                planId = UUID.randomUUID().toString(),
                planName = "智能配餐方案 ${today.monthValue}月${today.dayOfMonth}日",
                planType = planType,
                startDate = today,
                endDate = endDate,
                status = MealPlanStatus.DRAFT,
                meals = result.meals,
                nutritionAnalysis = result.nutritionSummary
            )
            mealPlanRepository.savePlan(plan)
            _uiState.value = state.copy(saved = true)
        }
    }
}