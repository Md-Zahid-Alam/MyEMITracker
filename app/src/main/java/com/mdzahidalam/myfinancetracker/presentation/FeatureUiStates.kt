package com.mdzahidalam.myfinancetracker.presentation

import com.mdzahidalam.myfinancetracker.debtRemainingAmount
import com.mdzahidalam.myfinancetracker.domain.model.*

/** Immutable feature projections. Screens observe only the records relevant to their feature. */
data class DashboardUiState(
    val emis: List<EmiItem>, val loans: List<Loan>, val debts: List<Debt>,
    val expenses: List<Expense>, val recentExpenses: List<Expense>,
    val debtToPay: Double, val moneyToReceive: Double
)

data class PaymentsUiState(
    val emis: List<EmiItem>, val loans: List<Loan>,
    val debtsIOwe: List<Debt>, val owedToMe: List<Debt>
) {
    val debts: List<Debt> get() = debtsIOwe + owedToMe
}

data class ExpensesUiState(val expenses: List<Expense>)
data class ReportsUiState(val data: FinanceData)
data class SettingsUiState(val receiptProfile: ReceiptProfile)

object FeatureUiStateFactory {
    fun dashboard(data: FinanceData) = DashboardUiState(
        emis = data.emis,
        loans = data.loans,
        debts = data.debts,
        expenses = data.expenses,
        recentExpenses = data.expenses.sortedByDescending(Expense::date).take(5),
        debtToPay = data.debts.filter { it.direction == "I Owe" }.sumOf { debtRemainingAmount(it) },
        moneyToReceive = data.debts.filter { it.direction == "Owed to Me" }.sumOf { debtRemainingAmount(it) }
    )

    fun payments(data: FinanceData) = PaymentsUiState(
        emis = data.emis,
        loans = data.loans,
        debtsIOwe = data.debts.filter { it.direction == "I Owe" },
        owedToMe = data.debts.filter { it.direction == "Owed to Me" }
    )

    fun expenses(data: FinanceData) = ExpensesUiState(data.expenses)
    fun reports(data: FinanceData) = ReportsUiState(data)
    fun settings(data: FinanceData) = SettingsUiState(data.receiptProfile)
}
