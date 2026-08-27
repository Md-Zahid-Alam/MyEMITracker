package com.mdzahidalam.myfinancetracker.domain

import com.mdzahidalam.myfinancetracker.domain.model.Debt
import com.mdzahidalam.myfinancetracker.domain.model.Payment
import com.mdzahidalam.myfinancetracker.domain.usecase.FinanceCalculations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DebtLimitsTest {
    private val debt = Debt(name="Test", direction="I Owe", originalAmount=5000.0, dueDate=null, notes="", payments=listOf(Payment(1, 1L, 4000.0, 1L)))
    @Test fun remainingAmountIsEnforced() {
        assertEquals(1000.0, FinanceCalculations.debtRemaining(debt), 0.001)
        assertNull(FinanceCalculations.validateDebtTransaction(debt, 1000.0))
        assertEquals("amount_exceeds_remaining", FinanceCalculations.validateDebtTransaction(debt, 1000.01))
    }
}
