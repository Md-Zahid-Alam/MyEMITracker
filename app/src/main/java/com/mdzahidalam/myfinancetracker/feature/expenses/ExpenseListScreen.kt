package com.mdzahidalam.myfinancetracker.feature.expenses
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
// EXPENSES
// ============================================================

@Composable
fun ExpenseList(
    viewModel: FinanceViewModel,
    onOpen: (String) -> Unit
) {
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf("DAILY") }
    var pendingDelete by remember { mutableStateOf<Expense?>(null) }
    var pendingAction by remember { mutableStateOf<ConfirmationRequest?>(null) }
    var allMonths by remember { mutableStateOf(false) }
    var selectedMonth by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }
    var search by remember { mutableStateOf("") }
    var sortMode by remember { mutableStateOf("Newest first") }
    var searchVisible by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }
    var categorySummaryExpanded by remember { mutableStateOf(false) }
    var categoryFilter by remember { mutableStateOf("All") }
    var expandedPeriods by remember { mutableStateOf(emptySet<String>()) }
    var monthPickerVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var viewingDocuments by remember { mutableStateOf<Expense?>(null) }

    val sorted = when (sortMode) {
        "Oldest first" -> viewModel.data.expenses.sortedBy { it.date }
        "Highest amount" -> viewModel.data.expenses.sortedByDescending { it.amount }
        "Lowest amount" -> viewModel.data.expenses.sortedBy { it.amount }
        else -> viewModel.data.expenses.sortedByDescending { it.date }
    }
    val availableCategories = listOf("All") + sorted.map { it.category }.distinct().sorted()
    val selectedPeriodExpenses = if (selectedDate != null) {
        val target = Calendar.getInstance().apply { timeInMillis = selectedDate!! }
        sorted.filter { expense ->
            val value = Calendar.getInstance().apply { timeInMillis = expense.date }
            target.get(Calendar.YEAR) == value.get(Calendar.YEAR) &&
                target.get(Calendar.DAY_OF_YEAR) == value.get(Calendar.DAY_OF_YEAR)
        }
    } else if (allMonths) {
        sorted
    } else {
        val selectedCalendar = Calendar.getInstance().apply { timeInMillis = selectedMonth }
        sorted.filter { expense ->
            val expenseCalendar = Calendar.getInstance().apply { timeInMillis = expense.date }
            selectedCalendar.get(Calendar.YEAR) == expenseCalendar.get(Calendar.YEAR) &&
                    selectedCalendar.get(Calendar.MONTH) == expenseCalendar.get(Calendar.MONTH)
        }
    }
    val visibleExpenses = selectedPeriodExpenses.filter { expense ->
        val matchesCategory = categoryFilter == "All" || expense.category == categoryFilter
        val query = search.trim()
        val matchesSearch = query.isBlank() ||
                expense.title.contains(query, ignoreCase = true) ||
                expense.category.contains(query, ignoreCase = true) ||
                expense.notes.contains(query, ignoreCase = true)
        matchesCategory && matchesSearch
    }
    val categoryTotals = selectedPeriodExpenses
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
    val grouped = visibleExpenses.groupBy {
        if (viewMode == "DAILY") expenseDayKey(it.date) else expenseMonthKey(it.date)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Expenses",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { searchVisible = !searchVisible }) {
                    Icon(if (searchVisible) Icons.Default.Close else Icons.Default.Search, "Search")
                }
                Box {
                    IconButton(onClick = { sortExpanded = true }) { Icon(Icons.Default.Sort, "Sort") }
                    SortMenu(
                        expanded = sortExpanded,
                        selected = sortMode,
                        options = listOf("Newest first", "Oldest first", "Highest amount", "Lowest amount"),
                        onSelect = { sortMode = it; sortExpanded = false },
                        onDismiss = { sortExpanded = false }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactTabButton(
                    label = "Daily",
                    selected = viewMode == "DAILY",
                    onClick = { viewMode = "DAILY" },
                    modifier = Modifier.weight(1f)
                )
                CompactTabButton(
                    label = "Monthly",
                    selected = viewMode == "MONTHLY",
                    onClick = { viewMode = "MONTHLY" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        selectedMonth = addMonths(selectedMonth, -1)
                        allMonths = false
                        selectedDate = null
                        expandedPeriods = emptySet()
                    }
                ) { Icon(Icons.Default.KeyboardArrowLeft, "Previous month") }

                TextButton(
                    onClick = { monthPickerVisible = true },
                    modifier = Modifier.weight(1f)
                ) { Text(selectedDate?.let { expenseDayKey(it) } ?: if (allMonths) "All Months" else expenseMonthKey(selectedMonth), fontWeight = FontWeight.Bold) }

                IconButton(
                    onClick = {
                        selectedMonth = addMonths(selectedMonth, 1)
                        allMonths = false
                        selectedDate = null
                        expandedPeriods = emptySet()
                    }
                ) { Icon(Icons.Default.KeyboardArrowRight, "Next month") }

                IconButton(onClick = {
                    val initial = Calendar.getInstance().apply { timeInMillis = selectedDate ?: selectedMonth }
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            selectedDate = Calendar.getInstance().apply {
                                set(year, month, day, 0, 0, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis
                            allMonths = false
                            expandedPeriods = emptySet()
                        },
                        initial.get(Calendar.YEAR),
                        initial.get(Calendar.MONTH),
                        initial.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) { Icon(Icons.Default.DateRange, "Select specific date") }
            }
        }

        if (selectedDate != null) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Showing one day", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = { selectedDate = null }) { Text("Clear date") }
                }
            }
        }

        if (searchVisible) {
            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Search expenses") },
                    singleLine = true,
                    trailingIcon = {
                        if (search.isNotBlank()) {
                            IconButton(onClick = { search = "" }) { Icon(Icons.Default.Close, "Clear") }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            ChoiceDropdown(
                label = "Category filter",
                value = categoryFilter,
                options = availableCategories,
                onSelect = { categoryFilter = it }
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(if (selectedDate != null) "Spent this day" else if (allMonths) "Total spending" else "Spent this month")
                    Text(
                        money(selectedPeriodExpenses.sumOf { it.amount }),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { categorySummaryExpanded = !categorySummaryExpanded }) {
                        Text(if (categorySummaryExpanded) "Hide category summary" else "View category summary")
                    }
                    if (categorySummaryExpanded) {
                        if (categoryTotals.isEmpty()) {
                            Text("No expenses recorded for this period.")
                        } else {
                            categoryTotals.forEach { (category, total) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(category)
                                    Text(money(total), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (visibleExpenses.isEmpty()) {
            item {
                Text(
                    if (selectedPeriodExpenses.isEmpty()) {
                        "No expenses for this period. Tap + to add one."
                    } else {
                        "No expenses match the current search or category filter."
                    }
                )
            }
        }

        grouped.forEach { (period, entries) ->
            item(key = "period-$period") {
                OutlinedButton(
                    onClick = {
                        expandedPeriods = if (period in expandedPeriods) {
                            expandedPeriods - period
                        } else {
                            expandedPeriods + period
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "${if (period in expandedPeriods) "-" else "+"} $period - ${money(entries.sumOf { it.amount })}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (period in expandedPeriods) {
                items(entries, key = { it.id }) { expense ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(expense.title, fontWeight = FontWeight.Bold)
                                    Text(expense.category)
                                    Text(expenseDayKey(expense.date))
                                }
                                Text(
                                    money(expense.amount),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (expense.notes.isNotBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(expense.notes)
                            }
                            if (expense.attachments.isNotEmpty()) {
                                Text("${expense.attachments.size} document(s) attached", color = MaterialTheme.colorScheme.primary)
                            }

                            Row {
                                if (expense.attachments.isNotEmpty()) {
                                    TextButton(onClick = { viewingDocuments = expense }) {
                                        Text(if (expense.attachments.size == 1) "View Document" else "View Documents")
                                    }
                                }
                                TextButton(onClick = {
                                    pendingAction = ConfirmationRequest(
                                        title = "Edit Expense?",
                                        message = "You are about to change ${expense.title} for ${money(expense.amount)}.",
                                        confirmLabel = "Edit",
                                        onConfirm = { onOpen(expense.id) }
                                    )
                                }) {
                                    Text("Edit")
                                }
                                TextButton(onClick = { pendingDelete = expense }) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { expense ->
        DeleteConfirmationDialog(
            itemType = "expense",
            itemName = expense.title,
            message = "Delete \"${expense.title}\" for ${money(expense.amount)}? This cannot be undone.",
            onConfirm = {
                viewModel.deleteExpense(expense.id)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }

    pendingAction?.let { request ->
        ConfirmationDialog(request) { pendingAction = null }
    }

    viewingDocuments?.let { expense ->
        AlertDialog(
            onDismissRequest = { viewingDocuments = null },
            title = { Text("${expense.title} Documents") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    expense.attachments.forEach { attachment ->
                        OutlinedButton(onClick = { runCatching { openAttachment(context, attachment) } }, modifier = Modifier.fillMaxWidth()) {
                            Text("Open ${attachment.name}")
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { viewingDocuments = null }) { Text("Close") } }
        )
    }

    if (monthPickerVisible) {
        MonthYearPickerDialog(
            currentMonth = selectedMonth,
            onAllMonths = {
                allMonths = true
                selectedDate = null
                expandedPeriods = emptySet()
                monthPickerVisible = false
            },
            onMonthSelected = { value ->
                selectedMonth = value
                allMonths = false
                selectedDate = null
                expandedPeriods = emptySet()
                monthPickerVisible = false
            },
            onDismiss = { monthPickerVisible = false }
        )
    }
}


