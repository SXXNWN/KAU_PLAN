package com.example.kau_plan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val AppColorScheme = lightColorScheme(
    primary = KauPrimary,
    onPrimary = KauTextOnCard,
    primaryContainer = KauCard,
    onPrimaryContainer = KauTextOnCard,
    secondary = KauGreen,
    onSecondary = Color.White,
    tertiary = KauYellow,
    onTertiary = Color.White,
    background = KauBackground,
    onBackground = Color.Black,
    surface = KauCard,
    onSurface = KauTextOnCard,
    surfaceVariant = Color.White, // 프로필 아이콘 배경
    onSurfaceVariant = KauIconGray, // 프로필 아이콘 색상
    outline = KauSubTextOnCard, // 체크 안된 박스, 구분선
    secondaryContainer = KauButtonBackground, // '수정하기' 버튼 배경
    onSecondaryContainer = KauButtonText      // '수정하기' 버튼 텍스트
)

@Composable
fun KAU_PLANTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// HomeScreen 전용
@Composable
fun KauplanHomeScreenTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 상태 표시줄 색상을 AppColorScheme의 primary 색상으로 설정
            window.statusBarColor = AppColorScheme.primary.toArgb()
            // 라이트 테마이므로 상태 표시줄 아이콘을 어둡게 설정
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = AppColorScheme, // 새로 정의한 AppColorScheme을 사용
        typography = Typography,
        content = content
    )
}