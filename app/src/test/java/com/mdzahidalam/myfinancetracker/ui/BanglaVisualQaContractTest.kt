package com.mdzahidalam.myfinancetracker.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class BanglaVisualQaContractTest {
    private val sourceRoot = listOf(
        File("src/main/java/com/mdzahidalam/myfinancetracker"),
        File("app/src/main/java/com/mdzahidalam/myfinancetracker")
    ).first { it.exists() }

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
            assertTrue("Missing Bangla mapping for: $english", source.contains("\"$english\" to \""))
        }
    }

    @Test
    fun dynamicMoneyAndRecordSummariesUseBanglaTemplates() {
        val source = File(sourceRoot, "core/legacy/LegacyFoundation.kt").readText()
        listOf(
            "matching records •",
            "Regex(\"^Today",
            "Money I Owe •",
            "Money Owed to Me •",
            "Next payment:",
            "You owe",
            "owes you"
        ).forEach { template ->
            assertTrue("Missing dynamic Bangla template: $template", source.contains(template))
        }
    }

    @Test
    fun normalPhoneUsesCompactDashboardWhileLargeTextStillStacks() {
        val source = File(sourceRoot, "feature/dashboard/DashboardScreen.kt").readText()
        assertTrue(source.contains("maxWidth < 320.dp || largeText"))
        assertTrue(source.contains("fontScale >= 1.6f"))
    }
}
