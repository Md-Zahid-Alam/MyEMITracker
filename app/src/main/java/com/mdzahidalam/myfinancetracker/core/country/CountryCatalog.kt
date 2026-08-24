package com.mdzahidalam.myfinancetracker.core.country

import java.util.Currency
import java.util.Locale

/** Offline ISO-3166 country metadata. No network access or bundled flag images are required. */
data class CountryOption(
    val isoCode: String,
    val name: String,
    val currencyCode: String,
    val currencySymbol: String
) {
    val bengaliName: String
        get() = Locale.Builder().setRegion(isoCode).build().getDisplayCountry(Locale("bn"))

    fun displayName(language: String): String = if (language == "BN" && bengaliName.isNotBlank()) bengaliName else name
    val flag: String
        get() = isoCode.uppercase(Locale.ROOT).map { codePoint ->
            Character.toChars(0x1F1E6 + (codePoint.code - 'A'.code)).concatToString()
        }.joinToString("")

    val searchText: String
        get() = "$name $bengaliName $isoCode $currencyCode".lowercase(Locale.ROOT)
}

object CountryCatalog {
    /** Android's bundled ISO list: all ISO-3166-1 countries and territories available offline. */
    val all: List<CountryOption> by lazy {
        Locale.getISOCountries().mapNotNull { iso ->
            val locale = Locale.Builder().setRegion(iso).build()
            val name = locale.getDisplayCountry(Locale.ENGLISH).takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val currency = runCatching { Currency.getInstance(locale) }.getOrNull()
            CountryOption(
                isoCode = iso,
                name = name,
                currencyCode = currency?.currencyCode ?: "XXX",
                currencySymbol = currency?.getSymbol(locale)?.takeIf { it.isNotBlank() } ?: "¤"
            )
        }.sortedBy(CountryOption::name)
    }

    fun findByName(name: String): CountryOption? = all.firstOrNull { it.name.equals(name, ignoreCase = true) }

    fun search(query: String): List<CountryOption> {
        val normalized = query.trim().lowercase(Locale.ROOT)
        return if (normalized.isEmpty()) all else all.filter { normalized in it.searchText }
    }
}
