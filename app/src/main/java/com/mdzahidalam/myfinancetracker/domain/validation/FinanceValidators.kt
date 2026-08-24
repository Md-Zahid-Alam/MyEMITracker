package com.mdzahidalam.myfinancetracker.domain.validation

object FinanceValidators {
    fun required(value: String, label: String, maxLength: Int = 120): String? = when {
        value.isBlank() -> "$label is required."
        value.trim().length > maxLength -> "$label must be $maxLength characters or less."
        else -> null
    }

    fun positiveAmount(value: String, label: String = "Amount"): String? {
        val number = value.toDoubleOrNull()
        return when {
            number == null || !number.isFinite() -> "Enter a valid $label."
            number <= 0.0 -> "$label must be greater than zero."
            number > 999_999_999_999.99 -> "$label is too large."
            else -> null
        }
    }

    fun optionalEmail(value: String): String? = when {
        value.isBlank() -> null
        !Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", RegexOption.IGNORE_CASE).matches(value.trim()) -> "Enter a valid email address."
        else -> null
    }

    fun optionalPhone(value: String): String? {
        if (value.isBlank()) return null
        val digits = value.filter(Char::isDigit)
        return if (!Regex("^\\+?[0-9][0-9 -]*$").matches(value.trim()) || digits.length !in 7..15) {
            "Enter a valid phone number containing 7–15 digits."
        } else null
    }

    fun installmentCount(value: String): String? = when (val number = value.toIntOrNull()) {
        null -> "Enter a whole number of installments."
        !in 1..600 -> "Installments must be between 1 and 600."
        else -> null
    }

    fun dueDay(value: String): String? = when (val number = value.toIntOrNull()) {
        null -> "Enter a valid due day."
        !in 1..28 -> "Due day must be between 1 and 28."
        else -> null
    }

    fun percentage(value: String): String? = when (val number = value.toDoubleOrNull()) {
        null -> "Enter a valid percentage."
        !in 0.0..100.0 -> "Percentage must be between 0 and 100."
        else -> null
    }
}
