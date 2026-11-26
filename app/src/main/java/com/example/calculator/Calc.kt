package com.example.calculator

enum class PercentMode(val title: String, val label1: String, val label2: String) {
    OF("X% của A", "Số A", "Phần trăm X (%)"),
    INCREASE("Tăng X%", "Giá trị ban đầu", "Tăng (%)"),
    DECREASE("Giảm X%", "Giá trị ban đầu", "Giảm (%)"),
    CHANGE("% thay đổi A→B", "Giá trị ban đầu A", "Giá trị mới B"),
    PART("A là % của B", "Số A", "Số B")
}

data class CalcResult(val main: String, val detail: String, val sign: Int = 0)

fun calculate(mode: PercentMode, a: Double, b: Double, decimals: Int = 2): CalcResult? = when (mode) {
    PercentMode.OF -> {
        val r = a * b / 100
        CalcResult(fmt(r, decimals), "${fmt(b, decimals)}% của ${fmt(a, decimals)}")
    }
    PercentMode.INCREASE -> {
        val r = a * (1 + b / 100)
        CalcResult(fmt(r, decimals), "Tăng thêm ${fmt(r - a, decimals)} so với ${fmt(a, decimals)}", 1)
    }
    PercentMode.DECREASE -> {
        val r = a * (1 - b / 100)
        CalcResult(fmt(r, decimals), "Giảm ${fmt(a - r, decimals)} so với ${fmt(a, decimals)}", -1)
    }
    PercentMode.CHANGE -> {
        if (a == 0.0) null else {
            val pct = (b - a) / a * 100
            val sign = if (pct > 0) 1 else if (pct < 0) -1 else 0
            CalcResult(
                "${fmt(pct, decimals)}%",
                "Chênh lệch ${fmt(b - a, decimals)} (từ ${fmt(a, decimals)} đến ${fmt(b, decimals)})",
                sign
            )
        }
    }
    PercentMode.PART -> {
        if (b == 0.0) null else {
            val pct = a / b * 100
            CalcResult("${fmt(pct, decimals)}%", "${fmt(a, decimals)} là ${fmt(pct, decimals)}% của ${fmt(b, decimals)}")
        }
    }
}
