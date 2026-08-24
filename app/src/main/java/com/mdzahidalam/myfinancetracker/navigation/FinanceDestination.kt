package com.mdzahidalam.myfinancetracker.navigation

/** Typed destinations replace fragile string concatenation as screens are migrated feature-by-feature. */
sealed interface FinanceDestination {
    data object Dashboard : FinanceDestination
    data object Payments : FinanceDestination
    data object Expenses : FinanceDestination
    data object Reports : FinanceDestination
    data object Settings : FinanceDestination
    data class PlanDetail(val type: PlanType, val id: String) : FinanceDestination
    data class PlanForm(val type: PlanType, val id: String? = null) : FinanceDestination
    data class PlanHistory(val type: PlanType, val id: String) : FinanceDestination
    data class PlanDocuments(val type: PlanType, val id: String) : FinanceDestination
    data class PlanFinancing(val type: PlanType, val id: String) : FinanceDestination
    data class PlanPayment(val type: PlanType, val id: String) : FinanceDestination
    data class ExpenseForm(val id: String? = null) : FinanceDestination
}

enum class PlanType { EMI, LOAN, DEBT }
