package com.mdzahidalam.myfinancetracker.domain.model

import java.util.UUID

data class Payment(
    val number: Int, val dueDate: Long, val amount: Double, val paidDate: Long? = null,
    val status: String = if (paidDate == null) "PENDING" else "PAID", val notes: String = "",
    val receiptUri: String? = null, val paymentMethod: String = "Not recorded",
    val paymentChannel: String = "", val referenceNumber: String = "", val counterparty: String = "",
    val attachments: List<Attachment> = emptyList(), val appliedRequestId: String = "",
    val accountNumber: String = "", val branch: String = "", val routingNumber: String = "",
    val methodDetails: String = ""
)

data class Attachment(
    val id: String = UUID.randomUUID().toString(), val name: String,
    val mimeType: String, val contentBase64: String
)

data class PaymentRequest(
    val id: String = UUID.randomUUID().toString(), val requestNumber: String,
    val createdDate: Long, val dueDate: Long?, val amount: Double, val paymentMethod: String,
    val paymentInstructions: String, val message: String, val status: String = "UNPAID",
    val receivedAmount: Double = 0.0, val paymentChannel: String = "", val accountName: String = "",
    val accountNumber: String = "", val branch: String = "", val routingNumber: String = "",
    val referenceNumber: String = "", val methodDetails: String = ""
)

data class ReceiptProfile(
    val fullName: String = "", val phone: String = "", val email: String = "",
    val address: String = "", val signature: Attachment? = null
)

data class EmiItem(
    val id: String = UUID.randomUUID().toString(), val name: String, val category: String,
    val seller: String, val price: Double, val downPayment: Double, val financedAmount: Double,
    val interestRate: Double, val interestAmount: Double, val totalPayable: Double,
    val installments: Int, val monthlyPayment: Double, val startDate: Long, val dueDay: Int,
    val reminderDays: List<Int>, val payments: List<Payment>, val archived: Boolean = false,
    val financingSource: String = "", val receivedMethod: String = "",
    val agreementReference: String = "", val financingNotes: String = "",
    val attachments: List<Attachment> = emptyList()
)

data class Loan(
    val id: String = UUID.randomUUID().toString(), val name: String, val type: String,
    val lender: String, val principal: Double, val interestRate: Double,
    val interestAmount: Double, val totalPayable: Double, val installments: Int,
    val monthlyPayment: Double, val startDate: Long, val dueDay: Int,
    val reminderDays: List<Int>, val payments: List<Payment>, val repaymentMode: String = "EQUAL",
    val archived: Boolean = false, val financingSource: String = "", val receivedMethod: String = "",
    val agreementReference: String = "", val financingNotes: String = "",
    val attachments: List<Attachment> = emptyList()
)

data class Debt(
    val id: String = UUID.randomUUID().toString(), val name: String, val direction: String,
    val originalAmount: Double, val dueDate: Long?, val notes: String, val payments: List<Payment>,
    val debtDate: Long = System.currentTimeMillis(), val archived: Boolean = false,
    val reason: String = "", val receivedOrGivenMethod: String = "", val referenceNumber: String = "",
    val attachments: List<Attachment> = emptyList(),
    val paymentRequests: List<PaymentRequest> = emptyList()
)

data class Expense(
    val id: String = UUID.randomUUID().toString(), val title: String, val category: String,
    val amount: Double, val date: Long, val notes: String,
    val attachments: List<Attachment> = emptyList()
)

data class FinanceData(
    val emis: List<EmiItem> = emptyList(), val loans: List<Loan> = emptyList(),
    val debts: List<Debt> = emptyList(), val expenses: List<Expense> = emptyList(),
    val receiptProfile: ReceiptProfile = ReceiptProfile()
)
