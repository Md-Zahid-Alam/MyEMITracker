package com.mdzahidalam.myfinancetracker.data.security
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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

internal fun localEncryptionKey(): SecretKey {
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

internal fun encryptLocalRecords(plainText: String): String {
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

internal fun decryptLocalRecords(payload: String): String {
    val root = JSONObject(payload)
    require(root.optString("format") == "MFT_LOCAL_ENCRYPTED") { "Unsupported encrypted record format." }
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val iv = Base64.decode(root.getString("iv"), Base64.NO_WRAP)
    cipher.init(Cipher.DECRYPT_MODE, localEncryptionKey(), GCMParameterSpec(128, iv))
    val clear = cipher.doFinal(Base64.decode(root.getString("ciphertext"), Base64.NO_WRAP))
    return clear.toString(Charsets.UTF_8)
}

internal fun encryptBackup(plainText: String, password: String): String {
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

internal fun decryptBackup(payload: String, password: String): String {
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


