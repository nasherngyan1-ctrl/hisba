package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.db.AppDatabase
import com.example.data.model.CompanyEntity
import com.example.data.model.DebtEntity
import com.example.data.model.FinanceTransactionEntity
import com.example.data.model.ParcelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class HesabatRepository(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val companyDao = database.companyDao()
    private val parcelDao = database.parcelDao()
    private val financeDao = database.financeDao()
    private val debtDao = database.debtDao()

    // --- Companies ---
    fun getAllCompanies(): Flow<List<CompanyEntity>> = companyDao.getAllCompanies()

    suspend fun getCompanyById(id: Long): CompanyEntity? = companyDao.getCompanyById(id)

    suspend fun addCompany(name: String, phone: String, notes: String, rate: Double = 250.0): Long {
        val entity = CompanyEntity(
            name = name.trim(),
            phoneNumber = phone.trim(),
            notes = notes.trim(),
            ratePerParcel = rate
        )
        return companyDao.insertCompany(entity)
    }

    suspend fun updateCompany(company: CompanyEntity) = companyDao.updateCompany(company)

    suspend fun deleteCompany(company: CompanyEntity) = companyDao.deleteCompany(company)

    // --- Parcels ---
    fun getAllParcels(): Flow<List<ParcelEntity>> = parcelDao.getAllParcels()

    fun getParcelsByMonth(monthKey: String): Flow<List<ParcelEntity>> =
        parcelDao.getParcelsByMonth(monthKey)

    fun getParcelsByCompany(companyId: Long): Flow<List<ParcelEntity>> =
        parcelDao.getParcelsByCompany(companyId)

    fun getParcelsByCompanyAndMonth(companyId: Long, monthKey: String): Flow<List<ParcelEntity>> =
        parcelDao.getParcelsByCompanyAndMonth(companyId, monthKey)

    suspend fun addParcel(
        companyId: Long?,
        imagePath: String,
        rateIqd: Double,
        customerInfo: String,
        notes: String,
        monthKey: String
    ): Long {
        val entity = ParcelEntity(
            companyId = companyId,
            imagePath = imagePath,
            rateIqd = rateIqd,
            customerInfo = customerInfo.trim(),
            notes = notes.trim(),
            monthKey = monthKey
        )
        return parcelDao.insertParcel(entity)
    }

    suspend fun deleteParcel(parcel: ParcelEntity) {
        // Delete image file if stored locally
        try {
            val file = File(parcel.imagePath)
            if (file.exists()) file.delete()
        } catch (_: Exception) {}
        parcelDao.deleteParcel(parcel)
    }

    suspend fun resetMonthParcels(monthKey: String) {
        parcelDao.clearMonthParcels(monthKey)
    }

    suspend fun setCompanyParcelsSettled(companyId: Long, monthKey: String, settled: Boolean) {
        parcelDao.setCompanyParcelsSettled(companyId, monthKey, settled)
    }

    suspend fun clearAllParcels() {
        parcelDao.clearAllParcels()
    }

    // --- Image Persistence ---
    suspend fun copyUriToInternalStorage(sourceUri: Uri): String = withContext(Dispatchers.IO) {
        val parcelDir = File(context.filesDir, "parcels").apply { if (!exists()) mkdirs() }
        val destFile = File(parcelDir, "parcel_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        destFile.absolutePath
    }

    fun createTempCameraImageUri(): Pair<File, Uri> {
        val parcelDir = File(context.filesDir, "parcels").apply { if (!exists()) mkdirs() }
        val imageFile = File(parcelDir, "camera_${System.currentTimeMillis()}.jpg")
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        return Pair(imageFile, uri)
    }

    // --- Finances ---
    fun getTransactionsByMonth(monthKey: String): Flow<List<FinanceTransactionEntity>> =
        financeDao.getTransactionsByMonth(monthKey)

    suspend fun addTransaction(
        amount: Double,
        isExpense: Boolean,
        category: String,
        note: String,
        monthKey: String
    ): Long {
        return financeDao.insertTransaction(
            FinanceTransactionEntity(
                amount = amount,
                isExpense = isExpense,
                category = category.trim(),
                note = note.trim(),
                monthKey = monthKey
            )
        )
    }

    suspend fun deleteTransaction(id: Long) = financeDao.deleteTransactionById(id)

    // --- Debts ---
    fun getAllDebts(): Flow<List<DebtEntity>> = debtDao.getAllDebts()

    suspend fun addDebt(
        personName: String,
        phone: String,
        amount: Double,
        isLent: Boolean,
        notes: String,
        dueDate: String
    ): Long {
        return debtDao.insertDebt(
            DebtEntity(
                personName = personName.trim(),
                phoneNumber = phone.trim(),
                amount = amount,
                isLent = isLent,
                notes = notes.trim(),
                dueDate = dueDate.trim()
            )
        )
    }

    suspend fun updateDebtStatus(id: Long, isPaid: Boolean) = debtDao.updateDebtStatus(id, isPaid)

    suspend fun deleteDebt(id: Long) = debtDao.deleteDebtById(id)
}
