package com.babynutricare.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babynutricare.app.data.repository.BabyInfoRepository
import com.babynutricare.core.domain.model.BabyInfo
import com.babynutricare.core.domain.weaning.WeaningRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
    val forbiddenFoods: List<String> = emptyList()
)

/**
 * 首页ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val babyInfoRepository: BabyInfoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val baby = babyInfoRepository.observeLatest()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            val baby = babyInfoRepository.getLatest()
            if (baby != null) {
                val monthAge = baby.getMonthAge()
                val stage = WeaningRules.getStage(monthAge)
                _uiState.value = HomeUiState(
                    isLoading = false,
                    baby = baby,
                    monthAge = monthAge,
                    stageName = stage.stageName,
                    stageDescription = stage.description,
                    forbiddenFoods = stage.forbiddenFoods
                )
            } else {
                _uiState.value = HomeUiState(isLoading = false)
            }
        }
    }
}