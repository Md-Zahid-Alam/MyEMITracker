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
// LOAN FORM
// ============================================================

@Composable
fun LoanForm(
    viewModel: FinanceViewModel,
    existing: Loan?,
    done: () -> Unit
) {

    val viewOnly = existing?.let { it.archived || loanCompleted(it) } == true
    var pendingUpdate by remember { mutableStateOf<Loan?>(null) }

    var name by remember {
        mutableStateOf(
            existing?.name ?: ""
        )
    }

    var type by remember {
        mutableStateOf(
            existing?.type ?: "Office Loan"
        )
    }
    var loanTypeChoice by remember(existing?.id) {
        mutableStateOf(if ((existing?.type ?: "Office Loan") in LoanTypeOptions.dropLast(1)) existing?.type ?: "Office Loan" else "Other")
    }

    var lender by remember {
        mutableStateOf(
            existing?.lender ?: ""
        )
    }

    var financingSource by remember { mutableStateOf(existing?.financingSource ?: "Bank") }
    var receivedMethod by remember { mutableStateOf(existing?.receivedMethod ?: "Bank transfer") }
    var agreementReference by remember { mutableStateOf(existing?.agreementReference ?: "") }
    var financingNotes by remember { mutableStateOf(existing?.financingNotes ?: "") }
    var financingChannel by remember { mutableStateOf(existing?.financingChannel ?: "") }
    var financingAccountName by remember { mutableStateOf(existing?.financingAccountName ?: "") }
    var financingAccountNumber by remember { mutableStateOf(existing?.financingAccountNumber ?: "") }
    var financingBranch by remember { mutableStateOf(existing?.financingBranch ?: "") }
    var financingRouting by remember { mutableStateOf(existing?.financingRoutingNumber ?: "") }
    var financingReference by remember { mutableStateOf(existing?.financingReference ?: "") }
    var financingMethodDetails by remember { mutableStateOf(existing?.financingMethodDetails ?: "") }
    var attachments by remember { mutableStateOf(existing?.attachments ?: emptyList()) }

    var principal by remember {
        mutableStateOf(
            existing?.principal?.toString()
                ?: ""
        )
    }

    var rate by remember {
        mutableStateOf(
            existing?.interestRate?.toString()
                ?: "0"
        )
    }

    var interest by remember {
        mutableStateOf(
            existing?.interestAmount?.toString()
                ?: "0"
        )
    }

    var installments by remember {
        mutableStateOf(
            existing?.installments?.toString()
                ?: "12"
        )
    }

    var repaymentMode by remember {
        mutableStateOf(existing?.repaymentMode ?: "EQUAL")
    }

    var flexibleMonthlyPayment by remember {
        mutableStateOf(
            if (existing?.repaymentMode == "FLEXIBLE") {
                existing.monthlyPayment.toString()
            } else ""
        )
    }

    var dueDay by remember {
        mutableStateOf(
            existing?.dueDay?.toString()
                ?: "10"
        )
    }

    var previous by remember {
        mutableStateOf(
            existing?.payments
                ?.count {
                    it.paidDate != null
                }
                ?.toString()
                ?: "0"
        )
    }

    val originalPreviousRepayments = existing?.payments?.count { it.paidDate != null } ?: 0

    var reminders by remember {
        mutableStateOf(
            existing?.reminderDays
                ?.joinToString(",")
                ?: "7,3,1,0"
        )
    }

    var error by remember {
        mutableStateOf("")
    }

    val principalAmount =
        principal.toDoubleOrNull()
            ?: 0.0

    val interestRate =
        rate.toDoubleOrNull()
            ?: 0.0

    val enteredInterest =
        interest.toDoubleOrNull()
            ?: 0.0

    val interestValue =
        if (enteredInterest > 0) {
            enteredInterest
        } else {
            principalAmount *
                    interestRate /
                    100.0
        }

    val total =
        principalAmount +
                interestValue

    val count =
        installments.toIntOrNull()
            ?: 0

    val monthly =
        if (repaymentMode == "EQUAL" && count > 0) {
            total / count
        } else {
            0.0
        }

    val flexibleMonthly = flexibleMonthlyPayment.toDoubleOrNull() ?: 0.0
    val scheduledAmounts =
        if (repaymentMode == "FLEXIBLE" && flexibleMonthly > 0) {
            buildList {
                var remaining = total
                while (remaining > 0.0001 && size < 600) {
                    val amount = minOf(flexibleMonthly, remaining)
                    add(amount)
                    remaining -= amount
                }
            }
        } else List(count.coerceAtLeast(0)) { monthly }
    val scheduleCount = scheduledAmounts.size
    val displayedMonthly = if (repaymentMode == "FLEXIBLE") flexibleMonthly else monthly

    val hasUnsavedChanges = !viewOnly && (
        name != (existing?.name ?: "") ||
        type != (existing?.type ?: "Office Loan") ||
        lender != (existing?.lender ?: "") ||
        financingSource != (existing?.financingSource ?: "Bank") ||
        receivedMethod != (existing?.receivedMethod ?: "Bank transfer") ||
        agreementReference != (existing?.agreementReference ?: "") ||
        financingNotes != (existing?.financingNotes ?: "") ||
        financingChannel != (existing?.financingChannel ?: "") || financingAccountName != (existing?.financingAccountName ?: "") ||
        financingAccountNumber != (existing?.financingAccountNumber ?: "") || financingBranch != (existing?.financingBranch ?: "") ||
        financingRouting != (existing?.financingRoutingNumber ?: "") || financingReference != (existing?.financingReference ?: "") ||
        financingMethodDetails != (existing?.financingMethodDetails ?: "") ||
        attachments != (existing?.attachments ?: emptyList<Attachment>()) ||
        principal != (existing?.principal?.toString() ?: "") ||
        rate != (existing?.interestRate?.toString() ?: "0") ||
        interest != (existing?.interestAmount?.toString() ?: "0") ||
        installments != (existing?.installments?.toString() ?: "12") ||
        repaymentMode != (existing?.repaymentMode ?: "EQUAL") ||
        flexibleMonthlyPayment != (if (existing?.repaymentMode == "FLEXIBLE") existing.monthlyPayment.toString() else "") ||
        dueDay != (existing?.dueDay?.toString() ?: "10") ||
        previous != originalPreviousRepayments.toString() ||
        reminders != (existing?.reminderDays?.joinToString(",") ?: "7,3,1,0")
    )

    FormColumn(
        title =
            if (existing == null) {
                "Add Loan"
            } else if (viewOnly) {
                "Loan Details"
            } else {
                "Edit Loan"
            },
        readOnly = viewOnly,
        onBack = done,
        hasUnsavedChanges = hasUnsavedChanges
    ) {

        if (viewOnly && existing != null) {
            Text(
                if (existing.archived) {
                    "This loan is archived and view-only. Restore it to make changes."
                } else {
                    "This loan is completed and view-only. Reopen it to make changes."
                },
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }

        Field(
            "Loan name",
            name
        ) {
            name = it
        }

        ChoiceDropdown("Loan type", loanTypeChoice, LoanTypeOptions) { selected ->
            loanTypeChoice = selected
            if (selected != "Other") type = selected else if (type in LoanTypeOptions) type = ""
        }
        if (loanTypeChoice == "Other") Field("Custom loan type", type) { type = it }

        Field(
            "Lender",
            lender
        ) {
            lender = it
        }

        Text("Financing details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ChoiceDropdown(
            "Financing source",
            financingSource,
            listOf("Bank", "Finance company", "Employer", "Shop or seller", "Credit card", "Friend or family", "Other")
        ) { financingSource = it }
        ChoiceDropdown(
            "How the loan was received",
            receivedMethod,
            listOf("Cash", "Bank transfer", "Mobile banking", "Salary arrangement", "Direct financing", "Other")
        ) {
            receivedMethod = it; financingChannel = ""; financingAccountName = ""; financingAccountNumber = ""
            financingBranch = ""; financingRouting = ""; financingReference = ""; financingMethodDetails = ""
        }
        PaymentMethodDetailsFields(
            receivedMethod, financingChannel, { financingChannel = it }, financingAccountName, { financingAccountName = it },
            financingAccountNumber, { financingAccountNumber = it }, financingBranch, { financingBranch = it },
            financingRouting, { financingRouting = it }, financingReference, { financingReference = it },
            financingMethodDetails, { financingMethodDetails = it }
        )
        Field("Agreement / reference number", agreementReference) { agreementReference = it }
        Field("Financing notes", financingNotes) { financingNotes = it }
        AttachmentSection(attachments, maxFiles = 5) { attachments = it }

        Field(
            "Principal amount",
            principal
        ) {
            principal = it
        }

        Field(
            "Interest rate %",
            rate
        ) {
            rate = it
        }

        Field(
            "Fixed interest amount",
            interest
        ) {
            interest = it
        }

        ChoiceDropdown(
            label = "Repayment method",
            value = if (repaymentMode == "FLEXIBLE") {
                "Flexible Monthly Payment"
            } else {
                "Equal Installments"
            },
            options = listOf(
                "Equal Installments",
                "Flexible Monthly Payment"
            )
        ) {
            repaymentMode = if (it == "Flexible Monthly Payment") "FLEXIBLE" else "EQUAL"
        }

        if (repaymentMode == "EQUAL") {
            Field("Installments", installments) { installments = it }
        } else {
            Field("Planned monthly payment", flexibleMonthlyPayment) {
                flexibleMonthlyPayment = it
            }
        }

        Field(
            "Due day 1-28",
            dueDay
        ) {
            dueDay = it
        }

        Field(
            "Previous repayments already made",
            previous
        ) {
            previous = it
        }

        Field(
            "Reminder days before due date (e.g. 7,3,1,0)",
            reminders
        ) {
            reminders = it
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
                    "Calculated",
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    "Interest: ${money(interestValue)}"
                )

                Text(
                    "Total payable: ${money(total)}"
                )

                Text(
                    if (repaymentMode == "FLEXIBLE") {
                        "Planned monthly payment: ${money(displayedMonthly)}"
                    } else {
                        "Monthly: ${money(displayedMonthly)}"
                    }
                )

                if (repaymentMode == "FLEXIBLE" && scheduledAmounts.isNotEmpty()) {
                    Text("Calculated payments: $scheduleCount")
                    Text("Final payment: ${money(scheduledAmounts.last())}")
                }
            }
        }

        if (error.isNotEmpty()) {

            Text(
                error,
                color =
                    MaterialTheme.colorScheme.error
            )
        }

        if (!viewOnly) {
        Button(

            onClick = {

                val previousCount =
                    previous.toIntOrNull()
                        ?: 0

                val day =
                    dueDay.toIntOrNull()
                        ?: 0

                error = when {

                    name.isBlank() ->
                        "Enter loan name."

                    name.trim().length > 100 ->
                        "Loan name must be 100 characters or less."

                    type.trim().length !in 2..60 || lender.trim().length > 100 ->
                        "Enter a valid loan type; lender must be 100 characters or less."

                    paymentMethodValidation(receivedMethod, financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails).isNotBlank() ->
                        paymentMethodValidation(receivedMethod, financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails)

                    !principalAmount.isFinite() || principalAmount <= 0 || principalAmount > 999_999_999.99 ->
                        "Enter principal."

                    !interestRate.isFinite() || !enteredInterest.isFinite() || interestRate !in 0.0..100.0 || enteredInterest < 0 ->
                        "Interest rate must be 0-100 and interest amount cannot be negative."

                    interestRate > 0 && enteredInterest > 0 ->
                        "Use either interest rate or fixed interest amount, not both."

                    repaymentMode == "EQUAL" && count !in 1..600 ->
                        "Installments must be between 1 and 600."

                    repaymentMode == "FLEXIBLE" && (flexibleMonthly <= 0 || scheduledAmounts.size >= 600) ->
                        "Enter a valid planned monthly payment."

                    previousCount !in 0..scheduleCount ->
                        "Previous repayments must be 0 to total installments."

                    day !in 1..28 ->
                        "Due day must be 1-28."

                    parseReminders(reminders)
                        .isEmpty() ->
                        "Enter reminder days."

                    else ->
                        ""
                }

                if (error.isEmpty()) {

                    val previousChanged = existing != null && previousCount != originalPreviousRepayments
                    val firstDue =
                        existing
                            ?.payments
                            ?.minOfOrNull {
                                it.dueDate
                            }
                            ?.takeIf { !previousChanged }
                            ?: addMonths(currentMonthDueDate(day), -previousCount)

                    val oldPaid =
                        existing
                            ?.payments
                            ?.filter {
                                it.paidDate != null
                            }
                            ?.associateBy {
                                it.number
                            }
                            ?.takeIf { !previousChanged }
                            ?: emptyMap()

                    val payments =
                        scheduledAmounts.mapIndexed { index, amount ->

                            val number = index + 1

                            Payment(

                                number = number,

                                dueDate =
                                    addMonths(
                                        firstDue,
                                        number - 1
                                    ),

                                amount = amount,

                                paidDate =
                                    oldPaid[number]
                                        ?.paidDate
                                        ?: if (
                                            (existing == null || previousChanged) &&
                                            number <= previousCount
                                        ) {
                                            System.currentTimeMillis()
                                        } else {
                                            null
                                        },
                                notes = oldPaid[number]?.notes ?: "",
                                receiptUri = oldPaid[number]?.receiptUri,
                                paymentMethod = oldPaid[number]?.paymentMethod ?: "Not recorded",
                                paymentChannel = oldPaid[number]?.paymentChannel ?: "",
                                referenceNumber = oldPaid[number]?.referenceNumber ?: "",
                                counterparty = oldPaid[number]?.counterparty ?: "",
                                attachments = oldPaid[number]?.attachments ?: emptyList()
                            )
                        }

                    val loan =
                        Loan(

                            id =
                                existing?.id
                                    ?: UUID.randomUUID()
                                        .toString(),

                            name =
                                name.trim(),

                            type =
                                type.trim(),

                            lender =
                                lender.trim(),

                            principal =
                                principalAmount,

                            interestRate =
                                interestRate,

                            interestAmount =
                                interestValue,

                            totalPayable =
                                total,

                            installments =
                                scheduleCount,

                            monthlyPayment =
                                displayedMonthly,

                            repaymentMode = repaymentMode,

                            startDate =
                                existing?.startDate
                                    ?: System.currentTimeMillis(),

                            dueDay =
                                day,

                            reminderDays =
                                parseReminders(reminders),

                            payments =
                                payments,

                            archived = existing?.archived ?: false,
                            financingSource = financingSource,
                            receivedMethod = receivedMethod,
                            agreementReference = agreementReference.trim(),
                            financingNotes = financingNotes.trim(),
                            financingChannel = financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" },
                            financingAccountName = financingAccountName.trim(), financingAccountNumber = financingAccountNumber.trim(),
                            financingBranch = financingBranch.trim(), financingRoutingNumber = financingRouting.trim(),
                            financingReference = financingReference.trim(), financingMethodDetails = financingMethodDetails.trim(),
                            attachments = attachments
                        )

                    if (existing == null) {
                        viewModel.addLoan(loan)
                        done()
                    } else {
                        pendingUpdate = loan
                    }
                }
            },

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Text(
                if (existing == null) {
                    "Save Loan"
                } else {
                    "Update Loan"
                }
            )
        }
        }

    }

    pendingUpdate?.let { loan ->
        ConfirmationDialog(
            request = ConfirmationRequest(
                title = "Update Loan?",
                message = "The new values will replace this loan plan. Changes to amounts, repayments, previous payments, or dates may rebuild its repayment schedule.",
                confirmLabel = "Update",
                onConfirm = {
                    viewModel.updateLoan(loan)
                    done()
                }
            ),
            onDismiss = { pendingUpdate = null }
        )
    }
}


