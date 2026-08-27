package com.mdzahidalam.myfinancetracker.ui

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class BanglaVisualQaContractTest {
    private val sourceRoot = File("src/main/java/com/mdzahidalam/myfinancetracker")

    @Test
    fun screenshotVisibleEnglishHasBanglaCoverage() {
        val source = File(sourceRoot, "core/legacy/LegacyFoundation.kt").readText()
        listOf(
            "A clear view of your money today.",
            "SPENT THIS MONTH",
            "Commitments",
            "Manage instalments, loans and personal balances.",
            "Financing details",
            "Supporting documents (optional)",
            "Filtered Summary",
            "Security and local data",
            "Documents and app"
        ).forEach { english ->
            assertTrue(source.contains("\"$english\" to \""), "Missing Bangla mapping for: $english")
        }
    }

    @Test
    fun dynamicMoneyAndRecordSummariesUseBanglaTemplates() {
        val source = File(sourceRoot, "core/legacy/LegacyFoundation.kt").readText()
        listOf(
            "matching records •",
            "Today\\\\s+",
            "Money I Owe •",
            "Money Owed to Me •",
            "Next payment:",
            "You owe",
            "owes you"
        ).forEach { template ->
            assertTrue(source.contains(template), "Missing dynamic Bangla template: $template")
        }
    }

    @Test
    fun normalPhoneUsesCompactDashboardWhileLargeTextStillStacks() {
        val source = File(sourceRoot, "feature/dashboard/DashboardScreen.kt").readText()
        assertTrue(source.contains("maxWidth < 340.dp || largeText"))
    }
}
