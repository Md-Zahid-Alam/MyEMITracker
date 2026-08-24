package com.mdzahidalam.myfinancetracker.architecture

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ArchitectureBoundariesTest {
    private val sourceRoot = listOf(
        File("src/main/java/com/mdzahidalam/myfinancetracker"),
        File("app/src/main/java/com/mdzahidalam/myfinancetracker")
    ).first { it.exists() }

    @Test fun mainActivityOnlyHostsTheApplication() {
        val activity = File(sourceRoot, "MainActivity.kt")
        assertTrue(activity.exists())
        assertTrue("MainActivity must stay below 150 lines", activity.readLines().size < 150)
        val source = activity.readText()
        assertFalse(source.contains("JSONObject"))
        assertFalse(source.contains("PdfDocument"))
        assertFalse(source.contains("class FinanceRepository"))
        assertFalse(source.contains("fun EmiForm"))
    }

    @Test fun featureAndInfrastructureBoundariesExist() {
        listOf(
            "feature/authentication/SetupScreen.kt",
            "feature/dashboard/DashboardScreen.kt",
            "feature/payments/PaymentsScreens.kt",
            "feature/expenses/ExpenseListScreen.kt",
            "feature/reports/ReportsScreen.kt",
            "data/legacy/SecurityLayer.kt",
            "data/legacy/FinanceRepository.kt",
            "data/legacy/ReminderSystem.kt",
            "presentation/FinanceViewModel.kt",
            "navigation/FinanceAppNavigation.kt"
        ).forEach { relative -> assertTrue("Missing boundary: $relative", File(sourceRoot, relative).exists()) }
    }
}
