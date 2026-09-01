package com.babynutricare.app.ui.mealplan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babynutricare.app.ui.theme.BackgroundCream
import com.babynutricare.app.ui.theme.PrimaryOrange
import com.babynutricare.app.ui.theme.SecondaryGreen
import com.babynutricare.app.ui.theme.WarningRed
import com.babynutricare.core.data.model.IngredientCategory
import com.babynutricare.core.domain.model.Ingredient

/**
 * 食材选择页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSelectScreen(
    onBack: () -> Unit,
    onGeneratePlan: (MealPlanType, Set<Long>) -> Unit,
    viewModel: IngredientSelectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BackgroundCream,
        topBar = {
            TopAppBar(
                title = { Text("选择现有食材") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryOrange,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (uiState.selectedIngredients.isNotEmpty()) {
                SelectedBar(
                    selectedCount = uiState.selectedIngredients.size,
                    onGenerate = {
                        onGeneratePlan(MealPlanType.INGREDIENT_BASED, uiState.selectedIds)
                    }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 月龄提示
            if (uiState.baby != null) {
                item {
                    AgeHintCard(monthAge = uiState.baby.getMonthAge())
                }
            }

            // 按分类展示食材
            IngredientCategory.values().forEach { category ->
                val categoryIngredients = uiState.ingredients.filter { it.category == category }
                if (categoryIngredients.isNotEmpty()) {
                    item {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(categoryIngredients, key = { it.id }) { ingredient ->
                        IngredientSelectRow(
                            ingredient = ingredient,
                            isSelected = ingredient.id in uiState.selectedIds,
                            onClick = { viewModel.toggleIngredient(ingredient) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * 月龄提示卡片
 */
@Composable
private fun AgeHintCard(monthAge: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryGreen.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "当前宝宝${monthAge}个月，系统将自动筛选适合月龄的食材，并规避过敏源。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(12.dp)
        )
    }
}

/**
 * 食材选择行
 */
@Composable
private fun IngredientSelectRow(
    ingredient: Ingredient,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                PrimaryOrange.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (ingredient.isAllergen) {
                        Text(
                            text = " 过敏源",
                            style = MaterialTheme.typography.labelMedium,
                            color = WarningRed
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ingredient.notes.ifEmpty { "${ingredient.defaultUnit}可食用" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            // 选中标记
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (isSelected) PrimaryOrange else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "已选择",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 底部生成方案栏
 */
@Composable
private fun SelectedBar(
    selectedCount: Int,
    onGenerate: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = PrimaryOrange
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "智能配餐（已选$selectedCount种食材）",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}