package com.mdzahidalam.myfinancetracker.domain.repository

import com.mdzahidalam.myfinancetracker.domain.model.FinanceData

interface FinanceDataRepository {
    fun load(): FinanceData
    fun save(data: FinanceData)
    fun backup(password: String): String
    fun restore(content: String, password: String?, allowLegacy: Boolean = false): FinanceData
}
