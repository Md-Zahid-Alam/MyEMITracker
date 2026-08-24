package com.mdzahidalam.myfinancetracker.core

import com.mdzahidalam.myfinancetracker.core.country.CountryCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryCatalogTest {
    @Test fun catalogContainsIsoCountriesAndBangladeshDefaults() {
        assertTrue(CountryCatalog.all.size >= 240)
        val bangladesh = CountryCatalog.findByName("Bangladesh")!!
        assertEquals("BD", bangladesh.isoCode)
        assertEquals("BDT", bangladesh.currencyCode)
    }

    @Test fun searchSupportsIsoAndCurrencyCodes() {
        assertTrue(CountryCatalog.search("bd").any { it.isoCode == "BD" })
        assertTrue(CountryCatalog.search("jpy").any { it.isoCode == "JP" })
    }
}
