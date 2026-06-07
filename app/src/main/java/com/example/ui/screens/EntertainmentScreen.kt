package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SierViewModel
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.SunsetGoldLight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EntertainmentScreen(
    viewModel: SierViewModel
) {
    val geminiRec by viewModel.geminiRecommendation.collectAsState()
    val isLoadingGemini by viewModel.isLoadingGemini.collectAsState()
    val geminiStatus by viewModel.geminiStatus.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Books, 1: Media, 2: Podcasts, 3: AI Advisor
    var userMood by remember { mutableStateOf("") }
    var userPrefType by remember { mutableStateOf("کتاب ترجمه") }

    val preloadedMoods = listOf(
        "خسته بعد از ۱۲ ساعت کار واگن",
        "آماده حرکت مجلل و پرانرژی",
        "بی‌خواب در کوپه استراحت قطار",
        "غمگین از دوری خانه و لایق آرامش",
        "علاقه‌مند به داستان‌های هیجانی و مرموز"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("کافه کتاب و سینمای همسفران", fontWeight = FontWeight.Bold, fontSize = 18.sp) 
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
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().testTag("entertainment_tabs")
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("معرفی کتاب", fontWeight = FontWeight.Bold) },
                    icon = { Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(20.dp)) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("فیلم و سریال", fontWeight = FontWeight.Bold) },
                    icon = { Icon(imageVector = Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(20.dp)) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("پادکست فارسی", fontWeight = FontWeight.Bold) },
                    icon = { Icon(imageVector = Icons.Default.Podcasts, contentDescription = null, modifier = Modifier.size(20.dp)) }
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    text = { Text("مشاور هنری AI", fontWeight = FontWeight.Bold) },
                    icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp)) }
                )
            }

            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { targetTab ->
                when (targetTab) {
                    0 -> { // Preloaded Books
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "کتاب‌های برتر با ترجمه جذاب فارسی (مناسب مطالعه ریلی)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            items(RecommendationCatalog.books) { book ->
                                BookItemCard(book)
                            }
                        }
                    }
                    1 -> { // Preloaded Media
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "فیلم و سریال‌های برجسته ایرانی و خارجی",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            items(RecommendationCatalog.media) { mediaItem ->
                                MediaItemCard(mediaItem)
                            }
                        }
                    }
                    2 -> { // Preloaded Podcasts
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "شنیدنی‌ترین پادکست‌های پارسی برای استراحت در حرکت",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            items(RecommendationCatalog.podcasts) { pod ->
                                PodcastItemCard(pod)
                            }
                        }
                    }
                    3 -> { // Gemini AI Advisor
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = SunsetGoldLight)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "میز مشاوره هنری هوشمند ریلی (AI)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Divider()
                                    Text(
                                        text = "احرافت را بنویس یا انتخاب کن، جمیله قطار (هوش مصنوعی) کتاب، تئاتر، فیلم یا موسیقی ناب را متناسب با احوال شما پیوند می‌دهد:",
                                        fontSize = 12.sp,
                                        lineHeight = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Preloaded mood buttons
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        preloadedMoods.forEach { mood ->
                                            SuggestionChip(
                                                onClick = { userMood = mood },
                                                label = { Text(mood, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                                colors = SuggestionChipDefaults.suggestionChipColors(
                                                    containerColor = if (userMood == mood) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                                )
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = userMood,
                                        onValueChange = { userMood = it },
                                        placeholder = { Text("مثلاً: دلم داستان‌های شیرین فلسفی کوتاه قرن ۱۸ می‌خواد") },
                                        label = { Text("شرح حال زنده شما") },
                                        minLines = 2,
                                        modifier = Modifier.fillMaxWidth().testTag("ai_mood_input")
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("علاقه‌مند به دریافت:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        listOf("کتاب ترجمه", "فیلم خانوادگی", "پادکست").forEach { t ->
                                            FilterChip(
                                                selected = userPrefType == t,
                                                onClick = { userPrefType = t },
                                                label = { Text(t, fontSize = 11.sp) }
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { 
                                            if (userMood.isNotEmpty()) {
                                                viewModel.askGeminiRecommendation(userMood, userPrefType)
                                            }
                                        },
                                        enabled = userMood.isNotEmpty() && !isLoadingGemini,
                                        modifier = Modifier.fillMaxWidth().testTag("get_ai_recommendation_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("دریافت پیشنهاد جالب ریلی من!")
                                    }
                                }
                            }

                            // Output
                            if (isLoadingGemini) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("جمیله هوشمند در حال بررسی و گزینش آثار هنری...", fontSize = 12.sp)
                                    }
                                }
                            }

                            if (geminiStatus == "API_KEY_ERROR") {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "کلید هوش مصنوعی (API Key) هنوز در برنامه تزریق نشده است، اما شما کماکان می‌توانید از دیتابیس غنی پیش‌فرض گنجانده شده در سه زبانه فوق کمال لذت را ببرید!",
                                        modifier = Modifier.padding(16.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            } else if (geminiStatus == "CONNECTION_ERROR") {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "خطای اتصال اینترنت! برقراری هماهنگی با هوش مصنوعی میسر نشد. لطفا شبکه خود را ارتقاء داده یا از گنجینه غنی آفلاین سه زبانه بالا استفاده نمایید.",
                                        modifier = Modifier.padding(16.dp),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            if (geminiRec.isNotEmpty() && !isLoadingGemini) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier.fillMaxWidth().testTag("ai_result_card")
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.LiveHelp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("توصیه هوشمند ویژه احوال شما", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                            }
                                            IconButton(onClick = { viewModel.clearRecommendation() }) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                            }
                                        }
                                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                                        Text(
                                            text = geminiRec,
                                            fontSize = 13.sp,
                                            lineHeight = 24.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookItemCard(book: BookRecommend) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = book.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SunsetGoldLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = book.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Text(text = "اثر: ${book.author} | مترجم نامدار: ${book.translator}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = book.genre, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text(text = book.summary, fontSize = 12.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
fun MediaItemCard(media: MediaRecommend) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = media.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "پدیدآورنده: ${media.creator} | سال تولید: ${media.year}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SunsetGoldLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = media.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = media.type, fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = media.genre, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            Text(text = media.summary, fontSize = 12.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
fun PodcastItemCard(pod: PodcastRecommend) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = pod.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SunsetGoldLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = pod.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Text(text = "میزبان پادکست: ${pod.host} | قلمرو موضوعی: ${pod.subject}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Text(text = "قسمت محبوب پیشنهادی: \"${pod.popularEpisode}\"", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SunsetGold)
            
            Text(text = pod.summary, fontSize = 12.sp, lineHeight = 20.sp)
        }
    }
}
