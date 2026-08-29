package com.mdzahidalam.myfinancetracker.domain

import com.mdzahidalam.myfinancetracker.financialAmountText
import com.mdzahidalam.myfinancetracker.domain.validation.FinanceValidators
import org.junit.Assert.assertEquals
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
        assertNotNull(FinanceValidators.positiveAmount("9.33355588E8"))
        assertNotNull(FinanceValidators.positiveAmount("123.456"))
        assertNotNull(FinanceValidators.positiveAmount("1000000000000"))
        assertNotNull(FinanceValidators.installmentCount("0"))
        assertNotNull(FinanceValidators.dueDay("31"))
        assertNotNull(FinanceValidators.percentage("101"))
    }

    @Test fun twelveWholeDigitsAndTwoDecimalsAreSupported() {
        assertNull(FinanceValidators.positiveAmount("999999999999.99"))
    }

    @Test fun storedLargeAmountsNeverDisplayScientificNotation() {
        assertEquals("996646644411", financialAmountText(996_646_644_411.0))
        assertEquals("999999999999.99", financialAmountText(999_999_999_999.99))
    }
}
