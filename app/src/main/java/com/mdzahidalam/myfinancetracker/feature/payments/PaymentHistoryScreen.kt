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
// PAYMENT HISTORY
// ============================================================

internal fun amountInWords(amount: Double): String {
    fun underThousand(value: Int): String {
        val ones = listOf("Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
        val tens = listOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")
        if (value < 20) return ones[value]
        if (value < 100) return tens[value / 10] + if (value % 10 == 0) "" else " ${ones[value % 10]}"
        return ones[value / 100] + " Hundred" + if (value % 100 == 0) "" else " ${underThousand(value % 100)}"
    }
    fun whole(value: Long): String {
        if (value == 0L) return "Zero"
        val scales = listOf(1_000_000_000L to "Billion", 1_000_000L to "Million", 1_000L to "Thousand")
        var remaining = value
        val parts = mutableListOf<String>()
        scales.forEach { (scale, label) ->
            if (remaining >= scale) {
                parts += "${whole(remaining / scale)} $label"
                remaining %= scale
            }
        }
        if (remaining > 0) parts += underThousand(remaining.toInt())
        return parts.joinToString(" ")
    }
    val taka = amount.toLong().coerceAtLeast(0)
    val poisha = kotlin.math.round((amount - taka) * 100).toInt().coerceIn(0, 99)
    return buildString {
        append(whole(taka)).append(" Taka")
        if (poisha > 0) append(" and ").append(underThousand(poisha)).append(" Poisha")
        append(" Only")
    }
}

internal fun paymentReceiptText(
    planName: String,
    direction: String,
    total: Double,
    payments: List<Payment>,
    payment: Payment,
    profile: ReceiptProfile
): String {
    val paidBefore = payments.filter { it.paidDate != null && it.number < payment.number }.sumOf { it.amount }
    val previousBalance = max(0.0, total - paidBefore)
    val remainingBalance = max(0.0, previousBalance - payment.amount)
    val owner = profile.fullName.ifBlank { "App user" }
    val payer = if (direction == "Owed to Me") planName else owner
    val recipient = if (direction == "Owed to Me") owner else payment.counterparty.ifBlank { planName }
    val planCode = planName.hashCode().toUInt().toString(16).takeLast(4).uppercase(Locale.US)
    val receiptNumber = "MFT-${SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(payment.paidDate ?: payment.dueDate))}-$planCode-${payment.number.toString().padStart(3, '0')}"
    return buildString {
        appendLine("MY FINANCE TRACKER")
        appendLine("PAYMENT RECEIPT")
        appendLine()
        appendLine("Receipt number: $receiptNumber")
        appendLine("Payment date: ${dateTimeText(payment.paidDate ?: payment.dueDate)}")
        appendLine("Record: $planName")
        appendLine("Payer: $payer")
        appendLine("Recipient: $recipient")
        appendLine("Amount: ${money(payment.amount)}")
        appendLine("Amount in words: ${amountInWords(payment.amount)}")
        appendLine("Payment method: ${payment.paymentMethod}")
        if (payment.paymentChannel.isNotBlank()) appendLine("Channel: ${payment.paymentChannel}")
        if (payment.accountNumber.isNotBlank()) appendLine("Account / last four digits: ${payment.accountNumber}")
        if (payment.branch.isNotBlank()) appendLine("Branch: ${payment.branch}")
        if (payment.routingNumber.isNotBlank()) appendLine("Routing number: ${payment.routingNumber}")
        if (payment.methodDetails.isNotBlank()) appendLine("Method details: ${payment.methodDetails}")
        if (payment.referenceNumber.isNotBlank()) appendLine("Transaction/reference ID: ${payment.referenceNumber}")
        appendLine("Previous balance: ${money(previousBalance)}")
        appendLine("Remaining balance: ${money(remainingBalance)}")
        if (payment.notes.isNotBlank()) appendLine("Notes: ${payment.notes}")
        appendLine()
        appendLine("Payer signature: ____________________")
        appendLine("Recipient signature: ________________")
        appendLine()
        appendLine("Personal payment record generated by My Finance Tracker. Recipient confirmation or signature may be required as proof of payment.")
        appendLine("Powered by Md. Zahid Alam")
        profile.signature?.let { appendLine("[[SIGNATURE:${it.contentBase64}]]") }
    }
}

@Composable
fun PaymentHistory(
    payments: List<Payment>,
    onUpdate: ((Payment) -> Unit)? = null,
    planName: String = "Payment",
    direction: String = "I Owe",
    planTotal: Double = payments.sumOf { it.amount },
    profile: ReceiptProfile = ReceiptProfile()
) {

    val context = LocalContext.current
    var editingPayment by remember { mutableStateOf<Payment?>(null) }
    var receiptPayment by remember { mutableStateOf<Payment?>(null) }
    var pendingAction by remember { mutableStateOf<ConfirmationRequest?>(null) }
    var pendingPdf by remember { mutableStateOf<Pair<String, String>?>(null) }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val pending = pendingPdf
        if (uri != null && pending != null) writePdfToUri(context, uri, pending.second)
        pendingPdf = null
    }

    val receiptLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        val payment = receiptPayment
        if (uri != null && payment != null && onUpdate != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            onUpdate(payment.copy(receiptUri = uri.toString()))
        }
        receiptPayment = null
    }

    Column(
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        payments.forEach { payment ->

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier =
                        Modifier.padding(12.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            "Payment ${payment.number}",
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            "Due ${dateText(payment.dueDate)} • " +
                                    money(payment.amount)
                        )

                        if (payment.paidDate != null) {

                            Text(
                                "Paid ${dateTimeText(payment.paidDate)}"
                            )
                        }

                        if (payment.notes.isNotBlank()) {
                            Text("Note: ${payment.notes}")
                        }

                        if (payment.receiptUri != null) {
                            Text("Receipt attached")
                        }
                        payment.attachments.forEach { attachment ->
                            TextButton(onClick = { runCatching { openAttachment(context, attachment) } }) {
                                Text("Open ${attachment.name}")
                            }
                        }
                    }

                    if (
                        payment.paidDate == null &&
                        onUpdate != null
                    ) {

                        Button(
                            onClick = {
                                editingPayment = payment.copy(
                                    paidDate = System.currentTimeMillis(),
                                    status = "PAID"
                                )
                            }
                        ) {
                            Text("Record Payment")
                        }

                    } else if (
                        payment.paidDate != null
                    ) {

                        Text(
                            "PAID",
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                MaterialTheme.colorScheme
                                    .primary
                        )
                    }
                }

                if (onUpdate != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (payment.paidDate != null) {
                            TextButton(
                                onClick = {
                                    pendingAction = ConfirmationRequest(
                                        title = "Undo Paid?",
                                        message = "Payment ${payment.number} for ${money(payment.amount)} will return to Pending. The plan balance and completion status will be recalculated.",
                                        confirmLabel = "Undo Paid",
                                        onConfirm = {
                                            onUpdate(
                                                payment.copy(
                                                    paidDate = null,
                                                    status = "PENDING"
                                                )
                                            )
                                        }
                                    )
                                }
                            ) { Text("Undo Paid") }
                        }

                        TextButton(onClick = {
                            pendingAction = ConfirmationRequest(
                                title = "Edit Payment?",
                                message = "You are about to change payment ${payment.number}, including its dates or notes.",
                                confirmLabel = "Edit",
                                onConfirm = { editingPayment = payment }
                            )
                        }) {
                            Text("Edit")
                        }

                        TextButton(
                            onClick = {
                                if (payment.receiptUri == null) {
                                    receiptPayment = payment
                                    receiptLauncher.launch(arrayOf("image/*"))
                                } else {
                                    runCatching {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                android.net.Uri.parse(payment.receiptUri)
                                            ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(if (payment.receiptUri == null) "Attach" else "Receipt")
                        }
                    }

                    if (payment.paidDate != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                val fileName = "MFT-Receipt-${payment.number}-${dateText(payment.paidDate)}.pdf"
                                val content = paymentReceiptText(planName, direction, planTotal, payments, payment, profile)
                                pendingPdf = fileName to content
                                pdfLauncher.launch(fileName)
                            }) { Text("PDF Receipt") }
                            TextButton(onClick = {
                                val fileName = "MFT-Receipt-${payment.number}.pdf"
                                sharePdf(context, fileName, paymentReceiptText(planName, direction, planTotal, payments, payment, profile))
                            }) { Text("Share") }
                        }
                    }
                }
                if (onUpdate == null && payment.paidDate != null) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            val fileName = "MFT-Receipt-${payment.number}-${dateText(payment.paidDate)}.pdf"
                            pendingPdf = fileName to paymentReceiptText(planName, direction, planTotal, payments, payment, profile)
                            pdfLauncher.launch(fileName)
                        }) { Text("Save PDF Receipt") }
                        TextButton(onClick = {
                            sharePdf(context, "MFT-Receipt-${payment.number}.pdf", paymentReceiptText(planName, direction, planTotal, payments, payment, profile))
                        }) { Text("Share") }
                    }
                }
            }
        }
    }

    editingPayment?.let { payment ->
        PaymentEditDialog(
            payment = payment,
            onSave = { updatedPayment ->
                editingPayment = null
                pendingAction = ConfirmationRequest(
                    title = "Update Payment?",
                    message = "Save the new dates and notes for payment ${updatedPayment.number}?",
                    confirmLabel = "Update",
                    onConfirm = { onUpdate?.invoke(updatedPayment) }
                )
            },
            onDismiss = { editingPayment = null }
        )
    }

    pendingAction?.let { request ->
        ConfirmationDialog(request) { pendingAction = null }
    }
}

@Composable
fun PaymentEditDialog(
    payment: Payment,
    onSave: (Payment) -> Unit,
    onDismiss: () -> Unit
) {
    var dueDate by remember(payment) { mutableStateOf(expenseDateText(payment.dueDate)) }
    var paidDate by remember(payment) {
        mutableStateOf(payment.paidDate?.let { expenseDateText(it) } ?: "")
    }
    var notes by remember(payment) { mutableStateOf(payment.notes) }
    var paymentMethod by remember(payment) { mutableStateOf(payment.paymentMethod) }
    var paymentChannel by remember(payment) { mutableStateOf(payment.paymentChannel) }
    var referenceNumber by remember(payment) { mutableStateOf(payment.referenceNumber) }
    var counterparty by remember(payment) { mutableStateOf(payment.counterparty) }
    var accountNumber by remember(payment) { mutableStateOf(payment.accountNumber) }
    var branch by remember(payment) { mutableStateOf(payment.branch) }
    var routingNumber by remember(payment) { mutableStateOf(payment.routingNumber) }
    var methodDetails by remember(payment) { mutableStateOf(payment.methodDetails) }
    var attachments by remember(payment) { mutableStateOf(payment.attachments) }
    var error by remember(payment) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Payment ${payment.number}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                DatePickerField("Due date", dueDate) { dueDate = it }
                if (payment.paidDate != null) {
                    DatePickerField("Paid date", paidDate) { paidDate = it }
                }
                Field("Payment notes", notes) { notes = it }
                ChoiceDropdown(
                    "Payment method",
                    paymentMethod,
                    listOf("Cash", "Bank transfer", "Mobile banking", "Salary deduction", "Card", "Cheque", "Other")
                ) { paymentMethod = it; paymentChannel = ""; counterparty = ""; accountNumber = ""; branch = ""; routingNumber = ""; referenceNumber = ""; methodDetails = "" }
                PaymentMethodDetailsFields(
                    paymentMethod, paymentChannel, { paymentChannel = it }, counterparty, { counterparty = it }, accountNumber, { accountNumber = it },
                    branch, { branch = it }, routingNumber, { routingNumber = it }, referenceNumber, { referenceNumber = it }, methodDetails, { methodDetails = it }
                )
                AttachmentSection(attachments, maxFiles = 3) { attachments = it }
                if (error.isNotEmpty()) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedDueDate = parseExpenseDate(dueDate)
                    val parsedPaidDate = if (payment.paidDate == null) null else parseExpenseDate(paidDate)

                    if (parsedDueDate == null || (payment.paidDate != null && parsedPaidDate == null)) {
                        error = "Enter valid dates as DD-MM-YYYY."
                    } else if (parsedPaidDate != null && parsedPaidDate > System.currentTimeMillis()) {
                        error = "Paid date cannot be in the future."
                    } else if (parsedPaidDate != null && paymentMethod == "Not recorded") {
                        error = "Select how this payment was made."
                    } else if (parsedPaidDate != null && paymentMethodValidation(paymentMethod, paymentChannel.ifBlank { if (paymentMethod == "Mobile banking") defaultProvider() else if (paymentMethod == "Bank transfer" || paymentMethod == "Cheque") defaultBank() else "" }, counterparty, accountNumber, referenceNumber, methodDetails).isNotBlank()) {
                        error = paymentMethodValidation(paymentMethod, paymentChannel.ifBlank { if (paymentMethod == "Mobile banking") defaultProvider() else if (paymentMethod == "Bank transfer" || paymentMethod == "Cheque") defaultBank() else "" }, counterparty, accountNumber, referenceNumber, methodDetails)
                    } else if (notes.length > 500 || referenceNumber.length > 100 || counterparty.length > 100) {
                        error = "Notes must be 500 characters or less; reference and party names must be 100 or less."
                    } else {
                        onSave(
                            payment.copy(
                                dueDate = parsedDueDate,
                                paidDate = parsedPaidDate,
                                notes = notes.trim(),
                                paymentMethod = paymentMethod,
                                paymentChannel = paymentChannel.ifBlank { if (paymentMethod == "Mobile banking") defaultProvider() else if (paymentMethod == "Bank transfer" || paymentMethod == "Cheque") defaultBank() else "" },
                                referenceNumber = referenceNumber.trim(),
                                counterparty = counterparty.trim(),
                                attachments = attachments,
                                accountNumber = accountNumber.trim(),
                                branch = branch.trim(),
                                routingNumber = routingNumber.trim(),
                                methodDetails = methodDetails.trim(),
                                status = if (parsedPaidDate == null) "PENDING" else "PAID"
                            )
                        )
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}


