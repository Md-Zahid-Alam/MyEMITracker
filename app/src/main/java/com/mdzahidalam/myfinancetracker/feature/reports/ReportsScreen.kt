package com.mdzahidalam.myfinancetracker.feature.reports
import androidx.compose.animation.AnimatedVisibility
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
import com.mdzahidalam.myfinancetracker.export.xlsx.PremiumXlsxStyles
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
        item {
            AnimatedVisibility(visible = filtersVisible) {
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

        appendLine("Date | Category | Expense | Amount | Notes")
        expenses.sortedByDescending { it.date }.forEach {
            appendLine("${expenseDayKey(it.date)} | ${it.category} | ${it.title} | ${money(it.amount)} | ${it.notes}")
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

        if (item.payments.isNotEmpty()) {
            appendLine()
            appendLine("PAYMENT HISTORY")
            appendLine("No. | Due date | Amount | Status")
            item.payments.forEach {

                appendLine(
                    "#${it.number} | Due ${dateText(it.dueDate)} | ${money(it.amount)} | " +
                        if (it.paidDate == null) "PENDING" else "PAID ${dateText(it.paidDate)}"
                )
            }
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

        if (item.payments.isNotEmpty()) {
            appendLine()
            appendLine("REPAYMENT HISTORY")
            appendLine("No. | Due date | Amount | Status")
            item.payments.forEach {

                appendLine(
                    "#${it.number} | Due ${dateText(it.dueDate)} | ${money(it.amount)} | " +
                        if (it.paidDate == null) "PENDING" else "PAID ${dateText(it.paidDate)}"
                )
            }
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

        if (item.payments.isNotEmpty()) {
            appendLine()
            appendLine("PAYMENT HISTORY")
            appendLine("No. | Payment date | Amount")
            item.payments.forEach {

                appendLine("#${it.number} | ${dateTimeText(it.paidDate ?: it.dueDate)} | ${money(it.amount)}")
            }
        }
    }
}


// ============================================================
// PDF GENERATION
// ============================================================

internal data class XlsxMoney(val value: Double)
internal data class XlsxNumber(val value: Number)
internal data class XlsxDate(val epochMillis: Long)
internal data class XlsxPercent(val value: Double)
internal data class XlsxFormula(val formula: String, val cachedValue: Double, val money: Boolean = true)
internal data class XlsxLink(val label: String, val location: String)

fun writeXlsxToUri(context: Context, uri: android.net.Uri, data: FinanceData, period: String, reportType: String) {
    fun escape(value: String): String {
        // Desktop Excel rejects a complete worksheet if user data contains an XML
        // control character, so every inline string goes through the tested rule.
        return OoxmlRules.sanitizeXmlText(value)
    }
    fun columnName(index: Int): String {
        var value = index + 1
        var result = ""
        while (value > 0) { value--; result = ('A'.code + value % 26).toChar() + result; value /= 26 }
        return result
    }
    fun sheet(title: String, headers: List<String>, rows: List<List<Any?>>, dashboard: Boolean = false): String {
        val headerRow = 5
        val lastColumn = columnName(headers.lastIndex)
        val links = mutableListOf<Triple<String, String, String>>()
        val xml = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">")
        append("<sheetViews><sheetView showGridLines=\"0\" workbookViewId=\"0\"><pane ySplit=\"5\" topLeftCell=\"A6\" activePane=\"bottomLeft\" state=\"frozen\"/></sheetView></sheetViews>")
        // OOXML requires sheetFormatPr before cols and sheetData. Mobile viewers
        // tolerated the old order, but desktop Microsoft Excel removed the sheets.
        append("<sheetFormatPr defaultRowHeight=\"18\"/>")
        append("<cols>"); headers.indices.forEach { index -> append("<col min=\"${index + 1}\" max=\"${index + 1}\" width=\"${when { headers[index].contains("Note") || headers[index].contains("Details") -> 28; headers[index].contains("Date") -> 15; else -> 20 }}\" customWidth=\"1\"/>") }; append("</cols><sheetData>")
        fun row(number: Int, values: List<Any?>, style: Int? = null, banded: Boolean = false) {
            append("<row r=\"$number\">")
            values.forEachIndexed { index, value ->
                val ref = "${columnName(index)}$number"
                val textValue = value?.toString() ?: ""
                val statusStyle = if (number > headerRow) when (textValue.uppercase(Locale.US)) {
                    "PAID", "COMPLETED", "ACTIVE" -> 7
                    "PENDING", "UNPAID", "PARTIALLY PAID", "PARTIAL" -> 8
                    "CANCELLED", "OVERDUE" -> 9
                    "ARCHIVED" -> 10
                    else -> null
                } else null
                when (value) {
                    is XlsxMoney -> append("<c r=\"$ref\" s=\"${if (banded) 6 else 2}\"><v>${value.value}</v></c>")
                    is XlsxNumber -> append("<c r=\"$ref\" s=\"${if (banded) 5 else 3}\"><v>${value.value}</v></c>")
                    is XlsxDate -> {
                        val serial = value.epochMillis / 86_400_000.0 + 25_569.0
                        append("<c r=\"$ref\" s=\"${if (banded) 13 else 12}\"><v>$serial</v></c>")
                    }
                    is XlsxPercent -> append("<c r=\"$ref\" s=\"${if (banded) 15 else 14}\"><v>${value.value / 100.0}</v></c>")
                    is XlsxFormula -> append("<c r=\"$ref\" s=\"${if (value.money) if (banded) 6 else 2 else if (banded) 5 else 3}\"><f>${escape(value.formula)}</f><v>${value.cachedValue}</v></c>")
                    is XlsxLink -> {
                        append("<c r=\"$ref\" t=\"inlineStr\" s=\"16\"><is><t xml:space=\"preserve\">${escape(localized(value.label))}</t></is></c>")
                        links += Triple(ref, value.location, value.label)
                    }
                    else -> {
                        val cellStyle = statusStyle ?: style ?: if (banded) 5 else 4
                        append("<c r=\"$ref\" t=\"inlineStr\" s=\"$cellStyle\"><is><t xml:space=\"preserve\">${escape(localized(textValue))}</t></is></c>")
                    }
                }
            }
            append("</row>")
        }
        row(1, listOf("MY FINANCE TRACKER — $title"), 1)
        row(2, listOf("Generated", dateTimeText(System.currentTimeMillis()), "Period", period), 11)
        row(3, listOf("Currency", "${AppLocaleState.currencyCode} (${AppLocaleState.currencySymbol})", "Report", reportType), 11)
        if (dashboard) {
            row(4, listOf(
                XlsxLink("EMI Plans", "'EMI Plans'!A1"), XlsxLink("Loans", "'Loans'!A1"),
                XlsxLink("Debts I Owe", "'Debts I Owe'!A1"), XlsxLink("Owed to Me", "'Owed to Me'!A1"),
                XlsxLink("Expenses", "'Expenses'!A1"), XlsxLink("Payments", "'Payments'!A1"),
                XlsxLink("Payment Requests", "'Payment Requests'!A1"), XlsxLink("Expense Categories", "'Expense Categories'!A1")
            ))
        } else row(4, listOf(XlsxLink("← Dashboard", "'Dashboard Summary'!A1")))
        row(headerRow, headers, 1)
        rows.forEachIndexed { index, values -> row(index + headerRow + 1, values, banded = index % 2 == 1) }
        append("</sheetData><mergeCells count=\"1\"><mergeCell ref=\"A1:${lastColumn}1\"/></mergeCells>")
        append("<autoFilter ref=\"A$headerRow:${lastColumn}${rows.size + headerRow}\"/>")
        if (dashboard) {
            append("<conditionalFormatting sqref=\"B6:B18\"><cfRule type=\"dataBar\" priority=\"1\"><dataBar showValue=\"1\"><cfvo type=\"min\" val=\"0\"/><cfvo type=\"max\" val=\"0\"/><color rgb=\"FF63C7C2\"/></dataBar></cfRule></conditionalFormatting>")
        }
        if (links.isNotEmpty()) append("<hyperlinks>${links.joinToString("") { (ref, location, label) -> "<hyperlink ref=\"$ref\" location=\"${escape(location)}\" display=\"${escape(label)}\"/>" }}</hyperlinks>")
        append("<printOptions horizontalCentered=\"1\"/><pageMargins left=\"0.25\" right=\"0.25\" top=\"0.65\" bottom=\"0.65\" header=\"0.25\" footer=\"0.25\"/>")
        append("<pageSetup orientation=\"landscape\" fitToWidth=\"1\" fitToHeight=\"0\"/><headerFooter><oddHeader>&amp;C&amp;B$title</oddHeader><oddFooter>&amp;LMy Finance Tracker&amp;CConfidential personal report&amp;RPage &amp;P of &amp;N</oddFooter></headerFooter>")
        if (dashboard) append("<drawing r:id=\"rId1\"/>")
        append("</worksheet>")
        }
        return OoxmlRules.requireValidWorksheet(xml)
    }

    val emiRemaining = data.emis.sumOf { it.payments.filter { payment -> payment.paidDate == null }.sumOf { payment -> payment.amount } }
    val loanRemaining = data.loans.sumOf { it.payments.filter { payment -> payment.paidDate == null }.sumOf { payment -> payment.amount } }
    val debtToPay = data.debts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) }
    val moneyToReceive = data.debts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) }
    val expenseTotal = data.expenses.sumOf { it.amount }
    val summaryRows = listOf(
        listOf("EMI plans", XlsxFormula("COUNTA('EMI Plans'!A6:A1048576)", data.emis.size.toDouble(), money = false)),
        listOf("Loans", XlsxFormula("COUNTA('Loans'!A6:A1048576)", data.loans.size.toDouble(), money = false)),
        listOf("Money I Owe", XlsxFormula("COUNTA('Debts I Owe'!A6:A1048576)", data.debts.count { it.direction == "I Owe" }.toDouble(), money = false)),
        listOf("Money Owed to Me", XlsxFormula("COUNTA('Owed to Me'!A6:A1048576)", data.debts.count { it.direction == "Owed to Me" }.toDouble(), money = false)),
        listOf("Expenses", XlsxFormula("COUNTA('Expenses'!A6:A1048576)", data.expenses.size.toDouble(), money = false)),
        listOf("Expense total", XlsxFormula("SUM('Expenses'!D6:D1048576)", expenseTotal)),
        listOf("EMI remaining", XlsxFormula("SUM('EMI Plans'!S6:S1048576)", emiRemaining)),
        listOf("Loan remaining", XlsxFormula("SUM('Loans'!Q6:Q1048576)", loanRemaining)),
        listOf("Debt to pay", XlsxFormula("SUM('Debts I Owe'!J6:J1048576)", debtToPay)),
        listOf("Money to receive", XlsxFormula("SUM('Owed to Me'!J6:J1048576)", moneyToReceive)),
        listOf("Net receivable position", XlsxFormula("B15-B14", moneyToReceive - debtToPay)),
        listOf("Total active commitments", XlsxFormula("B12+B13+B14", emiRemaining + loanRemaining + debtToPay))
    )
    val categoryRows = data.expenses.groupBy { it.category }.map { (category, entries) ->
        listOf(category, XlsxMoney(entries.sumOf { it.amount }), XlsxNumber(entries.size))
    }.sortedByDescending { (it[1] as XlsxMoney).value }.ifEmpty { listOf(listOf("No expenses", XlsxMoney(0.0), XlsxNumber(0))) }
    val sheets = listOf(
        "Dashboard Summary" to sheet("Dashboard Summary", listOf("Metric", "Value"), summaryRows, dashboard = true),
        "EMI Plans" to sheet("EMI Plans", listOf("Item", "Category", "Seller", "Financing Source", "Received Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Transaction Reference", "Price", "Down Payment", "Financed", "Interest %", "Total Payable", "Installments", "Monthly", "Start Date", "Remaining", "Status"), data.emis.map { listOf(it.name, it.category, it.seller, it.financingSource, it.receivedMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.price), XlsxMoney(it.downPayment), XlsxMoney(it.financedAmount), XlsxPercent(it.interestRate), XlsxMoney(it.totalPayable), XlsxNumber(it.installments), XlsxMoney(it.monthlyPayment), XlsxDate(it.startDate), XlsxMoney(it.payments.filter { payment -> payment.paidDate == null }.sumOf { payment -> payment.amount }), if (it.archived) "Archived" else if (emiCompleted(it)) "Completed" else "Active") }),
        "Loans" to sheet("Loans", listOf("Loan", "Type", "Lender", "Financing Source", "Received Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Transaction Reference", "Principal", "Interest %", "Total Payable", "Installments", "Monthly", "Start Date", "Remaining", "Status"), data.loans.map { listOf(it.name, it.type, it.lender, it.financingSource, it.receivedMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.principal), XlsxPercent(it.interestRate), XlsxMoney(it.totalPayable), XlsxNumber(it.installments), XlsxMoney(it.monthlyPayment), XlsxDate(it.startDate), XlsxMoney(it.payments.filter { payment -> payment.paidDate == null }.sumOf { payment -> payment.amount }), if (it.archived) "Archived" else if (loanCompleted(it)) "Completed" else "Active") }),
        "Debts I Owe" to sheet("Debts I Owe", listOf("Person / Organization", "Debt Date", "Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Reference", "Original", "Paid", "Remaining", "Due Date", "Reason", "Status"), data.debts.filter { it.direction == "I Owe" }.map { listOf(it.name, XlsxDate(it.debtDate), it.receivedOrGivenMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.originalAmount), XlsxMoney(debtPaidAmount(it)), XlsxMoney(debtRemainingAmount(it)), it.dueDate?.let(::XlsxDate) ?: "", it.reason, if (it.archived) "Archived" else if (debtCompleted(it)) "Completed" else "Active") }),
        "Owed to Me" to sheet("Money Owed to Me", listOf("Person / Organization", "Debt Date", "Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Reference", "Original", "Received", "Remaining", "Due Date", "Reason", "Status"), data.debts.filter { it.direction == "Owed to Me" }.map { listOf(it.name, XlsxDate(it.debtDate), it.receivedOrGivenMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.originalAmount), XlsxMoney(debtPaidAmount(it)), XlsxMoney(debtRemainingAmount(it)), it.dueDate?.let(::XlsxDate) ?: "", it.reason, if (it.archived) "Archived" else if (debtCompleted(it)) "Completed" else "Active") }),
        "Expenses" to sheet("Expenses", listOf("Date", "Expense", "Category", "Amount", "Notes"), data.expenses.map { listOf(XlsxDate(it.date), it.title, it.category, XlsxMoney(it.amount), it.notes) }),
        "Payments" to sheet("Payment History", listOf("Record Type", "Record", "Payment No.", "Due Date", "Paid Date", "Amount", "Status", "Method", "Provider / Bank", "Reference", "Party", "Account", "Branch", "Routing", "Details", "Notes"), buildList {
            data.emis.forEach { plan -> plan.payments.forEach { p -> add(listOf("EMI", plan.name, XlsxNumber(p.number), XlsxDate(p.dueDate), p.paidDate?.let(::XlsxDate) ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
            data.loans.forEach { plan -> plan.payments.forEach { p -> add(listOf("Loan", plan.name, XlsxNumber(p.number), XlsxDate(p.dueDate), p.paidDate?.let(::XlsxDate) ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
            data.debts.forEach { plan -> plan.payments.forEach { p -> add(listOf("Debt", plan.name, XlsxNumber(p.number), XlsxDate(p.dueDate), p.paidDate?.let(::XlsxDate) ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
        }),
        "Payment Requests" to sheet("Payment Requests", listOf("Request No.", "Requested From", "Created", "Due Date", "Amount", "Received", "Outstanding", "Status", "Preferred Method", "Provider / Bank", "Account Name", "Account / Mobile", "Reference", "Instructions", "Message"), buildList {
            data.debts.filter { it.direction == "Owed to Me" }.forEach { debt -> debt.paymentRequests.forEach { request -> add(listOf(request.requestNumber, debt.name, XlsxDate(request.createdDate), request.dueDate?.let(::XlsxDate) ?: "", XlsxMoney(request.amount), XlsxMoney(request.receivedAmount), XlsxMoney(max(0.0, request.amount - request.receivedAmount)), request.status, request.paymentMethod, request.paymentChannel, request.accountName, request.accountNumber, request.referenceNumber, request.paymentInstructions, request.message)) } }
        }),
        "Expense Categories" to sheet("Expense Categories", listOf("Category", "Total", "Entries"), categoryRows)
    )
    context.contentResolver.openOutputStream(uri)?.use { output ->
        ZipOutputStream(output).use { zip ->
            fun entry(name: String, content: String) { zip.putNextEntry(ZipEntry(name)); zip.write(content.toByteArray(Charsets.UTF_8)); zip.closeEntry() }
            entry("[Content_Types].xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/><Override PartName=\"/xl/drawings/drawing1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.drawing+xml\"/><Override PartName=\"/xl/charts/chart1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.drawingml.chart+xml\"/>${sheets.indices.joinToString("") { "<Override PartName=\"/xl/worksheets/sheet${it + 1}.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>" }}</Types>")
            entry("_rels/.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>")
            entry("xl/workbook.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><bookViews><workbookView activeTab=\"0\"/></bookViews><sheets>${sheets.mapIndexed { index, pair -> "<sheet name=\"${pair.first}\" sheetId=\"${index + 1}\" r:id=\"rId${index + 1}\"/>" }.joinToString("")}</sheets><calcPr calcId=\"191029\" fullCalcOnLoad=\"1\" forceFullCalc=\"1\"/></workbook>")
            entry("xl/_rels/workbook.xml.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">${sheets.indices.joinToString("") { "<Relationship Id=\"rId${it + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet${it + 1}.xml\"/>" }}<Relationship Id=\"rId${sheets.size + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/></Relationships>")
            val premiumStyles = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><numFmts count=\"1\"><numFmt numFmtId=\"164\" formatCode=\"${escape(AppLocaleState.currencySymbol)}#,##0.00\"/></numFmts><fonts count=\"4\"><font><sz val=\"11\"/><name val=\"Calibri\"/><color rgb=\"FF232A2A\"/></font><font><b/><color rgb=\"FFFFFFFF\"/><sz val=\"11\"/><name val=\"Calibri\"/></font><font><b/><color rgb=\"FF005251\"/><sz val=\"11\"/><name val=\"Calibri\"/></font><font><color rgb=\"FF5C6766\"/><sz val=\"10\"/><name val=\"Calibri\"/></font></fonts><fills count=\"8\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF007C7A\"/></patternFill></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFF3F8F7\"/></patternFill></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFE2F5F2\"/></patternFill></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFE4F4EA\"/></patternFill></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFFFF0D5\"/></patternFill></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFF8E3E3\"/></patternFill></fill></fills><borders count=\"2\"><border/><border><bottom style=\"thin\"><color rgb=\"FFD9E2E1\"/></bottom></border></borders><cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs><cellXfs count=\"12\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/><xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" applyFill=\"1\" applyFont=\"1\" applyAlignment=\"1\"><alignment vertical=\"center\"/></xf><xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"1\" applyNumberFormat=\"1\"/><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\"/><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" applyAlignment=\"1\"><alignment vertical=\"top\" wrapText=\"1\"/></xf><xf numFmtId=\"0\" fontId=\"0\" fillId=\"3\" borderId=\"1\" applyFill=\"1\" applyAlignment=\"1\"><alignment vertical=\"top\" wrapText=\"1\"/></xf><xf numFmtId=\"164\" fontId=\"0\" fillId=\"3\" borderId=\"1\" applyFill=\"1\" applyNumberFormat=\"1\"/><xf numFmtId=\"0\" fontId=\"2\" fillId=\"5\" borderId=\"1\" applyFill=\"1\" applyFont=\"1\"/><xf numFmtId=\"0\" fontId=\"2\" fillId=\"6\" borderId=\"1\" applyFill=\"1\" applyFont=\"1\"/><xf numFmtId=\"0\" fontId=\"2\" fillId=\"7\" borderId=\"1\" applyFill=\"1\" applyFont=\"1\"/><xf numFmtId=\"0\" fontId=\"3\" fillId=\"3\" borderId=\"1\" applyFill=\"1\" applyFont=\"1\"/><xf numFmtId=\"0\" fontId=\"3\" fillId=\"0\" borderId=\"0\" applyFont=\"1\"/></cellXfs></styleSheet>"
            entry("xl/styles.xml", OoxmlRules.requireValidStyles(PremiumXlsxStyles.xml(AppLocaleState.currencySymbol)))
            sheets.forEachIndexed { index, pair -> entry("xl/worksheets/sheet${index + 1}.xml", pair.second) }
            entry("xl/worksheets/_rels/sheet1.xml.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/drawing\" Target=\"../drawings/drawing1.xml\"/></Relationships>")
            entry("xl/drawings/drawing1.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xdr:wsDr xmlns:xdr=\"http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing\" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"><xdr:twoCellAnchor><xdr:from><xdr:col>3</xdr:col><xdr:colOff>0</xdr:colOff><xdr:row>5</xdr:row><xdr:rowOff>0</xdr:rowOff></xdr:from><xdr:to><xdr:col>10</xdr:col><xdr:colOff>0</xdr:colOff><xdr:row>19</xdr:row><xdr:rowOff>0</xdr:rowOff></xdr:to><xdr:graphicFrame macro=\"\"><xdr:nvGraphicFramePr><xdr:cNvPr id=\"2\" name=\"Expense categories chart\"/><xdr:cNvGraphicFramePr/></xdr:nvGraphicFramePr><xdr:xfrm/><a:graphic><a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/chart\"><c:chart xmlns:c=\"http://schemas.openxmlformats.org/drawingml/2006/chart\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"rId1\"/></a:graphicData></a:graphic></xdr:graphicFrame><xdr:clientData/></xdr:twoCellAnchor></xdr:wsDr>")
            entry("xl/drawings/_rels/drawing1.xml.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/chart\" Target=\"../charts/chart1.xml\"/></Relationships>")
            val categoryLastRow = 5 + categoryRows.size
            entry("xl/charts/chart1.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><c:chartSpace xmlns:c=\"http://schemas.openxmlformats.org/drawingml/2006/chart\" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"><c:chart><c:autoTitleDeleted val=\"0\"/><c:title><c:tx><c:rich><a:bodyPr/><a:lstStyle/><a:p><a:r><a:rPr lang=\"en-US\" sz=\"1200\" b=\"1\"/><a:t>Expenses by category</a:t></a:r></a:p></c:rich></c:tx><c:layout/><c:overlay val=\"0\"/></c:title><c:plotArea><c:layout/><c:barChart><c:barDir val=\"col\"/><c:grouping val=\"clustered\"/><c:varyColors val=\"0\"/><c:ser><c:idx val=\"0\"/><c:order val=\"0\"/><c:tx><c:v>Expenses</c:v></c:tx><c:spPr><a:solidFill><a:srgbClr val=\"007C7A\"/></a:solidFill></c:spPr><c:cat><c:strRef><c:f>'Expense Categories'!\$A\$6:\$A\$$categoryLastRow</c:f></c:strRef></c:cat><c:val><c:numRef><c:f>'Expense Categories'!\$B\$6:\$B\$$categoryLastRow</c:f></c:numRef></c:val></c:ser><c:axId val=\"48650112\"/><c:axId val=\"48672768\"/></c:barChart><c:catAx><c:axId val=\"48650112\"/><c:scaling><c:orientation val=\"minMax\"/></c:scaling><c:delete val=\"0\"/><c:axPos val=\"b\"/><c:tickLblPos val=\"nextTo\"/><c:crossAx val=\"48672768\"/><c:crosses val=\"autoZero\"/></c:catAx><c:valAx><c:axId val=\"48672768\"/><c:scaling><c:orientation val=\"minMax\"/></c:scaling><c:delete val=\"0\"/><c:axPos val=\"l\"/><c:numFmt formatCode=\"#,##0\" sourceLinked=\"0\"/><c:tickLblPos val=\"nextTo\"/><c:crossAx val=\"48650112\"/><c:crosses val=\"autoZero\"/></c:valAx></c:plotArea><c:legend><c:legendPos val=\"b\"/><c:overlay val=\"0\"/></c:legend><c:plotVisOnly val=\"1\"/></c:chart></c:chartSpace>")
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
            PremiumPdfRenderer.write(context, output, text)
        }
}
