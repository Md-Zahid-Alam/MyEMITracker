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

private const val PREFS = "finance_tracker_v3"
private const val KEY_DATA = "data"
private const val KEY_DATA_ENCRYPTED = "data_encrypted_v1"
private const val KEY_PASSWORD_HASH = "password_hash"
private const val KEY_PASSWORD_SALT = "password_salt"
private const val CHANNEL_ID = "finance_reminders"
private const val KEY_THEME_MODE = "theme_mode"
private const val KEY_LANGUAGE = "app_language"
private const val KEY_COUNTRY = "payment_country"
private const val KEY_CURRENCY_CODE = "currency_code"
private const val KEY_CURRENCY_SYMBOL = "currency_symbol"
private const val KEY_CUSTOM_BANKS = "custom_banks"
private const val KEY_CUSTOM_PROVIDERS = "custom_providers"
private const val LOCAL_KEY_ALIAS = "my_finance_tracker_records_v1"
private const val BACKUP_FORMAT = "MFT_ENCRYPTED_BACKUP"

private val AppLightColorScheme = FinanceDesignSystem.LightColors
private val AppDarkColorScheme = FinanceDesignSystem.DarkColors

private val LocalFormReadOnly = staticCompositionLocalOf { false }
private val LocalAppLanguage = staticCompositionLocalOf { "EN" }

private object AppLocaleState {
    var language: String = "EN"
    var country: String = "Bangladesh"
    var currencyCode: String = "BDT"
    var currencySymbol: String = "৳"
    var customBanks: List<String> = emptyList()
    var customProviders: List<String> = emptyList()
}

private val BanglaText = mapOf(
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

private val BanglaAdditionalText = mapOf(
    "Version 9.0" to "সংস্করণ ৯.০", "Version 8.3" to "সংস্করণ ৮.৩", "Save this bank for future use" to "ভবিষ্যতের জন্য এই ব্যাংক সংরক্ষণ করুন",
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

private val BanglaValidationText = mapOf(
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

private val BanglaUiText2 = mapOf(
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

private fun localized(value: String, language: String = AppLocaleState.language): String {
    if (language != "BN") return value
    BanglaText[value]?.let { return it }
    BanglaAdditionalText[value]?.let { return it }
    BanglaValidationText[value]?.let { return it }
    BanglaUiText2[value]?.let { return it }

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

private fun localizedExport(value: String): String {
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

private fun syncPaymentRequestStatuses(item: Debt): Debt {
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

private fun availableRequestAmount(item: Debt, excludingRequestId: String = ""): Double {
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

private fun localEncryptionKey(): SecretKey {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    (keyStore.getKey(LOCAL_KEY_ALIAS, null) as? SecretKey)?.let { return it }
    val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    generator.init(
        KeyGenParameterSpec.Builder(
            LOCAL_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
    )
    return generator.generateKey()
}

private fun encryptLocalRecords(plainText: String): String {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, localEncryptionKey())
    val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
    return JSONObject().apply {
        put("format", "MFT_LOCAL_ENCRYPTED")
        put("version", 1)
        put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
        put("ciphertext", Base64.encodeToString(encrypted, Base64.NO_WRAP))
    }.toString()
}

private fun decryptLocalRecords(payload: String): String {
    val root = JSONObject(payload)
    require(root.optString("format") == "MFT_LOCAL_ENCRYPTED") { "Unsupported encrypted record format." }
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val iv = Base64.decode(root.getString("iv"), Base64.NO_WRAP)
    cipher.init(Cipher.DECRYPT_MODE, localEncryptionKey(), GCMParameterSpec(128, iv))
    val clear = cipher.doFinal(Base64.decode(root.getString("ciphertext"), Base64.NO_WRAP))
    return clear.toString(Charsets.UTF_8)
}

private fun encryptBackup(plainText: String, password: String): String {
    require(password.isNotBlank()) { "Enter a backup password." }
    val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val spec = PBEKeySpec(password.toCharArray(), salt, 210_000, 256)
    val keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    spec.clearPassword()
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(keyBytes, "AES"))
    keyBytes.fill(0)
    val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
    return JSONObject().apply {
        put("format", BACKUP_FORMAT)
        put("version", 1)
        put("kdf", "PBKDF2-HMAC-SHA256")
        put("iterations", 210_000)
        put("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
        put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
        put("ciphertext", Base64.encodeToString(encrypted, Base64.NO_WRAP))
    }.toString()
}

private fun decryptBackup(payload: String, password: String): String {
    BackupPolicy.requireAcceptableSize(payload.length)
    val root = JSONObject(payload)
    require(root.optString("format") == BACKUP_FORMAT) { "This is not an encrypted My Finance Tracker backup." }
    val salt = Base64.decode(root.getString("salt"), Base64.NO_WRAP)
    val iterations = BackupPolicy.validatedIterations(root.optInt("iterations", BackupPolicy.DEFAULT_ITERATIONS))
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, 256)
    val keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    spec.clearPassword()
    return try {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(keyBytes, "AES"),
            GCMParameterSpec(128, Base64.decode(root.getString("iv"), Base64.NO_WRAP))
        )
        cipher.doFinal(Base64.decode(root.getString("ciphertext"), Base64.NO_WRAP)).toString(Charsets.UTF_8)
    } catch (_: Exception) {
        throw IllegalArgumentException("Incorrect backup password or damaged backup file.")
    } finally {
        keyBytes.fill(0)
    }
}


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
    onThemeChange: (String) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    country: String,
    currencyCode: String,
    currencySymbol: String,
    onRegionChange: (String, String, String) -> Unit,
    onCustomPaymentListsChange: (List<String>, List<String>) -> Unit
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
        mutableStateOf("")
    }
    var showBackupPasswordDialog by remember { mutableStateOf(false) }
    var backupPassword by remember { mutableStateOf("") }
    var backupPasswordConfirm by remember { mutableStateOf("") }
    var backupError by remember { mutableStateOf("") }
    var pendingBackupContent by remember { mutableStateOf("") }
    var restoreContent by remember { mutableStateOf<String?>(null) }
    var restorePassword by remember { mutableStateOf("") }
    var restoreError by remember { mutableStateOf("") }
    var restoreIsLegacy by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedType.isNotBlank() || tab != 0) {
        if (selectedType.isNotBlank()) {
            selectedType = when (selectedType) {
            "emi_history", "emi_documents", "emi_financing", "emi_information", "emi_payment" -> "emi_detail"
            "loan_history", "loan_documents", "loan_financing", "loan_information", "loan_payment" -> "loan_detail"
            "debt_history", "debt_documents", "debt_financing", "debt_information", "debt_payment" -> "debt_detail"
                "emi" -> if (selectedId.isNotBlank()) "emi_detail" else ""
                "loan" -> if (selectedId.isNotBlank()) "loan_detail" else ""
                "debt" -> if (selectedId.isNotBlank()) "debt_detail" else ""
                else -> ""
            }
            if (selectedType.isBlank()) selectedId = ""
        } else if (tab == 1 && paymentSection.isNotBlank()) {
            paymentSection = ""
        } else {
            tab = 0
        }
    }

    val backupLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument(
                "application/octet-stream"
            )
        ) { uri ->

            if (uri != null) {

                context.contentResolver
                    .openOutputStream(uri)
                    ?.use { output ->

                        output.write(pendingBackupContent.toByteArray())
                    }
            }
            pendingBackupContent = ""
        }

    val restoreLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                runCatching {
                    val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        ?: throw IllegalArgumentException("Unable to read the selected backup.")
                    require(content.length <= 40_000_000) { "Backup file is too large." }
                    content
                }.onSuccess { content ->
                    restoreContent = content
                    restoreIsLegacy = runCatching { JSONObject(content).optString("format") }.getOrNull() != BACKUP_FORMAT
                    restorePassword = ""
                    restoreError = ""
                }.onFailure { restoreError = it.message ?: "Unable to read the backup." }
            }
        }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (selectedType.isBlank()) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {

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
                            if (index == 1) paymentSection = ""
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
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            }
        },

        floatingActionButton = {

            if (selectedType.isBlank() && (tab == 2 || (tab == 1 && paymentSection.isNotBlank()))) {

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
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = if (selectedType.isBlank() && tab == 0) FinanceLayout.dashboardContentMax else FinanceLayout.phoneContentMax)
                    .fillMaxSize()
            ) {
            when {

                selectedType.endsWith("_detail") -> {
                    val kind = selectedType.removeSuffix("_detail")
                    PaymentPlanDetail(
                        viewModel = viewModel,
                        kind = kind,
                        id = selectedId,
                        onBack = { selectedType = ""; selectedId = "" },
                        onOpen = { page -> selectedType = if (page == "edit") kind else "${kind}_$page" }
                    )
                }

                selectedType.endsWith("_history") -> {
                    PaymentPlanHistory(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_history"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_history") + "_detail" }
                    )
                }

                selectedType.endsWith("_documents") -> {
                    PaymentPlanDocuments(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_documents"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_documents") + "_detail" }
                    )
                }

                selectedType.endsWith("_financing") -> {
                    PaymentPlanFinancing(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_financing"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_financing") + "_detail" }
                    )
                }

                selectedType.endsWith("_information") -> {
                    PaymentPlanInformation(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_information"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_information") + "_detail" }
                    )
                }

                selectedType.endsWith("_payment") -> {
                    PaymentPlanPayment(
                        viewModel = viewModel,
                        kind = selectedType.removeSuffix("_payment"),
                        id = selectedId,
                        onBack = { selectedType = selectedType.removeSuffix("_payment") + "_detail" }
                    )
                }

                selectedType == "emi" -> {

                    EmiForm(
                        viewModel,
                        viewModel.data.emis.find {
                            it.id == selectedId
                        },
                        done = {
                            if (selectedId.isBlank()) selectedType = "" else selectedType = "emi_detail"
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
                            if (selectedId.isBlank()) selectedType = "" else selectedType = "loan_detail"
                        }
                    )
                }

                selectedType == "debt" -> {

                    DebtForm(
                        viewModel,
                        viewModel.data.debts.find {
                            it.id == selectedId
                        },
                        initialDirection = if (paymentSection == "DebtsOwed") "Owed to Me" else "I Owe",
                        done = {
                            if (selectedId.isBlank()) selectedType = "" else selectedType = "debt_detail"
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

                selectedType == "about" -> {
                    AboutScreen(done = { selectedType = "" })
                }

                selectedType == "receipt_profile" -> {
                    ReceiptProfileForm(
                        existing = viewModel.data.receiptProfile,
                        onSave = viewModel::updateReceiptProfile,
                        done = { selectedType = "" }
                    )
                }

                selectedType == "country" -> {
                    CountrySettingsScreen(
                        country = country,
                        currencyCode = currencyCode,
                        currencySymbol = currencySymbol,
                        onRegionChange = onRegionChange,
                        onCustomPaymentListsChange = onCustomPaymentListsChange,
                        done = { selectedType = "" }
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
                        backupPassword = ""
                        backupPasswordConfirm = ""
                        backupError = ""
                        showBackupPasswordDialog = true
                    },
                    onRestore = {
                        restoreLauncher.launch(
                            arrayOf("application/json", "text/plain", "*/*")
                        )
                    },
                    onLock = onLogout,
                    onAbout = { selectedType = "about" },
                    onReceiptProfile = { selectedType = "receipt_profile" },
                    onCountry = { selectedType = "country" },
                    themeMode = themeMode,
                    onThemeChange = onThemeChange,
                    language = language,
                    onLanguageChange = onLanguageChange,
                    country = country,
                    currencyCode = currencyCode,
                    currencySymbol = currencySymbol,
                    onRegionChange = onRegionChange,
                    onCustomPaymentListsChange = onCustomPaymentListsChange
                )
            }
            }
        }
    }

    if (showBackupPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showBackupPasswordDialog = false },
            title = { Text("Export Encrypted Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose a backup password. You will need it to restore this file on any phone.")
                    Field("Backup password", backupPassword, isPassword = true) { backupPassword = it }
                    Field("Confirm backup password", backupPasswordConfirm, isPassword = true) { backupPasswordConfirm = it }
                    if (backupError.isNotBlank()) Text(backupError, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    backupError = when {
                        backupPassword.length < 6 -> "Use at least 6 characters for the backup password."
                        backupPassword != backupPasswordConfirm -> "Backup passwords do not match."
                        else -> ""
                    }
                    if (backupError.isBlank()) {
                        runCatching { viewModel.backup(backupPassword) }
                            .onSuccess {
                                pendingBackupContent = it
                                showBackupPasswordDialog = false
                                val stamp = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                                backupLauncher.launch("MyFinanceTracker_Backup_$stamp.mftbackup")
                            }
                            .onFailure { backupError = it.message ?: "Backup could not be created." }
                    }
                }) { Text("Export") }
            },
            dismissButton = { TextButton(onClick = { showBackupPasswordDialog = false }) { Text("Cancel") } }
        )
    }

    restoreContent?.let { content ->
        AlertDialog(
            onDismissRequest = { restoreContent = null },
            title = { Text(if (restoreIsLegacy) "Restore Legacy Backup?" else "Restore Encrypted Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (restoreIsLegacy) "This older JSON backup is readable and not encrypted. Import it only if you trust its source. Current records will be replaced."
                        else "Enter the backup password. Current records will be replaced only after authentication and validation."
                    )
                    if (!restoreIsLegacy) Field("Backup password", restorePassword, isPassword = true) { restorePassword = it }
                    if (restoreError.isNotBlank()) Text(restoreError, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    runCatching { viewModel.restore(content, restorePassword, restoreIsLegacy) }
                        .onSuccess {
                            restoreContent = null
                            restorePassword = ""
                            restoreError = ""
                        }
                        .onFailure { restoreError = it.message ?: "Restore failed." }
                }) { Text(if (restoreIsLegacy) "Import Legacy Backup" else "Restore") }
            },
            dismissButton = { TextButton(onClick = { restoreContent = null }) { Text("Cancel") } }
        )
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
    if (section.isBlank()) {
        PaymentsLanding(viewModel = viewModel, onOpenSection = onSectionChange)
        return
    }
    var search by remember { mutableStateOf("") }
    var sortMode by remember { mutableStateOf("Newest first") }
    var searchVisible by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onSectionChange("") }) {
                Icon(Icons.Default.ArrowBack, "Back to Payments")
            }
            Text(
                when (section) {
                    "EMI" -> "EMI Plans"
                    "Loans" -> "Loans"
                    "DebtsOwe" -> "Money I Owe"
                    else -> "Money Owed to Me"
                },
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

        Box(modifier = Modifier.weight(1f)) {
            when (section) {
                "EMI" -> EmiList(viewModel, search, sortMode) { onOpen("emi_detail", it) }
                "Loans" -> LoanList(viewModel, search, sortMode) { onOpen("loan_detail", it) }
                "DebtsOwe" -> DebtList(viewModel, search, sortMode, "I Owe") { onOpen("debt_detail", it) }
                else -> DebtList(viewModel, search, sortMode, "Owed to Me") { onOpen("debt_detail", it) }
            }
        }
    }
}

@Composable
fun PaymentsLanding(viewModel: FinanceViewModel, onOpenSection: (String) -> Unit) {
    var debtsExpanded by remember { mutableStateOf(false) }
    val activeEmis = viewModel.data.emis.filter { !it.archived && !emiCompleted(it) }
    val activeLoans = viewModel.data.loans.filter { !it.archived && !loanCompleted(it) }
    val activeDebts = viewModel.data.debts.filter { !it.archived && !debtCompleted(it) }
    val debtToPay = activeDebts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) }
    val moneyToReceive = activeDebts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = FinanceSpacing.screen, vertical = FinanceSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FinanceSpacing.md)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Payments", style = MaterialTheme.typography.headlineLarge)
                Text("Manage instalments, loans and personal balances.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            PaymentSectionCard(
                title = "EMI Plans",
                summary = "${activeEmis.size} active • ${money(activeEmis.sumOf { emi -> emi.payments.filter { it.paidDate == null }.sumOf { it.amount } })} left",
                onClick = { onOpenSection("EMI") }
            )
        }
        item {
            PaymentSectionCard(
                title = "Loans",
                summary = "${activeLoans.size} active • ${money(activeLoans.sumOf { loan -> loan.payments.filter { it.paidDate == null }.sumOf { it.amount } })} left",
                onClick = { onOpenSection("Loans") }
            )
        }
        item {
            PaymentSectionCard("Debts", "Pay ${money(debtToPay)} • Receive ${money(moneyToReceive)}") { debtsExpanded = !debtsExpanded }
        }
        if (debtsExpanded) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(Modifier.padding(FinanceSpacing.sm), verticalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)) {
                        OutlinedButton(onClick = { onOpenSection("DebtsOwe") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Money I Owe • ${activeDebts.count { it.direction == "I Owe" }} active • ${money(debtToPay)}")
                        }
                        OutlinedButton(onClick = { onOpenSection("DebtsOwed") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Money Owed to Me • ${activeDebts.count { it.direction == "Owed to Me" }} active • ${money(moneyToReceive)}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentPlanDetail(
    viewModel: FinanceViewModel,
    kind: String,
    id: String,
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    var showRequestDialog by remember { mutableStateOf(false) }
    var editingRequest by remember { mutableStateOf<PaymentRequest?>(null) }
    var cancellingRequest by remember { mutableStateOf<PaymentRequest?>(null) }
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    val name = emi?.name ?: loan?.name ?: debt?.name ?: "Plan"
    val payments = emi?.payments ?: loan?.payments ?: debt?.payments ?: emptyList()
    val total = emi?.totalPayable ?: loan?.totalPayable ?: debt?.originalAmount ?: 0.0
    val paid = payments.filter { it.paidDate != null }.sumOf { it.amount }
    val archived = emi?.archived ?: loan?.archived ?: debt?.archived ?: false
    val completed = paid + 0.005 >= total
    val documents = emi?.attachments ?: loan?.attachments ?: debt?.attachments ?: emptyList()
    val typeName = when (kind) { "emi" -> "EMI"; "loan" -> "Loan"; else -> "Debt" }

    FormColumn(title = "$typeName Details", onBack = onBack, readOnly = true) {
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(when { archived -> "Archived"; completed -> "Completed"; else -> "Active" }, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Total: ${money(total)}")
                Text("Paid: ${money(paid)}")
                Text("Remaining: ${money(max(0.0, total - paid))}", fontWeight = FontWeight.Bold)
                if (debt != null) Text(if (debt.direction == "I Owe") "You owe ${debt.name}" else "${debt.name} owes you")
            }
        }
        if (!archived && !completed) {
            Button(onClick = { onOpen("payment") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (debt?.direction == "Owed to Me") "Record Received Amount" else "Record Payment")
            }
            OutlinedButton(onClick = { onOpen("edit") }, modifier = Modifier.fillMaxWidth()) { Text("Edit Plan Information") }
            if (debt?.direction == "Owed to Me") {
                OutlinedButton(onClick = { showRequestDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("Create Payment Request") }
            }
        } else {
            Text("This record is view-only. Reopen or restore it from the plan menu before making changes.")
        }
        DetailNavigationButton("Payment History", "${payments.count { it.paidDate != null }} recorded") { onOpen("history") }
        DetailNavigationButton("Plan Information", "View all original plan details") { onOpen("information") }
        DetailNavigationButton("Documents", if (documents.isEmpty()) "No documents" else "${documents.size} attached") { onOpen("documents") }
        DetailNavigationButton("Financing Information", "Source, method, reference and notes") { onOpen("financing") }
        if (debt != null && debt.paymentRequests.isNotEmpty()) {
            Text("Payment Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            debt.paymentRequests.sortedByDescending { it.createdDate }.forEach { request ->
                PaymentRequestCard(
                    debt, request, viewModel.data.receiptProfile,
                    onEdit = { editingRequest = request },
                    onCancel = { cancellingRequest = request }
                )
            }
        }
    }
    if (showRequestDialog && debt != null) {
        PaymentRequestDialog(debt, onSave = { viewModel.addPaymentRequest(debt.id, it); showRequestDialog = false }, onDismiss = { showRequestDialog = false })
    }
    if (editingRequest != null && debt != null) {
        PaymentRequestDialog(debt, existing = editingRequest, onSave = { viewModel.updatePaymentRequest(debt.id, it); editingRequest = null }, onDismiss = { editingRequest = null })
    }
    cancellingRequest?.let { request ->
        ConfirmationDialog(
            ConfirmationRequest(
                title = "Cancel Payment Request?",
                message = "${request.requestNumber} will remain in your records but cannot receive further payments. This action cannot be undone.",
                confirmLabel = "Cancel Request",
                onConfirm = { debt?.let { viewModel.cancelPaymentRequest(it.id, request.id) } }
            )
        ) { cancellingRequest = null }
    }
}

@Composable
private fun DetailNavigationButton(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier.padding(horizontal = FinanceSpacing.md, vertical = FinanceSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Open $title", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun PaymentPlanHistory(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    val payments = emi?.payments ?: loan?.payments ?: debt?.payments ?: emptyList()
    val name = emi?.name ?: loan?.name ?: debt?.name ?: "Payment"
    val total = emi?.totalPayable ?: loan?.totalPayable ?: debt?.originalAmount ?: 0.0
    FormColumn(title = "Payment History", onBack = onBack, readOnly = false) {
        PaymentHistory(
            payments = payments,
            onUpdate = when (kind) {
                "emi" -> ({ payment -> viewModel.updateEmiPayment(id, payment) })
                "loan" -> ({ payment -> viewModel.updateLoanPayment(id, payment) })
                else -> ({ payment -> viewModel.updateDebtPayment(id, payment) })
            },
            planName = name,
            direction = debt?.direction ?: "I Owe",
            planTotal = total,
            profile = viewModel.data.receiptProfile
        )
    }
}

@Composable
fun PaymentPlanDocuments(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val documents = when (kind) {
        "emi" -> viewModel.data.emis.find { it.id == id }?.attachments
        "loan" -> viewModel.data.loans.find { it.id == id }?.attachments
        else -> viewModel.data.debts.find { it.id == id }?.attachments
    } ?: emptyList()
    FormColumn(title = "Documents", onBack = onBack, readOnly = true) {
        if (documents.isEmpty()) Text("No supporting documents are attached.")
        documents.forEach { attachment ->
            OutlinedButton(onClick = { runCatching { openAttachment(context, attachment) } }, modifier = Modifier.fillMaxWidth()) {
                Text("Open ${attachment.name}")
            }
        }
    }
}

@Composable
fun PaymentPlanFinancing(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    FormColumn(title = "Financing Information", onBack = onBack, readOnly = true) {
        if (emi != null) {
            InfoRow("Source", emi.financingSource)
            InfoRow("How received", emi.receivedMethod)
            InfoRow("Seller / provider", emi.seller)
            InfoRow("Agreement reference", emi.agreementReference)
            InfoRow("Notes", emi.financingNotes)
            FinancingMethodInfo(emi.financingChannel, emi.financingAccountName, emi.financingAccountNumber, emi.financingBranch, emi.financingRoutingNumber, emi.financingReference, emi.financingMethodDetails)
        } else if (loan != null) {
            InfoRow("Source", loan.financingSource)
            InfoRow("How received", loan.receivedMethod)
            InfoRow("Lender", loan.lender)
            InfoRow("Agreement reference", loan.agreementReference)
            InfoRow("Notes", loan.financingNotes)
            FinancingMethodInfo(loan.financingChannel, loan.financingAccountName, loan.financingAccountNumber, loan.financingBranch, loan.financingRoutingNumber, loan.financingReference, loan.financingMethodDetails)
        } else if (debt != null) {
            InfoRow("Direction", debt.direction)
            InfoRow("Reason", debt.reason)
            InfoRow("How received / given", debt.receivedOrGivenMethod)
            InfoRow("Agreement reference", debt.referenceNumber)
            InfoRow("Notes", debt.notes)
            FinancingMethodInfo(debt.financingChannel, debt.financingAccountName, debt.financingAccountNumber, debt.financingBranch, debt.financingRoutingNumber, debt.financingReference, debt.financingMethodDetails)
        } else Text("Record not found.")
    }
}

@Composable
private fun FinancingMethodInfo(channel: String, accountName: String, accountNumber: String, branch: String, routing: String, reference: String, details: String) {
    if (channel.isNotBlank()) InfoRow("Provider / bank", channel)
    if (accountName.isNotBlank()) InfoRow("Account holder / party", accountName)
    if (accountNumber.isNotBlank()) InfoRow("Account / mobile number", accountNumber)
    if (branch.isNotBlank()) InfoRow("Branch", branch)
    if (routing.isNotBlank()) InfoRow("Routing number", routing)
    if (reference.isNotBlank()) InfoRow("Transaction reference", reference)
    if (details.isNotBlank()) InfoRow("Method details", details)
}

@Composable
fun PaymentPlanInformation(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    FormColumn(title = "Plan Information", onBack = onBack, readOnly = true) {
        when {
            emi != null -> {
                InfoRow("Item name", emi.name); InfoRow("Category", emi.category); InfoRow("Seller / provider", emi.seller)
                InfoRow("Purchase price", money(emi.price)); InfoRow("Down payment", money(emi.downPayment)); InfoRow("Financed amount", money(emi.financedAmount))
                InfoRow("Interest rate", "${emi.interestRate}%"); InfoRow("Interest amount", money(emi.interestAmount)); InfoRow("Total payable", money(emi.totalPayable))
                InfoRow("Installments", emi.installments.toString()); InfoRow("Monthly payment", money(emi.monthlyPayment)); InfoRow("Start date", dateText(emi.startDate))
                InfoRow("Due day", emi.dueDay.toString()); InfoRow("Reminder days", emi.reminderDays.joinToString(", ")); InfoRow("Status", if (emi.archived) "Archived" else if (emiCompleted(emi)) "Completed" else "Active")
            }
            loan != null -> {
                InfoRow("Loan name", loan.name); InfoRow("Loan type", loan.type); InfoRow("Lender", loan.lender)
                InfoRow("Principal", money(loan.principal)); InfoRow("Interest rate", "${loan.interestRate}%"); InfoRow("Interest amount", money(loan.interestAmount)); InfoRow("Total payable", money(loan.totalPayable))
                InfoRow("Repayment mode", loan.repaymentMode); InfoRow("Installments", loan.installments.toString()); InfoRow("Monthly payment", money(loan.monthlyPayment)); InfoRow("Start date", dateText(loan.startDate))
                InfoRow("Due day", loan.dueDay.toString()); InfoRow("Reminder days", loan.reminderDays.joinToString(", ")); InfoRow("Status", if (loan.archived) "Archived" else if (loanCompleted(loan)) "Completed" else "Active")
            }
            debt != null -> {
                InfoRow("Person / organization", debt.name); InfoRow("Direction", debt.direction); InfoRow("Debt date", dateText(debt.debtDate)); InfoRow("Original amount", money(debt.originalAmount))
                InfoRow("Paid / received", money(debtPaidAmount(debt))); InfoRow("Remaining", money(debtRemainingAmount(debt))); InfoRow("Due date", debt.dueDate?.let { dateText(it) } ?: "Not specified")
                InfoRow("Reason", debt.reason); InfoRow("Notes", debt.notes); InfoRow("Status", if (debt.archived) "Archived" else if (debtCompleted(debt)) "Completed" else "Active")
            }
            else -> Text("Record not found.")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            Text(value.ifBlank { "Not recorded" }, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PaymentPlanPayment(viewModel: FinanceViewModel, kind: String, id: String, onBack: () -> Unit) {
    val emi = viewModel.data.emis.find { it.id == id }
    val loan = viewModel.data.loans.find { it.id == id }
    val debt = viewModel.data.debts.find { it.id == id }
    val pending = (emi?.payments ?: loan?.payments ?: emptyList()).firstOrNull { it.paidDate == null }
    FormColumn(title = if (debt?.direction == "Owed to Me") "Receive Payment" else "Record Payment", onBack = onBack, readOnly = false) {
        if (debt != null && debtCompleted(debt)) {
            Text("This debt is fully completed. No additional payment can be recorded.", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        } else if (debt != null) {
            DebtPaymentEntry(viewModel, debt, onSaved = onBack)
        } else if (pending == null) {
            Text("There is no pending payment for this plan.")
        } else {
            Text("Next installment: ${money(pending.amount)}", fontWeight = FontWeight.Bold)
            Text("Due ${dateText(pending.dueDate)}")
            PaymentHistory(
                payments = listOf(pending),
                onUpdate = if (kind == "emi") ({ p -> viewModel.updateEmiPayment(id, p); onBack() }) else ({ p -> viewModel.updateLoanPayment(id, p); onBack() }),
                planName = emi?.name ?: loan?.name ?: "Payment",
                planTotal = emi?.totalPayable ?: loan?.totalPayable ?: 0.0,
                profile = viewModel.data.receiptProfile
            )
        }
    }
}

@Composable
private fun DebtPaymentEntry(viewModel: FinanceViewModel, debt: Debt, onSaved: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var paidDate by remember { mutableStateOf(expenseDateText(System.currentTimeMillis())) }
    var method by remember { mutableStateOf("Cash") }
    var channel by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf(debt.name) }
    var accountNumber by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var routingNumber by remember { mutableStateOf("") }
    var methodDetails by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(emptyList<Attachment>()) }
    var error by remember { mutableStateOf("") }
    val remaining = debtRemainingAmount(debt)
    val openRequests = debt.paymentRequests.filter { it.status in listOf("UNPAID", "PARTIALLY PAID") }
    val requestOptions = listOf("No payment request") + openRequests.map { "${it.requestNumber} • ${money(it.amount - it.receivedAmount)} left" }
    var requestSelection by remember(debt.id, openRequests.map { it.id to it.status }) {
        mutableStateOf(if (openRequests.size == 1) requestOptions[1] else "No payment request")
    }

    Text("${debt.name} • Remaining ${money(remaining)}", fontWeight = FontWeight.Bold)
    Field(if (debt.direction == "Owed to Me") "Received amount" else "Payment amount", amount) { amount = it; error = "" }
    DatePickerField("Payment date", paidDate) { paidDate = it; error = "" }
    ChoiceDropdown("Payment method", method, listOf("Cash", "Bank transfer", "Mobile banking", "Salary deduction", "Card", "Cheque", "Other")) {
        method = it; channel = ""; accountNumber = ""; branch = ""; routingNumber = ""; reference = ""; methodDetails = ""; accountName = if (it == "Cash") debt.name else ""
    }
    PaymentMethodDetailsFields(
        method, channel, { channel = it }, accountName, { accountName = it }, accountNumber, { accountNumber = it },
        branch, { branch = it }, routingNumber, { routingNumber = it }, reference, { reference = it }, methodDetails, { methodDetails = it }
    )
    Field("Payment notes", notes) { notes = it }
    AttachmentSection(attachments, maxFiles = 3) { attachments = it }
    if (debt.direction == "Owed to Me" && openRequests.isNotEmpty()) {
        ChoiceDropdown("Apply payment to", requestSelection, requestOptions) { requestSelection = it }
    }
    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    Button(onClick = {
        val value = amount.toDoubleOrNull() ?: 0.0
        val date = parseExpenseDate(paidDate)
        val selectedRequest = openRequests.find { requestSelection.startsWith(it.requestNumber) }
        error = when {
            !value.isFinite() || value <= 0.0 -> "Enter a valid amount greater than zero."
            value > 999_999_999.99 -> "Amount is too large."
            value > remaining + 0.005 -> "Amount cannot exceed the remaining ${money(remaining)}."
            selectedRequest != null && value > (selectedRequest.amount - selectedRequest.receivedAmount) + 0.005 -> "Amount cannot exceed this request's outstanding balance."
            date == null -> "Select a valid payment date."
            date > System.currentTimeMillis() -> "Payment date cannot be in the future."
            paymentMethodValidation(method, channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, accountName, accountNumber, reference, methodDetails).isNotBlank() -> paymentMethodValidation(method, channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, accountName, accountNumber, reference, methodDetails)
            else -> ""
        }
        if (error.isBlank() && date != null) {
            viewModel.markDebtPaid(debt.id, value, paidDate = date, method = method, channel = channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, reference = reference.trim(), counterparty = accountName.trim(), notes = notes.trim(), attachments = attachments, requestId = selectedRequest?.id ?: "", accountNumber = accountNumber.trim(), branch = branch.trim(), routingNumber = routingNumber.trim(), methodDetails = methodDetails.trim())
            onSaved()
        }
    }, modifier = Modifier.fillMaxWidth()) { Text(if (debt.direction == "Owed to Me") "Save Received Amount" else "Save Payment") }
}

@Composable
fun PaymentSectionCard(title: String, summary: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = FinanceShapes.large
    ) {
        Row(Modifier.padding(FinanceSpacing.lg), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(summary, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = FinanceShapes.pill) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Open $title",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(8.dp)
                )
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
            shape = FinanceShapes.pill,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) { Text(label, maxLines = 1) }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = FinanceShapes.pill,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
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

    val debtToPay =
        viewModel.data.debts.filter { !it.archived && it.direction == "I Owe" }.sumOf { debt ->
            debtRemainingAmount(debt)
        }

    val moneyToReceive =
        viewModel.data.debts.filter { !it.archived && it.direction == "Owed to Me" }.sumOf { debt ->
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
            PaddingValues(horizontal = FinanceSpacing.screen, vertical = FinanceSpacing.lg),

        verticalArrangement =
            Arrangement.spacedBy(FinanceSpacing.md)

    ) {

        item {

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                Column(Modifier.padding(FinanceSpacing.lg), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("SPENT THIS MONTH", style = MaterialTheme.typography.labelMedium)
                    Text(money(monthExpenses), style = MaterialTheme.typography.displaySmall)
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.58f),
                        shape = FinanceShapes.pill
                    ) {
                        Text(
                            "Today  ${money(todayExpenses)}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        item { PremiumSectionHeader("Commitments", "Amounts still active") }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FinanceSpacing.sm)
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
                    Arrangement.spacedBy(FinanceSpacing.sm)
            ) {

                SummaryCard(
                    "Loan Left",
                    money(loanRemaining),
                    Modifier.weight(1f)
                )

                SummaryCard(
                    "Debt to Pay",
                    money(debtToPay),
                    Modifier.weight(1f)
                )
            }
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

        val recentExpenses = viewModel.data.expenses
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
        modifier = modifier.heightIn(min = 96.dp),
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
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
private fun PremiumSectionHeader(title: String, subtitle: String = "") {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PremiumEmptyState(title: String, message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = FinanceShapes.large
    ) {
        Column(
            modifier = Modifier.padding(FinanceSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Modifier
                .fillMaxSize()
                .widthIn(max = FinanceLayout.formContentMax),

        contentPadding =
            PaddingValues(horizontal = FinanceSpacing.screen, vertical = FinanceSpacing.lg),

        verticalArrangement =
            Arrangement.spacedBy(FinanceSpacing.md)
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
    direction: String,
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
        statusMatches && item.direction == direction && (
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
            val directionTotal = viewModel.data.debts.filter { !it.archived && it.direction == direction }.sumOf { debtRemainingAmount(it) }
            Text(if (direction == "I Owe") "Money I Owe" else "Money Owed to Me", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            SummaryCard(if (direction == "I Owe") "Total to pay" else "Total to receive", money(directionTotal), Modifier.fillMaxWidth())
        }

        item {
            StatusFilterRow(statusFilter) { statusFilter = it }
        }

        if (visibleItems.isEmpty()) {

            item {

                Text(
                    "No $statusFilter records."
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
                    Text(
                        if (item.direction == "I Owe") "↑ YOU NEED TO PAY" else "↓ YOU NEED TO RECEIVE",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Original ${money(item.originalAmount)}")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (item.direction == "I Owe") "Paid ${money(paid)}" else "Received ${money(paid)}")
                        Text(
                            if (item.direction == "I Owe") "${money(remaining)} to pay" else "${money(remaining)} to receive",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
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
    var viewingDocuments by remember { mutableStateOf<Expense?>(null) }

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
                            if (expense.attachments.isNotEmpty()) {
                                Text("${expense.attachments.size} document(s) attached", color = MaterialTheme.colorScheme.primary)
                            }

                            Row {
                                if (expense.attachments.isNotEmpty()) {
                                    TextButton(onClick = { viewingDocuments = expense }) {
                                        Text(if (expense.attachments.size == 1) "View Document" else "View Documents")
                                    }
                                }
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

    viewingDocuments?.let { expense ->
        AlertDialog(
            onDismissRequest = { viewingDocuments = null },
            title = { Text("${expense.title} Documents") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    expense.attachments.forEach { attachment ->
                        OutlinedButton(onClick = { runCatching { openAttachment(context, attachment) } }, modifier = Modifier.fillMaxWidth()) {
                            Text("Open ${attachment.name}")
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { viewingDocuments = null }) { Text("Close") } }
        )
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

private fun amountInWords(amount: Double): String {
    fun underThousand(value: Int): String {
        val ones = listOf("Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
        val tens = listOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")
        if (value < 20) return ones[value]
        if (value < 100) return tens[value / 10] + if (value % 10 == 0) "" else " ${ones[value % 10]}"
        return ones[value / 100] + " Hundred" + if (value % 100 == 0) "" else " ${underThousand(value % 100)}"
    }
    fun whole(value: Long): String {
        if (value == 0L) return "Zero"
        val scales = listOf(1_000_000_000L to "Billion", 1_000_000L to "Million", 1_000L to "Thousand")
        var remaining = value
        val parts = mutableListOf<String>()
        scales.forEach { (scale, label) ->
            if (remaining >= scale) {
                parts += "${whole(remaining / scale)} $label"
                remaining %= scale
            }
        }
        if (remaining > 0) parts += underThousand(remaining.toInt())
        return parts.joinToString(" ")
    }
    val taka = amount.toLong().coerceAtLeast(0)
    val poisha = kotlin.math.round((amount - taka) * 100).toInt().coerceIn(0, 99)
    return buildString {
        append(whole(taka)).append(" Taka")
        if (poisha > 0) append(" and ").append(underThousand(poisha)).append(" Poisha")
        append(" Only")
    }
}

private fun paymentReceiptText(
    planName: String,
    direction: String,
    total: Double,
    payments: List<Payment>,
    payment: Payment,
    profile: ReceiptProfile
): String {
    val paidBefore = payments.filter { it.paidDate != null && it.number < payment.number }.sumOf { it.amount }
    val previousBalance = max(0.0, total - paidBefore)
    val remainingBalance = max(0.0, previousBalance - payment.amount)
    val owner = profile.fullName.ifBlank { "App user" }
    val payer = if (direction == "Owed to Me") planName else owner
    val recipient = if (direction == "Owed to Me") owner else payment.counterparty.ifBlank { planName }
    val planCode = planName.hashCode().toUInt().toString(16).takeLast(4).uppercase(Locale.US)
    val receiptNumber = "MFT-${SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(payment.paidDate ?: payment.dueDate))}-$planCode-${payment.number.toString().padStart(3, '0')}"
    return buildString {
        appendLine("MY FINANCE TRACKER")
        appendLine("PAYMENT RECEIPT")
        appendLine()
        appendLine("Receipt number: $receiptNumber")
        appendLine("Payment date: ${dateTimeText(payment.paidDate ?: payment.dueDate)}")
        appendLine("Record: $planName")
        appendLine("Payer: $payer")
        appendLine("Recipient: $recipient")
        appendLine("Amount: ${money(payment.amount)}")
        appendLine("Amount in words: ${amountInWords(payment.amount)}")
        appendLine("Payment method: ${payment.paymentMethod}")
        if (payment.paymentChannel.isNotBlank()) appendLine("Channel: ${payment.paymentChannel}")
        if (payment.accountNumber.isNotBlank()) appendLine("Account / last four digits: ${payment.accountNumber}")
        if (payment.branch.isNotBlank()) appendLine("Branch: ${payment.branch}")
        if (payment.routingNumber.isNotBlank()) appendLine("Routing number: ${payment.routingNumber}")
        if (payment.methodDetails.isNotBlank()) appendLine("Method details: ${payment.methodDetails}")
        if (payment.referenceNumber.isNotBlank()) appendLine("Transaction/reference ID: ${payment.referenceNumber}")
        appendLine("Previous balance: ${money(previousBalance)}")
        appendLine("Remaining balance: ${money(remainingBalance)}")
        if (payment.notes.isNotBlank()) appendLine("Notes: ${payment.notes}")
        appendLine()
        appendLine("Payer signature: ____________________")
        appendLine("Recipient signature: ________________")
        appendLine()
        appendLine("Personal payment record generated by My Finance Tracker. Recipient confirmation or signature may be required as proof of payment.")
        appendLine("Powered by Md. Zahid Alam")
        profile.signature?.let { appendLine("[[SIGNATURE:${it.contentBase64}]]") }
    }
}

@Composable
fun PaymentHistory(
    payments: List<Payment>,
    onUpdate: ((Payment) -> Unit)? = null,
    planName: String = "Payment",
    direction: String = "I Owe",
    planTotal: Double = payments.sumOf { it.amount },
    profile: ReceiptProfile = ReceiptProfile()
) {

    val context = LocalContext.current
    var editingPayment by remember { mutableStateOf<Payment?>(null) }
    var receiptPayment by remember { mutableStateOf<Payment?>(null) }
    var pendingAction by remember { mutableStateOf<ConfirmationRequest?>(null) }
    var pendingPdf by remember { mutableStateOf<Pair<String, String>?>(null) }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val pending = pendingPdf
        if (uri != null && pending != null) writePdfToUri(context, uri, pending.second)
        pendingPdf = null
    }

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
                        payment.attachments.forEach { attachment ->
                            TextButton(onClick = { runCatching { openAttachment(context, attachment) } }) {
                                Text("Open ${attachment.name}")
                            }
                        }
                    }

                    if (
                        payment.paidDate == null &&
                        onUpdate != null
                    ) {

                        Button(
                            onClick = {
                                editingPayment = payment.copy(
                                    paidDate = System.currentTimeMillis(),
                                    status = "PAID"
                                )
                            }
                        ) {
                            Text("Record Payment")
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

                    if (payment.paidDate != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                val fileName = "MFT-Receipt-${payment.number}-${dateText(payment.paidDate)}.pdf"
                                val content = paymentReceiptText(planName, direction, planTotal, payments, payment, profile)
                                pendingPdf = fileName to content
                                pdfLauncher.launch(fileName)
                            }) { Text("PDF Receipt") }
                            TextButton(onClick = {
                                val fileName = "MFT-Receipt-${payment.number}.pdf"
                                sharePdf(context, fileName, paymentReceiptText(planName, direction, planTotal, payments, payment, profile))
                            }) { Text("Share") }
                        }
                    }
                }
                if (onUpdate == null && payment.paidDate != null) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            val fileName = "MFT-Receipt-${payment.number}-${dateText(payment.paidDate)}.pdf"
                            pendingPdf = fileName to paymentReceiptText(planName, direction, planTotal, payments, payment, profile)
                            pdfLauncher.launch(fileName)
                        }) { Text("Save PDF Receipt") }
                        TextButton(onClick = {
                            sharePdf(context, "MFT-Receipt-${payment.number}.pdf", paymentReceiptText(planName, direction, planTotal, payments, payment, profile))
                        }) { Text("Share") }
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
    var paymentMethod by remember(payment) { mutableStateOf(payment.paymentMethod) }
    var paymentChannel by remember(payment) { mutableStateOf(payment.paymentChannel) }
    var referenceNumber by remember(payment) { mutableStateOf(payment.referenceNumber) }
    var counterparty by remember(payment) { mutableStateOf(payment.counterparty) }
    var accountNumber by remember(payment) { mutableStateOf(payment.accountNumber) }
    var branch by remember(payment) { mutableStateOf(payment.branch) }
    var routingNumber by remember(payment) { mutableStateOf(payment.routingNumber) }
    var methodDetails by remember(payment) { mutableStateOf(payment.methodDetails) }
    var attachments by remember(payment) { mutableStateOf(payment.attachments) }
    var error by remember(payment) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Payment ${payment.number}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                DatePickerField("Due date", dueDate) { dueDate = it }
                if (payment.paidDate != null) {
                    DatePickerField("Paid date", paidDate) { paidDate = it }
                }
                Field("Payment notes", notes) { notes = it }
                ChoiceDropdown(
                    "Payment method",
                    paymentMethod,
                    listOf("Cash", "Bank transfer", "Mobile banking", "Salary deduction", "Card", "Cheque", "Other")
                ) { paymentMethod = it; paymentChannel = ""; counterparty = ""; accountNumber = ""; branch = ""; routingNumber = ""; referenceNumber = ""; methodDetails = "" }
                PaymentMethodDetailsFields(
                    paymentMethod, paymentChannel, { paymentChannel = it }, counterparty, { counterparty = it }, accountNumber, { accountNumber = it },
                    branch, { branch = it }, routingNumber, { routingNumber = it }, referenceNumber, { referenceNumber = it }, methodDetails, { methodDetails = it }
                )
                AttachmentSection(attachments, maxFiles = 3) { attachments = it }
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
                    } else if (parsedPaidDate != null && parsedPaidDate > System.currentTimeMillis()) {
                        error = "Paid date cannot be in the future."
                    } else if (parsedPaidDate != null && paymentMethod == "Not recorded") {
                        error = "Select how this payment was made."
                    } else if (parsedPaidDate != null && paymentMethodValidation(paymentMethod, paymentChannel.ifBlank { if (paymentMethod == "Mobile banking") defaultProvider() else if (paymentMethod == "Bank transfer" || paymentMethod == "Cheque") defaultBank() else "" }, counterparty, accountNumber, referenceNumber, methodDetails).isNotBlank()) {
                        error = paymentMethodValidation(paymentMethod, paymentChannel.ifBlank { if (paymentMethod == "Mobile banking") defaultProvider() else if (paymentMethod == "Bank transfer" || paymentMethod == "Cheque") defaultBank() else "" }, counterparty, accountNumber, referenceNumber, methodDetails)
                    } else if (notes.length > 500 || referenceNumber.length > 100 || counterparty.length > 100) {
                        error = "Notes must be 500 characters or less; reference and party names must be 100 or less."
                    } else {
                        onSave(
                            payment.copy(
                                dueDate = parsedDueDate,
                                paidDate = parsedPaidDate,
                                notes = notes.trim(),
                                paymentMethod = paymentMethod,
                                paymentChannel = paymentChannel.ifBlank { if (paymentMethod == "Mobile banking") defaultProvider() else if (paymentMethod == "Bank transfer" || paymentMethod == "Cheque") defaultBank() else "" },
                                referenceNumber = referenceNumber.trim(),
                                counterparty = counterparty.trim(),
                                attachments = attachments,
                                accountNumber = accountNumber.trim(),
                                branch = branch.trim(),
                                routingNumber = routingNumber.trim(),
                                methodDetails = methodDetails.trim(),
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

private val EmiCategoryOptions = listOf(
    "Electronics", "Appliances", "Furniture", "Vehicle", "Mobile / Computer",
    "Education", "Medical", "Home Improvement", "Other"
)

private val LoanTypeOptions = listOf(
    "Personal Loan", "Bank Loan", "Office Loan", "Salary Loan", "Home Loan",
    "Vehicle Loan", "Education Loan", "Business Loan", "Other"
)

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
    var categoryChoice by remember(existing?.id) {
        mutableStateOf(if ((existing?.category ?: "Electronics") in EmiCategoryOptions.dropLast(1)) existing?.category ?: "Electronics" else "Other")
    }

    var seller by remember {
        mutableStateOf(
            existing?.seller ?: ""
        )
    }

    var financingSource by remember { mutableStateOf(existing?.financingSource ?: "Shop or seller") }
    var receivedMethod by remember { mutableStateOf(existing?.receivedMethod ?: "Direct purchase financing") }
    var agreementReference by remember { mutableStateOf(existing?.agreementReference ?: "") }
    var financingNotes by remember { mutableStateOf(existing?.financingNotes ?: "") }
    var financingChannel by remember { mutableStateOf(existing?.financingChannel ?: "") }
    var financingAccountName by remember { mutableStateOf(existing?.financingAccountName ?: "") }
    var financingAccountNumber by remember { mutableStateOf(existing?.financingAccountNumber ?: "") }
    var financingBranch by remember { mutableStateOf(existing?.financingBranch ?: "") }
    var financingRouting by remember { mutableStateOf(existing?.financingRoutingNumber ?: "") }
    var financingReference by remember { mutableStateOf(existing?.financingReference ?: "") }
    var financingMethodDetails by remember { mutableStateOf(existing?.financingMethodDetails ?: "") }
    var attachments by remember { mutableStateOf(existing?.attachments ?: emptyList()) }

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
        financingSource != (existing?.financingSource ?: "Shop or seller") ||
        receivedMethod != (existing?.receivedMethod ?: "Direct purchase financing") ||
        agreementReference != (existing?.agreementReference ?: "") ||
        financingNotes != (existing?.financingNotes ?: "") ||
        financingChannel != (existing?.financingChannel ?: "") || financingAccountName != (existing?.financingAccountName ?: "") ||
        financingAccountNumber != (existing?.financingAccountNumber ?: "") || financingBranch != (existing?.financingBranch ?: "") ||
        financingRouting != (existing?.financingRoutingNumber ?: "") || financingReference != (existing?.financingReference ?: "") ||
        financingMethodDetails != (existing?.financingMethodDetails ?: "") ||
        attachments != (existing?.attachments ?: emptyList<Attachment>()) ||
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

        ChoiceDropdown("Category", categoryChoice, EmiCategoryOptions) { selected ->
            categoryChoice = selected
            if (selected != "Other") category = selected else if (category in EmiCategoryOptions) category = ""
        }
        if (categoryChoice == "Other") Field("Custom category", category) { category = it }

        Field(
            "Seller / Provider",
            seller
        ) {
            seller = it
        }

        Text("Financing details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ChoiceDropdown(
            "Financing source",
            financingSource,
            listOf("Shop or seller", "Bank", "Finance company", "Employer", "Credit card", "Friend or family", "Other")
        ) { financingSource = it }
        ChoiceDropdown(
            "How item/finance was received",
            receivedMethod,
            listOf("Direct purchase financing", "Cash", "Bank transfer", "Mobile banking", "Salary arrangement", "Other")
        ) {
            receivedMethod = it; financingChannel = ""; financingAccountName = ""; financingAccountNumber = ""
            financingBranch = ""; financingRouting = ""; financingReference = ""; financingMethodDetails = ""
        }
        PaymentMethodDetailsFields(
            receivedMethod, financingChannel, { financingChannel = it }, financingAccountName, { financingAccountName = it },
            financingAccountNumber, { financingAccountNumber = it }, financingBranch, { financingBranch = it },
            financingRouting, { financingRouting = it }, financingReference, { financingReference = it },
            financingMethodDetails, { financingMethodDetails = it }
        )
        Field("Agreement / reference number", agreementReference) { agreementReference = it }
        Field("Financing notes", financingNotes) { financingNotes = it }
        AttachmentSection(attachments, maxFiles = 5) { attachments = it }

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

                    itemName.length > 100 ->
                        "Item name must be 100 characters or less."

                    category.trim().length !in 2..60 || seller.trim().length > 100 ->
                        "Enter a valid category; seller/provider must be 100 characters or less."

                    paymentMethodValidation(receivedMethod, financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails).isNotBlank() ->
                        paymentMethodValidation(receivedMethod, financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails)

                    !purchasePrice.isFinite() || purchasePrice <= 0 || purchasePrice > 999_999_999.99 ->
                        "Enter a valid price."

                    !rate.isFinite() || !enteredInterest.isFinite() || rate !in 0.0..100.0 || enteredInterest < 0 ->
                        "Interest rate must be 0-100 and interest amount cannot be negative."

                    rate > 0 && enteredInterest > 0 ->
                        "Use either interest rate or fixed interest amount, not both."

                    down < 0 ||
                            down >= purchasePrice ->
                        "Check down payment."

                    count !in 1..600 ->
                        "Installments must be between 1 and 600."

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
                                        },
                                notes = oldPaid[number]?.notes ?: "",
                                receiptUri = oldPaid[number]?.receiptUri,
                                paymentMethod = oldPaid[number]?.paymentMethod ?: "Not recorded",
                                paymentChannel = oldPaid[number]?.paymentChannel ?: "",
                                referenceNumber = oldPaid[number]?.referenceNumber ?: "",
                                counterparty = oldPaid[number]?.counterparty ?: "",
                                attachments = oldPaid[number]?.attachments ?: emptyList()
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

                            archived = existing?.archived ?: false,
                            financingSource = financingSource,
                            receivedMethod = receivedMethod,
                            agreementReference = agreementReference.trim(),
                            financingNotes = financingNotes.trim(),
                            financingChannel = financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" },
                            financingAccountName = financingAccountName.trim(), financingAccountNumber = financingAccountNumber.trim(),
                            financingBranch = financingBranch.trim(), financingRoutingNumber = financingRouting.trim(),
                            financingReference = financingReference.trim(), financingMethodDetails = financingMethodDetails.trim(),
                            attachments = attachments
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
    var loanTypeChoice by remember(existing?.id) {
        mutableStateOf(if ((existing?.type ?: "Office Loan") in LoanTypeOptions.dropLast(1)) existing?.type ?: "Office Loan" else "Other")
    }

    var lender by remember {
        mutableStateOf(
            existing?.lender ?: ""
        )
    }

    var financingSource by remember { mutableStateOf(existing?.financingSource ?: "Bank") }
    var receivedMethod by remember { mutableStateOf(existing?.receivedMethod ?: "Bank transfer") }
    var agreementReference by remember { mutableStateOf(existing?.agreementReference ?: "") }
    var financingNotes by remember { mutableStateOf(existing?.financingNotes ?: "") }
    var financingChannel by remember { mutableStateOf(existing?.financingChannel ?: "") }
    var financingAccountName by remember { mutableStateOf(existing?.financingAccountName ?: "") }
    var financingAccountNumber by remember { mutableStateOf(existing?.financingAccountNumber ?: "") }
    var financingBranch by remember { mutableStateOf(existing?.financingBranch ?: "") }
    var financingRouting by remember { mutableStateOf(existing?.financingRoutingNumber ?: "") }
    var financingReference by remember { mutableStateOf(existing?.financingReference ?: "") }
    var financingMethodDetails by remember { mutableStateOf(existing?.financingMethodDetails ?: "") }
    var attachments by remember { mutableStateOf(existing?.attachments ?: emptyList()) }

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
                while (remaining > 0.0001 && size < 600) {
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
        financingSource != (existing?.financingSource ?: "Bank") ||
        receivedMethod != (existing?.receivedMethod ?: "Bank transfer") ||
        agreementReference != (existing?.agreementReference ?: "") ||
        financingNotes != (existing?.financingNotes ?: "") ||
        financingChannel != (existing?.financingChannel ?: "") || financingAccountName != (existing?.financingAccountName ?: "") ||
        financingAccountNumber != (existing?.financingAccountNumber ?: "") || financingBranch != (existing?.financingBranch ?: "") ||
        financingRouting != (existing?.financingRoutingNumber ?: "") || financingReference != (existing?.financingReference ?: "") ||
        financingMethodDetails != (existing?.financingMethodDetails ?: "") ||
        attachments != (existing?.attachments ?: emptyList<Attachment>()) ||
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

        ChoiceDropdown("Loan type", loanTypeChoice, LoanTypeOptions) { selected ->
            loanTypeChoice = selected
            if (selected != "Other") type = selected else if (type in LoanTypeOptions) type = ""
        }
        if (loanTypeChoice == "Other") Field("Custom loan type", type) { type = it }

        Field(
            "Lender",
            lender
        ) {
            lender = it
        }

        Text("Financing details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ChoiceDropdown(
            "Financing source",
            financingSource,
            listOf("Bank", "Finance company", "Employer", "Shop or seller", "Credit card", "Friend or family", "Other")
        ) { financingSource = it }
        ChoiceDropdown(
            "How the loan was received",
            receivedMethod,
            listOf("Cash", "Bank transfer", "Mobile banking", "Salary arrangement", "Direct financing", "Other")
        ) {
            receivedMethod = it; financingChannel = ""; financingAccountName = ""; financingAccountNumber = ""
            financingBranch = ""; financingRouting = ""; financingReference = ""; financingMethodDetails = ""
        }
        PaymentMethodDetailsFields(
            receivedMethod, financingChannel, { financingChannel = it }, financingAccountName, { financingAccountName = it },
            financingAccountNumber, { financingAccountNumber = it }, financingBranch, { financingBranch = it },
            financingRouting, { financingRouting = it }, financingReference, { financingReference = it },
            financingMethodDetails, { financingMethodDetails = it }
        )
        Field("Agreement / reference number", agreementReference) { agreementReference = it }
        Field("Financing notes", financingNotes) { financingNotes = it }
        AttachmentSection(attachments, maxFiles = 5) { attachments = it }

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

                    name.trim().length > 100 ->
                        "Loan name must be 100 characters or less."

                    type.trim().length !in 2..60 || lender.trim().length > 100 ->
                        "Enter a valid loan type; lender must be 100 characters or less."

                    paymentMethodValidation(receivedMethod, financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails).isNotBlank() ->
                        paymentMethodValidation(receivedMethod, financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails)

                    !principalAmount.isFinite() || principalAmount <= 0 || principalAmount > 999_999_999.99 ->
                        "Enter principal."

                    !interestRate.isFinite() || !enteredInterest.isFinite() || interestRate !in 0.0..100.0 || enteredInterest < 0 ->
                        "Interest rate must be 0-100 and interest amount cannot be negative."

                    interestRate > 0 && enteredInterest > 0 ->
                        "Use either interest rate or fixed interest amount, not both."

                    repaymentMode == "EQUAL" && count !in 1..600 ->
                        "Installments must be between 1 and 600."

                    repaymentMode == "FLEXIBLE" && (flexibleMonthly <= 0 || scheduledAmounts.size >= 600) ->
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
                                        },
                                notes = oldPaid[number]?.notes ?: "",
                                receiptUri = oldPaid[number]?.receiptUri,
                                paymentMethod = oldPaid[number]?.paymentMethod ?: "Not recorded",
                                paymentChannel = oldPaid[number]?.paymentChannel ?: "",
                                referenceNumber = oldPaid[number]?.referenceNumber ?: "",
                                counterparty = oldPaid[number]?.counterparty ?: "",
                                attachments = oldPaid[number]?.attachments ?: emptyList()
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

                            archived = existing?.archived ?: false,
                            financingSource = financingSource,
                            receivedMethod = receivedMethod,
                            agreementReference = agreementReference.trim(),
                            financingNotes = financingNotes.trim(),
                            financingChannel = financingChannel.ifBlank { if (receivedMethod == "Mobile banking") defaultProvider() else if (receivedMethod == "Bank transfer") defaultBank() else "" },
                            financingAccountName = financingAccountName.trim(), financingAccountNumber = financingAccountNumber.trim(),
                            financingBranch = financingBranch.trim(), financingRoutingNumber = financingRouting.trim(),
                            financingReference = financingReference.trim(), financingMethodDetails = financingMethodDetails.trim(),
                            attachments = attachments
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
    initialDirection: String = "I Owe",
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
            existing?.direction ?: initialDirection
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

    var reason by remember { mutableStateOf(existing?.reason ?: "") }
    var debtDate by remember { mutableStateOf(expenseDateText(existing?.debtDate ?: System.currentTimeMillis())) }
    var dueDate by remember { mutableStateOf(existing?.dueDate?.let(::expenseDateText) ?: "") }
    var receivedOrGivenMethod by remember { mutableStateOf(existing?.receivedOrGivenMethod ?: "Cash") }
    var debtReference by remember { mutableStateOf(existing?.referenceNumber ?: "") }
    var financingChannel by remember { mutableStateOf(existing?.financingChannel ?: "") }
    var financingAccountName by remember { mutableStateOf(existing?.financingAccountName ?: "") }
    var financingAccountNumber by remember { mutableStateOf(existing?.financingAccountNumber ?: "") }
    var financingBranch by remember { mutableStateOf(existing?.financingBranch ?: "") }
    var financingRouting by remember { mutableStateOf(existing?.financingRoutingNumber ?: "") }
    var financingReference by remember { mutableStateOf(existing?.financingReference ?: "") }
    var financingMethodDetails by remember { mutableStateOf(existing?.financingMethodDetails ?: "") }
    var attachments by remember { mutableStateOf(existing?.attachments ?: emptyList()) }

    var payment by remember {
        mutableStateOf("")
    }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var paymentChannel by remember { mutableStateOf("") }
    var paymentReference by remember { mutableStateOf("") }
    var paymentNotes by remember { mutableStateOf("") }
    var paymentAttachments by remember { mutableStateOf(emptyList<Attachment>()) }
    var showRequestDialog by remember { mutableStateOf(false) }
    var pendingDebtUpdate by remember { mutableStateOf<ConfirmationRequest?>(null) }

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
        reason != (existing?.reason ?: "") ||
        debtDate != expenseDateText(existing?.debtDate ?: System.currentTimeMillis()) ||
        dueDate != (existing?.dueDate?.let(::expenseDateText) ?: "") ||
        receivedOrGivenMethod != (existing?.receivedOrGivenMethod ?: "Cash") ||
        debtReference != (existing?.referenceNumber ?: "") ||
        financingChannel != (existing?.financingChannel ?: "") || financingAccountName != (existing?.financingAccountName ?: "") ||
        financingAccountNumber != (existing?.financingAccountNumber ?: "") || financingBranch != (existing?.financingBranch ?: "") ||
        financingRouting != (existing?.financingRoutingNumber ?: "") || financingReference != (existing?.financingReference ?: "") ||
        financingMethodDetails != (existing?.financingMethodDetails ?: "") ||
        attachments != (existing?.attachments ?: emptyList<Attachment>()) ||
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

        if (existing != null && existing.payments.isEmpty() && existing.paymentRequests.isEmpty() && !viewOnly) {
            ChoiceDropdown("Direction", direction, listOf("I Owe", "Owed to Me")) { direction = it }
        } else {
            InfoRow("Direction", direction)
        }

        Text(
            if (direction == "I Owe") "You need to pay this person or organization." else "This person or organization needs to pay you.",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
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
        Field("Reason for debt", reason) { reason = it }
        DatePickerField("Debt date", debtDate) { debtDate = it }
        DatePickerField("Due date (optional)", dueDate) { dueDate = it }
        ChoiceDropdown(
            if (direction == "I Owe") "How you received it" else "How you gave it",
            receivedOrGivenMethod,
            listOf("Cash", "Bank transfer", "Mobile banking", "Goods or service", "Other")
        ) {
            receivedOrGivenMethod = it; financingChannel = ""; financingAccountName = ""; financingAccountNumber = ""
            financingBranch = ""; financingRouting = ""; financingReference = ""; financingMethodDetails = ""
        }
        PaymentMethodDetailsFields(
            receivedOrGivenMethod, financingChannel, { financingChannel = it }, financingAccountName, { financingAccountName = it },
            financingAccountNumber, { financingAccountNumber = it }, financingBranch, { financingBranch = it },
            financingRouting, { financingRouting = it }, financingReference, { financingReference = it },
            financingMethodDetails, { financingMethodDetails = it }
        )
        Field("Agreement / reference number", debtReference) { debtReference = it }
        AttachmentSection(attachments, maxFiles = 5) { attachments = it }

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
                OutlinedButton(
                    onClick = {
                        val updatedAmount = amount.toDoubleOrNull() ?: 0.0
                        val financingValidation = paymentMethodValidation(receivedOrGivenMethod, financingChannel.ifBlank { if (receivedOrGivenMethod == "Mobile banking") defaultProvider() else if (receivedOrGivenMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails)
                        if (name.isBlank() || name.trim().length > 100 || !updatedAmount.isFinite() || updatedAmount <= 0 || updatedAmount > 999_999_999.99 || updatedAmount + 0.005 < paid || notes.length > 500 || debtReference.length > 100 || financingValidation.isNotBlank()) {
                            paymentError = "Enter a valid name and an original amount not below the recorded total."
                        } else {
                            pendingDebtUpdate = ConfirmationRequest(
                                title = "Update Debt?",
                                message = "Save the changed direction, financial details, notes, and documents for ${existing.name}?",
                                confirmLabel = "Update",
                                onConfirm = {
                                    viewModel.updateDebt(
                                        existing.copy(
                                            name = name.trim(),
                                            direction = direction,
                                            originalAmount = updatedAmount,
                                            debtDate = parseExpenseDate(debtDate) ?: existing.debtDate,
                                            dueDate = if (dueDate.isBlank()) null else parseExpenseDate(dueDate),
                                            notes = notes.trim(),
                                            reason = reason.trim(),
                                            receivedOrGivenMethod = receivedOrGivenMethod,
                                            referenceNumber = debtReference.trim(),
                                            financingChannel = financingChannel.ifBlank { if (receivedOrGivenMethod == "Mobile banking") defaultProvider() else if (receivedOrGivenMethod == "Bank transfer") defaultBank() else "" },
                                            financingAccountName = financingAccountName.trim(), financingAccountNumber = financingAccountNumber.trim(),
                                            financingBranch = financingBranch.trim(), financingRoutingNumber = financingRouting.trim(),
                                            financingReference = financingReference.trim(), financingMethodDetails = financingMethodDetails.trim(),
                                            attachments = attachments
                                        )
                                    )
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Update Debt Details") }
            }

            if (false && !viewOnly) {
            Field(
                if (direction == "I Owe") "New payment amount" else "New received amount",
                payment
            ) {
                payment = it
                paymentError = ""
            }
            ChoiceDropdown(
                "Payment method",
                paymentMethod,
                listOf("Cash", "Bank transfer", "Mobile banking", "Salary deduction", "Card", "Cheque", "Other")
            ) { paymentMethod = it }
            if (paymentMethod == "Mobile banking") {
                ChoiceDropdown("Mobile banking provider", paymentChannel.ifBlank { defaultProvider() }, availableProviders()) { paymentChannel = it }
            } else if (paymentMethod == "Bank transfer" || paymentMethod == "Salary deduction") {
                Field(if (paymentMethod == "Bank transfer") "Bank name" else "Employer / salary month", paymentChannel) { paymentChannel = it }
            }
            Field("Transaction / reference ID", paymentReference) { paymentReference = it }
            Field("Payment notes", paymentNotes) { paymentNotes = it }
            AttachmentSection(paymentAttachments, maxFiles = 3) { paymentAttachments = it }

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
                                paymentAmount,
                                method = paymentMethod,
                                channel = paymentChannel,
                                reference = paymentReference.trim(),
                                counterparty = existing.name,
                                notes = paymentNotes.trim(),
                                attachments = paymentAttachments
                            )

                            payment = ""
                            paymentReference = ""
                            paymentNotes = ""
                            paymentAttachments = emptyList()
                            paymentError = ""
                        }
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    if (direction == "I Owe") "Record Payment" else "Record Received Amount"
                )
            }

            if (direction == "Owed to Me" && !viewOnly) {
                OutlinedButton(onClick = { showRequestDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Create Payment Request")
                }
            }

            if (existing.paymentRequests.isNotEmpty()) {
                Text("Payment Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                existing.paymentRequests.sortedByDescending { it.createdDate }.forEach { request ->
                    PaymentRequestCard(existing, request, viewModel.data.receiptProfile)
                }
            }
            }

            if (viewOnly && existing.paymentRequests.isNotEmpty()) {
                Text("Payment Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                existing.paymentRequests.sortedByDescending { it.createdDate }.forEach { request ->
                    PaymentRequestCard(existing, request, viewModel.data.receiptProfile)
                }
            }

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
                    val selectedDebtDate = parseExpenseDate(debtDate)
                    val selectedDueDate = if (dueDate.isBlank()) null else parseExpenseDate(dueDate)
                    val financingValidation = paymentMethodValidation(receivedOrGivenMethod, financingChannel.ifBlank { if (receivedOrGivenMethod == "Mobile banking") defaultProvider() else if (receivedOrGivenMethod == "Bank transfer") defaultBank() else "" }, financingAccountName, financingAccountNumber, financingReference, financingMethodDetails)

                    if (
                        name.isBlank() || name.trim().length > 100 || notes.length > 500 || debtReference.length > 100 ||
                        !originalAmount.isFinite() || originalAmount <= 0 || originalAmount > 999_999_999.99 ||
                        !previousAmount.isFinite() || previousAmount < 0 ||
                        previousAmount > originalAmount || selectedDebtDate == null || (dueDate.isNotBlank() && selectedDueDate == null) ||
                        (selectedDueDate != null && selectedDebtDate != null && selectedDueDate < selectedDebtDate) || financingValidation.isNotBlank()
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
                                debtDate = selectedDebtDate,
                                dueDate =
                                    selectedDueDate,
                                notes = notes.trim(),
                                reason = reason.trim(),
                                receivedOrGivenMethod = receivedOrGivenMethod,
                                referenceNumber = debtReference.trim(),
                                financingChannel = financingChannel.ifBlank { if (receivedOrGivenMethod == "Mobile banking") defaultProvider() else if (receivedOrGivenMethod == "Bank transfer") defaultBank() else "" },
                                financingAccountName = financingAccountName.trim(), financingAccountNumber = financingAccountNumber.trim(),
                                financingBranch = financingBranch.trim(), financingRoutingNumber = financingRouting.trim(),
                                financingReference = financingReference.trim(), financingMethodDetails = financingMethodDetails.trim(),
                                attachments = attachments,
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

    if (showRequestDialog && existing != null) {
        PaymentRequestDialog(
            debt = existing,
            onSave = {
                viewModel.addPaymentRequest(existing.id, it)
                showRequestDialog = false
            },
            onDismiss = { showRequestDialog = false }
        )
    }
    pendingDebtUpdate?.let { request ->
        ConfirmationDialog(request) { pendingDebtUpdate = null }
    }
}


// ============================================================
// EXPENSE FORM
// ============================================================

private fun paymentRequestText(debt: Debt, request: PaymentRequest, profile: ReceiptProfile): String = buildString {
    appendLine("MY FINANCE TRACKER")
    appendLine("PAYMENT REQUEST")
    appendLine()
    appendLine("Request number: ${request.requestNumber}")
    appendLine("Request date: ${dateText(request.createdDate)}")
    request.dueDate?.let { appendLine("Due date: ${dateText(it)}") }
    appendLine("Requested by: ${profile.fullName.ifBlank { "Not specified" }}")
    if (profile.phone.isNotBlank()) appendLine("Phone: ${profile.phone}")
    if (profile.email.isNotBlank()) appendLine("Email: ${profile.email}")
    if (profile.address.isNotBlank()) appendLine("Address: ${profile.address}")
    appendLine("Payment requested from: ${debt.name}")
    appendLine("Reason: ${debt.reason.ifBlank { debt.notes }}")
    appendLine("Original amount: ${money(debt.originalAmount)}")
    appendLine("Amount already received: ${money(debtPaidAmount(debt))}")
    appendLine("Amount requested: ${money(request.amount)}")
    appendLine("Amount received for request: ${money(request.receivedAmount)}")
    appendLine("Request status: ${request.status}")
    appendLine("Remaining to receive: ${money(debtRemainingAmount(debt))}")
    appendLine("Preferred method: ${request.paymentMethod}")
    if (request.paymentChannel.isNotBlank()) appendLine("Provider / bank: ${request.paymentChannel}")
    if (request.accountName.isNotBlank()) appendLine("Account holder: ${request.accountName}")
    if (request.accountNumber.isNotBlank()) appendLine("Account / mobile number: ${request.accountNumber}")
    if (request.branch.isNotBlank()) appendLine("Branch: ${request.branch}")
    if (request.routingNumber.isNotBlank()) appendLine("Routing number: ${request.routingNumber}")
    if (request.referenceNumber.isNotBlank()) appendLine("Reference: ${request.referenceNumber}")
    if (request.methodDetails.isNotBlank()) appendLine("Method details: ${request.methodDetails}")
    if (request.paymentInstructions.isNotBlank()) appendLine("Payment instructions: ${request.paymentInstructions}")
    if (request.message.isNotBlank()) appendLine("Message: ${request.message}")
    appendLine()
    appendLine("This is a personal payment request generated from the issuer's records. It is not a bank statement, legal judgment, or tax invoice.")
    appendLine("Generated by My Finance Tracker")
    appendLine("Powered by Md. Zahid Alam")
    profile.signature?.let { appendLine("[[SIGNATURE:${it.contentBase64}]]") }
}

@Composable
fun PaymentRequestCard(
    debt: Debt,
    request: PaymentRequest,
    profile: ReceiptProfile,
    onEdit: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var pendingText by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null && pendingText.isNotBlank()) writePdfToUri(context, uri, pendingText)
        pendingText = ""
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(request.requestNumber, fontWeight = FontWeight.Bold)
            Text("Requested ${money(request.amount)} • ${dateText(request.createdDate)}")
            request.dueDate?.let { Text("Due ${dateText(it)}") }
            if (request.receivedAmount > 0) Text("Received ${money(request.receivedAmount)}")
            Text(request.status, color = if (request.status == "CANCELLED") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                TextButton(onClick = {
                    pendingText = paymentRequestText(debt, request, profile)
                    launcher.launch("${request.requestNumber}.pdf")
                }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 2.dp)) { Text("Save PDF", fontSize = 11.sp, maxLines = 1) }
                TextButton(onClick = { sharePdf(context, "${request.requestNumber}.pdf", paymentRequestText(debt, request, profile)) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 2.dp)) { Text("Share", fontSize = 11.sp, maxLines = 1) }
                if (request.status in listOf("UNPAID", "PARTIALLY PAID")) {
                    onEdit?.let { TextButton(onClick = it, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 2.dp)) { Text("Edit", fontSize = 11.sp, maxLines = 1) } }
                    onCancel?.let { TextButton(onClick = it, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 2.dp)) { Text("Cancel", color = MaterialTheme.colorScheme.error, fontSize = 11.sp, maxLines = 1) } }
                }
            }
        }
    }
}

@Composable
fun PaymentRequestDialog(debt: Debt, existing: PaymentRequest? = null, onSave: (PaymentRequest) -> Unit, onDismiss: () -> Unit) {
    val maximum = availableRequestAmount(debt, existing?.id ?: "") + (existing?.receivedAmount ?: 0.0)
    var amount by remember { mutableStateOf((existing?.amount ?: maximum).toString()) }
    var dueDate by remember { mutableStateOf(expenseDateText(existing?.dueDate ?: System.currentTimeMillis())) }
    var method by remember { mutableStateOf(existing?.paymentMethod ?: "Mobile banking") }
    var channel by remember { mutableStateOf(existing?.paymentChannel ?: "") }
    var accountName by remember { mutableStateOf(existing?.accountName ?: "") }
    var accountNumber by remember { mutableStateOf(existing?.accountNumber ?: "") }
    var branch by remember { mutableStateOf(existing?.branch ?: "") }
    var routingNumber by remember { mutableStateOf(existing?.routingNumber ?: "") }
    var referenceNumber by remember { mutableStateOf(existing?.referenceNumber ?: "") }
    var methodDetails by remember { mutableStateOf(existing?.methodDetails ?: "") }
    var instructions by remember { mutableStateOf(existing?.paymentInstructions ?: "") }
    var message by remember { mutableStateOf(existing?.message ?: "") }
    var error by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Create Payment Request" else "Edit Payment Request") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Request money from ${debt.name}")
                Text("Available to request: ${money(maximum)}", color = MaterialTheme.colorScheme.primary)
                Field("Requested amount", amount) { amount = it }
                DatePickerField("Due date", dueDate) { dueDate = it }
                ChoiceDropdown("Preferred payment method", method, listOf("Cash", "Bank transfer", "Mobile banking", "Cheque", "Other")) {
                    method = it; channel = ""; accountName = ""; accountNumber = ""; branch = ""; routingNumber = ""; referenceNumber = ""; methodDetails = ""
                }
                PaymentMethodDetailsFields(
                    method, channel, { channel = it }, accountName, { accountName = it }, accountNumber, { accountNumber = it },
                    branch, { branch = it }, routingNumber, { routingNumber = it }, referenceNumber, { referenceNumber = it }, methodDetails, { methodDetails = it }
                )
                Field("Payment instructions", instructions) { instructions = it }
                Field("Message", message) { message = it }
                if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val value = amount.toDoubleOrNull() ?: 0.0
                val parsedDue = parseExpenseDate(dueDate)
                error = when {
                    !value.isFinite() || value <= 0 -> "Enter a valid requested amount."
                    value > maximum + 0.005 -> "Request cannot exceed the available receivable balance."
                    value < (existing?.receivedAmount ?: 0.0) -> "Request amount cannot be below the amount already received."
                    parsedDue == null -> "Enter a valid due date."
                    parsedDue < Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis -> "Due date cannot be in the past."
                    method in listOf("Bank transfer", "Mobile banking") && instructions.isBlank() -> "Enter payment instructions for the selected method."
                    paymentMethodValidation(method, channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, accountName, accountNumber, referenceNumber, methodDetails).isNotBlank() -> paymentMethodValidation(method, channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" }, accountName, accountNumber, referenceNumber, methodDetails)
                    instructions.length > 300 || message.length > 500 -> "Instructions must be 300 characters or less and message 500 or less."
                    else -> ""
                }
                if (error.isBlank()) {
                    val stamp = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
                    onSave(
                        PaymentRequest(
                            id = existing?.id ?: UUID.randomUUID().toString(),
                            requestNumber = existing?.requestNumber ?: "MFT-REQ-$stamp-${UUID.randomUUID().toString().take(4).uppercase(Locale.US)}",
                            createdDate = existing?.createdDate ?: System.currentTimeMillis(),
                            dueDate = parsedDue,
                            amount = value,
                            paymentMethod = method,
                            paymentInstructions = instructions.trim(),
                            message = message.trim(),
                            status = existing?.status ?: "UNPAID",
                            receivedAmount = existing?.receivedAmount ?: 0.0,
                            paymentChannel = channel.ifBlank { if (method == "Mobile banking") defaultProvider() else if (method == "Bank transfer" || method == "Cheque") defaultBank() else "" },
                            accountName = accountName.trim(),
                            accountNumber = accountNumber.trim(),
                            branch = branch.trim(),
                            routingNumber = routingNumber.trim(),
                            referenceNumber = referenceNumber.trim(),
                            methodDetails = methodDetails.trim()
                        )
                    )
                }
            }) { Text(if (existing == null) "Create" else "Save Changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

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
    var attachments by remember { mutableStateOf(existing?.attachments ?: emptyList()) }
    var error by remember { mutableStateOf("") }

    val hasUnsavedChanges =
        title != (existing?.title ?: "") ||
        category != (existing?.category ?: "Food") ||
        amount != (existing?.amount?.toString() ?: "") ||
        date != initialDate ||
        notes != (existing?.notes ?: "") ||
        attachments != (existing?.attachments ?: emptyList<Attachment>())

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
        AttachmentSection(attachments, maxFiles = 2) { attachments = it }

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
                    title.trim().length > 100 -> "Expense name must be 100 characters or less."
                    !expenseAmount.isFinite() || expenseAmount <= 0 || expenseAmount > 999_999_999.99 -> "Enter a valid amount greater than zero."
                    expenseDate == null -> "Enter a valid date as DD-MM-YYYY."
                    expenseDate > System.currentTimeMillis() -> "Expense date cannot be in the future."
                    notes.length > 500 -> "Notes must be 500 characters or less."
                    else -> ""
                }

                if (error.isEmpty() && expenseDate != null) {
                    val expense = Expense(
                        id = existing?.id ?: UUID.randomUUID().toString(),
                        title = title.trim(),
                        category = category,
                        amount = expenseAmount,
                        date = expenseDate,
                        notes = notes.trim(),
                        attachments = attachments
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

private fun attachmentName(context: Context, uri: android.net.Uri): String {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) return cursor.getString(0) ?: "Document"
    }
    return uri.lastPathSegment ?: "Document"
}

private fun readAttachment(context: Context, uri: android.net.Uri): Attachment {
    val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
        val output = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        var total = 0
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            total += count
            require(total <= 5 * 1024 * 1024) { "Each document must be 5 MB or smaller." }
            output.write(buffer, 0, count)
        }
        output.toByteArray()
    } ?: throw IllegalArgumentException("Unable to read the selected document.")
    return Attachment(
        name = attachmentName(context, uri),
        mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream",
        contentBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    )
}

private fun openAttachment(context: Context, attachment: Attachment) {
    val safeName = attachment.name.replace(Regex("[^A-Za-z0-9._-]"), "_").ifBlank { "document" }
    val folder = File(context.cacheDir, "shared_documents").apply { mkdirs() }
    folder.listFiles()?.forEach { it.delete() }
    val file = File(folder, safeName)
    file.writeBytes(Base64.decode(attachment.contentBase64, Base64.NO_WRAP))
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
    context.startActivity(
        Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, attachment.mimeType)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    )
}

private fun sharePdf(context: Context, fileName: String, text: String) {
    val folder = File(context.cacheDir, "shared_documents").apply { mkdirs() }
    folder.listFiles()?.forEach { it.delete() }
    val safeName = fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
    val file = File(folder, safeName).apply { createNewFile() }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
    writePdfToUri(context, uri, text)
    context.startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SEND)
                .setType("application/pdf")
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
            "Share PDF"
        )
    )
}

@Composable
fun AttachmentSection(
    attachments: List<Attachment>,
    maxFiles: Int,
    title: String = "Supporting documents (optional)",
    mimeTypes: Array<String> = arrayOf("image/*", "application/pdf"),
    buttonLabel: String = "Attach image or PDF",
    onChange: (List<Attachment>) -> Unit
) {
    val context = LocalContext.current
    var error by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            runCatching {
                val available = (maxFiles - attachments.size).coerceAtLeast(0)
                require(available > 0) { "Maximum $maxFiles documents allowed." }
                attachments + uris.take(available).map { readAttachment(context, it) }
            }.onSuccess {
                onChange(it)
                error = ""
            }.onFailure { error = it.message ?: "Could not attach the document." }
        }
    }

    Text(title, fontWeight = FontWeight.Bold)
    attachments.forEach { attachment ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(attachment.name, modifier = Modifier.weight(1f))
                TextButton(onClick = { runCatching { openAttachment(context, attachment) } }) { Text("Open") }
                if (!LocalFormReadOnly.current) {
                    TextButton(onClick = { onChange(attachments.filterNot { it.id == attachment.id }) }) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
    if (!LocalFormReadOnly.current && attachments.size < maxFiles) {
        OutlinedButton(
            onClick = { launcher.launch(mimeTypes) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("$buttonLabel (${attachments.size}/$maxFiles)") }
    }
    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    Text("Documents are included inside encrypted app data and encrypted backups.", style = MaterialTheme.typography.bodySmall)
}

@Composable
fun SettingsScreen(
    onChangePassword: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onLock: () -> Unit,
    onAbout: () -> Unit,
    onReceiptProfile: () -> Unit,
    onCountry: () -> Unit,
    themeMode: String,
    onThemeChange: (String) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    country: String,
    currencyCode: String,
    currencySymbol: String,
    onRegionChange: (String, String, String) -> Unit,
    onCustomPaymentListsChange: (List<String>, List<String>) -> Unit
) {
    FormColumn("Settings") {
        Text("Personalize your experience and protect your local records.", color = MaterialTheme.colorScheme.onSurfaceVariant)

        PremiumSectionHeader("Language and appearance")
        ChoiceDropdown("Language", if (language == "BN") "Bangla" else "English", listOf("English", "Bangla")) {
            onLanguageChange(if (it == "Bangla") "BN" else "EN")
        }

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

        DetailNavigationButton("Country and currency", "$country • $currencyCode ($currencySymbol)", onCountry)

        PremiumSectionHeader("Security and local data")
        DetailNavigationButton("Change Password", "Update the password used to unlock this app", onChangePassword)

        DetailNavigationButton("Export Encrypted Backup", "Create a password-protected copy of records and documents", onBackup)

        DetailNavigationButton("Restore Backup", "Restore a trusted My Finance Tracker backup", onRestore)

        PremiumSectionHeader("Documents and app")
        DetailNavigationButton("Receipt Profile", "Your identity and signature on generated documents", onReceiptProfile)

        DetailNavigationButton("About My Finance Tracker", "Version, ownership and privacy information", onAbout)

        Button(
            onClick = onLock,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Lock App")
        }

        Text(
            "Encrypted backup protects all records and attached documents with a password you choose.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CountrySettingsScreen(
    country: String,
    currencyCode: String,
    currencySymbol: String,
    onRegionChange: (String, String, String) -> Unit,
    onCustomPaymentListsChange: (List<String>, List<String>) -> Unit,
    done: () -> Unit
) {
    val initialCountry = remember(country) {
        CountryCatalog.findByName(country) ?: CountryCatalog.findByName("Bangladesh") ?: CountryCatalog.all.first()
    }
    var selectedCountry by remember(country) { mutableStateOf(initialCountry) }
    var draftCurrencyCode by remember(currencyCode) { mutableStateOf(currencyCode) }
    var draftCurrencySymbol by remember(currencySymbol) { mutableStateOf(currencySymbol) }
    var customBanks by remember { mutableStateOf(AppLocaleState.customBanks) }
    var customProviders by remember { mutableStateOf(AppLocaleState.customProviders) }
    var newBank by remember { mutableStateOf("") }
    var newProvider by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var showRegionWarning by remember { mutableStateOf(false) }

    FormColumn("Country", onBack = done) {
        Text("Country and currency", fontWeight = FontWeight.Bold)
        SearchableCountryPicker(selectedCountry, LocalAppLanguage.current) {
            selectedCountry = it
            if (it.currencyCode.isNotBlank()) draftCurrencyCode = it.currencyCode
            if (it.currencySymbol.isNotBlank()) draftCurrencySymbol = it.currencySymbol
            error = ""
        }
        Text("Default currency is filled automatically. You may change it for a special account or territory.", style = MaterialTheme.typography.bodySmall)
        Field("Currency code (for example USD)", draftCurrencyCode) { draftCurrencyCode = it.uppercase().take(3); error = "" }
        Field("Currency symbol", draftCurrencySymbol) { draftCurrencySymbol = it.take(4); error = "" }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        OutlinedButton(onClick = {
            error = when {
                draftCurrencyCode.length != 3 || !draftCurrencyCode.all { it.isLetter() } -> "Currency code must contain three letters."
                draftCurrencySymbol.isBlank() -> "Enter a currency symbol."
                else -> ""
            }
            if (error.isBlank()) showRegionWarning = true
        }, modifier = Modifier.fillMaxWidth()) { Text("Apply country and currency") }
        Text("Changing currency changes the displayed symbol only; existing amounts are not converted.", style = MaterialTheme.typography.bodySmall)

        Text("My payment institutions", fontWeight = FontWeight.Bold)
        Text(if (selectedCountry.name == "Bangladesh") "Bangladesh banks and mobile banking services are already included. Add any extra services below." else "Add only the banks and payment services you use. They stay on this device.")
        Field("Add bank", newBank) { newBank = it }
        OutlinedButton(onClick = {
            val value = newBank.trim()
            if (value.length in 2..100 && customBanks.none { it.equals(value, true) }) { customBanks = customBanks + value; newBank = ""; onCustomPaymentListsChange(customBanks, customProviders) }
        }, modifier = Modifier.fillMaxWidth()) { Text("Add bank") }
        customBanks.forEach { bank -> TextButton(onClick = { customBanks = customBanks - bank; onCustomPaymentListsChange(customBanks, customProviders) }) { Text("$bank  •  Remove") } }
        Field("Add mobile banking provider", newProvider) { newProvider = it }
        OutlinedButton(onClick = {
            val value = newProvider.trim()
            if (value.length in 2..100 && customProviders.none { it.equals(value, true) }) { customProviders = customProviders + value; newProvider = ""; onCustomPaymentListsChange(customBanks, customProviders) }
        }, modifier = Modifier.fillMaxWidth()) { Text("Add provider") }
        customProviders.forEach { provider -> TextButton(onClick = { customProviders = customProviders - provider; onCustomPaymentListsChange(customBanks, customProviders) }) { Text("$provider  •  Remove") } }
    }
    if (showRegionWarning) ConfirmationDialog(
        request = ConfirmationRequest(
            title = "Change country and currency?",
            message = "Existing money values will not be converted. Only the country, currency code, symbol, and available payment choices will change.",
            confirmLabel = "Change",
            onConfirm = { onRegionChange(selectedCountry.name, draftCurrencyCode, draftCurrencySymbol) }
        ),
        onDismiss = { showRegionWarning = false }
    )
}

@Composable
fun ReceiptProfileForm(existing: ReceiptProfile, onSave: (ReceiptProfile) -> Unit, done: () -> Unit) {
    var fullName by remember { mutableStateOf(existing.fullName) }
    var phone by remember { mutableStateOf(existing.phone) }
    var email by remember { mutableStateOf(existing.email) }
    var address by remember { mutableStateOf(existing.address) }
    var signature by remember { mutableStateOf(existing.signature?.let { listOf(it) } ?: emptyList()) }
    var error by remember { mutableStateOf("") }
    val changed = fullName != existing.fullName || phone != existing.phone || email != existing.email || address != existing.address || signature.firstOrNull() != existing.signature
    FormColumn("Receipt Profile", onBack = done, hasUnsavedChanges = changed) {
        Text("This identity appears as the issuer on payment receipts and payment requests. App ownership remains separate.")
        Field("Full name", fullName) { fullName = it }
        Field("Phone (optional)", phone) { phone = it }
        Field("Email (optional)", email) { email = it }
        Field("Address (optional)", address) { address = it }
        Text("Signature (optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Attach a PNG or JPEG signature image. It is stored with your encrypted app data and added to generated receipts and payment requests.", style = MaterialTheme.typography.bodySmall)
        AttachmentSection(
            signature,
            maxFiles = 1,
            title = "Signature image (optional)",
            mimeTypes = arrayOf("image/*"),
            buttonLabel = "Attach signature image"
        ) { selected ->
            signature = selected.filter { it.mimeType in listOf("image/png", "image/jpeg", "image/jpg", "image/webp") }.take(1)
            if (selected.isNotEmpty() && signature.isEmpty()) error = "Signature must be a PNG, JPEG, or WebP image."
        }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        Button(
            onClick = {
                val cleanPhone = phone.trim()
                val cleanEmail = email.trim()
                val phoneDigits = cleanPhone.filter { it.isDigit() }
                error = when {
                    fullName.isBlank() -> "Enter your full name."
                    fullName.trim().length !in 2..100 -> "Full name must be between 2 and 100 characters."
                    cleanPhone.isNotBlank() && (!Regex("^\\+?[0-9][0-9 -]*$").matches(cleanPhone) || phoneDigits.length !in 7..15) -> "Enter a valid phone number containing 7-15 digits."
                    cleanEmail.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() -> "Enter a valid email address."
                    address.length > 250 -> "Address must be 250 characters or less."
                    else -> ""
                }
                if (error.isBlank()) {
                    onSave(ReceiptProfile(fullName.trim(), cleanPhone, cleanEmail, address.trim(), signature.firstOrNull()))
                    done()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Receipt Profile") }
    }
}

@Composable
fun AboutScreen(done: () -> Unit) {
    FormColumn(title = "About", onBack = done) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(com.mdzahidalam.myfinancetracker.R.drawable.app_logo),
                contentDescription = "My Finance Tracker logo",
                modifier = Modifier.size(112.dp)
            )
            Text("My Finance Tracker", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Version 9.0")
            Spacer(Modifier.height(8.dp))
            Text("Created and owned by", color = MaterialTheme.colorScheme.secondary)
            Text("Md. Zahid Alam", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(
                "A private offline application for tracking EMI plans, loans, debts, and daily expenses.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("© 2026 Md. Zahid Alam")
            Text("All rights reserved.")
        }
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
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = FinanceShapes.medium,
            enabled = !LocalFormReadOnly.current
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, style = MaterialTheme.typography.bodyLarge)
                }
                Text("⌄", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
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

private val BangladeshBanks = listOf(
    "Sonali Bank", "Janata Bank", "Agrani Bank", "Rupali Bank", "BRAC Bank",
    "Dutch-Bangla Bank", "Islami Bank Bangladesh", "The City Bank", "Eastern Bank",
    "Prime Bank", "Pubali Bank", "Bank Asia", "Southeast Bank", "Standard Chartered Bank",
    "HSBC Bangladesh", "Mutual Trust Bank", "United Commercial Bank", "IFIC Bank",
    "NCC Bank", "Mercantile Bank", "Social Islami Bank", "Al-Arafah Islami Bank",
    "Shahjalal Islami Bank", "Jamuna Bank", "ONE Bank", "Dhaka Bank", "Trust Bank",
    "Community Bank Bangladesh", "Other bank"
)

private fun availableBanks(): List<String> =
    if (AppLocaleState.country == "Bangladesh") (BangladeshBanks + AppLocaleState.customBanks).distinct()
    else (AppLocaleState.customBanks + "Other bank").distinct()

private fun availableProviders(): List<String> =
    if (AppLocaleState.country == "Bangladesh") (listOf("bKash", "Nagad", "Rocket", "Upay") + AppLocaleState.customProviders + "Other provider").distinct()
    else (AppLocaleState.customProviders + "Other provider").distinct()

private fun defaultBank() = availableBanks().firstOrNull() ?: "Other bank"
private fun defaultProvider() = availableProviders().firstOrNull() ?: "Other provider"

private fun rememberCustomBank(context: Context, enteredName: String): String? {
    val name = enteredName.trim().replace(Regex("\\s+"), " ")
    if (name.length !in 2..100 || name.equals("Other bank", ignoreCase = true)) return null
    availableBanks().firstOrNull { it.equals(name, ignoreCase = true) }?.let { return it }
    val updated = (AppLocaleState.customBanks + name).distinctBy { it.lowercase(Locale.US) }.sortedBy { it.lowercase(Locale.US) }
    AppLocaleState.customBanks = updated
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putStringSet(KEY_CUSTOM_BANKS, updated.toSet()).apply()
    return name
}

@Composable
private fun SaveCustomBankAction(bankName: String, onSaved: (String) -> Unit) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = { rememberCustomBank(context, bankName)?.let(onSaved) },
        modifier = Modifier.fillMaxWidth(),
        enabled = !LocalFormReadOnly.current && bankName.trim().length in 2..100
    ) { Text("Save this bank for future use") }
}

@Composable
private fun SearchableBankPicker(value: String, onSelect: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    OutlinedButton(onClick = { visible = true }, modifier = Modifier.fillMaxWidth()) {
        Text("Bank name: ${value.ifBlank { defaultBank() }}")
    }
    if (visible) {
        val matches = availableBanks().filter { query.isBlank() || it.contains(query.trim(), ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { visible = false },
            title = { Text("Select Bank") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(query, { query = it }, label = { Text("Search bank") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Column(Modifier.height(300.dp).verticalScroll(rememberScrollState())) {
                        matches.forEach { bank ->
                            TextButton(onClick = { onSelect(bank); visible = false; query = "" }, modifier = Modifier.fillMaxWidth()) { Text(bank) }
                        }
                        if (matches.isEmpty()) Text("No bank found.")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { visible = false }) { Text("Close") } }
        )
    }
}

@Composable
fun PaymentMethodDetailsFields(
    method: String,
    channel: String,
    onChannel: (String) -> Unit,
    accountName: String,
    onAccountName: (String) -> Unit,
    accountNumber: String,
    onAccountNumber: (String) -> Unit,
    branch: String,
    onBranch: (String) -> Unit,
    routingNumber: String,
    onRoutingNumber: (String) -> Unit,
    reference: String,
    onReference: (String) -> Unit,
    methodDetails: String,
    onMethodDetails: (String) -> Unit
) {
    when (method) {
        "Mobile banking" -> {
            ChoiceDropdown("Mobile banking provider", channel.ifBlank { defaultProvider() }, availableProviders(), onChannel)
            if (channel == "Other provider") Field("Provider name", methodDetails, onChange = onMethodDetails)
            Field("Mobile / account number", accountNumber, onChange = onAccountNumber)
            Field("Account holder (optional)", accountName, onChange = onAccountName)
            Field("Transaction ID", reference, onChange = onReference)
        }
        "Bank transfer" -> {
            SearchableBankPicker(channel.ifBlank { defaultBank() }, onChannel)
            if (channel == "Other bank") {
                Field("Other bank name", methodDetails, onChange = onMethodDetails)
                SaveCustomBankAction(methodDetails, onChannel)
            }
            Field("Account holder", accountName, onChange = onAccountName)
            Field("Account number", accountNumber, onChange = onAccountNumber)
            Field("Branch (optional)", branch, onChange = onBranch)
            Field("Routing number (optional)", routingNumber, onChange = onRoutingNumber)
            Field("Transaction / reference ID", reference, onChange = onReference)
        }
        "Cheque" -> {
            SearchableBankPicker(channel.ifBlank { defaultBank() }, onChannel)
            if (channel == "Other bank") {
                Field("Other bank name", methodDetails, onChange = onMethodDetails)
                SaveCustomBankAction(methodDetails, onChannel)
            }
            Field("Cheque number", reference, onChange = onReference)
            Field("Account holder (optional)", accountName, onChange = onAccountName)
        }
        "Salary deduction", "Salary arrangement" -> {
            Field("Employer", accountName, onChange = onAccountName)
            Field("Salary month", methodDetails, onChange = onMethodDetails)
            Field("Reference (optional)", reference, onChange = onReference)
        }
        "Card" -> {
            Field("Card issuer / bank", channel, onChange = onChannel)
            Field("Last four digits", accountNumber, onChange = onAccountNumber)
            Field("Transaction ID", reference, onChange = onReference)
        }
        "Cash" -> Field("Paid to / received from", accountName, onChange = onAccountName)
        "Other" -> {
            Field("Method name", methodDetails, onChange = onMethodDetails)
            Field("Reference (optional)", reference, onChange = onReference)
        }
    }
}

private fun paymentMethodValidation(
    method: String,
    channel: String,
    accountName: String,
    accountNumber: String,
    reference: String,
    methodDetails: String
): String = when {
    method == "Mobile banking" && channel.isBlank() -> "Select a mobile banking provider."
    method == "Mobile banking" && channel == "Other provider" && methodDetails.isBlank() -> "Enter the provider name."
    method == "Mobile banking" && accountNumber.filter { it.isDigit() }.length !in 7..15 -> "Enter a valid mobile/account number."
    method == "Bank transfer" && channel.isBlank() -> "Select a bank."
    method == "Bank transfer" && channel == "Other bank" && methodDetails.isBlank() -> "Enter the bank name."
    method == "Bank transfer" && accountName.isBlank() -> "Enter the account holder name."
    method == "Bank transfer" && accountNumber.isBlank() -> "Enter the account number."
    method == "Cheque" && (channel.isBlank() || reference.isBlank()) -> "Select the bank and enter the cheque number."
    method in listOf("Salary deduction", "Salary arrangement") && (accountName.isBlank() || methodDetails.isBlank()) -> "Enter the employer and salary month."
    method == "Card" && (!Regex("^[0-9]{4}$").matches(accountNumber) || reference.isBlank()) -> "Enter the card's last four digits and transaction ID."
    method == "Other" && methodDetails.isBlank() -> "Enter the payment method name."
    else -> ""
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
                horizontalArrangement = Arrangement.spacedBy(FinanceSpacing.xs)
            ) {
                if (onBack != null) {
                    IconButton(onClick = requestBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        item {
            CompositionLocalProvider(LocalFormReadOnly provides readOnly) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(FinanceSpacing.sm),
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
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = FinanceShapes.medium,
        enabled = !LocalFormReadOnly.current
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge)
            }
            Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
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
            Modifier.fillMaxWidth().heightIn(min = 56.dp),

        shape = FinanceShapes.medium,

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

private fun LazyListScope.reportResultSection(
    title: String,
    lines: List<String>,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    if (lines.isEmpty()) return
    item(key = "section-$title") {
        OutlinedButton(onClick = onToggle, modifier = Modifier.fillMaxWidth()) {
            Text("${if (expanded) "−" else "+"} $title (${lines.size})")
        }
    }
    if (expanded) {
        items(lines) { line -> Card(Modifier.fillMaxWidth()) { Text(line, Modifier.padding(12.dp)) } }
    }
}

@Composable
fun Reports(
    viewModel: FinanceViewModel
) {
    val context = LocalContext.current
    var pendingReport by remember { mutableStateOf("") }
    var pendingExcel by remember { mutableStateOf<FinanceData?>(null) }
    var filtersVisible by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var reportType by remember { mutableStateOf("Overview") }
    var period by remember { mutableStateOf("This month") }
    var status by remember { mutableStateOf("All statuses") }
    var sort by remember { mutableStateOf("Newest first") }
    var startDate by remember { mutableStateOf(expenseDateText(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis)) }
    var endDate by remember { mutableStateOf(expenseDateText(System.currentTimeMillis())) }
    var expanded by remember { mutableStateOf(setOf<String>()) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null && pendingReport.isNotEmpty()) writePdfToUri(context, uri, pendingReport)
        pendingReport = ""
    }
    val excelLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { uri ->
        if (uri != null && pendingExcel != null) writeXlsxToUri(context, uri, pendingExcel!!, period, reportType)
        pendingExcel = null
    }
    fun inPeriod(value: Long): Boolean {
        val now = Calendar.getInstance()
        val record = Calendar.getInstance().apply { timeInMillis = value }
        return when (period) {
            "This month" -> now.get(Calendar.YEAR) == record.get(Calendar.YEAR) && now.get(Calendar.MONTH) == record.get(Calendar.MONTH)
            "Last month" -> Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.let { it.get(Calendar.YEAR) == record.get(Calendar.YEAR) && it.get(Calendar.MONTH) == record.get(Calendar.MONTH) }
            "Custom range" -> value in (parseExpenseDate(startDate) ?: 0L)..((parseExpenseDate(endDate) ?: Long.MAX_VALUE) + 86_399_999L)
            else -> true
        }
    }
    fun statusMatches(archived: Boolean, completed: Boolean): Boolean = when (status) {
        "Active" -> !archived && !completed
        "Completed" -> !archived && completed
        "Archived" -> archived
        "Paid" -> completed
        "Pending" -> !archived && !completed
        "Cancelled" -> false
        else -> true
    }
    val query = search.trim()
    var emis = viewModel.data.emis.filter { (inPeriod(it.startDate) || it.payments.any { p -> inPeriod(p.paidDate ?: p.dueDate) }) && statusMatches(it.archived, emiCompleted(it)) && (query.isBlank() || listOf(it.name, it.category, it.seller).any { value -> value.contains(query, true) }) && reportType in listOf("Overview", "Payments", "EMI") }
    var loans = viewModel.data.loans.filter { (inPeriod(it.startDate) || it.payments.any { p -> inPeriod(p.paidDate ?: p.dueDate) }) && statusMatches(it.archived, loanCompleted(it)) && (query.isBlank() || listOf(it.name, it.type, it.lender).any { value -> value.contains(query, true) }) && reportType in listOf("Overview", "Payments", "Loans") }
    var debts = viewModel.data.debts.filter { ((it.dueDate?.let { value -> inPeriod(value) } == true) || it.payments.any { p -> inPeriod(p.paidDate ?: p.dueDate) } || it.paymentRequests.any { request -> inPeriod(request.createdDate) }) && (if (status == "Cancelled") it.paymentRequests.any { request -> request.status == "CANCELLED" } else statusMatches(it.archived, debtCompleted(it))) && (query.isBlank() || listOf(it.name, it.notes, it.reason).any { value -> value.contains(query, true) }) && when (reportType) { "Overview", "Payments" -> true; "Money I Owe" -> it.direction == "I Owe"; "Money Owed to Me" -> it.direction == "Owed to Me"; else -> false } }
    var expenses = viewModel.data.expenses.filter { status == "All statuses" && inPeriod(it.date) && (query.isBlank() || listOf(it.title, it.category, it.notes).any { value -> value.contains(query, true) }) && reportType in listOf("Overview", "Expenses") }
    when (sort) {
        "Oldest first" -> { emis = emis.sortedBy { it.startDate }; loans = loans.sortedBy { it.startDate }; debts = debts.sortedBy { it.dueDate ?: 0L }; expenses = expenses.sortedBy { it.date } }
        "Highest amount" -> { emis = emis.sortedByDescending { it.totalPayable }; loans = loans.sortedByDescending { it.totalPayable }; debts = debts.sortedByDescending { it.originalAmount }; expenses = expenses.sortedByDescending { it.amount } }
        "Lowest amount" -> { emis = emis.sortedBy { it.totalPayable }; loans = loans.sortedBy { it.totalPayable }; debts = debts.sortedBy { it.originalAmount }; expenses = expenses.sortedBy { it.amount } }
        else -> { emis = emis.sortedByDescending { it.startDate }; loans = loans.sortedByDescending { it.startDate }; debts = debts.sortedByDescending { it.dueDate ?: 0L }; expenses = expenses.sortedByDescending { it.date } }
    }
    val filteredData = FinanceData(emis, loans, debts, expenses, viewModel.data.receiptProfile)
    val matchCount = emis.size + loans.size + debts.size + expenses.size

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Reports", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = { filtersVisible = !filtersVisible }) { Icon(Icons.Default.Sort, "Report filters") }
            }
            Text("$matchCount matching records • $period")
        }
        if (filtersVisible) item {
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(search, { search = it }, label = { Text("Search records") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                ChoiceDropdown("Report type", reportType, listOf("Overview", "Payments", "EMI", "Loans", "Money I Owe", "Money Owed to Me", "Expenses")) { reportType = it }
                ChoiceDropdown("Report period", period, listOf("This month", "Last month", "Custom range", "All time")) { period = it }
                if (period == "Custom range") { DatePickerField("From", startDate) { startDate = it }; DatePickerField("To", endDate) { endDate = it } }
                ChoiceDropdown("Status", status, listOf("All statuses", "Active", "Completed", "Archived", "Paid", "Pending", "Cancelled")) { status = it }
                ChoiceDropdown("Sort", sort, listOf("Newest first", "Oldest first", "Highest amount", "Lowest amount")) { sort = it }
                TextButton(onClick = { search = ""; reportType = "Overview"; period = "This month"; status = "All statuses"; sort = "Newest first" }) { Text("Clear Filters") }
            } }
        }
        item {
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Filtered Summary", fontWeight = FontWeight.Bold)
                Text("EMI ${emis.size} • Loans ${loans.size} • Debts ${debts.size} • Expenses ${expenses.size}")
                Text("Expenses: ${money(expenses.sumOf { it.amount })}", color = MaterialTheme.colorScheme.primary)
            } }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(onClick = { pendingReport = buildSummaryReport(filteredData, period, reportType); launcher.launch("Finance_Summary.pdf") }, enabled = matchCount > 0, modifier = Modifier.weight(1f)) { Text("Summary PDF") }
                OutlinedButton(onClick = { pendingReport = buildCompleteReport(filteredData); launcher.launch("Finance_Detailed.pdf") }, enabled = matchCount > 0, modifier = Modifier.weight(1f)) { Text("Detailed PDF") }
            }
            OutlinedButton(onClick = { pendingExcel = filteredData; excelLauncher.launch("Filtered_Finance_Report.xlsx") }, enabled = matchCount > 0, modifier = Modifier.fillMaxWidth()) { Text("Professional Excel (.xlsx)") }
        }
        reportResultSection("EMI", emis.map { "${it.name} • ${money(it.totalPayable)} • ${if (emiCompleted(it)) "Completed" else "Active"}" }, "EMI" in expanded) { expanded = if ("EMI" in expanded) expanded - "EMI" else expanded + "EMI" }
        reportResultSection("Loans", loans.map { "${it.name} • ${money(it.totalPayable)} • ${if (loanCompleted(it)) "Completed" else "Active"}" }, "Loans" in expanded) { expanded = if ("Loans" in expanded) expanded - "Loans" else expanded + "Loans" }
        reportResultSection("Money I Owe", debts.filter { it.direction == "I Owe" }.map { "${it.name} • ${money(debtRemainingAmount(it))} to pay" }, "Money I Owe" in expanded) { expanded = if ("Money I Owe" in expanded) expanded - "Money I Owe" else expanded + "Money I Owe" }
        reportResultSection("Money Owed to Me", debts.filter { it.direction == "Owed to Me" }.map { "${it.name} • ${money(debtRemainingAmount(it))} to receive" }, "Money Owed to Me" in expanded) { expanded = if ("Money Owed to Me" in expanded) expanded - "Money Owed to Me" else expanded + "Money Owed to Me" }
        reportResultSection("Expenses", expenses.map { "${expenseDayKey(it.date)} • ${it.title} • ${money(it.amount)}" }, "Expenses" in expanded) { expanded = if ("Expenses" in expanded) expanded - "Expenses" else expanded + "Expenses" }
        if (matchCount == 0) item { Text("No records match the selected filters.") }
    }
}

fun buildSummaryReport(data: FinanceData, period: String, reportType: String): String = buildString {
    appendLine("MY FINANCE TRACKER — SUMMARY REPORT")
    appendLine("Generated: ${dateTimeText(System.currentTimeMillis())}")
    appendLine("Period: $period")
    appendLine("Report type: $reportType")
    appendLine()
    appendLine("RECORD SUMMARY")
    appendLine("EMI plans: ${data.emis.size}")
    appendLine("Loans: ${data.loans.size}")
    appendLine("Money I owe: ${data.debts.count { it.direction == "I Owe" }}")
    appendLine("Money owed to me: ${data.debts.count { it.direction == "Owed to Me" }}")
    appendLine("Expenses: ${data.expenses.size}")
    appendLine("Expense total: ${money(data.expenses.sumOf { it.amount })}")
    appendLine("EMI remaining: ${money(data.emis.sumOf { it.payments.filter { p -> p.paidDate == null }.sumOf { p -> p.amount } })}")
    appendLine("Loan remaining: ${money(data.loans.sumOf { it.payments.filter { p -> p.paidDate == null }.sumOf { p -> p.amount } })}")
    appendLine("Debt to pay: ${money(data.debts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) })}")
    appendLine("Money to receive: ${money(data.debts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) })}")
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
        appendLine("Financing source: ${item.financingSource}")
        appendLine("How received: ${item.receivedMethod}")
        if (item.financingChannel.isNotBlank()) appendLine("Provider / bank: ${item.financingChannel}")
        if (item.financingAccountName.isNotBlank()) appendLine("Account holder / party: ${item.financingAccountName}")
        if (item.financingAccountNumber.isNotBlank()) appendLine("Account / mobile number: ${item.financingAccountNumber}")
        if (item.financingReference.isNotBlank()) appendLine("Transaction reference: ${item.financingReference}")

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
        appendLine("Financing source: ${item.financingSource}")
        appendLine("How received: ${item.receivedMethod}")
        if (item.financingChannel.isNotBlank()) appendLine("Provider / bank: ${item.financingChannel}")
        if (item.financingAccountName.isNotBlank()) appendLine("Account holder / party: ${item.financingAccountName}")
        if (item.financingAccountNumber.isNotBlank()) appendLine("Account / mobile number: ${item.financingAccountNumber}")
        if (item.financingReference.isNotBlank()) appendLine("Transaction reference: ${item.financingReference}")

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
        appendLine("How received / given: ${item.receivedOrGivenMethod}")
        if (item.financingChannel.isNotBlank()) appendLine("Provider / bank: ${item.financingChannel}")
        if (item.financingAccountName.isNotBlank()) appendLine("Account holder / party: ${item.financingAccountName}")
        if (item.financingAccountNumber.isNotBlank()) appendLine("Account / mobile number: ${item.financingAccountNumber}")
        if (item.financingReference.isNotBlank()) appendLine("Transaction reference: ${item.financingReference}")

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

private data class XlsxMoney(val value: Double)
private data class XlsxNumber(val value: Number)

fun writeXlsxToUri(context: Context, uri: android.net.Uri, data: FinanceData, period: String, reportType: String) {
    fun escape(value: String): String {
        // XML 1.0 forbids most control characters. Desktop Excel rejects the entire
        // worksheet when one appears in a user-entered name, reference, or note.
        val xmlSafe = value.filter { character ->
            character == '\t' || character == '\n' || character == '\r' || character.code >= 0x20
        }
        return xmlSafe.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
    }
    fun columnName(index: Int): String {
        var value = index + 1
        var result = ""
        while (value > 0) { value--; result = ('A'.code + value % 26).toChar() + result; value /= 26 }
        return result
    }
    fun sheet(title: String, headers: List<String>, rows: List<List<Any?>>): String {
        val headerRow = 5
        val lastColumn = columnName(headers.lastIndex)
        val xml = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
        append("<sheetViews><sheetView showGridLines=\"0\" workbookViewId=\"0\"><pane ySplit=\"5\" topLeftCell=\"A6\" activePane=\"bottomLeft\" state=\"frozen\"/></sheetView></sheetViews>")
        // OOXML requires sheetFormatPr before cols and sheetData. Mobile viewers
        // tolerated the old order, but desktop Microsoft Excel removed the sheets.
        append("<sheetFormatPr defaultRowHeight=\"18\"/>")
        append("<cols>"); headers.indices.forEach { index -> append("<col min=\"${index + 1}\" max=\"${index + 1}\" width=\"${when { headers[index].contains("Note") || headers[index].contains("Details") -> 28; headers[index].contains("Date") -> 15; else -> 20 }}\" customWidth=\"1\"/>") }; append("</cols><sheetData>")
        fun row(number: Int, values: List<Any?>, style: Int? = null) {
            append("<row r=\"$number\">")
            values.forEachIndexed { index, value ->
                val ref = "${columnName(index)}$number"
                when (value) {
                    is XlsxMoney -> append("<c r=\"$ref\" s=\"2\"><v>${value.value}</v></c>")
                    is XlsxNumber -> append("<c r=\"$ref\" s=\"3\"><v>${value.value}</v></c>")
                    else -> append("<c r=\"$ref\" t=\"inlineStr\"${style?.let { " s=\"$it\"" } ?: ""}><is><t xml:space=\"preserve\">${escape(localized(value?.toString() ?: ""))}</t></is></c>")
                }
            }
            append("</row>")
        }
        row(1, listOf("MY FINANCE TRACKER — $title"), 1)
        row(2, listOf("Generated", dateTimeText(System.currentTimeMillis()), "Period", period))
        row(3, listOf("Currency", "${AppLocaleState.currencyCode} (${AppLocaleState.currencySymbol})", "Report", reportType))
        row(headerRow, headers, 1)
        rows.forEachIndexed { index, values -> row(index + headerRow + 1, values) }
        append("</sheetData><mergeCells count=\"1\"><mergeCell ref=\"A1:${lastColumn}1\"/></mergeCells>")
        append("<autoFilter ref=\"A$headerRow:${lastColumn}${rows.size + headerRow}\"/><pageMargins left=\"0.25\" right=\"0.25\" top=\"0.5\" bottom=\"0.5\" header=\"0.2\" footer=\"0.2\"/><pageSetup orientation=\"landscape\" fitToWidth=\"1\" fitToHeight=\"0\"/></worksheet>")
        }
        return OoxmlRules.requireValidWorksheet(xml)
    }

    val summaryRows = listOf(
        listOf("Generated", dateTimeText(System.currentTimeMillis())), listOf("Period", period), listOf("Report type", reportType),
        listOf("EMI plans", XlsxNumber(data.emis.size)), listOf("Loans", XlsxNumber(data.loans.size)),
        listOf("Money I Owe", XlsxNumber(data.debts.count { it.direction == "I Owe" })), listOf("Money Owed to Me", XlsxNumber(data.debts.count { it.direction == "Owed to Me" })),
        listOf("Expenses", XlsxNumber(data.expenses.size)), listOf("Expense total", XlsxMoney(data.expenses.sumOf { it.amount })),
        listOf("Debt to pay", XlsxMoney(data.debts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) })),
        listOf("Money to receive", XlsxMoney(data.debts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) }))
    )
    val sheets = listOf(
        "Dashboard Summary" to sheet("Dashboard Summary", listOf("Metric", "Value"), summaryRows),
        "EMI Plans" to sheet("EMI Plans", listOf("Item", "Category", "Seller", "Financing Source", "Received Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Transaction Reference", "Price", "Down Payment", "Financed", "Interest %", "Total Payable", "Installments", "Monthly", "Start Date", "Status"), data.emis.map { listOf(it.name, it.category, it.seller, it.financingSource, it.receivedMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.price), XlsxMoney(it.downPayment), XlsxMoney(it.financedAmount), XlsxNumber(it.interestRate), XlsxMoney(it.totalPayable), XlsxNumber(it.installments), XlsxMoney(it.monthlyPayment), dateText(it.startDate), if (it.archived) "Archived" else if (emiCompleted(it)) "Completed" else "Active") }),
        "Loans" to sheet("Loans", listOf("Loan", "Type", "Lender", "Financing Source", "Received Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Transaction Reference", "Principal", "Interest %", "Total Payable", "Installments", "Monthly", "Start Date", "Status"), data.loans.map { listOf(it.name, it.type, it.lender, it.financingSource, it.receivedMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.principal), XlsxNumber(it.interestRate), XlsxMoney(it.totalPayable), XlsxNumber(it.installments), XlsxMoney(it.monthlyPayment), dateText(it.startDate), if (it.archived) "Archived" else if (loanCompleted(it)) "Completed" else "Active") }),
        "Debts I Owe" to sheet("Debts I Owe", listOf("Person / Organization", "Debt Date", "Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Reference", "Original", "Paid", "Remaining", "Due Date", "Reason", "Status"), data.debts.filter { it.direction == "I Owe" }.map { listOf(it.name, dateText(it.debtDate), it.receivedOrGivenMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.originalAmount), XlsxMoney(debtPaidAmount(it)), XlsxMoney(debtRemainingAmount(it)), it.dueDate?.let(::dateText) ?: "", it.reason, if (it.archived) "Archived" else if (debtCompleted(it)) "Completed" else "Active") }),
        "Owed to Me" to sheet("Money Owed to Me", listOf("Person / Organization", "Debt Date", "Method", "Provider / Bank", "Account Holder", "Account / Mobile", "Reference", "Original", "Received", "Remaining", "Due Date", "Reason", "Status"), data.debts.filter { it.direction == "Owed to Me" }.map { listOf(it.name, dateText(it.debtDate), it.receivedOrGivenMethod, it.financingChannel, it.financingAccountName, it.financingAccountNumber, it.financingReference, XlsxMoney(it.originalAmount), XlsxMoney(debtPaidAmount(it)), XlsxMoney(debtRemainingAmount(it)), it.dueDate?.let(::dateText) ?: "", it.reason, if (it.archived) "Archived" else if (debtCompleted(it)) "Completed" else "Active") }),
        "Expenses" to sheet("Expenses", listOf("Date", "Expense", "Category", "Amount", "Notes"), data.expenses.map { listOf(expenseDayKey(it.date), it.title, it.category, XlsxMoney(it.amount), it.notes) }),
        "Payments" to sheet("Payment History", listOf("Record Type", "Record", "Payment No.", "Due Date", "Paid Date", "Amount", "Status", "Method", "Provider / Bank", "Reference", "Party", "Account", "Branch", "Routing", "Details", "Notes"), buildList {
            data.emis.forEach { plan -> plan.payments.forEach { p -> add(listOf("EMI", plan.name, XlsxNumber(p.number), dateText(p.dueDate), p.paidDate?.let { dateText(it) } ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
            data.loans.forEach { plan -> plan.payments.forEach { p -> add(listOf("Loan", plan.name, XlsxNumber(p.number), dateText(p.dueDate), p.paidDate?.let { dateText(it) } ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
            data.debts.forEach { plan -> plan.payments.forEach { p -> add(listOf("Debt", plan.name, XlsxNumber(p.number), dateText(p.dueDate), p.paidDate?.let { dateText(it) } ?: "", XlsxMoney(p.amount), p.status, p.paymentMethod, p.paymentChannel, p.referenceNumber, p.counterparty, p.accountNumber, p.branch, p.routingNumber, p.methodDetails, p.notes)) } }
        }),
        "Payment Requests" to sheet("Payment Requests", listOf("Request No.", "Requested From", "Created", "Due Date", "Amount", "Received", "Status", "Preferred Method", "Provider / Bank", "Account Name", "Account / Mobile", "Reference", "Instructions", "Message"), buildList {
            data.debts.filter { it.direction == "Owed to Me" }.forEach { debt -> debt.paymentRequests.forEach { request -> add(listOf(request.requestNumber, debt.name, dateText(request.createdDate), request.dueDate?.let(::dateText) ?: "", XlsxMoney(request.amount), XlsxMoney(request.receivedAmount), request.status, request.paymentMethod, request.paymentChannel, request.accountName, request.accountNumber, request.referenceNumber, request.paymentInstructions, request.message)) } }
        })
    )
    context.contentResolver.openOutputStream(uri)?.use { output ->
        ZipOutputStream(output).use { zip ->
            fun entry(name: String, content: String) { zip.putNextEntry(ZipEntry(name)); zip.write(content.toByteArray(Charsets.UTF_8)); zip.closeEntry() }
            entry("[Content_Types].xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>${sheets.indices.joinToString("") { "<Override PartName=\"/xl/worksheets/sheet${it + 1}.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>" }}</Types>")
            entry("_rels/.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>")
            entry("xl/workbook.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets>${sheets.mapIndexed { index, pair -> "<sheet name=\"${pair.first}\" sheetId=\"${index + 1}\" r:id=\"rId${index + 1}\"/>" }.joinToString("")}</sheets></workbook>")
            entry("xl/_rels/workbook.xml.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">${sheets.indices.joinToString("") { "<Relationship Id=\"rId${it + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet${it + 1}.xml\"/>" }}<Relationship Id=\"rId${sheets.size + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/></Relationships>")
            entry("xl/styles.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><numFmts count=\"1\"><numFmt numFmtId=\"164\" formatCode=\"${escape(AppLocaleState.currencySymbol)}#,##0.00\"/></numFmts><fonts count=\"2\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font><font><b/><color rgb=\"FFFFFFFF\"/><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts><fills count=\"3\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF007C7A\"/><bgColor indexed=\"64\"/></patternFill></fill></fills><borders count=\"1\"><border/></borders><cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs><cellXfs count=\"4\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/><xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" applyFill=\"1\" applyFont=\"1\"/><xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"0\" applyNumberFormat=\"1\"/><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellXfs></styleSheet>")
            sheets.forEachIndexed { index, pair -> entry("xl/worksheets/sheet${index + 1}.xml", pair.second) }
        }
    }
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

            val paint = Paint().apply { isAntiAlias = true }
            val teal = android.graphics.Color.rgb(0, 124, 122)
            val paleTeal = android.graphics.Color.rgb(226, 245, 242)
            val ink = android.graphics.Color.rgb(35, 42, 42)
            val muted = android.graphics.Color.rgb(92, 103, 102)

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

            fun drawHeader() {
                paint.color = teal
                canvas.drawRect(0f, 0f, 595f, 104f, paint)
                val logo = BitmapFactory.decodeResource(context.resources, com.mdzahidalam.myfinancetracker.R.drawable.app_logo)
                if (logo != null) canvas.drawBitmap(logo, null, RectF(28f, 20f, 92f, 84f), paint)
                paint.color = android.graphics.Color.WHITE
                paint.textSize = 18f
                paint.isFakeBoldText = true
                canvas.drawText(localized("MY FINANCE TRACKER"), 112f, 49f, paint)
                paint.textSize = 10f
                paint.isFakeBoldText = false
                canvas.drawText(localized("Secure personal finance record"), 112f, 69f, paint)
            }
            fun wrap(value: String, maxChars: Int = 76): List<String> {
                if (value.length <= maxChars) return listOf(value)
                val result = mutableListOf<String>()
                var current = ""
                value.split(" ").forEach { word ->
                    if ((current.length + word.length + 1) > maxChars) { if (current.isNotBlank()) result += current; current = word }
                    else current = if (current.isBlank()) word else "$current $word"
                }
                if (current.isNotBlank()) result += current
                return result
            }
            drawHeader()
            var y = 132f
            val reportDocument = LegacyReportAdapter.fromText(text)
            val inputLines = reportDocument.blocks.map { block ->
                when (block) {
                    is ReportBlock.Heading -> block.text
                    is ReportBlock.Field -> "${block.label}: ${block.value}"
                    is ReportBlock.Paragraph -> block.text
                    is ReportBlock.Signature -> "[[SIGNATURE:${block.encodedImage}]]"
                    is ReportBlock.Table -> block.rows.joinToString(" • ") { it.joinToString(" | ") }
                }
            }.map(::localizedExport)
            inputLines.forEach { raw ->
                val line = raw.trim()
                if (y > 785f) {
                    paint.color = muted; paint.textSize = 9f; paint.isFakeBoldText = false
                    canvas.drawText("Page $pageNumber", 515f, 822f, paint)
                    document.finishPage(page)
                    pageNumber++
                    page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
                    canvas = page.canvas
                    drawHeader()
                    y = 132f
                }
                when {
                    line.startsWith("[[SIGNATURE:") && line.endsWith("]]" ) -> {
                        val encoded = line.removePrefix("[[SIGNATURE:").removeSuffix("]]" )
                        val signature = runCatching {
                            val bytes = Base64.decode(encoded, Base64.NO_WRAP)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }.getOrNull()
                        if (signature != null) {
                            paint.color = muted; paint.textSize = 10f; paint.isFakeBoldText = true
                            canvas.drawText(localized("Authorized signature"), 40f, y, paint)
                            val ratio = signature.width.toFloat() / signature.height.coerceAtLeast(1)
                            val height = 58f
                            val width = minOf(170f, height * ratio)
                            canvas.drawBitmap(signature, null, RectF(40f, y + 8f, 40f + width, y + 8f + height), paint)
                            paint.color = muted
                            canvas.drawLine(40f, y + 72f, 230f, y + 72f, paint)
                            y += 90f
                        }
                    }
                    line.isBlank() -> y += 10f
                    line == line.uppercase(Locale.US) && !line.contains(":") -> {
                        paint.color = paleTeal; canvas.drawRoundRect(28f, y - 20f, 567f, y + 10f, 6f, 6f, paint)
                        paint.color = teal; paint.textSize = 14f; paint.isFakeBoldText = true
                        canvas.drawText(line, 42f, y, paint); y += 40f
                    }
                    line.contains(":") -> {
                        val label = line.substringBefore(":").trim()
                        val value = line.substringAfter(":").trim()
                        paint.color = android.graphics.Color.rgb(247, 249, 249)
                        val wrapped = wrap(value, 52)
                        val rowHeight = max(30f, 18f * wrapped.size + 10f)
                        canvas.drawRoundRect(28f, y - 17f, 567f, y - 17f + rowHeight, 4f, 4f, paint)
                        paint.color = muted; paint.textSize = 10f; paint.isFakeBoldText = true
                        canvas.drawText(label, 40f, y, paint)
                        paint.color = ink; paint.isFakeBoldText = false
                        wrapped.forEachIndexed { index, valueLine -> canvas.drawText(valueLine, 190f, y + index * 16f, paint) }
                        y += rowHeight + 5f
                    }
                    else -> {
                        paint.color = ink; paint.textSize = 10f; paint.isFakeBoldText = false
                        wrap(line).forEach { valueLine -> canvas.drawText(valueLine, 34f, y, paint); y += 15f }
                    }
                }
            }
            paint.color = paleTeal; canvas.drawRect(0f, 796f, 595f, 842f, paint)
            paint.color = teal; paint.textSize = 9f; paint.isFakeBoldText = true
            canvas.drawText(localized("Generated by My Finance Tracker • Powered by Md. Zahid Alam"), 30f, 822f, paint)

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
            var language by remember { mutableStateOf(preferences.getString(KEY_LANGUAGE, "EN") ?: "EN") }
            var country by remember { mutableStateOf(preferences.getString(KEY_COUNTRY, "Bangladesh") ?: "Bangladesh") }
            var currencyCode by remember { mutableStateOf(preferences.getString(KEY_CURRENCY_CODE, "BDT") ?: "BDT") }
            var currencySymbol by remember { mutableStateOf(preferences.getString(KEY_CURRENCY_SYMBOL, "৳") ?: "৳") }
            AppLocaleState.language = language; AppLocaleState.country = country; AppLocaleState.currencyCode = currencyCode; AppLocaleState.currencySymbol = currencySymbol
            AppLocaleState.customBanks = preferences.getStringSet(KEY_CUSTOM_BANKS, emptySet())?.sorted() ?: emptyList()
            AppLocaleState.customProviders = preferences.getStringSet(KEY_CUSTOM_PROVIDERS, emptySet())?.sorted() ?: emptyList()
            val useDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            CompositionLocalProvider(LocalAppLanguage provides language) { MaterialTheme(
                colorScheme = if (useDarkTheme) AppDarkColorScheme else AppLightColorScheme,
                typography = FinanceDesignSystem.Typography,
                shapes = FinanceDesignSystem.Shapes
            ) {

            if (!security.hasPassword()) {

                SetupScreen(language = language, onLanguageChange = { selected -> language = selected; preferences.edit().putString(KEY_LANGUAGE, selected).apply() }) { password, selectedCountry, code, symbol ->

                    security.setPassword(password)
                    country = selectedCountry; currencyCode = code; currencySymbol = symbol
                    preferences.edit().putString(KEY_COUNTRY, selectedCountry).putString(KEY_CURRENCY_CODE, code).putString(KEY_CURRENCY_SYMBOL, symbol).apply()

                    unlocked = true

                    showContent()
                }

            } else if (!unlocked) {

                LockScreen(language = language, onLanguageChange = { selected -> language = selected; preferences.edit().putString(KEY_LANGUAGE, selected).apply() }) { password ->

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
                    },
                    language = language,
                    onLanguageChange = { selected -> language = selected; preferences.edit().putString(KEY_LANGUAGE, selected).apply() },
                    country = country,
                    currencyCode = currencyCode,
                    currencySymbol = currencySymbol,
                    onRegionChange = { selectedCountry, code, symbol -> country = selectedCountry; currencyCode = code; currencySymbol = symbol; preferences.edit().putString(KEY_COUNTRY, selectedCountry).putString(KEY_CURRENCY_CODE, code).putString(KEY_CURRENCY_SYMBOL, symbol).apply() },
                    onCustomPaymentListsChange = { banks, providers -> preferences.edit().putStringSet(KEY_CUSTOM_BANKS, banks.toSet()).putStringSet(KEY_CUSTOM_PROVIDERS, providers.toSet()).apply(); AppLocaleState.customBanks = banks; AppLocaleState.customProviders = providers }
                )
            } }
            }
        }
    }
}


// ============================================================
// SETUP SCREEN
// ============================================================

@Composable
fun SetupScreen(
    language: String,
    onLanguageChange: (String) -> Unit,
    onSet: (String, String, String, String) -> Unit
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
    var region by remember { mutableStateOf(CountryCatalog.findByName("Bangladesh") ?: CountryCatalog.all.first()) }
    var currencyCode by remember { mutableStateOf("BDT") }
    var currencySymbol by remember { mutableStateOf("৳") }

    PremiumAuthLayout(
        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = language == "EN", onClick = { onLanguageChange("EN") }, label = { Text("English") })
            FilterChip(selected = language == "BN", onClick = { onLanguageChange("BN") }, label = { Text("Bangla") })
        }

        Image(
            painter = painterResource(com.mdzahidalam.myfinancetracker.R.drawable.app_logo),
            contentDescription = "My Finance Tracker logo",
            modifier = Modifier.size(112.dp)
        )

        Text(
            "Secure My Finance Tracker",
            style =
                MaterialTheme.typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Text(
            "Create an app password. " +
                    "The password itself is not stored; " +
                    "only a protected hash is stored on this phone.",
            color = MaterialTheme.colorScheme.onBackground
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

        Spacer(Modifier.height(8.dp))
        SearchableCountryPicker(region, language) {
            region = it
            if (it.currencyCode.isNotBlank()) currencyCode = it.currencyCode
            if (it.currencySymbol.isNotBlank()) currencySymbol = it.currencySymbol
        }
        Field("Currency code (for example USD)", currencyCode) { currencyCode = it.uppercase().take(3) }
        Field("Currency symbol", currencySymbol) { currencySymbol = it.take(4) }

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

                    currencyCode.length != 3 || currencySymbol.isBlank() -> "Enter a valid currency code and symbol."

                    else ->
                        ""
                }

                if (error.isEmpty()) {
                    onSet(password, region.name, currencyCode, currencySymbol)
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
    language: String,
    onLanguageChange: (String) -> Unit,
    onUnlock: (String) -> Boolean
) {

    var password by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf("")
    }

    PremiumAuthLayout(
        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = language == "EN", onClick = { onLanguageChange("EN") }, label = { Text("English") })
            FilterChip(selected = language == "BN", onClick = { onLanguageChange("BN") }, label = { Text("Bangla") })
        }

        Image(
            painter = painterResource(com.mdzahidalam.myfinancetracker.R.drawable.app_logo),
            contentDescription = "My Finance Tracker logo",
            modifier = Modifier.size(112.dp)
        )

        Text(
            "My Finance Tracker",
            style =
                MaterialTheme.typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Text("Your private offline finance tracker", color = MaterialTheme.colorScheme.onBackground)

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

        Spacer(Modifier.height(8.dp))

        Text(
            "Powered by Md. Zahid Alam",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun PremiumAuthLayout(
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = FinanceSpacing.lg, vertical = FinanceSpacing.xl),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}


