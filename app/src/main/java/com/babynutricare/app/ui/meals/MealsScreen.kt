package com.babynutricare.app.ui.meals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
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
import com.babynutricare.app.ui.theme.WarningRed
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 三餐Tab - 日/周维度配餐记录与营养分析
 */
@Composable
fun MealsScreen(
    onGeneratePlan: (MealPlanType, Set<Long>) -> Unit,
    viewModel: MealsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val today = LocalDate.now()
    val fmt = DateTimeFormatter.ofPattern("MM.dd")
    val dayCn = listOf("日", "一", "二", "三", "四", "五", "六")

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF7BC47F), Color(0xFF45B7A0))))
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text("🍽️ 宝宝三餐", style = MaterialTheme.typography.headlineMedium,
                    color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("${today.monthValue}月${today.dayOfMonth}日 · 记录每一餐，营养更均衡",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f))
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!uiState.hasBaby) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = WarningRed.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)) {
                        Text("⚠ 请先在首页设置宝宝信息，才能查看精准的营养分析",
                            style = MaterialTheme.typography.bodyMedium, color = WarningRed,
                            modifier = Modifier.padding(12.dp))
                    }
                }
            }

            item { SectionTitle("📅 本周饮食记录") }
            item {
                Card(shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        uiState.weekDays.forEach { day ->
                            WeekDayCell(
                                dayLabel = "周${dayCn[day.date.dayOfWeek.value % 7]}",
                                dateText = day.date.format(fmt),
                                mealCount = day.mealCount,
                                isToday = day.date == today,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item { SectionTitle("🤖 智能配餐") }
            item {
                PlanActionRow(
                    onDaily = { onGeneratePlan(MealPlanType.DAILY, emptySet()) },
                    onWeekly = { onGeneratePlan(MealPlanType.WEEKLY, emptySet()) }
                )
            }

            item { SectionTitle("📊 营养分析") }
            item {
                NutritionAnalysisCard(
                    achievement = uiState.achievement,
                    advice = uiState.advice
                )
            }
        }
    }
}

/**
 * 区块标题
 */
@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
}

/**
 * 单日状态格
 */
@Composable
private fun WeekDayCell(
    dayLabel: String,
    dateText: String,
    mealCount: Int,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = if (isToday) PrimaryOrange.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = dayLabel, style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) PrimaryOrange else TextLight)
        Spacer(Modifier.height(4.dp))
        Text(text = dateText, style = MaterialTheme.typography.labelMedium,
            color = if (isToday) PrimaryOrange else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (index < mealCount) PrimaryOrange
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

/**
 * 配餐操作按钮行
 */
@Composable
private fun PlanActionRow(
    onDaily: () -> Unit,
    onWeekly: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PlanActionCard(
            emoji = "🌅", title = "日维度配餐",
            desc = "记录早餐，推荐午晚餐",
            modifier = Modifier.weight(1f), onClick = onDaily
        )
        PlanActionCard(
            emoji = "📆", title = "周维度续配餐",
            desc = "记录本周，智能续配剩余天",
            modifier = Modifier.weight(1f), onClick = onWeekly
        )
    }
}

/**
 * 配餐操作卡片
 */
@Composable
private fun PlanActionCard(
    emoji: String,
    title: String,
    desc: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(desc, style = MaterialTheme.typography.labelMedium, color = TextLight)
        }
    }
}

/**
 * 营养分析卡片
 */
@Composable
private fun NutritionAnalysisCard(
    achievement: Map<String, Float>,
    advice: List<String>
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (achievement.isEmpty()) {
                Text("还没有饮食记录，先去记录宝宝三餐吧",
                    style = MaterialTheme.typography.bodyMedium, color = TextLight)
            } else {
                listOf(
                    "蛋白质" to (achievement["protein"] ?: 0f),
                    "钙" to (achievement["calcium"] ?: 0f),
                    "铁" to (achievement["iron"] ?: 0f),
                    "热量" to (achievement["calorie"] ?: 0f)
                ).forEach { (name, rate) ->
                    Row(modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(name, style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(56.dp))
                        LinearProgressIndicator(
                            progress = { (rate / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.weight(1f).height(8.dp),
                            color = if (rate >= 80f) SecondaryGreen else WarningRed,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("${rate.toInt()}%",
                            style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold,
                            color = if (rate >= 80f) SecondaryGreen else WarningRed,
                            modifier = Modifier.width(42.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            advice.forEach { text ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Icon(Icons.Filled.Lightbulb, contentDescription = null,
                        tint = SecondaryGreen, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(text, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                }
            }
        }
    }
}