package com.mdzahidalam.myfinancetracker.domain.usecase

import com.mdzahidalam.myfinancetracker.domain.model.Debt
import com.mdzahidalam.myfinancetracker.domain.model.EmiItem
import com.mdzahidalam.myfinancetracker.domain.model.Loan
import com.mdzahidalam.myfinancetracker.domain.model.Payment
import kotlin.math.max

/** Pure financial rules. No Android, storage, or UI dependencies. */
object FinanceCalculations {
    const val MONEY_TOLERANCE = 0.005

    fun isEmiCompleted(item: EmiItem) = item.payments.isNotEmpty() && item.payments.all { it.paidDate != null }
    fun isLoanCompleted(item: Loan) = item.payments.isNotEmpty() && item.payments.all { it.paidDate != null }
    fun debtPaid(item: Debt) = item.payments.asSequence().filter { it.paidDate != null }.sumOf { it.amount }
    fun debtRemaining(item: Debt) = max(0.0, item.originalAmount - debtPaid(item))
    fun isDebtCompleted(item: Debt) = item.originalAmount > 0 && debtRemaining(item) <= MONEY_TOLERANCE
    fun completionDate(payments: List<Payment>) = payments.mapNotNull { it.paidDate }.maxOrNull()

    fun validateDebtTransaction(item: Debt, amount: Double): String? = when {
        !amount.isFinite() || amount <= 0.0 -> "amount_invalid"
        amount > debtRemaining(item) + MONEY_TOLERANCE -> "amount_exceeds_remaining"
        else -> null
    }

    fun calculateInstallmentAmounts(total: Double, count: Int): List<Double> {
        require(total.isFinite() && total >= 0.0)
        require(count > 0)
        val regular = kotlin.math.floor((total / count) * 100.0) / 100.0
        return List(count) { index -> if (index == count - 1) total - regular * (count - 1) else regular }
    }
}
