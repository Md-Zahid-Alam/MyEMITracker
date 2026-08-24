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
// MAIN APP
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceApp(
    onLogout: () -> Unit,
    onPasswordChange: (String) -> Unit,
    verifyPassword: (String) -> Boolean,
    themeMode: String,
    onThemeChange: (String) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    country: String,
    currencyCode: String,
    currencySymbol: String,
    onRegionChange: (String, String, String) -> Unit,
    onCustomPaymentListsChange: (List<String>, List<String>) -> Unit
) {

    val context =
        LocalContext.current

    val viewModel =
        financeViewModel(context)

    var tab by remember {
        mutableStateOf(0)
    }

    var selectedType by remember {
        mutableStateOf("")
    }

    var selectedId by remember {
        mutableStateOf("")
    }

    var paymentSection by remember {
        mutableStateOf("")
    }
    var showBackupPasswordDialog by remember { mutableStateOf(false) }
    var backupPassword by remember { mutableStateOf("") }
    var backupPasswordConfirm by remember { mutableStateOf("") }
    var backupError by remember { mutableStateOf("") }
    var pendingBackupContent by remember { mutableStateOf("") }
    var restoreContent by remember { mutableStateOf<String?>(null) }
    var restorePassword by remember { mutableStateOf("") }
    var restoreError by remember { mutableStateOf("") }
    var restoreIsLegacy by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedType.isNotBlank() || tab != 0) {
        if (selectedType.isNotBlank()) {
            selectedType = when (selectedType) {
            "emi_history", "emi_documents", "emi_financing", "emi_information", "emi_payment" -> "emi_detail"
            "loan_history", "loan_documents", "loan_financing", "loan_information", "loan_payment" -> "loan_detail"
            "debt_history", "debt_documents", "debt_financing", "debt_information", "debt_payment" -> "debt_detail"
                "emi" -> if (selectedId.isNotBlank()) "emi_detail" else ""
                "loan" -> if (selectedId.isNotBlank()) "loan_detail" else ""
                "debt" -> if (selectedId.isNotBlank()) "debt_detail" else ""
                else -> ""
            }
            if (selectedType.isBlank()) selectedId = ""
        } else if (tab == 1 && paymentSection.isNotBlank()) {
            paymentSection = ""
        } else {
            tab = 0
        }
    }

    val backupLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument(
                "application/octet-stream"
            )
        ) { uri ->

            if (uri != null) {

                context.contentResolver
                    .openOutputStream(uri)
                    ?.use { output ->

                        output.write(pendingBackupContent.toByteArray())
                    }
            }
            pendingBackupContent = ""
        }

    val restoreLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                runCatching {
                    val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        ?: throw IllegalArgumentException("Unable to read the selected backup.")
                    require(content.length <= 40_000_000) { "Backup file is too large." }
                    content
                }.onSuccess { content ->
                    restoreContent = content
                    restoreIsLegacy = runCatching { JSONObject(content).optString("format") }.getOrNull() != BACKUP_FORMAT
                    restorePassword = ""
                    restoreError = ""
                }.onFailure { restoreError = it.message ?: "Unable to read the backup." }
            }
        }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (selectedType.isBlank()) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {

                val labels =
                    listOf(
                        "Home",
                        "Payments",
                        "Expenses",
                        "Reports",
                        "Settings"
                    )

                labels.forEachIndexed { index, label ->

                    NavigationBarItem(

                        selected = tab == index,

                        onClick = {
                            tab = index
                            if (index == 1) paymentSection = ""
                            selectedType = ""
                            selectedId = ""
                        },

                        icon = {

                            val icon =
                                when (index) {
                                    0 -> Icons.Default.Home
                                    1 -> Icons.Default.AccountBalance
                                    2 -> Icons.Default.CreditCard
                                    3 -> Icons.Default.Description
                                    else -> Icons.Default.Settings
                                }

                            Icon(
                                icon,
                                contentDescription = label
                            )
                        },

                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            }
        },

        floatingActionButton = {

            if (selectedType.isBlank() && (tab == 2 || (tab == 1 && paymentSection.isNotBlank()))) {

                FloatingActionButton(

                    onClick = {

                        selectedType =
                            when {
                                tab == 2 -> "expense"
                                paymentSection == "EMI" -> "emi"
                                paymentSection == "Loans" -> "loan"
                                else -> "debt"
                            }

                        selectedId = ""
                    }
                ) {

                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = if (selectedType.isBlank() && tab == 0) FinanceLayout.dashboardContentMax else FinanceLayout.phoneContentMax)
                    .fillMaxSize()
            ) {
            when {

                selectedType.endsWith("_detail") -> {
                    val kind = selectedType.removeSuffix("_detail")
                    PaymentPlanDetail(
                        viewModel = viewModel,
                        kind = kind,
                        id = selectedId,
                        onBack = { selectedType = ""; selectedId = "" },
                        onOpen = { page -> selectedType = if (page == "edit") kind else "${kind}_$page" }
                    )
                }

                selectedType.endsWith("_history") -> {
                    PaymentPlanHistory(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_history"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_history") + "_detail" }
                    )
                }

                selectedType.endsWith("_documents") -> {
                    PaymentPlanDocuments(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_documents"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_documents") + "_detail" }
                    )
                }

                selectedType.endsWith("_financing") -> {
                    PaymentPlanFinancing(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_financing"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_financing") + "_detail" }
                    )
                }

                selectedType.endsWith("_information") -> {
                    PaymentPlanInformation(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_information"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_information") + "_detail" }
                    )
                }

                selectedType.endsWith("_payment") -> {
                    PaymentPlanPayment(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_payment"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_payment") + "_detail" }
                    )
                }

                selectedType == "emi" -> {

                    EmiForm(
                        viewModel,
                        viewModel.data.emis.find {
                            it.id == selectedId
                        },
                        done = {
                            if (selectedId.isBlank()) selectedType = "" else selectedType = "emi_detail"
                        }
                    )
                }

                selectedType == "loan" -> {

                    LoanForm(
                        viewModel,
                        viewModel.data.loans.find {
                            it.id == selectedId
                        },
                        done = {
                            if (selectedId.isBlank()) selectedType = "" else selectedType = "loan_detail"
                        }
                    )
                }

                selectedType == "debt" -> {

                    DebtForm(
                        viewModel,
                        viewModel.data.debts.find {
                            it.id == selectedId
                        },
                        initialDirection = if (paymentSection == "DebtsOwed") "Owed to Me" else "I Owe",
                        done = {
                            if (selectedId.isBlank()) selectedType = "" else selectedType = "debt_detail"
                        }
                    )
                }

                selectedType == "expense" -> {
                    ExpenseForm(
                        viewModel,
                        viewModel.data.expenses.find {
                            it.id == selectedId
                        },
                        done = {
                            selectedType = ""
                            selectedId = ""
                        }
                    )
                }

                selectedType == "password" -> {

                    ChangePasswordForm(
                        onChange = onPasswordChange,
                        verifyCurrent = verifyPassword,
                        done = {
                            selectedType = ""
                        }
                    )
                }

                selectedType == "about" -> {
                    AboutScreen(done = { selectedType = "" })
                }

                selectedType == "receipt_profile" -> {
                    ReceiptProfileForm(
                        existing = viewModel.data.receiptProfile,
                        onSave = viewModel::updateReceiptProfile,
                        done = { selectedType = "" }
                    )
                }

                selectedType == "country" -> {
                    CountrySettingsScreen(
                        country = country,
                        currencyCode = currencyCode,
                        currencySymbol = currencySymbol,
                        onRegionChange = onRegionChange,
                        onCustomPaymentListsChange = onCustomPaymentListsChange,
                        done = { selectedType = "" }
                    )
                }

                tab == 0 -> Dashboard(viewModel)

                tab == 1 -> PaymentsHub(
                    viewModel = viewModel,
                    section = paymentSection,
                    onSectionChange = { paymentSection = it },
                    onOpen = { type, id ->
                        selectedType = type
                        selectedId = id
                    }
                )

                tab == 2 -> ExpenseList(
                    viewModel,
                    onOpen = {
                        selectedId = it
                        selectedType = "expense"
                    }
                )

                tab == 3 -> Reports(viewModel)

                else -> SettingsScreen(
                    onChangePassword = { selectedType = "password" },
                    onBackup = {
                        backupPassword = ""
                        backupPasswordConfirm = ""
                        backupError = ""
                        showBackupPasswordDialog = true
                    },
                    onRestore = {
                        restoreLauncher.launch(
                            arrayOf("application/json", "text/plain", "*/*")
                        )
                    },
                    onLock = onLogout,
                    onAbout = { selectedType = "about" },
                    onReceiptProfile = { selectedType = "receipt_profile" },
                    onCountry = { selectedType = "country" },
                    themeMode = themeMode,
                    onThemeChange = onThemeChange,
                    language = language,
                    onLanguageChange = onLanguageChange,
                    country = country,
                    currencyCode = currencyCode,
                    currencySymbol = currencySymbol,
                    onRegionChange = onRegionChange,
                    onCustomPaymentListsChange = onCustomPaymentListsChange
                )
            }
            }
        }
    }

    if (showBackupPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showBackupPasswordDialog = false },
            title = { Text("Export Encrypted Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose a backup password. You will need it to restore this file on any phone.")
                    Field("Backup password", backupPassword, isPassword = true) { backupPassword = it }
                    Field("Confirm backup password", backupPasswordConfirm, isPassword = true) { backupPasswordConfirm = it }
                    if (backupError.isNotBlank()) Text(backupError, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    backupError = when {
                        backupPassword.length < 6 -> "Use at least 6 characters for the backup password."
                        backupPassword != backupPasswordConfirm -> "Backup passwords do not match."
                        else -> ""
                    }
                    if (backupError.isBlank()) {
                        runCatching { viewModel.backup(backupPassword) }
                            .onSuccess {
                                pendingBackupContent = it
                                showBackupPasswordDialog = false
                                val stamp = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                                backupLauncher.launch("MyFinanceTracker_Backup_$stamp.mftbackup")
                            }
                            .onFailure { backupError = it.message ?: "Backup could not be created." }
                    }
                }) { Text("Export") }
            },
            dismissButton = { TextButton(onClick = { showBackupPasswordDialog = false }) { Text("Cancel") } }
        )
    }

    restoreContent?.let { content ->
        AlertDialog(
            onDismissRequest = { restoreContent = null },
            title = { Text(if (restoreIsLegacy) "Restore Legacy Backup?" else "Restore Encrypted Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (restoreIsLegacy) "This older JSON backup is readable and not encrypted. Import it only if you trust its source. Current records will be replaced."
                        else "Enter the backup password. Current records will be replaced only after authentication and validation."
                    )
                    if (!restoreIsLegacy) Field("Backup password", restorePassword, isPassword = true) { restorePassword = it }
                    if (restoreError.isNotBlank()) Text(restoreError, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    runCatching { viewModel.restore(content, restorePassword, restoreIsLegacy) }
                        .onSuccess {
                            restoreContent = null
                            restorePassword = ""
                            restoreError = ""
                        }
                        .onFailure { restoreError = it.message ?: "Restore failed." }
                }) { Text(if (restoreIsLegacy) "Import Legacy Backup" else "Restore") }
            },
            dismissButton = { TextButton(onClick = { restoreContent = null }) { Text("Cancel") } }
        )
    }
}


