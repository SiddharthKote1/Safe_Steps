package com.example.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun PhoneScreen(navController: NavController){
    var countryCode by remember {mutableStateOf("")}
    var phoneNumber by remember {mutableStateOf("")}
    val context=LocalContext.current


    Column(modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top){

        Spacer(modifier=Modifier.padding(50.dp))
        Image(painter = painterResource(id=R.drawable.chatpi),
            contentDescription = null)
        Spacer(modifier=Modifier.padding(10.dp))
        Text(
            text = "Enter Phone Number",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier=Modifier.padding(10.dp))

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = countryCode,
                onValueChange = {
                    if (it.length <= 4) countryCode = it
                },
                label = { Text("Code") },
                modifier = Modifier.width(80.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        phoneNumber = it
                    }
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                label = { Text("Phone Number") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
        }
        Spacer(modifier=Modifier.padding(10.dp))
        Button(onClick = {
            navController.navigate("OTP")
            onLoginClicked(context, navController, countryCode, phoneNumber) {

            }
        },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Generate OTP")
        }
    }
}


@Composable
@Preview(showBackground = true)
fun PhoneScreenPreview(){
    PhoneScreen(navController = NavController(context = LocalContext.current))
}