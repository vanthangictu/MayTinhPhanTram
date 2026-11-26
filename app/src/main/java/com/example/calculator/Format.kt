package com.example.calculator

import java.util.Locale

// Định dạng số theo kiểu Việt Nam, làm tròn đến số chữ số thập phân cho trước
fun fmt(x: Double, decimals: Int = 2): String =
    String.format(Locale("vi", "VN"), "%,.${decimals}f", x)
