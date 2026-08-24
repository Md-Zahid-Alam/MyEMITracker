package com.mdzahidalam.myfinancetracker.domain

import com.mdzahidalam.myfinancetracker.domain.usecase.FinanceCalculations
import org.junit.Assert.assertEquals
import org.junit.Test

class FinanceCalculationsTest {
    @Test fun finalInstallmentAbsorbsRounding() {
        val amounts = FinanceCalculations.calculateInstallmentAmounts(100.0, 3)
        assertEquals(33.33, amounts[0], 0.0001)
        assertEquals(33.33, amounts[1], 0.0001)
        assertEquals(33.34, amounts[2], 0.0001)
        assertEquals(100.0, amounts.sum(), 0.0001)
    }
}
