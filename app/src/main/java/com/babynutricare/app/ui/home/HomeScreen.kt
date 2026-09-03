package com.babynutricare.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
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
import com.babynutricare.core.domain.model.MealSlot
import com.babynutricare.core.domain.model.PlannedMeal

/**
 * 首页Tab - 宝宝信息 + 今日食谱推荐
 */
@Composable
fun HomeScreen(
    onEditBaby: () -> Unit,
    onViewPlan: (MealPlanType, Set<Long>) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val baby = uiState.baby

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ===== 顶部横幅 =====
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(PrimaryOrange, Color(0xFFFFB37E)))
                    )
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Column {
                    Text(
                        text = if (baby != null) "你好，${baby.name}家长 👋" else "欢迎使用 🍼",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "科学配餐 · 均衡营养 · 陪伴宝宝健康成长",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // ===== 宝宝信息卡 =====
        item {
            if (baby == null) {
                BabySetupCard(onEditBaby = onEditBaby)
            } else {
                BabyInfoCard(
                    babyName = baby.name,
                    genderEmoji = if (baby.gender == 0) "👧" else "👦",
                    monthAge = uiState.monthAge,
                    stageName = uiState.stageName,
                    stageDescription = uiState.stageDescription,
                    onEditBaby = onEditBaby
                )
            }
        }

        // ===== 今日食谱推荐 =====
        item {
            Text(
                text = "🍼 今日食谱推荐",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        val plan = uiState.todayPlan
        if (baby == null || plan == null) {
            item {
                EmptyPlanHint(
                    hasBaby = baby != null,
                    onGoRecipe = { onViewPlan(MealPlanType.INGREDIENT_BASED, emptySet()) }
                )
            }
        } else {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable {
                            onViewPlan(MealPlanType.INGREDIENT_BASED, emptySet())
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        plan.meals.forEachIndexed { index, meal ->
                            if (index > 0) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                )
                            }
                            MealRow(meal = meal)
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = { onViewPlan(MealPlanType.INGREDIENT_BASED, emptySet()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text("查看完整方案", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 宝宝信息未设置提示卡
 */
@Composable
private fun BabySetupCard(onEditBaby: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onEditBaby),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryOrange.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🍼", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("设置宝宝信息", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = PrimaryOrange)
                Text("填写昵称与出生日期，开启专属营养配餐",
                    style = MaterialTheme.typography.bodyMedium, color = TextLight)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = "设置", tint = PrimaryOrange)
        }
    }
}

/**
 * 宝宝信息卡
 */
@Composable
private fun BabyInfoCard(
    babyName: String,
    genderEmoji: String,
    monthAge: Int,
    stageName: String,
    stageDescription: String,
    onEditBaby: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onEditBaby),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(PrimaryOrange.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(genderEmoji, style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(babyName, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("  ·  $monthAge 个月", style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryGreen, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(2.dp))
                Text("辅食阶段：$stageName", style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryOrange, fontWeight = FontWeight.Medium)
                Text(stageDescription, style = MaterialTheme.typography.bodyMedium,
                    color = TextLight)
            }
            Icon(Icons.Filled.Edit, contentDescription = "编辑", tint = TextLight,
                modifier = Modifier.size(20.dp))
        }
    }
}

/**
 * 空态推荐提示
 */
@Composable
private fun EmptyPlanHint(
    hasBaby: Boolean,
    onGoRecipe: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🥣", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (hasBaby) {
                    "推荐方案准备中，去食谱页选点食材试试吧"
                } else {
                    "设置宝宝信息后，即可生成专属的今日食谱"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextLight
            )
        }
    }
}

/**
 * 单餐推荐行
 */
@Composable
private fun MealRow(meal: PlannedMeal) {
    val slotEmoji = when (meal.mealSlot) {
        MealSlot.BREAKFAST -> "🌅"
        MealSlot.LUNCH -> "☀️"
        MealSlot.DINNER -> "🌙"
        MealSlot.SNACK -> "🍪"
    }
    val slotName = when (meal.mealSlot) {
        MealSlot.BREAKFAST -> "早餐"
        MealSlot.LUNCH -> "午餐"
        MealSlot.DINNER -> "晚餐"
        MealSlot.SNACK -> "加餐"
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(slotEmoji, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(slotName, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Text(
                text = meal.ingredients.joinToString("、") { portion ->
                    "${portion.ingredientName} ${portion.amount.toInt()}${portion.unit}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextLight
            )
        }
    }
}