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


