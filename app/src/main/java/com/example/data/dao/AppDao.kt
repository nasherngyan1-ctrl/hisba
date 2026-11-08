package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CompanyEntity
import com.example.data.model.DebtEntity
import com.example.data.model.FinanceTransactionEntity
import com.example.data.model.ParcelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Query("SELECT * FROM companies ORDER BY name ASC")
    fun getAllCompanies(): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM companies WHERE id = :id LIMIT 1")
    suspend fun getCompanyById(id: Long): CompanyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: CompanyEntity): Long

    @Update
    suspend fun updateCompany(company: CompanyEntity)

    @Delete
    suspend fun deleteCompany(company: CompanyEntity)
}

@Dao
interface ParcelDao {
    @Query("SELECT * FROM parcels ORDER BY timestamp DESC")
    fun getAllParcels(): Flow<List<ParcelEntity>>

    @Query("SELECT * FROM parcels WHERE monthKey = :monthKey ORDER BY timestamp DESC")
    fun getParcelsByMonth(monthKey: String): Flow<List<ParcelEntity>>

    @Query("SELECT * FROM parcels WHERE companyId = :companyId ORDER BY timestamp DESC")
    fun getParcelsByCompany(companyId: Long): Flow<List<ParcelEntity>>

    @Query("SELECT * FROM parcels WHERE companyId = :companyId AND monthKey = :monthKey ORDER BY timestamp DESC")
    fun getParcelsByCompanyAndMonth(companyId: Long, monthKey: String): Flow<List<ParcelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParcel(parcel: ParcelEntity): Long

    @Delete
    suspend fun deleteParcel(parcel: ParcelEntity)

    @Query("DELETE FROM parcels WHERE id = :id")
    suspend fun deleteParcelById(id: Long)

    @Query("DELETE FROM parcels WHERE monthKey = :monthKey")
    suspend fun clearMonthParcels(monthKey: String)

    @Query("UPDATE parcels SET isSettled = :isSettled WHERE companyId = :companyId AND monthKey = :monthKey")
    suspend fun setCompanyParcelsSettled(companyId: Long, monthKey: String, isSettled: Boolean)

    @Query("DELETE FROM parcels")
    suspend fun clearAllParcels()
}

@Dao
interface FinanceDao {
    @Query("SELECT * FROM transactions WHERE monthKey = :monthKey ORDER BY timestamp DESC")
    fun getTransactionsByMonth(monthKey: String): Flow<List<FinanceTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinanceTransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY timestamp DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Query("UPDATE debts SET isPaid = :isPaid WHERE id = :id")
    suspend fun updateDebtStatus(id: Long, isPaid: Boolean)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebtById(id: Long)
}
