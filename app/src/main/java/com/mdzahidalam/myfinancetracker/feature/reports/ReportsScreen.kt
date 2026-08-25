package com.mdzahidalam.myfinancetracker.feature.reports
import com.mdzahidalam.myfinancetracker.*
import com.mdzahidalam.myfinancetracker.core.ui.*
import com.mdzahidalam.myfinancetracker.presentation.*
import com.mdzahidalam.myfinancetracker.data.repository.*
import com.mdzahidalam.myfinancetracker.data.security.*
import com.mdzahidalam.myfinancetracker.data.notifications.*
import com.mdzahidalam.myfinancetracker.feature.authentication.*
import com.mdzahidalam.myfinancetracker.feature.dashboard.*
import com.mdzahidalam.myfinancetracker.feature.expenses.*
import com.mdzahidalam.myfinancetracker.feature.payments.*
import com.mdzahidalam.myfinancetracker.feature.reports.*


import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.OpenableColumns
import android.graphics.Paint
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.KeyStore
import java.security.SecureRandom
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.mdzahidalam.myfinancetracker.domain.model.*
import com.mdzahidalam.myfinancetracker.domain.usecase.FinanceCalculations
import com.mdzahidalam.myfinancetracker.core.designsystem.FinanceDesignSystem
import com.mdzahidalam.myfinancetracker.core.designsystem.FinanceLayout
import com.mdzahidalam.myfinancetracker.core.designsystem.FinanceShapes
import com.mdzahidalam.myfinancetracker.core.designsystem.FinanceSpacing
import com.mdzahidalam.myfinancetracker.core.designsystem.FinanceStatusColors
import com.mdzahidalam.myfinancetracker.domain.repository.FinanceDataRepository
import com.mdzahidalam.myfinancetracker.data.backup.BackupPolicy
import com.mdzahidalam.myfinancetracker.export.xlsx.OoxmlRules
import com.mdzahidalam.myfinancetracker.export.model.LegacyReportAdapter
import com.mdzahidalam.myfinancetracker.export.model.ReportBlock
import com.mdzahidalam.myfinancetracker.core.country.CountryCatalog
import com.mdzahidalam.myfinancetracker.core.ui.SearchableCountryPicker
import java.io.File
import javax.crypto.SecretKeyFactory
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.max

// ============================================================
// REPORTS
// ============================================================

internal fun LazyListScope.reportResultSection(
    title: String,
    lines: List<String>,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    if (lines.isEmpty()) return
    item(key = "section-$title") {
        OutlinedButton(onClick = onToggle, modifier = Modifier.fillMaxWidth()) {
            Text("${if (expanded) "−" else "+"} $title (${lines.size})")
        }
    }
    if (expanded) {
        items(lines) { line ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = FinanceShapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) { Text(line, Modifier.padding(FinanceSpacing.sm)) }
        }
    }
}

@Composable
fun Reports(
    viewModel: FinanceViewModel
) {
    val context = LocalContext.current
    var pendingReport by remember { mutableStateOf("") }
    var pendingExcel by remember { mutableStateOf<FinanceData?>(null) }
    var filtersVisible by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var reportType by remember { mutableStateOf("Overview") }
    var period by remember { mutableStateOf("This month") }
    var status by remember { mutableStateOf("All statuses") }
    var sort by remember { mutableStateOf("Newest first") }
    var startDate by remember { mutableStateOf(expenseDateText(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis)) }
    var endDate by remember { mutableStateOf(expenseDateText(System.currentTimeMillis())) }
    var expanded by remember { mutableStateOf(setOf<String>()) }
    var exportFeedback by remember { mutableStateOf("") }
    var exportFailed by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null && pendingReport.isNotEmpty()) {
            runCatching { writePdfToUri(context, uri, pendingReport) }
                .onSuccess { exportFeedback = "PDF saved successfully."; exportFailed = false }
                .onFailure { exportFeedback = it.message ?: "PDF could not be saved."; exportFailed = true }
        }
        pendingReport = ""
    }
    val excelLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { uri ->
        if (uri != null && pendingExcel != null) {
            runCatching { writeXlsxToUri(context, uri, pendingExcel!!, period, reportType) }
                .onSuccess { exportFeedback = "Excel report saved successfully."; exportFailed = false }
                .onFailure { exportFeedback = it.message ?: "Excel report could not be saved."; exportFailed = true }
        }
        pendingExcel = null
    }
    fun inPeriod(value: Long): Boolean {
        val now = Calendar.getInstance()
        val record = Calendar.getInstance().apply { timeInMillis = value }
        return when (period) {
            "This month" -> now.get(Calendar.YEAR) == record.get(Calendar.YEAR) && now.get(Calendar.MONTH) == record.get(Calendar.MONTH)
            "Last month" -> Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.let { it.get(Calendar.YEAR) == record.get(Calendar.YEAR) && it.get(Calendar.MONTH) == record.get(Calendar.MONTH) }
            "Custom range" -> value in (parseExpenseDate(startDate) ?: 0L)..((parseExpenseDate(endDate) ?: Long.MAX_VALUE) + 86_399_999L)
            else -> true
        }
    }
    fun statusMatches(archived: Boolean, completed: Boolean): Boolean = when (status) {
        "Active" -> !archived && !completed
        "Completed" -> !archived && completed
        "Archived" -> archived
        "Paid" -> completed
        "Pending" -> !archived && !completed
        "Cancelled" -> false
        else -> true
    }
    val query = search.trim()
    var emis = viewModel.reportsUiState.data.emis.filter { (inPeriod(it.startDate) || it.payments.any { p -> inPeriod(p.paidDate ?: p.dueDate) }) && statusMatches(it.archived, emiCompleted(it)) && (query.isBlank() || listOf(it.name, it.category, it.seller).any { value -> value.contains(query, true) }) && reportType in listOf("Overview", "Payments", "EMI") }
    var loans = viewModel.reportsUiState.data.loans.filter { (inPeriod(it.startDate) || it.payments.any { p -> inPeriod(p.paidDate ?: p.dueDate) }) && statusMatches(it.archived, loanCompleted(it)) && (query.isBlank() || listOf(it.name, it.type, it.lender).any { value -> value.contains(query, true) }) && reportType in listOf("Overview", "Payments", "Loans") }
    var debts = viewModel.reportsUiState.data.debts.filter { ((it.dueDate?.let { value -> inPeriod(value) } == true) || it.payments.any { p -> inPeriod(p.paidDate ?: p.dueDate) } || it.paymentRequests.any { request -> inPeriod(request.createdDate) }) && (if (status == "Cancelled") it.paymentRequests.any { request -> request.status == "CANCELLED" } else statusMatches(it.archived, debtCompleted(it))) && (query.isBlank() || listOf(it.name, it.notes, it.reason).any { value -> value.contains(query, true) }) && when (reportType) { "Overview", "Payments" -> true; "Money I Owe" -> it.direction == "I Owe"; "Money Owed to Me" -> it.direction == "Owed to Me"; else -> false } }
    var expenses = viewModel.reportsUiState.data.expenses.filter { status == "All statuses" && inPeriod(it.date) && (query.isBlank() || listOf(it.title, it.category, it.notes).any { value -> value.contains(query, true) }) && reportType in listOf("Overview", "Expenses") }
    when (sort) {
        "Oldest first" -> { emis = emis.sortedBy { it.startDate }; loans = loans.sortedBy { it.startDate }; debts = debts.sortedBy { it.dueDate ?: 0L }; expenses = expenses.sortedBy { it.date } }
        "Highest amount" -> { emis = emis.sortedByDescending { it.totalPayable }; loans = loans.sortedByDescending { it.totalPayable }; debts = debts.sortedByDescending { it.originalAmount }; expenses = expenses.sortedByDescending { it.amount } }
        "Lowest amount" -> { emis = emis.sortedBy { it.totalPayable }; loans = loans.sortedBy { it.totalPayable }; debts = debts.sortedBy { it.originalAmount }; expenses = expenses.sortedBy { it.amount } }
        else -> { emis = emis.sortedByDescending { it.startDate }; loans = loans.sortedByDescending { it.startDate }; debts = debts.sortedByDescending { it.dueDate ?: 0L }; expenses = expenses.sortedByDescending { it.date } }
    }
    val filteredData = FinanceData(emis, loans, debts, expenses, viewModel.reportsUiState.data.receiptProfile)
    val matchCount = emis.size + loans.size + debts.size + expenses.size

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(FinanceSpacing.md), verticalArrangement = Arrangement.spacedBy(FinanceSpacing.sm)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Reports", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = { filtersVisible = !filtersVisible }) { Icon(Icons.Default.Sort, "Report filters") }
            }
            Text("$matchCount matching records • $period")
        }
        if (exportFeedback.isNotBlank()) item { FinanceFeedbackBanner(exportFeedback, exportFailed) }
        if (filtersVisible) item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = FinanceShapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) { Column(Modifier.padding(FinanceSpacing.sm), verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)) {
                OutlinedTextField(search, { search = it }, label = { Text("Search records") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                ChoiceDropdown("Report type", reportType, listOf("Overview", "Payments", "EMI", "Loans", "Money I Owe", "Money Owed to Me", "Expenses")) { reportType = it }
                ChoiceDropdown("Report period", period, listOf("This month", "Last month", "Custom range", "All time")) { period = it }
                if (period == "Custom range") { DatePickerField("From", startDate) { startDate = it }; DatePickerField("To", endDate) { endDate = it } }
                ChoiceDropdown("Status", status, listOf("All statuses", "Active", "Completed", "Archived", "Paid", "Pending", "Cancelled")) { status = it }
                ChoiceDropdown("Sort", sort, listOf("Newest first", "Oldest first", "Highest amount", "Lowest amount")) { sort = it }
                TextButton(onClick = { search = ""; reportType = "Overview"; period = "This month"; status = "All statuses"; sort = "Newest first" }) { Text("Clear Filters") }
            } }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = FinanceShapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) { Column(Modifier.padding(FinanceSpacing.md), verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)) {
                Text("Filtered Summary", fontWeight = FontWeight.Bold)
                Text("EMI ${emis.size} • Loans ${loans.size} • Debts ${debts.size} • Expenses ${expenses.size}")
                Text("Expenses: ${money(expenses.sumOf { it.amount })}", color = MaterialTheme.colorScheme.primary)
            } }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)) {
                Button(onClick = { pendingReport = buildSummaryReport(filteredData, period, reportType); launcher.launch("Finance_Summary.pdf") }, enabled = matchCount > 0, modifier = Modifier.weight(1f)) { Text("Summary PDF") }
                OutlinedButton(onClick = { pendingReport = buildCompleteReport(filteredData); launcher.launch("Finance_Detailed.pdf") }, enabled = matchCount > 0, modifier = Modifier.weight(1f)) { Text("Detailed PDF") }
            }
            OutlinedButton(onClick = { pendingExcel = filteredData; excelLauncher.launch("Filtered_Finance_Report.xlsx") }, enabled = matchCount > 0, modifier = Modifier.fillMaxWidth()) { Text("Professional Excel (.xlsx)") }
        }
        reportResultSection("EMI", emis.map { "${it.name} • ${money(it.totalPayable)} • ${if (emiCompleted(it)) "Completed" else "Active"}" }, "EMI" in expanded) { expanded = if ("EMI" in expanded) expanded - "EMI" else expanded + "EMI" }
        reportResultSection("Loans", loans.map { "${it.name} • ${money(it.totalPayable)} • ${if (loanCompleted(it)) "Completed" else "Active"}" }, "Loans" in expanded) { expanded = if ("Loans" in expanded) expanded - "Loans" else expanded + "Loans" }
        reportResultSection("Money I Owe", debts.filter { it.direction == "I Owe" }.map { "${it.name} • ${money(debtRemainingAmount(it))} to pay" }, "Money I Owe" in expanded) { expanded = if ("Money I Owe" in expanded) expanded - "Money I Owe" else expanded + "Money I Owe" }
        reportResultSection("Money Owed to Me", debts.filter { it.direction == "Owed to Me" }.map { "${it.name} • ${money(debtRemainingAmount(it))} to receive" }, "Money Owed to Me" in expanded) { expanded = if ("Money Owed to Me" in expanded) expanded - "Money Owed to Me" else expanded + "Money Owed to Me" }
        reportResultSection("Expenses", expenses.map { "${expenseDayKey(it.date)} • ${it.title} • ${money(it.amount)}" }, "Expenses" in expanded) { expanded = if ("Expenses" in expanded) expanded - "Expenses" else expanded + "Expenses" }
        if (matchCount == 0) item { FinanceEmptyState("No matching records", "Change or clear the report filters to see more results.") }
    }
}

fun buildSummaryReport(data: FinanceData, period: String, reportType: String): String = buildString {
    appendLine("MY FINANCE TRACKER — SUMMARY REPORT")
    appendLine("Generated: ${dateTimeText(System.currentTimeMillis())}")
    appendLine("Period: $period")
    appendLine("Report type: $reportType")
    appendLine()
    appendLine("RECORD SUMMARY")
    appendLine("EMI plans: ${data.emis.size}")
    appendLine("Loans: ${data.loans.size}")
    appendLine("Money I owe: ${data.debts.count { it.direction == "I Owe" }}")
    appendLine("Money owed to me: ${data.debts.count { it.direction == "Owed to Me" }}")
    appendLine("Expenses: ${data.expenses.size}")
    appendLine("Expense total: ${money(data.expenses.sumOf { it.amount })}")
    appendLine("EMI remaining: ${money(data.emis.sumOf { it.payments.filter { p -> p.paidDate == null }.sumOf { p -> p.amount } })}")
    appendLine("Loan remaining: ${money(data.loans.sumOf { it.payments.filter { p -> p.paidDate == null }.sumOf { p -> p.amount } })}")
    appendLine("Debt to pay: ${money(data.debts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) })}")
    appendLine("Money to receive: ${money(data.debts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) })}")
}


// ============================================================
// REPORT BUILDERS
// ============================================================

fun buildCompleteReport(
    data: FinanceData
): String {

    return buildString {

        appendLine(
            "MY FINANCE TRACKER — COMPLETE REPORT"
        )

        appendLine(
            "Generated: ${
                dateTimeText(
                    System.currentTimeMillis()
                )
            }"
        )

        appendLine()

        appendLine("EMI SUMMARY")

        appendLine(
            "Plans: ${data.emis.size}"
        )

        appendLine(
            "Remaining: ${
                money(
                    data.emis.sumOf { item ->
                        item.payments
                            .filter {
                                it.paidDate == null
                            }
                            .sumOf {
                                it.amount
                            }
                    }
                )
            }"
        )

        appendLine()
        appendLine("EXPENSE SUMMARY")
        appendLine("Entries: ${data.expenses.size}")
        appendLine(
            "Spent today: ${money(data.expenses.filter { isCurrentExpenseDay(it.date) }.sumOf { it.amount })}"
        )
        appendLine(
            "Spent this month: ${money(data.expenses.filter { isCurrentExpenseMonth(it.date) }.sumOf { it.amount })}"
        )
        appendLine("All expenses: ${money(data.expenses.sumOf { it.amount })}")

        appendLine()

        appendLine("LOAN SUMMARY")

        appendLine(
            "Loans: ${data.loans.size}"
        )

        appendLine(
            "Remaining: ${
                money(
                    data.loans.sumOf { item ->
                        item.payments
                            .filter {
                                it.paidDate == null
                            }
                            .sumOf {
                                it.amount
                            }
                    }
                )
            }"
        )

        appendLine()

        appendLine("DEBT SUMMARY")

        appendLine(
            "Debts: ${data.debts.size}"
        )

        appendLine(
            "Remaining: ${
                money(
                    data.debts.sumOf { debtRemainingAmount(it) }
                )
            }"
        )

        data.emis.forEach {

            appendLine()

            append(
                buildEmiReport(it)
            )
        }

        data.loans.forEach {

            appendLine()

            append(
                buildLoanReport(it)
            )
        }

        data.debts.forEach {

            appendLine()

            append(
                buildDebtReport(it)
            )
        }

        if (data.expenses.isNotEmpty()) {
            appendLine()
            append(buildExpenseReport(data.expenses))
        }
    }
}

fun buildExpenseReport(expenses: List<Expense>): String {
    return buildString {
        appendLine("EXPENSE REPORT")
        appendLine("Entries: ${expenses.size}")
        appendLine("Total: ${money(expenses.sumOf { it.amount })}")
        appendLine()

        appendLine("CURRENT MONTH BY CATEGORY")
        expenses
            .filter { isCurrentExpenseMonth(it.date) }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .forEach { (category, total) ->
                appendLine("$category: ${money(total)}")
            }
        appendLine()

        expenses.sortedByDescending { it.date }.forEach {
            appendLine(
                "${expenseDayKey(it.date)} | ${it.category} | ${it.title} | ${money(it.amount)}" +
                        if (it.notes.isBlank()) "" else " | ${it.notes}"
            )
        }
    }
}

fun buildEmiReport(
    item: EmiItem
): String {

    return buildString {

        appendLine("EMI REPORT")
        appendLine("Item: ${item.name}")
        appendLine("Category: ${item.category}")
        appendLine("Seller: ${item.seller}")
        appendLine("Financing source: ${item.financingSource}")
        appendLine("How received: ${item.receivedMethod}")
        if (item.financingChannel.isNotBlank()) appendLine("Provider / bank: ${item.financingChannel}")
        if (item.financingAccountName.isNotBlank()) appendLine("Account holder / party: ${item.financingAccountName}")
        if (item.financingAccountNumber.isNotBlank()) appendLine("Account / mobile number: ${item.financingAccountNumber}")
        if (item.financingReference.isNotBlank()) appendLine("Transaction reference: ${item.financingReference}")

        appendLine(
            "Price: ${money(item.price)}"
        )

        appendLine(
            "Down payment: ${money(item.downPayment)}"
        )

        appendLine(
            "Financed amount: ${money(item.financedAmount)}"
        )

        appendLine(
            "Interest rate: ${item.interestRate}%"
        )

        appendLine(
            "Interest amount: ${money(item.interestAmount)}"
        )

        appendLine(
            "Total payable: ${money(item.totalPayable)}"
        )

        appendLine(
            "Monthly payment: ${money(item.monthlyPayment)}"
        )

        appendLine(
            "Installments: ${item.installments}"
        )

        appendLine(
            "Due day: ${item.dueDay}"
        )

        appendLine(
            "Reminder days: ${
                item.reminderDays.joinToString(", ")
            }"
        )

        appendLine(
            "Progress: ${
                item.payments.count {
                    it.paidDate != null
                }
            }/${item.installments}"
        )

        appendLine(
            "Remaining: ${
                money(
                    item.payments
                        .filter {
                            it.paidDate == null
                        }
                        .sumOf {
                            it.amount
                        }
                )
            }"
        )

        appendLine()

        appendLine(
            "PAYMENT HISTORY"
        )

        item.payments.forEach {

            appendLine(
                "#${it.number} | " +
                        "Due ${dateText(it.dueDate)} | " +
                        "${money(it.amount)} | " +
                        if (it.paidDate == null) {
                            "PENDING"
                        } else {
                            "PAID ${dateText(it.paidDate)}"
                        }
            )
        }
    }
}

fun buildLoanReport(
    item: Loan
): String {

    return buildString {

        appendLine("LOAN REPORT")
        appendLine("Loan: ${item.name}")
        appendLine("Type: ${item.type}")
        appendLine("Lender: ${item.lender}")
        appendLine("Financing source: ${item.financingSource}")
        appendLine("How received: ${item.receivedMethod}")
        if (item.financingChannel.isNotBlank()) appendLine("Provider / bank: ${item.financingChannel}")
        if (item.financingAccountName.isNotBlank()) appendLine("Account holder / party: ${item.financingAccountName}")
        if (item.financingAccountNumber.isNotBlank()) appendLine("Account / mobile number: ${item.financingAccountNumber}")
        if (item.financingReference.isNotBlank()) appendLine("Transaction reference: ${item.financingReference}")

        appendLine(
            "Principal: ${money(item.principal)}"
        )

        appendLine(
            "Interest rate: ${item.interestRate}%"
        )

        appendLine(
            "Interest: ${money(item.interestAmount)}"
        )

        appendLine(
            "Total payable: ${money(item.totalPayable)}"
        )

        appendLine(
            "Monthly payment: ${money(item.monthlyPayment)}"
        )

        appendLine(
            "Installments: ${item.installments}"
        )

        appendLine(
            "Due day: ${item.dueDay}"
        )

        appendLine(
            "Progress: ${
                item.payments.count {
                    it.paidDate != null
                }
            }/${item.installments}"
        )

        appendLine(
            "Remaining: ${
                money(
                    item.payments
                        .filter {
                            it.paidDate == null
                        }
                        .sumOf {
                            it.amount
                        }
                )
            }"
        )

        appendLine()

        appendLine(
            "REPAYMENT HISTORY"
        )

        item.payments.forEach {

            appendLine(
                "#${it.number} | " +
                        "Due ${dateText(it.dueDate)} | " +
                        "${money(it.amount)} | " +
                        if (it.paidDate == null) {
                            "PENDING"
                        } else {
                            "PAID ${dateText(it.paidDate)}"
                        }
            )
        }
    }
}

fun buildDebtReport(
    item: Debt
): String {

    return buildString {

        appendLine("DEBT REPORT")

        appendLine(
            "Name: ${item.name}"
        )

        appendLine(
            "Direction: ${item.direction}"
        )
        appendLine("How received / given: ${item.receivedOrGivenMethod}")
        if (item.financingChannel.isNotBlank()) appendLine("Provider / bank: ${item.financingChannel}")
        if (item.financingAccountName.isNotBlank()) appendLine("Account holder / party: ${item.financingAccountName}")
        if (item.financingAccountNumber.isNotBlank()) appendLine("Account / mobile number: ${item.financingAccountNumber}")
        if (item.financingReference.isNotBlank()) appendLine("Transaction reference: ${item.financingReference}")

        appendLine(
            "Original amount: ${
                money(item.originalAmount)
            }"
        )

        appendLine(
            "Paid: ${
                money(
                    debtPaidAmount(item)
                )
            }"
        )

        appendLine(
            "Remaining: ${
                money(
                    debtRemainingAmount(item)
                )
            }"
        )

        appendLine(
            "Notes: ${item.notes}"
        )

        appendLine()

        appendLine(
            "PAYMENT HISTORY"
        )

        item.payments.forEach {

            appendLine(
                "#${it.number} | " +
                        "${dateTimeText(
                            it.paidDate
                                ?: it.dueDate
                        )} | " +
                        money(it.amount)
            )
        }
    }
}


// ============================================================
// PDF GENERATION
// ============================================================

internal data class XlsxMoney(val value: Double)
internal data class XlsxNumber(val value: Number)

fun writeXlsxToUri(context: Context, uri: android.net.Uri, data: FinanceData, period: String, reportType: String) {
    fun escape(value: String): String {
        // XML 1.0 forbids most control characters. Desktop Excel rejects the entire
        // worksheet when one appears in a user-entered name, reference, or note.
        val xmlSafe = value.filter { character ->
            character == '\t' || character == '\n' || character == '\r' || character.code >= 0x20
        }
        return xmlSafe.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
    }
    fun columnName(index: Int): String {
        var value = index + 1
        var result = ""
        while (value > 0) { value--; result = ('A'.code + value % 26).toChar() + result; value /= 26 }
        return result
    }
    fun sheet(title: String, headers: List<String>, rows: List<List<Any?>>): String {
        val headerRow = 5
        val lastColumn = columnName(headers.lastIndex)
        val xml = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
        append("<sheetViews><sheetView showGridLines=\"0\" workbookViewId=\"0\"><pane ySplit=\"5\" topLeftCell=\"A6\" activePane=\"bottomLeft\" state=\"frozen\"/></sheetView></sheetViews>")
        // OOXML requires sheetFormatPr before cols and sheetData. Mobile viewers
        // tolerated the old order, but desktop Microsoft Excel removed the sheets.
        append("<sheetFormatPr defaultRowHeight=\"18\"/>")
        append("<cols>"); headers.indices.forEach { index -> append("<col min=\"${index + 1}\" max=\"${index + 1}\" width=\"${when { headers[index].contains("Note") || headers[index].contains("Details") -> 28; headers[index].contains("Date") -> 15; else -> 20 }}\" customWidth=\"1\"/>") }; append("</cols><sheetData>")
        fun row(number: Int, values: List<Any?>, style: Int? = null) {
            append("<row r=\"$number\">")
            values.forEachIndexed { index, value ->
                val ref = "${columnName(index)}$number"
                when (value) {
                    is XlsxMoney -> append("<c r=\"$ref\" s=\"2\"><v>${value.value}</v></c>")
                    is XlsxNumber -> append("<c r=\"$ref\" s=\"3\"><v>${value.value}</v></c>")
                    else -> append("<c r=\"$ref\" t=\"inlineStr\"${style?.let { " s=\"$it\"" } ?: ""}><is><t xml:space=\"preserve\">${escape(localized(value?.toString() ?: ""))}</t></is></c>")
                }
            }
            append("</row>")
        }
        row(1, listOf("MY FINANCE TRACKER — $title"), 1)
        row(2, listOf("Generated", dateTimeText(System.currentTimeMillis()), "Period", period))
        row(3, listOf("Currency", "${AppLocaleState.currencyCode} (${AppLocaleState.currencySymbol})", "Report", reportType))
        row(headerRow, headers, 1)
        rows.forEachIndexed { index, values -> row(index + headerRow + 1, values) }
        append("</sheetData><mergeCells count=\"1\"><mergeCell ref=\"A1:${lastColumn}1\"/></mergeCells>")
        append("<autoFilter ref=\"A$headerRow:${lastColumn}${rows.size + headerRow}\"/><pageMargins left=\"0.25\" right=\"0.25\" top=\"0.5\" bottom=\"0.5\" header=\"0.2\" footer=\"0.2\"/><pageSetup orientation=\"landscape\" fitToWidth=\"1\" fitToHeight=\"0\"/></worksheet>")
        }
        return OoxmlRules.requireValidWorksheet(xml)
    }

    val summaryRows = listOf(
        listOf("Generated", dateTimeText(System.currentTimeMillis())), listOf("Period", period), listOf("Report type", reportType),
        listOf("EMI plans", XlsxNumber(data.emis.size)), listOf("Loans", XlsxNumber(data.loans.size)),
        listOf("Money I Owe", XlsxNumber(data.debts.count { it.direction == "I Owe" })), listOf("Money Owed to Me", XlsxNumber(data.debts.count { it.direction == "Owed to Me" })),
        listOf("Expenses", XlsxNumber(data.expenses.size)), listOf("Expense total", XlsxMoney(data.expenses.sumOf { it.amount })),
        listOf("Debt to pay", XlsxMoney(data.debts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) })),
        listOf("Money to receive", XlsxMoney(data.debts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) }))
    )
    val sheets = listOf(
        "Dashboard Summary" to sheet("Dashboard Summary", listOf("Metric", "Value"), summaryRows),
        "EMI Plans" to sheet("EMI Plans", listOf("Item", "Category", "Seller", "Financing Source", "Received Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Transaction Reference", "Price", "Down Payment", "Financed", "Interest %", "Total Payable", "Installments", "Monthly", "Start Date", "Status"), data.emis.map { listOf(it.name, it.category, it.seller, it.financingSource, it.receivedMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.price), XlsxMoney(it.downPayment), XlsxMoney(it.financedAmount), XlsxNumber(it.interestRate), XlsxMoney(it.totalPayable), XlsxNumber(it.installments), XlsxMoney(it.monthlyPayment), dateText(it.startDate), if (it.archived) "Archived" else if (emiCompleted(it)) "Completed" else "Active") }),
        "Loans" to sheet("Loans", listOf("Loan", "Type", "Lender", "Financing Source", "Received Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Transaction Reference", "Principal", "Interest %", "Total Payable", "Installments", "Monthly", "Start Date", "Status"), data.loans.map { listOf(it.name, it.type, it.lender, it.financingSource, it.receivedMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.principal), XlsxNumber(it.interestRate), XlsxMoney(it.totalPayable), XlsxNumber(it.installments), XlsxMoney(it.monthlyPayment), dateText(it.startDate), if (it.archived) "Archived" else if (loanCompleted(it)) "Completed" else "Active") }),
        "Debts I Owe" to sheet("Debts I Owe", listOf("Person / Organization", "Debt Date", "Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Reference", "Original", "Paid", "Remaining", "Due Date", "Reason", "Status"), data.debts.filter { it.direction == "I Owe" }.map { listOf(it.name, dateText(it.debtDate), it.receivedOrGivenMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.originalAmount), XlsxMoney(debtPaidAmount(it)), XlsxMoney(debtRemainingAmount(it)), it.dueDate?.let(::dateText) ?: "", it.reason, if (it.archived) "Archived" else if (debtCompleted(it)) "Completed" else "Active") }),
        "Owed to Me" to sheet("Money Owed to Me", listOf("Person / Organization", "Debt Date", "Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Reference", "Original", "Received", "Remaining", "Due Date", "Reason", "Status"), data.debts.filter { it.direction == "Owed to Me" }.map { listOf(it.name, dateText(it.debtDate), it.receivedOrGivenMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.originalAmount), XlsxMoney(debtPaidAmount(it)), XlsxMoney(debtRemainingAmount(it)), it.dueDate?.let(::dateText) ?: "", it.reason, if (it.archived) "Archived" else if (debtCompleted(it)) "Completed" else "Active") }),
        "Expenses" to sheet("Expenses", listOf("Date", "Expense", "Category", "Amount", "Notes"), data.expenses.map { listOf(expenseDayKey(it.date), it.title, it.category, XlsxMoney(it.amount), it.notes) }),
        "Payments" to sheet("Payment History", listOf("Record Type", "Record", "Payment No.", "Due Date", "Paid Date", "Amount", "Status", "Method", "Provider / Bank", "Reference", "Party", "Account", "Branch", "Routing", "Details", "Notes"), buildList {
            data.emis.forEach { plan -> plan.payments.forEach { p -> add(listOf("EMI", plan.name, XlsxNumber(p.number), dateText(p.dueDate), p.paidDate?.let { dateText(it) } ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
            data.loans.forEach { plan -> plan.payments.forEach { p -> add(listOf("Loan", plan.name, XlsxNumber(p.number), dateText(p.dueDate), p.paidDate?.let { dateText(it) } ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
            data.debts.forEach { plan -> plan.payments.forEach { p -> add(listOf("Debt", plan.name, XlsxNumber(p.number), dateText(p.dueDate), p.paidDate?.let { dateText(it) } ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
        }),
        "Payment Requests" to sheet("Payment Requests", listOf("Request No.", "Requested From", "Created", "Due Date", "Amount", "Received", "Status", "Preferred Method", "Provider / Bank", "Account Name", "Account / Mobile", "Reference", "Instructions", "Message"), buildList {
            data.debts.filter { it.direction == "Owed to Me" }.forEach { debt -> debt.paymentRequests.forEach { request -> add(listOf(request.requestNumber, debt.name, dateText(request.createdDate), request.dueDate?.let(::dateText) ?: "", XlsxMoney(request.amount), XlsxMoney(request.receivedAmount), request.status, request.paymentMethod, request.paymentChannel, request.accountName, request.accountNumber, request.referenceNumber, request.paymentInstructions, request.message)) } }
        })
    )
    context.contentResolver.openOutputStream(uri)?.use { output ->
        ZipOutputStream(output).use { zip ->
            fun entry(name: String, content: String) { zip.putNextEntry(ZipEntry(name)); zip.write(content.toByteArray(Charsets.UTF_8)); zip.closeEntry() }
            entry("[Content_Types].xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>${sheets.indices.joinToString("") { "<Override PartName=\"/xl/worksheets/sheet${it + 1}.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>" }}</Types>")
            entry("_rels/.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>")
            entry("xl/workbook.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets>${sheets.mapIndexed { index, pair -> "<sheet name=\"${pair.first}\" sheetId=\"${index + 1}\" r:id=\"rId${index + 1}\"/>" }.joinToString("")}</sheets></workbook>")
            entry("xl/_rels/workbook.xml.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">${sheets.indices.joinToString("") { "<Relationship Id=\"rId${it + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet${it + 1}.xml\"/>" }}<Relationship Id=\"rId${sheets.size + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/></Relationships>")
            entry("xl/styles.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><numFmts count=\"1\"><numFmt numFmtId=\"164\" formatCode=\"${escape(AppLocaleState.currencySymbol)}#,##0.00\"/></numFmts><fonts count=\"2\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font><font><b/><color rgb=\"FFFFFFFF\"/><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts><fills count=\"3\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF007C7A\"/><bgColor indexed=\"64\"/></patternFill></fill></fills><borders count=\"1\"><border/></borders><cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs><cellXfs count=\"4\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/><xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" applyFill=\"1\" applyFont=\"1\"/><xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"0\" applyNumberFormat=\"1\"/><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellXfs></styleSheet>")
            sheets.forEachIndexed { index, pair -> entry("xl/worksheets/sheet${index + 1}.xml", pair.second) }
        }
    }
}

fun writePdfToUri(
    context: Context,
    uri: android.net.Uri,
    text: String
) {

    context.contentResolver
        .openOutputStream(uri)
        ?.use { output ->

            val document =
                PdfDocument()

            val paint = Paint().apply { isAntiAlias = true }
            val teal = android.graphics.Color.rgb(0, 124, 122)
            val paleTeal = android.graphics.Color.rgb(226, 245, 242)
            val ink = android.graphics.Color.rgb(35, 42, 42)
            val muted = android.graphics.Color.rgb(92, 103, 102)

            var pageNumber = 1

            var page =
                document.startPage(
                    PdfDocument.PageInfo.Builder(
                        595,
                        842,
                        pageNumber
                    ).create()
                )

            var canvas =
                page.canvas

            fun drawHeader() {
                paint.color = teal
                canvas.drawRect(0f, 0f, 595f, 104f, paint)
                val logo = BitmapFactory.decodeResource(context.resources, com.mdzahidalam.myfinancetracker.R.drawable.app_logo)
                if (logo != null) canvas.drawBitmap(logo, null, RectF(28f, 20f, 92f, 84f), paint)
                paint.color = android.graphics.Color.WHITE
                paint.textSize = 18f
                paint.isFakeBoldText = true
                canvas.drawText(localized("MY FINANCE TRACKER"), 112f, 49f, paint)
                paint.textSize = 10f
                paint.isFakeBoldText = false
                canvas.drawText(localized("Secure personal finance record"), 112f, 69f, paint)
            }
            fun wrap(value: String, maxWidth: Float = 520f): List<String> {
                if (value.isBlank()) return listOf("")
                if (paint.measureText(value) <= maxWidth) return listOf(value)
                val result = mutableListOf<String>()
                var current = ""
                value.split(Regex("\\s+")).forEach { word ->
                    val candidate = if (current.isBlank()) word else "$current $word"
                    if (paint.measureText(candidate) > maxWidth && current.isNotBlank()) {
                        result += current
                        current = word
                    } else {
                        current = candidate
                    }
                }
                if (current.isNotBlank()) result += current
                return result.ifEmpty { listOf("") }
            }
            drawHeader()
            var y = 132f
            val reportDocument = LegacyReportAdapter.fromText(text)
            val inputLines = reportDocument.blocks.map { block ->
                when (block) {
                    is ReportBlock.Heading -> block.text
                    is ReportBlock.Field -> "${block.label}: ${block.value}"
                    is ReportBlock.Paragraph -> block.text
                    is ReportBlock.Signature -> "[[SIGNATURE:${block.encodedImage}]]"
                    is ReportBlock.Table -> block.rows.joinToString(" • ") { it.joinToString(" | ") }
                }
            }.map(::localizedExport)
            inputLines.forEach { raw ->
                val line = raw.trim()
                if (y > 785f) {
                    paint.color = muted; paint.textSize = 9f; paint.isFakeBoldText = false
                    canvas.drawText("Page $pageNumber", 515f, 822f, paint)
                    document.finishPage(page)
                    pageNumber++
                    page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
                    canvas = page.canvas
                    drawHeader()
                    y = 132f
                }
                when {
                    line.startsWith("[[SIGNATURE:") && line.endsWith("]]" ) -> {
                        val encoded = line.removePrefix("[[SIGNATURE:").removeSuffix("]]" )
                        val signature = runCatching {
                            val bytes = Base64.decode(encoded, Base64.NO_WRAP)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }.getOrNull()
                        if (signature != null) {
                            paint.color = muted; paint.textSize = 10f; paint.isFakeBoldText = true
                            canvas.drawText(localized("Authorized signature"), 40f, y, paint)
                            val ratio = signature.width.toFloat() / signature.height.coerceAtLeast(1)
                            val height = 58f
                            val width = minOf(170f, height * ratio)
                            canvas.drawBitmap(signature, null, RectF(40f, y + 8f, 40f + width, y + 8f + height), paint)
                            paint.color = muted
                            canvas.drawLine(40f, y + 72f, 230f, y + 72f, paint)
                            y += 90f
                        }
                    }
                    line.isBlank() -> y += 10f
                    line == line.uppercase(Locale.US) && !line.contains(":") -> {
                        paint.color = paleTeal; canvas.drawRoundRect(28f, y - 20f, 567f, y + 10f, 6f, 6f, paint)
                        paint.color = teal; paint.textSize = 14f; paint.isFakeBoldText = true
                        canvas.drawText(line, 42f, y, paint); y += 40f
                    }
                    line.contains(":") -> {
                        val label = line.substringBefore(":").trim()
                        val value = line.substringAfter(":").trim()
                        paint.color = android.graphics.Color.rgb(247, 249, 249)
                        paint.textSize = 10f
                        paint.isFakeBoldText = false
                        val wrapped = wrap(value, 360f)
                        val rowHeight = max(30f, 18f * wrapped.size + 10f)
                        canvas.drawRoundRect(28f, y - 17f, 567f, y - 17f + rowHeight, 4f, 4f, paint)
                        paint.color = muted; paint.textSize = 10f; paint.isFakeBoldText = true
                        canvas.drawText(label, 40f, y, paint)
                        paint.color = ink; paint.isFakeBoldText = false
                        wrapped.forEachIndexed { index, valueLine -> canvas.drawText(valueLine, 190f, y + index * 16f, paint) }
                        y += rowHeight + 5f
                    }
                    else -> {
                        paint.color = ink; paint.textSize = 10f; paint.isFakeBoldText = false
                        wrap(line).forEach { valueLine -> canvas.drawText(valueLine, 34f, y, paint); y += 15f }
                    }
                }
            }
            paint.color = paleTeal; canvas.drawRect(0f, 796f, 595f, 842f, paint)
            paint.color = teal; paint.textSize = 9f; paint.isFakeBoldText = true
            canvas.drawText(localized("Generated by My Finance Tracker • Powered by Md. Zahid Alam"), 30f, 822f, paint)

            document.finishPage(
                page
            )

            document.writeTo(
                output
            )

            document.close()
        }
}
