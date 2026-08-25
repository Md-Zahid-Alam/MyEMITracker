package com.mdzahidalam.myfinancetracker.core.ui
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
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
// FORM COMPONENTS
// ============================================================

internal fun attachmentName(context: Context, uri: android.net.Uri): String {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) return cursor.getString(0) ?: "Document"
    }
    return uri.lastPathSegment ?: "Document"
}

internal fun readAttachment(context: Context, uri: android.net.Uri): Attachment {
    val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
        val output = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        var total = 0
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            total += count
            require(total <= 5 * 1024 * 1024) { "Each document must be 5 MB or smaller." }
            output.write(buffer, 0, count)
        }
        output.toByteArray()
    } ?: throw IllegalArgumentException("Unable to read the selected document.")
    return Attachment(
        name = attachmentName(context, uri),
        mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream",
        contentBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    )
}

internal fun openAttachment(context: Context, attachment: Attachment) {
    val safeName = attachment.name.replace(Regex("[^A-Za-z0-9._-]"), "_").ifBlank { "document" }
    val folder = File(context.cacheDir, "shared_documents").apply { mkdirs() }
    folder.listFiles()?.forEach { it.delete() }
    val file = File(folder, safeName)
    file.writeBytes(Base64.decode(attachment.contentBase64, Base64.NO_WRAP))
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
    context.startActivity(
        Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, attachment.mimeType)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    )
}

internal fun sharePdf(context: Context, fileName: String, text: String) {
    val folder = File(context.cacheDir, "shared_documents").apply { mkdirs() }
    folder.listFiles()?.forEach { it.delete() }
    val safeName = fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
    val file = File(folder, safeName).apply { createNewFile() }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
    writePdfToUri(context, uri, text)
    context.startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SEND)
                .setType("application/pdf")
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
            "Share PDF"
        )
    )
}

@Composable
fun AttachmentSection(
    attachments: List<Attachment>,
    maxFiles: Int,
    title: String = "Supporting documents (optional)",
    mimeTypes: Array<String> = arrayOf("image/*", "application/pdf"),
    buttonLabel: String = "Attach image or PDF",
    onChange: (List<Attachment>) -> Unit
) {
    val context = LocalContext.current
    var error by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            runCatching {
                val available = (maxFiles - attachments.size).coerceAtLeast(0)
                require(available > 0) { "Maximum $maxFiles documents allowed." }
                attachments + uris.take(available).map { readAttachment(context, it) }
            }.onSuccess {
                onChange(it)
                error = ""
            }.onFailure { error = it.message ?: "Could not attach the document." }
        }
    }

    Text(title, fontWeight = FontWeight.Bold)
    attachments.forEach { attachment ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(FinanceSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(attachment.name, modifier = Modifier.weight(1f))
                TextButton(onClick = { runCatching { openAttachment(context, attachment) } }) { Text("Open") }
                if (!LocalFormReadOnly.current) {
                    TextButton(onClick = { onChange(attachments.filterNot { it.id == attachment.id }) }) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
    if (!LocalFormReadOnly.current && attachments.size < maxFiles) {
        OutlinedButton(
            onClick = { launcher.launch(mimeTypes) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("$buttonLabel (${attachments.size}/$maxFiles)") }
    }
    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    Text("Documents are included inside encrypted app data and encrypted backups.", style = MaterialTheme.typography.bodySmall)
}

@Composable
fun SettingsScreen(
    onChangePassword: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onLock: () -> Unit,
    onAbout: () -> Unit,
    onReceiptProfile: () -> Unit,
    onCountry: () -> Unit,
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
    FormColumn("Settings") {
        Text("Personalize your experience and protect your local records.", color = MaterialTheme.colorScheme.onSurfaceVariant)

        PremiumSectionHeader("Language and appearance")
        ChoiceDropdown("Language", if (language == "BN") "Bangla" else "English", listOf("English", "Bangla")) {
            onLanguageChange(if (it == "Bangla") "BN" else "EN")
        }

        ChoiceDropdown(
            label = "Theme",
            value = when (themeMode) {
                "LIGHT" -> "Light"
                "DARK" -> "Dark"
                else -> "System default"
            },
            options = listOf("System default", "Light", "Dark"),
            onSelect = { label ->
                onThemeChange(
                    when (label) {
                        "Light" -> "LIGHT"
                        "Dark" -> "DARK"
                        else -> "SYSTEM"
                    }
                )
            }
        )

        DetailNavigationButton("Country and currency", "$country • $currencyCode ($currencySymbol)", onCountry)

        PremiumSectionHeader("Security and local data")
        DetailNavigationButton("Change Password", "Update the password used to unlock this app", onChangePassword)

        DetailNavigationButton("Export Encrypted Backup", "Create a password-protected copy of records and documents", onBackup)

        DetailNavigationButton("Restore Backup", "Restore a trusted My Finance Tracker backup", onRestore)

        PremiumSectionHeader("Documents and app")
        DetailNavigationButton("Receipt Profile", "Your identity and signature on generated documents", onReceiptProfile)

        DetailNavigationButton("About My Finance Tracker", "Version, ownership and privacy information", onAbout)

        Button(
            onClick = onLock,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Lock App")
        }

        Text(
            "Encrypted backup protects all records and attached documents with a password you choose.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CountrySettingsScreen(
    country: String,
    currencyCode: String,
    currencySymbol: String,
    onRegionChange: (String, String, String) -> Unit,
    onCustomPaymentListsChange: (List<String>, List<String>) -> Unit,
    done: () -> Unit
) {
    val initialCountry = remember(country) {
        CountryCatalog.findByName(country) ?: CountryCatalog.findByName("Bangladesh") ?: CountryCatalog.all.first()
    }
    var selectedCountry by remember(country) { mutableStateOf(initialCountry) }
    var draftCurrencyCode by remember(currencyCode) { mutableStateOf(currencyCode) }
    var draftCurrencySymbol by remember(currencySymbol) { mutableStateOf(currencySymbol) }
    var customBanks by remember { mutableStateOf(AppLocaleState.customBanks) }
    var customProviders by remember { mutableStateOf(AppLocaleState.customProviders) }
    var newBank by remember { mutableStateOf("") }
    var newProvider by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var showRegionWarning by remember { mutableStateOf(false) }

    FormColumn("Country", onBack = done) {
        Text("Country and currency", fontWeight = FontWeight.Bold)
        SearchableCountryPicker(selectedCountry, LocalAppLanguage.current) {
            selectedCountry = it
            if (it.currencyCode.isNotBlank()) draftCurrencyCode = it.currencyCode
            if (it.currencySymbol.isNotBlank()) draftCurrencySymbol = it.currencySymbol
            error = ""
        }
        Text("Default currency is filled automatically. You may change it for a special account or territory.", style = MaterialTheme.typography.bodySmall)
        Field("Currency code (for example USD)", draftCurrencyCode) { draftCurrencyCode = it.uppercase().take(3); error = "" }
        Field("Currency symbol", draftCurrencySymbol) { draftCurrencySymbol = it.take(4); error = "" }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        OutlinedButton(onClick = {
            error = when {
                draftCurrencyCode.length != 3 || !draftCurrencyCode.all { it.isLetter() } -> "Currency code must contain three letters."
                draftCurrencySymbol.isBlank() -> "Enter a currency symbol."
                else -> ""
            }
            if (error.isBlank()) showRegionWarning = true
        }, modifier = Modifier.fillMaxWidth()) { Text("Apply country and currency") }
        Text("Changing currency changes the displayed symbol only; existing amounts are not converted.", style = MaterialTheme.typography.bodySmall)

        Text("My payment institutions", fontWeight = FontWeight.Bold)
        Text(if (selectedCountry.name == "Bangladesh") "Bangladesh banks and mobile banking services are already included. Add any extra services below." else "Add only the banks and payment services you use. They stay on this device.")
        Field("Add bank", newBank) { newBank = it }
        OutlinedButton(onClick = {
            val value = newBank.trim()
            if (value.length in 2..100 && customBanks.none { it.equals(value, true) }) { customBanks = customBanks + value; newBank = ""; onCustomPaymentListsChange(customBanks, customProviders) }
        }, modifier = Modifier.fillMaxWidth()) { Text("Add bank") }
        customBanks.forEach { bank -> TextButton(onClick = { customBanks = customBanks - bank; onCustomPaymentListsChange(customBanks, customProviders) }) { Text("$bank  •  Remove") } }
        Field("Add mobile banking provider", newProvider) { newProvider = it }
        OutlinedButton(onClick = {
            val value = newProvider.trim()
            if (value.length in 2..100 && customProviders.none { it.equals(value, true) }) { customProviders = customProviders + value; newProvider = ""; onCustomPaymentListsChange(customBanks, customProviders) }
        }, modifier = Modifier.fillMaxWidth()) { Text("Add provider") }
        customProviders.forEach { provider -> TextButton(onClick = { customProviders = customProviders - provider; onCustomPaymentListsChange(customBanks, customProviders) }) { Text("$provider  •  Remove") } }
    }
    if (showRegionWarning) ConfirmationDialog(
        request = ConfirmationRequest(
            title = "Change country and currency?",
            message = "Existing money values will not be converted. Only the country, currency code, symbol, and available payment choices will change.",
            confirmLabel = "Change",
            onConfirm = { onRegionChange(selectedCountry.name, draftCurrencyCode, draftCurrencySymbol) }
        ),
        onDismiss = { showRegionWarning = false }
    )
}

@Composable
fun ReceiptProfileForm(existing: ReceiptProfile, onSave: (ReceiptProfile) -> Unit, done: () -> Unit) {
    var fullName by remember { mutableStateOf(existing.fullName) }
    var phone by remember { mutableStateOf(existing.phone) }
    var email by remember { mutableStateOf(existing.email) }
    var address by remember { mutableStateOf(existing.address) }
    var signature by remember { mutableStateOf(existing.signature?.let { listOf(it) } ?: emptyList()) }
    var error by remember { mutableStateOf("") }
    val changed = fullName != existing.fullName || phone != existing.phone || email != existing.email || address != existing.address || signature.firstOrNull() != existing.signature
    FormColumn("Receipt Profile", onBack = done, hasUnsavedChanges = changed) {
        Text("This identity appears as the issuer on payment receipts and payment requests. App ownership remains separate.")
        Field("Full name", fullName) { fullName = it }
        Field("Phone (optional)", phone) { phone = it }
        Field("Email (optional)", email) { email = it }
        Field("Address (optional)", address) { address = it }
        Text("Signature (optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Attach a PNG or JPEG signature image. It is stored with your encrypted app data and added to generated receipts and payment requests.", style = MaterialTheme.typography.bodySmall)
        AttachmentSection(
            signature,
            maxFiles = 1,
            title = "Signature image (optional)",
            mimeTypes = arrayOf("image/*"),
            buttonLabel = "Attach signature image"
        ) { selected ->
            signature = selected.filter { it.mimeType in listOf("image/png", "image/jpeg", "image/jpg", "image/webp") }.take(1)
            if (selected.isNotEmpty() && signature.isEmpty()) error = "Signature must be a PNG, JPEG, or WebP image."
        }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        Button(
            onClick = {
                val cleanPhone = phone.trim()
                val cleanEmail = email.trim()
                val phoneDigits = cleanPhone.filter { it.isDigit() }
                error = when {
                    fullName.isBlank() -> "Enter your full name."
                    fullName.trim().length !in 2..100 -> "Full name must be between 2 and 100 characters."
                    cleanPhone.isNotBlank() && (!Regex("^\\+?[0-9][0-9 -]*$").matches(cleanPhone) || phoneDigits.length !in 7..15) -> "Enter a valid phone number containing 7-15 digits."
                    cleanEmail.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() -> "Enter a valid email address."
                    address.length > 250 -> "Address must be 250 characters or less."
                    else -> ""
                }
                if (error.isBlank()) {
                    onSave(ReceiptProfile(fullName.trim(), cleanPhone, cleanEmail, address.trim(), signature.firstOrNull()))
                    done()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Receipt Profile") }
    }
}

@Composable
fun AboutScreen(done: () -> Unit) {
    FormColumn(title = "About", onBack = done) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FinanceSpacing.sm)
        ) {
            Image(
                painter = painterResource(com.mdzahidalam.myfinancetracker.R.drawable.app_logo),
                contentDescription = "My Finance Tracker logo",
                modifier = Modifier.size(112.dp)
            )
            Text("My Finance Tracker", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Version 11.0")
            Spacer(Modifier.height(8.dp))
            Text("Created and owned by", color = MaterialTheme.colorScheme.secondary)
            Text("Md. Zahid Alam", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(
                "A private offline application for tracking EMI plans, loans, debts, and daily expenses.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("© 2026 Md. Zahid Alam")
            Text("All rights reserved.")
        }
    }
}

@Composable
fun ChangePasswordForm(
    onChange: (String) -> Unit,
    verifyCurrent: (String) -> Boolean,
    done: () -> Unit
) {
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    FormColumn(
        title = "Change Password",
        onBack = done,
        hasUnsavedChanges = current.isNotBlank() || newPassword.isNotBlank() || confirm.isNotBlank()
    ) {
        Text("Your new password replaces the old one securely.")
        Field("Current password", current, isPassword = true) { current = it }
        Field("New password", newPassword, isPassword = true) { newPassword = it }
        Field("Confirm new password", confirm, isPassword = true) { confirm = it }
        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = {
                error = when {
                    current.isBlank() -> "Enter your current password."
                    !verifyCurrent(current) -> "Current password is incorrect."
                    newPassword.length < 4 -> "Use at least 4 characters."
                    newPassword != confirm -> "New passwords do not match."
                    else -> ""
                }
                if (error.isEmpty()) {
                    onChange(newPassword)
                    done()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Change Password") }
        OutlinedButton(onClick = done, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}

@Composable
fun ChoiceDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = FinanceShapes.medium,
            enabled = !LocalFormReadOnly.current
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, style = MaterialTheme.typography.bodyLarge)
                }
                Text("⌄", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

internal val BangladeshBanks = listOf(
    "Sonali Bank", "Janata Bank", "Agrani Bank", "Rupali Bank", "BRAC Bank",
    "Dutch-Bangla Bank", "Islami Bank Bangladesh", "The City Bank", "Eastern Bank",
    "Prime Bank", "Pubali Bank", "Bank Asia", "Southeast Bank", "Standard Chartered Bank",
    "HSBC Bangladesh", "Mutual Trust Bank", "United Commercial Bank", "IFIC Bank",
    "NCC Bank", "Mercantile Bank", "Social Islami Bank", "Al-Arafah Islami Bank",
    "Shahjalal Islami Bank", "Jamuna Bank", "ONE Bank", "Dhaka Bank", "Trust Bank",
    "Community Bank Bangladesh", "Other bank"
)

internal fun availableBanks(): List<String> =
    if (AppLocaleState.country == "Bangladesh") (BangladeshBanks + AppLocaleState.customBanks).distinct()
    else (AppLocaleState.customBanks + "Other bank").distinct()

internal fun availableProviders(): List<String> =
    if (AppLocaleState.country == "Bangladesh") (listOf("bKash", "Nagad", "Rocket", "Upay") + AppLocaleState.customProviders + "Other provider").distinct()
    else (AppLocaleState.customProviders + "Other provider").distinct()

internal fun defaultBank() = availableBanks().firstOrNull() ?: "Other bank"
internal fun defaultProvider() = availableProviders().firstOrNull() ?: "Other provider"

internal fun rememberCustomBank(context: Context, enteredName: String): String? {
    val name = enteredName.trim().replace(Regex("\\s+"), " ")
    if (name.length !in 2..100 || name.equals("Other bank", ignoreCase = true)) return null
    availableBanks().firstOrNull { it.equals(name, ignoreCase = true) }?.let { return it }
    val updated = (AppLocaleState.customBanks + name).distinctBy { it.lowercase(Locale.US) }.sortedBy { it.lowercase(Locale.US) }
    AppLocaleState.customBanks = updated
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putStringSet(KEY_CUSTOM_BANKS, updated.toSet()).apply()
    return name
}

@Composable
internal fun SaveCustomBankAction(bankName: String, onSaved: (String) -> Unit) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = { rememberCustomBank(context, bankName)?.let(onSaved) },
        modifier = Modifier.fillMaxWidth(),
        enabled = !LocalFormReadOnly.current && bankName.trim().length in 2..100
    ) { Text("Save this bank for future use") }
}

@Composable
internal fun SearchableBankPicker(value: String, onSelect: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    OutlinedButton(onClick = { visible = true }, modifier = Modifier.fillMaxWidth()) {
        Text("Bank name: ${value.ifBlank { defaultBank() }}")
    }
    if (visible) {
        val matches = availableBanks().filter { query.isBlank() || it.contains(query.trim(), ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { visible = false },
            title = { Text("Select Bank") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)) {
                    OutlinedTextField(query, { query = it }, label = { Text("Search bank") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Column(Modifier.height(300.dp).verticalScroll(rememberScrollState())) {
                        matches.forEach { bank ->
                            TextButton(onClick = { onSelect(bank); visible = false; query = "" }, modifier = Modifier.fillMaxWidth()) { Text(bank) }
                        }
                        if (matches.isEmpty()) Text("No bank found.")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { visible = false }) { Text("Close") } }
        )
    }
}

@Composable
fun PaymentMethodDetailsFields(
    method: String,
    channel: String,
    onChannel: (String) -> Unit,
    accountName: String,
    onAccountName: (String) -> Unit,
    accountNumber: String,
    onAccountNumber: (String) -> Unit,
    branch: String,
    onBranch: (String) -> Unit,
    routingNumber: String,
    onRoutingNumber: (String) -> Unit,
    reference: String,
    onReference: (String) -> Unit,
    methodDetails: String,
    onMethodDetails: (String) -> Unit
) {
    when (method) {
        "Mobile banking" -> {
            ChoiceDropdown("Mobile banking provider", channel.ifBlank { defaultProvider() }, availableProviders(), onChannel)
            if (channel == "Other provider") Field("Provider name", methodDetails, onChange = onMethodDetails)
            Field("Mobile / account number", accountNumber, onChange = onAccountNumber)
            Field("Account holder (optional)", accountName, onChange = onAccountName)
            Field("Transaction ID", reference, onChange = onReference)
        }
        "Bank transfer" -> {
            SearchableBankPicker(channel.ifBlank { defaultBank() }, onChannel)
            if (channel == "Other bank") {
                Field("Other bank name", methodDetails, onChange = onMethodDetails)
                SaveCustomBankAction(methodDetails, onChannel)
            }
            Field("Account holder", accountName, onChange = onAccountName)
            Field("Account number", accountNumber, onChange = onAccountNumber)
            Field("Branch (optional)", branch, onChange = onBranch)
            Field("Routing number (optional)", routingNumber, onChange = onRoutingNumber)
            Field("Transaction / reference ID", reference, onChange = onReference)
        }
        "Cheque" -> {
            SearchableBankPicker(channel.ifBlank { defaultBank() }, onChannel)
            if (channel == "Other bank") {
                Field("Other bank name", methodDetails, onChange = onMethodDetails)
                SaveCustomBankAction(methodDetails, onChannel)
            }
            Field("Cheque number", reference, onChange = onReference)
            Field("Account holder (optional)", accountName, onChange = onAccountName)
        }
        "Salary deduction", "Salary arrangement" -> {
            Field("Employer", accountName, onChange = onAccountName)
            Field("Salary month", methodDetails, onChange = onMethodDetails)
            Field("Reference (optional)", reference, onChange = onReference)
        }
        "Card" -> {
            Field("Card issuer / bank", channel, onChange = onChannel)
            Field("Last four digits", accountNumber, onChange = onAccountNumber)
            Field("Transaction ID", reference, onChange = onReference)
        }
        "Cash" -> Field("Paid to / received from", accountName, onChange = onAccountName)
        "Other" -> {
            Field("Method name", methodDetails, onChange = onMethodDetails)
            Field("Reference (optional)", reference, onChange = onReference)
        }
    }
}

internal fun paymentMethodValidation(
    method: String,
    channel: String,
    accountName: String,
    accountNumber: String,
    reference: String,
    methodDetails: String
): String = when {
    method == "Mobile banking" && channel.isBlank() -> "Select a mobile banking provider."
    method == "Mobile banking" && channel == "Other provider" && methodDetails.isBlank() -> "Enter the provider name."
    method == "Mobile banking" && accountNumber.filter { it.isDigit() }.length !in 7..15 -> "Enter a valid mobile/account number."
    method == "Bank transfer" && channel.isBlank() -> "Select a bank."
    method == "Bank transfer" && channel == "Other bank" && methodDetails.isBlank() -> "Enter the bank name."
    method == "Bank transfer" && accountName.isBlank() -> "Enter the account holder name."
    method == "Bank transfer" && accountNumber.isBlank() -> "Enter the account number."
    method == "Cheque" && (channel.isBlank() || reference.isBlank()) -> "Select the bank and enter the cheque number."
    method in listOf("Salary deduction", "Salary arrangement") && (accountName.isBlank() || methodDetails.isBlank()) -> "Enter the employer and salary month."
    method == "Card" && (!Regex("^[0-9]{4}$").matches(accountNumber) || reference.isBlank()) -> "Enter the card's last four digits and transaction ID."
    method == "Other" && methodDetails.isBlank() -> "Enter the payment method name."
    else -> ""
}

@Composable
fun FormColumn(
    title: String,
    readOnly: Boolean = false,
    onBack: (() -> Unit)? = null,
    hasUnsavedChanges: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var showDiscardDialog by remember { mutableStateOf(false) }

    val requestBack: () -> Unit = {
        if (hasUnsavedChanges && !readOnly) {
            showDiscardDialog = true
        } else {
            onBack?.invoke()
        }
        Unit
    }

    BackHandler(enabled = onBack != null) {
        requestBack()
    }

    LazyColumn(

        modifier =
            Modifier.fillMaxSize(),

        contentPadding =
            PaddingValues(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(FinanceSpacing.sm)
    ) {

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = FinanceShapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = FinanceSpacing.sm, vertical = FinanceSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)
                ) {
                    if (onBack != null) {
                        IconButton(onClick = requestBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                    Column(Modifier.weight(1f).semantics { heading() }) {
                        Text(title, style = MaterialTheme.typography.headlineSmall)
                        if (readOnly) {
                            Text(
                                "View only",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            CompositionLocalProvider(LocalFormReadOnly provides readOnly) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(FinanceSpacing.sm),
                    content = content
                )
            }
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard your changes?") },
            text = { Text("Your unsaved changes will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onBack?.invoke()
                    }
                ) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep editing")
                }
            }
        )
    }
}

@Composable
fun FinanceEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = FinanceShapes.large
    ) {
        Column(
            modifier = Modifier.padding(FinanceSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FinanceFeedbackBanner(
    message: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (message.isBlank()) return
    Surface(
        modifier = modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
        color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer,
        shape = FinanceShapes.medium
    ) {
        Text(message, modifier = Modifier.padding(FinanceSpacing.md), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = {
            val initial = parseExpenseDate(value) ?: System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply { timeInMillis = initial }
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    val selected = Calendar.getInstance().apply {
                        clear()
                        set(year, month, day)
                    }.timeInMillis
                    onChange(expenseDateText(selected))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = FinanceShapes.medium,
        enabled = !LocalFormReadOnly.current
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge)
            }
            Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun Field(
    label: String,
    value: String,
    isPassword: Boolean = false,
    onChange: (String) -> Unit
) {

    var passwordVisible by remember { mutableStateOf(false) }
    val normalizedLabel = label.lowercase(Locale.US)
    val numberField = !isPassword &&
            !normalizedLabel.contains("date") &&
            !normalizedLabel.contains("note") &&
            listOf(
                "amount",
                "price",
                "interest",
                "installment",
                "due day",
                "previous payment",
                "principal"
            ).any { normalizedLabel.contains(it) }

    OutlinedTextField(

        value = value,

        onValueChange = onChange,

        label = {
            Text(label)
        },

        modifier =
            Modifier.fillMaxWidth().heightIn(min = 56.dp),

        shape = FinanceShapes.medium,

        enabled = !LocalFormReadOnly.current,

        keyboardOptions = KeyboardOptions(
            keyboardType = if (numberField) KeyboardType.Decimal else KeyboardType.Text
        ),

        visualTransformation =
            if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },

        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (passwordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        }
                    )
                }
            }
        } else {
            null
        },

        singleLine = true
    )
}
