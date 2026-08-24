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
// REPOSITORY
// ============================================================

class FinanceRepository(private val context: Context) : FinanceDataRepository {

    private val prefs = context.getSharedPreferences(
        PREFS,
        Context.MODE_PRIVATE
    )

    override fun load(): FinanceData {

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

    override fun save(data: FinanceData) {

        val encrypted = encryptLocalRecords(toJson(data).toString())
        check(decryptLocalRecords(encrypted).isNotBlank())
        prefs.edit()
            .putString(KEY_DATA_ENCRYPTED, encrypted)
            .remove(KEY_DATA)
            .commit()
    }

    override fun backup(password: String): String {
        return encryptBackup(toJson(load()).toString(), password)
    }

    override fun restore(content: String, password: String?, allowLegacy: Boolean): FinanceData {
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
                put("signature", data.receiptProfile.signature?.let { attachmentsJson(listOf(it)).optJSONObject(0) } ?: JSONObject.NULL)
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
        put("receivedAmount", item.receivedAmount)
        put("paymentChannel", item.paymentChannel)
        put("accountName", item.accountName)
        put("accountNumber", item.accountNumber)
        put("branch", item.branch)
        put("routingNumber", item.routingNumber)
        put("referenceNumber", item.referenceNumber)
        put("methodDetails", item.methodDetails)
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
            put("appliedRequestId", payment.appliedRequestId)
            put("accountNumber", payment.accountNumber)
            put("branch", payment.branch)
            put("routingNumber", payment.routingNumber)
            put("methodDetails", payment.methodDetails)
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
            put("financingChannel", item.financingChannel)
            put("financingAccountName", item.financingAccountName)
            put("financingAccountNumber", item.financingAccountNumber)
            put("financingBranch", item.financingBranch)
            put("financingRoutingNumber", item.financingRoutingNumber)
            put("financingReference", item.financingReference)
            put("financingMethodDetails", item.financingMethodDetails)
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
            put("financingChannel", item.financingChannel)
            put("financingAccountName", item.financingAccountName)
            put("financingAccountNumber", item.financingAccountNumber)
            put("financingBranch", item.financingBranch)
            put("financingRoutingNumber", item.financingRoutingNumber)
            put("financingReference", item.financingReference)
            put("financingMethodDetails", item.financingMethodDetails)
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
            put("debtDate", item.debtDate)

            put(
                "dueDate",
                item.dueDate ?: JSONObject.NULL
            )

            put("notes", item.notes)
            put("archived", item.archived)
            put("reason", item.reason)
            put("receivedOrGivenMethod", item.receivedOrGivenMethod)
            put("referenceNumber", item.referenceNumber)
            put("financingChannel", item.financingChannel)
            put("financingAccountName", item.financingAccountName)
            put("financingAccountNumber", item.financingAccountNumber)
            put("financingBranch", item.financingBranch)
            put("financingRoutingNumber", item.financingRoutingNumber)
            put("financingReference", item.financingReference)
            put("financingMethodDetails", item.financingMethodDetails)
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
                            status = item.optString("status", "UNPAID"),
                            receivedAmount = item.optDouble("receivedAmount", 0.0),
                            paymentChannel = item.optString("paymentChannel", ""),
                            accountName = item.optString("accountName", ""),
                            accountNumber = item.optString("accountNumber", ""),
                            branch = item.optString("branch", ""),
                            routingNumber = item.optString("routingNumber", ""),
                            referenceNumber = item.optString("referenceNumber", ""),
                            methodDetails = item.optString("methodDetails", "")
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
                            attachments = readAttachments(item.optJSONArray("attachments")),
                            appliedRequestId = item.optString("appliedRequestId", ""),
                            accountNumber = item.optString("accountNumber", ""),
                            branch = item.optString("branch", ""),
                            routingNumber = item.optString("routingNumber", ""),
                            methodDetails = item.optString("methodDetails", "")
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
                        financingChannel = item.optString("financingChannel", ""),
                        financingAccountName = item.optString("financingAccountName", ""),
                        financingAccountNumber = item.optString("financingAccountNumber", ""),
                        financingBranch = item.optString("financingBranch", ""),
                        financingRoutingNumber = item.optString("financingRoutingNumber", ""),
                        financingReference = item.optString("financingReference", ""),
                        financingMethodDetails = item.optString("financingMethodDetails", ""),
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
                        financingChannel = item.optString("financingChannel", ""),
                        financingAccountName = item.optString("financingAccountName", ""),
                        financingAccountNumber = item.optString("financingAccountNumber", ""),
                        financingBranch = item.optString("financingBranch", ""),
                        financingRoutingNumber = item.optString("financingRoutingNumber", ""),
                        financingReference = item.optString("financingReference", ""),
                        financingMethodDetails = item.optString("financingMethodDetails", ""),
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
                        debtDate = item.optLong("debtDate", item.optLong("dueDate", System.currentTimeMillis())),
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
                        financingChannel = item.optString("financingChannel", ""),
                        financingAccountName = item.optString("financingAccountName", ""),
                        financingAccountNumber = item.optString("financingAccountNumber", ""),
                        financingBranch = item.optString("financingBranch", ""),
                        financingRoutingNumber = item.optString("financingRoutingNumber", ""),
                        financingReference = item.optString("financingReference", ""),
                        financingMethodDetails = item.optString("financingMethodDetails", ""),
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
                    address = it.optString("address", ""),
                    signature = readAttachments(it.optJSONObject("signature")?.let { signature -> JSONArray().put(signature) }).firstOrNull()
                )
            } ?: ReceiptProfile()
        )
    }
}


