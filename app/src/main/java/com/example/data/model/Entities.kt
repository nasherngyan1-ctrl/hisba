package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Company or Social Media Page that the courier/user delivers parcels for.
 */
@Entity(tableName = "companies")
data class CompanyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String = "",
    val notes: String = "",
    val ratePerParcel: Double = 250.0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Parcel entry with an attached photo, price per parcel (default 250 IQD),
 * company association, and month tracking.
 */
@Entity(tableName = "parcels")
data class ParcelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val companyId: Long? = null,
    val imagePath: String,
    val rateIqd: Double = 250.0,
    val customerInfo: String = "",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val monthKey: String, // e.g. "2026-11"
    val isSettled: Boolean = false // Cleared or settled with the company
)

/**
 * General monthly financial transactions (Income / Expense).
 */
@Entity(tableName = "transactions")
data class FinanceTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val isExpense: Boolean,
    val category: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val monthKey: String
)

/**
 * Personal debts and credits (قەرزم لایە یان قەرزارم).
 */
@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personName: String,
    val phoneNumber: String = "",
    val amount: Double,
    val isLent: Boolean, // true = قەرزم لایەتی (They owe me), false = قەرزارم (I owe them)
    val notes: String = "",
    val dueDate: String = "",
    val isPaid: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
