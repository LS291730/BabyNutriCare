package com.babynutricare.app.ui.home

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babynutricare.app.ui.theme.BackgroundCream
import com.babynutricare.app.ui.theme.PrimaryOrange
import com.babynutricare.app.ui.theme.SecondaryGreen
import com.babynutricare.app.ui.theme.TextLight

/**
 * 首页
 */
@Composable
fun HomeScreen(
    onNavigateToIngredientSelect: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BackgroundCream,
        topBar = {
            HomeTopBar(
                babyName = uiState.baby?.name ?: "宝宝",
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 宝宝信息卡片
            item {
                BabyInfoCard(
                    monthAge = uiState.monthAge,
                    stageName = uiState.stageName,
                    stageDescription = uiState.stageDescription,
                    isSetup = uiState.baby != null
                )
            }

            // 核心功能：智能配餐
            item {
                Text(
                    text = "智能配餐",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                FeatureCard(
                    title = "现有食材配餐",
                    description = "选择家里已有的食材，智能组合出均衡宝宝餐",
                    icon = Icons.Filled.Restaurant,
                    color = PrimaryOrange,
                    onClick = onNavigateToIngredientSelect
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FeatureCard(
                        title = "周维度续配餐",
                        description = "分析本周已吃饮食，推荐剩余天数搭配",
                        icon = Icons.Filled.CalendarMonth,
                        color = SecondaryGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { /* 导航到周维度配餐 */ }
                    )
                    FeatureCard(
                        title = "日维度配餐",
                        description = "记录早餐，精准推荐午餐晚餐",
                        icon = Icons.Filled.Star,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                        onClick = { /* 导航到日维度配餐 */ }
                    )
                }
            }

            // 其他功能
            item {
                Text(
                    text = "其他功能",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SmallFeatureCard(
                        title = "饮食日历",
                        icon = Icons.Filled.CalendarMonth,
                        modifier = Modifier.weight(1f),
                        onClick = { /* 导航到饮食日历 */ }
                    )
                    SmallFeatureCard(
                        title = "食材库",
                        icon = Icons.Filled.Favorite,
                        modifier = Modifier.weight(1f),
                        onClick = { /* 导航到食材库 */ }
                    )
                    SmallFeatureCard(
                        title = "辅食指南",
                        icon = Icons.Filled.MenuBook,
                        modifier = Modifier.weight(1f),
                        onClick = { /* 导航到知识库 */ }
                    )
                }
            }
        }
    }
}

/**
 * 顶部栏
 */
@Composable
private fun HomeTopBar(
    babyName: String,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryOrange)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ChildCare,
                contentDescription = "宝宝",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "你好，${babyName}家长",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "今天也要好好吃饭哦",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "设置",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * 宝宝信息卡片
 */
@Composable
private fun BabyInfoCard(
    monthAge: Int,
    stageName: String,
    stageDescription: String,
    isSetup: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = if (isSetup) "$monthAge 个月" else "还未设置宝宝信息",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isSetup) {
                Text(
                    text = "辅食阶段：$stageName",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stageDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            } else {
                Text(
                    text = "请先设置宝宝信息，获取精准的配餐方案",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }
    }
}

/**
 * 功能卡片
 */
@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLight
                )
            }
        }
    }
}

/**
 * 小功能卡片
 */
@Composable
private fun SmallFeatureCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryOrange,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
