package com.babynutricare.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babynutricare.app.data.repository.BabyInfoRepository
import com.babynutricare.app.data.repository.IngredientRepository
import com.babynutricare.core.data.model.NutritionStandardRepository
import com.babynutricare.core.domain.meal.MealGenerator
import com.babynutricare.core.domain.meal.MealPlanResult
import com.babynutricare.core.domain.model.BabyInfo
import com.babynutricare.core.domain.weaning.WeaningRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页UI状态
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val baby: BabyInfo? = null,
    val monthAge: Int = 0,
    val stageName: String = "",
    val stageDescription: String = "",
    val todayPlan: MealPlanResult? = null
)

/**
 * 首页ViewModel
 * 负责宝宝信息展示 + 今日食谱推荐生成
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val babyInfoRepository: BabyInfoRepository,
    private val ingredientRepository: IngredientRepository,
    private val mealGenerator: MealGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            val baby = babyInfoRepository.getLatest()

            if (baby == null) {
                _uiState.value = HomeUiState(isLoading = false)
                return@launch
            }

            val monthAge = baby.getMonthAge()
            val stage = WeaningRules.getStage(monthAge)

            // 尝试生成今日推荐三餐
            val todayPlan = try {
                val ingredients = ensureIngredientData()
                val standard = NutritionStandardRepository.getStandardByMonth(monthAge)
                mealGenerator.generateIngredientBasedPlan(
                    baby = baby,
                    availableIngredients = ingredients,
                    standard = standard
                )
            } catch (e: Exception) {
                null
            }

            _uiState.value = HomeUiState(
                isLoading = false,
                baby = baby,
                monthAge = monthAge,
                stageName = stage.stageName,
                stageDescription = stage.description,
                todayPlan = todayPlan
            )
        }
    }

    private suspend fun ensureIngredientData(): List<com.babynutricare.core.domain.model.Ingredient> {
        val existing = ingredientRepository.getAll()
        if (existing.isNotEmpty()) return existing
        ingredientRepository.saveAll(com.babynutricare.core.domain.meal.BuiltInIngredients.ingredients)
        return ingredientRepository.getAll()
    }
}