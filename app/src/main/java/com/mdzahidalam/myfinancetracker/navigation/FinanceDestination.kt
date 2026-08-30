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

/** Validated route value used by the compatibility navigator; UI state no longer stores a raw route string. */
@JvmInline
value class FinanceRoute private constructor(val key: String) {
    companion object {
        val None = FinanceRoute("")
        private val allowed = Regex("^(emi|loan|debt)(_(detail|history|documents|financing|information|payment))?$|^(expense|password|about|receipt_profile|country)$")
        fun of(key: String): FinanceRoute {
            require(key.isEmpty() || allowed.matches(key)) { "Unknown finance destination: $key" }
            return FinanceRoute(key)
        }
    }

    val isNone: Boolean get() = key.isEmpty()
    fun hasSuffix(suffix: String): Boolean = key.endsWith(suffix)
    fun withoutSuffix(suffix: String): String = key.removeSuffix(suffix)
}

enum class PaymentSection(val key: String) {
    NONE(""), EMI("EMI"), LOANS("Loans"), DEBTS_OWE("DebtsOwe"), DEBTS_OWED("DebtsOwed");

    companion object {
        fun fromKey(key: String): PaymentSection = entries.firstOrNull { it.key == key }
            ?: throw IllegalArgumentException("Unknown payment section: $key")
    }
}
