package com.mdzahidalam.myfinancetracker.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object FinanceDesignSystem {
    val LightColors = lightColorScheme(
        primary = Color(0xFF007C7A), onPrimary = Color.White,
        primaryContainer = Color(0xFFC8F2EE), onPrimaryContainer = Color(0xFF003735),
        secondary = Color(0xFF256A66), tertiary = Color(0xFF356A5F),
        tertiaryContainer = Color(0xFFD2F2EB), onTertiaryContainer = Color(0xFF123D36)
    )
    val DarkColors = darkColorScheme(
        primary = Color(0xFF70DAD3), onPrimary = Color(0xFF003735),
        primaryContainer = Color(0xFF00504E), onPrimaryContainer = Color(0xFFC8F2EE),
        secondary = Color(0xFF9ACFC9), tertiary = Color(0xFF8FD7CC),
        tertiaryContainer = Color(0xFF174D48), onTertiaryContainer = Color(0xFFD2F2EB),
        background = Color(0xFF101414), surface = Color(0xFF171C1C),
        surfaceVariant = Color(0xFF24302F), onBackground = Color(0xFFE1E7E5),
        onSurface = Color(0xFFE1E7E5)
    )
    val Typography = Typography()
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
    val lg = 24.dp; val xl = 32.dp
    val screen = 16.dp; val section = 20.dp; val touchTarget = 48.dp
}

object FinanceShapes {
    val small = RoundedCornerShape(8.dp)
    val medium = RoundedCornerShape(12.dp)
    val large = RoundedCornerShape(20.dp)
    val pill = RoundedCornerShape(50)
}
