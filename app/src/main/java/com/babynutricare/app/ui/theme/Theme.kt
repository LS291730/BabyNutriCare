package com.babynutricare.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 品牌主色 - 温暖橙（营养、健康、亲和）
val PrimaryOrange = Color(0xFFFF8A5C)
val PrimaryOrangeDark = Color(0xFFE06B3D)
val SecondaryGreen = Color(0xFF7BC47F)
val SecondaryGreenDark = Color(0xFF4E9A53)
val AccentYellow = Color(0xFFFFD166)
val BackgroundCream = Color(0xFFFFFBF5)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF3A3A3A)
val TextLight = Color(0xFF8A8A8A)
val WarningRed = Color(0xFFE05252)
val SuccessGreen = Color(0xFF4CAF50)

// 营养素主题色
val ProteinColor = Color(0xFFFF6B6B)
val FatColor = Color(0xFF4ECDC4)
val CarbColor = Color(0xFF45B7D1)
val CalciumColor = Color(0xFF96CEB4)
val IronColor = Color(0xFFF2A65A)
val ZincColor = Color(0xFFDDA0DD)
val VitaminAColor = Color(0xFF98D8C8)
val VitaminCColor = Color(0xFFF7DC6F)
val VitaminDColor = Color(0xFFBB8FCE)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    primaryContainer = AccentYellow,
    onPrimaryContainer = TextDark,
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    tertiary = AccentYellow,
    onTertiary = TextDark,
    background = BackgroundCream,
    onBackground = TextDark,
    surface = SurfaceWhite,
    onSurface = TextDark,
    error = WarningRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    tertiary = AccentYellow,
    background = Color(0xFF1C1C1E),
    onBackground = Color(0xFFF2F2F2),
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xFFF2F2F2),
    error = WarningRed,
    onError = Color.White
)

@Composable
fun BabyNutriCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}