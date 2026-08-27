package com.mdzahidalam.myfinancetracker.export.xlsx

object OoxmlRules {
    fun requireValidWorksheet(xml: String): String {
        require(xml.startsWith("<?xml"))
        val format = xml.indexOf("<sheetFormatPr")
        val columns = xml.indexOf("<cols>")
        val data = xml.indexOf("<sheetData>")
        require(format >= 0 && columns > format && data > columns) {
            "Invalid OOXML worksheet element order."
        }
        require(xml.endsWith("</worksheet>"))
        return xml
    }
}
