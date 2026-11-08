package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CompanyDao
import com.example.data.dao.DebtDao
import com.example.data.dao.FinanceDao
import com.example.data.dao.ParcelDao
import com.example.data.model.CompanyEntity
import com.example.data.model.DebtEntity
import com.example.data.model.FinanceTransactionEntity
import com.example.data.model.ParcelEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CompanyEntity::class,
        ParcelEntity::class,
        FinanceTransactionEntity::class,
        DebtEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun parcelDao(): ParcelDao
    abstract fun financeDao(): FinanceDao
    abstract fun debtDao(): DebtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hesabati_xom.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-seed initial sample pages/companies in background
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).let { database ->
                                    database.companyDao().insertCompany(
                                        CompanyEntity(
                                            name = "پەیجی ستایل و فاشن",
                                            phoneNumber = "0750 111 2233",
                                            notes = "ڕۆژانە بەریدی هەیە لە هەولێر",
                                            ratePerParcel = 250.0
                                        )
                                    )
                                    database.companyDao().insertCompany(
                                        CompanyEntity(
                                            name = "ئۆنلاین شۆپ کوردستان",
                                            phoneNumber = "0770 444 5566",
                                            notes = "گەیاندنی کاڵای جوانکاری",
                                            ratePerParcel = 250.0
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
