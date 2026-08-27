package com.mdzahidalam.myfinancetracker.export

import com.mdzahidalam.myfinancetracker.export.xlsx.OoxmlRules
import org.junit.Test

class OoxmlRulesTest {
    @Test fun desktopExcelWorksheetOrderIsAccepted() {
        OoxmlRules.requireValidWorksheet("<?xml version=\"1.0\"?><worksheet><sheetFormatPr/><cols></cols><sheetData></sheetData></worksheet>")
    }

    @Test(expected = IllegalArgumentException::class)
    fun oldBrokenWorksheetOrderIsRejected() {
        OoxmlRules.requireValidWorksheet("<?xml version=\"1.0\"?><worksheet><cols></cols><sheetData></sheetData><sheetFormatPr/></worksheet>")
    }
}
