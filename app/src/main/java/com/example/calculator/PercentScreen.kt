package com.example.calculator

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PercentScreen() {
    var mode by remember { mutableStateOf(PercentMode.OF) }
    var decimals by remember { mutableIntStateOf(2) }
    var inputA by remember { mutableStateOf("") }
    var inputB by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<CalcResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val history = remember { mutableStateListOf<String>() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Tính phần trăm", style = MaterialTheme.typography.headlineSmall)

        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PercentMode.entries.forEach { m ->
                FilterChip(
                    selected = mode == m,
                    onClick = { mode = m; result = null; error = null },
                    label = { Text(m.title) }
                )
            }
        }

        Text("Làm tròn đến", style = MaterialTheme.typography.labelLarge)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (0..4).forEach { d ->
                FilterChip(
                    selected = decimals == d,
                    onClick = { decimals = d },
                    label = { Text(if (d == 0) "Nguyên" else "$d số lẻ") }
                )
            }
        }

        OutlinedTextField(
            value = inputA,
            onValueChange = { inputA = it },
            label = { Text(mode.label1) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = inputB,
            onValueChange = { inputB = it },
            label = { Text(mode.label2) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val a = inputA.replace(",", ".").toDoubleOrNull()
                val b = inputB.replace(",", ".").toDoubleOrNull()
                if (a == null || b == null) {
                    error = "Vui lòng nhập số hợp lệ"
                    result = null
                } else {
                    val r = calculate(mode, a, b, decimals)
                    if (r == null) {
                        error = "Không thể chia cho 0"
                        result = null
                    } else {
                        error = null
                        result = r
                        history.add(0, "${mode.title}: ${r.main}")
                        if (history.size > 10) history.removeAt(history.lastIndex)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tính")
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        result?.let { r ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Kết quả", style = MaterialTheme.typography.labelLarge)
                    Text(r.main, style = MaterialTheme.typography.headlineMedium)
                    when (r.sign) {
                        1 -> Text("▲ Tăng", color = Color(0xFF2E7D32))
                        -1 -> Text("▼ Giảm", color = Color(0xFFC62828))
                    }
                    Text(r.detail, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (history.isNotEmpty()) {
            HorizontalDivider()
            Text("Lịch sử", style = MaterialTheme.typography.titleMedium)
            history.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
            TextButton(onClick = { history.clear() }) { Text("Xóa lịch sử") }
        }
    }
}
