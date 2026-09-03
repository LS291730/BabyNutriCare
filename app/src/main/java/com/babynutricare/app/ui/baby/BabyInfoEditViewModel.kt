package com.babynutricare.app.ui.baby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babynutricare.app.data.repository.BabyInfoRepository
import com.babynutricare.core.domain.model.BabyInfo
import com.babynutricare.core.domain.weaning.WeaningRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * 宝宝信息编辑UI状态
 */
data class BabyEditUiState(
    val isLoading: Boolean = true,
    val babyId: Long = 0,
    val name: String = "",
    val birthDate: LocalDate = LocalDate.now().minusMonths(10),
    val gender: Int = 1,                      // 0-女, 1-男
    val weight: String = "",
    val height: String = "",
    val allergies: Set<String> = emptySet(),
    val saved: Boolean = false
)

/**
 * 宝宝信息编辑ViewModel
 */
@HiltViewModel
class BabyInfoEditViewModel @Inject constructor(
    private val babyInfoRepository: BabyInfoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BabyEditUiState())
    val uiState = _uiState.asStateFlow()

    val commonAllergens = WeaningRules.commonAllergens

    init {
        viewModelScope.launch {
            val baby = babyInfoRepository.getLatest()
            if (baby != null) {
                _uiState.value = BabyEditUiState(
                    isLoading = false,
                    babyId = baby.id,
                    name = baby.name,
                    birthDate = baby.birthDate,
                    gender = baby.gender,
                    weight = if (baby.weight > 0f) baby.weight.toString() else "",
                    height = if (baby.height > 0f) baby.height.toString() else "",
                    allergies = baby.allergies.toSet(),
                    saved = false
                )
            } else {
                _uiState.value = BabyEditUiState(isLoading = false)
            }
        }
    }

    fun updateName(name: String) = _uiState.update { it.copy(name = name) }
    fun updateBirthDate(date: LocalDate) = _uiState.update { it.copy(birthDate = date) }
    fun updateGender(gender: Int) = _uiState.update { it.copy(gender = gender) }
    fun updateWeight(weight: String) = _uiState.update { it.copy(weight = weight) }
    fun updateHeight(height: String) = _uiState.update { it.copy(height = height) }

    fun toggleAllergy(allergen: String) {
        _uiState.update { state ->
            val newSet = if (allergen in state.allergies) {
                state.allergies - allergen
            } else {
                state.allergies + allergen
            }
            state.copy(allergies = newSet)
        }
    }

    /**
     * 保存宝宝信息
     */
    fun save() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.name.isBlank()) {
                _uiState.value = state.copy(saved = false)
                return@launch
            }
            val baby = BabyInfo(
                id = state.babyId,
                name = state.name.trim(),
                birthDate = state.birthDate,
                gender = state.gender,
                weight = state.weight.toFloatOrNull() ?: 0f,
                height = state.height.toFloatOrNull() ?: 0f,
                allergies = state.allergies.toList()
            )
            babyInfoRepository.saveBaby(baby)
            _uiState.value = state.copy(saved = true)
        }
    }

    private fun MutableStateFlow<BabyEditUiState>.update(transform: (BabyEditUiState) -> BabyEditUiState) {
        this.value = transform(this.value)
    }
}