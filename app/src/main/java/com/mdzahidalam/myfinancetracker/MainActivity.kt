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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.io.File
import javax.crypto.SecretKeyFactory
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.max

private const val PREFS = "finance_tracker_v3"
private const val KEY_DATA = "data"
private const val KEY_DATA_ENCRYPTED = "data_encrypted_v1"
private const val KEY_PASSWORD_HASH = "password_hash"
private const val KEY_PASSWORD_SALT = "password_salt"
private const val CHANNEL_ID = "finance_reminders"
private const val KEY_THEME_MODE = "theme_mode"
private const val LOCAL_KEY_ALIAS = "my_finance_tracker_records_v1"
private const val BACKUP_FORMAT = "MFT_ENCRYPTED_BACKUP"

private val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFF007C7A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8F2EE),
    onPrimaryContainer = Color(0xFF003735),
    secondary = Color(0xFF256A66),
    tertiary = Color(0xFF356A5F),
    tertiaryContainer = Color(0xFFD2F2EB),
    onTertiaryContainer = Color(0xFF123D36)
)

private val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFF70DAD3),
    onPrimary = Color(0xFF003735),
    primaryContainer = Color(0xFF00504E),
    onPrimaryContainer = Color(0xFFC8F2EE),
    secondary = Color(0xFF9ACFC9),
    tertiary = Color(0xFF8FD7CC),
    tertiaryContainer = Color(0xFF174D48),
    onTertiaryContainer = Color(0xFFD2F2EB),
    background = Color(0xFF101414),
    surface = Color(0xFF171C1C),
    surfaceVariant = Color(0xFF24302F),
    onBackground = Color(0xFFE1E7E5),
    onSurface = Color(0xFFE1E7E5)
)

private val LocalFormReadOnly = staticCompositionLocalOf { false }


// ============================================================
// DATA MODELS
// ============================================================

data class Payment(
    val number: Int,
    val dueDate: Long,
    val amount: Double,
    val paidDate: Long? = null,
    val status: String = if (paidDate == null) "PENDING" else "PAID",
    val notes: String = "",
    val receiptUri: String? = null,
    val paymentMethod: String = "Not recorded",
    val paymentChannel: String = "",
    val referenceNumber: String = "",
    val counterparty: String = "",
    val attachments: List<Attachment> = emptyList()
)

data class Attachment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val mimeType: String,
    val contentBase64: String
)

data class PaymentRequest(
    val id: String = UUID.randomUUID().toString(),
    val requestNumber: String,
    val createdDate: Long,
    val dueDate: Long?,
    val amount: Double,
    val paymentMethod: String,
    val paymentInstructions: String,
    val message: String,
    val status: String = "UNPAID"
)

data class ReceiptProfile(
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = ""
)

data class EmiItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val seller: String,
    val price: Double,
    val downPayment: Double,
    val financedAmount: Double,
    val interestRate: Double,
    val interestAmount: Double,
    val totalPayable: Double,
    val installments: Int,
    val monthlyPayment: Double,
    val startDate: Long,
    val dueDay: Int,
    val reminderDays: List<Int>,
    val payments: List<Payment>,
    val archived: Boolean = false,
    val financingSource: String = "",
    val receivedMethod: String = "",
    val agreementReference: String = "",
    val financingNotes: String = "",
    val attachments: List<Attachment> = emptyList()
)

data class Loan(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val lender: String,
    val principal: Double,
    val interestRate: Double,
    val interestAmount: Double,
    val totalPayable: Double,
    val installments: Int,
    val monthlyPayment: Double,
    val startDate: Long,
    val dueDay: Int,
    val reminderDays: List<Int>,
    val payments: List<Payment>,
    val repaymentMode: String = "EQUAL",
    val archived: Boolean = false,
    val financingSource: String = "",
    val receivedMethod: String = "",
    val agreementReference: String = "",
    val financingNotes: String = "",
    val attachments: List<Attachment> = emptyList()
)

data class Debt(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val direction: String,
    val originalAmount: Double,
    val dueDate: Long?,
    val notes: String,
    val payments: List<Payment>,
    val archived: Boolean = false,
    val reason: String = "",
    val receivedOrGivenMethod: String = "",
    val referenceNumber: String = "",
    val attachments: List<Attachment> = emptyList(),
    val paymentRequests: List<PaymentRequest> = emptyList()
)

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val amount: Double,
    val date: Long,
    val notes: String,
    val attachments: List<Attachment> = emptyList()
)

data class FinanceData(
    val emis: List<EmiItem> = emptyList(),
    val loans: List<Loan> = emptyList(),
    val debts: List<Debt> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val receiptProfile: ReceiptProfile = ReceiptProfile()
)

data class ConfirmationRequest(
    val title: String,
    val message: String,
    val confirmLabel: String = "Confirm",
    val onConfirm: () -> Unit
)

@Composable
fun ConfirmationDialog(
    request: ConfirmationRequest,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(request.title) },
        text = { Text(request.message) },
        confirmButton = {
            TextButton(
                onClick = {
                    request.onConfirm()
                    onDismiss()
                }
            ) { Text(request.confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}


// ============================================================
// UTILITY FUNCTIONS
// ============================================================

fun money(value: Double): String {
    return "৳" + NumberFormat.getNumberInstance(Locale.US).format(value)
}

fun dateText(value: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(value))
}

fun dateTimeText(value: Long): String {
    return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(value))
}

fun expenseDateText(value: Long): String {
    return SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(value))
}

fun parseExpenseDate(value: String): Long? {
    return runCatching {
        SimpleDateFormat("dd-MM-yyyy", Locale.US).apply {
            isLenient = false
        }.parse(value.trim())?.time
    }.getOrNull()
}

fun expenseDayKey(value: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(value))
}

fun expenseMonthKey(value: Long): String {
    return SimpleDateFormat("MMMM yyyy", Locale.US).format(Date(value))
}

fun isCurrentExpenseDay(value: Long): Boolean {
    val current = Calendar.getInstance()
    val other = Calendar.getInstance().apply { timeInMillis = value }
    return current.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            current.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}

fun isCurrentExpenseMonth(value: Long): Boolean {
    val current = Calendar.getInstance()
    val other = Calendar.getInstance().apply { timeInMillis = value }
    return current.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            current.get(Calendar.MONTH) == other.get(Calendar.MONTH)
}

fun emiCompleted(item: EmiItem): Boolean {
    return item.payments.isNotEmpty() && item.payments.all { it.paidDate != null }
}

fun loanCompleted(item: Loan): Boolean {
    return item.payments.isNotEmpty() && item.payments.all { it.paidDate != null }
}

fun debtPaidAmount(item: Debt): Double {
    return item.payments.filter { it.paidDate != null }.sumOf { it.amount }
}

fun debtRemainingAmount(item: Debt): Double {
    return max(0.0, item.originalAmount - debtPaidAmount(item))
}

fun debtCompleted(item: Debt): Boolean {
    return item.originalAmount > 0 && debtRemainingAmount(item) <= 0.005
}

fun completionDate(payments: List<Payment>): Long? {
    return payments.mapNotNull { it.paidDate }.maxOrNull()
}

fun addMonths(time: Long, months: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = time
        add(Calendar.MONTH, months)
    }.timeInMillis
}

fun dueDate(start: Long, dueDay: Int): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = start
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    calendar.set(Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, 28))

    if (calendar.timeInMillis < start) {
        calendar.add(Calendar.MONTH, 1)
    }

    return calendar.timeInMillis
}

fun currentMonthDueDate(dueDay: Int): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 9)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    set(Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, 28))
}.timeInMillis

fun parseReminders(text: String): List<Int> {
    return text
        .split(",")
        .mapNotNull { it.trim().toIntOrNull() }
        .filter { it in 0..30 }
        .distinct()
        .sortedDescending()
}

fun hashPassword(password: String, salt: ByteArray): ByteArray {
    val spec = PBEKeySpec(
        password.toCharArray(),
        salt,
        120_000,
        256
    )

    return SecretKeyFactory
        .getInstance("PBKDF2WithHmacSHA256")
        .generateSecret(spec)
        .encoded
}

fun hex(bytes: ByteArray): String {
    return bytes.joinToString("") {
        "%02x".format(it)
    }
}

fun unhex(value: String): ByteArray {
    return ByteArray(value.length / 2) { index ->
        value
            .substring(index * 2, index * 2 + 2)
            .toInt(16)
            .toByte()
    }
}

fun safe(value: String): String {
    return value
        .replace(Regex("[^A-Za-z0-9_-]"), "_")
        .take(40)
}


// ============================================================
// SECURITY
// ============================================================

class SecurityStore(private val context: Context) {

    private val prefs = context.getSharedPreferences(
        PREFS,
        Context.MODE_PRIVATE
    )

    fun hasPassword(): Boolean {
        return prefs.contains(KEY_PASSWORD_HASH)
    }

    fun setPassword(password: String) {
        val salt = ByteArray(16)

        SecureRandom().nextBytes(salt)

        prefs.edit()
            .putString(KEY_PASSWORD_SALT, hex(salt))
            .putString(
                KEY_PASSWORD_HASH,
                hex(hashPassword(password, salt))
            )
            .apply()
    }

    fun verify(password: String): Boolean {

        val saltText =
            prefs.getString(KEY_PASSWORD_SALT, null)
                ?: return false

        val hashText =
            prefs.getString(KEY_PASSWORD_HASH, null)
                ?: return false

        val actual = hashPassword(
            password,
            unhex(saltText)
        )

        return MessageDigest.isEqual(
            actual,
            unhex(hashText)
        )
    }
}

private fun localEncryptionKey(): SecretKey {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    (keyStore.getKey(LOCAL_KEY_ALIAS, null) as? SecretKey)?.let { return it }
    val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    generator.init(
        KeyGenParameterSpec.Builder(
            LOCAL_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
    )
    return generator.generateKey()
}

private fun encryptLocalRecords(plainText: String): String {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, localEncryptionKey())
    val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
    return JSONObject().apply {
        put("format", "MFT_LOCAL_ENCRYPTED")
        put("version", 1)
        put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
        put("ciphertext", Base64.encodeToString(encrypted, Base64.NO_WRAP))
    }.toString()
}

private fun decryptLocalRecords(payload: String): String {
    val root = JSONObject(payload)
    require(root.optString("format") == "MFT_LOCAL_ENCRYPTED") { "Unsupported encrypted record format." }
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val iv = Base64.decode(root.getString("iv"), Base64.NO_WRAP)
    cipher.init(Cipher.DECRYPT_MODE, localEncryptionKey(), GCMParameterSpec(128, iv))
    val clear = cipher.doFinal(Base64.decode(root.getString("ciphertext"), Base64.NO_WRAP))
    return clear.toString(Charsets.UTF_8)
}

private fun encryptBackup(plainText: String, password: String): String {
    require(password.isNotBlank()) { "Enter a backup password." }
    val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val spec = PBEKeySpec(password.toCharArray(), salt, 210_000, 256)
    val keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    spec.clearPassword()
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(keyBytes, "AES"))
    keyBytes.fill(0)
    val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
    return JSONObject().apply {
        put("format", BACKUP_FORMAT)
        put("version", 1)
        put("kdf", "PBKDF2-HMAC-SHA256")
        put("iterations", 210_000)
        put("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
        put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
        put("ciphertext", Base64.encodeToString(encrypted, Base64.NO_WRAP))
    }.toString()
}

private fun decryptBackup(payload: String, password: String): String {
    require(payload.length <= 40_000_000) { "Backup file is too large." }
    val root = JSONObject(payload)
    require(root.optString("format") == BACKUP_FORMAT) { "This is not an encrypted My Finance Tracker backup." }
    val salt = Base64.decode(root.getString("salt"), Base64.NO_WRAP)
    val iterations = root.optInt("iterations", 210_000).coerceIn(120_000, 500_000)
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, 256)
    val keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    spec.clearPassword()
    return try {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(keyBytes, "AES"),
            GCMParameterSpec(128, Base64.decode(root.getString("iv"), Base64.NO_WRAP))
        )
        cipher.doFinal(Base64.decode(root.getString("ciphertext"), Base64.NO_WRAP)).toString(Charsets.UTF_8)
    } catch (_: Exception) {
        throw IllegalArgumentException("Incorrect backup password or damaged backup file.")
    } finally {
        keyBytes.fill(0)
    }
}


// ============================================================
// REPOSITORY
// ============================================================

class FinanceRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences(
        PREFS,
        Context.MODE_PRIVATE
    )

    fun load(): FinanceData {

        val encrypted = prefs.getString(KEY_DATA_ENCRYPTED, null)
        if (!encrypted.isNullOrBlank()) {
            return runCatching { fromJson(decryptLocalRecords(encrypted)) }
                .getOrDefault(FinanceData())
        }

        val raw = prefs.getString(KEY_DATA, null)
        if (!raw.isNullOrBlank()) {
            val migrated = runCatching { fromJson(raw) }.getOrDefault(FinanceData())
            save(migrated)
            return migrated
        }

        return migrateV2()
    }

    fun save(data: FinanceData) {

        val encrypted = encryptLocalRecords(toJson(data).toString())
        check(decryptLocalRecords(encrypted).isNotBlank())
        prefs.edit()
            .putString(KEY_DATA_ENCRYPTED, encrypted)
            .remove(KEY_DATA)
            .commit()
    }

    fun backup(password: String): String {
        return encryptBackup(toJson(load()).toString(), password)
    }

    fun restore(content: String, password: String?, allowLegacy: Boolean = false): FinanceData {
        val clearText = if (runCatching { JSONObject(content).optString("format") }.getOrNull() == BACKUP_FORMAT) {
            decryptBackup(content, password.orEmpty())
        } else {
            require(allowLegacy) { "Select legacy restore to import an old readable JSON backup." }
            content
        }
        val data = fromJson(clearText)

        save(data)

        return data
    }

    private fun migrateV2(): FinanceData {

        val oldPrefs = context.getSharedPreferences(
            "emi_v2_data",
            Context.MODE_PRIVATE
        )

        val raw = oldPrefs.getString(
            "plans",
            null
        ) ?: return FinanceData()

        return runCatching {

            val array = JSONArray(raw)

            val emis = buildList {

                for (i in 0 until array.length()) {

                    val item = array.getJSONObject(i)

                    val paymentArray =
                        item.optJSONArray("payments")
                            ?: JSONArray()

                    val payments = buildList {

                        for (j in 0 until paymentArray.length()) {

                            val payment =
                                paymentArray.getJSONObject(j)

                            add(
                                Payment(
                                    number = payment.optInt(
                                        "installment",
                                        j + 1
                                    ),
                                    dueDate = payment.optLong(
                                        "dueDate",
                                        System.currentTimeMillis()
                                    ),
                                    amount = payment.optDouble(
                                        "amount",
                                        0.0
                                    ),
                                    paidDate =
                                        if (payment.isNull("paidDate")) {
                                            null
                                        } else {
                                            payment.optLong("paidDate")
                                        }
                                )
                            )
                        }
                    }

                    val price =
                        item.optDouble("price", 0.0)

                    val down =
                        item.optDouble("downPayment", 0.0)

                    val monthly =
                        item.optDouble("monthlyEmi", 0.0)

                    add(
                        EmiItem(
                            id = item.optString(
                                "id",
                                UUID.randomUUID().toString()
                            ),
                            name = item.optString(
                                "phoneName",
                                "Imported EMI"
                            ),
                            category = "Imported from Version 2",
                            seller = "",
                            price = price,
                            downPayment = down,
                            financedAmount = max(
                                0.0,
                                price - down
                            ),
                            interestRate = 0.0,
                            interestAmount = 0.0,
                            totalPayable = price,
                            installments = item.optInt(
                                "installments",
                                payments.size
                            ),
                            monthlyPayment = monthly,
                            startDate = item.optLong(
                                "startDate",
                                System.currentTimeMillis()
                            ),
                            dueDay = item.optInt(
                                "dueDay",
                                1
                            ),
                            reminderDays = listOf(
                                7,
                                3,
                                1,
                                0
                            ),
                            payments = payments
                        )
                    )
                }
            }

            FinanceData(emis = emis)

        }.getOrDefault(FinanceData())
    }

    private fun toJson(data: FinanceData): JSONObject {

        return JSONObject().apply {

            put("version", 7)

            put(
                "emis",
                JSONArray().apply {
                    data.emis.forEach {
                        put(emiJson(it))
                    }
                }
            )

            put(
                "loans",
                JSONArray().apply {
                    data.loans.forEach {
                        put(loanJson(it))
                    }
                }
            )

            put(
                "debts",
                JSONArray().apply {
                    data.debts.forEach {
                        put(debtJson(it))
                    }
                }
            )

            put(
                "expenses",
                JSONArray().apply {
                    data.expenses.forEach {
                        put(expenseJson(it))
                    }
                }
            )
            put("receiptProfile", JSONObject().apply {
                put("fullName", data.receiptProfile.fullName)
                put("phone", data.receiptProfile.phone)
                put("email", data.receiptProfile.email)
                put("address", data.receiptProfile.address)
            })
        }
    }

    private fun attachmentsJson(items: List<Attachment>) = JSONArray().apply {
        items.forEach { item ->
            put(JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("mimeType", item.mimeType)
                put("contentBase64", item.contentBase64)
            })
        }
    }

    private fun paymentRequestJson(item: PaymentRequest) = JSONObject().apply {
        put("id", item.id)
        put("requestNumber", item.requestNumber)
        put("createdDate", item.createdDate)
        put("dueDate", item.dueDate ?: JSONObject.NULL)
        put("amount", item.amount)
        put("paymentMethod", item.paymentMethod)
        put("paymentInstructions", item.paymentInstructions)
        put("message", item.message)
        put("status", item.status)
    }

    private fun paymentJson(payment: Payment): JSONObject {

        return JSONObject().apply {

            put("number", payment.number)
            put("dueDate", payment.dueDate)
            put("amount", payment.amount)

            put(
                "paidDate",
                payment.paidDate ?: JSONObject.NULL
            )

            put("status", payment.status)
            put("notes", payment.notes)
            put("receiptUri", payment.receiptUri ?: JSONObject.NULL)
            put("paymentMethod", payment.paymentMethod)
            put("paymentChannel", payment.paymentChannel)
            put("referenceNumber", payment.referenceNumber)
            put("counterparty", payment.counterparty)
            put("attachments", attachmentsJson(payment.attachments))
        }
    }

    private fun emiJson(item: EmiItem): JSONObject {

        return JSONObject().apply {

            put("id", item.id)
            put("name", item.name)
            put("category", item.category)
            put("seller", item.seller)

            put("price", item.price)
            put("downPayment", item.downPayment)
            put("financedAmount", item.financedAmount)

            put("interestRate", item.interestRate)
            put("interestAmount", item.interestAmount)
            put("totalPayable", item.totalPayable)

            put("installments", item.installments)
            put("monthlyPayment", item.monthlyPayment)
            put("startDate", item.startDate)
            put("dueDay", item.dueDay)
            put("archived", item.archived)
            put("financingSource", item.financingSource)
            put("receivedMethod", item.receivedMethod)
            put("agreementReference", item.agreementReference)
            put("financingNotes", item.financingNotes)
            put("attachments", attachmentsJson(item.attachments))

            put(
                "reminderDays",
                JSONArray(item.reminderDays)
            )

            put(
                "payments",
                JSONArray().apply {
                    item.payments.forEach {
                        put(paymentJson(it))
                    }
                }
            )
        }
    }

    private fun loanJson(item: Loan): JSONObject {

        return JSONObject().apply {

            put("id", item.id)
            put("name", item.name)
            put("type", item.type)
            put("lender", item.lender)

            put("principal", item.principal)

            put("interestRate", item.interestRate)
            put("interestAmount", item.interestAmount)

            put("totalPayable", item.totalPayable)

            put("installments", item.installments)
            put("monthlyPayment", item.monthlyPayment)
            put("repaymentMode", item.repaymentMode)

            put("startDate", item.startDate)
            put("dueDay", item.dueDay)
            put("archived", item.archived)
            put("financingSource", item.financingSource)
            put("receivedMethod", item.receivedMethod)
            put("agreementReference", item.agreementReference)
            put("financingNotes", item.financingNotes)
            put("attachments", attachmentsJson(item.attachments))

            put(
                "reminderDays",
                JSONArray(item.reminderDays)
            )

            put(
                "payments",
                JSONArray().apply {
                    item.payments.forEach {
                        put(paymentJson(it))
                    }
                }
            )
        }
    }

    private fun debtJson(item: Debt): JSONObject {

        return JSONObject().apply {

            put("id", item.id)
            put("name", item.name)
            put("direction", item.direction)

            put("originalAmount", item.originalAmount)

            put(
                "dueDate",
                item.dueDate ?: JSONObject.NULL
            )

            put("notes", item.notes)
            put("archived", item.archived)
            put("reason", item.reason)
            put("receivedOrGivenMethod", item.receivedOrGivenMethod)
            put("referenceNumber", item.referenceNumber)
            put("attachments", attachmentsJson(item.attachments))
            put("paymentRequests", JSONArray().apply {
                item.paymentRequests.forEach { put(paymentRequestJson(it)) }
            })

            put(
                "payments",
                JSONArray().apply {
                    item.payments.forEach {
                        put(paymentJson(it))
                    }
                }
            )
        }
    }

    private fun expenseJson(item: Expense): JSONObject {
        return JSONObject().apply {
            put("id", item.id)
            put("title", item.title)
            put("category", item.category)
            put("amount", item.amount)
            put("date", item.date)
            put("notes", item.notes)
            put("attachments", attachmentsJson(item.attachments))
        }
    }

    private fun fromJson(raw: String): FinanceData {

        require(raw.length <= 40_000_000) { "Backup data is too large." }
        val root = JSONObject(raw)
        require(root.has("emis") || root.has("loans") || root.has("debts") || root.has("expenses")) {
            "Backup does not contain My Finance Tracker records."
        }

        fun readAttachments(array: JSONArray?): List<Attachment> {
            if (array == null) return emptyList()
            return buildList {
                for (i in 0 until array.length()) {
                    val item = array.optJSONObject(i) ?: continue
                    val content = item.optString("contentBase64", "")
                    if (content.isNotBlank()) {
                        require(content.length <= 7_000_000) { "An attached document is too large." }
                        runCatching { Base64.decode(content, Base64.NO_WRAP) }
                            .getOrElse { throw IllegalArgumentException("An attached document is damaged.") }
                        add(
                            Attachment(
                                id = item.optString("id", UUID.randomUUID().toString()),
                                name = item.optString("name", "Document"),
                                mimeType = item.optString("mimeType", "application/octet-stream"),
                                contentBase64 = content
                            )
                        )
                    }
                }
            }
        }

        fun readPaymentRequests(array: JSONArray?): List<PaymentRequest> {
            if (array == null) return emptyList()
            return buildList {
                for (i in 0 until array.length()) {
                    val item = array.optJSONObject(i) ?: continue
                    add(
                        PaymentRequest(
                            id = item.optString("id", UUID.randomUUID().toString()),
                            requestNumber = item.optString("requestNumber", "MFT-REQ"),
                            createdDate = item.optLong("createdDate", System.currentTimeMillis()),
                            dueDate = if (item.isNull("dueDate")) null else item.optLong("dueDate"),
                            amount = item.optDouble("amount", 0.0),
                            paymentMethod = item.optString("paymentMethod", "Not recorded"),
                            paymentInstructions = item.optString("paymentInstructions", ""),
                            message = item.optString("message", ""),
                            status = item.optString("status", "UNPAID")
                        )
                    )
                }
            }
        }

        fun readPayments(array: JSONArray?): List<Payment> {

            if (array == null) {
                return emptyList()
            }

            return buildList {

                for (i in 0 until array.length()) {

                    val item =
                        array.getJSONObject(i)

                    add(
                        Payment(
                            number = item.optInt(
                                "number",
                                i + 1
                            ),
                            dueDate = item.optLong(
                                "dueDate",
                                System.currentTimeMillis()
                            ),
                            amount = item.optDouble(
                                "amount",
                                0.0
                            ),
                            paidDate =
                                if (item.isNull("paidDate")) {
                                    null
                                } else {
                                    item.optLong("paidDate")
                                },
                            notes = item.optString("notes", ""),
                            receiptUri =
                                if (item.isNull("receiptUri")) {
                                    null
                                } else {
                                    item.optString("receiptUri")
                                },
                            paymentMethod = item.optString("paymentMethod", "Cash"),
                            paymentChannel = item.optString("paymentChannel", ""),
                            referenceNumber = item.optString("referenceNumber", ""),
                            counterparty = item.optString("counterparty", ""),
                            attachments = readAttachments(item.optJSONArray("attachments"))
                        )
                    )
                }
            }
        }

        val emis = buildList {

            val array =
                root.optJSONArray("emis")
                    ?: JSONArray()
            require(array.length() <= 100_000) { "Backup contains too many EMI records." }

            for (i in 0 until array.length()) {

                val item =
                    array.getJSONObject(i)

                val reminders =
                    item.optJSONArray("reminderDays")

                val reminderList = buildList {

                    if (reminders != null) {

                        for (j in 0 until reminders.length()) {
                            add(reminders.optInt(j))
                        }
                    }
                }

                add(
                    EmiItem(
                        id = item.optString(
                            "id",
                            UUID.randomUUID().toString()
                        ),
                        name = item.optString(
                            "name",
                            "EMI"
                        ),
                        category = item.optString(
                            "category",
                            "Other"
                        ),
                        seller = item.optString(
                            "seller",
                            ""
                        ),
                        price = item.optDouble(
                            "price",
                            0.0
                        ),
                        downPayment = item.optDouble(
                            "downPayment",
                            0.0
                        ),
                        financedAmount = item.optDouble(
                            "financedAmount",
                            0.0
                        ),
                        interestRate = item.optDouble(
                            "interestRate",
                            0.0
                        ),
                        interestAmount = item.optDouble(
                            "interestAmount",
                            0.0
                        ),
                        totalPayable = item.optDouble(
                            "totalPayable",
                            0.0
                        ),
                        installments = item.optInt(
                            "installments",
                            0
                        ),
                        monthlyPayment = item.optDouble(
                            "monthlyPayment",
                            0.0
                        ),
                        startDate = item.optLong(
                            "startDate",
                            System.currentTimeMillis()
                        ),
                        dueDay = item.optInt(
                            "dueDay",
                            1
                        ),
                        reminderDays = reminderList,
                        payments = readPayments(
                            item.optJSONArray("payments")
                        ),
                        archived = item.optBoolean("archived", false),
                        financingSource = item.optString("financingSource", ""),
                        receivedMethod = item.optString("receivedMethod", ""),
                        agreementReference = item.optString("agreementReference", ""),
                        financingNotes = item.optString("financingNotes", ""),
                        attachments = readAttachments(item.optJSONArray("attachments"))
                    )
                )
            }
        }

        val loans = buildList {

            val array =
                root.optJSONArray("loans")
                    ?: JSONArray()
            require(array.length() <= 100_000) { "Backup contains too many loan records." }

            for (i in 0 until array.length()) {

                val item =
                    array.getJSONObject(i)

                val reminders =
                    item.optJSONArray("reminderDays")

                val reminderList = buildList {

                    if (reminders != null) {

                        for (j in 0 until reminders.length()) {
                            add(reminders.optInt(j))
                        }
                    }
                }

                add(
                    Loan(
                        id = item.optString(
                            "id",
                            UUID.randomUUID().toString()
                        ),
                        name = item.optString(
                            "name",
                            "Loan"
                        ),
                        type = item.optString(
                            "type",
                            "Other"
                        ),
                        lender = item.optString(
                            "lender",
                            ""
                        ),
                        principal = item.optDouble(
                            "principal",
                            0.0
                        ),
                        interestRate = item.optDouble(
                            "interestRate",
                            0.0
                        ),
                        interestAmount = item.optDouble(
                            "interestAmount",
                            0.0
                        ),
                        totalPayable = item.optDouble(
                            "totalPayable",
                            0.0
                        ),
                        installments = item.optInt(
                            "installments",
                            0
                        ),
                        monthlyPayment = item.optDouble(
                            "monthlyPayment",
                            0.0
                        ),
                        repaymentMode = item.optString(
                            "repaymentMode",
                            "EQUAL"
                        ).ifBlank { "EQUAL" },
                        startDate = item.optLong(
                            "startDate",
                            System.currentTimeMillis()
                        ),
                        dueDay = item.optInt(
                            "dueDay",
                            1
                        ),
                        reminderDays = reminderList,
                        payments = readPayments(
                            item.optJSONArray("payments")
                        ),
                        archived = item.optBoolean("archived", false),
                        financingSource = item.optString("financingSource", ""),
                        receivedMethod = item.optString("receivedMethod", ""),
                        agreementReference = item.optString("agreementReference", ""),
                        financingNotes = item.optString("financingNotes", ""),
                        attachments = readAttachments(item.optJSONArray("attachments"))
                    )
                )
            }
        }

        val debts = buildList {

            val array =
                root.optJSONArray("debts")
                    ?: JSONArray()
            require(array.length() <= 100_000) { "Backup contains too many debt records." }

            for (i in 0 until array.length()) {

                val item =
                    array.getJSONObject(i)

                add(
                    Debt(
                        id = item.optString(
                            "id",
                            UUID.randomUUID().toString()
                        ),
                        name = item.optString(
                            "name",
                            "Debt"
                        ),
                        direction = item.optString(
                            "direction",
                            "I Owe"
                        ),
                        originalAmount = item.optDouble(
                            "originalAmount",
                            0.0
                        ),
                        dueDate =
                            if (item.isNull("dueDate")) {
                                null
                            } else {
                                item.optLong("dueDate")
                            },
                        notes = item.optString(
                            "notes",
                            ""
                        ),
                        payments = readPayments(
                            item.optJSONArray("payments")
                        ),
                        archived = item.optBoolean("archived", false),
                        reason = item.optString("reason", ""),
                        receivedOrGivenMethod = item.optString("receivedOrGivenMethod", ""),
                        referenceNumber = item.optString("referenceNumber", ""),
                        attachments = readAttachments(item.optJSONArray("attachments")),
                        paymentRequests = readPaymentRequests(item.optJSONArray("paymentRequests"))
                    )
                )
            }
        }

        val expenses = buildList {
            val array = root.optJSONArray("expenses") ?: JSONArray()
            require(array.length() <= 200_000) { "Backup contains too many expense records." }

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                add(
                    Expense(
                        id = item.optString("id", UUID.randomUUID().toString()),
                        title = item.optString("title", "Expense"),
                        category = item.optString("category", "Other"),
                        amount = item.optDouble("amount", 0.0),
                        date = item.optLong("date", System.currentTimeMillis()),
                        notes = item.optString("notes", ""),
                        attachments = readAttachments(item.optJSONArray("attachments"))
                    )
                )
            }
        }

        return FinanceData(
            emis = emis,
            loans = loans,
            debts = debts,
            expenses = expenses,
            receiptProfile = root.optJSONObject("receiptProfile")?.let {
                ReceiptProfile(
                    fullName = it.optString("fullName", ""),
                    phone = it.optString("phone", ""),
                    email = it.optString("email", ""),
                    address = it.optString("address", "")
                )
            } ?: ReceiptProfile()
        )
    }
}


// ============================================================
// REMINDER SYSTEM
// ============================================================

object ReminderScheduler {

    fun reschedule(
        context: Context,
        data: FinanceData
    ) {

        val alarmManager =
            context.getSystemService(
                AlarmManager::class.java
            )

        data.emis.filterNot { it.archived }.forEach { item ->

            item.payments
                .filter { it.paidDate == null }
                .forEach { payment ->

                    item.reminderDays.forEach { days ->

                        schedule(
                            context,
                            alarmManager,
                            item.name,
                            payment,
                            days
                        )
                    }
                }
        }

        data.loans.filterNot { it.archived }.forEach { item ->

            item.payments
                .filter { it.paidDate == null }
                .forEach { payment ->

                    item.reminderDays.forEach { days ->

                        schedule(
                            context,
                            alarmManager,
                            item.name,
                            payment,
                            days
                        )
                    }
                }
        }
    }

    private fun schedule(
        context: Context,
        alarmManager: AlarmManager,
        name: String,
        payment: Payment,
        days: Int
    ) {

        val trigger =
            payment.dueDate -
                    days * 24L * 60L * 60L * 1000L

        if (trigger <= System.currentTimeMillis()) {
            return
        }

        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            ).apply {

                putExtra("name", name)
                putExtra("amount", payment.amount)
                putExtra("number", payment.number)
                putExtra("due", payment.dueDate)
                putExtra("days", days)
            }

        val requestCode =
            name.hashCode() * 31 +
                    payment.number * 37 +
                    days

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            trigger,
            pendingIntent
        )
    }
}

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val name =
            intent.getStringExtra("name")
                ?: "Payment"

        val amount =
            intent.getDoubleExtra(
                "amount",
                0.0
            )

        val number =
            intent.getIntExtra(
                "number",
                1
            )

        val due =
            intent.getLongExtra(
                "due",
                System.currentTimeMillis()
            )

        val days =
            intent.getIntExtra(
                "days",
                0
            )

        if (Build.VERSION.SDK_INT >= 26) {

            val manager =
                context.getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Finance Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PRIVATE
                    description = "Private payment due reminders"
                }
            )
        }

        if (
            Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val title =
                if (days == 0) {
                    "Payment Due Today"
                } else {
                    "Payment Reminder"
                }

            val body =
                if (days == 0) {
                    "$name: ${money(amount)} is due today (payment $number)."
                } else {
                    "$name: ${money(amount)} is due in $days day(s)."
                }

            val publicNotification = androidx.core.app.NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Payment reminder")
                .setContentText("Open My Finance Tracker to view details.")
                .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            val notification =
                androidx.core.app.NotificationCompat
                    .Builder(
                        context,
                        CHANNEL_ID
                    )
                    .setSmallIcon(
                        android.R.drawable.ic_dialog_info
                    )
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(
                        androidx.core.app.NotificationCompat
                            .BigTextStyle()
                            .bigText(
                                "$body\nDue: ${dateText(due)}"
                            )
                    )
                    .setAutoCancel(true)
                    .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PRIVATE)
                    .setPublicVersion(publicNotification)
                    .setPriority(
                        androidx.core.app.NotificationCompat
                            .PRIORITY_DEFAULT
                    )
                    .build()

            androidx.core.app.NotificationManagerCompat
                .from(context)
                .notify(
                    name.hashCode() +
                            number +
                            days * 101,
                    notification
                )
        }
    }
}


// ============================================================
// VIEW MODEL
// ============================================================

class FinanceViewModel(
    private val context: Context
) : ViewModel() {

    private val repository =
        FinanceRepository(
            context.applicationContext
        )

    var data by mutableStateOf(
        repository.load()
    )
        private set

    fun save(newData: FinanceData) {

        data = newData

        repository.save(newData)

        ReminderScheduler.reschedule(
            context.applicationContext,
            newData
        )
    }

    fun addEmi(item: EmiItem) {
        save(
            data.copy(
                emis = data.emis + item
            )
        )
    }

    fun updateEmi(item: EmiItem) {

        save(
            data.copy(
                emis = data.emis.map {
                    if (it.id == item.id) {
                        item
                    } else {
                        it
                    }
                }
            )
        )
    }

    fun deleteEmi(id: String) {

        save(
            data.copy(
                emis = data.emis.filterNot {
                    it.id == id
                }
            )
        )
    }

    fun setEmiArchived(id: String, archived: Boolean) {
        val item = data.emis.firstOrNull { it.id == id } ?: return
        updateEmi(item.copy(archived = archived))
    }

    fun reopenEmi(id: String) {
        val item = data.emis.firstOrNull { it.id == id } ?: return
        val lastPaid = item.payments.filter { it.paidDate != null }.maxByOrNull { it.number } ?: return
        updateEmiPayment(id, lastPaid.copy(paidDate = null, status = "PENDING"))
    }

    fun addLoan(item: Loan) {

        save(
            data.copy(
                loans = data.loans + item
            )
        )
    }

    fun updateLoan(item: Loan) {

        save(
            data.copy(
                loans = data.loans.map {
                    if (it.id == item.id) {
                        item
                    } else {
                        it
                    }
                }
            )
        )
    }

    fun deleteLoan(id: String) {

        save(
            data.copy(
                loans = data.loans.filterNot {
                    it.id == id
                }
            )
        )
    }

    fun setLoanArchived(id: String, archived: Boolean) {
        val item = data.loans.firstOrNull { it.id == id } ?: return
        updateLoan(item.copy(archived = archived))
    }

    fun reopenLoan(id: String) {
        val item = data.loans.firstOrNull { it.id == id } ?: return
        val lastPaid = item.payments.filter { it.paidDate != null }.maxByOrNull { it.number } ?: return
        updateLoanPayment(id, lastPaid.copy(paidDate = null, status = "PENDING"))
    }

    fun addDebt(item: Debt) {

        save(
            data.copy(
                debts = data.debts + item
            )
        )
    }

    fun updateDebt(item: Debt) {

        save(
            data.copy(
                debts = data.debts.map {
                    if (it.id == item.id) {
                        item
                    } else {
                        it
                    }
                }
            )
        )
    }

    fun deleteDebt(id: String) {

        save(
            data.copy(
                debts = data.debts.filterNot {
                    it.id == id
                }
            )
        )
    }

    fun setDebtArchived(id: String, archived: Boolean) {
        val item = data.debts.firstOrNull { it.id == id } ?: return
        updateDebt(item.copy(archived = archived))
    }

    fun reopenDebt(id: String) {
        val item = data.debts.firstOrNull { it.id == id } ?: return
        val lastPaid = item.payments.filter { it.paidDate != null }.maxByOrNull { it.number } ?: return
        updateDebtPayment(id, lastPaid.copy(paidDate = null, status = "PENDING"))
    }

    fun addExpense(item: Expense) {
        save(data.copy(expenses = data.expenses + item))
    }

    fun updateExpense(item: Expense) {
        save(
            data.copy(
                expenses = data.expenses.map {
                    if (it.id == item.id) item else it
                }
            )
        )
    }

    fun deleteExpense(id: String) {
        save(
            data.copy(
                expenses = data.expenses.filterNot { it.id == id }
            )
        )
    }

    fun updateEmiPayment(id: String, payment: Payment) {
        val item = data.emis.firstOrNull { it.id == id } ?: return
        updateEmi(
            item.copy(
                payments = item.payments.map {
                    if (it.number == payment.number) payment else it
                }
            )
        )
    }

    fun updateLoanPayment(id: String, payment: Payment) {
        val item = data.loans.firstOrNull { it.id == id } ?: return
        updateLoan(
            item.copy(
                payments = item.payments.map {
                    if (it.number == payment.number) payment else it
                }
            )
        )
    }

    fun updateDebtPayment(id: String, payment: Payment) {
        val item = data.debts.firstOrNull { it.id == id } ?: return
        updateDebt(
            item.copy(
                payments = item.payments.map {
                    if (it.number == payment.number) payment else it
                }
            )
        )
    }

    fun markEmiPaid(
        id: String,
        number: Int
    ) {

        val item =
            data.emis.firstOrNull {
                it.id == id
            } ?: return

        val updated =
            item.copy(
                payments = item.payments.map {
                    if (
                        it.number == number &&
                        it.paidDate == null
                    ) {
                        it.copy(
                            paidDate =
                                System.currentTimeMillis(),
                            status = "PAID"
                        )
                    } else {
                        it
                    }
                }
            )

        updateEmi(updated)
    }

    fun markLoanPaid(
        id: String,
        number: Int
    ) {

        val item =
            data.loans.firstOrNull {
                it.id == id
            } ?: return

        val updated =
            item.copy(
                payments = item.payments.map {
                    if (
                        it.number == number &&
                        it.paidDate == null
                    ) {
                        it.copy(
                            paidDate =
                                System.currentTimeMillis(),
                            status = "PAID"
                        )
                    } else {
                        it
                    }
                }
            )

        updateLoan(updated)
    }

    fun markDebtPaid(
        id: String,
        amount: Double,
        paidDate: Long = System.currentTimeMillis(),
        method: String = "Cash",
        channel: String = "",
        reference: String = "",
        counterparty: String = "",
        notes: String = "",
        attachments: List<Attachment> = emptyList()
    ) {

        if (amount <= 0) {
            return
        }

        val item =
            data.debts.firstOrNull {
                it.id == id
            } ?: return

        val remaining = debtRemainingAmount(item)
        if (remaining <= 0 || amount > remaining + 0.005) {
            return
        }

        val nextNumber =
            (item.payments.maxOfOrNull {
                it.number
            } ?: 0) + 1

        val payment =
            Payment(
                number = nextNumber,
                dueDate = paidDate,
                amount = amount,
                paidDate = paidDate,
                notes = notes,
                paymentMethod = method,
                paymentChannel = channel,
                referenceNumber = reference,
                counterparty = counterparty,
                attachments = attachments
            )

        updateDebt(
            item.copy(
                payments =
                    item.payments + payment
            )
        )
    }

    fun addPaymentRequest(debtId: String, request: PaymentRequest) {
        val debt = data.debts.firstOrNull { it.id == debtId } ?: return
        if (debt.direction != "Owed to Me") return
        updateDebt(debt.copy(paymentRequests = debt.paymentRequests + request))
    }

    fun updateReceiptProfile(profile: ReceiptProfile) {
        save(data.copy(receiptProfile = profile))
    }

    fun backup(password: String): String {
        return repository.backup(password)
    }

    fun restore(content: String, password: String?, allowLegacy: Boolean = false) {
        save(repository.restore(content, password, allowLegacy))
    }
}


// ============================================================
// VIEW MODEL FACTORY
// ============================================================

@Composable
fun financeViewModel(
    context: Context
): FinanceViewModel {

    return viewModel(
        factory = object :
            ViewModelProvider.Factory {

            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {

                @Suppress("UNCHECKED_CAST")
                return FinanceViewModel(
                    context.applicationContext
                ) as T
            }
        }
    )
}


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
    onThemeChange: (String) -> Unit
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
                "emi_history", "emi_documents", "emi_financing", "emi_payment" -> "emi_detail"
                "loan_history", "loan_documents", "loan_financing", "loan_payment" -> "loan_detail"
                "debt_history", "debt_documents", "debt_financing", "debt_payment" -> "debt_detail"
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

        topBar = {
            if (selectedType.isBlank()) {
                TopAppBar(

                title = {
                    Text(
                        "My Finance Tracker",
                        fontWeight = FontWeight.Bold
                    )
                }
                )
            }
        },

        bottomBar = {
            if (selectedType.isBlank()) {
                NavigationBar {

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
                            Text(label)
                        }
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
                    themeMode = themeMode,
                    onThemeChange = onThemeChange
                )
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
                    else -> "Debts"
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
                else -> DebtList(viewModel, search, sortMode) { onOpen("debt_detail", it) }
            }
        }
    }
}

@Composable
fun PaymentsLanding(viewModel: FinanceViewModel, onOpenSection: (String) -> Unit) {
    val activeEmis = viewModel.data.emis.filter { !it.archived && !emiCompleted(it) }
    val activeLoans = viewModel.data.loans.filter { !it.archived && !loanCompleted(it) }
    val activeDebts = viewModel.data.debts.filter { !it.archived && !debtCompleted(it) }
    val debtToPay = activeDebts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) }
    val moneyToReceive = activeDebts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Payments", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { Text("Choose the type of financial record you want to manage.") }
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
            PaymentSectionCard(
                title = "Debts",
                summary = "Pay ${money(debtToPay)} • Receive ${money(moneyToReceive)}",
                onClick = { onOpenSection("Debts") }
            )
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
        DetailNavigationButton("Documents", if (documents.isEmpty()) "No documents" else "${documents.size} attached") { onOpen("documents") }
        DetailNavigationButton("Financing Information", "Source, method, reference and notes") { onOpen("financing") }
        if (debt != null && debt.paymentRequests.isNotEmpty()) {
            Text("Payment Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            debt.paymentRequests.sortedByDescending { it.createdDate }.forEach { PaymentRequestCard(debt, it, viewModel.data.receiptProfile) }
        }
    }
    if (showRequestDialog && debt != null) {
        PaymentRequestDialog(debt, onSave = { viewModel.addPaymentRequest(debt.id, it); showRequestDialog = false }, onDismiss = { showRequestDialog = false })
    }
}

@Composable
private fun DetailNavigationButton(title: String, subtitle: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
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
        } else if (loan != null) {
            InfoRow("Source", loan.financingSource)
            InfoRow("How received", loan.receivedMethod)
            InfoRow("Lender", loan.lender)
            InfoRow("Agreement reference", loan.agreementReference)
            InfoRow("Notes", loan.financingNotes)
        } else if (debt != null) {
            InfoRow("Direction", debt.direction)
            InfoRow("Reason", debt.reason)
            InfoRow("How received / given", debt.receivedOrGivenMethod)
            InfoRow("Agreement reference", debt.referenceNumber)
            InfoRow("Notes", debt.notes)
        } else Text("Record not found.")
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
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
        if (debt != null) {
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
private fun DebtPaymentEntry(viewModel: FinanceViewModel, debt: Debt, onSaved: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var paidDate by remember { mutableStateOf(expenseDateText(System.currentTimeMillis())) }
    var method by remember { mutableStateOf("Cash") }
    var channel by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(emptyList<Attachment>()) }
    var error by remember { mutableStateOf("") }
    val remaining = debtRemainingAmount(debt)

    Text("${debt.name} • Remaining ${money(remaining)}", fontWeight = FontWeight.Bold)
    Field(if (debt.direction == "Owed to Me") "Received amount" else "Payment amount", amount) { amount = it; error = "" }
    DatePickerField("Payment date", paidDate) { paidDate = it; error = "" }
    ChoiceDropdown("Payment method", method, listOf("Cash", "Bank transfer", "Mobile banking", "Salary deduction", "Card", "Cheque", "Other")) { method = it }
    if (method == "Mobile banking") ChoiceDropdown("Provider", channel.ifBlank { "bKash" }, listOf("bKash", "Nagad", "Rocket", "Other")) { channel = it }
    if (method == "Bank transfer") Field("Bank name", channel) { channel = it }
    Field("Transaction / reference ID", reference) { reference = it }
    Field("Payment notes", notes) { notes = it }
    AttachmentSection(attachments, maxFiles = 3) { attachments = it }
    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    Button(onClick = {
        val value = amount.toDoubleOrNull() ?: 0.0
        val date = parseExpenseDate(paidDate)
        error = when {
            value <= 0.0 -> "Enter a valid amount greater than zero."
            value > 999_999_999.99 -> "Amount is too large."
            value > remaining + 0.005 -> "Amount cannot exceed the remaining ${money(remaining)}."
            date == null -> "Select a valid payment date."
            date > System.currentTimeMillis() -> "Payment date cannot be in the future."
            (method == "Mobile banking" || method == "Bank transfer") && channel.isBlank() -> "Enter the payment provider or bank."
            else -> ""
        }
        if (error.isBlank() && date != null) {
            viewModel.markDebtPaid(debt.id, value, paidDate = date, method = method, channel = channel, reference = reference.trim(), counterparty = debt.name, notes = notes.trim(), attachments = attachments)
            onSaved()
        }
    }, modifier = Modifier.fillMaxWidth()) { Text(if (debt.direction == "Owed to Me") "Save Received Amount" else "Save Payment") }
}

@Composable
fun PaymentSectionCard(title: String, summary: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(summary, color = MaterialTheme.colorScheme.secondary)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Open $title")
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
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) { Text(label, maxLines = 1) }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
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


// ============================================================
// DASHBOARD
// ============================================================

@Composable
fun Dashboard(
    viewModel: FinanceViewModel
) {

    val emiRemaining =
        viewModel.data.emis.filterNot { it.archived }.sumOf { emi ->

            emi.payments
                .filter { it.paidDate == null }
                .sumOf { it.amount }
        }

    val loanRemaining =
        viewModel.data.loans.filterNot { it.archived }.sumOf { loan ->

            loan.payments
                .filter { it.paidDate == null }
                .sumOf { it.amount }
        }

    val debtToPay =
        viewModel.data.debts.filter { !it.archived && it.direction == "I Owe" }.sumOf { debt ->
            debtRemainingAmount(debt)
        }

    val moneyToReceive =
        viewModel.data.debts.filter { !it.archived && it.direction == "Owed to Me" }.sumOf { debt ->
            debtRemainingAmount(debt)
        }

    val monthly =
        viewModel.data.emis.filter { !it.archived && !emiCompleted(it) }.sumOf {
            it.monthlyPayment
        } +
                viewModel.data.loans.filter { !it.archived && !loanCompleted(it) }.sumOf {
                    it.monthlyPayment
                }

    val todayExpenses =
        viewModel.data.expenses
            .filter { isCurrentExpenseDay(it.date) }
            .sumOf { it.amount }

    val monthExpenses =
        viewModel.data.expenses
            .filter { isCurrentExpenseMonth(it.date) }
            .sumOf { it.amount }

    val nextPayment =
        (
                viewModel.data.emis.filterNot { it.archived }.flatMap { item ->
                    item.payments
                        .filter { it.paidDate == null }
                        .map {
                            Triple(
                                item.name,
                                it.dueDate,
                                it.amount
                            )
                        }
                } +
                        viewModel.data.loans.filterNot { it.archived }.flatMap { item ->
                            item.payments
                                .filter {
                                    it.paidDate == null
                                }
                                .map {
                                    Triple(
                                        item.name,
                                        it.dueDate,
                                        it.amount
                                    )
                                }
                        }
                )
            .minByOrNull {
                it.second
            }

    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        contentPadding =
            PaddingValues(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)

    ) {

        item {

            Text(
                "Financial Overview",
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )
        }

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                SummaryCard(
                    "Spent Today",
                    money(todayExpenses),
                    Modifier.weight(1f)
                )

                SummaryCard(
                    "Spent This Month",
                    money(monthExpenses),
                    Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    "Monthly Payments",
                    money(monthly),
                    Modifier.weight(1f)
                )
                SummaryCard(
                    "EMI Left",
                    money(emiRemaining),
                    Modifier.weight(1f)
                )
            }
        }

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                SummaryCard(
                    "Loan Left",
                    money(loanRemaining),
                    Modifier.weight(1f)
                )

                SummaryCard(
                    "Debt to Pay",
                    money(debtToPay),
                    Modifier.weight(1f)
                )
            }
        }

        item {
            SummaryCard(
                "Money to Receive",
                money(moneyToReceive),
                Modifier.fillMaxWidth()
            )
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(18.dp)
                ) {

                    Text(
                        "Next Payment",
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    if (nextPayment == null) {

                        Text(
                            "No pending EMI or loan payments."
                        )

                    } else {

                        Text(
                            nextPayment.first,
                            style =
                                MaterialTheme.typography
                                    .titleMedium
                        )

                        Text(
                            money(nextPayment.third),
                            style =
                                MaterialTheme.typography
                                    .headlineSmall
                        )

                        Text(
                            "Due ${dateText(nextPayment.second)}"
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Recent Expenses",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        val recentExpenses = viewModel.data.expenses
            .sortedByDescending { it.date }
            .take(5)

        if (recentExpenses.isEmpty()) {
            item { Text("No expenses recorded yet.") }
        } else {
            items(recentExpenses, key = { it.id }) { expense ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(expense.title, fontWeight = FontWeight.Bold)
                            Text("${expense.category} - ${expenseDayKey(expense.date)}")
                        }
                        Text(money(expense.amount), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ============================================================
// SUMMARY CARD
// ============================================================

@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier
) {

    Card(
        modifier = modifier
    ) {

        Column(
            modifier =
                Modifier.padding(14.dp)
        ) {

            Text(
                title,
                style =
                    MaterialTheme.typography
                        .labelMedium
            )

            Text(
                value,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


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
            Modifier.fillMaxSize(),

        contentPadding =
            PaddingValues(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
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
    onOpen: (String) -> Unit
) {

    var pendingDelete by remember { mutableStateOf<Debt?>(null) }
    var pendingAction by remember { mutableStateOf<ConfirmationRequest?>(null) }
    var statusFilter by remember { mutableStateOf("Active") }
    var directionFilter by remember { mutableStateOf("I Owe") }
    val filteredItems = viewModel.data.debts.filter { item ->
        val statusMatches = when (statusFilter) {
            "Archived" -> item.archived
            "Completed" -> !item.archived && debtCompleted(item)
            else -> !item.archived && !debtCompleted(item)
        }
        statusMatches && item.direction == directionFilter && (
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
            val toPay = viewModel.data.debts.filter { !it.archived && it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) }
            val toReceive = viewModel.data.debts.filter { !it.archived && it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) }
            Text("Debts", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryCard("Debt to pay", money(toPay), Modifier.weight(1f))
                SummaryCard("Money to receive", money(toReceive), Modifier.weight(1f))
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("I Owe", "Owed to Me").forEach { option ->
                    if (directionFilter == option) {
                        Button(onClick = { directionFilter = option }, modifier = Modifier.weight(1f)) { Text(option) }
                    } else {
                        OutlinedButton(onClick = { directionFilter = option }, modifier = Modifier.weight(1f)) { Text(option) }
                    }
                }
            }
        }

        item {
            StatusFilterRow(statusFilter) { statusFilter = it }
        }

        if (visibleItems.isEmpty()) {

            item {

                Text(
                    "No $statusFilter records under $directionFilter."
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


// ============================================================
// PAYMENT HISTORY
// ============================================================

private fun amountInWords(amount: Double): String {
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

private fun paymentReceiptText(
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
                        if (payment.paidDate != null) {
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
                ) { paymentMethod = it }
                if (paymentMethod == "Mobile banking") {
                    ChoiceDropdown("Mobile banking provider", paymentChannel.ifBlank { "bKash" }, listOf("bKash", "Nagad", "Rocket", "Other")) { paymentChannel = it }
                } else if (paymentMethod == "Bank transfer" || paymentMethod == "Salary deduction") {
                    Field(if (paymentMethod == "Bank transfer") "Bank name" else "Employer / salary month", paymentChannel) { paymentChannel = it }
                }
                Field("Transaction / reference ID", referenceNumber) { referenceNumber = it }
                Field("Paid to / received from", counterparty) { counterparty = it }
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
                    } else if (parsedPaidDate != null && (paymentMethod == "Mobile banking" || paymentMethod == "Bank transfer") && paymentChannel.isBlank()) {
                        error = "Enter the mobile banking provider or bank name."
                    } else if (notes.length > 500 || referenceNumber.length > 100 || counterparty.length > 100) {
                        error = "Notes must be 500 characters or less; reference and party names must be 100 or less."
                    } else {
                        onSave(
                            payment.copy(
                                dueDate = parsedDueDate,
                                paidDate = parsedPaidDate,
                                notes = notes.trim(),
                                paymentMethod = paymentMethod,
                                paymentChannel = paymentChannel.trim(),
                                referenceNumber = referenceNumber.trim(),
                                counterparty = counterparty.trim(),
                                attachments = attachments,
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


// ============================================================
// EMI FORM
// ============================================================

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

    var seller by remember {
        mutableStateOf(
            existing?.seller ?: ""
        )
    }

    var financingSource by remember { mutableStateOf(existing?.financingSource ?: "Shop or seller") }
    var receivedMethod by remember { mutableStateOf(existing?.receivedMethod ?: "Direct purchase financing") }
    var agreementReference by remember { mutableStateOf(existing?.agreementReference ?: "") }
    var financingNotes by remember { mutableStateOf(existing?.financingNotes ?: "") }
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

        Field(
            "Category",
            category
        ) {
            category = it
        }

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
        ) { receivedMethod = it }
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
                    Modifier.padding(16.dp)
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

                    purchasePrice <= 0 || purchasePrice > 999_999_999.99 ->
                        "Enter a valid price."

                    rate !in 0.0..100.0 || enteredInterest < 0 ->
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

    var lender by remember {
        mutableStateOf(
            existing?.lender ?: ""
        )
    }

    var financingSource by remember { mutableStateOf(existing?.financingSource ?: "Bank") }
    var receivedMethod by remember { mutableStateOf(existing?.receivedMethod ?: "Bank transfer") }
    var agreementReference by remember { mutableStateOf(existing?.agreementReference ?: "") }
    var financingNotes by remember { mutableStateOf(existing?.financingNotes ?: "") }
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

        Field(
            "Loan type",
            type
        ) {
            type = it
        }

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
        ) { receivedMethod = it }
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

                    principalAmount <= 0 || principalAmount > 999_999_999.99 ->
                        "Enter principal."

                    interestRate !in 0.0..100.0 || enteredInterest < 0 ->
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


// ============================================================
// DEBT FORM
// ============================================================

@Composable
fun DebtForm(
    viewModel: FinanceViewModel,
    existing: Debt?,
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
            existing?.direction ?: "I Owe"
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
    var receivedOrGivenMethod by remember { mutableStateOf(existing?.receivedOrGivenMethod ?: "Cash") }
    var debtReference by remember { mutableStateOf(existing?.referenceNumber ?: "") }
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
        receivedOrGivenMethod != (existing?.receivedOrGivenMethod ?: "Cash") ||
        debtReference != (existing?.referenceNumber ?: "") ||
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

        ChoiceDropdown(
            label = "Direction",
            value = direction,
            options = listOf("I Owe", "Owed to Me"),
            onSelect = { direction = it }
        )

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
        ChoiceDropdown(
            if (direction == "I Owe") "How you received it" else "How you gave it",
            receivedOrGivenMethod,
            listOf("Cash", "Bank transfer", "Mobile banking", "Goods or service", "Other")
        ) { receivedOrGivenMethod = it }
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
                        if (name.isBlank() || updatedAmount <= 0 || updatedAmount + 0.005 < paid) {
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
                                            notes = notes.trim(),
                                            reason = reason.trim(),
                                            receivedOrGivenMethod = receivedOrGivenMethod,
                                            referenceNumber = debtReference.trim(),
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
                ChoiceDropdown("Mobile banking provider", paymentChannel.ifBlank { "bKash" }, listOf("bKash", "Nagad", "Rocket", "Other")) { paymentChannel = it }
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

            if (false) Text(
                "Payment History",
                style =
                    MaterialTheme.typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            if (false) PaymentHistory(
                payments = existing.payments,
                onUpdate = if (viewOnly) null else {
                    { payment -> viewModel.updateDebtPayment(existing.id, payment) }
                },
                planName = existing.name,
                direction = existing.direction,
                planTotal = existing.originalAmount,
                profile = viewModel.data.receiptProfile
            )

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

                    if (
                        name.isBlank() || name.trim().length > 100 ||
                        originalAmount <= 0 || originalAmount > 999_999_999.99 ||
                        previousAmount < 0 ||
                        previousAmount > originalAmount
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
                                dueDate =
                                    null,
                                notes = notes.trim(),
                                reason = reason.trim(),
                                receivedOrGivenMethod = receivedOrGivenMethod,
                                referenceNumber = debtReference.trim(),
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


// ============================================================
// EXPENSE FORM
// ============================================================

private fun paymentRequestText(debt: Debt, request: PaymentRequest, profile: ReceiptProfile): String = buildString {
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
    appendLine("Remaining to receive: ${money(debtRemainingAmount(debt))}")
    appendLine("Preferred method: ${request.paymentMethod}")
    if (request.paymentInstructions.isNotBlank()) appendLine("Payment instructions: ${request.paymentInstructions}")
    if (request.message.isNotBlank()) appendLine("Message: ${request.message}")
    appendLine()
    appendLine("This is a personal payment request generated from the issuer's records. It is not a bank statement, legal judgment, or tax invoice.")
    appendLine("Generated by My Finance Tracker")
    appendLine("Powered by Md. Zahid Alam")
}

@Composable
fun PaymentRequestCard(debt: Debt, request: PaymentRequest, profile: ReceiptProfile) {
    val context = LocalContext.current
    val receivedSinceRequest = debt.payments.filter { (it.paidDate ?: 0L) >= request.createdDate }.sumOf { it.amount }
    val displayStatus = when {
        receivedSinceRequest + 0.005 >= request.amount -> "PAID"
        receivedSinceRequest > 0 -> "PARTIALLY PAID"
        else -> "UNPAID"
    }
    var pendingText by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null && pendingText.isNotBlank()) writePdfToUri(context, uri, pendingText)
        pendingText = ""
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(request.requestNumber, fontWeight = FontWeight.Bold)
            Text("Requested ${money(request.amount)} • ${dateText(request.createdDate)}")
            request.dueDate?.let { Text("Due ${dateText(it)}") }
            Text(displayStatus, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            TextButton(onClick = {
                pendingText = paymentRequestText(debt, request, profile)
                launcher.launch("${request.requestNumber}.pdf")
            }) { Text("Save PDF") }
            TextButton(onClick = {
                sharePdf(context, "${request.requestNumber}.pdf", paymentRequestText(debt, request, profile))
            }) { Text("Share") }
        }
    }
}

@Composable
fun PaymentRequestDialog(debt: Debt, onSave: (PaymentRequest) -> Unit, onDismiss: () -> Unit) {
    var amount by remember { mutableStateOf(debtRemainingAmount(debt).toString()) }
    var dueDate by remember { mutableStateOf(expenseDateText(System.currentTimeMillis())) }
    var method by remember { mutableStateOf("Mobile banking") }
    var instructions by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Payment Request") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Request money from ${debt.name}")
                Field("Requested amount", amount) { amount = it }
                DatePickerField("Due date", dueDate) { dueDate = it }
                ChoiceDropdown("Preferred payment method", method, listOf("Cash", "Bank transfer", "Mobile banking", "Cheque", "Other")) { method = it }
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
                    value <= 0 -> "Enter a valid requested amount."
                    value > debtRemainingAmount(debt) + 0.005 -> "Request cannot exceed the remaining balance."
                    parsedDue == null -> "Enter a valid due date."
                    else -> ""
                }
                if (error.isBlank()) {
                    val stamp = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
                    onSave(
                        PaymentRequest(
                            requestNumber = "MFT-REQ-$stamp-${UUID.randomUUID().toString().take(4).uppercase(Locale.US)}",
                            createdDate = System.currentTimeMillis(),
                            dueDate = parsedDue,
                            amount = value,
                            paymentMethod = method,
                            paymentInstructions = instructions.trim(),
                            message = message.trim()
                        )
                    )
                }
            }) { Text("Create") }
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
                    expenseAmount <= 0 || expenseAmount > 999_999_999.99 -> "Enter a valid amount greater than zero."
                    expenseDate == null -> "Enter a valid date as DD-MM-YYYY."
                    expenseDate > System.currentTimeMillis() -> "Expense date cannot be in the future."
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


// ============================================================
// FORM COMPONENTS
// ============================================================

private fun attachmentName(context: Context, uri: android.net.Uri): String {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) return cursor.getString(0) ?: "Document"
    }
    return uri.lastPathSegment ?: "Document"
}

private fun readAttachment(context: Context, uri: android.net.Uri): Attachment {
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

private fun openAttachment(context: Context, attachment: Attachment) {
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

private fun sharePdf(context: Context, fileName: String, text: String) {
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

    Text("Supporting documents (optional)", fontWeight = FontWeight.Bold)
    attachments.forEach { attachment ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(10.dp),
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
            onClick = { launcher.launch(arrayOf("image/*", "application/pdf")) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Attach image or PDF (${attachments.size}/$maxFiles)") }
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
    themeMode: String,
    onThemeChange: (String) -> Unit
) {
    FormColumn("Settings") {
        Text("Security and local data tools")

        Text("Appearance", fontWeight = FontWeight.Bold)
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

        OutlinedButton(
            onClick = onChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Change Password")
        }

        OutlinedButton(
            onClick = onBackup,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Backup, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Export Encrypted Backup")
        }

        OutlinedButton(
            onClick = onRestore,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Restore, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Restore Backup")
        }

        OutlinedButton(onClick = onReceiptProfile, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Description, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Receipt Profile")
        }

        OutlinedButton(onClick = onAbout, modifier = Modifier.fillMaxWidth()) {
            Text("About My Finance Tracker")
        }

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
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ReceiptProfileForm(existing: ReceiptProfile, onSave: (ReceiptProfile) -> Unit, done: () -> Unit) {
    var fullName by remember { mutableStateOf(existing.fullName) }
    var phone by remember { mutableStateOf(existing.phone) }
    var email by remember { mutableStateOf(existing.email) }
    var address by remember { mutableStateOf(existing.address) }
    val changed = fullName != existing.fullName || phone != existing.phone || email != existing.email || address != existing.address
    FormColumn("Receipt Profile", onBack = done, hasUnsavedChanges = changed) {
        Text("This identity appears as the issuer on payment receipts and payment requests. App ownership remains separate.")
        Field("Full name", fullName) { fullName = it }
        Field("Phone (optional)", phone) { phone = it }
        Field("Email (optional)", email) { email = it }
        Field("Address (optional)", address) { address = it }
        Button(
            onClick = {
                onSave(ReceiptProfile(fullName.trim(), phone.trim(), email.trim(), address.trim()))
                done()
            },
            enabled = fullName.isNotBlank(),
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(com.mdzahidalam.myfinancetracker.R.drawable.app_logo),
                contentDescription = "My Finance Tracker logo",
                modifier = Modifier.size(112.dp)
            )
            Text("My Finance Tracker", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Version 5.1")
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
            modifier = Modifier.fillMaxWidth(),
            enabled = !LocalFormReadOnly.current
        ) { Text("$label: $value") }
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
            Arrangement.spacedBy(10.dp)
    ) {

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (onBack != null) {
                    IconButton(onClick = requestBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            CompositionLocalProvider(LocalFormReadOnly provides readOnly) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
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
        modifier = Modifier.fillMaxWidth(),
        enabled = !LocalFormReadOnly.current
    ) {
        Text("$label: $value")
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
            Modifier.fillMaxWidth(),

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


// ============================================================
// REPORTS
// ============================================================

@Composable
fun Reports(
    viewModel: FinanceViewModel
) {

    val context =
        LocalContext.current

    var pendingReport by remember {
        mutableStateOf("")
    }
    var pendingExcel by remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument(
                "application/pdf"
            )
        ) { uri ->

            if (
                uri != null &&
                pendingReport.isNotEmpty()
            ) {

                writePdfToUri(
                    context,
                    uri,
                    pendingReport
                )
            }

            pendingReport = ""
        }

    val excelLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/vnd.ms-excel")
    ) { uri ->
        if (uri != null && pendingExcel.isNotEmpty()) writeExcelToUri(context, uri, pendingExcel)
        pendingExcel = ""
    }

    fun createReport(
        fileName: String,
        content: String
    ) {

        pendingReport = content

        launcher.launch(fileName)
    }

    fun createExcel(fileName: String, content: String) {
        pendingExcel = content
        excelLauncher.launch(fileName)
    }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "Reports",
            style =
                MaterialTheme.typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            "PDF reports are generated locally. " +
                    "Save them in Downloads or Documents " +
                    "so they remain available even if the app is uninstalled."
        )

        Button(

            onClick = {

                createReport(
                    "Complete_Finance_Report.pdf",
                    buildCompleteReport(
                        viewModel.data
                    )
                )
            },

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Icon(
                Icons.Default.PictureAsPdf,
                contentDescription = null
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(
                "Generate Complete Report"
            )
        }

        OutlinedButton(
            onClick = { createExcel("Complete_Finance_Report.xls", buildCompleteReport(viewModel.data)) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Download Complete Excel Report") }

        viewModel.data.emis.forEach { item ->

            OutlinedButton(

                onClick = {

                    createReport(
                        "EMI_${safe(item.name)}.pdf",
                        buildEmiReport(item)
                    )
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    "PDF: ${item.name}"
                )
            }
        }

        viewModel.data.loans.forEach { item ->

            OutlinedButton(

                onClick = {

                    createReport(
                        "Loan_${safe(item.name)}.pdf",
                        buildLoanReport(item)
                    )
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    "PDF: ${item.name}"
                )
            }
        }

        viewModel.data.debts.forEach { item ->

            OutlinedButton(

                onClick = {

                    createReport(
                        "Debt_${safe(item.name)}.pdf",
                        buildDebtReport(item)
                    )
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    "PDF: ${item.name}"
                )
            }
        }

        if (viewModel.data.expenses.isNotEmpty()) {
            OutlinedButton(
                onClick = {
                    createReport(
                        "Expense_Report.pdf",
                        buildExpenseReport(viewModel.data.expenses)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("PDF: All Expenses")
            }
        }
    }
}


// ============================================================
// REPORT BUILDERS
// ============================================================

fun buildCompleteReport(
    data: FinanceData
): String {

    return buildString {

        appendLine(
            "MY FINANCE TRACKER — COMPLETE REPORT"
        )

        appendLine(
            "Generated: ${
                dateTimeText(
                    System.currentTimeMillis()
                )
            }"
        )

        appendLine()

        appendLine("EMI SUMMARY")

        appendLine(
            "Plans: ${data.emis.size}"
        )

        appendLine(
            "Remaining: ${
                money(
                    data.emis.sumOf { item ->
                        item.payments
                            .filter {
                                it.paidDate == null
                            }
                            .sumOf {
                                it.amount
                            }
                    }
                )
            }"
        )

        appendLine()
        appendLine("EXPENSE SUMMARY")
        appendLine("Entries: ${data.expenses.size}")
        appendLine(
            "Spent today: ${money(data.expenses.filter { isCurrentExpenseDay(it.date) }.sumOf { it.amount })}"
        )
        appendLine(
            "Spent this month: ${money(data.expenses.filter { isCurrentExpenseMonth(it.date) }.sumOf { it.amount })}"
        )
        appendLine("All expenses: ${money(data.expenses.sumOf { it.amount })}")

        appendLine()

        appendLine("LOAN SUMMARY")

        appendLine(
            "Loans: ${data.loans.size}"
        )

        appendLine(
            "Remaining: ${
                money(
                    data.loans.sumOf { item ->
                        item.payments
                            .filter {
                                it.paidDate == null
                            }
                            .sumOf {
                                it.amount
                            }
                    }
                )
            }"
        )

        appendLine()

        appendLine("DEBT SUMMARY")

        appendLine(
            "Debts: ${data.debts.size}"
        )

        appendLine(
            "Remaining: ${
                money(
                    data.debts.sumOf { debtRemainingAmount(it) }
                )
            }"
        )

        data.emis.forEach {

            appendLine()

            append(
                buildEmiReport(it)
            )
        }

        data.loans.forEach {

            appendLine()

            append(
                buildLoanReport(it)
            )
        }

        data.debts.forEach {

            appendLine()

            append(
                buildDebtReport(it)
            )
        }

        if (data.expenses.isNotEmpty()) {
            appendLine()
            append(buildExpenseReport(data.expenses))
        }
    }
}

fun buildExpenseReport(expenses: List<Expense>): String {
    return buildString {
        appendLine("EXPENSE REPORT")
        appendLine("Entries: ${expenses.size}")
        appendLine("Total: ${money(expenses.sumOf { it.amount })}")
        appendLine()

        appendLine("CURRENT MONTH BY CATEGORY")
        expenses
            .filter { isCurrentExpenseMonth(it.date) }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .forEach { (category, total) ->
                appendLine("$category: ${money(total)}")
            }
        appendLine()

        expenses.sortedByDescending { it.date }.forEach {
            appendLine(
                "${expenseDayKey(it.date)} | ${it.category} | ${it.title} | ${money(it.amount)}" +
                        if (it.notes.isBlank()) "" else " | ${it.notes}"
            )
        }
    }
}

fun buildEmiReport(
    item: EmiItem
): String {

    return buildString {

        appendLine("EMI REPORT")
        appendLine("Item: ${item.name}")
        appendLine("Category: ${item.category}")
        appendLine("Seller: ${item.seller}")

        appendLine(
            "Price: ${money(item.price)}"
        )

        appendLine(
            "Down payment: ${money(item.downPayment)}"
        )

        appendLine(
            "Financed amount: ${money(item.financedAmount)}"
        )

        appendLine(
            "Interest rate: ${item.interestRate}%"
        )

        appendLine(
            "Interest amount: ${money(item.interestAmount)}"
        )

        appendLine(
            "Total payable: ${money(item.totalPayable)}"
        )

        appendLine(
            "Monthly payment: ${money(item.monthlyPayment)}"
        )

        appendLine(
            "Installments: ${item.installments}"
        )

        appendLine(
            "Due day: ${item.dueDay}"
        )

        appendLine(
            "Reminder days: ${
                item.reminderDays.joinToString(", ")
            }"
        )

        appendLine(
            "Progress: ${
                item.payments.count {
                    it.paidDate != null
                }
            }/${item.installments}"
        )

        appendLine(
            "Remaining: ${
                money(
                    item.payments
                        .filter {
                            it.paidDate == null
                        }
                        .sumOf {
                            it.amount
                        }
                )
            }"
        )

        appendLine()

        appendLine(
            "PAYMENT HISTORY"
        )

        item.payments.forEach {

            appendLine(
                "#${it.number} | " +
                        "Due ${dateText(it.dueDate)} | " +
                        "${money(it.amount)} | " +
                        if (it.paidDate == null) {
                            "PENDING"
                        } else {
                            "PAID ${dateText(it.paidDate)}"
                        }
            )
        }
    }
}

fun buildLoanReport(
    item: Loan
): String {

    return buildString {

        appendLine("LOAN REPORT")
        appendLine("Loan: ${item.name}")
        appendLine("Type: ${item.type}")
        appendLine("Lender: ${item.lender}")

        appendLine(
            "Principal: ${money(item.principal)}"
        )

        appendLine(
            "Interest rate: ${item.interestRate}%"
        )

        appendLine(
            "Interest: ${money(item.interestAmount)}"
        )

        appendLine(
            "Total payable: ${money(item.totalPayable)}"
        )

        appendLine(
            "Monthly payment: ${money(item.monthlyPayment)}"
        )

        appendLine(
            "Installments: ${item.installments}"
        )

        appendLine(
            "Due day: ${item.dueDay}"
        )

        appendLine(
            "Progress: ${
                item.payments.count {
                    it.paidDate != null
                }
            }/${item.installments}"
        )

        appendLine(
            "Remaining: ${
                money(
                    item.payments
                        .filter {
                            it.paidDate == null
                        }
                        .sumOf {
                            it.amount
                        }
                )
            }"
        )

        appendLine()

        appendLine(
            "REPAYMENT HISTORY"
        )

        item.payments.forEach {

            appendLine(
                "#${it.number} | " +
                        "Due ${dateText(it.dueDate)} | " +
                        "${money(it.amount)} | " +
                        if (it.paidDate == null) {
                            "PENDING"
                        } else {
                            "PAID ${dateText(it.paidDate)}"
                        }
            )
        }
    }
}

fun buildDebtReport(
    item: Debt
): String {

    return buildString {

        appendLine("DEBT REPORT")

        appendLine(
            "Name: ${item.name}"
        )

        appendLine(
            "Direction: ${item.direction}"
        )

        appendLine(
            "Original amount: ${
                money(item.originalAmount)
            }"
        )

        appendLine(
            "Paid: ${
                money(
                    debtPaidAmount(item)
                )
            }"
        )

        appendLine(
            "Remaining: ${
                money(
                    debtRemainingAmount(item)
                )
            }"
        )

        appendLine(
            "Notes: ${item.notes}"
        )

        appendLine()

        appendLine(
            "PAYMENT HISTORY"
        )

        item.payments.forEach {

            appendLine(
                "#${it.number} | " +
                        "${dateTimeText(
                            it.paidDate
                                ?: it.dueDate
                        )} | " +
                        money(it.amount)
            )
        }
    }
}


// ============================================================
// PDF GENERATION
// ============================================================

fun writeExcelToUri(context: Context, uri: android.net.Uri, text: String) {
    fun escape(value: String) = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    val xml = buildString {
        append("<?xml version=\"1.0\"?><Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"><Worksheet ss:Name=\"Report\"><Table>")
        val reportWithOwnership = text + "\n\nGenerated by My Finance Tracker\nCreated by Md. Zahid Alam"
        reportWithOwnership.lines().forEach { line -> append("<Row><Cell><Data ss:Type=\"String\">${escape(line)}</Data></Cell></Row>") }
        append("</Table></Worksheet></Workbook>")
    }
    context.contentResolver.openOutputStream(uri)?.use { it.write(xml.toByteArray()) }
}

fun writePdfToUri(
    context: Context,
    uri: android.net.Uri,
    text: String
) {

    context.contentResolver
        .openOutputStream(uri)
        ?.use { output ->

            val document =
                PdfDocument()

            val paint = Paint().apply { isAntiAlias = true }
            val teal = android.graphics.Color.rgb(0, 124, 122)
            val paleTeal = android.graphics.Color.rgb(226, 245, 242)
            val ink = android.graphics.Color.rgb(35, 42, 42)
            val muted = android.graphics.Color.rgb(92, 103, 102)

            var pageNumber = 1

            var page =
                document.startPage(
                    PdfDocument.PageInfo.Builder(
                        595,
                        842,
                        pageNumber
                    ).create()
                )

            var canvas =
                page.canvas

            fun drawHeader() {
                paint.color = teal
                canvas.drawRect(0f, 0f, 595f, 104f, paint)
                val logo = BitmapFactory.decodeResource(context.resources, com.mdzahidalam.myfinancetracker.R.drawable.app_logo)
                if (logo != null) canvas.drawBitmap(logo, null, RectF(28f, 20f, 92f, 84f), paint)
                paint.color = android.graphics.Color.WHITE
                paint.textSize = 18f
                paint.isFakeBoldText = true
                canvas.drawText("MY FINANCE TRACKER", 112f, 49f, paint)
                paint.textSize = 10f
                paint.isFakeBoldText = false
                canvas.drawText("Secure personal finance record", 112f, 69f, paint)
            }
            fun wrap(value: String, maxChars: Int = 76): List<String> {
                if (value.length <= maxChars) return listOf(value)
                val result = mutableListOf<String>()
                var current = ""
                value.split(" ").forEach { word ->
                    if ((current.length + word.length + 1) > maxChars) { if (current.isNotBlank()) result += current; current = word }
                    else current = if (current.isBlank()) word else "$current $word"
                }
                if (current.isNotBlank()) result += current
                return result
            }
            drawHeader()
            var y = 132f
            val inputLines = text.lines().dropWhile { it.equals("MY FINANCE TRACKER", true) }
            inputLines.forEach { raw ->
                val line = raw.trim()
                if (y > 785f) {
                    paint.color = muted; paint.textSize = 9f; paint.isFakeBoldText = false
                    canvas.drawText("Page $pageNumber", 515f, 822f, paint)
                    document.finishPage(page)
                    pageNumber++
                    page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
                    canvas = page.canvas
                    drawHeader()
                    y = 132f
                }
                when {
                    line.isBlank() -> y += 10f
                    line == line.uppercase(Locale.US) && !line.contains(":") -> {
                        paint.color = paleTeal; canvas.drawRoundRect(28f, y - 20f, 567f, y + 10f, 6f, 6f, paint)
                        paint.color = teal; paint.textSize = 14f; paint.isFakeBoldText = true
                        canvas.drawText(line, 42f, y, paint); y += 40f
                    }
                    line.contains(":") -> {
                        val label = line.substringBefore(":").trim()
                        val value = line.substringAfter(":").trim()
                        paint.color = android.graphics.Color.rgb(247, 249, 249)
                        val wrapped = wrap(value, 52)
                        val rowHeight = max(30f, 18f * wrapped.size + 10f)
                        canvas.drawRoundRect(28f, y - 17f, 567f, y - 17f + rowHeight, 4f, 4f, paint)
                        paint.color = muted; paint.textSize = 10f; paint.isFakeBoldText = true
                        canvas.drawText(label, 40f, y, paint)
                        paint.color = ink; paint.isFakeBoldText = false
                        wrapped.forEachIndexed { index, valueLine -> canvas.drawText(valueLine, 190f, y + index * 16f, paint) }
                        y += rowHeight + 5f
                    }
                    else -> {
                        paint.color = ink; paint.textSize = 10f; paint.isFakeBoldText = false
                        wrap(line).forEach { valueLine -> canvas.drawText(valueLine, 34f, y, paint); y += 15f }
                    }
                }
            }
            paint.color = paleTeal; canvas.drawRect(0f, 796f, 595f, 842f, paint)
            paint.color = teal; paint.textSize = 9f; paint.isFakeBoldText = true
            canvas.drawText("Generated by My Finance Tracker • Powered by Md. Zahid Alam", 30f, 822f, paint)

            document.finishPage(
                page
            )

            document.writeTo(
                output
            )

            document.close()
        }
}


// ============================================================
// MAIN ACTIVITY / APP PASSWORD
// ============================================================

class MainActivity : ComponentActivity() {

    private lateinit var security: SecurityStore

    private var unlocked = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        security =
            SecurityStore(this)

        showContent()
    }

    override fun onStop() {

        super.onStop()

        if (!isChangingConfigurations) {
            unlocked = false
        }
    }

    private fun showContent() {

        setContent {
            val preferences = remember {
                getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            }
            var themeMode by remember {
                mutableStateOf(preferences.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM")
            }
            val useDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            MaterialTheme(
                colorScheme = if (useDarkTheme) AppDarkColorScheme else AppLightColorScheme
            ) {

            if (!security.hasPassword()) {

                SetupScreen {

                    security.setPassword(it)

                    unlocked = true

                    showContent()
                }

            } else if (!unlocked) {

                LockScreen { password ->

                    if (security.verify(password)) {

                        unlocked = true

                        showContent()
                        true
                    } else {
                        false
                    }
                }

            } else {

                FinanceApp(
                    onLogout = {
                        unlocked = false
                        showContent()
                    },
                    onPasswordChange = { password ->
                        security.setPassword(password)
                    },
                    verifyPassword = { password -> security.verify(password) },
                    themeMode = themeMode,
                    onThemeChange = { selectedMode ->
                        themeMode = selectedMode
                        preferences.edit().putString(KEY_THEME_MODE, selectedMode).apply()
                    }
                )
            }
            }
        }
    }
}


// ============================================================
// SETUP SCREEN
// ============================================================

@Composable
fun SetupScreen(
    onSet: (String) -> Unit
) {

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf("")
    }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Image(
            painter = painterResource(com.mdzahidalam.myfinancetracker.R.drawable.app_logo),
            contentDescription = "My Finance Tracker logo",
            modifier = Modifier.size(112.dp)
        )

        Text(
            "Secure My Finance Tracker",
            style =
                MaterialTheme.typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Text(
            "Create an app password. " +
                    "The password itself is not stored; " +
                    "only a protected hash is stored on this phone."
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Field("Password", password, isPassword = true) {
            password = it
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Field("Confirm password", confirmPassword, isPassword = true) {
            confirmPassword = it
        }

        if (error.isNotEmpty()) {

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                error,
                color =
                    MaterialTheme.colorScheme.error
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Button(

            onClick = {

                error = when {

                    password.length < 4 ->
                        "Use at least 4 characters."

                    password != confirmPassword ->
                        "Passwords do not match."

                    else ->
                        ""
                }

                if (error.isEmpty()) {
                    onSet(password)
                }
            },

            modifier =
                Modifier.fillMaxWidth()

        ) {

            Text(
                "Create Password"
            )
        }
    }
}


// ============================================================
// LOCK SCREEN
// ============================================================

@Composable
fun LockScreen(
    onUnlock: (String) -> Boolean
) {

    var password by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf("")
    }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Image(
            painter = painterResource(com.mdzahidalam.myfinancetracker.R.drawable.app_logo),
            contentDescription = "My Finance Tracker logo",
            modifier = Modifier.size(112.dp)
        )

        Text(
            "My Finance Tracker",
            style =
                MaterialTheme.typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Text("Your private offline finance tracker")

        Spacer(
            Modifier.height(16.dp)
        )

        Field("Password", password, isPassword = true) {
            password = it
            error = ""
        }

        if (error.isNotEmpty()) {

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                error,
                color =
                    MaterialTheme.colorScheme.error
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Button(

            onClick = {

                if (password.isBlank()) {

                    error =
                        "Enter your password."

                } else {

                    if (onUnlock(password)) {
                        error = ""
                    } else {
                        error = "Incorrect password."
                    }
                }
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled = password.isNotBlank()

        ) {

            Text(
                "Unlock"
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Your financial data stays on this device.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Powered by Md. Zahid Alam",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
