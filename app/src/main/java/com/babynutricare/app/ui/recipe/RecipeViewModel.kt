package com.babynutricare.app.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babynutricare.app.data.repository.BabyInfoRepository
import com.babynutricare.app.data.repository.IngredientRepository
import com.babynutricare.core.data.model.IngredientCategory
import com.babynutricare.core.domain.meal.BuiltInIngredients
import com.babynutricare.core.domain.model.Ingredient
import com.babynutricare.core.domain.model.NutritionInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 食谱Tab UI状态
 */
data class RecipeUiState(
    val isLoading: Boolean = true,
    val ingredients: List<Ingredient> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val selectedIngredients: List<Ingredient> = emptyList(),
    val categoryFilter: IngredientCategory? = null,
    val babyMonth: Int = 0,
    val hasBaby: Boolean = false,
    val savedToast: Boolean = false
)

/**
 * 食谱Tab ViewModel
 * 负责食材库管理 + 快速添加 + 智能配餐入口
 */
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val ingredientRepository: IngredientRepository,
    private val babyInfoRepository: BabyInfoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState = _uiState.asStateFlow()

    /** 内置常见食材（用于快速添加） */
    val commonQuickAdd = BuiltInIngredients.ingredients.take(12)

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val baby = babyInfoRepository.getLatest()
            val ingredients = ensureIngredientData()
            _uiState.value = RecipeUiState(
                isLoading = false,
                ingredients = ingredients,
                selectedIds = emptySet(),
                selectedIngredients = emptyList(),
                categoryFilter = null,
                babyMonth = baby?.getMonthAge() ?: 0,
                hasBaby = baby != null
            )
        }
    }

    /**
     * 确保内置食材已导入数据库
     */
    private suspend fun ensureIngredientData(): List<Ingredient> {
        val existing = ingredientRepository.getAll()
        if (existing.isNotEmpty()) return existing
        ingredientRepository.saveAll(BuiltInIngredients.ingredients)
        return ingredientRepository.getAll()
    }

    /**
     * 切换分类筛选
     */
    fun selectCategory(category: IngredientCategory?) {
        _uiState.update { it.copy(categoryFilter = category) }
    }

    /**
     * 勾选/取消食材（加入配餐篮）
     */
    fun toggleIngredient(ingredient: Ingredient) {
        _uiState.update { state ->
            val newSelectedIds = if (ingredient.id in state.selectedIds) {
                state.selectedIds - ingredient.id
            } else {
                state.selectedIds + ingredient.id
            }
            state.copy(
                selectedIds = newSelectedIds,
                selectedIngredients = state.ingredients.filter { it.id in newSelectedIds }
            )
        }
    }

    /**
     * 快速添加：确保食材在库中并选中
     */
    fun quickAdd(ingredient: Ingredient) {
        viewModelScope.launch {
            _uiState.update { it.copy(savedToast = true) }
            val current = _uiState.value
            val inDb = current.ingredients.firstOrNull { it.name == ingredient.name }
            if (inDb != null) {
                toggleIngredient(inDb)
            } else {
                // 库中不存在，先入库再选中
                ingredientRepository.saveAll(listOf(ingredient))
                val updated = ensureIngredientData()
                val saved = updated.firstOrNull { it.name == ingredient.name }
                _uiState.value = current.copy(ingredients = updated)
                if (saved != null) toggleIngredient(saved)
            }
        }
    }

    /**
     * 手动添加自定义食材
     */
    fun addCustomIngredient(name: String, category: IngredientCategory) {
        viewModelScope.launch {
            val trimmed = name.trim()
            if (trimmed.isEmpty()) return@launch
            val current = _uiState.value
            if (current.ingredients.any { it.name == trimmed }) return@launch

            val custom = Ingredient(
                name = trimmed,
                category = category,
                nutritionInfo = NutritionInfo(),
                defaultUnit = "g",
                digestibility = 6,
                minAgeMonth = 6,
                notes = "家长自定义食材"
            )
            ingredientRepository.saveAll(listOf(custom))
            val updated = ensureIngredientData()
            _uiState.value = current.copy(ingredients = updated)
        }
    }

    /**
     * 清空已选食材
     */
    fun clearSelection() {
        _uiState.update { it.copy(selectedIds = emptySet(), selectedIngredients = emptyList()) }
    }

    private fun MutableStateFlow<RecipeUiState>.update(transform: (RecipeUiState) -> RecipeUiState) {
        this.value = transform(this.value)
    }
}