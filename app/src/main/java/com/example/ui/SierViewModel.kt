package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiService
import com.example.data.*
import com.example.utils.JalaliCalendar
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChecklistItem(val text: String, var isChecked: Boolean = false)

class SierViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = SierRepository(db.sierDao())

    // All logged travels (reactive Flow)
    val allSiers: StateFlow<List<Sier>> = repository.allSiers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Jalali date helper
    private val currentJalali = JalaliCalendar.getCurrentJalali()

    // Dashboard Year and Month filter
    private val _filterYear = MutableStateFlow(currentJalali.year)
    val filterYear = _filterYear.asStateFlow()

    private val _filterMonth = MutableStateFlow(currentJalali.month)
    val filterMonth = _filterMonth.asStateFlow()

    // Filtered monthly Siers for stats and lists
    val monthlySiers: StateFlow<List<Sier>> = combine(allSiers, filterYear, filterMonth) { siers, year, month ->
        siers.filter { it.dateYear == year && it.dateMonth == month }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected trip details/edit target
    private val _selectedSier = MutableStateFlow<Sier?>(null)
    val selectedSier = _selectedSier.asStateFlow()

    // Gemini AI states
    private val _geminiStory = MutableStateFlow<String>("")
    val geminiStory = _geminiStory.asStateFlow()

    private val _geminiRecommendation = MutableStateFlow<String>("")
    val geminiRecommendation = _geminiRecommendation.asStateFlow()

    private val _isLoadingGemini = MutableStateFlow(false)
    val isLoadingGemini = _isLoadingGemini.asStateFlow()

    private val _geminiStatus = MutableStateFlow<String>("") // "SUCCESS", "API_KEY_ERROR", "CONNECTION_ERROR", etc.
    val geminiStatus = _geminiStatus.asStateFlow()

    // Conductor's wage rate configuration (Toman) with standard default
    private val _wagePerTrip = MutableStateFlow(1200000L) // Default 1,200,000 Tomans
    val wagePerTrip = _wagePerTrip.asStateFlow()

    // Pre-trip checking items list (toggable state)
    private val _checklist = MutableStateFlow(
        listOf(
            ChecklistItem("بررسی پلمپ کپسول‌های آتش‌نشانی سالن"),
            ChecklistItem("تحویل و شمارش دقیق واگن ملحفه‌ها، لوازم خواب و روبالشتی‌ها"),
            ChecklistItem("بررسی پاکیزگی، مایع دستشویی و جریان آب سرویس‌های بهداشتی"),
            ChecklistItem("تست فیزیکی سیستم صوتی سالن، پیجر ارتباطی و تابلوی اطلاعات واگن"),
            ChecklistItem("بررسی کارکرد صحیح سرمایش/گرمایش و تهویه مطبوع کوپه‌ها"),
            ChecklistItem("تحویل دفترچه گزارش واگن، برگه ماموریت و نقشه صندلی‌های مسافران"),
            ChecklistItem("بررسی منبع آب جوش (سماور الکترونیکی واگن) و ذخیره آب آشامیدنی")
        )
    )
    val checklist = _checklist.asStateFlow()

    fun updateFilters(year: Int, month: Int) {
        _filterYear.value = year
        _filterMonth.value = month
    }

    fun updateWageRate(rate: Long) {
        _wagePerTrip.value = rate
    }

    fun toggleChecklistItem(text: String) {
        _checklist.value = _checklist.value.map {
            if (it.text == text) it.copy(isChecked = !it.isChecked) else it
        }
    }

    fun resetChecklist() {
        _checklist.value = _checklist.value.map { it.copy(isChecked = false) }
    }

    fun selectSier(sier: Sier?) {
        _selectedSier.value = sier
        _geminiStory.value = "" // Reset story when selecting another trip
    }

    fun saveSier(
        id: Int = 0,
        year: Int,
        month: Int,
        day: Int,
        trainName: String,
        routeFrom: String,
        routeTo: String,
        wagonNumber: String,
        salonOutbound: String,
        salonReturn: String,
        trainMaster: String,
        headAttendant: String,
        memories: String,
        photoPaths: List<String>,
        rating: Float,
        isCompleted: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val photoJson = buildPhotoJson(photoPaths)
            val sier = Sier(
                id = id,
                dateYear = year,
                dateMonth = month,
                dateDay = day,
                trainName = trainName,
                routeFrom = routeFrom,
                routeTo = routeTo,
                wagonNumber = wagonNumber,
                salonOutbound = salonOutbound,
                salonReturn = salonReturn,
                trainMaster = trainMaster,
                headAttendant = headAttendant,
                memories = memories,
                photoUrisJson = photoJson,
                wageEstimate = _wagePerTrip.value,
                rating = rating,
                isCompleted = isCompleted
            )
            if (id == 0) {
                repository.insert(sier)
            } else {
                repository.update(sier)
            }
            onSuccess()
        }
    }

    fun deleteSier(sier: Sier, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.delete(sier)
            onSuccess()
        }
    }

    // Generate beautiful Travel Story based on trip details using Gemini!
    fun generateSierStory(sier: Sier) {
        _isLoadingGemini.value = true
        _geminiStatus.value = "LOADING"
        viewModelScope.launch {
            val systemInstruction = "شما یک دستیار هوشمند و نویسنده خلاق و با ذوق ایرانی برای مهمانداران قطار هستید. باید با جزئیات سفر ورودی، یک یادداشت ادبی، پر از حس همکاری، خاطره‌ساز، گرم و جذاب (به زبان فارسی خلاقانه و گاهی صمیمانه) تولید کنید که مهماندار واگن بتواند آن را ذخیره کند یا با بقیه همکارانش به اشتراک بگذارد."
            val prompt = """
                من یک مهماندار قطار هستم. لطفاً بر اساس اطلاعات سفر من یک واقعه‌نگاری یا سفرنامه ادبی و خاطره‌انگیز زیبا به زبان فارسی بنویس:
                - تاریخ سفر: ${sier.dateDay} ${JalaliCalendar.getMonthName(sier.dateMonth)} ${sier.dateYear}
                - مسیر قطار: ${sier.routeFrom} به ${sier.routeTo}
                - قطار: ${sier.trainName}
                - شماره واگن اینجانب: ${sier.wagonNumber}
                - شماره سالن در رفت: ${sier.salonOutbound}، در برگشت: ${sier.salonReturn}
                - رئیس قطار محترم: ${sier.trainMaster}
                - سرمهماندار گرامی: ${sier.headAttendant}
                - خاطره یا اتفاق ویژه این سیر: ${sier.memories.ifEmpty { "روند سیر آرام و بدون اتفاق خاص بود." }}
                
                لطفاً این سفرنامه را با عنوان زیبایی متناسب با مسیر شروع کن و در آخر یک آرزوی قشنگ ریلی و خسته نباشید صمیمانه برای من و همکارانم بفرست.
            """.trimIndent()

            val result = GeminiService.generateResponse(prompt = prompt, systemInstruction = systemInstruction)
            handleGeminiResult(result) { text ->
                _geminiStory.value = text
            }
            _isLoadingGemini.value = false
        }
    }

    // Ask custom AI recommended cultural contents according to mood
    fun askGeminiRecommendation(mood: String, type: String) {
        _isLoadingGemini.value = true
        _geminiStatus.value = "LOADING"
        viewModelScope.launch {
            val systemInstruction = "شما مشاور هنری، ادبی و فرهنگی باذوق با تسلط عالی به زبان فارسی هستید. وظیفه شما معرفی کتاب‌های ترجمه فارسی، فیلم‌های برتر سینمای ایران و جهان، سریال‌ها و پادکست‌های شنیدنی متناسب با احوال و حس مسافر یا پرسنل قطار است."
            val prompt = """
                من مهماندار خسته قطار هستم و بعد از کلی کار الان در استراحت کوپه یا خانه هستم. حالم اینگونه است: "$mood".
                لطفا بر اساس حالم یک مورد باکیفیت و جذاب ترجیحاً از نوع "$type" (کتاب ترجمه فارسی، فیلم برتر ایرانی یا خارجی، یا پادکست فارسی) به من معرفی کن.
                توضیحات معرفی شامل:
                1. عنوان اثر و پدیدآورنده آن.
                2. خلاصه کوتاه و جذاب به همراه دلیل اینکه چرا با حال فعلی من سازگار است.
                3. یک جمله عمیق یا توصیف کوتاه الهام‌بخش از اثر.
                لحن پاسخ گرم، هنرمندانه و همراه با صمیمیت ریلی باشد.
            """.trimIndent()

            val result = GeminiService.generateResponse(prompt = prompt, systemInstruction = systemInstruction)
            handleGeminiResult(result) { text ->
                _geminiRecommendation.value = text
            }
            _isLoadingGemini.value = false
        }
    }

    private fun handleGeminiResult(result: String, onBlock: (String) -> Unit) {
        when (result) {
            "API_KEY_ERROR" -> {
                _geminiStatus.value = "API_KEY_ERROR"
                onBlock("")
            }
            "CONNECTION_ERROR", "HTTP_ERROR" -> {
                _geminiStatus.value = "CONNECTION_ERROR"
                onBlock("")
            }
            "EMPTY_RESPONSE" -> {
                _geminiStatus.value = "EMPTY_RESPONSE"
                onBlock("")
            }
            else -> {
                _geminiStatus.value = "SUCCESS"
                onBlock(result)
            }
        }
    }

    fun clearRecommendation() {
        _geminiRecommendation.value = ""
        _geminiStatus.value = ""
    }

    // JSON parsing helper for image paths
    private fun buildPhotoJson(paths: List<String>): String {
        val array = org.json.JSONArray()
        paths.forEach { array.put(it) }
        return array.toString()
    }

    fun parsePhotoJson(json: String): List<String> {
        val list = mutableListOf<String>()
        if (json.isEmpty()) return list
        try {
            val array = org.json.JSONArray(json)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (e: Exception) {
            // Fallback for custom formatted values
        }
        return list
    }
}
