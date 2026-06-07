package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Sier
import com.example.ui.SierViewModel
import com.example.ui.theme.SunsetGold
import com.example.utils.JalaliCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: SierViewModel,
    onNavigateToDetails: (Sier) -> Unit,
    onNavigateToEdit: (Sier) -> Unit
) {
    val allSiers by viewModel.allSiers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var yearFilter by remember { mutableStateOf<Int?>(null) }
    var monthFilter by remember { mutableStateOf<Int?>(null) }

    var yearExpanded by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }

    // Unique years present in data for filtering, defaulting to current if empty
    val availableYears = remember(allSiers) {
        val years = allSiers.map { it.dateYear }.distinct().sortedDescending().toMutableList()
        if (years.isEmpty()) {
            years.add(JalaliCalendar.getCurrentJalali().year)
        }
        years
    }

    // Filtered Siers based on UI parameters
    val filteredSiers = remember(allSiers, searchQuery, yearFilter, monthFilter) {
        allSiers.filter { sier ->
            val matchQuery = searchQuery.isEmpty() ||
                    sier.trainName.contains(searchQuery, ignoreCase = true) ||
                    sier.routeFrom.contains(searchQuery, ignoreCase = true) ||
                    sier.routeTo.contains(searchQuery, ignoreCase = true) ||
                    sier.trainMaster.contains(searchQuery, ignoreCase = true) ||
                    sier.headAttendant.contains(searchQuery, ignoreCase = true)

            val matchYear = yearFilter == null || sier.dateYear == yearFilter
            val matchMonth = monthFilter == null || sier.dateMonth == monthFilter

            matchQuery && matchYear && matchMonth
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("بایگانی و گزارشات کامل سیر", fontWeight = FontWeight.Bold, fontSize = 18.sp) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Filter Deck Component
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search text field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("جستجو در قطارها، شهرها و همکاران...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("history_search")
                    )

                    // Drops layout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Year filter button
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { yearExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (yearFilter == null) "کامل سال‌ها" else "سال: $yearFilter",
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = yearExpanded,
                                onDismissRequest = { yearExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("تمام سال‌ها", fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        yearFilter = null
                                        yearExpanded = false
                                    }
                                )
                                availableYears.forEach { yr ->
                                    DropdownMenuItem(
                                        text = { Text(yr.toString()) },
                                        onClick = {
                                            yearFilter = yr
                                            yearExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Month filter button
                        Box(modifier = Modifier.weight(1.2f)) {
                            OutlinedButton(
                                onClick = { monthExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (monthFilter == null) "کامل ماه‌ها" else JalaliCalendar.getMonthName(monthFilter!!),
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = monthExpanded,
                                onDismissRequest = { monthExpanded = false },
                                modifier = Modifier.heightIn(max = 240.dp)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("تمام ماه‌ها", fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        monthFilter = null
                                        monthExpanded = false
                                    }
                                )
                                for (m in 1..12) {
                                    DropdownMenuItem(
                                        text = { Text(JalaliCalendar.getMonthName(m)) },
                                        onClick = {
                                            monthFilter = m
                                            monthExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Reset Filters Button
                        if (yearFilter != null || monthFilter != null || searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                yearFilter = null
                                monthFilter = null
                                searchQuery = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Default.FilterListOff,
                                    contentDescription = "Reset Filters",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // List of Siers
            if (filteredSiers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            modifier = Modifier.size(96.dp)
                        )
                        Text(
                            text = "هیچ سیری مطابق با فیلترها یافت نشد!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "ماه‌ها و نام‌های فرضی را مجددا بررسی کنید یا بوسیله دکمه شناور، اولین سفر خود را یادداشت کنید.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f).testTag("history_list")
                ) {
                    items(filteredSiers) { sier ->
                        SierItemCard(
                            sier = sier,
                            onClick = { onNavigateToDetails(sier) },
                            onDelete = { viewModel.deleteSier(sier, {}) },
                            onEdit = { onNavigateToEdit(sier) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SierItemCard(
    sier: Sier,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sier_item_${sier.id}")
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Train & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Train,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "قطار ${sier.trainName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("ویرایش") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("حذف سیر", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            // Journey Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = sier.routeFrom, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Icon(
                    imageVector = Icons.Default.ArrowRightAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(text = sier.routeTo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                Spacer(modifier = Modifier.weight(1f))

                // Done Badge
                Box(
                    modifier = Modifier
                        .background(
                            color = if (sier.isCompleted) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFFF59E0B).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (sier.isCompleted) "انجام شده" else "برنامه‌ریزی شده",
                        fontSize = 10.sp,
                        color = if (sier.isCompleted) Color(0xFF059669) else Color(0xFFD97706),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Details (Wagon, Salons, Rating, Date)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "واگن: ${sier.wagonNumber} (سالن: ${sier.salonOutbound})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "رئیس: ${sier.trainMaster.ifEmpty { "ثبت نشده" }}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${sier.dateDay} ${JalaliCalendar.getMonthName(sier.dateMonth)} ${sier.dateYear}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SunsetGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${sier.rating}/5", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
