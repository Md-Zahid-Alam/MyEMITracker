package com.mdzahidalam.myfinancetracker

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
// EMI LIST
// ============================================================

@Composable
fun EmiList(
    viewModel: FinanceViewModel,
    search: String,
    sortMode: String,
    onOpen: (String) -> Unit
) {

    var pendingDelete by remember { mutableStateOf<EmiItem?>(null) }
    var pendingAction by remember { mutableStateOf<ConfirmationRequest?>(null) }
    var statusFilter by remember { mutableStateOf("Active") }
    val filteredItems = viewModel.data.emis.filter { item ->
        val statusMatches = when (statusFilter) {
            "Archived" -> item.archived
            "Completed" -> !item.archived && emiCompleted(item)
            else -> !item.archived && !emiCompleted(item)
        }
        statusMatches && (
            search.isBlank() ||
            listOf(item.name, item.category, item.seller).any {
                it.contains(search.trim(), ignoreCase = true)
            } || item.payments.any { it.notes.contains(search.trim(), ignoreCase = true) }
        )
    }
    val visibleItems = when (sortMode) {
        "Oldest first" -> filteredItems.sortedBy { it.startDate }
        "Highest amount" -> filteredItems.sortedByDescending { it.totalPayable }
        "Lowest amount" -> filteredItems.sortedBy { it.totalPayable }
        "Next due date" -> filteredItems.sortedBy { item ->
            item.payments.filter { it.paidDate == null }.minOfOrNull { it.dueDate } ?: Long.MAX_VALUE
        }
        else -> filteredItems.sortedByDescending { it.startDate }
    }

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .widthIn(max = FinanceLayout.formContentMax),

        contentPadding =
            PaddingValues(horizontal = FinanceSpacing.screen, vertical = FinanceSpacing.lg),

        verticalArrangement =
            Arrangement.spacedBy(FinanceSpacing.md)
    ) {

        item {

            Text(
                "EMI Plans",
                style =
                    MaterialTheme.typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )
        }

        item {
            StatusFilterRow(statusFilter) { statusFilter = it }
        }

        if (visibleItems.isEmpty()) {

            item {

                Text(
                    "No $statusFilter EMI plans."
                )
            }
        }

        items(visibleItems, key = { it.id }) { item ->

            val paid =
                item.payments.count {
                    it.paidDate != null
                }
            val progress =
                if (item.installments > 0) {
                    (
                            paid.toFloat() /
                                    item.installments
                            ).coerceIn(
                            0f,
                            1f
                        )
                } else {
                    0f
                }

            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    if (!item.archived && !emiCompleted(item)) {
                        pendingAction = ConfirmationRequest(
                            title = "Edit EMI?",
                            message = "Changes to installments, previous payments, amounts, or dates may rebuild this EMI payment schedule.",
                            confirmLabel = "Continue",
                            onConfirm = { onOpen(item.id) }
                        )
                    } else {
                        onOpen(item.id)
                    }
                }
            ) {

                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        val actions = buildList<Pair<String, () -> Unit>> {
                            if (emiCompleted(item) && !item.archived) {
                                add("Reopen" to {
                                pendingAction = ConfirmationRequest(
                                    title = "Reopen EMI?",
                                    message = "The latest payment will return to Pending, this EMI will move to Active, and reminders may resume.",
                                    confirmLabel = "Reopen",
                                    onConfirm = { viewModel.reopenEmi(item.id) }
                                )
                                })
                            }
                            add((if (item.archived) "Restore" else "Archive") to {
                                pendingAction = if (item.archived) {
                                    ConfirmationRequest(
                                        title = "Restore EMI?",
                                        message = "This EMI will return to Active or Completed according to its payment status. Reminders resume if payments are pending.",
                                        confirmLabel = "Restore",
                                        onConfirm = { viewModel.setEmiArchived(item.id, false) }
                                    )
                                } else {
                                    ConfirmationRequest(
                                        title = "Archive EMI?",
                                        message = "This EMI will leave normal lists and pending reminders will stop. Its history will remain available.",
                                        confirmLabel = "Archive",
                                        onConfirm = { viewModel.setEmiArchived(item.id, true) }
                                    )
                                }
                            })
                        }
                        PlanActionMenu(actions = actions, onDelete = { pendingDelete = item })
                    }
                    if (emiCompleted(item)) completionDate(item.payments)?.let { Text("Completed ${dateText(it)}", style = MaterialTheme.typography.bodySmall) }
                    Text("${item.category} • ${money(item.monthlyPayment)} / month")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$paid of ${item.installments} paid")
                        Text(
                            "${money(item.payments.filter { it.paidDate == null }.sumOf { it.amount })} remaining",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Text("${(progress * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    item.payments.filter { it.paidDate == null }.minOfOrNull { it.dueDate }?.let {
                        Text("Next payment: ${dateText(it)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    pendingDelete?.let { item ->
        DeleteConfirmationDialog(
            itemType = "EMI purchase",
            itemName = item.name,
            onConfirm = {
                viewModel.deleteEmi(item.id)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }

    pendingAction?.let { request ->
        ConfirmationDialog(request) { pendingAction = null }
    }

}

@Composable
fun MonthYearPickerDialog(
    currentMonth: Long,
    onAllMonths: () -> Unit,
    onMonthSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val initialYear = Calendar.getInstance().apply { timeInMillis = currentMonth }.get(Calendar.YEAR)
    var year by remember(currentMonth) { mutableStateOf(initialYear) }
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select period") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onAllMonths, modifier = Modifier.fillMaxWidth()) { Text("All Months") }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = { year-- }) { Icon(Icons.Default.KeyboardArrowLeft, "Previous year") }
                    Text(year.toString(), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { year++ }) { Icon(Icons.Default.KeyboardArrowRight, "Next year") }
                }
                months.chunked(3).forEachIndexed { rowIndex, rowMonths ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        rowMonths.forEachIndexed { columnIndex, label ->
                            val monthIndex = rowIndex * 3 + columnIndex
                            OutlinedButton(
                                onClick = {
                                    onMonthSelected(Calendar.getInstance().apply {
                                        set(year, monthIndex, 1, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }.timeInMillis)
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) { Text(label) }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}


// ============================================================
// LOAN LIST
// ============================================================

@Composable
fun LoanList(
    viewModel: FinanceViewModel,
    search: String,
    sortMode: String,
    onOpen: (String) -> Unit
) {

    var pendingDelete by remember { mutableStateOf<Loan?>(null) }
    var pendingAction by remember { mutableStateOf<ConfirmationRequest?>(null) }
    var statusFilter by remember { mutableStateOf("Active") }
    val filteredItems = viewModel.data.loans.filter { item ->
        val statusMatches = when (statusFilter) {
            "Archived" -> item.archived
            "Completed" -> !item.archived && loanCompleted(item)
            else -> !item.archived && !loanCompleted(item)
        }
        statusMatches && (
            search.isBlank() ||
            listOf(item.name, item.type, item.lender).any {
                it.contains(search.trim(), ignoreCase = true)
            } || item.payments.any { it.notes.contains(search.trim(), ignoreCase = true) }
        )
    }
    val visibleItems = when (sortMode) {
        "Oldest first" -> filteredItems.sortedBy { it.startDate }
        "Highest amount" -> filteredItems.sortedByDescending { it.totalPayable }
        "Lowest amount" -> filteredItems.sortedBy { it.totalPayable }
        "Next due date" -> filteredItems.sortedBy { item ->
            item.payments.filter { it.paidDate == null }.minOfOrNull { it.dueDate } ?: Long.MAX_VALUE
        }
        else -> filteredItems.sortedByDescending { it.startDate }
    }

    LazyColumn(

        modifier =
            Modifier.fillMaxSize(),

        contentPadding =
            PaddingValues(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        item {

            Text(
                "Loans",
                style =
                    MaterialTheme.typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )
        }

        item {
            StatusFilterRow(statusFilter) { statusFilter = it }
        }

        if (visibleItems.isEmpty()) {

            item {

                Text(
                    "No $statusFilter loans."
                )
            }
        }

        items(visibleItems, key = { it.id }) { item ->

            val paid =
                item.payments.count {
                    it.paidDate != null
                }
            val progress = if (item.installments > 0) (paid.toFloat() / item.installments).coerceIn(0f, 1f) else 0f

            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    if (!item.archived && !loanCompleted(item)) {
                        pendingAction = ConfirmationRequest(
                            title = "Edit Loan?",
                            message = "Changes to repayments, previous payments, amounts, or dates may rebuild this loan repayment schedule.",
                            confirmLabel = "Continue",
                            onConfirm = { onOpen(item.id) }
                        )
                    } else {
                        onOpen(item.id)
                    }
                }
            ) {

                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        val actions = buildList<Pair<String, () -> Unit>> {
                            if (loanCompleted(item) && !item.archived) {
                                add("Reopen" to {
                                pendingAction = ConfirmationRequest(
                                    title = "Reopen Loan?",
                                    message = "The latest repayment will return to Pending, this loan will move to Active, and reminders may resume.",
                                    confirmLabel = "Reopen",
                                    onConfirm = { viewModel.reopenLoan(item.id) }
                                )
                                })
                            }
                            add((if (item.archived) "Restore" else "Archive") to {
                                pendingAction = if (item.archived) {
                                    ConfirmationRequest(
                                        title = "Restore Loan?",
                                        message = "This loan will return to Active or Completed according to its repayment status. Reminders resume if payments are pending.",
                                        confirmLabel = "Restore",
                                        onConfirm = { viewModel.setLoanArchived(item.id, false) }
                                    )
                                } else {
                                    ConfirmationRequest(
                                        title = "Archive Loan?",
                                        message = "This loan will leave normal lists and pending reminders will stop. Its history will remain available.",
                                        confirmLabel = "Archive",
                                        onConfirm = { viewModel.setLoanArchived(item.id, true) }
                                    )
                                }
                            })
                        }
                        PlanActionMenu(actions = actions, onDelete = { pendingDelete = item })
                    }
                    if (loanCompleted(item)) completionDate(item.payments)?.let { Text("Completed ${dateText(it)}", style = MaterialTheme.typography.bodySmall) }
                    Text("${item.type} • ${item.lender} • ${money(item.monthlyPayment)} / month")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$paid of ${item.installments} paid")
                        Text("${money(item.payments.filter { it.paidDate == null }.sumOf { it.amount })} remaining", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Text("${(progress * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    item.payments.filter { it.paidDate == null }.minOfOrNull { it.dueDate }?.let { Text("Next payment: ${dateText(it)}", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }

    pendingDelete?.let { item ->
        DeleteConfirmationDialog(
            itemType = "loan",
            itemName = item.name,
            onConfirm = {
                viewModel.deleteLoan(item.id)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }

    pendingAction?.let { request ->
        ConfirmationDialog(request) { pendingAction = null }
    }

}


// ============================================================
// DEBT LIST
// ============================================================

@Composable
fun DebtList(
    viewModel: FinanceViewModel,
    search: String,
    sortMode: String,
    direction: String,
    onOpen: (String) -> Unit
) {

    var pendingDelete by remember { mutableStateOf<Debt?>(null) }
    var pendingAction by remember { mutableStateOf<ConfirmationRequest?>(null) }
    var statusFilter by remember { mutableStateOf("Active") }
    val filteredItems = viewModel.data.debts.filter { item ->
        val statusMatches = when (statusFilter) {
            "Archived" -> item.archived
            "Completed" -> !item.archived && debtCompleted(item)
            else -> !item.archived && !debtCompleted(item)
        }
        statusMatches && item.direction == direction && (
            search.isBlank() ||
            listOf(item.name, item.direction, item.notes).any {
                it.contains(search.trim(), ignoreCase = true)
            } || item.payments.any { it.notes.contains(search.trim(), ignoreCase = true) }
        )
    }
    val visibleItems = when (sortMode) {
        "Oldest first" -> filteredItems.sortedBy { it.payments.minOfOrNull { payment -> payment.dueDate } ?: 0L }
        "Highest amount" -> filteredItems.sortedByDescending { it.originalAmount }
        "Lowest amount" -> filteredItems.sortedBy { it.originalAmount }
        "Next due date" -> filteredItems.sortedBy { item -> item.dueDate ?: Long.MAX_VALUE }
        else -> filteredItems.sortedByDescending { it.payments.maxOfOrNull { payment -> payment.dueDate } ?: 0L }
    }

    LazyColumn(

        modifier =
            Modifier.fillMaxSize(),

        contentPadding =
            PaddingValues(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        item {
            val directionTotal = viewModel.data.debts.filter { !it.archived && it.direction == direction }.sumOf { debtRemainingAmount(it) }
            Text(if (direction == "I Owe") "Money I Owe" else "Money Owed to Me", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            SummaryCard(if (direction == "I Owe") "Total to pay" else "Total to receive", money(directionTotal), Modifier.fillMaxWidth())
        }

        item {
            StatusFilterRow(statusFilter) { statusFilter = it }
        }

        if (visibleItems.isEmpty()) {

            item {

                Text(
                    "No $statusFilter records."
                )
            }
        }

        items(visibleItems, key = { it.id }) { item ->

            val paid = debtPaidAmount(item)

            val remaining = debtRemainingAmount(item)

            val progress =
                if (item.originalAmount > 0) {
                    (
                            paid /
                                    item.originalAmount
                            )
                        .toFloat()
                        .coerceIn(
                            0f,
                            1f
                        )
                } else {
                    0f
                }

            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    if (!item.archived && !debtCompleted(item)) {
                        pendingAction = ConfirmationRequest(
                            title = "Edit Debt?",
                            message = "You are opening an active debt record where payments and notes can be changed.",
                            confirmLabel = "Continue",
                            onConfirm = { onOpen(item.id) }
                        )
                    } else {
                        onOpen(item.id)
                    }
                }
            ) {

                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        val actions = buildList<Pair<String, () -> Unit>> {
                            if (debtCompleted(item) && !item.archived) {
                                add("Reopen" to {
                                pendingAction = ConfirmationRequest(
                                    title = "Reopen Debt?",
                                    message = "The latest payment will return to Pending and this debt will move back to Active.",
                                    confirmLabel = "Reopen",
                                    onConfirm = { viewModel.reopenDebt(item.id) }
                                )
                                })
                            }
                            add((if (item.archived) "Restore" else "Archive") to {
                                pendingAction = if (item.archived) {
                                    ConfirmationRequest(
                                        title = "Restore Debt?",
                                        message = "This debt will return to Active or Completed according to its payment status.",
                                        confirmLabel = "Restore",
                                        onConfirm = { viewModel.setDebtArchived(item.id, false) }
                                    )
                                } else {
                                    ConfirmationRequest(
                                        title = "Archive Debt?",
                                        message = "This debt will leave normal lists, but its complete payment history will remain available.",
                                        confirmLabel = "Archive",
                                        onConfirm = { viewModel.setDebtArchived(item.id, true) }
                                    )
                                }
                            })
                        }
                        PlanActionMenu(actions = actions, onDelete = { pendingDelete = item })
                    }
                    if (debtCompleted(item)) completionDate(item.payments)?.let { Text("Completed ${dateText(it)}", style = MaterialTheme.typography.bodySmall) }
                    Text(
                        if (item.direction == "I Owe") "↑ YOU NEED TO PAY" else "↓ YOU NEED TO RECEIVE",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Original ${money(item.originalAmount)}")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (item.direction == "I Owe") "Paid ${money(paid)}" else "Received ${money(paid)}")
                        Text(
                            if (item.direction == "I Owe") "${money(remaining)} to pay" else "${money(remaining)} to receive",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Text("${(progress * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    item.dueDate?.let { Text("Due: ${dateText(it)}", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }

    pendingDelete?.let { item ->
        DeleteConfirmationDialog(
            itemType = "debt",
            itemName = item.name,
            onConfirm = {
                viewModel.deleteDebt(item.id)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }

    pendingAction?.let { request ->
        ConfirmationDialog(request) { pendingAction = null }
    }
}


@Composable
fun DeleteConfirmationDialog(
    itemType: String,
    itemName: String,
    message: String = "Delete \"$itemName\" and all of its payment history? This cannot be undone.",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete $itemType?") },
        text = {
            Text(message)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


