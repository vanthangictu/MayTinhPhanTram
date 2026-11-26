package com.example.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Currency
import java.util.Locale

// API miễn phí, không cần key, tỷ giá cơ sở là USD
private const val RATES_URL = "https://open.er-api.com/v6/latest/USD"

suspend fun fetchRates(): Map<String, Double> = withContext(Dispatchers.IO) {
    val json = JSONObject(URL(RATES_URL).readText())
    val rates = json.getJSONObject("rates")
    rates.keys().asSequence().associateWith { rates.getDouble(it) }
}

fun currencyName(code: String): String =
    runCatching { Currency.getInstance(code).getDisplayName(Locale("vi", "VN")) }
        .getOrDefault(code)

@Composable
fun CurrencyScreen() {
    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var amount by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("USD") }
    var to by remember { mutableStateOf("VND") }
    val scope = rememberCoroutineScope()

    fun refresh() {
        scope.launch {
            loading = true
            error = null
            try {
                rates = fetchRates()
            } catch (e: Exception) {
                error = "Không tải được tỷ giá: ${e.message}"
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    val codes = rates.keys.sorted()
    val converted = remember(amount, from, to, rates) {
        val a = amount.replace(",", ".").toDoubleOrNull()
        val rf = rates[from]
        val rt = rates[to]
        if (a == null || rf == null || rt == null) null else a / rf * rt
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Chuyển đổi tiền tệ", style = MaterialTheme.typography.headlineSmall)

        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Số tiền") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        CurrencyPicker("Từ", from, codes) { from = it }

        OutlinedButton(
            onClick = { val t = from; from = to; to = t },
            modifier = Modifier.fillMaxWidth()
        ) { Text("⇅ Đổi chiều") }

        CurrencyPicker("Sang", to, codes) { to = it }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Kết quả", style = MaterialTheme.typography.labelLarge)
                Text(
                    converted?.let { "${fmt(it)} $to" } ?: "Nhập số tiền hợp lệ",
                    style = MaterialTheme.typography.headlineMedium
                )
                if (converted != null) {
                    Text(
                        "1 $from = ${fmt((rates[to] ?: 0.0) / (rates[from] ?: 1.0))} $to",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Button(onClick = { refresh() }, modifier = Modifier.fillMaxWidth()) {
            Text("Cập nhật tỷ giá")
        }
    }
}

@Composable
fun CurrencyPicker(label: String, selected: String, codes: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
        Text("$label: $selected - ${currencyName(selected)}")
    }

    if (expanded) {
        AlertDialog(
            onDismissRequest = { expanded = false },
            confirmButton = {},
            title = { Text("Chọn tiền tệ") },
            text = {
                LazyColumn(Modifier.heightIn(max = 400.dp)) {
                    items(codes) { code ->
                        TextButton(
                            onClick = { onSelect(code); expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$code - ${currencyName(code)}")
                        }
                    }
                }
            }
        )
    }
}
