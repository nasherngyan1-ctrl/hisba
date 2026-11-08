package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.CompanyEntity
import com.example.ui.util.NumberUtils
import java.io.File

@Composable
fun AddParcelDialog(
    imageUri: Uri?,
    imageFile: File?,
    companies: List<CompanyEntity>,
    defaultRate: Double,
    onDismiss: () -> Unit,
    onSave: (companyId: Long?, rate: Double, customerInfo: String, notes: String) -> Unit,
    onQuickAddCompany: (name: String, phone: String) -> Unit
) {
    var selectedCompanyId by remember { mutableStateOf<Long?>(null) }
    var rateText by remember { mutableStateOf(defaultRate.toInt().toString()) }
    var customerInfo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var companyDropdownExpanded by remember { mutableStateOf(false) }
    var showQuickAddCompanyDialog by remember { mutableStateOf(false) }

    val selectedCompany = companies.find { it.id == selectedCompanyId }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تۆمارکردنی بەرید",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "داخستن")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Image Preview Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = imageFile ?: imageUri,
                            contentDescription = "وێنەی بەرید",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        // Price tag overlay
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "${NumberUtils.formatIqd(rateText.toDoubleOrNull() ?: defaultRate)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Select Company / Page
                Text(
                    text = "پەیج:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCompany?.name ?: "بەریدی گشتی (بێ پەیج)",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { companyDropdownExpanded = true }
                            .testTag("company_selector_input"),
                        leadingIcon = {
                            Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            Text(
                                text = "گۆڕین ▾",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .clickable { companyDropdownExpanded = true }
                                    .padding(end = 12.dp),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    )

                    DropdownMenu(
                        expanded = companyDropdownExpanded,
                        onDismissRequest = { companyDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("بەریدی گشتی (بێ پەیج)") },
                            onClick = {
                                selectedCompanyId = null
                                companyDropdownExpanded = false
                            }
                        )
                        companies.forEach { company ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(company.name, fontWeight = FontWeight.Bold)
                                        if (company.phoneNumber.isNotBlank()) {
                                            Text(
                                                company.phoneNumber,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedCompanyId = company.id
                                    rateText = company.ratePerParcel.toInt().toString()
                                    companyDropdownExpanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "➕ پەیجی نوێ",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = {
                                companyDropdownExpanded = false
                                showQuickAddCompanyDialog = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rate Input
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it },
                    label = { Text("نرخی هەر بەریدێک (د.ع)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parcel_rate_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Recipient / Location note
                OutlinedTextField(
                    value = customerInfo,
                    onValueChange = { customerInfo = it },
                    label = { Text("ناوی وەرگر یان شوێن (ئارەزوومەندانە)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("تێبینی یان ژمارەی پسوولە") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        val parsedRate = rateText.toDoubleOrNull() ?: defaultRate
                        onSave(selectedCompanyId, parsedRate, customerInfo, notes)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_parcel_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "تۆمارکردن (+${NumberUtils.formatIqd(rateText.toDoubleOrNull() ?: defaultRate)})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showQuickAddCompanyDialog) {
        QuickAddCompanyDialog(
            defaultRate = defaultRate,
            onDismiss = { showQuickAddCompanyDialog = false },
            onConfirm = { name, phone ->
                onQuickAddCompany(name, phone)
                showQuickAddCompanyDialog = false
            }
        )
    }
}

@Composable
fun QuickAddCompanyDialog(
    defaultRate: Double,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "پەیجی نوێ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ناوی پەیج *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("ژمارەی مۆبایل") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("پەشیمانمەوە")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name, phone)
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("زیادکردن")
                    }
                }
            }
        }
    }
}
