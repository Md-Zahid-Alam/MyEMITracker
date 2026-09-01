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
import androidx.compose.ui.text.style.TextOverflow
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

internal const val PREFS = "finance_tracker_v3"
internal const val KEY_DATA = "data"
internal const val KEY_DATA_ENCRYPTED = "data_encrypted_v1"
internal const val KEY_PASSWORD_HASH = "password_hash"
internal const val KEY_PASSWORD_SALT = "password_salt"
internal const val CHANNEL_ID = "finance_reminders"
internal const val KEY_THEME_MODE = "theme_mode"
internal const val KEY_LANGUAGE = "app_language"
internal const val KEY_COUNTRY = "payment_country"
internal const val KEY_CURRENCY_CODE = "currency_code"
internal const val KEY_CURRENCY_SYMBOL = "currency_symbol"
internal const val KEY_CUSTOM_BANKS = "custom_banks"
internal const val KEY_CUSTOM_PROVIDERS = "custom_providers"
internal const val LOCAL_KEY_ALIAS = "my_finance_tracker_records_v1"
internal const val BACKUP_FORMAT = "MFT_ENCRYPTED_BACKUP"

internal val AppLightColorScheme = FinanceDesignSystem.LightColors
internal val AppDarkColorScheme = FinanceDesignSystem.DarkColors

internal val LocalFormReadOnly = staticCompositionLocalOf { false }
internal val LocalAppLanguage = staticCompositionLocalOf { "EN" }

internal object AppLocaleState {
    var language: String = "EN"
    var country: String = "Bangladesh"
    var currencyCode: String = "BDT"
    var currencySymbol: String = "৳"
    var customBanks: List<String> = emptyList()
    var customProviders: List<String> = emptyList()
}

internal const val MAX_FINANCIAL_AMOUNT = 999_999_999_999.99
private val FinancialAmountPattern = Regex("^\\d{1,12}(\\.\\d{1,2})?$")
private val FinancialAmountTypingPattern = Regex("^\\d{0,12}(\\.\\d{0,2})?$")

internal fun normalizeFinancialAmountInput(raw: String): String? {
    val cleaned = raw.replace(",", "").replace(" ", "").let { if (it == ".") "0." else it }
    return cleaned.takeIf { it.isEmpty() || FinancialAmountTypingPattern.matches(it) }
}

/** Displays stored Double amounts without scientific notation (for example E11). */
internal fun financialAmountText(value: Double): String =
    java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()

internal fun validFinancialAmount(value: String, allowZero: Boolean = false): Boolean {
    val cleaned = value.trim()
    if (!FinancialAmountPattern.matches(cleaned)) return false
    val number = cleaned.toDoubleOrNull() ?: return false
    return number.isFinite() && number <= MAX_FINANCIAL_AMOUNT && if (allowZero) number >= 0.0 else number > 0.0
}

internal val BanglaText = mapOf(
    "Home" to "হোম", "Payments" to "পেমেন্ট", "Expenses" to "খরচ", "Reports" to "রিপোর্ট", "Settings" to "সেটিংস",
    "English" to "English", "Bangla" to "বাংলা", "Language" to "ভাষা", "Cancel" to "বাতিল", "Close" to "বন্ধ করুন",
    "Save" to "সংরক্ষণ", "Edit" to "সম্পাদনা", "Delete" to "মুছুন", "Share" to "শেয়ার", "Search" to "খুঁজুন",
    "Active" to "চলমান", "Completed" to "সম্পন্ন", "Archived" to "আর্কাইভ", "Paid" to "পরিশোধিত", "Pending" to "বাকি",
    "Cancelled" to "বাতিল", "UNPAID" to "অপরিশোধিত", "PARTIALLY PAID" to "আংশিক পরিশোধিত", "PAID" to "পরিশোধিত", "CANCELLED" to "বাতিল",
    "Money I Owe" to "আমার দেনা", "Money Owed to Me" to "আমার পাওনা", "Debts" to "দেনা-পাওনা", "Loans" to "ঋণ", "EMI Plans" to "ইএমআই পরিকল্পনা",
    "Payment History" to "পেমেন্টের ইতিহাস", "Plan Information" to "পরিকল্পনার তথ্য", "Documents" to "ডকুমেন্ট", "Financing Information" to "অর্থায়নের তথ্য",
    "Record Payment" to "পেমেন্ট লিখুন", "Record Received Amount" to "প্রাপ্ত টাকা লিখুন", "Create Payment Request" to "পেমেন্ট অনুরোধ তৈরি করুন",
    "Save PDF" to "PDF সংরক্ষণ", "Summary PDF" to "সারাংশ PDF", "Detailed PDF" to "বিস্তারিত PDF", "Professional Excel (.xlsx)" to "প্রফেশনাল Excel (.xlsx)",
    "Report type" to "রিপোর্টের ধরন", "Report period" to "রিপোর্টের সময়কাল", "Status" to "অবস্থা", "Sort" to "সাজান", "Clear Filters" to "ফিল্টার মুছুন",
    "This month" to "এই মাস", "Last month" to "গত মাস", "Custom range" to "নিজস্ব সময়সীমা", "All time" to "সব সময়",
    "Overview" to "সারসংক্ষেপ", "All statuses" to "সব অবস্থা", "Newest first" to "নতুন আগে", "Oldest first" to "পুরোনো আগে", "Highest amount" to "বেশি টাকা আগে", "Lowest amount" to "কম টাকা আগে",
    "Payment method" to "পেমেন্ট পদ্ধতি", "Preferred payment method" to "পছন্দের পেমেন্ট পদ্ধতি", "Mobile banking" to "মোবাইল ব্যাংকিং", "Bank transfer" to "ব্যাংক ট্রান্সফার",
    "Cash" to "নগদ", "Cheque" to "চেক", "Card" to "কার্ড", "Other" to "অন্যান্য", "Bank name" to "ব্যাংকের নাম", "Select Bank" to "ব্যাংক নির্বাচন করুন",
    "Account holder" to "হিসাবধারীর নাম", "Account number" to "হিসাব নম্বর", "Branch (optional)" to "শাখা (ঐচ্ছিক)", "Routing number (optional)" to "রাউটিং নম্বর (ঐচ্ছিক)",
    "Transaction / reference ID" to "লেনদেন / রেফারেন্স আইডি", "Mobile banking provider" to "মোবাইল ব্যাংকিং সেবা", "Phone (optional)" to "ফোন (ঐচ্ছিক)", "Email (optional)" to "ইমেইল (ঐচ্ছিক)",
    "Address (optional)" to "ঠিকানা (ঐচ্ছিক)", "Full name" to "পূর্ণ নাম", "Receipt Profile" to "রসিদ প্রোফাইল", "Change Password" to "পাসওয়ার্ড পরিবর্তন",
    "Unlock" to "আনলক", "Password" to "পাসওয়ার্ড", "Theme" to "থিম", "Light" to "লাইট", "Dark" to "ডার্ক", "System default" to "সিস্টেম ডিফল্ট",
    "Add Expense" to "খরচ যোগ করুন", "Edit Expense" to "খরচ সম্পাদনা", "Expense name *" to "খরচের নাম *", "Amount *" to "পরিমাণ *", "Date *" to "তারিখ *", "Notes (optional)" to "নোট (ঐচ্ছিক)",
    "Daily" to "দৈনিক", "Monthly" to "মাসিক", "Category filter" to "ক্যাটাগরি ফিল্টার", "View category summary" to "ক্যাটাগরি সারাংশ দেখুন",
    "Back" to "ফিরুন", "Continue" to "চালিয়ে যান", "Confirm" to "নিশ্চিত করুন", "Update" to "আপডেট", "Restore" to "ফিরিয়ে আনুন", "Reopen" to "পুনরায় খুলুন", "Archive" to "আর্কাইভ",
    "Summary" to "সারাংশ", "EMI" to "ইএমআই", "Expenses" to "খরচসমূহ", "Metric" to "বিষয়", "Value" to "মান",
    "Date" to "তারিখ", "Amount" to "পরিমাণ", "Notes" to "নোট", "Category" to "ক্যাটাগরি", "Direction" to "ধরন",
    "Generated" to "তৈরির সময়", "Period" to "সময়কাল", "Report type" to "রিপোর্টের ধরন", "Status" to "অবস্থা",
    "PAYMENT RECEIPT" to "পেমেন্ট রসিদ", "PAYMENT REQUEST" to "পেমেন্ট অনুরোধ", "SUMMARY REPORT" to "সারাংশ রিপোর্ট", "COMPLETE REPORT" to "সম্পূর্ণ রিপোর্ট",
    "MY FINANCE TRACKER" to "মাই ফাইন্যান্স ট্র্যাকার", "Secure personal finance record" to "নিরাপদ ব্যক্তিগত আর্থিক রেকর্ড",
    "Authorized signature" to "অনুমোদিত স্বাক্ষর", "Generated by My Finance Tracker • Powered by Md. Zahid Alam" to "My Finance Tracker দ্বারা তৈরি • Powered by Md. Zahid Alam",
    "Language and region" to "ভাষা ও অঞ্চল", "Country" to "দেশ", "Bangladesh" to "বাংলাদেশ", "Other country" to "অন্যান্য দেশ",
    "Country name" to "দেশের নাম", "Currency code (for example USD)" to "মুদ্রা কোড (যেমন USD)", "Currency symbol" to "মুদ্রার প্রতীক",
    "Apply country and currency" to "দেশ ও মুদ্রা প্রয়োগ করুন", "My payment institutions" to "আমার পেমেন্ট প্রতিষ্ঠান",
    "Add bank" to "ব্যাংক যোগ করুন", "Add mobile banking provider" to "মোবাইল ব্যাংকিং সেবা যোগ করুন", "Add provider" to "সেবা যোগ করুন",
    "Security and local data tools" to "নিরাপত্তা ও স্থানীয় ডেটা টুল", "Appearance" to "চেহারা", "Lock App" to "অ্যাপ লক করুন",
    "Export Encrypted Backup" to "এনক্রিপ্টেড ব্যাকআপ রপ্তানি", "Restore Backup" to "ব্যাকআপ পুনরুদ্ধার", "About My Finance Tracker" to "My Finance Tracker সম্পর্কে",
    "Secure My Finance Tracker" to "My Finance Tracker সুরক্ষিত করুন", "Confirm password" to "পাসওয়ার্ড নিশ্চিত করুন", "Create Password" to "পাসওয়ার্ড তৈরি করুন",
    "Add EMI" to "ইএমআই যোগ করুন", "Edit EMI" to "ইএমআই সম্পাদনা", "EMI Details" to "ইএমআই বিস্তারিত", "Add Loan" to "ঋণ যোগ করুন", "Edit Loan" to "ঋণ সম্পাদনা", "Loan Details" to "ঋণের বিস্তারিত",
    "Add Debt" to "দেনা-পাওনা যোগ করুন", "Edit Debt" to "দেনা-পাওনা সম্পাদনা", "Debt Details" to "দেনা-পাওনার বিস্তারিত",
    "Item name" to "পণ্যের নাম", "Seller / Provider" to "বিক্রেতা / সেবাদাতা", "Purchase price" to "ক্রয়মূল্য", "Down payment" to "ডাউন পেমেন্ট",
    "Interest rate % (optional)" to "সুদের হার % (ঐচ্ছিক)", "Fixed interest amount (optional)" to "নির্দিষ্ট সুদের পরিমাণ (ঐচ্ছিক)", "Number of installments" to "কিস্তির সংখ্যা",
    "Loan name" to "ঋণের নাম", "Lender" to "ঋণদাতা", "Principal amount" to "মূল ঋণের পরিমাণ", "Person / Organization" to "ব্যক্তি / প্রতিষ্ঠান",
    "Original amount" to "মূল পরিমাণ", "Due date" to "পরিশোধের তারিখ", "Reason / purpose" to "কারণ / উদ্দেশ্য", "Reference (optional)" to "রেফারেন্স (ঐচ্ছিক)",
    "No bank found." to "কোনো ব্যাংক পাওয়া যায়নি।", "Search bank" to "ব্যাংক খুঁজুন", "Other bank" to "অন্যান্য ব্যাংক", "Other provider" to "অন্যান্য সেবা",
    "Mobile / account number" to "মোবাইল / হিসাব নম্বর", "Transaction ID" to "লেনদেন আইডি", "Account holder (optional)" to "হিসাবধারী (ঐচ্ছিক)",
    "Change country and currency?" to "দেশ ও মুদ্রা পরিবর্তন করবেন?", "Change" to "পরিবর্তন করুন"
)

internal val BanglaAdditionalText = mapOf(
    "Version 11.0" to "সংস্করণ ১১.০", "Version 10.0" to "সংস্করণ ১০.০", "Version 9.0" to "সংস্করণ ৯.০", "Version 8.3" to "সংস্করণ ৮.৩", "Save this bank for future use" to "ভবিষ্যতের জন্য এই ব্যাংক সংরক্ষণ করুন",
    "No EMI plans found" to "কোনো ইএমআই পরিকল্পনা পাওয়া যায়নি", "No loans found" to "কোনো ঋণ পাওয়া যায়নি", "No debt records found" to "কোনো দেনা-পাওনার রেকর্ড পাওয়া যায়নি", "Use + to add a record or change the selected status." to "+ ব্যবহার করে রেকর্ড যোগ করুন অথবা নির্বাচিত অবস্থা পরিবর্তন করুন।",
    "PDF saved successfully." to "পিডিএফ সফলভাবে সংরক্ষিত হয়েছে।", "PDF could not be saved." to "পিডিএফ সংরক্ষণ করা যায়নি।", "Excel report saved successfully." to "এক্সেল রিপোর্ট সফলভাবে সংরক্ষিত হয়েছে।", "Excel report could not be saved." to "এক্সেল রিপোর্ট সংরক্ষণ করা যায়নি।",
    "No matching records" to "মিলছে এমন কোনো রেকর্ড নেই", "Change or clear the report filters to see more results." to "আরও ফলাফল দেখতে রিপোর্ট ফিল্টার পরিবর্তন বা মুছে দিন।",
    "No category totals" to "ক্যাটাগরি সারাংশ নেই", "Add an expense in this period to create the category summary." to "ক্যাটাগরি সারাংশ তৈরি করতে এই সময়ে একটি খরচ যোগ করুন।",
    "No expenses for this period" to "এই সময়ের কোনো খরচ নেই", "Use + to add your first expense for this period." to "এই সময়ের প্রথম খরচ যোগ করতে + ব্যবহার করুন।", "No matching expenses" to "মিলছে এমন কোনো খরচ নেই", "Change or clear the search and category filter." to "অনুসন্ধান ও ক্যাটাগরি ফিল্টার পরিবর্তন বা মুছে দিন।",
    "No supporting documents" to "কোনো সহায়ক নথি নেই", "This plan does not have any attached image or PDF." to "এই পরিকল্পনায় কোনো ছবি বা পিডিএফ সংযুক্ত নেই।",
    "Version 8.2" to "সংস্করণ ৮.২",
    "Provider / bank" to "সেবাদাতা / ব্যাংক", "Account holder / party" to "হিসাবধারী / পক্ষ",
    "Account / mobile number" to "হিসাব / মোবাইল নম্বর", "Transaction reference" to "লেনদেন রেফারেন্স",
    "Method details" to "পদ্ধতির বিস্তারিত", "Received / Given Method" to "পাওয়া / দেওয়ার পদ্ধতি",
    // App identity, login, dashboard and navigation
    "My Finance Tracker" to "মাই ফাইন্যান্স ট্র্যাকার",
    "Your private offline finance tracker" to "আপনার ব্যক্তিগত অফলাইন অর্থ ব্যবস্থাপক",
    "Your financial data stays on this device." to "আপনার আর্থিক তথ্য এই ডিভাইসেই থাকে।",
    "Powered by Md. Zahid Alam" to "পরিচালনায়: মোঃ জাহিদ আলম",
    "Financial Overview" to "আর্থিক সারসংক্ষেপ",
    "Spent Today" to "আজকের খরচ", "Spent This Month" to "এই মাসের খরচ",
    "Monthly Payments" to "মাসিক পরিশোধ", "EMI Left" to "বাকি ইএমআই",
    "Loan Left" to "বাকি ঋণ", "Debt to Pay" to "পরিশোধযোগ্য দেনা",
    "Money to Receive" to "প্রাপ্য টাকা", "Next Payment" to "পরবর্তী পেমেন্ট",
    "Recent Expenses" to "সাম্প্রতিক খরচ",
    "No pending EMI or loan payments." to "কোনো ইএমআই বা ঋণের পেমেন্ট বাকি নেই।",
    "No expenses recorded yet." to "এখনও কোনো খরচ লেখা হয়নি।",
    "Choose the type of financial record you want to manage." to "আপনি যে ধরনের আর্থিক রেকর্ড পরিচালনা করতে চান তা বেছে নিন।",

    // General actions, states and messages
    "Open" to "খুলুন", "Remove" to "সরান", "Export" to "রপ্তানি", "Clear" to "মুছুন",
    "Clear date" to "তারিখ মুছুন", "Keep editing" to "সম্পাদনা চালিয়ে যান", "Discard" to "বাতিল করুন",
    "Discard your changes?" to "পরিবর্তনগুলো বাতিল করবেন?", "Your unsaved changes will be lost." to "সংরক্ষণ না করা পরিবর্তনগুলো হারিয়ে যাবে।",
    "Record not found." to "রেকর্ড পাওয়া যায়নি।", "No supporting documents are attached." to "কোনো সহায়ক ডকুমেন্ট সংযুক্ত নেই।",
    "There is no pending payment for this plan." to "এই পরিকল্পনায় কোনো পেমেন্ট বাকি নেই।",
    "This record is view-only. Reopen or restore it from the plan menu before making changes." to "এই রেকর্ডটি শুধু দেখা যাবে। পরিবর্তনের আগে পরিকল্পনা মেনু থেকে পুনরায় খুলুন বা পুনরুদ্ধার করুন।",
    "This debt is fully completed. No additional payment can be recorded." to "এই দেনা-পাওনা সম্পূর্ণ হয়েছে। আর কোনো পেমেন্ট লেখা যাবে না।",
    "Open My Finance Tracker to view details." to "বিস্তারিত দেখতে My Finance Tracker খুলুন।",

    // Payments landing and lists
    "EMI Plans" to "ইএমআই পরিকল্পনা", "Payment Requests" to "পেমেন্ট অনুরোধসমূহ",
    "Financing details" to "অর্থায়নের বিস্তারিত", "Calculated" to "হিসাবকৃত ফলাফল",
    "Calculated payments" to "হিসাবকৃত পেমেন্ট", "Financed" to "অর্থায়িত পরিমাণ",
    "Interest" to "সুদ", "Total payable" to "মোট পরিশোধযোগ্য", "Monthly payment" to "মাসিক পেমেন্ট",
    "Progress" to "অগ্রগতি", "Remaining" to "বাকি", "Paid / received" to "পরিশোধিত / প্রাপ্ত",
    "PDF Receipt" to "PDF রসিদ", "Save PDF Receipt" to "PDF রসিদ সংরক্ষণ", "Receipt attached" to "রসিদ সংযুক্ত",
    "Undo Paid" to "পরিশোধ বাতিল", "Undo Paid?" to "পরিশোধ বাতিল করবেন?",
    "Edit Plan Information" to "পরিকল্পনার তথ্য সম্পাদনা", "Update Debt Details" to "দেনা-পাওনার তথ্য আপডেট",
    "Apply payment to" to "পেমেন্ট প্রয়োগ করুন", "Payment notes" to "পেমেন্ট নোট",
    "Paid date" to "পরিশোধের তারিখ", "Payment date" to "পেমেন্টের তারিখ",
    "Payment instructions" to "পেমেন্ট নির্দেশনা", "Requested amount" to "অনুরোধকৃত পরিমাণ",
    "Message" to "বার্তা", "Available to request" to "অনুরোধের জন্য উপলভ্য",
    "Request money from" to "যার কাছে টাকা চাওয়া হবে", "Cancel Request" to "অনুরোধ বাতিল",
    "Cancel Payment Request?" to "পেমেন্ট অনুরোধ বাতিল করবেন?",

    // EMI and loan forms
    "Category" to "ক্যাটাগরি", "Electronics" to "ইলেকট্রনিক্স", "Food" to "খাবার",
    "Transport" to "যাতায়াত", "Shopping" to "কেনাকাটা", "Bills" to "বিল", "Health" to "স্বাস্থ্য",
    "Education" to "শিক্ষা", "Entertainment" to "বিনোদন",
    "Financing source" to "অর্থায়নের উৎস", "How item/finance was received" to "পণ্য/অর্থায়ন যেভাবে পাওয়া হয়েছে",
    "How the loan was received" to "ঋণ যেভাবে পাওয়া হয়েছে", "Financing notes" to "অর্থায়নের নোট",
    "Agreement / reference number" to "চুক্তি / রেফারেন্স নম্বর", "Agreement reference" to "চুক্তির রেফারেন্স",
    "Supporting documents (optional)" to "সহায়ক ডকুমেন্ট (ঐচ্ছিক)", "Attach image or PDF" to "ছবি বা PDF সংযুক্ত করুন",
    "Documents are included inside encrypted app data and encrypted backups." to "ডকুমেন্টগুলো এনক্রিপ্টেড অ্যাপ ডেটা ও এনক্রিপ্টেড ব্যাকআপের মধ্যে রাখা হয়।",
    "Bank" to "ব্যাংক", "Finance company" to "অর্থায়ন প্রতিষ্ঠান", "Employer" to "নিয়োগকর্তা",
    "Shop or seller" to "দোকান বা বিক্রেতা", "Credit card" to "ক্রেডিট কার্ড", "Friend or family" to "বন্ধু বা পরিবার",
    "Direct purchase financing" to "সরাসরি ক্রয় অর্থায়ন", "Salary arrangement" to "বেতনভিত্তিক ব্যবস্থা", "Goods or service" to "পণ্য বা সেবা",
    "Loan type" to "ঋণের ধরন", "Office Loan" to "অফিস ঋণ", "Personal Loan" to "ব্যক্তিগত ঋণ", "Bank Loan" to "ব্যাংক ঋণ",
    "Appliances" to "গৃহস্থালি যন্ত্রপাতি", "Furniture" to "আসবাবপত্র", "Vehicle" to "যানবাহন", "Mobile / Computer" to "মোবাইল / কম্পিউটার",
    "Medical" to "চিকিৎসা", "Home Improvement" to "বাড়ি উন্নয়ন", "Custom category" to "নিজস্ব ক্যাটাগরি",
    "Salary Loan" to "বেতন ঋণ", "Home Loan" to "গৃহঋণ", "Vehicle Loan" to "যানবাহন ঋণ", "Education Loan" to "শিক্ষা ঋণ", "Business Loan" to "ব্যবসায়িক ঋণ", "Custom loan type" to "নিজস্ব ঋণের ধরন",
    "Repayment method" to "পরিশোধ পদ্ধতি", "Equal Installments" to "সমান কিস্তি", "Flexible Monthly Payment" to "পরিবর্তনশীল মাসিক পেমেন্ট",
    "Installments" to "কিস্তির সংখ্যা", "Planned monthly payment" to "পরিকল্পিত মাসিক পেমেন্ট",
    "Interest rate %" to "সুদের হার %", "Fixed interest amount" to "নির্দিষ্ট সুদের পরিমাণ",
    "Due day 1-28" to "পরিশোধের দিন ১-২৮", "Monthly due day 1-28" to "মাসিক পরিশোধের দিন ১-২৮",
    "Previous installments already paid" to "আগে পরিশোধ করা কিস্তির সংখ্যা", "Previous repayments already made" to "আগে করা পরিশোধের সংখ্যা",
    "Reminder days before due date (e.g. 7,3,1,0)" to "পরিশোধের তারিখের আগে রিমাইন্ডারের দিন (যেমন ৭,৩,১,০)",
    "Start date" to "শুরুর তারিখ", "Due day" to "পরিশোধের দিন", "Reminder days" to "রিমাইন্ডারের দিন",
    "Repayment mode" to "পরিশোধের ধরন", "Principal" to "মূল ঋণ", "Source" to "উৎস",
    "Save EMI" to "ইএমআই সংরক্ষণ", "Save Loan" to "ঋণ সংরক্ষণ", "Save Debt" to "দেনা-পাওনা সংরক্ষণ",

    // Debt form
    "Person / organization" to "ব্যক্তি / প্রতিষ্ঠান", "I Owe" to "আমার দেনা", "Owed to Me" to "আমার পাওনা",
    "You need to pay this person or organization." to "আপনাকে এই ব্যক্তি বা প্রতিষ্ঠানকে টাকা পরিশোধ করতে হবে।",
    "This person or organization needs to pay you." to "এই ব্যক্তি বা প্রতিষ্ঠান আপনাকে টাকা পরিশোধ করবে।",
    "Reason for debt" to "দেনা-পাওনার কারণ", "Debt date" to "দেনা-পাওনার তারিখ", "Due date (optional)" to "পরিশোধের তারিখ (ঐচ্ছিক)",
    "How you received it" to "যেভাবে টাকা পেয়েছেন", "How you gave it" to "যেভাবে টাকা দিয়েছেন",
    "Previous payment amount (optional)" to "আগের পরিশোধের পরিমাণ (ঐচ্ছিক)",
    "How received / given" to "যেভাবে পাওয়া / দেওয়া হয়েছে", "Reason" to "কারণ",

    // Expense screens
    "All Months" to "সব মাস", "All" to "সব", "Category filter: All" to "ক্যাটাগরি ফিল্টার: সব",
    "Spent this month" to "এই মাসে খরচ", "No expenses for this period. Tap + to add one." to "এই সময়ে কোনো খরচ নেই। যোগ করতে + চাপুন।",
    "No expenses recorded for this period." to "এই সময়ের জন্য কোনো খরচ লেখা নেই।",
    "This expense will appear in both daily and monthly summaries." to "এই খরচ দৈনিক ও মাসিক—দুই সারাংশেই দেখাবে।",
    "Attach image or PDF (0/2)" to "ছবি বা PDF সংযুক্ত করুন (০/২)",

    // Reports
    "Filtered Summary" to "ফিল্টার করা সারাংশ", "No records match the selected filters." to "নির্বাচিত ফিল্টারের সঙ্গে কোনো রেকর্ড মেলেনি।",
    "Select period" to "সময়কাল নির্বাচন", "From" to "শুরু", "To" to "শেষ",
    "Showing one day" to "এক দিনের তথ্য দেখানো হচ্ছে", "Search records" to "রেকর্ড খুঁজুন",

    // Settings, profile and about
    "Language and appearance" to "ভাষা ও চেহারা", "Country, currency and payment institutions" to "দেশ, মুদ্রা ও পেমেন্ট প্রতিষ্ঠান",
    "Country and currency" to "দেশ ও মুদ্রা", "Changing currency changes the displayed symbol only; existing amounts are not converted." to "মুদ্রা পরিবর্তন করলে শুধু প্রদর্শিত প্রতীক বদলাবে; আগের টাকার পরিমাণ রূপান্তর হবে না।",
    "Bangladesh banks and mobile banking services are already included. Add any extra services below." to "বাংলাদেশের ব্যাংক ও মোবাইল ব্যাংকিং সেবাগুলো আগে থেকেই আছে। অতিরিক্ত সেবা নিচে যোগ করুন।",
    "Add only the banks and payment services you use. They stay on this device." to "আপনি যে ব্যাংক ও পেমেন্ট সেবা ব্যবহার করেন শুধু সেগুলো যোগ করুন। এগুলো এই ডিভাইসেই থাকবে।",
    "Encrypted backup protects all records and attached documents with a password you choose." to "আপনার নির্বাচিত পাসওয়ার্ড দিয়ে এনক্রিপ্টেড ব্যাকআপ সব রেকর্ড ও সংযুক্ত ডকুমেন্ট সুরক্ষিত রাখে।",
    "Choose a backup password. You will need it to restore this file on any phone." to "একটি ব্যাকআপ পাসওয়ার্ড দিন। যেকোনো ফোনে ফাইলটি পুনরুদ্ধার করতে এটি লাগবে।",
    "Backup password" to "ব্যাকআপ পাসওয়ার্ড", "Confirm backup password" to "ব্যাকআপ পাসওয়ার্ড নিশ্চিত করুন",
    "Current password" to "বর্তমান পাসওয়ার্ড", "New password" to "নতুন পাসওয়ার্ড", "Confirm new password" to "নতুন পাসওয়ার্ড নিশ্চিত করুন",
    "Your new password replaces the old one securely." to "নতুন পাসওয়ার্ডটি নিরাপদভাবে পুরোনো পাসওয়ার্ডের জায়গায় ব্যবহৃত হবে।",
    "This identity appears as the issuer on payment receipts and payment requests. App ownership remains separate." to "পেমেন্ট রসিদ ও অনুরোধে এই পরিচয় ইস্যুকারী হিসেবে দেখাবে। অ্যাপের মালিকানা আলাদা থাকবে।",
    "Signature (optional)" to "স্বাক্ষর (ঐচ্ছিক)", "Signature image (optional)" to "স্বাক্ষরের ছবি (ঐচ্ছিক)", "Attach signature image" to "স্বাক্ষরের ছবি সংযুক্ত করুন",
    "Save Receipt Profile" to "রসিদ প্রোফাইল সংরক্ষণ", "Created and owned by" to "তৈরি ও মালিকানায়",
    "A private offline application for tracking EMI plans, loans, debts, and daily expenses." to "ইএমআই, ঋণ, দেনা-পাওনা ও দৈনিক খরচ রাখার একটি ব্যক্তিগত অফলাইন অ্যাপ।",
    "All rights reserved." to "সর্বস্বত্ব সংরক্ষিত।", "About" to "অ্যাপ সম্পর্কে",
    "Licence and copyright" to "লাইসেন্স ও কপিরাইট",
    "Personal-use terms, ownership and permitted use" to "ব্যক্তিগত ব্যবহারের শর্ত, মালিকানা ও অনুমোদিত ব্যবহার",
    "© 2026 Md. Zahid Alam. All rights reserved." to "© ২০২৬ মোঃ জাহিদ আলম। সর্বস্বত্ব সংরক্ষিত।",
    "Licence grant" to "লাইসেন্সের অনুমতি",
    "This application is licensed, not sold. You may install and use it for your personal, non-commercial financial record keeping." to "এই অ্যাপটি বিক্রি করা হয়নি; ব্যবহারের লাইসেন্স দেওয়া হয়েছে। আপনি ব্যক্তিগত ও অ-বাণিজ্যিক আর্থিক হিসাব রাখার জন্য এটি ইনস্টল ও ব্যবহার করতে পারবেন।",
    "Restrictions" to "বিধিনিষেধ",
    "Without prior written permission from Md. Zahid Alam, you may not redistribute, resell, publish, sublicense, or create and distribute modified or derivative versions of this application, except where applicable law expressly permits otherwise." to "প্রযোজ্য আইন স্পষ্টভাবে অনুমতি না দিলে, মোঃ জাহিদ আলমের পূর্ব লিখিত অনুমতি ছাড়া এই অ্যাপ পুনর্বিতরণ, পুনর্বিক্রয়, প্রকাশ, সাবলাইসেন্স অথবা এর পরিবর্তিত বা উদ্ভূত সংস্করণ তৈরি ও বিতরণ করা যাবে না।",
    "Ownership" to "মালিকানা",
    "The application, source code, branding, visual assets, reports, documentation, and original content remain the intellectual property of Md. Zahid Alam. This licence does not transfer ownership." to "অ্যাপ, সোর্স কোড, ব্র্যান্ডিং, ভিজ্যুয়াল উপকরণ, রিপোর্ট, ডকুমেন্টেশন ও মৌলিক কনটেন্ট মোঃ জাহিদ আলমের মেধাস্বত্ব হিসেবে থাকবে। এই লাইসেন্স মালিকানা হস্তান্তর করে না।",
    "Privacy and local data" to "গোপনীয়তা ও স্থানীয় ডেটা",
    "My Finance Tracker is designed for offline use. Your records remain on your device unless you choose to export, share, or restore a backup or generated document." to "My Finance Tracker অফলাইনে ব্যবহারের জন্য তৈরি। আপনি ব্যাকআপ বা তৈরি করা ডকুমেন্ট রপ্তানি, শেয়ার বা পুনরুদ্ধার না করলে আপনার রেকর্ড ডিভাইসেই থাকে।",
    "No warranty" to "ওয়ারেন্টি নেই",
    "The application is provided as is, without warranties to the extent permitted by applicable law. You are responsible for checking records and maintaining secure backups." to "প্রযোজ্য আইন যতটুকু অনুমতি দেয়, অ্যাপটি কোনো ওয়ারেন্টি ছাড়াই বর্তমান অবস্থায় প্রদান করা হয়েছে। রেকর্ড যাচাই ও নিরাপদ ব্যাকআপ রাখার দায়িত্ব আপনার।",
    "Licence version: 1 September 2026" to "লাইসেন্স সংস্করণ: ১ সেপ্টেম্বর ২০২৬",

    // Payment method details
    "Provider name" to "সেবাদাতার নাম", "Other bank name" to "অন্যান্য ব্যাংকের নাম", "Account holder (optional)" to "হিসাবধারী (ঐচ্ছিক)",
    "Cheque number" to "চেক নম্বর", "Card issuer / bank" to "কার্ড ইস্যুকারী / ব্যাংক", "Last four digits" to "শেষ চার সংখ্যা",
    "Paid to / received from" to "যাকে দেওয়া / যার কাছ থেকে পাওয়া", "Method name" to "পদ্ধতির নাম", "Salary month" to "বেতনের মাস",
    "Transaction ID" to "লেনদেন আইডি", "Payment method" to "পেমেন্ট পদ্ধতি",

    // Dialog titles, confirmations and remaining information labels
    "Archive Debt?" to "দেনা-পাওনা আর্কাইভ করবেন?", "Archive EMI?" to "ইএমআই আর্কাইভ করবেন?", "Archive Loan?" to "ঋণ আর্কাইভ করবেন?",
    "Edit Debt?" to "দেনা-পাওনা সম্পাদনা করবেন?", "Edit EMI?" to "ইএমআই সম্পাদনা করবেন?", "Edit Loan?" to "ঋণ সম্পাদনা করবেন?",
    "Edit Expense?" to "খরচ সম্পাদনা করবেন?", "Edit Payment?" to "পেমেন্ট সম্পাদনা করবেন?",
    "Update Debt?" to "দেনা-পাওনা আপডেট করবেন?", "Update EMI?" to "ইএমআই আপডেট করবেন?", "Update Loan?" to "ঋণ আপডেট করবেন?",
    "Update Expense?" to "খরচ আপডেট করবেন?", "Update Payment?" to "পেমেন্ট আপডেট করবেন?",
    "Reopen Debt?" to "দেনা-পাওনা পুনরায় খুলবেন?", "Reopen EMI?" to "ইএমআই পুনরায় খুলবেন?", "Reopen Loan?" to "ঋণ পুনরায় খুলবেন?",
    "Restore Debt?" to "দেনা-পাওনা পুনরুদ্ধার করবেন?", "Restore EMI?" to "ইএমআই পুনরুদ্ধার করবেন?", "Restore Loan?" to "ঋণ পুনরুদ্ধার করবেন?",
    "Financed amount" to "অর্থায়িত পরিমাণ", "How received" to "যেভাবে পাওয়া হয়েছে", "Interest amount" to "সুদের পরিমাণ", "Interest rate" to "সুদের হার",
    "Search expenses" to "খরচ খুঁজুন", "Version 6.0" to "সংস্করণ ৬.০", "Version 6.1" to "সংস্করণ ৬.১", "Version 7.0" to "সংস্করণ ৭.০", "Version 8.0" to "সংস্করণ ৮.০", "Version 8.1" to "সংস্করণ ৮.১", "© 2026 Md. Zahid Alam" to "© ২০২৬ মোঃ জাহিদ আলম",
    "Md. Zahid Alam" to "মোঃ জাহিদ আলম",
    "Create an app password. The password itself is not stored; only a protected hash is stored on this phone." to "একটি অ্যাপ পাসওয়ার্ড তৈরি করুন। পাসওয়ার্ডটি সংরক্ষণ করা হয় না; এই ফোনে শুধু এর সুরক্ষিত হ্যাশ রাখা হয়।",
    "Attach a PNG or JPEG signature image. It is stored with your encrypted app data and added to generated receipts and payment requests." to "PNG বা JPEG স্বাক্ষরের ছবি সংযুক্ত করুন। এটি এনক্রিপ্টেড অ্যাপ ডেটায় রাখা হবে এবং তৈরি করা রসিদ ও পেমেন্ট অনুরোধে যোগ হবে।",
    "Existing money values will not be converted. Only the country, currency code, symbol, and available payment choices will change." to "আগের টাকার পরিমাণ রূপান্তর হবে না। শুধু দেশ, মুদ্রা কোড, প্রতীক ও উপলভ্য পেমেন্ট পদ্ধতি বদলাবে।",
    "Changes to installments, previous payments, amounts, or dates may rebuild this EMI payment schedule." to "কিস্তি, আগের পেমেন্ট, পরিমাণ বা তারিখ বদলালে ইএমআই পেমেন্ট সূচি নতুন করে তৈরি হতে পারে।",
    "Changes to repayments, previous payments, amounts, or dates may rebuild this loan repayment schedule." to "পরিশোধ, আগের পেমেন্ট, পরিমাণ বা তারিখ বদলালে ঋণ পরিশোধ সূচি নতুন করে তৈরি হতে পারে।",
    "The latest payment will return to Pending and this debt will move back to Active." to "সর্বশেষ পেমেন্ট আবার বাকি হবে এবং দেনা-পাওনাটি চলমান অবস্থায় ফিরবে।",
    "The latest payment will return to Pending, this EMI will move to Active, and reminders may resume." to "সর্বশেষ পেমেন্ট আবার বাকি হবে, ইএমআই চলমান হবে এবং রিমাইন্ডার পুনরায় চালু হতে পারে।",
    "The latest repayment will return to Pending, this loan will move to Active, and reminders may resume." to "সর্বশেষ পরিশোধ আবার বাকি হবে, ঋণ চলমান হবে এবং রিমাইন্ডার পুনরায় চালু হতে পারে।",
    "This debt will leave normal lists, but its complete payment history will remain available." to "দেনা-পাওনাটি সাধারণ তালিকা থেকে সরে যাবে, তবে সম্পূর্ণ পেমেন্ট ইতিহাস থাকবে।",
    "This debt will return to Active or Completed according to its payment status." to "পেমেন্টের অবস্থা অনুযায়ী দেনা-পাওনাটি চলমান বা সম্পন্ন তালিকায় ফিরবে।",
    "This EMI will leave normal lists and pending reminders will stop. Its history will remain available." to "ইএমআই সাধারণ তালিকা থেকে সরে যাবে এবং বাকি রিমাইন্ডার বন্ধ হবে। ইতিহাস সংরক্ষিত থাকবে।",
    "This EMI will return to Active or Completed according to its payment status. Reminders resume if payments are pending." to "পেমেন্টের অবস্থা অনুযায়ী ইএমআই চলমান বা সম্পন্ন তালিকায় ফিরবে। পেমেন্ট বাকি থাকলে রিমাইন্ডার চালু হবে।",
    "This loan will leave normal lists and pending reminders will stop. Its history will remain available." to "ঋণ সাধারণ তালিকা থেকে সরে যাবে এবং বাকি রিমাইন্ডার বন্ধ হবে। ইতিহাস সংরক্ষিত থাকবে।",
    "This loan will return to Active or Completed according to its repayment status. Reminders resume if payments are pending." to "পরিশোধের অবস্থা অনুযায়ী ঋণ চলমান বা সম্পন্ন তালিকায় ফিরবে। পেমেন্ট বাকি থাকলে রিমাইন্ডার চালু হবে।",
    "You are opening an active debt record where payments and notes can be changed." to "আপনি একটি চলমান দেনা-পাওনার রেকর্ড খুলছেন, যেখানে পেমেন্ট ও নোট পরিবর্তন করা যাবে।",
    "The new values will replace this EMI plan. Changes to amounts, installments, previous payments, or dates may rebuild its payment schedule." to "নতুন মানগুলো এই ইএমআই পরিকল্পনার আগের তথ্য প্রতিস্থাপন করবে। পরিমাণ, কিস্তি, আগের পেমেন্ট বা তারিখ বদলালে পেমেন্ট সূচি নতুন করে তৈরি হতে পারে।",
    "The new values will replace this loan plan. Changes to amounts, repayments, previous payments, or dates may rebuild its repayment schedule." to "নতুন মানগুলো এই ঋণ পরিকল্পনার আগের তথ্য প্রতিস্থাপন করবে। পরিমাণ, পরিশোধ, আগের পেমেন্ট বা তারিখ বদলালে পরিশোধ সূচি নতুন করে তৈরি হতে পারে।"
)

internal val BanglaValidationText = mapOf(
    "Enter a backup password." to "একটি ব্যাকআপ পাসওয়ার্ড দিন।", "Incorrect backup password or damaged backup file." to "ব্যাকআপ পাসওয়ার্ড ভুল অথবা ব্যাকআপ ফাইলটি ক্ষতিগ্রস্ত।",
    "An attached document is damaged." to "সংযুক্ত একটি ডকুমেন্ট ক্ষতিগ্রস্ত।", "Unable to read the selected backup." to "নির্বাচিত ব্যাকআপ পড়া যায়নি।",
    "Unable to read the backup." to "ব্যাকআপ পড়া যায়নি।", "Use at least 6 characters for the backup password." to "ব্যাকআপ পাসওয়ার্ডে কমপক্ষে ৬টি অক্ষর ব্যবহার করুন।",
    "Backup passwords do not match." to "ব্যাকআপ পাসওয়ার্ড দুটি মেলেনি।", "Backup could not be created." to "ব্যাকআপ তৈরি করা যায়নি।",
    "Enter a valid amount greater than zero." to "শূন্যের বেশি একটি সঠিক পরিমাণ দিন।", "Amount is too large." to "পরিমাণটি খুব বড়।",
    "Select a valid payment date." to "সঠিক পেমেন্টের তারিখ নির্বাচন করুন।", "Payment date cannot be in the future." to "পেমেন্টের তারিখ ভবিষ্যতের হতে পারবে না।",
    "Enter valid dates as DD-MM-YYYY." to "DD-MM-YYYY বিন্যাসে সঠিক তারিখ দিন।", "Paid date cannot be in the future." to "পরিশোধের তারিখ ভবিষ্যতের হতে পারবে না।",
    "Select how this payment was made." to "পেমেন্টটি কীভাবে করা হয়েছে তা নির্বাচন করুন।", "Notes must be 500 characters or less; reference and party names must be 100 or less." to "নোট সর্বোচ্চ ৫০০ অক্ষর এবং রেফারেন্স ও পক্ষের নাম সর্বোচ্চ ১০০ অক্ষর হতে হবে।",
    "Enter item name." to "পণ্যের নাম দিন।", "Item name must be 100 characters or less." to "পণ্যের নাম সর্বোচ্চ ১০০ অক্ষর হতে হবে।",
    "Enter a valid category; seller/provider must be 100 characters or less." to "সঠিক ক্যাটাগরি দিন; বিক্রেতা/সেবাদাতার নাম সর্বোচ্চ ১০০ অক্ষর হতে হবে।",
    "Enter a valid price." to "সঠিক মূল্য দিন।", "Interest rate must be 0-100 and interest amount cannot be negative." to "সুদের হার ০-১০০ হতে হবে এবং সুদের পরিমাণ ঋণাত্মক হতে পারবে না।",
    "Use either interest rate or fixed interest amount, not both." to "সুদের হার অথবা নির্দিষ্ট সুদের পরিমাণ—যেকোনো একটি ব্যবহার করুন, দুটো নয়।",
    "Installments must be between 1 and 600." to "কিস্তির সংখ্যা ১ থেকে ৬০০-এর মধ্যে হতে হবে।", "Previous paid must be 0 to total installments." to "আগে পরিশোধিত কিস্তি ০ থেকে মোট কিস্তির মধ্যে হতে হবে।",
    "Due day must be 1-28." to "পরিশোধের দিন ১-২৮-এর মধ্যে হতে হবে।", "Enter at least one reminder day." to "কমপক্ষে একটি রিমাইন্ডারের দিন দিন।",
    "Enter loan name." to "ঋণের নাম দিন।", "Loan name must be 100 characters or less." to "ঋণের নাম সর্বোচ্চ ১০০ অক্ষর হতে হবে।",
    "Enter a valid loan type; lender must be 100 characters or less." to "সঠিক ঋণের ধরন দিন; ঋণদাতার নাম সর্বোচ্চ ১০০ অক্ষর হতে হবে।", "Enter principal." to "মূল ঋণের পরিমাণ দিন।",
    "Enter a valid planned monthly payment." to "সঠিক পরিকল্পিত মাসিক পেমেন্ট দিন।", "Previous repayments must be 0 to total installments." to "আগের পরিশোধের সংখ্যা ০ থেকে মোট কিস্তির মধ্যে হতে হবে।",
    "Enter reminder days." to "রিমাইন্ডারের দিন দিন।", "Enter a valid name and an original amount not below the recorded total." to "সঠিক নাম এবং রেকর্ডকৃত মোটের চেয়ে কম নয় এমন মূল পরিমাণ দিন।",
    "Enter a valid payment amount." to "সঠিক পেমেন্টের পরিমাণ দিন।", "Enter a name and valid amount." to "একটি নাম ও সঠিক পরিমাণ দিন।",
    "Enter a valid requested amount." to "সঠিক অনুরোধকৃত পরিমাণ দিন।", "Request cannot exceed the available receivable balance." to "অনুরোধ প্রাপ্য অবশিষ্ট টাকার বেশি হতে পারবে না।",
    "Request amount cannot be below the amount already received." to "অনুরোধের পরিমাণ আগে প্রাপ্ত টাকার চেয়ে কম হতে পারবে না।", "Enter a valid due date." to "সঠিক পরিশোধের তারিখ দিন।",
    "Due date cannot be in the past." to "পরিশোধের তারিখ অতীতের হতে পারবে না।", "Enter payment instructions for the selected method." to "নির্বাচিত পদ্ধতির পেমেন্ট নির্দেশনা দিন।",
    "Instructions must be 300 characters or less and message 500 or less." to "নির্দেশনা সর্বোচ্চ ৩০০ এবং বার্তা সর্বোচ্চ ৫০০ অক্ষর হতে হবে।",
    "Enter an expense name." to "খরচের নাম দিন।", "Expense name must be 100 characters or less." to "খরচের নাম সর্বোচ্চ ১০০ অক্ষর হতে হবে।",
    "Enter a valid date as DD-MM-YYYY." to "DD-MM-YYYY বিন্যাসে সঠিক তারিখ দিন।", "Expense date cannot be in the future." to "খরচের তারিখ ভবিষ্যতের হতে পারবে না।",
    "Notes must be 500 characters or less." to "নোট সর্বোচ্চ ৫০০ অক্ষর হতে হবে।", "Each document must be 5 MB or smaller." to "প্রতিটি ডকুমেন্ট ৫ MB বা তার কম হতে হবে।",
    "Unable to read the selected document." to "নির্বাচিত ডকুমেন্ট পড়া যায়নি।", "Enter a valid country name." to "সঠিক দেশের নাম দিন।",
    "Currency code must contain three letters." to "মুদ্রা কোডে তিনটি অক্ষর থাকতে হবে।", "Enter a currency symbol." to "মুদ্রার প্রতীক দিন।",
    "Signature must be a PNG, JPEG, or WebP image." to "স্বাক্ষর PNG, JPEG অথবা WebP ছবি হতে হবে।", "Enter your full name." to "আপনার পূর্ণ নাম দিন।",
    "Full name must be between 2 and 100 characters." to "পূর্ণ নাম ২ থেকে ১০০ অক্ষরের মধ্যে হতে হবে।", "Enter a valid phone number containing 7-15 digits." to "৭-১৫ সংখ্যার একটি সঠিক ফোন নম্বর দিন।",
    "Enter a valid email address." to "সঠিক ইমেইল ঠিকানা দিন।", "Address must be 250 characters or less." to "ঠিকানা সর্বোচ্চ ২৫০ অক্ষর হতে হবে।",
    "Enter your current password." to "বর্তমান পাসওয়ার্ড দিন।", "Current password is incorrect." to "বর্তমান পাসওয়ার্ড ভুল।",
    "Use at least 4 characters." to "কমপক্ষে ৪টি অক্ষর ব্যবহার করুন।", "New passwords do not match." to "নতুন পাসওয়ার্ড দুটি মেলেনি।",
    "Select a mobile banking provider." to "একটি মোবাইল ব্যাংকিং সেবা নির্বাচন করুন।", "Enter the provider name." to "সেবাদাতার নাম দিন।",
    "Enter a valid mobile/account number." to "সঠিক মোবাইল/হিসাব নম্বর দিন।", "Select a bank." to "একটি ব্যাংক নির্বাচন করুন।",
    "Enter the bank name." to "ব্যাংকের নাম দিন।", "Enter the account holder name." to "হিসাবধারীর নাম দিন।", "Enter the account number." to "হিসাব নম্বর দিন।",
    "Select the bank and enter the cheque number." to "ব্যাংক নির্বাচন করে চেক নম্বর দিন।", "Enter the employer and salary month." to "নিয়োগকর্তা ও বেতনের মাস দিন।",
    "Enter the card's last four digits and transaction ID." to "কার্ডের শেষ চার সংখ্যা ও লেনদেন আইডি দিন।", "Enter the payment method name." to "পেমেন্ট পদ্ধতির নাম দিন।",
    "Enter your country name." to "আপনার দেশের নাম দিন।", "Enter a valid currency code and symbol." to "সঠিক মুদ্রা কোড ও প্রতীক দিন।",
    "Passwords do not match." to "পাসওয়ার্ড দুটি মেলেনি।", "Enter your password." to "আপনার পাসওয়ার্ড দিন।", "Incorrect password." to "পাসওয়ার্ড ভুল।"
)

internal val BanglaUiText2 = mapOf(
    "Add" to "যোগ করুন", "Attach" to "সংযুক্ত করুন", "Details" to "বিস্তারিত", "Document" to "ডকুমেন্ট", "No documents" to "কোনো ডকুমেন্ট নেই",
    "emi" to "ইএমআই", "loans" to "ঋণ", "debts" to "দেনা-পাওনা", "expenses" to "খরচ", "records" to "রেকর্ড", "PENDING" to "বাকি",
    "View Document" to "ডকুমেন্ট দেখুন", "View Documents" to "ডকুমেন্টগুলো দেখুন", "Open My Finance Tracker" to "My Finance Tracker খুলুন",
    "Plan" to "পরিকল্পনা", "Plan actions" to "পরিকল্পনার কাজ", "View all original plan details" to "পরিকল্পনার মূল তথ্য দেখুন",
    "Next month" to "পরের মাস", "Previous month" to "আগের মাস", "Next year" to "পরের বছর", "Previous year" to "আগের বছর",
    "Next due date" to "পরবর্তী পরিশোধের তারিখ", "Spent this day" to "এই দিনের খরচ", "Total spending" to "মোট খরচ",
    "Total to pay" to "মোট পরিশোধযোগ্য", "Total to receive" to "মোট প্রাপ্য", "Report filters" to "রিপোর্ট ফিল্টার",
    "No expenses match the current search or category filter." to "বর্তমান খোঁজ বা ক্যাটাগরি ফিল্টারের সঙ্গে কোনো খরচ মেলেনি।",
    "Hide category summary" to "ক্যাটাগরি সারাংশ লুকান", "Source, method, reference and notes" to "উৎস, পদ্ধতি, রেফারেন্স ও নোট",
    "Save Changes" to "পরিবর্তন সংরক্ষণ", "Save Expense" to "খরচ সংরক্ষণ", "Save Payment" to "পেমেন্ট সংরক্ষণ",
    "Save Received Amount" to "প্রাপ্ত টাকা সংরক্ষণ", "Update EMI" to "ইএমআই আপডেট", "Update Loan" to "ঋণ আপডেট", "Update Expense" to "খরচ আপডেট",
    "Edit Payment Request" to "পেমেন্ট অনুরোধ সম্পাদনা", "Receive Payment" to "পেমেন্ট গ্রহণ", "Share PDF" to "PDF শেয়ার",
    "New payment amount" to "নতুন পেমেন্টের পরিমাণ", "New received amount" to "নতুন প্রাপ্ত টাকার পরিমাণ", "Payment amount" to "পেমেন্টের পরিমাণ", "Received amount" to "প্রাপ্ত পরিমাণ",
    "Employer / salary month" to "নিয়োগকর্তা / বেতনের মাস", "Salary deduction" to "বেতন থেকে কর্তন", "Direct financing" to "সরাসরি অর্থায়ন",
    "Not recorded" to "লেখা নেই", "Not specified" to "উল্লেখ নেই", "No payment request" to "কোনো পেমেন্ট অনুরোধ নেই",
    "Select specific date" to "নির্দিষ্ট তারিখ নির্বাচন", "Show password" to "পাসওয়ার্ড দেখান", "Hide password" to "পাসওয়ার্ড লুকান",
    "Restore Encrypted Backup" to "এনক্রিপ্টেড ব্যাকআপ পুনরুদ্ধার", "Restore Legacy Backup?" to "পুরোনো ব্যাকআপ পুনরুদ্ধার করবেন?",
    "Import Legacy Backup" to "পুরোনো ব্যাকআপ আমদানি", "Restore failed." to "পুনরুদ্ধার ব্যর্থ হয়েছে।", "Backup file is too large." to "ব্যাকআপ ফাইলটি খুব বড়।",
    "This older JSON backup is readable and not encrypted. Import it only if you trust its source. Current records will be replaced." to "এই পুরোনো JSON ব্যাকআপটি পড়া যায় এবং এনক্রিপ্টেড নয়। উৎসটি বিশ্বাসযোগ্য হলে তবেই আমদানি করুন। বর্তমান রেকর্ড প্রতিস্থাপিত হবে।",
    "This debt is archived and view-only. Restore it to make changes." to "দেনা-পাওনাটি আর্কাইভ করা এবং শুধু দেখা যাবে। পরিবর্তনের জন্য পুনরুদ্ধার করুন।",
    "This debt is completed and view-only. Reopen it to make changes." to "দেনা-পাওনাটি সম্পন্ন এবং শুধু দেখা যাবে। পরিবর্তনের জন্য পুনরায় খুলুন।",
    "This debt is fully paid. Reopen it before adding another payment." to "দেনা-পাওনা সম্পূর্ণ পরিশোধিত। নতুন পেমেন্ট যোগ করার আগে পুনরায় খুলুন।",
    "This EMI is archived and view-only. Restore it to make changes." to "ইএমআই আর্কাইভ করা এবং শুধু দেখা যাবে। পরিবর্তনের জন্য পুনরুদ্ধার করুন।",
    "This EMI is completed and view-only. Reopen it to make changes." to "ইএমআই সম্পন্ন এবং শুধু দেখা যাবে। পরিবর্তনের জন্য পুনরায় খুলুন।",
    "This loan is archived and view-only. Restore it to make changes." to "ঋণ আর্কাইভ করা এবং শুধু দেখা যাবে। পরিবর্তনের জন্য পুনরুদ্ধার করুন।",
    "This loan is completed and view-only. Reopen it to make changes." to "ঋণ সম্পন্ন এবং শুধু দেখা যাবে। পরিবর্তনের জন্য পুনরায় খুলুন।",
    "Account" to "হিসাব", "Branch" to "শাখা", "Routing" to "রাউটিং", "Reference" to "রেফারেন্স", "Party" to "পক্ষ",
    "Record" to "রেকর্ড", "Record Type" to "রেকর্ডের ধরন", "Payment No." to "পেমেন্ট নং", "Method" to "পদ্ধতি", "Provider / Bank" to "সেবাদাতা / ব্যাংক",
    "Item" to "পণ্য", "Seller" to "বিক্রেতা", "Price" to "মূল্য", "Original" to "মূল পরিমাণ", "Type" to "ধরন",
    "Expense" to "খরচ", "Expense total" to "মোট খরচ", "Entries" to "এন্ট্রি", "Plans" to "পরিকল্পনা",
    "EMI SUMMARY" to "ইএমআই সারাংশ", "LOAN SUMMARY" to "ঋণ সারাংশ", "DEBT SUMMARY" to "দেনা-পাওনা সারাংশ", "EXPENSE SUMMARY" to "খরচের সারাংশ",
    "EMI REPORT" to "ইএমআই রিপোর্ট", "LOAN REPORT" to "ঋণ রিপোর্ট", "DEBT REPORT" to "দেনা-পাওনা রিপোর্ট", "EXPENSE REPORT" to "খরচের রিপোর্ট",
    "RECORD SUMMARY" to "রেকর্ড সারাংশ", "REPAYMENT HISTORY" to "পরিশোধের ইতিহাস", "CURRENT MONTH BY CATEGORY" to "এই মাসের ক্যাটাগরিভিত্তিক খরচ",
    "↑ YOU NEED TO PAY" to "↑ আপনাকে পরিশোধ করতে হবে", "↓ YOU NEED TO RECEIVE" to "↓ আপনি টাকা পাবেন",
    "Generated by My Finance Tracker" to "My Finance Tracker দ্বারা তৈরি", "Personal payment record generated by My Finance Tracker. Recipient confirmation or signature may be required as proof of payment." to "My Finance Tracker দ্বারা তৈরি ব্যক্তিগত পেমেন্ট রেকর্ড। পেমেন্টের প্রমাণ হিসেবে প্রাপকের নিশ্চিতকরণ বা স্বাক্ষর প্রয়োজন হতে পারে।",
    "This is a personal payment request generated from the issuer's records. It is not a bank statement, legal judgment, or tax invoice." to "এটি ইস্যুকারীর রেকর্ড থেকে তৈরি ব্যক্তিগত পেমেন্ট অনুরোধ। এটি ব্যাংক স্টেটমেন্ট, আইনি রায় বা কর চালান নয়।",
    "Generated" to "তৈরির সময়", "EMI plans" to "ইএমআই পরিকল্পনা", "Money I owe" to "আমার দেনা", "Money owed to me" to "আমার পাওনা",
    "Debt to pay" to "পরিশোধযোগ্য দেনা", "Money to receive" to "প্রাপ্য টাকা", "Expense total" to "মোট খরচ",
    "Request number" to "অনুরোধ নম্বর", "Request date" to "অনুরোধের তারিখ", "Request status" to "অনুরোধের অবস্থা",
    "Requested by" to "অনুরোধকারী", "Payment requested from" to "যার কাছে পেমেন্ট চাওয়া হয়েছে", "Amount already received" to "আগে প্রাপ্ত পরিমাণ",
    "Amount requested" to "অনুরোধকৃত পরিমাণ", "Amount received for request" to "অনুরোধের বিপরীতে প্রাপ্ত পরিমাণ", "Preferred method" to "পছন্দের পদ্ধতি",
    "Provider / bank" to "সেবাদাতা / ব্যাংক", "Payment instructions" to "পেমেন্ট নির্দেশনা", "Account / mobile number" to "হিসাব / মোবাইল নম্বর",
    "Receipt number" to "রসিদ নম্বর", "Payment date" to "পেমেন্টের তারিখ", "Payer" to "পরিশোধকারী", "Recipient" to "প্রাপক",
    "Previous balance" to "আগের বাকি", "Remaining balance" to "বর্তমান বাকি", "Amount in words" to "কথায় পরিমাণ",
    "Payer signature" to "পরিশোধকারীর স্বাক্ষর", "Recipient signature" to "প্রাপকের স্বাক্ষর", "Record" to "রেকর্ড",
    "Account / last four digits" to "হিসাব / শেষ চার সংখ্যা", "Transaction/reference ID" to "লেনদেন/রেফারেন্স আইডি", "Method details" to "পদ্ধতির বিস্তারিত"
)

// Strings found during the Build 32 bilingual visual-QA pass. Keep these as
// complete phrases so Bangla screens never fall back to mixed word-by-word UI.
internal val BanglaVisualQaText = mapOf(
    "A clear view of your money today." to "আজকের আর্থিক অবস্থার পরিষ্কার চিত্র।",
    "SPENT THIS MONTH" to "এই মাসের খরচ",
    "Today" to "আজ",
    "Commitments" to "চলমান দায়",
    "Amounts still active" to "এখনও চলমান পরিমাণ",
    "Your latest five entries" to "আপনার সর্বশেষ পাঁচটি এন্ট্রি",
    "Tap Expenses to add your first record." to "প্রথম রেকর্ড যোগ করতে খরচসমূহে ট্যাপ করুন।",
    "Manage instalments, loans and personal balances." to "কিস্তি, ঋণ এবং ব্যক্তিগত দেনা-পাওনা পরিচালনা করুন।",
    "View only" to "শুধু দেখুন",
    "Total" to "মোট",
    "Paid" to "পরিশোধিত",
    "Remaining" to "বাকি",
    "Due" to "পরিশোধের তারিখ",
    "Next payment" to "পরবর্তী পেমেন্ট",
    "Edit Plan Information" to "পরিকল্পনার তথ্য সম্পাদনা",
    "Payment Requests" to "পেমেন্ট অনুরোধসমূহ",
    "This record is view-only. Reopen or restore it from the plan menu before making changes." to "এই রেকর্ডটি শুধু দেখা যাবে। পরিবর্তনের আগে পরিকল্পনার মেনু থেকে পুনরায় খুলুন বা পুনরুদ্ধার করুন।",
    "Financing details" to "অর্থায়নের বিস্তারিত",
    "Supporting documents (optional)" to "সহায়ক ডকুমেন্ট (ঐচ্ছিক)",
    "Documents are included inside encrypted app data and encrypted backups." to "ডকুমেন্ট এনক্রিপ্টেড অ্যাপ ডেটা ও এনক্রিপ্টেড ব্যাকআপে রাখা হয়।",
    "Agreement / reference number" to "চুক্তি / রেফারেন্স নম্বর",
    "Financing notes" to "অর্থায়নের নোট",
    "Interest rate %" to "সুদের হার %",
    "Fixed interest amount" to "নির্দিষ্ট সুদের পরিমাণ",
    "Installments" to "কিস্তির সংখ্যা",
    "Monthly due day 1-28" to "মাসিক পরিশোধের দিন ১-২৮",
    "Due day 1-28" to "পরিশোধের দিন ১-২৮",
    "Previous installments already paid" to "আগে পরিশোধিত কিস্তির সংখ্যা",
    "Previous repayments already made" to "আগে পরিশোধিত কিস্তির সংখ্যা",
    "Reminder days before due date (e.g. 7,3,1,0)" to "পরিশোধের তারিখের আগে রিমাইন্ডার দিন (যেমন ৭,৩,১,০)",
    "Calculated" to "হিসাবকৃত",
    "Financed" to "অর্থায়িত",
    "Interest" to "সুদ",
    "Total payable" to "মোট পরিশোধযোগ্য",
    "Monthly" to "মাসিক",
    "Repayment method" to "পরিশোধ পদ্ধতি",
    "Equal Installments" to "সমান কিস্তি",
    "Flexible Amounts" to "পরিবর্তনশীল পরিমাণ",
    "Loan type" to "ঋণের ধরন",
    "Financing source" to "অর্থায়নের উৎস",
    "How the loan was received" to "ঋণ যেভাবে পাওয়া হয়েছে",
    "How item/finance was received" to "পণ্য/অর্থায়ন যেভাবে পাওয়া হয়েছে",
    "How you received it" to "যেভাবে পেয়েছেন",
    "How you gave it" to "যেভাবে দিয়েছেন",
    "Direction" to "দিক",
    "I Owe" to "আমার দেনা",
    "Owed to Me" to "আমার পাওনা",
    "You need to pay this person or organization." to "আপনাকে এই ব্যক্তি বা প্রতিষ্ঠানকে টাকা পরিশোধ করতে হবে।",
    "This person or organization needs to pay you." to "এই ব্যক্তি বা প্রতিষ্ঠান আপনাকে টাকা পরিশোধ করবে।",
    "Debt date" to "দেনা-পাওনার তারিখ",
    "Due date (optional)" to "পরিশোধের তারিখ (ঐচ্ছিক)",
    "Reason for debt" to "দেনা-পাওনার কারণ",
    "Paid to / received from" to "যাকে দিয়েছেন / যার কাছ থেকে পেয়েছেন",
    "Previous payment amount (optional)" to "আগের পেমেন্টের পরিমাণ (ঐচ্ছিক)",
    "This expense will appear in both daily and monthly summaries." to "এই খরচ দৈনিক ও মাসিক—দুই সারাংশেই দেখা যাবে।",
    "Select period" to "সময়কাল নির্বাচন করুন",
    "All Months" to "সব মাস",
    "Jan" to "জানু", "Feb" to "ফেব্রু", "Mar" to "মার্চ", "Apr" to "এপ্রিল",
    "May" to "মে", "Jun" to "জুন", "Jul" to "জুলাই", "Aug" to "আগস্ট",
    "Sep" to "সেপ্টে", "Oct" to "অক্টো", "Nov" to "নভে", "Dec" to "ডিসে",
    "Spent this month" to "এই মাসের খরচ",
    "Category: Food" to "ক্যাটাগরি: খাবার",
    "Search expenses" to "খরচ খুঁজুন",
    "Showing one day" to "এক দিনের খরচ দেখানো হচ্ছে",
    "Clear date" to "তারিখ মুছুন",
    "Filtered Summary" to "ফিল্টার করা সারাংশ",
    "Search records" to "রেকর্ড খুঁজুন",
    "Personalize your experience and protect your local records." to "আপনার অভিজ্ঞতা সাজান এবং স্থানীয় রেকর্ড সুরক্ষিত রাখুন।",
    "Language and appearance" to "ভাষা ও চেহারা",
    "Security and local data" to "নিরাপত্তা ও স্থানীয় ডেটা",
    "Documents and app" to "ডকুমেন্ট ও অ্যাপ",
    "Country and currency" to "দেশ ও মুদ্রা",
    "Update the password used to unlock this app" to "অ্যাপ আনলক করার পাসওয়ার্ড পরিবর্তন করুন",
    "Create a password-protected copy of records and documents" to "রেকর্ড ও ডকুমেন্টের পাসওয়ার্ড-সুরক্ষিত কপি তৈরি করুন",
    "Restore a trusted My Finance Tracker backup" to "বিশ্বস্ত My Finance Tracker ব্যাকআপ পুনরুদ্ধার করুন",
    "Your identity and signature on generated documents" to "তৈরি করা ডকুমেন্টে আপনার পরিচয় ও স্বাক্ষর",
    "Version, ownership and privacy information" to "সংস্করণ, মালিকানা ও গোপনীয়তার তথ্য",
    "Encrypted backup protects all records and attached documents with a password you choose." to "আপনার নির্বাচিত পাসওয়ার্ড দিয়ে এনক্রিপ্টেড ব্যাকআপ সব রেকর্ড ও সংযুক্ত ডকুমেন্ট সুরক্ষিত রাখে।",
    "Lock App" to "অ্যাপ লক করুন",
    "Open" to "খুলুন", "Remove" to "সরান", "Undo Paid" to "পরিশোধ বাতিল করুন",
    "PDF Receipt" to "PDF রসিদ", "Save PDF Receipt" to "PDF রসিদ সংরক্ষণ",
    "Receipt attached" to "রসিদ সংযুক্ত আছে", "Note" to "নোট",
    "Save Receipt Profile" to "রসিদ প্রোফাইল সংরক্ষণ",
    "Signature (optional)" to "স্বাক্ষর (ঐচ্ছিক)",
    "Discard your changes?" to "পরিবর্তন বাতিল করবেন?",
    "Your unsaved changes will be lost." to "সংরক্ষণ না করা পরিবর্তন হারিয়ে যাবে।",
    "Discard" to "বাতিল করুন", "Keep editing" to "সম্পাদনা চালিয়ে যান",
    "Request money from" to "টাকা চাওয়ার ব্যক্তি",
    "Available to request" to "অনুরোধ করা যাবে",
    "Save PDF" to "PDF সংরক্ষণ", "Create" to "তৈরি করুন",
    "Requested" to "অনুরোধকৃত", "Received" to "প্রাপ্ত",
    "UNPAID" to "অপরিশোধিত",
    "Create an app password. " to "একটি অ্যাপ পাসওয়ার্ড তৈরি করুন। ",
    "Default currency is filled automatically. You may change it for a special account or territory." to "ডিফল্ট মুদ্রা স্বয়ংক্রিয়ভাবে পূরণ হয়। বিশেষ হিসাব বা অঞ্চলের জন্য প্রয়োজনে এটি পরিবর্তন করতে পারেন।",
    "Remove signature?" to "স্বাক্ষর সরাবেন?",
    "This signature will no longer appear on newly generated receipts or payment requests after you save the profile." to "প্রোফাইল সংরক্ষণ করার পর নতুন রসিদ বা পেমেন্ট অনুরোধে এই স্বাক্ষর আর দেখা যাবে না।",
    "Keep signature" to "স্বাক্ষর রাখুন",
    "Enter an amount using up to 12 digits and 2 decimal places." to "সর্বোচ্চ ১২টি পূর্ণসংখ্যার অঙ্ক ও ২টি দশমিক অঙ্ক ব্যবহার করে পরিমাণ লিখুন।"
)

internal fun localized(value: String, language: String = AppLocaleState.language): String {
    if (language != "BN") return value
    BanglaText[value]?.let { return it }
    BanglaAdditionalText[value]?.let { return it }
    BanglaValidationText[value]?.let { return it }
    BanglaUiText2[value]?.let { return it }
    BanglaVisualQaText[value]?.let { return it }

    val months = mapOf(
        "January" to "জানুয়ারি", "February" to "ফেব্রুয়ারি", "March" to "মার্চ", "April" to "এপ্রিল",
        "May" to "মে", "June" to "জুন", "July" to "জুলাই", "August" to "আগস্ট",
        "September" to "সেপ্টেম্বর", "October" to "অক্টোবর", "November" to "নভেম্বর", "December" to "ডিসেম্বর"
    )
    Regex("^(January|February|March|April|May|June|July|August|September|October|November|December) (\\d{4})$")
        .matchEntire(value)?.let { return "${months[it.groupValues[1]]} ${it.groupValues[2]}" }

    // Translate structured labels and summaries as complete parts. This avoids the
    // former mixed Bangla/English result caused by replacing individual words.
    if (value.contains(" • ")) return value.split(" • ").joinToString(" • ") { localized(it, language) }
    if (value.contains(": ")) {
        val label = value.substringBefore(": ")
        val content = value.substringAfter(": ")
        return "${localized(label, language)}: ${localized(content, language)}"
    }

    Regex("^(\\d+) active$").matchEntire(value)?.let { return "${it.groupValues[1]}টি চলমান" }
    Regex("^(\\d+) matching records$").matchEntire(value)?.let { return "মিলেছে ${it.groupValues[1]}টি রেকর্ড" }
    Regex("^(\\d+) matching records • (.+)$").matchEntire(value)?.let { return "মিলেছে ${it.groupValues[1]}টি রেকর্ড • ${localized(it.groupValues[2], language)}" }
    Regex("^(EMI|Loans|Debts|Expenses) (\\d+)$").matchEntire(value)?.let { return "${localized(it.groupValues[1], language)} ${it.groupValues[2]}টি" }
    Regex("^(\\d+) of (\\d+) paid$").matchEntire(value)?.let { return "${it.groupValues[2]}টির মধ্যে ${it.groupValues[1]}টি পরিশোধিত" }
    Regex("^No (Active|Completed|Archived) EMI plans\\.$").matchEntire(value)?.let {
        return when (it.groupValues[1]) { "Active" -> "কোনো চলমান ইএমআই পরিকল্পনা নেই।"; "Completed" -> "কোনো সম্পন্ন ইএমআই পরিকল্পনা নেই।"; else -> "কোনো আর্কাইভ করা ইএমআই পরিকল্পনা নেই।" }
    }
    Regex("^No (Active|Completed|Archived) loans\\.$").matchEntire(value)?.let {
        return when (it.groupValues[1]) { "Active" -> "কোনো চলমান ঋণ নেই।"; "Completed" -> "কোনো সম্পন্ন ঋণ নেই।"; else -> "কোনো আর্কাইভ করা ঋণ নেই।" }
    }
    Regex("^No (Active|Completed|Archived) records\\.$").matchEntire(value)?.let { return "এই অবস্থায় কোনো রেকর্ড নেই।" }
    Regex("^Search (.+)$").matchEntire(value)?.let { return "${localized(it.groupValues[1], language)} খুঁজুন" }
    Regex("^Today\\s+(.+)$").matchEntire(value)?.let { return "আজ ${it.groupValues[1]}" }
    Regex("^(\\d+) active • (.+) left$").matchEntire(value)?.let { return "${it.groupValues[1]}টি চলমান • বাকি ${it.groupValues[2]}" }
    Regex("^Pay (.+) • Receive (.+)$").matchEntire(value)?.let { return "পরিশোধযোগ্য ${it.groupValues[1]} • প্রাপ্য ${it.groupValues[2]}" }
    Regex("^Money I Owe • (\\d+) active • (.+)$").matchEntire(value)?.let { return "আমার দেনা • ${it.groupValues[1]}টি চলমান • ${it.groupValues[2]}" }
    Regex("^Money Owed to Me • (\\d+) active • (.+)$").matchEntire(value)?.let { return "আমার পাওনা • ${it.groupValues[1]}টি চলমান • ${it.groupValues[2]}" }
    Regex("^(Total|Paid|Remaining|Original|Financed|Interest|Monthly): (.+)$").matchEntire(value)?.let { return "${localized(it.groupValues[1], language)}: ${it.groupValues[2]}" }
    Regex("^Next payment: (.+)$").matchEntire(value)?.let { return "পরবর্তী পেমেন্ট: ${it.groupValues[1]}" }
    Regex("^You owe (.+)$").matchEntire(value)?.let { return "আপনি ${it.groupValues[1]}-এর কাছে দেনাদার" }
    Regex("^(.+) owes you$").matchEntire(value)?.let { return "${it.groupValues[1]} আপনার কাছে দেনাদার" }
    Regex("^Available to request: (.+)$").matchEntire(value)?.let { return "অনুরোধ করা যাবে: ${it.groupValues[1]}" }
    Regex("^Request money from (.+)$").matchEntire(value)?.let { return "${it.groupValues[1]}-এর কাছে টাকা চান" }
    Regex("^Calculated payments: (\\d+)$").matchEntire(value)?.let { return "হিসাবকৃত পেমেন্ট: ${it.groupValues[1]}টি" }
    Regex("^Final payment: (.+)$").matchEntire(value)?.let { return "শেষ পেমেন্ট: ${it.groupValues[1]}" }
    Regex("^Open (.+)$").matchEntire(value)?.let { return "${it.groupValues[1]} খুলুন" }
    Regex("^(.+) / month$").matchEntire(value)?.let { return "${it.groupValues[1]} / মাস" }
    Regex("^(Pay|Receive) (.+)$").matchEntire(value)?.let { return "${if (it.groupValues[1] == "Pay") "পরিশোধযোগ্য" else "প্রাপ্য"} ${it.groupValues[2]}" }
    Regex("^(.+) left$").matchEntire(value)?.let { return "বাকি ${it.groupValues[1]}" }
    Regex("^(.+) remaining$").matchEntire(value)?.let { return "বাকি ${it.groupValues[1]}" }
    Regex("^Due (.+)$").matchEntire(value)?.let { return "পরিশোধের তারিখ ${it.groupValues[1]}" }
    Regex("^Paid (.+)$").matchEntire(value)?.let { return "পরিশোধিত ${it.groupValues[1]}" }
    Regex("^Received (.+)$").matchEntire(value)?.let { return "প্রাপ্ত ${it.groupValues[1]}" }
    Regex("^Requested (.+)$").matchEntire(value)?.let { return "অনুরোধকৃত ${it.groupValues[1]}" }
    Regex("^Completed (.+)$").matchEntire(value)?.let { return "সম্পন্ন ${it.groupValues[1]}" }
    Regex("^Payment (\\d+)$").matchEntire(value)?.let { return "পেমেন্ট ${it.groupValues[1]}" }
    Regex("^Edit Payment (\\d+)$").matchEntire(value)?.let { return "পেমেন্ট ${it.groupValues[1]} সম্পাদনা" }
    Regex("^(.+) document\\(s\\) attached$").matchEntire(value)?.let { return "${it.groupValues[1]}টি ডকুমেন্ট সংযুক্ত" }
    Regex("^Attach image or PDF \\((.+)\\)$").matchEntire(value)?.let { return "ছবি বা PDF সংযুক্ত করুন (${it.groupValues[1]})" }
    Regex("^Amount cannot exceed the remaining (.+)\\.$").matchEntire(value)?.let { return "পরিমাণ বাকি ${it.groupValues[1]}-এর বেশি হতে পারবে না।" }
    Regex("^Payment cannot be more than the remaining (.+)\\.$").matchEntire(value)?.let { return "পেমেন্ট বাকি ${it.groupValues[1]}-এর বেশি হতে পারবে না।" }
    Regex("^Amount cannot exceed this request's outstanding balance\\.$").matchEntire(value)?.let { return "পরিমাণ এই অনুরোধের বাকি টাকার বেশি হতে পারবে না।" }
    Regex("^(.+) will remain in your records but cannot receive further payments\\. This action cannot be undone\\.$").matchEntire(value)?.let { return "${it.groupValues[1]} রেকর্ডে থাকবে, তবে এতে আর পেমেন্ট গ্রহণ করা যাবে না। এই কাজটি ফিরিয়ে নেওয়া যাবে না।" }
    Regex("^Save the changed direction, financial details, notes, and documents for (.+)\\?$").matchEntire(value)?.let { return "${it.groupValues[1]}-এর পরিবর্তিত ধরন, আর্থিক তথ্য, নোট ও ডকুমেন্ট সংরক্ষণ করবেন?" }
    Regex("^Save the new dates and notes for payment (\\d+)\\?$").matchEntire(value)?.let { return "পেমেন্ট ${it.groupValues[1]}-এর নতুন তারিখ ও নোট সংরক্ষণ করবেন?" }
    Regex("^Save these changes to (.+) for (.+)\\?$").matchEntire(value)?.let { return "${it.groupValues[1]}-এর ${it.groupValues[2]} পরিমাণের পরিবর্তন সংরক্ষণ করবেন?" }
    Regex("^You are about to change (.+) for (.+)\\.$").matchEntire(value)?.let { return "আপনি ${it.groupValues[1]}-এর ${it.groupValues[2]} পরিমাণের তথ্য পরিবর্তন করতে যাচ্ছেন।" }
    Regex("^You are about to change payment (\\d+), including its dates or notes\\.$").matchEntire(value)?.let { return "আপনি পেমেন্ট ${it.groupValues[1]}-এর তারিখ বা নোট পরিবর্তন করতে যাচ্ছেন।" }
    Regex("""^Delete "(.+)" and all of its payment history\? This cannot be undone\.$""").matchEntire(value)?.let { return "‘${it.groupValues[1]}’ ও এর সম্পূর্ণ পেমেন্ট ইতিহাস মুছবেন? এটি ফিরিয়ে নেওয়া যাবে না।" }
    Regex("""^Delete "(.+)" for (.+)\? This cannot be undone\.$""").matchEntire(value)?.let { return "‘${it.groupValues[1]}’-এর ${it.groupValues[2]} পরিমাণের রেকর্ড মুছবেন? এটি ফিরিয়ে নেওয়া যাবে না।" }
    Regex("^Delete (EMI|Loan|Debt)\\?$").matchEntire(value)?.let { return "${localized(it.groupValues[1], language)} মুছবেন?" }

    return value
}

internal fun localizedExport(value: String): String {
    if (AppLocaleState.language != "BN") return value
    val trimmed = value.trim()
    if (trimmed.contains(":")) {
        val label = trimmed.substringBefore(":")
        return localized(label) + ":" + localized(trimmed.substringAfter(":").trim())
    }
    return localized(trimmed)
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    style: TextStyle = LocalTextStyle.current
) {
    val resolvedColor = if (color == Color.Unspecified) LocalContentColor.current else color
    MaterialText(
        text = localized(text, LocalAppLanguage.current),
        modifier = modifier,
        color = resolvedColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        overflow = overflow,
        style = style
    )
}


// ============================================================
// DATA MODELS
// ============================================================

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
    return AppLocaleState.currencySymbol + NumberFormat.getNumberInstance(Locale.US).format(value)
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
    return FinanceCalculations.isEmiCompleted(item)
}

fun loanCompleted(item: Loan): Boolean {
    return FinanceCalculations.isLoanCompleted(item)
}

fun debtPaidAmount(item: Debt): Double {
    return FinanceCalculations.debtPaid(item)
}

fun debtRemainingAmount(item: Debt): Double {
    return FinanceCalculations.debtRemaining(item)
}

internal fun syncPaymentRequestStatuses(item: Debt): Debt {
    val requests = item.paymentRequests.map { request ->
        if (request.status == "CANCELLED") request else {
            val received = item.payments
                .filter { it.paidDate != null && it.appliedRequestId == request.id }
                .sumOf { it.amount }
                .coerceAtMost(request.amount)
            request.copy(
                receivedAmount = received,
                status = when {
                    received + 0.005 >= request.amount -> "PAID"
                    received > 0.0 -> "PARTIALLY PAID"
                    else -> "UNPAID"
                }
            )
        }
    }
    return item.copy(paymentRequests = requests)
}

internal fun availableRequestAmount(item: Debt, excludingRequestId: String = ""): Double {
    val reserved = item.paymentRequests
        .filter { it.id != excludingRequestId && it.status in listOf("UNPAID", "PARTIALLY PAID") }
        .sumOf { max(0.0, it.amount - it.receivedAmount) }
    return max(0.0, debtRemainingAmount(item) - reserved)
}

fun debtCompleted(item: Debt): Boolean {
    return FinanceCalculations.isDebtCompleted(item)
}

fun completionDate(payments: List<Payment>): Long? {
    return FinanceCalculations.completionDate(payments)
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
