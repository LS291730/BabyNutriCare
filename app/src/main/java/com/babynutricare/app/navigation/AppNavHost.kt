package com.babynutricare.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.babynutricare.app.ui.home.HomeScreen
import com.babynutricare.app.ui.mealplan.IngredientSelectScreen
import com.babynutricare.app.ui.mealplan.MealPlanResultScreen
import com.babynutricare.app.ui.mealplan.MealPlanType
import com.babynutricare.app.ui.settings.SettingsScreen

/**
 * 页面路由定义
 */
object Routes {
    const val HOME = "home"
    const val INGREDIENT_SELECT = "ingredient_select"
    const val MEAL_PLAN_RESULT = "meal_plan_result"
    const val SETTINGS = "settings"

    // 参数名
    const val ARG_PLAN_TYPE = "planType"
    const val ARG_SELECTED_IDS = "selectedIds"
    const val ARG_RECORD_DATE = "recordDate"
    const val ARG_BABY_ID = "babyId"
}

/**
 * 应用导航图
 */
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToIngredientSelect = {
                    navController.navigate(Routes.INGREDIENT_SELECT)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.INGREDIENT_SELECT) {
            IngredientSelectScreen(
                onBack = { navController.popBackStack() },
                onGeneratePlan = { planType, selectedIds ->
                    val idsParam = selectedIds.joinToString(",")
                    navController.navigate(
                        "${Routes.MEAL_PLAN_RESULT}" +
                            "?${Routes.ARG_PLAN_TYPE}=${planType.name}" +
                            "&${Routes.ARG_SELECTED_IDS}=$idsParam"
                    )
                }
            )
        }

        composable(
            "${Routes.MEAL_PLAN_RESULT}" +
                "?${Routes.ARG_PLAN_TYPE}={${Routes.ARG_PLAN_TYPE}}" +
                "&${Routes.ARG_SELECTED_IDS}={${Routes.ARG_SELECTED_IDS}}"
        ) { backStackEntry ->
            val planTypeName = backStackEntry.arguments
                ?.getString(Routes.ARG_PLAN_TYPE)
                ?.let { MealPlanType.valueOf(it) }
                ?: MealPlanType.INGREDIENT_BASED
            MealPlanResultScreen(
                planType = planTypeName,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}