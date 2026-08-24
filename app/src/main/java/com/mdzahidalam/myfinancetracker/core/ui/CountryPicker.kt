package com.mdzahidalam.myfinancetracker.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdzahidalam.myfinancetracker.core.country.CountryCatalog
import com.mdzahidalam.myfinancetracker.core.country.CountryOption

@Composable
fun SearchableCountryPicker(value: CountryOption, language: String = "EN", onSelect: (CountryOption) -> Unit) {
    var open by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
        Text("${value.flag}  ${value.displayName(language)} — ${value.currencyCode} (${value.currencySymbol})")
    }
    if (open) CountryPickerDialog(value, language, onDismiss = { open = false }) {
        onSelect(it)
        open = false
    }
}

@Composable
private fun CountryPickerDialog(selected: CountryOption, language: String, onDismiss: () -> Unit, onSelect: (CountryOption) -> Unit) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { CountryCatalog.search(query) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (language == "BN") "দেশ নির্বাচন করুন" else "Select country") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(if (language == "BN") "দেশ, কোড বা মুদ্রা খুঁজুন" else "Search country, code or currency") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                LazyColumn(modifier = Modifier.heightIn(max = 430.dp)) {
                    items(results, key = CountryOption::isoCode) { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(country) }
                                .padding(vertical = 12.dp)
                                .semantics { contentDescription = country.name },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(country.flag)
                            Column(Modifier.weight(1f)) {
                                Text(country.displayName(language), fontWeight = if (country == selected) FontWeight.Bold else FontWeight.Normal)
                                Text("${country.isoCode} • ${country.currencyCode} (${country.currencySymbol})")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(if (language == "BN") "বাতিল" else "Cancel") } }
    )
}
