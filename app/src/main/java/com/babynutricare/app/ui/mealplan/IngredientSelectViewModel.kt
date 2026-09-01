package com.babynutricare.app.ui.mealplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babynutricare.app.data.repository.BabyInfoRepository
import com.babynutricare.app.data.repository.IngredientRepository
import com.babynutricare.core.data.model.IngredientCategory
import com.babynutricare.core.domain.model.BabyInfo
import com.babynutricare.core.domain.model.Ingredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 食材选择UI状态
 */
data class IngredientSelectUiState(
    val isLoading: Boolean = true,
    val baby: BabyInfo? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val selectedIngredients: List<Ingredient> = emptyList()
)

/**
 * 食材选择ViewModel
 */
@HiltViewModel
class IngredientSelectViewModel @Inject constructor(
    private val ingredientRepository: IngredientRepository,
    private val babyInfoRepository: BabyInfoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngredientSelectUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val baby = babyInfoRepository.getLatest()
            val ingredients = ingredientRepository.getAll().ifEmpty {
                // 首次使用：导入内置食材库后重新读取（获取真实数据库ID）
                ingredientRepository.saveAll(com.babynutricare.core.domain.meal.BuiltInIngredients.ingredients)
                ingredientRepository.getAll()
            }
            _uiState.value = IngredientSelectUiState(
                isLoading = false,
                baby = baby,
                ingredients = ingredients,
                selectedIds = emptySet(),
                selectedIngredients = emptyList()
            )
        }
    }

    /**
     * 切换食材选中状态
     */
    fun toggleIngredient(ingredient: Ingredient) {
        val current = _uiState.value
        val selectedIds = if (ingredient.id in current.selectedIds) {
            current.selectedIds - ingredient.id
        } else {
            current.selectedIds + ingredient.id
        }
        val selectedIngredients = current.ingredients.filter { it.id in selectedIds }
        _uiState.value = current.copy(
            selectedIds = selectedIds,
            selectedIngredients = selectedIngredients
        )
    }

    /**
     * 按分类获取食材
     */
    fun getIngredientsByCategory(category: IngredientCategory): List<Ingredient> {
        return _uiState.value.ingredients.filter { it.category == category }
    }
}