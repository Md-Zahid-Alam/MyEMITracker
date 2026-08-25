package com.mdzahidalam.myfinancetracker.domain

import com.mdzahidalam.myfinancetracker.domain.validation.FinanceValidators
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FinanceValidatorsTest {
    @Test fun validMoneyAndContactDetailsPass() {
        assertNull(FinanceValidators.positiveAmount("1250.50"))
        assertNull(FinanceValidators.optionalEmail("zahid@example.com"))
        assertNull(FinanceValidators.optionalPhone("+880 1712 345678"))
    }

    @Test fun invalidFinancialRangesAreRejected() {
        assertNotNull(FinanceValidators.positiveAmount("0"))
        assertNotNull(FinanceValidators.installmentCount("0"))
        assertNotNull(FinanceValidators.dueDay("31"))
        assertNotNull(FinanceValidators.percentage("101"))
    }
}
