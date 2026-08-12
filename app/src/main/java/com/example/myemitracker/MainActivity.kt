package com.example.myemitracker

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
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.math.max

private const val PREFS = "finance_tracker_v3"
private const val KEY_DATA = "data"
private const val KEY_PASSWORD_HASH = "password_hash"
private const val KEY_PASSWORD_SALT = "password_salt"
private const val CHANNEL_ID = "finance_reminders"
private const val KEY_THEME_MODE = "theme_mode"

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
    val receiptUri: String? = null
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
    val archived: Boolean = false
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
    val archived: Boolean = false
)

data class Debt(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val direction: String,
    val originalAmount: Double,
    val dueDate: Long?,
    val notes: String,
    val payments: List<Payment>,
    val archived: Boolean = false
)

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val amount: Double,
    val date: Long,
    val notes: String
)

data class FinanceData(
    val emis: List<EmiItem> = emptyList(),
    val loans: List<Loan> = emptyList(),
    val debts: List<Debt> = emptyList(),
    val expenses: List<Expense> = emptyList()
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


// ============================================================
// REPOSITORY
// ============================================================

class FinanceRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences(
        PREFS,
        Context.MODE_PRIVATE
    )

    fun load(): FinanceData {

        val raw = prefs.getString(KEY_DATA, null)

        if (!raw.isNullOrBlank()) {
            return runCatching {
                fromJson(raw)
            }.getOrDefault(FinanceData())
        }

        return migrateV2()
    }

    fun save(data: FinanceData) {

        prefs.edit()
            .putString(
                KEY_DATA,
                toJson(data).toString()
            )
            .apply()
    }

    fun backup(): String {
        return toJson(load()).toString(2)
    }

    fun restore(json: String): FinanceData {

        val data = fromJson(json)

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

            put("version", 6)

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
        }
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
        }
    }

    private fun fromJson(raw: String): FinanceData {

        val root = JSONObject(raw)

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
                                }
                        )
                    )
                }
            }
        }

        val emis = buildList {

            val array =
                root.optJSONArray("emis")
                    ?: JSONArray()

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
                        archived = item.optBoolean("archived", false)
                    )
                )
            }
        }

        val loans = buildList {

            val array =
                root.optJSONArray("loans")
                    ?: JSONArray()

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
                        archived = item.optBoolean("archived", false)
                    )
                )
            }
        }

        val debts = buildList {

            val array =
                root.optJSONArray("debts")
                    ?: JSONArray()

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
                        archived = item.optBoolean("archived", false)
                    )
                )
            }
        }

        val expenses = buildList {
            val array = root.optJSONArray("expenses") ?: JSONArray()

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                add(
                    Expense(
                        id = item.optString("id", UUID.randomUUID().toString()),
                        title = item.optString("title", "Expense"),
                        category = item.optString("category", "Other"),
                        amount = item.optDouble("amount", 0.0),
                        date = item.optLong("date", System.currentTimeMillis()),
                        notes = item.optString("notes", "")
                    )
                )
            }
        }

        return FinanceData(
            emis = emis,
            loans = loans,
            debts = debts,
            expenses = expenses
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
                )
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
        amount: Double
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
                dueDate = System.currentTimeMillis(),
                amount = amount,
                paidDate = System.currentTimeMillis()
            )

        updateDebt(
            item.copy(
                payments =
                    item.payments + payment
            )
        )
    }

    fun backup(): String {
        return repository.backup()
    }

    fun restore(json: String) {
        save(repository.restore(json))
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
        mutableStateOf("EMI")
    }

    BackHandler(enabled = selectedType.isNotBlank() || tab != 0) {
        if (selectedType.isNotBlank()) {
            selectedType = ""
            selectedId = ""
        } else {
            tab = 0
        }
    }

    val backupLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument(
                "application/json"
            )
        ) { uri ->

            if (uri != null) {

                context.contentResolver
                    .openOutputStream(uri)
                    ?.use { output ->

                        output.write(
                            viewModel
                                .backup()
                                .toByteArray()
                        )
                    }
            }
        }

    val restoreLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                context.contentResolver
                    .openInputStream(uri)
                    ?.bufferedReader()
                    ?.use { reader ->

                        runCatching {
                            viewModel.restore(
                                reader.readText()
                            )
                        }
                    }
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

            if (selectedType.isBlank() && (tab == 1 || tab == 2)) {

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

                selectedType == "emi" -> {

                    EmiForm(
                        viewModel,
                        viewModel.data.emis.find {
                            it.id == selectedId
                        },
                        done = {
                            selectedType = ""
                            selectedId = ""
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
                            selectedType = ""
                            selectedId = ""
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
                            selectedType = ""
                            selectedId = ""
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
                        backupLauncher.launch("my-finance-tracker-backup.json")
                    },
                    onRestore = {
                        restoreLauncher.launch(
                            arrayOf("application/json", "text/plain", "*/*")
                        )
                    },
                    onLock = onLogout,
                    themeMode = themeMode,
                    onThemeChange = onThemeChange
                )
            }
        }
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
    var search by remember { mutableStateOf("") }
    var sortMode by remember { mutableStateOf("Newest first") }
    var searchVisible by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Payments",
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("EMI", "Loans", "Debts").forEach { option ->
                CompactTabButton(
                    label = option,
                    selected = section == option,
                    onClick = { onSectionChange(option) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (section) {
                "EMI" -> EmiList(viewModel, search, sortMode) { onOpen("emi", it) }
                "Loans" -> LoanList(viewModel, search, sortMode) { onOpen("loan", it) }
                else -> DebtList(viewModel, search, sortMode) { onOpen("debt", it) }
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

    val debtRemaining =
        viewModel.data.debts.filterNot { it.archived }.sumOf { debt ->
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
                    "Debt Left",
                    money(debtRemaining),
                    Modifier.weight(1f)
                )
            }
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
    val filteredItems = viewModel.data.debts.filter { item ->
        val statusMatches = when (statusFilter) {
            "Archived" -> item.archived
            "Completed" -> !item.archived && debtCompleted(item)
            else -> !item.archived && !debtCompleted(item)
        }
        statusMatches && (
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

            Text(
                "Debts",
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
                    "No $statusFilter debts."
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
                    Text("${item.direction} • Original ${money(item.originalAmount)}")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Paid ${money(paid)}")
                        Text("${money(remaining)} remaining", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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

                            Row {
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

@Composable
fun PaymentHistory(
    payments: List<Payment>,
    onUpdate: ((Payment) -> Unit)? = null
) {

    val context = LocalContext.current
    var editingPayment by remember { mutableStateOf<Payment?>(null) }
    var receiptPayment by remember { mutableStateOf<Payment?>(null) }
    var pendingAction by remember { mutableStateOf<ConfirmationRequest?>(null) }

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
                    }

                    if (
                        payment.paidDate == null &&
                        onUpdate != null
                    ) {

                        Button(
                            onClick = {
                                onUpdate(
                                    payment.copy(
                                        paidDate = System.currentTimeMillis(),
                                        status = "PAID"
                                    )
                                )
                            }
                        ) {
                            Text("Mark Paid")
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
    var error by remember(payment) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Payment ${payment.number}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickerField("Due date", dueDate) { dueDate = it }
                if (payment.paidDate != null) {
                    DatePickerField("Paid date", paidDate) { paidDate = it }
                }
                Field("Payment notes", notes) { notes = it }
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
                    } else {
                        onSave(
                            payment.copy(
                                dueDate = parsedDueDate,
                                paidDate = parsedPaidDate,
                                notes = notes.trim(),
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

                    purchasePrice <= 0 ->
                        "Enter a valid price."

                    down < 0 ||
                            down >= purchasePrice ->
                        "Check down payment."

                    count <= 0 ->
                        "Enter installments."

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
                                        }
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

                            archived = existing?.archived ?: false
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

        if (existing != null) {

            Text(
                "Payment History",
                style =
                    MaterialTheme.typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            PaymentHistory(
                payments = existing.payments,
                onUpdate = if (viewOnly) null else {
                    { payment -> viewModel.updateEmiPayment(existing.id, payment) }
                }
            )
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
                while (remaining > 0.0001) {
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

                    principalAmount <= 0 ->
                        "Enter principal."

                    repaymentMode == "EQUAL" && count <= 0 ->
                        "Enter installments."

                    repaymentMode == "FLEXIBLE" && flexibleMonthly <= 0 ->
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
                                        }
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

                            archived = existing?.archived ?: false
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

        if (existing != null) {

            Text(
                "Repayment History",
                style =
                    MaterialTheme.typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            PaymentHistory(
                payments = existing.payments,
                onUpdate = if (viewOnly) null else {
                    { payment -> viewModel.updateLoanPayment(existing.id, payment) }
                }
            )
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

    var payment by remember {
        mutableStateOf("")
    }

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
            Field(
                "New payment amount",
                payment
            ) {
                payment = it
                paymentError = ""
            }

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
                                paymentAmount
                            )

                            payment = ""
                            paymentError = ""
                        }
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    "Add Payment"
                )
            }
            }

            Text(
                "Payment History",
                style =
                    MaterialTheme.typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            PaymentHistory(
                payments = existing.payments,
                onUpdate = if (viewOnly) null else {
                    { payment -> viewModel.updateDebtPayment(existing.id, payment) }
                }
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
                        name.isBlank() ||
                        originalAmount <= 0 ||
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
                                notes =
                                    notes.trim(),
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
}


// ============================================================
// EXPENSE FORM
// ============================================================

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
    var error by remember { mutableStateOf("") }

    val hasUnsavedChanges =
        title != (existing?.title ?: "") ||
        category != (existing?.category ?: "Food") ||
        amount != (existing?.amount?.toString() ?: "") ||
        date != initialDate ||
        notes != (existing?.notes ?: "")

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
                    expenseAmount <= 0 -> "Enter a valid amount greater than zero."
                    expenseDate == null -> "Enter a valid date as DD-MM-YYYY."
                    else -> ""
                }

                if (error.isEmpty() && expenseDate != null) {
                    val expense = Expense(
                        id = existing?.id ?: UUID.randomUUID().toString(),
                        title = title.trim(),
                        category = category,
                        amount = expenseAmount,
                        date = expenseDate,
                        notes = notes.trim()
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

@Composable
fun SettingsScreen(
    onChangePassword: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onLock: () -> Unit,
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
            Text("Backup Data")
        }

        OutlinedButton(
            onClick = onRestore,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Restore, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Restore Data")
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
            "Backup saves all EMI, loan, debt, and expense records to a local JSON file.",
            style = MaterialTheme.typography.bodySmall
        )
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
        text.lines().forEach { line -> append("<Row><Cell><Data ss:Type=\"String\">${escape(line)}</Data></Cell></Row>") }
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

            val paint =
                Paint().apply {
                    textSize = 11f
                }

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

            var y = 35f

            text.lines().forEach { line ->

                if (y > 810f) {

                    document.finishPage(
                        page
                    )

                    pageNumber++

                    page =
                        document.startPage(
                            PdfDocument.PageInfo.Builder(
                                595,
                                842,
                                pageNumber
                            ).create()
                        )

                    canvas =
                        page.canvas

                    y = 35f
                }

                canvas.drawText(
                    line.take(95),
                    30f,
                    y,
                    paint
                )

                y += 16f
            }

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
            painter = painterResource(com.example.myemitracker.R.drawable.app_logo),
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
            painter = painterResource(com.example.myemitracker.R.drawable.app_logo),
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
    }
}
