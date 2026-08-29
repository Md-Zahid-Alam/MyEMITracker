package com.mdzahidalam.myfinancetracker.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinanceRouteTest {
    @Test fun acceptsSupportedRoutesAndExposesSuffixes() {
        val route = FinanceRoute.of("loan_history")
        assertTrue(route.hasSuffix("_history"))
        assertEquals("loan", route.withoutSuffix("_history"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsUnknownRoutes() {
        FinanceRoute.of("unknown_screen")
    }

    @Test fun paymentSectionsRejectUnknownKeys() {
        assertEquals(PaymentSection.DEBTS_OWED, PaymentSection.fromKey("DebtsOwed"))
    }
}
