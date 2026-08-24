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
// DEBT FORM
// ============================================================

@Composable
fun DebtForm(
    viewModel: FinanceViewModel,
    existing: Debt?,
    initialDirection: String = "I Owe",
    done: () -> Unit
) {

    val viewOnly = existing?.let { it.archived || debtCompleted(it) } == true

    var name by remember {
        mutableStateOf(
            existing?.name ?: ""
        )
    }

    var direction by remember {
        mutableStateOf(
            existing?.direction ?: initialDirection
        )
    }

    var amount by remember {
        mutableStateOf(
            existing?.originalAmount
                ?.toString()
                ?: ""
        )
    }

    var notes by remember {
        mutableStateOf(
            existing?.notes ?: ""
        )
    }

    var reason by remember { mutableStateOf(existing?.reason ?: "") }
    var debtDate by remember { mutableStateOf(expenseDateText(existing?.debtDate ?: System.currentTimeMillis())) }
    var dueDate by remember { mutableStateOf(existing?.dueDate?.let(::expenseDateText) ?: "") }
    var receivedOrGivenMethod by remember { mutableStateOf(existing?.receivedOrGivenMethod ?: "Cash") }
    var debtReference by remember { mutableStateOf(existing?.referenceNumber ?: "") }
    var financingChannel by remember { mutableStateOf(existing?.financingChannel ?: "") }
    var financingAccountName by remember { mutableStateOf(existing?.financingAccountName ?: "") }
    var financingAccountNumber by remember { mutableStateOf(existing?.financingAccountNumber ?: "") }
    var financingBranch by remember { mutableStateOf(existing?.financingBranch ?: "") }
    var financingRouting by remember { mutableStateOf(existing?.financingRoutingNumber ?: "") }
    var financingReference by remember { mutableStateOf(existing?.financingReference ?: "") }
    var financingMethodDetails by remember { mutableStateOf(existing?.financingMethodDetails ?: "") }
    var attachments by remember { mutableStateOf(existing?.attachments ?: emptyList()) }

    var payment by remember {
        mutableStateOf("")
    }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var paymentChannel by remember { mutableStateOf("") }
    var paymentReference by remember { mutableStateOf("") }
    var paymentNotes by remember { mutableStateOf("") }
    var paymentAttachments by remember { mutableStateOf(emptyList<Attachment>()) }
    var showRequestDialog by remember { mutableStateOf(false) }
    var pendingDebtUpdate by remember { mutableStateOf<ConfirmationRequest?>(null) }

    var previousPayment by remember { mutableStateOf("") }

    var paymentError by remember { mutableStateOf("") }

    var error by remember {
        mutableStateOf("")
    }

    val hasUnsavedChanges = !viewOnly && (
        name != (existing?.name ?: "") ||
        direction != (existing?.direction ?: "I Owe") ||
        amount != (existing?.originalAmount?.toString() ?: "") ||
        notes != (existing?.notes ?: "") ||
        reason != (existing?.reason ?: "") ||
        debtDate != expenseDateText(existing?.debtDate ?: System.currentTimeMillis()) ||
        dueDate != (existing?.dueDate?.let(::expenseDateText) ?: "") ||
        receivedOrGivenMethod != (existing?.receivedOrGivenMethod ?: "Cash") ||
        debtReference != (existing?.referenceNumber ?: "") ||
        financingChannel != (existing?.financingChannel ?: "") || financingAccountName != (existing?.financingAccountName ?: "") ||
        financingAccountNumber != (existing?.financingAccountNumber ?: "") || financingBranch != (existing?.financingBranch ?: "") ||
        financingRouting != (existing?.financingRoutingNumber ?: "") || financingReference != (existing?.financingReference ?: "") ||
        financingMethodDetails != (existing?.financingMethodDetails ?: "") ||
        attachments != (existing?.attachments ?: emptyList<Attachment>()) ||
        payment.isNotBlank() ||
        previousPayment.isNotBlank()
    )

    FormColumn(
        title =
            if (existing == null) {
                "Add Debt"
            } else if (viewOnly) {
                "Debt Details"
            } else {
                "Edit Debt"
            },
        readOnly = viewOnly,
        onBack = done,
        hasUnsavedChanges = hasUnsavedChanges
    ) {

        if (viewOnly && existing != null) {
            Text(
                if (existing.archived) {
                    "This debt is archived and view-only. Restore it to make changes."
                } else {
                    "This debt is completed and view-only. Reopen it to make changes."
                },
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }

        Field(
            "Person / organization",
            name
        ) {
            name = it
        }

        if (existing != null && existing.payments.isEmpty() && existing.paymentRequests.isEmpty() && !viewOnly) {
            ChoiceDropdown("Direction", direction, listOf("I Owe", "Owed to Me")) { direction = it }
        } else {
            InfoRow("Direction", direction)
        }

        Text(
            if (direction == "I Owe") "You need to pay this person or organization." else "This person or organization needs to pay you.",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Field(
            "Original amount",
            amount
        ) {
            amount = it
        }

        Field(
            "Notes",
            notes
        ) {
            notes = it
        }
        Field("Reason for debt", reason) { reason = it }
        DatePickerField("Debt date", debtDate) { debtDate = it }
        DatePickerField("Due date (optional)", dueDate) { dueDate = it }
        ChoiceDropdown(
            if (direction == "I Owe") "How you received it" else "How you gave it",
            receivedOrGivenMethod,
            listOf("Cash", "Bank transfer", "Mobile banking", "Goods or service", "Other")
        ) {
            receivedOrGivenMethod = it; financingChannel = ""; financingAccountName = ""; financingAccountNumber = ""
            financingBranch = ""; financingRouting = ""; financingReference = ""; financingMethodDetails = ""
        }
        PaymentMethodDetailsFields(
            receivedOrGivenMethod, financingChannel, { financingChannel = it }, financingAccountName, { financingAccountName = it },
            financingAccountNumber, { financingAccountNumber = it }, financingBranch, { financingBranch = it },
            financingRouting, { financingRouting = it }, financingReference, { financingReference = it },
            financingMethodDetails, { financingMethodDetails = it }
        )
        Field("Agreement / reference number", debtReference) { debtReference = it }
        AttachmentSection(attachments, maxFiles = 5) { attachments = it }

        if (existing == null) {
            Field("Previous payment amount (optional)", previousPayment) {
                previousPayment = it
            }
        }

        if (existing != null) {

            val original =
                amount.toDoubleOrNull()
                    ?: existing.originalAmount

            val paid = debtPaidAmount(existing)

            val remaining = max(0.0, original - paid)

            val progress =
                if (original > 0) {
                    (
                            paid / original
                            * 100.0
                            )
                        .coerceIn(
                            0.0,
                            100.0
                        )
                } else {
                    0.0
                }

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Text(
                        "Remaining: ${money(remaining)}",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        "Progress: ${
                            String.format(
                                Locale.US,
                                "%.1f",
                                progress
                            )
                        }%"
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    LinearProgressIndicator(
                        progress = {
                            (
                                    progress /
                                            100.0
                                    )
                                .toFloat()
                                .coerceIn(
                                    0f,
                                    1f
                                )
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }
            }

            if (!viewOnly) {
                OutlinedButton(
                    onClick = {
                        val updatedAmount = amount.toDoubleOrNull() ?: 0.0
                        val financingValidation = paymentMethodValidation(receivedOrGivenMethod, financingChannel.ifBlank { if (receivedOrGivenMethod == "Mobile banking") defaultProvider() else if (receivedOrGivenMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails)
                        if (name.isBlank() || name.trim().length > 100 || !updatedAmount.isFinite() || updatedAmount <= 0 || updatedAmount > 999_999_999.99 || updatedAmount + 0.005 < paid || notes.length > 500 || debtReference.length > 100 || financingValidation.isNotBlank()) {
                            paymentError = "Enter a valid name and an original amount not below the recorded total."
                        } else {
                            pendingDebtUpdate = ConfirmationRequest(
                                title = "Update Debt?",
                                message = "Save the changed direction, financial details, notes, and documents for ${existing.name}?",
                                confirmLabel = "Update",
                                onConfirm = {
                                    viewModel.updateDebt(
                                        existing.copy(
                                            name = name.trim(),
                                            direction = direction,
                                            originalAmount = updatedAmount,
                                            debtDate = parseExpenseDate(debtDate) ?: existing.debtDate,
                                            dueDate = if (dueDate.isBlank()) null else parseExpenseDate(dueDate),
                                            notes = notes.trim(),
                                            reason = reason.trim(),
                                            receivedOrGivenMethod = receivedOrGivenMethod,
                                            referenceNumber = debtReference.trim(),
                                            financingChannel = financingChannel.ifBlank { if (receivedOrGivenMethod == "Mobile banking") defaultProvider() else if (receivedOrGivenMethod == "Bank transfer") defaultBank() else "" },
                                            financingAccountName = financingAccountName.trim(), financingAccountNumber = financingAccountNumber.trim(),
                                            financingBranch = financingBranch.trim(), financingRoutingNumber = financingRouting.trim(),
                                            financingReference = financingReference.trim(), financingMethodDetails = financingMethodDetails.trim(),
                                            attachments = attachments
                                        )
                                    )
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Update Debt Details") }
            }

            if (false && !viewOnly) {
            Field(
                if (direction == "I Owe") "New payment amount" else "New received amount",
                payment
            ) {
                payment = it
                paymentError = ""
            }
            ChoiceDropdown(
                "Payment method",
                paymentMethod,
                listOf("Cash", "Bank transfer", "Mobile banking", "Salary deduction", "Card", "Cheque", "Other")
            ) { paymentMethod = it }
            if (paymentMethod == "Mobile banking") {
                ChoiceDropdown("Mobile banking provider", paymentChannel.ifBlank { defaultProvider() }, availableProviders()) { paymentChannel = it }
            } else if (paymentMethod == "Bank transfer" || paymentMethod == "Salary deduction") {
                Field(if (paymentMethod == "Bank transfer") "Bank name" else "Employer / salary month", paymentChannel) { paymentChannel = it }
            }
            Field("Transaction / reference ID", paymentReference) { paymentReference = it }
            Field("Payment notes", paymentNotes) { paymentNotes = it }
            AttachmentSection(paymentAttachments, maxFiles = 3) { paymentAttachments = it }

            if (paymentError.isNotEmpty()) {
                Text(paymentError, color = MaterialTheme.colorScheme.error)
            }

            Button(

                onClick = {

                    val paymentAmount =
                        payment.toDoubleOrNull()
                            ?: 0.0

                    when {
                        remaining <= 0 -> {
                            paymentError = "This debt is fully paid. Reopen it before adding another payment."
                        }
                        paymentAmount <= 0 -> {
                            paymentError = "Enter a valid payment amount."
                        }
                        paymentAmount > remaining + 0.005 -> {
                            paymentError = "Payment cannot be more than the remaining ${money(remaining)}."
                        }
                        else -> {

                            viewModel.markDebtPaid(
                                existing.id,
                                paymentAmount,
                                method = paymentMethod,
                                channel = paymentChannel,
                                reference = paymentReference.trim(),
                                counterparty = existing.name,
                                notes = paymentNotes.trim(),
                                attachments = paymentAttachments
                            )

                            payment = ""
                            paymentReference = ""
                            paymentNotes = ""
                            paymentAttachments = emptyList()
                            paymentError = ""
                        }
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    if (direction == "I Owe") "Record Payment" else "Record Received Amount"
                )
            }

            if (direction == "Owed to Me" && !viewOnly) {
                OutlinedButton(onClick = { showRequestDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Create Payment Request")
                }
            }

            if (existing.paymentRequests.isNotEmpty()) {
                Text("Payment Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                existing.paymentRequests.sortedByDescending { it.createdDate }.forEach { request ->
                    PaymentRequestCard(existing, request, viewModel.data.receiptProfile)
                }
            }
            }

            if (viewOnly && existing.paymentRequests.isNotEmpty()) {
                Text("Payment Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                existing.paymentRequests.sortedByDescending { it.createdDate }.forEach { request ->
                    PaymentRequestCard(existing, request, viewModel.data.receiptProfile)
                }
            }

        } else {

            if (error.isNotEmpty()) {

                Text(
                    error,
                    color =
                        MaterialTheme.colorScheme.error
                )
            }

            Button(

                onClick = {

                    val originalAmount =
                        amount.toDoubleOrNull()
                            ?: 0.0
                    val previousAmount = previousPayment.toDoubleOrNull() ?: 0.0
                    val selectedDebtDate = parseExpenseDate(debtDate)
                    val selectedDueDate = if (dueDate.isBlank()) null else parseExpenseDate(dueDate)
                    val financingValidation = paymentMethodValidation(receivedOrGivenMethod, financingChannel.ifBlank { if (receivedOrGivenMethod == "Mobile banking") defaultProvider() else if (receivedOrGivenMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails)

                    if (
                        name.isBlank() || name.trim().length > 100 || notes.length > 500 || debtReference.length > 100 ||
                        !originalAmount.isFinite() || originalAmount <= 0 || originalAmount > 999_999_999.99 ||
                        !previousAmount.isFinite() || previousAmount < 0 ||
                        previousAmount > originalAmount || selectedDebtDate == null || (dueDate.isNotBlank() && selectedDueDate == null) ||
                        (selectedDueDate != null && selectedDebtDate != null && selectedDueDate < selectedDebtDate) || financingValidation.isNotBlank()
                    ) {

                        error =
                            "Enter a name and valid amount."

                    } else {

                        viewModel.addDebt(
                            Debt(
                                name =
                                    name.trim(),
                                direction =
                                    direction.trim(),
                                originalAmount =
                                    originalAmount,
                                debtDate = selectedDebtDate,
                                dueDate =
                                    selectedDueDate,
                                notes = notes.trim(),
                                reason = reason.trim(),
                                receivedOrGivenMethod = receivedOrGivenMethod,
                                referenceNumber = debtReference.trim(),
                                financingChannel = financingChannel.ifBlank { if (receivedOrGivenMethod == "Mobile banking") defaultProvider() else if (receivedOrGivenMethod == "Bank transfer") defaultBank() else "" },
                                financingAccountName = financingAccountName.trim(), financingAccountNumber = financingAccountNumber.trim(),
                                financingBranch = financingBranch.trim(), financingRoutingNumber = financingRouting.trim(),
                                financingReference = financingReference.trim(), financingMethodDetails = financingMethodDetails.trim(),
                                attachments = attachments,
                                payments = if (previousAmount > 0) {
                                    listOf(Payment(1, System.currentTimeMillis(), previousAmount, System.currentTimeMillis()))
                                } else emptyList()
                            )
                        )

                        done()
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    "Save Debt"
                )
            }
        }
    }

    if (showRequestDialog && existing != null) {
        PaymentRequestDialog(
            debt = existing,
            onSave = {
                viewModel.addPaymentRequest(existing.id, it)
                showRequestDialog = false
            },
            onDismiss = { showRequestDialog = false }
        )
    }
    pendingDebtUpdate?.let { request ->
        ConfirmationDialog(request) { pendingDebtUpdate = null }
    }
}


