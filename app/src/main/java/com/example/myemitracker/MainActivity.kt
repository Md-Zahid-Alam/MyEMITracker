package com.example.myemitracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

data class EmiPlan(
    val phoneName: String = "",
    val price: Double = 0.0,
    val downPayment: Double = 0.0,
    val installments: Int = 0,
    val monthlyEmi: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val dueDay: Int = 1,
    val payments: List<Payment> = emptyList()
)

data class Payment(
    val installment: Int,
    val dueDate: Long,
    val amount: Double,
    val paidDate: Long? = null
)

class EmiViewModel(private val context: Context) : ViewModel() {
    private val prefs = context.getSharedPreferences("emi_data", Context.MODE_PRIVATE)

    var plan by mutableStateOf(load())
        private set

    private fun load(): EmiPlan? {
        val name = prefs.getString("phoneName", null) ?: return null
        val price = prefs.getString("price", "0")!!.toDouble()
        val down = prefs.getString("down", "0")!!.toDouble()
        val count = prefs.getInt("installments", 0)
        val monthly = prefs.getString("monthly", "0")!!.toDouble()
        val start = prefs.getLong("startDate", System.currentTimeMillis())
        val dueDay = prefs.getInt("dueDay", 1)
        val payments = (1..count).map { i ->
            Payment(
                installment = i,
                dueDate = addMonths(start, i - 1),
                amount = monthly,
                paidDate = if (prefs.getLong("paid_$i", 0L) == 0L) null
                else prefs.getLong("paid_$i", 0L)
            )
        }
        return EmiPlan(name, price, down, count, monthly, start, dueDay, payments)
    }

    fun save(
        name: String,
        price: Double,
        down: Double,
        count: Int,
        startDate: Long,
        dueDay: Int
    ) {
        val remaining = max(0.0, price - down)
        val monthly = if (count > 0) remaining / count else 0.0

        prefs.edit()
            .putString("phoneName", name)
            .putString("price", price.toString())
            .putString("down", down.toString())
            .putInt("installments", count)
            .putString("monthly", monthly.toString())
            .putLong("startDate", startDate)
            .putInt("dueDay", dueDay.coerceIn(1, 28))
            .apply()

        plan = load()
    }

    fun markPaid(number: Int) {
        prefs.edit().putLong("paid_$number", System.currentTimeMillis()).apply()
        plan = load()
    }

    fun delete() {
        prefs.edit().clear().apply()
        plan = null
    }

    companion object {
        fun addMonths(date: Long, months: Int): Long {
            val c = Calendar.getInstance().apply { timeInMillis = date }
            c.add(Calendar.MONTH, months)
            return c.timeInMillis
        }
    }
}

@Composable
fun getVm(context: Context): EmiViewModel {
    return viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EmiViewModel(context.applicationContext) as T
        }
    })
}

fun money(value: Double): String {
    return "৳" + NumberFormat.getNumberInstance(Locale.US).format(value)
}

fun dateText(time: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(time))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEmiApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val vm = getVm(context)
    var screen by remember { mutableStateOf(if (vm.plan == null) "add" else "home") }

    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
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
                    }
                )
            },
            bottomBar = {
                if (screen == "home" && vm.plan != null) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = true,
                            onClick = { screen = "home" },
                            icon = { Icon(Icons.Default.Home, null) },
                            label = { Text("Dashboard") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { screen = "history" },
                            icon = { Icon(Icons.Default.Home, null) },
                            label = { Text("History") }
                        )
                    }
                }
            },
            floatingActionButton = {
                if (screen == "home" && vm.plan != null) {
                    FloatingActionButton(onClick = { screen = "edit" }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (screen) {
                    "home" -> Dashboard(vm, onHistory = { screen = "history" })
                    "history" -> HistoryScreen(vm)
                    "edit" -> AddEditScreen(vm, onDone = { screen = "home" })
                    "add" -> AddEditScreen(vm, onDone = { screen = "home" })
                }
            }
        }
    }
}

@Composable
fun Dashboard(vm: EmiViewModel, onHistory: () -> Unit) {
    val plan = vm.plan ?: return
    val paid = plan.payments.count { it.paidDate != null }
    val remaining = plan.payments.count { it.paidDate == null }
    val paidAmount = paid * plan.monthlyEmi
    val remainingAmount = remaining * plan.monthlyEmi
    val progress = if (plan.installments == 0) 0f else paid.toFloat() / plan.installments

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, null, Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(plan.phoneName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Phone EMI Plan")
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    LinearProgressIndicator(progress = { progress }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Text("$paid of ${plan.installments} installments paid")
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Monthly EMI", money(plan.monthlyEmi), Modifier.weight(1f))
                StatCard("Remaining", money(remainingAmount), Modifier.weight(1f))
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Paid", money(paidAmount), Modifier.weight(1f))
                StatCard("EMIs Left", remaining.toString(), Modifier.weight(1f))
            }
        }

        item {
            val next = plan.payments.firstOrNull { it.paidDate == null }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Next Payment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    if (next != null) {
                        Text(money(next.amount), style = MaterialTheme.typography.headlineSmall)
                        Text("Installment ${next.installment} • ${dateText(next.dueDate)}")
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { vm.markPaid(next.installment) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Mark as Paid")
                        }
                    } else {
                        Text("All installments are paid 🎉")
                    }
                }
            }
        }

        item {
            OutlinedButton(onClick = onHistory, Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Home, null)
                Spacer(Modifier.width(8.dp))
                Text("View Payment History")
            }
        }

        item {
            OutlinedButton(
                onClick = { vm.delete() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, null)
                Spacer(Modifier.width(8.dp))
                Text("Delete EMI Plan")
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
fun HistoryScreen(vm: EmiViewModel) {
    val plan = vm.plan ?: return
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Payment History", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
        }
        items(plan.payments) { payment ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Installment ${payment.installment}", fontWeight = FontWeight.Bold)
                        Text("Due: ${dateText(payment.dueDate)}")
                        Text(money(payment.amount))
                    }
                    if (payment.paidDate != null) {
                        Text("PAID", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    } else {
                        Text("PENDING", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(vm: EmiViewModel, onDone: () -> Unit) {
    val existing = vm.plan
    var name by remember { mutableStateOf(existing?.phoneName ?: "") }
    var price by remember { mutableStateOf(if (existing != null) existing.price.toString() else "") }
    var down by remember { mutableStateOf(if (existing != null) existing.downPayment.toString() else "") }
    var installments by remember { mutableStateOf(if (existing != null) existing.installments.toString() else "") }
    var dueDay by remember { mutableStateOf(if (existing != null) existing.dueDay.toString() else "10") }
    var error by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                if (existing == null) "Add Your Phone EMI" else "Edit EMI Plan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Phone Model") },
                placeholder = { Text("e.g. Samsung Galaxy S25") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Phone Price (৳)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = down,
                onValueChange = { down = it },
                label = { Text("Down Payment (৳)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = installments,
                onValueChange = { installments = it },
                label = { Text("Number of Installments") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = dueDay,
                onValueChange = { dueDay = it },
                label = { Text("Monthly Due Day (1-28)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            val p = price.toDoubleOrNull() ?: 0.0
            val d = down.toDoubleOrNull() ?: 0.0
            val n = installments.toIntOrNull() ?: 0
            val monthly = if (n > 0) max(0.0, p - d) / n else 0.0
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Calculated Monthly EMI", style = MaterialTheme.typography.labelLarge)
                    Text(money(monthly), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = {
                    val p = price.toDoubleOrNull()
                    val d = down.toDoubleOrNull()
                    val n = installments.toIntOrNull()
                    val day = dueDay.toIntOrNull()
                    when {
                        name.isBlank() -> error = "Please enter the phone model."
                        p == null || p <= 0 -> error = "Please enter a valid phone price."
                        d == null || d < 0 || d >= p -> error = "Please enter a valid down payment."
                        n == null || n <= 0 -> error = "Please enter the number of installments."
                        day == null || day !in 1..28 -> error = "Due day must be between 1 and 28."
                        else -> {
                            vm.save(name.trim(), p, d, n, System.currentTimeMillis(), day)
                            onDone()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text("Save EMI Plan")
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "emi_reminders",
                "EMI Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        setContent {
            MyEmiApp()
        }
    }
}
