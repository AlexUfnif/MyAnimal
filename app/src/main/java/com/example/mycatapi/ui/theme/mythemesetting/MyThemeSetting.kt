package com.example.mycatapi.ui.theme.mythemesetting

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.mycatapi.ui.theme.Pink40
import com.example.mycatapi.ui.theme.Pink80
import com.example.mycatapi.ui.theme.Purple40
import com.example.mycatapi.ui.theme.Purple80
import com.example.mycatapi.ui.theme.PurpleGrey40
import com.example.mycatapi.ui.theme.PurpleGrey80

private val LightThemeColors = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val DarkThemeColors = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Позволяет переключаться между светлой и темной темой в настройках приложения.
 */
object ThemeSettings {
    var isDarkThemeEnabled by mutableStateOf(false)
}

/**
 * Отвечает за переключение цветовой палитры для темной и светлой темы.
 */
@Composable
fun MyTheme(content: @Composable () -> Unit) {
    ReadThemeState(themeSetting = ThemeSettings)
    val isDarkThemeEnabled = isSystemInDarkTheme() || ThemeSettings.isDarkThemeEnabled
    val colors = if (isDarkThemeEnabled) DarkThemeColors else LightThemeColors

    MaterialTheme(colorScheme = colors, content = content)
}

@Composable
fun ReadThemeState(themeSetting: ThemeSettings){
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("currentTheme", Context.MODE_PRIVATE)
    themeSetting.isDarkThemeEnabled = sharedPreferences.getBoolean("theme", false)
}


