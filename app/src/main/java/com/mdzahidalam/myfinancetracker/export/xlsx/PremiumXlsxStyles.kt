package com.mdzahidalam.myfinancetracker.export.xlsx

internal object PremiumXlsxStyles {
    fun xml(currencySymbol: String): String {
        val symbol = OoxmlRules.sanitizeXmlText(currencySymbol)
        return """<?xml version="1.0" encoding="UTF-8"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <numFmts count="2"><numFmt numFmtId="164" formatCode="$symbol#,##0.00"/><numFmt numFmtId="165" formatCode="dd mmm yyyy"/></numFmts>
  <fonts count="5">
    <font><sz val="11"/><name val="Calibri"/><color rgb="FF232A2A"/></font>
    <font><b/><color rgb="FFFFFFFF"/><sz val="11"/><name val="Calibri"/></font>
    <font><b/><color rgb="FF005251"/><sz val="11"/><name val="Calibri"/></font>
    <font><color rgb="FF5C6766"/><sz val="10"/><name val="Calibri"/></font>
    <font><u/><color rgb="FF007C7A"/><sz val="10"/><name val="Calibri"/></font>
  </fonts>
  <fills count="8">
    <fill><patternFill patternType="none"/></fill><fill><patternFill patternType="gray125"/></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FF007C7A"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFF3F8F7"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFE2F5F2"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFE4F4EA"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFFFF0D5"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFF8E3E3"/></patternFill></fill>
  </fills>
  <borders count="2"><border/><border><bottom style="thin"><color rgb="FFD9E2E1"/></bottom></border></borders>
  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
  <cellXfs count="17">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
    <xf numFmtId="0" fontId="1" fillId="2" borderId="0" applyFill="1" applyFont="1" applyAlignment="1"><alignment vertical="center"/></xf>
    <xf numFmtId="164" fontId="0" fillId="0" borderId="1" applyNumberFormat="1"/>
    <xf numFmtId="0" fontId="0" fillId="0" borderId="1"/>
    <xf numFmtId="0" fontId="0" fillId="0" borderId="1" applyAlignment="1"><alignment vertical="top" wrapText="1"/></xf>
    <xf numFmtId="0" fontId="0" fillId="3" borderId="1" applyFill="1" applyAlignment="1"><alignment vertical="top" wrapText="1"/></xf>
    <xf numFmtId="164" fontId="0" fillId="3" borderId="1" applyFill="1" applyNumberFormat="1"/>
    <xf numFmtId="0" fontId="2" fillId="5" borderId="1" applyFill="1" applyFont="1"/>
    <xf numFmtId="0" fontId="2" fillId="6" borderId="1" applyFill="1" applyFont="1"/>
    <xf numFmtId="0" fontId="2" fillId="7" borderId="1" applyFill="1" applyFont="1"/>
    <xf numFmtId="0" fontId="3" fillId="3" borderId="1" applyFill="1" applyFont="1"/>
    <xf numFmtId="0" fontId="3" fillId="0" borderId="0" applyFont="1"/>
    <xf numFmtId="165" fontId="0" fillId="0" borderId="1" applyNumberFormat="1"/>
    <xf numFmtId="165" fontId="0" fillId="3" borderId="1" applyFill="1" applyNumberFormat="1"/>
    <xf numFmtId="10" fontId="0" fillId="0" borderId="1" applyNumberFormat="1"/>
    <xf numFmtId="10" fontId="0" fillId="3" borderId="1" applyFill="1" applyNumberFormat="1"/>
    <xf numFmtId="0" fontId="4" fillId="0" borderId="0" applyFont="1"/>
  </cellXfs>
</styleSheet>""".trimIndent()
    }
}
