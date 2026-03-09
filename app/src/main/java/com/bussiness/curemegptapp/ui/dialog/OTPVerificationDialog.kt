package com.bussiness.curemegptapp.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.GradientButton

@Composable
fun OTPVerificationDialog(
    onDismiss: () -> Unit,
    onVerify: (otp: String) -> Unit,
    title: String = "Verify OTP",
    message: String = "Enter the OTP sent to your email/phone",
    otpLength: Int = 6
) {
    var otp by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Message
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF697383),
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // OTP Input Fields
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(otpLength) { index ->
                        OTPInputField(
                            value = if (index < otp.length) otp[index].toString() else "",
                            onValueChange = { newValue ->
                                if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                    val newOtp = otp.toMutableList()
                                    if (newValue.isEmpty()) {
                                        if (index < newOtp.size) {
                                            newOtp.removeAt(index)
                                        }
                                    } else {
                                        if (index < newOtp.size) {
                                            newOtp[index] = newValue[0]
                                        } else {
                                            newOtp.add(newValue[0])
                                        }
                                    }
                                    otp = newOtp.joinToString("")
                                }
                            },
                            index = index,
                            totalLength = otpLength,
                            isFilled = index < otp.length
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Verify Button
                GradientButton(
                    text = "Verify OTP",
                    onClick = {
                        if (otp.length == otpLength) {
                            onVerify(otp)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cancel Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, Color(0xFF697383), RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF697383)
                    )
                }
            }
        }
    }
}

@Composable
fun OTPInputField(
    value: String,
    onValueChange: (String) -> Unit,
    index: Int,
    totalLength: Int,
    isFilled: Boolean
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isFilled) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFF5F5F5)
            )
            .border(
                width = 2.dp,
                color = if (isFilled) Color(0xFF4CAF50) else Color(0xFFE5E5E5),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            enabled = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OTPVerificationDialogPreview() {
    OTPVerificationDialog(
        onDismiss = {},
        onVerify = { otp ->
            println("OTP Entered: $otp")
        }
    )
}

