package com.example.myemitracker

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.math.max

private const val PREFS = "finance_tracker_v3"
private const val KEY_DATA = "data"
private const val KEY_PASSWORD_HASH = "password_hash"
private const val KEY_PASSWORD_SALT = "password_salt"
private const val KEY_BIOMETRIC = "biometric_enabled"
private const val CHANNEL_ID = "finance_reminders"

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

fun money(v: Double): String = "৳" + NumberFormat.getNumberInstance(Locale.US).format(v)
fun dateText(v: Long): String = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(v))
fun dateTimeText(v: Long): String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(v))

fun addMonths(time: Long, months: Int): Long =
    Calendar.getInstance().apply { timeInMillis = time; add(Calendar.MONTH, months) }.timeInMillis

fun dueDate(start: Long, dueDay: Int): Long {
    val c = Calendar.getInstance().apply {
        timeInMillis = start
        set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    c.set(Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, 28))
    if (c.timeInMillis < start) c.add(Calendar.MONTH, 1)
    return c.timeInMillis
}

fun parseReminders(text: String): List<Int> =
    text.split(",").mapNotNull { it.trim().toIntOrNull() }.filter { it in 0..30 }.distinct().sortedDescending()

fun hashPassword(password: String, salt: ByteArray): ByteArray {
    val spec = PBEKeySpec(password.toCharArray(), salt, 120_000, 256)
    return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
}

fun hex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }
fun unhex(s: String): ByteArray = ByteArray(s.length / 2) { s.substring(it * 2, it * 2 + 2).toInt(16).toByte() }

class SecurityStore(private val context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun hasPassword() = prefs.contains(KEY_PASSWORD_HASH)

    fun setPassword(password: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        prefs.edit()
            .putString(KEY_PASSWORD_SALT, hex(salt))
            .putString(KEY_PASSWORD_HASH, hex(hashPassword(password, salt)))
            .apply()
    }

    fun verify(password: String): Boolean {
        val saltText = prefs.getString(KEY_PASSWORD_SALT, null) ?: return false
        val hashText = prefs.getString(KEY_PASSWORD_HASH, null) ?: return false
        val actual = hashPassword(password, unhex(saltText))
        return MessageDigest.isEqual(actual, unhex(hashText))
    }

    fun biometricEnabled() = prefs.getBoolean(KEY_BIOMETRIC, false)
    fun setBiometricEnabled(v: Boolean) = prefs.edit().putBoolean(KEY_BIOMETRIC, v).apply()
}

class FinanceRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(): FinanceData {
        val raw = prefs.getString(KEY_DATA, null)
        if (!raw.isNullOrBlank()) return fromJson(raw)
        return migrateV2()
    }

    fun save(data: FinanceData) {
        prefs.edit().putString(KEY_DATA, toJson(data).toString()).apply()
    }

    fun backup(): String = toJson(load()).toString(2)

    fun restore(json: String): FinanceData {
        val d = fromJson(json)
        save(d)
        return d
    }

    private fun migrateV2(): FinanceData {
        val old = context.getSharedPreferences("emi_v2_data", Context.MODE_PRIVATE)
        val raw = old.getString("plans", null) ?: return FinanceData()
        return runCatching {
            val arr = JSONArray(raw)
            val emis = buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val pArr = o.optJSONArray("payments") ?: JSONArray()
                    val payments = buildList {
                        for (j in 0 until pArr.length()) {
                            val p = pArr.getJSONObject(j)
                            add(Payment(
                                p.optInt("installment", j + 1),
                                p.optLong("dueDate", System.currentTimeMillis()),
                                p.optDouble("amount", 0.0),
                                if (p.isNull("paidDate")) null else p.optLong("paidDate")
                            ))
                        }
                    }
                    val price = o.optDouble("price", 0.0)
                    val down = o.optDouble("downPayment", 0.0)
                    val monthly = o.optDouble("monthlyEmi", 0.0)
                    add(EmiItem(
                        id = o.optString("id", UUID.randomUUID().toString()),
                        name = o.optString("phoneName", "Imported EMI"),
                        category = "Imported from Version 2",
                        seller = "",
                        price = price,
                        downPayment = down,
                        financedAmount = max(0.0, price - down),
                        interestRate = 0.0,
                        interestAmount = 0.0,
                        totalPayable = price,
                        installments = o.optInt("installments", payments.size),
                        monthlyPayment = monthly,
                        startDate = o.optLong("startDate", System.currentTimeMillis()),
                        dueDay = o.optInt("dueDay", 1),
                        reminderDays = listOf(1, 0),
                        payments = payments
                    ))
                }
            }
            FinanceData(emis = emis)
        }.getOrDefault(FinanceData())
    }

    private fun toJson(d: FinanceData) = JSONObject().apply {
        put("version", 3)
        put("emis", JSONArray().apply { d.emis.forEach { put(emiJson(it)) } })
        put("loans", JSONArray().apply { d.loans.forEach { put(loanJson(it)) } })
        put("debts", JSONArray().apply { d.debts.forEach { put(debtJson(it)) } })
    }

    private fun emiJson(x: EmiItem) = JSONObject().apply {
        put("id", x.id); put("name", x.name); put("category", x.category); put("seller", x.seller)
        put("price", x.price); put("downPayment", x.downPayment); put("financedAmount", x.financedAmount)
        put("interestRate", x.interestRate); put("interestAmount", x.interestAmount); put("totalPayable", x.totalPayable)
        put("installments", x.installments); put("monthlyPayment", x.monthlyPayment); put("startDate", x.startDate)
        put("dueDay", x.dueDay); put("reminderDays", JSONArray(x.reminderDays))
        put("payments", JSONArray().apply { x.payments.forEach { put(paymentJson(it)) } })
    }

    private fun loanJson(x: Loan) = JSONObject().apply {
        put("id", x.id); put("name", x.name); put("type", x.type); put("lender", x.lender)
        put("principal", x.principal); put("interestRate", x.interestRate); put("interestAmount", x.interestAmount)
        put("totalPayable", x.totalPayable); put("installments", x.installments); put("monthlyPayment", x.monthlyPayment)
        put("startDate", x.startDate); put("dueDay", x.dueDay); put("reminderDays", JSONArray(x.reminderDays))
        put("payments", JSONArray().apply { x.payments.forEach { put(paymentJson(it)) } })
    }

    private fun debtJson(x: Debt) = JSONObject().apply {
        put("id", x.id); put("name", x.name); put("direction", x.direction)
        put("originalAmount", x.originalAmount); put("dueDate", x.dueDate ?: JSONObject.NULL)
        put("notes", x.notes); put("payments", JSONArray().apply { x.payments.forEach { put(paymentJson(it)) } })
    }

    private fun paymentJson(p: Payment) = JSONObject().apply {
        put("number", p.number); put("dueDate", p.dueDate); put("amount", p.amount)
        put("paidDate", p.paidDate ?: JSONObject.NULL); put("status", p.status)
    }

    private fun fromJson(raw: String): FinanceData {
        val o = JSONObject(raw)
        fun payments(a: JSONArray?) = buildList {
            val x = a ?: JSONArray()
            for (i in 0 until x.length()) {
                val p = x.getJSONObject(i)
                add(Payment(p.optInt("number", i + 1), p.optLong("dueDate"), p.optDouble("amount"),
                    if (p.isNull("paidDate")) null else p.optLong("paidDate")))
            }
        }
        val emis = buildList {
            val a = o.optJSONArray("emis") ?: JSONArray()
            for (i in 0 until a.length()) {
                val x = a.getJSONObject(i)
                val r = x.optJSONArray("reminderDays") ?: JSONArray()
                add(EmiItem(
                    x.optString("id", UUID.randomUUID().toString()), x.optString("name", "EMI"),
                    x.optString("category", "Other"), x.optString("seller", ""), x.optDouble("price"),
                    x.optDouble("downPayment"), x.optDouble("financedAmount"), x.optDouble("interestRate"),
                    x.optDouble("interestAmount"), x.optDouble("totalPayable"), x.optInt("installments"),
                    x.optDouble("monthlyPayment"), x.optLong("startDate"), x.optInt("dueDay", 1),
                    buildList { for (j in 0 until r.length()) add(r.optInt(j)) },
                    payments(x.optJSONArray("payments"))
                ))
            }
        }
        val loans = buildList {
            val a = o.optJSONArray("loans") ?: JSONArray()
            for (i in 0 until a.length()) {
                val x = a.getJSONObject(i)
                val r = x.optJSONArray("reminderDays") ?: JSONArray()
                add(Loan(
                    x.optString("id", UUID.randomUUID().toString()), x.optString("name", "Loan"),
                    x.optString("type", "Other"), x.optString("lender", ""), x.optDouble("principal"),
                    x.optDouble("interestRate"), x.optDouble("interestAmount"), x.optDouble("totalPayable"),
                    x.optInt("installments"), x.optDouble("monthlyPayment"), x.optLong("startDate"),
                    x.optInt("dueDay", 1), buildList { for (j in 0 until r.length()) add(r.optInt(j)) },
                    payments(x.optJSONArray("payments"))
                ))
            }
        }
        val debts = buildList {
            val a = o.optJSONArray("debts") ?: JSONArray()
            for (i in 0 until a.length()) {
                val x = a.getJSONObject(i)
                add(Debt(
                    x.optString("id", UUID.randomUUID().toString()), x.optString("name", "Debt"),
                    x.optString("direction", "I Owe"), x.optDouble("originalAmount"),
                    if (x.isNull("dueDate")) null else x.optLong("dueDate"), x.optString("notes"),
                    payments(x.optJSONArray("payments"))
                ))
            }
        }
        return FinanceData(emis, loans, debts)
    }
}

object ReminderScheduler {
    fun reschedule(context: Context, data: FinanceData) {
        val am = context.getSystemService(AlarmManager::class.java)
        data.emis.forEach { item ->
            item.payments.filter { it.paidDate == null }.forEach { p ->
                item.reminderDays.forEach { days -> schedule(context, am, item.name, p, days) }
            }
        }
        data.loans.forEach { item ->
            item.payments.filter { it.paidDate == null }.forEach { p ->
                item.reminderDays.forEach { days -> schedule(context, am, item.name, p, days) }
            }
        }
    }

    private fun schedule(context: Context, am: AlarmManager, name: String, p: Payment, days: Int) {
        val whenAt = p.dueDate - days * 24L * 60L * 60L * 1000L
        if (whenAt <= System.currentTimeMillis()) return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("name", name); putExtra("amount", p.amount); putExtra("number", p.number)
            putExtra("due", p.dueDate); putExtra("days", days)
        }
        val code = (name.hashCode() * 31 + p.number * 37 + days)
        val pi = PendingIntent.getBroadcast(context, code, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenAt, pi)
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra("name") ?: "Payment"
        val amount = intent.getDoubleExtra("amount", 0.0)
        val number = intent.getIntExtra("number", 1)
        val due = intent.getLongExtra("due", System.currentTimeMillis())
        val days = intent.getIntExtra("days", 0)
        if (Build.VERSION.SDK_INT >= 26) {
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Finance Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        if (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val title = if (days == 0) "Payment Due Today" else "Payment Reminder"
            val body = if (days == 0) "$name: ${money(amount)} is due today (payment $number)." else "$name: ${money(amount)} is due in $days day(s)."
            val n = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$body\nDue: ${dateText(due)}"))
                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
            NotificationManagerCompat.from(context).notify((name.hashCode() + number + days * 101), n)
        }
    }
}

class FinanceViewModel(private val context: Context) : ViewModel() {
    private val repo = FinanceRepository(context.applicationContext)
    var data by mutableStateOf(repo.load()); private set

    fun save(d: FinanceData) { data = d; repo.save(d); ReminderScheduler.reschedule(context, d) }

    fun addEmi(x: EmiItem) = save(data.copy(emis = data.emis + x))
    fun updateEmi(x: EmiItem) = save(data.copy(emis = data.emis.map { if (it.id == x.id) x else it }))
    fun deleteEmi(id: String) = save(data.copy(emis = data.emis.filterNot { it.id == id }))

    fun addLoan(x: Loan) = save(data.copy(loans = data.loans + x))
    fun updateLoan(x: Loan) = save(data.copy(loans = data.loans.map { if (it.id == x.id) x else it }))
    fun deleteLoan(id: String) = save(data.copy(loans = data.loans.filterNot { it.id == id }))

    fun addDebt(x: Debt) = save(data.copy(debts = data.debts + x))
    fun updateDebt(x: Debt) = save(data.copy(debts = data.debts.map { if (it.id == x.id) x else it }))
    fun deleteDebt(id: String) = save(data.copy(debts = data.debts.filterNot { it.id == id }))

    fun markEmiPaid(id: String, number: Int) {
        data.emis.firstOrNull { it.id == id }?.let { x ->
            val updated = x.copy(payments = x.payments.map { if (it.number == number && it.paidDate == null) it.copy(paidDate = System.currentTimeMillis()) else it })
            updateEmi(updated)
        }
    }

    fun markLoanPaid(id: String, number: Int) {
        data.loans.firstOrNull { it.id == id }?.let { x ->
            val updated = x.copy(payments = x.payments.map { if (it.number == number && it.paidDate == null) it.copy(paidDate = System.currentTimeMillis()) else it })
            updateLoan(updated)
        }
    }

    fun markDebtPaid(id: String, amount: Double) {
        data.debts.firstOrNull { it.id == id }?.let { x ->
            val nextNo = (x.payments.maxOfOrNull { it.number } ?: 0) + 1
            val updated = x.copy(payments = x.payments + Payment(nextNo, System.currentTimeMillis(), amount, System.currentTimeMillis()))
            updateDebt(updated)
        }
    }

    fun backup() = repo.backup()
    fun restore(json: String) { save(repo.restore(json)) }
}

@Composable
fun vm(context: Context): FinanceViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(c: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FinanceViewModel(context.applicationContext) as T
    }
})

@Composable
fun FinanceApp(onLogout: () -> Unit) {
    val context = LocalContext.current
    val v = vm(context)
    var tab by remember { mutableStateOf(0) }
    var selectedType by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(false) }

    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) context.contentResolver.openOutputStream(uri)?.use { it.write(v.backup().toByteArray()) }
    }
    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { v.restore(it.readText()) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Finance Tracker", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { backupLauncher.launch("my-finance-tracker-backup.json") }) { Icon(Icons.Default.Backup, "Backup") }
                    IconButton(onClick = { restoreLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) }) { Icon(Icons.Default.Restore, "Restore") }
                    IconButton(onClick = onLogout) { Icon(Icons.Default.Lock, "Lock") }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listOf("Dashboard", "EMI", "Loans", "Debts", "Reports").forEachIndexed { i, label ->
                    NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = {
                        Icon(if (i == 0) Icons.Default.Home else if (i == 1) Icons.Default.Devices else if (i == 2) Icons.Default.AccountBalance else if (i == 3) Icons.Default.CreditCard else Icons.Default.Description, null)
                    }, label = { Text(label) })
                }
            }
        },
        floatingActionButton = {
            if (tab in 1..3) FloatingActionButton(onClick = { selectedType = when(tab){1->"emi";2->"loan";else->"debt"}; selectedId=""; editing=false }) { Icon(Icons.Default.Add, "Add") }
        }
    ) { p ->
        Box(Modifier.padding(p).fillMaxSize()) {
            when {
                selectedType == "emi" -> EmiForm(v, v.data.emis.find { it.id == selectedId }, { selectedType="" })
                selectedType == "loan" -> LoanForm(v, v.data.loans.find { it.id == selectedId }, { selectedType="" })
                selectedType == "debt" -> DebtForm(v, v.data.debts.find { it.id == selectedId }, { selectedType="" })
                else -> when(tab) {
                    0 -> Dashboard(v)
                    1 -> EmiList(v, { selectedId=it; selectedType="emi" }, { selectedType="emi"; selectedId="" })
                    2 -> LoanList(v, { selectedId=it; selectedType="loan" }, { selectedType="loan"; selectedId="" })
                    3 -> DebtList(v, { selectedId=it; selectedType="debt" }, { selectedType="debt"; selectedId="" })
                    else -> Reports(v)
                }
            }
        }
    }
}

@Composable
fun Dashboard(v: FinanceViewModel) {
    val emiRemain = v.data.emis.sumOf { it.payments.filter { p -> p.paidDate == null }.sumOf { p -> p.amount } }
    val loanRemain = v.data.loans.sumOf { it.payments.filter { p -> p.paidDate == null }.sumOf { p -> p.amount } }
    val debtRemain = v.data.debts.sumOf { d -> max(0.0, d.originalAmount - d.payments.sumOf { it.amount }) }
    val monthly = v.data.emis.sumOf { it.monthlyPayment } + v.data.loans.sumOf { it.monthlyPayment }
    val next = (v.data.emis.flatMap { x -> x.payments.filter { it.paidDate == null }.map { Triple(x.name, it.dueDate, it.amount) } } +
            v.data.loans.flatMap { x -> x.payments.filter { it.paidDate == null }.map { Triple(x.name, it.dueDate, it.amount) } })
        .minByOrNull { it.second }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Financial Overview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard("Monthly", money(monthly), Modifier.weight(1f))
            SummaryCard("EMI Left", money(emiRemain), Modifier.weight(1f))
        }}
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard("Loan Left", money(loanRemain), Modifier.weight(1f))
            SummaryCard("Debt Left", money(debtRemain), Modifier.weight(1f))
        }}
        item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) {
            Text("Next Payment", fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            if (next == null) Text("No pending EMI or loan payments.")
            else { Text(next.first, style=MaterialTheme.typography.titleMedium); Text(money(next.third), style=MaterialTheme.typography.headlineSmall); Text("Due ${dateText(next.second)}") }
        }}}
    }
}

@Composable
fun SummaryCard(title: String, value: String, m: Modifier) {
    Card(m) { Column(Modifier.padding(14.dp)) { Text(title, style=MaterialTheme.typography.labelMedium); Text(value, fontWeight=FontWeight.Bold) } }
}

@Composable
fun EmiList(v: FinanceViewModel, onOpen:(String)->Unit, onAdd:()->Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(16.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
        item { Text("EMI Plans", style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Bold) }
        if (v.data.emis.isEmpty()) item { Text("No EMI plans yet. Tap + to add one.") }
        items(v.data.emis) { x ->
            Card(Modifier.fillMaxWidth(), onClick={onOpen(x.id)}) {
                Column(Modifier.padding(16.dp)) {
                    Text(x.name, style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold)
                    Text("${x.category} • ${money(x.monthlyPayment)} / month")
                    val paid=x.payments.count{it.paidDate!=null}
                    Text("$paid/${x.installments} paid • Remaining ${money(x.payments.filter{it.paidDate==null}.sumOf{it.amount})}")
                    LinearProgressIndicator(progress={if(x.installments==0)0f else paid.toFloat()/x.installments}, Modifier.fillMaxWidth().padding(top=8.dp))
                }
            }
        }
    }
}

@Composable
fun LoanList(v: FinanceViewModel, onOpen:(String)->Unit, onAdd:()->Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(16.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
        item { Text("Loans", style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Bold) }
        if(v.data.loans.isEmpty()) item { Text("No loans yet. Tap + to add one.") }
        items(v.data.loans) { x ->
            Card(Modifier.fillMaxWidth(), onClick={onOpen(x.id)}) { Column(Modifier.padding(16.dp)) {
                Text(x.name, style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold)
                Text("${x.type} • ${x.lender}")
                Text("${money(x.monthlyPayment)} / month • ${x.payments.count{it.paidDate!=null}}/${x.installments} paid")
                Text("Remaining ${money(x.payments.filter{it.paidDate==null}.sumOf{it.amount})}")
            }}
        }
    }
}

@Composable
fun DebtList(v: FinanceViewModel, onOpen:(String)->Unit, onAdd:()->Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(16.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
        item { Text("Debts", style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Bold) }
        if(v.data.debts.isEmpty()) item { Text("No debts yet. Tap + to add one.") }
        items(v.data.debts) { x ->
            val paid=x.payments.sumOf{it.amount}; val remain=max(0.0,x.originalAmount-paid)
            Card(Modifier.fillMaxWidth(), onClick={onOpen(x.id)}) { Column(Modifier.padding(16.dp)) {
                Text(x.name, style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold)
                Text(x.direction)
                Text("Original ${money(x.originalAmount)} • Paid ${money(paid)}")
                Text("Remaining ${money(remain)}")
                LinearProgressIndicator(progress={if(x.originalAmount<=0)0f else (paid/x.originalAmount).toFloat().coerceIn(0f,1f)}, Modifier.fillMaxWidth().padding(top=8.dp))
            }}
        }
    }
}

@Composable
fun PaymentHistory(payments: List<Payment>, onPaid: ((Int)->Unit)? = null) {
    Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
        payments.forEach { p ->
            Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp), verticalAlignment=Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Payment ${p.number}", fontWeight=FontWeight.Bold)
                    Text("Due ${dateText(p.dueDate)} • ${money(p.amount)}")
                    if(p.paidDate!=null) Text("Paid ${dateTimeText(p.paidDate)}")
                }
                if(p.paidDate==null && onPaid!=null) Button(onClick={onPaid(p.number)}) { Text("Mark Paid") }
                else if(p.paidDate!=null) Text("PAID", fontWeight=FontWeight.Bold, color=MaterialTheme.colorScheme.primary)
            }}
        }
    }
}

@Composable
fun EmiForm(v: FinanceViewModel, existing: EmiItem?, done:()->Unit) {
    var name by remember{mutableStateOf(existing?.name?:"")}
    var category by remember{mutableStateOf(existing?.category?:"Electronics")}
    var seller by remember{mutableStateOf(existing?.seller?:"")}
    var price by remember{mutableStateOf(existing?.price?.toString()?:"")}
    var down by remember{mutableStateOf(existing?.downPayment?.toString()?:"0")}
    var interestRate by remember{mutableStateOf(existing?.interestRate?.toString()?:"0")}
    var interestAmount by remember{mutableStateOf(existing?.interestAmount?.toString()?:"0")}
    var installments by remember{mutableStateOf(existing?.installments?.toString()?:"12")}
    var dueDay by remember{mutableStateOf(existing?.dueDay?.toString()?:"10")}
    var previousPaid by remember{mutableStateOf(existing?.payments?.count{it.paidDate!=null}?.toString()?:"0")}
    var reminders by remember{mutableStateOf(existing?.reminderDays?.joinToString(",")?:"7,3,1,0")}
    var error by remember{mutableStateOf("")}
    val p=price.toDoubleOrNull()?:0.0; val d=down.toDoubleOrNull()?:0.0; val rate=interestRate.toDoubleOrNull()?:0.0
    val fin=max(0.0,p-d); val intAmt=if(interestAmount.toDoubleOrNull()!=null && interestAmount.toDoubleOrNull()!!>0) interestAmount.toDouble() else fin*rate/100.0
    val total=fin+intAmt; val n=installments.toIntOrNull()?:0; val monthly=if(n>0)total/n else 0.0

    FormColumn(title=if(existing==null)"Add EMI":"Edit EMI") {
        Field("Item name",name){name=it}; Field("Category",category){category=it}; Field("Seller / Provider",seller){seller=it}
        Field("Purchase price",price){price=it}; Field("Down payment",down){down=it}; Field("Interest rate % (optional)",interestRate){interestRate=it}
        Field("Fixed interest amount (optional)",interestAmount){interestAmount=it}; Field("Installments",installments){installments=it}; Field("Monthly due day 1-28",dueDay){dueDay=it}
        Field("Previous installments already paid",previousPaid){previousPaid=it}; Field("Reminder days, comma separated (0=due date)",reminders){reminders=it}
        Card(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text("Calculated",fontWeight=FontWeight.Bold);Text("Financed: ${money(fin)}");Text("Interest: ${money(intAmt)}");Text("Total payable: ${money(total)}");Text("Monthly: ${money(monthly)}")}}
        if(error.isNotEmpty()) Text(error,color=MaterialTheme.colorScheme.error)
        Button(onClick={
            val nn=name.trim(); val count=n; val prev=previousPaid.toIntOrNull()?:0; val day=dueDay.toIntOrNull()?:0
            error=when{nn.isEmpty()->"Enter item name.";p<=0->"Enter a valid price.";d<0||d>=p->"Check down payment.";count<=0->"Enter installments.";prev !in 0..count->"Previous paid must be 0 to total installments.";day !in 1..28->"Due day must be 1-28.";parseReminders(reminders).isEmpty()->"Enter at least one reminder day.";else->""}
            if(error.isEmpty()){
                val first=existing?.payments?.minOfOrNull{it.dueDate}?:dueDate(System.currentTimeMillis(),day)
                val oldPaid=existing?.payments?.filter{it.paidDate!=null}?.associateBy{it.number}?:emptyMap()
                val pays=(1..count).map{i->Payment(i,addMonths(first,i-1),monthly,oldPaid[i]?.paidDate ?: if(existing==null && i<=prev) System.currentTimeMillis() else null)}
                val x=EmiItem(existing?.id?:UUID.randomUUID().toString(),nn,category.trim(),seller.trim(),p,d,fin,rate,intAmt,total,count,monthly,existing?.startDate?:System.currentTimeMillis(),day,parseReminders(reminders),pays)
                if(existing==null)v.addEmi(x) else v.updateEmi(x); done()
            }
        },Modifier.fillMaxWidth()){Text(if(existing==null)"Save EMI":"Update EMI")}
        if(existing!=null){Text("Payment history",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);PaymentHistory(existing.payments){v.markEmiPaid(existing.id,it)}}
    }
}

@Composable
fun LoanForm(v: FinanceViewModel, existing: Loan?, done:()->Unit) {
    var name by remember{mutableStateOf(existing?.name?:"")}
    var type by remember{mutableStateOf(existing?.type?:"Office Loan")}
    var lender by remember{mutableStateOf(existing?.lender?:"")}
    var principal by remember{mutableStateOf(existing?.principal?.toString()?:"")}
    var rate by remember{mutableStateOf(existing?.interestRate?.toString()?:"0")}
    var interest by remember{mutableStateOf(existing?.interestAmount?.toString()?:"0")}
    var installments by remember{mutableStateOf(existing?.installments?.toString()?:"12")}
    var day by remember{mutableStateOf(existing?.dueDay?.toString()?:"10")}
    var prev by remember{mutableStateOf(existing?.payments?.count{it.paidDate!=null}?.toString()?:"0")}
    var reminders by remember{mutableStateOf(existing?.reminderDays?.joinToString(",")?:"7,3,1,0")}
    var error by remember{mutableStateOf("")}
    val pr=principal.toDoubleOrNull()?:0.0; val rr=rate.toDoubleOrNull()?:0.0; val ia=interest.toDoubleOrNull()?:0.0
    val interestAmt=if(ia>0)ia else pr*rr/100.0; val total=pr+interestAmt; val n=installments.toIntOrNull()?:0; val monthly=if(n>0)total/n else 0.0
    FormColumn(if(existing==null)"Add Loan" else "Edit Loan"){
        Field("Loan name",name){name=it};Field("Loan type",type){type=it};Field("Lender",lender){lender=it};Field("Principal amount",principal){principal=it}
        Field("Interest rate %",rate){rate=it};Field("Fixed interest amount",interest){interest=it};Field("Installments",installments){installments=it};Field("Due day 1-28",day){day=it}
        Field("Previous repayments already made",prev){prev=it};Field("Reminder days, comma separated",reminders){reminders=it}
        Card(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text("Calculated",fontWeight=FontWeight.Bold);Text("Interest: ${money(interestAmt)}");Text("Total payable: ${money(total)}");Text("Monthly: ${money(monthly)}")}}
        if(error.isNotEmpty())Text(error,color=MaterialTheme.colorScheme.error)
        Button(onClick={
            val count=n;val pp=prev.toIntOrNull()?:0;val dd=day.toIntOrNull()?:0
            error=when{name.isBlank()->"Enter loan name.";pr<=0->"Enter principal.";count<=0->"Enter installments.";pp !in 0..count->"Previous repayments must be 0 to total installments.";dd !in 1..28->"Due day must be 1-28.";parseReminders(reminders).isEmpty()->"Enter reminder days.";else->""}
            if(error.isEmpty()){
                val first=existing?.payments?.minOfOrNull{it.dueDate}?:dueDate(System.currentTimeMillis(),dd)
                val oldPaid=existing?.payments?.filter{it.paidDate!=null}?.associateBy{it.number}?:emptyMap()
                val pays=(1..count).map{i->Payment(i,addMonths(first,i-1),monthly,oldPaid[i]?.paidDate ?: if(existing==null&&i<=pp)System.currentTimeMillis() else null)}
                val x=Loan(existing?.id?:UUID.randomUUID().toString(),name.trim(),type.trim(),lender.trim(),pr,rr,interestAmt,total,count,monthly,existing?.startDate?:System.currentTimeMillis(),dd,parseReminders(reminders),pays)
                if(existing==null)v.addLoan(x) else v.updateLoan(x);done()
            }
        },Modifier.fillMaxWidth()){Text(if(existing==null)"Save Loan" else "Update Loan")}
        if(existing!=null){Text("Repayment history",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);PaymentHistory(existing.payments){v.markLoanPaid(existing.id,it)}}
    }
}

@Composable
fun DebtForm(v: FinanceViewModel, existing: Debt?, done:()->Unit) {
    var name by remember{mutableStateOf(existing?.name?:"")}
    var direction by remember{mutableStateOf(existing?.direction?:"I Owe")}
    var amount by remember{mutableStateOf(existing?.originalAmount?.toString()?:"")}
    var notes by remember{mutableStateOf(existing?.notes?:"")}
    var payment by remember{mutableStateOf("")}
    var error by remember{mutableStateOf("")}
    FormColumn(if(existing==null)"Add Debt" else "Debt Details"){
        Field("Person / organization",name){name=it};Field("Direction (I Owe / Owed to Me)",direction){direction=it};Field("Original amount",amount){amount=it};Field("Notes",notes){notes=it}
        if(existing!=null){
            val paid=existing.payments.sumOf{it.amount};val remain=max(0.0,(amount.toDoubleOrNull()?:existing.originalAmount)-paid)
            Card(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text("Remaining: ${money(remain)}",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);Text("Progress: ${if((amount.toDoubleOrNull()?:0.0)>0"%.1f".format(Locale.US,paid/(amount.toDouble()?:1.0)*100)else"0.0")}%")}}
            Field("New payment amount",payment){payment=it}
            Button(onClick={val a=payment.toDoubleOrNull()?:0.0;if(a>0)v.markDebtPaid(existing.id,a);payment="";},Modifier.fillMaxWidth()){Text("Add Payment")}
            PaymentHistory(existing.payments)
        } else {
            if(error.isNotEmpty())Text(error,color=MaterialTheme.colorScheme.error)
            Button(onClick={val a=amount.toDoubleOrNull()?:0.0;if(name.isBlank()||a<=0){error="Enter a name and valid amount."}else{v.addDebt(Debt(name=name.trim(),direction=direction.trim(),originalAmount=a,dueDate=null,notes=notes.trim(),payments=emptyList()));done()}},Modifier.fillMaxWidth()){Text("Save Debt")}
        }
    }
}

@Composable
fun FormColumn(title:String, content:@Composable ColumnScope.()->Unit){
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){item{Text(title,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)};item{Column(verticalArrangement=Arrangement.spacedBy(10.dp),content=content)}}
}

@Composable
fun Field(label:String,value:String,onChange:(String)->Unit){
    OutlinedTextField(value,onChange,label={Text(label)},modifier=Modifier.fillMaxWidth(),singleLine=true)
}

@Composable
fun Reports(v: FinanceViewModel) {
    val context=LocalContext.current
    var pending by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null && pending.isNotEmpty()) writePdfToUri(context, uri, pending)
        pending = ""
    }
    fun make(name:String, body:String){ pending=body; launcher.launch(name) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Reports",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("PDFs are generated locally. Save them in Downloads/Documents or another user-selected folder so they remain after uninstall.")
        Button(onClick={make("Complete_Finance_Report.pdf",buildCompleteReport(v.data))},Modifier.fillMaxWidth()){Icon(Icons.Default.PictureAsPdf,null);Spacer(Modifier.width(8.dp));Text("Generate Complete Report")}
        v.data.emis.forEach{x->OutlinedButton(onClick={make("EMI_${safe(x.name)}.pdf",buildEmiReport(x))},Modifier.fillMaxWidth()){Text("PDF: ${x.name}")}}
        v.data.loans.forEach{x->OutlinedButton(onClick={make("Loan_${safe(x.name)}.pdf",buildLoanReport(x))},Modifier.fillMaxWidth()){Text("PDF: ${x.name}")}}
        v.data.debts.forEach{x->OutlinedButton(onClick={make("Debt_${safe(x.name)}.pdf",buildDebtReport(x))},Modifier.fillMaxWidth()){Text("PDF: ${x.name}")}}
    }
}

fun safe(s:String)=s.replace(Regex("[^A-Za-z0-9_-]"),"_").take(40)

fun buildCompleteReport(d:FinanceData)=buildString{
    appendLine("MY FINANCE TRACKER — COMPLETE REPORT");appendLine("Generated: ${dateTimeText(System.currentTimeMillis())}");appendLine()
    appendLine("EMI SUMMARY");appendLine("Plans: ${d.emis.size}");appendLine("Remaining: ${money(d.emis.sumOf{it.payments.filter{p->p.paidDate==null}.sumOf{p->p.amount}})}");appendLine()
    appendLine("LOAN SUMMARY");appendLine("Loans: ${d.loans.size}");appendLine("Remaining: ${money(d.loans.sumOf{it.payments.filter{p->p.paidDate==null}.sumOf{p->p.amount}})}");appendLine()
    appendLine("DEBT SUMMARY");appendLine("Debts: ${d.debts.size}");appendLine("Remaining: ${money(d.debts.sumOf{max(0.0,it.originalAmount-it.payments.sumOf{p->p.amount})})}");appendLine()
    d.emis.forEach{appendLine();append(buildEmiReport(it))}
    d.loans.forEach{appendLine();append(buildLoanReport(it))}
    d.debts.forEach{appendLine();append(buildDebtReport(it))}
}

fun buildEmiReport(x:EmiItem)=buildString{
    appendLine("EMI REPORT");appendLine("Item: ${x.name}");appendLine("Category: ${x.category}");appendLine("Seller: ${x.seller}")
    appendLine("Price: ${money(x.price)}");appendLine("Down payment: ${money(x.downPayment)}");appendLine("Financed amount: ${money(x.financedAmount)}")
    appendLine("Interest rate: ${x.interestRate}%");appendLine("Interest amount: ${money(x.interestAmount)}");appendLine("Total payable: ${money(x.totalPayable)}")
    appendLine("Monthly payment: ${money(x.monthlyPayment)}");appendLine("Installments: ${x.installments}");appendLine("Due day: ${x.dueDay}")
    appendLine("Reminder days: ${x.reminderDays.joinToString(", ")}");appendLine("Progress: ${x.payments.count{it.paidDate!=null}}/${x.installments}")
    appendLine("Remaining: ${money(x.payments.filter{it.paidDate==null}.sumOf{it.amount})}");appendLine("PAYMENT HISTORY")
    x.payments.forEach{appendLine("#${it.number} | Due ${dateText(it.dueDate)} | ${money(it.amount)} | ${if(it.paidDate==null)"PENDING" else "PAID ${dateText(it.paidDate)}"}")}
}

fun buildLoanReport(x:Loan)=buildString{
    appendLine("LOAN REPORT");appendLine("Loan: ${x.name}");appendLine("Type: ${x.type}");appendLine("Lender: ${x.lender}")
    appendLine("Principal: ${money(x.principal)}");appendLine("Interest rate: ${x.interestRate}%");appendLine("Interest: ${money(x.interestAmount)}");appendLine("Total payable: ${money(x.totalPayable)}")
    appendLine("Monthly payment: ${money(x.monthlyPayment)}");appendLine("Installments: ${x.installments}");appendLine("Due day: ${x.dueDay}")
    appendLine("Progress: ${x.payments.count{it.paidDate!=null}}/${x.installments}");appendLine("Remaining: ${money(x.payments.filter{it.paidDate==null}.sumOf{it.amount})}");appendLine("REPAYMENT HISTORY")
    x.payments.forEach{appendLine("#${it.number} | Due ${dateText(it.dueDate)} | ${money(it.amount)} | ${if(it.paidDate==null)"PENDING" else "PAID ${dateText(it.paidDate)}"}")}
}

fun buildDebtReport(x:Debt)=buildString{
    appendLine("DEBT REPORT");appendLine("Name: ${x.name}");appendLine("Direction: ${x.direction}");appendLine("Original amount: ${money(x.originalAmount)}")
    appendLine("Paid: ${money(x.payments.sumOf{it.amount})}");appendLine("Remaining: ${money(max(0.0,x.originalAmount-x.payments.sumOf{it.amount}))}")
    appendLine("Notes: ${x.notes}");appendLine("PAYMENT HISTORY");x.payments.forEach{appendLine("#${it.number} | ${dateTimeText(it.paidDate?:it.dueDate)} | ${money(it.amount)}")}
}

fun writePdfToUri(context:Context,uri:android.net.Uri,text:String){
    context.contentResolver.openOutputStream(uri)?.use{out->
        val doc=PdfDocument();val paint=Paint().apply{textSize=11f}
        var pageNo=1;var page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNo).create());var canvas=page.canvas;var y=35f
        text.lines().forEach{line->
            if(y>810){doc.finishPage(page);pageNo++;page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNo).create());canvas=page.canvas;y=35f}
            canvas.drawText(line.take(95),30f,y,paint);y+=16f
        }
        doc.finishPage(page);doc.writeTo(out);doc.close()
    }
}

class MainActivity:ComponentActivity(){
    private lateinit var security:SecurityStore
    private var unlocked=false
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState);security=SecurityStore(this)
        showContent()
    }
    override fun onStop(){super.onStop();if(!isChangingConfigurations)unlocked=false}
    private fun showContent(){setContent{if(!security.hasPassword())SetupScreen{security.setPassword(it);unlocked=true;showContent()}else if(!unlocked)LockScreen{if(security.verify(it)){unlocked=true;showContent()}}else FinanceApp{unlocked=false;showContent()}}}
}

@Composable
fun SetupScreen(onSet:(String)->Unit){
    var p by remember{mutableStateOf("")};var c by remember{mutableStateOf("")};var e by remember{mutableStateOf("")}
    Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally){
        Icon(Icons.Default.Lock,null,Modifier.size(64.dp));Text("Secure My Finance Tracker",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(16.dp));Text("Create an app password. This password is stored only as a protected hash on this phone.")
        Spacer(Modifier.height(16.dp));OutlinedTextField(p,{p=it},label={Text("Password")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Spacer(Modifier.height(8.dp));OutlinedTextField(c,{c=it},label={Text("Confirm password")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        if(e.isNotEmpty())Text(e,color=MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp));Button(onClick={e=when{p.length<6->"Use at least 6 characters.";p!=c->"Passwords do not match.";else->""};if(e.isEmpty())onSet(p)},Modifier.fillMaxWidth()){Text("Create Password")}
    }
}

@Composable
fun LockScreen(onUnlock:(String)->Unit){
    var p by remember{mutableStateOf("")};var e by remember{mutableStateOf("")}
    Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally){
        Icon(Icons.Default.Lock,null,Modifier.size(64.dp));Text("My Finance Tracker",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Enter your app password to continue.")
        Spacer(Modifier.height(16.dp));OutlinedTextField(p,{p=it},label={Text("Password")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        if(e.isNotEmpty())Text(e,color=MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp));Button(onClick={if(p.isBlank())e="Enter your password." else {onUnlock(p);e="Incorrect password."}},Modifier.fillMaxWidth()){Text("Unlock")}
    }
}
