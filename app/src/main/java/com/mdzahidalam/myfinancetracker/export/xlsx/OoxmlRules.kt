package com.mdzahidalam.myfinancetracker.export.xlsx

object OoxmlRules {
    fun sanitizeXmlText(value: String): String = value
        .filter { it == '\t' || it == '\n' || it == '\r' || it.code >= 0x20 }
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")

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

    fun requireValidStyles(xml: String): String {
        require(xml.startsWith("<?xml"))
        require(xml.contains("<numFmts"))
        require(xml.contains("<cellXfs"))
        require(xml.endsWith("</styleSheet>"))
        return xml
    }
}
