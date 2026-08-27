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
// EMI FORM
// ============================================================

internal val EmiCategoryOptions = listOf(
    "Electronics", "Appliances", "Furniture", "Vehicle", "Mobile / Computer",
    "Education", "Medical", "Home Improvement", "Other"
)

internal val LoanTypeOptions = listOf(
    "Personal Loan", "Bank Loan", "Office Loan", "Salary Loan", "Home Loan",
    "Vehicle Loan", "Education Loan", "Business Loan", "Other"
)

@Composable
fun EmiForm(
    viewModel: FinanceViewModel,
    existing: EmiItem?,
    done: () -> Unit
) {

    val viewOnly = existing?.let { it.archived || emiCompleted(it) } == true
    var pendingUpdate by remember { mutableStateOf<EmiItem?>(null) }

    var name by remember {
        mutableStateOf(
            existing?.name ?: ""
        )
    }

    var category by remember {
        mutableStateOf(
            existing?.category ?: "Electronics"
        )
    }
    var categoryChoice by remember(existing?.id) {
        mutableStateOf(if ((existing?.category ?: "Electronics") in EmiCategoryOptions.dropLast(1)) existing?.category ?: "Electronics" else "Other")
    }

    var seller by remember {
        mutableStateOf(
            existing?.seller ?: ""
        )
    }

    var financingSource by remember { mutableStateOf(existing?.financingSource ?: "Shop or seller") }
    var receivedMethod by remember { mutableStateOf(existing?.receivedMethod ?: "Direct purchase financing") }
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

    var price by remember {
        mutableStateOf(
            existing?.price?.toString() ?: ""
        )
    }

    var downPayment by remember {
        mutableStateOf(
            existing?.downPayment?.toString()
                ?: "0"
        )
    }

    var interestRate by remember {
        mutableStateOf(
            existing?.interestRate?.toString()
                ?: "0"
        )
    }

    var interestAmount by remember {
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

    var dueDay by remember {
        mutableStateOf(
            existing?.dueDay?.toString()
                ?: "10"
        )
    }

    var previousPaid by remember {
        mutableStateOf(
            existing?.payments
                ?.count {
                    it.paidDate != null
                }
                ?.toString()
                ?: "0"
        )
    }

    val originalPreviousPaid = existing?.payments?.count { it.paidDate != null } ?: 0

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

    val purchasePrice =
        price.toDoubleOrNull() ?: 0.0

    val down =
        downPayment.toDoubleOrNull() ?: 0.0

    val rate =
        interestRate.toDoubleOrNull() ?: 0.0

    val financed =
        max(
            0.0,
            purchasePrice - down
        )

    val enteredInterest =
        interestAmount.toDoubleOrNull()
            ?: 0.0

    val calculatedInterest =
        if (enteredInterest > 0) {
            enteredInterest
        } else {
            financed * rate / 100.0
        }

    val total =
        financed + calculatedInterest

    val count =
        installments.toIntOrNull() ?: 0

    val monthly =
        if (count > 0) {
            total / count
        } else {
            0.0
        }

    val hasUnsavedChanges = !viewOnly && (
        name != (existing?.name ?: "") ||
        category != (existing?.category ?: "Electronics") ||
        seller != (existing?.seller ?: "") ||
        financingSource != (existing?.financingSource ?: "Shop or seller") ||
        receivedMethod != (existing?.receivedMethod ?: "Direct purchase financing") ||
        agreementReference != (existing?.agreementReference ?: "") ||
        financingNotes != (existing?.financingNotes ?: "") ||
        financingChannel != (existing?.financingChannel ?: "") || financingAccountName != (existing?.financingAccountName ?: "") ||
        financingAccountNumber != (existing?.financingAccountNumber ?: "") || financingBranch != (existing?.financingBranch ?: "") ||
        financingRouting != (existing?.financingRoutingNumber ?: "") || financingReference != (existing?.financingReference ?: "") ||
        financingMethodDetails != (existing?.financingMethodDetails ?: "") ||
        attachments != (existing?.attachments ?: emptyList<Attachment>()) ||
        price != (existing?.price?.toString() ?: "") ||
        downPayment != (existing?.downPayment?.toString() ?: "0") ||
        interestRate != (existing?.interestRate?.toString() ?: "0") ||
        interestAmount != (existing?.interestAmount?.toString() ?: "0") ||
        installments != (existing?.installments?.toString() ?: "12") ||
        dueDay != (existing?.dueDay?.toString() ?: "10") ||
        previousPaid != originalPreviousPaid.toString() ||
        reminders != (existing?.reminderDays?.joinToString(",") ?: "7,3,1,0")
    )

    FormColumn(
        title =
            if (existing == null) {
                "Add EMI"
            } else if (viewOnly) {
                "EMI Details"
            } else {
                "Edit EMI"
            },
        readOnly = viewOnly,
        onBack = done,
        hasUnsavedChanges = hasUnsavedChanges
    ) {

        if (viewOnly && existing != null) {
            Text(
                if (existing.archived) {
                    "This EMI is archived and view-only. Restore it to make changes."
                } else {
                    "This EMI is completed and view-only. Reopen it to make changes."
                },
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }

        Field(
            "Item name",
            name
        ) {
            name = it
        }

        ChoiceDropdown("Category", categoryChoice, EmiCategoryOptions) { selected ->
            categoryChoice = selected
            if (selected != "Other") category = selected else if (category in EmiCategoryOptions) category = ""
        }
        if (categoryChoice == "Other") Field("Custom category", category) { category = it }

        Field(
            "Seller / Provider",
            seller
        ) {
            seller = it
        }

        Text("Financing details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ChoiceDropdown(
            "Financing source",
            financingSource,
            listOf("Shop or seller", "Bank", "Finance company", "Employer", "Credit card", "Friend or family", "Other")
        ) { financingSource = it }
        ChoiceDropdown(
            "How item/finance was received",
            receivedMethod,
            listOf("Direct purchase financing", "Cash", "Bank transfer", "Mobile banking", "Salary arrangement", "Other")
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
            "Purchase price",
            price
        ) {
            price = it
        }

        Field(
            "Down payment",
            downPayment
        ) {
            downPayment = it
        }

        Field(
            "Interest rate % (optional)",
            interestRate
        ) {
            interestRate = it
        }

        Field(
            "Fixed interest amount (optional)",
            interestAmount
        ) {
            interestAmount = it
        }

        Field(
            "Installments",
            installments
        ) {
            installments = it
        }

        Field(
            "Monthly due day 1-28",
            dueDay
        ) {
            dueDay = it
        }

        Field(
            "Previous installments already paid",
            previousPaid
        ) {
            previousPaid = it
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
                    Modifier.padding(FinanceSpacing.md)
            ) {

                Text(
                    "Calculated",
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    "Financed: ${money(financed)}"
                )

                Text(
                    "Interest: ${money(calculatedInterest)}"
                )

                Text(
                    "Total payable: ${money(total)}"
                )

                Text(
                    "Monthly: ${money(monthly)}"
                )
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

                val itemName =
                    name.trim()

                val previous =
                    previousPaid.toIntOrNull()
                        ?: 0

                val day =
                    dueDay.toIntOrNull()
                        ?: 0

                error = when {

                    itemName.isEmpty() ->
                        "Enter item name."

                    itemName.length > 100 ->
                        "Item name must be 100 characters or less."

                    category.trim().length !in 2..60 || seller.trim().length > 100 ->
                        "Enter a valid category; seller/provider must be 100 characters or less."

                    paymentMethodValidation(receivedMethod, financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails).isNotBlank() ->
                        paymentMethodValidation(receivedMethod, financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails)

                    !purchasePrice.isFinite() || purchasePrice <= 0 || purchasePrice > 999_999_999.99 ->
                        "Enter a valid price."

                    !rate.isFinite() || !enteredInterest.isFinite() || rate !in 0.0..100.0 || enteredInterest < 0 ->
                        "Interest rate must be 0-100 and interest amount cannot be negative."

                    rate > 0 && enteredInterest > 0 ->
                        "Use either interest rate or fixed interest amount, not both."

                    down < 0 ||
                            down >= purchasePrice ->
                        "Check down payment."

                    count !in 1..600 ->
                        "Installments must be between 1 and 600."

                    previous !in 0..count ->
                        "Previous paid must be 0 to total installments."

                    day !in 1..28 ->
                        "Due day must be 1-28."

                    parseReminders(reminders)
                        .isEmpty() ->
                        "Enter at least one reminder day."

                    else ->
                        ""
                }

                if (error.isEmpty()) {

                    val previousChanged = existing != null && previous != originalPreviousPaid
                    val firstDue =
                        existing
                            ?.payments
                            ?.minOfOrNull {
                                it.dueDate
                            }
                            ?.takeIf { !previousChanged }
                            ?: addMonths(currentMonthDueDate(day), -previous)

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
                        (1..count).map { number ->

                            Payment(

                                number = number,

                                dueDate =
                                    addMonths(
                                        firstDue,
                                        number - 1
                                    ),

                                amount =
                                    monthly,

                                paidDate =
                                    oldPaid[number]
                                        ?.paidDate
                                        ?: if (
                                            (existing == null || previousChanged) &&
                                            number <= previous
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

                    val item =
                        EmiItem(

                            id =
                                existing?.id
                                    ?: UUID.randomUUID()
                                        .toString(),

                            name =
                                itemName,

                            category =
                                category.trim(),

                            seller =
                                seller.trim(),

                            price =
                                purchasePrice,

                            downPayment =
                                down,

                            financedAmount =
                                financed,

                            interestRate =
                                rate,

                            interestAmount =
                                calculatedInterest,

                            totalPayable =
                                total,

                            installments =
                                count,

                            monthlyPayment =
                                monthly,

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
                        viewModel.addEmi(item)
                        done()
                    } else {
                        pendingUpdate = item
                    }
                }
            },

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Text(
                if (existing == null) {
                    "Save EMI"
                } else {
                    "Update EMI"
                }
            )
        }
        }

    }

    pendingUpdate?.let { item ->
        ConfirmationDialog(
            request = ConfirmationRequest(
                title = "Update EMI?",
                message = "The new values will replace this EMI plan. Changes to amounts, installments, previous payments, or dates may rebuild its payment schedule.",
                confirmLabel = "Update",
                onConfirm = {
                    viewModel.updateEmi(item)
                    done()
                }
            ),
            onDismiss = { pendingUpdate = null }
        )
    }
}

