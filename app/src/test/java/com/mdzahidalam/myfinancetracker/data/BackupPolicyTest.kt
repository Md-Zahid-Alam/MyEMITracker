package com.mdzahidalam.myfinancetracker.data

import com.mdzahidalam.myfinancetracker.data.backup.BackupPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class BackupPolicyTest {
    @Test fun unsafeIterationCountsAreClamped() {
        assertEquals(120_000, BackupPolicy.validatedIterations(1))
        assertEquals(210_000, BackupPolicy.validatedIterations(210_000))
        assertEquals(500_000, BackupPolicy.validatedIterations(Int.MAX_VALUE))
    }

    @Test(expected = IllegalArgumentException::class)
    fun oversizedBackupIsRejected() { BackupPolicy.requireAcceptableSize(BackupPolicy.MAX_PAYLOAD_LENGTH + 1) }
}
