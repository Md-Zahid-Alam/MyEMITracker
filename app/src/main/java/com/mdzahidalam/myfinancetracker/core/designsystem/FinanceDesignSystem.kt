package com.mdzahidalam.myfinancetracker.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object FinanceDesignSystem {
    val LightColors = lightColorScheme(
        primary = Color(0xFF007F79), onPrimary = Color.White,
        primaryContainer = Color(0xFFD1F4F0), onPrimaryContainer = Color(0xFF003734),
        secondary = Color(0xFF476560), onSecondary = Color.White,
        secondaryContainer = Color(0xFFD2E8E3), onSecondaryContainer = Color(0xFF08201D),
        tertiary = Color(0xFF315F82), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFCDE5FF), onTertiaryContainer = Color(0xFF001D34),
        background = Color(0xFFF7FAF9), onBackground = Color(0xFF171D1C),
        surface = Color(0xFFFFFFFF), onSurface = Color(0xFF171D1C),
        surfaceVariant = Color(0xFFE8EFED), onSurfaceVariant = Color(0xFF3F4947),
        outline = Color(0xFF6F7977), outlineVariant = Color(0xFFBEC9C6),
        error = Color(0xFFBA1A1A), onError = Color.White,
        errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002)
    )
    val DarkColors = darkColorScheme(
        primary = Color(0xFF70DAD3), onPrimary = Color(0xFF003735),
        primaryContainer = Color(0xFF00504E), onPrimaryContainer = Color(0xFFC8F2EE),
        secondary = Color(0xFFB0CCC6), onSecondary = Color(0xFF1B3531),
        secondaryContainer = Color(0xFF314B47), onSecondaryContainer = Color(0xFFCBE8E2),
        tertiary = Color(0xFF9BCBFA), onTertiary = Color(0xFF003354),
        tertiaryContainer = Color(0xFF174B6D), onTertiaryContainer = Color(0xFFCDE5FF),
        background = Color(0xFF0F1514), surface = Color(0xFF171D1C),
        surfaceVariant = Color(0xFF27302E), onSurfaceVariant = Color(0xFFBEC9C6),
        onBackground = Color(0xFFE0E6E4), onSurface = Color(0xFFE0E6E4),
        outline = Color(0xFF899390), outlineVariant = Color(0xFF3F4947),
        error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6)
    )
    val Typography = Typography(
        displaySmall = TextStyle(fontSize = 36.sp, lineHeight = 44.sp, fontWeight = FontWeight.Bold),
        headlineLarge = TextStyle(fontSize = 30.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold),
        headlineMedium = TextStyle(fontSize = 26.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold),
        headlineSmall = TextStyle(fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold),
        titleLarge = TextStyle(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
        titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
        titleSmall = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
        bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal),
        bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp, fontWeight = FontWeight.Normal),
        bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Normal),
        labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
        labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
    )
    val Shapes = Shapes(
        extraSmall = FinanceShapes.small,
        small = FinanceShapes.small,
        medium = FinanceShapes.medium,
        large = FinanceShapes.large,
        extraLarge = FinanceShapes.large
    )
}

object FinanceSpacing {
    val xxs = 4.dp; val xs = 8.dp; val sm = 12.dp; val md = 16.dp
    val lg = 24.dp; val xl = 32.dp; val xxl = 40.dp
    val screen = 20.dp; val section = 24.dp; val touchTarget = 48.dp
}

object FinanceShapes {
    val small = RoundedCornerShape(10.dp)
    val medium = RoundedCornerShape(16.dp)
    val large = RoundedCornerShape(24.dp)
    val pill = RoundedCornerShape(50)
}

object FinanceLayout {
    val phoneContentMax = 720.dp
    val dashboardContentMax = 960.dp
    val formContentMax = 680.dp
}

object FinanceStatusColors {
    val success = Color(0xFF16856F)
    val warning = Color(0xFFB36A00)
    val danger = Color(0xFFBA1A1A)
    val info = Color(0xFF315F82)
}
