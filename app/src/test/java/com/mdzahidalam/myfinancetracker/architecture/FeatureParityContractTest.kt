package com.mdzahidalam.myfinancetracker.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FeatureParityContractTest {
    private val sourceRoot = listOf(
        File("src/main/java/com/mdzahidalam/myfinancetracker"),
        File("app/src/main/java/com/mdzahidalam/myfinancetracker")
    ).first { it.exists() }
    private val allSource by lazy { sourceRoot.walkTopDown().filter { it.extension == "kt" }.joinToString("\n") { it.readText() } }

    @Test fun criticalWorkflowsRemainPresent() {
        listOf(
            "fun SetupScreen", "fun LockScreen", "fun Dashboard", "fun EmiList", "fun LoanList",
            "fun DebtList", "fun ExpenseList", "fun EmiForm", "fun LoanForm", "fun DebtForm",
            "fun ExpenseForm", "fun PaymentHistory", "fun PaymentRequestDialog", "fun Reports",
            "fun CountrySettingsScreen", "fun ReceiptProfileForm", "class SecurityStore",
            "class FinanceRepository", "object ReminderScheduler", "fun writePdfToUri", "fun writeXlsxToUri"
        ).forEach { signature -> assertTrue("Missing feature contract: $signature", allSource.contains(signature)) }
    }

    @Test fun storageAndBackupCompatibilityKeysRemainPresent() {
        listOf(
            "finance_tracker_v3", "data_encrypted_v1", "MFT_ENCRYPTED_BACKUP",
            "paymentRequests", "receiptProfile", "attachments", "financingChannel"
        ).forEach { key -> assertTrue("Missing compatibility key: $key", allSource.contains(key)) }
    }
}
