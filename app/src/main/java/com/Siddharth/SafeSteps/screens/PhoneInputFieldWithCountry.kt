package com.Siddharth.SafeSteps.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Country(
    val code: String,
    val shortName: String,
    val flag: String
)

val defaultCountries = listOf(
    Country("+91", "IN", "🇮🇳"),
    Country("+1", "US", "🇺🇸"),
    Country("+44", "UK", "🇬🇧"),
    Country("+61", "AU", "🇦🇺"),
    Country("+81", "JP", "🇯🇵"),
    Country("+49", "DE", "🇩🇪"),
    Country("+33", "FR", "🇫🇷"),
    Country("+39", "IT", "🇮🇹"),
    Country("+86", "CN", "🇨🇳"),
    Country("+55", "BR", "🇧🇷")
)

@Composable
fun PhoneInputFieldWithCountry(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    selectedCountry: Country,
    onCountryChange: (Country) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Country Selector
        Box {
            Row(
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedCountry.flag} ${selectedCountry.shortName} (${selectedCountry.code})",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Country",
                    tint = Color.Gray
                )
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                defaultCountries.forEach { country ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${country.flag} ${country.shortName} (${country.code})",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onCountryChange(country)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(0.6f)
                .background(Color.LightGray)
                .padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))

        // Phone Input
        Box(modifier = Modifier.weight(1f)) {
            if (phoneNumber.isEmpty() && !isFocused) {
                Text("Phone number", color = Color.Gray, fontSize = 16.sp)
            }
            
            BasicTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
            )
        }
    }
}
