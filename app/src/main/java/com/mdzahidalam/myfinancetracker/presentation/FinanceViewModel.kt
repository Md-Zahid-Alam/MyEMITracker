package com.mdzahidalam.myfinancetracker.presentation
import com.mdzahidalam.myfinancetracker.*
import com.mdzahidalam.myfinancetracker.data.repository.*
import com.mdzahidalam.myfinancetracker.data.security.*
import com.mdzahidalam.myfinancetracker.data.notifications.*


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
// VIEW MODEL
// ============================================================

class FinanceViewModel(
    private val context: Context,
    private val repository: FinanceDataRepository = FinanceRepository(context.applicationContext)
) : ViewModel() {

    var data by mutableStateOf(
        repository.load()
    )
        private set

    val dashboardUiState: DashboardUiState get() = FeatureUiStateFactory.dashboard(data)
    val paymentsUiState: PaymentsUiState get() = FeatureUiStateFactory.payments(data)
    val expensesUiState: ExpensesUiState get() = FeatureUiStateFactory.expenses(data)
    val reportsUiState: ReportsUiState get() = FeatureUiStateFactory.reports(data)
    val settingsUiState: SettingsUiState get() = FeatureUiStateFactory.settings(data)

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
        val currentOtherPaid = item.payments.filter { it.number != payment.number && it.paidDate != null }.sumOf { it.amount }
        if (payment.paidDate != null && currentOtherPaid + payment.amount > item.originalAmount + 0.005) return
        updateDebt(syncPaymentRequestStatuses(
            item.copy(
                payments = item.payments.map {
                    if (it.number == payment.number) payment else it
                }
            )
        ))
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
        attachments: List<Attachment> = emptyList(),
        requestId: String = "",
        accountNumber: String = "",
        branch: String = "",
        routingNumber: String = "",
        methodDetails: String = ""
    ) {

        if (!amount.isFinite() || amount <= 0) {
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
                attachments = attachments,
                appliedRequestId = requestId,
                accountNumber = accountNumber,
                branch = branch,
                routingNumber = routingNumber,
                methodDetails = methodDetails
            )

        updateDebt(syncPaymentRequestStatuses(
            item.copy(
                payments =
                    item.payments + payment
            )
        ))
    }

    fun addPaymentRequest(debtId: String, request: PaymentRequest) {
        val debt = data.debts.firstOrNull { it.id == debtId } ?: return
        if (debt.direction != "Owed to Me") return
        if (!request.amount.isFinite() || request.amount <= 0 || request.amount > availableRequestAmount(debt) + 0.005) return
        updateDebt(debt.copy(paymentRequests = debt.paymentRequests + request))
    }

    fun updatePaymentRequest(debtId: String, request: PaymentRequest) {
        val debt = data.debts.firstOrNull { it.id == debtId } ?: return
        val old = debt.paymentRequests.firstOrNull { it.id == request.id } ?: return
        if (old.status !in listOf("UNPAID", "PARTIALLY PAID")) return
        if (!request.amount.isFinite() || request.amount < old.receivedAmount || request.amount > availableRequestAmount(debt, request.id) + old.receivedAmount + 0.005) return
        updateDebt(syncPaymentRequestStatuses(debt.copy(paymentRequests = debt.paymentRequests.map { if (it.id == request.id) request else it })))
    }

    fun cancelPaymentRequest(debtId: String, requestId: String) {
        val debt = data.debts.firstOrNull { it.id == debtId } ?: return
        updateDebt(debt.copy(paymentRequests = debt.paymentRequests.map {
            if (it.id == requestId && it.status in listOf("UNPAID", "PARTIALLY PAID")) it.copy(status = "CANCELLED") else it
        }))
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

