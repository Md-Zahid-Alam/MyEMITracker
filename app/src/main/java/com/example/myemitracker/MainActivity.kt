package com.example.myemitracker

import android.Manifest
import android.app.AlarmManager
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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


// ============================================================
// DATA MODELS
// ============================================================

data class Payment(
    val number: Int,
    val dueDate: Long,
    val amount: Double,
    val paidDate: Long? = null,
    val status: String = if (paidDate == null) "PENDING" else "PAID"
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
    val payments: List<Payment>
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
    val payments: List<Payment>
)

data class Debt(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val direction: String,
    val originalAmount: Double,
    val dueDate: Long?,
    val notes: String,
    val payments: List<Payment>
)

data class FinanceData(
    val emis: List<EmiItem> = emptyList(),
    val loans: List<Loan> = emptyList(),
    val debts: List<Debt> = emptyList()
)


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

            put("version", 3)

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

            put("startDate", item.startDate)
            put("dueDay", item.dueDay)

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
                        )
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
                        )
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
                        )
                    )
                )
            }
        }

        return FinanceData(
            emis = emis,
            loans = loans,
            debts = debts
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

        data.emis.forEach { item ->

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

        data.loans.forEach { item ->

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
                                System.currentTimeMillis()
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
                                System.currentTimeMillis()
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
    onLogout: () -> Unit
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

            TopAppBar(

                title = {
                    Text(
                        "My Finance Tracker",
                        fontWeight = FontWeight.Bold
                    )
                },

                actions = {

                    IconButton(
                        onClick = {
                            backupLauncher.launch(
                                "my-finance-tracker-backup.json"
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.Backup,
                            contentDescription = "Backup"
                        )
                    }

                    IconButton(
                        onClick = {
                            restoreLauncher.launch(
                                arrayOf(
                                    "application/json",
                                    "text/plain",
                                    "*/*"
                                )
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.Restore,
                            contentDescription = "Restore"
                        )
                    }

                    IconButton(
                        onClick = onLogout
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Lock"
                        )
                    }
                }
            )
        },

        bottomBar = {

            NavigationBar {

                val labels =
                    listOf(
                        "Dashboard",
                        "EMI",
                        "Loans",
                        "Debts",
                        "Reports"
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
                                    1 -> Icons.Default.Devices
                                    2 -> Icons.Default.AccountBalance
                                    3 -> Icons.Default.CreditCard
                                    else -> Icons.Default.Description
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
        },

        floatingActionButton = {

            if (tab in 1..3) {

                FloatingActionButton(

                    onClick = {

                        selectedType =
                            when (tab) {
                                1 -> "emi"
                                2 -> "loan"
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

                tab == 0 -> Dashboard(viewModel)

                tab == 1 -> EmiList(
                    viewModel,
                    onOpen = {
                        selectedId = it
                        selectedType = "emi"
                    }
                )

                tab == 2 -> LoanList(
                    viewModel,
                    onOpen = {
                        selectedId = it
                        selectedType = "loan"
                    }
                )

                tab == 3 -> DebtList(
                    viewModel,
                    onOpen = {
                        selectedId = it
                        selectedType = "debt"
                    }
                )

                else -> Reports(viewModel)
            }
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
        viewModel.data.emis.sumOf { emi ->

            emi.payments
                .filter { it.paidDate == null }
                .sumOf { it.amount }
        }

    val loanRemaining =
        viewModel.data.loans.sumOf { loan ->

            loan.payments
                .filter { it.paidDate == null }
                .sumOf { it.amount }
        }

    val debtRemaining =
        viewModel.data.debts.sumOf { debt ->

            max(
                0.0,
                debt.originalAmount -
                        debt.payments.sumOf {
                            it.amount
                        }
            )
        }

    val monthly =
        viewModel.data.emis.sumOf {
            it.monthlyPayment
        } +
                viewModel.data.loans.sumOf {
                    it.monthlyPayment
                }

    val nextPayment =
        (
                viewModel.data.emis.flatMap { item ->
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
                        viewModel.data.loans.flatMap { item ->
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
                    "Monthly",
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
                modifier =
                    Modifier.fillMaxWidth()
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
    onOpen: (String) -> Unit
) {

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

        if (viewModel.data.emis.isEmpty()) {

            item {

                Text(
                    "No EMI plans yet. Tap + to add one."
                )
            }
        }

        items(viewModel.data.emis) { item ->

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
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Text(
                        item.name,
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        "${item.category} • " +
                                "${money(item.monthlyPayment)} / month"
                    )

                    Text(
                        "$paid/${item.installments} paid • " +
                                "Remaining ${
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

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            onOpen(item.id)
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text("Open")
                    }
                }
            }
        }
    }
}


// ============================================================
// LOAN LIST
// ============================================================

@Composable
fun LoanList(
    viewModel: FinanceViewModel,
    onOpen: (String) -> Unit
) {

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

        if (viewModel.data.loans.isEmpty()) {

            item {

                Text(
                    "No loans yet. Tap + to add one."
                )
            }
        }

        items(viewModel.data.loans) { item ->

            val paid =
                item.payments.count {
                    it.paidDate != null
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
                        item.name,
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        "${item.type} • ${item.lender}"
                    )

                    Text(
                        "${money(item.monthlyPayment)} / month"
                    )

                    Text(
                        "$paid/${item.installments} paid"
                    )

                    Text(
                        "Remaining ${
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

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            onOpen(item.id)
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text("Open")
                    }
                }
            }
        }
    }
}


// ============================================================
// DEBT LIST
// ============================================================

@Composable
fun DebtList(
    viewModel: FinanceViewModel,
    onOpen: (String) -> Unit
) {

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

        if (viewModel.data.debts.isEmpty()) {

            item {

                Text(
                    "No debts yet. Tap + to add one."
                )
            }
        }

        items(viewModel.data.debts) { item ->

            val paid =
                item.payments.sumOf {
                    it.amount
                }

            val remaining =
                max(
                    0.0,
                    item.originalAmount - paid
                )

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
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Text(
                        item.name,
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        item.direction
                    )

                    Text(
                        "Original ${money(item.originalAmount)}"
                    )

                    Text(
                        "Paid ${money(paid)}"
                    )

                    Text(
                        "Remaining ${money(remaining)}"
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    LinearProgressIndicator(
                        progress = {
                            progress
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            onOpen(item.id)
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text("Open")
                    }
                }
            }
        }
    }
}


// ============================================================
// PAYMENT HISTORY
// ============================================================

@Composable
fun PaymentHistory(
    payments: List<Payment>,
    onPaid: ((Int) -> Unit)? = null
) {

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
                    }

                    if (
                        payment.paidDate == null &&
                        onPaid != null
                    ) {

                        Button(
                            onClick = {
                                onPaid(payment.number)
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
            }
        }
    }
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

    FormColumn(
        title =
            if (existing == null) {
                "Add EMI"
            } else {
                "Edit EMI"
            }
    ) {

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
            "Reminder days, comma separated (0=due date)",
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

                    val firstDue =
                        existing
                            ?.payments
                            ?.minOfOrNull {
                                it.dueDate
                            }
                            ?: dueDate(
                                System.currentTimeMillis(),
                                day
                            )

                    val oldPaid =
                        existing
                            ?.payments
                            ?.filter {
                                it.paidDate != null
                            }
                            ?.associateBy {
                                it.number
                            }
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
                                            existing == null &&
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
                                payments
                        )

                    if (existing == null) {
                        viewModel.addEmi(item)
                    } else {
                        viewModel.updateEmi(item)
                    }

                    done()
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
                existing.payments
            ) {
                viewModel.markEmiPaid(
                    existing.id,
                    it
                )
            }
        }
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
        if (count > 0) {
            total / count
        } else {
            0.0
        }

    FormColumn(
        title =
            if (existing == null) {
                "Add Loan"
            } else {
                "Edit Loan"
            }
    ) {

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

        Field(
            "Installments",
            installments
        ) {
            installments = it
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
            "Reminder days, comma separated",
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

                    count <= 0 ->
                        "Enter installments."

                    previousCount !in 0..count ->
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

                    val firstDue =
                        existing
                            ?.payments
                            ?.minOfOrNull {
                                it.dueDate
                            }
                            ?: dueDate(
                                System.currentTimeMillis(),
                                day
                            )

                    val oldPaid =
                        existing
                            ?.payments
                            ?.filter {
                                it.paidDate != null
                            }
                            ?.associateBy {
                                it.number
                            }
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
                                            existing == null &&
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
                                payments
                        )

                    if (existing == null) {
                        viewModel.addLoan(loan)
                    } else {
                        viewModel.updateLoan(loan)
                    }

                    done()
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
                existing.payments
            ) {
                viewModel.markLoanPaid(
                    existing.id,
                    it
                )
            }
        }
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

    var error by remember {
        mutableStateOf("")
    }

    FormColumn(
        title =
            if (existing == null) {
                "Add Debt"
            } else {
                "Debt Details"
            }
    ) {

        Field(
            "Person / organization",
            name
        ) {
            name = it
        }

        Field(
            "Direction (I Owe / Owed to Me)",
            direction
        ) {
            direction = it
        }

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

        if (existing != null) {

            val original =
                amount.toDoubleOrNull()
                    ?: existing.originalAmount

            val paid =
                existing.payments.sumOf {
                    it.amount
                }

            val remaining =
                max(
                    0.0,
                    original - paid
                )

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

            Field(
                "New payment amount",
                payment
            ) {
                payment = it
            }

            Button(

                onClick = {

                    val paymentAmount =
                        payment.toDoubleOrNull()
                            ?: 0.0

                    if (paymentAmount > 0) {

                        viewModel.markDebtPaid(
                            existing.id,
                            paymentAmount
                        )

                        payment = ""
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    "Add Payment"
                )
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
                existing.payments
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

                    if (
                        name.isBlank() ||
                        originalAmount <= 0
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
                                payments =
                                    emptyList()
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
// FORM COMPONENTS
// ============================================================

@Composable
fun FormColumn(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

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
                title,
                style =
                    MaterialTheme.typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )
        }

        item {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp),
                content = content
            )
        }
    }
}

@Composable
fun Field(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {

    OutlinedTextField(

        value = value,

        onValueChange = onChange,

        label = {
            Text(label)
        },

        modifier =
            Modifier.fillMaxWidth(),

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

    fun createReport(
        fileName: String,
        content: String
    ) {

        pendingReport = content

        launcher.launch(fileName)
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
                    data.debts.sumOf {
                        max(
                            0.0,
                            it.originalAmount -
                                    it.payments.sumOf {
                                        payment ->
                                        payment.amount
                                    }
                        )
                    }
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
                    item.payments.sumOf {
                        it.amount
                    }
                )
            }"
        )

        appendLine(
            "Remaining: ${
                money(
                    max(
                        0.0,
                        item.originalAmount -
                                item.payments.sumOf {
                                    it.amount
                                }
                    )
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

                FinanceApp {

                    unlocked = false

                    showContent()
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

        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            modifier =
                Modifier.size(64.dp)
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

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            modifier =
                Modifier.fillMaxWidth(),

            singleLine = true
        )

        Spacer(
            Modifier.height(8.dp)
        )

        OutlinedTextField(

            value = confirmPassword,

            onValueChange = {
                confirmPassword = it
            },

            label = {
                Text("Confirm password")
            },

            modifier =
                Modifier.fillMaxWidth(),

            singleLine = true
        )

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

                    password.length < 6 ->
                        "Use at least 6 characters."

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

        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            modifier =
                Modifier.size(64.dp)
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

        Text(
            "Enter your app password to continue."
        )

        Spacer(
            Modifier.height(16.dp)
        )

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
                error = ""
            },

            label = {
                Text("Password")
            },

            modifier =
                Modifier.fillMaxWidth(),

            singleLine = true
        )

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
                Modifier.fillMaxWidth()

        ) {

            Text(
                "Unlock"
            )
        }
    }
}
