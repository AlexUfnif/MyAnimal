package com.example.mycatapi

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import com.example.mycatapi.network.BaseURL
import com.example.mycatapi.ui.theme.mythemesetting.ThemeSettings

@Composable
fun SavedSettings(vm: MainViewModel, themeSetting: ThemeSettings, saved: MutableState<Boolean>) {
    // Сохраняем переменную score
    if (saved.value) {
        val context = LocalContext.current
        val sharedPreferences = context.getSharedPreferences("currentTheme", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("baseURL", vm.baseUrl).apply()
        sharedPreferences.edit().putBoolean("theme", themeSetting.isDarkThemeEnabled).apply()

    }
    saved.value = false
}

@Composable
fun ReadSettings(baseUrl: MutableState<String>, restore: MutableState<Boolean>) {
    if (restore.value) {
        val context = LocalContext.current
        val sharedPreferences = context.getSharedPreferences("currentTheme", Context.MODE_PRIVATE)
        baseUrl.value = sharedPreferences.getString("baseURL", BaseURL.BASE_URL_CAT).toString()
    }
    restore.value = false
}

@Composable
fun SaveThemeState(themeSetting: ThemeSettings) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("currentTheme", Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean("theme", themeSetting.isDarkThemeEnabled).apply()
}

@Composable
fun ReadThemeState(themeSetting: ThemeSettings) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("currentTheme", Context.MODE_PRIVATE)
    themeSetting.isDarkThemeEnabled = sharedPreferences.getBoolean("theme", false)
}
