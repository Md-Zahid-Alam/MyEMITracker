package com.mdzahidalam.myfinancetracker.export

import com.mdzahidalam.myfinancetracker.export.xlsx.OoxmlRules
import com.mdzahidalam.myfinancetracker.export.xlsx.PremiumXlsxStyles
import org.junit.Assert.assertEquals
import org.junit.Test

class OoxmlRulesTest {
    @Test fun desktopExcelWorksheetOrderIsAccepted() {
        OoxmlRules.requireValidWorksheet("<?xml version=\"1.0\"?><worksheet><sheetFormatPr/><cols></cols><sheetData></sheetData></worksheet>")
    }

    @Test(expected = IllegalArgumentException::class)
    fun oldBrokenWorksheetOrderIsRejected() {
        OoxmlRules.requireValidWorksheet("<?xml version=\"1.0\"?><worksheet><cols></cols><sheetData></sheetData><sheetFormatPr/></worksheet>")
    }

    @Test fun userTextIsSafeForDesktopExcelXml() {
        val safe = OoxmlRules.sanitizeXmlText("Bank & <Branch>\u0001")
        assertEquals("Bank &amp; &lt;Branch&gt;", safe)
    }

    @Test fun premiumStylesStructureIsAccepted() {
        OoxmlRules.requireValidStyles("<?xml version=\"1.0\"?><styleSheet><numFmts/><cellXfs/></styleSheet>")
    }

    @Test fun premiumReportStylesContainEveryReferencedFormat() {
        val styles = PremiumXlsxStyles.xml("৳")
        OoxmlRules.requireValidStyles(styles)
        assertEquals(true, styles.contains("cellXfs count=\"17\""))
        assertEquals(true, styles.contains("numFmtId=\"165\""))
        assertEquals(true, styles.contains("fontId=\"4\""))
    }
}
