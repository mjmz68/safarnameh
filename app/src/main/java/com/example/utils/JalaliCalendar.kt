package com.example.utils

import java.util.Calendar
import java.util.GregorianCalendar

object JalaliCalendar {
    data class JalaliDate(val year: Int, val month: Int, val day: Int) : java.io.Serializable {
        fun format(): String = "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
        fun formatDetailed(): String = "$day ${getMonthName(month)} $year"
    }

    val MONTH_NAMES_PERSIAN = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    fun getMonthName(month: Int): String {
        return if (month in 1..12) MONTH_NAMES_PERSIAN[month - 1] else ""
    }

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = gy - 1600
        val gm2 = gm - 1
        val gd2 = gd - 1

        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
            gDayNo++ // leap year
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy2 = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy2 += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var i = 0
        while (i < 11 && jDayNo >= jDaysInMonth[i]) {
            jDayNo -= jDaysInMonth[i]
            i++
        }
        val jm3 = i + 1
        val jd3 = jDayNo + 1

        return JalaliDate(jy2, jm3, jd3)
    }

    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Calendar {
        val gy = jy - 979
        val gm = jm - 1
        val gd = jd - 1

        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        var jDayNo = 365 * gy + (gy / 33) * 8 + ((gy % 33) + 3) / 4
        for (i in 0 until gm) {
            jDayNo += jDaysInMonth[i]
        }
        jDayNo += gd

        var gDayNo = jDayNo + 79

        var gy2 = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = 1
        if (gDayNo >= 36525) {
            gDayNo--
            gy2 += 100 * (gDayNo / 36524)
            gDayNo %= 36524
            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = 0
            }
        }

        gy2 += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            leap = 0
            gDayNo--
            gy2 += gDayNo / 365
            gDayNo %= 365
        }

        var i = 0
        while (true) {
            var days = gDaysInMonth[i]
            if (i == 1 && leap == 1) {
                days = 29
            }
            if (gDayNo < days) {
                break
            }
            gDayNo -= days
            i++
        }

        val cal = GregorianCalendar(gy2, i, gDayNo + 1)
        return cal
    }

    fun getCurrentJalali(): JalaliDate {
        val calendar = Calendar.getInstance()
        return gregorianToJalali(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun isJalaliLeap(year: Int): Boolean {
        val rem = (year - 474) % 2820
        val l = ((rem + 38) * 31) % 128
        return l < 31
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        if (month in 1..6) return 31
        if (month in 7..11) return 30
        return if (isJalaliLeap(year)) 30 else 29
    }
}
