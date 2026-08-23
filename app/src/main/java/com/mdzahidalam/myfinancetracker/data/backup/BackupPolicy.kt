package com.mdzahidalam.myfinancetracker.data.backup

object BackupPolicy {
    const val MAX_PAYLOAD_LENGTH = 40_000_000
    const val DEFAULT_ITERATIONS = 210_000
    fun validatedIterations(value: Int) = value.coerceIn(120_000, 500_000)
    fun requireAcceptableSize(length: Int) = require(length <= MAX_PAYLOAD_LENGTH) { "Backup file is too large." }
}
