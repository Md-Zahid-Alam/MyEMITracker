package com.mdzahidalam.myfinancetracker.feature.payments
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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.platform.LocalDensity
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
// EXPENSE FORM
// ============================================================

internal fun paymentRequestText(debt: Debt, request: PaymentRequest, profile: ReceiptProfile): String = buildString {
    appendLine("MY FINANCE TRACKER")
    appendLine("PAYMENT REQUEST")
    appendLine()
    appendLine("Request number: ${request.requestNumber}")
    appendLine("Request date: ${dateText(request.createdDate)}")
    request.dueDate?.let { appendLine("Due date: ${dateText(it)}") }
    appendLine("Requested by: ${profile.fullName.ifBlank { "Not specified" }}")
    if (profile.phone.isNotBlank()) appendLine("Phone: ${profile.phone}")
    if (profile.email.isNotBlank()) appendLine("Email: ${profile.email}")
    if (profile.address.isNotBlank()) appendLine("Address: ${profile.address}")
    appendLine("Payment requested from: ${debt.name}")
    appendLine("Reason: ${debt.reason.ifBlank { debt.notes }}")
    appendLine("Original amount: ${money(debt.originalAmount)}")
    appendLine("Amount already received: ${money(debtPaidAmount(debt))}")
    appendLine("Amount requested: ${money(request.amount)}")
    appendLine("Amount received for request: ${money(request.receivedAmount)}")
    appendLine("Request status: ${request.status}")
    appendLine("Remaining to receive: ${money(debtRemainingAmount(debt))}")
    appendLine("Preferred method: ${request.paymentMethod}")
    if (request.paymentChannel.isNotBlank()) appendLine("Provider / bank: ${request.paymentChannel}")
    if (request.accountName.isNotBlank()) appendLine("Account holder: ${request.accountName}")
    if (request.accountNumber.isNotBlank()) appendLine("Account / mobile number: ${request.accountNumber}")
    if (request.branch.isNotBlank()) appendLine("Branch: ${request.branch}")
    if (request.routingNumber.isNotBlank()) appendLine("Routing number: ${request.routingNumber}")
    if (request.referenceNumber.isNotBlank()) appendLine("Reference: ${request.referenceNumber}")
    if (request.methodDetails.isNotBlank()) appendLine("Method details: ${request.methodDetails}")
    if (request.paymentInstructions.isNotBlank()) appendLine("Payment instructions: ${request.paymentInstructions}")
    if (request.message.isNotBlank()) appendLine("Message: ${request.message}")
    appendLine()
    appendLine("This is a personal payment request generated from the issuer's records. It is not a bank statement, legal judgment, or tax invoice.")
    appendLine("Generated by My Finance Tracker")
    appendLine("Powered by Md. Zahid Alam")
    profile.signature?.let { appendLine("[[SIGNATURE:${it.contentBase64}]]") }
}

@Composable
fun PaymentRequestCard(
    debt: Debt,
    request: PaymentRequest,
    profile: ReceiptProfile,
    onEdit: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var pendingText by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null && pendingText.isNotBlank()) writePdfToUri(context, uri, pendingText)
        pendingText = ""
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = FinanceShapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(FinanceSpacing.sm), verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xxs)) {
            Text(request.requestNumber, fontWeight = FontWeight.Bold)
            Text("Requested ${money(request.amount)} • ${dateText(request.createdDate)}")
            request.dueDate?.let { Text("Due ${dateText(it)}") }
            if (request.receivedAmount > 0) Text("Received ${money(request.receivedAmount)}")
            Text(request.status, color = if (request.status == "CANCELLED") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val compactActions = maxWidth < 440.dp || LocalDensity.current.fontScale >= 1.3f
                val activeRequest = request.status in listOf("UNPAID", "PARTIALLY PAID")
                val save: @Composable (Modifier) -> Unit = { buttonModifier ->
                    TextButton(onClick = {
                        pendingText = paymentRequestText(debt, request, profile)
                        launcher.launch("${request.requestNumber}.pdf")
                    }, modifier = buttonModifier) { Text("Save PDF", style = MaterialTheme.typography.labelLarge, maxLines = 1) }
                }
                val share: @Composable (Modifier) -> Unit = { buttonModifier ->
                    TextButton(onClick = { sharePdf(context, "${request.requestNumber}.pdf", paymentRequestText(debt, request, profile)) }, modifier = buttonModifier) { Text("Share", style = MaterialTheme.typography.labelLarge, maxLines = 1) }
                }
                if (compactActions) {
                    Column(verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xxs)) {
                        Row { save(Modifier.weight(1f)); share(Modifier.weight(1f)) }
                        if (activeRequest) Row {
                            onEdit?.let { TextButton(onClick = it, modifier = Modifier.weight(1f)) { Text("Edit", style = MaterialTheme.typography.labelLarge) } }
                            onCancel?.let { TextButton(onClick = it, modifier = Modifier.weight(1f)) { Text("Cancel", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge) } }
                        }
                    }
                } else {
                    Row {
                        save(Modifier.weight(1f)); share(Modifier.weight(1f))
                        if (activeRequest) {
                            onEdit?.let { TextButton(onClick = it, modifier = Modifier.weight(1f)) { Text("Edit", style = MaterialTheme.typography.labelLarge) } }
                            onCancel?.let { TextButton(onClick = it, modifier = Modifier.weight(1f)) { Text("Cancel", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge) } }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentRequestDialog(debt: Debt, existing: PaymentRequest? = null, onSave: (PaymentRequest) -> Unit, onDismiss: () -> Unit) {
    val maximum = availableRequestAmount(debt, existing?.id ?: "") + (existing?.receivedAmount ?: 0.0)
    var amount by remember { mutableStateOf((existing?.amount ?: maximum).toString()) }
    var dueDate by remember { mutableStateOf(expenseDateText(existing?.dueDate ?: System.currentTimeMillis())) }
    var method by remember { mutableStateOf(existing?.paymentMethod ?: "Mobile banking") }
    var channel by remember { mutableStateOf(existing?.paymentChannel ?: "") }
    var accountName by remember { mutableStateOf(existing?.accountName ?: "") }
    var accountNumber by remember { mutableStateOf(existing?.accountNumber ?: "") }
    var branch by remember { mutableStateOf(existing?.branch ?: "") }
    var routingNumber by remember { mutableStateOf(existing?.routingNumber ?: "") }
    var referenceNumber by remember { mutableStateOf(existing?.referenceNumber ?: "") }
    var methodDetails by remember { mutableStateOf(existing?.methodDetails ?: "") }
    var instructions by remember { mutableStateOf(existing?.paymentInstructions ?: "") }
    var message by remember { mutableStateOf(existing?.message ?: "") }
    var error by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Create Payment Request" else "Edit Payment Request") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs), modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Request money from ${debt.name}")
                Text("Available to request: ${money(maximum)}", color = MaterialTheme.colorScheme.primary)
                Field("Requested amount", amount) { amount = it }
                DatePickerField("Due date", dueDate) { dueDate = it }
                ChoiceDropdown("Preferred payment method", method, listOf("Cash", "Bank transfer", "Mobile banking", "Cheque", "Other")) {
                    method = it; channel = ""; accountName = ""; accountNumber = ""; branch = ""; routingNumber = ""; referenceNumber = ""; methodDetails = ""
                }
                PaymentMethodDetailsFields(
                    method, channel, { channel = it }, accountName, { accountName = it }, accountNumber, { accountNumber = it },
                    branch, { branch = it }, routingNumber, { routingNumber = it }, referenceNumber, { referenceNumber = it }, methodDetails, { methodDetails = it }
                )
                Field("Payment instructions", instructions) { instructions = it }
                Field("Message", message) { message = it }
                if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val value = amount.toDoubleOrNull() ?: 0.0
                val parsedDue = parseExpenseDate(dueDate)
                error = when {
                    !validFinancialAmount(amount) -> "Enter an amount using up to 12 digits and 2 decimal places."
                    value > maximum + 0.005 -> "Request cannot exceed the available receivable balance."
                    value < (existing?.receivedAmount ?: 0.0) -> "Request amount cannot be below the amount already received."
                    parsedDue == null -> "Enter a valid due date."
                    parsedDue < Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis -> "Due date cannot be in the past."
                    method in listOf("Bank transfer", "Mobile banking") && instructions.isBlank() -> "Enter payment instructions for the selected method."
                    paymentMethodValidation(method, channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, accountName, accountNumber, referenceNumber, methodDetails).isNotBlank() -> paymentMethodValidation(method, channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, accountName, accountNumber, referenceNumber, methodDetails)
                    instructions.length > 300 || message.length > 500 -> "Instructions must be 300 characters or less and message 500 or less."
                    else -> ""
                }
                if (error.isBlank()) {
                    val stamp = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
                    onSave(
                        PaymentRequest(
                            id = existing?.id ?: UUID.randomUUID().toString(),
                            requestNumber = existing?.requestNumber ?: "MFT-REQ-$stamp-${UUID.randomUUID().toString().take(4).uppercase(Locale.US)}",
                            createdDate = existing?.createdDate ?: System.currentTimeMillis(),
                            dueDate = parsedDue,
                            amount = value,
                            paymentMethod = method,
                            paymentInstructions = instructions.trim(),
                            message = message.trim(),
                            status = existing?.status ?: "UNPAID",
                            receivedAmount = existing?.receivedAmount ?: 0.0,
                            paymentChannel = channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" },
                            accountName = accountName.trim(),
                            accountNumber = accountNumber.trim(),
                            branch = branch.trim(),
                            routingNumber = routingNumber.trim(),
                            referenceNumber = referenceNumber.trim(),
                            methodDetails = methodDetails.trim()
                        )
                    )
                }
            }) { Text(if (existing == null) "Create" else "Save Changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ExpenseForm(
    viewModel: FinanceViewModel,
    existing: Expense?,
    done: () -> Unit
) {
    var pendingUpdate by remember { mutableStateOf<Expense?>(null) }
    val initialDate = remember(existing?.id) {
        expenseDateText(existing?.date ?: System.currentTimeMillis())
    }
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "Food") }
    var amount by remember { mutableStateOf(existing?.amount?.toString() ?: "") }
    var date by remember {
        mutableStateOf(initialDate)
    }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    var attachments by remember { mutableStateOf(existing?.attachments ?: emptyList()) }
    var error by remember { mutableStateOf("") }

    val hasUnsavedChanges =
        title != (existing?.title ?: "") ||
        category != (existing?.category ?: "Food") ||
        amount != (existing?.amount?.toString() ?: "") ||
        date != initialDate ||
        notes != (existing?.notes ?: "") ||
        attachments != (existing?.attachments ?: emptyList<Attachment>())

    FormColumn(
        title = if (existing == null) "Add Expense" else "Edit Expense",
        onBack = done,
        hasUnsavedChanges = hasUnsavedChanges
    ) {
        Field("Expense name *", title) { title = it }

        ChoiceDropdown(
            label = "Category",
            value = category,
            options = listOf(
                "Food",
                "Transport",
                "Shopping",
                "Bills",
                "Health",
                "Education",
                "Entertainment",
                "Family",
                "Other"
            ),
            onSelect = { category = it }
        )

        Field("Amount *", amount) { amount = it }
        DatePickerField("Date *", date) { date = it }
        Field("Notes (optional)", notes) { notes = it }
        AttachmentSection(attachments, maxFiles = 2) { attachments = it }

        Text(
            "This expense will appear in both daily and monthly summaries.",
            style = MaterialTheme.typography.bodySmall
        )

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val expenseAmount = amount.toDoubleOrNull() ?: 0.0
                val expenseDate = parseExpenseDate(date)

                error = when {
                    title.isBlank() -> "Enter an expense name."
                    title.trim().length > 100 -> "Expense name must be 100 characters or less."
                    !validFinancialAmount(amount) -> "Enter an amount using up to 12 digits and 2 decimal places."
                    expenseDate == null -> "Enter a valid date as DD-MM-YYYY."
                    expenseDate > System.currentTimeMillis() -> "Expense date cannot be in the future."
                    notes.length > 500 -> "Notes must be 500 characters or less."
                    else -> ""
                }

                if (error.isEmpty() && expenseDate != null) {
                    val expense = Expense(
                        id = existing?.id ?: UUID.randomUUID().toString(),
                        title = title.trim(),
                        category = category,
                        amount = expenseAmount,
                        date = expenseDate,
                        notes = notes.trim(),
                        attachments = attachments
                    )

                    if (existing == null) {
                        viewModel.addExpense(expense)
                        done()
                    } else {
                        pendingUpdate = expense
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (existing == null) "Save Expense" else "Update Expense")
        }

        OutlinedButton(onClick = done, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }

    pendingUpdate?.let { expense ->
        ConfirmationDialog(
            request = ConfirmationRequest(
                title = "Update Expense?",
                message = "Save these changes to ${expense.title} for ${money(expense.amount)}?",
                confirmLabel = "Update",
                onConfirm = {
                    viewModel.updateExpense(expense)
                    done()
                }
            ),
            onDismiss = { pendingUpdate = null }
        )
    }
}
