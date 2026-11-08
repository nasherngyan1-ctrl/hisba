package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CompanyEntity
import com.example.data.model.ParcelEntity
import com.example.data.repository.HesabatRepository
import com.example.ui.util.NumberUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class CompanyStats(
    val company: CompanyEntity,
    val parcelCount: Int,
    val totalAmount: Double,
    val unsettledCount: Int
)

data class HesabatUiState(
    val selectedMonthKey: String = NumberUtils.getCurrentMonthKey(),
    val companies: List<CompanyEntity> = emptyList(),
    val parcels: List<ParcelEntity> = emptyList(),
    val defaultRate: Double = 250.0,
    val selectedCompanyFilterId: Long? = null,
    val bannerMessage: String? = null
) {
    // Current filtered parcels by selected company (if any)
    val displayParcels: List<ParcelEntity>
        get() = if (selectedCompanyFilterId != null) {
            parcels.filter { it.companyId == selectedCompanyFilterId }
        } else {
            parcels
        }

    // Grand totals for current month
    val totalParcelsCount: Int get() = parcels.size
    val totalParcelsAmount: Double get() = parcels.sumOf { it.rateIqd }

    // Breakdown per company
    val companyBreakdown: List<CompanyStats>
        get() {
            return companies.map { company ->
                val companyParcels = parcels.filter { it.companyId == company.id }
                CompanyStats(
                    company = company,
                    parcelCount = companyParcels.size,
                    totalAmount = companyParcels.sumOf { it.rateIqd },
                    unsettledCount = companyParcels.count { !it.isSettled }
                )
            }.sortedByDescending { it.parcelCount }
        }
}

class HesabatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HesabatRepository(application)

    private val _selectedMonthKey = MutableStateFlow(NumberUtils.getCurrentMonthKey())
    private val _defaultRate = MutableStateFlow(250.0)
    private val _selectedCompanyFilterId = MutableStateFlow<Long?>(null)
    private val _bannerMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HesabatUiState> = combine(
        _selectedMonthKey,
        repository.getAllCompanies(),
        _defaultRate,
        _selectedCompanyFilterId,
        _bannerMessage
    ) { monthKey, companies, rate, filterId, banner ->
        StateHolder(monthKey, companies, rate, filterId, banner)
    }.combine(
        repository.getAllParcels()
    ) { holder, allParcels ->
        val monthParcels = allParcels.filter { it.monthKey == holder.monthKey }
        HesabatUiState(
            selectedMonthKey = holder.monthKey,
            companies = holder.companies,
            parcels = monthParcels,
            defaultRate = holder.rate,
            selectedCompanyFilterId = holder.filterId,
            bannerMessage = holder.banner
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HesabatUiState()
    )

    private data class StateHolder(
        val monthKey: String,
        val companies: List<CompanyEntity>,
        val rate: Double,
        val filterId: Long?,
        val banner: String?
    )

    fun changeMonth(newMonthKey: String) {
        _selectedMonthKey.value = newMonthKey
    }

    fun nextMonth() {
        _selectedMonthKey.value = NumberUtils.getNextMonthKey(_selectedMonthKey.value)
    }

    fun previousMonth() {
        _selectedMonthKey.value = NumberUtils.getPreviousMonthKey(_selectedMonthKey.value)
    }

    fun setCompanyFilter(companyId: Long?) {
        _selectedCompanyFilterId.value = companyId
    }

    fun updateDefaultRate(newRate: Double) {
        if (newRate > 0) {
            _defaultRate.value = newRate
            showBanner("نرخی هەر بەریدێک کرا بە ${NumberUtils.formatIqd(newRate)}")
        }
    }

    fun createCameraTempUri(): Pair<File, Uri> {
        return repository.createTempCameraImageUri()
    }

    fun addParcelFromUri(
        companyId: Long?,
        sourceUri: Uri,
        rate: Double,
        customerInfo: String,
        notes: String
    ) {
        viewModelScope.launch {
            try {
                val localPath = repository.copyUriToInternalStorage(sourceUri)
                repository.addParcel(
                    companyId = companyId,
                    imagePath = localPath,
                    rateIqd = rate,
                    customerInfo = customerInfo,
                    notes = notes,
                    monthKey = _selectedMonthKey.value
                )
                showBanner("بەرید تۆمارکرا (+${NumberUtils.formatIqd(rate)})")
            } catch (e: Exception) {
                showBanner("هەڵە لە پاشەکەوتکردنی وێنە")
            }
        }
    }

    fun addParcelFromCameraFile(
        companyId: Long?,
        file: File,
        rate: Double,
        customerInfo: String,
        notes: String
    ) {
        viewModelScope.launch {
            try {
                if (file.exists()) {
                    repository.addParcel(
                        companyId = companyId,
                        imagePath = file.absolutePath,
                        rateIqd = rate,
                        customerInfo = customerInfo,
                        notes = notes,
                        monthKey = _selectedMonthKey.value
                    )
                    showBanner("وێنەی بەرید تۆمارکرا (+${NumberUtils.formatIqd(rate)})")
                }
            } catch (e: Exception) {
                showBanner("هەڵە لە وێنەگرتن")
            }
        }
    }

    fun deleteParcel(parcel: ParcelEntity) {
        viewModelScope.launch {
            repository.deleteParcel(parcel)
            showBanner("بەرید سڕایەوە")
        }
    }

    fun resetCurrentMonthParcels() {
        viewModelScope.launch {
            repository.resetMonthParcels(_selectedMonthKey.value)
            showBanner("ژمێرەری ئەم مانگە بە سەرکەوتوویی سفڕکرایەوە")
        }
    }

    fun settleCompanyParcels(companyId: Long, isSettled: Boolean) {
        viewModelScope.launch {
            repository.setCompanyParcelsSettled(companyId, _selectedMonthKey.value, isSettled)
            showBanner(if (isSettled) "حسابات پاکتاوکرا" else "حسابات گەڕێندرایەوە")
        }
    }

    // --- Companies ---
    fun addCompany(name: String, phone: String, notes: String, rate: Double) {
        viewModelScope.launch {
            repository.addCompany(name, phone, notes, rate)
            showBanner("پەیج زیادکرا")
        }
    }

    fun updateCompany(company: CompanyEntity) {
        viewModelScope.launch {
            repository.updateCompany(company)
            showBanner("زانیاری پەیج نوێکرایەوە")
        }
    }

    fun deleteCompany(company: CompanyEntity) {
        viewModelScope.launch {
            repository.deleteCompany(company)
            showBanner("پەیج سڕایەوە")
        }
    }

    fun clearBanner() {
        _bannerMessage.value = null
    }

    private fun showBanner(msg: String) {
        _bannerMessage.value = msg
    }
}
