package com.mdzahidalam.myfinancetracker.feature.dashboard
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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.heading
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
// DASHBOARD
// ============================================================

@Composable
fun Dashboard(
    viewModel: FinanceViewModel
) {

    val state = viewModel.dashboardUiState

    val emiRemaining =
        state.emis.filterNot { it.archived }.sumOf { emi ->

            emi.payments
                .filter { it.paidDate == null }
                .sumOf { it.amount }
        }

    val loanRemaining =
        state.loans.filterNot { it.archived }.sumOf { loan ->

            loan.payments
                .filter { it.paidDate == null }
                .sumOf { it.amount }
        }

    val debtToPay =
        state.debts.filter { !it.archived && it.direction == "I Owe" }.sumOf { debt ->
            debtRemainingAmount(debt)
        }

    val moneyToReceive =
        state.debts.filter { !it.archived && it.direction == "Owed to Me" }.sumOf { debt ->
            debtRemainingAmount(debt)
        }

    val monthly =
        state.emis.filter { !it.archived && !emiCompleted(it) }.sumOf {
            it.monthlyPayment
        } +
                state.loans.filter { !it.archived && !loanCompleted(it) }.sumOf {
                    it.monthlyPayment
                }

    val todayExpenses =
        state.expenses
            .filter { isCurrentExpenseDay(it.date) }
            .sumOf { it.amount }

    val monthExpenses =
        state.expenses
            .filter { isCurrentExpenseMonth(it.date) }
            .sumOf { it.amount }

    val nextPayment =
        (
                state.emis.filterNot { it.archived }.flatMap { item ->
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
                        state.loans.filterNot { it.archived }.flatMap { item ->
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
            PaddingValues(horizontal = FinanceSpacing.screen, vertical = FinanceSpacing.lg),

        verticalArrangement =
            Arrangement.spacedBy(FinanceSpacing.md)

    ) {

        item {

            Column(verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xxs)) {
                Text("My Finance Tracker", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text("Financial Overview", style = MaterialTheme.typography.headlineLarge)
                Text("A clear view of your money today.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = FinanceShapes.large
            ) {
                Column(Modifier.padding(FinanceSpacing.lg), verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)) {
                    Text("SPENT THIS MONTH", style = MaterialTheme.typography.labelMedium)
                    Text(money(monthExpenses), style = MaterialTheme.typography.displaySmall)
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.58f),
                        shape = FinanceShapes.pill
                    ) {
                        Text(
                            "Today  ${money(todayExpenses)}",
                            modifier = Modifier.padding(horizontal = FinanceSpacing.sm, vertical = FinanceSpacing.xs),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        item { PremiumSectionHeader("Commitments", "Amounts still active") }

        item {
            AdaptiveSummaryPair(
                firstTitle = "Monthly Payments",
                firstValue = money(monthly),
                secondTitle = "EMI Left",
                secondValue = money(emiRemaining)
            )
        }

        item {
            AdaptiveSummaryPair(
                firstTitle = "Loan Left",
                firstValue = money(loanRemaining),
                secondTitle = "Debt to Pay",
                secondValue = money(debtToPay)
            )
        }

        item { SummaryCard("Money to Receive", money(moneyToReceive), Modifier.fillMaxWidth(), tone = "positive") }

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = FinanceShapes.large
            ) {

                Column(
                    modifier =
                        Modifier.padding(FinanceSpacing.lg)
                ) {

                    Text(
                        "Next Payment",
                        style = MaterialTheme.typography.titleMedium
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
            PremiumSectionHeader("Recent Expenses", "Your latest five entries")
        }

        val recentExpenses = state.recentExpenses
            .sortedByDescending { it.date }
            .take(5)

        if (recentExpenses.isEmpty()) {
            item { PremiumEmptyState("No expenses recorded yet.", "Tap Expenses to add your first record.") }
        } else {
            items(recentExpenses, key = { it.id }) { expense ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(FinanceSpacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(expense.title, fontWeight = FontWeight.Bold)
                            Text("${expense.category} • ${expenseDayKey(expense.date)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(money(expense.amount), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdaptiveSummaryPair(
    firstTitle: String,
    firstValue: String,
    secondTitle: String,
    secondValue: String
) {
    val largeText = LocalDensity.current.fontScale >= 1.3f
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 380.dp || largeText) {
            Column(verticalArrangement = Arrangement.spacedBy(FinanceSpacing.sm)) {
                SummaryCard(firstTitle, firstValue, Modifier.fillMaxWidth())
                SummaryCard(secondTitle, secondValue, Modifier.fillMaxWidth())
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(FinanceSpacing.sm)) {
                SummaryCard(firstTitle, firstValue, Modifier.weight(1f))
                SummaryCard(secondTitle, secondValue, Modifier.weight(1f))
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
    modifier: Modifier,
    tone: String = "normal"
) {

    Card(
        modifier = modifier.heightIn(min = 96.dp).semantics(mergeDescendants = true) {},
        colors = CardDefaults.cardColors(
            containerColor = when (tone) {
                "positive" -> MaterialTheme.colorScheme.secondaryContainer
                "warning" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.62f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = FinanceShapes.medium
    ) {

        Column(
            modifier =
                Modifier.padding(FinanceSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)
        ) {

            Text(
                title,
                style =
                    MaterialTheme.typography
                        .labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                color = when (tone) {
                    "positive" -> FinanceStatusColors.success
                    "warning" -> FinanceStatusColors.danger
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
internal fun PremiumSectionHeader(title: String, subtitle: String = "") {
    Column(
        modifier = Modifier.semantics { heading() },
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun PremiumEmptyState(title: String, message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = FinanceShapes.large
    ) {
        Column(
            modifier = Modifier.padding(FinanceSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
