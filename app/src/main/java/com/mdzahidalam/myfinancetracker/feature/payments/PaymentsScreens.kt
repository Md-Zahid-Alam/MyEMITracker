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
// PAYMENTS HUB
// ============================================================

@Composable
fun PaymentsHub(
    viewModel: FinanceViewModel,
    section: String,
    onSectionChange: (String) -> Unit,
    onOpen: (String, String) -> Unit
) {
    if (section.isBlank()) {
        PaymentsLanding(viewModel = viewModel, onOpenSection = onSectionChange)
        return
    }
    var search by remember { mutableStateOf("") }
    var sortMode by remember { mutableStateOf("Newest first") }
    var searchVisible by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onSectionChange("") }) {
                Icon(Icons.Default.ArrowBack, "Back to Payments")
            }
            Text(
                when (section) {
                    "EMI" -> "EMI Plans"
                    "Loans" -> "Loans"
                    "DebtsOwe" -> "Money I Owe"
                    else -> "Money Owed to Me"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { searchVisible = !searchVisible }) {
                Icon(if (searchVisible) Icons.Default.Close else Icons.Default.Search, "Search")
            }
            Box {
                IconButton(onClick = { sortExpanded = true }) {
                    Icon(Icons.Default.Sort, "Sort")
                }
                SortMenu(
                    expanded = sortExpanded,
                    selected = sortMode,
                    options = listOf("Newest first", "Oldest first", "Highest amount", "Lowest amount", "Next due date"),
                    onSelect = { sortMode = it; sortExpanded = false },
                    onDismiss = { sortExpanded = false }
                )
            }
        }

        if (searchVisible) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search ${section.lowercase()}") },
                singleLine = true,
                trailingIcon = {
                    if (search.isNotBlank()) {
                        IconButton(onClick = { search = "" }) { Icon(Icons.Default.Close, "Clear") }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (section) {
                "EMI" -> EmiList(viewModel, search, sortMode) { onOpen("emi_detail", it) }
                "Loans" -> LoanList(viewModel, search, sortMode) { onOpen("loan_detail", it) }
                "DebtsOwe" -> DebtList(viewModel, search, sortMode, "I Owe") { onOpen("debt_detail", it) }
                else -> DebtList(viewModel, search, sortMode, "Owed to Me") { onOpen("debt_detail", it) }
            }
        }
    }
}

@Composable
fun PaymentsLanding(viewModel: FinanceViewModel, onOpenSection: (String) -> Unit) {
    var debtsExpanded by remember { mutableStateOf(false) }
    val activeEmis = viewModel.data.emis.filter { !it.archived && !emiCompleted(it) }
    val activeLoans = viewModel.data.loans.filter { !it.archived && !loanCompleted(it) }
    val activeDebts = viewModel.data.debts.filter { !it.archived && !debtCompleted(it) }
    val debtToPay = activeDebts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) }
    val moneyToReceive = activeDebts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = FinanceSpacing.screen, vertical = FinanceSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FinanceSpacing.md)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Payments", style = MaterialTheme.typography.headlineLarge)
                Text("Manage instalments, loans and personal balances.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            PaymentSectionCard(
                title = "EMI Plans",
                summary = "${activeEmis.size} active • ${money(activeEmis.sumOf { emi -> emi.payments.filter { it.paidDate == null }.sumOf { it.amount } })} left",
                onClick = { onOpenSection("EMI") }
            )
        }
        item {
            PaymentSectionCard(
                title = "Loans",
                summary = "${activeLoans.size} active • ${money(activeLoans.sumOf { loan -> loan.payments.filter { it.paidDate == null }.sumOf { it.amount } })} left",
                onClick = { onOpenSection("Loans") }
            )
        }
        item {
            PaymentSectionCard("Debts", "Pay ${money(debtToPay)} • Receive ${money(moneyToReceive)}") { debtsExpanded = !debtsExpanded }
        }
        if (debtsExpanded) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(Modifier.padding(FinanceSpacing.sm), verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)) {
                        OutlinedButton(onClick = { onOpenSection("DebtsOwe") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Money I Owe • ${activeDebts.count { it.direction == "I Owe" }} active • ${money(debtToPay)}")
                        }
                        OutlinedButton(onClick = { onOpenSection("DebtsOwed") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Money Owed to Me • ${activeDebts.count { it.direction == "Owed to Me" }} active • ${money(moneyToReceive)}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentPlanDetail(
    viewModel: FinanceViewModel,
    kind: String,
    id: String,
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    var showRequestDialog by remember { mutableStateOf(false) }
    var editingRequest by remember { mutableStateOf<PaymentRequest?>(null) }
    var cancellingRequest by remember { mutableStateOf<PaymentRequest?>(null) }
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    val name = emi?.name ?: loan?.name ?: debt?.name ?: "Plan"
    val payments = emi?.payments ?: loan?.payments ?: debt?.payments ?: emptyList()
    val total = emi?.totalPayable ?: loan?.totalPayable ?: debt?.originalAmount ?: 0.0
    val paid = payments.filter { it.paidDate != null }.sumOf { it.amount }
    val archived = emi?.archived ?: loan?.archived ?: debt?.archived ?: false
    val completed = paid + 0.005 >= total
    val documents = emi?.attachments ?: loan?.attachments ?: debt?.attachments ?: emptyList()
    val typeName = when (kind) { "emi" -> "EMI"; "loan" -> "Loan"; else -> "Debt" }

    FormColumn(title = "$typeName Details", onBack = onBack, readOnly = true) {
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(when { archived -> "Archived"; completed -> "Completed"; else -> "Active" }, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Total: ${money(total)}")
                Text("Paid: ${money(paid)}")
                Text("Remaining: ${money(max(0.0, total - paid))}", fontWeight = FontWeight.Bold)
                if (debt != null) Text(if (debt.direction == "I Owe") "You owe ${debt.name}" else "${debt.name} owes you")
            }
        }
        if (!archived && !completed) {
            Button(onClick = { onOpen("payment") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (debt?.direction == "Owed to Me") "Record Received Amount" else "Record Payment")
            }
            OutlinedButton(onClick = { onOpen("edit") }, modifier = Modifier.fillMaxWidth()) { Text("Edit Plan Information") }
            if (debt?.direction == "Owed to Me") {
                OutlinedButton(onClick = { showRequestDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("Create Payment Request") }
            }
        } else {
            Text("This record is view-only. Reopen or restore it from the plan menu before making changes.")
        }
        DetailNavigationButton("Payment History", "${payments.count { it.paidDate != null }} recorded") { onOpen("history") }
        DetailNavigationButton("Plan Information", "View all original plan details") { onOpen("information") }
        DetailNavigationButton("Documents", if (documents.isEmpty()) "No documents" else "${documents.size} attached") { onOpen("documents") }
        DetailNavigationButton("Financing Information", "Source, method, reference and notes") { onOpen("financing") }
        if (debt != null && debt.paymentRequests.isNotEmpty()) {
            Text("Payment Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            debt.paymentRequests.sortedByDescending { it.createdDate }.forEach { request ->
                PaymentRequestCard(
                    debt, request, viewModel.data.receiptProfile,
                    onEdit = { editingRequest = request },
                    onCancel = { cancellingRequest = request }
                )
            }
        }
    }
    if (showRequestDialog && debt != null) {
        PaymentRequestDialog(debt, onSave = { viewModel.addPaymentRequest(debt.id, it); showRequestDialog = false }, onDismiss = { showRequestDialog = false })
    }
    if (editingRequest != null && debt != null) {
        PaymentRequestDialog(debt, existing = editingRequest, onSave = { viewModel.updatePaymentRequest(debt.id, it); editingRequest = null }, onDismiss = { editingRequest = null })
    }
    cancellingRequest?.let { request ->
        ConfirmationDialog(
            ConfirmationRequest(
                title = "Cancel Payment Request?",
                message = "${request.requestNumber} will remain in your records but cannot receive further payments. This action cannot be undone.",
                confirmLabel = "Cancel Request",
                onConfirm = { debt?.let { viewModel.cancelPaymentRequest(it.id, request.id) } }
            )
        ) { cancellingRequest = null }
    }
}

@Composable
internal fun DetailNavigationButton(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier.padding(horizontal = FinanceSpacing.md, vertical = FinanceSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Open $title", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun PaymentPlanHistory(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    val payments = emi?.payments ?: loan?.payments ?: debt?.payments ?: emptyList()
    val name = emi?.name ?: loan?.name ?: debt?.name ?: "Payment"
    val total = emi?.totalPayable ?: loan?.totalPayable ?: debt?.originalAmount ?: 0.0
    FormColumn(title = "Payment History", onBack = onBack, readOnly = false) {
        PaymentHistory(
            payments = payments,
            onUpdate = when (kind) {
                "emi" -> ({ payment -> viewModel.updateEmiPayment(id, payment) })
                "loan" -> ({ payment -> viewModel.updateLoanPayment(id, payment) })
                else -> ({ payment -> viewModel.updateDebtPayment(id, payment) })
            },
            planName = name,
            direction = debt?.direction ?: "I Owe",
            planTotal = total,
            profile = viewModel.data.receiptProfile
        )
    }
}

@Composable
fun PaymentPlanDocuments(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val documents = when (kind) {
        "emi" -> viewModel.data.emis.find { it.id == id }?.attachments
        "loan" -> viewModel.data.loans.find { it.id == id }?.attachments
        else -> viewModel.data.debts.find { it.id == id }?.attachments
    } ?: emptyList()
    FormColumn(title = "Documents", onBack = onBack, readOnly = true) {
        if (documents.isEmpty()) Text("No supporting documents are attached.")
        documents.forEach { attachment ->
            OutlinedButton(onClick = { runCatching { openAttachment(context, attachment) } }, modifier = Modifier.fillMaxWidth()) {
                Text("Open ${attachment.name}")
            }
        }
    }
}

@Composable
fun PaymentPlanFinancing(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    FormColumn(title = "Financing Information", onBack = onBack, readOnly = true) {
        if (emi != null) {
            InfoRow("Source", emi.financingSource)
            InfoRow("How received", emi.receivedMethod)
            InfoRow("Seller / provider", emi.seller)
            InfoRow("Agreement reference", emi.agreementReference)
            InfoRow("Notes", emi.financingNotes)
            FinancingMethodInfo(emi.financingChannel, emi.financingAccountName, emi.financingAccountNumber, emi.financingBranch, emi.financingRoutingNumber, emi.financingReference, emi.financingMethodDetails)
        } else if (loan != null) {
            InfoRow("Source", loan.financingSource)
            InfoRow("How received", loan.receivedMethod)
            InfoRow("Lender", loan.lender)
            InfoRow("Agreement reference", loan.agreementReference)
            InfoRow("Notes", loan.financingNotes)
            FinancingMethodInfo(loan.financingChannel, loan.financingAccountName, loan.financingAccountNumber, loan.financingBranch, loan.financingRoutingNumber, loan.financingReference, loan.financingMethodDetails)
        } else if (debt != null) {
            InfoRow("Direction", debt.direction)
            InfoRow("Reason", debt.reason)
            InfoRow("How received / given", debt.receivedOrGivenMethod)
            InfoRow("Agreement reference", debt.referenceNumber)
            InfoRow("Notes", debt.notes)
            FinancingMethodInfo(debt.financingChannel, debt.financingAccountName, debt.financingAccountNumber, debt.financingBranch, debt.financingRoutingNumber, debt.financingReference, debt.financingMethodDetails)
        } else Text("Record not found.")
    }
}

@Composable
internal fun FinancingMethodInfo(channel: String, accountName: String, accountNumber: String, branch: String, routing: String, reference: String, details: String) {
    if (channel.isNotBlank()) InfoRow("Provider / bank", channel)
    if (accountName.isNotBlank()) InfoRow("Account holder / party", accountName)
    if (accountNumber.isNotBlank()) InfoRow("Account / mobile number", accountNumber)
    if (branch.isNotBlank()) InfoRow("Branch", branch)
    if (routing.isNotBlank()) InfoRow("Routing number", routing)
    if (reference.isNotBlank()) InfoRow("Transaction reference", reference)
    if (details.isNotBlank()) InfoRow("Method details", details)
}

@Composable
fun PaymentPlanInformation(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    FormColumn(title = "Plan Information", onBack = onBack, readOnly = true) {
        when {
            emi != null -> {
                InfoRow("Item name", emi.name); InfoRow("Category", emi.category); InfoRow("Seller / provider", emi.seller)
                InfoRow("Purchase price", money(emi.price)); InfoRow("Down payment", money(emi.downPayment)); InfoRow("Financed amount", money(emi.financedAmount))
                InfoRow("Interest rate", "${emi.interestRate}%"); InfoRow("Interest amount", money(emi.interestAmount)); InfoRow("Total payable", money(emi.totalPayable))
                InfoRow("Installments", emi.installments.toString()); InfoRow("Monthly payment", money(emi.monthlyPayment)); InfoRow("Start date", dateText(emi.startDate))
                InfoRow("Due day", emi.dueDay.toString()); InfoRow("Reminder days", emi.reminderDays.joinToString(", ")); InfoRow("Status", if (emi.archived) "Archived" else if (emiCompleted(emi)) "Completed" else "Active")
            }
            loan != null -> {
                InfoRow("Loan name", loan.name); InfoRow("Loan type", loan.type); InfoRow("Lender", loan.lender)
                InfoRow("Principal", money(loan.principal)); InfoRow("Interest rate", "${loan.interestRate}%"); InfoRow("Interest amount", money(loan.interestAmount)); InfoRow("Total payable", money(loan.totalPayable))
                InfoRow("Repayment mode", loan.repaymentMode); InfoRow("Installments", loan.installments.toString()); InfoRow("Monthly payment", money(loan.monthlyPayment)); InfoRow("Start date", dateText(loan.startDate))
                InfoRow("Due day", loan.dueDay.toString()); InfoRow("Reminder days", loan.reminderDays.joinToString(", ")); InfoRow("Status", if (loan.archived) "Archived" else if (loanCompleted(loan)) "Completed" else "Active")
            }
            debt != null -> {
                InfoRow("Person / organization", debt.name); InfoRow("Direction", debt.direction); InfoRow("Debt date", dateText(debt.debtDate)); InfoRow("Original amount", money(debt.originalAmount))
                InfoRow("Paid / received", money(debtPaidAmount(debt))); InfoRow("Remaining", money(debtRemainingAmount(debt))); InfoRow("Due date", debt.dueDate?.let { dateText(it) } ?: "Not specified")
                InfoRow("Reason", debt.reason); InfoRow("Notes", debt.notes); InfoRow("Status", if (debt.archived) "Archived" else if (debtCompleted(debt)) "Completed" else "Active")
            }
            else -> Text("Record not found.")
        }
    }
}

@Composable
internal fun InfoRow(label: String, value: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            Text(value.ifBlank { "Not recorded" }, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PaymentPlanPayment(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    val pending = (emi?.payments ?: loan?.payments ?: emptyList()).firstOrNull { it.paidDate == null }
    FormColumn(title = if (debt?.direction == "Owed to Me") "Receive Payment" else "Record Payment", onBack = onBack, readOnly = false) {
        if (debt != null && debtCompleted(debt)) {
            Text("This debt is fully completed. No additional payment can be recorded.", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        } else if (debt != null) {
            DebtPaymentEntry(viewModel, debt, onSaved = onBack)
        } else if (pending == null) {
            Text("There is no pending payment for this plan.")
        } else {
            Text("Next installment: ${money(pending.amount)}", fontWeight = FontWeight.Bold)
            Text("Due ${dateText(pending.dueDate)}")
            PaymentHistory(
                payments = listOf(pending),
                onUpdate = if (kind == "emi") ({ p -> viewModel.updateEmiPayment(id, p); onBack() }) else ({ p -> viewModel.updateLoanPayment(id, p); onBack() }),
                planName = emi?.name ?: loan?.name ?: "Payment",
                planTotal = emi?.totalPayable ?: loan?.totalPayable ?: 0.0,
                profile = viewModel.data.receiptProfile
            )
        }
    }
}

@Composable
internal fun DebtPaymentEntry(viewModel: FinanceViewModel, debt: Debt, onSaved: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var paidDate by remember { mutableStateOf(expenseDateText(System.currentTimeMillis())) }
    var method by remember { mutableStateOf("Cash") }
    var channel by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf(debt.name) }
    var accountNumber by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var routingNumber by remember { mutableStateOf("") }
    var methodDetails by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(emptyList<Attachment>()) }
    var error by remember { mutableStateOf("") }
    val remaining = debtRemainingAmount(debt)
    val openRequests = debt.paymentRequests.filter { it.status in listOf("UNPAID", "PARTIALLY PAID") }
    val requestOptions = listOf("No payment request") + openRequests.map { "${it.requestNumber} • ${money(it.amount - it.receivedAmount)} left" }
    var requestSelection by remember(debt.id, openRequests.map { it.id to it.status }) {
        mutableStateOf(if (openRequests.size == 1) requestOptions[1] else "No payment request")
    }

    Text("${debt.name} • Remaining ${money(remaining)}", fontWeight = FontWeight.Bold)
    Field(if (debt.direction == "Owed to Me") "Received amount" else "Payment amount", amount) { amount = it; error = "" }
    DatePickerField("Payment date", paidDate) { paidDate = it; error = "" }
    ChoiceDropdown("Payment method", method, listOf("Cash", "Bank transfer", "Mobile banking", "Salary deduction", "Card", "Cheque", "Other")) {
        method = it; channel = ""; accountNumber = ""; branch = ""; routingNumber = ""; reference = ""; methodDetails = ""; accountName = if (it == "Cash") debt.name else ""
    }
    PaymentMethodDetailsFields(
        method, channel, { channel = it }, accountName, { accountName = it }, accountNumber, { accountNumber = it },
        branch, { branch = it }, routingNumber, { routingNumber = it }, reference, { reference = it }, methodDetails, { methodDetails = it }
    )
    Field("Payment notes", notes) { notes = it }
    AttachmentSection(attachments, maxFiles = 3) { attachments = it }
    if (debt.direction == "Owed to Me" && openRequests.isNotEmpty()) {
        ChoiceDropdown("Apply payment to", requestSelection, requestOptions) { requestSelection = it }
    }
    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    Button(onClick = {
        val value = amount.toDoubleOrNull() ?: 0.0
        val date = parseExpenseDate(paidDate)
        val selectedRequest = openRequests.find { requestSelection.startsWith(it.requestNumber) }
        error = when {
            !value.isFinite() || value <= 0.0 -> "Enter a valid amount greater than zero."
            value > 999_999_999.99 -> "Amount is too large."
            value > remaining + 0.005 -> "Amount cannot exceed the remaining ${money(remaining)}."
            selectedRequest != null && value > (selectedRequest.amount - selectedRequest.receivedAmount) + 0.005 -> "Amount cannot exceed this request's outstanding balance."
            date == null -> "Select a valid payment date."
            date > System.currentTimeMillis() -> "Payment date cannot be in the future."
            paymentMethodValidation(method, channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, accountName, accountNumber, reference, methodDetails).isNotBlank() -> paymentMethodValidation(method, channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, accountName, accountNumber, reference, methodDetails)
            else -> ""
        }
        if (error.isBlank() && date != null) {
            viewModel.markDebtPaid(debt.id, value, paidDate = date, method = method, channel = channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, reference = reference.trim(), counterparty = accountName.trim(), notes = notes.trim(), attachments = attachments, requestId = selectedRequest?.id ?: "", accountNumber = accountNumber.trim(), branch = branch.trim(), routingNumber = routingNumber.trim(), methodDetails = methodDetails.trim())
            onSaved()
        }
    }, modifier = Modifier.fillMaxWidth()) { Text(if (debt.direction == "Owed to Me") "Save Received Amount" else "Save Payment") }
}

@Composable
fun PaymentSectionCard(title: String, summary: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = FinanceShapes.large
    ) {
        Row(Modifier.padding(FinanceSpacing.lg), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(summary, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = FinanceShapes.pill) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Open $title",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun SortMenu(
    expanded: Boolean,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(if (option == selected) "✓ $option" else option) },
                onClick = { onSelect(option) }
            )
        }
    }
}

@Composable
fun CompactTabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = FinanceShapes.pill,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) { Text(label, maxLines = 1) }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = FinanceShapes.pill,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
        ) { Text(label, maxLines = 1) }
    }
}

@Composable
fun SearchSortControls(
    search: String,
    onSearchChange: (String) -> Unit,
    sortMode: String,
    onSortChange: (String) -> Unit,
    sortOptions: List<String>,
    placeholder: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            label = { Text(placeholder) },
            singleLine = true,
            trailingIcon = {
                if (search.isNotBlank()) {
                    TextButton(onClick = { onSearchChange("") }) { Text("Clear") }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        ChoiceDropdown(
            label = "Sort",
            value = sortMode,
            options = sortOptions,
            onSelect = onSortChange
        )
    }
}

@Composable
fun SelectableButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) { Text(label, maxLines = 1, fontSize = 12.sp) }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(label, maxLines = 1, fontSize = 12.sp)
        }
    }
}

@Composable
fun StatusFilterRow(
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf("Active", "Completed", "Archived").forEach { option ->
            CompactTabButton(
                label = option,
                selected = selected == option,
                onClick = { onSelect(option) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PlanActionMenu(
    actions: List<Pair<String, () -> Unit>>,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Plan actions")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            actions.forEach { (label, action) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { expanded = false; action() }
                )
            }
            DropdownMenuItem(
                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                onClick = { expanded = false; onDelete() }
            )
        }
    }
}


