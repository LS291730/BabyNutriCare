package com.babynutricare.app.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babynutricare.app.ui.mealplan.MealPlanType
import com.babynutricare.app.ui.theme.PrimaryOrange
import com.babynutricare.app.ui.theme.SecondaryGreen
import com.babynutricare.app.ui.theme.TextLight
import com.babynutricare.core.data.model.IngredientCategory
import com.babynutricare.core.domain.model.Ingredient

/**
 * 食谱Tab - 食材选择与智能配餐入口
 */
@Composable
fun RecipeScreen(
    onGeneratePlan: (MealPlanType, Set<Long>) -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部渐变横幅
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(PrimaryOrange, Color(0xFFFFB37E))))
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        text = "🍎 智能食谱",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (uiState.hasBaby) {
                            "宝宝${uiState.babyMonth}个月，选一选家里食材，马上配出营养餐"
                        } else {
                            "先去首页设置宝宝信息，配餐更精准哦"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "⚡ 厨房常见食材 · 一键添加",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(viewModel.commonQuickAdd, key = { it.name }) { ingredient ->
                            val selectedNames = uiState.selectedIngredients.map { it.name }
                            QuickAddChip(
                                ingredient = ingredient,
                                isSelected = ingredient.name in selectedNames,
                                onClick = { viewModel.quickAdd(ingredient) }
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(SecondaryGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, tint = SecondaryGreen)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "手动添加食材",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "家里有其他食材？补上名称即可",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextLight
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "🥬 食材库",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CategoryChip(null, uiState.categoryFilter == null) {
                            viewModel.selectCategory(null)
                        }
                        IngredientCategory.values().take(6).forEach { category ->
                            CategoryChip(category, uiState.categoryFilter == category) {
                                viewModel.selectCategory(category)
                            }
                        }
                    }
                }

                val shown = if (uiState.categoryFilter == null) {
                    uiState.ingredients
                } else {
                    uiState.ingredients.filter { it.category == uiState.categoryFilter }
                }
                if (shown.isEmpty()) {
                    item {
                        Text(
                            text = "没有找到食材，试试手动添加吧",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextLight,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                } else {
                    items(shown, key = { it.id }) { ingredient ->
                        IngredientPickRow(
                            ingredient = ingredient,
                            isSelected = ingredient.id in uiState.selectedIds,
                            onClick = { viewModel.toggleIngredient(ingredient) }
                        )
                    }
                }
                // 底部留白，避免食材列表被悬浮按钮遮挡
                item { Spacer(Modifier.height(96.dp)) }
            }
        }

        // ===== 底部智能配餐悬浮按钮（Box悬浮层） =====
        if (uiState.selectedIngredients.isNotEmpty()) {
            BottomPlanBar(
                selectedCount = uiState.selectedIngredients.size,
                onClear = viewModel::clearSelection,
                onGenerate = {
                    onGeneratePlan(MealPlanType.INGREDIENT_BASED, uiState.selectedIds)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }

        // 手动添加对话框
        if (showAddDialog) {
            AddIngredientDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, category ->
                    viewModel.addCustomIngredient(name, category)
                    showAddDialog = false
                }
            )
        }
    }
}

/**
 * 手动添加食材对话框
 */
@Composable
private fun AddIngredientDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, IngredientCategory) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(IngredientCategory.VEGETABLE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("手动添加食材") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("食材名称") },
                    placeholder = { Text("例如：黄瓜") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "选择分类",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextLight
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        IngredientCategory.GRAIN to "🌾谷物",
                        IngredientCategory.VEGETABLE to "🥬蔬菜",
                        IngredientCategory.FRUIT to "🍎水果",
                        IngredientCategory.MEAT to "🥩肉蛋乳"
                    ).forEach { (c, label) ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, category) },
                enabled = name.isNotBlank()
            ) { Text("添加") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

/**
 * 快速添加chips
 */
@Composable
private fun QuickAddChip(
    ingredient: Ingredient,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) SecondaryGreen else MaterialTheme.colorScheme.surface,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/**
 * 分类筛选chip
 */
@Composable
private fun CategoryChip(category: IngredientCategory?, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = category?.label ?: "全部",
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    )
}

/**
 * 食材选择行
 */
@Composable
private fun IngredientPickRow(
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (ingredient.isAllergen) {
                        Text(
                            text = "  过敏源",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    text = ingredient.notes.ifEmpty { "${ingredient.defaultUnit}可食用" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
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
 * 底部智能配餐悬浮栏
 */
@Composable
private fun BottomPlanBar(
    selectedCount: Int,
    onClear: () -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "清空",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = "已选 $selectedCount 种食材",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onGenerate,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Icon(
                    imageVector = Icons.Filled.RestaurantMenu,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("智能配餐", fontWeight = FontWeight.Bold)
            }
        }
    }
}
