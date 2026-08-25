package com.mdzahidalam.myfinancetracker.presentation

import com.mdzahidalam.myfinancetracker.domain.model.FinanceData
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureUiStateFactoryTest {
    @Test fun emptyDataProducesSafeFeatureStates() {
        val data = FinanceData()
        assertTrue(FeatureUiStateFactory.dashboard(data).recentExpenses.isEmpty())
        assertTrue(FeatureUiStateFactory.payments(data).debts.isEmpty())
        assertTrue(FeatureUiStateFactory.expenses(data).expenses.isEmpty())
        assertTrue(FeatureUiStateFactory.reports(data).data.emis.isEmpty())
    }
}
