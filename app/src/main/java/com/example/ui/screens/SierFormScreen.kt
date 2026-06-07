package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.Sier
import com.example.ui.SierViewModel
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.SunsetGoldLight
import com.example.utils.JalaliCalendar
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SierFormScreen(
    viewModel: SierViewModel,
    editingSier: Sier?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Form fields
    var year by remember { mutableStateOf(editingSier?.dateYear ?: JalaliCalendar.getCurrentJalali().year) }
    var month by remember { mutableStateOf(editingSier?.dateMonth ?: JalaliCalendar.getCurrentJalali().month) }
    var day by remember { mutableStateOf(editingSier?.dateDay ?: JalaliCalendar.getCurrentJalali().day) }

    var trainName by remember { mutableStateOf(editingSier?.trainName ?: "") }
    var routeFrom by remember { mutableStateOf(editingSier?.routeFrom ?: "") }
    var routeTo by remember { mutableStateOf(editingSier?.routeTo ?: "") }
    var wagonNumber by remember { mutableStateOf(editingSier?.wagonNumber ?: "") }
    var salonOutbound by remember { mutableStateOf(editingSier?.salonOutbound ?: "") }
    var salonReturn by remember { mutableStateOf(editingSier?.salonReturn ?: "") }
    var trainMaster by remember { mutableStateOf(editingSier?.trainMaster ?: "") }
    var headAttendant by remember { mutableStateOf(editingSier?.headAttendant ?: "") }
    var memories by remember { mutableStateOf(editingSier?.memories ?: "") }
    var rating by remember { mutableStateOf(editingSier?.rating ?: 5.0f) }
    var isCompleted by remember { mutableStateOf(editingSier?.isCompleted ?: true) }

    // Selected photo URIs / paths
    var photoPaths = remember { 
        mutableStateListOf<String>().apply { 
            if (editingSier != null) {
                addAll(viewModel.parsePhotoJson(editingSier.photoUrisJson))
            }
        } 
    }

    // Active dialog toggles
    var showDatePicker by remember { mutableStateOf(false) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            uris.forEach { uri ->
                val path = uri.toString()
                if (!photoPaths.contains(path)) {
                    photoPaths.add(path)
                }
            }
        }
    )

    // Preloaded Rail illustrations as lovely fallbacks
    val fallbackPhotos = listOf(
        "https://images.unsplash.com/photo-1532103054090-334e6e60ab29?auto=format&fit=crop&q=80&w=600", // Train cabin Cozy night
        "https://images.unsplash.com/photo-1474487548417-781cb71495f3?auto=format&fit=crop&q=80&w=600", // Locomotive sunset
        "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&q=80&w=600", // Snowy railroads
        "https://images.unsplash.com/photo-1541417901756-c49c0148c263?auto=format&fit=crop&q=80&w=600"  // Retro train track
    )

    // Form errors
    var trainNameError by remember { mutableStateOf(false) }
    var routeFromError by remember { mutableStateOf(false) }
    var routeToError by remember { mutableStateOf(false) }
    var wagonError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (editingSier == null) "ثبت سیر جدید و ماموریت" else "ویرایش اطلاعات سیر",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shamsi Calendar Active Display Card
            Card(
                onClick = { showDatePicker = true },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("date_picker_trigger")
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تاریخ سیر (تقویم شمسی دقیق)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$day ${JalaliCalendar.getMonthName(month)} $year",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Button(
                        onClick = { showDatePicker = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تغییر تاریخ", fontSize = 12.sp)
                    }
                }
            }

            // Train core specification card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "مشخصات قطار و مسیر حرکت",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    OutlinedTextField(
                        value = trainName,
                        onValueChange = { 
                            trainName = it
                            if (it.isNotEmpty()) trainNameError = false
                        },
                        label = { Text("نام قطار (مثال: فدک، رجا، غزال، سپهر)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Train, contentDescription = null) },
                        isError = trainNameError,
                        supportingText = { if (trainNameError) Text("لطفاً نام قطار را وارد کنید") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_train_name")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = routeFrom,
                            onValueChange = { 
                                routeFrom = it
                                if (it.isNotEmpty()) routeFromError = false
                            },
                            label = { Text("مبداء سیر") },
                            isError = routeFromError,
                            supportingText = { if (routeFromError) Text("الزامی") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_route_from")
                        )

                        IconButton(
                            onClick = {
                                val temp = routeFrom
                                routeFrom = routeTo
                                routeTo = temp
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Swap")
                        }

                        OutlinedTextField(
                            value = routeTo,
                            onValueChange = { 
                                routeTo = it
                                if (it.isNotEmpty()) routeToError = false
                            },
                            label = { Text("مقصد سیر") },
                            isError = routeToError,
                            supportingText = { if (routeToError) Text("الزامی") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_route_to")
                        )
                    }
                }
            }

            // Wagon details card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "اطلاعات واگن تحویلی و رییس قطار",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    OutlinedTextField(
                        value = wagonNumber,
                        onValueChange = { 
                            wagonNumber = it
                            if (it.isNotEmpty()) wagonError = false
                        },
                        label = { Text("شماره واگن تحویل گرفته شده (مثال: 480)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null) },
                        isError = wagonError,
                        supportingText = { if (wagonError) Text("شماره واگن الزامی است") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_wagon")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = salonOutbound,
                            onValueChange = { salonOutbound = it },
                            label = { Text("سالن رفت (مثال: 5)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_salon_outbound")
                        )

                        OutlinedTextField(
                            value = salonReturn,
                            onValueChange = { salonReturn = it },
                            label = { Text("سالن برگشت (مثال: 5)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_salon_return")
                        )
                    }

                    OutlinedTextField(
                        value = trainMaster,
                        onValueChange = { trainMaster = it },
                        label = { Text("نام رئیس یا روسای قطار (مثال: آقای حمیدی)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_train_master")
                    )

                    OutlinedTextField(
                        value = headAttendant,
                        onValueChange = { headAttendant = it },
                        label = { Text("نام سرمهماندار واگن (مثال: آقای غیاثوند)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.SupervisorAccount, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_head_attendant")
                    )
                }
            }

            // Diary notes & ratings
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "خاطرات، وقایع و توصیف سفر",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    OutlinedTextField(
                        value = memories,
                        onValueChange = { memories = it },
                        label = { Text("یادداشت اتفاقات ویژه یا خاطرات شیرین...") },
                        placeholder = { Text("مثلاً: امروز کانیه وست سالن من بود و چای واگن ما رو میل کرد!") },
                        leadingIcon = { Icon(imageVector = Icons.Default.EditNote, contentDescription = null) },
                        minLines = 3,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth().testTag("input_memories")
                    )

                    Text(
                        text = "شرایط سفر و رضایت پرسنل:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i.toFloat() }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star $i",
                                    tint = if (i <= rating) SunsetGoldLight else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    // Done vs Upcoming toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isCompleted) "سفر با موفقیت انجام و کار شد" else "سفر برنامه‌ریزی شده در آینده",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = isCompleted,
                            onCheckedChange = { isCompleted = it },
                            modifier = Modifier.testTag("toggle_completed")
                        )
                    }
                }
            }

            // Photos attachments card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "عکس‌های یادگاری سفر",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { 
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("گالری گوشی", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { 
                                val randomPic = fallbackPhotos.random()
                                if (!photoPaths.contains(randomPic)) {
                                    photoPaths.add(randomPic)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.BurstMode, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("دریافت عکس ریلی واگن", fontSize = 11.sp)
                        }
                    }

                    if (photoPaths.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            photoPaths.forEachIndexed { index, path ->
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = path,
                                        contentDescription = "Trip Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { photoPaths.removeAt(index) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .background(
                                                color = Color.Black.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(bottomStart = 8.dp)
                                            )
                                            .size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هنوز عکسی برای این سفر ثبت نشده است",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Save actions
            Button(
                onClick = {
                    // Validations
                    if (trainName.trim().isEmpty()) { trainNameError = true }
                    if (routeFrom.trim().isEmpty()) { routeFromError = true }
                    if (routeTo.trim().isEmpty()) { routeToError = true }
                    if (wagonNumber.trim().isEmpty()) { wagonError = true }

                    if (!trainNameError && !routeFromError && !routeToError && !wagonError) {
                        viewModel.saveSier(
                            id = editingSier?.id ?: 0,
                            year = year,
                            month = month,
                            day = day,
                            trainName = trainName,
                            routeFrom = routeFrom,
                            routeTo = routeTo,
                            wagonNumber = wagonNumber,
                            salonOutbound = salonOutbound,
                            salonReturn = salonReturn,
                            trainMaster = trainMaster,
                            headAttendant = headAttendant,
                            memories = memories,
                            photoPaths = photoPaths.toList(),
                            rating = rating,
                            isCompleted = isCompleted,
                            onSuccess = {
                                onNavigateBack()
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_sier_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.CheckBox, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (editingSier == null) "ثبت اطلاعات و ذخیره ماموریت" else "ذخیره تغییرات سیر",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // --- NATIVE SHAMSI DATE PICKER DIALOG ---
        if (showDatePicker) {
            Dialog(onDismissRequest = { showDatePicker = false }) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )

                        Text(
                            text = "انتخاب تاریخ شمسی جهت ثبت سیر",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "سال‌های مجاز (۱۳۹۰ تا ۱۵۰۰)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Year picker
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text("سال", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                                Spacer(modifier = Modifier.height(4.dp))
                                var yearText by remember { mutableStateOf(year.toString()) }
                                OutlinedTextField(
                                    value = yearText,
                                    onValueChange = { val input = it.filter { c -> c.isDigit() }
                                        if (input.length <= 4) {
                                            yearText = input
                                            val y = input.toIntOrNull()
                                            if (y != null && y in 1390..1500) {
                                                year = y
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                                )
                            }

                            // Month scrollable column/dropdown
                            Column(modifier = Modifier.weight(1.8f)) {
                                Text("ماه", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                var monthExpanded by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { monthExpanded = true },
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                    ) {
                                        Text(
                                            text = JalaliCalendar.getMonthName(month),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            fontSize = 13.sp
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = monthExpanded,
                                        onDismissRequest = { monthExpanded = false },
                                        modifier = Modifier.heightIn(max = 240.dp)
                                    ) {
                                        JalaliCalendar.MONTH_NAMES_PERSIAN.forEachIndexed { index, name ->
                                            DropdownMenuItem(
                                                text = { Text(name, fontWeight = FontWeight.Medium) },
                                                onClick = {
                                                    month = index + 1
                                                    monthExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Day picker
                            Column(modifier = Modifier.weight(1f)) {
                                Text("روز", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                                Spacer(modifier = Modifier.height(4.dp))
                                var dayExpanded by remember { mutableStateOf(false) }
                                val maxDays = JalaliCalendar.getDaysInMonth(year, month)
                                if (day > maxDays) { day = maxDays }

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { dayExpanded = true },
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                    ) {
                                        Text(text = day.toString(), fontWeight = FontWeight.Bold)
                                    }
                                    DropdownMenu(
                                        expanded = dayExpanded,
                                        onDismissRequest = { dayExpanded = false },
                                        modifier = Modifier.heightIn(max = 240.dp)
                                    ) {
                                        for (d in 1..maxDays) {
                                            DropdownMenuItem(
                                                text = { Text(d.toString(), fontWeight = FontWeight.Medium) },
                                                onClick = {
                                                    day = d
                                                    dayExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Date Preview Label
                        Text(
                            text = "تاریخ انتخابی: $day ${JalaliCalendar.getMonthName(month)} $year",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = { showDatePicker = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("انصراف")
                            }

                            Button(
                                onClick = { showDatePicker = false },
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("تایید تاریخ")
                            }
                        }
                    }
                }
            }
        }
    }
}
