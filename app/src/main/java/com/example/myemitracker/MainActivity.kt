package com.example.myemitracker

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.max

private const val CHANNEL_ID = "emi_reminders"
private const val PREFS = "emi_v2_data"
private const val KEY_PLANS = "plans"

// -------------------- Data --------------------

data class EmiPayment(
    val installment: Int,
    val dueDate: Long,
    val amount: Double,
    val paidDate: Long? = null
)

data class EmiPlan(
    val id: String = UUID.randomUUID().toString(),
    val phoneName: String,
    val price: Double,
    val downPayment: Double,
    val installments: Int,
    val monthlyEmi: Double,
    val dueDay: Int,
    val startDate: Long,
    val payments: List<EmiPayment>
)

// -------------------- Date / formatting helpers --------------------

fun money(value: Double): String =
    "৳" + NumberFormat.getNumberInstance(Locale.US).format(value)

fun dateText(time: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(time))

fun dateTimeText(time: Long): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(time))

fun firstDueDate(start: Long, dueDay: Int): Long {
    val base = Calendar.getInstance().apply {
        timeInMillis = start
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val candidate = base.clone() as Calendar
    candidate.set(Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, 28))
    if (candidate.timeInMillis < start) candidate.add(Calendar.MONTH, 1)
    return candidate.timeInMillis
}

fun addMonths(date: Long, months: Int): Long {
    val c = Calendar.getInstance().apply { timeInMillis = date }
    c.add(Calendar.MONTH, months)
    return c.timeInMillis
}

// -------------------- Notification scheduling --------------------

object EmiNotificationScheduler {
    fun schedulePlan(context: Context, plan: EmiPlan) {
        cancelPlan(context, plan)
        plan.payments.filter { it.paidDate == null }.forEach { payment ->
            schedule(context, plan, payment, false)
            schedule(context, plan, payment, true)
        }
    }

    private fun schedule(
        context: Context,
        plan: EmiPlan,
        payment: EmiPayment,
        dueDay: Boolean
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderTime = if (dueDay) payment.dueDate else payment.dueDate - 24L * 60L * 60L * 1000L
        if (reminderTime <= System.currentTimeMillis()) return

        val intent = Intent(context, EmiReminderReceiver::class.java).apply {
            putExtra("planId", plan.id)
            putExtra("phoneName", plan.phoneName)
            putExtra("amount", payment.amount)
            putExtra("installment", payment.installment)
            putExtra("dueDate", payment.dueDate)
            putExtra("isDue", dueDay)
        }
        val requestCode = (plan.id.hashCode() * 31 + payment.installment * 2 + if (dueDay) 1 else 0)
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pending)
    }

    fun cancelPlan(context: Context, plan: EmiPlan) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        plan.payments.forEach { payment ->
            for (dueDay in listOf(false, true)) {
                val requestCode = (plan.id.hashCode() * 31 + payment.installment * 2 + if (dueDay) 1 else 0)
                val intent = Intent(context, EmiReminderReceiver::class.java)
                val pending = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pending != null) {
                    alarmManager.cancel(pending)
                    pending.cancel()
                }
            }
        }
    }

    fun rescheduleAll(context: Context, plans: List<EmiPlan>) {
        plans.forEach { schedulePlan(context, it) }
    }
}

class EmiReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val phoneName = intent.getStringExtra("phoneName") ?: "Phone EMI"
        val amount = intent.getDoubleExtra("amount", 0.0)
        val installment = intent.getIntExtra("installment", 1)
        val dueDate = intent.getLongExtra("dueDate", System.currentTimeMillis())
        val isDue = intent.getBooleanExtra("isDue", false)

        createNotificationChannel(context)
        val title = if (isDue) "EMI Due Today" else "EMI Reminder"
        val text = if (isDue) {
            "$phoneName: ${money(amount)} is due today (installment $installment)."
        } else {
            "$phoneName: ${money(amount)} is due tomorrow (installment $installment)."
        }

        if (Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$text\nDue date: ${dateText(dueDate)}"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(context).notify(
                (intent.getStringExtra("planId") ?: "").hashCode() + installment + if (isDue) 10000 else 0,
                notification
            )
        }
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= 26) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "EMI Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminders for upcoming and due phone EMIs"
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}

// -------------------- Storage --------------------

class EmiRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun loadPlans(): List<EmiPlan> {
        val raw = prefs.getString(KEY_PLANS, "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) add(fromJson(array.getJSONObject(i)))
            }
        }.getOrDefault(emptyList())
    }

    fun savePlans(plans: List<EmiPlan>) {
        val array = JSONArray()
        plans.forEach { array.put(toJson(it)) }
        prefs.edit().putString(KEY_PLANS, array.toString()).apply()
    }

    fun backupJson(): String = JSONArray().apply {
        loadPlans().forEach { put(toJson(it)) }
    }.toString(2)

    fun restoreJson(json: String): List<EmiPlan> {
        val array = JSONArray(json)
        val restored = buildList {
            for (i in 0 until array.length()) add(fromJson(array.getJSONObject(i)))
        }
        savePlans(restored)
        return restored
    }

    private fun toJson(plan: EmiPlan): JSONObject = JSONObject().apply {
        put("id", plan.id)
        put("phoneName", plan.phoneName)
        put("price", plan.price)
        put("downPayment", plan.downPayment)
        put("installments", plan.installments)
        put("monthlyEmi", plan.monthlyEmi)
        put("dueDay", plan.dueDay)
        put("startDate", plan.startDate)
        put("payments", JSONArray().apply {
            plan.payments.forEach { p ->
                put(JSONObject().apply {
                    put("installment", p.installment)
                    put("dueDate", p.dueDate)
                    put("amount", p.amount)
                    put("paidDate", p.paidDate ?: JSONObject.NULL)
                })
            }
        })
    }

    private fun fromJson(obj: JSONObject): EmiPlan {
        val paymentsJson = obj.optJSONArray("payments") ?: JSONArray()
        val payments = buildList {
            for (i in 0 until paymentsJson.length()) {
                val p = paymentsJson.getJSONObject(i)
                add(
                    EmiPayment(
                        installment = p.getInt("installment"),
                        dueDate = p.getLong("dueDate"),
                        amount = p.getDouble("amount"),
                        paidDate = if (p.isNull("paidDate")) null else p.getLong("paidDate")
                    )
                )
            }
        }
        return EmiPlan(
            id = obj.optString("id", UUID.randomUUID().toString()),
            phoneName = obj.optString("phoneName", "Phone"),
            price = obj.optDouble("price", 0.0),
            downPayment = obj.optDouble("downPayment", 0.0),
            installments = obj.optInt("installments", payments.size),
            monthlyEmi = obj.optDouble("monthlyEmi", 0.0),
            dueDay = obj.optInt("dueDay", 1).coerceIn(1, 28),
            startDate = obj.optLong("startDate", System.currentTimeMillis()),
            payments = payments
        )
    }
}

class EmiViewModel(private val context: Context) : ViewModel() {
    private val repo = EmiRepository(context.applicationContext)
    var plans by mutableStateOf(repo.loadPlans())
        private set

    fun addPlan(
        name: String,
        price: Double,
        down: Double,
        count: Int,
        dueDay: Int
    ) {
        val monthly = max(0.0, price - down) / count
        val start = System.currentTimeMillis()
        val firstDue = firstDueDate(start, dueDay)
        val payments = (1..count).map { i ->
            EmiPayment(i, addMonths(firstDue, i - 1), monthly)
        }
        val plan = EmiPlan(
            phoneName = name.trim(),
            price = price,
            downPayment = down,
            installments = count,
            monthlyEmi = monthly,
            dueDay = dueDay,
            startDate = start,
            payments = payments
        )
        plans = plans + plan
        repo.savePlans(plans)
        EmiNotificationScheduler.schedulePlan(context, plan)
    }

    fun updatePlan(
        old: EmiPlan,
        name: String,
        price: Double,
        down: Double,
        count: Int,
        dueDay: Int
    ) {
        EmiNotificationScheduler.cancelPlan(context, old)
        val monthly = max(0.0, price - down) / count
        val firstDue = firstDueDate(old.startDate, dueDay)
        val oldPaid = old.payments.filter { it.paidDate != null }.associateBy { it.installment }
        val payments = (1..count).map { i ->
            EmiPayment(i, addMonths(firstDue, i - 1), monthly, oldPaid[i]?.paidDate)
        }
        val updated = old.copy(
            phoneName = name.trim(), price = price, downPayment = down,
            installments = count, monthlyEmi = monthly, dueDay = dueDay, payments = payments
        )
        plans = plans.map { if (it.id == old.id) updated else it }
        repo.savePlans(plans)
        EmiNotificationScheduler.schedulePlan(context, updated)
    }

    fun markPaid(planId: String, installment: Int) {
        plans = plans.map { plan ->
            if (plan.id != planId) plan else plan.copy(
                payments = plan.payments.map { p ->
                    if (p.installment == installment && p.paidDate == null) p.copy(paidDate = System.currentTimeMillis()) else p
                }
            )
        }
        repo.savePlans(plans)
        plans.firstOrNull { it.id == planId }?.let { plan ->
            EmiNotificationScheduler.cancelPlan(context, plan)
            EmiNotificationScheduler.schedulePlan(context, plan)
        }
    }

    fun delete(plan: EmiPlan) {
        EmiNotificationScheduler.cancelPlan(context, plan)
        plans = plans.filterNot { it.id == plan.id }
        repo.savePlans(plans)
    }

    fun backup(): String = repo.backupJson()

    fun restore(json: String): Boolean = runCatching {
        plans.forEach { EmiNotificationScheduler.cancelPlan(context, it) }
        plans = repo.restoreJson(json)
        EmiNotificationScheduler.rescheduleAll(context, plans)
    }.isSuccess
}

@Composable
fun getVm(context: Context): EmiViewModel = viewModel(
    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EmiViewModel(context.applicationContext) as T
        }
    }
)

// -------------------- UI --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEmiApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val vm = getVm(context)
    var screen by remember { mutableStateOf("home") }
    var selectedPlanId by remember { mutableStateOf<String?>(null) }
    var showDelete by remember { mutableStateOf<EmiPlan?>(null) }
    var message by remember { mutableStateOf("") }

    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use {
                    it.write(vm.backup().toByteArray())
                }
                message = "Backup saved successfully."
            }.onFailure { message = "Could not save backup: ${it.message}" }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Empty file")
                if (vm.restore(json)) message = "Backup restored successfully."
                else message = "Invalid backup file."
            }.onFailure { message = "Could not restore backup: ${it.message}" }
        }
    }

    MaterialTheme(colorScheme = lightColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My EMI Tracker", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (screen != "home") {
                            IconButton(onClick = { screen = "home" }) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                    },
                    actions = {
                        if (screen == "home") {
                            IconButton(onClick = { backupLauncher.launch("my-emi-tracker-backup.json") }) {
                                Icon(Icons.Default.Backup, "Backup")
                            }
                            IconButton(onClick = { restoreLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) }) {
                                Icon(Icons.Default.Restore, "Restore")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (screen == "home") {
                    NavigationBar {
                        NavigationBarItem(true, { screen = "home" }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Dashboard") })
                        NavigationBarItem(false, { screen = "plans" }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("EMI Plans") })
                    }
                }
            },
            floatingActionButton = {
                if (screen == "home" || screen == "plans") {
                    FloatingActionButton(onClick = { selectedPlanId = null; screen = "add" }) {
                        Icon(Icons.Default.Add, "Add EMI")
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (screen) {
                    "home" -> Dashboard(vm, onAdd = { screen = "add" }, onPlan = { selectedPlanId = it.id; screen = "details" })
                    "plans" -> PlansScreen(vm, onPlan = { selectedPlanId = it.id; screen = "details" }, onAdd = { screen = "add" })
                    "details" -> vm.plans.firstOrNull { it.id == selectedPlanId }?.let { plan ->
                        PlanDetails(plan, vm, onEdit = { selectedPlanId = plan.id; screen = "edit" }, onBack = { screen = "home" }, onDelete = { showDelete = plan })
                    } ?: run { screen = "home" }
                    "add" -> AddEditScreen(vm, null) { screen = "home" }
                    "edit" -> vm.plans.firstOrNull { it.id == selectedPlanId }?.let { plan -> AddEditScreen(vm, plan) { screen = "details" } }
                }
                if (message.isNotEmpty()) {
                    Card(Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(message, Modifier.weight(1f))
                            Text("OK", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                }
            }
        }

        showDelete?.let { plan ->
            AlertDialog(
                onDismissRequest = { showDelete = null },
                title = { Text("Delete EMI plan?") },
                text = { Text("Delete ${plan.phoneName} and its payment history? Scheduled reminders will also be removed.") },
                confirmButton = {
                    Button(onClick = { vm.delete(plan); showDelete = null; screen = "home" }) { Text("Delete") }
                },
                dismissButton = { OutlinedButton(onClick = { showDelete = null }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
fun Dashboard(vm: EmiViewModel, onAdd: () -> Unit, onPlan: (EmiPlan) -> Unit) {
    val plans = vm.plans
    val totalMonthly = plans.sumOf { it.monthlyEmi }
    val totalRemaining = plans.sumOf { p -> p.payments.filter { it.paidDate == null }.sumOf { it.amount } }
    val totalPaid = plans.sumOf { p -> p.payments.filter { it.paidDate != null }.sumOf { it.amount } }
    val next = plans.flatMap { p -> p.payments.filter { it.paidDate == null }.map { p to it } }
        .minByOrNull { it.second.dueDate }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Total EMI / Month", style = MaterialTheme.typography.labelLarge)
                    Text(money(totalMonthly), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard("Total Paid", money(totalPaid), Modifier.weight(1f))
                        StatCard("Remaining", money(totalRemaining), Modifier.weight(1f))
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Next Payment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    if (next != null) {
                        Text(next.first.phoneName, style = MaterialTheme.typography.titleMedium)
                        Text(money(next.second.amount), style = MaterialTheme.typography.headlineSmall)
                        Text("Installment ${next.second.installment} • ${dateText(next.second.dueDate)}")
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { vm.markPaid(next.first.id, next.second.installment) }, Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.CheckCircle, null); Spacer(Modifier.width(8.dp)); Text("Mark as Paid")
                        }
                    } else Text("No pending EMIs. 🎉")
                }
            }
        }
        item { Text("Your EMI Plans", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (plans.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No EMI plans yet")
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = onAdd) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Add Your First EMI") }
                    }
                }
            }
        } else {
            items(plans) { plan -> PlanCard(plan, onClick = { onPlan(plan) }) }
        }
    }
}


@Composable
fun PlansScreen(vm: EmiViewModel, onPlan: (EmiPlan) -> Unit, onAdd: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("EMI Plans", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Manage all of your phone EMI plans in one place.")
        }
        items(vm.plans) { plan -> PlanCard(plan, onClick = { onPlan(plan) }) }
        item {
            OutlinedButton(onClick = onAdd, Modifier.fillMaxWidth()) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Add New EMI") }
        }
    }
}

@Composable
fun PlanCard(plan: EmiPlan, onClick: () -> Unit) {
    val paid = plan.payments.count { it.paidDate != null }
    val progress = if (plan.installments == 0) 0f else paid.toFloat() / plan.installments
    Card(Modifier.fillMaxWidth(), onClick = onClick) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, null, Modifier.size(30.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(plan.phoneName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${money(plan.monthlyEmi)} / month • Due day ${plan.dueDay}")
                }
                Text("$paid/${plan.installments}")
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator({ progress }, Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun PlanDetails(plan: EmiPlan, vm: EmiViewModel, onEdit: () -> Unit, onBack: () -> Unit, onDelete: () -> Unit) {
    val paid = plan.payments.count { it.paidDate != null }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text(plan.phoneName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Monthly EMI: ${money(plan.monthlyEmi)}")
                    Text("Due day: ${plan.dueDay} of each month")
                    Text("Progress: $paid/${plan.installments} paid")
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onEdit, Modifier.weight(1f)) { Icon(Icons.Default.Edit, null); Spacer(Modifier.width(6.dp)); Text("Edit") }
                        OutlinedButton(onClick = onDelete, Modifier.weight(1f)) { Icon(Icons.Default.Delete, null); Spacer(Modifier.width(6.dp)); Text("Delete") }
                    }
                }
            }
        }
        item { Text("Payment History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(plan.payments) { payment ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Installment ${payment.installment}", fontWeight = FontWeight.Bold)
                        Text("Due: ${dateText(payment.dueDate)}")
                        Text(money(payment.amount))
                        if (payment.paidDate != null) Text("Paid: ${dateTimeText(payment.paidDate)}")
                    }
                    if (payment.paidDate != null) {
                        Text("PAID", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    } else {
                        Button(onClick = { vm.markPaid(plan.id, payment.installment) }) { Text("Mark Paid") }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddEditScreen(vm: EmiViewModel, existing: EmiPlan?, onDone: () -> Unit) {
    var name by remember { mutableStateOf(existing?.phoneName ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var down by remember { mutableStateOf(existing?.downPayment?.toString() ?: "") }
    var installments by remember { mutableStateOf(existing?.installments?.toString() ?: "") }
    var dueDay by remember { mutableStateOf(existing?.dueDay?.toString() ?: "10") }
    var error by remember { mutableStateOf("") }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(if (existing == null) "Add Your Phone EMI" else "Edit EMI Plan", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { OutlinedTextField(name, { name = it }, label = { Text("Phone Model") }, placeholder = { Text("e.g. Samsung Galaxy S25") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
        item { OutlinedTextField(price, { price = it }, label = { Text("Phone Price (৳)") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
        item { OutlinedTextField(down, { down = it }, label = { Text("Down Payment (৳)") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
        item { OutlinedTextField(installments, { installments = it }, label = { Text("Number of Installments") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
        item {
            OutlinedTextField(dueDay, { dueDay = it }, label = { Text("Monthly Due Day (1-28)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Text("You'll receive a reminder 1 day before and another on the due date.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 5.dp))
        }
        item {
            val p = price.toDoubleOrNull() ?: 0.0
            val d = down.toDoubleOrNull() ?: 0.0
            val n = installments.toIntOrNull() ?: 0
            val monthly = if (n > 0) max(0.0, p - d) / n else 0.0
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Calculated Monthly EMI", style = MaterialTheme.typography.labelLarge); Text(money(monthly), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) } }
        }
        item {
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
            Button(onClick = {
                val p = price.toDoubleOrNull(); val d = down.toDoubleOrNull(); val n = installments.toIntOrNull(); val day = dueDay.toIntOrNull()
                error = when {
                    name.isBlank() -> "Please enter the phone model."
                    p == null || p <= 0 -> "Please enter a valid phone price."
                    d == null || d < 0 || d >= p -> "Please enter a valid down payment."
                    n == null || n <= 0 -> "Please enter the number of installments."
                    day == null || day !in 1..28 -> "Due day must be between 1 and 28."
                    else -> ""
                }
                if (error.isEmpty()) {
                    if (existing == null) vm.addPlan(name, p!!, d!!, n!!, day!!) else vm.updatePlan(existing, name, p!!, d!!, n!!, day!!)
                    onDone()
                }
            }, Modifier.fillMaxWidth()) {
                Icon(if (existing == null) Icons.Default.Save else Icons.Default.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text(if (existing == null) "Save EMI Plan" else "Update EMI Plan")
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
        val repository = EmiRepository(this)
        EmiNotificationScheduler.rescheduleAll(this, repository.loadPlans())
        setContent { MyEmiApp() }
    }
}
