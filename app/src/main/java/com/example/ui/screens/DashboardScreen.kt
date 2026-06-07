package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Sier
import com.example.ui.SierViewModel
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.SunsetGoldLight
import com.example.utils.JalaliCalendar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SierViewModel,
    onNavigateToAddSier: () -> Unit,
    onNavigateToDetails: (Sier) -> Unit,
    onNavigateToEdit: (Sier) -> Unit
) {
    val monthlySiers by viewModel.monthlySiers.collectAsState()
    val filterYear by viewModel.filterYear.collectAsState()
    val filterMonth by viewModel.filterMonth.collectAsState()
    val wagePerTrip by viewModel.wagePerTrip.collectAsState()
    val checklist by viewModel.checklist.collectAsState()

    // Dialog toggles
    var showWageDialog by remember { mutableStateOf(false) }
    var tempWageText by remember { mutableStateOf(wagePerTrip.toString()) }

    val currentJalali = remember { JalaliCalendar.getCurrentJalali() }

    // Computations
    val totalTripsThisMonth = monthlySiers.size
    val estimatedEarnings = totalTripsThisMonth * wagePerTrip
    val completedTripsThisMonth = monthlySiers.count { it.isCompleted }

    // Currency Formatting (Persian style, Rial/Toman)
    val formattedEarnings = remember(estimatedEarnings) {
        try {
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            formatter.format(estimatedEarnings)
        } catch (e: Exception) {
            estimatedEarnings.toString()
        }
    }

    val formattedWageRate = remember(wagePerTrip) {
        try {
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            formatter.format(wagePerTrip)
        } catch (e: Exception) {
            wagePerTrip.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Warm Welcomer Banner (Sleek Theme style) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_header")
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_rail_logo_1780820919431),
                    contentDescription = "Train Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Column {
                    Text(
                        text = "سامانه مأمورین",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 15.sp,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "مدیریت سیر و مأموریتهای ریلی",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 9.sp
                    )
                }
            }

            // Beautiful Shamsi current date badge
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentJalali.formatDetailed(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- Calendar Summary Card (Sleek Theme style) ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    var monthMenuExpanded by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { monthMenuExpanded = true }
                    ) {
                        Text(
                            text = "${JalaliCalendar.getMonthName(filterMonth)} $filterYear",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        DropdownMenu(
                            expanded = monthMenuExpanded,
                            onDismissRequest = { monthMenuExpanded = false },
                            modifier = Modifier.heightIn(max = 240.dp)
                        ) {
                            for (m in 1..12) {
                                DropdownMenuItem(
                                    text = { Text(JalaliCalendar.getMonthName(m), fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.updateFilters(filterYear, m)
                                        monthMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$totalTripsThisMonth سیر ثبت شده",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Horizontal simulated date scroller
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val daysOfWeek = listOf("ش", "ی", "د", "س", "چ", "پ")
                    val currentDayOfMonth = currentJalali.day
                    val baseDay = (currentDayOfMonth - 2).coerceAtLeast(1)

                    for (i in 0..5) {
                        val dayNum = baseDay + i
                        val isToday = dayNum == currentDayOfMonth
                        val weekDay = daysOfWeek[i % daysOfWeek.size]

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.primary 
                                    else Color.Transparent
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isToday) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = weekDay,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Core Statistics & Wages Estimate deck ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Trips Count
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(125.dp)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text("تعداد کل سیرها", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalTripsThisMonth سفر", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Completed vs Scheduled
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(125.dp)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                    Column {
                        Text("سیرهای موفق", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$completedTripsThisMonth سفر", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF059669))
                    }
                }
            }
        }

        // Wage estimate visual calculator card "سرمون کلاه نره"
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wage_estimate_card")
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "محاسب حق‌سیر ماهانه واگن (تخمین ریالی)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    IconButton(onClick = { showWageDialog = true }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Edit Wage Rate", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "بهای هر سیر کامل پایه‌واگن شما:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$formattedWageRate تومان",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "حق‌الزحمه طلب کل ماه:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$formattedEarnings تومان",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SunsetGoldLight
                        )
                    }
                }

                Text(
                    text = "* این رقم بر مبنای ضرب تعداد واگن‌های پروازی تحویلی در نرخ حق سیر پایه محاسبه شده و سندی جهت مغایرت‌گیری با دفاتر شرکت پیمانکار قطار است.",
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }

        // --- Interactive Mechanical Inspection / Pre-trip safety checklist ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("inspection_checklist_card")
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PlaylistAddCheck, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "چک‌لیست ایمنی و تحویل سالن",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "اقدامات ضروری مهماندار پیش از مسافرگیری",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TextButton(onClick = { viewModel.resetChecklist() }) {
                        Text("بازنشانی", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    }
                }

                Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    checklist.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleChecklistItem(item.text) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { viewModel.toggleChecklistItem(item.text) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.text,
                                fontSize = 12.sp,
                                color = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (item.isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // --- Simple Month's Trip Quick Scroll ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_trips_card")
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "سیرهای قطار در ماه جاری (${JalaliCalendar.getMonthName(filterMonth)})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider()

                if (monthlySiers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.DirectionsTransit, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), modifier = Modifier.size(54.dp))
                            Text(
                                text = "هنوز هیچ سیری برای این ماه ثبت نشده است.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            TextButton(onClick = onNavigateToAddSier) {
                                Text("ثبت اولین سیر واگن همین الان", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        monthlySiers.take(5).forEach { sier ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToDetails(sier) }
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Train, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "قطار ${sier.trainName} (واگن ${sier.wagonNumber})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${sier.routeFrom} ➔ ${sier.routeTo}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "${sier.dateDay} ${JalaliCalendar.getMonthName(sier.dateMonth)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        if (monthlySiers.size > 5) {
                            Text(
                                text = "+ ${monthlySiers.size - 5} سیر دیگر در بایگانی ماهانه موجود است.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // --- BASE RATE EDIT DIALOG ---
    if (showWageDialog) {
        Dialog(onDismissRequest = { showWageDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().testTag("wage_settings_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "تنظیم نرخ پایه هر ماموریت",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "مبلع دریافتی بابت هر سیر واگن تحویلی (به تومان) را به صورت دقیق وارد کنید تا برآورد حق‌سیر شما انجام گیرد:",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = tempWageText,
                        onValueChange = { tempWageText = it.filter { c -> c.isDigit() } },
                        label = { Text("مبلغ به تومان") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("wage_rate_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showWageDialog = false }) {
                            Text("انصراف")
                        }

                        Button(
                            onClick = {
                                val rate = tempWageText.toLongOrNull()
                                if (rate != null) {
                                    viewModel.updateWageRate(rate)
                                }
                                showWageDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("ذخیره نرخ")
                        }
                    }
                }
            }
        }
    }
}
