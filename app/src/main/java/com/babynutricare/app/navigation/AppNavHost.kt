package com.babynutricare.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.babynutricare.app.ui.baby.BabyInfoEditScreen
import com.babynutricare.app.ui.home.HomeScreen
import com.babynutricare.app.ui.mealplan.MealPlanResultScreen
import com.babynutricare.app.ui.mealplan.MealPlanType
import com.babynutricare.app.ui.meals.MealsScreen
import com.babynutricare.app.ui.recipe.RecipeScreen
import com.babynutricare.app.ui.theme.PrimaryOrange

/**
 * 页面路由定义
 */
object Routes {
    // 一级 Tab
    const val HOME_TAB = "home_tab"
    const val RECIPE_TAB = "recipe_tab"
    const val MEALS_TAB = "meals_tab"

    // 二级页面
    const val BABY_EDIT = "baby_edit"
    const val MEAL_PLAN_RESULT = "meal_plan_result"

    // 参数名
    const val ARG_PLAN_TYPE = "planType"
    const val ARG_SELECTED_IDS = "selectedIds"
}

/**
 * 底部Tab项定义
 */
data class BottomTabItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomTabItems = listOf(
    BottomTabItem(Routes.HOME_TAB, "首页", Icons.Filled.Home),
    BottomTabItem(Routes.RECIPE_TAB, "食谱", Icons.Filled.RestaurantMenu),
    BottomTabItem(Routes.MEALS_TAB, "三餐", Icons.Filled.CalendarMonth)
)

/**
 * 应用主界面：底部3Tab导航 + 二级页面
 */
@Composable
fun AppNavHost(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // 一级Tab页面显示底部导航栏
    val showBottomBar = bottomTabItems.any { it.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomTabBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_TAB,
            modifier = Modifier.padding(padding)
        ) {
            // ===== 首页 =====
            composable(Routes.HOME_TAB) {
                HomeScreen(
                    onEditBaby = { navController.navigate(Routes.BABY_EDIT) },
                    onViewPlan = { planType, selectedIds ->
                        navigateToPlan(navController, planType, selectedIds)
                    }
                )
            }

            // ===== 食谱 =====
            composable(Routes.RECIPE_TAB) {
                RecipeScreen(
                    onGeneratePlan = { planType, selectedIds ->
                        navigateToPlan(navController, planType, selectedIds)
                    }
                )
            }

            // ===== 三餐 =====
            composable(Routes.MEALS_TAB) {
                MealsScreen(
                    onGeneratePlan = { planType, selectedIds ->
                        navigateToPlan(navController, planType, selectedIds)
                    }
                )
            }

            // ===== 宝宝信息设置（二级页） =====
            composable(Routes.BABY_EDIT) {
                BabyInfoEditScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ===== 配餐结果（二级页） =====
            composable(
                "${Routes.MEAL_PLAN_RESULT}" +
                    "?${Routes.ARG_PLAN_TYPE}={${Routes.ARG_PLAN_TYPE}}" +
                    "&${Routes.ARG_SELECTED_IDS}={${Routes.ARG_SELECTED_IDS}}"
            ) { backStackEntry ->
                val planTypeName = backStackEntry.arguments
                    ?.getString(Routes.ARG_PLAN_TYPE)
                    ?.let { runCatching { MealPlanType.valueOf(it) }.getOrNull() }
                    ?: MealPlanType.INGREDIENT_BASED
                MealPlanResultScreen(
                    planType = planTypeName,
                    onBack = { navController.popBackStack() }
                )
            }

        }
    }
}

/**
 * 跳转到配餐结果页
 */
private fun navigateToPlan(
    navController: NavHostController,
    planType: MealPlanType,
    selectedIds: Set<Long>
) {
    val idsParam = selectedIds.joinToString(",")
    navController.navigate(
        "${Routes.MEAL_PLAN_RESULT}" +
            "?${Routes.ARG_PLAN_TYPE}=${planType.name}" +
            "&${Routes.ARG_SELECTED_IDS}=$idsParam"
    )
}

/**
 * 底部Tab栏
 */
@Composable
private fun BottomTabBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        bottomTabItems.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab.route) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = PrimaryOrange,
                    indicatorColor = PrimaryOrange,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}