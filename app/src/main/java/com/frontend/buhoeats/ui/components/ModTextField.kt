package com.frontend.buhoeats.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textColor: Color,
    containerColor: Color,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    validateDigits: Boolean = false,
    contextMessage: String = ""
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        Text(
            text = "$label:",
            style = TextStyle(
                fontFamily = montserratFontFamily,
                color = Color.White,
                fontSize = 24.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = value,
            onValueChange = {
                var newValue = it
                if (validateDigits && it.any { c -> c.isDigit() }) {
                    Toast.makeText(context, contextMessage, Toast.LENGTH_SHORT).show()
                    newValue = it.filter { c -> !c.isDigit() }
                }
                onValueChange(newValue)
            },
            placeholder = {
                Text(
                    placeholder,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    style = TextStyle(fontFamily = montserratFontFamily)
                )
            },
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontFamily = montserratFontFamily
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor,
                errorContainerColor = containerColor,
                cursorColor = textColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )
    }
}