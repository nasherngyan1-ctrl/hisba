package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CompanyEntity
import com.example.data.model.ParcelEntity
import com.example.ui.util.NumberUtils
import com.example.ui.viewmodel.HesabatUiState
import com.example.ui.viewmodel.HesabatViewModel
import java.io.File

@Composable
fun HomeScreen(
    state: HesabatUiState,
    viewModel: HesabatViewModel,
    onNavigateToCompanies: () -> Unit
) {
    var selectedParcelForView by remember { mutableStateOf<ParcelEntity?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showChangeRateDialog by remember { mutableStateOf(false) }

    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }
    var showAddParcelDialog by remember { mutableStateOf(false) }

    var tempCameraFile by remember { mutableStateOf<File?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pendingPhotoUri = uri
            pendingPhotoFile = null
            showAddParcelDialog = true
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraFile != null) {
            pendingPhotoFile = tempCameraFile
            pendingPhotoUri = tempCameraUri
            showAddParcelDialog = true
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val (file, uri) = viewModel.createCameraTempUri()
            tempCameraFile = file
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // --- 1. Clean Month Navigator ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.previousMonth() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "مانگی پێشوو",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = NumberUtils.getKurdishMonthName(state.selectedMonthKey),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = { viewModel.nextMonth() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "مانگی داهاتوو",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // --- 2. Minimalist Hero Counter Card ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                    .testTag("hero_counter_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header label and rate chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ژمێرەری بەرید",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                .clickable { showChangeRateDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${NumberUtils.formatIqd(state.defaultRate)} / دانە",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "گۆڕینی نرخ",
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Two Big Metrics: Count & Total IQD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "ژمارەی بەرید",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${NumberUtils.formatNumber(state.totalParcelsCount)} بەرید",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "کۆی گشتی پارە",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = NumberUtils.formatIqd(state.totalParcelsAmount),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // One-click reset & Pages link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { showResetConfirmDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outlineVariant)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("one_click_reset_button")
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "سفڕکردنەوە",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(onClick = onNavigateToCompanies) {
                            Text(
                                text = "پەیجەکان (${state.companies.size}) ◀",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- 3. Clean Action Buttons: Camera & Gallery ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Camera Button (Black in Light mode, White in Dark mode)
                Button(
                    onClick = {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("camera_photo_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "گرتنی وێنە",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Gallery Button (Outlined clean button)
                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .testTag("gallery_photo_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "لە گەلەری",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- 4. Company Filter Chips ---
        if (state.companyBreakdown.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedCompanyFilterId == null,
                            onClick = { viewModel.setCompanyFilter(null) },
                            label = { Text("هەمووی (${state.totalParcelsCount})") },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }

                    items(state.companyBreakdown) { stat ->
                        FilterChip(
                            selected = state.selectedCompanyFilterId == stat.company.id,
                            onClick = {
                                if (state.selectedCompanyFilterId == stat.company.id) {
                                    viewModel.setCompanyFilter(null)
                                } else {
                                    viewModel.setCompanyFilter(stat.company.id)
                                }
                            },
                            label = { Text("${stat.company.name} (${stat.parcelCount})") },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // --- 5. Parcels List Header ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "وێنەی بەریدەکان",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "${state.displayParcels.size} وێنە",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Empty state
        if (state.displayParcels.isEmpty()) {
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
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "هیچ بەریدێک لەم مانگەدا نییە",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "وێنەیەک بگرە یان لە گەلەری هەڵیبژێرە، خۆی ٢٥٠ د.ع ئەژمار دەکات.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            // Parcels List
            items(state.displayParcels, key = { it.id }) { parcel ->
                val company = state.companies.find { it.id == parcel.companyId }
                MinimalParcelCard(
                    parcel = parcel,
                    company = company,
                    onClick = { selectedParcelForView = parcel },
                    onDelete = { viewModel.deleteParcel(parcel) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // --- Dialogs ---

    if (showAddParcelDialog) {
        AddParcelDialog(
            imageUri = pendingPhotoUri,
            imageFile = pendingPhotoFile,
            companies = state.companies,
            defaultRate = state.defaultRate,
            onDismiss = {
                showAddParcelDialog = false
                pendingPhotoUri = null
                pendingPhotoFile = null
            },
            onSave = { companyId, rate, customerInfo, notes ->
                if (pendingPhotoFile != null) {
                    viewModel.addParcelFromCameraFile(
                        companyId = companyId,
                        file = pendingPhotoFile!!,
                        rate = rate,
                        customerInfo = customerInfo,
                        notes = notes
                    )
                } else if (pendingPhotoUri != null) {
                    viewModel.addParcelFromUri(
                        companyId = companyId,
                        sourceUri = pendingPhotoUri!!,
                        rate = rate,
                        customerInfo = customerInfo,
                        notes = notes
                    )
                }
                showAddParcelDialog = false
                pendingPhotoUri = null
                pendingPhotoFile = null
            },
            onQuickAddCompany = { name, phone ->
                viewModel.addCompany(name, phone, "", state.defaultRate)
            }
        )
    }

    if (selectedParcelForView != null) {
        val company = state.companies.find { it.id == selectedParcelForView?.companyId }
        ParcelPhotoViewerDialog(
            parcel = selectedParcelForView!!,
            company = company,
            onDismiss = { selectedParcelForView = null },
            onDelete = { parcel ->
                viewModel.deleteParcel(parcel)
                selectedParcelForView = null
            }
        )
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("سفڕکردنەوەی ژمێرەر", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "دڵنیایت لە سفڕکردنەوەی ژمێرەری ئەم مانگە؟\nکۆی ${NumberUtils.formatNumber(state.totalParcelsCount)} بەرید بە بەهای ${NumberUtils.formatIqd(state.totalParcelsAmount)} دەسڕێنەوە."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetCurrentMonthParcels()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("بەڵێ، سفڕی بکەوە", color = MaterialTheme.colorScheme.surface, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("پەشیمانمەوە")
                }
            }
        )
    }

    if (showChangeRateDialog) {
        var rateInput by remember { mutableStateOf(state.defaultRate.toInt().toString()) }
        AlertDialog(
            onDismissRequest = { showChangeRateDialog = false },
            title = { Text("نرخی هەر بەریدێک", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("نرخی ئەژمارکراوی هەر وێنەیەک بە دیناری عێراقی:")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rateInput,
                        onValueChange = { rateInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("نرخ بە د.ع") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = rateInput.toDoubleOrNull() ?: state.defaultRate
                        viewModel.updateDefaultRate(parsed)
                        showChangeRateDialog = false
                    }
                ) {
                    Text("پاشەکەوتکردن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeRateDialog = false }) {
                    Text("داخستن")
                }
            }
        )
    }
}

@Composable
fun MinimalParcelCard(
    parcel: ParcelEntity,
    company: CompanyEntity?,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("parcel_item_${parcel.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = File(parcel.imagePath),
                contentDescription = "وێنەی بەرید",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = company?.name ?: "بەریدی گشتی",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "+${NumberUtils.formatIqd(parcel.rateIqd)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (parcel.customerInfo.isNotBlank()) {
                    Text(
                        text = parcel.customerInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = NumberUtils.formatRelativeTime(parcel.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "سڕینەوە",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
