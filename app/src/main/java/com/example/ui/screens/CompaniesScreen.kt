package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.CompanyEntity
import com.example.ui.util.NumberUtils
import com.example.ui.viewmodel.CompanyStats
import com.example.ui.viewmodel.HesabatUiState
import com.example.ui.viewmodel.HesabatViewModel

@Composable
fun CompaniesScreen(
    state: HesabatUiState,
    viewModel: HesabatViewModel,
    onFilterByCompany: (Long) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var companyToEdit by remember { mutableStateOf<CompanyEntity?>(null) }
    var companyToDelete by remember { mutableStateOf<CompanyEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("add_company_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "پەیجی نوێ",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "پەیجەکانی کارکردن (${state.companies.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("زیادکردن", style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (state.companyBreakdown.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "هیچ پەیجێک تۆمار نەکراوە",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ناوی ئەو پەیجانە تۆمار بکە کە بەریدیان بۆ دەبەیت تا حساباتیان بە وردی دیاربێت.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.companyBreakdown, key = { it.company.id }) { stat ->
                    MinimalCompanyCard(
                        stat = stat,
                        onCall = { phone ->
                            if (phone.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                context.startActivity(intent)
                            }
                        },
                        onEdit = { companyToEdit = stat.company },
                        onDelete = { companyToDelete = stat.company },
                        onViewParcels = { onFilterByCompany(stat.company.id) },
                        onSettleToggle = { isSettled ->
                            viewModel.settleCompanyParcels(stat.company.id, isSettled)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        MinimalCompanyFormDialog(
            company = null,
            defaultRate = state.defaultRate,
            onDismiss = { showAddDialog = false },
            onSave = { name, phone, notes, rate ->
                viewModel.addCompany(name, phone, notes, rate)
                showAddDialog = false
            }
        )
    }

    if (companyToEdit != null) {
        MinimalCompanyFormDialog(
            company = companyToEdit,
            defaultRate = state.defaultRate,
            onDismiss = { companyToEdit = null },
            onSave = { name, phone, notes, rate ->
                viewModel.updateCompany(
                    companyToEdit!!.copy(
                        name = name,
                        phoneNumber = phone,
                        notes = notes,
                        ratePerParcel = rate
                    )
                )
                companyToEdit = null
            }
        )
    }

    if (companyToDelete != null) {
        AlertDialog(
            onDismissRequest = { companyToDelete = null },
            title = { Text("سڕینەوەی پەیج", fontWeight = FontWeight.Bold) },
            text = { Text("دڵنیایت لە سڕینەوەی (${companyToDelete?.name})؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCompany(companyToDelete!!)
                        companyToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("سڕینەوە", color = MaterialTheme.colorScheme.surface)
                }
            },
            dismissButton = {
                TextButton(onClick = { companyToDelete = null }) {
                    Text("پەشیمانمەوە")
                }
            }
        )
    }
}

@Composable
fun MinimalCompanyCard(
    stat: CompanyStats,
    onCall: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewParcels: () -> Unit,
    onSettleToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .testTag("company_card_${stat.company.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Name & Call/Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stat.company.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (stat.company.phoneNumber.isNotBlank()) {
                        Text(
                            text = stat.company.phoneNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    if (stat.company.phoneNumber.isNotBlank()) {
                        IconButton(
                            onClick = { onCall(stat.company.phoneNumber) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "پەیوەندیکردن",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "دەستکاری",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "سڕینەوە",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics box for this company
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${NumberUtils.formatNumber(stat.parcelCount)} بەرید",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = NumberUtils.formatIqd(stat.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (stat.company.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stat.company.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onViewParcels) {
                    Text(
                        "بینینی بەریدەکان (${stat.parcelCount}) ◀",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (stat.parcelCount > 0) {
                    OutlinedButton(
                        onClick = { onSettleToggle(stat.unsettledCount > 0) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (stat.unsettledCount == 0) "پاکتاوکراوە ✓" else "پاکتاوکردنی حسابات",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MinimalCompanyFormDialog(
    company: CompanyEntity?,
    defaultRate: Double,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, notes: String, rate: Double) -> Unit
) {
    var name by remember { mutableStateOf(company?.name ?: "") }
    var phone by remember { mutableStateOf(company?.phoneNumber ?: "") }
    var notes by remember { mutableStateOf(company?.notes ?: "") }
    var rateText by remember {
        mutableStateOf(
            company?.ratePerParcel?.toInt()?.toString() ?: defaultRate.toInt().toString()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (company == null) "پەیجی نوێ" else "دەستکاریکردنی پەیج",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ناوی پەیج *") },
                    placeholder = { Text("بۆ نموونە: پەیجی ستایل") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("company_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("ژمارەی مۆبایل") },
                    placeholder = { Text("0750 xxx xxxx") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it },
                    label = { Text("نرخی هەر بەریدێک (د.ع)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("تێبینی (ئارەزوومەندانە)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val parsedRate = rateText.toDoubleOrNull() ?: defaultRate
                        onSave(name.trim(), phone.trim(), notes.trim(), parsedRate)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.testTag("save_company_button")
            ) {
                Text("پاشەکەوتکردن", color = MaterialTheme.colorScheme.surface)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("پەشیمانمەوە")
            }
        }
    )
}
