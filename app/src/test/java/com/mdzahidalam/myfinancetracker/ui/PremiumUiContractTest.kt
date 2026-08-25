package com.mdzahidalam.myfinancetracker.ui

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PremiumUiContractTest {
    private val sourceRoot = listOf(
        File("src/main/java/com/mdzahidalam/myfinancetracker"),
        File("app/src/main/java/com/mdzahidalam/myfinancetracker")
    ).first { it.exists() }

    @Test fun designSystemDefinesSemanticTokensAndAdaptiveWidths() {
        val source = File(sourceRoot, "core/designsystem/FinanceDesignSystem.kt").readText()
        listOf("LightColors", "DarkColors", "Typography", "FinanceSpacing", "FinanceShapes", "FinanceLayout")
            .forEach { assertTrue("Missing design token: $it", source.contains(it)) }
        assertTrue(source.contains("touchTarget = 48.dp"))
    }

    @Test fun topLevelNavigationAdaptsForWideDisplays() {
        val source = File(sourceRoot, "navigation/FinanceAppNavigation.kt").readText()
        assertTrue(source.contains("screenWidthDp >= 840"))
        assertTrue(source.contains("NavigationRail"))
        assertTrue(source.contains("NavigationBar"))
    }

    @Test fun sharedFormsAndEmptyStatesCarryAccessibilitySemantics() {
        val components = File(sourceRoot, "core/ui/FinanceComponents.kt").readText()
        val dashboard = File(sourceRoot, "feature/dashboard/DashboardScreen.kt").readText()
        assertTrue(components.contains("fun FinanceEmptyState"))
        assertTrue(components.contains("semantics { heading() }"))
        assertTrue(dashboard.contains("AdaptiveSummaryPair"))
        assertTrue(dashboard.contains("semantics { heading() }"))
    }

    @Test fun featureSpacingUsesDesignTokens() {
        val source = sequenceOf(File(sourceRoot, "feature"), File(sourceRoot, "core/ui"))
            .flatMap { it.walkTopDown().filter { file -> file.extension == "kt" } }
            .joinToString("\n") { it.readText() }
        val hardCodedSpacing = Regex("(?:padding|spacedBy)\\([^\\n]*(?:4|6|7|8|10|12|14|16|20|24|28|32)\\.dp")
        assertTrue("Use FinanceSpacing tokens instead of hard-coded layout spacing", !hardCodedSpacing.containsMatchIn(source))
    }

    @Test fun reportsExposeAccessibleSaveFeedback() {
        val components = File(sourceRoot, "core/ui/FinanceComponents.kt").readText()
        val reports = File(sourceRoot, "feature/reports/ReportsScreen.kt").readText()
        assertTrue(components.contains("fun FinanceFeedbackBanner"))
        assertTrue(components.contains("LiveRegionMode.Polite"))
        assertTrue(reports.contains("PDF saved successfully."))
        assertTrue(reports.contains("Excel report saved successfully."))
    }
}
