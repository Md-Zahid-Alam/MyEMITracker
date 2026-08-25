package com.mdzahidalam.myfinancetracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import com.mdzahidalam.myfinancetracker.core.designsystem.FinanceDesignSystem
import com.mdzahidalam.myfinancetracker.data.security.SecurityStore
import com.mdzahidalam.myfinancetracker.feature.authentication.LockScreen
import com.mdzahidalam.myfinancetracker.feature.authentication.SetupScreen
import com.mdzahidalam.myfinancetracker.navigation.FinanceApp

// ============================================================
// MAIN ACTIVITY / APP PASSWORD
// ============================================================

class MainActivity : ComponentActivity() {

    private lateinit var security: SecurityStore

    private var unlocked = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        security =
            SecurityStore(this)

        showContent()
    }

    override fun onStop() {

        super.onStop()

        if (!isChangingConfigurations) {
            unlocked = false
        }
    }

    private fun showContent() {

        setContent {
            val preferences = remember {
                getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            }
            var themeMode by remember {
                mutableStateOf(preferences.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM")
            }
            var language by remember { mutableStateOf(preferences.getString(KEY_LANGUAGE, "EN") ?: "EN") }
            var country by remember { mutableStateOf(preferences.getString(KEY_COUNTRY, "Bangladesh") ?: "Bangladesh") }
            var currencyCode by remember { mutableStateOf(preferences.getString(KEY_CURRENCY_CODE, "BDT") ?: "BDT") }
            var currencySymbol by remember { mutableStateOf(preferences.getString(KEY_CURRENCY_SYMBOL, "৳") ?: "৳") }
            AppLocaleState.language = language; AppLocaleState.country = country; AppLocaleState.currencyCode = currencyCode; AppLocaleState.currencySymbol = currencySymbol
            AppLocaleState.customBanks = preferences.getStringSet(KEY_CUSTOM_BANKS, emptySet())?.sorted() ?: emptyList()
            AppLocaleState.customProviders = preferences.getStringSet(KEY_CUSTOM_PROVIDERS, emptySet())?.sorted() ?: emptyList()
            val useDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            CompositionLocalProvider(LocalAppLanguage provides language) { MaterialTheme(
                colorScheme = if (useDarkTheme) AppDarkColorScheme else AppLightColorScheme,
                typography = FinanceDesignSystem.Typography,
                shapes = FinanceDesignSystem.Shapes
            ) {

            if (!security.hasPassword()) {

                SetupScreen(language = language, onLanguageChange = { selected -> language = selected; preferences.edit().putString(KEY_LANGUAGE, selected).apply() }) { password, selectedCountry, code, symbol ->

                    security.setPassword(password)
                    country = selectedCountry; currencyCode = code; currencySymbol = symbol
                    preferences.edit().putString(KEY_COUNTRY, selectedCountry).putString(KEY_CURRENCY_CODE, code).putString(KEY_CURRENCY_SYMBOL, symbol).apply()

                    unlocked = true

                    showContent()
                }

            } else if (!unlocked) {

                LockScreen(language = language, onLanguageChange = { selected -> language = selected; preferences.edit().putString(KEY_LANGUAGE, selected).apply() }) { password ->

                    if (security.verify(password)) {

                        unlocked = true

                        showContent()
                        true
                    } else {
                        false
                    }
                }

            } else {

                FinanceApp(
                    onLogout = {
                        unlocked = false
                        showContent()
                    },
                    onPasswordChange = { password ->
                        security.setPassword(password)
                    },
                    verifyPassword = { password -> security.verify(password) },
                    themeMode = themeMode,
                    onThemeChange = { selectedMode ->
                        themeMode = selectedMode
                        preferences.edit().putString(KEY_THEME_MODE, selectedMode).apply()
                    },
                    language = language,
                    onLanguageChange = { selected -> language = selected; preferences.edit().putString(KEY_LANGUAGE, selected).apply() },
                    country = country,
                    currencyCode = currencyCode,
                    currencySymbol = currencySymbol,
                    onRegionChange = { selectedCountry, code, symbol -> country = selectedCountry; currencyCode = code; currencySymbol = symbol; preferences.edit().putString(KEY_COUNTRY, selectedCountry).putString(KEY_CURRENCY_CODE, code).putString(KEY_CURRENCY_SYMBOL, symbol).apply() },
                    onCustomPaymentListsChange = { banks, providers -> preferences.edit().putStringSet(KEY_CUSTOM_BANKS, banks.toSet()).putStringSet(KEY_CUSTOM_PROVIDERS, providers.toSet()).apply(); AppLocaleState.customBanks = banks; AppLocaleState.customProviders = providers }
                )
            } }
            }
        }
    }
}
